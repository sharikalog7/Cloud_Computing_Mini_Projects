package LogParsingIPMaxCount.LogParsingIPMaxCount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogParserGetMaxIP {

    public static class LogParsingMapper extends Mapper<LongWritable, Text, Text, LongWritable>
    {
        String logPattern = "^([\\d.]+) (\\S+) (.+?) \\[([\\w:/]+\\s[+-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\S+)";
        private final static LongWritable one = new LongWritable(1);
        private Text outputKey;
        public void map(LongWritable key, Text value, Context context)
        {
            try
            {
                Pattern pattern = Pattern.compile(logPattern);
                Matcher logMatcher = pattern.matcher(value.toString());
                if(logMatcher.matches())
                {
                    outputKey = new Text(logMatcher.group(1));
                    context.write(outputKey, one);
                }

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class LogParsingReducer extends Reducer<Text, LongWritable, Text, LongWritable>
    {
        private HashMap<Text, LongWritable> ipWithCount = new HashMap<Text, LongWritable>();

        public void reduce(Text key, Iterable<LongWritable> values, Context context)
        {
            try
            {
                long count = 0;
                for(LongWritable val : values)
                    count = count + val.get();
                ipWithCount.put(new Text(key), new LongWritable(count));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        protected void cleanup(Context context)
        {
            try
            {
                LinkedHashMap<Text, LongWritable> sortedMap = new LinkedHashMap<Text, LongWritable>();
                ipWithCount.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
                Map.Entry<Text, LongWritable> entry = sortedMap.entrySet().iterator().next();
                LongWritable max = entry.getValue();
                System.out.println(max);
                for(Text key : sortedMap.keySet())
                {
                    if(max.compareTo(sortedMap.get(key)) == 0)
                        context.write(key, sortedMap.get(key));
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    public static class LogParsingCombiner extends Reducer<Text, LongWritable, Text, LongWritable> {

        public void reduce(Text key, Iterable<LongWritable> values, Context context) {
            try {
                long count = 0;
                for (LongWritable val : values)
                    count = count + val.get();
                context.write(key, new LongWritable(count));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws Exception
    {
        Configuration config = new Configuration();
        Job job = Job.getInstance(config, "Log Parsing");
        job.setJarByClass(LogParserGetMaxIP.class);
        job.setMapperClass(LogParsingMapper.class);
        job.setReducerClass(LogParsingReducer.class);
        job.setCombinerClass(LogParsingCombiner.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
