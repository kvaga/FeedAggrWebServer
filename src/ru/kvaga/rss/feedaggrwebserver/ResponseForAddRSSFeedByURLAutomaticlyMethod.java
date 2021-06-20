package ru.kvaga.rss.feedaggrwebserver;

public class ResponseForAddRSSFeedByURLAutomaticlyMethod {
	private int size;
	private String feedId;
	public ResponseForAddRSSFeedByURLAutomaticlyMethod(int size, String feedId){
		this.size=size;
		this.feedId=feedId;
	}
	public int getSize() {
		return size;
	}
	public String getFeedId() {
		return feedId;
	}
}