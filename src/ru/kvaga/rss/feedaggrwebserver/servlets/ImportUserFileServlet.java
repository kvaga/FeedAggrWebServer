package ru.kvaga.rss.feedaggrwebserver.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

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
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.monitoring.MonitoringUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

/**
 * Servlet implementation class ExportCompositeFeedServlet
 */
@WebServlet("/ImportUserFile")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 5 * 5)

public class ImportUserFileServlet extends HttpServlet {
	final private static Logger log = LogManager.getLogger(ImportUserFileServlet.class);

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImportUserFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectTo = request.getParameter("redirectTo");
		String source = request.getParameter("source");
		String userName = (String)request.getSession().getAttribute("login");
		/*
		 * TODO: implement getting request parameters from multipart/form-data  Currently redirect is hardcoded as UserFileImport.jsp
		 */
		RequestDispatcher rd = redirectTo !=null? getServletContext().getRequestDispatcher(redirectTo) : (source!=null ? getServletContext().getRequestDispatcher(source) : getServletContext().getRequestDispatcher("/LoginSuccess.jsp"));
		String fileName = "";

		try {
			log.debug("Got parameters "+ServerUtils.listOfParametersToString("redirectTo", redirectTo, "source", source, "userName", userName));
			User importedUserFile = importUserFileServletExec(userName, request.getParts());
			importedUserFile.saveXMLObjectToFileByLogin();
			request.setAttribute("ResponseResult", "The new user's file ["+importedUserFile.getName()+"] imported successfully to the user ["+userName+"] instead of an older file!");
		}catch (Exception e) {
			log.error("Exception on ImportUserFileServlet", e);
			request.setAttribute("Exception", e);
		}	finally {
			rd.forward(request, response);
		}
	}
	
//	public User exportCompositeFeed(String userName) throws Exception {
//		// export part from user
//		return User.getXMLObjectFromXMLFileByUserName(userName);
//	}
	
	public String resultToString(User user) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(User.class);
        StringWriter sw = new StringWriter();
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
        m.marshal(user, sw);
        return sw.toString();
	}
	
//	private String getParameter(Part part, String parameter) {
//    	for (String content : part.getHeader("content-disposition").split(";")) {
//	        if (content.trim().startsWith(parameter)) {
//	            return content.substring(content.indexOf("=") + 2, content.length() - 1);
//	        }
//	    }
//	    return null;
//	}

	public User importUserFileServletExec(String userName, Collection<Part> collection) throws Exception {
		String fileName="";
		 StringBuilder textBuilder = new StringBuilder();
		 System.err.println("collection.size: " + collection.size());
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
			
			if(textBuilder.toString().equals("")) {
				throw new Exception("Imported file can't be empty");
			}
//			User userFromImportedXML = getUserObjectFromImportedString(textBuilder.toString());
			User user = getUserObjectFromImportedString(textBuilder.toString());
			
			for(UserFeed uf : user.getUserFeeds()) {
				if(!RSS.getRSSFileByFeedId(uf.getId()).exists()) {
					;
					RSS rss = new RSS(ConfigMap.rssVersion, new Channel(
																	uf.getUserFeedTitle(), 
																	uf.getUserFeedUrl(), 
																	"Description: " + uf.getUserFeedTitle(), 
																	new Date(), 
																	ConfigMap.generator, 
																	ConfigMap.ttlOfFeedsInDays, 
																	new ArrayList<ru.kvaga.rss.feedaggr.objects.Item>()
															)
									);
					rss.saveXMLObjectToFileByFeedId(uf.getId());
					log.debug("UserFeed with id: " + uf.getId() + " doesn't exist. File created");
				}
			}
			for(CompositeUserFeed cuf : user.getCompositeUserFeeds()) {
				if(!RSS.getRSSFileByFeedId(cuf.getId()).exists()) {
					RSS rss = new RSS(ConfigMap.rssVersion, new Channel(
							cuf.getCompositeUserFeedTitle(), 
							"_link", 
							"Description: " + cuf.getCompositeUserFeedTitle(), 
							new Date(), 
							ConfigMap.generator, 
							ConfigMap.ttlOfFeedsInDays, 
							new ArrayList<ru.kvaga.rss.feedaggr.objects.Item>()
					)
);
					rss.saveXMLObjectToFileByFeedId(cuf.getId());
					log.debug("CompositeUserFeed with id: " + cuf.getId() + " doesn't exist. File created");
				}
			}
//			String newCompouseFeedId = "composite_"+ServerUtils.getNewFeedId();
//			exportCompositeFeedServletResult.getCompositeUserFeed().setId(newCompouseFeedId);
			//exportCompositeFeedServletResult.getCompositeUserFeed().setTitle(exportCompositeFeedServletResult.getCompositeUserFeed().getTitle());

			// 
			/*
			HashSet<String> newFeedIds = new HashSet<String>(); 
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
			*/
		return user;
	}

	
	private String getFileName(Part part) {
    	for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(content.indexOf("=") + 2, content.length() - 1);
	        }
	    }
	    return null;
	}

	private User getUserObjectFromImportedString(String string) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(string);
		return (User) unmarshaller.unmarshal(reader);
	}


}
