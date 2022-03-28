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
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

/**
 * Servlet implementation class ExportCompositeFeedServlet
 */
@WebServlet("/ExportUserFile")
public class ExportUserFileServlet extends HttpServlet {
	final private static Logger log = LogManager.getLogger(ExportUserFileServlet.class);

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExportUserFileServlet() {
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
		String fileName = "ExportUserFile_"+userName+"_"+ServerUtils.getNewFeedId()+".xml";
		log.debug("Got parameters " + ServerUtils.listOfParametersToString("userName", userName, "redirectTo", redirectTo, "source", source));
//		response.setContentType("text/plain");
//		response.setHeader("Content-disposition", "attachment; filename=sample.txt");
		response.setContentType("APPLICATION/OCTET-STREAM; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		//RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));
		
		try {
			//request.setAttribute("compositeFeedList", getCompositeFeedList(userName));
			response.getWriter()
			.write(resultToString(exportCompositeFeed(userName)));
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
	
	public User exportCompositeFeed(String userName) throws Exception {
		// export part from user
		return User.getXMLObjectFromXMLFileByUserName(userName);
	}
	
	public String resultToString(User user) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(User.class);
        StringWriter sw = new StringWriter();
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
        m.marshal(user, sw);
        return sw.toString();
	}

}
