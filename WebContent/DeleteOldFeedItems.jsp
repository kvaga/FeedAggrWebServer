<!--  <?xml version="1.0" encoding="ISO-8859-1" ?>-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.kvaga.rss.feedaggr.objects.RSSForPrintingComparatorByTitle"%>
<%@ page
	import="ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSS,
	ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
	ru.kvaga.rss.feedaggrwebserver.ServerUtils,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap,
	java.util.Collections,
	java.util.HashMap,
	java.io.File,
	ru.kvaga.rss.feedaggrwebserver.objects.user.User,
	org.apache.logging.log4j.*,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap
	"
	
	%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Delete Old Feed Items</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>

 <%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());
  %>
<h2>Your [<%= request.getSession().getAttribute("login")%>] RSS list:</h2>
<form action="DeleteOldFeedItems">
<table>
<tr>Count of days after deletion since now <input type="text" name="countOfDaysForDeletion" value="90"></input><input type="submit" name="Delete Old"></input></tr>
<%
File userFile = null;
User user = null;
CompositeUserFeed compositeUserFeed=null;
/*
if(request.getParameter("feedId")!=null){
	out.println("<input type=\"hidden\" name=\"feedId\" value=\""+request.getParameter("feedId")+"\">");
	userFile=new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + request.getSession().getAttribute("login") + ".xml");
	//user=User.getXMLObjectFromXMLFile(userFile);
	compositeUserFeed=user.getCompositeUserFeedById(request.getParameter("feedId"));
}
*/

int k=0;
ArrayList<RSS> rssListForPrinting = new ArrayList<RSS>();
HashMap<RSS,String> mapRssStringForPrinting = new HashMap<RSS, String>();

// Get all feed list
for(Feed feedOnServer : ServerUtils.getFeedsList(true, true)) {
	//if(feedOnServer.getId().startsWith("composite")) continue;
	RSS rssFeed = RSS.getRSSObjectFromXMLFile(feedOnServer.getXmlFile());
	rssListForPrinting.add(rssFeed);
	mapRssStringForPrinting.put(rssFeed, feedOnServer.getId());
}

Collections.sort(rssListForPrinting, new RSSForPrintingComparatorByTitle());

// Print composite list only
out.println("<h3>Composite Feeds List</h3>");
for(RSS rss : rssListForPrinting){
	if(mapRssStringForPrinting.get(rss).startsWith("composite_")){
		out.println("<tr>");
		out.println("<td valign=\"top\"><input type=\"checkbox\" id=\"vehicle1\" name=\"id_"+(k)+"\" value=\""+mapRssStringForPrinting.get(rss)+"\"></td>");
		out.println("<td><a href=\"showFeed?feedId="+mapRssStringForPrinting.get(rss) +"\">"+rss.getChannel().getTitle()+"</a>");
		out.println("<br>");	 
		out.println("Source URL: "+rss.getChannel().getLink());
		out.println("<br>");	 
		out.println("Last updated: " + rss.getChannel().getLastBuildDate());
		out.println("</td></tr>");	 
		k++;
	}
}
out.println("</table>");

out.println("<h3>Feeds List</h3>");
out.println("<table>");
// Print common feeds list
for(RSS rss : rssListForPrinting){
	if(!mapRssStringForPrinting.get(rss).startsWith("composite_")){
		out.println("<tr>");
		out.println("<td valign=\"top\"><input type=\"checkbox\" id=\"vehicle1\" name=\"id_"+(k)+"\" value=\""+mapRssStringForPrinting.get(rss)+"\"></td>");
		out.println("<td><a href=\"showFeed?feedId="+mapRssStringForPrinting.get(rss) +"\">"+rss.getChannel().getTitle()+"</a>");
		out.println("<br>");	 
		out.println("Source URL: "+rss.getChannel().getLink());
		out.println("<br>");	 
		out.println("Last updated: " + rss.getChannel().getLastBuildDate());
		out.println("</td></tr>");	 
		k++;
	}
}




%>
<tr><td></td><td><input type="submit" name="Delete Old"></input><input type="hidden" name="redirectTo" value="DeleteOldFeedItems.jsp"></input></td></tr>
</table>
</form>
</body>
</html>