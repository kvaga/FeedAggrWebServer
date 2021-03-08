package ru.kvaga.rss.feedaggrwebserver.jobs;

import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;

public class CompositeFeedsUpdateJob implements Runnable {

	private static Logger log = LogManager.getLogger(CompositeFeedsUpdateJob.class);
	private File compositeRSSFile = null;
	private String userName = null;

	public CompositeFeedsUpdateJob() {

	}

	public void run() {
//		ArrayList<String> al = new ArrayList<String>();
//		al.add("1613078641721");
//		al.add("1613078071148");
		log.debug("CompositeFeedsUpdateJob started");

		for (File userFile : ServerUtils.getAllUserFiles()) {
			if(!userFile.getName().endsWith(".xml")) {
				continue;
			}
			try {
//				User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
				ServerUtils.updateCompositeRSSFilesOfUser(userFile.getName().replace(".xml", ""));
			} catch (Exception e) {
				log.error("Exception", e);
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

		log.debug("CompositeFeedsUpdateJob finished");

	}

}
