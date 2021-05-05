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
		String mas[] = {
				"One string with top-5 word",
				"the second string without top word",
				"the third string without TOP-8 word at all"
		};
		for(String s : mas) {
			if(s.toLowerCase().contains("top-".toLowerCase())) {
				System.out.println(s);
			}
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
