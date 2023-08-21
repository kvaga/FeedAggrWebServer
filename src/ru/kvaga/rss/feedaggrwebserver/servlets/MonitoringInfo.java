package ru.kvaga.rss.feedaggrwebserver.servlets;

import ru.kvaga.rss.feedaggrwebserver.ConfigMap;

public class MonitoringInfo {
	private boolean feedsUpdateJobIsWorkingNow;
	private boolean compositeFeedUpdateJobIsWorkingNow;
	private boolean isJobsPaused;
	
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
	
	public boolean getIsJobsPaused() {
		this.isJobsPaused = ConfigMap.JOBS_PAUSED;
		return isJobsPaused;
	}
	public void setIsJobsPaused(boolean isJobsPaused) {
		this.isJobsPaused = isJobsPaused;
	}

	
	
}
