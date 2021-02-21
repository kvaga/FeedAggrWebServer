package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

/**
 * Servlet implementation class mergeRSS
 */
@WebServlet("/mergeRSS")
public class mergeRSSServlet extends HttpServlet {
	private static Logger log = LogManager.getLogger(mergeRSSServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public mergeRSSServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String userName = (String) request.getSession().getAttribute("login");

			log.debug("Getting list of incoming feed ids ["+userName+"]:");
			;
			ArrayList<String> feedIdList = new ArrayList<String>();
			Enumeration<String> en = request.getParameterNames();
			while (en.hasMoreElements()) {
				String parameter = en.nextElement();
				if (parameter.startsWith("id_")) {
					log.debug("Parameter " + parameter + ": " + request.getParameter(parameter));
					feedIdList.add(request.getParameter(parameter));
				}
			}
			String compositeRSSTitle = request.getParameter("compositeRSSTitle");
//			ServerUtils.mergeRSS(compositeRSSTitle, userName, feedIdList, null);
			ServerUtils.createCompositeRSS(userName, compositeRSSTitle, feedIdList);
			ServerUtils.updateCompositeRSSFilesOfUser(userName);
		} catch (Exception e) {
			log.error("Exception: ", e);
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/mergeRSS.jsp");
			PrintWriter out = response.getWriter();
			out.print("<font color=red>Error: " + e.getMessage() + "</font>");
			rd.include(request, response);
		}
		response.sendRedirect("FeedsList.jsp");

		/* TODO: implement adding information about composite to User File */
		/* TODO: create job */

	}

}
