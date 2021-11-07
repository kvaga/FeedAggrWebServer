package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.monitoring.MonitoringUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;

/**
 * Servlet implementation class ExportCompositeFeedServlet
 */
@WebServlet("/ExportCompositeFeed")
public class ExportCompositeFeedServlet extends HttpServlet {
	final private static Logger log = LogManager.getLogger(ExportCompositeFeedServlet.class);

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExportCompositeFeedServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = (String)request.getParameter("userName");
		String compositeFeedId = request.getParameter("compositeFeedId");
		String redirectTo = (String)request.getParameter("redirectTo");
		String source = (String)request.getParameter("source");
		String fileName = "ExportCompositeFeed_"+ServerUtils.getNewFeedId()+".xml";
		log.debug("Got parameters " + ServerUtils.listOfParametersToString("userName", userName, "compositeFeedId", compositeFeedId, "redirectTo", redirectTo, "source", source));
//		response.setContentType("text/plain");
//		response.setHeader("Content-disposition", "attachment; filename=sample.txt");
		response.setContentType("APPLICATION/OCTET-STREAM; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		//RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));
		
		try {
			//request.setAttribute("compositeFeedList", getCompositeFeedList(userName));
			ExportCompositeFeedServletResult exportCompositeFeedServletResult = exportCompositeFeed(userName, compositeFeedId);
			response.getWriter()
			.write(resultToString(exportCompositeFeedServletResult));
//			try(InputStream in = request.getServletContext().getResourceAsStream("/WEB-INF/sample.txt");
//					OutputStream out = response.getOutputStream()) {
//			        byte[] buffer = new byte[1024];
//			        int numBytesRead;
//			        while ((numBytesRead = in.read(buffer)) > 0) {
//			                out.write(buffer, 0, numBytesRead);
//			            }
//			        }
			//log.debug("Sent the response attribute compositeFeedList with value size ["+((ArrayList<CompositeFeedTotalInfo>)request.getAttribute("compositeFeedList")).size()+"]");
		}catch (Exception e) {
			log.error("Exception on ExportCompositeFeedServlet", e);
			request.setAttribute("Exception", e);
		}	finally {
			//rd.include(request, response);
		}
	}
	
	public ExportCompositeFeedServletResult exportCompositeFeed(String userName, String compositeFeedId) throws Exception {
		// export part from user
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		CompositeUserFeed cuf = user.getCompositeUserFeedById(compositeFeedId);
		
		// export RSS compositeFeed
		RSS compositeRSS = RSS.getRSSObjectByFeedId(compositeFeedId);
		
		// export RSS Feeds of composite
		ArrayList<RSS> feedRSSList = new ArrayList<RSS>();
		for(String feedId : cuf.getFeedIds()) {
			feedRSSList.add(RSS.getRSSObjectByFeedId(feedId));
		}
//		return new ExportCompositeFeedServletResult(cuf, compositeRSS, feedRSSList);
		return new ExportCompositeFeedServletResult(cuf, compositeRSS, feedRSSList);

	}
	
	public String resultToString(ExportCompositeFeedServletResult exportCompositeFeedServletResult) throws JAXBException {
		 JAXBContext context = JAXBContext.newInstance(ExportCompositeFeedServletResult.class, CompositeUserFeed.class, RSS.class);
        StringWriter sw = new StringWriter();
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
        
        
        m.marshal(exportCompositeFeedServletResult, sw);
        return sw.toString();
	}

}
