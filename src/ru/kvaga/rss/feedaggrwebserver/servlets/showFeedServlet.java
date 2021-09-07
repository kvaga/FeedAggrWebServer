package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

/**
 * Servlet implementation class showFeed
 */
@WebServlet("/showFeed")
public class showFeedServlet extends HttpServlet {
	final static Logger log = LogManager.getLogger(showFeedServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public showFeedServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String user = null;
		Cookie[] cookies = request.getCookies();
		long t1 = System.currentTimeMillis();

		response.setContentType("text/xml");
		/*
		PrintWriter writer = response.getWriter();
		*/
//		writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//		writer.append("<sales_description>descriptin</sales_description>");

		StringBuilder feedXML = new StringBuilder();
//		PrintWriter out = response.getWriter();
//		out.append("q");
//		out.println("Hello!");

		/*
		 * if(cookies!=null) { for(Cookie cookie : cookies) {
		 * if(cookie.getName().equals("user")) { user=cookie.getValue(); } break; }
		 * if(user==null) {
		 * 
		 * RequestDispatcher rd =
		 * getServletContext().getRequestDispatcher("/Login.html"); PrintWriter out =
		 * response.getWriter();
		 * out.print("<font color=red>Unkown user. Please log in</font>");
		 * rd.include(request, response);
		 * 
		 * } }else {
		 * 
		 * RequestDispatcher rd =
		 * getServletContext().getRequestDispatcher("/Login.html"); PrintWriter out =
		 * response.getWriter();
		 * out.print("<font color=red>Unkown user. Please log in</font>");
		 * rd.include(request, response);
		 * 
		 * }
		 */
		String feedId = request.getParameter("feedId");
		BufferedReader br = null;
		String filePathStr=ConfigMap.feedsPath+File.separator+feedId+".xml";
		// Path path = Paths.get(filePathStr);
		float fileSizeMb = -1; 
		File file = new File(filePathStr);
//		String realPath=getServletContext().getRealPath("data/feeds/");
//		String realPath=getServletContext().getRealPath(ConfigMap.dataPath+"/feeds/");

//		log.debug("realPath: "+realPath);
		
		 try(InputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
			 fileSizeMb = Exec.getFileSize(filePathStr); //Files.size(path)/1024/1024;;
		            byte[] buffer = new byte[ConfigMap.SERVLET_SHOW_FEED_BUFFER_READ_BYTES];
		        
		            int numBytesRead;
		            while ((numBytesRead = in.read(buffer)) > 0) {
		                out.write(buffer, 0, numBytesRead);
		            }
		}catch(Exception e) {
			log.error("Exception", e);
		}
		 log.debug("Response sent for the feedId ["+request.getParameter("feedId")+"] for ["+(System.currentTimeMillis()-t1)+"] millis, size ["+fileSizeMb+"] mb");
//		doGet(request, response);
	}

	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	/*
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String user = null;
		Cookie[] cookies = request.getCookies();

		response.setContentType("text/xml; charset=UTF-8");
		PrintWriter writer = response.getWriter();
//		writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//		writer.append("<sales_description>descriptin</sales_description>");

		StringBuilder feedXML = new StringBuilder();
//		PrintWriter out = response.getWriter();
//		out.append("q");
//		out.println("Hello!");

		
//		 * if(cookies!=null) { for(Cookie cookie : cookies) {
//		 * if(cookie.getName().equals("user")) { user=cookie.getValue(); } break; }
//		 * if(user==null) {
//		 * 
//		 * RequestDispatcher rd =
//		 * getServletContext().getRequestDispatcher("/Login.html"); PrintWriter out =
//		 * response.getWriter();
//		 * out.print("<font color=red>Unkown user. Please log in</font>");
//		 * rd.include(request, response);
//		 * 
//		 * } }else {
//		 * 
//		 * RequestDispatcher rd =
//		 * getServletContext().getRequestDispatcher("/Login.html"); PrintWriter out =
//		 * response.getWriter();
//		 * out.print("<font color=red>Unkown user. Please log in</font>");
//		 * rd.include(request, response);
//		 * 
//		 * }
		 
		String feedId = request.getParameter("feedId");
		BufferedReader br = null;
//		String realPath=getServletContext().getRealPath("data/feeds/");
//		String realPath=getServletContext().getRealPath(ConfigMap.dataPath+"/feeds/");

//		log.debug("realPath: "+realPath);
		try {

			// for(Feed feedOnServer : ServerUtils.getFeedsList(ConfigMap.feedsPath)) {
			for (Feed feedOnServer : ServerUtils.getFeedsList(true, true)) {

				if (feedOnServer.getId().equals(feedId)) {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(feedOnServer.getXmlFile()),
							StandardCharsets.UTF_8));
					String s;
//					out.println(feedOnServer.getId());
					while ((s = br.readLine()) != null) {
						feedXML.append(s);

					}
					break;
				}

//				    OutputStream output = response.getOutputStream();
//				    output.write(feedXML.toString().getBytes());
//				response.setContentType("text/xml;charset=UTF-8");
//			    response.setCharacterEncoding("UTF-8");

//			ObjectsUtills.printXMLObject(rssFeed);
			}

			writer.write(feedXML.toString());
		} catch (GetFeedsListByUser e) {

			log.error("GetFeedsListByUser", e);
		} catch (JAXBException e) {
			log.error("JAXBException", e);
		} finally {
			if (br != null) {
				br.close();
			}
		}
//		doGet(request, response);
	}
*/
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
