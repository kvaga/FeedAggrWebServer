package ru.kvaga.calendar.ics;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ICSServlet
 */
@WebServlet("/ICSServlet")
public class ICSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ICSServlet() {
        super();
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("APPLICATION/OCTET-STREAM");   
		 response.setHeader("Content-Disposition","attachment; filename=\"" + request.getParameter("ics_filename") + "\"");
		
		response.getWriter().write(getICS(request.getParameter("summary"), request.getParameter("description")));
		response.getWriter().close();
	}
	
	public String getICS(String summary, String description) {
		String icsTemplate=
				"BEGIN:VCALENDAR\r\n" + 
				"PRODID:-//Splash Event Planning System\r\n" + 
				"VERSION:2.0\r\n" + 
				"METHOD:PUBLISH\r\n" + 
				"BEGIN:VEVENT\r\n" + 
				"DTSTAMP:20210219T080333Z\r\n" + 
				"UID:135095d2c04cc43221de40dcb2aab16bdcb75a90@events.elastic.\r\n" + 
				" co/russiaelasticday2021\r\n" + 
				"URL:http://events.elastic.co/russiaelasticday2021\r\n" + 
				"DTSTART:20210303T070000Z\r\n" + 
				"DTEND:20210303T103000Z\r\n" + 
				"SUMMARY:"+summary+"\r\n" + 
				"DESCRIPTION:"+description+"\r\n" + 
				"LOCATION:Онлайн\r\n" + 
				"END:VEVENT\r\n" + 
				"END:VCALENDAR\r\n" + 
				"\r\n" + 
				""
				;
		return icsTemplate;
	}

}
