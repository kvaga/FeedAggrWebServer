<%@page import="ru.kvaga.rss.feedaggrwebserver.servlets.MonitoringInfo"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>

 <%
	if(request.getAttribute("monitoringInfo")!=null){
		//MonitoringInfo monitoringInfo = (MonitoringInfo) request.getAttribute("MonitoringInfo");
		request.setAttribute("monitoringInfo", request.getAttribute("monitoringInfo"));
%>
		<c:set var="monitoringInfo" scope="session" value="${monitoringInfo}"/>
<%
	}else{
		// <jsp:forward page="/Monitoring1"/>
	%>
	
	
	<%
	}
%>
<!--   
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
-->
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script>
try{
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4) {
            //var data = JSON and JSON.parse(xhr.responseText);
            var data = JSON.parse(xhr.responseText);
            //alert(data);
            
            if(data.feedsUpdateJobIsWorkingNow){
                document.getElementById("FeedsUpdateJobStatus").innerHTML='<span>&#128308;</span>';
            }else{
                document.getElementById("FeedsUpdateJobStatus").innerHTML='<span>&#128994;</span>';
            }
            if(data.compositeFeedUpdateJobIsWorkingNow){
                document.getElementById("CompositeFeedsUpdateJobStatus").innerHTML='<span>&#128308;</span>';
            }else{
                document.getElementById("CompositeFeedsUpdateJobStatus").innerHTML='<span>&#128994;</span>';
            }
        }
    }
    xhr.open('GET', '${pageContext.request.contextPath}/Monitoring?type=json', true);
    xhr.send(null);
}catch(err){
	document.getElementById("FeedsUpdateJobStatus").innerHTML=err.message;
    document.getElementById("CompositeFeedsUpdateJobStatus").innerHTML=err.message;

}
</script>
<title>Header</title>
</head>
<body>


<a href="LoginSuccess.jsp">Main page</a>. If you have other feeds, <a href="Feed.jsp?action=new">add</a> them to your account. Or <a href="mergeRSS.jsp">create Composite</a> rss feed
<br/>
<table border="1">
	<tr>
		<td><a href="FeedsList.jsp">Feeds List</a><br/><a href="FeedsListShort.jsp">Feeds List Short</a> <br/> <a href="CompositeFeedsList.jsp">Composite Feeds List</a><br/> <a href="CompositeFeedsListShort.jsp">Composite Feeds Short List</a></td>
		<td><a href="addFeedsByList.jsp">Add feeds by URL list</a><br/><a href="CheckoutPage.jsp">Checkout Page</a></td>
		<td><a href="Test">Test page</a><br/><a href="HealthCheck.jsp">Health Check page</a></td>
		<td><a href="addFeedId2CompositeFeed.jsp">addFeedId2CompositeFeed</a><br/><a href="DeleteOldFeedItems.jsp">DeleteOldFeedItems.jsp</a></td>
		<td><a href="deleteFeedsFeedsByList.jsp">deleteFeedsFeedsByList</a></td>
		<td><a href="CompositeFeedsExport.jsp">Composite Feeds Export</a><br/><a href="CompositeFeedsImport.jsp">Composite Feeds Import</a></td>
		<td>
			<div id="FeedsUpdateJobStatus"></div>&nbsp;FeedsUpdateJob Status<br/>
			<div id="CompositeFeedsUpdateJobStatus"></div>&nbsp;CompositeFeedsUpdateJob Status<br/>
		</td>
		<!--  
		<td>
			<c:if test="${monitoringInfo.getFeedsUpdateJobIsWorkingNow()}"><span>&#128308;</span></c:if>
			<c:if test="${not monitoringInfo.getFeedsUpdateJobIsWorkingNow()}"><span>&#128994;</span></c:if>
		    FeedsUpdateJob Status
		    <br/>
		    <c:if test="${monitoringInfo.getCompositeFeedUpdateJobIsWorkingNow()}"><span>&#128308;</span></c:if>
			<c:if test="${not monitoringInfo.getCompositeFeedUpdateJobIsWorkingNow()}"><span>&#128994;</span></c:if>
		    CompositeFeedsUpdateJob Status
		 </td>
		 -->
	</tr>
	
</table>

  
</body>
</html>
