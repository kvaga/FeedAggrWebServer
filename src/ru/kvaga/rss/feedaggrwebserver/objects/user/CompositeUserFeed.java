package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

import ru.kvaga.monitoring.influxdb2.InfluxDB;
import ru.kvaga.rss.feedaggrwebserver.MonitoringUtils;

public class CompositeUserFeed {
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
	public void setFeedIds(ArrayList<String> id) {
		this.feedIds = feedIds;
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
	
	// Переопределяем hashCode и equals для корректного сравнения и поиска уникальнх объектов в HashSet
	public int hashCode() {
		return new BigInteger(id.getBytes()).intValue();
	}
	public boolean equals(Object object) {
		if(object instanceof UserFeed) {
			return object.hashCode()==this.hashCode();
		}else {
			return false;
		}
	}
	
	public boolean equals(CompositeUserFeed object) {
		return object.id.equals(this.id);
	}
	
	
	
}
