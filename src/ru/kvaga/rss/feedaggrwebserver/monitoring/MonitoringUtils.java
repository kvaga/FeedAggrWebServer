package ru.kvaga.rss.feedaggrwebserver.monitoring;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;

import com.influxdb.client.domain.WritePrecision;

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.monitoring.influxdb.InfluxDB2;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.servlets.HealthCheckServlet;

public class MonitoringUtils {
	public static String LOGGER_MONITORING="monitoring";
	private static Logger logMonitoring = LogManager.getLogger(MonitoringUtils.LOGGER_MONITORING);

	//private static DecimalFormat myFormatter = new DecimalFormat("###################");
	private static String getEpochTime() {
		return new Date().getTime() +"000000";
	}
	private static boolean enabled=true;
	//final private static Logger log = LogManager.getLogger(MonitoringUtils.class);

	public static void enable(){
		enabled=true;
	}
	
//	public static void init_(String host, int port, String dbName) {
//		if(!enabled) return;
//		//InfluxDB.getInstance(host, port, dbName);
//	}
	public static void init(String url, String orgId, String bucket, String token) {
		//if(!enabled) return;
//		InfluxDB2.getInstance(
//        		url,
//        		orgId, 
//        		bucket,
//        		token
//        );
	}
	public static void disable() {
		enabled=false;
	}
	public static void sendResponseTime2InfluxDB(Object obj, long responseTime) {
		//if(!enabled) return;
//		try {
//			// TODO: temporarily disabled sendResponseTime2InfluxDB due to load impact
//			//if(true) return;
//			String className=obj.getClass().getName();
//			int ind=-1;
//			if((ind = className.indexOf('$'))!=-1) {
//				className=className.substring(0, ind);
//			}
//			if(InfluxDB.getInstance()!=null) {
//				
//				Point point = Point.measurement("response_time")
//									  .time(System.currentTimeMillis() - new Random().nextInt()%1000, TimeUnit.MILLISECONDS)
//									  .tag("method", className + "." + obj.getClass().getEnclosingMethod().getName())
//									  .addField("v", responseTime)
//									  .build();
//				if(InfluxDB.getInstance()!=null) {
//					InfluxDB.getInstance().send(point);
//				}
//			}
//			if(InfluxDB2.getInstance()!=null) {
//				com.influxdb.client.write.Point point = com.influxdb.client.write.Point.measurement("response_time")
//			                .addTag("method",  className + "." + obj.getClass().getEnclosingMethod().getName())
//			                .addField("value", responseTime)
//			                .time(Instant.now().toEpochMilli(), WritePrecision.MS);
//				InfluxDB2.getInstance().send(point, WritePrecision.MS);
//			}
//			log.debug("response_time, method="+className + "." + obj.getClass().getEnclosingMethod().getName()+" value="+responseTime);
//		}catch(Exception e) {
//			try {
//				log.error("Error on sending metric: obj ["+obj+"], responseTime=["+responseTime+"]", e);
//			}catch(Exception e1) {log.error(e1);}
//		}
	}
	
	private static String influxPattern="%s value=%s %s";
	
	public static void sendCommonMetric(String metricName, int metricValue) {
//		if(!enabled) return;
		logMonitoring.info(String.format(influxPattern, metricName, metricValue, getEpochTime()));
//		try {
//			if(InfluxDB.getInstance()!=null) {
//				Builder b = Point.measurement(metricName)
//									  .time(System.currentTimeMillis() - new Random().nextInt()%1000, TimeUnit.MILLISECONDS)
//									  .addField("value", metricValue);
//				Point point = b.build();
//				InfluxDB.getInstance().send(point);
//			}
//			
//			if(InfluxDB2.getInstance()!=null) {
//				com.influxdb.client.write.Point p = com.influxdb.client.write.Point.measurement(metricName)
//			                //.addTag("method",  className + "." + obj.getClass().getEnclosingMethod().getName())
//			                .addField("value", metricValue)
//			                .time(Instant.now().toEpochMilli(), WritePrecision.MS);
//				
//				InfluxDB2.getInstance().send(p, WritePrecision.MS);
//			}
//			log.debug(metricName+" value="+metricValue);
//		}catch(Exception e) {
//			try {
//				log.error("Error on sending metric: metricName ["+metricName+"], metricValue=["+metricValue+"]", e);
//			}catch(Exception e1) {log.error(e1);}
//		}
	} 
	
	public static void sendCommonMetric(String metricName, int metricValue, Tag ... tags) {
		for(Tag tag : tags) {
			metricName+=","+tag.getName()+"="+tag.getValue().replaceAll("(^\\h*)|(\\h*$)","").replace('\u00a0',' ').trim().replace(" ", "\\ ").replace("[", "\\[");
		}
		logMonitoring.info(String.format(influxPattern, metricName, metricValue, getEpochTime()));

//		if(!enabled) return;
//		try {
//			if(InfluxDB.getInstance()!=null) {
//				Builder b = Point.measurement(metricName)
//									  .time(System.currentTimeMillis() - new Random().nextInt()%1000, TimeUnit.MILLISECONDS)
//									  .addField("value", metricValue);
//				for(Tag tag: tags) {
//					b.tag(tag.getName(), tag.getValue());
//				}
//				 Point point = b.build();
//				 
//				 InfluxDB.getInstance().send(point);
//			 }
//			
//			if(InfluxDB2.getInstance()!=null) {
//				com.influxdb.client.write.Point point = com.influxdb.client.write.Point.measurement(metricName)
//			                //.addTag("method",  className + "." + obj.getClass().getEnclosingMethod().getName())
//			                .addField("value", metricValue)
//			                .time(Instant.now().toEpochMilli(), WritePrecision.MS);
//				for(Tag tag : tags) {
//					point.addTag(tag.getName(), tag.getValue());
//				}
//				InfluxDB2.getInstance().send(point, WritePrecision.MS);
//			}
//			log.debug(metricName+" value="+metricValue);
//		}catch(Exception e) {
//			try {
//				StringBuilder sb = new StringBuilder();
//				sb.append("[");
//				for(Tag t : tags) {
//					sb.append(enabled);
//					sb.append(", ");
//				}
//				sb.append("]");
//				log.error("Error on sending metric: metricName ["+metricName+"], metricValue=["+metricValue+"], tags ["+sb.toString()+"]", e);
//			}catch(Exception e1) {log.error(e1);}
//		}
	} 
}
