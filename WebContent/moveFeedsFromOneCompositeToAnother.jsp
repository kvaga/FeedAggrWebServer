<%@page import="ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="ru.kvaga.rss.feedaggr.objects.RSSForPrintingComparatorByTitle"%>
<%@page import="ru.kvaga.rss.feedaggrwebserver.ConfigMap"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
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
//Get a list of Composite User Feed
var feedIdsFromCompositeUserFeed;
try{
    let getCompositeUserFeedListRequest = new XMLHttpRequest();
    getCompositeUserFeedListRequest.onreadystatechange = function() {
        if (getCompositeUserFeedListRequest.readyState == 4) {
        	feedIdsFromCompositeUserFeed = JSON.parse(getCompositeUserFeedListRequest.responseText);
        }
    }

    getCompositeUserFeedListRequest.open('GET', '${pageContext.request.contextPath}/GetFeedIdsFromCompositeUserFeed?userName=<%= request.getSession().getAttribute("login")%>&compositeFeedId=<%= request.getParameter("compositeFeedId")%>', true);
    getCompositeUserFeedListRequest.send(null);
}catch(err){
	//document.getElementById("tt").innerHTML=err.message;
}

// Get a list of User Feeds
try{
    let xhr1 = new XMLHttpRequest();
    xhr1.onreadystatechange = function() {
        if (xhr1.readyState == 4) {
            var dataObj = JSON.parse(xhr1.responseText);
            fulfillHeaderTable(dataObj, feedIdsFromCompositeUserFeed);
       }
    }

    xhr1.open('GET', '${pageContext.request.contextPath}/FeedsList?type=json&short=true&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr1.send(null);
}catch(err){
	document.getElementById("tt").innerHTML=err.message;
}

// fulfill header table
function fulfillHeaderTable(dataObj, feedIdsFromCompositeUserFeed){
	let table = document.getElementById('checkedTable');
	 for(let i=0; i<dataObj.length;i++){ 
	 	let tr = document.createElement('tr');
			if (compositeUserFeedContainsFeedId(feedIdsFromCompositeUserFeed, dataObj[i].id) /*compositeUserFeedsList.feedIds.includes(dataObj[i].id)*/) {
				tr.innerHTML += '<td><input type="checkbox" id="vehicle1" name="feedId" value="'+dataObj[i].id+'" ></td>'; 
				tr.innerHTML +=
				'<td>' + '<a href="${pageContext.request.contextPath}/showFeed?feedId='+dataObj[i].id + '">' + dataObj[i].userFeedTitle +'</a>' + '</td>' +
	    		'<td>' + '<a href="'+dataObj[i].userFeedUrl+'">' + dataObj[i].userFeedUrl + '</a>'+'</td>';
	    		table.appendChild(tr);
			}
	 }
	 
}

function compositeUserFeedContainsFeedId(feedIdsFromCompositeUserFeed, feedId){
	for(let i=0; i<feedIdsFromCompositeUserFeed.length;i++){
		if(feedIdsFromCompositeUserFeed[i]===feedId){
			return true;
		}
	}
	return false;
}

</script>
<script>
// Fulfill a table of Composites
try{
    var xhr1 = new XMLHttpRequest();
    xhr1.onreadystatechange = function() {
        if (xhr1.readyState == 4) {
            const dataObj = JSON.parse(xhr1.responseText);
            fulfillTableCompouseUserFeedShort(dataObj);
        }
    }

    xhr1.open('GET', '${pageContext.request.contextPath}/CompositeFeedsList?type=json&short=true&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr1.send(null);
}catch(err){
	document.getElementById("tt").innerHTML=err.message;
}

//this function appends the json data to the table 'gable'
function fulfillTableCompouseUserFeedShort(dataObj){
	let table = document.getElementById('tableComposite');
	 for(let i=0; i<dataObj.length;i++){            
	 	console.log(dataObj[i].feedId);
	 	let tr = document.createElement('tr');
		tr.innerHTML = 
			'<td>' + '<input type="checkbox" id="feed_id" name="compositeFeedIdTo" value="'+dataObj[i].feedId+'">' + '</td>' +
			'<td>' + '<a href="${pageContext.request.contextPath}/showFeed?feedId='+dataObj[i].feedId+'">' + dataObj[i].name + '</a>' + '</td>' +
	    	'<td>' + dataObj[i].feedIds.length + '</td>';
	    	table.appendChild(tr);
	 }
}

</script>
<script>
function fixedEncodeURIComponent(str) {
	  return encodeURIComponent(str).replace(/[!'()*]/g, function(c) {
	    return '%' + c.charCodeAt(0).toString(16);
	  });
	}
</script>
<meta charset="utf-8">
<title>Move Feeds From One Composite To Another</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>


<h2>Move Feeds From '<%= request.getParameter("compositeFeedTitle")%>' Composite To Another</h3>
<form action="MoveFeedsFromCompositeOne2Another">

<h3>Feeds of '<%= request.getParameter("compositeFeedTitle")%>'</h3>
<table id="checkedTable">
		<tr>
	                <th onclick="sortTable(1)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbsp#</th>
	                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
	                <th onclick="sortTable(3)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspURL</th>
	    </tr>
</table>
<h3>List of compouses where to put</h3>
<table id="tableComposite">
	<tr>
                <th onclick="sortTable(1)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbsp#</th>
                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
               <th onclick="sortTable(3, true)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspCount of Feeds</th>
    </tr>
</table>
	<input type="submit" name="Move">
	<input type="hidden" name="compositeFeedIdFrom" value="<%= request.getParameter("compositeFeedId")%>">
	<input type="hidden" name="compositeFeedTitleFrom" value="<%= request.getParameter("compositeFeedTitle")%>">
	<input type="hidden" name="userName" value="<%= request.getSession().getAttribute("login")%>">
</form>
<p id="tt"></p>
</body>
</html>
