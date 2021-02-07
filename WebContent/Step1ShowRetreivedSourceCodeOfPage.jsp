<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ page import="ru.kvaga.rss.feedaggr.Exec"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
	<%
		//
		// Сделать здесь редирект на главную если responseHtmlBody==null
		//
		String responseHtmlBody = request.getParameter("responseHtmlBody");
		String feedTitle = Exec.getTitleFromHtmlBody(responseHtmlBody);
		System.out.println("[point 8] feedTitle="+feedTitle);
		if(feedTitle!=null){
			request.getSession().setAttribute("feedTitle", feedTitle);
		}
	%>
	<table class="w100"
		<%if (responseHtmlBody == null) {
				out.print("style=\"display:none;\"");
			} else {
				out.print("style=\"display: block;\"");
			}%>>
		<tbody>
			<tr>
				<td class="w100" style="padding-right: 20px"><span
					id="get_status" class="status"><span class="ok">Page
							too big. First <%=responseHtmlBody.getBytes().length%> bytes
							loaded. Page loaded successfully (encoding: utf-8)
					</span></span></td>

			</tr>

		</tbody>
	</table>

		<p>Below is the HTML source of the retrieved page. Use it to setup
			extraction rules (see next step).</p>

		<p class="nobr">
			Page Source: <a class="small help" title="Help on this option"
				href="javascript:help('page_source')">?</a>
		</p>
		<table>
			<tbody>
				<tr valign="top">
					<td class="w100">
						<div id="raw_data" class="textarea">
							<textarea cols="120" rows="20">
								<%
									out.println(responseHtmlBody);
								%>
								</textarea>
						</div>
					</td>
					<td style="padding-left: 5px">
						<div class="small" style="text-align: center; line-height: 14pt">
							<a title="Decrease area height"
								href="javascript:resize_height('raw_data',-100)">(–)</a><br>
							<a title="Increase area height"
								href="javascript:resize_height('raw_data',100)">(+)</a>
						</div>
					</td>
				</tr>
			</tbody>
		</table>


</body>
</html>