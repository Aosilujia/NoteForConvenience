import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

/*import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;*/
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import scala.Tuple2;

public class SparkSMain {

	private static String[] countries = { "RMB", "USD", "JPY", "EUR" };
	private static BigDecimal[] oricurrency = { new BigDecimal("2.0"), new BigDecimal("12.0"), new BigDecimal("0.5"),
			new BigDecimal("6.0") };

	private static BigDecimal[] currency = { new BigDecimal("2.0"), new BigDecimal("12.0"), new BigDecimal("0.5"),
			new BigDecimal("6.0") };

	private static int updatecurrency(String time) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timezero = "2018-01-01 00:00:00";
		try {
			Date newdate = simpleDateFormat.parse(time);
			Date olddate = simpleDateFormat.parse(timezero);
			int gap = (int) ((newdate.getTime() - olddate.getTime()) / 1000 / 60);
			for (int i = 0; i < currency.length; i++) {
				BigDecimal adds = new BigDecimal("0.1").multiply(new BigDecimal(gap));
				BigDecimal newc = oricurrency[i].add(adds);
				currency[i] = newc;
			}
			return gap;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}

	
	
	
	public static void main(String[] args) throws InterruptedException {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("WordCount");
		JavaStreamingContext javaStreamingContext = new JavaStreamingContext(conf, Durations.seconds(4));
		JavaReceiverInputDStream<String> lines = javaStreamingContext.socketTextStream("bigdata05.nebuinfo.com", 9999);
		
		String[] temps={"null"};
		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterator<String> call(String s) throws Exception {
				//return Arrays.asList(s.split(" ")).iterator();
				temps[1]=s;
				return Arrays.asList(temps).iterator();
			}
		});

		JsonParser parser=new JsonParser();
		
		JavaPairDStream<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
			@Override
			public Tuple2<String, Integer> call(String s) throws Exception {
				JsonObject order=(JsonObject) parser.parse(s);
				BigDecimal value = order.get("value").getAsBigDecimal();
				String time = order.get("time").getAsString();
				String src_name = order.get("src_name").getAsString();
				String dst_name = order.get("dst_name").getAsString();
				
				
				
				
				return new Tuple2<>(s, 1);
			}
		});

		JavaPairDStream<String, Integer> wordCount = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer call(Integer v1, Integer v2) throws Exception {
				return v1 + v2;
			}
		});
		wordCount.print();

		// 必须调用start方法才会开始
		javaStreamingContext.start();
		// 一直等待直到结束
		javaStreamingContext.awaitTermination();
		javaStreamingContext.close();
	}
}
