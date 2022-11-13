package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.kvaga.rss.feedaggrwebserver.objects.user.URLTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

		RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));

		try {
			log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName, "command", command));
			User user = User.getXMLObjectFromXMLFileByUserName(userName);
			if(command.toLowerCase().equals("list")) {
				if(user.getUrlTranslations()==null) {
					user.setUrlTranslations(new HashSet<URLTranslation>());
					user.saveXMLObjectToFileByLogin();
				}
				request.setAttribute("ResponseResult", user.getUrlTranslations());
			}else if(command.toLowerCase().equals("add")) {
				if(user.getUrlTranslations()==null) {
					user.setUrlTranslations(new HashSet<URLTranslation>());
					user.saveXMLObjectToFileByLogin();
				}
				request.setAttribute("ResponseResult", user.getUrlTranslations());
			}else {
				throw new Exception("Unknown command: " + command);
			}
		}catch (Exception e) {
			log.error("Exception", e);
			request.setAttribute("Exception", e);
		}	finally {
			rd.include(request, response);
		}

	}
	

}
