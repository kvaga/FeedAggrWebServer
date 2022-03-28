<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

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
	document.getElementById("compositeFeedList1").innerHTML=err.message;

}

*/
</script>

<title>User File Export</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>

<%
String userName=(String)request.getSession().getAttribute("login");
//if(request.getAttribute("compositeFeedList")!=null){
	//request.setAttribute("compositeFeedList", request.getAttribute("compositeFeedList"));
%>

<h1>User File Export</h1>
<table id="usersTable" border="1">
	<thead>
	    <tr>
			<th>Name</th>
			<!--  <th>Delete</th> -->
			<th>Export</th>
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
	  		<td><a href="ExportUserFile?userName=<%= request.getSession().getAttribute("login")%>&amp;source=/UserFileExportExport.jsp">Export</a></td>
	  	</tr>
	  </tbody>
</table>


<%-- 
<c:if test="${compositeFeedList.size() >0 }">
	<table border="1">
		<tr>
			<th>Name</th>
			<th>Delete</th>
			<th>Export</th>
			<th>Last updated</th>
			<th>Count of items</th>
			<th>Size, mb</th>
			<th>Oldest PubDate</th>
			<th>Newest PubDate</th>
		</tr>
</c:if>


<c:forEach items="${compositeFeedList}" var="item">
	<tr>
		<td>${item.getName()}</td>
		<td>Delete</td>
		<td><a href="ExportCompositeFeed?userName=<%= userName%>&amp;compositeFeedId=${item.getFeedId()}&amp;source=/CompositeFeedsExport.jsp">Export</a></td>
		<td>${item.getLastUpdated()}</td>
		<td>${item.getCountOfItems()}</td>
		<td>${item.getSizeMb()}</td>
		<td>${item.getOldestPubDate()}</td>
		<td>${item.getNewestPubDate()}</td>
	</tr>
</c:forEach>
<c:if test="${compositeFeedList.size() >0 }">
	</table>
</c:if>

<%
}else{
	if(request.getAttribute("Exception")!=null){
		Exception e = (Exception)request.getAttribute("responseResultException");
		out.write(Exec.getHTMLFailText(e));
	}
}
%>
--%>
</body>
</html>