package ru.kvaga.rss.feedaggr.objects;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Channel {
	private String title;
	private String link;
	private String description;
	private Date lastBuildDate;
	private String generator;
	private int ttl;
	private ArrayList<Item> item = new ArrayList<Item>();

	public Channel() {
	}

	public Channel(String title, String link, String descriprion, Date lastBuildDate, String generator, int ttl,
			ArrayList<Item> item) {
		this.title = title;
		this.link = link;
		this.description = descriprion;
		this.lastBuildDate = lastBuildDate;
		this.generator = generator;
		this.ttl = ttl;
		this.item = item;
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

	public Date getLastBuildDate() {
		return lastBuildDate;
	}

	public void setLastBuildDate(Date lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public ArrayList<Item> getItem() {
		return item;
	}

	public void setItem(ArrayList<Item> item) {
		this.item = item;
	}

	public boolean containsItem(Item item) {
		for (Item _i : getItem()) {
			if (item.getGuid().getValue().equals(_i.getGuid().getValue())) {
				return true;
			}
		}
		return false;
	}
}