package ru.kvaga.rss.feedaggrwebserver;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;

import ru.kvaga.monitoring.influxdb2.InfluxDB;

public class MonitoringUtils {
	public static void sendResponseTime2InfluxDB(Object obj, long responseTime) {
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
		InfluxDB.getInstance().send(point);
	}
}
