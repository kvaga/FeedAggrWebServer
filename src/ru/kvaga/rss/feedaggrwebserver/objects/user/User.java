package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
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

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
@XmlRootElement
public class User {
	private static Logger log=LogManager.getLogger(User.class);
	private String name;
	private Set<UserFeed> userFeeds=new HashSet<UserFeed>();
	private Set<CompositeUserFeed> compositeUserFeeds=new HashSet<CompositeUserFeed>();
	private Set<UserRepeatableSearchPattern> repeatableSearchPatterns = new HashSet<UserRepeatableSearchPattern>();
	private Set<UserRssItemPropertiesPatterns> rssItemPropertiesPatterns = 
			ConcurrentHashMap.newKeySet();
//		new UserRssItemPropertiesPatternsSet()
			;

	public User() {}
	public User(String name) {
		this.name=name;
	};
	public User(String name, HashSet<UserFeed> userFeeds) {
		this.name=name;
		this.userFeeds=userFeeds;
	};
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="userFeed")
	public Set<UserFeed> getUserFeeds() {
		return userFeeds;
	}
	public void setUserFeeds(Set<UserFeed> userFeeds) {
		this.userFeeds = userFeeds;
	}
	
	@XmlElement(name="compositeUserFeed")
	public Set<CompositeUserFeed> getCompositeUserFeeds() {
		return compositeUserFeeds;
	}
	public void setCompositeUserFeeds(Set<CompositeUserFeed> compositeUserFeeds) {
		this.compositeUserFeeds = compositeUserFeeds;
	}
	
	@XmlElement(name="repeatableSearchPattern")
	public Set<UserRepeatableSearchPattern> getRepeatableSearchPatterns() {
		return repeatableSearchPatterns;
	}
	public void setRepeatableSearchPatterns(Set<UserRepeatableSearchPattern> repeatableSearchPatterns) {
		this.repeatableSearchPatterns = repeatableSearchPatterns;
	}
	
	@XmlElement(name="rssItemPropertiesPatterns")
	public Set<UserRssItemPropertiesPatterns> getRssItemPropertiesPatterns() {
		return rssItemPropertiesPatterns;
	}
	public void setRssItemPropertiesPatterns(UserRssItemPropertiesPatternsSet rssItemPropertiesPatterns) {
		this.rssItemPropertiesPatterns = rssItemPropertiesPatterns;
	}
	
	public synchronized void updateRssItemPropertiesPatterns(UserRssItemPropertiesPatterns userRssItemPropertiesPatterns) {
		 long t1 = new Date().getTime();
		for(UserRssItemPropertiesPatterns iterItem : getRssItemPropertiesPatterns()) {
			if (iterItem.equals(userRssItemPropertiesPatterns)) {
				iterItem.setPatternTitle(userRssItemPropertiesPatterns.getPatternTitle());
				iterItem.setPatternLink(userRssItemPropertiesPatterns.getPatternLink());
				iterItem.setPatternDescription(userRssItemPropertiesPatterns.getPatternDescription());
			} else {
				getRssItemPropertiesPatterns().add(userRssItemPropertiesPatterns);
			}
		}
	}
	public boolean containsFeedId(String feedId) {
		 long t1 = new Date().getTime();
		for(UserFeed userFeed : getUserFeeds()) {
			if(userFeed.getId().equals(feedId)) {
				InfluxDB.getInstance().send("response_time,method=User.containsFeedId", new Date().getTime() - t1);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return a map of urls and feedIds
	 * @throws JAXBException
	 */
	public HashMap<String, String> getAllUserFeedUrls() throws JAXBException {
		 long t1 = new Date().getTime();
		 HashMap<String, String> map = new HashMap<String, String>();
		for(UserFeed userFeed : getUserFeeds()) {
			RSS rss = RSS.getRSSObjectFromXMLFile(ConfigMap.feedsPath.getAbsoluteFile() + "/" + userFeed.getId() + ".xml");
			map.put(rss.getChannel().getLink(), userFeed.getId());
		}
		log.debug("Found ["+map.size()+"] urls of user ["+name+"]");
		InfluxDB.getInstance().send("response_time,method=User.containsFeedIdByUrl", new Date().getTime() - t1);
		return map;
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
		long t1 = new Date().getTime();
		for(UserFeed userFeed : getUserFeeds()) {
			RSS rss = RSS.getRSSObjectFromXMLFile(ConfigMap.feedsPath.getAbsoluteFile() + "/" + userFeed.getId() + ".xml");
			if(rss.getChannel().getLink().equals(url)) {
				InfluxDB.getInstance().send("response_time,method=User.containsFeedIdByUrl", new Date().getTime() - t1);
				return userFeed.getId();
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.containsFeedIdByUrl", new Date().getTime() - t1);
		return null;
		*/
	}
	
	public String containsFeedIdByUrl(String url, HashMap<String, String> localUrlsCache) throws JAXBException {
		long t1 = new Date().getTime();
		if (localUrlsCache != null) {
			if (localUrlsCache.containsKey(url)) {
				InfluxDB.getInstance().send("response_time,method=User.containsFeedIdByUrl", new Date().getTime() - t1);
				return localUrlsCache.get(url);
			}
		} else {
			for(UserFeed userFeed : getUserFeeds()) {
				RSS rss = RSS.getRSSObjectFromXMLFile(ConfigMap.feedsPath.getAbsoluteFile() + "/" + userFeed.getId() + ".xml");
				if(rss.getChannel().getLink().equals(url)) {
					InfluxDB.getInstance().send("response_time,method=User.containsFeedIdByUrl", new Date().getTime() - t1);
					return userFeed.getId();
				}
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.containsFeedIdByUrl", new Date().getTime() - t1);
		return null;
	}

	public boolean containsCompositeFeedId(String compositeFeedId) {
		 long t1 = new Date().getTime();
		for(CompositeUserFeed userFeed : getCompositeUserFeeds()) {
			if(userFeed.getId().equals(compositeFeedId)) {
				InfluxDB.getInstance().send("response_time,method=User.containsCompositeFeedId", new Date().getTime() - t1);
				return true;
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.containsCompositeFeedId", new Date().getTime() - t1);
		return false;
	}
	
	public CompositeUserFeed getCompositeUserFeedById(String feedId) throws Exception {
		 long t1 = new Date().getTime();
		for(CompositeUserFeed userFeed : getCompositeUserFeeds()) {
			if(userFeed.getId().equals(feedId)) {
				InfluxDB.getInstance().send("response_time,method=User.getCompositeUserFeedById", new Date().getTime() - t1);
				return userFeed;
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.getCompositeUserFeedById", new Date().getTime() - t1);
		throw new Exception("User ["+getName()+"] doesn't have such compositeFeed ["+feedId+"]");
	}
	
	public boolean removeCompositeUserFeedById(String feedId) {
		 long t1 = new Date().getTime();
		for (Iterator<CompositeUserFeed> iterator = getCompositeUserFeeds().iterator(); iterator.hasNext();) {
		    if (iterator.next().getId().equals(feedId)) {
		        iterator.remove();
				InfluxDB.getInstance().send("response_time,method=User.removeCompositeUserFeedById", new Date().getTime() - t1);
		        return true;
		    }       
		}
		InfluxDB.getInstance().send("response_time,method=User.removeCompositeUserFeedById", new Date().getTime() - t1);
		return false;
	}
	
	
	public UserRssItemPropertiesPatterns getRssItemPropertiesPatternByDomain(String domain) {
		 long t1 = new Date().getTime();
		for(UserRssItemPropertiesPatterns ursp : getRssItemPropertiesPatterns()) {
			if(ursp.getDomain().equals(domain)) {
				InfluxDB.getInstance().send("response_time,method=User.getRssItemPropertiesPatternByDomain", new Date().getTime() - t1);
				return ursp;
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.getRssItemPropertiesPatternByDomain", new Date().getTime() - t1);
		return null;
	}
	
	public String getRepeatableSearchPatternByDomain(String domain) {
		 long t1 = new Date().getTime();
		for(UserRepeatableSearchPattern ursp : getRepeatableSearchPatterns()) {
			if(ursp.getDomain().equals(domain)) {
				InfluxDB.getInstance().send("response_time,method=User.getRepeatableSearchPatternByDomain", new Date().getTime() - t1);
				return ursp.getPattern();
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.getRepeatableSearchPatternByDomain", new Date().getTime() - t1);
		return null;
	}
	
	public String getRepeatableSearchPatternByFeedId(String feedId) {
		 long t1 = new Date().getTime();
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				InfluxDB.getInstance().send("response_time,method=User.getRepeatableSearchPatternByFeedId", new Date().getTime() - t1);
				return uf.getRepeatableSearchPattern();
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.getRepeatableSearchPatternByFeedId", new Date().getTime() - t1);
		return null;
	}
	
	public String getItemTitleTemplateByFeedId(String feedId) {
		 long t1 = new Date().getTime();
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				return uf.getItemTitleTemplate();
			}
		}
		return null;
	}
	
	public String getItemLinkTemplateByFeedId(String feedId) {
		 long t1 = new Date().getTime();
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				InfluxDB.getInstance().send("response_time,method=User.getItemLinkTemplateByFeedId", new Date().getTime() - t1);
				return uf.getItemLinkTemplate();
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.getItemLinkTemplateByFeedId", new Date().getTime() - t1);
		return null;
	}
	
	public String getItemContentTemplateByFeedId(String feedId) {
		 long t1 = new Date().getTime();
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				InfluxDB.getInstance().send("response_time,method=User.getItemContentTemplateByFeedId", new Date().getTime() - t1);
				return uf.getItemContentTemplate();
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.getItemContentTemplateByFeedId", new Date().getTime() - t1);
		return null;
	}
	
	public String getFilterWordsByFeedId(String feedId) {
		 long t1 = new Date().getTime();
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				InfluxDB.getInstance().send("response_time,method=User.getFilterWordsByFeedId", new Date().getTime() - t1);
				return uf.getFilterWords();
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.getFilterWordsByFeedId", new Date().getTime() - t1);
		return null;
	}
	
	public UserFeed getUserFeedByFeedId(String feedId) {
		 long t1 = new Date().getTime();
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				InfluxDB.getInstance().send("response_time,method=User.getUserFeedByFeedId", new Date().getTime() - t1);
				return uf;
			}
		}
		InfluxDB.getInstance().send("response_time,method=User.getUserFeedByFeedId", new Date().getTime() - t1);
		return null;
	}
	
	public static synchronized User getXMLObjectFromXMLFile(File xmlFile) throws JAXBException {
		 long t1 = new Date().getTime();
    	JAXBContext jaxbContext;
	    jaxbContext = JAXBContext.newInstance(User.class);              
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    User obj = (User) jaxbUnmarshaller.unmarshal(xmlFile);
	    obj.setName(xmlFile.getName().replaceFirst("[.][^.]+$", ""));
		InfluxDB.getInstance().send("response_time,method=User.getXMLObjectFromXMLFile", new Date().getTime() - t1);
	    return obj;
	}
	
	public static synchronized User getXMLObjectFromXMLFileByUserName(String login) throws JAXBException {
		 long t1 = new Date().getTime();
		 log.debug("Loading xml file file by user id ["+login+"]");
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + login + ".xml");
    	JAXBContext jaxbContext;
	    jaxbContext = JAXBContext.newInstance(User.class);              
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    User obj = (User) jaxbUnmarshaller.unmarshal(userFile);
	    obj.setName(login);
		InfluxDB.getInstance().send("response_time,method=User.getXMLObjectFromXMLFileByUserName", new Date().getTime() - t1);
	    return obj;
	}
	
	public static synchronized void changeFeedIdByUserNameWithSaving(String login, String oldFeedId, String newFeedId) throws Exception {
		 long t1 = new Date().getTime();
		File oldXmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + oldFeedId + ".xml");
		File newXmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + newFeedId + ".xml");
		if(newXmlFile.exists()) {
			InfluxDB.getInstance().send("response_time,method=User.changeFeedIdByUserNameWithSaving", new Date().getTime() - t1);
			throw new Exception("File with new feed id ["+newFeedId+"] already exists");
		}
		
		User user = User.getXMLObjectFromXMLFileByUserName(login);
		user.renameFeedWithoutSavingToFile(oldFeedId, newFeedId);
		user.saveXMLObjectToFileByLogin(login);
		
		if(!oldXmlFile.renameTo(newXmlFile)) {
			InfluxDB.getInstance().send("response_time,method=User.changeFeedIdByUserNameWithSaving", new Date().getTime() - t1);
			throw new Exception("Couldn't rename old feed id file ["+oldXmlFile+"] to the new feed id file ["+newXmlFile+"]");
		}
		InfluxDB.getInstance().send("response_time,method=User.changeFeedIdByUserNameWithSaving", new Date().getTime() - t1);
	}
	
	
	 public synchronized void renameFeedWithoutSavingToFile(String oldFeedId, String newFeedId) throws Exception {
		 long t1 = new Date().getTime();
		 if(getUserFeedByFeedId(newFeedId)!=null) {
				InfluxDB.getInstance().send("response_time,method=User.getUserFeedByFeedId", new Date().getTime() - t1);
			 throw new Exception("Feed with newFeedId ["+newFeedId+"] already exists");
		 }
		 UserFeed oldUserFeed = getUserFeedByFeedId(oldFeedId);
		 if(oldUserFeed==null) {
				InfluxDB.getInstance().send("response_time,method=User.getUserFeedByFeedId", new Date().getTime() - t1);
			 throw new Exception("Feed with oldFeedId ["+oldFeedId+"] doesn't exist for this user ["+this.name+"]");
		 }
		 oldUserFeed.setId(newFeedId);
			InfluxDB.getInstance().send("response_time,method=User.getUserFeedByFeedId", new Date().getTime() - t1);
	}
	

	
	 public synchronized void saveXMLObjectToFile(File file) throws JAXBException {
		 long t1 = new Date().getTime();
	        JAXBContext jc = JAXBContext.newInstance(this.getClass());
	        Marshaller m = jc.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        m.marshal(this, file);
			log.debug("Object user [" + getName() + "] successfully saved to the [" + file + "] file");
			InfluxDB.getInstance().send("response_time,method=User.saveXMLObjectToFile", new Date().getTime() - t1);
	}
	 
	 public synchronized void saveXMLObjectToFileByLogin(String login) throws JAXBException {
		 long t1 = new Date().getTime();
			File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + login + ".xml");
	        JAXBContext jc = JAXBContext.newInstance(this.getClass());
	        Marshaller m = jc.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        m.marshal(this, userFile);
			log.debug("Object user [" + getName() + "] successfully saved to the [" + userFile + "] file");
			InfluxDB.getInstance().send("response_time,method=User.saveXMLObjectToFileByLogin", new Date().getTime() - t1);

	}
}
