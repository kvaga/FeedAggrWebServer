<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:include page="Header.jsp"></jsp:include>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script src="js/lib.js"></script>
<script src="js/userFile.js"></script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>User File</title>
<style>
.button{
    height:20px;
    width:80px;
}
</style>
</head>
<body>
<h2>Domains</h3>
<div id="response"></div>
<h3>Repeatable Search Items</h3>
<script>
	const urlGetRepeatableSearchPatterns='${pageContext.request.contextPath}/UserFile?command=getrssrepeatablesearchpatterns&userName=<%= request.getSession().getAttribute("login")%>';
	const urlChangeRepeatableSearchPatternsForDomain='${pageContext.request.contextPath}/UserFile?command=changerepeatableseachpatternfordomain&userName=<%= request.getSession().getAttribute("login")%>&source=${pageContext.request.contextPath}/UsersFiles.jsp';
	const urlApplyToAllCorrespondingRepeatableSearchPatternsByDomainsForDomain='${pageContext.request.contextPath}/UserFile?command=applyToAllCorrespondingDomainsRepeatableSearchPatternByDomains&userName=<%= request.getSession().getAttribute("login")%>&source=/UsersFiles.jsp';

	
	getGetJSONContentFromURL(
			urlGetRepeatableSearchPatterns,
			onErr,
			function onSuccess(obj){
				console.log(obj);
				createRepeatableSearchPatternsByDomainsTable(obj, 'divRepeatableSearchPatternsByDomains');
			} 
	);	
</script>
<div id="divRepeatableSearchPatternsByDomains"></div>
<h3>RSS Item Content Template</h3>
<script>
const urlGetRSSItemContentTemplatesByDomains='${pageContext.request.contextPath}/UserFile?command=getrssitempropertiespatternsbydomains&userName=<%= request.getSession().getAttribute("login")%>';
//const urlUserFilechangeRssItemPropertiesPatternsForDomain='${pageContext.request.contextPath}/UserFile?command=changerssetempropertiespatternsfordomain&userName=<%= request.getSession().getAttribute("login")%>';
const urlChangeRSSItemContentTemplateByDomainsForDomain='${pageContext.request.contextPath}/UserFile?command=changerssetempropertiespatternsfordomain&userName=<%= request.getSession().getAttribute("login")%>&source=${pageContext.request.contextPath}/UsersFiles.jsp';
const urlApplyToAllCorrespondingRSSItemContentTemplateByDomainsForDomain='${pageContext.request.contextPath}/UserFile?command=applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomains&userName=<%= request.getSession().getAttribute("login")%>&source=/UsersFiles.jsp';

getGetJSONContentFromURL(
		urlGetRSSItemContentTemplatesByDomains,
		onErr,
		function onSuccess(obj){
			console.log(obj);
			createRSSItemContentTemplateByDomainsTable(obj, 'divRSSItemContentTemplateByDomains');
		} 
);	
</script>
<div id="divRSSItemContentTemplateByDomains"></div>
</body>
</html>