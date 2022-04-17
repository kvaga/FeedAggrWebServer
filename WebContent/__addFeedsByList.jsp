<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
<style type="text/css">
	table, th, td {
	    border: 1px solid black;
	}
	th {
	    cursor: pointer;
	}
</style>
<script type="text/javascript" src="js/lib.js"></script>

<script>
	function addFeedsByList(){
		try{
			let listOfURLs = document.getElementById('listOfURLs').value;
			sendR(listOfURLs);
		}catch(err){
			exception('exception', err.message);
		}
	}
	
	function sendR(listUrls){
		try{
		    var xhr1 = new XMLHttpRequest();
		    xhr1.onreadystatechange = function() {
		        if (xhr1.readyState == 4) {
		            const dataObj = JSON.parse(xhr1.responseText);
		            fulfillTableCompouseUserFeedShort(dataObj);
		        	/*
		        		document.getElementById("tt").innerHTML=
		        											//dataObj;
		        											xhr1.responseText;
		        	*/
		        }
		    }

		    xhr1.open('GET', '${pageContext.request.contextPath}/AddFeedsByUrlsList?listUrls='+listUrls+'&userName=<%= request.getSession().getAttribute("login")%>', true);
		    xhr1.send(null);
		}catch(err){
			exception('exception', err.message);
		}
	
	}
	
	//this function appends the json data to the table 'gable'
	function fulfillTableCompouseUserFeedShort(dataObj){
		try{
			let table = document.getElementById('tableOfNewlyAddedFeeds');
			deleteTBody(table);
			
			for(var i=0; i<dataObj.length;i++){            
		 	console.log(dataObj[i]);
		 	var tr = document.createElement('tr');
		 	//tr.innerHTML = '';
		 	if (dataObj[i].size > 0) {
		 		tr.innerHTML += 
					'<td><input type="checkbox" id="feed_id" name="feedId" value="'+dataObj[i].feedId+'" ></td>'+
					'<td>' + dataObj[i].feedTitle + '</td>'+
					'<td>' + dataObj[i].url + '</td>'+
					'<td>' + dataObj[i].size + '</td>'+
					'<td><a href="__addFeedId2CompositeFeed.jsp?feedId=' + dataObj[i].feedId + '">Add to composite</a>'+'</td>';
			} else {
				tr.innerHTML += 
					'<td><input type="checkbox" id="feed_id" name="feedId" value="null" disabled></td>'+
					'<td>' + error_text(dataObj[i].feedTitle) + '</td>'+
					'<td>' + dataObj[i].url + '</td>'+
					'<td>' + dataObj[i].size + '</td>'+
					'<td>---</td>';
			}
		 	table.appendChild(tr);
		 }
		}catch(err){
			exception('exception', err.message);
		}
	}

</script>
<style type="text/css">
	table, th, td {
	    border: 1px solid black;
	}
	th {
	    cursor: pointer;
	}
</style>
</head>
<body>
	<jsp:include page="Header.jsp"></jsp:include>
	<%
		if (request.getParameter("listOfURLs") == null) {
			request.getSession().removeAttribute("listOfURLs");
			//log.debug("Session's attribute [listOfURLs] was removed");
		}else{
			request.getSession().setAttribute("listOfURLs", request.getParameter("listOfURLs"));
			//log.debug("Session's attribute [listOfURLs] set to ["+request.getParameter("listOfURLs")+"]");
		}
	%>
	<form>
		Specify the list of URLs (each URL on next line)
		<textarea id="listOfURLs" rows="12" cols="120" name="listOfURLs"><%=request.getSession().getAttribute("listOfURLs") == null ? ""
					: (String) request.getSession().getAttribute("listOfURLs")%></textarea>
		<br /> <input type="button" value="Add" onclick="addFeedsByList()"/>
	</form>
	
	<form action="__addFeedId2CompositeFeed.jsp">
		<table id="tableOfNewlyAddedFeeds">
			<tr>
				        <th	onClick="toggle(this)"><span class="glyphicon glyphicon-sort"></span>&nbsp&nbsp#</th>
		                <th onclick=""><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspName</th>
		                <th onclick=""><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspURL</th>
		              	<th onclick=""><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspSize</th>
		             	<th onclick=""><span class="glyphicon glyphicon-sort"></span>&nbsp&nbspAdd to composite</th>
		    </tr>
		</table>
		<input type="submit" name="Добавить выбранные" value="Добавить выбранные"/>
	</form>
	
	<div id="exception"></div>
</body>
</html>