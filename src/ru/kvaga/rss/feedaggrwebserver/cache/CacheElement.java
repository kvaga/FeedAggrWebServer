package ru.kvaga.rss.feedaggrwebserver.cache;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

public class CacheElement {
	private int countOfItems=-1;
	private Date lastUpdated;
	private float sizeMb=-1;
	private Date oldestPubDate;
	private Date newestPubDate;
	private String lastUpdateStatus=null;
	
	public CacheElement() {}
	
	public CacheElement(int countOfItems,Date lastUpdated, float sizeMb, Date oldestPubDate, Date newestPubDate) {
		this.countOfItems = countOfItems;
		this.lastUpdated = lastUpdated;
		this.sizeMb = sizeMb;
		this.oldestPubDate = oldestPubDate;
		this.newestPubDate = newestPubDate;
	}
	
	public int getCountOfItems() {
		return countOfItems;
	}
	public CacheElement setCountOfItems(int countOfItems) {
		this.countOfItems = countOfItems;
		return this;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public CacheElement setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
		return this;
	}
	public float getSizeMb() {
		return sizeMb;
	}
	public CacheElement setSizeMb(float sizeMb) {
		this.sizeMb = sizeMb;
		return this;
	}
	public Date getOldestPubDate() {
		return oldestPubDate;
	}
	public CacheElement setOldestPubDate(Date oldestPubDate) {
		this.oldestPubDate = oldestPubDate;
		return this;
	}
	public Date getNewestPubDate() {
		return newestPubDate;
	}
	public CacheElement setNewestPubDate(Date newestPubDate) {
		this.newestPubDate = newestPubDate;
		return this;
	}
	public String getLastUpdateStatus() {
		return lastUpdateStatus;
	}
	public CacheElement setLastUpdateStatus(String lastUpdateStatus) {
		this.lastUpdateStatus = lastUpdateStatus;
		return this;
	}
	
}
