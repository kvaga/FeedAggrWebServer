<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="ru.kvaga.rss.feedaggrwebserver.DurationMillisecondsForUpdatingFeeds"%>
<%@page
	import="ru.kvaga.rss.feedaggrwebserver.ServerUtils,
	ru.kvaga.rss.feedaggrwebserver.ResponseForAddRSSFeedByURLAutomaticlyMethod,
org.apache.logging.log4j.*,
ru.kvaga.rss.feedaggrwebserver.ConfigMap,
ru.kvaga.rss.feedaggr.Exec,
ru.kvaga.rss.feedaggrwebserver.objects.user.User,
ru.kvaga.rss.feedaggrwebserver.ServerUtilsConcurrent,
java.util.ArrayList,
java.io.File,
java.util.HashMap,
java.util.HashSet
"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	final Logger log = LogManager.getLogger(ConfigMap.prefixForlog4jJSP + this.getClass().getSimpleName());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Add Feeds by List</title>
</head>
<body>
	<jsp:include page="Header.jsp"></jsp:include>

<%
if (request.getParameter("listOfURLs") == null) {
	request.getSession().removeAttribute("listOfURLs");
	log.debug("Session's attribute [listOfURLs] was removed");
}else{
	request.getSession().setAttribute("listOfURLs", request.getParameter("listOfURLs"));
	log.debug("Session's attribute [listOfURLs] set to ["+request.getParameter("listOfURLs")+"]");
}
%>
	<form>
		Specify the list of URLs (each URL on next line)
		<textarea rows="12" cols="120" name="listOfURLs"><%=request.getSession().getAttribute("listOfURLs") == null ? ""
					: (String) request.getSession().getAttribute("listOfURLs")%></textarea>
		<br /> <input type="submit" value="Add" />
	</form>
	<%
		if (request.getParameter("listOfURLs") != null) {
			log.debug("Starting a process to adding list of urls ["+request.getParameter("listOfURLs") +"]");
			User user = User.getXMLObjectFromXMLFile(ServerUtils.getUserFileByLogin((String) request.getSession().getAttribute("login")));
			HashMap<String, String> localUrlsCache = user.getAllUserUrlsAndFeedIdsMap();
			log.debug("localUrlsCache size ["+localUrlsCache.size()+"]");
			out.write("<table border=\"1\"><tr><td>URL</td><td>Status</td><td>Add to composite</td></tr>");
			
			for (String url : ((String) request.getParameter("listOfURLs")).split("\r\n")) {
				try {
					// Checking for existence of playlists and adding playlists feeds
					//log.debug("Checking URL [" + url + "] for existence in playlists");
					String channelId = Exec.getYoutubeChannelId(url);
					//log.debug("Found channel id ["+channelId+"] in the url ["+url+"]");
					if (channelId == null) {
						log.warn("ChannelId can't be null for url [" + url + "]");
					} else {
						log.debug("Found channelId [" + channelId + "] for URL [" + url + "]");
						String mainPlaylistUrl = Exec.getYoutubeMainPlaylistURL(channelId);
						log.debug("Found main playlist url [" + mainPlaylistUrl	+ "] and getting list of playlists urls");
						HashSet<String> l = Exec.getYoutubeListOfPlaylistsURLs(mainPlaylistUrl);
						log.debug("Found ["+l.size()+"] playlists for the url ["+url+"]");
						String titleOfMainUrl = Exec.getTitleFromHtmlBody(ServerUtilsConcurrent.getInstance().getURLContent(url));
						
						for (String playlistUrl : l) {
							try {
								log.debug("Processing of ["+playlistUrl+"] playlist url of main url ["+url+"]");
								//int size = ServerUtils.addRSSFeedByURLAutomaticly(playlistUrl,	(String) request.getSession().getAttribute("login"), titleOfMainUrl, localUrlsCache, DurationMillisecondsForUpdatingFeeds.EACH_2_WEEKS);
								ResponseForAddRSSFeedByURLAutomaticlyMethod responseForAddRSSFeedByURLAutomaticlyMethod = ServerUtils.addRSSFeedByURLAutomaticly(playlistUrl,(String) request.getSession().getAttribute("login"), titleOfMainUrl, localUrlsCache, DurationMillisecondsForUpdatingFeeds.EACH_2_WEEKS);
								int size = responseForAddRSSFeedByURLAutomaticlyMethod.getSize();
								String createdFeedId = responseForAddRSSFeedByURLAutomaticlyMethod.getFeedId();
								
								if (size > 0) {
									out.write("<tr><td>" + playlistUrl + "</td><td>" + size + "</td><td><a href=\"addFeedId2CompositeFeed.jsp?feedId="+createdFeedId+"\">Add to composite</a></td></tr>");
								} else {
									out.write("<font color=\"red\"><tr><td>" + playlistUrl + "</td><td>" + size	+ "</td><td></td></tr></font>");
								}
							} catch (Exception e) {
								out.write("<font color=\"red\"><tr><td>" + playlistUrl + "</td><td>" + e.getMessage()	+ "</td></tr></font>");
								log.error("ShowResultTableException", e);
							}
						}
					}

					log.debug("Adding main feed");
					// Adding feeds from main videos URLs
					long durationMillisecondsForUpdatingFeeds = ConfigMap.DEFAULT_DURATION_IN_MILLIS_FOR_FEED_UPDATE;
					
					ResponseForAddRSSFeedByURLAutomaticlyMethod responseForAddRSSFeedByURLAutomaticlyMethod = ServerUtils.addRSSFeedByURLAutomaticly(url,(String) request.getSession().getAttribute("login"), localUrlsCache, durationMillisecondsForUpdatingFeeds);
					int size = responseForAddRSSFeedByURLAutomaticlyMethod.getSize();
					String createdFeedId = responseForAddRSSFeedByURLAutomaticlyMethod.getFeedId();
					if (size > 0) {
						out.write("<tr><td>" + url + "</td><td>" + size + "</td></tr>");
					} else {
						out.write("<font color=\"red\"><tr><td>" + url + "</td><td>" + size + "</td><td><a href=\"addFeedId2CompositeFeed.jsp?feedId="+createdFeedId+"\">Add to composite</a></td></tr></font>");
					}
				} catch (Exception e) {
					out.write("<font color=\"red\"><tr><td>" + url + "</td><td>" + e.getMessage()+ "</td><td></td></tr></font>");
					log.error("ShowResultTableException", e);
				}
			}
			out.write("</table>");
			//ServerUtils.clearSessionFromFeedAttributes(request);
		}
	%>
</body>
</html>