<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
	ru.kvaga.rss.feedaggr.Exec,
	ru.kvaga.rss.feedaggrwebserver.servlets.CompositeFeedTotalInfo
	"%>
	    
<jsp:include page="Header.jsp"></jsp:include>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />


	<script>
	/*
try{
	 let xhr = new XMLHttpRequest();
	    xhr.onreadystatechange = function() {
	        if (xhr.readyState == 4) {
	            let data1 = JSON && JSON.parse(xhr.responseText);

	            const table = document.getElementById("tBody");
	        
	        	for(let i=0; i< data1.length; i++){
	        		let item = data1[i];
	                document.getElementById("story").innerHTML+=item.name;

	                //Object.keys(user)
	                
	                
					//for(var key in user) {
					//    if(object.hasOwnProperty(key)) {
					//        var property = object[key];
					//        document.getElementById("story").innerHTML+=property;
					//        
					//    }
					//}
	                
	                    let row = table.insertRow();
		        		
	   		        	let name = row.insertCell(0);
	   		        	name.innerHTML = item.name;
	   		        	
	   		        	//let Delete = row.insertCell(1);
	   		        	//Delete.innerHTML='Delete';
	   		        	
	   					let Export = row.insertCell(2);
	   					Export.innerHTML='<a href="ExportCompositeFeed?userName=<%= request.getSession().getAttribute("login")%>&amp;compositeFeedId='+item.feedId+'&amp;source=/CompositeFeedsExport.jsp">Export</a>';
	   					
	   					//let LastUpdated = row.insertCell(3);
	   					//LastUpdated.innerHTML=item.lastUpdated;
	   					
	   					//let CountOfItems = row.insertCell(4);
	   					//CountOfItems.innerHTML=item.countOfItems;
	   					
	   					//let Size = row.insertCell(5);
	   					//Size.innerHTML=item.sizeMb;
	   					
	   					//let OldestPubDate=row.insertCell(6);
	   					//OldestPubDate.innerHTML=item.oldestPubDate;
	   					
	   					//let NewestPubDate=row.insertCell(7);
	   					//NewestPubDate.innerHTML=item.newestPubDate;
		        }
                document.getElementById("story").innerHTML+=JSON.stringify(data1);

	        }
	    }
	
    xhr.open('GET', '${pageContext.request.contextPath}/CompositeFeedsList?type=json&userName=<%= request.getSession().getAttribute("login")%>', true);
    xhr.send(null);
}catch(err){
	exception(err.message);

}

*/
</script>

<title>User File Import</title>
</head>
<body>

<%
String userName=(String)request.getSession().getAttribute("login");
//if(request.getAttribute("compositeFeedList")!=null){
	//request.setAttribute("compositeFeedList", request.getAttribute("compositeFeedList"));
%>

<h1>User File import</h1>
Upload user's file for the user <%= session.getAttribute("login") %>

<form method="post" action="ImportUserFile" enctype="multipart/form-data">
    Choose a file: <input type="file" name="multiPartServlet" />
    <input type="hidden" name="redirectTo" value="/UserFileImport.jsp"/>
    <input type="submit" value="Upload" />
</form>

<c:out value="${ResponseResult}"></c:out>
<br/>
<c:out value="${Exception}"></c:out>
----
<table id="usersTable" border="1">
	<thead>
	    <tr>
			<th>Name</th>
			<!--  <th>Delete</th> -->
			<th>Import</th>
			<!--  <th>Last updated</th>
			<th>Count of items</th>
			<th>Size, mb</th>
			<th>Oldest PubDate</th>
			<th>Newest PubDate</th> -->
		</tr>
	  </thead>
	  <tbody id="tBody">
	  	<tr>
	  		<td><%=userName %></td>
	  		<td><a href="ExportUserFile?userName=<%= request.getSession().getAttribute("login")%>&amp;source=/UserFileExportExport.jsp">Import</a></td>
	  	</tr>
	  </tbody>
</table>

</body>
</html>