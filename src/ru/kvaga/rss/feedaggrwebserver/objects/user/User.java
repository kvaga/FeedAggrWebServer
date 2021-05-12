package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
@XmlRootElement
public class User {
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
	

}
