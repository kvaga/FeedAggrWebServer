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
<h1>Composite Feeds Import</h1>
<%
if(request.getAttribute("ResponseResult")!=null)
	request.setAttribute("ResponseResult", request.getAttribute("ResponseResult"));
if(request.getAttribute("Exception")!=null)
	request.setAttribute("Exception", request.getAttribute("Exception"));
%>
<%-- 
<%
if(request.getAttribute("userList")!=null){
	request.setAttribute("userList", request.getAttribute("userList"));
%>
--%>
Upload file to the user <%= session.getAttribute("login") %>
<!-- 
The list of Users to assign Composite User Feed
-->

<form method="post" action="ImportCompositeUserFeed" enctype="multipart/form-data">
<!--
	<c:forEach items="${userList}" var="item">
		<input type="radio" id="userName" name="userName" value="${item.getName()}"/>
    	<label for="${item.getName()}">${item.getName()}</label>
	</c:forEach>
	
<br/>
 -->
    Choose a file: <input type="file" name="multiPartServlet" />
    <input type="hidden" name="redirectTo" value="/CompositeFeedsImport.jsp"/>
    <input type="submit" value="Upload" />
</form>
<%--
}else if(request.getAttribute("ResponseResult")!=null){
	request.setAttribute("ResponseResult", request.getAttribute("ResponseResult"));
--%>
<c:out value="${ResponseResult}"></c:out>
<br/>
<c:out value="${Exception}"></c:out>

<%-- 		
}else{
	if(request.getAttribute("Exception")!=null){
		Exception e = (Exception)request.getAttribute("responseResultException");
		out.write(Exec.getHTMLFailText(e));
	}
}
--%>
</body>
</html>