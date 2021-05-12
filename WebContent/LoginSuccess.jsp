<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
	<%@ page
	import="
	org.apache.logging.log4j.*,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap
	"%>
	<%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());

	%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Login Success Page</title>
</head>
<body>

<%  
// allow access only if session exists
String user = (String) session.getAttribute("user");

String userName=null;
String sessionID=null;
Cookie[] cookies = request.getCookies();
if(cookies!=null){
	for(Cookie cookie : cookies){
		if(cookie.getName().equals("user")){
			userName=cookie.getValue();
		}
		if(cookie.getName().equals("JSESSIONID")){
			sessionID=cookie.getValue();
		}
	}
}
%>
	<h3>Hi <%= userName %>, Login successful. <%= request.getSession().getAttribute("login") %></h3>
	<!--  User=<%= user %> -->
	<br> 
	
	<!--  Your SessionID=<%= sessionID %>-->
	<br>
	<jsp:include page="/FeedsList.jsp">
        <jsp:param value="" name=""/>
    </jsp:include>
    <br>
	<form action="LogoutServlet" method="post">
		<input type="submit" value="Logout">
	</form>
</body>
</html>