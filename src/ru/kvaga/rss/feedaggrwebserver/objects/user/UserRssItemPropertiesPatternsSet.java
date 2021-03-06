package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.util.HashSet;
import java.util.Iterator;


public class UserRssItemPropertiesPatternsSet extends HashSet<UserRssItemPropertiesPatterns>{
	public void update(UserRssItemPropertiesPatterns userRssItemPropertiesPatterns) {
		Iterator<UserRssItemPropertiesPatterns> iterator = iterator(); 
		while(iterator.hasNext()) {
			UserRssItemPropertiesPatterns iterItem = iterator.next();
			if(iterItem.equals(userRssItemPropertiesPatterns)) {
				iterItem.setPatternTitle(userRssItemPropertiesPatterns.getPatternTitle());
				iterItem.setPatternLink(userRssItemPropertiesPatterns.getPatternLink());
				iterItem.setPatternDescription(userRssItemPropertiesPatterns.getPatternDescription());
			}else {
				add(userRssItemPropertiesPatterns);
			}
		}
	}
}
