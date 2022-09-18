<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:include page="Header.jsp"></jsp:include>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ page import="java.util.*" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>FixLostRSSURLsAndTitlesFromUserFile</title>
</head>
<body>
<table border="1">
<tr><th>Title</th></tr>

<c:forEach items="${requestScope.FixedList}" var="fixedItem">
        <tr>
          <td>${fixedItem}</td>
        </tr>
      </c:forEach>
</table>
	<form action="FixLostRSSURLsAndTitlesFromUserFile">
		<input type="hidden" name="redirectTo" value="FixLostRSSURLsAndTitlesFromUserFile.jsp"></input>
		<input type="hidden" name="userName" value="<%= request.getSession().getAttribute("login") %>"></input>
		<input type="submit" name="Fix" value="Fix"/>
	</form>
</body>
</html>