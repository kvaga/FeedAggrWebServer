<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="ru.kvaga.rss.feedaggr.Item,ru.kvaga.rss.feedaggr.Exec,
	ru.kvaga.rss.feedaggrwebserver.ServerUtilsConcurrent,
	ru.kvaga.rss.feedaggr.FeedAggrException,java.util.LinkedList,
	org.apache.logging.log4j.*,
	ru.kvaga.rss.feedaggrwebserver.ConfigMap
	"%>
<%
final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP+this.getClass().getSimpleName());

%>
<!DOCTYPE html>
<jsp:include page="Header.jsp"></jsp:include>

<html>
<head>
<!--[en-GB,en;q=0.9,ru-RU;q=0.8,ru;q=0.7,en-US;q=0.6]--[en]--[]-->
<meta charset="utf-8">
<title>Feed Aggregator Web Server</title>
<meta name="MSSmartTagsPreventParsing" content="TRUE">
<meta http-equiv="imagetoolbar" content="no">
<meta name="viewport" content="initial-scale=0.5">
<meta name="Keywords"
	content="convert html to rss,rss feed generator,rss feed scraper,feed generator,html scrape,html scraper,web scrape,web scraper,rss scraping,feed for free,qqq,qqq,page2rss,syndirella,feedfire,feedyes,ponyfish,feedrinse">
<link rel="stylesheet" type="text/css"
	href="/res/style.css?v1.5.2018012703">
<script async="" src="https://www.google-analytics.com/analytics.js"></script>
<script type="text/javascript" src="/res/libXmlRequest.js"></script>
<script type="text/javascript" src="/res/prototypes.js"></script>
<script type="text/javascript" src="/res/engine.js?v1.5.2018012703"></script>
<script language="JavaScript">
	action_url = 'feed.html';
	default_params.push('name', 'auth');
</script>

<script type="text/javascript">
	function setCookie(name, value, days) {
		var expires;
		if (days) {
			var date = new Date();
			date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
			expires = '; expires=' + date.toGMTString();
		} else {
			expires = '';
		}
		document.cookie = name + '=' + value + expires + '; path=/';
	}

	function setLanguage(lang) {
		setCookie('lang', lang, lang === '' ? -1 : 2 * 365);
		window.location.reload();
	}

	function logout() {
		setCookie('user_auth', '', -1);
		window.location.href = window.location.href;
	}
</script>
</head>
<body class="feed">
	<div class="body">

		<div class="header">
			<table class="main">
				<tbody>
					<tr>
						<td width="240"><a href="/" class="logo">Feed<span>Aggregator</span></a></td>
						<td width="100%" class="menu">
							<!-- menu --> <span class="bullet">»&nbsp;</span><a href="/">Home</a>
							<span class="bullet">»&nbsp;</span><a href="/upgrade.html">Pricing</a>
							<span class="bullet">»&nbsp;</span><a href="/help.html">Help</a>
							<span class="bullet">»&nbsp;</span><a href="/feedback.html">Contact</a>
							<div>

								<b>Logged in as Kvaga</b> <span class="bullet">»&nbsp;</span><a
									href="/users/Kvaga" class="bold">My feeds</a> <span
									class="bullet">»&nbsp;</span><a href="javascript:logout()">Sign
									out</a>


							</div> <!-- /menu -->
						</td>
					</tr>
				</tbody>
			</table>
			<div class="main page-heading">
				<h1>
					Editing feed: <span id="title">Renault Logan (2G) — отзывы и
						личный опыт на DRIVE2</span>
				</h1>
			</div>
		</div>




		<div class="main">
			<input id="name" type="hidden" value="8422168026151243"> <input
				id="auth" type="hidden" value="006eb65ef0494d6e0eb15a429a3e0313">

			<p>
				Edit your feed. Required fields are marked with asterisk (<span
					class="bullet">*</span>).
			</p>
			<p>
				Click <a class="help" title="Open help in separate window"
					href="javascript:help('index')">?</a> next to each parameter for
				help.
			</p>

			<!-- ---------------------------- -->



			<h2 style="margin-bottom: 0;">Step 1. Specify source page
				address (URL)</h2>
			<form method="post" onsubmit="doAction('get'); return false;">
				<table class="w100">
					<tbody>
						<tr>
							<td class="w100" style="vertical-align: bottom">
								<p>
									Address<span class="bullet">*</span>: <span class="small">(<a
										title="Open typed address in new window"
										href="javascript:openInBrowser();">Open in browser</a>) <a
										class="small help" title="Help on this option"
										href="javascript:help('url')">?</a></span><br> <input id="url"
										style="width: 490px" size="64" class="text" maxlength="2048"
										value="https://www.drive2.ru/experience/renault/g4096">
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

				
			</form>
		</div>

<table class="w100">
					<tbody>
						<tr>
							<td class="w100" style="padding-right: 20px"><span
								id="get_status" class="status"><span class="ok">Page
										too big. First 102400 bytes loaded. Page loaded successfully
										(encoding: utf-8)</span></span></td>
							<td><input class="button" type="submit" value="Reload"></td>
						</tr>
					</tbody>
				</table>
		<div id="step2" class="hidden" style="display: block;">

			<p>Below is the HTML source of the retrieved page. Use it to
				setup extraction rules (see next step).</p>

			<p class="nobr">
				Page Source: <a class="small help" title="Help on this option"
					href="javascript:help('page_source')">?</a>
			</p>
			<table>
				<tbody>
					<tr valign="top">
						<td class="w100">
							<div id="raw_data" class="textarea">
								<textarea>
								<%
									String urlText = "https://www.drive2.ru/experience/renault/g4096";
									String responseHtmlBody = Exec.getURLContent(urlText);
									String repeatableSearchPattern = "<div class=\"c-post-preview__title\">{*}<a class=\"c-link c-link--text\" href=\"{%}\"  rel=\"noopener\" target=\"_blank\" data-ym-target=\"post_title\">{%}</a>{*}<div class=\"c-post-preview__lead\">{%}</div>{*}<div class=\"c-post-preview__comments\">";
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

			<!-- ---------------------------- -->

			<h2>Step 2. Define extraction rules</h2>

			<form method="post" onsubmit="doAction('search'); return false;">
				<p class="nobr">
					<a href="javascript:toggle('global_search_pattern')"><span
						id="global_search_pattern_toggle" class="toggle"></span>Global
						Search Pattern (optional):</a> <a class="small help"
						title="Help on this option"
						href="javascript:help('global_pattern')">?</a>
				</p>
				<table id="global_search_pattern" class="hidden">
					<tbody>
						<tr valign="top">
							<td class="w100"><textarea id="global_pattern" cols="40"
									rows="4" wrap="soft"></textarea></td>
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
					Item (repeatable) Search Pattern<span class="bullet">*</span>: <a
						class="small help" title="Help on this option"
						href="javascript:help('item_pattern')">?</a>
				</p>
				<table>
					<tbody>
						<tr valign="top">
							<td class="w100"><textarea id="item_pattern" cols="40"
									rows="4" wrap="soft">&lt;div class="c-post-preview__title"&gt;{*}&lt;a class="{*}" href="{%}"{*}&gt;{%}&lt;/a&gt;{*}&lt;/div&gt;{*}&lt;div class="c-post-preview__lead"&gt;{%}&lt;/div&gt;</textarea></td>
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



				<p>
					<%
						String substringForHtmlBodySplit = Exec.getSubstringForHtmlBodySplit(repeatableSearchPattern);
						int countOfPercentItemsInSearchPattern = Exec.countWordsUsingSplit(repeatableSearchPattern, "{%}");
						if (countOfPercentItemsInSearchPattern < 1) {
							throw new FeedAggrException.CommonException(
									String.format("The repeatable search pattern [%s] doesn't contain any{%}"));
						}

						//LinkedList<Item> items = Exec.getItems(responseHtmlBody, substringForHtmlBodySplit, repeatableSearchPattern,
						//		countOfPercentItemsInSearchPattern);
						
						LinkedList<Item> items = ServerUtilsConcurrent.getInstance().getItems(responseHtmlBody, substringForHtmlBodySplit, repeatableSearchPattern,
								countOfPercentItemsInSearchPattern);
						
						
					%>
				</p>
				<table class="w100">
					<tbody>
						<tr>
							<td class="w100" style="padding-right: 20px"><span
								id="search_status" class="status"><span class="ok">
										<%
											if (items.size() > 0) {
												out.println("OK");
											} else {
												out.println("FAIL");
											}
											out.println(" (" + items.size() + " items found)");
										%>
								</span></span></td>
							<td><input class="button" type="submit" value="Extract"></td>
						</tr>
					</tbody>
				</table>
				<p></p>
			</form>


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


			<div id="step3" class="hidden"
				style="margin-top: 30px; display: block;">

				<!-- ---------------------------- -->

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
									<textarea rows="" cols="">
										<div class="preview">
												<%
													int k = 0;
													for (Item item : items) {
														out.println("<h4>Item " + ++k
																+ " <span class=\"pubdate\">&lt;Sat, 02 Jan 2021 14:22:07 GMT&gt;</span></h4>");
														out.print("<p>");
														for (int i = 1; i <= item.length(); i++) {
															out.println("<nobr><span class=\"param\">{%" + i + "}</span> = " + item.get(i) + "</nobr><br>");
														}
														out.println("</p>");
													}
												%>
										</div>
									</textarea>	
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

				<h2>Step 3. Define output format</h2>

				<form method="post" onsubmit="doAction('build'); return false;">
					<h3>RSS feed properties</h3>

					<p>These are global feed properties that won't change over
						time. Some feed readers may show them to the user when subscribing
						to the feed.</p>

					<p>
						Feed Title<span class="bullet">*</span>: <a class="small help"
							title="Help on this option" href="javascript:help('feed_title')">?</a><br>
						<input id="feed_title" class="text" size="64" maxlength="150"
							value="Renault Logan (2G) — отзывы и личный опыт на DRIVE2"
							onkeyup="updateTitle()" onchange="updateTitle()">
					</p>

					<p>
						Feed Link<span class="bullet">*</span>: <a class="small help"
							title="Help on this option" href="javascript:help('feed_link')">?</a><br>
						<input id="feed_link" class="text" size="64" maxlength="2048"
							value="https://www.drive2.ru/experience/renault/g4096">
					</p>

					<p>
						Feed Description<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('feed_description')">?</a><br>
						<textarea id="feed_description" cols="40" rows="3" wrap="soft">Renault Logan (2G) — отзывы и личный опыт на DRIVE2</textarea>
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
							href="javascript:help('item_title')">?</a><br> <input
							id="item_title" class="text" size="64" maxlength="150"
							value="{%2}">
					</p>

					<p>
						Item Link Template<span class="bullet">*</span>: <a
							class="small help" title="Help on this option"
							href="javascript:help('item_link')">?</a><br> <input
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
								<td class="w100"><textarea id="item_template" cols="40"
										rows="4" wrap="soft">{%3}
&lt;br&gt;
&lt;center&gt;&lt;font size="36"&gt;&lt;a href="{%1}"&gt;============================&lt;/a&gt;&lt;/font&gt;&lt;/center&gt;
&lt;br&gt;
&lt;center&gt;&lt;font size="36"&gt;&lt;a href="{%1}"&gt;============ Link ============&lt;/a&gt;&lt;/font&gt;&lt;/center&gt;
&lt;br&gt;
&lt;center&gt;&lt;font size="36"&gt;&lt;a href="{%1}"&gt;============================&lt;/a&gt;&lt;/font&gt;&lt;/center&gt;</textarea></td>
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
									id="build_status" class="status">Click [Preview] button
										to save above settings and show sample view of your feed</span></td>
								<td><input class="button" type="submit" value="Preview"></td>
							</tr>
						</tbody>
					</table>
					<p></p>
				</form>

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
								<td class="w100"><div id="preview" class="textarea">Click
										[Preview] to see contents</div></td>
								<td style="padding-left: 5px">
									<div class="small"
										style="text-align: center; line-height: 14pt">
										<a title="Decrease area height"
											href="javascript:resize_height('preview',-100)">(–)</a><br>
										<a title="Increase area height"
											href="javascript:resize_height('preview',100)">(+)</a>
									</div>
								</td>
							</tr>
						</tbody>
					</table>

					<h2>Your feed is ready!</h2>

					<p style="margin-top: 30px">
						<span class="big">Feed URL: <a
							title="Click to open this URL in new window" id="feed-link"
							target="_blank" href="https://qqq.com/8422168026151243.xml"><span
								class="feed-icon"></span>
								https://qqq.com/8422168026151243.xml</a></span> <a class="small help"
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
							href="https://www.qqq.com/?from=qqqq&amp;add_feed=https://qqq.com/8422168026151243.xml">Subscribe
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