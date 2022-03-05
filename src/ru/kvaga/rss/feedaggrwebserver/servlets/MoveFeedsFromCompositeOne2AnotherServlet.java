package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.catalina.connector.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

/**
 * Servlet implementation class CompositeFeedsList
 */
@WebServlet("/MoveFeedsFromCompositeOne2Another")
public class MoveFeedsFromCompositeOne2AnotherServlet extends HttpServlet {
	final private static Logger log = LogManager.getLogger(MoveFeedsFromCompositeOne2AnotherServlet.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MoveFeedsFromCompositeOne2AnotherServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String source = request.getParameter("source");
		String userName = request.getParameter("userName");
		String compositeFeedIdFrom = request.getParameter("compositeFeedIdFrom");
		String compositeFeedIdTo = request.getParameter("compositeFeedIdTo");
		ArrayList<String> feedsForMoving = new ArrayList<String>();
		for(String feedId : request.getParameterValues("feedId")) {
			feedsForMoving.add(feedId);
		}
		
		log.debug("Got parameters "+ServerUtils.listOfParametersToString("userName", userName, "compositeFeedIdFrom", compositeFeedIdFrom, "compositeFeedIdTo", compositeFeedIdTo, "feedIds", printFeedIdsFromList(feedsForMoving) ,"redirectTo", redirectTo, "source", source));
		
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(OBJECT_MAPPER.writeValueAsString(moveFeedsFromCompositeOne2Another(userName, compositeFeedIdFrom, compositeFeedIdTo, feedsForMoving)));
		}catch (Exception e) {
			log.error("Exception on CompositeFeedsListServlet", e);
			response.getWriter().write(OBJECT_MAPPER.writeValueAsString(e));
		}	
	}

	public HashMap<String, String> moveFeedsFromCompositeOne2Another(String userName, String compositeFeedIdFrom, String compositeFeedIdTo, ArrayList<String> feedsForMoving) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		HashMap<String, String> result = new HashMap<String, String>();
		CompositeUserFeed cufFrom = user.getCompositeUserFeedById(compositeFeedIdFrom);
		CompositeUserFeed cufTo = user.getCompositeUserFeedById(compositeFeedIdTo);
		RSS rssFrom = RSS.getRSSObjectByFeedId(compositeFeedIdFrom);
		RSS rssTo = RSS.getRSSObjectByFeedId(compositeFeedIdTo);
		if(cufFrom == null) throw new Exception("User ["+userName+"] doesn't have compositeFeedIdFrom ["+compositeFeedIdFrom+"]"); 
		if(cufTo == null) throw new Exception("User ["+userName+"] doesn't have compositeFeedIdTo ["+compositeFeedIdTo+"]"); 
		for(String feedId: feedsForMoving) {
			result.put(feedId, ""); 
			if(!cufFrom.doesHaveCompositeFeedId(feedId)) {
				result.put(feedId, "User's ["+userName+"] composite feed id ["+cufFrom+"] doesn't have feedId ["+feedId+"]"); 
				log.debug("User's ["+userName+"] composite feed id ["+cufFrom+"] doesn't have feedId ["+feedId+"]");
				continue;
			}
			// move id only
			cufFrom.getFeedIds().remove(feedId);
			log.debug("User's ["+userName+"] feedId ["+feedId+"] was removed from composite user feed id ["+cufFrom+"]");
			if(cufTo.getFeedIds().add(feedId)) {
				log.debug("FeedId ["+feedId+"] was added to composite user feed id ["+cufTo+"] for the user ["+userName+"]");
			}else{
				log.error("FeedId ["+feedId+"] wasn't added to composite user feed id ["+cufTo+"] for the user ["+userName+"]");
				result.put(feedId, "FeedId ["+feedId+"] wasn't added to composite user feed id ["+cufTo+"] for the user ["+userName+"]"); 
				continue;
			}
			// move content from compositeFeedFrom to compositeFeedTo
			RSS rssFeed = RSS.getRSSObjectByFeedId(feedId);
			ArrayList<Item> rssFromItems = (ArrayList<Item>)rssFrom.getChannel().getItem().clone();
			int countOfSuccessfullyMovedItems = 0;
			// iterate over composite feed from
			for(Item compositeFeedFrom : rssFromItems) {
				// check if Item exists in rssFeed 
				if(rssFeed.getChannel().containsItem(compositeFeedFrom)) {
					// then move it from rssFrom to rssTo
					rssTo.getChannel().getItem().add(compositeFeedFrom);
					// delete from rssFrom
					if(rssFrom.getChannel().getItem().removeIf(t -> t.getGuid().toString().equals(compositeFeedFrom.getGuid().toString()))) countOfSuccessfullyMovedItems++;
					//
					 
				}
			}
			result.put(feedId, "Successfully moved ["+countOfSuccessfullyMovedItems+"] items for feed [" + rssFeed.getChannel().getTitle() + "]");
		}
		rssFrom.saveXMLObjectToFileByFeedId(compositeFeedIdFrom);
		rssTo.saveXMLObjectToFileByFeedId(compositeFeedIdTo);
		user.saveXMLObjectToFileByLogin();

		return result;
	}
	
//	new Response(e){
//		private Exception e;
//		public Response(Exception e) {
//			this.e=e;
//		}
//	}
	
	private String printFeedIdsFromList(ArrayList<String> feedIds) {
		boolean first=true;
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for(String feedId : feedIds) {
			if(!first) {
				sb.append(",");
			}
			first=false;
			sb.append(feedId);
		}
		sb.append(']');
		return sb.toString();
	}
	
}

