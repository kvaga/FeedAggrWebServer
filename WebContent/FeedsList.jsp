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
Your feeds are listed below. If you have other feeds, <a href="Feed.jsp?action=new">add</a> them to your account. Or <a href="mergeRSS.jsp">create Composite</a> rss feed
<br>
<h3>Common feeds</h3>
<%
ArrayList<RSS> nullVal=null;
if(true){
	nullVal.add(new RSS());
}
//String realPath=getServletContext().getRealPath("data/feeds/");
ArrayList<RSS> rssListForPrinting = new ArrayList<RSS>();
ArrayList<RSS> rssCompositeListForPrinting = new ArrayList<RSS>();

HashMap<RSS,String> mapRssStringForPrinting = new HashMap<RSS, String>();
log.debug("=======================> " + ConfigMap.feedsPath);
for(Feed feedOnServer : ServerUtils.getFeedsList(ConfigMap.feedsPath)) {
//	log.debug(feedOnServer.getXmlFile());
	RSS rssFeed = (RSS)ObjectsUtils.getXMLObjectFromXMLFile(feedOnServer.getXmlFile(), new RSS());
	if(feedOnServer.getId().startsWith("composite")) {
		rssCompositeListForPrinting.add(rssFeed);
	}else{
		rssListForPrinting.add(rssFeed);
	}
	mapRssStringForPrinting.put(rssFeed, feedOnServer.getId());
}
Collections.sort(rssListForPrinting, new RSSForPrintingComparatorByTitle());
Collections.sort(rssCompositeListForPrinting, new RSSForPrintingComparatorByTitle());
for(RSS rss : rssListForPrinting){
	out.println("<br>");	 
	out.println("<a href=\"showFeed?feedId="+mapRssStringForPrinting.get(rss) +"\">"+rss.getChannel().getTitle()+"</a>&nbsp&nbsp&nbsp[<a href=\"deleteFeed?feedId="+mapRssStringForPrinting.get(rss)+"\">Delete</a>]");
	out.println("<br>");	 
	out.println("Source URL: "+rss.getChannel().getLink());
	out.println("<br>");	 
	out.println("Last updated: " + rss.getChannel().getLastBuildDate());
	out.println("<br><br>");	 
//	ObjectsUtills.printXMLObject(rssFeed);
}
%>
<br>
<h3>Composite feeds</h3>
<%

for(RSS rss : rssCompositeListForPrinting) {
	out.println("<br>");	 
	out.println("<a href=\"showFeed?feedId="+mapRssStringForPrinting.get(rss) +"\">"+rss.getChannel().getTitle()+"</a>&nbsp&nbsp&nbsp[<a href=\"deleteFeed?feedId="+mapRssStringForPrinting.get(rss)+"\">Delete</a>]");
	out.println("&nbsp&nbsp&nbsp[<a href=\"mergeRSS.jsp?feedId="+mapRssStringForPrinting.get(rss)+"&feedTitle="+rss.getChannel().getTitle()+"\">Edit</a>]");
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