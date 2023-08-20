<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:include page="Header.jsp"></jsp:include>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Common User Settings</title>
</head>
<body>

<b style='color:red;'>${requestScope.Exception}</b>
<b style='color:black;'>${requestScope.Info}</b>

<h1>Common User Settings</h3>
<h2>Composite User Feed Settings</h2>
<!-- 
<table border="1">
			<tr>
				<th>Property</th>
				<th>Value</th>
				<th>Action</th>
			</tr>
			<tr>
				<form action="UserSettingsServlet">
					<td><input type="text" name="property" /></td>
					<td><input type="text" name="value" /></td>
					<td><input type="submit" name="Add" value="Add"/></td>
					<input type="hidden" name="command" value="Add"/>
					<input type="hidden" name="redirectTo" value="/UserSettings.jsp" />
				</form>
			</tr>
		</table>
		
		<hr>
		-->
		<table border="1">
			<tr>
				<th>Property</th>
				<th>Value</th>
				<!-- <th>Delete</th> -->
				<th>Update</th>				
			</tr>
			<c:forEach items="${requestScope.ResponseResult}" var="item">
					<tr>
						<form action="UserSettingsServlet">
							<td>${item.key}</td>
							<td><input type="text" name="value" value="${item.value}"/></td>
							<!--<td><input type="submit" name="command" value="Delete"/></td>-->
							 <td><input type="submit" name="command" value="UpdateCompositeUserFeedSetting"/></td> 
							<input type="hidden" name="redirectTo" value="/UserSettings.jsp" />
							<input type="hidden" name="property" value="${item.key}"/>
						</form>		
					</tr>
			</c:forEach>
		</table>
		<form action="UserSettingsServlet">
			<input type="submit" name="command" value="ResetCompositeUserFeedSettings"/>
			<input type="hidden" name="redirectTo" value="/UserSettings.jsp" />
		</form>
</body>
</html>