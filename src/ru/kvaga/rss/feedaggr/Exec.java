package ru.kvaga.rss.feedaggr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ru.kvaga.monitoring.influxdb.InfluxDB2;
import ru.kvaga.rss.feedaggr.FeedAggrException.CommonException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggr.FeedAggrException.SplitHTMLContent;
import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.monitoring.*;
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
	
	public static String getHumanReadableHoursMinutesSecondsFromMilliseconds(long millis) {
		return String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
	public static synchronized String getHTMLSuccessText(String text) {
		return "<font color=\"green\">"+text+"</font>";
	}
	public static synchronized String getHTMLFailText(String text) {
		return "<font color=\"red\">"+text+"</font>";
	}
	public static synchronized String getHTMLFailText(Exception e) {
		return "<font color=\"red\">Exception: "+e.getMessage()+", Cause: "+e.getCause()+"</font>";
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
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
//			System.err.println("Filed: " + matcher.group("title").replaceAll("<!\\[CDATA\\[", "").replaceAll(" Channel\\]\\]>", ""));
			return matcher.group("title").replaceAll("<!\\[CDATA\\[", "").replaceAll(" Channel]]>", "");
		}else {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

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
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

			throw new FeedAggrException.GetSubstringForHtmlBodySplitException(repeatableSearchPattern);
		}
//		System.err.println(indexOfAsterisk);
//		System.err.println(indexOfPercent);
//		System.err.println(index);
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

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
	public static String encodeURLString(String urlString) throws MalformedURLException, URISyntaxException {
		URL url= new URL(urlString);
		URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		return uri.toASCIIString();
	}
	private static HashMap<String, Long> getURLContentDomainLocks = new HashMap<String, Long>();
	@Deprecated
	public static synchronized String getURLContent(String urlText) throws FeedAggrException.GetURLContentException, NoSuchAlgorithmException, KeyManagementException, UnsupportedEncodingException, MalformedURLException, URISyntaxException {
		long t1 = new Date().getTime();
		urlText=encodeURLString(urlText);
				//URLEncoder.encode(urlText, StandardCharsets.UTF_8.toString());
		String domain = Exec.getDomainFromURL(urlText);

		String body = null;
		String charset; // You should determine it based on response header.
		
//		/* Start of Fix SSL Checks */
//        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
//            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
//            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
//
//        } };
//
//        SSLContext sc = SSLContext.getInstance("SSL");
//        sc.init(null, trustAllCerts, new java.security.SecureRandom());
//        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//        // Create all-trusting host name verifier
//        HostnameVerifier allHostsValid = new HostnameVerifier() {
//            public boolean verify(String hostname, SSLSession session) { return true; }
//        };
//        // Install the all-trusting host verifier
//        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//        /* End of the fix*/
        
//		HttpsURLConnection con=null;
        HttpURLConnection con=null;
        
		try {
			
			// Check if lock was expired
			if(getURLContentDomainLocks.containsKey(domain) && getURLContentDomainLocks.get(domain) + ConfigMap.WAIT_TIME_AFTER_GET_CONTENT_URL_EXCEPTION_IN_MILLIS < new Date().getTime()) {
				getURLContentDomainLocks.remove(domain);
				MonitoringUtils.sendCommonMetric("GetURLContent.Locks", 0, new Tag("domain", domain));
				log.debug("The getURLContentDomainLocks for the domain ["+domain+"] was removed");
			}
			
			// Check if lock exists
			if(getURLContentDomainLocks.containsKey(domain)) {
				MonitoringUtils.sendCommonMetric("GetURLContent.Locks", 1, new Tag("domain", domain));
				throw new Exception("There is lock for the domain ["+domain+"] during ["+(new Date().getTime()-getURLContentDomainLocks.get(domain))+"] milliseconds, hh:mm:ss ["+Exec.getHumanReadableHoursMinutesSecondsFromMilliseconds(new Date().getTime()-getURLContentDomainLocks.get(domain))+"]. We just have to wait for ["+(getURLContentDomainLocks.get(domain) + ConfigMap.WAIT_TIME_AFTER_GET_CONTENT_URL_EXCEPTION_IN_MILLIS - new Date().getTime())+"] milliseconds, hh:mm:ss ["+Exec.getHumanReadableHoursMinutesSecondsFromMilliseconds(getURLContentDomainLocks.get(domain) + ConfigMap.WAIT_TIME_AFTER_GET_CONTENT_URL_EXCEPTION_IN_MILLIS - new Date().getTime())+"]");
			}
			
			URL url = new URL(urlText);
//			con = (HttpsURLConnection) url.openConnection();
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

			charset = "UTF-8";
			
			/*
			if (con.getContentType().toLowerCase().contains("charset=utf-8")) {
				charset = "UTF-8";
			} else if(con.getContentType().toLowerCase().contains("application/json")) {
				charset = "UTF-8";
			} else if(con.getContentType().toLowerCase().contains("application/rss+xml")) {
				charset = "UTF-8";
			}else if(con.getContentType().toLowerCase().contains("text/html")) {
				charset = "UTF-8";
			} else {
				MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
				throw new FeedAggrException.GetURLContentException(urlText,
						String.format("Received unsupported contentType: %s. ", con.getContentType()));
			}
			*/
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
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

			return body;
			
		} catch (Exception e) {
			log.error("GetURLContentException: couldn't get a content for the ["+urlText+"] URL]. ", e);
			if(e.getMessage().contains("Server returned HTTP response code: 403")) {
				MonitoringUtils.sendCommonMetric("GetURLContent.HTTP_403", 1, new Tag("domain", domain));
				if(!getURLContentDomainLocks.containsKey(domain)) getURLContentDomainLocks.put(domain, new Date().getTime());
			}
			if(con!=null) {
				con.disconnect();
			}
			MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

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
									String filterWords,
									String skipWords
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
		LinkedList<Item> itemsFromHtmlBody = ServerUtilsConcurrent.getInstance().getItems(responseHtmlBody, substringForHtmlBodySplit, repeatableSearchPattern,countOfPercentItemsInSearchPattern, filterWords, skipWords);					

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
	        MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

	return rss;
	}
	
	public static synchronized float getFileSize(String filePathStr) throws IOException {
		Path path = Paths.get(filePathStr);
		return Files.size(path)/1024/1024;
	}
	
	public static synchronized float getFileSizeByFeedId(String feedId) throws IOException {
		Path path = Paths.get(ConfigMap.feedsPath+File.separator+feedId+".xml");
		return Files.size(path)/1024/1024;
	}
	private static Pattern getNumberFromItemLinkPattern = Pattern.compile(".*\\{%(\\d+)}.*");
	public static synchronized int getNumberFromItemLink(String itemLink) throws Exception {
		long t1 = new Date().getTime();
		Matcher m = getNumberFromItemLinkPattern.matcher(itemLink);
		if(m.matches()) {
			log.debug("Found number ["+m.group(1)+"] in the item link ["+itemLink+"]");
			//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

			return Integer.parseInt(m.group(1));
		}
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
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
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		throw new FeedAggrException.CommonException("checkItemURLForFullness: Can't find left path in the URL ["+feedURL+"] by the regex pattern ["+leftPathPatternText+"]");
	}
	finalURL=leftPathOfFeedURL+itemURL;
	log.debug("Now Item URL ["+itemURL+"] converted to ["+leftPathOfFeedURL+itemURL+"]");
	log.debug("checkItemURLForFullness feedURL=["+feedURL+"] itemURL=["+itemURL+"] t=["+(new Date().getTime()-t1)+"]");
	//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

	return leftPathOfFeedURL+itemURL;
}

private static Pattern getChannelIdFromXMLURLPattern = Pattern.compile("https://www.youtube.com/feeds/videos.xml[?]channel_id=(?<channelId>.*)");
public synchronized static String getChannelIdFromXMLURL(String url) throws Exception {
	log.debug("getChannelIdFromXMLURL: processing url {}", url);
	Matcher m = getChannelIdFromXMLURLPattern.matcher(url);
	if(m.find()) {
		log.debug("getChannelIdFromXMLURL: found channel id {} for the url {}", m.group("channelId"), url);
		return m.group("channelId");
	}
	log.error("getChannelIdFromXMLURL: couldn't find channel id for the url {}", url);
	throw new Exception("Didn't find any channel id for url ["+url+"]");
}

private static Pattern youtubeUrlChannelVideosPattern=Pattern.compile("https://www.youtube.com/channel/(?<channleId>.*)/videos");
private static Pattern yutubeUrlContentExternalId = Pattern.compile("\"externalId\":\"(.*?)\",");
public static synchronized String getYoutubeChannelId(String youtubeVideosUrl) throws Exception {
	long t1 = new Date().getTime();
	log.debug("getYoutubeChannelId: processing url {}", youtubeVideosUrl);
	Matcher m1 = youtubeUrlChannelVideosPattern.matcher(youtubeVideosUrl);
	if(m1.find()) {
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		log.debug("getYoutubeChannelId: found channel {}", m1.group("channleId"));
		return m1.group("channleId");
	}
	if(youtubeVideosUrl.contains("feeds/videos.xml")) {
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return Exec.getChannelIdFromXMLURL(youtubeVideosUrl);
	}
//	String regex="\"externalId\":\"(([A-Z]*[0-9]*[a-z]*)*)\",";
//	String regex = "https://www.youtube.com/channel/(.*)/videos";
//	String urlContent=Exec.getURLContent(youtubeVideosUrl);
	log.debug("getYoutubeChannelId: try to get yutubeUrlContentExternalId for the url {}", youtubeVideosUrl);
	Matcher m = yutubeUrlContentExternalId.matcher(Exec.getURLContent(youtubeVideosUrl));
	if(m.find()) {
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		log.debug("getYoutubeChannelId: found yutubeUrlContentExternalId {}", m.group(1));
		return m.group(1);
	}
	log.error("getYoutubeChannelId: coundn't find youtubeChannelId and yutubeUrlContentExternalId for the url {}", youtubeVideosUrl);
	//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
	return null;
}

public static synchronized String getYoutubeFeedURL(String url) throws Exception {
	long t1 = new Date().getTime();
	log.debug("getYoutubeFeedURL: Got url {}", url);	
	String youtubeChannelPattern="https://www.youtube.com/feeds/videos.xml?channel_id=%s";
	String channelId = getYoutubeChannelId(url);
	if(channelId!=null) {
		log.debug("getYoutubeFeedURL: Determined youtube's url {} for the url {}", String.format(youtubeChannelPattern, channelId), url);	
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return String.format(youtubeChannelPattern, channelId);
	}
	//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
	log.error("getYoutubeFeedURL: Couldn't find youtube's feed url for the url {}", url);	
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
public static synchronized HashSet<String> getYoutubeListOfPlaylistsURLs(String mainPlaylistURL) throws GetURLContentException, KeyManagementException, NoSuchAlgorithmException, UnsupportedEncodingException, MalformedURLException, URISyntaxException{
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
	MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime()-t1);
	return l;
}

private static Pattern getDomainFromURLPattern = 
//	Pattern.compile("(?<url>[a-zA-Z0-9][a-zA-Z0-9-]{1,161}[a-zA-Z0-9]\\.[a-zA-Z]{2,})");

Pattern.compile("http(s)?:\\/\\/(?<url>.*("
		+ "\\.com"
		+ "|\\.ru"
		+ "|\\.org"
		+ "|\\.net"
		+ "|\\.io"
		+ "|\\.co"
		+ "|\\.ai"
		+ "|\\.co\\.uk"
		+ "|\\.ca"
		+ "|\\.dev"
		+ "|\\.me"
		+ "|\\.eu"
		+ "|\\.info"
		+ "|\\.today"
		+ "|\\.ok"
		+ "|\\.gov"
		+ "|\\.app"


		+ "))(\\/)?");
		 
public synchronized static String getDomainFromURL(String url){
	long t1 = new Date().getTime();
	Matcher m = getDomainFromURLPattern.matcher(url);
	if(m.find()) {
		//log.debug("Found domain ["+m.group("url")+"] in the url ["+url+"]");
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return m.group("url");
	}
	log.error("Didn't find domain in the url ["+url+"]");
	//MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
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
//		System.out.println("url: " + url);
//		System.out.println("lang: " + m.group("lang"));
//		System.out.println("other: " + m.group("other"));

		habrUrl = String.format(prefix, m.group("lang")==null?"":m.group("lang")) + m.group("other");
		//System.out.println("finalUrl: " + habrUrl);
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object(){}, new Date().getTime() - t1);
		return habrUrl;
	}else {
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object(){}, new Date().getTime() - t1);
		throw new Exception("Can't convert url ["+url+"] to the habr rss pattern url");
	}
}

private static Pattern telegramPattern = Pattern.compile(
		//"^https?:\\/\\/habr(ahabr)?[.](com|ru)?(?<lang>en\\/|ru\\/)?(?<other>.*)"
		"^https?:\\/\\/t[.]me\\/(?<channel>.*)"
		);
public static synchronized String getTelegramURL(String url) throws Exception {
	long t1 = new Date().getTime();
	if(	url.startsWith("https://rsshub.app/telegram/channel/")) {
		return url;
	}
	String telegramURL;
	String prefix = "https://rsshub.app/telegram/channel/%s";
	Matcher m = telegramPattern.matcher(url);
	if(m.find()) {
//		System.out.println("url: " + url);
//		System.out.println("lang: " + m.group("lang"));
//		System.out.println("other: " + m.group("other"));
		if(m.group("channel")==null) {
			throw new Exception("Could't find channel nam from the url ["+url+"]");
		}
		telegramURL = String.format(prefix, m.group("channel"));
		//System.out.println("finalUrl: " + habrUrl);
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object(){}, new Date().getTime() - t1);
		return telegramURL;
	}else {
		//MonitoringUtils.sendResponseTime2InfluxDB(new Object(){}, new Date().getTime() - t1);
		throw new Exception("Can't convert url ["+url+"] to the telegram pattern url ["+telegramPattern+"]");
	}
}
}
