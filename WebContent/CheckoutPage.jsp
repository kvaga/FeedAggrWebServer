<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
	<%@page import="
org.apache.logging.log4j.*,
ru.kvaga.rss.feedaggrwebserver.ConfigMap
	"%>
	
	<%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());
	%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "https://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="Header.jsp"></jsp:include>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Checkout Page</title>
<style>
   .layer {
    overflow: scroll; /* Добавляем полосы прокрутки */
    width: 300px; /* Ширина блока */
    height: 150px; /* Высота блока */
    padding: 5px; /* Поля вокруг текста */
    border: solid 1px black; /* Параметры рамки */
   } 
  </style>
</head>
<body>
<h1 style="color:blue;font-size:40px;"><a href="{%2}">{%1}</a></h1>  
	<%
		String userName = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("user"))
					userName = cookie.getValue();
				
			}
		}
	%>
	<h3>
		Hi
		<%=userName%>, do the checkout.
	</h3>
	
	<br>
	<form action="LogoutServlet" method="post">
	<div class="layer">
	<h2>Item 1</h2>
	</div>
		<input type="submit" value="Logout">
	</form>
</body>
</html>
