<%@page import="ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed"%>
<%@page import="java.util.Enumeration,
java.util.ArrayList,
java.util.HashMap,
java.util.HashSet,
java.util.Set
"%>
<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
ru.kvaga.rss.feedaggr.Exec,
org.apache.logging.log4j.*,
ru.kvaga.rss.feedaggrwebserver.ConfigMap,
java.io.File,
ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
ru.kvaga.rss.feedaggrwebserver.objects.user.User,
ru.kvaga.rss.feedaggr.objects.RSS,
ru.kvaga.rss.feedaggr.objects.Channel,
ru.kvaga.rss.feedaggr.objects.Feed
"%>
<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Health Check</title>
<script language="JavaScript">
function toggle(source) {
	checkboxes = document.querySelectorAll('[name^=feed_id_]');
	  for(var i=0, n=checkboxes.length;i<n;i++) {
	    checkboxes[i].checked = source.checked;
	  }
	}
</script>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>
<%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());
%>
<%		
	ArrayList<Feed> allFeedsListOnTheServer = ServerUtils.getFeedsList();
	HashMap<String,String> allFeedIdsOfAllUsersMap = User.getFeedsIdsOfAllUsersMap();
	//User user = User.getXMLObjectFromXMLFileByUserName((String) request.getSession().getAttribute("login"));
%>
<h3>Abandoned feeds by users (no user who has these feeds)</h3>
<%
	ArrayList<Feed> abandonedFeedsList = new ArrayList<Feed>();
	ArrayList<Feed> compositeAbandonedList = new ArrayList();

	for (Feed feedFromAll : allFeedsListOnTheServer) {
		if (!allFeedIdsOfAllUsersMap.containsKey(feedFromAll.getId())) {
			log.warn("Found abandoned feed [" + feedFromAll.getId() + "]");
			if(feedFromAll.getId().startsWith("composite")){
				compositeAbandonedList.add(feedFromAll);
			}else{
				abandonedFeedsList.add(feedFromAll);
			}
		}
	}
	if(abandonedFeedsList.size()>0){
		// Abandoned feeds
		String table = "<form method=\"POST\" action=\"addAbandonedFeedToUser\">";
		table+="<table border='1'>"+
							"<tr align=\"center\"><td><input type=\"checkbox\" onClick=\"toggle(this)\" />#</td><td>Abandoned feed</td><td>Delete</td><td>Add to user</td></tr>";
		for(Feed feed : abandonedFeedsList){
			if(feed.getId().startsWith("composite")) continue;
			RSS rss = RSS.getRSSObjectFromXMLFile(feed.getXmlFile());
			table+=	"<tr>"+
						"<td><input type=\"checkbox\" id=\"feed_id_"+feed.getId()+"\" name=\"feed_id_"+feed.getId()+"\" value=\""+feed.getId()+"\"></td>"+
						"<td><a href=\"showFeed?feedId="+feed.getId()+"\">"+rss.getChannel().getTitle()+ "</a><br>"+rss.getChannel().getLink()+"</td>"+
						"<td>[<a href=\"deleteFeed?feedId="+feed.getId()+"&redirectTo=HealthCheck.jsp\">Delete</a>]</td>"+
						"<td><a href=\"addAbandonedFeedToUser?redirectTo=HealthCheck.jsp&feedId="+feed.getId()+"\">ADD</a></td>"+
					"</tr>";
		}
		table+="<tr><td colspan=\"3\"></td><td><input type=\"submit\" value=\"Add\"></td></tr></table><input id=\"batch\" name=\"batch\" type=\"hidden\" value=\"yes\"><input type=\"hidden\" name=\"redirectTo\" value=\"HealthCheck.jsp\"></form>";
		out.append(table);
	}else{
		out.append("There are no abandoned feeds<br><br>");
	}
	
	if(compositeAbandonedList.size()>0){
		// Composite abandoned feeds
		String tableComposite = "<table border='1'>"+
				"<tr align=\"center\"><td>Composite abandoned feed</td><td>Delete</td><td>Add to user</td></tr>";
		for(Feed feed : compositeAbandonedList){
			if(!feed.getId().startsWith("composite")) continue;
			RSS rss = RSS.getRSSObjectFromXMLFile(feed.getXmlFile());
			tableComposite+=	"<tr>"+
						"<td><a href=\"showFeed?feedId="+feed.getId()+"\">"+rss.getChannel().getTitle()+ "</a><br>"+rss.getChannel().getLink()+"</td>"+
						"<td>[<a href=\"deleteFeed?feedId="+feed.getId()+"&redirectTo=HealthCheck.jsp\">Delete</a>]</td>"+
						"<td><a href=\"addAbandonedFeedToUser?redirectTo=HealthCheck.jsp&feedId="+feed.getId()+"\">ADD</a></td>"+
					"</tr>";
		}
		tableComposite+="</table>";
		out.append(tableComposite);
	}else{
		out.append("There are no composite abandoned feeds<br>");
	}
		log.debug("Finished searching of abandoned files");
	%>

<h3>Duplicate feeds by users (duplicate feeds for user that have the same url)</h3>

<%
boolean foundDuplicates=false;
	for (User user : User.getAllUsersList()) {
		Set<CompositeUserFeed> allCompositeUserFeedCache = user.getCompositeUserFeeds();
		HashMap<String, HashSet<String>> feedIdsWithDuplicateUrls = user.getFeedIdsWithDuplicateUrls();
		if(feedIdsWithDuplicateUrls.size()>0){
			out.append("<table border='1'><tr><td align=\"center\" colspan=\"2\">User: "+user.getName()+"</td></tr><tr align=\"center\"><td>Url</td><td>FeedId</td></tr>");
			foundDuplicates=true;
			for (String url : feedIdsWithDuplicateUrls.keySet()) {
				out.append("<tr><td>"+url+"</td><td>");
				log.debug("Fulfilling html table with duplicated url ["+url+"]");
				for (String feedId : feedIdsWithDuplicateUrls.get(url)) {
					RSS rss = RSS.getRSSObjectByFeedId(feedId);
					
					out.append("<a href=\"showFeed?feedId="+feedId+"\">["+user.getCompositeUserFeedIdsListWhichContainUserFeedId(feedId, allCompositeUserFeedCache).size()+"] "+rss.getChannel().getTitle()+"</a>&nbsp&nbsp&nbsp[<a href=\"deleteFeed?feedId="+feedId+"&redirectTo=HealthCheck.jsp\">Delete</a>]");
					out.append("<br>");
				}
				out.append("</td></tr>");
			}
			out.append("</td></tr></table>");	
		}
	}
	if(!foundDuplicates){
		out.append("There are no duplicated feeds");
	}
log.debug("Finished searching of duplicate urls in feeds of users");

%>
<h3>Zombie feeds in composite feeds by user (composite feed has feeds that don't exist)</h3>
<%

//for (User user : User.getAllUsersList()) {
//	Set<CompositeUserFeed> allCompositeUserFeedCache = user.getCompositeUserFeeds();
	
%>
</body>
</html>