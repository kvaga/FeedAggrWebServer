package ru.kvaga.rss.feedaggrwebserver;

import java.io.File;

import ru.kvaga.monitoring.influxdb2.InfluxDB;

public class ConfigMap {
	public static File configFile=null;
	public static File dataPath=null;
	public static File feedsPath=null;
	public static File usersPath=null;
	public static String adminLogin;
	public static String adminPassword;
	public static String generator="Feed Aggr Web Server Generator";
	public static String rssVersion="2.0";
	public static int ttlOfFeedsInDays=365;
	public static String prefixForlog4jJSP="ru.kvaga.feedaggrwebserver.jsps.";
	public static String INFLUXDB_HOST,	INFLUXDB_DBNAME;
	public static int INFLUXDB_PORT;
	public static int INFLUXDB_THREAD_NUMBER;
	public static boolean INFLUXDB_ENABLED;
	public static int INFLUXDB_COUNT_OF_ATTEMPTS_IF_FAILS;
	public static long INFLUXDB_TIMEOUT;
	public static long DEFAULT_DURATION_IN_MILLIS_FOR_FEED_UPDATE=DurationMillisecondsForUpdatingFeeds.EACH_DAY;
	public static boolean TEST_MODE=false;
	public static long WAIT_TIME_AFTER_GET_CONTENT_URL_EXCEPTION_IN_MILLIS=1000;
	public static int SERVLET_SHOW_FEED_BUFFER_READ_BYTES=512;
	public static int UPDATE_COMPOSITE_RSS_FILES_DAYS_COUNT_FOR_DELETION=31;
	public static String trustStore;
	public static String trustStorePassword;
			

	
	/*
	 regex for drive: https://www.drive2.ru/experience/kia/g3688?sort=Date
<div class="c-post-preview__title">{*}<a class="c-link c-link--text" href="{%}"  rel="noopener" target="_blank" data-ym-target="post_title">{%}</a>{*}<div class="c-post-preview__lead">{%}<button class="c-post-preview
	 regex for drive: youtube
<entry>{*}<title>{%}</title>{*}<link rel=\"alternate\" href=\"{%}\"/>{*}<author>{*}<media:description>{%}</media:description>{*}</entry>
     regex for 4brain: "item":{"id":{*},"broker_id":{*}"price_start":{%},"price":{*}"target_yield":{%},"title":"{%}","description":"{%}\r\n{*}href=\"{%}?ii_ref=lfls{*}"name":"{%}","accuracy":{%}}}},
		
     // Tinkoff Investmnets
	 //	URL: https://www.tinkoff.ru/api/invest/smartfeed-public/v1/feed/api/main?nav_code=ideas
	 // Desciption:
		(Broker: {%6}, upside: {%2}, accuracy: {%7}): {%3}
		<br>
		{%4}: {%5}
		<br>
		<center><font size="36"><a href="{%1}">============================</a></font></center><br><center><font size="36"><a href="{%1}">============ Link ============</a></font></center><br><center><font size="36"><a href="{%1}">============================</a></font></center><br>
		regex: "item":{"id":{%},"broker_id":{*}target_yield":{%},"title":"{%}","description":{*}"ticker":"{%}","name"{*}"brand_name":"{%}","logo_name"{*}"name":"{%}","accuracy":{%}}}},

	// Sber Vacancy
	 regex: "title":"{%}","header"{*}"id":{%},"fullPartTime{*}custorgStreamI":"{%}","accessibility{*}
	 url: https://my.sbertalents.ru/job-requisition/v2?postingCategory=797&postingCategory=796&postingCategory=777&region=1466&keywords=%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80&page=0&size=120

	Tinkoff Invest
	URL:
	Regex: "item":{"id":{%},"broker_id"{*},"price_start":{%},"price":{%},"yield":{%},"target_yield":{%},"title":"{%}","description":{*}"ticker":"{%}","name":"{%}",{*}brand_name":"��������",{*}"broker":{*}"name":"{%}","accuracy":{%}}}},

	Tinkoff Feed Line
	URL: https://www.tinkoff.ru/api/invest/smartfeed-public/v1/feed/api/main
	regex: "item":{"id":{%},"announce":"{%}","title":"{%}","img_big":"","date":"{*}ticker":"{%}","name":"{%}","type":"{*}","brand_name":"{%}","logo{*}price":{%}}],"tags"{*}"name":"{%}"},{*}
	
	HeadHunter
	URL:
	regex: 
	<title>{%}</title><link>{%}</link>{*}<description><!\[CDATA\[{%}\]\]></description>{*}</item>
	*/

	
}
