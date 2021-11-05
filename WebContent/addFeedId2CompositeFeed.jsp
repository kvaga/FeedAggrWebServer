<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page import="ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.kvaga.rss.feedaggr.objects.RSSForPrintingComparatorByTitle"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <%@ page
	import="ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSS,
	ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
	ru.kvaga.rss.feedaggrwebserver.ServerUtils,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap,
	java.util.Collections,
	java.util.HashMap,
		java.util.Set,
	java.io.File,
	ru.kvaga.rss.feedaggrwebserver.objects.user.User,
	org.apache.logging.log4j.*,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap
	"
	
	%>
    <%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Add feed id to one of composite feeds</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>

<%
User user = User.getXMLObjectFromXMLFileByUserName((String) request.getSession().getAttribute("login"));
Set<CompositeUserFeed> cufSet = user.getCompositeUserFeeds();



%>
<form action="mergeRSS">

<%
if(request.getParameterValues("feedId")!=null && request.getParameterValues("feedId").length>0){
	out.write("The list of feed ids for merging: ");
	for(String feedId: request.getParameterValues("feedId")){
		out.write(feedId+" ");
		out.write("<input type=\"hidden\" name=\"feedId\" value=\""+feedId+"\">");
	}
}else{
%>
<br/>
Single Feed id: <input type="text" name="feedId" value="<%= request.getParameter("feedId")!=null? request.getParameter("feedId"):"" %>"></input>
<br/>
<%
}
if(cufSet.size()>0){
%>
<h3>The list of user's [<%= (String) request.getSession().getAttribute("login")%>] composite feeds</h3>
<%
HashMap<RSS,String> mapRssStringForPrinting = new HashMap<RSS, String>();
ArrayList<RSS> rssCompositeListForPrinting = new ArrayList<RSS>();
StringBuilder sb = new StringBuilder();
for(Feed feedOnServer : ServerUtils.getFeedsList(false, true)) {
	try{
	//	log.debug(feedOnServer.getXmlFile());
		//RSS rssFeed = (RSS)ObjectsUtils.getXMLObjectFromXMLFile(feedOnServer.getXmlFile(), new RSS());
		RSS rssFeed = RSS.getRSSObjectFromXMLFile(feedOnServer.getXmlFile());
		if(feedOnServer.getId().startsWith("composite")) {
			rssCompositeListForPrinting.add(rssFeed);
		}
		mapRssStringForPrinting.put(rssFeed, feedOnServer.getId());
	}catch(Exception e){
		sb.append(feedOnServer.getId());
		sb.append(", ");
		log.error("Exception was occured on FeedsList.jsp page during building a list of feeds. The problem was detected on the feed on server ["+feedOnServer.getId()+"]", e);
	}
}
Collections.sort(rssCompositeListForPrinting, new RSSForPrintingComparatorByTitle());

for(RSS rss : rssCompositeListForPrinting) {
	out.println("<br>");
	%>
	<input type="radio" id="compositeFeedId" name="compositeFeedId" value="<%=mapRssStringForPrinting.get(rss)%>"/>
	<%
	out.println("<a href=\"showFeed?feedId="+mapRssStringForPrinting.get(rss) +"\">"+rss.getChannel().getTitle()+"</a>&nbsp&nbsp&nbsp[<a href=\"deleteFeed?feedId="+mapRssStringForPrinting.get(rss)+"\">Delete</a>]");
	out.println("&nbsp&nbsp&nbsp[<a href=\"mergeRSS.jsp?feedId="+mapRssStringForPrinting.get(rss)+"&feedTitle="+rss.getChannel().getTitle()+"\">Edit</a>]");
	out.println("<br>");	 
	out.println("&nbsp&nbsp&nbsp&nbsp&nbsp&nbspSource URL: "+rss.getChannel().getLink());
	out.println("<br>");	 
	out.println("&nbsp&nbsp&nbsp&nbsp&nbsp&nbspLast updated: " + rss.getChannel().getLastBuildDate());
	out.println("<br><br>");	 
//	ObjectsUtills.printXMLObject(rssFeed);
}
%>
<%
for(CompositeUserFeed cuf : cufSet){
	//RSS rss = RSS.getRSSObjectByFeedId(cuf.getId());
%>

<%
}
}else{
	out.write("User ["+(String) request.getSession().getAttribute("login")+"] doesn't have any composite feed");
}
%>
<br>
<input type="submit" value="Add"></input> 
</form>
</body>
</html>