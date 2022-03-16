package ru.kvaga.rss.feedaggrwebserver.cache;

import java.util.concurrent.ConcurrentHashMap;

public class CacheUserFeed {
	private static CacheUserFeed cache=null;
	private ConcurrentHashMap<String, CacheElement> map=new ConcurrentHashMap<String, CacheElement>();	

	private CacheUserFeed() {}
	public static CacheUserFeed getInstance() {
		if(cache==null) {
			cache = new CacheUserFeed();
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
