package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.DurationMillisecondsForUpdatingFeeds;
import ru.kvaga.rss.feedaggrwebserver.ResponseForAddRSSFeedByURLAutomaticlyMethod;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.ServerUtilsConcurrent;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;

/**
 * Servlet implementation class AddFeedsByUrlsListServlet
 * Accepts url list or a string delimetered by '\r\n' or '\n'
 */
@WebServlet("/AddFeedsByUrlsList")
public class AddFeedsByUrlsListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(AddFeedsByUrlsListServlet.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddFeedsByUrlsListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

  
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String source = request.getParameter("source");
		String userName = request.getParameter("userName");
		String urlListAsSingleString = request.getParameter("listUrls");
		
		log.debug("Got parameters "+ServerUtils.listOfParametersToString("userName", userName, "urlList", urlListAsSingleString.toString(), "redirectTo", redirectTo, "source", source));
		String urlListFinal[]=null;
		try {
			// check if listUrls parameter is not a list but String separated by '\r\n' or '\n''
			urlListFinal = ("http" + urlListAsSingleString.replaceAll("^http", "").replaceAll("http", ",http")).split(","); 

//			if(urlListSplitted.length==1) {
//				if(urlListSplitted[0].contains("\r")) {
//					urlListFinal = urlListSplitted[0].split("\r\n");
//				}else if(urlList[0].contains("\n")) {
//					urlListFinal = urlList[0].split("\n");
//				}
//			}
			//urlListFinal = urlList;
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(OBJECT_MAPPER.writeValueAsString(addFeedsByUrlsList(userName, urlListFinal).toArray()));
		}catch (Exception e) {
			log.error("Exception on ", e);
			response.getWriter().write(OBJECT_MAPPER.writeValueAsString(e));
		}	
	}

    public ArrayList<ResponseForAddRSSFeedByURLAutomaticlyMethod> addFeedsByUrlsList(String userName, String[] urlList) {
    	ArrayList<ResponseForAddRSSFeedByURLAutomaticlyMethod> result = new ArrayList<ResponseForAddRSSFeedByURLAutomaticlyMethod>();
    	for (String url : urlList) {
    		log.debug("Processing url {}", url);
			try {
				
					if (url.startsWith("https://youtube") || url.startsWith("https://www.youtube") || url.startsWith("http://youtube") || url.startsWith("http://www.youtube")) {
						// Checking for existence of playlists and adding playlists feeds
						//log.debug("Checking URL [" + url + "] for existence in playlists");
						String channelId = Exec.getYoutubeChannelId(url);
						//log.debug("Found channel id ["+channelId+"] in the url ["+url+"]");
						if (channelId == null) {
							log.warn("ChannelId can't be null for url [" + url + "]");
						} else {
							log.debug("Found channelId [" + channelId + "] for URL [" + url + "]");
							String mainPlaylistUrl = Exec.getYoutubeMainPlaylistURL(channelId);
							log.debug("Found main playlist url [" + mainPlaylistUrl
									+ "] and getting list of playlists urls");
							HashSet<String> l = Exec.getYoutubeListOfPlaylistsURLs(mainPlaylistUrl);
							log.debug("Found [" + l.size() + "] playlists for the url [" + url + "]");
							String titleOfMainUrl = Exec
									.getTitleFromHtmlBody(ServerUtilsConcurrent.getInstance().getURLContent(url));

							for (String playlistUrl : l) {
								try {
									log.debug("Processing of [" + playlistUrl + "] playlist url of main url [" + url
											+ "]");
									//int size = ServerUtils.addRSSFeedByURLAutomaticly(playlistUrl,	(String) request.getSession().getAttribute("login"), titleOfMainUrl, localUrlsCache, DurationMillisecondsForUpdatingFeeds.EACH_2_WEEKS);
									
									ResponseForAddRSSFeedByURLAutomaticlyMethod responseForAddRSSFeedByURLAutomaticlyMethod = ServerUtils
											.addRSSFeedByURLAutomaticly(
													playlistUrl,
													userName,
													titleOfMainUrl,
													DurationMillisecondsForUpdatingFeeds.EACH_2_WEEKS);
									result.add(responseForAddRSSFeedByURLAutomaticlyMethod);
//									int size = responseForAddRSSFeedByURLAutomaticlyMethod.getSize();
//									String createdFeedId = responseForAddRSSFeedByURLAutomaticlyMethod.getFeedId();
//
//									
//									//	!!! THIS SNIPPET IS DUPLICTED BELOW !!!
//									//	Don't forget to correct code below
//									
//									if (size > 0) {
//										out.write("<tr><td><input type=\"checkbox\" id=\"feed_id\" name=\"feedId\" value=\""+createdFeedId+"\" ></td><td>"+responseForAddRSSFeedByURLAutomaticlyMethod.getFeedTitle()+"</td><td>" + responseForAddRSSFeedByURLAutomaticlyMethod.getUrl() + "</td><td>" + size + "</td><td><a href=\"addFeedId2CompositeFeed.jsp?feedId=" + createdFeedId	+ "\">Add to composite</a></td></tr>");
//
//									} else {
//										out.write("<tr><td><input type=\"checkbox\"disabled></td><td>"+responseForAddRSSFeedByURLAutomaticlyMethod.getFeedTitle()+"</td><td>" + Exec.getHTMLFailText(responseForAddRSSFeedByURLAutomaticlyMethod.getUrl()) + "</td><td>" + size	+ "</td><td><a href=\"addFeedId2CompositeFeed.jsp?feedId=" + createdFeedId	+ "\">Add to composite</a></td></tr>");
//									}
								} catch (Exception e) {
									//out.write("<tr><td><input type=\"checkbox\" disabled></td></td><td>" + Exec.getHTMLFailText(url) + "</font></td><td>" + Exec.getHTMLFailText(e.getMessage())+ "</td><td></td></tr>");
									log.error("ShowResultTableException", e);
									result.add(new ResponseForAddRSSFeedByURLAutomaticlyMethod(-1, null, playlistUrl, e.getMessage()));
								}
							}
						}
					}

					if(url.startsWith("https://habr")){
						url=Exec.getHabrFeedURL(url);
					}
					
					url = URLTranslationServlet.translateURL(url, User.getXMLObjectFromXMLFileByUserName(userName));
//					if(url.startsWith("https://t.me")){
//						url=Exec.getTelegramURL(url);
//					}
					 
				//url = (url.contains("youtube.com") && !url.contains("youtube.com/feeds/videos.xml")) ? Exec.getYoutubeFeedURL(url): url;
				//url = (url.startsWith("https://habr.com/ru/rss") || url.startsWith("https://habr.com/rss") || url.startsWith("https://habrahabr.com/rss")|| url.startsWith("https://habrahabr.ru/rss")) ? Exec.getHabrFeedURL(url) : url;
				
					log.debug("Adding main feed for the url {}", url);
					// Adding feeds from main videos URLs
					long durationMillisecondsForUpdatingFeeds = ConfigMap.DEFAULT_DURATION_IN_MILLIS_FOR_FEED_UPDATE;

					ResponseForAddRSSFeedByURLAutomaticlyMethod responseForAddRSSFeedByURLAutomaticlyMethod = ServerUtils
							.addRSSFeedByURLAutomaticly(
									url, 
									userName,
									durationMillisecondsForUpdatingFeeds);
					result.add(responseForAddRSSFeedByURLAutomaticlyMethod);

//					int size = responseForAddRSSFeedByURLAutomaticlyMethod.getSize();
//					
//					String createdFeedId = responseForAddRSSFeedByURLAutomaticlyMethod.getFeedId();
//					if (size > 0) {
//						out.write("<tr><td><input type=\"checkbox\"  id=\"feed_id\" name=\"feedId\" value=\""+createdFeedId+"\"  ></td><td>"+responseForAddRSSFeedByURLAutomaticlyMethod.getFeedTitle()+"</td><td>" + responseForAddRSSFeedByURLAutomaticlyMethod.getUrl() + "</td><td>" + size + "</td><td><a href=\"addFeedId2CompositeFeed.jsp?feedId=" + createdFeedId
//								+ "\">Add to composite</a></td></tr>");					
//					} else {
//						out.write("<tr><td><input type=\"checkbox\"disabled></td><td>"+responseForAddRSSFeedByURLAutomaticlyMethod.getFeedTitle()+"</td><td>" + Exec.getHTMLFailText(responseForAddRSSFeedByURLAutomaticlyMethod.getUrl()) + "</td><td>" + size
//								+ "</td><td><a href=\"addFeedId2CompositeFeed.jsp?feedId=" + createdFeedId
//								+ "\">Add to composite</a></td></tr>");
//					}
				} catch (Exception e) {
					//out.write("<tr><td><input type=\"checkbox\" disabled></td></td><td>"+Exec.getHTMLFailText(url) + "</font></td><td>" + Exec.getHTMLFailText(e.getMessage())+ "</td><td></td></tr>");
					log.error("ShowResultTableException", e);
					result.add(new ResponseForAddRSSFeedByURLAutomaticlyMethod(-1, "", url, e.getMessage()));
				}
			}
    	return result;
    }

}
