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
<b style='color:red;'>${requestScope.exception}</b>

	<table border="1">
		<tr><th>Domain</th><th>RegexInURL</th><th>TemplateOutURL</th></tr>
		<form action="URLTranslationServlet">
			<tr>
				<td><input type="text" name="domain"/></td>
				<td><input type="text" name="RegexInURL"/></td>
				<td><input type="text" name="TemplateOutURL"/></td>
			</tr>
			<input type="hidden" name="command" value="add"/>
		</form>
		<tr><th>Domain</th><th>RegexInURL</th><th>TemplateOutURL</th></tr>
		<form action="URLTranslationServlet">
			<c:forEach items="${requestScope.UrlTranslationsList}" var="item">
		        <tr>
		          <td>${item.domain}</td><td>${item.regexInURLPatternText}<td>${item.templateOutUrlText}</td>
		        </tr>
		    </c:forEach>
		    <input type="hidden" name="command" value="update"/>
	    </form>
	</table>
	
</body>
</html>

