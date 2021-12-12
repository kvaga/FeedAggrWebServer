package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.servlets.utils.ServletUtils;

/**
 * Servlet implementation class UserListServlet
 */
@WebServlet("/UserList")
public class UserListServlet extends HttpServlet {
	final private static Logger log = LogManager.getLogger(UserListServlet.class);
	private static final long serialVersionUID = 1L;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = (String)request.getParameter("redirectTo");
		String source = (String)request.getParameter("source");
		String responseType = (String) request.getParameter("type");

		log.debug("Got parameters " + ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "type", responseType));
		if(source == null && redirectTo == null) {
			source = ServletUtils.getSource(request);
		}
		
		//RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));

		try {
			//request.setAttribute("userList", getUserList());
			if(responseType!=null && responseType.equals("json")) {
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(OBJECT_MAPPER.writeValueAsString(getUserList().toArray()));
				//System.err.println(OBJECT_MAPPER.writeValueAsString(getUserList()));
			}else {
				RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));

				request.setAttribute("userList", getUserList());
				//Thread.sleep(50000);
				rd.include(request, response);
			}
		} catch (Exception e) {
			log.error("UserListServlet exception", e);
			request.setAttribute("Exception", e);
		}	
	}

	public ArrayList<User> getUserList() throws GetFeedsListByUser, JAXBException {
		return User.getAllUsersList();
	}
}
