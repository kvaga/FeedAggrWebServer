package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.monitoring.influxdb2.InfluxDB;
import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.monitoring.*;
public class CompositeUserFeed {
	final private static Logger log = LogManager.getLogger(CompositeUserFeed.class);
 
	private String id;
	private ArrayList<String> feedIds = new ArrayList<String>();
	
	public CompositeUserFeed() {}
	public CompositeUserFeed(String id) {
		this.id=id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<String> getFeedIds() {
		return feedIds;
	}
	public void setFeedIds(ArrayList<String> ids) {
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
		
		File compositeRSSFile = RSS.getRSSFileByFeedId(compositeFeedId);

		// old compatibility
		if (user.getCompositeUserFeeds() == null) {
			user.setCompositeUserFeeds(new HashSet<CompositeUserFeed>());
		}

		// Creating new CompositeUserFeed and adding to user
		CompositeUserFeed compositeUserFeed = new CompositeUserFeed();
		compositeUserFeed.setId(compositeFeedId);
		//user.getCompositeUserFeeds().add(compositeUserFeed);
		
		if(user.getCompositeUserFeeds().add(compositeUserFeed)) {
			log.debug("Composite feed ["+compositeFeedId+"] was added to the ["+user.getName()+"] user");
		}else {
			throw new Exception("Couldn't add composite user feed ["+compositeFeedId+"] to the ["+user.getName()+"] user");
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
					compositeRSS.getChannel().getItem().add(item);
					log.debug("Added item [" + item.getTitle() + "] to the composite items list");
				}
			}
		}

		compositeRSS.saveXMLObjectToFile(RSS.getRSSFileByFeedId(compositeFeedId));
		user.saveXMLObjectToFile(userFile);

		log.debug("Composite RSS was successfully saved to the file [" + RSS.getRSSFileByFeedId(compositeFeedId).getAbsolutePath() + "]");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return compositeRSS;
	}
	
	/**
	 * Update all composite feed files with new items from common feeds. If {@code singleCompositeFeedIdForUpdatingAfterAddingNewFeeds} 
	 * was set and not equals null then pubDate of {@code newlyAddedFeedIdsList} will be updated with current date. It allows to save 
	 * newly added feeds for {@code ConfigMap.UPDATE_COMPOSITE_RSS_FILES_DAYS_COUNT_FOR_DELETION} days before deletion. In the future all
	 * deleted (and old) feeds will be skipped and will not be added to the composite feed
	 * @param userName
	 * @param singleCompositeFeedIdForUpdatingAfterAddingNewFeeds - the composite feed with newly added feeds from {@code newlyAddedFeedIdsList} 
	 * which {@code pubDate} will be updated 
	 * with current date 
	 * @param newlyAddedFeedIdsList - if not equals null then {@code pubDate} of these feedIds will be set to the current date 
	 * @return int[] - allFeedsCount, successFeedsCount, allFeedsCount-successFeedsCount (failed), countOfDeletedOldItems
	 * @throws JAXBException
	 */
	public static synchronized int[] updateItemsInCompositeRSSFilesOfUser(String userName) throws JAXBException {
		long t1 = new Date().getTime();
		int allFeedsCount=0, successFeedsCount=0;
		int countOfDeletedOldItemsTotal=0;
		Date deleteItemsWhichOlderThanThisDate = ServerUtils.getDateSinceToday(-ConfigMap.UPDATE_COMPOSITE_RSS_FILES_DAYS_COUNT_FOR_DELETION);
		log.debug("deleteItemsWhichOlderThanThisDate ["+deleteItemsWhichOlderThanThisDate+"]");
		log.info("Started proccess updateCompositeRSSFilesOfUser for user ["+userName+"]");
		File userFile = User.getUsersFileByUserName(userName);
//		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
		User user = User.getXMLObjectFromXMLFile(userFile);

		for (CompositeUserFeed compositeUserFeed : user.getCompositeUserFeeds()) {
			int countOfDeletedOldItems=0;
//			if(singleCompositeFeedIdForUpdatingAfterAddingNewFeeds!=null && !compositeUserFeed.getId().equals(singleCompositeFeedIdForUpdatingAfterAddingNewFeeds)) {
//				log.debug("Skipped composite feed id ["+compositeUserFeed.getId()+"] because it isn't equal to singleCompositeFeedIdForUpdatingAfterAddingNewFeeds ["+singleCompositeFeedIdForUpdatingAfterAddingNewFeeds+"]");
//				continue;
//			}
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
//							if(singleCompositeFeedIdForUpdatingAfterAddingNewFeeds!=null) {
//								// Set a new date for newly added feed ids and then we will countdown lifetime from that date and delete all old items
//								if(newlyAddedFeedIdsList.contains(feedId)) {// change pubDate only for newly added feeds to the composite
//									itemFromRSSFileForSpecificFeedId.setPubDate(new Date());
//									compositeRSS.getChannel().getItem().add(itemFromRSSFileForSpecificFeedId);
//								}else { // leave old items
//									if(!compositeRSS.getChannel().getItem().contains(itemFromRSSFileForSpecificFeedId)) {
//										System.out.println("itemFromRSSFileForSpecificFeedId: " + itemFromRSSFileForSpecificFeedId.getGuid().getValue() + ", pubDate: " + itemFromRSSFileForSpecificFeedId.getPubDate());
//										compositeRSS.getChannel().getItem().add(itemFromRSSFileForSpecificFeedId);
//									}
//								}
//								log.debug("Added item [" + itemFromRSSFileForSpecificFeedId + "] to the composite ["+compositeRSS+"] items list with newest pubDate ["+itemFromRSSFileForSpecificFeedId.getPubDate()+"]");
//							}else { // 
//								Item existedItem = compositeRSS.getChannel().getItem().get(compositeRSS.getChannel().getItem().indexOf(item));
								//Item existedItem = compositeRSS.getChannel().getItemByGuid(item.getGuid().getValue());
								if(itemFromRSSFileForSpecificFeedId.getPubDate().before(deleteItemsWhichOlderThanThisDate)) { // check if old
									log.debug(itemFromRSSFileForSpecificFeedId + " was skipped and wasn't added to the compose ["+compositeRSS+"] because it older than ["+deleteItemsWhichOlderThanThisDate+"] date");
								}else {
									compositeRSS.getChannel().getItem().add(itemFromRSSFileForSpecificFeedId);
									log.debug("Added item [" + itemFromRSSFileForSpecificFeedId + "] to the composite ["+compositeRSS+"] items list because it newer than ["+deleteItemsWhichOlderThanThisDate+"] date");
								}
//							}      
						}else { // cpmposite feed has already this item therefore we will check the age of this item and delete if it is old
							Item itemFromCompositeRSSFile = compositeRSS.getChannel().getItemByGuid(itemFromRSSFileForSpecificFeedId.getGuid().getValue());
							if(itemFromCompositeRSSFile.getPubDate().before(deleteItemsWhichOlderThanThisDate)) {
//								iterator.remove(); 
								compositeRSS.getChannel().getItem().remove(itemFromCompositeRSSFile);
								log.debug(itemFromCompositeRSSFile + " was deleted from rss composite ["+compositeRSS+"] list because it older than ["+deleteItemsWhichOlderThanThisDate+"] date");
								countOfDeletedOldItems++;
								countOfDeletedOldItemsTotal++;
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
		return new int[] {allFeedsCount, successFeedsCount, allFeedsCount-successFeedsCount, countOfDeletedOldItemsTotal};
	}
	
	/**
	 * Delete all feed ids from composite user feed {@code compositeFeedId} despite {@code feedIdListToSave}
	 * @param userName
	 * @param compositeFeedId
	 * @param finalFeedIdListToSave
	 * return ArrayList of deleted feedIds
	 * @throws Exception 
	 */
	public static ArrayList<String> deleteFeedIdsFromCompositeUserFeedDespiteFinalList(String userName, String compositeFeedId, ArrayList<String> finalFeedIdListToSave) throws Exception {
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
	public static boolean updateRSSTitleOfComposeFeed(String compositeRSSTitle, String compositeFeedId, String userName) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		if(user.getCompositeUserFeedById(compositeFeedId)==null) throw new Exception("User ["+userName+"] doesn't have the ["+compositeFeedId+"] feed");
		RSS rss = RSS.getRSSObjectByFeedId(compositeFeedId);
		if(!rss.getChannel().getTitle().equals(compositeRSSTitle)) {
			rss.getChannel().setTitle(compositeRSSTitle);
			rss.saveXMLObjectToFileByFeedId(compositeFeedId);
			return true;
		}
		return false;
	}
	
}
