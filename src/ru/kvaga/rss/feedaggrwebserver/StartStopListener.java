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
				InfluxDB.ENABLED=ConfigMap.INFLUXDB_ENABLED;
				log.info("Loaded parameter influxdb.enabled="+ConfigMap.INFLUXDB_ENABLED);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.enabled parameter ["+props.getProperty("influxdb.enabled")+"]. InfluxDB disabled");
				InfluxDB.ENABLED=false;
			}
			ConfigMap.INFLUXDB_HOST=props.getProperty("influxdb.host");
			log.info("Loaded parameter influxdb.host="+ConfigMap.INFLUXDB_HOST);
			ConfigMap.INFLUXDB_DBNAME=props.getProperty("influxdb.dbname");
			log.info("Loaded parameter influxdb.dbname="+ConfigMap.INFLUXDB_DBNAME);
			try {
				ConfigMap.INFLUXDB_THREAD_NUMBER=Integer.parseInt(props.getProperty("influxdb.threads.numder"));
				log.info("Loaded parameter influxdb.threads.numder="+ConfigMap.INFLUXDB_THREAD_NUMBER + " and set to the InfluxDB");
				InfluxDB.THREADS_NUMBER=ConfigMap.INFLUXDB_THREAD_NUMBER;
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.threads.numder parameter ["+props.getProperty("influxdb.threads.numder")+"]. Set default value 10");
				InfluxDB.THREADS_NUMBER=10;
			}
			try {
				ConfigMap.INFLUXDB_PORT=Integer.parseInt(props.getProperty("influxdb.port"));
				log.info("Loaded parameter influxdb.port="+ConfigMap.INFLUXDB_PORT);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.port parameter ["+props.getProperty("influxdb.port")+"]. InfluxDB disabled");
				InfluxDB.ENABLED=false;
			}
			InfluxDB.getInstance(ConfigMap.INFLUXDB_HOST, ConfigMap.INFLUXDB_PORT, ConfigMap.INFLUXDB_DBNAME, InfluxDB.THREADS_NUMBER);

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
