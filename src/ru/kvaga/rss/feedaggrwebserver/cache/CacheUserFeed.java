package ru.kvaga.rss.feedaggrwebserver.cache;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

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
	
	public CacheElement updateItem(String feedId, RSS rss) {
		CacheElement cacheElement = getItem(feedId);
		Date[] oldestNewest = rss.getOldestNewestPubDate();
		cacheElement.setCountOfItems(rss.getChannel().getItem()!=null?rss.getChannel().getItem().size():0)
		.setLastUpdated(rss.getChannel().getLastBuildDate())
		.setNewestPubDate(oldestNewest[1])
		.setOldestPubDate(oldestNewest[0])
		.setSizeMb((ServerUtils.getRssFeedFileByFeedId(feedId)).length()/1024/1024);
		return cacheElement;
	}
	
	public List<String> getCompositeFeedIdsList() {
		return map.keySet()
                .stream()
                .collect(Collectors.toList());
	}
}
