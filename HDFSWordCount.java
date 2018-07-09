
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * @Package spark.streaming
 * @Description:
 * @Author elwyn
 * @Date 2017/11/7 22:13
 * @Email elonyong@163.com
 */
public class HDFSWordCount {
	public static void main(String[] args) throws InterruptedException {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("WordCount").set("spark.serializer","org.apache.spark.serializer.KryoSerializer");
		JavaStreamingContext javaStreamingContext = new JavaStreamingContext(conf, Durations.seconds(2));

		JavaPairInputDStream<LongWritable, Text> longWritableTextJavaPairInputDStream = javaStreamingContext.fileStream("hdfs://bigdata02.nebuinfo" +
						".com:8020/sparktest/data/wordcount",
				LongWritable.class, Text.class, TextInputFormat.class,
				new Function<Path, Boolean>() {
					@Override
					public Boolean call(Path v1) throws Exception {
						return true;
					}
				}, false);

		longWritableTextJavaPairInputDStream.print();

		/*
		JavaDStream<String> lines = javaStreamingContext.textFileStream("hdfs://bigdata02.nebuinfo.com:8020/sparktest/data/wordcount");
		lines.flatMap(x -> Arrays.asList(x.split(" ")).iterator())
				.mapToPair(x -> new Tuple2<String, Integer>(x, 1))
				.reduceByKey((x, y) -> x + y).print();*/
		//必须调用start方法才会开始
		javaStreamingContext.start();
		//一直等待
		javaStreamingContext.awaitTermination();
		javaStreamingContext.close();
	}

}