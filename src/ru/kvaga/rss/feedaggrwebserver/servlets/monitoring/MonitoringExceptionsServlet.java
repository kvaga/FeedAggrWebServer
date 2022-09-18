package ru.kvaga.rss.feedaggrwebserver.servlets.monitoring;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.monitoring.MetricURLContentExceptions;
import ru.kvaga.rss.feedaggrwebserver.servlets.AddFeedsByUrlsListServlet;

/**
 * Servlet implementation class MonitoringExceptions
 */
@WebServlet("/MonitoringExceptions")
public class MonitoringExceptionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(MonitoringExceptionsServlet.class);

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MonitoringExceptionsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String userName = request.getParameter("userName");
		String source = request.getParameter("source");
		
		log.debug("Got parameters "+ServerUtils.listOfParametersToString("userName", userName, "redirectTo", redirectTo));
		String urlListFinal[]=null;
		try {
			RequestDispatcher rd = redirectTo !=null? request.getRequestDispatcher(redirectTo) : (source!=null ? request.getRequestDispatcher(source) : request.getRequestDispatcher("/LoginSuccess.jsp"));
//			response.getWriter().write(OBJECT_MAPPER.writeValueAsString(fixLostRSSURLsAndTitlesFromUserFile(userName).toArray()));
//			MetricURLContentExceptions.getInstance().add(new Exception("my new exception"), "http://url.ru");
//			MetricURLContentExceptions.getInstance().add(new Exception("my new exception"), "http://url2.ru");
//			MetricURLContentExceptions.getInstance().add(new Exception("my new exception2"), "http://url.ru");


			request.setAttribute("ExceptionsList", MetricURLContentExceptions.getInstance().getAllExceptions());
//			request.setAttribute("message", "QQQQ");

			rd.forward(request, response);

		}catch (Exception e) {
			log.error("Exception on MonitoringExceptionsServlet", e);
			request.setAttribute("Exception", e);
	}
	}

}
