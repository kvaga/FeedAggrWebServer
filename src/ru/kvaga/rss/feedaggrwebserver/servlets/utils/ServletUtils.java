package ru.kvaga.rss.feedaggrwebserver.servlets.utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import ru.kvaga.rss.feedaggrwebserver.servlets.ServletError;

public class ServletUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
	  
//	public static String getJsonErrorString(String errorDescription) {
//		return "{ 'error': '"+errorDescription+"'}";
//	}
	
	
	public static void responseJSONError(String textNonJSON, HttpServletResponse response) throws JsonProcessingException, IOException {
		response.getWriter().print(new Gson().toJson(new ServletError(textNonJSON)));

		/* 
		Error err = new Error(textNonJSON);
		response.getWriter().write(
				OBJECT_MAPPER.writeValueAsString(
						//ServletUtils.getJsonErrorString(textNonJSON)
							err
						)
				//ServletUtils.getJsonErrorString(textNonJSON)

//						OBJECT_MAPPER.writeValueAsBytes(ServletUtils.getJsonErrorString(textNonJSON)
				);
				*/
	}
	 
	public static void responseJSON(Object object, HttpServletResponse response) throws JsonProcessingException, IOException {
		//response.getWriter().write(OBJECT_MAPPER.writeValueAsString(object));
		response.getWriter().print(new Gson().toJson(object));

	}
	
//	public static void responseJSON(String str, HttpServletResponse response) throws JsonProcessingException, IOException {
//		response.getWriter().write(str);
//	}
}
