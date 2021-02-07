package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
//		String itemContentTemplate="{%3}\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============ Link ============</a></font></center>\r\n" + 
//				"<br>\r\n" + 
//				"<center><font size=\"36\"><a href=\"{%1}\">============================</a></font></center>\r\n" + 
//				"";
//		
//		System.out.println(itemContentTemplate.
//				replaceAll("\\{%3}", "QQQ")
//				.replaceAll("\\{%1}", "http://yandex.ru")
//				);
		checkItemURLForFullness("https://www.drive2.ru/experience/kia/g3688/","/l/579308488493106103/");
	}
	
	static String checkItemURLForFullness(String feedURL, String itemURL) {
		String leftPathOfFeedURL=null;
		
		if(itemURL.startsWith("http:") || itemURL.startsWith("https:")) {
			System.out.println("Item URL doesn't start from any 'http:' or 'https:'");
			return itemURL;
		}
		
		if(itemURL.startsWith("/")) {
			itemURL=itemURL.replaceFirst("/", "");
		}
		
		// getting url first path
		Pattern pattern = Pattern.compile("http[s]{0,1}:\\/\\/.*?\\/");
		Matcher matcher = pattern.matcher(feedURL);
		if(matcher.find()) {
			leftPathOfFeedURL = matcher.group();
		}else {
			// exception
		}
		return leftPathOfFeedURL+itemURL;
	}

}
