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
<input type="hidden" name="appendSingleFeedId" value="true"></input>
Feed id: <input type="text" name="id_<%=request.getParameter("feedId")%>" value="<%= request.getParameter("feedId")!=null? request.getParameter("feedId"):"" %>"></input>
<br/>
<%
if(cufSet.size()>0){
%>
<h3>The list of user's [<%= (String) request.getSession().getAttribute("login")%>] composite feeds</h3>
<%
for(CompositeUserFeed cuf : cufSet){
	//RSS rss = RSS.getRSSObjectByFeedId(cuf.getId());
%>
<input type="radio" id="feedId" name="feedId" value="<%=cuf.getId()%>"><label for="feedId"><%=cuf.getId()%></label>
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