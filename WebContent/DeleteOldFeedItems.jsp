<%@page import="ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="ru.kvaga.rss.feedaggr.objects.RSSForPrintingComparatorByTitle"%>
<%@page import="ru.kvaga.rss.feedaggrwebserver.ConfigMap"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <%@ page
	import="ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSS,
	ru.kvaga.rss.feedaggr.objects.Feed,ru.kvaga.rss.feedaggr.objects.RSSForPrintingComparatorByTitle,
	ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
	ru.kvaga.rss.feedaggrwebserver.ServerUtils,
	java.util.Collections,
	java.util.HashMap,
	java.util.Date,
	java.util.ArrayList,
	org.apache.logging.log4j.*,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap,
	ru.kvaga.rss.feedaggr.Exec
	"%>
	<%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());

	%>
    
<!DOCTYPE html>
<jsp:include page="Header.jsp"></jsp:include>

<html>
<head>
<script src="sort_table.js"></script>
<script src="lib.js"></script>

<style type="text/css">
	table, th, td {
	    border: 1px solid black;
	}
	th {
	    cursor: pointer;
	}
</style>

<script>
// Get Composite Feeds List
try{
    var xhr0 = new XMLHttpRequest();
    xhr0.onreadystatechange = function() {
        if (xhr0.readyState == 4) {
            const dataObj = JSON.parse(xhr0.responseText);
            fulfillTableCompositeFeedsList(dataObj);
        }
    }

    xhr0.open('GET', '${pageContext.request.contextPath}/CompositeFeedsList?type=json&short=true&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr0.send(null);
}catch(err){
	exception(err.message);
}

//this function appends the json data to the table 'gable'
function fulfillTableCompositeFeedsList(dataObj){
	var table = document.getElementById('tableCompositeFeedsList');
	 for(var i=0; i<dataObj.length;i++){            
	 	//console.log(dataObj[i].feedId);
	 	var tr = document.createElement('tr');
		tr.innerHTML =
			'<td>' + '<input type="checkbox" id="feed_id" name="feedId" value="'+dataObj[i].feedId+'">' + '</td>' +
			'<td>' + '<a href="${pageContext.request.contextPath}/showFeed?feedId='+dataObj[i].feedId + '">' + dataObj[i].name +'</a>' + '</td>' +
	    	'<td>' + dataObj[i].feedIds.length + '</td>';
	    	table.appendChild(tr);
	 }
}

</script>

<script>
// Get Feeds List
try{
	loadingStart();
    var xhr1 = new XMLHttpRequest();
    xhr1.onreadystatechange = function() {
        if (xhr1.readyState == 4) {
        	loadingStop();
            const dataObj = JSON.parse(xhr1.responseText);
            fulfillTableFeedsList(dataObj);
        }
    }

    xhr1.open('GET', '${pageContext.request.contextPath}/FeedsList?type=json&short=true&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr1.send(null);
}catch(err){
	exception(err.message);
}

//this function appends the json data to the table 'gable'
function fulfillTableFeedsList(dataObj){
	var table = document.getElementById('tableFeedsList');
	 for(var i=0; i<dataObj.length;i++){            
	 	//console.log(dataObj[i].feedId);
	 	var tr = document.createElement('tr');
		tr.innerHTML =
			'<td>' + '<input type="checkbox" id="feed_id" name ="feedId" value="'+dataObj[i].id+'">' + '</td>' +
			'<td>' + '<a href="${pageContext.request.contextPath}/showFeed?feedId='+dataObj[i].id + '">' + dataObj[i].userFeedTitle +'</a>' + '</td>' +
	    	'<td>' + '<a href="'+dataObj[i].userFeedUrl+'">' + dataObj[i].userFeedUrl + '</a>'+'</td>';
	    	table.appendChild(tr);
	 }
}

</script>
<meta charset="utf-8">
<title>Delete Feeds by List Short</title>
</head>
<body>


<h3>Delete Old Feeds</h3>
<!-- 
<textarea rows="20" cols="50" id="tt"></textarea>
 -->
 <form action="DeleteOldFeedItems">
 Count of days after deletion since now <input type="text" name="countOfDaysForDeletion" value="90"></input><input type="submit" name="Delete Old"></input>
 <h3>Composite Feeds List</h3>
	<table id="tableCompositeFeedsList">
		<tr>
			        <th	onClick="toggle(this)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbsp#</th>
	                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
	               	<th onclick="sortTable(3)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspCount of Feeds</th>
	    </tr>
	</table>
	<h3>Feeds List</h3>
	<table id="tableFeedsList">
		<tr>
			        <th	onClick="toggle(this)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbsp#</th>
	                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
	                <th onclick="sortTable(3)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspURL</th>
	    </tr>
	</table>
	<input type="submit" name="Delete Old"></input>
	<input type="hidden" name="redirectTo" value="DeleteOldFeedItems.jsp"></input>

</form>

</body>
</html>