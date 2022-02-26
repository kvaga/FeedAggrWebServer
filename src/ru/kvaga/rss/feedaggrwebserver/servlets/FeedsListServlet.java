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
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

/**
 * Servlet implementation class CompositeFeedsList
 */
@WebServlet("/FeedsList")
public class FeedsListServlet extends HttpServlet {
	final private static Logger log = LogManager.getLogger(FeedsListServlet.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeedsListServlet() {
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
		String responseType = request.getParameter("type");
		String shortInfo = request.getParameter("short");
		
		log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName, "type", responseType, "short", shortInfo));
		
		
		
		try {
			if(responseType!=null && responseType.equals("json")) {
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					if(shortInfo.equals("true")) {
						response.getWriter().write(OBJECT_MAPPER.writeValueAsString(getFeedListShortInfo(userName).toArray()));
					}else {
						response.getWriter().write(OBJECT_MAPPER.writeValueAsString(getFeedList(userName).toArray()));
					}
			}else {
				RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));
				request.setAttribute("compositeFeedList", getFeedList(userName));
				rd.include(request, response);
				log.debug("Sent the response attribute compositeFeedList with value size ["+((ArrayList<CompositeFeedTotalInfo>)request.getAttribute("compositeFeedList")).size()+"]");
			}

		}catch (Exception e) {
			log.error("Exception on CompositeFeedsListServlet", e);
			request.setAttribute("Exception", e);
		}	

	}

	public ArrayList<UserFeed> getFeedListShortInfo(String userName) throws GetFeedsListByUser, JAXBException, IOException {
		ArrayList<UserFeed> feedsShortInfoList = new ArrayList<UserFeed>();
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		// title compositefeedid countOfFeeds 
		for(UserFeed userFeed : user.getUserFeeds()) {
			feedsShortInfoList.add(userFeed);
		}
		return feedsShortInfoList;
	}
	
	public ArrayList<CompositeFeedTotalInfo> getFeedList(String userName) throws Exception {
		if(true) {
			throw new Exception("Unimplemented method getFeedList");
		}
		ArrayList<CompositeFeedTotalInfo> compositeFeedTotalInfoList = new ArrayList<CompositeFeedTotalInfo>();
		for(Feed feedOnServer : ServerUtils.getFeedsList(false, true)) {
				RSS rssFeed = RSS.getRSSObjectFromXMLFile(feedOnServer.getXmlFile());
				Date[] oldestNewest = rssFeed.getOldestNewestPubDate();
				compositeFeedTotalInfoList.add(new CompositeFeedTotalInfo(
							feedOnServer.getId(), rssFeed.getChannel().getTitle(), 
							rssFeed.getChannel().getLastBuildDate(), 
							rssFeed.getChannel().getItem().size(), 
							Exec.getFileSizeByFeedId(feedOnServer.getId()), 
							oldestNewest[0], 
							oldestNewest[1])
							);
		}
		return compositeFeedTotalInfoList;
	}

}
