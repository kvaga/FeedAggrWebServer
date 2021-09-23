package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import ru.kvaga.monitoring.influxdb2.InfluxDB;
import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.MonitoringUtils;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

@XmlRootElement
public class User {
	private static Logger log = LogManager.getLogger(User.class);
	private String name;
	private Set<UserFeed> userFeeds = new HashSet<UserFeed>();
	private Set<CompositeUserFeed> compositeUserFeeds = new HashSet<CompositeUserFeed>();
	private Set<UserRepeatableSearchPattern> repeatableSearchPatterns = new HashSet<UserRepeatableSearchPattern>();
	private Set<UserRssItemPropertiesPatterns> rssItemPropertiesPatterns = ConcurrentHashMap.newKeySet();
//		new UserRssItemPropertiesPatternsSet()
	;

	public User() {
	}

	public User(String name) {
		this.name = name;
	};

	public User(String name, HashSet<UserFeed> userFeeds) {
		this.name = name;
		this.userFeeds = userFeeds;
	};

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "userFeed")
	public Set<UserFeed> getUserFeeds() {
		return userFeeds;
	}

	public void setUserFeeds(Set<UserFeed> userFeeds) {
		this.userFeeds = userFeeds;
	}

	@XmlElement(name = "compositeUserFeed")
	public Set<CompositeUserFeed> getCompositeUserFeeds() {
		return compositeUserFeeds;
	}

	public void setCompositeUserFeeds(Set<CompositeUserFeed> compositeUserFeeds) {
		this.compositeUserFeeds = compositeUserFeeds;
	}

	@XmlElement(name = "repeatableSearchPattern")
	public Set<UserRepeatableSearchPattern> getRepeatableSearchPatterns() {
		return repeatableSearchPatterns;
	}

	public void setRepeatableSearchPatterns(Set<UserRepeatableSearchPattern> repeatableSearchPatterns) {
		this.repeatableSearchPatterns = repeatableSearchPatterns;
	}

	@XmlElement(name = "rssItemPropertiesPatterns")
	public Set<UserRssItemPropertiesPatterns> getRssItemPropertiesPatterns() {
		return rssItemPropertiesPatterns;
	}

	public void setRssItemPropertiesPatterns(UserRssItemPropertiesPatternsSet rssItemPropertiesPatterns) {
		this.rssItemPropertiesPatterns = rssItemPropertiesPatterns;
	}

	public synchronized void updateRepeatableSearchPatterns(UserRepeatableSearchPattern userRepeatableSearchPattern) {
		long t1 = new Date().getTime();
		for (UserRepeatableSearchPattern iterItem : getRepeatableSearchPatterns()) {
			if (iterItem.equals(userRepeatableSearchPattern)) {
				log.debug("For user [" + getName() + "] for domain [" + iterItem.getDomain()
						+ "] updated repeatableSearchPattern [" + iterItem.getPattern() + "] to the ["
						+ userRepeatableSearchPattern.getPattern() + "]");
				iterItem.setPattern(userRepeatableSearchPattern.getPattern());
				return;
			}
		}
		log.debug("For user [" + getName() + "] for domain [" + userRepeatableSearchPattern.getDomain()
				+ "] added new repeatableSearchPattern [" + userRepeatableSearchPattern.getPattern() + "]");
		getRepeatableSearchPatterns().add(userRepeatableSearchPattern);
	}

	public synchronized void updateRssItemPropertiesPatterns(
			UserRssItemPropertiesPatterns userRssItemPropertiesPatterns) {
		long t1 = new Date().getTime();
		for (UserRssItemPropertiesPatterns iterItem : getRssItemPropertiesPatterns()) {
			if (iterItem.equals(userRssItemPropertiesPatterns)) {
				iterItem.setPatternTitle(userRssItemPropertiesPatterns.getPatternTitle());
				iterItem.setPatternLink(userRssItemPropertiesPatterns.getPatternLink());
				iterItem.setPatternDescription(userRssItemPropertiesPatterns.getPatternDescription());
				return;
			}
		}
		getRssItemPropertiesPatterns().add(userRssItemPropertiesPatterns);
	}

	public boolean containsFeedId(String feedId) {
		long t1 = new Date().getTime();
		for (UserFeed userFeed : getUserFeeds()) {
			if (userFeed.getId().equals(feedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return map<(String) url, (String) feedId>
	 * @throws JAXBException
	 */
	public synchronized HashMap<String, String> getAllUserUrlsAndFeedIdsMap() throws JAXBException {
		long t1 = new Date().getTime();
		HashMap<String, String> map = new HashMap<String, String>();
		for (UserFeed userFeed : getUserFeeds()) {
			RSS rss = RSS
					.getRSSObjectFromXMLFile(ConfigMap.feedsPath.getAbsoluteFile() + "/" + userFeed.getId() + ".xml");
			map.put(rss.getChannel().getLink(), userFeed.getId());
		}
		log.debug("Found [" + map.size() + "] urls of user [" + name + "]");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return map;
	}

	/**
	 * 
	 * @return map<(String) feedId, (String) url>
	 * @throws JAXBException
	 */
	public synchronized HashMap<String, String> getAllUserFeedIdsAndUrlsMap() throws JAXBException {
		long t1 = new Date().getTime();
		HashMap<String, String> map = new HashMap<String, String>();
		for (UserFeed userFeed : getUserFeeds()) {
			RSS rss = RSS
					.getRSSObjectFromXMLFile(ConfigMap.feedsPath.getAbsoluteFile() + "/" + userFeed.getId() + ".xml");
			map.put(userFeed.getId(), rss.getChannel().getLink());
		}
		log.debug("Found [" + map.size() + "] urls of user [" + name + "]");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return map;
	}

	/**
	 * 
	 * @return map<(String) url, (HashSet<String>) feedIds>
	 * @throws JAXBException
	 */
	public HashMap<String, HashSet<String>> getFeedIdsWithDuplicateUrls() throws JAXBException {
		HashMap<String, HashSet<String>> aggregateUrlsAndTheisFeedIds = new HashMap<String, HashSet<String>>();
		HashMap<String, String> allUserFeedIdsAndUrlsMap = getAllUserFeedIdsAndUrlsMap();
		for (String feedId : allUserFeedIdsAndUrlsMap.keySet()) {
			if (aggregateUrlsAndTheisFeedIds.containsKey(allUserFeedIdsAndUrlsMap.get(feedId))) {
				aggregateUrlsAndTheisFeedIds.get(allUserFeedIdsAndUrlsMap.get(feedId)).add(feedId);
			} else {
				aggregateUrlsAndTheisFeedIds.put(allUserFeedIdsAndUrlsMap.get(feedId),
						new HashSet<String>(Arrays.asList(feedId)));
			}
		}
//		for(String url : aggregateUrlsAndTheisFeedIds.keySet()) {
//			System.err.print(url+":");
//			for(String feedId : aggregateUrlsAndTheisFeedIds.get(url)) {
//				System.err.print(feedId+" ");
//			}
//			System.err.println();
//		}
		HashMap<String, HashSet<String>> duplicateUrlsAndTheirFeedIds = new HashMap<String, HashSet<String>>();
		for (String url : aggregateUrlsAndTheisFeedIds.keySet()) {
			if (aggregateUrlsAndTheisFeedIds.get(url).size() > 1) {
				duplicateUrlsAndTheirFeedIds.put(url, aggregateUrlsAndTheisFeedIds.get(url));
			}
		}
		log.debug("Found [" + duplicateUrlsAndTheirFeedIds.size() + "] duplicate urls for user [" + getName() + "]");
		return duplicateUrlsAndTheirFeedIds;
	}

	/**
	 * 
	 * @param url
	 * @return id of feed which contains this @param url
	 * @throws JAXBException
	 */
	public String containsFeedIdByUrl(String url) throws JAXBException {
		return containsFeedIdByUrl(url, null);
		/*
		 * long t1 = new Date().getTime(); for(UserFeed userFeed : getUserFeeds()) { RSS
		 * rss = RSS.getRSSObjectFromXMLFile(ConfigMap.feedsPath.getAbsoluteFile() + "/"
		 * + userFeed.getId() + ".xml"); if(rss.getChannel().getLink().equals(url)) {
		 * MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
		 * new Date().getTime() - t1); return userFeed.getId(); } }
		 *MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
		 * new Date().getTime() - t1); return null;
		 */
	}

	public String containsFeedIdByUrl(String url, HashMap<String, String> localUrlsCache) throws JAXBException {
		long t1 = new Date().getTime();
		if (localUrlsCache != null) {
			if (localUrlsCache.containsKey(url)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				return localUrlsCache.get(url);
			}
		} else {
			for (UserFeed userFeed : getUserFeeds()) {
				RSS rss = RSS.getRSSObjectFromXMLFile(
						ConfigMap.feedsPath.getAbsoluteFile() + "/" + userFeed.getId() + ".xml");
				if (rss.getChannel().getLink().equals(url)) {
					MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
							new Date().getTime() - t1);
					return userFeed.getId();
				}
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return null;
	}

	public boolean containsCompositeFeedId(String compositeFeedId) {
		long t1 = new Date().getTime();
		for (CompositeUserFeed userFeed : getCompositeUserFeeds()) {
			if (userFeed.getId().equals(compositeFeedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
						new Date().getTime() - t1);
				return true;
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return false;
	}

	public CompositeUserFeed getCompositeUserFeedById(String feedId) throws Exception {
		long t1 = new Date().getTime();
		for (CompositeUserFeed compositeUserFeed : getCompositeUserFeeds()) {
			System.out.println(compositeUserFeed.getId() + " ? " + feedId);
			if (compositeUserFeed.getId().equals(feedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},	new Date().getTime() - t1);
				return compositeUserFeed;
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		throw new Exception("User [" + getName() + "] doesn't have such compositeFeed [" + feedId + "]");
	}

	public boolean removeCompositeUserFeedById(String feedId) {
		long t1 = new Date().getTime();
		for (Iterator<CompositeUserFeed> iterator = getCompositeUserFeeds().iterator(); iterator.hasNext();) {
			if (iterator.next().getId().equals(feedId)) {
				iterator.remove();
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
						new Date().getTime() - t1);
				return true;
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return false;
	}

	/*
	public boolean removeFeedFromUserFeed(String feedId) {
		long t1 = new Date().getTime();
		try {
			for(UserFeed uf : getUserFeeds()) {
				if(uf.getId().equals(feedId)) {
					MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
					return getUserFeeds().remove(uf);
				}
			}
			
		} catch (Exception e) {
			log.error("There is no such user feed ["+feedId+"]");
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return false;
	}
	*/
	public static synchronized boolean deleteUserFeedByIdFromUser(String feedId, String userName) throws Exception {
		long t1 = new Date().getTime();
		log.debug("Trying to delete feed id [" + feedId + "] for user [" + userName + "]");
		boolean deletedFeedId = false;
		boolean deletedFeedIdFromAllComposites = false;
		HashSet<UserFeed> userFeedNew = new HashSet<UserFeed>();
		File userConfigFile = new File(ConfigMap.usersPath + "/" + userName + ".xml");
//		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userConfigFile, new User());
		User user = User.getXMLObjectFromXMLFile(userConfigFile);
		log.debug("Successfully read file [" + userConfigFile + "]");
		for (UserFeed feed : user.getUserFeeds()) {
			if (feed.getId().equals(feedId)) {
				deletedFeedId=true;
				continue;
			}
			userFeedNew.add(feed);
		}
		deletedFeedIdFromAllComposites = user.removeFeedFromAllCompositeUserFeeds(feedId);
		log.debug("Created new list without [" + feedId + "] feed");
		user.setUserFeeds(userFeedNew);
		user.saveXMLObjectToFile(userConfigFile);
		log.debug("File [" + userConfigFile + "] successfully updated. This feedId ["+feedId+"] wasn't located in any compsoiteFeedIds, beacuse deletedFeedIdFromAllComposites ["+deletedFeedIdFromAllComposites+"], deletedFeedId ["+deletedFeedId+"]");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return deletedFeedId;
	}
	public static synchronized boolean deleteCompositeUserFeedByIdFromUser(String compositeFeedId, String userName) throws Exception {
		long t1 = new Date().getTime();
		boolean deletedBol = false;
		log.debug("Trying to delete composite feed id [" + compositeFeedId + "] for user [" + userName + "]");
		HashSet<CompositeUserFeed> userFeedNew = new HashSet<CompositeUserFeed>();
		File userConfigFile = new File(ConfigMap.usersPath + "/" + userName + ".xml");
//		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userConfigFile, new User());
		User user = User.getXMLObjectFromXMLFile(userConfigFile);
		log.debug("Successfully read file [" + userConfigFile + "]");
		for (CompositeUserFeed feed : user.getCompositeUserFeeds()) {
			if (feed.getId().equals(compositeFeedId)) {
				deletedBol=true;
				continue;
			}
			userFeedNew.add(feed);
		}
		log.debug("Created new composite list without [" + compositeFeedId + "] feed");
		user.setCompositeUserFeeds(userFeedNew);
		user.saveXMLObjectToFile(userConfigFile);
		log.debug("File [" + userConfigFile + "] successfully updated");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return deletedBol;
	}

	/**
	 * Delete feedId for user <code>userName</code>
	 * @param feedId
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean deleteFeed(String feedId, String userName) throws Exception {
		long t1 = new Date().getTime();
		boolean deletedFile=false;
		boolean deletedUserFeedBol=false;

		if(feedId.startsWith("composite_")) {
			deletedUserFeedBol = deleteCompositeUserFeedByIdFromUser(feedId, userName);
		}else {
			deletedUserFeedBol = deleteUserFeedByIdFromUser(feedId, userName);
		}
		File feedFile = new File(ConfigMap.feedsPath + "/" + feedId + ".xml");

		log.debug("Trying to delete feed file [" + feedFile.getAbsolutePath() + "]");
		deletedFile = feedFile.delete();
		log.debug("File [" + feedFile.getAbsolutePath() + "] deleted? status ["+deletedFile+"], Feed id ["+feedId+"] deleted from user ["+userName+"]? status ["+deletedUserFeedBol+"]");

		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return deletedFile && deletedUserFeedBol;
	}
	public boolean removeFeedFromCompositeUserFeed(String feedId, String compositeFeedId) {
		long t1 = new Date().getTime();
		CompositeUserFeed cuf;
		try {
			cuf = getCompositeUserFeedById(compositeFeedId);
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			return cuf.getFeedIds().remove(feedId);
		} catch (Exception e) {
			log.error("There is no such composite user feed ["+compositeFeedId+"]");
		}
		
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return false;
	}
	
	public boolean removeFeedFromAllCompositeUserFeeds(String feedId) {
		long t1 = new Date().getTime();
		boolean deleted = false;
		
			for(CompositeUserFeed cuf : getCompositeUserFeeds()) {
				deleted = cuf.getFeedIds().remove(feedId);
			}
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		
		return deleted;
	}
	
	public UserRssItemPropertiesPatterns getRssItemPropertiesPatternByDomain(String domain) {
		long t1 = new Date().getTime();
		for (UserRssItemPropertiesPatterns ursp : getRssItemPropertiesPatterns()) {
			if (ursp.getDomain().equals(domain)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
						new Date().getTime() - t1);
				return ursp;
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
				new Date().getTime() - t1);
		return null;
	}

	public String getRepeatableSearchPatternByDomain(String domain) {
		long t1 = new Date().getTime();
		for (UserRepeatableSearchPattern ursp : getRepeatableSearchPatterns()) {
			if (ursp.getDomain().equals(domain)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
						new Date().getTime() - t1);
				return ursp.getPattern();
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
				new Date().getTime() - t1);
		return null;
	}

	public String getRepeatableSearchPatternByFeedId(String feedId) {
		long t1 = new Date().getTime();
		for (UserFeed uf : getUserFeeds()) {
			if (uf.getId().equals(feedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
						new Date().getTime() - t1);
				return uf.getRepeatableSearchPattern();
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
				new Date().getTime() - t1);
		return null;
	}

	public String getItemTitleTemplateByFeedId(String feedId) {
		long t1 = new Date().getTime();
		for (UserFeed uf : getUserFeeds()) {
			if (uf.getId().equals(feedId)) {
				return uf.getItemTitleTemplate(); 
			}
		}
		return null;
	}

	public String getItemLinkTemplateByFeedId(String feedId) {
		long t1 = new Date().getTime();
		for (UserFeed uf : getUserFeeds()) {
			if (uf.getId().equals(feedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
						new Date().getTime() - t1);
				return uf.getItemLinkTemplate();
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return null;
	}

	public String getItemContentTemplateByFeedId(String feedId) {
		long t1 = new Date().getTime();
		for (UserFeed uf : getUserFeeds()) {
			if (uf.getId().equals(feedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
						new Date().getTime() - t1);
				return uf.getItemContentTemplate();
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
				new Date().getTime() - t1);
		return null;
	}

	public Long getDurationInMillisForUpdateByFeedId(String feedId) {
		long t1 = new Date().getTime();
		for (UserFeed uf : getUserFeeds()) {
			if (uf.getId().equals(feedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
						new Date().getTime() - t1);
				return uf.getDurationInMillisForUpdate();
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return null;
	}
	
	public String getFilterWordsByFeedId(String feedId) {
		long t1 = new Date().getTime();
		for (UserFeed uf : getUserFeeds()) {
			if (uf.getId().equals(feedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
						new Date().getTime() - t1);
				return uf.getFilterWords();
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return null;
	}
	
	

	public UserFeed getUserFeedByFeedId(String feedId) {
		long t1 = new Date().getTime();
		for (UserFeed uf : getUserFeeds()) {
			if (uf.getId().equals(feedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				return uf;
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return null;
	}

	public static synchronized User getXMLObjectFromXMLFile(File xmlFile) throws JAXBException {
		long t1 = new Date().getTime();
		JAXBContext jaxbContext;
		jaxbContext = JAXBContext.newInstance(User.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		User obj = (User) jaxbUnmarshaller.unmarshal(xmlFile);
		obj.setName(xmlFile.getName().replaceFirst("[.][^.]+$", ""));
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return obj;
	}

	public static synchronized User getXMLObjectFromXMLFileByUserName(String login) throws JAXBException {
		long t1 = new Date().getTime();
		log.debug("Loading xml file file by user id [" + login + "]");
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + login + ".xml");
		JAXBContext jaxbContext;
		jaxbContext = JAXBContext.newInstance(User.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		User obj = (User) jaxbUnmarshaller.unmarshal(userFile);
		obj.setName(login);
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
				new Date().getTime() - t1);
		return obj;
	}

	public static synchronized void changeFeedIdByUserNameWithSaving(String login, String oldFeedId, String newFeedId)
			throws Exception {
		long t1 = new Date().getTime();
		File oldXmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + oldFeedId + ".xml");
		File newXmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + newFeedId + ".xml");
		if (newXmlFile.exists()) {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
					new Date().getTime() - t1);
			throw new Exception("File with new feed id [" + newFeedId + "] already exists");
		}

		User user = User.getXMLObjectFromXMLFileByUserName(login);
		user.renameFeedWithoutSavingToFile(oldFeedId, newFeedId);
		user.saveXMLObjectToFileByLogin(login);

		if (!oldXmlFile.renameTo(newXmlFile)) {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
					new Date().getTime() - t1);
			throw new Exception("Couldn't rename old feed id file [" + oldXmlFile + "] to the new feed id file ["
					+ newXmlFile + "]");
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {},
				new Date().getTime() - t1);
	}

	public synchronized void renameFeedWithoutSavingToFile(String oldFeedId, String newFeedId) throws Exception {
		long t1 = new Date().getTime();
		if (getUserFeedByFeedId(newFeedId) != null) {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new Exception("Feed with newFeedId [" + newFeedId + "] already exists");
		}
		UserFeed oldUserFeed = getUserFeedByFeedId(oldFeedId);
		if (oldUserFeed == null) {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new Exception(
					"Feed with oldFeedId [" + oldFeedId + "] doesn't exist for this user [" + this.name + "]");
		}
		oldUserFeed.setId(newFeedId);
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
	}

	public synchronized void saveXMLObjectToFile(File file) throws JAXBException {
		long t1 = new Date().getTime();
		JAXBContext jc = JAXBContext.newInstance(this.getClass());
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(this, file);
		log.debug("Object user [" + getName() + "] successfully saved to the [" + file + "] file");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
	}

	public synchronized void saveXMLObjectToFileByLogin(String login) throws JAXBException {
		long t1 = new Date().getTime();
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + login + ".xml");
		JAXBContext jc = JAXBContext.newInstance(this.getClass());
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(this, userFile);
		log.debug("Object user [" + getName() + "] successfully saved to the [" + userFile + "] file");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

	}

	public static synchronized ArrayList<User> getAllUsersList() throws GetFeedsListByUser, JAXBException {
		long t1 = new Date().getTime();
		ArrayList<User> allUsersList = new ArrayList<User>();
		for (File userFile : getAllUserFiles()) {
			allUsersList.add(User.getXMLObjectFromXMLFile(userFile));
		}
		log.debug("Found [" + allUsersList.size() + "] users on a server");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return allUsersList;
	}

	/**
	 * 
	 * @return map<(String) feedId,(String) userName>
	 * @throws GetFeedsListByUser
	 * @throws JAXBException
	 */
	public static synchronized HashMap<String, String> getFeedsIdsOfAllUsersMap()
			throws GetFeedsListByUser, JAXBException {
		long t1 = new Date().getTime();
		HashMap<String, String> feedsOfAllUsersMap = new HashMap<String, String>();
		for (User user : getAllUsersList()) {
			for (UserFeed userFeed : user.getUserFeeds()) {
				feedsOfAllUsersMap.put(userFeed.getId(), user.getName());
			}
			for (CompositeUserFeed compositeUserFeed : user.getCompositeUserFeeds()) {
				feedsOfAllUsersMap.put(compositeUserFeed.getId(), user.getName());
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return feedsOfAllUsersMap;
	}

	public static synchronized ArrayList<File> getAllUserFiles() {
		long t1 = new Date().getTime();
		ArrayList<File> al = new ArrayList<File>();
		for (File file : ConfigMap.usersPath.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".xml")) {
				al.add(file);
				log.trace("File: " + file.getName());
			}
		}
		log.debug("Found [" + al.size() + "] users files ");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return al;
	}

	public HashSet<String> getCompositeUserFeedIdsListWhichContainUserFeedId(String feedId,Set<CompositeUserFeed> allCompositeUserFeedCache) {
		HashSet<String> al = new HashSet<String>();
		if (allCompositeUserFeedCache == null) {
			// NO cache
			for (CompositeUserFeed cuf : getCompositeUserFeeds()) {
				if (cuf.getFeedIds().contains(feedId)) {
					al.add(cuf.getId());
				}
			}
			return al;
		} else {
			// witch cache
			for (CompositeUserFeed cuf : allCompositeUserFeedCache) {
				if (cuf.getFeedIds().contains(feedId)) {
					al.add(cuf.getId());
				}
			}
			return al;
		}
	}
	public HashSet<String> getCompositeUserFeedIdsListWhichContainUserFeedId(String feedId) {
		return getCompositeUserFeedIdsListWhichContainUserFeedId(feedId, null);
	}
	
	public HashMap<String, ArrayList<String>> getZombieFeedIdsList(Set<CompositeUserFeed> compositeUserFeedsCache) throws Exception{
		HashMap<String, ArrayList<String>> zombieFeedIdsList = new HashMap<String, ArrayList<String>>();
		 if(true) throw new Exception("Unimplemented");
		
		return zombieFeedIdsList;
	}
}
