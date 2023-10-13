package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.math.BigInteger;
import java.util.HashMap;

public class UserFeed {
	private String id;
	private String userFeedTitle;
	private String userFeedUrl;
	private String itemTitleTemplate;
	private String itemLinkTemplate;
	private String itemContentTemplate;
	private String repeatableSearchPattern;
	private String filterWords;
	private String skipWords;
	private Long durationInMillisForUpdate;
	// by deafult all userFeeds are active. Therefore they have suspendStatus is false
	private Boolean suspendStatus;

	public UserFeed() {
		suspendStatus=false;
	}

	public UserFeed(String id, String itemTitleTemplate, String itemLinkTemplate, String itemContentTemplate,
			String repeatableSearchPattern, String filterWords, String skipWords, long durationInMillisForUpdate, String userFeedTitle, String userFeedUrl) {
		this();
		this.id = id;
		this.userFeedTitle=userFeedTitle;
		this.userFeedUrl=userFeedUrl;
		this.itemTitleTemplate = itemTitleTemplate;
		this.itemLinkTemplate = itemLinkTemplate;
		this.itemContentTemplate = itemContentTemplate;
		this.repeatableSearchPattern = repeatableSearchPattern;
		this.filterWords = filterWords;
		this.skipWords = skipWords;
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
	
	
	public void setSkipWords(String skipWords) {
		this.skipWords = skipWords;
	}
	public String getSkipWords() {
		return skipWords;
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

	
	// Settings of Specific Common User Settings
//	private HashMap<String, String> settings;
//	public static String USER_FEED_SETTING_FIELD_FILTER_WORDS_DELIMETERED_BY_PIPE="FILTER_WORDS_DELIMETERED_BY_PIPE";
//	public static String USER_FEED_SETTING_FIELD_SKIP_WORDS_DELIMETERED_BY_PIPE = "SKIP_WORDS_DELIMETERED_BY_PIPE";
//	public static String USER_FEED_SETTING_FOOTER_OF_DESCRIPTION = "FOOTER_OF_DESCRIPTION";
//	private static HashMap<String,String> DEFAULT_SPECIFIC_USER_FEED_SETTINGS = new HashMap<String,String>(){{
//		put(USER_FEED_SETTING_FIELD_FILTER_WORDS_DELIMETERED_BY_PIPE, "");
//		put(USER_FEED_SETTING_FIELD_SKIP_WORDS_DELIMETERED_BY_PIPE, "");
//		put(USER_FEED_SETTING_FOOTER_OF_DESCRIPTION, "<br><h1 align=\"center\" style=\"color:blue;font-size:40px;\"><a href=\"%SERVER_URL_OF_WEB_APP%/SettingsOfSpecificUserFeedServlet?redirectTo=/SettingsOfSpecificUserFeed.jsp&command=GetSettingsOfUserFeed&feedId=%FEED_ID%\">USER FEED SETTINGS</a></h1>");
//
//	}};
//	
//	public HashMap<String, String> getSettings() {
//		if(settings==null) {
//			settings=(HashMap<String,String>) DEFAULT_SPECIFIC_USER_FEED_SETTINGS.clone();
//		}
//		return settings;
//	}
//	
//	public HashMap<String,String> setSettings(HashMap<String, String> settings) {
//		this.settings = settings;
//		return settings;
//	}
//	public HashMap<String,String> resetSettings() {
//		this.settings = (HashMap<String,String>)DEFAULT_SPECIFIC_USER_FEED_SETTINGS.clone();
//		return this.settings;
//	}
	
	public boolean equals(UserFeed object) {
		return object.id.equals(this.getId());
	}

	/**
	 * Get the status of userFeed - active or not
	 * @return true  - means userFeed is not active. It is suspended
	 * @return false - means userFeed is active. Isn't suspended
	 */
	public Boolean getSuspendStatus() {
		return suspendStatus;
	}
	
	/**
	 * Set the status of userFeed - active or not
	 * @param status = true   - means userFeed set to 'not active'. It will be suspended
	 * @param status = false  - means userFeed set to 'active'. It will be activated
	 * @return this object
	 */
	public UserFeed setSuspendStatus(Boolean status) {
		this.suspendStatus=status;
		return this;
	}

}
