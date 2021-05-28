package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

public class Test {

	
	public static void main(String[] args) throws URISyntaxException, IOException, GetURLContentException {
		
//		InfluxDB influxDB = InfluxDB.getInstance("130.61.122.117", 8086, "system_monitoring");
//		InfluxDB influxDB = InfluxDB.getInstance("130.61.122.117", 8086, "feedaggrwebserver");

		
//		influxDB.createDatabase();
//		influxDB.deleteDatabase();
		
//		influxDB.send("cpu,host=localhost", "100");

		String url = "https://www.youtube.com/channel/UCEtJi-euMY2-jUZpuiU1POg/videos";
//		System.out.println(Exec.getTitleFromHtmlBody(responseHtmlBody));
	}
	
	private static String readString(File file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String s;
		StringBuilder sb = new StringBuilder();
		while((s=br.readLine())!=null) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	

}
