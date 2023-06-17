package ru.kvaga.rss.feedaggrwebserver.junit.util;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.StartStopListener;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

public class JUnitUtils {
	final private static Logger log = LogManager.getLogger(JUnitUtils.class);

	public static void serverInit() {
		StartStopListener ssl = new StartStopListener();
		ssl.contextInitialized(null);
		ConfigMap.usersPath=new File(ConfigMap.dataPath.getAbsoluteFile()+"/junit_users");
		log.info("Created parameter ConfigMap.usersPath="+ConfigMap.usersPath);
		ConfigMap.feedsPath=new File(ConfigMap.dataPath.getAbsoluteFile()+"/junit_feeds");
		log.info("Created parameter ConfigMap.feedsPath="+ConfigMap.feedsPath);
	}
	
	public static UserFeed createUserFeedTestWithoutBindingToUser() throws NoSuchAlgorithmException, JAXBException {
		String feedId = getNewFeedIdTest();
		RSS rss = createRSSTest();
		rss.saveXMLObjectToFileByFeedId(feedId);
		return new UserFeed(feedId, "itemTitleTemplate", "itemLinkTemplate", "itemContentTemplate", "repeatableSearchPattern", "filterWords", "skipWords",Long.MAX_VALUE, rss.getChannel().getTitle(), rss.getChannel().getLink());
	}
	
	public static UserFeed createUserFeedTestAndBindToUser(String userName) throws NoSuchAlgorithmException, JAXBException {
		String feedId = getNewFeedIdTest();
		RSS rss = createRSSTest();
		rss.saveXMLObjectToFileByFeedId(feedId);
		File userFile = User.getUsersFileByUserName(userName);
		User user = User.getXMLObjectFromXMLFile(userFile);
		UserFeed userFeed = new UserFeed(feedId, "itemTitleTemplate", "itemLinkTemplate", "itemContentTemplate", "repeatableSearchPattern", "filterWords", "skipWords", Long.MAX_VALUE, rss.getChannel().getTitle(), rss.getChannel().getLink());
		if(!user.getUserFeeds().add(userFeed)) {
			return null;
		}
		user.saveXMLObjectToFile(userFile);
		return userFeed; 
	}
	
	public static boolean deleteUserFeedWithUnbindingFromUser(String userName, String feedId) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		UserFeed uf = new UserFeed();
		uf.setId(feedId);
		boolean statusOfDeletionOfUserFeedFromUser = user.getUserFeeds().remove(uf);
		user.saveXMLObjectToFileByLogin();
		boolean statusOfDeletionOfFeedRSSFile = RSS.deleteRSSFile(feedId);
		return statusOfDeletionOfUserFeedFromUser && statusOfDeletionOfFeedRSSFile;
	}
	
	public static RSS createRSSTest() throws NoSuchAlgorithmException {
		String version = ConfigMap.rssVersion;
		ArrayList<Item> itemsList = new ArrayList<Item>();
		itemsList.add(createItem());
		Channel channel = new Channel("title", "link", "descriprion", new Date(), "generator", 360, itemsList);
		RSS rss = new RSS(version, channel);
		return rss;
	}
	
	public static String getNewFeedIdTest() {
		return "test_"+ServerUtils.getNewFeedId();
	}
	
	public static Item createItem() throws NoSuchAlgorithmException {
		String url = "http://yandex.ru/#"+getNewFeedIdTest();
		return new Item("title", url, "description", new GUID(ConfigMap.generator, GUID.generateGUID(url)), new Date());
	}

}
