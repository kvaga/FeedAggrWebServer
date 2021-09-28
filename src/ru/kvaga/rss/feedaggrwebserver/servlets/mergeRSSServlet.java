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
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;

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
		String redirectTo = request.getParameter("redirectTo");
		RequestDispatcher rd = null;
		if(redirectTo!=null) {
			rd = getServletContext().getRequestDispatcher("/"+redirectTo);
		}else {
			rd = getServletContext().getRequestDispatcher("/LoginSuccess.jsp");
		}
		PrintWriter out = response.getWriter();

		try {

			String userName = (String) request.getSession().getAttribute("login");
			String compositeFeedID=request.getParameter("feedId");
			log.debug("Getting list of incoming feed ids ["+userName+"]:");
			;
			ArrayList<String> newlyAddedFeedIdList = new ArrayList<String>();
			ArrayList<String> feedIdList = new ArrayList<String>();
			Enumeration<String> en = request.getParameterNames();
			while (en.hasMoreElements()) {
				String parameter = en.nextElement();
				if (parameter.startsWith("id_")) {
					log.debug("Parameter [" + parameter + "]: [" + request.getParameter(parameter)+"]");
					feedIdList.add(request.getParameter(parameter));
				}
			}
			String compositeRSSTitle = request.getParameter("compositeRSSTitle");
//			ServerUtils.mergeRSS(compositeRSSTitle, userName, feedIdList, null);
			if(compositeFeedID==null) {
				compositeFeedID = ServerUtils.createCompositeRSS(userName, compositeRSSTitle, feedIdList);
				newlyAddedFeedIdList = feedIdList;
			}else {
				// Get a list of feeds ids which are new for the current list for specific user
				User user = User.getXMLObjectFromXMLFileByUserName(userName);
				for(String fId : feedIdList) {
					// Check if current composite feed doesn't have incoming feed
					if(!user.getCompositeUserFeedById(compositeFeedID).getFeedIds().contains(fId)){
						newlyAddedFeedIdList.add(fId);
					}
				}
				
				ServerUtils.updateCompositeRSS(compositeFeedID, userName, compositeRSSTitle, feedIdList, true);
				/*
				// other work
				if(request.getParameter("appendSingleFeedIds")!=null) {
					ServerUtils.updateCompositeRSS(compositeFeedID, userName, compositeRSSTitle, feedIdList, true);
				}else {
					ServerUtils.updateCompositeRSS(compositeFeedID, userName, compositeRSSTitle, feedIdList);
				}
				*/
			}
			ServerUtils.updateCompositeRSSFilesOfUser(userName, compositeFeedID, newlyAddedFeedIdList);
			out.print("<font color=\"green\">Composite feed ["+compositeRSSTitle+"] successfully updated</font><br>");
		} catch (Exception e) {
			log.error("Exception: ", e);
			out.print("<font color=red>Error: " + e.getMessage() + "</font>");
			
		}
		rd.include(request, response);
//		response.sendRedirect("LoginSuccess.jsp");

		/* TODO: implement adding information about composite to User File */
		/* TODO: create job */

	}

}
