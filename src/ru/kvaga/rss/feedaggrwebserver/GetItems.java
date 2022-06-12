package ru.kvaga.rss.feedaggrwebserver;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.FeedAggrException;
import ru.kvaga.rss.feedaggr.Item;
import ru.kvaga.rss.feedaggrwebserver.monitoring.MonitoringUtils;

public class GetItems implements Callable<LinkedList<Item>>{
	private static final Logger log = LogManager.getLogger(GetItems.class);

	private String responseHtmlBody=null; 
	private String substringForHtmlBodySplit=null; 
	private String repeatableSearchPattern=null;
	private int countOfPercentItemsInSearchPattern=-1;
	private String filterWords=null;
	
	public GetItems(String responseHtmlBody, 
			String substringForHtmlBodySplit, 
			String repeatableSearchPattern,
			int countOfPercentItemsInSearchPattern) {
		this.responseHtmlBody=responseHtmlBody; 
		this.substringForHtmlBodySplit=substringForHtmlBodySplit; 
		this.repeatableSearchPattern=repeatableSearchPattern;
		this.countOfPercentItemsInSearchPattern=countOfPercentItemsInSearchPattern;
	}
	public GetItems(String responseHtmlBody, 
			String substringForHtmlBodySplit, 
			String repeatableSearchPattern,
			int countOfPercentItemsInSearchPattern,
			String filterWords) {
		this.responseHtmlBody=responseHtmlBody; 
		this.substringForHtmlBodySplit=substringForHtmlBodySplit; 
		this.repeatableSearchPattern=repeatableSearchPattern;
		this.countOfPercentItemsInSearchPattern=countOfPercentItemsInSearchPattern;
		this.filterWords=filterWords;
	}
	
	
	public LinkedList<Item> getItems() throws FeedAggrException.SplitHTMLContent {
		
		long t1 = new Date().getTime();
		int i = 0;
		log.debug("Filter words were set ["+filterWords+"]");

		repeatableSearchPattern = repeatableSearchPattern
				.replaceAll("\\{\\*}", ".*")
				.replaceAll("\\{%}", "(.*)")
				.replaceAll("/", "\\\\/");
		responseHtmlBody = responseHtmlBody.replaceAll("\r\n", "").replaceAll("\n", "");

		LinkedList<Item> ll = new LinkedList<Item>();
		log.debug("substringForHtmlBodySplit="+substringForHtmlBodySplit);
		log.debug("is response html body null? => "+(responseHtmlBody==null?null:"not null"));
		log.debug("countOfPercentItemsInSearchPattern="+countOfPercentItemsInSearchPattern);
		log.debug("repeatableSearchPattern="+repeatableSearchPattern);
		for (String s : splitHtmlContent(responseHtmlBody, substringForHtmlBodySplit)) {
			if (i == 0) {
				i++;
				continue;
			}
			s = substringForHtmlBodySplit + s;
			
			// Check a presence of filter words in the item if filter set 
			if(filterWords!=null && !filterWords.equals("")) {
				boolean contains = false;
				for(String word : filterWords.split("\\|")) {
					log.debug("Searching word ["+word+"] in the string ["+s+"]");
					if(s.toLowerCase().contains(word.toLowerCase())) {
						log.debug("Found filter word ["+word+"] in item therefore saving item ["+s+"]");
						contains=true;
						break;
					}
				}
				if(!contains) {
					log.debug("Didn't find filter words in item. Therefore skipping this item ["+s+"]");
					continue;
				}
			}
//			System.err.println("=============" + ++i + "==========");
//			System.err.println(repeatableSearchPattern);
//			System.err.println(s);
			repeatableSearchPattern=repeatableSearchPattern.trim();
			String temp_text_pattern = repeatableSearchPattern
					.replaceAll("(\\\\r\\\\n|\\\\n)", ".")
					.replaceAll("\\{", "\\\\{")
//					.replace('\r', '.')
//					.replace('\n', '.')
//					.replace("\\", "\\\\")
//					.replaceAll("\\{", "\\\\{")
					;
			Pattern pattern = Pattern.compile(temp_text_pattern);
			Matcher matcher = pattern.matcher(s);
			if (matcher.find()) {
//				log.debug("Item found");
//				System.err.println("[0]: " + matcher.group(0));
				Item item = new Item();
				
				log.debug("GetItems: matcher groups: ");

				for(int j=1;j<=/*countOfPercentItemsInSearchPattern*/ matcher.groupCount();j++) {
					log.debug("GetItems: matcher group["+j+"]: " + matcher.group(j));

					item.add(j,matcher.group(j).replaceAll("\\$", "(dollar sign)"));
//					System.err.println("{%"+j+"}: " +item.get(j));
				}
				log.debug("GetItems: Added item " + item.getContentForPrinting() + " to a list \n ");
				ll.add(item);
			} else {
//				log.error("-1==> temp_text_pattern: " + temp_text_pattern);
//				log.error("0==> repeatableSearch: "+repeatableSearchPattern);
//				log.error("1==> " + s);
//				log.error("2==> " + repeatableSearchPattern);

				log.warn("Couldn't find item in the piece ["+(s.length()>=repeatableSearchPattern.length()?s.substring(0,repeatableSearchPattern.length()-1):s)+"] of html content by regex expression ["+repeatableSearchPattern+"] and substringForHtmlBodySplit ["+substringForHtmlBodySplit+"]");
				
				
			}

		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

		return ll;
		
	}
	private String[] splitHtmlContent(String htmlBody, String substringForHtmlBodySplit) throws FeedAggrException.SplitHTMLContent {
		return splitHtmlContent(null, htmlBody, substringForHtmlBodySplit);
	}
	private String[] splitHtmlContent(String url, String htmlBody, String substringForHtmlBodySplit) throws FeedAggrException.SplitHTMLContent {
		long t1 = new Date().getTime();
//		System.err.println("repeatable search: " + substringForHtmlBodySplit);
		log.debug("Splitting html content [is html content null: " + (htmlBody==null? "null":"not null")+"]");
		String ss = substringForHtmlBodySplit.replaceAll("\\{", "\\\\{");
		String splittedItems[]=htmlBody.split(ss);
		log.debug("splitted html content items.length="+splittedItems.length);
		log.debug("substringForHtmlBodySplit="+substringForHtmlBodySplit);
		if(splittedItems.length<2) {
			//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
			throw new FeedAggrException.SplitHTMLContent(url, htmlBody,substringForHtmlBodySplit);			
		}
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

		return splittedItems;
	}

	@Override
	public LinkedList<Item> call() throws Exception {
		return getItems();
	}

}
