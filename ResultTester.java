import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mesos.Log.WriterFailedException;
import org.apache.spark.deploy.worker.Sleeper;

import com.fasterxml.jackson.databind.deser.std.DateDeserializers.CalendarDeserializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import scala.language;
import scala.reflect.internal.Trees.New;

public class ResultTester {

	private static JsonParser parser = new JsonParser();
	private static JsonArray[] array = new JsonArray[3];
	private static String[] countries = { "RMB", "USD", "JPY", "EUR" };
	private static BigDecimal[] oricurrency = { new BigDecimal("2.0"), new BigDecimal("12.0"), new BigDecimal("0.5"),
			new BigDecimal("6.0") };

	static class Record {
		ArrayList<BigDecimal> expend = new ArrayList<BigDecimal>();
		ArrayList<BigDecimal> income = new ArrayList<BigDecimal>();

		Record() {
			expend.add(new BigDecimal(0));
			expend.add(new BigDecimal(0));
			expend.add(new BigDecimal(0));
			expend.add(new BigDecimal(0));

			income.add(new BigDecimal(0));
			income.add(new BigDecimal(0));
			income.add(new BigDecimal(0));
			income.add(new BigDecimal(0));
		}
	}

	private static int parsec(String country) {
		if (country.equals("RMB")) {
			return 0;
		} else if (country.equals("USD")) {
			return 1;
		} else if (country.equals("JPY")) {
			return 2;
		} else if (country.equals("EUR")) {
			return 3;
		} else
			return -1;
	}

	private static int updatecurrency(String time) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String timezero = "2018-01-01 00:00";
		try {
			Date newdate = simpleDateFormat.parse(time);
			Date olddate = simpleDateFormat.parse(timezero);
			int gap = (int) ((newdate.getTime() - olddate.getTime()) / 1000 / 60);
			for (int i = 0; i < currency.length; i++) {
				BigDecimal adds = new BigDecimal("0.1").multiply(new BigDecimal(gap));
				BigDecimal newc = oricurrency[i].add(adds);
				currency[i]=newc;
			}
			return gap;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private static Map<String, Record> Data = new HashMap<String, Record>();

	private static BigDecimal[] currency =  { new BigDecimal("2.0"), new BigDecimal("12.0"), new BigDecimal("0.5"),
			new BigDecimal("6.0") };
	
	public static void main(String[] args) {


		try {
			array[0] = (JsonArray) parser.parse(new FileReader("test_data_0.json"));
			array[1] = (JsonArray) parser.parse(new FileReader("test_data_1.json"));
			array[2] = (JsonArray) parser.parse(new FileReader("test_data_2.json"));
			for (int a = 0; a < 3; a++) {
				JsonArray tarray = array[a];

				for (int i = 0; i < tarray.size(); i++) {
					JsonObject subObject = tarray.get(i).getAsJsonObject();
					BigDecimal value = subObject.get("value").getAsBigDecimal();
					String time = subObject.get("time").getAsString();
					String src_name = subObject.get("src_name").getAsString();
					String dst_name = subObject.get("dst_name").getAsString();
					int src_num = parsec(src_name);
					int dst_num = parsec(dst_name);
					Record record = new Record();
					time = time.substring(0, 16);
					if (Data.containsKey(time)) {
						record = Data.get(time);
					} else {
						System.out.println(updatecurrency(time));

						System.out.println(currency[0]);
					}
					BigDecimal expend1, expend2, income1, income2;
					// System.out.println(src_name);
					expend1 = record.expend.get(src_num).add(value);
					BigDecimal incomevalue = value.multiply(currency[src_num]).divide(currency[dst_num], 10,
							BigDecimal.ROUND_HALF_EVEN);

					income1 = record.income.get(dst_num).add(incomevalue);
					record.expend.set(src_num, expend1);
					record.income.set(dst_num, income1);

					Data.put(time, record);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("hi");

		File outputfile = new File("testoutput.txt");
		BufferedWriter outputbuffer;
		try {
			outputfile.createNewFile();
			outputbuffer = new BufferedWriter(new FileWriter(outputfile));

			for (Map.Entry<String, Record> entry : Data.entrySet()) {
				System.out.println("Time = " + entry.getKey());
				outputbuffer.write("Time = " + entry.getKey() + "\r\n");
				Record tRecord = entry.getValue();
				for (int i = 0; i < tRecord.income.size(); i++) {
					BigDecimal tincome = tRecord.income.get(i);
					BigDecimal texpense = tRecord.expend.get(i);
					outputbuffer.write(countries[i] + ":income=" + tincome.toString() + "\r\n");
					outputbuffer.write("expend=" + texpense.toString() + "\r\n");
					System.out.println(tincome.toString());
				}
			}

			outputbuffer.flush();
			outputbuffer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
