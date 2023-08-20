package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.servlets.utils.ServletUtils;

/**
 * Servlet implementation class UserSettingsServlet
 */
@WebServlet("/UserSettingsServlet")
public class UserSettingsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(UserSettingsServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserSettingsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String source = request.getParameter("source");
		String userName = (String)request.getSession().getAttribute("login");

		String command = request.getParameter("command");
		
		System.out.println("contentType: " + request.getContentType()  + " method: " + request.getMethod());
		RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));

		try {
			log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName, "command", command));
			if(request.getParameter("property")!=null && request.getParameter("property").equals("")){
				request.setAttribute("Exception", new String("Property can't be empty"));
			}
			 User user = User.getXMLObjectFromXMLFileByUserName(userName); 
			HashMap<String,String> props = user.getCompositeUserFeedCommonSettings();
			if(command.equalsIgnoreCase("GetCompositeUserFeedSettings")) {
				request.setAttribute("ResponseResult", props);
			}else if(command.equalsIgnoreCase("ResetCompositeUserFeedSettings")) {
				user.resetToDefaultCompositeUserFeedCommonSettings();
//				user.saveXMLObjectToFileByLogin();
				props = user.getCompositeUserFeedCommonSettings();
				request.setAttribute("Info", "CompositeUserFeed settings set to default values");
				log.debug("CompositeUserSettings reset");
//				request.setAttribute("ResponseResult", props);
//			}else if(command.equalsIgnoreCase("Add")) {
//				if(props.containsKey((String)request.getParameter("property")) ){
//					request.setAttribute("Exception", new String("Property ["+request.getParameter("property")+"] already exists"));
//				}else {
//					props.put((String)request.getParameter("property"), request.getParameter("value"));
////					user.setCompositeUserFeedCommonSettings(props);
////					 user.saveXMLObjectToFileByLogin();
//					request.setAttribute("Info", new String("Item ["+User.getXMLObjectFromXMLFileByUserName(userName).getCompositeUserFeedCommonSettings().get(request.getParameter("property")))+"] added");
//				}
			}else if(command.equalsIgnoreCase("UpdateCompositeUserFeedSetting")) {
				if(props.containsKey(request.getParameter("property"))) {
					props.remove(request.getParameter("property"));
				}
				props.put(request.getParameter("property"), request.getParameter("value"));
				request.setAttribute("Info", new String("Item ["+request.getParameter("property")+"] updated"));
//			}else if(command.equalsIgnoreCase("delete")) {
////				HashMap props = user.getXMLObjectFromXMLFileByUserName(userName).getCompositeUserFeedCommonSettings();
//				if(!props.containsKey(request.getParameter("property"))) {
//					props.remove(request.getParameter("property"));
//					request.setAttribute("Info", new String("Item ["+request.getParameter("property")+"] removed"));
//				}else {
//					
//				}
			}else if(command.equalsIgnoreCase("test")){
//				String resultUrl = URLTranslationServlet.translateURL(testUrl, user);
//				request.setAttribute("Info", new String("Получится URL: <a href=\"" +resultUrl+ "\">"+resultUrl+"</a>"));
			}else {
				request.setAttribute("Exception", new String("Unknown command: " + command));
			}
			user.setCompositeUserFeedCommonSettings(props);
			user.saveXMLObjectToFileByLogin();
			request.setAttribute("ResponseResult", User.getXMLObjectFromXMLFileByUserName(userName).getCompositeUserFeedCommonSettings());

		}catch (Exception e) { 
			log.error("Exception", e);
			request.setAttribute("Exception", e);
		}	finally {
			rd.forward(request, response);		
		}
	}	 
	

	
}
