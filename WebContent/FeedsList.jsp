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
	java.util.Date, 
	java.text.SimpleDateFormat,
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
<meta charset="utf-8">
<script src="sort_table.js"></script> 
<!--<script src="lib.js"></script>-->

<title>Feeds List</title>
<style type="text/css">
	table, th, td {
	    border: 1px solid black;
	}
	th {
	    cursor: pointer;
	}
</style>
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
out.println("<table id=\"table\" border=1 style=\"white-space:nowrap;\">");
out.println("<tr><th onclick=\"sortTable(1)\">Name</th><th>Delete</th><th>Edit</th><th onclick=\"sortTable(4)\">Last updated</th><th onclick=\"sortTable(5, 'true')\">Count of items</th><th onclick=\"sortTable(6, 'true')\">Size, mb</th><th onClick='show_hide_column(7,false);'>URL</th><th onclick=\"sortTable(7, 'true')\">Oldest PubDate</th><th onclick=\"sortTable(8, 'true')\">Newest PubDate</th></tr>");	 
//out.println("<tr><th>Name</th><th>Delete</th><th>Edit</th><th>Last updated</th><th>Count of items</th><th>Size, mb</th><th onClick='show_hide_column(7,false);' class=\"targ\">URL</th></tr>");	 
/*
out.println("<col class=\"col1\"/>");
out.println("<col class=\"col2\"/>");
out.println("<col class=\"col3\"/>");
out.println("<col class=\"col4\"/>");
out.println("<col class=\"col5\"/>");
out.println("<col class=\"col6\"/>");
out.println("<col class=\"col7\"/>");
*/
//for(RSS rss : rssListForPrinting){
//	out.println("<br>");	 
//	out.println("<a href=\"showFeed?feedId="+mapRssStringForPrinting.get(rss) +"\">"+rss.getChannel().getTitle()+"</a>&nbsp&nbsp&nbsp[<a href=\"deleteFeed?feedId="+mapRssStringForPrinting.get(rss)+"\">Delete</a>]&nbsp&nbsp&nbsp[<a href=\"Feed.jsp?action=edit&feedId="+mapRssStringForPrinting.get(rss)+"\">Edit</a>]");
//	out.println("<br>");	 
//	out.println("Source URL: "+rss.getChannel().getLink());
//	out.println("<br>");	 
//	out.println("Last updated: " + rss.getChannel().getLastBuildDate());
//	out.println("<br><br>");	 
////	ObjectsUtills.printXMLObject(rssFeed);
//}
SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

for(RSS rss : rssListForPrinting) {
	Date[] oldestNewest = rss.getOldestNewestPubDate();
	out.println("<td><a href=\"showFeed?feedId="+mapRssStringForPrinting.get(rss) +"\">"+rss.getChannel().getTitle()+"</a></td><td>[<a href=\"deleteFeed?feedId="+mapRssStringForPrinting.get(rss)+"\">Delete</a>]</td>");
	//out.println("<td>[<a href=\"mergeRSS.jsp?feedId="+mapRssStringForPrinting.get(rss)+"&feedTitle="+rss.getChannel().getTitle()+"\">Edit</a>]</td>");
	out.println("<td>[<a href=\"Feed.jsp?action=edit&feedId="+mapRssStringForPrinting.get(rss)+"\">Edit</a>]</td>");

	
	out.println("<td>"+rss.getChannel().getLastBuildDate()+"</td>");
	out.println("<td>"+rss.getChannel().getItem().size()+"</td>");
	out.println("<td>"+Exec.getFileSizeByFeedId(mapRssStringForPrinting.get(rss))+"</td>");
	out.println("<td><a href=\""+rss.getChannel().getLink()+"\">"+rss.getChannel().getLink()+"</a></td>");
	out.println("<td>"+sdf.format(oldestNewest[0])+"</td>");
	out.println("<td>"+sdf.format(oldestNewest[1])+"</td>");
	out.println("</tr>");	 
//	ObjectsUtills.printXMLObject(rssFeed);
}
out.println("</table>");
%>





</body>
</html>