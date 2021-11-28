package ru.kvaga.rss.feedaggrwebserver;

public class ResponseForAddRSSFeedByURLAutomaticlyMethod {
	private int size;
	private String feedId;
	private String feedTitle;
	private String url;
	public ResponseForAddRSSFeedByURLAutomaticlyMethod(int size, String feedId, String url, String feedTitle){
		this.size=size;
		this.feedId=feedId;
		this.url=url;
		this.feedTitle=feedTitle;
	}
	public String getFeedTitle() {
		return feedTitle;
	}
	public String getUrl() {
		return url;
	}
	public int getSize() {
		return size;
	}
	public String getFeedId() {
		return feedId;
	}
}