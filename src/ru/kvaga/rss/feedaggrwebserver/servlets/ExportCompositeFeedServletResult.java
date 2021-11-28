package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.monitoring.MonitoringUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

@XmlRootElement
public class ExportCompositeFeedServletResult {
	private CompositeUserFeed compositeUserFeed;
	private RSS compositeRSS;
	private ArrayList<RSS> feedRSSList;
	ArrayList<UserFeed> userFeedList;
	
	public RSS getCompositeRSS() {
		return compositeRSS;
	}

	public void setCompositeRSS(RSS compositeRSS) {
		this.compositeRSS = compositeRSS;
	}

	public ExportCompositeFeedServletResult() {
	}
	
	public ExportCompositeFeedServletResult(CompositeUserFeed compositeUserFeed, RSS compositeRSS, ArrayList<RSS> feedRSSList, ArrayList<UserFeed> userFeedList) {
		this.compositeUserFeed=compositeUserFeed;
		this.compositeRSS=compositeRSS;
		this.feedRSSList=feedRSSList;
		this.userFeedList=userFeedList;
	}
	public ArrayList<UserFeed> getUserFeedList() {
		return userFeedList;
	}

	public void setUserFeedList(ArrayList<UserFeed> userFeedList) {
		this.userFeedList = userFeedList;
	}

	public ArrayList<RSS> getFeedRSSList() {
		return feedRSSList;
	}

	public void setFeedRSSList(ArrayList<RSS> feedRSSList) {
		this.feedRSSList = feedRSSList;
	}

	public CompositeUserFeed getCompositeUserFeed() {
		return compositeUserFeed;
	}

	public void setCompositeUserFeed(CompositeUserFeed compositeUserFeed) {
		this.compositeUserFeed = compositeUserFeed;
	}
	
	
//	private CompositeUserFeed compositeUserFeed;
//	private RSS compositeRSS;
//	private ArrayList<RSS> feedRSSList = new ArrayList<RSS>();
	
	
//	public ExportCompositeFeedServletResult(CompositeUserFeed compositeUserFeed, RSS compositeRSS, ArrayList<RSS> feedRSSList) {
//		this.compositeUserFeed=compositeUserFeed;
////		this.compositeRSS=compositeRSS;
////		this.feedRSSList=feedRSSList;
//	}

//    @XmlElement(name = "CompositeUserFeed")
//	public CompositeUserFeed getCompositeUserFeed() {
//		return compositeUserFeed;
//	}
    
//	public void setCompositeUserFeed(CompositeUserFeed compositeUserFeed) {
//		this.compositeUserFeed = compositeUserFeed;
//	}
    
//    @XmlElement(name = "CompositeRSS")
//	public RSS getCompositeRSS() {
//		return compositeRSS;
//	}
//	public void setCompositeRSS(RSS compositeRSS) {
//		this.compositeRSS = compositeRSS;
//	}
//    @XmlElement(name = "FeedRSSList")
//	public ArrayList<RSS> getFeedRSSList() {
//		return feedRSSList;
//	}
//	
//	public void setFeedRSSList(ArrayList<RSS> feedRSSList) {
//		this.feedRSSList = feedRSSList;
//	}
	
	
}
