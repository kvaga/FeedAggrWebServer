package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggrwebserver.objects.user.User;

/**
 * Servlet implementation class renameFeedServlet
 */
@WebServlet("/renameFeed")
public class renameFeedServlet extends HttpServlet {
	private static final Logger log = LogManager.getLogger(renameFeedServlet.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public renameFeedServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = null;
		PrintWriter out = response.getWriter();

		log.debug("Requested changing old feed id ["+request.getParameter("oldFeedId")+"] to new feed id ["+request.getParameter("newFeedId")+"] for user ["+request.getParameter("login")+"] and callback to the path ["+request.getParameter("callback")+"]");
		if(request.getParameter("callback")!=null) {
			rd = getServletContext().getRequestDispatcher(request.getParameter("callback")+"&feedId="+request.getParameter("newFeedId"));
		}else {
			rd = getServletContext().getRequestDispatcher("/Login.html");
		}
		
		if(request.getParameter("oldFeedId")==null || request.getParameter("newFeedId")==null || request.getParameter("login")==null) {
			log.error("Either user name or password is wrong.");
			out.print("<font color=red>Either [oldFeedId or newFeedId or login] parameters is null</font><br>");
			rd.include(request, response);
			return;
		}
		
//		User user =null;
		try {
//			user = User.getXMLObjectFromXMLFileByUserName(request.getParameter("login"));
//			user.renameFeedWithoutSavingToFile(request.getParameter("oldFeedId"), request.getParameter("newFeedId"));
//			user.saveXMLObjectToFileByLogin(request.getParameter("login"));
			User.changeFeedIdByUserNameWithSaving(request.getParameter("login"), request.getParameter("oldFeedId"), request.getParameter("newFeedId"));
			log.debug("User's ["+request.getParameter("login")+"] oldFeedId ["+request.getParameter("oldFeedId")+"] changed to ["+ request.getParameter("newFeedId")+"]");
		} catch (Exception e) {
			out.print("<font color=red>Exception: "+e.getMessage() + ", cause: " + e.getCause() +"</font><br>");
			rd.include(request, response);
			return;
		}
		out.print("<font color=green>Feed id changed from ["+request.getParameter("oldFeedId")+"] to ["+request.getParameter("newFeedId")+"]</font><br>");
		
		rd.include(request, response);
	}

}
