package ru.kvaga.rss.feedaggrwebserver.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.monitoring.*;import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.servlets.mergeRSSServlet;

public class CompositeFeedsUpdateJob implements Runnable {
	public static boolean isWorkingNow=false;
	private static Logger log = LogManager.getLogger(CompositeFeedsUpdateJob.class);


	private File compositeRSSFile = null;
	private String userName = null;

	public CompositeFeedsUpdateJob() {}
//	private static boolean FIRST_TIME_AFTER_RESTART=true;
	
	
	public void run() {
		long t1 = new Date().getTime();
		isWorkingNow=true;
		MonitoringUtils.sendCommonMetric("JobsWork", 1, new Tag("job", "CompositeFeedsUpdateJob"));

//		ArrayList<String> al = new ArrayList<String>();
//		al.add("1613078641721");
//		al.add("1613078071148");  
		log.debug("CompositeFeedsUpdateJob started");

		for (File userFile : User.getAllUserFiles()) {
			if(!userFile.getName().endsWith(".xml")) {
				continue;
			}
			String userName=userFile.getName().replace(".xml", "");
			try {
//				if(FIRST_TIME_AFTER_RESTART) {
//					CompositeUserFeed.fixSettingsAfterRestart(userName);
//					FIRST_TIME_AFTER_RESTART=false;
//				}
				
//				User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
				int result[] = CompositeUserFeed.updateItemsInCompositeRSSFilesOfUser(userName);
				log.debug("Processed composite feeds: all ["+result[0]+"], successful ["+result[1]+"], failed ["+result[2]+"]");
				MonitoringUtils.sendCommonMetric("CompositeUserFeedJob", result[0], new Tag("type","all"), new Tag("operation", "Processing"));
				MonitoringUtils.sendCommonMetric("CompositeUserFeedJob", result[1], new Tag("type","successful"), new Tag("operation", "Processing"));
				MonitoringUtils.sendCommonMetric("CompositeUserFeedJob", result[2], new Tag("type","failed"), new Tag("operation", "Processing"));
//				logMonitoring.info(String.format("CompositeFeedsUpdateJobMetricStatusAll %d",result[0]));
//				logMonitoring.info(String.format("CompositeFeedsUpdateJobMetricStatusSuccessful %d",result[1]));;
//				logMonitoring.info(String.format("CompositeFeedsUpdateJobMetricStatusFailed %d",result[2]));;

			} catch (Exception e) {
				log.error("CompositeFeedsUpdateJob Exception", e);
				continue;
			}finally {
			}
//				for(CompositeUserFeed compositeUserFeed : user.getCompositeUserFeeds()) {
//					ArrayList<String> al = new ArrayList<String>();
//					for(String str : compositeUserFeed.getFeedIds()) {
//						al.add(str);
//					}
////					ServerUtils.mergeRSS(null, userFile.getName().replace(".xml", ""), al, new File(ConfigMap.feedsPath+"/"+compositeUserFeed.getId()+".xml"));
//					
//				}
//				ServerUtils.mergeRSS(null, "kvaga", al, new File("C:\\eclipseWorkspace\\FeedAggrWebServer\\data\\feeds\\composite_1613763817102.xml"));
		}

		isWorkingNow=false;
		log.debug("CompositeFeedsUpdateJob finished for ["+(Exec.getHumanReadableHoursMinutesSecondsFromMilliseconds(new Date().getTime()-t1))+"]");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		MonitoringUtils.sendCommonMetric("CompositeFeedsUpdateJobMetric.JobsWork", 0, new Tag("job", "CompositeFeedsUpdateJob"));
		isWorkingNow=false;
	}

}
