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
