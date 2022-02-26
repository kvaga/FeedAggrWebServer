package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.servlets.utils.ServletUtils;

/**
 * Servlet implementation class DeleteOldFeedItemsServlet
 */
@WebServlet("/DeleteOldFeedItems")
public class DeleteOldFeedItemsServlet extends HttpServlet {
	private static Logger log = LogManager.getLogger(DeleteOldFeedItemsServlet.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteOldFeedItemsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = (String) request.getSession().getAttribute("login");
		String redirectTo = request.getParameter("redirectTo");
		String source = (String)request.getParameter("source");

		log.debug("Got parameters " + ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName, "countOfDaysForDeletion", request.getParameter("countOfDaysForDeletion")));
		if(source == null && redirectTo == null) {
			source = ServletUtils.getSource(request);
		}
		
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/"+redirectTo);
		PrintWriter out = response.getWriter();

		try {
			
			//String compositeFeedID=request.getParameter("feedId");
			// get time fo deleteion
			log.info("Got time period for deletion ["+request.getParameter("countOfDaysForDeletion")+"]");
			int countDays = Integer.parseInt(request.getParameter("countOfDaysForDeletion"));
			
			log.info("Getting list of feed ids of user ["+userName+"] which intends for deletion");
			ArrayList<String> feedIdList = new ArrayList<String>();
			for (String feedId : request.getParameterValues("feedId")) {
				feedIdList.add(feedId);
			}
			HashMap<String, Integer> result = new HashMap<String, Integer>();
			log.info("Got a list of feed ids with size ["+feedIdList.size()+"]");
			for(String feedId : feedIdList) {
				if(feedId!=null && feedId.startsWith("composite_")) {
					User user = User.getXMLObjectFromXMLFileByUserName(userName);
					CompositeUserFeed cuf = user.getCompositeUserFeedById(feedId);
					for(String childFeedId: cuf.getFeedIds()) {
						result.put(childFeedId, ServerUtils.deleteOldFeedItems(childFeedId, countDays));
					}
				}
				result.put(feedId, ServerUtils.deleteOldFeedItems(feedId, countDays));
			}
			for(String fId : result.keySet()) {
				out.println("<font color=green>For feed id ["+fId+"] were deleted ["+result.get(fId)+"] old items</font><br>");
			}
			//response.sendRedirect(redirectTo);
		}catch(Exception e) {
			log.error("Exception during deletion old feed items for user ["+userName+"]. Redirect to ["+redirectTo+"]", e);
			out.print("<font color=red>Error during deletion: Message ["+e.getMessage()+"], Cause: ["+e.getCause()+"]</font>");
		}finally {
			rd.include(request, response);
		}
	}

}
