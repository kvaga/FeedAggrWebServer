package ru.kvaga.rss.feedaggrwebserver.tests;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBException;

import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.StartStopListener;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

public class JUnitUtils {

	public static void serverInit() {
		StartStopListener ssl = new StartStopListener();
		ssl.contextInitialized(null);
	}
	
	public static UserFeed getUserFeedTest() throws NoSuchAlgorithmException, JAXBException {
		String feedId = getNewFeedIdTest();
		RSS rss = createRSSTest();
		rss.saveXMLObjectToFileByFeedId(feedId);
		return new UserFeed(feedId, "itemTitleTemplate", "itemLinkTemplate", "itemContentTemplate", "repeatableSearchPattern", "filterWords", Long.MAX_VALUE);
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
