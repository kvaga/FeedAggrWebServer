package ru.kvaga.rss.feedaggr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.rss.feedaggr.FeedAggrException.CommonException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggr.FeedAggrException.SplitHTMLContent;
import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtilsConcurrent;

public class Exec {

	final static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Exec.class);

/*
	public static synchronized LinkedList<Item> getItems(
			String responseHtmlBody, 
			String substringForHtmlBodySplit, 
			String repeatableSearchPattern,
			int countOfPercentItemsInSearchPattern
			) throws SplitHTMLContent{
		return getItems(responseHtmlBody, substringForHtmlBodySplit, repeatableSearchPattern, countOfPercentItemsInSearchPattern, null);
	}
	public static synchronized LinkedList<Item> getItems(
			String responseHtmlBody, 
			String substringForHtmlBodySplit, 
			String repeatableSearchPattern,
			int countOfPercentItemsInSearchPattern,
			String filterWords
			) throws FeedAggrException.SplitHTMLContent {
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
			if(filterWords!=null) {
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

				for(int j=1;j<=//countOfPercentItemsInSearchPattern 
							matcher.groupCount();j++) {
					log.debug("GetItems: matcher group["+j+"]: " + matcher.group(j));

					item.add(j,matcher.group(j).replaceAll("\\$", "(dollar sign)"));
//					System.err.println("{%"+j+"}: " +item.get(j));
				}
				log.debug("GetItems: Added item " + item.getContentForPrinting() + " to a list \n ");
				ll.add(item);
			} else {
				System.err.println("-1==> temp_text_pattern: " + temp_text_pattern);
				System.err.println("0==> repeatableSearch: "+repeatableSearchPattern);
				System.err.println("1==> " + s);
				System.err.println("2==> " + repeatableSearchPattern);

				log.warn("Couldn't find item in the piece ["+(s.length()>=repeatableSearchPattern.length()?s.substring(0,repeatableSearchPattern.length()-1):s)+"] of html content by regex expression ["+repeatableSearchPattern+"] and substringForHtmlBodySplit ["+substringForHtmlBodySplit+"]");
				
				
			}

		}
		InfluxDB.getInstance().send("response_time,method=Exec.getItems", new Date().getTime() - t1);

		return ll;
		
	}

*/	
	public static synchronized String getHTMLSuccessText(String text) {
		return "<font color=\"green\">"+text+"</font>";
	}
	public static synchronized String getHTMLFailText(String text) {
		return "<font color=\"red\">"+text+"</font>";
	}
	
	public static synchronized int countWordsUsingSplit(String input, String splitItem) { 
		if (input == null || input.isEmpty()) { 
			return 0; 
		} 
		String[] items = input.split(splitItem.replaceAll("\\{", "\\\\{")); 
		return items.length-1; 
	}


	private static Pattern getTitleFromHtmlBodyPattern = Pattern.compile("<title>(?<title>.*?)<\\/title>");
	public static synchronized String getTitleFromHtmlBody(String responseHtmlBody) {
		long t1 = new Date().getTime();
//		Pattern pattern = Pattern.compile(".*<html.*><head.*>.*<title.*>(?<title>.*)<\\/title>.*<\\/head>");
		if(responseHtmlBody==null) {
			return null;
		}
		responseHtmlBody = responseHtmlBody.replaceAll("\r\n", "").replaceAll("\n", "");
		Matcher matcher = getTitleFromHtmlBodyPattern.matcher(responseHtmlBody);
		if(matcher.find()) {
			InfluxDB.getInstance().send("response_time,method=Exec,getTitleFromHtmlBody", new Date().getTime() - t1);
			return matcher.group("title");
		}else {
			InfluxDB.getInstance().send("response_time,method=Exec.getTitleFromHtmlBody", new Date().getTime() - t1);

			return null;
		}
	}
		
	public static synchronized String getSubstringForHtmlBodySplit(String repeatableSearchPattern) throws FeedAggrException.GetSubstringForHtmlBodySplitException {
		long t1 = new Date().getTime();
		int index = -1;
		int indexOfAsterisk = repeatableSearchPattern.indexOf("{*}");
		int indexOfPercent = repeatableSearchPattern.indexOf("{%}");
		index = indexOfAsterisk==-1? indexOfPercent:Math.min(indexOfPercent, indexOfAsterisk);

		if (index == -1) {
			InfluxDB.getInstance().send("response_time,method=Exec.getSubstringForHtmlBodySplit", new Date().getTime() - t1);

			throw new FeedAggrException.GetSubstringForHtmlBodySplitException(repeatableSearchPattern);
		}
//		System.err.println(indexOfAsterisk);
//		System.err.println(indexOfPercent);
//		System.err.println(index);
		InfluxDB.getInstance().send("response_time,method=Exec.getSubstringForHtmlBodySplit", new Date().getTime() - t1);

		return repeatableSearchPattern.substring(0, index);
	}
/*
	static synchronized String[] splitHtmlContent(String htmlBody, String substringForHtmlBodySplit) throws FeedAggrException.SplitHTMLContent {
		long t1 = new Date().getTime();
//		System.err.println("repeatable search: " + substringForHtmlBodySplit);
		log.debug("Splitting html content [is html content null: " + (htmlBody==null? "null":"not null")+"]");
		String ss = substringForHtmlBodySplit.replaceAll("\\{", "\\\\{");
		String splittedItems[]=htmlBody.split(ss);
		log.debug("splitted html content items.length="+splittedItems.length);
		log.debug("substringForHtmlBodySplit="+substringForHtmlBodySplit);
		if(splittedItems.length<2) {
			InfluxDB.getInstance().send("response_time,method=Exec.getSubstringForHtmlBodySplit", new Date().getTime() - t1);
			throw new FeedAggrException.SplitHTMLContent(htmlBody,substringForHtmlBodySplit);			
		}
		InfluxDB.getInstance().send("response_time,method=Exec.getSubstringForHtmlBodySplit", new Date().getTime() - t1);

		return splittedItems;
	}
*/
	private static HashMap<String, Long> getURLContentDomainLocks = new HashMap<String, Long>();
	@Deprecated
	public static synchronized String getURLContent(String urlText) throws FeedAggrException.GetURLContentException {
		long t1 = new Date().getTime();
		String domain = Exec.getDomainFromURL(urlText);

		String body = null;
		String charset; // You should determine it based on response header.
		HttpURLConnection con=null;

		try {
			
			// Check if lock was expired
			if(getURLContentDomainLocks.containsKey(domain) && getURLContentDomainLocks.get(domain) + ConfigMap.WAIT_TIME_AFTER_GET_CONTENT_URL_EXCEPTION_IN_MILLIS < new Date().getTime()) {
				getURLContentDomainLocks.remove(domain);
				log.debug("The getURLContentDomainLocks for the domain ["+domain+"] was removed");
			}
			
			// Check if lock exists
			if(getURLContentDomainLocks.containsKey(domain)) {
				throw new Exception("There is lock for the domain ["+domain+"] during ["+(new Date().getTime()-getURLContentDomainLocks.get(domain))+"] milliseconds. We just have to wait for ["+(getURLContentDomainLocks.get(domain) + ConfigMap.WAIT_TIME_AFTER_GET_CONTENT_URL_EXCEPTION_IN_MILLIS - new Date().getTime())+"] milliseconds");
			}
			
			URL url = new URL(urlText);
			con = (HttpURLConnection) url.openConnection();
//			con.connect();
			

//			log.debug("Con: " + con.getResponseCode());
			con.setRequestMethod("GET");
			con.setRequestProperty("accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//			con.setRequestProperty("accept-encoding", "gzip, deflate, br");
			con.setRequestProperty("accept-encoding", "gzip");

			con.setRequestProperty("accept-language", "en-GB,en;q=0.9,ru-RU;q=0.8,ru;q=0.7,en-US;q=0.6");
			con.setRequestProperty("cache-control", "max-age=0");
			con.setRequestProperty("sec-ch-ua",
					"\"Google Chrome\";v=\"87\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"87\"");
			con.setRequestProperty("sec-ch-ua-mobile", "?0");
			con.setRequestProperty("sec-fetch-dest", "document");
			con.setRequestProperty("sec-fetch-mode", "navigate");
			con.setRequestProperty("sec-fetch-site", "none");
			con.setRequestProperty("sec-fetch-user", "?1");
			con.setRequestProperty("upgrade-insecure-requests", "1");
			con.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");

			log.debug("url ["+urlText+"], connection response code [" + con.getResponseCode()+"], contentType  ["+ con.getContentType()+"]");

			
			if (con.getContentType().toLowerCase().contains("charset=utf-8")) {
				charset = "UTF-8";
			} else if(con.getContentType().toLowerCase().contains("application/json")) {
				charset = "UTF-8";
			} else if(con.getContentType().toLowerCase().contains("application/rss+xml")) {
				charset = "UTF-8";
			}else if(con.getContentType().toLowerCase().contains("text/html")) {
				charset = "UTF-8";
			} else {
				InfluxDB.getInstance().send("response_time,method=Exec.getURLContent", new Date().getTime() - t1);
				throw new FeedAggrException.GetURLContentException(urlText,
						String.format("Received unsupported contentType: %s. ", con.getContentType()));
			}
			String encoding=con.getContentEncoding();
			if (encoding!=null && encoding.equals("gzip")) {
				try (InputStream gzippedResponse = con.getInputStream();
						InputStream ungzippedResponse = new GZIPInputStream(gzippedResponse);
						Reader reader = new InputStreamReader(ungzippedResponse, charset);
						Writer writer = new StringWriter();) {
					char[] buffer = new char[10240];
					for (int length = 0; (length = reader.read(buffer)) > 0;) {
						writer.write(buffer, 0, length);
					}
					body = writer.toString();
					writer.close();
//				    System.err.println(body);
				}

			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String s;
//				System.err.println("Response Message: " + con.getContentEncoding());
				StringBuilder sb = new StringBuilder();
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				body = sb.toString();
				br.close();
			}
			con.disconnect();
			InfluxDB.getInstance().send("response_time,method=Exec.getURLContent", new Date().getTime() - t1);

			return body;
			
		} catch (Exception e) {
			log.error("GetURLContentException: couldn't get a content for the ["+urlText+"] URL]. ", e);
			if(e.getMessage().contains("Server returned HTTP response code: 403")) {
				if(!getURLContentDomainLocks.containsKey(domain)) getURLContentDomainLocks.put(domain, new Date().getTime());
			}
			if(con!=null) {
				con.disconnect();
			}
			InfluxDB.getInstance().send("response_time,method=Exec.getURLContent", new Date().getTime() - t1);

			throw new FeedAggrException.GetURLContentException(e.getMessage(),urlText);
		}
	}

	public static synchronized RSS getRSSFromWeb(String url, 
									String responseHtmlBody, 
									String substringForHtmlBodySplit, 
									String repeatableSearchPattern,
									String itemTitleTemplate,
									String itemLinkTemplate,
									String itemContentTemplate,
									String filterWords
	) throws Exception {
		long t1 = new Date().getTime();
		log.debug("===== getRSSFromWeb ===== ");
		int countOfPercentItemsInSearchPattern = Exec.countWordsUsingSplit(repeatableSearchPattern, "{%}");
		String feedTitle = Exec.getTitleFromHtmlBody(responseHtmlBody);
		RSS rss = new RSS();
        rss.setVersion("2.0");
        
        Channel channel = new Channel();
        channel.setTitle(feedTitle);
        channel.setLink(url);
        channel.setTtl(360);
        channel.setLastBuildDate(new Date());
        channel.setGenerator(ConfigMap.generator);
        channel.setDescription(feedTitle);
        ArrayList<ru.kvaga.rss.feedaggr.objects.Item> items = new ArrayList<ru.kvaga.rss.feedaggr.objects.Item>();

        // ������ ���������� �� html body ���������
        long t2 = new Date().getTime();
//		LinkedList<Item> itemsFromHtmlBody = Exec.getItems(responseHtmlBody, substringForHtmlBodySplit, repeatableSearchPattern,countOfPercentItemsInSearchPattern, filterWords);					
		LinkedList<Item> itemsFromHtmlBody = ServerUtilsConcurrent.getInstance().getItems(responseHtmlBody, substringForHtmlBodySplit, repeatableSearchPattern,countOfPercentItemsInSearchPattern, filterWords);					

		log.debug("getRSSFromWeb: Exec.getItems url=["+url+"] t=["+ (new Date().getTime()-t1)+"]");
		String itemTitle=null;
		String itemLink=null;
		String itemContent=null;
			int k = 0;
			for (Item itemFromHtmlBody : itemsFromHtmlBody) {
		        ru.kvaga.rss.feedaggr.objects.Item _item = new ru.kvaga.rss.feedaggr.objects.Item();
//				itemTitle=itemTitleTemplate;
//				itemLink=itemLinkTemplate;
//				itemContent=itemContentTemplate;

				int itemLinkNumber = Exec.getNumberFromItemLink(itemLinkTemplate);
				itemLink=itemLinkTemplate.replaceAll("\\{%"+itemLinkNumber+"}", itemFromHtmlBody.get(itemLinkNumber));
				itemLink=Exec.checkItemURLForFullness(url, itemLink);
				itemTitle=itemTitleTemplate;
				itemContent=itemContentTemplate.replaceAll("\\{%"+itemLinkNumber+"}", itemLink);											
				
				//log.debug("title before: " + itemTitle + ", itemTitleTemplate: " + itemTitleTemplate);
				//log.debug("content before: " + itemContent + ", itemContentTemplate: " + itemContentTemplate);

				log.debug(itemFromHtmlBody.getContentForPrinting());
				//���� ��� ������ ���� {%�} �� ��������
					for (int i = 1; i <= itemFromHtmlBody.length(); i++) {
						//log.debug("in cycle: itemFromHtmlBody.get("+i+")="+itemFromHtmlBody.get(i));
						itemTitle=itemTitle.replaceAll("\\{%"+i+"}", itemFromHtmlBody.get(i));
//						log.debug("title cont: " + itemTitle + ", itemTitleTemplate: " + itemTitleTemplate);
						itemContent=itemContent.replaceAll("\\{%"+i+"}", itemFromHtmlBody.get(i));
					}
					//log.debug("title after: " + itemTitle + ", itemTitleTemplate: " + itemTitleTemplate);
					//log.debug("content after: " + itemContent + ", itemContentTemplate: " + itemContentTemplate);

					_item.setTitle(itemTitle);
			        _item.setLink(Exec.checkItemURLForFullness(channel.getLink(), itemLink));
//			        _item.setDescription("<![CDATA[\""+itemContent+"\"]]>");
			        _item.setDescription(itemContent);
			        _item.setPubDate(new Date());
			        _item.setGuid(new GUID("false", itemLink));
					 items.add(_item);
			}
			channel.setItem(items);
	        rss.setChannel(channel);
	        InfluxDB.getInstance().send("response_time,method=Exec.getURLContent", new Date().getTime() - t1);

	return rss;
	}
	
	private static Pattern getNumberFromItemLinkPattern = Pattern.compile(".*\\{%(\\d+)}.*");
	public static synchronized int getNumberFromItemLink(String itemLink) throws Exception {
		long t1 = new Date().getTime();
		Matcher m = getNumberFromItemLinkPattern.matcher(itemLink);
		if(m.matches()) {
			log.debug("Found number ["+m.group(1)+"] in the item link ["+itemLink+"]");
			InfluxDB.getInstance().send("response_time,method=Exec.getNumberFromItemLink", new Date().getTime() - t1);

			return Integer.parseInt(m.group(1));
		}
		InfluxDB.getInstance().send("response_time,method=Exec.getNumberFromItemLink", new Date().getTime() - t1);
		throw new Exception("Can't find number in the item link ["+itemLink+"]");
	}

private static Pattern checkItemURLForFullnessPattern = Pattern.compile("http[s]{0,1}:\\/\\/.*?\\/");
public static synchronized String checkItemURLForFullness(String feedURL, String itemURL) throws CommonException {
	long t1 = new Date().getTime();
	String leftPathPatternText="http[s]{0,1}:\\/\\/.*?\\/";
	String leftPathOfFeedURL=null;
	String finalURL=null;
	
	if(itemURL.startsWith("http:") || itemURL.startsWith("https:")) {
//		log.debug("Item URL ["+itemURL+"] starts from any 'http:' or 'https:'. Nothing to do");
		return itemURL;
	}
	log.debug("Item URL ["+itemURL+"] doesn't contain http or https prefix and we must to add prefix (left path of URL) using feed URL ["+feedURL+"]");
	
	if(itemURL.startsWith("/")) {
		itemURL=itemURL.replaceFirst("/", "");
	}
	
	// getting url left path
	Matcher matcher = checkItemURLForFullnessPattern.matcher(feedURL);
	if(matcher.find()) {
		leftPathOfFeedURL = matcher.group();
	}else {
		InfluxDB.getInstance().send("response_time,method=Exec.checkItemURLForFullness", new Date().getTime() - t1);
		throw new FeedAggrException.CommonException("checkItemURLForFullness: Can't find left path in the URL ["+feedURL+"] by the regex pattern ["+leftPathPatternText+"]");
	}
	finalURL=leftPathOfFeedURL+itemURL;
	log.debug("Now Item URL ["+itemURL+"] converted to ["+leftPathOfFeedURL+itemURL+"]");
	log.debug("checkItemURLForFullness feedURL=["+feedURL+"] itemURL=["+itemURL+"] t=["+(new Date().getTime()-t1)+"]");
	InfluxDB.getInstance().send("response_time,method=Exec.checkItemURLForFullness", new Date().getTime() - t1);

	return leftPathOfFeedURL+itemURL;
}

private static Pattern getChannelIdFromXMLURLPattern = Pattern.compile("https://www.youtube.com/feeds/videos.xml[?]channel_id=(?<channelId>.*)");
public synchronized static String getChannelIdFromXMLURL(String url) throws Exception {
	Matcher m = getChannelIdFromXMLURLPattern.matcher(url);
	if(m.find()) {
		return m.group("channelId");
	}
	throw new Exception("Didnt' find any channel id for url ["+url+"]");
}

private static Pattern youtubeUrlChannelVideosPattern=Pattern.compile("https://www.youtube.com/channel/(?<channleId>.*)/videos");
private static Pattern yutubeUrlContentExternalId = Pattern.compile("\"externalId\":\"(.*?)\",");
public static synchronized String getYoutubeChannelId(String youtubeVideosUrl) throws Exception {
	long t1 = new Date().getTime();
	Matcher m1 = youtubeUrlChannelVideosPattern.matcher(youtubeVideosUrl);
	if(m1.find()) {
		InfluxDB.getInstance().send("response_time,method=Exec.getYoutubeChannelId.part1", new Date().getTime() - t1);
		return m1.group("channleId");
	}
	if(youtubeVideosUrl.contains("feeds/videos.xml")) {
		InfluxDB.getInstance().send("response_time,method=Exec.getYoutubeChannelId.part2", new Date().getTime() - t1);
		return Exec.getChannelIdFromXMLURL(youtubeVideosUrl);
	}
//	String regex="\"externalId\":\"(([A-Z]*[0-9]*[a-z]*)*)\",";
//	String regex = "https://www.youtube.com/channel/(.*)/videos";
//	String urlContent=Exec.getURLContent(youtubeVideosUrl);
	Matcher m = yutubeUrlContentExternalId.matcher(Exec.getURLContent(youtubeVideosUrl));
	if(m.find()) {
		InfluxDB.getInstance().send("response_time,method=Exec.getYoutubeChannelId.part3", new Date().getTime() - t1);
		return m.group(1);
	}
	InfluxDB.getInstance().send("response_time,method=Exec.getYoutubeChannelId", new Date().getTime() - t1);
	return null;
}

public static synchronized String getYoutubeFeedURL(String url) throws Exception {
	long t1 = new Date().getTime();
	String youtubeChannelPattern="https://www.youtube.com/feeds/videos.xml?channel_id=%s";
	String channelId = getYoutubeChannelId(url);
	if(channelId!=null) {
		InfluxDB.getInstance().send("response_time,method=Exec.getYoutubeFeedURL.part1", new Date().getTime() - t1);
		return String.format(youtubeChannelPattern, channelId);
	}
	InfluxDB.getInstance().send("response_time,method=Exec.getYoutubeFeedURL", new Date().getTime() - t1);
	return null;
}

public static synchronized String getYoutubeMainPlaylistURL(String channelId) throws GetURLContentException {
	String playlistsUrlPattern="https://www.youtube.com/channel/%s/playlists";
	if(channelId!=null) {
		return String.format(playlistsUrlPattern, channelId);
	}
	return null;
}

private static Pattern getYoutubeListOfPlaylistsURLsPattern = Pattern.compile("\\/playlist[?]list=(.*?)\",\"webPageTyp");
public static synchronized HashSet<String> getYoutubeListOfPlaylistsURLs(String mainPlaylistURL) throws GetURLContentException{
	long t1=new Date().getTime();
	String urlPlaylistFeedPattern="https://www.youtube.com/feeds/videos.xml?playlist_id=%s";
//	ArrayList<String> l = new ArrayList<String>();
	HashSet<String> l = new HashSet<String>();
	Matcher m = getYoutubeListOfPlaylistsURLsPattern.matcher(Exec.getURLContent(mainPlaylistURL));
	while(m.find()) {
		for(int i = 1; i<=m.groupCount(); i++) {
			l.add(String.format(urlPlaylistFeedPattern, m.group(i)));
		}
	}
	InfluxDB.getInstance().send("response_time,method=getYoutubeListOfPlaylistsURLs", new Date().getTime()-t1);
	return l;
}

private static Pattern getDomainFromURLPattern = Pattern.compile("http(s)?:\\/\\/(?<url>.*(\\.com|\\.ru|\\.org))(\\/)?");
public synchronized static String getDomainFromURL(String url){
	long t1 = new Date().getTime();
	Matcher m = getDomainFromURLPattern.matcher(url);
	if(m.find()) {
		log.debug("Found domain ["+m.group("url")+"] in the url ["+url+"]");
		InfluxDB.getInstance().send("response_time,method=Exec.getDomainFromURL", new Date().getTime() - t1);
		return m.group("url");
	}
	log.error("Didn't find domain in the url ["+url+"]");
	InfluxDB.getInstance().send("response_time,method=getDomainFromURL", new Date().getTime() - t1);
	return null;
}

public static void sleep(long timeInMillis) {
	try {
		Thread.sleep(timeInMillis);
	} catch (InterruptedException e) {
		log.error("Error during sleeping for " + timeInMillis + " millis");
	}
}

private static Pattern habrPattern = Pattern.compile(
		//"^https?:\\/\\/habr(ahabr)?[.](com|ru)?(?<lang>en\\/|ru\\/)?(?<other>.*)"
		"^https?:\\/\\/habr(ahabr)?[.](com|ru)\\/(?<lang>en\\/|ru\\/)?(?<other>.*)"
		);
public static synchronized String getHabrFeedURL(String url) throws Exception {
	long t1 = new Date().getTime();
	if(		url.startsWith("https://habr.com/en/rss/") ||
			url.startsWith("https://habr.com/ru/rss/") ||
			url.startsWith("https://habr.com/rss/") ||
			url.startsWith("https://habrahabr.com/en/rss/") ||
			url.startsWith("https://habrahabr.com/ru/rss/") ||
			url.startsWith("https://habrahabr.com/rss/") ||
			url.startsWith("https://habr.ru/en/rss/") ||
			url.startsWith("https://habr.ru/ru/rss/") ||
			url.startsWith("https://habr.ru/rss/") ||
			url.startsWith("https://habrahabr.ru/en/rss/") ||
			url.startsWith("https://habrahabr.ru/ru/rss/") ||
			url.startsWith("https://habrahabr.ru/rss/") 
			) {
		return url;
	}
	String habrUrl;
	String prefix = "https://habr.com/%srss/";
	Matcher m = habrPattern.matcher(url);
	if(m.find()) {
		System.out.println("url: " + url);
		System.out.println("lang: " + m.group("lang"));
		System.out.println("other: " + m.group("other"));

		habrUrl = String.format(prefix, m.group("lang")==null?"":m.group("lang")) + m.group("other");
		System.out.println("finalUrl: " + habrUrl);
		//InfluxDB.getInstance().send("response_time,method=Exec.getHabrFeedURL", new Date().getTime() - t1);
		return habrUrl;
	}else {
		//InfluxDB.getInstance().send("response_time,method=Exec.getHabrFeedURLError", new Date().getTime() - t1);
		throw new Exception("Can't convert url ["+url+"] to the habr rss pattern url");
	}
}
}
