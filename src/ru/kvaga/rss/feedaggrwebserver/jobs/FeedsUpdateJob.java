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

import ru.kvaga.monitoring.influxdb2.InfluxDB;
import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.CommonException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetSubstringForHtmlBodySplitException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggr.FeedAggrException.SplitHTMLContent;
import ru.kvaga.rss.feedaggr.Item;
import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.MonitoringUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

public class FeedsUpdateJob implements Runnable {
	final static Logger log = LogManager.getLogger(FeedsUpdateJob.class);

//	private static Logger log= LogManager.getLogger(FeedsUpdateJob.class);



	private ServletContext context;

//	private String path="";
	public FeedsUpdateJob(ServletContext context) {

		this.context = context;
//		try {
//			path = context.getResource(".").getFile();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch blockb//
//			log.error("Incorrect URL", e);
//		}
	}

	int[] updateFeeds() throws Exception {
		long t1 = new Date().getTime();
//		URL urlLog = org.apache.logging.log4j.LogManager.class.getResource("/log4j.properties");
//		log.info("==========----------------------------------------------------------------------------------------------------------------------------->>>" + urlLog);

//		String feedsPath = ConfigMap.feedsPath+"/feeds";
		String usersPath = ConfigMap.usersPath.getAbsolutePath();
//		log.debug("feedsPath="+feedsPath);
		log.debug("usersPath=" + usersPath);
		String url = null;
		String responseHtmlBody = null;
		String repeatableSearchPattern = null;
		String substringForHtmlBodySplit = null;

		String itemTitleTemplate = null;// get from config
		String itemLinkTemplate = null; // get from config
		String itemContentTemplate = null; // get from config
		String filterWords = null;
		Long durationInMillisForUpdate=null;
		
		int allFeedsCount=0, successFeedsCount=0, postponedCount=0;
//		for(File feedsFiles : new File(feedsPath).listFiles()) {
//			log.debug(feedsFiles.getName());
//		}

		File[] listOfUsersFiles = ConfigMap.usersPath.listFiles();
		if (listOfUsersFiles == null || listOfUsersFiles.length == 0) {
			MonitoringUtils.sendResponseTime2InfluxDB(new Object(){}, new Date().getTime() - t1);
			throw new RuntimeException("The list of files for path [" + ConfigMap.usersPath + "]=0");
		}
		try {
			// ����������� �� ���� �������������
			for (File userFile : listOfUsersFiles) {
				if (!userFile.getName().endsWith(".xml")) {
					continue;
				}
				log.debug("Found user file [" + userFile + "]");
//				User user = (User) ObjectsUtils.getXMLObjectFromXMLFile(userFile, new User());
				User user = User.getXMLObjectFromXMLFile(userFile);

				// ������� � ������� ������������ ������ feed id � ��������������� ��
				// item*Templates

				if (user.getUserFeeds() == null || user.getUserFeeds().size() == 0) {
					log.warn("The user [" + user.getName() + "] doesn't have any feed ");
				}

				for (UserFeed userFeed : user.getUserFeeds()) {
					allFeedsCount++;
					try {
						if(userFeed.getDurationInMillisForUpdate()==null) {
							log.debug("DurationInMillisForUpdate parameter in the ["+userFeed.getId()+"] is null. We set default value ["+ConfigMap.DEFAULT_DURATION_IN_MILLIS_FOR_FEED_UPDATE+"]");
							userFeed.setDurationInMillisForUpdate(ConfigMap.DEFAULT_DURATION_IN_MILLIS_FOR_FEED_UPDATE);
						}
						
						String feedId = userFeed.getId();
						String rssXmlFile = ConfigMap.feedsPath.getAbsolutePath() + "/" + userFeed.getId() + ".xml";
						
						log.debug("Found rssXmlFile [" + rssXmlFile + "] for users file [" + userFile + "]");
						// �������� feed ������ �� �����
//						RSS rssFromFile = (RSS) ObjectsUtils.getXMLObjectFromXMLFile(rssXmlFile, new RSS());
						RSS rssFromFile = RSS.getRSSObjectFromXMLFile(rssXmlFile);
						
						long currentTimeInMillis = new Date().getTime();
						if(!isItTimeToUpdateFeed(currentTimeInMillis, userFeed, rssFromFile)) {
							postponedCount++;
							continue;
						}
						
						rssFromFile.removeItemsOlderThanXDays(ConfigMap.ttlOfFeedsInDays);
//				ObjectsUtils.printXMLObject(rssFromFile);

						// �������� ��������������� ���������� ��� ��������� feed (RSS) ������� �� Web
						url = rssFromFile.getChannel().getLink();
						url = (url.contains("youtube.com") && !url.contains("youtube.com/feeds/videos.xml")) ? Exec.getYoutubeFeedURL(url): url;
						log.debug("Feed id [" + feedId + "] contains url [" + url + "]. Trying to get URL's content");
						responseHtmlBody = Exec.getURLContent(url);
//				repeatableSearchPattern="<h2 class=\"title\">{*}<a href=\"{%}\" title=\"{%}\" rel=\"bookmark\">{%}</a>{*}</h2>\r\n";
						log.debug("The content of the [" + url + "] was downloaded");
						itemTitleTemplate = userFeed.getItemTitleTemplate();
						itemLinkTemplate = userFeed.getItemLinkTemplate();
						itemContentTemplate = userFeed.getItemContentTemplate();
						repeatableSearchPattern = userFeed.getRepeatableSearchPattern();
						filterWords = userFeed.getFilterWords();
						durationInMillisForUpdate = userFeed.getDurationInMillisForUpdate();
						
						log.debug(String.format(
								"Got parameters for feed [feedId='%s', itemTitleTemplate='%s', itemLinkTemplate='%s', itemContentTemplate='%s', repeatableSearchPattern='%s', filterWords='%s', durationInMillisForUpdate='%d']",
								feedId, itemTitleTemplate, itemLinkTemplate, itemContentTemplate,
								repeatableSearchPattern, filterWords, durationInMillisForUpdate));

						substringForHtmlBodySplit = Exec.getSubstringForHtmlBodySplit(repeatableSearchPattern);

						// �������� feed (RSS) ������ �� Web
						RSS rssFromWeb = Exec.getRSSFromWeb(url, responseHtmlBody, substringForHtmlBodySplit,
								repeatableSearchPattern, itemTitleTemplate, itemLinkTemplate, itemContentTemplate,
								filterWords);
//					ObjectsUtils.printXMLObject(rssFromWeb);

						// ������������ ������ item �� Web � �����
						for (ru.kvaga.rss.feedaggr.objects.Item itemFromWeb : rssFromWeb.getChannel().getItem()) {
							boolean foundItemBol = false;
							itemFromWeb.setLink(Exec.checkItemURLForFullness(url, itemFromWeb.getLink()));
							for (ru.kvaga.rss.feedaggr.objects.Item itemFromFile : rssFromFile.getChannel().getItem()) {
								if (itemFromWeb.getGuid().getValue().equals(itemFromFile.getGuid().getValue())) {
									foundItemBol = true;
									break;
								}
							}
							if (!foundItemBol) {
								log.debug("������ item [" + itemFromWeb.getTitle() + "] � guid ["
										+ itemFromWeb.getGuid().getValue() + "] ��� � �����");
								itemFromWeb.setDescription(itemFromWeb.getDescription());
								log.debug("Added title to the end of itemFromWeb because thiw item is a new one");
								rssFromFile.getChannel().getItem().add(itemFromWeb);
								log.debug("itemFromWeb [" + itemFromWeb.getTitle() + "] � guid ["
										+ itemFromWeb.getGuid().getValue() + "] �������� � rssFromFile");
							}
						}
						rssFromFile.getChannel().setLastBuildDate(new Date());
//						ObjectsUtils.saveXMLObjectToFile(rssFromFile, rssFromFile.getClass(), new File(rssXmlFile));
						rssFromFile.saveXMLObjectToFile(new File(rssXmlFile));
						log.debug("������ rssFromFile �������� � ���� [" + rssXmlFile + "]");
						successFeedsCount++;

					} catch (Exception e) {
						log.error("Exception on feedId [" + userFeed.getId() + "]", e);
					}
				}
				user.saveXMLObjectToFile(userFile);
				log.debug("User's file " +userFile+ " successfully saved");
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			log.error("Exception occured", e);
		}
		MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return new int[] {allFeedsCount, successFeedsCount, allFeedsCount-successFeedsCount-postponedCount, postponedCount};
	}

	private boolean isItTimeToUpdateFeed(long currentTimeInMillis, UserFeed userFeed, RSS rssFromFile) {
		if((currentTimeInMillis - rssFromFile.getChannel().getLastBuildDate().getTime())< userFeed.getDurationInMillisForUpdate()) {
			log.warn("It's too early to update feed id ["+userFeed.getId()+"] "
					+ "because last update date was ["+rssFromFile.getChannel().getLastBuildDate()+"] and "
					+ "in millis ["+rssFromFile.getChannel().getLastBuildDate().getTime()+"] and "
					+ "parameter getDurationInMillisForUpdate set to ["+userFeed.getDurationInMillisForUpdate()+"]. Current date&time in millis ["+currentTimeInMillis+"] - getLastBuildDate in millis ["+rssFromFile.getChannel().getLastBuildDate().getTime()+"] = ["+(currentTimeInMillis-rssFromFile.getChannel().getLastBuildDate().getTime())+"] < getDurationInMillisForUpdate [" + userFeed.getDurationInMillisForUpdate() + "], [" + Exec.getHumanReadableHoursMinutesSecondsFromMilliseconds(currentTimeInMillis-rssFromFile.getChannel().getLastBuildDate().getTime()) + " < " + Exec.getHumanReadableHoursMinutesSecondsFromMilliseconds(userFeed.getDurationInMillisForUpdate())+"]");
			return false; 
		}else {
			log.debug("It's good time to update feed id ["+userFeed.getId()+"] "
					+ "because last update date was ["+rssFromFile.getChannel().getLastBuildDate()+"] and "
					+ "in millis ["+rssFromFile.getChannel().getLastBuildDate().getTime()+"] and " 
					+ "parameter getDurationInMillisForUpdate set to ["+userFeed.getDurationInMillisForUpdate()+"]. "
					+ "Current date&time ["+currentTimeInMillis+"] - getLastBuildDate ["+rssFromFile.getChannel().getLastBuildDate().getTime()+"] = ["+(currentTimeInMillis-rssFromFile.getChannel().getLastBuildDate().getTime())+"] > getDurationInMillisForUpdate [" + userFeed.getDurationInMillisForUpdate() + "], [" + Exec.getHumanReadableHoursMinutesSecondsFromMilliseconds(currentTimeInMillis-rssFromFile.getChannel().getLastBuildDate().getTime()) + " > " + Exec.getHumanReadableHoursMinutesSecondsFromMilliseconds( userFeed.getDurationInMillisForUpdate())+"]");
			return true;
		}//
	}

	public void run() {
		log.info("Job started");
		int[] result;
		long t1 = new Date().getTime();
		try {
			result = updateFeeds();
			log.debug("Processed feeds: all ["+result[0]+"], successful ["+result[1]+"], failed ["+result[2]+"], postponed ["+result[3]+"]");
		} catch (NoSuchAlgorithmException e) {
			log.error("NoSuchAlgorithmException", e);
		} catch (SplitHTMLContent e) {
			log.error("SplitHTMLContent", e);
		} catch (GetURLContentException e) {
			log.error("GetURLContentException", e);
		} catch (GetSubstringForHtmlBodySplitException e) {
			log.error("GetSubstringForHtmlBodySplitException", e);
		} catch (IOException e) {
			log.error("IOException", e);
		} catch (Exception e) {
			log.error("Exception", e);
		}
		log.info("Job finished for ["+(Exec.getHumanReadableHoursMinutesSecondsFromMilliseconds(new Date().getTime()-t1))+"]");

	}

}
