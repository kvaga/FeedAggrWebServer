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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.cache.CacheCompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.cache.CacheUserFeed;
import ru.kvaga.rss.feedaggrwebserver.jobs.CompositeFeedsUpdateJob;
import ru.kvaga.rss.feedaggrwebserver.monitoring.MonitoringUtils;
import ru.kvaga.rss.feedaggrwebserver.monitoring.Tag;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserRepeatableSearchPattern;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserRssItemPropertiesPatterns;
import ru.kvaga.rss.feedaggr.objects.RSS;

public class ServerUtils {
	final private static Logger log = LogManager.getLogger(ServerUtils.class);


	public static synchronized String getNewFeedId() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			log.error(e);
		}
		return "" + new Date().getTime();
	}

	
	public static synchronized void clearSessionFromFeedAttributes(javax.servlet.http.HttpServletRequest request) {
		long t1 = new Date().getTime();
		StringBuilder sb = new StringBuilder();
		String removeAttributes[] = {
			"responseHtmlBody",
			"url",
			"dataClippedBol",
			"feedTitle",
			"feedDescription",
			"feedId",
			"repeatableSearchPattern",
			"itemTitleTemplate",
			"itemLinkTemplate",
			"itemContentTemplate",
			"filterWords",
			"durationUpdate",
			"suspendingStatus"
		};
		boolean firstIteration=true;
		for(String item : removeAttributes) {
			request.getSession().removeAttribute(item);
			if(!firstIteration) {
				sb.append(", ");
			}
			sb.append(item);
			firstIteration=false;
		}
		log.debug("Session cleared from feed attributes: " + sb.toString());
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

	}
	public static synchronized ArrayList<UserFeed> getUserFeedListByUser(String userName) throws Exception {
		long t1 = new Date().getTime();
		ArrayList<Feed> al = new ArrayList<Feed>();
		log.debug("Getting user configuration file");
		File userConfigFile = new File(ConfigMap.usersPath.getAbsolutePath() + "/" + userName + ".xml");
		if (!userConfigFile.exists()) {
			//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new Exception("Configuration file of user [" + userName + "] doesn't exist");
		}
		log.debug("Getting Feeds list from the file [" + userConfigFile.getAbsolutePath() + "]");

//		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userConfigFile, new User());
		User user = User.getXMLObjectFromXMLFile(userConfigFile);
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		ArrayList<UserFeed> userFeedsArrayList = new ArrayList<UserFeed>();
		for(Object uf : user.getUserFeeds().toArray()) {
			userFeedsArrayList.add((UserFeed)uf);
		}
		//return (ArrayList<UserFeed>) user.getUserFeeds();
		return userFeedsArrayList;
	}

	private static synchronized ArrayList<Feed> getFeedsList(String realPath) throws GetFeedsListByUser, JAXBException {
		/*
		 * // String dataDirText="WebContent/data";  
		 * // String userDirText=String.format("%s/%s",
		 * dataDirText,user); ArrayList<Feed> al = new ArrayList<Feed>(); File dir = new
		 * File(realPath); 
		 * 
		 * if(!dir.isDirectory()) { throw new
		 * FeedAggrException.GetFeedsListByUser(String.
		 * format("Couldn't find feeds [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s"
		 * , dir, dir.getPath(),dir.getAbsolutePath(), new
		 * File(".").getAbsolutePath())); } for(File feedFile : dir.listFiles()) { //
		 * feedFile.getName().replaceAll("\\.xml", ""); Feed feed = new Feed(feedId,
		 * feedFile, null); 
		 * //  al.add(feed); 
		 * } 
		 * return al;
		 */
		return getFeedsList(new File(realPath));
	}
	

	
	/**
	 * @param commonFeeds includes common feeds
	 * @param compositeFeeds includes composite feeds
	 * @return
	 * @throws GetFeedsListByUser
	 * @throws JAXBException
	 */
	public static synchronized ArrayList<Feed> getFeedsList(boolean commonFeeds, boolean compositeFeeds) throws GetFeedsListByUser, JAXBException {
		return getFeedsList(ConfigMap.feedsPath, commonFeeds, compositeFeeds);
	}
	
	
	private static synchronized ArrayList<Feed> getFeedsList(File dir) throws GetFeedsListByUser, JAXBException {
		return getFeedsList(dir, true, true);
	}
	

	/**
	 * Get a list of all feeds (feeds + composite) for the specific location
	 * @param dir
	 * @return
	 * @throws GetFeedsListByUser
	 * @throws JAXBException
	 */
	private static synchronized ArrayList<Feed> getFeedsList(File dir, boolean commonFeeds, boolean compositeFeeds) throws GetFeedsListByUser, JAXBException {
		long t1 = new Date().getTime();
//		String dataDirText="WebContent/data";
//		String userDirText=String.format("%s/%s", dataDirText,user);
		ArrayList<Feed> al = new ArrayList<Feed>();
		log.debug("Searching Feed in the [" + dir + "] directory for commonFeeds ["+commonFeeds+"] and compositeFeeds ["+compositeFeeds+"]");
		if (!dir.isDirectory()) {
			//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

			throw new FeedAggrException.GetFeedsListByUser(String.format(
					"Couldn't find feeds [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s",
					dir, dir.getPath(), dir.getAbsolutePath(), new File(".").getAbsolutePath()));
		}
		for (File feedFile : dir.listFiles()) {
			String feedId = feedFile.getName().replaceAll("\\.xml", "");
			if(commonFeeds && !feedId.startsWith("composite_")) {
				Feed feed = new Feed(feedId, feedFile, null);
				al.add(feed);
			}else if(compositeFeeds && feedId.startsWith("composite_")) {
				Feed feed = new Feed(feedId, feedFile, null);
				al.add(feed);
			}
		}
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return al;
	}
	
	

	public static synchronized Feed getFeedById(String feedId) throws GetFeedsListByUser, JAXBException {
		long t1 = new Date().getTime();
		log.debug("Searching Feed by id [" + feedId + "]");
		for (Feed feed : getFeedsList(ConfigMap.feedsPath)) {
			log.debug("f_id: " + feed.getId());
			if (feed.getId().equals(feedId)) {
				log.debug("Feed with id [" + feedId + "] successfully found");
				//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

				return feed;
			}
		}
		log.warn("Feed id wasn't found");
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

		return null;
	}

	public static synchronized Feed getFeedByUserAndId(String userName, String feedId) throws Exception {
		throw new Exception("Unimplemented method");
	}

	

	public static synchronized final String escapeHTML(String s) {
		long t1 = new Date().getTime();
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
			case ':':
				sb.append("%3A");
				break;
			case '[':
				sb.append("%5B");
				break;
			case ']':
				sb.append("%5D");
				break;
			/*case 'à':
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
				break;*/
			// be carefull with this one (non-breaking whitee space)
			case ' ':
				sb.append("&nbsp;");
				break;

			default:
				sb.append(c);
				break;
			}
		}//Composite Feeds Export
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return sb.toString();
	}

	public static synchronized  String stringToHTMLString(String string) {
		long t1 = new Date().getTime();
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
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return sb.toString();
	}

	public static synchronized  Object getSessionAttribute(HttpServletRequest request, String attr) {
		return request.getAttribute(attr);
	}

	public static synchronized String encodeString(String text, String encoding) throws IOException {
		long t1 = new Date().getTime();
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
		// MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		 return sb.toString();
	}
	
	public static synchronized  String convertStringToUTF8(String str) {
//		ByteBuffer buffer = StandardCharsets.UTF_8.encode(str); 
//		return StandardCharsets.UTF_8.decode(buffer).toString();
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

		return new String(bytes, StandardCharsets.UTF_8);
	}

	
/*
	public static synchronized void updateCompositeRSS(String feedId, String userName, String compositeRSSTitle, ArrayList<String> feedIdList, boolean appendFeedIdsToComposite) throws Exception {
		createCompositeRSS(feedId, userName, compositeRSSTitle, feedIdList, appendFeedIdsToComposite);
	}
	
	public static synchronized void updateCompositeRSS(String feedId, String userName, String compositeRSSTitle, ArrayList<String> feedIdList) throws Exception {
		createCompositeRSS(feedId, userName, compositeRSSTitle, feedIdList, false);
	}
	public static synchronized String createCompositeRSS(String userName, String compositeRSSTitle, ArrayList<String> feedIdList) throws Exception {
		return createCompositeRSS(null, userName, compositeRSSTitle, feedIdList, false);
	}
	@Deprecated
	public static synchronized String createCompositeRSS(String feedId, String userName, String compositeRSSTitle, ArrayList<String> feedIdList, boolean appendFeedIdsToComposite)
			throws Exception {
		long t1 = new Date().getTime();
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + userName + ".xml");
//		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());

		User user = User.getXMLObjectFromXMLFile(userFile);
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
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				throw new Exception("User [" + userName + "] doesn't have feed id [" + feedIdFromList + "]");
			}
		}

		// old compatibility
		if (user.getCompositeUserFeeds() == null) {
			user.setCompositeUserFeeds(new HashSet<CompositeUserFeed>());
		}

		// Creating new CompositeUserFeed and adding to user
		CompositeUserFeed compositeUserFeed = null;
		if(!appendFeedIdsToComposite) {
			if(user.removeCompositeUserFeedById(compositeFeedId)) {
					log.debug("Removed previous version of composite feed ["+compositeFeedId+"]");
					//System.err.println("Removed previous version of composite feed ["+compositeFeedId+"]");		
			}
			compositeUserFeed = new CompositeUserFeed();
			compositeUserFeed.setId(compositeFeedId);
		}else {
			compositeUserFeed=user.getCompositeUserFeedById(compositeFeedId);
		}
//		for(CompositeUserFeed f : user.getCompositeUserFeeds()) {
//			System.err.println("f:" + f.getId());
//		}
//		System.err.println("size: " + user.getCompositeUserFeeds().size());
		for (String feedIdFromList : feedIdList) {
//			if(compositeUserFeed.doesHaveCompositeFeedId(feedIdFromList)) {
//				if(compositeUserFeed.getFeedIds().remove((String)feedIdFromList)) {
//					log.debug("compositeUserFeed already had feed id ["+feedIdFromList+"], therefore it was removed");
//				}
//			}
			compositeUserFeed.getFeedIds().add(feedIdFromList);
			log.debug("Feed id ["+feedIdFromList+"] was added to composite feed");
		}
		
		user.removeCompositeUserFeedById(compositeFeedId);
		if(user.getCompositeUserFeeds().add(compositeUserFeed)) {
			log.debug("Composite feed ["+compositeFeedId+"] was added to the ["+user.getName()+"] user");
		}else {
			log.error("Couldn't add composite user feed ["+compositeFeedId+"] to the ["+user.getName()+"] user");
		}

		// Creating new composite rss and channel
		RSS compositeRSS = null;
		
		if(feedId==null) {
			compositeRSS = 	new RSS();
			compositeRSS.setVersion(ConfigMap.rssVersion);
			Channel compositeChannel = new Channel();
			compositeChannel.setTitle(appendFeedIdsToComposite? RSS.getRSSObjectByFeedId(compositeFeedId).getChannel().getTitle():compositeRSSTitle);
			compositeChannel.setDescription(compositeRSSTitle);
			compositeChannel.setGenerator(ConfigMap.generator);
			compositeChannel.setLastBuildDate(new Date());
			compositeChannel.setLink("_link");
			compositeChannel.setTtl(360);
			compositeRSS.setChannel(compositeChannel);
			log.debug("Created new compositeRSSFile [" + compositeRSSFile.getAbsolutePath() + "]");
			compositeRSS.saveXMLObjectToFile(compositeRSSFile);
		}

		// Storing composite RSS and USer to files
		log.debug("Composite RSS was successfully saved to the file [" + compositeRSSFile.getAbsolutePath() + "]");
		user.saveXMLObjectToFile(userFile);
		log.debug("User's ["+userName+"] configuration was successfully saved to the file [" + userFile.getAbsolutePath() + "]");
		
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return compositeFeedId;
	}

	*/

	
	// If compositeRSSFile = null then create a new file
//	public static synchronized void mergeRSS(String compositeRSSTitle, String userName, ArrayList<String> feedIdList,
//			File compositeRSSFile) throws Exception {
//		long t1 = new Date().getTime();
//		log.debug("Merge RSS request for feed id list: " + feedIdList + " and user [" + userName + "]");
//		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + userName + ".xml");
////		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
//		User user = User.getXMLObjectFromXMLFile(userFile);
//		log.debug("Got configuration of user [" + userName + "]");
//		RSS compositeRSS = null;
//		String compositeFeedId = null;
//		if (compositeRSSFile == null) {
//			compositeFeedId = "composite_" + ServerUtils.getNewFeedId();
//			compositeRSSFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + compositeFeedId + ".xml");
//			compositeRSS = new RSS();
//			compositeRSS.setVersion(ConfigMap.rssVersion);
//			Channel compositeChannel = new Channel();
//			compositeChannel.setTitle(compositeRSSTitle);
//			compositeChannel.setDescription(compositeRSSTitle);
//			compositeChannel.setGenerator(ConfigMap.generator);
//			compositeChannel.setLastBuildDate(new Date());
//			compositeChannel.setLink("_link");
//			compositeChannel.setTtl(360);
//			compositeRSS.setChannel(compositeChannel);
//
//			log.debug("Created new compositeRSSFile [" + compositeRSSFile.getAbsolutePath() + "]");
//		} else {
////			compositeRSS = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(compositeRSSFile, new RSS());
//			compositeRSS = RSS.getRSSObjectFromXMLFile(compositeRSSFile);
//			compositeFeedId = compositeRSSFile.getName().replace(".xml", "");
//			log.debug("compositeRSSFile [" + compositeRSSFile.getAbsolutePath() + "] already exists. Use this file");
//		}
//		if (compositeRSS.getChannel() == null) {
//			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
//			throw new Exception(
//					"Unable to find channel in the compositeRSS and file [" + compositeRSSFile.getAbsolutePath() + "]");
//		}
//
//		log.debug("Composite RSS contains [" + compositeRSS.getChannel().getItem().size() + "] items");
//		/* TODO: implement checking each feed id belongs to user */
//
//		if (compositeFeedId == null) {
//			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
//			throw new Exception("compositeFeedId can't be null");
//		}
//
////		ArrayList<RSS> compositeListRSS = new ArrayList<RSS>();
//		log.debug("Start of feedIdList processing");
//		for (String feedId : feedIdList) {
//			if (!user.containsFeedId(feedId)) {
//				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
//				throw new Exception("User [" + userName + "] doesn't have feed id [" + feedId + "]");
//			}
//			File xmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + feedId + ".xml");
//			RSS rss;
//			try {
////				rss = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(xmlFile, new RSS());
//				rss = RSS.getRSSObjectFromXMLFile(xmlFile);
//				log.debug("Got rss from the file [" + xmlFile.getAbsolutePath() + "] with ["
//						+ rss.getChannel().getItem().size() + "] items");
//			} catch (JAXBException e) {
//				log.error("Can't read file [" + xmlFile.getAbsolutePath() + "] to get RSS for merging. "
//						+ "Continue to next file", e);
//				continue;
//			}
//
//			for (Item item : rss.getChannel().getItem()) {
//				if (!compositeRSS.getChannel().containsItem(item)) {
//					compositeRSS.getChannel().getItem().add(item);
//					log.debug("Added item [" + item.getTitle() + "] to the composite items list");
//				}
//			}
//		}
//
//		/*
//		 * if(!user.doesHaveCompositeFeedId(compositeFeedId)) { CompositeUserFeed
//		 * compositeUserFeed = new CompositeUserFeed();
//		 * compositeUserFeed.setId(compositeFeedId); for(String feedId : feedIdList) {
//		 * compositeUserFeed.getFeedIds().add(feedId);
//		 * log.debug("Added feedId ["+feedId+"] to newly created compositeUserFeed ["
//		 * +compositeUserFeed.getId()+"]"); }
//		 * user.getCompositeUserFeeds().add(compositeUserFeed); }else {
//		 * for(CompositeUserFeed compositeUserFeed : user.getCompositeUserFeeds()) {
//		 * if(compositeUserFeed.getId().equals(compositeFeedId)) { for(String feedId :
//		 * feedIdList) { compositeUserFeed.getFeedIds().add(feedId);
//		 * log.debug("Added feedId ["+feedId+"] to compositeUserFeed ["
//		 * +compositeUserFeed.getId()+"]"); } } } }
//		 */
//		compositeRSS.saveXMLObjectToFile(compositeRSSFile);
//		user.saveXMLObjectToFile(userFile);
//
//		log.debug("Composite RSS was successfully saved to the file [" + compositeRSSFile.getAbsolutePath() + "]");
//		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
//	}
//	
	/**
	 * 
	 * @param url - the URL of site that we need to autmotically add
	 * @param login - login of the user
	 * @param titlePrefix - a prefix of title. It needed for playlists for yuotube
	 * @return - count of items that were found
	 * @throws Exception
	 */
	
	public synchronized static ResponseForAddRSSFeedByURLAutomaticlyMethod addRSSFeedByURLAutomaticly(String url, String login, String titlePrefixForYoutubePlaylist, /*HashMap<String, String> cache,*/ Long durationMillisecondsForUpdatingFeeds) throws Exception {
		long t1 = new Date().getTime();
		log.debug("addRSSFeedByURLAutomaticly: url ["+url+"], login ["+login+"], titlePrefixForYoutubePlaylist ["+titlePrefixForYoutubePlaylist+"], durationMillisecondsForUpdatingFeeds ["+durationMillisecondsForUpdatingFeeds+"]");
		// javax.servlet.http.HttpServletRequest request
		url = (url.contains("youtube.com") && !url.contains("youtube.com/feeds/videos.xml")) ? Exec.getYoutubeFeedURL(url): url;
		if (url==null){
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new Exception("Can't find feed channel url");
		}
		String htmlContent = ServerUtilsConcurrent.getInstance().getURLContent(url);
		String feedTitle = Exec.getTitleFromHtmlBody(htmlContent);
		if(titlePrefixForYoutubePlaylist!=null) {
			feedTitle = titlePrefixForYoutubePlaylist + " [Playlist: " + feedTitle + "]";
		}
		String feedId = ServerUtils.getNewFeedId();
		File xmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + feedId + ".xml");
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + login + ".xml");
		User user = User.getXMLObjectFromXMLFile(userFile);
		String existingFeedIdWithCurrentURL = null;

		if ((existingFeedIdWithCurrentURL = user.containsFeedIdByUrl(url/*, cache*/)) != null) {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},	new Date().getTime() - t1);
			throw new Exception("User already has feed title ["+feedTitle+"] id [" + existingFeedIdWithCurrentURL + "] with such URL [" + url + "]");
		}

		String repeatableSearchPattern = user.getRepeatableSearchPatternByDomain(Exec.getDomainFromURL(url));
		String substringForHtmlBodySplit=Exec.getSubstringForHtmlBodySplit(repeatableSearchPattern);
		int countOfPercentItemsInSearchPattern = Exec.countWordsUsingSplit(repeatableSearchPattern, "{%}");
//		LinkedList<ru.kvaga.rss.feedaggr.Item> items = Exec.getItems(htmlContent, substringForHtmlBodySplit, repeatableSearchPattern, countOfPercentItemsInSearchPattern);					
		LinkedList<ru.kvaga.rss.feedaggr.Item> items = ServerUtilsConcurrent.getInstance().getItems(htmlContent, substringForHtmlBodySplit, repeatableSearchPattern, countOfPercentItemsInSearchPattern);
		String 	itemTitleTemplate = null, 
				itemLinkTemplate = null, 
				itemContentTemplate = null;
		if(user.getRssItemPropertiesPatterns()!=null && user.getRssItemPropertiesPatternByDomain(Exec.getDomainFromURL(url))!=null){
			itemTitleTemplate = user.getRssItemPropertiesPatternByDomain(Exec.getDomainFromURL(url)).getPatternTitle();
		}else{
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new Exception("Couldn't find existing item Title template for URL ["+url+"] and domain ["+Exec.getDomainFromURL(url)+"]");
		}
		
		if(user.getRssItemPropertiesPatterns()!=null && user.getRssItemPropertiesPatternByDomain(Exec.getDomainFromURL(url))!=null){
			itemLinkTemplate = user.getRssItemPropertiesPatternByDomain(Exec.getDomainFromURL(url)).getPatternLink();
		}else {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new Exception("Couldn't find existing item link template for URL ["+url+"] and domain ["+Exec.getDomainFromURL(url)+"]");
		}
		
		if(user.getRssItemPropertiesPatterns()!=null && user.getRssItemPropertiesPatternByDomain(Exec.getDomainFromURL(url))!=null){
			itemContentTemplate = user.getRssItemPropertiesPatternByDomain(Exec.getDomainFromURL(url)).getPatternDescription();
		}else{
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new Exception("Couldn't find existing item content template for URL ["+url+"] and domain ["+Exec.getDomainFromURL(url)+"]");
		}

		RSS rss = new RSS();
		Channel channel = new Channel();
		channel.setTitle(feedTitle);
		channel.setLink(url);
		channel.setLastBuildDate(new Date());
		channel.setDescription(feedTitle);
		channel.setItemsFromRawHtmlBodyItems(items, url, itemTitleTemplate, itemLinkTemplate, itemContentTemplate);
		rss.setChannel(channel);

		// ---

		rss.saveXMLObjectToFile(xmlFile);

		// Cache
		CacheUserFeed.getInstance().updateItem(feedId, rss);
		
		//
		if(user.containsFeedId(feedId)){
			UserFeed uf = user.getUserFeedByFeedId(feedId);
			uf.setItemTitleTemplate(itemTitleTemplate);
			uf.setItemLinkTemplate(itemLinkTemplate);
			uf.setItemContentTemplate(itemContentTemplate);
			uf.setRepeatableSearchPattern(repeatableSearchPattern);
		}else{
			if(durationMillisecondsForUpdatingFeeds==null) {
				user.getUserFeeds().add(new UserFeed(feedId, itemTitleTemplate, itemLinkTemplate, itemContentTemplate, repeatableSearchPattern, "", ConfigMap.DEFAULT_DURATION_IN_MILLIS_FOR_FEED_UPDATE, feedTitle, url));
			}else {
				user.getUserFeeds().add(new UserFeed(feedId, itemTitleTemplate, itemLinkTemplate, itemContentTemplate, repeatableSearchPattern, "", durationMillisecondsForUpdatingFeeds, feedTitle, url));
			}
		}
		// save repeatable search patterns
		user.getRepeatableSearchPatterns()
				.add(new UserRepeatableSearchPattern(Exec.getDomainFromURL(url),
						//"<entry>{*}<title>{%}</title>{*}<link rel=\"alternate\" href=\"{%}\"/>{*}<author>{*}<media:description>{%}</media:description>{*}</entry>"
						repeatableSearchPattern));

		// save rss output properties templates
		user.updateRssItemPropertiesPatterns(/*getRssItemPropertiesPatterns().update(*/
				new UserRssItemPropertiesPatterns(Exec.getDomainFromURL(url), itemTitleTemplate, itemLinkTemplate, itemContentTemplate));
		//----------------------
		user.saveXMLObjectToFile(userFile);
		
		
//		if(cache!=null)
//			cache.put(url, feedId);
		// ---
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
//		return items.size();
		return new ResponseForAddRSSFeedByURLAutomaticlyMethod(items.size(), feedId, url, feedTitle);
	}
	
	public synchronized static ResponseForAddRSSFeedByURLAutomaticlyMethod addRSSFeedByURLAutomaticly(String url, String login, String titlePrefixForYoutubePlaylist) throws Exception {
		return addRSSFeedByURLAutomaticly(url, login, titlePrefixForYoutubePlaylist, null);
	}

	public synchronized static ResponseForAddRSSFeedByURLAutomaticlyMethod addRSSFeedByURLAutomaticly(String url, String login) throws Exception {
		return addRSSFeedByURLAutomaticly(url, login, null, null);
	}
	
	public synchronized static ResponseForAddRSSFeedByURLAutomaticlyMethod addRSSFeedByURLAutomaticly(String url, String login, Long durationMillisecondsForUpdatingFeeds) throws Exception {
		return addRSSFeedByURLAutomaticly(url, login, null, durationMillisecondsForUpdatingFeeds);
	}
	
	public synchronized static File getUserFileByLogin(String login) {
		return new File(ConfigMap.usersPath + File.separator + login + ".xml");
	}
	public synchronized static File getRssFeedFileByFeedId(String feedId) {
		return new File(ConfigMap.feedsPath + File.separator + feedId + ".xml");
	}
	
	/**
	 * Get date since today. ATTENTION: if yoou need to get a date in the last then you have to 
	 * specify negative value of the {@code count_of_days_since_today}
	 * @param count_of_days_since_today - count of days since today
	 * @return the Date before or after to today
	 */
	public synchronized static Date getDateSinceToday(int count_of_days_since_today) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, count_of_days_since_today);
		return cal.getTime();
	}
	
	/**
	 * 
	 * @param feedId
	 * @param deleteFeedItemsOlderThanCountDays
	 * @return count of feed items were deleted
	 */
	public synchronized static int deleteOldFeedItems(String feedId, int deleteFeedItemsOlderThanCountDays) {
		long t1 = new Date().getTime();
		Date dateForDeletion = getDateSinceToday(-deleteFeedItemsOlderThanCountDays);
		log.info("Trying to delete feed ["+feedId+"] items older than ["+dateForDeletion+"]. Current date ["+new Date()+"]");
		ArrayList<Item> listAfterDeletion = new ArrayList<Item>();
		int currentSizeOfFeedItems = 0;
		try {
			RSS rss = RSS.getRSSObjectByFeedId(feedId);
			currentSizeOfFeedItems=rss.getChannel().getItem().size();
			for(Item item: rss.getChannel().getItem()) {
				if (item.getPubDate().before(dateForDeletion)) continue;
				listAfterDeletion.add(item);
			}
			rss.getChannel().setItem(listAfterDeletion);
			File rssFile = new File(ConfigMap.feedsPath+File.separator+feedId+".xml");
			rss.saveXMLObjectToFile(rssFile);
			log.info("Deleted ["+(currentSizeOfFeedItems - listAfterDeletion.size())+"] feed ["+feedId+"] items. Initial size was ["+currentSizeOfFeedItems+"]. ["+listAfterDeletion.size()+"] items stored to the file ["+rssFile.getAbsolutePath()+"]");
			
			// Cache
			if(feedId.toLowerCase().startsWith("composite_")) {
				CacheCompositeUserFeed.getInstance().updateItem(feedId, rss);
			}else {
				CacheUserFeed.getInstance().updateItem(feedId, rss);
			}
			
			//
			//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			return currentSizeOfFeedItems - listAfterDeletion.size();
		} catch (JAXBException e) {
			log.error("An error was occured during deletion old feed items in feedId ["+feedId+"]", e);
			return 0;
		}
	}
	
	public synchronized static String getStringFromArrayListWithItemsDelimeteredByComma(ArrayList<String> list) {
		StringBuilder sb = new StringBuilder();
		boolean firstIteration=true;
		if(list==null) return "";
		for(String item : list) {
			if(firstIteration) {
				sb.append(item);
				firstIteration=false;
			}else {
				sb.append(", ");
				sb.append(item);
			}
		}
		return sb.toString();
	}
	/*
	public static synchronized int[] updateCompositeRSSFilesOfUser(String userName, String singleCompositeFeedIdForUpdatingAfterAddingNewFeeds, ArrayList<String> newlyAddedFeedIdsList) throws JAXBException {
		long t1 = new Date().getTime();
		int allFeedsCount=0, successFeedsCount=0;
		Date deleteItemsWhichOlderThanThisDate = ServerUtils.getDateSinceToday(-ConfigMap.UPDATE_COMPOSITE_RSS_FILES_DAYS_COUNT_FOR_DELETION);
		log.debug("deleteItemsWhichOlderThanThisDate ["+deleteItemsWhichOlderThanThisDate+"]");
		log.info("Started proccess updateCompositeRSSFilesOfUser for user ["+userName+"]");
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + userName + ".xml");
//		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
		User user = User.getXMLObjectFromXMLFile(userFile);

		for (CompositeUserFeed compositeUserFeed : user.getCompositeUserFeeds()) {
			int countOfDeletedOldItems=0;
			if(singleCompositeFeedIdForUpdatingAfterAddingNewFeeds!=null && !compositeUserFeed.getId().equals(singleCompositeFeedIdForUpdatingAfterAddingNewFeeds)) {
				log.debug("Skipped composite feed id ["+compositeUserFeed.getId()+"] because it isn't equal to singleCompositeFeedIdForUpdatingAfterAddingNewFeeds ["+singleCompositeFeedIdForUpdatingAfterAddingNewFeeds+"]");
				continue;
			}
			allFeedsCount++;
			File compositeRSSFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + compositeUserFeed.getId() + ".xml");
//			RSS compositeRSS = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(compositeRSSFile, new RSS());
			RSS compositeRSS = RSS.getRSSObjectFromXMLFile(compositeRSSFile);
			try {
				for (String feedId : compositeUserFeed.getFeedIds()) {
					
					File xmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + feedId + ".xml");
//					RSS rss = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(xmlFile, new RSS());
					RSS rss = RSS.getRSSObjectFromXMLFile(xmlFile);
					log.debug("Got rss from the file [" + xmlFile.getAbsolutePath() + "] with ["+ rss.getChannel().getItem().size() + "] items");
					Iterator<Item> iteratorFromRSSItemsForSpecificFeedId = rss.getChannel().getItem().iterator();
//					for (Item item : rss.getChannel().getItem()) {
					while(iteratorFromRSSItemsForSpecificFeedId.hasNext()){
						Item itemFromRSSFileForSpecificFeedId = iteratorFromRSSItemsForSpecificFeedId.next();
						itemFromRSSFileForSpecificFeedId.setTitle("["+rss.getChannel().getTitle()+"] "+itemFromRSSFileForSpecificFeedId.getTitle());
						if (!compositeRSS.getChannel().containsItem(itemFromRSSFileForSpecificFeedId)) {
							if(singleCompositeFeedIdForUpdatingAfterAddingNewFeeds!=null) {
								// Set a new date for newly added feed ids and then we will countdown lifetime from that date and delete all old items
								if(newlyAddedFeedIdsList.contains(feedId)) {// change pubDate only for newly added feeds to the composite
									itemFromRSSFileForSpecificFeedId.setPubDate(new Date());
									compositeRSS.getChannel().getItem().add(itemFromRSSFileForSpecificFeedId);
								}else { // leave old items
									if(!compositeRSS.getChannel().getItem().contains(itemFromRSSFileForSpecificFeedId)) {
										System.out.println("itemFromRSSFileForSpecificFeedId: " + itemFromRSSFileForSpecificFeedId.getGuid().getValue() + ", pubDate: " + itemFromRSSFileForSpecificFeedId.getPubDate());
										compositeRSS.getChannel().getItem().add(itemFromRSSFileForSpecificFeedId);
									}
								}
								log.debug("Added item [" + itemFromRSSFileForSpecificFeedId + "] to the composite ["+compositeRSS+"] items list with newest pubDate ["+itemFromRSSFileForSpecificFeedId.getPubDate()+"]");
							}else { // if doesn't exists in the compose 
//								Item existedItem = compositeRSS.getChannel().getItem().get(compositeRSS.getChannel().getItem().indexOf(item));
								//Item existedItem = compositeRSS.getChannel().getItemByGuid(item.getGuid().getValue());
								if(itemFromRSSFileForSpecificFeedId.getPubDate().before(deleteItemsWhichOlderThanThisDate)) { // check if old
									log.debug(itemFromRSSFileForSpecificFeedId + " was skipped and wasn't added to the compose ["+compositeRSS+"] because it older than ["+deleteItemsWhichOlderThanThisDate+"] date");
								}else {
									compositeRSS.getChannel().getItem().add(itemFromRSSFileForSpecificFeedId);
									log.debug("Added item [" + itemFromRSSFileForSpecificFeedId + "] to the composite ["+compositeRSS+"] items list because it newer than ["+deleteItemsWhichOlderThanThisDate+"] date");
								}
							}      
						}else { // cpmposite feed has already this item therefore we will check the age of this item and delete if it is old
							Item itemFromCompositeRSSFile = compositeRSS.getChannel().getItemByGuid(itemFromRSSFileForSpecificFeedId.getGuid().getValue());
							if(itemFromCompositeRSSFile.getPubDate().before(deleteItemsWhichOlderThanThisDate)) {
//								iterator.remove(); 
								compositeRSS.getChannel().getItem().remove(itemFromCompositeRSSFile);
								log.debug(itemFromCompositeRSSFile + " was deleted from rss composite ["+compositeRSS+"] list because it older than ["+deleteItemsWhichOlderThanThisDate+"] date");
								countOfDeletedOldItems++;
							} else {
								log.debug(itemFromCompositeRSSFile + " exists in the rss composite ["+compositeRSS+"] list and skipped because newer than ["+deleteItemsWhichOlderThanThisDate+"] ");
							}
						}
					}
				}
				compositeRSS.getChannel().setLastBuildDate(new Date());
			} catch (Exception e) {
				log.error("updateCompositeRSSFilesOfUser Exception in the composite feed id ["+compositeUserFeed.getId()+"]", e);
				//InfluxDB.getInstance().send("response_time,method=ServerUtils.updateCompositeRSSFilesOfUserException", new Date().getTime() - t1);
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				continue;
			}
			successFeedsCount++;
			compositeRSS.saveXMLObjectToFile(compositeRSSFile);
			MonitoringUtils.sendCommonMetric("CompositeFeedsUpdateJob.CountOfDeletedOldItems", countOfDeletedOldItems, new Tag("compositeFeedId",compositeUserFeed.getId()));
		}
		//InfluxDB.getInstance().send("response_time,method=ServerUtils.updateCompositeRSSFilesOfUser", new Date().getTime() - t1);
		MonitoringUtils.sendResponseTime2InfluxDB(new Object(){}, new Date().getTime() - t1);
		return new int[] {allFeedsCount, successFeedsCount, allFeedsCount-successFeedsCount};
	}
	*/


	public synchronized static String listOfParametersToString(String...parameters) {
		if(parameters!=null) {
			int i=1;
			StringBuilder sb = new StringBuilder();
			boolean first=true;
			for(String parameter : parameters) {
				if(first) {
					first=false;
					sb.append(parameter);
					i++;
				}else {
					if(i%2==0) {
						sb.append("[");
					}else {
						sb.append(", ");
					}
					sb.append(parameter);
					sb.append(" ");
					if(i%2==0) {
						sb.append("]");
					}
					i++;
				}
			}
			return sb.toString();
		}
		return null;
	}
}


