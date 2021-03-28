package ru.kvaga.rss.feedaggrwebserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JSpinner.DateEditor;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.jobs.CompositeFeedsUpdateJob;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;
import ru.kvaga.rss.feedaggr.objects.RSS;

public class ServerUtils {
	final private static Logger log = LogManager.getLogger(ServerUtils.class);

	public static void main(String[] args) throws GetFeedsListByUser, JAXBException, ParseException {
//		for(Feed feedOnServer : getFeedsListByUser("kvaga")) {
////			System.out.println(feedOnServer.getXmlFile());
//			RSS rssFeed = (RSS)ObjectsUtils.getXMLObjectFromXMLFile(feedOnServer.getXmlFile(), new RSS());
//			System.out.println(rssFeed.getChannel().getTitle());
//			System.out.println("Source URL: "+rssFeed.getChannel().getLink());
//			System.out.println("Last updated: " + rssFeed.getChannel().getLastBuildDate());
//			 
////			ObjectsUtills.printXMLObject(rssFeed);
//		}
	
	}
	public static synchronized String getNewFeedId() {
		return "" + new Date().getTime();
	}

	public static synchronized void deleteUserFeedByIdFromUser(String feedId, String userName) throws Exception {
		log.debug("Trying to delete feed id [" + feedId + "] for user [" + userName + "]");
		HashSet<UserFeed> userFeedNew = new HashSet<UserFeed>();
		File userConfigFile = new File(ConfigMap.usersPath + "/" + userName + ".xml");
		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userConfigFile, new User());
		log.debug("Successfully read file [" + userConfigFile + "]");
		for (UserFeed feed : user.getUserFeeds()) {
			if (feed.getId().equals(feedId)) {
				continue;
			}
			userFeedNew.add(feed);
		}
		log.debug("Created new list without [" + feedId + "] feed");
		user.setUserFeeds(userFeedNew);
		ObjectsUtils.saveXMLObjectToFile(user, user.getClass(), userConfigFile);
		log.debug("File [" + userConfigFile + "] successfully updated");
	}
	public static synchronized void deleteCompositeUserFeedByIdFromUser(String compositeFeedId, String userName) throws Exception {
		log.debug("Trying to delete composite feed id [" + compositeFeedId + "] for user [" + userName + "]");
		HashSet<CompositeUserFeed> userFeedNew = new HashSet<CompositeUserFeed>();
		File userConfigFile = new File(ConfigMap.usersPath + "/" + userName + ".xml");
		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userConfigFile, new User());
		log.debug("Successfully read file [" + userConfigFile + "]");
		for (CompositeUserFeed feed : user.getCompositeUserFeeds()) {
			if (feed.getId().equals(compositeFeedId)) {
				continue;
			}
			userFeedNew.add(feed);
		}
		log.debug("Created new composite list without [" + compositeFeedId + "] feed");
		user.setCompositeUserFeeds(userFeedNew);
		ObjectsUtils.saveXMLObjectToFile(user, user.getClass(), userConfigFile);
		log.debug("File [" + userConfigFile + "] successfully updated");
	}

	public static synchronized ArrayList<UserFeed> getUserFeedListByUser(String userName) throws Exception {
//		String dataDirText="WebContent/data";
//		System.out.println("CurrentDir: " + userDirPath);
//		String userDirText=String.format("%s/%s", dataDirText,user);
		ArrayList<Feed> al = new ArrayList<Feed>();
//		System.out.println("UserDir: " + userDir);
		log.debug("Getting user configuration file");
		File userConfigFile = new File(ConfigMap.usersPath.getAbsolutePath() + "/" + userName + ".xml");
		if (!userConfigFile.exists()) {
			throw new Exception("Configuration file of user [" + userName + "] doesn't exist");
		}
		log.debug("Getting Feeds list from the file [" + userConfigFile.getAbsolutePath() + "]");

		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userConfigFile, new User());
		return (ArrayList<UserFeed>) user.getUserFeeds();
	}

	public static synchronized ArrayList<Feed> getFeedsList(String realPath) throws GetFeedsListByUser, JAXBException {
		/*
		 * // String dataDirText="WebContent/data"; // System.out.println("CurrentDir: "
		 * + userDirPath); // String userDirText=String.format("%s/%s",
		 * dataDirText,user); ArrayList<Feed> al = new ArrayList<Feed>(); File dir = new
		 * File(realPath); // System.out.println("UserDir: " + userDir);
		 * 
		 * if(!dir.isDirectory()) { throw new
		 * FeedAggrException.GetFeedsListByUser(String.
		 * format("Couldn't find feeds [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s"
		 * , dir, dir.getPath(),dir.getAbsolutePath(), new
		 * File(".").getAbsolutePath())); } for(File feedFile : dir.listFiles()) { //
		 * System.out.println("feedIdDir: " + feedIdDir); String feedId =
		 * feedFile.getName().replaceAll("\\.xml", ""); Feed feed = new Feed(feedId,
		 * feedFile, null); // System.out.println(feed); al.add(feed); } return al;
		 */
		return getFeedsList(new File(realPath));
	}

	public static synchronized ArrayList<Feed> getFeedsList(File dir) throws GetFeedsListByUser, JAXBException {
//		String dataDirText="WebContent/data";
//		System.out.println("CurrentDir: " + userDirPath);
//		String userDirText=String.format("%s/%s", dataDirText,user);
		ArrayList<Feed> al = new ArrayList<Feed>();
//		System.out.println("UserDir: " + userDir);
		log.debug("Searching Feed in the [" + dir + "] directory");
		if (!dir.isDirectory()) {
			throw new FeedAggrException.GetFeedsListByUser(String.format(
					"Couldn't find feeds [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s",
					dir, dir.getPath(), dir.getAbsolutePath(), new File(".").getAbsolutePath()));
		}
		for (File feedFile : dir.listFiles()) {
//				System.out.println("feedIdDir: " + feedIdDir);
			String feedId = feedFile.getName().replaceAll("\\.xml", "");
			Feed feed = new Feed(feedId, feedFile, null);
//				System.out.println(feed);
			al.add(feed);
		}
		return al;
	}

	public static synchronized Feed getFeedById(String feedId) throws GetFeedsListByUser, JAXBException {
		log.debug("Searching Feed by id [" + feedId + "]");
		for (Feed feed : getFeedsList(ConfigMap.feedsPath)) {
			log.debug("f_id: " + feed.getId());
			if (feed.getId().equals(feedId)) {
				log.debug("Feed with id [" + feedId + "] successfully found");
				return feed;
			}
		}
		log.warn("Feed id wasn't found");
		return null;
	}

	public static synchronized Feed getFeedByUserAndId(String userName, String feedId) throws Exception {
		throw new Exception("Unimplemented method");
	}

	public static synchronized void deleteFeed(String feedId, String userName) throws Exception {
		if(feedId.startsWith("composite_")) {
			deleteCompositeUserFeedByIdFromUser(feedId, userName);
		}else {
			deleteUserFeedByIdFromUser(feedId, userName);
		}
		File feedFile = new File(ConfigMap.feedsPath + "/" + feedId + ".xml");

		log.debug("Trying to delete feed file [" + feedFile.getAbsolutePath() + "]");
		feedFile.delete();
		log.debug("Feed file [" + feedFile.getAbsolutePath() + "] deleted");
//		Feed feed = getFeedByUserAndId(feedId);
//		if(feed!=null) {
//			log.debug("Trying to delete Feed with id ["+feedId+"], feed xml file ["+feed.getXmlFile()+"] and update users file ["+feed.getConfFile()+"]");
//			
//			
//			User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(feed.getConfFile(), new User());
//	        HashSet<UserFeed> userFeeds = (HashSet<UserFeed>) user.getUserFeeds();
//	        for(UserFeed userFeed : userFeeds) {
//	        	if(userFeed.getId().equals(feedId)) {
//	        		userFeeds.remove(userFeed);
//	        		log.debug("User feed ["+feedId+"] was found and deleted from the list");
//	        		break;
//	        	}
//	        }
//	        feed.getXmlFile().delete();
//			log.debug("File ["+feed.getXmlFile()+"] was deleted");
//			ObjectsUtils.saveXMLObjectToFile(user, user.getClass(), feed.getConfFile());
//			log.debug("Users file "+feed.getConfFile()+" was updated");
//		}else {
//			throw new Exception("couldn't find Feed for feed id ["+feedId+"]");
//		}
	}

	public static synchronized final String escapeHTML(String s) {
		StringBuffer sb = new StringBuffer();
		int n = s.length();
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case 'à':
				sb.append("&agrave;");
				break;
			case 'À':
				sb.append("&Agrave;");
				break;
			case 'â':
				sb.append("&acirc;");
				break;
			case 'Â':
				sb.append("&Acirc;");
				break;
			case 'ä':
				sb.append("&auml;");
				break;
			case 'Ä':
				sb.append("&Auml;");
				break;
			case 'å':
				sb.append("&aring;");
				break;
			case 'Å':
				sb.append("&Aring;");
				break;
			case 'æ':
				sb.append("&aelig;");
				break;
			case 'Æ':
				sb.append("&AElig;");
				break;
			case 'ç':
				sb.append("&ccedil;");
				break;
			case 'Ç':
				sb.append("&Ccedil;");
				break;
			case 'é':
				sb.append("&eacute;");
				break;
			case 'É':
				sb.append("&Eacute;");
				break;
			case 'è':
				sb.append("&egrave;");
				break;
			case 'È':
				sb.append("&Egrave;");
				break;
			case 'ê':
				sb.append("&ecirc;");
				break;
			case 'Ê':
				sb.append("&Ecirc;");
				break;
			case 'ë':
				sb.append("&euml;");
				break;
			case 'Ë':
				sb.append("&Euml;");
				break;
			case 'ï':
				sb.append("&iuml;");
				break;
			case 'Ï':
				sb.append("&Iuml;");
				break;
			case 'ô':
				sb.append("&ocirc;");
				break;
			case 'Ô':
				sb.append("&Ocirc;");
				break;
			case 'ö':
				sb.append("&ouml;");
				break;
			case 'Ö':
				sb.append("&Ouml;");
				break;
			case 'ø':
				sb.append("&oslash;");
				break;
			case 'Ø':
				sb.append("&Oslash;");
				break;
			case 'ß':
				sb.append("&szlig;");
				break;
			case 'ù':
				sb.append("&ugrave;");
				break;
			case 'Ù':
				sb.append("&Ugrave;");
				break;
			case 'û':
				sb.append("&ucirc;");
				break;
			case 'Û':
				sb.append("&Ucirc;");
				break;
			case 'ü':
				sb.append("&uuml;");
				break;
			case 'Ü':
				sb.append("&Uuml;");
				break;
			case '®':
				sb.append("&reg;");
				break;
			case '©':
				sb.append("&copy;");
				break;
			case '€':
				sb.append("&euro;");
				break;
			// be carefull with this one (non-breaking whitee space)
			case ' ':
				sb.append("&nbsp;");
				break;

			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	public static synchronized  String stringToHTMLString(String string) {
		StringBuffer sb = new StringBuffer(string.length());
		// true if last char was blank
		boolean lastWasBlankChar = false;
		int len = string.length();
		char c;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);
			if (c == ' ') {
				// blank gets extra work,
				// this solves the problem you get if you replace all
				// blanks with &nbsp;, if you do that you loss
				// word breaking
				if (lastWasBlankChar) {
					lastWasBlankChar = false;
					sb.append("&nbsp;");
				} else {
					lastWasBlankChar = true;
					sb.append(' ');
				}
			} else {
				lastWasBlankChar = false;
				//
				// HTML Special Chars
				if (c == '"')
					sb.append("&quot;");
				else if (c == '&')
					sb.append("&amp;");
				else if (c == '<')
					sb.append("&lt;");
				else if (c == '>')
					sb.append("&gt;");
				else if (c == '\n')
					// Handle Newline
					sb.append("&lt;br/&gt;");
				else {
					int ci = 0xffff & c;
					if (ci < 160)
						// nothing special only 7 Bit
						sb.append(c);
					else {
						// Not 7 Bit use the unicode system
						sb.append("&#");
						sb.append(new Integer(ci).toString());
						sb.append(';');
					}
				}
			}
		}
		return sb.toString();
	}

	public static synchronized  Object getSessionAttribute(HttpServletRequest request, String attr) {
		return request.getAttribute(attr);
	}

	public static synchronized String encodeString(String text, String encoding) throws IOException {
		BufferedReader br = new BufferedReader(
		        new InputStreamReader(
		          new ByteArrayInputStream(text.getBytes())
		          , Charset.forName(encoding)
		          ));
		String s;
		StringBuilder sb = new StringBuilder();
		 while((s=br.readLine())!=null) {
			 sb.append(s);
			 sb.append("\n");
		 }
		 return sb.toString();
	}
	
	public static synchronized  String convertStringToUTF8(String str) {
//		ByteBuffer buffer = StandardCharsets.UTF_8.encode(str); 
//		return StandardCharsets.UTF_8.decode(buffer).toString();
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public static synchronized  ArrayList<File> getAllUserFiles() {
		ArrayList<File> al = new ArrayList<File>();
		for (File file : ConfigMap.usersPath.listFiles()) {
			al.add(file);
		}
		return al;
	}

	public static synchronized void updateCompositeRSS(String feedId, String userName, String compositeRSSTitle, ArrayList<String> feedIdList) throws Exception {
		createCompositeRSS(feedId, userName, compositeRSSTitle, feedIdList);
	}
	public static synchronized void createCompositeRSS(String userName, String compositeRSSTitle, ArrayList<String> feedIdList) {
		createCompositeRSS(userName, compositeRSSTitle, feedIdList);
	}
	public static synchronized void createCompositeRSS(String feedId, String userName, String compositeRSSTitle, ArrayList<String> feedIdList)
			throws Exception {
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + userName + ".xml");
		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
		String compositeFeedId = null;
		if(feedId==null) {
			compositeFeedId="composite_" + ServerUtils.getNewFeedId();
		}else {
			compositeFeedId=feedId;
		}
		File compositeRSSFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + compositeFeedId + ".xml");

		// Check does user have all needed feed ids
		for (String feedIdFromList : feedIdList) {
			if (!user.containsFeedId(feedIdFromList)) {
				throw new Exception("User [" + userName + "] doesn't have feed id [" + feedIdFromList + "]");
			}
		}

		// old compatibility
		if (user.getCompositeUserFeeds() == null) {
			user.setCompositeUserFeeds(new HashSet<CompositeUserFeed>());
		}

		// Creating new CompositeUserFeed and adding to user
		CompositeUserFeed compositeUserFeed = new CompositeUserFeed();
		compositeUserFeed.setId(compositeFeedId);
		
		if(user.removeCompositeUserFeedById(compositeFeedId)) {
				log.debug("Removed previous version of composite feed ["+compositeFeedId+"]");
				System.err.println("Removed previous version of composite feed ["+compositeFeedId+"]");
				
				
		}
		for(CompositeUserFeed f : user.getCompositeUserFeeds()) {
			System.err.println("f:" + f.getId());
		}
		System.err.println("size: " + user.getCompositeUserFeeds().size());
		for (String feedIdFromList : feedIdList) {
//			if(compositeUserFeed.doesHaveCompositeFeedId(feedIdFromList)) {
//				if(compositeUserFeed.getFeedIds().remove((String)feedIdFromList)) {
//					log.debug("compositeUserFeed already had feed id ["+feedIdFromList+"], therefore it was removed");
//					System.err.println("compositeUserFeed already had feed id ["+feedIdFromList+"], therefore it was removed");
//				}
//			}
			compositeUserFeed.getFeedIds().add(feedIdFromList);
			log.debug("Feed id ["+feedIdFromList+"] was added to composite feed");
		}
		
		user.getCompositeUserFeeds().add(compositeUserFeed);
		log.debug("Composite feed ["+compositeFeedId+"] was added to the ["+user.getName()+"] user");


		// Creating new composite rss and channel
		RSS compositeRSS = new RSS();
		compositeRSS.setVersion(ConfigMap.rssVersion);
		Channel compositeChannel = new Channel();
		compositeChannel.setTitle(compositeRSSTitle);
		compositeChannel.setDescription(compositeRSSTitle);
		compositeChannel.setGenerator(ConfigMap.generator);
		compositeChannel.setLastBuildDate(new Date());
		compositeChannel.setLink("_link");
		compositeChannel.setTtl(360);
		compositeRSS.setChannel(compositeChannel);
		if(feedId==null) {
			log.debug("Created new compositeRSSFile [" + compositeRSSFile.getAbsolutePath() + "]");
		}else {
			log.debug("Updated compositeRSSFile [" + compositeRSSFile.getAbsolutePath() + "]");
		}
		// Storing composite RSS and USer to files
		ObjectsUtils.saveXMLObjectToFile(compositeRSS, compositeRSS.getClass(), compositeRSSFile);
		log.debug("Composite RSS was successfully saved to the file [" + compositeRSSFile.getAbsolutePath() + "]");
		ObjectsUtils.saveXMLObjectToFile(user, user.getClass(), userFile);
		log.debug("User's ["+userName+"] configuration was successfully saved to the file [" + userFile.getAbsolutePath() + "]");
	}

	public static synchronized void updateCompositeRSSFilesOfUser(String userName) throws JAXBException {
		log.info("Started proccess updateCompositeRSSFilesOfUser for user ["+userName+"]");
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + userName + ".xml");
		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());

		for (CompositeUserFeed compositeUserFeed : user.getCompositeUserFeeds()) {
			File compositeRSSFile = new File(
					ConfigMap.feedsPath.getAbsoluteFile() + "/" + compositeUserFeed.getId() + ".xml");
			RSS compositeRSS = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(compositeRSSFile, new RSS());
			try {
				for (String feedId : compositeUserFeed.getFeedIds()) {
					File xmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + feedId + ".xml");

					RSS rss = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(xmlFile, new RSS());
					log.debug("Got rss from the file [" + xmlFile.getAbsolutePath() + "] with ["
							+ rss.getChannel().getItem().size() + "] items");
					for (Item item : rss.getChannel().getItem()) {
						if (!compositeRSS.getChannel().containsItem(item)) {
							compositeRSS.getChannel().getItem().add(item);
							log.debug("Added item [" + item.getTitle() + "] to the composite items list");
						}
					}
				}
				compositeRSS.getChannel().setLastBuildDate(new Date());
			} catch (Exception e) {
				log.error("Exception", e);
				continue;
			}
			ObjectsUtils.saveXMLObjectToFile(compositeRSS, compositeRSS.getClass(), compositeRSSFile);
		}

	}

	// If compositeRSSFile = null then create a new file
	public static synchronized void mergeRSS(String compositeRSSTitle, String userName, ArrayList<String> feedIdList,
			File compositeRSSFile) throws Exception {
		log.debug("Merge RSS request for feed id list: " + feedIdList + " and user [" + userName + "]");
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + userName + ".xml");
		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
		log.debug("Got configuration of user [" + userName + "]");
		RSS compositeRSS = null;
		String compositeFeedId = null;
		if (compositeRSSFile == null) {
			compositeFeedId = "composite_" + ServerUtils.getNewFeedId();
			compositeRSSFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + compositeFeedId + ".xml");
			compositeRSS = new RSS();
			compositeRSS.setVersion(ConfigMap.rssVersion);
			Channel compositeChannel = new Channel();
			compositeChannel.setTitle(compositeRSSTitle);
			compositeChannel.setDescription(compositeRSSTitle);
			compositeChannel.setGenerator(ConfigMap.generator);
			compositeChannel.setLastBuildDate(new Date());
			compositeChannel.setLink("_link");
			compositeChannel.setTtl(360);
			compositeRSS.setChannel(compositeChannel);

			log.debug("Created new compositeRSSFile [" + compositeRSSFile.getAbsolutePath() + "]");
		} else {
			compositeRSS = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(compositeRSSFile, new RSS());
			compositeFeedId = compositeRSSFile.getName().replace(".xml", "");
			log.debug("compositeRSSFile [" + compositeRSSFile.getAbsolutePath() + "] already exists. Use this file");
		}
		if (compositeRSS.getChannel() == null) {
			throw new Exception(
					"Unable to find channel in the compositeRSS and file [" + compositeRSSFile.getAbsolutePath() + "]");
		}

		log.debug("Composite RSS contains [" + compositeRSS.getChannel().getItem().size() + "] items");
		/* TODO: implement checking each feed id belongs to user */

		if (compositeFeedId == null) {
			throw new Exception("compositeFeedId can't be null");
		}

//		ArrayList<RSS> compositeListRSS = new ArrayList<RSS>();
		log.debug("Start of feedIdList processing");
		for (String feedId : feedIdList) {
			if (!user.containsFeedId(feedId)) {
				throw new Exception("User [" + userName + "] doesn't have feed id [" + feedId + "]");
			}
			File xmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + feedId + ".xml");
			RSS rss;
			try {
				rss = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(xmlFile, new RSS());
				log.debug("Got rss from the file [" + xmlFile.getAbsolutePath() + "] with ["
						+ rss.getChannel().getItem().size() + "] items");
			} catch (JAXBException e) {
				log.error("Can't read file [" + xmlFile.getAbsolutePath() + "] to get RSS for merging. "
						+ "Continue to next file", e);
				continue;
			}

			for (Item item : rss.getChannel().getItem()) {
				if (!compositeRSS.getChannel().containsItem(item)) {
					compositeRSS.getChannel().getItem().add(item);
					log.debug("Added item [" + item.getTitle() + "] to the composite items list");
				}
			}
		}

		/*
		 * if(!user.doesHaveCompositeFeedId(compositeFeedId)) { CompositeUserFeed
		 * compositeUserFeed = new CompositeUserFeed();
		 * compositeUserFeed.setId(compositeFeedId); for(String feedId : feedIdList) {
		 * compositeUserFeed.getFeedIds().add(feedId);
		 * log.debug("Added feedId ["+feedId+"] to newly created compositeUserFeed ["
		 * +compositeUserFeed.getId()+"]"); }
		 * user.getCompositeUserFeeds().add(compositeUserFeed); }else {
		 * for(CompositeUserFeed compositeUserFeed : user.getCompositeUserFeeds()) {
		 * if(compositeUserFeed.getId().equals(compositeFeedId)) { for(String feedId :
		 * feedIdList) { compositeUserFeed.getFeedIds().add(feedId);
		 * log.debug("Added feedId ["+feedId+"] to compositeUserFeed ["
		 * +compositeUserFeed.getId()+"]"); } } } }
		 */
		ObjectsUtils.saveXMLObjectToFile(compositeRSS, compositeRSS.getClass(), compositeRSSFile);
		ObjectsUtils.saveXMLObjectToFile(user, user.getClass(), userFile);

		log.debug("Composite RSS was successfully saved to the file [" + compositeRSSFile.getAbsolutePath() + "]");
	}
}
