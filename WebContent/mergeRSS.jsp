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
<style type="text/css">
	table, th, td {
	    border: 1px solid black;
	}
	th {
	    cursor: pointer;
	}
</style>
<script>
// Get a list of Composite User Feed
var feedIdsFromCompositeUserFeed;
var feedIdParameter=<%= request.getParameter("feedId")==null?null:"'"+request.getParameter("feedId")+"'"%>;
if(feedIdParameter){
	try{
		loadingStart();
	    var getCompositeUserFeedListRequest = new XMLHttpRequest();
	    getCompositeUserFeedListRequest.onreadystatechange = function() {
	        if (getCompositeUserFeedListRequest.readyState == 4) {
	        	loadingStop();
	        	feedIdsFromCompositeUserFeed = JSON.parse(getCompositeUserFeedListRequest.responseText);
	        	//document.getElementById("tt").innerHTML=
	        											//dataObj;
	        		//getCompositeUserFeedListRequest.responseText;
	        }
	    }
	
	    getCompositeUserFeedListRequest.open('GET', '${pageContext.request.contextPath}/GetFeedIdsFromCompositeUserFeed?userName=<%= request.getSession().getAttribute("login")%>&compositeFeedId=<%= request.getParameter("feedId")%>', true);
	    getCompositeUserFeedListRequest.send(null);
	}catch(err){
		exception(err.message);
	}
}
// Get a list of User Feeds
try{
	loadingStart();
    var xhr1 = new XMLHttpRequest();
    xhr1.onreadystatechange = function() {
        if (xhr1.readyState == 4) {
        	loadingStop();
            var dataObj = JSON.parse(xhr1.responseText);
            fulfillHeaderTable(dataObj, feedIdsFromCompositeUserFeed);
            fulfillTableUserFeeds(dataObj, feedIdsFromCompositeUserFeed);
           	/*
            var table = document.getElementById('gable');
			 for(var i=0; i<dataObj.length;i++){            
			 	console.log(dataObj[i].feedId);
			 	var tr = document.createElement('tr');
				tr.innerHTML = 
					'<td>' + dataObj[i].name + '</td>' +
			    	'<td>' + dataObj[i].name + '</td>' +
			    	'<td>' + dataObj[i].feedId + '</td>' +
			    	'<td>' + dataObj[i].countOfUserFeeds + '</td>';
			    	table.appendChild(tr);
			 }
			 */
           //append_json(xhr.responseText);
        	//document.getElementById("tt").innerHTML=
        											//dataObj;
        		//									xhr1.responseText;

        }
    }

    xhr1.open('GET', '${pageContext.request.contextPath}/FeedsList?type=json&short=true&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr1.send(null);
}catch(err){
	exception(err.message);
}

// fulfill header table
function fulfillHeaderTable(dataObj, feedIdsFromCompositeUserFeed){
	let table = document.getElementById('checkedTable');
	 for(let i=0; i<dataObj.length;i++){ 
		//console.log('res: ' + feedIdsFromCompositeUserFeed);
	 	let tr = document.createElement('tr');
			if (feedIdParameter && compositeUserFeedContainsFeedId(feedIdsFromCompositeUserFeed, dataObj[i].id) /*compositeUserFeedsList.feedIds.includes(dataObj[i].id)*/) {
				tr.innerHTML += '<td><input type="checkbox" id="vehicle1" disabled="disabled" value="'+dataObj[i].id+'" checked></td>'; 
				tr.innerHTML +=
				//'<td>' + '<input type="checkbox" id="vehicle1" name="feedId" value="'+dataObj[i].id+'">' + '</td>' + 
				'<td>' + '<a href="${pageContext.request.contextPath}/showFeed?feedId='+dataObj[i].id + '">' + dataObj[i].userFeedTitle +'</a>' + '</td>' +
	    		'<td>' + '<a href="'+dataObj[i].userFeedUrl+'">' + dataObj[i].userFeedUrl + '</a>'+'</td>';
	    		table.appendChild(tr);
			}
	 }
	 
}

//fulfill main tble
function fulfillTableUserFeeds(dataObj,feedIdsFromCompositeUserFeed){
	let table = document.getElementById('table');
 	//console.log(compositeUserFeedsList);

	 for(let i=0; i<dataObj.length;i++){ 
		 //console.log('res: ' + feedIdsFromCompositeUserFeed);
	 	let tr = document.createElement('tr');
	 	let listOfCompositeFeedsTitles='';
		for (let _i = 0, keys = Object.keys(dataObj[i].compositeFeedsMap), _ii = keys.length; _i < _ii; _i++) {
 			 //console.log('key : ' + keys[_i] + ' val : ' + dataObj[i].compositeFeedsMap[keys[_i]]);
			listOfCompositeFeedsTitles+=dataObj[i].compositeFeedsMap[keys[_i]] + '<br>';
		}
		tr.innerHTML += '<td>' + Object.keys(dataObj[i].compositeFeedsMap).length + '</td>';

	 		if (feedIdParameter && compositeUserFeedContainsFeedId(feedIdsFromCompositeUserFeed, dataObj[i].id) /*compositeUserFeedsList.feedIds.includes(dataObj[i].id)*/) {
				tr.innerHTML += '<td><input type="checkbox" id="vehicle1" name="feedId" value="'+dataObj[i].id+'" checked></td>'; 
			}else{
				tr.innerHTML += '<td><input type="checkbox" id="vehicle1" name="feedId" value="'+dataObj[i].id+'"></td>'; 
			}
	 		
			tr.innerHTML +=
			//'<td>' + '<input type="checkbox" id="vehicle1" name="feedId" value="'+dataObj[i].id+'">' + '</td>' + 
			'<td>' + '<a href="${pageContext.request.contextPath}/showFeed?feedId='+dataObj[i].id + '">' + dataObj[i].userFeedTitle +'</a>' + '</td>' +
	    	'<td>' + '<a href="'+dataObj[i].userFeedUrl+'">' + dataObj[i].userFeedUrl + '</a>'+'</td>'+
	    	'<td>' + listOfCompositeFeedsTitles + '</td>';

	    	table.appendChild(tr);
	 }
	 
}

function compositeUserFeedContainsFeedId(feedIdsFromCompositeUserFeed, feedId){
	for(let i=0; i<feedIdsFromCompositeUserFeed.length;i++){
		//console.log(feedIdsFromCompositeUserFeed[i] + ' vs ' + feedId);
		if(feedIdsFromCompositeUserFeed[i]===feedId){
			//console.log('yes: ' + feedIdsFromCompositeUserFeed[i] + ' ' + feedId);
			return true;
		}
	}
	return false;
}

</script>
<meta charset="utf-8">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<title>Merge RSS</title>
</head>
<body>


<h2>Feeds List Short Info for '<%= request.getParameter("feedTitle")%>'</h2>
<form action="mergeRSS" method="POST">

Title of composite RSS: <input type="text" name="compositeRSSTitle" value="<%= request.getParameter("feedTitle")==null?"":request.getParameter("feedTitle") %>"></input>

<!-- 
<textarea rows="20" cols="50" id="tt"></textarea>
 -->
<h3>Checked</h3>
<table id="checkedTable">
		<tr>
	                <th onclick="sortTable(1)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbsp#</th>
	                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
	                <th onclick="sortTable(3)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspURL</th>	                
	    </tr>
</table>
<h3>All Other</h3>
	<input type="submit" name="Merge">

	<table id="table">
		<tr>
					<th onclick="sortTable(1, true)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspCUF</th>
	                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbsp#</th>
	                <th onclick="sortTable(3)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
	                <th onclick="sortTable(4)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspURL</th>
	                <th onclick="sortTable(5)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspComposite User Feeds Titles</th>
	    </tr>
	</table>
	
	<c:if test="${not empty param.feedId}">
		<input type="hidden" name="compositeFeedID" value="<%= request.getParameter("feedId")%>">
    </c:if>
		<input type="submit" name="Merge">
	
</form>
<p id="tt"></p>
</body>
</html>
