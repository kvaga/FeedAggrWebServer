package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.math.BigInteger;

public class UserFeed {
	private String id;
	private String userFeedTitle;
	private String userFeedUrl;
	private String itemTitleTemplate;
	private String itemLinkTemplate;
	private String itemContentTemplate;
	private String repeatableSearchPattern;
	private String filterWords;
	private Long durationInMillisForUpdate;

	public UserFeed() {
	}

	public UserFeed(String id, String itemTitleTemplate, String itemLinkTemplate, String itemContentTemplate,
			String repeatableSearchPattern, String filterWords, long durationInMillisForUpdate, String userFeedTitle, String userFeedUrl) {
		this.id = id;
		this.userFeedTitle=userFeedTitle;
		this.userFeedUrl=userFeedUrl;
		this.itemTitleTemplate = itemTitleTemplate;
		this.itemLinkTemplate = itemLinkTemplate;
		this.itemContentTemplate = itemContentTemplate;
		this.repeatableSearchPattern = repeatableSearchPattern;
		this.filterWords = filterWords;
		this.durationInMillisForUpdate = durationInMillisForUpdate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getUserFeedUrl() {
		return userFeedUrl;
	}

	public void setUserFeedUrl(String userFeedUrl) {
		this.userFeedUrl = userFeedUrl;
	}
	
	public String getUserFeedTitle() {
		return userFeedTitle;
	}

	public void setUserFeedTitle(String userFeedTitle) {
		this.userFeedTitle = userFeedTitle;
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

	public String getFilterWords() {
		return filterWords;
	}

	public void setFilterWords(String filterWords) {
		this.filterWords = filterWords;
	}

	public Long getDurationInMillisForUpdate() {
		return durationInMillisForUpdate;
	}

	public long setDurationInMillisForUpdate(Long durationInMillisForUpdate) {
		return this.durationInMillisForUpdate = durationInMillisForUpdate;
	}

	// �������������� hashCode � equals ��� ����������� ��������� � ������ ���������
	// �������� � HashSet
	public int hashCode() {
		return new BigInteger(id.getBytes()).intValue();
	}

	public boolean equals(Object object) {
		if (object instanceof UserFeed) {
			return equals((UserFeed) object);
		} else {
			return false;
		}
	}

	public boolean equals(UserFeed object) {
		return object.id.equals(this.getId());
	}

}
