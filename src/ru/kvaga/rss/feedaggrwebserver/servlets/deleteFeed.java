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

import ru.kvaga.rss.feedaggr.Exec;
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
		//String feedId = request.getParameter("feedId");
		
		String userName=(String) request.getSession().getAttribute("login");
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		RequestDispatcher rd = request.getParameter("redirectTo")!=null? getServletContext().getRequestDispatcher(request.getParameter("redirectTo")) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp");
		PrintWriter out = response.getWriter();

		try {

			for(String feedId : request.getParameterValues("feedId")) {
				log.debug("Got request for deleteing feed id ["+feedId+"] for the user ["+userName+"]");
				sb.append("Status of deletion of feedId ["+feedId+"]: ");
				if(ServerUtils.deleteFeed(feedId, userName)) {
					sb.append(Exec.getHTMLSuccessText("SUCCESS"));
				}else {
					sb.append(Exec.getHTMLFailText("FAIL"));
				}
				sb.append("<br>");
			}
			sb.append("<br></html>");
			out.print(sb.toString());
		} catch (Exception e) {
			log.error("DeleteFeed exception", e);
			log.error("Can't remove feed.");
			out.print("<font color=red>Can't remove feed</font>");
		}	finally {
			rd.include(request, response);
		}
	}

}
