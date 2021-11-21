package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;

/**
 * Servlet implementation class ImportCompositeUserFeed
 */
@WebServlet("/ImportCompositeUserFeed")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ImportCompositeUserFeedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(ImportCompositeUserFeedServlet.class);

       
    /**
     * @return 
     * @see HttpServlet#HttpServlet()
     */
    public ImportCompositeUserFeedServlet() {
        super();
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String source = request.getParameter("source");
//		String userName = request.getParameter("userName");
		String userName = (String)request.getSession().getAttribute("login");
		RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));
		String fileName = "";

		try {

			
			log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName));
			CompositeUserFeed importedCompositeUserFeed = importCompositeUserFeedServletExec(userName, request.getParts());
		   
			request.setAttribute("ResponseResult", "CompositeUserFeed with new compositeUserFeedId ["+importedCompositeUserFeed.getId()+"] imported successfully to the user ["+userName+"]!");
		}catch (Exception e) {
			log.error("Exception on ImportCompositeUserFeedServlet", e);
			request.setAttribute("Exception", e);
		}	finally {
			rd.include(request, response);
		}

	}
	
	private String getParameter(Part part, String parameter) {
    	for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith(parameter)) {
	            return content.substring(content.indexOf("=") + 2, content.length() - 1);
	        }
	    }
	    return null;
	}

	public CompositeUserFeed importCompositeUserFeedServletExec(String userName, Collection<Part> collection) throws Exception {
		String fileName="";
		 StringBuilder textBuilder = new StringBuilder();
		 	// getting the file name
			for (Part part : collection) {
			    fileName = getFileName(part);			   
			    try (Reader reader = new BufferedReader(new InputStreamReader
			      ( part.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
			        int c = 0;
			        while ((c = reader.read()) != -1) {
			            textBuilder.append((char) c);
			        }
			    }
			    break;
			}
			
			ExportCompositeFeedServletResult exportCompositeFeedServletResult = getExportCompositeFeedServletResultFromString(textBuilder.toString());
			User user = User.getXMLObjectFromXMLFileByUserName(userName);
			String newCompouseFeedId = "composite_"+ServerUtils.getNewFeedId();
			exportCompositeFeedServletResult.getCompositeUserFeed().setId(newCompouseFeedId);
			// 
			ArrayList<String> newFeedIds = new ArrayList<String>(); 
			for(RSS feedRSS : exportCompositeFeedServletResult.getFeedRSSList()) {
				String newFeedId = ServerUtils.getNewFeedId();
				newFeedIds.add(newFeedId);
//				// replace old feed id with new one in the CompositeUserFeed feeds list
//				exportCompositeFeedServletResult.getCompositeUserFeed().getFeedIds().remove(oldFeedId);
//				exportCompositeFeedServletResult.getCompositeUserFeed().getFeedIds().add(newFeedId);
				// Save RSS of feed with new feed id
				feedRSS.saveXMLObjectToFileByFeedId(newFeedId);
			}
			// Update compositeUserFeed with new feed ids
			exportCompositeFeedServletResult.getCompositeUserFeed().setFeedIds(newFeedIds);
			// set new composite feed id
			user.getCompositeUserFeeds().add(exportCompositeFeedServletResult.getCompositeUserFeed());
			// save RSS of composite user feed
			RSS compositeUserFeedRSS = exportCompositeFeedServletResult.getCompositeRSS();
			compositeUserFeedRSS.getChannel().setTitle(compositeUserFeedRSS.getChannel().getTitle() + " [Imported at "+new Date()+"]");
			compositeUserFeedRSS.saveXMLObjectToFileByFeedId(newCompouseFeedId);
			user.saveXMLObjectToFileByLogin();			
			log.debug("Content of file: " + textBuilder.toString());
			log.debug("File " + fileName + " has uploaded successfully!");
		return user.getCompositeUserFeedById(newCompouseFeedId);
	}

	
	private String getFileName(Part part) {
    	for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(content.indexOf("=") + 2, content.length() - 1);
	        }
	    }
	    return null;
	}

	private ExportCompositeFeedServletResult getExportCompositeFeedServletResultFromString(String string) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(ExportCompositeFeedServletResult.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(string);
		return (ExportCompositeFeedServletResult) unmarshaller.unmarshal(reader);
	}
}
