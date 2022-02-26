package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;

/**
 * Servlet implementation class CompositeFeedsServlet
 */
@WebServlet("/CompositeFeeds")
public class CompositeFeedsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(CompositeFeedsServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CompositeFeedsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	//if (true) throw new ServletException("unimplemented");

    	if(true) {
    		throw new ServletException("Deprecated");
    	}
		String redirectTo = request.getParameter("redirectTo");
		String userName = (String) request.getSession().getAttribute("login");
		String command = (String) request.getParameter("command");
		
		String compositeFeedID=request.getParameter("compositeFeedId");
		String compositeRSSTitle = request.getParameter("compositeRSSTitle");
		
		ArrayList<String> feedIdList = request.getParameterValues("feedId")==null ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(request.getParameterValues("feedId")));
		
		
		
		RequestDispatcher rd = null;
		if(redirectTo!=null) {
			rd = getServletContext().getRequestDispatcher("/"+redirectTo);
		}else {
			rd = getServletContext().getRequestDispatcher("/LoginSuccess.jsp");
		}
		//PrintWriter out = response.getWriter();
		try {
			if(command==null) {
	            throw new Exception("Unknown command value ["+command+"]");
			}
			String responseStatus = compositeFeedsServletExec(command, compositeFeedID, compositeRSSTitle, userName, feedIdList);
			request.setAttribute("responseStatus", responseStatus);
		} catch (Exception e) {
			log.error("Exception", e);
			request.setAttribute("responseStatus", Exec.getHTMLFailText(e.getMessage()));
		}
		
		if(true) {
			request.setAttribute("responseStatus", "This servet was deprecated");
		}
		rd.include(request, response);
//		response.sendRedirect("LoginSuccess.jsp");

		/* TODO: implement adding information about composite to User File */
		/* TODO: create job */

	}

	private String compositeFeedsServletExec(String command, String compositeFeedID, String compositeRSSTitle, String userName, ArrayList<String> appendedfeedIdsList) {
		StringBuilder sbResponseStatus = new StringBuilder();
		try {
			log.debug("Got parameters command ["+command+"], compositeFeedID ["+compositeFeedID+"], compositeRSSTitle ["+compositeRSSTitle+"], userName ["+userName+"], appendedfeedIdsList ["+appendedfeedIdsList+"]");
			switch (command.toLowerCase()) {
		        case  ("appendnewuserfeeds"):
		        	int[] result = CompositeUserFeed.appendUserFeedsToCompositeUserFeed(compositeFeedID, appendedfeedIdsList, userName );
		        	log.debug("Appended [" + result[0] +"] feedIds to the compositeFeedID ["+compositeFeedID+"] and appended ["+result[1]+"] feed items to a composite file and user ["+userName+"]");
		        	sbResponseStatus.append(Exec.getHTMLSuccessText("Appended [" + result[0] +"] feedIds to the compositeFeedID ["+compositeFeedID+"] and appended ["+result[1]+"] feed items to a composite file and user ["+userName+"]"));
		        	
		            break;
		        default:
		            throw new Exception("Unknown command value ["+command+"]");
		            //break;
			}
		}catch(Exception e) {
			log.error("Exception", e);
			sbResponseStatus.append(Exec.getHTMLFailText(e));
		}
		return sbResponseStatus.toString();
	}

}
