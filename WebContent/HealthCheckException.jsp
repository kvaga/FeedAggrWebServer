<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:include page="Header.jsp"></jsp:include>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Health Check Exception</title>
</head>
<body>

<table border="1">
<tr><th>Exception</th><th></th>Size</th><th>URLs</th></tr>
	<c:forEach items="${requestScope.ExceptionsList}" var="item">
        <tr>
          <td>${item.key}</td><td>${item.value.size()}</td><td>${item.value}</td>
        </tr>
      </c:forEach>
</table>
</body>
</html>