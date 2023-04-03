package LogParsingMaxURLCount.LogParsingMaxURLCount;

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

public class LogParserGetMaxURL {

    public static class LogParserGetMaxURLMapper extends Mapper<LongWritable, Text, Text, LongWritable>
    {
        String logPattern = "^([\\d.]+) (\\S+) (.+?) \\[([\\w:/]+\\s[+-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\S+)";
        private Text outputKey = new Text();
        public void map(LongWritable key, Text value, Context context)
        {
            try
            {
                Pattern pattern = Pattern.compile(logPattern);
                Matcher logMatcher = pattern.matcher(value.toString());
                if(logMatcher.matches())
                {
                    outputKey = new Text(logMatcher.group(5).split(" ")[1]);
                    // System.out.println(outputKey);
                    context.write(outputKey, new LongWritable(1));
                }

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class LogParserGetMaxURLCombiner extends Reducer<Text, LongWritable, Text, LongWritable>
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

    public static class LogParserGetMaxURLReducer extends Reducer<Text, LongWritable, Text, LongWritable>
    {
        private HashMap<Text, LongWritable> urlCount = new HashMap<>();
        public void reduce(Text key, Iterable<LongWritable> values, Context context)
        {
            try
            {
                long count = 0;
                for(LongWritable val : values)
                    count = count + val.get();
                urlCount.put(new Text(key), new LongWritable(count));
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
                LinkedHashMap<Text, LongWritable> sortedMap = new LinkedHashMap<>();
                urlCount.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
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

    public static void main(String args[]) throws Exception
    {
        Configuration config = new Configuration();
        Job job = Job.getInstance(config, "Log Parsing");
        job.setJarByClass(LogParserGetMaxURL.class);
        job.setMapperClass(LogParserGetMaxURLMapper.class);
        job.setReducerClass(LogParserGetMaxURLReducer.class);
        job.setCombinerClass(LogParserGetMaxURLCombiner.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
