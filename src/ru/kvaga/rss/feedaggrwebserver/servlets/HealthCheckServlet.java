package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.servlets.utils.ServletUtils;

/**
 * Servlet implementation class HealthCheckServlet
 */
@WebServlet("/HealthCheck")
public class HealthCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(HealthCheckServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HealthCheckServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    private Gson gson = new Gson();

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String source = request.getParameter("source");
		String userName = request.getParameter("userName");
		String kindOfCheck = request.getParameter("kindOfCheck");
		
		try {
			log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName, "kindOfCheck", kindOfCheck));
			if(kindOfCheck==null) {
//				ServletUtils.responseJSONError("Couldn't find any kindOfCheck parameter", response);
//				response.getWriter().print(gson.toJson("{ error: 'Couldn't find any kindOfCheck parameter!'"));
				
				//response.getWriter().print(gson.toJson(new ServletError("Couldn't find any kindOfCheck parameter!")));
				ServletUtils.responseJSONError("Couldn't find any kindOfCheck parameter", response);
				return;
			} 
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			switch(kindOfCheck) {
				case "checkZombiFeedsInCompositeFeeds":
					//ServletUtils.responseJSON(checkZombiFeedsInCompositeFeeds(userName), response);
					response.getWriter().print(gson.toJson(checkZombiFeedsInCompositeFeeds(userName)));
					break;
			}
		}catch (Exception e) { 
			log.error("Exception on CompositeFeedsListServlet", e);
			//ServletUtils.responseJSONError("Exception: " + e.getMessage(), response);
//			response.getWriter().write(
//					new ObjectMapper().writeValueAsString(new ServletError("Exception: " + e.getMessage())));
			//response.getWriter().print(gson.toJson("{ error: 'Exception: " + e.getMessage()+"'"));
			ServletUtils.responseJSONError(e.getMessage(), response);

		}	 
	}
	
	/**
	 * <h3>Check duplicate feeds in composite</h3>
	 * @throws JAXBException 
	 */
	public void checkDuplicateFeedIdsInCompositeFeeds(String userName) throws JAXBException {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
//		for(user.getCompositeUserFeeds()) {
//			
//		}
	}
	
//		<h3>Duplicate feeds by users (duplicate feeds for user that have the same url)</h3>
//				<h3>Zombie feeds by user (user has feeds that don't exist)</h3>
//						<h3>Composite User Feeds with null title of user Kvaga</h3>
//						<h3>User Feeds with null title or url of user Kvaga</h3>
//						<h3>Cache usersFeeds incorrect items</h3>
//						<h3>Cache compositeUsersFeeds incorrect items</h3>
//						
	
	class AbandonedFeed{
		public String feedId;
		public String title;
		public String link;
		public AbandonedFeed(String feedId, String link, String title) {
			this.feedId=feedId;
			this.link=link;
			this.title=title;
		}
	}
	class CompositeAbandonedFeed extends AbandonedFeed {
		public CompositeAbandonedFeed(String feedId, String link, String title) {
			super(feedId, link, title);
		}
	}
	
	/**
	 * <h3>Zombie feeds in composite feeds by user (composite feed has feeds that don't exist)</h3>
	 * Check that user has composite feeds that have links to feeds that files don't exist
	 * 
	 * @throws JAXBException 
	 */
	public HashMap<CompositeUserFeed, ArrayList<String>> checkZombiFeedsInCompositeFeeds(String userName) throws JAXBException {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		HashMap<CompositeUserFeed, ArrayList<String>> map = new HashMap<CompositeUserFeed, ArrayList<String>>();
		for(CompositeUserFeed cuf : user.getCompositeUserFeeds()) {
			for(String feedId : cuf.getFeedIds()) {
				if(!RSS.getRSSFileByFeedId(feedId).exists()) {
					
						if(!map.containsKey(cuf)) {
							map.put(cuf, new ArrayList<String>());
						}
						map.get(cuf).add(feedId);
						//log.debug("cuf: {}, feedId: {}, file {}", cuf, feedId, RSS.getRSSFileByFeedId(feedId));
					
				}
			}
		}
		return map;
	}
	
	/**
	 * <h3>Abandoned feeds by users (no user who has these feeds)</h3>
	 */
	/*
	public void getAbandonedFeeds(String userName) {
		HashMap<String,String> allFeedIdsOfAllUsersMap = User.getFeedsIdsOfAllUsersMap();
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		
		ArrayList<AbandonedFeed> abandonedFeedsList = new ArrayList<AbandonedFeed>();
		ArrayList<CompositeAbandonedFeed> compositeAbandonedList = new ArrayList<CompositeAbandonedFeed>();

		for (Feed feedFromAll :  ServerUtils.getFeedsList(true, true)) {
			if (!allFeedIdsOfAllUsersMap.containsKey(feedFromAll.getId())) {
				log.warn("Found abandoned feed [" + feedFromAll.getId() + "]");
				if(feedFromAll.getId().startsWith("composite")){
					compositeAbandonedList.add(new CompositeAbandonedFeed(feedFromAll.getId(),"", feedFromAll.getXmlFile()));
				}else{
					abandonedFeedsList.add(new AbandonedFeed(feedFromAll.getId(),"", feedFromAll.getXmlFile()));
				}
			}
		}
		if(abandonedFeedsList.size()>0){
			// Abandoned feeds
			//String table = "<form method=\"POST\" action=\"addAbandonedFeedToUser\">";
			//table+="<table border='1'>"+
			//"<tr align=\"center\"><td><input type=\"checkbox\" onClick=\"toggle(this)\" />#</td><td>Abandoned feed</td><td>Delete</td><td>Add to user</td></tr>";
			for(AbandonedFeed feed : abandonedFeedsList){
				if(feed.feedId.startsWith("composite")) continue;
				RSS rss = RSS.getRSSObjectFromXMLFile(feed.xmlFile);
				feed.
//				table+=	"<tr>"+
//							"<td><input type=\"checkbox\" id=\"feed_id_"+feed.getId()+"\" name=\"feed_id_"+feed.getId()+"\" value=\""+feed.getId()+"\"></td>"+
//							"<td><a href=\"showFeed?feedId="+feed.getId()+"\">"+rss.getChannel().getTitle()+ "</a><br>"+rss.getChannel().getLink()+"</td>"+
//							"<td>[<a href=\"deleteFeed?feedId="+feed.getId()+"&redirectTo=/HealthCheck.jsp\">Delete</a>]</td>"+
//							"<td><a href=\"addAbandonedFeedToUser?redirectTo=/HealthCheck.jsp&feedId="+feed.getId()+"\">ADD</a></td>"+
//						"</tr>";
			}
			table+="<tr><td colspan=\"3\"></td><td><input type=\"submit\" value=\"Add\"></td></tr></table><input id=\"batch\" name=\"batch\" type=\"hidden\" value=\"yes\"><input type=\"hidden\" name=\"redirectTo\" value=\"HealthCheck.jsp\"></form>";
			out.append(table);
		}else{
			out.append("There are no abandoned feeds<br><br>");
		}
		
		if(compositeAbandonedList.size()>0){
			// Composite abandoned feeds
			String tableComposite = "<table border='1'>"+
					"<tr align=\"center\"><td>Composite abandoned feed</td><td>Delete</td><td>Add to user</td></tr>";
			for(Feed feed : compositeAbandonedList){
				if(!feed.getId().startsWith("composite")) continue;
				RSS rss = RSS.getRSSObjectFromXMLFile(feed.getXmlFile());
				tableComposite+=	"<tr>"+
							"<td><a href=\"showFeed?feedId="+feed.getId()+"\">"+rss.getChannel().getTitle()+ "</a><br>"+rss.getChannel().getLink()+"</td>"+
							"<td>[<a href=\"deleteFeed?feedId="+feed.getId()+"&redirectTo=/HealthCheck.jsp\">Delete</a>]</td>"+
							"<td><a href=\"addAbandonedFeedToUser?redirectTo=HealthCheck.jsp&feedId="+feed.getId()+"\">ADD</a></td>"+
						"</tr>";
			}
			tableComposite+="</table>";
			out.append(tableComposite);
		}else{
			out.append("There are no composite abandoned feeds<br>");
		}
			log.debug("Finished searching of abandoned files");
		
	}
	*/
	private String arrayToString(String[] array) {
		StringBuilder sb = new StringBuilder();
		if(array==null || array.length==0) {
			return null;
		}else if(array.length==1) {
			sb.append(array[0]);	
		}else {
			sb.append(array[0]);
			for(int i=1;i<=array.length;i++) {
				sb.append(", ");
				sb.append(array[i]);
			}
		}
		return sb.toString();
	}

}
