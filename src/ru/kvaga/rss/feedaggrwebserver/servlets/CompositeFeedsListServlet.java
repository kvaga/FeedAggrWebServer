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

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

/**
 * Servlet implementation class CompositeFeedsList
 */
@WebServlet("/CompositeFeedsList")
public class CompositeFeedsListServlet extends HttpServlet {
	final private static Logger log = LogManager.getLogger(CompositeFeedsListServlet.class);

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CompositeFeedsListServlet() {
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
		log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName));
		RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));
		
		try {
			request.setAttribute("compositeFeedList", getCompositeFeedList(userName));
			log.debug("Sent the response attribute compositeFeedList with value size ["+((ArrayList<CompositeFeedTotalInfo>)request.getAttribute("compositeFeedList")).size()+"]");
		}catch (Exception e) {
			log.error("Exception on CompositeFeedsListServlet", e);
			request.setAttribute("responseResultException", e);
		}	finally {
			rd.include(request, response);
		}

	}

	public ArrayList<CompositeFeedTotalInfo> getCompositeFeedList(String userName) throws GetFeedsListByUser, JAXBException, IOException {
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
