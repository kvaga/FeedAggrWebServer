<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<%@ page
	import="ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSS,
	ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSSForPrintingComparatorByTitle,
	ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
	ru.kvaga.rss.feedaggrwebserver.ServerUtils,
	java.util.Collections,
	java.util.HashMap,
	java.util.Date,
	java.util.ArrayList,
	org.apache.logging.log4j.*,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap,
	ru.kvaga.rss.feedaggr.Exec,
	ru.kvaga.rss.feedaggrwebserver.servlets.CompositeFeedTotalInfo
	"%>
<title>Composite Feed Export</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>
<%
String userName=(String)request.getSession().getAttribute("login");
if(request.getAttribute("compositeFeedList")!=null){
	request.setAttribute("compositeFeedList", request.getAttribute("compositeFeedList"));
%>



<c:if test="${compositeFeedList.size() >0 }">
	<table border="1">
		<tr>
			<th>Name</th>
			<th>Delete</th>
			<th>Export</th>
			<th>Last updated</th>
			<th>Count of items</th>
			<th>Size, mb</th>
			<th>Oldest PubDate</th>
			<th>Newest PubDate</th>
		</tr>
</c:if>


<c:forEach items="${compositeFeedList}" var="item">
	<tr>
		<td>${item.getName()}</td>
		<td>Delete</td>
		<td><a href="ExportCompositeFeed?userName=<%= userName%>&amp;compositeFeedId=${item.getFeedId()}&amp;source=/CompositeFeedsExport.jsp">Export</a></td>
		<td>${item.getLastUpdated()}</td>
		<td>${item.getCountOfItems()}</td>
		<td>${item.getSizeMb()}</td>
		<td>${item.getOldestPubDate()}</td>
		<td>${item.getNewestPubDate()}</td>
	</tr>
</c:forEach>
<c:if test="${compositeFeedList.size() >0 }">
	</table>
</c:if>

<%
}else{
	if(request.getAttribute("Exception")!=null){
		Exception e = (Exception)request.getAttribute("responseResultException");
		out.write(Exec.getHTMLFailText(e));
	}
}
%>

</body>
</html>