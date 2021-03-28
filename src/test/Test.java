package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

public class Test {

	public static void main(String[] args) throws GetURLContentException, IOException {
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
		
//		String url="https://www.youtube.com/user/AcademeG/videos";
		String urls[]= {"https://www.youtube.com/c/FailArmyNation/videos"};
//		for(String url : urls) {
//			getDomainFromURL(url);
//		}
		String str="<feed xmlns:yt=\"http://www.youtube.com/xml/schemas/2015\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns=\"http://www.w3.org/2005/Atom\">\r\n" + 
				"<link rel=\"self\" href=\"http://www.youtube.com/feeds/videos.xml?channel_id=UC0lT9K8Wfuc1KPqm6YjRf1A\"/>\r\n" + 
				"<id>yt:channel:UC0lT9K8Wfuc1KPqm6YjRf1A</id>\r\n" + 
				"<yt:channelId>UC0lT9K8Wfuc1KPqm6YjRf1A</yt:channelId>\r\n" + 
				"<title>AcademeG</title>\r\n" + 
				"<link rel=\"alternate\" href=\"https://www.youtube.com/channel/UC0lT9K8Wfuc1KPqm6YjRf1A\"/>\r\n" + 
				"<author>\r\n" + 
				"<name>AcademeG</name>\r\n" + 
				"<uri>https://www.youtube.com/channel/UC0lT9K8Wfuc1KPqm6YjRf1A</uri>\r\n" + 
				"</author>\r\n" + 
				"<published>2010-12-27T16:26:44+00:00</published>\r\n" + 
				"<entry>\r\n" + 
				"<id>yt:video:NM3_wjqWhro</id>\r\n" + 
				"<yt:videoId>NM3_wjqWhro</yt:videoId>\r\n" + 
				"<yt:channelId>UC0lT9K8Wfuc1KPqm6YjRf1A</yt:channelId>\r\n" + 
				"<title>RANGE ROVER ПРОФИ. Начало.</title>\r\n" + 
				"<link rel=\"alternate\" href=\"https://www.youtube.com/watch?v=NM3_wjqWhro\"/>\r\n" + 
				"<author>\r\n" + 
				"<name>AcademeG</name>\r\n" + 
				"<uri>https://www.youtube.com/channel/UC0lT9K8Wfuc1KPqm6YjRf1A</uri>\r\n" + 
				"</author>\r\n" + 
				"<published>2021-02-25T16:59:39+00:00</published>\r\n" + 
				"<updated>2021-02-25T18:43:01+00:00</updated>\r\n" + 
				"<media:group>\r\n" + 
				"<media:title>RANGE ROVER ПРОФИ. Начало.</media:title>\r\n" + 
				"<media:content url=\"https://www.youtube.com/v/NM3_wjqWhro?version=3\" type=\"application/x-shockwave-flash\" width=\"640\" height=\"390\"/>\r\n" + 
				"<media:thumbnail url=\"https://i3.ytimg.com/vi/NM3_wjqWhro/hqdefault.jpg\" width=\"480\" height=\"360\"/>\r\n" + 
				"<media:description>Регистрируйся и возвращай свой горящий кэшбэк вместе с LetyShops — https://letyshops.app.link/y8FL0Dv84db Электротонировка на любой автомобиль https://on-glass.ru Музыка: https://vk.com/wall-114871206_27422 ПО вопросам рекламы и сотрудничества: academeg@a-proved.ru Мой канал стримов: https://www.youtube.com/c/AcademeGDailyStream Instagram: https://instagram.com/academeg Вконтакт: http://vk.com/AcademeG AcademeG тру ориджинал групп: http://vk.com/academeg_reviews Фтарой канал: https://www.youtube.com/user/AcademeG2ndCH TikTok https://www.tiktok.com/@academeg.official Автохимия Suprotec-Aprohim: https://suprotec.ru/</media:description>\r\n" + 
				"<media:community>";
//		System.out.println(Exec.getTitleFromHtmlBody(str));
//		System.out.println(getYoutubeFeedURL(url));
		//"item":{"id":{*},"broker_id":{*}"price_start":{%},"price":{*}"target_yield":{%},"title":"{%}","description":"{%}\r\n{*}href=\"{%}?ii_ref=lfls{*}"name":"{%}","accuracy":{%}}}},
		
		//-1==> temp_text_pattern: "item":\{"id":.*,"broker_id":.*"price_start":(.*),"price":.*"target_yield":(.*),"title":"(.*)","description":"(.*)\\r\\n.*href=\\"(.*)?ii_ref=lfls.*"name":"(.*)","accuracy":(.*)}}},
		//0==> repeatableSearch: "item":{"id":.*,"broker_id":.*"price_start":(.*),"price":.*"target_yield":(.*),"title":"(.*)","description":"(.*)\r\n.*href=\"(.*)?ii_ref=lfls.*"name":"(.*)","accuracy":(.*)}}},

		
		String st = "\"item\":{\"id\":.*,\"broker_id\":.*\"price_start\":(.*),\"price\":.*\"target_yield\":(.*),\"title\":\"(.*)\",\"description\":\"(.*)\r\n.*href=\"(.*)?ii_ref=lfls.*\"name\":\"(.*)\",\"accuracy\":(.*)}}},\r\n" + 
				"";
//		for(String s : st.split(":{".replaceAll("\\{", "\\\\{"))) {
//			System.out.println(s);
//		}
//		System.out.println(
//				st.replace('\r', '.')
//				.replace('\n', '.')
//				.replace("\\", "\\\\")
//				.replaceAll("\\{", "\\\\{")
//				);
		String regex=readString(new File("c:/temp/11/string_regex.txt"));
		String string_for_test=readString(new File("c:/temp/11/string_for_test.txt"));
		System.out.println("regex before: " + regex);
		System.out.println("string_for_test: " + string_for_test);
		
regex=regex
.replaceAll("(\\\\r\\\\n|\\\\n)", ".")
.replaceAll("\\{", "\\\\{")
;
System.out.println("regex after: " + regex);

		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(string_for_test);

		if(m.find()) {
			System.out.println("group[1]="+m.group(1));
		}else {
			System.err.println("error");
		}
	}
	
	
	private static String readString(File file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String s;
		StringBuilder sb = new StringBuilder();
		while((s=br.readLine())!=null) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	
	

}
