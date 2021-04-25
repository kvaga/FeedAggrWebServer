package ru.kvaga.calendar.ics;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class ICSServlet
 */
@WebServlet("/ICSServlet")
public class ICSServlet extends HttpServlet {
	private static Logger log = org.apache.logging.log4j.LogManager.getLogger(ICSServlet.class);
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
		
		try {
			response.getWriter().write(getICS(request.getParameter("summary"), request.getParameter("description"),request.getParameter("date_format"),request.getParameter("date")));
		} catch (ParseException e) {
			log.error("Exception", e);
		}
		response.getWriter().close();
	}
	
	public String getICS(String summary, String description, String dateFormat, String strDate) throws ParseException {
		// http://localhost:8080/FeedAggrWebServer/ICSServlet?ics_filename=ics.ics&summary=qqq&description=qqq&date_format=dd.MM.yyyy%20%20HH:mm&date=21.05.2021%20%2016:15
		SimpleDateFormat sdfIncoming = new SimpleDateFormat(dateFormat);
		SimpleDateFormat sdfICS = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		Date dateIncoming = sdfIncoming.parse(strDate);
		String icsTemplate=
				"BEGIN:VCALENDAR\r\n" + 
				"PRODID:-//Kvaga Event Planning System\r\n" + 
				"VERSION:2.0\r\n" + 
				"METHOD:PUBLISH\r\n" + 
				"BEGIN:VEVENT\r\n" + 
//				"DTSTAMP:20210219T080333Z\r\n" +
"DTSTAMP:"+sdfICS.format(new Date())+"\r\n" +
				"UID:"+getUID()+"\r\n" + 
				"URL:http://someurl\r\n" + 
//				"DTSTART:20210303T070000Z\r\n" +
"DTSTART:"+sdfICS.format(dateIncoming)+"\r\n" +
//				"DTEND:20210303T103000Z\r\n" +
"DTEND:"+sdfICS.format(dateIncoming)+"\r\n" +
				"SUMMARY:"+summary+"\r\n" + 
				"DESCRIPTION:"+description+"\r\n" + 
				"LOCATION:Онлайн\r\n" + 
				"END:VEVENT\r\n" + 
				"END:VCALENDAR\r\n" + 
				"\r\n" + 
				""
				;
		log.debug(icsTemplate);
		return icsTemplate;
	}

	private long getUID() {
		return new Date().getTime();
	}
}
