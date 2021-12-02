package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
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
	 * Create a new one composite feed or update existed one and add feedIds to it
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String userName = (String) request.getSession().getAttribute("login");
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
			String responseStatus = mergeRSSServletExec(compositeFeedID, compositeRSSTitle, userName, feedIdList);
			request.setAttribute("responseStatus", responseStatus);
		} catch (Exception e) {
			log.error("Exception", e);
			request.setAttribute("responseStatus", Exec.getHTMLFailText(e.getMessage()));
		}
		
		
		rd.include(request, response);
//		response.sendRedirect("LoginSuccess.jsp");

		/* TODO: implement adding information about composite to User File */
		/* TODO: create job */

	}
	
	public String mergeRSSServletExec(String compositeFeedID, String compositeRSSTitle, String userName, ArrayList<String> feedIdList) throws Exception {
		log.debug("Got parameters compositeFeedID ["+compositeFeedID+"], compositeRSSTitle ["+compositeRSSTitle+"], userName ["+userName+"], feedIdList size ["+feedIdList+"]");
		StringBuilder sbResponseStatus = new StringBuilder();
//		ServerUtils.mergeRSS(compositeRSSTitle, userName, feedIdList, null);
		if(compositeFeedID==null) {
//			compositeFeedID = CompositeUserFeed.createCompositeRSS(userName, compositeRSSTitle, feedIdList);
//			newlyAddedFeedIds = feedIdList;
			ResultCreateCompositeFeedAndAddNewFeeds resultCreateCompositeFeedAndAddNewFeeds=null;
			if((resultCreateCompositeFeedAndAddNewFeeds=createCompositeFeedAndAddNewFeeds(compositeRSSTitle, feedIdList, userName))!=null) {
				sbResponseStatus.append(Exec.getHTMLSuccessText("Composite feed ["+compositeRSSTitle+"] successfully created and updated with new feed ids ["+ServerUtils.getStringFromArrayListWithItemsDelimeteredByComma(feedIdList))+"]<br>");
				compositeFeedID = resultCreateCompositeFeedAndAddNewFeeds.getFeedId();
			}
		}else {
			if(addNewFeedIds2ExistedCompositeFeed(compositeFeedID, feedIdList, userName)!=null) {
				sbResponseStatus.append(Exec.getHTMLSuccessText("Composite feed ["+compositeRSSTitle+"] successfully updated with new feed ids ["+ServerUtils.getStringFromArrayListWithItemsDelimeteredByComma(feedIdList)+"]<br>"));
			}
			
			if(compositeRSSTitle!=null && updateRSSTitleOfComposeFeed(compositeRSSTitle, compositeFeedID, userName)) {
				sbResponseStatus.append(Exec.getHTMLSuccessText("Composite feed's title was changed to the ["+compositeRSSTitle+"]<br>"));
			}
		}
			/*
			// other work
			if(request.getParameter("appendSingleFeedIds")!=null) {
				ServerUtils.updateCompositeRSS(compositeFeedID, userName, compositeRSSTitle, feedIdList, true);
			}else {
				ServerUtils.updateCompositeRSS(compositeFeedID, userName, compositeRSSTitle, feedIdList);
			}
			*/
		ArrayList<String> deletedFeedIdsFromCompositeFeed = deleteFeedIdsFromCompositeUserFeedDespiteFinalList(userName, compositeFeedID, feedIdList);
		if(deletedFeedIdsFromCompositeFeed.size()>0) {
			sbResponseStatus.append(Exec.getHTMLSuccessText("Deleted ["+ServerUtils.getStringFromArrayListWithItemsDelimeteredByComma(deletedFeedIdsFromCompositeFeed)+"] feed ids from the composite feed ["+compositeRSSTitle+"]<br>"));
		}
		
		
			
		sbResponseStatus.append(Exec.getHTMLSuccessText("Composite feed ["+compositeRSSTitle+"] successfully updated<br>"));
		return sbResponseStatus.toString();
		
	}
	private boolean updateRSSTitleOfComposeFeed(String compositeRSSTitle, String compositeFeedId, String userName) throws Exception {
		return CompositeUserFeed.updateRSSTitleOfComposeFeed(compositeRSSTitle, compositeFeedId, userName);
	}

	/**
	 * Delete all feed ids from composite user feed {@code compositeFeedId} despite {@code feedIdListToSave}
	 * @param userName
	 * @param compositeFeedId
	 * @param finalFeedIdListToSave
	 * return ArrayList of deleted feedIds
	 * @throws Exception 
	 */
	private ArrayList<String> deleteFeedIdsFromCompositeUserFeedDespiteFinalList(String userName, String compositeFeedId, ArrayList<String> finalFeedIdListToSave) throws Exception {
		return CompositeUserFeed.deleteFeedIdsFromCompositeUserFeedDespiteFinalList(userName, compositeFeedId, finalFeedIdListToSave);
	}

	/**
	 * Create a new one compositeFeed with {@code compositeFeedTitle} title and add {@code newlyAddedFeedIds}
	 * @param compositeFeedTitle
	 * @param userName
	 * @param newlyAddedFeedIds
	 * @throws Exception 
	 */
	private ResultCreateCompositeFeedAndAddNewFeeds createCompositeFeedAndAddNewFeeds(String compositeRSSTitle, ArrayList<String> newlyAddedFeedIds, String userName) throws Exception {
		ArrayList<String> newlyAddedFeedIdList = new ArrayList<String>();
		String compositeFeedId = CompositeUserFeed.createCompositeRSS(userName, compositeRSSTitle).getId();
		return new ResultCreateCompositeFeedAndAddNewFeeds(compositeFeedId, CompositeUserFeed.addNewFeeds2CompositeFeed(compositeFeedId, newlyAddedFeedIdList, userName));
	}
	class ResultCreateCompositeFeedAndAddNewFeeds{
		private String feedId;
		private RSS rss;
		public ResultCreateCompositeFeedAndAddNewFeeds(String feedId, RSS rss) {
			this.feedId=feedId;
			this.rss=rss;
		}
		public String getFeedId() {
			return feedId;
		}
		public RSS getRss() {
			return rss;
		}
	}
	/**
	 * Add {@code newlyAddedFeedIds} to the {@code compositeFeed}
	 * @param compositeFeedId
	 * @param feedIdList
	 * @param userName
	 * @throws Exception 
	 */
	private RSS addNewFeedIds2ExistedCompositeFeed(String compositeFeedId, ArrayList<String> feedIdList, String userName) throws Exception {
		ArrayList<String> newlyAddedFeedIdList = new ArrayList<String>();

		// Get a list of feeds ids which are new for the current list for specific user
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		for(String fId : feedIdList) {
			// Check if current composite feed doesn't have incoming feed
			if(!user.getCompositeUserFeedById(compositeFeedId).getFeedIds().contains(fId)){
				newlyAddedFeedIdList.add(fId);
			}
		}
		return CompositeUserFeed.addNewFeeds2CompositeFeed(compositeFeedId, newlyAddedFeedIdList, userName);
	}

}
