package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.ArrayList;

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

import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

/**
 * Servlet implementation class FixLostRSSURLsAndTitlesFromUserFile
 */
@WebServlet("/FixLostRSSURLsAndTitlesFromUserFile")
public class FixLostRSSURLsAndTitlesFromUserFileServlet extends HttpServlet {
	final private static Logger log = LogManager.getLogger(FixLostRSSURLsAndTitlesFromUserFileServlet.class);
	private static final long serialVersionUID = 1L;
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


	    /**
	     * @see HttpServlet#HttpServlet()
	     */
	    public FixLostRSSURLsAndTitlesFromUserFileServlet() {
	        super();
	        // TODO Auto-generated constructor stub
	    }

		/**
		 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
		 */
	    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String userName = (String)request.getParameter("userName");
			String redirectTo = (String)request.getParameter("redirectTo");
			String source = (String)request.getParameter("source");
//			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			log.debug("Got parameters " + ServerUtils.listOfParametersToString("userName", userName, "redirectTo", redirectTo, "source", source));

			try {
				RequestDispatcher rd = redirectTo !=null? request.getRequestDispatcher(redirectTo) : (source!=null ? request.getRequestDispatcher(source) : request.getRequestDispatcher("/LoginSuccess.jsp"));
//				response.getWriter().write(OBJECT_MAPPER.writeValueAsString(fixLostRSSURLsAndTitlesFromUserFile(userName).toArray()));
				request.setAttribute("FixedList", fixLostRSSURLsAndTitlesFromUserFile(userName));
//				request.setAttribute("message", "QQQQ");

				rd.forward(request, response);

			}catch (Exception e) {
				log.error("Exception on FixLostRSSURLsAndTitlesFromUserFile", e);
				request.setAttribute("Exception", e);
			}	finally {
				//rd.include(request, response);
				 
			}
		}
		
		private ArrayList<String> fixLostRSSURLsAndTitlesFromUserFile(String userName) throws JAXBException {
			User user = User.getXMLObjectFromXMLFileByUserName(userName);
			ArrayList<String> listOfFixedRSS = new ArrayList<String>();
			for(UserFeed uf : user.getUserFeeds()) {
				RSS rss = RSS.getRSSObjectByFeedId(uf.getId());
				boolean needFix = false;
				if(uf.getUserFeedTitle()!=null && !rss.getChannel().getTitle().equals(uf.getUserFeedTitle())) {
					rss.getChannel().setTitle(uf.getUserFeedTitle());
					needFix=true;
				}
				if(uf.getUserFeedUrl()!=null && !rss.getChannel().getLink().equals(uf.getUserFeedUrl()) ) {
					rss.getChannel().setLink(uf.getUserFeedUrl());
					needFix=true;
				}
				if(needFix) {
					rss.saveXMLObjectToFileByFeedId(uf.getId());
					log.debug("Fixed feed id ["+uf.getId()+"]");
					listOfFixedRSS.add(uf.getUserFeedTitle());
				}

			}
			return listOfFixedRSS;
		}
}


