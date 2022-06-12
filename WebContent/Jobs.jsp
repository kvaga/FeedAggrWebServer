<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:include page="Header.jsp"></jsp:include>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Jobs</title>
</head>
<body>
<script>
try{
    var xhr1 = new XMLHttpRequest();
    xhr1.onreadystatechange = function() {
        if (xhr1.readyState == 4) {

            const dataObj = JSON.parse(xhr1.responseText);
            //fulfillTableCompouseUserFeedShort(dataObj);
            let status='unknown';
            console.log(dataObj);
            if(dataObj==false){
            	status='ENABLED';
            }else if(dataObj==true){
            	status='DISABLED';
            }
        	document.getElementById("jobsStatus").innerHTML=
        											'Jobs status: ' + status;
        											//xhr1.responseText;
        }
    }

    xhr1.open('GET', '${pageContext.request.contextPath}/PauseJobs?command=status', true);
    xhr1.send(null);
}catch(err){
	exception(err.message);
}

function pause(){
	try{
	    var xhr1 = new XMLHttpRequest();
	    xhr1.onreadystatechange = function() {
	        if (xhr1.readyState == 4) {

	            const dataObj = JSON.parse(xhr1.responseText);
	            //fulfillTableCompouseUserFeedShort(dataObj);
	            
	        	document.getElementById("response").innerHTML=
	        											//'Jobs status: ' + status;
	        											xhr1.responseText;
	        }
	    }

	    xhr1.open('GET', '${pageContext.request.contextPath}/PauseJobs?command=pause', true);
	    xhr1.send(null);
	}catch(err){
		exception(err.message);
	}

}
function enable(){
	try{
	    var xhr1 = new XMLHttpRequest();
	    xhr1.onreadystatechange = function() {
	        if (xhr1.readyState == 4) {

	            const dataObj = JSON.parse(xhr1.responseText);
	            //fulfillTableCompouseUserFeedShort(dataObj);
	            
	        	document.getElementById("response").innerHTML=
	        											//'Jobs status: ' + status;
	        											xhr1.responseText;
	        }
	    }

	    xhr1.open('GET', '${pageContext.request.contextPath}/PauseJobs?command=enable', true);
	    xhr1.send(null);
	}catch(err){
		exception(err.message);
	}

}
</script>
<div id='jobsStatus'></div>
<div id='response'></div>
<form >
	<input type='submit' value='pause' onClick='pause()'/>
</form>

<form>
	<input type='submit' value='enable' onClick='enable()'/>
</form>
</body>
</html>