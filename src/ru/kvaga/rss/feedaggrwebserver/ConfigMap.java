package ru.kvaga.rss.feedaggrwebserver;

import java.io.File;

public class ConfigMap {
	public static File configFile=null;
	public static File dataPath=null;
	public static File feedsPath=null;
	public static File usersPath=null;
	public static String adminLogin;
	public static String adminPassword;
	public static String generator="Feed Aggr Web Server Generator";
	public static String rssVersion="2.0";
	public static int ttlOfFeedsInDays=90;
	public static String prefixForlog4jJSP="ru.kvaga.feedaggrwebserver.jsps.";
	
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
	Regex: "item":{"id":{%},"broker_id"{*},"price_start":{%},"price":{%},"yield":{%},"target_yield":{%},"title":"{%}","description":{*}"ticker":"{%}","name":"{%}",{*}brand_name":"Роснефть",{*}"broker":{*}"name":"{%}","accuracy":{%}}}},

	Tinkoff Feed Line
	URL: https://www.tinkoff.ru/api/invest/smartfeed-public/v1/feed/api/main
	regex: "item":{"id":{%},"announce":"{%}","title":"{%}","img_big":"","date":"{*}ticker":"{%}","name":"{%}","type":"{*}","brand_name":"{%}","logo{*}price":{%}}],"tags"{*}"name":"{%}"},{*}
	*/

}
