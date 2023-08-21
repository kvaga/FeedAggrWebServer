<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:include page="Header.jsp"></jsp:include>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>


<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Settings Of Composite User Feed</title>
</head>
<body>

	<b style='color: red;'>${requestScope.Exception}</b>
	<b style='color: black;'>${requestScope.Info}</b>

	<h1>Settings Of Composite User Feed [${requestScope.title}]</h1>
	<table border="1">
		<tr>
			<th>Property</th>
			<th>Value</th>
			<!-- <th>Delete</th> -->
			<th>Update</th>
		</tr>
		
		<c:forEach items="${requestScope.ResponseResult}" var="item">
			<tr>
				<form action="SettingsOfSpecificCompositeUserFeedServlet">
					<td>${item.key}</td>
					<td><textarea rows="5" cols="45" name="value">${fn:escapeXml(item.value)}</textarea></td>
					<!--<td><input type="submit" name="command" value="Delete"/></td>-->
					<td><input type="submit" name="command"
						value="UpdateCompositeUserFeedSetting" /></td> <input type="hidden"
						name="redirectTo" value="/SettingsOfSpecificCompositeUserFeed.jsp" />
					<input type="hidden" name="property" value="${item.key}" /> <input
						type="hidden" name="feedId" value="${requestScope.feedId}" />
				</form>
			</tr>
		</c:forEach>
	</table>
	<form action="SettingsOfSpecificCompositeUserFeedServlet">
		<input type="submit" name="command"
			value="ResetCompositeUserFeedSettings" /> <input type="hidden"
			name="redirectTo" value="/SettingsOfSpecificCompositeUserFeed.jsp" />
		<input type="hidden" name="feedId" value="${requestScope.feedId}" />

	</form>
</body>
</html>