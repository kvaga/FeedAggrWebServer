package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.util.ArrayList;

public class CompositeFeedShortInfo {
	private String feedId;
	private String name;
//	private int countOfUserFeeds;
	private ArrayList<String> feedIds;
	
	/**
	 * 
	 * @param feedId
	 * @param name
	 * @param countOfUserFeeds
	 */
	public CompositeFeedShortInfo(String feedId, String name, 
			//int countOfUserFeeds
			ArrayList<String> feedIds
			) {
		this.feedId=feedId;
		this.name=name;
//		this.countOfUserFeeds=countOfUserFeeds;
		this.feedIds=feedIds;
	}
	
	public CompositeFeedShortInfo() {}
	
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
//	public int getCountOfUserFeeds() {
//		return countOfUserFeeds;
//	}
//	public void setCountOfUserFeeds(int countOfUserFeeds) {
//		this.countOfUserFeeds = countOfUserFeeds;
//	}
	public ArrayList<String> getFeedIds() {
		return feedIds;
	}
	public void setCountOfUserFeeds(ArrayList<String> feedIds) {
		this.feedIds = feedIds;
	}
	
}
