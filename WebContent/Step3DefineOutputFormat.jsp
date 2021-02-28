<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
<h2>Step 3. Define output format</h2>

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
						<textarea id="feed_description" name="feedDescription" cols="120" rows="10" wrap="soft"><%=request.getSession().getAttribute("feedDescription") %></textarea>
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
							<input name="itemTitleTemplate" id="item_title" class="text" size="64" maxlength="150"
							value="{%2}">
					</p>

					<p>
						Item Link Template<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('item_link')">?</a><br> 
							<input name="itemLinkTemplate"
							id="item_link" class="text" size="64" maxlength="2048"
							value="{%1}">
					</p>

					<p class="nobr">
						Item Content Template<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('item_template')">?</a>
					</p>
					<table>
						<tbody>
							<tr valign="top">
								<td class="w100"><textarea id="item_template" name="itemContentTemplate" cols="40"
										rows="4" wrap="soft">{%3}
&lt;br&gt;
&lt;center&gt;&lt;font size="36"&gt;&lt;a href="{%1}"&gt;============================&lt;/a&gt;&lt;/font&gt;&lt;/center&gt;
&lt;br&gt;
&lt;center&gt;&lt;font size="36"&gt;&lt;a href="{%1}"&gt;============ Link ============&lt;/a&gt;&lt;/font&gt;&lt;/center&gt;
&lt;br&gt;
&lt;center&gt;&lt;font size="36"&gt;&lt;a href="{%1}"&gt;============================&lt;/a&gt;&lt;/font&gt;&lt;/center&gt;
&lt;br&gt;
<%=request.getSession().getAttribute("feedTitle") %>
</textarea></td>
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