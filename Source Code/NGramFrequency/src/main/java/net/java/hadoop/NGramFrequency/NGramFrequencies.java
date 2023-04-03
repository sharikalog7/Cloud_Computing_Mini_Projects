package net.java.hadoop.NGramFrequency;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class NGramFrequencies {

    public static class NGramFrequencyMapper extends Mapper<LongWritable, Text, Text, LongWritable>
    {
        private final static LongWritable one = new LongWritable(1);
        private Text outputKey = new Text();
        public void map(LongWritable key, Text value, Context context)
        {
            try
            {
                Configuration config = context.getConfiguration();
                int n = Integer.parseInt(config.get("n"));
                String input[] = value.toString().split(" ");
                for(int i = 0; i < input.length; i++) {
                    for (int j = 0; j <= input[i].length() - n; j++) {
                        outputKey.set(input[i].substring(j, j + n));
                        context.write(outputKey, one);
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class NGramFrequencyReducer extends Reducer<Text, LongWritable, Text, LongWritable>
    {
        private final LongWritable result = new LongWritable();
        public void reduce(Text key, Iterable<LongWritable> values, Context context)
        {
            try
            {
                long count = 0;
                for(LongWritable val : values)
                    count = count + val.get();
                result.set(count);
                context.write(key, result);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws Exception
    {
        Configuration config = new Configuration();
        config.set("n", args[0]);
        Job job = Job.getInstance(config, "N-gram Frequencies");
        job.setJarByClass(NGramFrequencies.class);
        job.setMapperClass(NGramFrequencyMapper.class);
        job.setReducerClass(NGramFrequencyReducer.class);
        job.setCombinerClass(NGramFrequencyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
