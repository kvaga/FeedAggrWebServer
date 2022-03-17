package ru.kvaga.rss.feedaggrwebserver.cache;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.stream.Collectors;

import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

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
	
	public CacheElement updateItem(String compositeFeedId, RSS compositeRSS) {
		CacheElement cacheElement = getItem(compositeFeedId);
		Date[] oldestNewest = compositeRSS.getOldestNewestPubDate();
		cacheElement.setCountOfItems(compositeRSS.getChannel().getItem()!=null?compositeRSS.getChannel().getItem().size():0)
		.setLastUpdated(compositeRSS.getChannel().getLastBuildDate())
		.setNewestPubDate(oldestNewest[1])
		.setOldestPubDate(oldestNewest[0])
		.setSizeMb((ServerUtils.getRssFeedFileByFeedId(compositeFeedId)).length()/1024/1024);
		return cacheElement;
	}
	
	public List<String> getCompositeFeedIdsList() {
		return map.keySet()
                .stream()
                .collect(Collectors.toList());
	}
}

