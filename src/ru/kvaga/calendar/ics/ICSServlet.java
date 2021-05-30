package ru.kvaga.calendar.ics;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.LogManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

import ru.kvaga.monitoring.influxdb.InfluxDB;

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
		long t1 = new Date().getTime();
		log.debug("Incoming parameters of getICS: "
				+ "fileName ["+request.getParameter("ics_filename")+"], "
				+ "summary ["+request.getParameter("summary")+"],"
				+ "description ["+request.getParameter("description")+"],"
				+ "date_format ["+request.getParameter("date_format")+"], "
				+ "date ["+request.getParameter("date")+"],"
				+ "incoming_timezone ["+request.getParameter("incoming_timezone")+"]"
				+ "outgoing_timezone ["+request.getParameter("outgoing_timezone")+"]"
				);
		String fileName=request.getParameter("ics_filename");
		if(request.getParameter("ics_filename")==null) {
			fileName=getUID()+".ics";
			log.debug("Filename set to ["+fileName+"]");
		}
		response.setContentType("APPLICATION/OCTET-STREAM; charset=UTF-8");   
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
			

		try {
			response.getWriter().write(getICS(request.getParameter("summary"), request.getParameter("description"),request.getParameter("date_format"),request.getParameter("date"), request.getParameter("incoming_timezone"), request.getParameter("outgoing_timezone")));
		} catch (ParseException e) {
			log.error("Exception", e);
			response.getWriter().write("Exception: " + e.getMessage() + ", cause: " + e.getCause());
		}
		response.getWriter().close();
		InfluxDB.getInstance().send("response_time,method=ICSServlet.service", new Date().getTime() - t1);

	}
	
	public String getICS(String summary, String description, String dateFormat, String strDate, String incomingTimezone, String outgoingTimezone) throws ParseException {
		long t1 = new Date().getTime();
		// http://localhost:8080/FeedAggrWebServer/ICSServlet?ics_filename=ics.ics&summary=qqq&description=qqq&date_format=dd.MM.yyyy%20%20HH:mm&date=21.05.2021%20%2016:15
		SimpleDateFormat sdfIncoming = new SimpleDateFormat(dateFormat);
		sdfIncoming.setTimeZone(TimeZone.getTimeZone("GMT+3"));
		if(incomingTimezone!=null) {
			sdfIncoming.setTimeZone(TimeZone.getTimeZone(incomingTimezone));
			log.debug("Time zone of sdfIncoming set to ["+incomingTimezone+"]");
		}
//		SimpleDateFormat sdfICS = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		SimpleDateFormat sdfICS = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		sdfICS.setTimeZone(TimeZone.getTimeZone("GMT+3"));
		if(outgoingTimezone!=null) {
			sdfICS.setTimeZone(TimeZone.getTimeZone(outgoingTimezone));
			log.debug("Time zone of sdfICS set to ["+outgoingTimezone+"]");
		}
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
		InfluxDB.getInstance().send("response_time,method=ICSServlet.getICS", new Date().getTime() - t1);

		return icsTemplate;
	}

	private long getUID() {
		return new Date().getTime();
	}
	
	
}
