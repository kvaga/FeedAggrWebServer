<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <%@page import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
    ru.kvaga.rss.feedaggr.FeedAggrException,ru.kvaga.rss.feedaggr.Exec,
    ru.kvaga.rss.feedaggr.FeedAggrException,ru.kvaga.rss.feedaggr.Item,
    java.util.LinkedList
    "%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
<style>
   .myClippedData {
    overflow: scroll; /* Добавляем полосы прокрутки */
    width: 900px; /* Ширина блока */
    height: 450px; /* Высота блока */
    padding: 5px; /* Поля вокруг текста */
    border: solid 1px black; /* Параметры рамки */
   } 
  </style>
</head>
<body>

				<%
				String repeatableSearchPattern=request.getParameter("repeatableSearchPattern");
				String substringForHtmlBodySplit=Exec.getSubstringForHtmlBodySplit(repeatableSearchPattern);
				String responseHtmlBody = (String)request.getSession().getAttribute("responseHtmlBody");
				int countOfPercentItemsInSearchPattern = Exec.countWordsUsingSplit(repeatableSearchPattern, "{%}");

				System.out.println("[point 2]: repeatableSearchPattern="+repeatableSearchPattern);
				System.out.println("[point 2]: substringForHtmlBodySplit="+substringForHtmlBodySplit);
				System.out.println("[point 2]: responseHtmlBody="+(responseHtmlBody==null?null:"OK"));
				System.out.println("[point 2]: countOfPercentItemsInSearchPattern="+countOfPercentItemsInSearchPattern);

				

				if (repeatableSearchPattern==null  ) {
					throw new FeedAggrException.CommonException(
							String.format("The repeatable search pattern can't be null"));
				}
				if (responseHtmlBody==null) {
					throw new FeedAggrException.CommonException(
							String.format("The response html body can't be null"));
				}
				if (countOfPercentItemsInSearchPattern < 1) {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login.html");
					response.getWriter().print("<font color=red>The repeatable search pattern ["+repeatableSearchPattern+"] doesn't contain any {%}+</font>");
					rd.include(request, response);
				}
				System.out.println("repeatableSearchPattern: " + repeatableSearchPattern);
				//LinkedList<Item> items = new LinkedList<Item>();
				LinkedList<Item> items = Exec.getItems(responseHtmlBody, substringForHtmlBodySplit, repeatableSearchPattern,countOfPercentItemsInSearchPattern);					
					%>
					
				<table class="w100">
					<tbody>
						<tr>
							<td class="w100" style="padding-right: 20px">
								<span id="search_status" class="status">
									<span class="ok">
											<%
												if (items.size() > 0) {
													out.println("OK");
													
													request.getSession().setAttribute("dataClippedBol", true);
													System.out.println("[point 2.1] request.getSession().getAttribute(\"dataClippedBol\", true)="+request.getSession().getAttribute("dataClippedBol"));
												} else {
													out.println("FAIL");
												}
												out.println(" (" + items.size() + " items found)");
											%>
									</span>
								</span>
							</td>
						</tr>
					</tbody>
				</table>
				<p></p>

	 
			<div id="patterns-insight" class="insight">
				<p>
					Do you need extra help in setting up your feed? <a target="_blank"
						href="/feedback.html?subject=I+need+help+setting+up+my+feed&amp;text=Source+web+page:+\n\n\nI+need+to+extract+from+this+page:+\n\n\nAdditional+comments:+\n">Contact
						us</a>, and we'll see if what you're trying to achieve is possible. We
					will set up one feed for you with the purchase of any <a
						target="_blank" href="/upgrade.html">paid subscription</a>.
				</p>

				<p>
					<strong>Business users:</strong> you can get a 1-hour consultation
					on setting up the feeds with the purchase of any <a target="_blank"
						href="/upgrade.html#business">business plan</a>.
				</p>
			</div>

<p>
					Below is list of extracted text snippets (<span class="param">{%N}</span>).
					You can reference them when setting up item properties (see next
					step).
				</p>

				<p class="nobr">
					Clipped Data: <a class="small help" title="Help on this option"
						href="javascript:help('clipped_data')">?</a>
				</p>
				<table>
					<tbody>
						<tr valign="top">
							<td class="w100">
								<div id="clipped_data" class="textarea">
									
										<div class="preview">
										  <div class="myClippedData"> 
										<!--  <div id="raw_data" class="textarea"> 
										<textarea cols="120" rows="20"> добавил недавно -->
												<%
													int k = 0;
													for (Item item : items) {
														out.println("<h4>Item " + ++k
																+ " <span class=\"pubdate\">&lt;Sat, 02 Jan 2021 14:22:07 GMT&gt;</span></h4>");
														out.print("<p>");
														for (int i = 1; i <= item.length(); i++) {
															if(i==3){
																/*
																System.out.println("-------------------------");

																System.out.println("item[3=]: " + item.get(i));
																System.out.println("-------------------------");
																*/
															}
															if(i==3){
																out.println("<nobr><span class=\"param\">{%" + i + "}</span> = <![CDATA[" + item.get(i) + "]]></nobr><br>");
															}else{
																out.println("<nobr><span class=\"param\">{%" + i + "}</span> = " + item.get(i) + "</nobr><br>");
															}
															
															
															
																
																
														
														}
														out.println("</p>");
													}
												%>
										<!--  		</textarea> -->
												</div>
										</div>
									
								</div>
							</td>
							<td style="padding-left: 5px">
								<div class="small" style="text-align: center; line-height: 14pt">
									<a title="Decrease area height"
										href="javascript:resize_height('clipped_data',-100)">(–)</a><br>
									<a title="Increase area height"
										href="javascript:resize_height('clipped_data',100)">(+)</a>
								</div>
							</td>
						</tr>
					</tbody>
				</table>


</body>
</html>