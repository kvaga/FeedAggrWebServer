<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
ru.kvaga.rss.feedaggr.Exec
"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
    ru.kvaga.rss.feedaggr.FeedAggrException,
    ru.kvaga.rss.feedaggr.Exec,
    ru.kvaga.rss.feedaggr.FeedAggrException,
    ru.kvaga.rss.feedaggr.Item,
    java.util.LinkedList,
    java.util.ArrayList,
    java.util.Date,
    java.io.File,
    ru.kvaga.rss.feedaggr.objects.RSS,
    ru.kvaga.rss.feedaggr.objects.Channel,
    ru.kvaga.rss.feedaggr.objects.Feed,
    ru.kvaga.rss.feedaggr.objects.GUID,
    ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
    ru.kvaga.rss.feedaggrwebserver.objects.user.User,
    ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed,
        ru.kvaga.rss.feedaggrwebserver.objects.user.UserRepeatableSearchPattern,
            ru.kvaga.rss.feedaggrwebserver.ConfigMap,
            org.apache.logging.log4j.*,
            ru.kvaga.monitoring.influxdb2.InfluxDB
            
        
    "%>
    <%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());
    %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
!!!!!!!!!!!!!!!1
<%
//InfluxDB2.getInstance().send("response_time,method=getYoutubeListOfPlaylistsURLs", 45);

%>
<%
File userFile=new File(ConfigMap.usersPath.getAbsoluteFile()+"/"+"kvaga"+".xml");
//User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
User user = User.getXMLObjectFromXMLFile(userFile);
request.getSession().setAttribute("url", "https://www.youtube.com/asdasdlj");
%>
<%
if(user.getRssItemPropertiesPatterns()!=null && user.getRssItemPropertiesPatternByDomain(
		Exec.getDomainFromURL((String)request.getSession().getAttribute("url")))!=null){
	out.print(user.getRssItemPropertiesPatternByDomain(
			Exec.getDomainFromURL((String)request.getSession().getAttribute("url"))).getPatternTitle());
}else{
	out.print("{%2}");
}
%>
<br>
<a href="ICSServlet?ics_filename=my_filename.ics&ics_summary=summary&ics_description=description">download the jsp file</a>  
</body>
</html>