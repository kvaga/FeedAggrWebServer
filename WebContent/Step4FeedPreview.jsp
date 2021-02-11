<%@page import="ru.kvaga.rss.feedaggrwebserver.ConfigMap"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
      <%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
    ru.kvaga.rss.feedaggr.FeedAggrException,ru.kvaga.rss.feedaggr.Exec,
    ru.kvaga.rss.feedaggr.FeedAggrException,ru.kvaga.rss.feedaggr.Item,
    java.util.LinkedList,
    java.util.ArrayList,
    java.util.Date,
    java.io.File,
    ru.kvaga.rss.feedaggr.objects.RSS,
    ru.kvaga.rss.feedaggr.objects.Channel,
    ru.kvaga.rss.feedaggr.objects.Feed,
    ru.kvaga.rss.feedaggr.objects.GUID,
    ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils,
    ru.kvaga.rss.feedaggrwebserver.objects.user.User,
    ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed
    "%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
<%

String repeatableSearchPattern=request.getParameter("repeatableSearchPattern");
String substringForHtmlBodySplit=Exec.getSubstringForHtmlBodySplit(repeatableSearchPattern);
String responseHtmlBody = (String)request.getSession().getAttribute("responseHtmlBody");
int countOfPercentItemsInSearchPattern = Exec.countWordsUsingSplit(repeatableSearchPattern, "{%}");

String itemTitleTemplate=request.getParameter("itemTitleTemplate");
String itemLinkTemplate=request.getParameter("itemLinkTemplate");
String itemContentTemplate=request.getParameter("itemContentTemplate");
String feedId=request.getParameter("feedId");
String feedTitle=request.getParameter("feedTitle");
String feedDescription=request.getParameter("feedDescription");
String url=request.getParameter("url");


%>
				<div id="step4" class="hidden">

					<!-- ---------------------------- -->

					<p>Here is how your feed will look like in feed reader. Go to
						next step to get the link to your feed.</p>

					<p class="nobr">
						Feed Preview: <a class="small help" title="Help on this option"
							href="javascript:help('preview')">?</a>
					</p>
					<table>
						<tbody>
							<tr valign="top">
								<td class="w100"><div id="preview" class="textarea">
								Click [Preview] to see contents</div></td>
								<td style="padding-left: 5px">
									<div class="small"
										style="text-align: center; line-height: 14pt">
										<a title="Decrease area height"
											href="javascript:resize_height('preview',-100)">(–)</a><br>
										<a title="Increase area height"
											href="javascript:resize_height('preview',100)">(+)</a>
									</div>
									<div class="myClippedData">
									Title:<%=feedTitle %><br>
									Url:<%=url %><br>
									Description:<%=feedDescription %><br>
									<hr>
												<%
												RSS rss = new RSS();
										        rss.setVersion("2.0");
										        
										        Channel channel = new Channel();
										        channel.setTitle(feedTitle);
										        channel.setLink(url);
										        channel.setTtl(360);
										        channel.setLastBuildDate(new Date());
										        channel.setGenerator("Feed Aggr Web Server Generator");
										        channel.setDescription(feedDescription);
										        
										        // спи
										        ArrayList<ru.kvaga.rss.feedaggr.objects.Item> items = new ArrayList<ru.kvaga.rss.feedaggr.objects.Item>();
										        
										        
										       
										        
										        // список полученных из html body элементов
												LinkedList<Item> itemsFromHtmlBody = Exec.getItems(responseHtmlBody, substringForHtmlBodySplit, repeatableSearchPattern,countOfPercentItemsInSearchPattern);					
												String itemTitle=null;
												String itemLink=null;
												String itemContent=null;
												//System.out.println("[point 9] itemTitleTemplate="+itemTitleTemplate);
												//System.out.println("[point 9] itemLinkTemplate="+itemLinkTemplate);
												//System.out.println("[point 9] itemContentTemplate="+itemContentTemplate);

													int k = 0;
													for (Item itemFromHtmlBody : itemsFromHtmlBody) {
													
												        ru.kvaga.rss.feedaggr.objects.Item _item = new ru.kvaga.rss.feedaggr.objects.Item();

														//out.println("<h4>Item " + ++k
															//	+ " <span class=\"pubdate\">&lt;Sat, 02 Jan 2021 14:22:07 GMT&gt;</span></h4>");
														out.print("<p>");

														itemTitle=itemTitleTemplate;
														itemLink=itemLinkTemplate;
														itemContent=itemContentTemplate;
														//цикл для замены всех {%Х} на значения
															for (int i = 1; i <= itemFromHtmlBody.length(); i++) {
																try{
																System.out.println("count: " + i + " [item.get("+i+")="+itemFromHtmlBody.get(i)+"]");
																
																itemTitle=itemTitle.replaceAll("\\{%"+i+"}", itemFromHtmlBody.get(i));
																itemLink=itemLink.replaceAll("\\{%"+i+"}", itemFromHtmlBody.get(i));
																//itemLink=Exec.checkItemURLForFullness(url, itemLink);
																itemContent=itemContent.replaceAll("\\{%"+i+"}", itemFromHtmlBody.get(i));
																System.out.println("[point 10] itemTitle="+itemTitle +", itemTitleTemplate="+itemTitleTemplate+", [item.get("+i+")="+itemFromHtmlBody.get(i)+"]");
																System.out.println("[point 10] itemLink="+itemLink +", itemLinkTemplate="+itemLinkTemplate+", [item.get("+i+")="+itemFromHtmlBody.get(i)+"]");
																System.out.println("[point 10] itemContent="+itemContent +", itemContentTemplate="+itemContentTemplate+", [item.get("+i+")="+itemFromHtmlBody.get(i)+"]");
																}catch(Exception e){
																	e.printStackTrace();
																}
																
																//out.println("<nobr><span class=\"param\">{%" + i + "}</span> = " + item.get(i) + "</nobr><br>");
															}
															out.println("<nobr><span class=\"param\">" + ++k +". " + itemTitle + "</nobr><br>");
															out.println("<nobr><span class=\"param\">" + itemLink + "</nobr><br>");
															out.println("<nobr><span class=\"param\">" + itemContent + "</nobr><br>");
															//System.out.println("===========================");
															//System.out.println("itemContent: " + itemContent);
															//System.out.println("===========================");
		
															
															_item.setTitle(itemTitle);
													        _item.setLink(itemLink);
													       // _item.setLink(Exec.checkItemURLForFullness(url, itemLink));
													        //System.out.println("CDATA: " + _item.getDescription());

													        _item.setDescription(itemContent);
													        //_item.setDescription("<![CDATA["+itemContent+"]]>");

													        _item.setPubDate(new Date());
													        _item.setGuid(new GUID("false", GUID.generateGUID(itemLink+itemTitle)));
													        //item.add(_item);
															 items.add(_item);

														out.println("</p>");
														out.println("<br>");

													}
													 channel.setItem(items);
												//	 <div class="c-post-preview__title"{*}href="{%}"  rel="noopener" target="_blank" data-ym-target="post_title">{%}</a>{*}class="c-post-preview__lead">{%}<div class="c-post-preview__comments">
													 rss.setChannel(channel);
												        File xmlFile=new File(ConfigMap.feedsPath.getAbsoluteFile()+"/"+feedId+".xml");
												        //File userFile=new File(getServletContext().getRealPath("data/users")+"/"+"kvaga"+".xml");
												        File userFile=new File(ConfigMap.usersPath.getAbsoluteFile()+"/"+"kvaga"+".xml");
												        ObjectsUtils.saveXMLObjectToFile(rss, rss.getClass(), xmlFile);
												        System.out.println("Object rss ["+rss.getChannel().getTitle()+"] successfully saved to the ["+xmlFile+"] file");

												        User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
												        //----------------------
												        //User kvaga = new User("kvaga"); // TODO: change to session value
														//File file = new File("C:\\eclipseWorkspace\\FeedAggrWebServer\\WebContent\\data\\users\\kvaga.xml");
														
												        user.getUserFeeds().add(new UserFeed(feedId, itemTitleTemplate, itemLinkTemplate, itemContentTemplate, repeatableSearchPattern));
														
														
												        //----------------------
														ObjectsUtils.saveXMLObjectToFile(user, user.getClass(), userFile);
												        System.out.println("Object user ["+user.getName()+"] successfully saved to the ["+userFile+"] file");

												%>
												</div>
								</td>
							</tr>
						</tbody>
					</table>

<h2>Your feed is ready!</h2>

					<p style="margin-top: 30px">
						<span class="big">Feed URL: <a
							title="Click to open this URL in new window" id="feed-link"
							target="_blank" href="showFeed?feedId=<%=feedId %>">
							
							<span
								class="feed-icon"></span>
								<%=request.getContextPath()+"/showFeed?feedId="+feedId%></a></span> <a class="small help"
							title="Help on this option" href="javascript:help('feed_url')">?</a>
					</p>

					<p>Point your feed reader to this URL or click to open it.</p>



					<h3 style="margin-top: 30px">Optional features</h3>

					<p>
						<a title="Click to show/hide the form"
							href="javascript:toggle('rename_form');"><span
							id="rename_form_toggle" class="toggle"></span>Change file name</a> of
						this feed to make it more user-friendly <a class="small help"
							title="Help on this option" href="javascript:help('rename')">?</a>
					</p>

					<div id="rename_form" class="popup hidden" style="width: 401px">
						<form onsubmit="doAction('rename'); return false;">
							<table>
								<tbody>
									<tr>
										<td>Name:</td>
										<td class="w100" style="padding-left: 5px"><input
											id="rename" class="text" size="30" maxlength="30"
											value="8422168026151243"></td>
										<td style="padding-left: 10px"><input class="button"
											type="submit" value="Rename"></td>
									</tr>
								</tbody>
							</table>
						</form>
						<p class="small" style="margin-top: 10px; margin-bottom: 0px;">
							<span id="rename_status" class="status">Only a-z, 0-9,
								underscore and hyphen symbols are allowed.</span>
						</p>
					</div>

					<div id="link_feature" class="hidden">
						<p>
							<a title="Click to show/hide the form"
								href="javascript:toggle('link_account_form');"><span
								id="link_account_form_toggle" class="toggle"></span><b>Add
									this feed to my account</b></a> <a class="small help"
								title="Help on this option"
								href="javascript:help('link_account')">?</a>
						</p>


						<div id="link_account_form" class="popup hidden"
							style="width: 300">
							<form onsubmit="doAction('link_account'); return false;">
								<table>
									<tbody>
										<tr>
											<td class="w100"><nobr>
													Account: <b>Kvaga</b>
												</nobr></td>
											<td style="padding-left: 10px"><input
												id="link_user_name" type="hidden" value=""><input
												id="link_user_pwd" type="hidden" value=""><input
												class="button" type="submit" value="Add"></td>
										</tr>
									</tbody>
								</table>
							</form>
							<p class="small" style="margin-top: 10px; margin-bottom: 0px;">
								<span id="link_account_status" class="status">Click [Add]
									to take ownership of this feed.</span>
							</p>
						</div>


					</div>

					<div id="protect_feature" class="hidden">
						<p>
							<a title="Click to show/hide the form"
								href="javascript:toggle('protect_edit_form');"><span
								id="protect_edit_form_toggle" class="toggle"></span><b>Protect
									feed from being edited</b></a> <a class="small help"
								title="Help on this option"
								href="javascript:help('protect_edit')">?</a>
						</p>

						<div id="protect_edit_form" class="popup hidden"
							style="width: 400px">
							<form onsubmit="doAction('protect_edit'); return false;">
								<table>
									<tbody>
										<tr>
											<td>Password<span class="bullet">*</span>:
											</td>
											<td class="w100" style="padding-left: 5px"><input
												id="edit_password" class="text fakepwd"
												onfocus="if (this.className != 'text') {this.className = 'text'; this.value = ''}"
												type="password" size="30" maxlength="16" value="#*$*#*$*#"></td>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td colspan="2" style="height: 10px"></td>
										</tr>
										<tr>
											<td><nobr>E-mail:</nobr></td>
											<td class="w100" style="padding-left: 5px"><input
												id="email" class="text" size="30" maxlength="64" value=""></td>
											<td style="padding-left: 10px"><input class="button"
												type="submit" value="Set"></td>
										</tr>
									</tbody>
								</table>
							</form>
							<p class="small" style="margin-top: 10px; margin-bottom: 0px;">
								<span id="protect_edit_status" class="status">You can
									specify your e-mail to be able to restore password later.</span>
							</p>
						</div>
					</div>

					<div id="change_feature" class="">
						<p>
							<a title="Click to show/hide the form"
								href="javascript:toggle('change_protect_form');"><span
								id="change_protect_form_toggle" class="toggle"></span>Change
								feed edit password</a> <a class="small help"
								title="Help on this option"
								href="javascript:help('change_protect')">?</a>
						</p>

						<div id="change_protect_form" class="popup hidden"
							style="width: 400px">
							<form onsubmit="doAction('change_protect'); return false;">
								<table>
									<tbody>
										<tr>
											<td>Password<span class="bullet">*</span>:
											</td>
											<td class="w100" style="padding-left: 5px"><input
												id="change_password" class="text fakepwd"
												onfocus="if (this.className != 'text') {this.className = 'text'; this.value = ''}"
												type="password" size="30" maxlength="16" value="#*$*#*$*#"></td>
											<td style="padding-left: 10px"><input class="button"
												type="submit" value="Set"></td>
										</tr>
									</tbody>
								</table>
							</form>
							<p class="small" style="margin-top: 10px; margin-bottom: 0px;">
								<span id="change_protect_status" class="status">Please
									provide a new password (or blank password to remove
									protection).</span>
							</p>
						</div>
					</div>

					<!-- - -->

					<p>
						<a title="Click to show/hide the form"
							href="javascript:toggle('protect_view_form');"><span
							id="protect_view_form_toggle" class="toggle"></span>Make this
							feed private</a> <a class="small help" title="Help on this option"
							href="javascript:help('protect_view')">?</a>
					</p>

					<div id="protect_view_form" class="popup hidden"
						style="width: 400px">
						<form onsubmit="doAction('protect_view'); return false;">
							<table>
								<tbody>
									<tr>
										<td><nobr>User Name:</nobr></td>
										<td class="w100" style="padding-left: 5px"><input
											id="view_username" class="text" size="30" maxlength="16"
											value=""></td>
										<td>&nbsp;</td>
									</tr>
									<tr>
										<td colspan="2" style="height: 10px"></td>
									</tr>
									<tr>
										<td>Password:</td>
										<td class="w100" style="padding-left: 5px"><input
											id="view_password" class="text"
											onfocus="if (this.className != 'text') {this.className = 'text'; this.value = ''}"
											type="password" size="30" maxlength="16" value=""></td>
										<td style="padding-left: 10px"><input class="button"
											type="submit" value="Set"></td>
									</tr>
								</tbody>
							</table>
						</form>
						<p class="small" style="margin-top: 10px; margin-bottom: 0px;">
							<span id="protect_view_status" class="status">Provide user
								name and password.</span>
						</p>
					</div>

					<!-- - -->

					<div id="email_feature" class="hidden">
						<p>
							<a title="Click to show/hide the form"
								href="javascript:toggle('email_form');"><span
								id="email_form_toggle" class="toggle"></span>Send me summary
								e-mail</a> with feed name, URL, etc. <a class="small help"
								title="Help on this option" href="javascript:help('email')">?</a>
						</p>

						<div id="email_form" class="popup hidden" style="width: 400px">
							<form onsubmit="doAction('email'); return false;">
								<table>
									<tbody>
										<tr>
											<td><nobr>E-mail:</nobr></td>
											<td class="w100" style="padding-left: 5px"><input
												id="remind_email" class="text" size="30" maxlength="64"
												value=""></td>
											<td style="padding-left: 10px"><input class="button"
												type="submit" value="Send"></td>
										</tr>
									</tbody>
								</table>
							</form>
							<p class="small" style="margin-top: 10px; margin-bottom: 0px;">
								<span id="email_status" class="status">Please enter your
									e-mail address.</span>
							</p>
						</div>
					</div>

					<h2>What to do next?</h2>

					<p class="big">
						<img src="/res/_icon.png" width="16" height="16"
							style="margin-right: 10px; position: relative; top: 2px;"><a
							id="-link"
							href="https://www.qqq.com/?from=qqq&amp;add_feed=https://qqq.com/8422168026151243.xml">Subscribe
							to this feed in QQQ</a>
					</p>
					<p style="margin-left: 26px;">QQQ is a great online
						reader that will keep the full news archive for this feed, and can
						alert you by email when the feed is updated.</p>

					<div style="padding-top: 30px">
						<a class="cta" href="feed.html?action=new">Create another feed</a>
					</div>

				</div>
				<!-- /step4 -->
			</div>
			<!-- /step3 -->
		</div>
		<!-- /step2 -->

		<div id="show_all">
			<p class="small" style="margin-top: 30px;">
				<a title="Show entire form at once" href="javascript:showAll()">See
					all parameters at once</a> <a class="small help"
					title="Help on this option" href="javascript:help('advanced_mode')">?</a>
			</p>
		</div>

	</div>



	<script>
		var autoloadURL = sessionStorage.autoloadURL || '';
		delete sessionStorage.autoloadURL;
	</script>




	<div class="main footer">
		<div id="language">
			Language: <select id="language_selector"
				onchange="setLanguage(this.value)">
				<option value="">Auto-detect</option>
				<option value="en" selected="">English</option>
				<option value="ru">Русский</option>
			</select>
		</div>
		Feed Aggregator Web Server v. 1.0. Copyright © 2020–2021 FAWS. All
		rights reserved. <a href="/tos.html">Terms of Service</a>
	</div>


</body>
</html>