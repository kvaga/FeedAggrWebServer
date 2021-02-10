<%@page import="ru.kvaga.rss.feedaggrwebserver.ConfigMap"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <%@ page
	import="ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSS,ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,ru.kvaga.rss.feedaggrwebserver.ServerUtils"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Feeds List</title>
</head>
<body>
Your feeds are listed below. If you have other feeds, <a href="Feed.jsp?action=new">add</a> them to your account.
<br>
<%
//final static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger();

//String realPath=getServletContext().getRealPath("data/feeds/");

for(Feed feedOnServer : ServerUtils.getFeedsList(ConfigMap.feedsPath)) {
//	System.out.println(feedOnServer.getXmlFile());
	out.println("<br>");	 
	RSS rssFeed = (RSS)ObjectsUtils.getXMLObjectFromXMLFile(feedOnServer.getXmlFile(), new RSS());
	out.println("<a href=\"showFeed?feedId="+feedOnServer.getId() +"\">"+rssFeed.getChannel().getTitle()+"</a>&nbsp&nbsp&nbsp[<a href=\"deleteFeed?feedId="+feedOnServer.getId()+"\">Delete</a>]");
	out.println("<br>");	 
	out.println("Source URL: "+rssFeed.getChannel().getLink());
	out.println("<br>");	 
	out.println("Last updated: " + rssFeed.getChannel().getLastBuildDate());
	out.println("<br><br>");	 
//	ObjectsUtills.printXMLObject(rssFeed);
}
%>
</body>
</html>