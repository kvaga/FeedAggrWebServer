package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserRssItemPropertiesPatterns;

/**
 * Servlet implementation class addAbandonedFeedToUserServlet
 */
@WebServlet("/addAbandonedFeedToUser")
public class addAbandonedFeedToUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(addAbandonedFeedToUserServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public addAbandonedFeedToUserServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	private void addAbandonedFeedToUser(String feedId, HttpServletRequest request, HttpServletResponse response) throws Exception {
			if (feedId.contains("composite")) {
//				CompositeUserFeed compositeUserFeed = new CompositeUserFeed();
//				compositeUserFeed.setId(request.getParameter("feedId"));
//				RSS rss = RSS.getRSSObjectByFeedId(request.getParameter("feedId"));
//				ArrayList<String> feedIds = new ArrayList<String>();
//				for(Item item : rss.getChannel().getItem()) {
//					feedIds.add(rss.getChannel().get)
//				}
				throw new Exception("You can't add composite feeds beacuse imposible to explore root feed ids");
			}else {
				log.debug("Trying to add feed id ["+feedId+"]");
				User user = User.getXMLObjectFromXMLFileByUserName((String)request.getSession().getAttribute("login"));
				RSS rss = RSS.getRSSObjectByFeedId(feedId);
				String url = rss.getChannel().getLink();
				String domain = Exec.getDomainFromURL(url);
				if(domain==null) {
					throw new Exception("Can't find domain for url ["+url+"]");
				}
				String repeatableSearchPattern = user.getRepeatableSearchPatternByDomain(domain);
				if(repeatableSearchPattern==null) {
					throw new Exception("Can't find repeatableSearchPattern for domain ["+domain+"] and user ["+request.getSession().getAttribute("login")+"]");		
				}
				UserRssItemPropertiesPatterns userRssItemPropertiesPatterns = user.getRssItemPropertiesPatternByDomain(domain);
				if(userRssItemPropertiesPatterns==null) {
					throw new Exception("Can't find userRssItemPropertiesPatterns for domain ["+domain+"] and user ["+request.getSession().getAttribute("login")+"]");		
				}
				UserFeed userFeed = new UserFeed(feedId, userRssItemPropertiesPatterns.getPatternTitle(), userRssItemPropertiesPatterns.getPatternLink(), userRssItemPropertiesPatterns.getPatternDescription(), repeatableSearchPattern, "", "",ConfigMap.DEFAULT_DURATION_IN_MILLIS_FOR_FEED_UPDATE, rss.getChannel().getTitle(), url);
				if(user.getUserFeeds().add(userFeed)) {
					user.saveXMLObjectToFileByLogin((String)request.getSession().getAttribute("login"));
					log.debug("Feed id ["+feedId+"] successfully added to the user ["+request.getSession().getAttribute("login")+"]");		
				}else {
					throw new Exception("Probably user ["+request.getSession().getAttribute("login")+"]["+user.getUserFeeds().contains(userFeed)+"] already has feed id ["+feedId+"]");		
				}
				
			}
			
		
	}
	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");

		String redirectTo = "LoginSuccess.jsp";
		String feedId=null;
		PrintWriter out = response.getWriter();

		try {
			if (request.getParameter("redirectTo") != null) {
				redirectTo = request.getParameter("redirectTo");
			}
			log.debug("Set redirect to [" + redirectTo + "]");
//			response.sendRedirect(redirectTo);

		if(request.getParameter("batch")!=null) {
			log.debug("Got batch parameter");
			//Iterator<String> iter = request.getParameterNames().asIterator();
			for(String fId: request.getParameterValues("feed_id_")) {
				
				
					feedId=fId.replace("feed_id_", "");
					addAbandonedFeedToUser(feedId, request, response);
					out.print("<font color=green>Abandoned feed id ["+feedId + "] was added to the user ["+request.getSession().getAttribute("login")+"]</font><br>");
				
			}
		}else {
			log.debug("Got feedId ["+request.getParameter("feedId")+"]");
			feedId=request.getParameter("feedId");
			addAbandonedFeedToUser(feedId, request, response);
			out.print("<font color=green>Abandoned feed id ["+feedId + "] was added to the user ["+request.getSession().getAttribute("login")+"]</font><br>");
		}
//		response.sendRedirect(redirectTo);
	} catch (Exception e) {
		log.error("AddAbandonedFeedToUserServlet exception for user ["+request.getSession().getAttribute("login")+"] and feed id ["+request.getParameter("feedId")+"]", e);
		out.print("<font color=red>Can't add abandoned feed id ["+request.getParameter("feedId") + "] to user ["+request.getSession().getAttribute("login")+"]. Exception: "+e.getMessage()+". ExceptionCause: "+e.getCause()+"</font><br>");
	}
	if(!redirectTo.startsWith("/")) {
		redirectTo="/"+redirectTo;
	}
	RequestDispatcher rd = getServletContext().getRequestDispatcher(redirectTo);
	rd.include(request, response);
	}

}
