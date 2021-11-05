package ru.kvaga.rss.feedaggr.objects;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Item{
	private GUID guid;
	private Date pubDate;
	private String title;
	private String link;
	private String description;
	
	public Item() {}
	public Item(String title, String link, String description, GUID guid, Date pubDate ) {
		this.title=title;
		this.link=link;
		this.description=description;
		this.guid=guid;
		this.pubDate=pubDate;
	}
	public GUID getGuid() {
		return guid;
	}
	public void setGuid(GUID guid) {
		this.guid = guid;
	}
	public Date getPubDate() {
		return pubDate;
	}
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean equals(Object obj) {
		Item i = (Item) obj;
		if(i.getGuid().getValue().equals(guid.getValue())) {
			return true;
		}else {
			return false;
		}
	}
//	public int hashCode() {
//		return guid.getValue().getBytes().hashCode();
//	}
	public String toString() {
		return "Feed title ["+title+"], link ["+link+"], pubDate ["+pubDate+"], guid ["+(guid==null?null:guid.getValue())+"]";
	}
	
}