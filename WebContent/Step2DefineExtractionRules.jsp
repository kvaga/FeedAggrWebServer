<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
    ru.kvaga.rss.feedaggr.FeedAggrException,ru.kvaga.rss.feedaggr.Exec,
    ru.kvaga.rss.feedaggr.FeedAggrException,ru.kvaga.rss.feedaggr.Item,
    java.util.LinkedList"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
<% 
String responseHtmlBody=request.getParameter("responseHtmlBody");

%>

		
				<h2>Step 2. Define extraction rules</h2>

			<form method="post" action="Feed.jsp">
						
				<p class="nobr">
					<a href="javascript:toggle('global_search_pattern')">
					<span id="global_search_pattern_toggle" class="toggle"></span>
						Global Search Pattern (optional):</a> 
					<a class="small help" title="Help on this option" href="javascript:help('global_pattern')">?</a>
				</p>
				<table id="global_search_pattern" class="hidden">
					<tbody>
						<tr valign="top">
							<td class="w100">
							<textarea id="global_pattern" cols="40" rows="4" wrap="soft"></textarea></td>
							<td style="padding-left: 5px">
								<div class="small" style="text-align: center; line-height: 14pt">
									<a title="Decrease area height"
										href="javascript:resize_height('global_pattern',-40)">(–)</a><br>
									<a title="Increase area height"
										href="javascript:resize_height('global_pattern',40)">(+)</a>
								</div>
							</td>
						</tr>
					</tbody>
				</table>

				<p class="nobr" style="margin-top: 20px;">
					Item (repeatable) Search Pattern<span class="bullet">*</span>: 
					<a class="small help" title="Help on this option" href="javascript:help('item_pattern')">?</a>
				</p>
				<table>
					<tbody>
						<tr valign="top">
							<td class="w100">
							<div id="raw_data" class="textarea">
								<textarea name="repeatableSearchPattern" cols="40" rows="4" wrap="soft">&lt;h2 class="title"&gt;{*}&lt;a href="{%}" title="{%}" rel="bookmark">{%}&lt;/a&gt;{*}&lt;/h2&gt;
								</textarea>
							</div>
							</td>
							<td style="padding-left: 5px">
								<div class="small" style="text-align: center; line-height: 14pt">
									<a title="Decrease area height"
										href="javascript:resize_height('item_pattern',-40)">(–)</a><br>
									<a title="Increase area height"
										href="javascript:resize_height('item_pattern',40)">(+)</a>
								</div>
							</td>
						</tr>
					</tbody>
				</table>
				
				<input type="submit" value="Extract">
			</form>
</body>
</html>