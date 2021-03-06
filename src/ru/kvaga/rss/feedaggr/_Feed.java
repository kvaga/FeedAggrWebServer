package ru.kvaga.rss.feedaggr;

import java.util.Date;

import ru.kvaga.rss.feedaggrwebserver.objects.user.User;

public class _Feed {
	/*
	private String title;
	private String sourceURL;
	private boolean privateBol;
	private boolean protectedFromEdits;
	private Date lastUpdated;
	private Date nextUpdate;
	private int serviceType;
	public int FREE_SERVICE_TYPE=0;
	
	public _Feed() {
		serviceType=FREE_SERVICE_TYPE;
		privateBol=false;
		protectedFromEdits=true;
	}

	public String getTitle(String title) {
		return title;
	}

	public _Feed(String title, String sourceURL) {
		super();
		this.serviceType=FREE_SERVICE_TYPE;
		this.privateBol=false;
		this.protectedFromEdits=true;
		this.title = title;
		this.sourceURL = sourceURL;
	}
	
	public _Feed(String title, String sourceURL, boolean privateBol, boolean protectedFromEdits, int serviceType) {
		super();
		this.title = title;
		this.sourceURL = sourceURL;
		this.privateBol = privateBol;
		this.protectedFromEdits = protectedFromEdits;
		this.serviceType = serviceType;
	}
	
	public _Feed(String title, String sourceURL, boolean privateBol, boolean protectedFromEdits, Date lastUpdated,
			Date nextUpdate, int serviceType) {
		super();
		this.title = title;
		this.sourceURL = sourceURL;
		this.privateBol = privateBol;
		this.protectedFromEdits = protectedFromEdits;
		this.lastUpdated = lastUpdated;
		this.nextUpdate = nextUpdate;
		this.serviceType = serviceType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public boolean isPrivateBol() {
		return privateBol;
	}

	public void setPrivateBol(boolean privateBol) {
		this.privateBol = privateBol;
	}

	public boolean isProtectedFromEdits() {
		return protectedFromEdits;
	}

	public void setProtectedFromEdits(boolean protectedFromEdits) {
		this.protectedFromEdits = protectedFromEdits;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Date getNextUpdate() {
		return nextUpdate;
	}

	public void setNextUpdate(Date nextUpdate) {
		this.nextUpdate = nextUpdate;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}
	*/
	private static String qqq() {
		User user = new User();
		QQQQ  request = new QQQQ();
		if(user.getRssItemPropertiesPatterns()!=null && user.getRssItemPropertiesPatternByDomain(
				Exec.getDomainFromURL((String)request.getSession().getAttribute("url")))!=null){
			return ""+user.getRssItemPropertiesPatternByDomain(
					Exec.getDomainFromURL((String)request.getSession().getAttribute("url"))).getPatternTitle();
		}else{
			return "{%2}";
		}
	}
	
	
}
class QQQQ{
	public QQQQ2 getSession() {
		return null;
	}
}

class QQQQ2{
	public String getAttribute(String str) {
		return null;
	}
}
