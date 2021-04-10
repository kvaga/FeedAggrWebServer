package ru.kvaga.rss.feedaggr;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.logging.log4j.*;

import ru.kvaga.rss.feedaggr.FeedAggrException;
import ru.kvaga.rss.feedaggr.FeedAggrException.CommonException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetSubstringForHtmlBodySplitException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggr.FeedAggrException.SplitHTMLContent;
import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

public class Exec {

	final static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Exec.class);

	public static void main(String[] args) throws FeedAggrException.GetURLContentException, FeedAggrException.GetSubstringForHtmlBodySplitException, FeedAggrException.SplitHTMLContent, FeedAggrException.CommonException, GetFeedsListByUser {
		String urlText[] = {
//			"https://hh.ru/search/vacancy/rss?area=1&clusters=true&enable_snippets=true&text=NAME%3A%28%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80+OR+%D0%BD%D0%B0%D1%87%D0%B0%D0%BB%D1%8C%D0%BD%D0%B8%D0%BA+OR+%D1%80%D1%83%D0%BA%D0%BE%D0%B2%D0%BE%D0%B4%D0%B8%D1%82%D0%B5%D0%BB%D1%8C%29+AND+COMPANY_NAME%3A%28%D0%98%D0%BD%D0%BD%D0%BE%D1%82%D0%B5%D1%85%29&specialization=1&from=cluster_professionalArea&showClusters=true",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCBzN3JKOWOPo6ic0_UtQXhA",
//				"https://4brain.ru/blog/",
//				"https://www.drive2.ru/experience/audi/g4859/?",
				"https://journal.open-broker.ru/",
				
		};
System.out.println(Charset.defaultCharset());
		try {
			for(String url : urlText) {
//				System.err.println(ServerUtils.encodeString(Exec.getURLContent(url), "UTF-8").replaceAll("<title>", "\n<title>"));
				System.err.println(Exec.getURLContent(url).replaceAll("<title>", "\n<title>"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(true) {
			System.exit(0);
		}
		String repeatableSearchPattern = "<div class=\"c-post-preview__title\">{*}<a class=\"c-link c-link--text\" href=\"{%}\"  rel=\"noopener\" target=\"_blank\" data-ym-target=\"post_title\">{%}</a>{*}<div class=\"c-post-preview__lead\">{%}</div>{*}<div class=\"c-post-preview__comments\">";

		String responseHtmlBody = Exec.getTitleFromHtmlBody(Exec.getURLContent(""));

		System.out.println(responseHtmlBody.length()>150 ? responseHtmlBody.substring(0,150) : responseHtmlBody);
		if(true) {
			System.exit(0);
		}
		String substringForHtmlBodySplit = getSubstringForHtmlBodySplit(repeatableSearchPattern);

		int countOfPercentItemsInSearchPattern=countWordsUsingSplit(repeatableSearchPattern, "{%}");
		if(countOfPercentItemsInSearchPattern<1) {
			throw new FeedAggrException.CommonException(String.format("The repeatable search pattern [%s] doesn't contain any{%}")) ;
		}

		LinkedList<Item> items = getItems(responseHtmlBody,substringForHtmlBodySplit,repeatableSearchPattern, countOfPercentItemsInSearchPattern);
		log.debug("OK. Found " + items.size() + " items");
		
		int k=0;
		for(Item item : items) {
			System.err.println("Item " + ++k);
			for(int i=1;i<=item.length();i++) {
				log.debug("{%"+i+"}: " + item.get(i));
			}
		}
		//		System.err.println(repeatableSearchPattern);
	}

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

				for(int j=1;j<=/*countOfPercentItemsInSearchPattern*/ matcher.groupCount();j++) {
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
		return ll;
		
	}

	
	public static synchronized int countWordsUsingSplit(String input, String splitItem) { 
		
		if (input == null || input.isEmpty()) { 
			return 0; 
		} 
		String[] items = input.split(splitItem.replaceAll("\\{", "\\\\{")); 
		return items.length-1; 
	}


	public static synchronized String getTitleFromHtmlBody(String responseHtmlBody) {
//		Pattern pattern = Pattern.compile(".*<html.*><head.*>.*<title.*>(?<title>.*)<\\/title>.*<\\/head>");
		Pattern pattern = Pattern.compile("<title>(?<title>.*?)<\\/title>");
		if(responseHtmlBody==null) {
			return null;
		}
		responseHtmlBody = responseHtmlBody.replaceAll("\r\n", "").replaceAll("\n", "");
		Matcher matcher = pattern.matcher(responseHtmlBody);
		if(matcher.find()) {
			return matcher.group("title");
		}else {
			return null;
		}
	}
		
	public static synchronized String getSubstringForHtmlBodySplit(String repeatableSearchPattern) throws FeedAggrException.GetSubstringForHtmlBodySplitException {
		int index = -1;
		int indexOfAsterisk = repeatableSearchPattern.indexOf("{*}");
		int indexOfPercent = repeatableSearchPattern.indexOf("{%}");
		index = indexOfAsterisk==-1? indexOfPercent:Math.min(indexOfPercent, indexOfAsterisk);

		if (index == -1) {
			throw new FeedAggrException.GetSubstringForHtmlBodySplitException(repeatableSearchPattern);
		}
//		System.err.println(indexOfAsterisk);
//		System.err.println(indexOfPercent);
//		System.err.println(index);
		return repeatableSearchPattern.substring(0, index);
	}

	static synchronized String[] splitHtmlContent(String htmlBody, String substringForHtmlBodySplit) throws FeedAggrException.SplitHTMLContent {
//		System.err.println("repeatable search: " + substringForHtmlBodySplit);
		log.debug("Splitting html content [is html content null: " + (htmlBody==null? "null":"not null")+"]");
		String ss = substringForHtmlBodySplit.replaceAll("\\{", "\\\\{");
		String splittedItems[]=htmlBody.split(ss);
		log.debug("splitted html content items.length="+splittedItems.length);
		log.debug("substringForHtmlBodySplit="+substringForHtmlBodySplit);
		if(splittedItems.length<2) {
			throw new FeedAggrException.SplitHTMLContent(htmlBody,substringForHtmlBodySplit);
		}
		return splittedItems;
	}

	public static synchronized String getURLContent(String urlText) throws FeedAggrException.GetURLContentException {
		String body = null;
		String charset; // You should determine it based on response header.
		HttpURLConnection con=null;

		try {
			URL url = new URL(urlText);
			con = (HttpURLConnection) url.openConnection();
//			con.connect();
			

//			System.out.println("Con: " + con.getResponseCode());
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

			log.debug("Connection response code: " + con.getResponseCode());
			log.debug("ContentType: " + con.getContentType());

			
			if (con.getContentType().toLowerCase().contains("charset=utf-8")) {
				charset = "UTF-8";
			} else if(con.getContentType().toLowerCase().contains("application/json")) {
				charset = "UTF-8";
			} else {
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
//					System.out.println(s);
					sb.append(s);
				}
				body = sb.toString();
				br.close();
			}
			con.disconnect();

			return body;
			
		} catch (Exception e) {
//			e.printStackTrace();
			log.error("GetURLContentException: couldn't get a content for the ["+urlText+"] URL", e);
			if(con!=null) {
				con.disconnect();
			}
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

        // список полученных из html body элементов
		LinkedList<Item> itemsFromHtmlBody = Exec.getItems(responseHtmlBody, substringForHtmlBodySplit, repeatableSearchPattern,countOfPercentItemsInSearchPattern, filterWords);					
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
				//цикл для замены всех {%Х} на значения
					for (int i = 1; i <= itemFromHtmlBody.length(); i++) {
						log.debug("in cycle: itemFromHtmlBody.get("+i+")="+itemFromHtmlBody.get(i));
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
			        _item.setGuid(new GUID("false", GUID.generateGUID(itemLink+itemTitle)));
					 items.add(_item);
			}
			channel.setItem(items);
	        rss.setChannel(channel);
	return rss;
	}
	
	public static synchronized int getNumberFromItemLink(String itemLink) throws Exception {
		Pattern pattern = Pattern.compile(".*\\{%(\\d+)}.*");
		Matcher m = pattern.matcher(itemLink);
		if(m.matches()) {
			log.debug("Found number ["+m.group(1)+"] in the item link ["+itemLink+"]");
			return Integer.parseInt(m.group(1));
		}
		throw new Exception("Can't find number in the item link ["+itemLink+"]");
	}

public static synchronized String checkItemURLForFullness(String feedURL, String itemURL) throws CommonException {
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
	Pattern pattern = Pattern.compile(leftPathPatternText);
	Matcher matcher = pattern.matcher(feedURL);
	if(matcher.find()) {
		leftPathOfFeedURL = matcher.group();
	}else {
		throw new FeedAggrException.CommonException("checkItemURLForFullness: Can't find left path in the URL ["+feedURL+"] by the regex pattern ["+leftPathPatternText+"]");
	}
	finalURL=leftPathOfFeedURL+itemURL;
	log.debug("Now Item URL ["+itemURL+"] converted to ["+leftPathOfFeedURL+itemURL+"]");
	return leftPathOfFeedURL+itemURL;
}


public static synchronized String getYoutubeFeedURL(String url) throws GetURLContentException {
//	String regex="\"externalId\":\"(([A-Z]*[0-9]*[a-z]*)*)\",";
	String regex="\"externalId\":\"(.*?)\",";
	String youtubeChannelPattern="https://www.youtube.com/feeds/videos.xml?channel_id=%s";
	
	Pattern p = Pattern.compile(regex);
	String urlContent=Exec.getURLContent(url);
	Matcher m = p.matcher(urlContent);
	if(m.find()) {
		return String.format(youtubeChannelPattern, m.group(1));
	}
	return null;
}

public synchronized static String getDomainFromURL(String url){
	Pattern p = Pattern.compile("http(s)?:\\/\\/(?<url>.*(\\.com|\\.ru|\\.org))(\\/)?");
	Matcher m = p.matcher(url);
	if(m.find()) {
		return m.group("url");
	}
	return null;
}

public static void sleep(long timeInMillis) {
	try {
		Thread.sleep(timeInMillis);
	} catch (InterruptedException e) {
		log.error("Error during sleeping for " + timeInMillis + " millis");
	}
}

}
