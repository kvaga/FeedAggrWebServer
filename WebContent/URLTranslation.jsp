<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:include page="Header.jsp"></jsp:include>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>URL Translation</title>
</head>
<body>
<b style='color:red;'>${requestScope.Exception}</b>
<b style='color:black;'>${requestScope.Info}</b>

		<table border="1">
			<tr>
				<th>Domain</th>
				<th>RegexInURL</th>
				<th>TemplateOutURL</th>
				<th>Action</th>
			</tr>
			<tr>
				<form action="URLTranslationServlet">
					<td><input type="text" name="domain" /></td>
					<td><input type="text" name="RegexInURL" /></td>
					<td><input type="text" name="TemplateOutURL" /></td>
					<td><input type="submit" name="Add" value="Add"/></td>
					<input type="hidden" name="command" value="Add"/>
					<input type="hidden" name="redirectTo" value="/URLTranslation.jsp" />
				</form>
			</tr>
		</table>
		<hr>
		<table border="1">
			<tr>
				<th>Domain</th>
				<th>RegexInURL</th>
				<th>TemplateOutURL</th>
				<th>Delete</th>
				<th>Update</th>
				<th>URL for Test</th>
				<th>Test</th>
				

			</tr>
			<c:forEach items="${requestScope.ResponseResult}" var="item">
					<tr>
					
						<form action="URLTranslationServlet">
							<td><input type="text" name="domain" value="${item.value.getDomain()}"/></td>
							<td><input type="text" name="RegexInURL" value="${item.value.getRegexInURLPatternText()}"/></td>
							<td><input type="text" name="TemplateOutURL" value="${item.value.getTemplateOutUrl()}"/></td>
							<td><input type="submit" name="command" value="Delete"/></td>
							<td><input type="submit" name="command" value="Update"/></td>
							<td><input type="text" name="testUrl" value=""/></td>
							<td><input type="submit" name="command" value="test"/></td>						
							<input type="hidden" name="redirectTo" value="/URLTranslation.jsp" />
						</form>		
					</tr>
			</c:forEach>
		</table>
</body>
</html>

