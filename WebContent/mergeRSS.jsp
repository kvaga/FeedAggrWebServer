<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <%@ page
	import="ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSS,
	ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
	ru.kvaga.rss.feedaggrwebserver.ServerUtils,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap
	"
	%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Create composite RSS</title>
</head>
<body>
<h2>Your [<%= request.getSession().getAttribute("login")%>] RSS list:</h2>
<form action="mergeRSS">
<table>
<tr>Title of composite RSS: <input type="text" name="compositeRSSTitle"></input></tr>
<%
int k=0;
for(Feed feedOnServer : ServerUtils.getFeedsList(ConfigMap.feedsPath)) {
//	System.out.println(feedOnServer.getXmlFile());
if(feedOnServer.getId().startsWith("composite")) continue;
	out.println("<tr><br>");	 
	RSS rssFeed = (RSS)ObjectsUtils.getXMLObjectFromXMLFile(feedOnServer.getXmlFile(), new RSS());
	out.println("<td><a href=\"showFeed?feedId="+feedOnServer.getId() +"\">"+rssFeed.getChannel().getTitle()+"</a>");
	out.println("<br>");	 
	out.println("Source URL: "+rssFeed.getChannel().getLink());
	out.println("<br>");	 
	out.println("Last updated: " + rssFeed.getChannel().getLastBuildDate());
	out.println("<td><td><input type=\"checkbox\" id=\"vehicle1\" name=\"id_"+(k)+"\" value=\""+feedOnServer.getId()+"\"></td></tr>");	 
k++;
}

%>
<tr><td></td><td><input type="submit" name="Create"></input></td></tr>
</table>
</form>
</body>
</html>