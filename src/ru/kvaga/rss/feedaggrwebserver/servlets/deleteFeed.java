package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

/**
 * Servlet implementation class deleteFeed
 */
@WebServlet("/deleteFeed")
public class deleteFeed extends HttpServlet {
	final private static Logger log = LogManager.getLogger(deleteFeed.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public deleteFeed() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String feedId = request.getParameter("feedId");
		String userName=(String) request.getSession().getAttribute("login");
		try {
			ServerUtils.deleteFeed(feedId, userName);
			response.sendRedirect("LoginSuccess.jsp");
		} catch (Exception e) {
			log.error("DeleteFeed exception", e);
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/showFeed");
			PrintWriter out = response.getWriter();
			log.error("Can't remove feed.");
			out.print("<font color=red>Can't remove feed</font>");
			rd.include(request, response);
		}	
//		if(userName!=null && userName.equals("") && feedId!=null && !feedId.equals("")) {
//			log.debug("Trying to delete feed id ["+feedId+"] for user ["+userName+"]");
//			try {
//				ServerUtils.deleteFeed(feedId, userName);
//				response.sendRedirect("LoginSuccess.jsp");
//			} catch (Exception e) {
//				log.error("DeleteFeed exception", e);
//				RequestDispatcher rd = getServletContext().getRequestDispatcher("/showFeed");
//				PrintWriter out = response.getWriter();
//				log.error("Can't remove feed.");
//				out.print("<font color=red>Can't remove feed</font>");
//				rd.include(request, response);
//			}		
//		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
