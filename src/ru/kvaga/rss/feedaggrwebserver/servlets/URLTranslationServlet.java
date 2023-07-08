package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.kvaga.rss.feedaggrwebserver.objects.user.URLTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;

/**
 * Servlet implementation class URLTranslationServlet
 */
@WebServlet("/URLTranslationServlet")
public class URLTranslationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(URLTranslationServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public URLTranslationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String source = request.getParameter("source");
		String userName = (String)request.getSession().getAttribute("login");
		String command = request.getParameter("command");
		String domain = request.getParameter("domain");
		String regexInURL = request.getParameter("RegexInURL");
		String templateOutURL = request.getParameter("TemplateOutURL");
		String testUrl = request.getParameter("testUrl");
		
		RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));

		try {
			log.debug("Got parameters "+ServerUtils.listOfParametersToString("testUrl", testUrl, "templateOutURL", templateOutURL, "regexInURL", regexInURL, "domain", domain, "redirectTo", redirectTo, "source", source, "userName", userName, "command", command));
			User user = User.getXMLObjectFromXMLFileByUserName(userName);
			if(user.getUrlTranslations()==null) {
				user.setUrlTranslations(new HashMap<String, URLTranslation>());
				user.saveXMLObjectToFileByLogin();
			}
			URLTranslation urlTranslation = new URLTranslation(request.getParameter("domain"), request.getParameter("RegexInURL"), request.getParameter("TemplateOutURL"));
			if(command.toLowerCase().equals("list")) {
				;
			}else if(command.toLowerCase().equals("add")) {
				if(user.getUrlTranslations().containsKey(urlTranslation.getDomain())) {
					request.setAttribute("Exception", new String("Domain ["+urlTranslation+"] already exists"));
				}else {
					user.getUrlTranslations().put(urlTranslation.getDomain(), urlTranslation);
					user.saveXMLObjectToFileByLogin();
					request.setAttribute("Info", new String("Item ["+urlTranslation+"] added"));
				}
			}else if(command.toLowerCase().equals("update")) {
				if(user.getUrlTranslations().containsKey(urlTranslation.getDomain())) {
					user.getUrlTranslations().remove(urlTranslation.getDomain());
				}
				user.getUrlTranslations().put(urlTranslation.getDomain(), urlTranslation);
				user.saveXMLObjectToFileByLogin();
				request.setAttribute("Info", new String("Item ["+urlTranslation+"] updated"));
			}else if(command.toLowerCase().equals("delete")) {
				user.getUrlTranslations().remove(urlTranslation.getDomain());
				user.saveXMLObjectToFileByLogin();
				request.setAttribute("Info", new String("Item ["+urlTranslation+"] removed"));
			}else if(command.toLowerCase().equals("test")){
				String resultUrl = URLTranslationServlet.translateURL(testUrl, user);
				request.setAttribute("Info", new String("Получится URL: <a href=\"" +resultUrl+ "\">"+resultUrl+"</a>"));
			}else {
				request.setAttribute("Exception", new String("Unknown command: " + command));
			}
			request.setAttribute("ResponseResult", user.getUrlTranslations());
		}catch (Exception e) {
			log.error("Exception", e);
			request.setAttribute("Exception", e);
		}	finally {
			rd.forward(request, response);		
		}
	}
    
    public static String translateURL(String inUrl, User user) {
    	if(user.getUrlTranslations().containsKey(Exec.getDomainFromURL(inUrl))) {
    		URLTranslation urlTranslation = user.getUrlTranslations().get(Exec.getDomainFromURL(inUrl)); 
    		Pattern p = Pattern.compile(urlTranslation.getRegexInURLPatternText());
    		Matcher m = p.matcher(inUrl);
    		String result = urlTranslation.getTemplateOutUrl();
    		if(m.find()) {
    			System.err.println("Found");
    			for(int i=1; i<=m.groupCount();i++) {
    				result=result.replace("{%"+i+"}", m.group(i));
    			}
    			System.err.println(result);
    			return result;
    		}
    	}
    	System.err.println("ELSE: domainFromURL: " + Exec.getDomainFromURL(inUrl));
    	return inUrl;
    }
}
