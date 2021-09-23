package ru.kvaga.rss.feedaggr.objects;

import java.util.Comparator;
import java.util.Date;

import ru.kvaga.monitoring.influxdb2.InfluxDB;
import ru.kvaga.rss.feedaggrwebserver.MonitoringUtils;

public class RSSForPrintingComparatorByTitle implements Comparator<RSS>{

	public int compare(RSS o1, RSS o2) {
		long t1 = new Date().getTime();
		if(o1.getChannel().getTitle() == o2.getChannel().getTitle())
			return 0;
		if(o1.getChannel().getTitle()==null)
			return 1;
		if(o2.getChannel().getTitle()==null)
			return -1;
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return o1.getChannel().getTitle().compareTo(o2.getChannel().getTitle());
	}

}
