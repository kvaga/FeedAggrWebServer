<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page
	import="
	java.util.Collections,
	java.util.HashMap,
	java.util.Date,
	java.util.ArrayList,
	org.apache.logging.log4j.*,
	ru.kvaga.rss.feedaggr.Exec,
	ru.kvaga.rss.feedaggrwebserver.servlets.CompositeFeedTotalInfo
	"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Composite Feeds Import</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>

<%
if(request.getAttribute("userList")!=null){
	request.setAttribute("userList", request.getAttribute("userList"));
%>
<ul>
	<c:forEach items="${userList}" var="item">
		<li>${item.getName()}</li>
	</c:forEach>
</ul>
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