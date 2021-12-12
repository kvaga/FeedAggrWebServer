package ru.kvaga.rss.feedaggrwebserver.servlets.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
	private static final Pattern p = Pattern.compile(".*/(?<source>.*)");
	public static synchronized String getSource(HttpServletRequest request) {
		String referer = request.getHeader("referer");
		//log.debug("Referer is ["+referer+"]");		
		Matcher m = p.matcher(referer);
		if(m.find()) {
			referer = "/" + m.group("source");
		}else {
			referer = "/";
		}
		return referer;
	}
}
