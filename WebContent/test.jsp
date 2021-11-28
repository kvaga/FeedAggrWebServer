<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
ru.kvaga.rss.feedaggr.Exec
"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
    ru.kvaga.rss.feedaggr.FeedAggrException,
    ru.kvaga.rss.feedaggr.Exec,
    ru.kvaga.rss.feedaggr.FeedAggrException,
    ru.kvaga.rss.feedaggr.Item,
    java.util.LinkedList,
    java.util.ArrayList,
    java.util.Date,
    java.io.File,
    ru.kvaga.rss.feedaggr.objects.RSS,
    ru.kvaga.rss.feedaggr.objects.Channel,
    ru.kvaga.rss.feedaggr.objects.Feed,
    ru.kvaga.rss.feedaggr.objects.GUID,
    ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
    ru.kvaga.rss.feedaggrwebserver.objects.user.User,
    ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed,
        ru.kvaga.rss.feedaggrwebserver.objects.user.UserRepeatableSearchPattern,
            ru.kvaga.rss.feedaggrwebserver.ConfigMap,
            org.apache.logging.log4j.*,
            ru.kvaga.monitoring.influxdb2.InfluxDB
            
        
    "%>
    <%
	if(request.getAttribute("monitoringInfo")!=null){
		//MonitoringInfo monitoringInfo = (MonitoringInfo) request.getAttribute("MonitoringInfo");
		request.setAttribute("monitoringInfo", request.getAttribute("monitoringInfo"));
%>
		<c:set var="monitoringInfo" scope="session" value="${monitoringInfo}"/>
<%
	}else{
	%>
	<jsp:forward page="/Monitoring?redirectTo=/test.jsp"/> 
	
	<%
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
FeedsUpdateJobIsWorkingNow: ${monitoringInfo.getFeedsUpdateJobIsWorkingNow()} 
<br/>
CompositeFeedsUpdateJobIsWorkingNow: ${monitoringInfo.getCompositeFeedUpdateJobIsWorkingNow()}
</body>
</html>