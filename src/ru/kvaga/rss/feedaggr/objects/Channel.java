package ru.kvaga.rss.feedaggr.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.monitoring.influxdb.InfluxDB2;
import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggrwebserver.monitoring.*;
@XmlRootElement
public class Channel {
	private static Logger log = LogManager.getLogger(Channel.class);

	private ArrayList<Item> item = new ArrayList<Item>();
	private String title;
	private String link;
	private String description;
	private Date lastBuildDate;
	private String generator="Feed Aggr Web Server Generator";
	private int ttl=360;
	
	public Channel() {
	}

	public Channel(String title, String link, String descriprion, Date lastBuildDate, String generator, int ttl,ArrayList<Item> item) {
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
		long t1 = new Date().getTime();
		if(item==null) {
			return false;
		}
		for (Item _i : getItem()) {
			if (item.getGuid().getValue().equals(_i.getGuid().getValue())) {
				//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				return true;
			}
		}
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return false;
	}
	
	public boolean removeItem(Item item) {
		long t1 = new Date().getTime();
		if(item==null) {
			return false;
		}
		for (Item _i : getItem()) {
			if (item.getGuid().getValue().equals(_i.getGuid().getValue())) {
				//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				return true;
			}
		}
//		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return false;
	}

	public void setItemsFromRawHtmlBodyItems(LinkedList<ru.kvaga.rss.feedaggr.Item> itemsFromHtmlBody, String url, String itemTitleTemplate, String itemLinkTemplate, String itemContentTemplate) throws Exception {
		long t1 = new Date().getTime();

		int k = 0;
		String 	itemTitle = null,
				itemLink = null,
				itemContent = null;
		ArrayList<ru.kvaga.rss.feedaggr.objects.Item> items = new ArrayList<ru.kvaga.rss.feedaggr.objects.Item>();

		for (ru.kvaga.rss.feedaggr.Item itemFromHtmlBody : itemsFromHtmlBody) {
			ru.kvaga.rss.feedaggr.objects.Item _item = new ru.kvaga.rss.feedaggr.objects.Item();
			itemTitle = itemTitleTemplate;
			itemLink = itemLinkTemplate;
			itemContent = itemContentTemplate + "<br>" + itemTitle;
			int itemLinkNumber = Exec.getNumberFromItemLink(itemLink);
			itemLink = itemLink.replaceAll("\\{%" + itemLinkNumber + "}", itemFromHtmlBody.get(itemLinkNumber));
			itemLink = Exec.checkItemURLForFullness(url, itemLink);

			//���� ��� ������ ���� {%�} �� ��������
			for (int i = 1; i <= itemFromHtmlBody.length(); i++) {
				try {
					itemTitle = itemTitle.replaceAll("\\{%" + i + "}", itemFromHtmlBody.get(i));
					itemContent = itemContent.replaceAll("\\{%" + itemLinkNumber + "}", itemLink);
					itemContent = itemContent.replaceAll("\\{%" + i + "}", itemFromHtmlBody.get(i));
				} catch (Exception e) {
					log.error("Exception", e);
					continue;
				}finally {
					log.debug("itemTitle=" + itemTitle + ", itemTitleTemplate=" + itemTitleTemplate + ", [item.get(" + i + ")=" + itemFromHtmlBody.get(i) + "]");
					log.debug("itemLink=" + itemLink + ", itemLinkTemplate=" + itemLinkTemplate	+ ", [item.get(" + i + ")=" + itemFromHtmlBody.get(i) + "]");
					//log.debug("itemContent=" + itemContent + ", itemContentTemplate=" + itemContentTemplate + ", [item.get(" + i + ")=" + itemFromHtmlBody.get(i) + "]");
				}
			}
			_item.setTitle(itemTitle);
			_item.setLink(itemLink);
			_item.setDescription(itemContent);
			_item.setPubDate(new Date());
			_item.setGuid(new GUID("false", itemLink));
			items.add(_item);
		}
		setItem(items);
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

	}
	
	public Item getItemByGuid(String guid) {
		
		for(Item i : getItem()) {
			if(i.getGuid().getValue().equals(guid)) {
				return i;
			}
		}
		return null;
	}
}