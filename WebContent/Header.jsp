<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
<!--   
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
-->
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Header</title>
</head>
<body>

<a href="LoginSuccess.jsp">Main page</a>. If you have other feeds, <a href="Feed.jsp?action=new">add</a> them to your account. Or <a href="mergeRSS.jsp">create Composite</a> rss feed
<br/>
<table border="1">
	<tr>
		<td><a href="FeedsList.jsp">Feeds List</a> <br/> <a href="CompositeFeedsList.jsp">Composite Feeds List</a></td>
		<td><a href="addFeedsByList.jsp">Add feeds by URL list</a><br/><a href="CheckoutPage.jsp">Checkout Page</a></td>
		<td><a href="Test">Test page</a><br/><a href="HealthCheck.jsp">Health Check page</a></td>
		<td><a href="addFeedId2CompositeFeed.jsp">addFeedId2CompositeFeed</a><br/><a href="DeleteOldFeedItems.jsp">DeleteOldFeedItems.jsp</a></td>
		<td><a href="deleteFeedsByList.jsp">deleteFeedsByList</a><br/><a href=""></a></td>
		<td><a href="CompositeFeedsList?redirectTo=/CompositeFeedsExport.jsp&amp;userName=kvaga">Composite Feeds Export</a><br/><a href="UserList?redirectTo=/CompositeFeedsImport.jsp">Composite Feeds Import</a></td>
	</tr>
</table>
<hr/>
<h3>Composite Feeds Export</h3>
  
</body>
</html>
