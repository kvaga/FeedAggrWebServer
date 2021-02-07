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

		} catch (IOException e) {
			log.error("Can't get configuration parameters of servlet", e);
			return;
		}
	        log.info("FeedAggrWebServer initialized");
	    }

	    @Override
	    public void contextDestroyed(ServletContextEvent servletContextEvent) {
	        log.info("Servlet has been stopped.");
	    }
	    
	
}
