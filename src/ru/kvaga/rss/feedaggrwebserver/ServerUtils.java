package ru.kvaga.rss.feedaggrwebserver;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ru.kvaga.rss.feedaggr.FeedAggrException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggr.objects.RSS;

public class ServerUtils {
	
	public static void main(String[] args) throws GetFeedsListByUser, JAXBException {
		for(Feed feedOnServer : getFeedsListByUser("kvaga")) {
//			System.out.println(feedOnServer.getXmlFile());
			RSS rssFeed = (RSS)ObjectsUtils.getXMLObjectFromXMLFile(feedOnServer.getXmlFile(), new RSS());
			System.out.println(rssFeed.getChannel().getTitle());
			System.out.println("Source URL: "+rssFeed.getChannel().getLink());
			System.out.println("Last updated: " + rssFeed.getChannel().getLastBuildDate());
			 
//			ObjectsUtills.printXMLObject(rssFeed);
		}
	}
	
	
	public static String getNewFeedId() {
		return ""+new Date().getTime();
	}
	
	public static ArrayList<Feed> getFeedsListByUser(String userDirPath) throws GetFeedsListByUser, JAXBException{
//		String dataDirText="WebContent/data";
//		System.out.println("CurrentDir: " + userDirPath);
//		String userDirText=String.format("%s/%s", dataDirText,user);
		ArrayList<Feed> al = new ArrayList<Feed>();
		File userDir = new File(userDirPath);
//		System.out.println("UserDir: " + userDir);

		if(!userDir.isDirectory()) {
			throw new FeedAggrException.GetFeedsListByUser(String.format("Couldn't find user's [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s", userDir, userDir.getPath(),userDir.getAbsolutePath(), new File(".").getAbsolutePath()));
		}
		for(File feedIdDir : userDir.listFiles()) {
			if(feedIdDir.isDirectory()) {
//				System.out.println("feedIdDir: " + feedIdDir);
				String feedId = feedIdDir.getName();
				File feedXmlFile = new File(String.format("%s/%s.xml", feedIdDir.getPath(),feedId,feedId));
				File feedConfigFile = new File(String.format("%s/%s.conf", feedIdDir.getPath(),feedId,feedId));
				Feed feed = new Feed(feedId, feedXmlFile, feedConfigFile);
//				System.out.println(feed);
				al.add(feed);
			}
		}
		return al;
	}
	
	public static ArrayList<Feed> getFeedsList(String realPath) throws GetFeedsListByUser, JAXBException{
		/*
//		String dataDirText="WebContent/data";
//		System.out.println("CurrentDir: " + userDirPath);
//		String userDirText=String.format("%s/%s", dataDirText,user);
		ArrayList<Feed> al = new ArrayList<Feed>();
		File dir = new File(realPath);
//		System.out.println("UserDir: " + userDir);

		if(!dir.isDirectory()) {
			throw new FeedAggrException.GetFeedsListByUser(String.format("Couldn't find feeds [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s", dir, dir.getPath(),dir.getAbsolutePath(), new File(".").getAbsolutePath()));
		}
		for(File feedFile : dir.listFiles()) {
//				System.out.println("feedIdDir: " + feedIdDir);
				String feedId = feedFile.getName().replaceAll("\\.xml", "");
				Feed feed = new Feed(feedId, feedFile, null);
//				System.out.println(feed);
				al.add(feed);
		}
		return al;
		*/
		return getFeedsList(new File(realPath));
	}
	
	public static ArrayList<Feed> getFeedsList(File dir) throws GetFeedsListByUser, JAXBException{
//		String dataDirText="WebContent/data";
//		System.out.println("CurrentDir: " + userDirPath);
//		String userDirText=String.format("%s/%s", dataDirText,user);
		ArrayList<Feed> al = new ArrayList<Feed>();
//		System.out.println("UserDir: " + userDir);

		if(!dir.isDirectory()) {
			throw new FeedAggrException.GetFeedsListByUser(String.format("Couldn't find feeds [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s", dir, dir.getPath(),dir.getAbsolutePath(), new File(".").getAbsolutePath()));
		}
		for(File feedFile : dir.listFiles()) {
//				System.out.println("feedIdDir: " + feedIdDir);
				String feedId = feedFile.getName().replaceAll("\\.xml", "");
				Feed feed = new Feed(feedId, feedFile, null);
//				System.out.println(feed);
				al.add(feed);
		}
		return al;
	}
}
