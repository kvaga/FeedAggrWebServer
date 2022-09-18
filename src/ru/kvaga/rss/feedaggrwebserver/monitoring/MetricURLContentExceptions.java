package ru.kvaga.rss.feedaggrwebserver.monitoring;

import java.util.HashMap;
import java.util.HashSet;

public class MetricURLContentExceptions {
	private static MetricURLContentExceptions metric = null;
	private static HashMap<String, HashSet<String>> map=null;
	private MetricURLContentExceptions() {}
	public static MetricURLContentExceptions getInstance() {
		if(metric == null) {
			metric = new MetricURLContentExceptions();
			map = new HashMap<String,HashSet<String>>();
		}
		return metric;
	}
	public void add(Exception e, String url) {
		String cause = e.getCause().toString()
				.replaceAll("\\[https?.*\\]", "[<<<STANZA>>>]")
				.replaceAll("java.io.FileNotFoundException: https?.*", "java.io.FileNotFoundException: <<<STANZA>>>")
				.replaceAll("for URL: http.*", "for URL: <<<STANZA>>>")
				.replaceAll("String for splitting \\[.*\\]", "String for splitting [<<<STANZA>>>]")
				;
		cause=cause.trim();
		cause="'"+cause+"' Size: " + cause.length();
		if(!map.containsKey(cause)) {
			HashSet<String> set = new HashSet<String>();
			set.add(url);
			map.put(cause, set);
		}else {
			map.get(cause).add(url);
		}
	}
	public static HashMap<String, HashSet<String>> getAllExceptions(){
		return map;
	}
}
