package ru.kvaga.rss.feedaggrwebserver.monitoring;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;

import ru.kvaga.monitoring.influxdb2.InfluxDB;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;

public class MonitoringUtils {
	private static boolean enabled=true;
	public static void enable(){
		enabled=true;
	}
	
	public static void init(String host, int port, String dbName) {
		if(!enabled) return;
		InfluxDB.getInstance(host, port, dbName);
	}
	public static void disable() {
		enabled=false;
	}
	public static void sendResponseTime2InfluxDB(Object obj, long responseTime) {
		if(!enabled) return;
		String className=obj.getClass().getName();
		int ind=-1;
		if((ind = className.indexOf('$'))!=-1) {
			className=className.substring(0, ind);
		}
		Point point = Point.measurement("response_time")
							  .time(System.currentTimeMillis() - new Random().nextInt()%1000, TimeUnit.MILLISECONDS)
							  .tag("method", className + "." + obj.getClass().getEnclosingMethod().getName())
							  .addField("v", responseTime)
							  .build();
		if(InfluxDB.getInstance()!=null) {
			InfluxDB.getInstance().send(point);
		}
	}
	
	public static void sendCommonMetric(String metricName, int metricValue) {
		if(!enabled) return;

		Builder b = Point.measurement(metricName)
							  .time(System.currentTimeMillis() - new Random().nextInt()%1000, TimeUnit.MILLISECONDS)
							  .addField("value", metricValue);
		Point point = b.build();
		InfluxDB.getInstance().send(point);
	} 
	
	public static void sendCommonMetric(String metricName, int metricValue, Tag ... tags) {
		if(!enabled) return;

		Builder b = Point.measurement(metricName)
							  .time(System.currentTimeMillis() - new Random().nextInt()%1000, TimeUnit.MILLISECONDS)
							  .addField("value", metricValue);
		for(Tag tag: tags) {
			b.tag(tag.getName(), tag.getValue());
		}
		 Point point = b.build();
		 if(InfluxDB.getInstance()!=null) {
			 InfluxDB.getInstance().send(point);
		 }
	} 
}
