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
<html>
<head>
<script src="sort_table.js"></script>
<script>
function fixedEncodeURIComponent(str) {
	  return encodeURIComponent(str).replace(/[!'()*]/g, function(c) {
	    return '%' + c.charCodeAt(0).toString(16);
	  });
	}

/*
function htmlEntities(str) {
    return String(str)
    			.replace(/&/g, '&amp;')
    			.replace(/</g, '&lt;')
    			.replace(/>/g, '&gt;')
    			.replace(/"/g, '&quot;')
    			.replace(/[/g, '&#91;')
    	    	.replace(/]/g, '&#93;')

    			;
}
*/
</script>


<style type="text/css">
	table, th, td {
	    border: 1px solid black;
	}
	th {
	    cursor: pointer;
	}
</style>
<script>
try{
    var xhr1 = new XMLHttpRequest();
    xhr1.onreadystatechange = function() {
        if (xhr1.readyState == 4) {
            //var data = JSON and JSON.parse(xhr.responseText);
            const dataObj = JSON.parse(xhr1.responseText);
           //alert(data);
           //append_json(dataObj);
           // const dataObj = JSON.parse(xhr.responseText);
            //alert(data);
            fulfillTableCompouseUserFeedShort(dataObj);
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
        	//document.getElementById("tt").innerHTML=        											xhr1.responseText;

        											//dataObj;

        }
    }

    xhr1.open('GET', '${pageContext.request.contextPath}/CompositeFeedsList?type=json&short=true&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr1.send(null);
}catch(err){
	document.getElementById("tt").innerHTML=err.message;
}

//this function appends the json data to the table 'gable'
function fulfillTableCompouseUserFeedShort(dataObj){
	var table = document.getElementById('table');
	 for(var i=0; i<dataObj.length;i++){            
	 	console.log(dataObj[i].feedId);
	 	var tr = document.createElement('tr');
		tr.innerHTML = 
			'<td>' + '<a href="${pageContext.request.contextPath}/showFeed?feedId='+dataObj[i].feedId+'">' + dataObj[i].name + '</a>' + '</td>' +
	    	'<td>' + '<a href="${pageContext.request.contextPath}/deleteFeed?redirectTo=/CompositeFeedsListShort.jsp&feedId='+dataObj[i].feedId+'">Delete' + '</a>'+'</td>' +
	    	'<td>' + '<a href="${pageContext.request.contextPath}/mergeRSS.jsp?feedTitle='+fixedEncodeURIComponent(dataObj[i].name)+'&feedId='+dataObj[i].feedId+'"">Edit' + '</a>'+'</td>' +
	    	'<td>' + dataObj[i].feedIds.length + '</td>';
	    	table.appendChild(tr);
	 }
    /*
    for(var i=0; i<dataObj.length;i++){
        var tr = document.createElement('tr');
    	tr.innerHTML = '<td>' + dataObj[i].name + '</td>' +
        '<td>' + dataObj[i].name + '</td>' +
        '<td>' + dataObj[i].feedId + '</td>' +
        '<td>' + dataObj[i].countOfUserFeeds + '</td>';
        table.appendChild(tr);
    }
    */
    
    /*
    for(const object in dataObj){
	   
    	//dataObj.forEach(function(object){
	        var tr = document.createElement('tr');
	       
	        tr.innerHTML = '<td>' + object + '</td>' +
	        '<td>' + object.name + '</td>' +
	        '<td>' + object.feedId + '</td>' +
	        '<td>' + object.countOfUserFeeds + '</td>';
	        table.appendChild(tr);
	    //});
	   
    }
    */
}

</script>
<meta charset="utf-8">
<title>Composite Feeds Short List</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>


<h3>Composite feeds Short Info</h3>
<!-- 
<textarea rows="20" cols="50" id="tt"></textarea>
 -->
<table id="table">
	<tr>
                <th onclick="sortTable(1)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspDelete</th>
                <th onclick="sortTable(3)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspEdit</th>
                <th onclick="sortTable(4, true)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspCount of Feeds</th>
    </tr>
</table>
</body>
</html>