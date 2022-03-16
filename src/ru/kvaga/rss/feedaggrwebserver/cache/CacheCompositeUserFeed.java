package ru.kvaga.rss.feedaggrwebserver.cache;

import java.util.concurrent.ConcurrentHashMap;

public class CacheCompositeUserFeed {
	private static CacheCompositeUserFeed cache=null;
	private ConcurrentHashMap<String, CacheElement> map=new ConcurrentHashMap<String, CacheElement>();	

	private CacheCompositeUserFeed() {}
	public static CacheCompositeUserFeed getInstance() {
		if(cache==null) {
			cache = new CacheCompositeUserFeed();
		}
		return cache;
	}
	
	public CacheElement getItem(String feedId) {
		if(map.get(feedId)==null) {
			CacheElement ce = new CacheElement();
			map.put(feedId, ce);
			return ce;
		}else {
			return map.get(feedId);
		}
	}
}

