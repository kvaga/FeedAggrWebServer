<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
ru.kvaga.rss.feedaggr.Exec
"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils"%>
   
		<c:set var="monitoringInfo" scope="session" value="${monitoringInfo}"/>

<!DOCTYPE html>
<jsp:include page="Header.jsp"></jsp:include>

<html>
<head>
<script src="js/lib.js"></script>
<meta charset="utf-8">
<title>Insert title here</title>

</head>
<body>

	<div id="exception1"></div>

Info: "${param.monitoringInfo}"
<br/>
</body>
</html>