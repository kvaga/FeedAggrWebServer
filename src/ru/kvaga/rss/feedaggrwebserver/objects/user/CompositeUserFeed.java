package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.io.File;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.cache.CacheCompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.cache.CacheElement;
import ru.kvaga.rss.feedaggrwebserver.cache.CacheUserFeed;
import ru.kvaga.rss.feedaggrwebserver.monitoring.*;
@XmlRootElement
public class CompositeUserFeed {
	final private static Logger log = LogManager.getLogger(CompositeUserFeed.class);
 
	private String id;
	private String compositeUserFeedTitle;
	//private ArrayList<String> feedIds = new ArrayList<String>();
	private HashSet<String> feedIds = new HashSet<String>();

	public String toString() {
		return "CompositeFeedTitle: " + compositeUserFeedTitle + ", id: " + id;
	}
	
	public CompositeUserFeed() {}
	public CompositeUserFeed(String id, String compositeUserFeedTitle) {
		this.id=id;
		this.compositeUserFeedTitle=compositeUserFeedTitle;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCompositeUserFeedTitle() {
		return compositeUserFeedTitle;
	}
	public CompositeUserFeed setCompositeUserFeedTitle(String compositeUserFeedTitle) {
		this.compositeUserFeedTitle = compositeUserFeedTitle;
		return this;
	}
	public HashSet<String> getFeedIds() {
		return feedIds;
	}
	public void setFeedIds(HashSet<String> ids) {
		this.feedIds = ids;
	}
	
	public boolean doesHaveCompositeFeedId(String compositeFeedId) {
		long t1 = new Date().getTime();
		for(String s : feedIds) {
			if(s.equals(compositeFeedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				return true;
			}
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return false;
	}
	
	public int hashCode() {
		return this.getId().hashCode();
	}
	
	public boolean equals(Object object) {
		if(object instanceof CompositeUserFeed) {
			return ((CompositeUserFeed) object).getId().equals(this.getId());
		}else {
			return false;
		}
	}
	
	
	 
	/**
	 * Create new empty composite RSS file and add newly created CompositeUserFeed to the user's file
	 * @param userName - a name of user
	 * @param compositeRSSTitle - RSS title of newly created composite
	 * @return CompositeUserFeed - newly created CompositeUserFeed or null in case of error
	 * @throws Exception
	 */
	public static synchronized CompositeUserFeed createCompositeRSS(String userName, String compositeRSSTitle)
			throws Exception {
		long t1 = new Date().getTime();
		
		File userFile = User.getUsersFileByUserName(userName);
		User user = User.getXMLObjectFromXMLFile(userFile);
		String compositeFeedId = "composite_" + ServerUtils.getNewFeedId();
		log.debug("Created new composite feed with id ["+compositeFeedId+"]");
		File compositeRSSFile = RSS.getRSSFileByFeedId(compositeFeedId);

		// old compatibility
		if (user.getCompositeUserFeeds() == null) {
			user.setCompositeUserFeeds(new HashSet<CompositeUserFeed>());
		}

		// Creating new CompositeUserFeed and adding to user
		CompositeUserFeed compositeUserFeed = new CompositeUserFeed(compositeFeedId, compositeRSSTitle);
		//compositeUserFeed.setId(compositeFeedId, compositeRSSTitle);
		//user.getCompositeUserFeeds().add(compositeUserFeed);
		
		if(user.getCompositeUserFeeds().add(compositeUserFeed)) {
			log.debug("Composite feed ["+compositeFeedId+"] was added to the ["+user.getName()+"] user");
		}else {
			throw new Exception("Couldn't add composite user feed [id: "+compositeFeedId+", title: "+compositeRSSTitle+"] to the ["+user.getName()+"] user");
		}

		// Creating new composite rss and channel
		RSS compositeRSS = null;
		compositeRSS = 	new RSS();
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
		compositeRSS.saveXMLObjectToFile(compositeRSSFile);

		// Storing composite RSS and USer to files
		log.debug("Composite RSS was successfully saved to the file [" + compositeRSSFile.getAbsolutePath() + "]");
		user.saveXMLObjectToFile(userFile);
		log.debug("User's ["+userName+"] configuration was successfully saved to the file [" + userFile.getAbsolutePath() + "]");
		
		// Cache
		CacheCompositeUserFeed.getInstance().updateItem(compositeFeedId, compositeRSS);
		
		// Monitoring
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return compositeUserFeed;
	}
	
	public static synchronized boolean deleteCompositeRSS(String userName, String compositeFeedId)
			throws Exception {
		long t1 = new Date().getTime();
		
		File userFile = User.getUsersFileByUserName(userName);
		User user = User.getXMLObjectFromXMLFile(userFile);
		
		
		
		// old compatibility
		if (user.getCompositeUserFeeds() != null) {
			CompositeUserFeed cuf = new CompositeUserFeed();
			cuf.setId(compositeFeedId);
			if(!user.getCompositeUserFeeds().remove(cuf)) {
				throw new Exception("Couldn't remove composite feed with id ["+compositeFeedId+"] from the user ["+userName+"]");
			}
		}
		
		File compositeRSSFile = RSS.getRSSFileByFeedId(compositeFeedId);
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return compositeRSSFile.delete();
		
	}
	
	/**
	 * Add specific feedId (common feed) to a composite feed with id {@code compositeFeedId} and add all items 
	 * of this {@code feedId} with current pubDate to the composite feed {@code compositeFeedId}
	 * @param compositeFeedId
	 * @param feedId
	 * @param userName
	 * @return RSS of composite feed or null/Exception in case of error
	 * @throws Exception
	 */
	public static synchronized RSS addNewFeeds2CompositeFeed(String compositeFeedId, String feedId, String userName) throws Exception {
		return addNewFeeds2CompositeFeed(
				compositeFeedId, 
				new ArrayList<String>() {{
					add(feedId);
				}}, 
				userName);
	}
	
	/**
	 * Add list of feeds (common feeds) to a composite feed with id  {@code compositeFeedId} and add all items 
	 * of these {@code feedIdList} with current pubDate to the composite feed {@code compositeFeedId}
	 * @param compositeFeedId
	 * @param feedIdList
	 * @param userName
	 * @return RSS of composite feed or null/Exception in case of error
	 * @throws Exception
	 */
	public static synchronized RSS addNewFeeds2CompositeFeed(String compositeFeedId, ArrayList<String> feedIdList, String userName) throws Exception {
		long t1 = new Date().getTime();
		log.debug("Add Feed ["+feedIdList+"] to the CompositeFeed ["+compositeFeedId+"] for the ["+userName+"] user");
		File userFile = User.getUsersFileByUserName(userName);
		User user = User.getXMLObjectFromXMLFile(userFile);
		
		log.debug("Got configuration of user [" + userName + "]");
		
		RSS compositeRSS = RSS.getRSSObjectByFeedId(compositeFeedId);
		
		if (compositeRSS.getChannel() == null) {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new Exception(
					"Unable to find channel in the compositeFeed ["+compositeFeedId+"]");
		}

		log.debug("Composite RSS contains [" + compositeRSS.getChannel().getItem().size() + "] items");
		/* TODO: implement checking each feed id belongs to user */

		if (compositeFeedId == null) {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new Exception("compositeFeedId can't be null");
		}

		log.debug("Start of feedIdList processing, size of list is ["+feedIdList.size()+"]");
		for (String feedId : feedIdList) {
			//Add feedId to composite user feed in the User's file
			user.getCompositeUserFeedById(compositeFeedId).getFeedIds().add(feedId);
			
			// drop feedIds if user doesn't own them
			if (!user.containsFeedId(feedId)) {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				throw new Exception("User [" + userName + "] doesn't have feed id [" + feedId + "]");
			}
			
			RSS rss;
			try {
//				rss = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(xmlFile, new RSS());
				rss = RSS.getRSSObjectByFeedId(feedId);
				log.debug("Got rss ["+feedId+"] with [" + rss.getChannel().getItem().size() + "] items");
			} catch (JAXBException e) {
				log.error("Can't get rss object for the ["+feedId+"]. Continue to next file", e);
				continue;
			}

			// Adding all items of the each single rss to the composite feeds list
			for (Item item : rss.getChannel().getItem()) {
				if (!compositeRSS.getChannel().containsItem(item)) {
					item.setPubDate(new Date());
					item.setTitle("["+rss.getChannel().getTitle() + "] " + item.getTitle());
					compositeRSS.getChannel().getItem().add(item);
					log.debug("Added item [" + item.getTitle() + "] to the composite items list");
				}
			}
		}

		compositeRSS.saveXMLObjectToFile(RSS.getRSSFileByFeedId(compositeFeedId));
		user.saveXMLObjectToFile(userFile);

		log.debug("Composite RSS was successfully saved to the file [" + RSS.getRSSFileByFeedId(compositeFeedId).getAbsolutePath() + "]");
		// Cache
		CacheCompositeUserFeed.getInstance().updateItem(compositeFeedId, compositeRSS);
		
		//
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return compositeRSS;
	}
	
	/**
	 * Update all composite feed files with new items from common feeds. 
	 * All items in composite files which older than {@code ConfigMap.UPDATE_COMPOSITE_RSS_FILES_DAYS_COUNT_FOR_DELETION} will be deleted.
	 * @return int[] - allCompositeFeedsCount, successCompositeFeedsCount, allCompositeFeedsCount-successCompositeFeedsCount (failed), countOfDeletedOldItems
	 * @throws JAXBException
	 */
	public static synchronized int[] updateItemsInCompositeRSSFilesOfUser(String userName) throws Exception {
		long t1 = new Date().getTime();
		CacheCompositeUserFeed cache = CacheCompositeUserFeed.getInstance();

		int allFeedsCount=0, successFeedsCount=0;
		int countOfDeletedOldItemsTotal=0;
		Date deleteItemsWhichOlderThanThisDate = ServerUtils.getDateSinceToday(-ConfigMap.UPDATE_COMPOSITE_RSS_FILES_DAYS_COUNT_FOR_DELETION);
		log.debug("deleteItemsWhichOlderThanThisDate ["+deleteItemsWhichOlderThanThisDate+"]");
		log.info("Started proccess updateCompositeRSSFilesOfUser for user ["+userName+"]");
		File userFile = User.getUsersFileByUserName(userName);
		User user = User.getXMLObjectFromXMLFile(userFile);
		Set<CompositeUserFeed> compositeUserFeedSet = user.getCompositeUserFeeds();
		// Iterate over all user's composite feeds 
		for (CompositeUserFeed compositeUserFeed : compositeUserFeedSet) {
			CacheElement cacheElement = cache.getItem(compositeUserFeed.getId());

			int countOfDeletedOldItems=0;
			allFeedsCount++;
			File compositeRSSFile = null;
			RSS compositeRSS = null;
			try {
				compositeRSSFile = ServerUtils.getRssFeedFileByFeedId(compositeUserFeed.getId());
				compositeRSS = RSS.getRSSObjectFromXMLFile(compositeRSSFile);
				ArrayList<Item> oldCompositeFeedItemsForDeletionFromCurrentCompositeFeed = new ArrayList<Item>();
				// check compositeUserFeed title for null value
				if(compositeUserFeed.getCompositeUserFeedTitle()==null) {
					compositeUserFeed.setCompositeUserFeedTitle(compositeRSS.getChannel().getTitle());
					log.debug("User's ["+user.getName()+"] compositeUserFeed [feedId: "+compositeUserFeed.getId()+"] title was null hence the title became ["+compositeRSS.getChannel().getTitle()+"] from the RSS file. These changes will take effect after saving of a user's file");
				}
			
				// iterate over all feeds of specific compositeUserFeed
				for (String feedId : compositeUserFeed.getFeedIds()) {

					RSS rss = RSS.getRSSObjectByFeedId(feedId);
					log.debug("Got rss [" + rss + "] for feedId ["+feedId+"] with ["+ rss.getChannel().getItem().size() + "] items");
					
					//Iterator<Item> iteratorFromRSSItemsForSpecificFeedId = rss.getChannel().getItem().iterator();
					// Iterate over all Items in of specific RSS
					for (Item itemFromRSSFileForSpecificFeedId : rss.getChannel().getItem()) {
						
						//Item itemFromRSSFileForSpecificFeedId = iteratorFromRSSItemsForSpecificFeedId.next();
						//itemFromRSSFileForSpecificFeedId.setTitle("["+rss.getChannel().getTitle()+"] "+itemFromRSSFileForSpecificFeedId.getTitle());
						// Check if compositeFeed already has this item anf check it age (if old then delete it)
						if (!compositeRSS.getChannel().containsItem(itemFromRSSFileForSpecificFeedId)) {
							
							// this branch is for Items which compositeFeed doesn't contain then add these items
							// to the compositeFeed with new current pubDate and title with prefix of parent
							itemFromRSSFileForSpecificFeedId.setPubDate(new Date());
							itemFromRSSFileForSpecificFeedId.setTitle("["+rss.getChannel().getTitle()+"] "+itemFromRSSFileForSpecificFeedId.getTitle());

							compositeRSS.getChannel().getItem().add(itemFromRSSFileForSpecificFeedId);   
							log.debug("Added new item ["+itemFromRSSFileForSpecificFeedId+"] to the compositeUserFeed ["+compositeUserFeed+"]");
						}
//						else { 
//							// this branch is for items which compositeFeed has already them
//							// and check the age of these item's pubDate
//							// if pubDate is older than needed then mark them to delete
//							Item itemFromCompositeRSSFile = compositeRSS.getChannel().getItemByGuid(itemFromRSSFileForSpecificFeedId.getGuid().getValue());
//							if(itemFromCompositeRSSFile.getPubDate().before(deleteItemsWhichOlderThanThisDate)) {
//								oldCompositeFeedItemsForDeletionFromCurrentCompositeFeed.add(itemFromCompositeRSSFile);
//								log.debug(itemFromCompositeRSSFile + " was marked for deletion (as older than ["+deleteItemsWhichOlderThanThisDate+"] from the compositeUserFeed ["+compositeUserFeed.getId()+"]");
//							} 
//						}
					}
				}
				compositeRSS.getChannel().setLastBuildDate(new Date());
				
				if(cacheElement != null) {
					cacheElement.setLastUpdateStatus(CacheElement.LAST_UPDATE_STATUS_OK);
				}
			} catch (Exception e) {
				log.error("updateCompositeRSSFilesOfUser Exception in the composite feed id ["+compositeUserFeed.getId()+"]", e);
				if(cacheElement!=null) {
					StringBuilder sb = new StringBuilder();
					sb.append("[Exception] ");
					if(e!=null) {
						if(e.getMessage()!=null) {
							sb.append("Message: ");
							sb.append(e.getMessage());
							sb.append(". ");
						}else if(e.getCause()!=null) {
							sb.append("Cause: ");
							sb.append(e.getCause());
							sb.append(". ");
						}
					}
					cacheElement.setLastUpdateStatus(sb.toString());
				}
				//InfluxDB.getInstance().send("response_time,method=ServerUtils.updateCompositeRSSFilesOfUserException", new Date().getTime() - t1);
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				continue;
			}
			
			// Cache
//			Date[] oldestNewest = compositeRSS.getOldestNewestPubDate();
//			cacheElement.setCountOfItems(compositeRSS.getChannel().getItem()!=null?compositeRSS.getChannel().getItem().size():0)
//			.setLastUpdated(compositeRSS.getChannel().getLastBuildDate())
//			.setNewestPubDate(oldestNewest[1])
//			.setOldestPubDate(oldestNewest[0])
//			.setSizeMb(compositeRSSFile.length()/1024/1024);
			cache.updateItem(compositeUserFeed.getId(), compositeRSS);
			//
			removeOldItems(compositeRSS, deleteItemsWhichOlderThanThisDate);
			compositeRSS.saveXMLObjectToFile(compositeRSSFile);
			successFeedsCount++;
			MonitoringUtils.sendCommonMetric("CompositeFeedsUpdateJob.CountOfDeletedOldItems", countOfDeletedOldItems, new Tag("compositeFeedId",compositeUserFeed.getId()));
		}
		user.setCompositeUserFeeds(compositeUserFeedSet);
		user.saveXMLObjectToFileByLogin();
		//InfluxDB.getInstance().send("response_time,method=ServerUtils.updateCompositeRSSFilesOfUser", new Date().getTime() - t1);
		MonitoringUtils.sendResponseTime2InfluxDB(new Object(){}, new Date().getTime() - t1);
		return new int[] {allFeedsCount, successFeedsCount, allFeedsCount-successFeedsCount, countOfDeletedOldItemsTotal};
	}
	
	private static synchronized int removeOldItems(RSS compositeRSS, Date deleteItemsWhichOlderThanThisDate) {
		int countOfDeletedOldItemsTotal = 0;
		ArrayList<Item> oldCompositeFeedItemsForDeletionFromCurrentCompositeFeed = new ArrayList<Item>();
		for(Item item : compositeRSS.getChannel().getItem()) {
			if(item.getPubDate().before(deleteItemsWhichOlderThanThisDate)) {
				oldCompositeFeedItemsForDeletionFromCurrentCompositeFeed.add(item);
			}
		}
		
		for(Item item : oldCompositeFeedItemsForDeletionFromCurrentCompositeFeed) {
			if(compositeRSS.getChannel().getItem().remove(item)) {
				log.debug(item + " was deleted from rss composite ["+compositeRSS+"] list because it older than ["+deleteItemsWhichOlderThanThisDate+"] date");
//				countOfDeletedOldItems++;
				countOfDeletedOldItemsTotal++;
			}else {
				log.error("Couldn't remove item " + item + " from the compositeRSS ["+compositeRSS.getChannel().getTitle()+"]");
			}
		}
		return countOfDeletedOldItemsTotal;
	}
	/**
	 * Delete all feed ids from composite user feed {@code compositeFeedId} despite {@code feedIdListToSave}
	 * @param userName
	 * @param compositeFeedId
	 * @param finalFeedIdListToSave
	 * return ArrayList of deleted feedIds
	 * @throws Exception 
	 */
	public static synchronized ArrayList<String> deleteFeedIdsFromCompositeUserFeedDespiteFinalList(String userName, String compositeFeedId, ArrayList<String> finalFeedIdListToSave) throws Exception {
		ArrayList<String> deletedFeedsIdsList = new ArrayList<String>();
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		ArrayList<String> feedIdsForDeletion = new ArrayList<String>();
		for(String fId : user.getCompositeUserFeedById(compositeFeedId).getFeedIds()) {
			if(!finalFeedIdListToSave.contains(fId)) {
				feedIdsForDeletion.add(fId);
			}
		}
		for(String fId : feedIdsForDeletion) {
			if(user.getCompositeUserFeedById(compositeFeedId).getFeedIds().remove(fId)) {
				deletedFeedsIdsList.add(fId);
			}
		}
		user.saveXMLObjectToFileByLogin();
		
		
		return deletedFeedsIdsList;
	}
	
	/**
	 * Update composite feed title with new one {@code compositeRSSTitle}
	 * @param compositeRSSTitle
	 * @param compositeFeedId
	 * @param userName
	 * @return 'true' composite feed title was successfully changed and 'false' if previous title equals to the {@code compositeRSSTitle}
	 * @throws Exception
	 */
	public static synchronized boolean updateRSSTitleOfComposeFeed(String compositeRSSTitle, String compositeFeedId, String userName) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		if(user.getCompositeUserFeedById(compositeFeedId)==null) throw new Exception("User ["+userName+"] doesn't have the ["+compositeFeedId+"] feed");
		user.getCompositeUserFeedById(compositeFeedId).setCompositeUserFeedTitle(compositeRSSTitle);
		System.err.print("----------- " + user.getCompositeUserFeedById(compositeFeedId).getCompositeUserFeedTitle());
		user.saveXMLObjectToFileByLogin();
		RSS rss = RSS.getRSSObjectByFeedId(compositeFeedId);
		if(!rss.getChannel().getTitle().equals(compositeRSSTitle)) {
			rss.getChannel().setTitle(compositeRSSTitle);
			rss.saveXMLObjectToFileByFeedId(compositeFeedId);
			return true;
		}
		return false;
	}
	
	public static synchronized int getCountOfFeeds(String compositeFeedId, String userName) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		if(user.getCompositeUserFeedById(compositeFeedId)==null) throw new Exception("User ["+userName+"] doesn't have the ["+compositeFeedId+"] feed");
		return user.getCompositeUserFeedById(compositeFeedId).getFeedIds().size();
	}
	public static synchronized int[] appendUserFeedsToCompositeUserFeed(String compositeFeedID, ArrayList<String> appendedfeedIdsList,
			String userName) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		CompositeUserFeed cuf  = user.getCompositeUserFeedById(compositeFeedID);
		int oldCountOfFeedsInCompositeUserFeed = cuf.getFeedIds().size();
		RSS rssCompositeUserFeed = RSS.getRSSObjectByFeedId(compositeFeedID);
		int oldCountOfItemsInCompositeFeedFile = rssCompositeUserFeed.getChannel().getItem().size();
		for(String feedId : appendedfeedIdsList) {
			if(!cuf.getFeedIds().contains(feedId)) {
				// append userFeedsIds to the Compoite user feed
				cuf.getFeedIds().add(feedId);
				// append feed items from appended userFeeds to a composite feed file
				RSS rss = RSS.getRSSObjectByFeedId(feedId);
				for(Item item : rss.getChannel().getItem()) {
					if(!rssCompositeUserFeed.getChannel().containsItem(item)) {
						item.setPubDate(new Date());
						item.setTitle("["+rss.getChannel().getTitle() + "] " + item.getTitle());
						rssCompositeUserFeed.getChannel().getItem().add(item);
					}
				}
			}
		}
		rssCompositeUserFeed.saveXMLObjectToFileByFeedId(compositeFeedID);
		
		// Cache
			CacheCompositeUserFeed.getInstance().updateItem(compositeFeedID, rssCompositeUserFeed);	
		
		//
		int newCountOfItemsInCompositeFeedFile = rssCompositeUserFeed.getChannel().getItem().size();
		user.saveXMLObjectToFileByLogin();
		int newCountOfFeedsInCompositeUserFeed = cuf.getFeedIds().size();
		return new int[] {newCountOfFeedsInCompositeUserFeed - oldCountOfFeedsInCompositeUserFeed, newCountOfItemsInCompositeFeedFile - oldCountOfItemsInCompositeFeedFile};
	}
	
}
