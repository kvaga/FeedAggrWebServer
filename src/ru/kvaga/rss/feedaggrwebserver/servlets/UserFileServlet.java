package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserRepeatableSearchPattern;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserRssItemPropertiesPatterns;
import ru.kvaga.rss.feedaggrwebserver.servlets.utils.ServletUtils;

/**
 * Servlet implementation class UserFileServlet
 */
@WebServlet("/UserFile")
public class UserFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(UserFileServlet.class);

    private Gson gson = new Gson();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String source = request.getParameter("source");
		String userName = request.getParameter("userName");
		String command = request.getParameter("command");
		
		System.out.println("contentType: " + request.getContentType()  + " method: " + request.getMethod());
		
		try {
			log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName, "command", command));
			if(command==null) {
				ServletUtils.responseJSONError("Couldn't find any command parameter", response);
				return;
			} 
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			switch(command) {
				case "getrssitempropertiespatternsbydomains":
					//ServletUtils.responseJSON(checkZombiFeedsInCompositeFeeds(userName), response);
					response.getWriter().print(gson.toJson(getRssItemPropertiesPatternsByDomains(userName)));
					break;
				case "changerssetempropertiespatternsfordomain":
					if(request.getParameter("domain")==null) {
						ServletUtils.responseJSONError("Couldn't find parameter 'domain' for the command '"+command+"'", response);
						return;
					} 
					if(request.getParameter("rssItemPropertiesPatternsByDomains")==null) {
						ServletUtils.responseJSONError("Couldn't find parameter 'rssItemPropertiesPatternsByDomains' for the command '"+command+"'", response);
						return;
					} 
					ServletUtils.responseJSON(changeRssItemPropertiesPatternsForDomain(request.getParameter("domain"),request.getParameter("rssItemPropertiesPatternsByDomains"), userName), response);
					response.sendRedirect(source!=null?source:ServletUtils.getSource(request));
					break;
				case "applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomains":
					if(request.getParameter("domain")==null) {
						throw new Exception("Couldn't find parameter 'domain' for the command '"+command+"'");
					} 
					if(request.getParameter("rssItemPropertiesPatternsByDomains")==null) {
						ServletUtils.responseJSONError("Couldn't find parameter 'rssItemPropertiesPatternsByDomains' for the command '"+command+"'", response);
						return;
					}
					ServletUtils.responseJSON(applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomainsTable(request.getParameter("domain"),request.getParameter("rssItemPropertiesPatternsByDomains"), userName), response);
					//response.sendRedirect(source!=null?source:ServletUtils.getSource(request));
					//PrintWriter pw = response.getWriter();	
					//pw.write("<html><body>"+new Gson().toJson(applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomainsTable(request.getParameter("domain"),request.getParameter("rssItemPropertiesPatternsByDomains"), userName))+"</body></html>");

					//RequestDispatcher rd = request.getParameter("source")!=null? getServletContext().getRequestDispatcher(request.getParameter("source")) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp");
					//rd.include(request, response);
					
					break;
				case "getrssrepeatablesearchpatterns":
					ServletUtils.responseJSON(getrssrepeatablesearchpatterns(userName), response);
					break;
					
				case "changerepeatableseachpatternfordomain":
					if(request.getParameter("domain")==null) {
						ServletUtils.responseJSONError("Couldn't find parameter 'domain' for the command '"+command+"'", response);
						return;
					} 
					if(request.getParameter("repeatableSearchPatternByDomain")==null) {
						ServletUtils.responseJSONError("Couldn't find parameter 'repeatableSearchPatternByDomain' for the command '"+command+"'", response);
						return;
					} 
					ServletUtils.responseJSON(changeRepeatableSearchPatternForDomain(request.getParameter("domain"),request.getParameter("repeatableSearchPatternByDomain"), userName), response);
					response.sendRedirect(source!=null?source:ServletUtils.getSource(request));
					break;
				case "applyToAllCorrespondingDomainsRepeatableSearchPatternByDomains":
					if(request.getParameter("domain")==null) {
						throw new Exception("Couldn't find parameter 'domain' for the command '"+command+"'");
					} 
					if(request.getParameter("repeatableSearchPatternByDomain")==null) {
						ServletUtils.responseJSONError("Couldn't find parameter 'repeatableSearchPatternByDomain' for the command '"+command+"'", response);
						return;
					}
					ServletUtils.responseJSON(applyToAllCorrespondingDomainsRepeatableSearchPatternsByDomainsTable(request.getParameter("domain"),request.getParameter("repeatableSearchPatternByDomain"), userName), response);
					break;
				default:
					throw new Exception("Unknown command ["+command+"]. Possible values are ['getrssitempropertiespatternsbydomains','changerssetempropertiespatternsfordomain','applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomainsTable', 'getrssrepeatablesearchpatterns']");
			}
		}catch (Exception e) { 
			log.error("Exception on CompositeFeedsListServlet", e);
			//ServletUtils.responseJSONError("Exception: " + e.getMessage(), response);
//			response.getWriter().write(
//					new ObjectMapper().writeValueAsString(new ServletError("Exception: " + e.getMessage())));
			//response.getWriter().print(gson.toJson("{ error: 'Exception: " + e.getMessage()+"'"));
			ServletUtils.responseJSONError(e.getMessage(), response);

		}	 
	}

    private ArrayList<String> applyToAllCorrespondingDomainsRepeatableSearchPatternsByDomainsTable(String domain, String pattern, String userName) throws Exception {
    	User user = User.getXMLObjectFromXMLFileByUserName(userName);
    	ArrayList<String> all = new ArrayList<String>();
    	for(UserFeed userFeed : user.getUserFeeds()) {
    		if(Exec.getDomainFromURL(userFeed.getUserFeedUrl()).equals(domain)) {
    			userFeed.setRepeatableSearchPattern(pattern);
    			all.add("title [" + userFeed.getUserFeedTitle() + "], link [" + userFeed.getUserFeedUrl()+"], pattern: " + pattern);
    		}
    	}
    	user.saveXMLObjectToFileByLogin();
    	return all;
	}

	private String changeRepeatableSearchPatternForDomain(String domain, String pattern, String userName) throws Exception {
    	User user = User.getXMLObjectFromXMLFileByUserName(userName);
    	UserRepeatableSearchPattern p = new UserRepeatableSearchPattern(domain, pattern);
    	 if(!user.getRepeatableSearchPatterns().add(p)) {
    		 user.getRepeatableSearchPatterns().remove(p);
    		 user.getRepeatableSearchPatterns().add(p);
    	 }
    	user.saveXMLObjectToFileByLogin();
    	return new String("For user ["+userName+"] for domain ["+domain+"] changed repeatableSearchPAttern to ["+pattern+"]");
    }

	private Set<UserRepeatableSearchPattern> getrssrepeatablesearchpatterns(String userName) throws JAXBException {
    	User user = User.getXMLObjectFromXMLFileByUserName(userName);
    	return user.getRepeatableSearchPatterns();
	}

	private ArrayList<String> applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomainsTable(String domain, String template, String userName) throws Exception {
    	User user = User.getXMLObjectFromXMLFileByUserName(userName);
    	ArrayList<String> feedTitlesOfChangedPatterns = new ArrayList<String>();
    	for(UserFeed userFeed : user.getUserFeeds()) {
    		if(Exec.getDomainFromURL(userFeed.getUserFeedUrl()).equals(domain)) {
    			userFeed.setItemContentTemplate(template);
    			feedTitlesOfChangedPatterns.add("title [" + userFeed.getUserFeedTitle() + "], link [" + userFeed.getUserFeedUrl()+"]");
    		}
    	}
    	user.saveXMLObjectToFileByLogin();
    	return feedTitlesOfChangedPatterns;
	}

	private synchronized String changeRssItemPropertiesPatternsForDomain(String domain, String pattern, String userName) throws Exception {
    	User user = User.getXMLObjectFromXMLFileByUserName(userName);
    	user.getRssItemPropertiesPatternByDomain(domain).setPatternDescription(pattern);
    	user.saveXMLObjectToFileByLogin();
    	return new String("For user ["+userName+"] for domain ["+domain+"] changed RssItemPropertiesPattern to ["+pattern+"]");
    }
    private Set<UserRssItemPropertiesPatterns> getRssItemPropertiesPatternsByDomains(String userName) throws JAXBException {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		return user.getRssItemPropertiesPatterns();
	}
    
	private Set<UserRepeatableSearchPattern> getRepeatableSearchPatternsByDomains(String userName) throws JAXBException {
		User user = User.getXMLObjectFromXMLFileByUserName(userName);
		return user.getRepeatableSearchPatterns();
	}
}
