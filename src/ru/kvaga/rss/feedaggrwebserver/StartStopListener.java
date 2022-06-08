package ru.kvaga.rss.feedaggrwebserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.Logger;

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.monitoring.influxdb.InfluxDB2;
import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggrwebserver.monitoring.MonitoringUtils;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
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
			
			ConfigMap.trustStore=props.getProperty("ssl.trustStore");
			log.info("Loaded parameter ssl.trustStore="+ConfigMap.trustStore);
			ConfigMap.trustStorePassword=props.getProperty("ssl.trustStorePassword");
			log.info("Loaded parameter ssl.trustStorePassword="+ConfigMap.trustStorePassword);
			try {
				System.setProperty("javax.net.ssl.trustStore", ConfigMap.trustStore);
				System.setProperty("javax.net.ssl.trustStorePassword", ConfigMap.trustStorePassword);	
				//System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\XUser\\.keystore");
				//System.setProperty("javax.net.ssl.trustStorePassword", "");
			}catch(Exception e) {
				log.error("SSL parameters Exception", e);
			}
			
			try {
				ConfigMap.INFLUXDB_ENABLED=Boolean.parseBoolean(props.getProperty("influxdb.enabled"));
				
				if(ConfigMap.INFLUXDB_ENABLED) {
					MonitoringUtils.enable();
				}else {
					MonitoringUtils.disable();
				}
				log.info("Loaded parameter influxdb.enabled="+ConfigMap.INFLUXDB_ENABLED);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.enabled parameter ["+props.getProperty("influxdb.enabled")+"]. InfluxDB disabled");
				//InfluxDB.disable();
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
				//InfluxDB.disable();
			}
			
			try {
				ConfigMap.INFLUXDB_COUNT_OF_ATTEMPTS_IF_FAILS=Integer.parseInt(props.getProperty("influxdb.attempts"));
				log.info("Loaded parameter influxdb.attempts="+ConfigMap.INFLUXDB_COUNT_OF_ATTEMPTS_IF_FAILS);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.attempts parameter ["+props.getProperty("influxdb.attempts")+"]. InfluxDB disabled");
				//InfluxDB.disable();
			}
			try {
				ConfigMap.INFLUXDB_TIMEOUT=Long.parseLong(props.getProperty("influxdb.timeout"));
				log.info("Loaded parameter influxdb.timeout="+ConfigMap.INFLUXDB_TIMEOUT);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb.timeout parameter ["+props.getProperty("influxdb.timeout")+"]. InfluxDB disabled");
				//InfluxDB.disable();
			}
			//---
			try {
				

				
//				TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
//			    SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
//			    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, 
//			      NoopHostnameVerifier.INSTANCE);
//			    
//			    Registry<ConnectionSocketFactory> socketFactoryRegistry = 
//			      RegistryBuilder.<ConnectionSocketFactory> create()
//			      .register("https", sslsf)
//			      .register("http", new PlainConnectionSocketFactory())
//			      .build();
//
//			    BasicHttpClientConnectionManager connectionManager = 
//			      new BasicHttpClientConnectionManager(socketFactoryRegistry);
//			    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
//			      .setConnectionManager(connectionManager).build();
			
			    
			    
		        
		        
				ConfigMap.INFLUXDB2_ENABLED=Boolean.parseBoolean(props.getProperty("influxdb2.enabled"));
				
				if(ConfigMap.INFLUXDB2_ENABLED) {
					MonitoringUtils.enable();
				}else {
					MonitoringUtils.disable();
				}
				log.info("Loaded parameter influxdb2.enabled="+ConfigMap.INFLUXDB2_ENABLED);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb2.enabled parameter ["+props.getProperty("influxdb2.enabled")+"]. InfluxDB disabled");
				//InfluxDB.disable();
			}
			try {
				ConfigMap.INFLUXDB2_ORGID=props.getProperty("influxdb2.orgId");
				log.info("Loaded parameter influxdb2.orgId="+ConfigMap.INFLUXDB2_ORGID);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb2.orgId parameter ["+props.getProperty("influxdb2.orgId")+"]. InfluxDB disabled");
			}
			try {
				ConfigMap.INFLUXDB2_TOKEN=props.getProperty("influxdb2.token");
				log.info("Loaded parameter influxdb2.token="+ConfigMap.INFLUXDB2_TOKEN);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb2.token parameter ["+props.getProperty("influxdb2.token")+"]. InfluxDB disabled");
			}
			try {
				ConfigMap.INFLUXDB2_URL=props.getProperty("influxdb2.url");
				log.info("Loaded parameter influxdb2.url="+ConfigMap.INFLUXDB2_URL);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb2.url parameter ["+props.getProperty("influxdb2.url")+"]. InfluxDB disabled");
			}
			try {
				ConfigMap.INFLUXDB2_BUCKET=props.getProperty("influxdb2.bucket");
				log.info("Loaded parameter influxdb2.bucket="+ConfigMap.INFLUXDB2_BUCKET);
			}catch(Exception e) {
				log.error("Incorrect format of influxdb2.bucket parameter ["+props.getProperty("influxdb2.bucket")+"]. InfluxDB disabled");
			}
					
					
			try {
				//InfluxDB.setCountOfAttemptsIfFails(10);
				//InfluxDB.setTimeoutInMillis(1000);
				//InfluxDB.getInstance(ConfigMap.INFLUXDB_HOST, ConfigMap.INFLUXDB_PORT, ConfigMap.INFLUXDB_DBNAME, ConfigMap.INFLUXDB_THREAD_NUMBER);
				//InfluxDB.getInstance(ConfigMap.INFLUXDB_HOST, ConfigMap.INFLUXDB_PORT, ConfigMap.INFLUXDB_DBNAME);
//				MonitoringUtils.init(ConfigMap.INFLUXDB_HOST, ConfigMap.INFLUXDB_PORT, ConfigMap.INFLUXDB_DBNAME);
				MonitoringUtils.init(ConfigMap.INFLUXDB2_URL, ConfigMap.INFLUXDB2_ORGID, ConfigMap.INFLUXDB2_BUCKET, ConfigMap.INFLUXDB2_TOKEN);

				log.debug("InfluXDB successfully started");
			}catch(Exception e) {
				log.error("Couldn't start InfluxDB monitoring sending", e);
			}
			log.info("InfluxDB: " + InfluxDB.getInstance());
			log.info("InfluxDB2: " + InfluxDB2.getInstance());

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
	    	if(InfluxDB.getInstance()!=null)
	    		InfluxDB.getInstance().destroy();
	        log.info("Servlet has been stopped.");
	    }
	    
	
}
