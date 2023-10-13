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
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;
import ru.kvaga.rss.feedaggrwebserver.servlets.utils.ServletUtils;

/**
 * Servlet implementation class UserSettingsServlet
 */
@WebServlet("/SettingsOfSpecificUserFeedServlet")
public class SettingsOfSpecificUserFeedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(SettingsOfSpecificUserFeedServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SettingsOfSpecificUserFeedServlet() {
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
		String feedId = request.getParameter("feedId");

		String command = request.getParameter("command");
		
//		System.out.println("contentType: " + request.getContentType()  + " method: " + request.getMethod());
		RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));

		try {
			log.debug("Got parameters: "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName, "command", command, "feedId", feedId));

			//			if(request.getParameter("property")!=null && request.getParameter("property").equals("")){
//				request.setAttribute("Exception", new String("Property can't be empty"));
//			}
			User user = User.getXMLObjectFromXMLFileByUserName(userName); 
			if(command.equalsIgnoreCase("GetSettingsOfUserFeed")) {
				//
			}else if(command.equalsIgnoreCase("ResetUserFeedSettings")) {
//				UserFeed uf = user.getUserFeedByFeedId(feedId);

				user.getUserFeedByFeedId(feedId).setFilterWords("");
				user.getUserFeedByFeedId(feedId).setSkipWords("");
				user.getUserFeedByFeedId(feedId).setSuspendStatus(false);
//				uf.setUserFeedTitle(request.getParameter("property"));
//				HashMap<String,String> props = user.getCompositeUserFeedById(feedId).resetSettings();
//				HashMap<String,String> props = user.getUserFeedByFeedId(feedId).resetSettings();
//				props=null;
//				user.saveXMLObjectToFileByLogin();
//				user = User.getXMLObjectFromXMLFileByUserName(userName);
//				props = user.getSettingsOfCompositeUserFeedByFeedId(feedId);
				log.debug("Settings Of User were reset");
//				request.setAttribute("ResponseResult", props);
				request.setAttribute("Info","Settings Of User were reset");
				user.saveXMLObjectToFileByLogin();

//			}else if(command.equalsIgnoreCase("Add")) {
//				if(props.containsKey((String)request.getParameter("property")) ){
//					request.setAttribute("Exception", new String("Property ["+request.getParameter("property")+"] already exists"));
//				}else {
//					props.put((String)request.getParameter("property"), request.getParameter("value"));
////					user.setCompositeUserFeedCommonSettings(props);
////					 user.saveXMLObjectToFileByLogin();
//					request.setAttribute("Info", new String("Item ["+User.getXMLObjectFromXMLFileByUserName(userName).getCompositeUserFeedCommonSettings().get(request.getParameter("property")))+"] added");
//				}
			}else if(command.equalsIgnoreCase("UpdateUserFeedSetting")) {
				System.err.println("wE ARE HERE");
//				HashMap<String,String> props = user.getCompositeUserFeedById(feedId).getSettings();
//				UserFeed uf = user.getUserFeedByFeedId(feedId);
//				HashMap<String,String> props = user.getUserFeedByFeedId(feedId).getSettings();
				if(request.getParameter("FilterWords")!=null) {
					System.err.println("Set: FilterWords: " + request.getParameter("FilterWords"));
					user.getUserFeedByFeedId(feedId).setFilterWords(request.getParameter("FilterWords"));
				}
				if(request.getParameter("SkipWords")!=null) {
					System.err.println("Set: SkipWords: " + request.getParameter("SkipWords"));
					user.getUserFeedByFeedId(feedId).setSkipWords(request.getParameter("SkipWords"));
				}
				if(request.getParameter("SuspendStatus")!=null) {
					System.err.println("Set: SuspendStatus: " + request.getParameter("SuspendStatus"));
					user.getUserFeedByFeedId(feedId).setSuspendStatus(Boolean.parseBoolean(request.getParameter("SuspendStatus")));
				}
				if(request.getParameter("UserFeedTitle")!=null) {
					System.err.println("Set: UserFeedTitle: " + request.getParameter("UserFeedTitle"));
					user.getUserFeedByFeedId(feedId).setUserFeedTitle(request.getParameter("UserFeedTitle"));
				}
//				user.getUserFeedByFeedId(feedId).set
				user.saveXMLObjectToFileByLogin();


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
			UserFeed uf = user.getUserFeedByFeedId(feedId);
			HashMap<String,String> settings = new HashMap<String,String>(){{
				put("FilterWords",uf.getFilterWords());
				put("SkipWords",uf.getSkipWords());
				put("SuspendStatus",uf.getSuspendStatus()+"");
				put("UserFeedTitle",uf.getUserFeedTitle());

			}};
			request.setAttribute("ResponseResult", settings);
			request.setAttribute("feedId", feedId);
			request.setAttribute("title", uf.getUserFeedTitle());

		}catch (Exception e) { 
			log.error("Exception", e);
			request.setAttribute("Exception", e);
		}	finally {
			rd.forward(request, response);		
		}
	}	 
	

	
}
