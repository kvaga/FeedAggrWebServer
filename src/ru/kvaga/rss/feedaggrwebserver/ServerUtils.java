package ru.kvaga.rss.feedaggrwebserver;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ru.kvaga.rss.feedaggr.FeedAggrException;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetFeedsListByUser;
import ru.kvaga.rss.feedaggr.objects.Feed;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggr.objects.RSS;

public class ServerUtils {
	
	public static void main(String[] args) throws GetFeedsListByUser, JAXBException {
		for(Feed feedOnServer : getFeedsListByUser("kvaga")) {
//			System.out.println(feedOnServer.getXmlFile());
			RSS rssFeed = (RSS)ObjectsUtils.getXMLObjectFromXMLFile(feedOnServer.getXmlFile(), new RSS());
			System.out.println(rssFeed.getChannel().getTitle());
			System.out.println("Source URL: "+rssFeed.getChannel().getLink());
			System.out.println("Last updated: " + rssFeed.getChannel().getLastBuildDate());
			 
//			ObjectsUtills.printXMLObject(rssFeed);
		}
	}
	
	
	public static String getNewFeedId() {
		return ""+new Date().getTime();
	}
	
	public static ArrayList<Feed> getFeedsListByUser(String userDirPath) throws GetFeedsListByUser, JAXBException{
//		String dataDirText="WebContent/data";
//		System.out.println("CurrentDir: " + userDirPath);
//		String userDirText=String.format("%s/%s", dataDirText,user);
		ArrayList<Feed> al = new ArrayList<Feed>();
		File userDir = new File(userDirPath);
//		System.out.println("UserDir: " + userDir);

		if(!userDir.isDirectory()) {
			throw new FeedAggrException.GetFeedsListByUser(String.format("Couldn't find user's [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s", userDir, userDir.getPath(),userDir.getAbsolutePath(), new File(".").getAbsolutePath()));
		}
		for(File feedIdDir : userDir.listFiles()) {
			if(feedIdDir.isDirectory()) {
//				System.out.println("feedIdDir: " + feedIdDir);
				String feedId = feedIdDir.getName();
				File feedXmlFile = new File(String.format("%s/%s.xml", feedIdDir.getPath(),feedId,feedId));
				File feedConfigFile = new File(String.format("%s/%s.conf", feedIdDir.getPath(),feedId,feedId));
				Feed feed = new Feed(feedId, feedXmlFile, feedConfigFile);
//				System.out.println(feed);
				al.add(feed);
			}
		}
		return al;
	}
	
	public static ArrayList<Feed> getFeedsList(String realPath) throws GetFeedsListByUser, JAXBException{
		/*
//		String dataDirText="WebContent/data";
//		System.out.println("CurrentDir: " + userDirPath);
//		String userDirText=String.format("%s/%s", dataDirText,user);
		ArrayList<Feed> al = new ArrayList<Feed>();
		File dir = new File(realPath);
//		System.out.println("UserDir: " + userDir);

		if(!dir.isDirectory()) {
			throw new FeedAggrException.GetFeedsListByUser(String.format("Couldn't find feeds [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s", dir, dir.getPath(),dir.getAbsolutePath(), new File(".").getAbsolutePath()));
		}
		for(File feedFile : dir.listFiles()) {
//				System.out.println("feedIdDir: " + feedIdDir);
				String feedId = feedFile.getName().replaceAll("\\.xml", "");
				Feed feed = new Feed(feedId, feedFile, null);
//				System.out.println(feed);
				al.add(feed);
		}
		return al;
		*/
		return getFeedsList(new File(realPath));
	}
	
	public static ArrayList<Feed> getFeedsList(File dir) throws GetFeedsListByUser, JAXBException{
//		String dataDirText="WebContent/data";
//		System.out.println("CurrentDir: " + userDirPath);
//		String userDirText=String.format("%s/%s", dataDirText,user);
		ArrayList<Feed> al = new ArrayList<Feed>();
//		System.out.println("UserDir: " + userDir);

		if(!dir.isDirectory()) {
			throw new FeedAggrException.GetFeedsListByUser(String.format("Couldn't find feeds [%s] directory because [path: %s, absolutePath: %s] is not a directory. Current directory: %s", dir, dir.getPath(),dir.getAbsolutePath(), new File(".").getAbsolutePath()));
		}
		for(File feedFile : dir.listFiles()) {
//				System.out.println("feedIdDir: " + feedIdDir);
				String feedId = feedFile.getName().replaceAll("\\.xml", "");
				Feed feed = new Feed(feedId, feedFile, null);
//				System.out.println(feed);
				al.add(feed);
		}
		return al;
	}
	
	public static final String escapeHTML(String s){
		   StringBuffer sb = new StringBuffer();
		   int n = s.length();
		   for (int i = 0; i < n; i++) {
		      char c = s.charAt(i);
		      switch (c) {
		         case '<': sb.append("&lt;"); break;
		         case '>': sb.append("&gt;"); break;
		         case '&': sb.append("&amp;"); break;
		         case '"': sb.append("&quot;"); break;
		         case 'à': sb.append("&agrave;");break;
		         case 'À': sb.append("&Agrave;");break;
		         case 'â': sb.append("&acirc;");break;
		         case 'Â': sb.append("&Acirc;");break;
		         case 'ä': sb.append("&auml;");break;
		         case 'Ä': sb.append("&Auml;");break;
		         case 'å': sb.append("&aring;");break;
		         case 'Å': sb.append("&Aring;");break;
		         case 'æ': sb.append("&aelig;");break;
		         case 'Æ': sb.append("&AElig;");break;
		         case 'ç': sb.append("&ccedil;");break;
		         case 'Ç': sb.append("&Ccedil;");break;
		         case 'é': sb.append("&eacute;");break;
		         case 'É': sb.append("&Eacute;");break;
		         case 'è': sb.append("&egrave;");break;
		         case 'È': sb.append("&Egrave;");break;
		         case 'ê': sb.append("&ecirc;");break;
		         case 'Ê': sb.append("&Ecirc;");break;
		         case 'ë': sb.append("&euml;");break;
		         case 'Ë': sb.append("&Euml;");break;
		         case 'ï': sb.append("&iuml;");break;
		         case 'Ï': sb.append("&Iuml;");break;
		         case 'ô': sb.append("&ocirc;");break;
		         case 'Ô': sb.append("&Ocirc;");break;
		         case 'ö': sb.append("&ouml;");break;
		         case 'Ö': sb.append("&Ouml;");break;
		         case 'ø': sb.append("&oslash;");break;
		         case 'Ø': sb.append("&Oslash;");break;
		         case 'ß': sb.append("&szlig;");break;
		         case 'ù': sb.append("&ugrave;");break;
		         case 'Ù': sb.append("&Ugrave;");break;         
		         case 'û': sb.append("&ucirc;");break;         
		         case 'Û': sb.append("&Ucirc;");break;
		         case 'ü': sb.append("&uuml;");break;
		         case 'Ü': sb.append("&Uuml;");break;
		         case '®': sb.append("&reg;");break;         
		         case '©': sb.append("&copy;");break;   
		         case '€': sb.append("&euro;"); break;
		         // be carefull with this one (non-breaking whitee space)
		         case ' ': sb.append("&nbsp;");break;         
		         
		         default:  sb.append(c); break;
		      }
		   }
		   return sb.toString();
		}
	
	public static String stringToHTMLString(String string) {
	    StringBuffer sb = new StringBuffer(string.length());
	    // true if last char was blank
	    boolean lastWasBlankChar = false;
	    int len = string.length();
	    char c;

	    for (int i = 0; i < len; i++)
	        {
	        c = string.charAt(i);
	        if (c == ' ') {
	            // blank gets extra work,
	            // this solves the problem you get if you replace all
	            // blanks with &nbsp;, if you do that you loss 
	            // word breaking
	            if (lastWasBlankChar) {
	                lastWasBlankChar = false;
	                sb.append("&nbsp;");
	                }
	            else {
	                lastWasBlankChar = true;
	                sb.append(' ');
	                }
	            }
	        else {
	            lastWasBlankChar = false;
	            //
	            // HTML Special Chars
	            if (c == '"')
	                sb.append("&quot;");
	            else if (c == '&')
	                sb.append("&amp;");
	            else if (c == '<')
	                sb.append("&lt;");
	            else if (c == '>')
	                sb.append("&gt;");
	            else if (c == '\n')
	                // Handle Newline
	                sb.append("&lt;br/&gt;");
	            else {
	                int ci = 0xffff & c;
	                if (ci < 160 )
	                    // nothing special only 7 Bit
	                    sb.append(c);
	                else {
	                    // Not 7 Bit use the unicode system
	                    sb.append("&#");
	                    sb.append(new Integer(ci).toString());
	                    sb.append(';');
	                    }
	                }
	            }
	        }
	    return sb.toString();
	}
}
