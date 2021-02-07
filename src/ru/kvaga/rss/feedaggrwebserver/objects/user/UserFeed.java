package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.math.BigInteger;

public class UserFeed {
	private String id;
	private String itemTitleTemplate;
	private String itemLinkTemplate;
	private String itemContentTemplate;
	private String repeatableSearchPattern;
	
	public UserFeed() {}
	public UserFeed(String id, String itemTitleTemplate, String itemLinkTemplate, String itemContentTemplate, String repeatableSearchPattern) {
		this.id=id;
		this.itemTitleTemplate=itemTitleTemplate;
		this.itemLinkTemplate=itemLinkTemplate;
		this.itemContentTemplate=itemContentTemplate;
		this.repeatableSearchPattern=repeatableSearchPattern;
//		System.out.println(hashCode());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getItemTitleTemplate() {
		return itemTitleTemplate;
	}
	public void setItemTitleTemplate(String itemTitleTemplate) {
		this.itemTitleTemplate = itemTitleTemplate;
	}
	public String getItemLinkTemplate() {
		return itemLinkTemplate;
	}
	public void setItemLinkTemplate(String itemLinkTemplate) {
		this.itemLinkTemplate = itemLinkTemplate;
	}
	public String getItemContentTemplate() {
		return itemContentTemplate;
	}
	public void setItemContentTemplate(String itemContentTemplate) {
		this.itemContentTemplate = itemContentTemplate;
	}
	
	public String getRepeatableSearchPattern() {
		return repeatableSearchPattern;
	}
	public void setRepeatableSearchPattern(String repeatableSearchPattern) {
		this.repeatableSearchPattern = repeatableSearchPattern;
	}
	
	// Переопределяем hashCode и equals для корректного сравнения и поиска уникальнх объектов в HashSet
	public int hashCode() {
//		System.out.println("==========: " + new BigInteger(id.getBytes()).intValue());
		return new BigInteger(id.getBytes()).intValue();
	}
	public boolean equals(Object object) {
		if(object instanceof UserFeed) {
			return object.hashCode()==this.hashCode();
		}else {
			return false;
		}
	}
}
