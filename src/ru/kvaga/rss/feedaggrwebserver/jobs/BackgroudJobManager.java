package ru.kvaga.rss.feedaggrwebserver.jobs;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.rss.feedaggr.Exec;

@WebListener
public class BackgroudJobManager implements ServletContextListener{
	private ScheduledExecutorService scheduler1;
	private ScheduledExecutorService scheduler2;

	final static Logger log = LogManager.getLogger(BackgroudJobManager.class);
	private static boolean jobsEnabled=true;
	public void contextInitialized (ServletContextEvent event) {
		long t1 = new Date().getTime();
		
		scheduler1 = Executors.newSingleThreadScheduledExecutor();
		scheduler2 = Executors.newSingleThreadScheduledExecutor();
		
		if(jobsEnabled) {
			scheduler1.scheduleAtFixedRate(new FeedsUpdateJob(event.getServletContext()), 0, 1, TimeUnit.HOURS);
//		scheduler1.scheduleAtFixedRate(new FeedsUpdateJob(event.getServletContext()), 0, 20, TimeUnit.SECONDS);
			log.info("BackgroudJobManager started with jobs [FeedsUpdateJob for each 1 hours]");
			scheduler2.scheduleAtFixedRate(new CompositeFeedsUpdateJob(), 0, 1, TimeUnit.HOURS);
			log.info("BackgroudJobManager started with jobs [MergeFeeds for each 1 hours]");
		}
		
//		Scheduler scheduler = new Scheduler();
//        scheduler.schedule("7 8-22 * * *", new Task());
//        scheduler.start();
//        servletContextEvent.getServletContext().setAttribute("SCHEDULER", scheduler);
		InfluxDB.getInstance().send("response_time,method=BackgroudJobManager.contextInitialized", new Date().getTime() - t1);
	}
	

    public void contextDestroyed(ServletContextEvent arg0) {
		log.info("BackgroudJobManager destroyed: " + arg0);
		scheduler1.shutdownNow();    
		scheduler2.shutdownNow();    

		}
}
