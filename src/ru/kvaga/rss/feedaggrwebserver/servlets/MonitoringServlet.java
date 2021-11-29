package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.jobs.CompositeFeedsUpdateJob;
import ru.kvaga.rss.feedaggrwebserver.jobs.FeedsUpdateJob;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;

/**
 * Servlet implementation class MonitoringServlet
 */
@WebServlet("/Monitoring")
public class MonitoringServlet extends HttpServlet {
	final static Logger log = LogManager.getLogger(MonitoringServlet.class);

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MonitoringServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = (String)request.getParameter("redirectTo");
		if(redirectTo==null) {
			redirectTo=(String)request.getAttribute("redirectTo");
		}
		//System.err.println("Referer: " + request.getHeader("referer"));
		
		String source = (String)request.getParameter("source");
		log.debug("Got parameters " + ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source));
		if(source == null && redirectTo == null) {
			source = getSource(request);
		}
		RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));

		try {
			MonitoringInfo monitoringInfo = new MonitoringInfo();
			monitoringInfo.setCompositeFeedUpdateJobIsWorkingNow(CompositeFeedsUpdateJob.isWorkingNow);
			monitoringInfo.setFeedsUpdateJobIsWorkingNow(FeedsUpdateJob.isWorkingNow);
			request.setAttribute("monitoringInfo", monitoringInfo);
			//Thread.sleep(50000);
		} catch (Exception e) {
			log.error("UserListServlet exception", e);
			request.setAttribute("Exception", e);
		}	finally {
			rd.include(request, response);
		}
	}

	Pattern p = Pattern.compile(".*/(?<source>.*)");
	private String getSource(HttpServletRequest request) {
		String referer = request.getHeader("referer");
		log.debug("Referer is ["+referer+"]");		
		Matcher m = p.matcher(referer);
		if(m.find()) {
			referer = "/" + m.group("source");
		}else {
			referer = "/";
		}
		return referer;
	}

}
