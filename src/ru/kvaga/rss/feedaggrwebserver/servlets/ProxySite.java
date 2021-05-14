package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.ServerUtilsConcurrent;

/**
 * Servlet implementation class ProxySite
 */
@WebServlet("/ProxySite")
public class ProxySite extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(ProxySite.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProxySite() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String url = request.getParameter("url");
		String action = request.getParameter("action");
		log.debug("Incoming request with parameters: url ["+url+"], action ["+action+"]");
		if(url==null || action==null) {
			log.error("URL or action parameters are null");
			response.setStatus(507);
			return;
		}
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8"); 
		String htmlBody=null;
		try {
			htmlBody = ServerUtilsConcurrent.getInstance().getURLContent(url);
		} catch (GetURLContentException e) {
			log.error("[" + url + "] Exception", e);
			response.setStatus(508);
			return;
		} catch (InterruptedException e) {
			log.error("[" + url + "] Exception", e);
			response.setStatus(509);
			return;
		} catch (ExecutionException e) {
			log.error("[" + url + "] Exception", e);
			response.setStatus(510);
			return;
		}
		if (action.equals("unescapeUnicode")) {
			response.getWriter().write(unescapeJava(htmlBody));
		}else if(action.equals("removeBackSlashes")){
			response.getWriter().write(removeBackSlashes(htmlBody));
		}else if(action.equals("unescapeUnicodeAndRemoveBackSlashes")){
			response.getWriter().write(removeBackSlashes(unescapeJava(htmlBody)));
		}else {
			log.error("Unknown action ["+action+"]");
			response.setStatus(506);
		}
//		if (url.matches("https{0,1}://tv.yandex.ru.*")) {
//			try {
//				String remoteUrlTemplate = "https://tv.yandex.ru/?date=%s&period=all-day";
//				int shiftDays = Integer.parseInt(request.getParameter("shiftDays"));
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				Calendar c = Calendar.getInstance();
//				c.add(Calendar.DATE, -3); 
//				String htmlBody = Exec.getURLContent(String.format("https://tv.yandex.ru/?date=%s&period=all-day", sdf.format(c.getTime())));
//				response.setContentType("text/plain");
//				response.setCharacterEncoding("UTF-8"); // You want world domination, huh?
//				response.getWriter().write(htmlBody);
//			} catch (Exception e) {
//				log.error("["+url+"] Exception", e);
//				response.setStatus(510);
//			}
//		} else if(url.matches("https{0,1}://tproger.ru/events/")){
//			String htmlBody;
//			try {
//				htmlBody = Exec.getURLContent(url);
//				response.setContentType("text/plain");
//				response.setCharacterEncoding("UTF-8"); // You want world domination, huh?
//				response.getWriter().write(unescapeJava(htmlBody));
//			} catch (GetURLContentException e) {
//				log.error("["+url+"] Exception", e);
//				response.setStatus(511);
//			}
//			
//		}else {
//			response.setStatus(509);
//		}
	}

	public static String removeBackSlashes(String str) {
		return str.replace("\\", "");
	}
	public static String unescapeJava(String escaped) {
		if (escaped.indexOf("\\u") == -1)
			return escaped;

		String processed = "";

		int position = escaped.indexOf("\\u");
		while (position != -1) {
			if (position != 0)
				processed += escaped.substring(0, position);
			String token = escaped.substring(position + 2, position + 6);
			escaped = escaped.substring(position + 6);
			processed += (char) Integer.parseInt(token, 16);
			position = escaped.indexOf("\\u");
		}
		processed += escaped;

		return processed;
	}

	

}
