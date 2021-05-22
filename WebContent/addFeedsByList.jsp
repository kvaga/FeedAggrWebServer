<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
org.apache.logging.log4j.*,
ru.kvaga.rss.feedaggrwebserver.ConfigMap
"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName()); 
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Add Feeds by List</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>

	<form>
		Specify the list of URLs (each URL on next line)
		<textarea rows="12" cols="120" name="listOfURLs"><%=request.getSession().getAttribute("listOfURLs") == null ? ""
					: (String) request.getSession().getAttribute("listOfURLs")%></textarea>
					<br/>
	<input type="submit" value="Add"/>
	</form>
	<%
		if (request.getParameter("listOfURLs") != null) {
			request.getSession().setAttribute("listOfURLs", request.getParameter("listOfURLs"));
			out.write("<table border=\"1\"><tr><td>URL</td><td>Status</td></tr>");
			for(String url : ((String)request.getParameter("listOfURLs")).split("\r\n")){
				try{
					int size = ServerUtils.addRSSFeedByURLAutomaticly(url, (String)request.getSession().getAttribute("login"));
					if(size>0){
						out.write("<tr><td>"+url+"</td><td>"+size+"</td></tr>");
					}else{
						out.write("<font color=\"red\"><tr><td>"+url+"</td><td>"+size+"</td></tr></font>");
					}
				}catch(Exception e){
					out.write("<font color=\"red\"><tr><td>"+url+"</td><td>"+e.getMessage()+"</td></tr></font>");
					log.error("ShowResultTableException", e);
				}
			}
			out.write("</table>");
			//ServerUtils.clearSessionFromFeedAttributes(request);
		}
	%>
</body>
</html>