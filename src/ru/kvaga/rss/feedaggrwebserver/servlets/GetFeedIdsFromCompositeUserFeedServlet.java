package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;

/**
 * Servlet implementation class CompositeFeedsList
 */
@WebServlet("/GetFeedIdsFromCompositeUserFeed")
public class GetFeedIdsFromCompositeUserFeedServlet extends HttpServlet {
	final private static Logger log = LogManager.getLogger(GetFeedIdsFromCompositeUserFeedServlet.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFeedIdsFromCompositeUserFeedServlet() {
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
		String compositeFeedId = request.getParameter("compositeFeedId");

		
		log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName, "compositeFeedId", compositeFeedId));
		
		
		
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(OBJECT_MAPPER.writeValueAsString(getFeedIdsFromCompositeUserFeed(userName, compositeFeedId).toArray()));
		}catch (Exception e) {
			log.error("Exception on CompositeFeedsListServlet", e);
		}	
	}

	public ArrayList<String> getFeedIdsFromCompositeUserFeed(String userName, String compositeFeedId) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		// title compositefeedid countOfFeeds 
		return user.getCompositeUserFeedById(compositeFeedId).getFeedIds();
	}
	
}
