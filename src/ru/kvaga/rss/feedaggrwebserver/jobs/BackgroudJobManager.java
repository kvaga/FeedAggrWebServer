package ru.kvaga.rss.feedaggrwebserver.jobs;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.Exec;

@WebListener
public class BackgroudJobManager implements ServletContextListener{
	private ScheduledExecutorService scheduler;
	final static Logger log = LogManager.getLogger(BackgroudJobManager.class);

	public void contextInitialized (ServletContextEvent event) {
//		Exec.sleep(10000);

//		for(Object prop : System.getProperties().entrySet()) {
//			log.debug("===========: "+System.getProperty("CATALINA_BASE"));
//		}
		scheduler = Executors.newSingleThreadScheduledExecutor();
//		scheduler.scheduleAtFixedRate(new FeedsUpdateJob(event.getServletContext()), 0, 15, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(new FeedsUpdateJob(event.getServletContext()), 0, 4, TimeUnit.HOURS);
		log.info("BackgroudJobManager started with jobs [FeedsUpdateJob for each 4 hours]");
		
//		System.out.println("BackgroudJobManager started: " + event);
//		Scheduler scheduler = new Scheduler();
//        scheduler.schedule("7 8-22 * * *", new Task());
//        scheduler.start();
//        servletContextEvent.getServletContext().setAttribute("SCHEDULER", scheduler);
	}
	

    public void contextDestroyed(ServletContextEvent arg0) {
		log.info("BackgroudJobManager destroyed: " + arg0);
		scheduler.shutdownNow();    }
}
