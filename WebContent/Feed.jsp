<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtilsConcurrent"%>
<%@page import="java.util.Enumeration"%>
<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
ru.kvaga.rss.feedaggr.Exec,
org.apache.logging.log4j.*,
ru.kvaga.rss.feedaggrwebserver.ConfigMap,
java.io.File,
ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
ru.kvaga.rss.feedaggrwebserver.objects.user.User,
ru.kvaga.rss.feedaggr.objects.RSS,
ru.kvaga.rss.feedaggr.objects.Channel,
ru.kvaga.rss.feedaggr.objects.Feed
"%>
<%
final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());
%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<%
if(request.getParameter("action")!=null && request.getParameter("action").equals("edit")){
	ServerUtils.clearSessionFromFeedAttributes(request);
	if(request.getParameter("feedId")!=null){
		request.getSession().setAttribute("feedId", request.getParameter("feedId"));
		File xmlFile = new File(ConfigMap.feedsPath.getAbsoluteFile() + "/" + request.getSession().getAttribute("feedId") + ".xml");
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + "/" + request.getSession().getAttribute("login") + ".xml");
		//User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
		User user = User.getXMLObjectFromXMLFile(userFile);
		//RSS rss = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(xmlFile, new RSS());
		RSS rss = RSS.getRSSObjectFromXMLFile(xmlFile);
		request.getSession().setAttribute("feedTitle", rss.getChannel().getTitle());
		request.getSession().setAttribute("url", rss.getChannel().getLink());
		request.getSession().setAttribute("repeatableSearchPattern",user.getRepeatableSearchPatternByFeedId((String)request.getSession().getAttribute("feedId")));
		request.getSession().setAttribute("itemTitleTemplate", user.getItemTitleTemplateByFeedId((String)request.getSession().getAttribute("feedId")));
		request.getSession().setAttribute("itemLinkTemplate", user.getItemLinkTemplateByFeedId((String)request.getSession().getAttribute("feedId")));
		request.getSession().setAttribute("itemContentTemplate", user.getItemContentTemplateByFeedId((String)request.getSession().getAttribute("feedId")));
		request.getSession().setAttribute("filterWords", user.getFilterWordsByFeedId((String)request.getSession().getAttribute("feedId")));
		request.getSession().setAttribute("durationUpdate", user.getDurationInMillisForUpdateByFeedId((String)request.getSession().getAttribute("feedId")));
		request.getSession().setAttribute("feedDescription", rss.getChannel().getDescription());

	}
}
%>
<html>
<head>
<meta charset="utf-8">
<title>Editing feed: <%= request.getSession().getAttribute("feedTitle")==null?"":request.getSession().getAttribute("feedTitle")%></title>

</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>


<%

/*
Enumeration enumParameters = request.getParameterNames();
out.print("Список параметров:<br>");
while(enumParameters.hasMoreElements()){
	String parName=(String)enumParameters.nextElement();
	out.print("parameter["+parName+"]: " + request.getParameter(parName)+"<br>");
}
out.print("-------------<br>");

Enumeration enumAttributes = request.getSession().getAttributeNames();
out.print("Список атрибутов сессии:<br>");
while(enumAttributes.hasMoreElements()){
	String parName=(String)enumAttributes.nextElement();
	if(parName.equals("responseHtmlBody")) continue;
	out.print("attribute["+parName+"]: " + request.getSession().getAttribute(parName)+"<br>");
}
out.print("-------------<br>");
*/


if(request.getParameter("url")!=null){
	request.getSession().setAttribute("url",request.getParameter("url"));
}

if(request.getParameter("feedDescription")!=null){
	request.getSession().setAttribute("feedDescription",request.getParameter("feedDescription"));
}

if(request.getParameter("feedId")!=null){
	request.getSession().setAttribute("feedId",request.getParameter("feedId"));
}
if(request.getParameter("feedTitle")!=null){
	request.getSession().setAttribute("feedTitle", request.getParameter("feedTitle"));
}

if(request.getParameter("filterWords") != null){
	request.getSession().setAttribute("filterWords", request.getParameter("filterWords"));
}

if(request.getParameter("durationUpdate") != null){
	request.getSession().setAttribute("durationUpdate", request.getParameter("durationUpdate"));
}

if(request.getParameter("repeatableSearchPattern")!=null){
	request.getSession().setAttribute("repeatableSearchPattern",request.getParameter("repeatableSearchPattern"));
}

if(request.getParameter("itemTitleTemplate")!=null){
	request.getSession().setAttribute("itemTitleTemplate",request.getParameter("itemTitleTemplate"));
}
if(request.getParameter("itemLinkTemplate")!=null){
	request.getSession().setAttribute("itemLinkTemplate",request.getParameter("itemLinkTemplate"));
}
if(request.getParameter("itemContentTemplate")!=null){
	request.getSession().setAttribute("itemContentTemplate",request.getParameter("itemContentTemplate"));
}
if(request.getParameter("responseHtmlBody") != null){
	request.getSession().setAttribute("responseHtmlBody", request.getParameter("responseHtmlBody"));
}

String enableStep4FeedPreview=request.getParameter("enableStep4FeedPreview");
String feedId=(String)request.getSession().getAttribute("feedId");
//String feedTitle=(String)request.getSession().getAttribute("feedTitle");

String feedTitle=request.getSession().getAttribute("feedTitle")==null?"":(String)request.getSession().getAttribute("feedTitle");

//String responseHtmlBody = request.getParameter("responseHtmlBody");
String responseHtmlBody = (String)request.getSession().getAttribute("responseHtmlBody");
String repeatableSearchPattern=(String)request.getSession().getAttribute("repeatableSearchPattern");
String feedDescription=(String)request.getSession().getAttribute("feedDescription");
String itemTitleTemplate	=(String)request.getSession().getAttribute("itemTitleTemplate");
String itemLinkTemplate		=(String)request.getSession().getAttribute("itemLinkTemplate");
String itemContentTemplate	=(String)request.getSession().getAttribute("itemContentTemplate");

if(request.getParameter("action")!=null && request.getParameter("action").equals("new")){
	feedId=ServerUtils.getNewFeedId();
	request.getSession().setAttribute("feedId",feedId);
	ServerUtils.clearSessionFromFeedAttributes(request);
    
	enableStep4FeedPreview=null;
	responseHtmlBody=null;
	repeatableSearchPattern=null;
	//feedDescription=null;
	itemTitleTemplate=null;
	itemLinkTemplate=null;
	itemContentTemplate=null;
	
	feedTitle="<New Feed>";
	log.debug("Parameters were cleared");
}

String url= (String)request.getSession().getAttribute("url");


%>

<h1>Editing feed: <%= feedTitle %></h1>
<hr>
	<input id="auth" type="hidden" value="006eb65ef0494d6e0eb15a429a3e0313">

			<p>
				Edit your feed. Required fields are marked with asterisk (<span
					class="bullet">*</span>).
			</p>
			<p>
				Click <a class="help" title="Open help in separate window"
					href="javascript:help('index')">?</a> next to each parameter for
				help.
			</p>

			<!-- Step 1 -->



			<h2 style="margin-bottom: 0;">Step 1. Specify source page address (URL)</h2>
			<form method="post" action="Feed.jsp">
				<input id="feedId" name="feedId" type="hidden" value="<%=feedId%>"> 
			
				<table class="w100">
					<tbody>
						<tr>
							<td class="w100" style="vertical-align: bottom">
								<p>
									Address<span class="bullet">*</span>: <span class="small">(<a
										title="Open typed address in new window"
										href="javascript:openInBrowser();">Open in browser</a>) <a
										class="small help" title="Help on this option"
										href="javascript:help('url')">?</a></span><br> 
										<input id="url" name="url" style="width: 490px" size="64" type="text" class="text" maxlength="2048" value="<%= request.getSession().getAttribute("url")==null?"":request.getSession().getAttribute("url")%>">
								</p>
							</td>
							<td style="padding-left: 10px; vertical-align: bottom">
								<p>
									Encoding: <a class="small help" title="Help on this option"
										href="javascript:help('encoding')">?</a><br> <input
										id="encoding" class="text" style="width: 150px" size="20"
										maxlength="32" value="utf-8">
								</p>
							</td>
						</tr>
					</tbody>
				</table>

				<table class="w100">
					<tbody>
						<tr>
							
							<td><input class="button" type="submit" value="Reload"></td>
						</tr>
					</tbody>
				</table>
			</form>
		


	
	
	<%if (url != null) {	
		try{
			//responseHtmlBody = ServerUtils.convertStringToUTF8(Exec.getURLContent(url));
			
			url = (url.contains("youtube.com") && !url.contains("youtube.com/feeds/videos.xml")) ? Exec.getYoutubeFeedURL(url): url;
			url = (url.startsWith("https://habr.com/ru/rss") || url.startsWith("https://habr.com/rss") || url.startsWith("https://habrahabr.com/rss")|| url.startsWith("https://habrahabr.ru/rss")) ? url : Exec.getHabrFeedURL(url);
			request.getSession().setAttribute("url",url);
			if (url==null){
				throw new Exception("Can't find feed channel url");
			}
			// responseHtmlBody = Exec.getURLContent(url);
			responseHtmlBody = ServerUtilsConcurrent.getInstance().getURLContent(url);
		}catch(Exception e){
			log.error("Exception", e);
			out.print("<font color=red>Couldn't get content from the URL</font>");
			//response.sendRedirect("Feed.jsp");
		}
		try{
			if(request.getSession().getAttribute("feedTitle")==null)
				request.getSession().setAttribute("feedTitle", Exec.getTitleFromHtmlBody(responseHtmlBody));
			feedTitle=(String)request.getSession().getAttribute("feedTitle");
		}catch(Exception e){
			log.error("Exception", e);
			out.print("<font color=red>Couldn't get content from the URL</font>");
		}
		request.getSession().setAttribute("responseHtmlBody", responseHtmlBody);
		//if(request.getSession().getAttribute("feedTitle")==null && feedTitle!=null){
		//	request.getSession().setAttribute("feedTitle", feedTitle);
		//};
		
	%>
				<jsp:include page="Step1ShowRetreivedSourceCodeOfPage.jsp">
		        	<jsp:param name="responseHtmlBody" value="<%=responseHtmlBody%>" />
		    	</jsp:include>
	<% }%>
	

	<!-- Step 2 -->

	<%if (responseHtmlBody != null) {			
	%>
				<jsp:include page="Step2DefineExtractionRules.jsp">
		        	<jsp:param name="responseHtmlBody" value="<%=responseHtmlBody%>" />
		    	</jsp:include>
	<% }%>
	
	<%if (repeatableSearchPattern != null) {	
		log.debug("repeatableSearchPattern="+repeatableSearchPattern);
		log.debug("responseHtmlBody="+(responseHtmlBody==null?null:"OK"));

	%>
				<jsp:include page="Step2ShowItems.jsp">
		        	<jsp:param name="repeatableSearchPattern" value="<%=repeatableSearchPattern%>" />
		        	<jsp:param name="responseHtmlBody" value="<%=responseHtmlBody%>" />
		    	</jsp:include>
	<% }%>
	
	<%
	log.debug("request.getAttribute(\"dataClippedBol\")="+request.getAttribute("dataClippedBol") );

	if(request.getSession().getAttribute("dataClippedBol")!=null && (Boolean)request.getSession().getAttribute("dataClippedBol")) {
		log.debug("dataClippedBol: OK" );
	%>
				<jsp:include page="Step3DefineOutputFormat.jsp">
		        	<jsp:param name="" value="" />
		    	</jsp:include>
		    	<% }%>
		
		<!-- Step 4 (Feed Preview) -->
		<%
		log.debug("enableStep4FeedPreview="+enableStep4FeedPreview);

		if(enableStep4FeedPreview!=null && enableStep4FeedPreview.equals("true")){ %>
				<jsp:include page="Step4FeedPreview.jsp">
					<jsp:param name="repeatableSearchPattern" value="<%=repeatableSearchPattern %>" />
		        	<jsp:param name="itemTitleTemplate" value="<%=itemTitleTemplate %>" />
		           	<jsp:param name="itemLinkTemplate" value="<%=itemLinkTemplate %>" />
		           	<jsp:param name="itemContentTemplate" value="<%=itemContentTemplate %>" />
		           	<jsp:param name="feedId" value="<%=feedId %>" />
		           	<jsp:param name="feedTitle" value="<%=feedTitle %>" />
		           	<jsp:param name="feedDescription" value="<%= feedDescription %>" />
		           	<jsp:param name="url" value="<%=url %>" />
		  	   	</jsp:include>
		<%} %>
		
</body>
</html>