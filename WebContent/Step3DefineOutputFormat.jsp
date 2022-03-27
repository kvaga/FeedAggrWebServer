<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
    ru.kvaga.rss.feedaggr.FeedAggrException,
    ru.kvaga.rss.feedaggr.Exec,
    ru.kvaga.rss.feedaggr.FeedAggrException,
    ru.kvaga.rss.feedaggr.Item,
    java.util.LinkedList,
    java.util.ArrayList,
    java.util.Date,
    java.io.File,
    ru.kvaga.rss.feedaggr.Exec,
    ru.kvaga.rss.feedaggr.objects.RSS,
    ru.kvaga.rss.feedaggr.objects.Channel,
    ru.kvaga.rss.feedaggr.objects.Feed,
    ru.kvaga.rss.feedaggr.objects.GUID,
    ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
    ru.kvaga.rss.feedaggrwebserver.objects.user.User,
    ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed,
    ru.kvaga.rss.feedaggrwebserver.objects.user.UserRepeatableSearchPattern,
    ru.kvaga.rss.feedaggrwebserver.ConfigMap,
    org.apache.logging.log4j.*,
    ru.kvaga.rss.feedaggrwebserver.DurationMillisecondsForUpdatingFeeds
    "%>
     
    <%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());
    %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
 <%
 /*
out.print("------------ Step 3.jsp ------------<br>");
out.print("parameter[feedTitle]: " + request.getParameter("feedTitle")+"<br>");
out.print("attribute[feedTitle]: " + request.getSession().getAttribute("feedTitle")+"<br>");
out.print("----------------------------<br>");
*/
%>
 
<h2>Step 3. Define output format</h2>
<%
File userFile=new File(ConfigMap.usersPath.getAbsoluteFile()+"/"+request.getSession().getAttribute("login")+".xml");
//User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
User user = User.getXMLObjectFromXMLFile(userFile);

%>


				<form method="post" action="Feed.jsp">
				<input type="hidden" name="enableStep4FeedPreview" value="true">
					<h3>RSS feed properties</h3>

					<p>These are global feed properties that won't change over
						time. Some feed readers may show them to the user when subscribing
						to the feed.</p>

					<p>
						Feed Title<span class="bullet">*</span>: <a class="small help"
							title="Help on this option" href="javascript:help('feed_title')">?</a><br>
						<input id="feed_title" name="feedTitle" class="text" size="64" maxlength="150"
							value="<%=request.getSession().getAttribute("feedTitle") %>"
							onkeyup="updateTitle()" onchange="updateTitle()">
					</p>

					<p>
						Feed Link<span class="bullet">*</span>: <a class="small help"
							title="Help on this option" href="javascript:help('feed_link')">?</a><br>
						<input id="feed_link" name="url" class="text" size="64" maxlength="2048"
							value="<%=request.getSession().getAttribute("url") %>">
					</p>

					<p>
						Feed Description<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('feed_description')">?</a><br>
						<textarea id="feed_description" name="feedDescription" cols="120" rows="10" wrap="soft"><%=request.getSession().getAttribute("feedDescription")!=null ? request.getSession().getAttribute("feedDescription") : "" %></textarea>
					</p>

					<h3>RSS item properties</h3>

					<p>
						Fields below can accept previously extracted parameters (<span
							class="param">{%1}</span>, <span class="param">{%2}</span> and so
						on).
					</p>
				<p>
						Item Title Template<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('item_title')">?</a><br>
							<input name="itemTitleTemplate" id="item_title" class="text" size="64" maxlength="150" value="<%
								if(request.getSession().getAttribute("itemTitleTemplate")!=null){	
									out.print(request.getSession().getAttribute("itemTitleTemplate"));
								}else if(	
										user.getRssItemPropertiesPatterns()!=null && 
										user.getRssItemPropertiesPatternByDomain(Exec.getDomainFromURL((String)request.getSession().getAttribute("url")))!=null){
										out.print(user.getRssItemPropertiesPatternByDomain(
												Exec.getDomainFromURL((String)request.getSession().getAttribute("url"))).getPatternTitle());
									}else{
										out.print("{%2}");
									}
%>"/>
							</p>

					<p>
						Item Link Template<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('item_link')">?</a><br> 
							<input name="itemLinkTemplate"
							id="item_link" class="text" size="64" maxlength="2048"
							value="<%
									if(request.getSession().getAttribute("itemLinkTemplate")!=null){	
										out.print(request.getSession().getAttribute("itemLinkTemplate"));
									}else if(user.getRssItemPropertiesPatterns()!=null && user.getRssItemPropertiesPatternByDomain(
		Exec.getDomainFromURL((String)request.getSession().getAttribute("url")))!=null){
	out.print(user.getRssItemPropertiesPatternByDomain(
			Exec.getDomainFromURL((String)request.getSession().getAttribute("url"))).getPatternLink());
}else{
	out.print("{%1}");
}
%>" />
					</p>


							
					<p class="nobr">
						Item Content Template<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('item_template')">?</a>
					</p>
					<table>
						<tbody>
							<tr valign="top">
								<td class="w100"><textarea id="item_template" name="itemContentTemplate" 
								cols="120"	rows="5" wrap="soft"><%
										if(request.getSession().getAttribute("itemContentTemplate")!=null){	
											out.print(request.getSession().getAttribute("itemContentTemplate"));
										}else if(user.getRssItemPropertiesPatterns()!=null && user.getRssItemPropertiesPatternByDomain(	Exec.getDomainFromURL((String)request.getSession().getAttribute("url")))!=null){
											out.print(user.getRssItemPropertiesPatternByDomain(
												Exec.getDomainFromURL((String)request.getSession().getAttribute("url"))).getPatternDescription());
										}else{
											//out.print("{%3}&lt;br&gt;&lt;center&gt;&lt;font size=\"36\"&gt;&lt;a href=\"{%1}\"&gt;============================&lt;/a&gt;&lt;/font&gt;&lt;/center&gt;&lt;br&gt;&lt;center&gt;&lt;font size=\"36\"&gt;&lt;a href=\"{%1}\"&gt;============ Link ============&lt;/a&gt;&lt;/font&gt;&lt;/center&gt;&lt;br&gt;&lt;center&gt;&lt;font size=\"36\"&gt;&lt;a href=\"{%1}\"&gt;============================&lt;/a&gt;&lt;/font&gt;&lt;/center&gt;&lt;br&gt;");
											out.print("{%3}&lt;br&gt;&lt;h1 style=\"color:blue;font-size:40px;\"&gt;&lt;a href=\"{%2}\"&gt;LINK&lt;/a&gt;&lt;/h1&gt;");
										}
%></textarea>
</td>
								<td style="padding-left: 5px">
									<div class="small"
										style="text-align: center; line-height: 14pt">
										<a title="Decrease area height"
											href="javascript:resize_height('item_template',-40)">(–)</a><br>
										<a title="Increase area height"
											href="javascript:resize_height('item_template',40)">(+)</a>
									</div>
								</td>
							</tr>
						</tbody>
					</table>

					<p>
						Filter words (divided by '|')<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('item_title')">?</a><br>
							<input name="filterWords" id="filterWords" class="text" size="64" maxlength="150" value="<%= request.getSession().getAttribute("filterWords")!=null ? request.getSession().getAttribute("filterWords") : "" %>"/>
					</p>
						
					<p>
						Duration for Feed Update<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('item_title')">?</a><br>
						<label for="cars">Choose a period:</label>
						<select required name="durationUpdate" id="durationUpdate">
						  <optgroup label="Duration Update">
						  <% 
	
						  	if(request.getSession().getAttribute("durationUpdate")!=null){
						  		out.write("<option value=\""+String.valueOf(DurationMillisecondsForUpdatingFeeds.EACH_HOUR) 	+ "\"" + ((""+request.getSession().getAttribute("durationUpdate")).equals(String.valueOf(DurationMillisecondsForUpdatingFeeds.EACH_HOUR))				?"selected":"") 	+ ">EACH HOUR</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_4_HOURS 	+ "\"" + ((""+request.getSession().getAttribute("durationUpdate")).equals(""+DurationMillisecondsForUpdatingFeeds.EACH_4_HOURS)			?"selected":"")	+ ">EACH 4 HOURS</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_DAY 		+ "\"" + ((""+request.getSession().getAttribute("durationUpdate")).equals(""+DurationMillisecondsForUpdatingFeeds.EACH_DAY)		?"selected":""+ "")	+ ">EACH DAY</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_WEEK 	+ "\"" + ((""+request.getSession().getAttribute("durationUpdate")).equals(""+DurationMillisecondsForUpdatingFeeds.EACH_WEEK)	?"selected":""+ "")	+ ">EACH WEEK</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_2_WEEKS 	+ "\"" + ((""+request.getSession().getAttribute("durationUpdate")).equals(""+DurationMillisecondsForUpdatingFeeds.EACH_2_WEEKS)	?"selected":"")	+ ">EACH 2 WEEKS</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_MONTH 	+ "\"" + ((""+request.getSession().getAttribute("durationUpdate")).equals(""+DurationMillisecondsForUpdatingFeeds.EACH_MONTH)	?"selected":"") + ">EACH MONTH</option>");
							  
						  	}else{
						  		out.write("<option value=\""+ DurationMillisecondsForUpdatingFeeds.EACH_HOUR 	+"\">EACH HOUR</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_4_HOURS 	+"\">EACH 4 HOURS</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_DAY   	+"\" selected>EACH DAY</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_WEEK 	+"\">EACH WEEK</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_2_WEEKS 	+"\">EACH 2 WEEKS</option>");
							    out.write("<option value=\""+DurationMillisecondsForUpdatingFeeds.EACH_MONTH 	+"\">EACH MONTH</option>");
							  
						  	}
						  %>
						   
						  </optgroup>
						</select>
					</p>
					
					<p>
						Suspending Status<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('suspendingStatus')">?</a><br>
						<label for="cars">Choose status:</label>
						<select required name="suspendingStatus" id="suspendingStatus">
						  <optgroup label="Suspending Status Update">
						  <% 
	
						  	if(request.getSession().getAttribute("suspendingStatus")!=null){
						  		out.write("<option value=\"true\"" + ((""+request.getSession().getAttribute("suspendingStatus")).equals("true")?"selected":"") + ">PAUSED</option>");
						  		out.write("<option value=\"false\"" + ((""+request.getSession().getAttribute("suspendingStatus")).equals("false")?"selected":"") + ">ACTIVE</option>");
						  	}else{
						  		out.write("<option value=\"true\">PAUSED</option>");
							    out.write("<option value=\"false\" selected>ACTIVE</option>");
							}
						  %>
						   
						  </optgroup>
						</select>
					</p>
						
					<p>
						<a href="javascript:toggle('optional_parameters')"><span
							id="optional_parameters_toggle" class="toggle"></span>Optional
							parameters</a>
					</p>
					<div id="optional_parameters" class="hidden">
						<p class="nobr">
							<input id="merge_items" type="checkbox" value="1"><label
								for="merge_items">Merge all items into single one,
								optionally applying global template:</label> <a class="small help"
								title="Help on this option"
								href="javascript:help('global_template')">?</a>
						</p>
						<table>
							<tbody>
								<tr valign="top">
									<td class="w100"><textarea id="global_template" cols="40"
											rows="2" wrap="soft"></textarea></td>
									<td style="padding-left: 5px">
										<div class="small"
											style="text-align: center; line-height: 14pt">
											<a title="Decrease area height"
												href="javascript:resize_height('global_template',-40)">(–)</a><br>
											<a title="Increase area height"
												href="javascript:resize_height('global_template',40)">(+)</a>
										</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>

					<p></p>
					<table class="w100">
						<tbody>
							<tr>
								<td class="w100" style="padding-right: 20px"><span
									id="build_status" class="status">
									Click [Preview] button to save above settings and show sample view of your feed</span></td>
								<td><input class="button" type="submit" value="Preview"></td>
							</tr>
						</tbody>
					</table>
					<p></p>
				</form>


</body>
</html>