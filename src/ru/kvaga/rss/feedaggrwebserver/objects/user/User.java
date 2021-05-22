package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
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

	public User() {
	}
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
		for(UserFeed userFeed : getUserFeeds()) {
			if(userFeed.getId().equals(feedId)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @param url
	 * @return id of feed which contains this @param url
	 * @throws JAXBException 
	 */
	public String containsFeedIdByUrl(String url) throws JAXBException {
		for(UserFeed userFeed : getUserFeeds()) {
			RSS rss = RSS.getRSSObjectFromXMLFile(ConfigMap.feedsPath.getAbsoluteFile() + "/" + userFeed.getId() + ".xml");
			if(rss.getChannel().getLink().equals(url)) {
				return userFeed.getId();
			}
		}
		return null;
	}
	
	public boolean containsCompositeFeedId(String compositeFeedId) {
		for(CompositeUserFeed userFeed : getCompositeUserFeeds()) {
			if(userFeed.getId().equals(compositeFeedId)) {
				return true;
			}
		}
		return false;
	}
	
	public CompositeUserFeed getCompositeUserFeedById(String feedId) throws Exception {
		for(CompositeUserFeed userFeed : getCompositeUserFeeds()) {
			if(userFeed.getId().equals(feedId)) {
				return userFeed;
			}
		}
		throw new Exception("User ["+getName()+"] doesn't have such compositeFeed ["+feedId+"]");
	}
	
	public boolean removeCompositeUserFeedById(String feedId) {
		for (Iterator<CompositeUserFeed> iterator = getCompositeUserFeeds().iterator(); iterator.hasNext();) {
		    if (iterator.next().getId().equals(feedId)) {
		        iterator.remove();
		        return true;
		    }       
		}
		return false;
	}
	
	
	public UserRssItemPropertiesPatterns getRssItemPropertiesPatternByDomain(String domain) {
		for(UserRssItemPropertiesPatterns ursp : getRssItemPropertiesPatterns()) {
			if(ursp.getDomain().equals(domain)) {
				return ursp;
			}
		}
		return null;
	}
	
	public String getRepeatableSearchPatternByDomain(String domain) {
		for(UserRepeatableSearchPattern ursp : getRepeatableSearchPatterns()) {
			if(ursp.getDomain().equals(domain)) {
				return ursp.getPattern();
			}
		}
		return null;
	}
	
	public String getRepeatableSearchPatternByFeedId(String feedId) {
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				return uf.getRepeatableSearchPattern();
			}
		}
		return null;
	}
	
	public String getItemTitleTemplateByFeedId(String feedId) {
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				return uf.getItemTitleTemplate();
			}
		}
		return null;
	}
	
	public String getItemLinkTemplateByFeedId(String feedId) {
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				return uf.getItemLinkTemplate();
			}
		}
		return null;
	}
	
	public String getItemContentTemplateByFeedId(String feedId) {
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				return uf.getItemContentTemplate();
			}
		}
		return null;
	}
	
	public String getFilterWordsByFeedId(String feedId) {
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				return uf.getFilterWords();
			}
		}
		return null;
	}
	
	public UserFeed getUserFeedByFeedId(String feedId) {
		for(UserFeed uf : getUserFeeds()) {
			if(uf.getId().equals(feedId)) {
				return uf;
			}
		}
		return null;
	}
	
	public static synchronized User getXMLObjectFromXMLFile(File xmlFile) throws JAXBException {
    	JAXBContext jaxbContext;
	    jaxbContext = JAXBContext.newInstance(User.class);              
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    User obj = (User) jaxbUnmarshaller.unmarshal(xmlFile);
	    return obj;
	}
	
	public static synchronized User getXMLObjectFromXMLFileByUserName(String login) throws JAXBException {
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + login + ".xml");
    	JAXBContext jaxbContext;
	    jaxbContext = JAXBContext.newInstance(User.class);              
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    User obj = (User) jaxbUnmarshaller.unmarshal(userFile);
	    obj.setName(login);
	    return obj;
	}
	
	public static synchronized void changeFeedIdByUserNameWithSaving(String login, String oldFeedId, String newFeedId) throws Exception {
		File oldXmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + oldFeedId + ".xml");
		File newXmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + newFeedId + ".xml");
		if(newXmlFile.exists()) {
			throw new Exception("File with new feed id ["+newFeedId+"] already exists");
		}
		
		User user = User.getXMLObjectFromXMLFileByUserName(login);
		user.renameFeedWithoutSavingToFile(oldFeedId, newFeedId);
		user.saveXMLObjectToFileByLogin(login);
		
		if(!oldXmlFile.renameTo(newXmlFile)) {
			throw new Exception("Couldn't rename old feed id file ["+oldXmlFile+"] to the new feed id file ["+newXmlFile+"]");
		}
	}
	
	
	 public synchronized void renameFeedWithoutSavingToFile(String oldFeedId, String newFeedId) throws Exception {
		 if(getUserFeedByFeedId(newFeedId)!=null) {
			 throw new Exception("Feed with newFeedId ["+newFeedId+"] already exists");
		 }
		 UserFeed oldUserFeed = getUserFeedByFeedId(oldFeedId);
		 if(oldUserFeed==null) {
			 throw new Exception("Feed with oldFeedId ["+oldFeedId+"] doesn't exist for this user ["+this.name+"]");
		 }
		 oldUserFeed.setId(newFeedId);
	}
	

	
	 public synchronized void saveXMLObjectToFile(File file) throws JAXBException {
	        JAXBContext jc = JAXBContext.newInstance(this.getClass());
	        Marshaller m = jc.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        m.marshal(this, file);
			log.debug("Object user [" + getName() + "] successfully saved to the [" + file + "] file");
	}
	 
	 public synchronized void saveXMLObjectToFileByLogin(String login) throws JAXBException {
			File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + login + ".xml");
	        JAXBContext jc = JAXBContext.newInstance(this.getClass());
	        Marshaller m = jc.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        m.marshal(this, userFile);
			log.debug("Object user [" + getName() + "] successfully saved to the [" + userFile + "] file");
	}
}
