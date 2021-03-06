package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
	private UserRssItemPropertiesPatternsSet rssItemPropertiesPatterns = new UserRssItemPropertiesPatternsSet();

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
	public UserRssItemPropertiesPatternsSet getRssItemPropertiesPatterns() {
		return rssItemPropertiesPatterns;
	}
	public void setRssItemPropertiesPatterns(UserRssItemPropertiesPatternsSet rssItemPropertiesPatterns) {
		this.rssItemPropertiesPatterns = rssItemPropertiesPatterns;
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
	
	public static void main(String args[]) throws JAXBException {
//		BigInteger bi = new BigInteger("dsfadsfnsdn".getBytes());
//		int q = bi.longValue();
//		User kvaga = new User("kvaga");
//		kvaga.setName("kvaga");
		File file = new File("C:\\eclipseWorkspace\\FeedAggrWebServer\\data\\users\\kvaga.xml ");
//		Set<String> fIds = kvaga.getFeedIds();
//		fIds.add("2526736822660417");
//		fIds.add("54433456543345666542245");
//		fIds.add("qqq");
//		fIds.add("2526736822660417");
//		fIds.add("qqq");
	
//		UserFeed userFeed1 = new UserFeed("2526736822660417", "{%2}", "{%1}", "{%3}\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============ Link ============</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>");
//		UserFeed userFeed2 = new UserFeed("54433456543345666542245", "{%2}", "{%1}", "{%3}\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============ Link ============</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>");
//		UserFeed userFeed3 = new UserFeed("qqq", "{%2}", "{%1}", "{%3}\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============ Link ============</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>");
//		
//		UserFeed userFeed4 = new UserFeed("qqq", "{%2}", "{%1}", "{%3}\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============ Link ============</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>");
		
		UserFeed userFeed1 = new UserFeed("2526736822660417", "{%2}", "{%1}", "{%3}", "some repetable search");
		UserFeed userFeed2 = new UserFeed("54433456543345666542245", "{%2}", "{%1}", "{%3}", "some repeateble search");
		UserFeed userFeed3 = new UserFeed("qqq", "{%2}", "{%1}", "{%3}", "some repet");
		UserFeed userFeed4 = new UserFeed("qqq", "{%2}", "{%1}", "{%3}", "some repet");

		
//		kvaga.getUserFeeds().add(userFeed1);
//		kvaga.getUserFeeds().add(userFeed2);
//		kvaga.getUserFeeds().add(userFeed3);
//		kvaga.getUserFeeds().add(userFeed4);
		
//		ObjectsUtils.saveXMLObjectToFile(kvaga, kvaga.getClass(), file);
		
		User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(file, new User());
		String url = "https://www.youtube.com/sadas";
		UserRssItemPropertiesPatterns t = user.getRssItemPropertiesPatternByDomain(
				Exec.getDomainFromURL(url));
		System.out.println(t.getPatternTitle());
		System.out.println(t.getPatternLink());
		System.out.println(t.getPatternDescription());

		user.getRssItemPropertiesPatterns().update(
				new UserRssItemPropertiesPatterns(
						Exec.getDomainFromURL(url),
						t.getPatternLink(),
						t.getPatternTitle(),
						t.getPatternDescription()
				)
		);
		
		if(user.getRssItemPropertiesPatterns()!=null && user.getRssItemPropertiesPatternByDomain(
				Exec.getDomainFromURL(url))!=null){
			System.out.println(":::"+user.getRssItemPropertiesPatternByDomain(
					Exec.getDomainFromURL(url)).getPatternTitle());
		}else{
			System.out.println(":::{%2}");
		}
		
//		ObjectsUtils.printXMLObject(kvaga1);
		
//		System.out.println(kvaga1.getName());
//		for(UserFeed s: kvaga.getUserFeeds()) {
//			ObjectsUtils.printXMLObject(kvaga1);
//			
//		}
		
		
	}
}
