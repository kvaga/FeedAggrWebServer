package ru.kvaga.rss.feedaggrwebserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.Logger;

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.rss.feedaggr.Exec;

import org.apache.logging.log4j.LogManager;;


@WebListener
public class StartStopListener implements ServletContextListener{
	 final static Logger log = LogManager.getLogger(StartStopListener.class);
	 @Override
	    public void contextInitialized(ServletContextEvent servletContextEvent) {
		 
		try {
			log.info("Looking for configuration file based on the catalina.base system property and constructed like $catalina.base/conf/feedaggrwebserver.conf");
			ConfigMap.configFile = new File(System.getProperty("catalina.base").replaceAll("\\\\", "/")+"/conf/feedaggrwebserver.conf");
			log.info("Got config file path: " + ConfigMap.configFile.getAbsolutePath());
			Properties props = new Properties();
			props.load(new FileInputStream(ConfigMap.configFile));
			log.info("Loaded config file: " + ConfigMap.configFile.getAbsolutePath());
			log.info("Looking for data.file parameter");
			ConfigMap.dataPath=new File(props.getProperty("data.path"));
			log.info("Found parameter data.path=" + ConfigMap.dataPath.getAbsolutePath());
			ConfigMap.usersPath=new File(ConfigMap.dataPath.getAbsoluteFile()+"/users");
			log.info("Created parameter ConfigMap.usersPath="+ConfigMap.usersPath);
			ConfigMap.feedsPath=new File(ConfigMap.dataPath.getAbsoluteFile()+"/feeds");
			log.info("Created parameter ConfigMap.feedsPath="+ConfigMap.feedsPath);
			ConfigMap.adminLogin=props.getProperty("admin.login");
			log.info("Loaded parameter admin.login="+ConfigMap.adminLogin);
			ConfigMap.adminPassword=props.getProperty("admin.password");
			log.info("Loaded parameter admin.password="+ConfigMap.adminPassword);
			ConfigMap.adminPassword=props.getProperty("admin.password");
			log.info("Loaded parameter admin.password="+ConfigMap.adminPassword);
			
			try {
				ConfigMap.INFLUXDB_ENABLED=Boolean.parseBoolean(props.getProperty("influxdb.enabled"));
				if(ConfigMap.INFLUXDB_ENABLED) {
					InfluxDB.enable();
				}else {
					InfluxDB.disable();
				}
				log.info("Loaded parameter influxdb.enabled="+ConfigMap.INFLUXDB_ENABLED);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.enabled parameter ["+props.getProperty("influxdb.enabled")+"]. InfluxDB disabled");
				InfluxDB.disable();
			}
			ConfigMap.INFLUXDB_HOST=props.getProperty("influxdb.host");
			log.info("Loaded parameter influxdb.host="+ConfigMap.INFLUXDB_HOST);
			ConfigMap.INFLUXDB_DBNAME=props.getProperty("influxdb.dbname");
			log.info("Loaded parameter influxdb.dbname="+ConfigMap.INFLUXDB_DBNAME);
			try {
				ConfigMap.INFLUXDB_THREAD_NUMBER=Integer.parseInt(props.getProperty("influxdb.threads.numder"));
				log.info("Loaded parameter influxdb.threads.numder="+ConfigMap.INFLUXDB_THREAD_NUMBER + " and set to the InfluxDB");
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.threads.numder parameter ["+props.getProperty("influxdb.threads.numder")+"]. Set default value 10");
			}
			try {
				ConfigMap.INFLUXDB_PORT=Integer.parseInt(props.getProperty("influxdb.port"));
				log.info("Loaded parameter influxdb.port="+ConfigMap.INFLUXDB_PORT);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.port parameter ["+props.getProperty("influxdb.port")+"]. InfluxDB disabled");
				InfluxDB.disable();
			}
			
			try {
				ConfigMap.INFLUXDB_COUNT_OF_ATTEMPTS_IF_FAILS=Integer.parseInt(props.getProperty("influxdb.attempts"));
				log.info("Loaded parameter influxdb.attempts="+ConfigMap.INFLUXDB_COUNT_OF_ATTEMPTS_IF_FAILS);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.attempts parameter ["+props.getProperty("influxdb.attempts")+"]. InfluxDB disabled");
				InfluxDB.disable();
			}
			try {
				ConfigMap.INFLUXDB_TIMEOUT=Long.parseLong(props.getProperty("influxdb.timeout"));
				log.info("Loaded parameter influxdb.timeout="+ConfigMap.INFLUXDB_TIMEOUT);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.timeout parameter ["+props.getProperty("influxdb.timeout")+"]. InfluxDB disabled");
				InfluxDB.disable();
			}
			
			try {
				InfluxDB.setCountOfAttemptsIfFails(10);
				InfluxDB.setTimeoutInMillis(1000);
				InfluxDB.getInstance(ConfigMap.INFLUXDB_HOST, ConfigMap.INFLUXDB_PORT, ConfigMap.INFLUXDB_DBNAME, ConfigMap.INFLUXDB_THREAD_NUMBER);
				log.debug("InfluXDB successfully started");
			}catch(Exception e) {
				log.error("Couldn't start InfluxDB monitoring sending", e);
			}
			log.info("InfluxDB: " + InfluxDB.getInstance());

			if(System.getProperty("TEST_MODE")!=null) {
				ConfigMap.TEST_MODE=true;
			}
			log.info("Loaded parameter TEST_MODE="+ConfigMap.TEST_MODE);
			
			try {
				ConfigMap.WAIT_TIME_AFTER_GET_CONTENT_URL_EXCEPTION_IN_MILLIS=Long.parseLong(props.getProperty("timeout.waittime_after_get_content_url_exception_in_millis"));
				log.info("Loaded parameter timeout.waittime_after_get_content_url_exception_in_millis="+ConfigMap.WAIT_TIME_AFTER_GET_CONTENT_URL_EXCEPTION_IN_MILLIS + ", hh:mm:ss ["+Exec.getHumanReadableHoursMinutesSecondsFromMilliseconds(ConfigMap.WAIT_TIME_AFTER_GET_CONTENT_URL_EXCEPTION_IN_MILLIS)+"]");
			}catch(Exception e) {
				log.error("Incorrect format of timeout.waittime_after_get_content_url_exception_in_millis parameter ["+props.getProperty("timeout.waittime_after_get_content_url_exception_in_millis")+"]");
			}
			
			try {
				ConfigMap.SERVLET_SHOW_FEED_BUFFER_READ_BYTES=Integer.parseInt(props.getProperty("servlet.show_feed.buffer_read_bytes"));
				log.info("Loaded parameter servlet.show_feed.buffer_read_bytes ["+ConfigMap.SERVLET_SHOW_FEED_BUFFER_READ_BYTES + "]");
			}catch(Exception e) {
				log.error("Incorrect format of servlet.show_feed.buffer_read_bytes parameter ["+props.getProperty("servlet.show_feed.buffer_read_bytes")+"]");
			}
			
			try {
				ConfigMap.UPDATE_COMPOSITE_RSS_FILES_DAYS_COUNT_FOR_DELETION=Integer.parseInt(props.getProperty("update_composite_rss_files.days_count_for_deletion"));
				log.info("Loaded parameter update_composite_rss_files.days_count_for_deletion="+ConfigMap.UPDATE_COMPOSITE_RSS_FILES_DAYS_COUNT_FOR_DELETION);
			}catch(Exception e) {
				log.error("Incorrect format of update_composite_rss_files.days_count_for_deletion parameter ["+props.getProperty("update_composite_rss_files.days_count_for_deletion")+"]. Use default value ["+ConfigMap.UPDATE_COMPOSITE_RSS_FILES_DAYS_COUNT_FOR_DELETION+"]");
			}
			
			
		} catch (IOException e) {
			log.error("Can't get configuration parameters of servlet", e);
			return;
		}
	        log.info("FeedAggrWebServer initialized");
	    }

	    @Override
	    public void contextDestroyed(ServletContextEvent servletContextEvent) {
	    	InfluxDB.destroy();
	        log.info("Servlet has been stopped.");
	    }
	    
	
}
