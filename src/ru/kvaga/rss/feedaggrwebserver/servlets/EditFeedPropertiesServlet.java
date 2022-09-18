package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

/**
 * Servlet implementation class EditFeedProperties
 */
@WebServlet("/EditFeedProperties")
public class EditFeedPropertiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(EditFeedPropertiesServlet.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditFeedPropertiesServlet() {
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
		String command = (String)request.getParameter("command");
		String feedId = (String)request.getParameter("feedId");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		log.debug("Got parameters " + ServerUtils.listOfParametersToString("feedId", feedId, "command", command, "userName", userName, "redirectTo", redirectTo, "source", source));

		try {
			if(command.toLowerCase().equals("getFeedPreperties".toLowerCase())) {
				if(feedId.startsWith("composite_")) {
					response.getWriter().write(OBJECT_MAPPER.writeValueAsString(getCompositeUserFeedProperties(userName, feedId)));
				}else {
					response.getWriter().write(OBJECT_MAPPER.writeValueAsString(getUserFeedProperties(userName, feedId)));
				}
			}else if(command.toLowerCase().equals("updateFeedProperties".toLowerCase())) {
				if(feedId.startsWith("composite_")) {
					response.getWriter().write(OBJECT_MAPPER.writeValueAsString(updateCompositeUserFeedProperties(userName, "")));
				}else {
					response.getWriter().write(OBJECT_MAPPER.writeValueAsString(updateUserFeedProperties(userName, feedId, feedId, feedId, false, 0)));
				}
			}
		}catch (Exception e) {
			log.error("Exception on EditFeedPropertiesServlet", e);
			request.setAttribute("Exception", e);
		}	finally {
			//rd.include(request, response);
		}
	}
	
	private Object updateUserFeedProperties(String userName, String oldFeedId, String newFeedId, String title, 
			boolean suspendStatus, long durationInMillisForUpdate) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		if(!oldFeedId.equals(newFeedId)) {
			if(RSS.getRSSFileByFeedId(oldFeedId).renameTo(RSS.getRSSFileByFeedId(newFeedId))) {
				user.getUserFeedByFeedId(oldFeedId).setId(newFeedId);
			}else {
				throw new Exception("Couldn't change name of RSS feed from ["+oldFeedId+"] to the new ["+newFeedId+"]");
			}
		}
		UserFeed uf = user.getUserFeedByFeedId(oldFeedId);
		uf.setUserFeedTitle(title);
		uf.setSuspendStatus(suspendStatus);
		uf.setDurationInMillisForUpdate(durationInMillisForUpdate);
		user.saveXMLObjectToFileByLogin();
		return user.getUserFeedByFeedId(newFeedId);
	}

	private Object updateCompositeUserFeedProperties(String userName, String feedId) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeUserFeed getCompositeUserFeedProperties(String userName, String feedId) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		return user.getCompositeUserFeedById(feedId);
	}
	
	private UserFeed getUserFeedProperties(String userName, String feedId) throws Exception {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		return user.getUserFeedByFeedId(feedId);
	}

	public User exportCompositeFeed(String userName) throws Exception {
		// export part from user
		return User.getXMLObjectFromXMLFileByUserName(userName);
	}
	
	


}
