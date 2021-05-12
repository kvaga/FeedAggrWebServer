package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;

public class UserRssItemPropertiesPatternsSet extends HashSet<UserRssItemPropertiesPatterns> {
	//UserRssItemPropertiesPatterns
	/*
	public synchronized void update(UserRssItemPropertiesPatterns userRssItemPropertiesPatterns) {
		synchronized (userRssItemPropertiesPatterns) {

			Iterator<UserRssItemPropertiesPatterns> iterator = iterator();
	
			UserRssItemPropertiesPatterns iterItem;
			while (iterator.hasNext()) {
				iterItem = iterator.next();

				if (iterItem.equals(userRssItemPropertiesPatterns)) {
					iterItem.setPatternTitle(userRssItemPropertiesPatterns.getPatternTitle());
					iterItem.setPatternLink(userRssItemPropertiesPatterns.getPatternLink());
					iterItem.setPatternDescription(userRssItemPropertiesPatterns.getPatternDescription());
				} else {
					synchronized (userRssItemPropertiesPatterns) {
						
					
						add(userRssItemPropertiesPatterns);
					}
				}
			}
}
	}
	*/

}
