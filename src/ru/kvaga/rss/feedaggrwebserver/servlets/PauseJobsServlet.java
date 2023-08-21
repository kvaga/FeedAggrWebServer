package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.servlets.utils.ServletUtils;

/**
 * Servlet implementation class PauseJobsServlet
 */
@WebServlet("/PauseJobs")
public class PauseJobsServlet extends HttpServlet {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	final private static Logger log = LogManager.getLogger(PauseJobsServlet.class);

	private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PauseJobsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String source = request.getParameter("source");
		String command = request.getParameter("command");
		
		log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "command", command));
		RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));

		try {

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			switch(command) {
				case "status":
//					response.getWriter().write(OBJECT_MAPPER.writeValueAsString(ConfigMap.JOBS_PAUSED));
					response.getWriter().print(gson.toJson(ConfigMap.JOBS_PAUSED));
					break;
				case "pause":
					ConfigMap.JOBS_PAUSED=true;
//					response.getWriter().write(OBJECT_MAPPER.writeValueAsString("Jobs activity paused " + ConfigMap.JOBS_PAUSED));
					response.getWriter().print(gson.toJson("Jobs activity paused " + ConfigMap.JOBS_PAUSED));

					break;
				case "enable":
					ConfigMap.JOBS_PAUSED=false;
					//response.getWriter().write(OBJECT_MAPPER.writeValueAsString("Jobs activity enabled"));
					response.getWriter().print(gson.toJson("Jobs activity enabled"));

					break;
				default:
					ServletUtils.responseJSONError("Unknown value of parameter command ["+command+"]. Allowed values are ['status', 'pause', 'enable']", response);
					break;
			}
					
		}catch (Exception e) {
			log.error("Exception on CompositeFeedsListServlet", e);
			request.setAttribute("Exception", e);
		}	finally {
			rd.forward(request, response);
		}
	}

}
