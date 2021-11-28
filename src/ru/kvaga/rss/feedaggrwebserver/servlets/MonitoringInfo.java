package ru.kvaga.rss.feedaggrwebserver.servlets;

public class MonitoringInfo {
	private boolean feedsUpdateJobIsWorkingNow;
	private boolean compositeFeedUpdateJobIsWorkingNow;
	public boolean getFeedsUpdateJobIsWorkingNow() {
		return feedsUpdateJobIsWorkingNow;
	}
	public void setFeedsUpdateJobIsWorkingNow(boolean feedsUpdateJobIsWorkingNow) {
		this.feedsUpdateJobIsWorkingNow = feedsUpdateJobIsWorkingNow;
	}
	public boolean getCompositeFeedUpdateJobIsWorkingNow() {
		return compositeFeedUpdateJobIsWorkingNow;
	}
	public void setCompositeFeedUpdateJobIsWorkingNow(boolean compositeFeedUpdateJobIsWorkingNow) {
		this.compositeFeedUpdateJobIsWorkingNow = compositeFeedUpdateJobIsWorkingNow;
	}

	
	
}
