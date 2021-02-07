package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
@XmlRootElement
public class User {
	private String name;
	private Set<UserFeed> userFeeds;

	public User() {
		
	}
	public User(String name) {
		this.name=name;
		this.userFeeds=new HashSet<UserFeed>();
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
	
	public static void main(String args[]) throws JAXBException {
//		BigInteger bi = new BigInteger("dsfadsfnsdn".getBytes());
//		int q = bi.longValue();
		User kvaga = new User("kvaga");
//		kvaga.setName("kvaga");
		File file = new File("C:\\eclipseWorkspace\\FeedAggrWebServer\\WebContent\\data\\users\\kvaga.xml");
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

		
		kvaga.getUserFeeds().add(userFeed1);
		kvaga.getUserFeeds().add(userFeed2);
		kvaga.getUserFeeds().add(userFeed3);
		kvaga.getUserFeeds().add(userFeed4);
		
		ObjectsUtils.saveXMLObjectToFile(kvaga, kvaga.getClass(), file);
		
		User kvaga1 = (User) ObjectsUtils.getXMLObjectFromXMLFile(file, new User());
		ObjectsUtils.printXMLObject(kvaga1);
		
//		System.out.println(kvaga1.getName());
//		for(UserFeed s: kvaga.getUserFeeds()) {
//			ObjectsUtils.printXMLObject(kvaga1);
//			
//		}
		
		
	}
}
