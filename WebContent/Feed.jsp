<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
ru.kvaga.rss.feedaggr.Exec
"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Editing feed: <%= request.getSession().getAttribute("feedTitle")==null?"":request.getSession().getAttribute("feedTitle")%></title>

</head>
<body>
<a href="LoginSuccess.jsp">Main page</a>
<hr>
<%

if(request.getParameter("url")!=null){
	request.getSession().setAttribute("url",request.getParameter("url"));
}

if(request.getParameter("feedDescription")!=null){
	request.getSession().setAttribute("feedDescription",request.getParameter("feedDescription"));
}

if(request.getParameter("feedId")!=null){
	request.getSession().setAttribute("feedId",request.getParameter("feedId"));
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


String enableStep4FeedPreview=request.getParameter("enableStep4FeedPreview");
String feedId=(String)request.getSession().getAttribute("feedId");
//String feedTitle=(String)request.getSession().getAttribute("feedTitle");
String feedTitle=request.getSession().getAttribute("feedTitle")==null?null:(String)request.getSession().getAttribute("feedTitle");

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
	request.getSession().setAttribute("responseHtmlBody", null);
	
	request.getSession().setAttribute("url", null);
	request.getSession().setAttribute("dataClippedBol", null);
	
	
    request.getSession().setAttribute("feedTitle", null);
    request.getSession().setAttribute("repeatableSearchPattern", null);
    
	enableStep4FeedPreview=null;
	responseHtmlBody=null;
	repeatableSearchPattern=null;
	feedDescription=null;
	itemTitleTemplate=null;
	itemLinkTemplate=null;
	itemContentTemplate=null;
	
	feedTitle="<New Feed>";
}
String url= (String)request.getSession().getAttribute("url");


%>
URL=<%= url %><br>
feedId=<%=feedId%><br>
feedTitle=<%=feedTitle %><br>
responseHtmlBody=<%= responseHtmlBody!=null?"OK":null%><br>
repeatableSearchPattern=<%=repeatableSearchPattern %><br>

===================
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
			url = (url.contains("youtube.com")) ? Exec.getYoutubeFeedURL(url): url;
			if (url==null){
				throw new Exception("Can't find feed channel url");
			}
			responseHtmlBody = Exec.getURLContent(url);
		}catch(Exception e){
			e.printStackTrace();
			out.print("<font color=red>Couldn't get content from the URL</font>");
			//response.sendRedirect("Feed.jsp");
		}
		try{
			feedTitle=Exec.getTitleFromHtmlBody(responseHtmlBody);
			request.getSession().setAttribute("feedTitle", feedTitle);
		}catch(Exception e){
			e.printStackTrace();
			out.print("<font color=red>Couldn't get content from the URL</font>");
		}
		request.getSession().setAttribute("responseHtmlBody", responseHtmlBody);
		//System.out.println("==================>>> feedTitle: " + feedTitle);
		if(request.getSession().getAttribute("feedTitle")==null && feedTitle!=null){
			request.getSession().setAttribute("feedTitle", feedTitle);
		};
		
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
		System.out.println("[point 1]: repeatableSearchPattern="+repeatableSearchPattern);
		System.out.println("[point 1]: responseHtmlBody="+(responseHtmlBody==null?null:"OK"));

	%>
				<jsp:include page="Step2ShowItems.jsp">
		        	<jsp:param name="repeatableSearchPattern" value="<%=repeatableSearchPattern%>" />
		        	<jsp:param name="responseHtmlBody" value="<%=responseHtmlBody%>" />
		    	</jsp:include>
	<% }%>
	
	<%
	System.out.println("[point 5]: request.getAttribute(\"dataClippedBol\")="+request.getAttribute("dataClippedBol") );

	if(request.getSession().getAttribute("dataClippedBol")!=null && (Boolean)request.getSession().getAttribute("dataClippedBol")) {
		System.out.println("[point 6]: OK" );
	%>
				<jsp:include page="Step3DefineOutputFormat.jsp">
		        	<jsp:param name="" value="" />
		    	</jsp:include>
		    	<% }%>
		
		<!-- Step 4 (Feed Preview) -->
		<%
		System.out.println("[point 8] enableStep4FeedPreview="+enableStep4FeedPreview);

		if(enableStep4FeedPreview!=null && enableStep4FeedPreview.equals("true")){ %>
				<jsp:include page="Step4FeedPreview.jsp">
					<jsp:param name="repeatableSearchPattern" value="<%=repeatableSearchPattern %>" />
		        	<jsp:param name="itemTitleTemplate" value="<%=itemTitleTemplate %>" />
		           	<jsp:param name="itemLinkTemplate" value="<%=itemLinkTemplate %>" />
		           	<jsp:param name="itemContentTemplate" value="<%=itemContentTemplate %>" />
		           	<jsp:param name="feedId" value="<%=feedId %>" />
		           	<jsp:param name="feedTitle" value="<%=feedTitle %>" />
		           	<jsp:param name="feedDescription" value="<%=feedDescription %>" />
		           	<jsp:param name="url" value="<%=url %>" />
		  	   	</jsp:include>
		<%} %>
		
</body>
</html>