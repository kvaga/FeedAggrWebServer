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
	ru.kvaga.rss.feedaggrwebserver.ConfigMap,
	ru.kvaga.rss.feedaggr.Exec
	"%>
	<%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());

	%>
    
<!DOCTYPE html>
<html>
<head>
<script src="sort_table.js"></script>
<style type="text/css">
	table, th, td {
	    border: 1px solid black;
	}
	th {
	    cursor: pointer;
	}
</style>
<meta charset="utf-8">
<title>Composite Feeds List</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>
<h3>Composite feeds</h3>
<%
HashMap<RSS,String> mapRssStringForPrinting = new HashMap<RSS, String>();
//log.debug("=======================> " + ConfigMap.feedsPath);
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
out.println("<table id=\"table1\" border=1>");
//out.println("<tr><td>Name</td><td>Delete</td><td>Edit</td><td>Last updated</td><td>Count of items</td><td>Size, mb</td></tr>");	 
out.println("<tr><th onclick=\"sortTable(1)\">Name</th><th>Delete</th><th>Edit</th><th onclick=\"sortTable(4)\">Last updated</th><th onclick=\"sortTable(5, 'true')\">Count of items</th><th onclick=\"sortTable(6, 'true')\">Size, mb</th></tr>");	 

for(RSS rss : rssCompositeListForPrinting) {
	out.println("<tr><td><a href=\"showFeed?feedId="+mapRssStringForPrinting.get(rss) +"\">"+rss.getChannel().getTitle()+"</a></td><td>[<a href=\"deleteFeed?feedId="+mapRssStringForPrinting.get(rss)+"\">Delete</a>]</td>");
	out.println("<td>[<a href=\"mergeRSS.jsp?feedId="+mapRssStringForPrinting.get(rss)+"&feedTitle="+rss.getChannel().getTitle()+"\">Edit</a>]</td>");
	out.println("<td>"+rss.getChannel().getLastBuildDate()+"</td>");
	out.println("<td>"+rss.getChannel().getItem().size()+"</td>");
	out.println("<td>"+Exec.getFileSizeByFeedId(mapRssStringForPrinting.get(rss))+"</td>");
	out.println("</tr>");	 
//	ObjectsUtills.printXMLObject(rssFeed);
}
out.println("</table>");

%>
</body>
</html>