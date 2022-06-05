<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:include page="Header.jsp"></jsp:include>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Health Check</title>
<script src="js/lib.js"></script>
<script src="js/healthcheck.js"></script>

<style type="text/css">
	table, th, td {
	    border: 1px solid black;
	}
	th {
	    cursor: pointer;
	}
</style>
<script>
// Check composite user feed for null titles

try{
	/*
	loadingStart();
    var xhr1 = new XMLHttpRequest();
    xhr1.onreadystatechange = function() {
        if (xhr1.readyState == 4) {
        	loadingStop();
            const dataObj = JSON.parse(xhr1.responseText);
            fulfillTableCompouseUserFeedShort(dataObj);
        	//document.getElementById("tt").innerHTML=
        											//dataObj;
        	//										xhr1.responseText;
        }
    }

    xhr1.open('GET', '${pageContext.request.contextPath}/CompositeFeedsList?type=json&short=true&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr1.send(null);
    */
}catch(err){
	exception(err.message);
}

//this function appends the json data to the table 'gable'
function fulfillTableCompouseUserFeedShort(dataObj){
	/*
	var table = document.getElementById('tableCompositeUserFeed');
	 for(var i=0; i<dataObj.length;i++){            
	 	//console.log(dataObj[i].feedId);
	 	if(!dataObj[i].name){
		 	var tr = document.createElement('tr');
			tr.innerHTML = 
				'<td>' + dataObj[i].feedId + '</td>' +
		    	'<td>' + '<a href="/mergeRSS.jsp?feedTitle='+dataObj[i].name+'&feedId='+dataObj[i].feedId+'">Edit' + '</a>'+'</td>' +
		    table.appendChild(tr);
		 }
	 }
	 */
}

</script>

<script>
// Check user feed for null title or url

try{
	/*
	loadingStart();
    var xhr2 = new XMLHttpRequest();
    xhr2.onreadystatechange = function() {
        if (xhr2.readyState == 4) {
        	loadingStop();
            const dataObj = JSON.parse(xhr2.responseText);
            fulfillTableUserFeedShort(dataObj);
        	//document.getElementById("tt").innerHTML=
        											//dataObj;
        	//										xhr2.responseText;
        }
    }

    xhr2.open('GET', '${pageContext.request.contextPath}/FeedsList?type=json&short=true&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr2.send(null);
    */
}catch(err){
	document.getElementById("tt").innerHTML=err.message;
}

//this function appends the json data to the table 'gable'
function fulfillTableUserFeedShort(dataObj){
	/*
	var tableUserFeed = document.getElementById('tableUserFeed');
	 for(var i=0; i<dataObj.length;i++){            
	 	//console.log(dataObj[i].feedId);
	 	if(!(dataObj[i].userFeedTitle && dataObj[i].userFeedUrl)){
		 	var tr = document.createElement('tr');
			tr.innerHTML = 
				'<td>' + dataObj[i].id + '</td>' +
				'<td>' + dataObj[i].userFeedTitle + '</td>' +
				'<td>' + dataObj[i].userFeedUrl + '</td>' +

		    	'<td>' + '<a href="/Feed.jsp?action=edit&feedId='+dataObj[i].id+'">Edit' + '</a>'+'</td>';
		    tableUserFeed.appendChild(tr);
		 }
	 }
	 */
}

</script>
</head>
<body>
<h3>Zombie feeds in composite feeds by user (composite feed has feeds that don't exist)</h3>
<div id='divCheckZombiFeedsInCompositeFeeds'></div>

<script type="text/javascript">
	getGetJSONContentFromURL(
			'${pageContext.request.contextPath}/HealthCheck?kindOfCheck=checkZombiFeedsInCompositeFeeds&userName=<%= request.getSession().getAttribute("login")%>',
			onErr,
			function onSuccess(obj){
				createTableForCheckZombiFeedsInCompositeFeeds(obj, 'divCheckZombiFeedsInCompositeFeeds');
			} 
	);
	
</script>

<h3>Abandoned feeds by users (no user who has these feeds)</h3>
...
<h3>Duplicate feeds by users (duplicate feeds for user that have the same url)</h3>

<%
/*
boolean foundDuplicates=false;
	for (User user : User.getAllUsersList()) {
		try{
			//Set<CompositeUserFeed> allCompositeUserFeedCache = user.getCompositeUserFeeds();
			HashMap<String, HashSet<String>> feedIdsWithDuplicateUrls = user.getFeedIdsWithDuplicateUrls();
			if(feedIdsWithDuplicateUrls.size()>0){
				out.append("<table border='1'><tr><td align=\"center\" colspan=\"2\">User: "+user.getName()+"</td></tr><tr align=\"center\"><td>Url</td><td>FeedId</td></tr>");
				foundDuplicates=true;
				for (String url : feedIdsWithDuplicateUrls.keySet()) {
					out.append("<tr><td>"+url+"</td><td>");
					//log.debug("Fulfilling html table with duplicated url ["+url+"]");
					for (String feedId : feedIdsWithDuplicateUrls.get(url)) {
						RSS rss = RSS.getRSSObjectByFeedId(feedId);
						out.append("<a href=\"showFeed?feedId="+feedId+"\">["+user.getCompositeUserFeedsListWhichContainUserFeedId(feedId).size()+"] "+rss.getChannel().getTitle()+"</a>&nbsp&nbsp&nbsp[<a href=\"deleteFeed?feedId="+feedId+"&redirectTo=/HealthCheck.jsp\">Delete</a>]");
						out.append("<br>");
					}
					out.append("</td></tr>");
				}
				out.append("</td></tr></table>");	
			}
		}catch(Exception e){
			out.println(Exec.getHTMLFailText(e)+"<br>");
		}
	}
	if(!foundDuplicates){
		out.append("There are no duplicated feeds");
	}
log.debug("Finished searching of duplicate urls in feeds of users");
*/
%>
<h3>Zombie feeds by user (user has feeds that don't exist)</h3>
<%
/*
HashMap<String, String> commonZombieFeedIds = new HashMap<String, String>();
for (User user : User.getAllUsersList()) {
	Set<UserFeed> allUserFeedCache = user.getUserFeeds();
	for(UserFeed uf : allUserFeedCache){
		String feedId = uf.getId();
		File file = new File(ConfigMap.feedsPath+File.separator+feedId+".xml");
		if(!(file.exists())){
			commonZombieFeedIds.put(feedId, "["+user.getName() + "] " +uf.getId() );
		}
	}
}

if(commonZombieFeedIds.size()>0){
	out.append("<table border='1'><tr><td align=\"center\" colspan=\"3\">Zombie feed ids</td></tr><tr align=\"center\"><td>Feed Id</td><td>User</td><td>Action</td></tr>");
	for(String feedId : commonZombieFeedIds.keySet()){
		out.append("<tr><td>"+feedId+"</td><td>"+commonZombieFeedIds.get(feedId)+"</td><td><a href=\"deleteFeed?feedId="+feedId+"&redirectTo=/HealthCheck.jsp\">Delete from user</a></td></tr>");
	}
	out.append("</table>");
}else{
	out.write("There is no any zombie feed");
}
*/
%>
<h3>Composite User Feeds with null title of user Kvaga</h3>
<table id="tableCompositeUserFeed">
	<tr>
                <th onclick="sortTable(1)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspFeedId</th>
                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspEdit</th>
    </tr>
</table>
<h3>User Feeds with null title or url of user Kvaga</h3>
<table id="tableUserFeed">
	<tr>
                <th onclick="sortTable(1)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspFeedId</th>
                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
                <th onclick="sortTable(3)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspURL</th>
                <th onclick="sortTable(4)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspEdit</th>
    </tr>
</table>

<h3>Cache usersFeeds incorrect items</h3>
<%
/*
CacheUserFeed cacheUserFeed = CacheUserFeed.getInstance();
for(String feedId : cacheUserFeed.getCompositeFeedIdsList()){
	if(feedId.toLowerCase().startsWith("composite_")){
		out.append(feedId);
		out.append("<br>");
	}
}
*/
%>
<h3>Cache compositeUsersFeeds incorrect items</h3>
<%
/*
CacheCompositeUserFeed cacheCompositeUserFeed = CacheCompositeUserFeed.getInstance();
for(String compositeFeedId : cacheCompositeUserFeed.getCompositeFeedIdsList()){
	if(!compositeFeedId.toLowerCase().startsWith("composite_")){
		out.append(compositeFeedId);
		out.append("<br>");
	}
}
*/
%>
</body>
</html>