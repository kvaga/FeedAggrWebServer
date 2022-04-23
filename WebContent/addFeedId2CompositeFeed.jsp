<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:include page="Header.jsp"></jsp:include>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<style type="text/css">
	table, th, td {
	    border: 1px solid black;
	}
	th {
	    cursor: pointer;
	}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
<%
	StringBuilder sbHidden = new StringBuilder();
	if(request.getParameterValues("feedId")!=null && request.getParameterValues("feedId").length>0){
		for(String feedId: request.getParameterValues("feedId")){
			sbHidden.append("<input type=\"hidden\" name=\"feedId\" value=\""+feedId+"\">");
		}
	}	
%>
<script>
try{
	loadingStart();
    var xhr1 = new XMLHttpRequest();
    xhr1.onreadystatechange = function() {
        if (xhr1.readyState == 4) {
            loadingStop();
            const dataObj = JSON.parse(xhr1.responseText);
            fulfillTableCompouseUserFeedShort(dataObj);
        }
    }

    xhr1.open('GET', '${pageContext.request.contextPath}/CompositeFeedsList?type=json&short=true&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr1.send(null);
    loadingStop();
}catch(err){
	exception(err.message);
}
//this function appends the json data to the table 'gable'
function fulfillTableCompouseUserFeedShort(dataObj){
	var table = document.getElementById('tableCompositeFeeds');
	 for(var i=0; i<dataObj.length;i++){            
	 	console.log(dataObj[i]);
	 	var tr = document.createElement('tr');
		tr.innerHTML = ''+
		''+
		'<td>' + '<a href="${pageContext.request.contextPath}/showFeed?feedId='+dataObj[i].feedId+'">' + dataObj[i].name + '</a></td>' +
    	'<td><form action="CompositeFeeds"><input type="submit" value="Add"></input>' + 
    	'<input type="hidden" name="compositeFeedId" value="'+dataObj[i].feedId+'"/>'+
    	'<input type="hidden" name="command" value="appendNewUserFeeds"/><%= sbHidden.toString()%></form></td>'
			;
		table.appendChild(tr);
	 } 
}


</script>
</head>
<body>





<table id="tableCompositeFeeds">
	<tr>
                <th onclick="sortTable(1)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
                <th onclick="sortTable(2)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspAction</th>
    </tr>
</table>

</body>
</html>