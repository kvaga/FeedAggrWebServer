package ru.kvaga.rss.feedaggr.objects;

import java.util.Comparator;

public class RSSForPrintingComparatorByTitle implements Comparator<RSS>{

	public int compare(RSS o1, RSS o2) {
		if(o1.getChannel().getTitle() == o2.getChannel().getTitle())
			return 0;
		if(o1.getChannel().getTitle()==null)
			return 1;
		if(o2.getChannel().getTitle()==null)
			return -1;
		
		return o1.getChannel().getTitle().compareTo(o2.getChannel().getTitle());
	}

}
