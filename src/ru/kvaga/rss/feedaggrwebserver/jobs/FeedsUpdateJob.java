package ru.kvaga.rss.feedaggrwebserver.jobs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.*;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetSubstringForHtmlBodySplitException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggr.FeedAggrException.SplitHTMLContent;
import ru.kvaga.rss.feedaggr.Item;
import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

public class FeedsUpdateJob implements Runnable {
	 final static Logger log = LogManager.getLogger(FeedsUpdateJob.class);

//	private static Logger log= LogManager.getLogger(FeedsUpdateJob.class);


//	public static void main(String args[]) throws IOException, GetURLContentException, GetSubstringForHtmlBodySplitException,
//			SplitHTMLContent, NoSuchAlgorithmException {
//	} //

	private ServletContext context;
//	private String path="";
	public FeedsUpdateJob(ServletContext context)  {
		
		this.context=context;
//		System.out.println("context="+context);
//		try {
//			path = context.getResource(".").getFile();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch blockb//
////			e.printStackTrace();
//			log.error("Incorrect URL", e);
//		}
	}
	void updateFeeds() throws NoSuchAlgorithmException, SplitHTMLContent, GetURLContentException, GetSubstringForHtmlBodySplitException, IOException {
//		URL urlLog = org.apache.logging.log4j.LogManager.class.getResource("/log4j.properties");
//		System.out.println("==========>>>" + urlLog);
//		log.info("==========----------------------------------------------------------------------------------------------------------------------------->>>" + urlLog);
		
//		System.out.println("[point 1 ] path='"+path+"'");
//		String feedsPath = ConfigMap.feedsPath+"/feeds";
		String usersPath = ConfigMap.usersPath.getAbsolutePath();
//		log.debug("feedsPath="+feedsPath);
		log.debug("usersPath="+usersPath);

		String url = null;
		String responseHtmlBody = null;
		String repeatableSearchPattern = null;
		String substringForHtmlBodySplit = null;

		String itemTitleTemplate = null;// get from config
		String itemLinkTemplate = null; // get from config
		String itemContentTemplate = null; // get from config
//		System.out.println("[point 2]");

//		System.out.println(new File(feedsPath).getCanonicalPath());
//		for(File feedsFiles : new File(feedsPath).listFiles()) {
//			System.out.println(feedsFiles.getName());
//		}

		
//				System.out.println("[point 3] new File(usersPath).listFiles():  " + ile(".").getCanonicalPath());
			File[] listOfUsersFiles=ConfigMap.usersPath.listFiles();
			if(listOfUsersFiles==null || listOfUsersFiles.length==0) {
				throw new RuntimeException("The list of files for path ["+ConfigMap.usersPath+"]=0");
			}
			 try {
			// Пробегаемся по всем пользователям
			for (File userFile : listOfUsersFiles) {
//				System.out.println("[point 4] userFile="+userFile);
				log.debug("Found user file ["+userFile+"]");
				User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
				// Находим у каждого пользователя список feed id и соответствующие им
				// item*Templates
				
				if(user.getUserFeeds()==null || user.getUserFeeds().size()==0) {
					log.warn("The user ["+user.getName()+"] doesn't have any feed ");
				}
				
				for (UserFeed userFeed : user.getUserFeeds()) {
					String feedId=userFeed.getId();
//				System.out.println(feedFile.getName());
					String rssXmlFile=ConfigMap.feedsPath.getAbsolutePath() + "/" + userFeed.getId() + ".xml";
//					System.out.println("[point 5] rssXmlFile="+rssXmlFile);
					
					log.debug("Found rssXmlFile ["+rssXmlFile+"] for users file ["+userFile+"]");
					// Получаем feed объект из файла
					RSS rssFromFile = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(rssXmlFile,new RSS());
//				ObjectsUtils.printXMLObject(rssFromFile);

					// Получаем вспомогательную информацию для получения feed (RSS) объекта из Web
					url = rssFromFile.getChannel().getLink();
//					System.out.println("[point 6] url="+url);
					log.debug("Feed id [" + feedId + "] contains url ["+url+"]. Trying to get URL's content");
					responseHtmlBody = Exec.getURLContent(url);
//				repeatableSearchPattern="<h2 class=\"title\">{*}<a href=\"{%}\" title=\"{%}\" rel=\"bookmark\">{%}</a>{*}</h2>\r\n";
					log.debug("The content of the ["+url+"] was downloaded");
					itemTitleTemplate = userFeed.getItemTitleTemplate();
					itemLinkTemplate = userFeed.getItemLinkTemplate();
					itemContentTemplate = userFeed.getItemContentTemplate();
					repeatableSearchPattern = userFeed.getRepeatableSearchPattern();
					log.debug(String.format("Got parameters for feed [feedId='%s', itemTitleTemplate='%s', itemLinkTemplate='%s', itemContentTemplate='%s', repeatableSearchPattern='%s']", 
							feedId,itemTitleTemplate, itemLinkTemplate, itemContentTemplate, repeatableSearchPattern));
//					System.out.println("itemTitleTemplate: " + itemTitleTemplate);
//					System.out.println("itemLinkTemplate: " + itemLinkTemplate);
//					System.out.println("itemContentTemplate: " + itemContentTemplate);
//					System.out.println("repeatableSearchPattern: " + repeatableSearchPattern);

					substringForHtmlBodySplit = Exec.getSubstringForHtmlBodySplit(repeatableSearchPattern);

					// Получаем feed (RSS) объект из Web
					RSS rssFromWeb = Exec.getRSSFromWeb(url, responseHtmlBody, substringForHtmlBodySplit,
							repeatableSearchPattern, itemTitleTemplate, itemLinkTemplate, itemContentTemplate);
//					ObjectsUtils.printXMLObject(rssFromWeb);
					
					// Сравниванием списки item из Web и Файла
					for(ru.kvaga.rss.feedaggr.objects.Item itemFromWeb : rssFromWeb.getChannel().getItem()) {
						boolean foundItemBol=false;
						for(ru.kvaga.rss.feedaggr.objects.Item itemFromFile:rssFromFile.getChannel().getItem()) {
							if(itemFromWeb.getGuid().getValue().equals(itemFromFile.getGuid().getValue())) {
								foundItemBol=true;
								break;
							}
						}
						if(!foundItemBol) {
//							System.out.println("Такого item [" + itemFromWeb.getTitle() + "] с guid ["+itemFromWeb.getGuid().getValue()+"] нет в файле");
							log.debug("Такого item [" + itemFromWeb.getTitle() + "] с guid ["+itemFromWeb.getGuid().getValue()+"] нет в файле");
							rssFromFile.getChannel().getItem().add(itemFromWeb);
							log.debug("itemFromWeb [" + itemFromWeb.getTitle() + "] с guid ["+itemFromWeb.getGuid().getValue()+"] добавлен в rssFromFile");
						}
					}
					rssFromFile.getChannel().setLastBuildDate(new Date());
					ObjectsUtils.saveXMLObjectToFile(rssFromFile, rssFromFile.getClass(), new File(rssXmlFile));
//					System.out.println("Объект rssFromFile сохранен в файл ["+rssXmlFile+"]");
					log.debug("Объект rssFromFile сохранен в файл ["+rssXmlFile+"]");
				}
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			log.error("Exception occured", e);
		}
	}
	
	
	public void run() {
//		System.out.println("=========== FeedsUpdateJob ===========");
		log.info("Try to start Job ");
//		log.info("Job started");
		
//		test();
		
		try {
			updateFeeds();
			log.debug("Job finished");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			log.error(e);
		} catch (SplitHTMLContent e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			log.error(e);
		} catch (GetURLContentException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			log.error(e);
		} catch (GetSubstringForHtmlBodySplitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		}catch(Exception e) {
			log.error("Exception",e);
		}
//		System.out.println("------------------");
	}

}
