<%@page import="ru.kvaga.rss.feedaggr.objects.RSSForPrintingComparatorByTitle"%>
<%@page import="ru.kvaga.rss.feedaggrwebserver.ConfigMap"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <%@ page
	import="ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSS,
	ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSSForPrintingComparatorByTitle,
	ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
	ru.kvaga.rss.feedaggrwebserver.ServerUtils,
	java.util.Collections,
	java.util.HashMap,
	java.util.ArrayList,
	org.apache.logging.log4j.*,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap
	"%>
	<%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());

	%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Feeds List</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>
<h3>Common feeds</h3>
<%

//String realPath=getServletContext().getRealPath("data/feeds/");
ArrayList<RSS> rssListForPrinting = new ArrayList<RSS>();

HashMap<RSS,String> mapRssStringForPrinting = new HashMap<RSS, String>();
log.debug("=======================> " + ConfigMap.feedsPath);
StringBuilder sb = new StringBuilder();
for(Feed feedOnServer : ServerUtils.getFeedsList(true,false)) {
	try{
	//	log.debug(feedOnServer.getXmlFile());
		//RSS rssFeed = (RSS)ObjectsUtils.getXMLObjectFromXMLFile(feedOnServer.getXmlFile(), new RSS());
		RSS rssFeed = RSS.getRSSObjectFromXMLFile(feedOnServer.getXmlFile());
		if(!feedOnServer.getId().startsWith("composite")) {
			rssListForPrinting.add(rssFeed);
		}
		mapRssStringForPrinting.put(rssFeed, feedOnServer.getId());
	}catch(Exception e){
		sb.append(feedOnServer.getId());
		sb.append(", ");
		log.error("Exception was occured on FeedsList.jsp page during building a list of feeds. The problem was detected on the feed on server ["+feedOnServer.getId()+"]", e);
	}
}
if(sb.length()!=0){
	throw new Exception("Exception was occured on FeedsList.jsp page during building a list of feeds. The problem was detected on the feed on server ["+sb.toString()+"]");
}
Collections.sort(rssListForPrinting, new RSSForPrintingComparatorByTitle());
for(RSS rss : rssListForPrinting){
	out.println("<br>");	 
	out.println("<a href=\"showFeed?feedId="+mapRssStringForPrinting.get(rss) +"\">"+rss.getChannel().getTitle()+"</a>&nbsp&nbsp&nbsp[<a href=\"deleteFeed?feedId="+mapRssStringForPrinting.get(rss)+"\">Delete</a>]&nbsp&nbsp&nbsp[<a href=\"Feed.jsp?action=edit&feedId="+mapRssStringForPrinting.get(rss)+"\">Edit</a>]");
	out.println("<br>");	 
	out.println("Source URL: "+rss.getChannel().getLink());
	out.println("<br>");	 
	out.println("Last updated: " + rss.getChannel().getLastBuildDate());
	out.println("<br><br>");	 
//	ObjectsUtills.printXMLObject(rssFeed);
}
%>

</body>
</html>