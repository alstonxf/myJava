package mapreduce.mapjoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MapJoinDriver {
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {

        // 1 获取job信息
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        // 2 设置加载jar包路径
        job.setJarByClass(MapJoinDriver.class);
        // 3 关联mapper
        job.setMapperClass(MapJoinMapper.class);
        // 4 设置Map输出KV类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        // 5 设置最终输出KV类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        // 加载缓存数据
        job.addCacheFile(new Path("/Users/lixiaofeng/myGitProjects/myJava/myHadoop/src/main/java/mapreduce/mapjoin/t_product").toUri());
        // Map端Join的逻辑不需要Reduce阶段，设置reduceTask数量为0
        job.setNumReduceTasks(0);

        // 6 设置输入输出路径
        FileInputFormat.setInputPaths(job, new Path("/Users/lixiaofeng/myGitProjects/myJava/myHadoop/src/main/java/mapreduce/mapjoin/t_order"));
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 将当前时间格式化为指定格式的字符串
        String formattedTime = currentTime.format(formatter);
        // 打印当前格式化后的时间
        System.out.println("Current time: " + formattedTime);
        String outputPath = null;
        if (args.length == 0){
            outputPath = "/Users/lixiaofeng/myGitProjects/myJava/myHadoop/src/main/java/mapreduce/mapjoin/output/"+formattedTime;
        }else{
            outputPath = args[1];
        }
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        // 7 提交
        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }

}
