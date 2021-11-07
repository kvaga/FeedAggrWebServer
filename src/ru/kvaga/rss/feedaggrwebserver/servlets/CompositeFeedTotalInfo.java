package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.util.Date;

public class CompositeFeedTotalInfo {
	private String feedId;
	private String name;
	private Date lastUpdated;
	private int countOfItems;
	private float sizeMb;
	private Date oldestPubDate;
	private Date newestPubDate;
	
	public CompositeFeedTotalInfo(String feedId, String name, Date lastUpdated, int countOfItems, float sizeMb, Date oldestPubDate, Date newestPubDate) {
		this.feedId=feedId;
		this.name=name;
		this.lastUpdated=lastUpdated;
		this.countOfItems=countOfItems;
		this.sizeMb=sizeMb;
		this.oldestPubDate=oldestPubDate;
		this.newestPubDate=newestPubDate;
		
	}
	
	public CompositeFeedTotalInfo() {}
	
	public String getFeedId() {
		return feedId;
	}
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public int getCountOfItems() {
		return countOfItems;
	}
	public void setCountOfItems(int countOfItems) {
		this.countOfItems = countOfItems;
	}
	public float getSizeMb() {
		return sizeMb;
	}
	public void setSizeMb(float sizeMb) {
		this.sizeMb = sizeMb;
	}
	public Date getOldestPubDate() {
		return oldestPubDate;
	}
	public void setOldestPubDate(Date oldestPubDate) {
		this.oldestPubDate = oldestPubDate;
	}
	public Date getNewestPubDate() {
		return newestPubDate;
	}
	public void setNewestPubDate(Date newestPubDate) {
		this.newestPubDate = newestPubDate;
	}
	
}
