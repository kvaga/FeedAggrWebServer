package ru.kvaga.rss.feedaggr;

import java.util.HashMap;




public class Item{

	HashMap<Integer, String> percentItemsMassive = new HashMap<Integer,String>();
	public void add(int i, String value) {
		percentItemsMassive.put(i, value);
	}
	public String get(int i) {
		return percentItemsMassive.get(i);
	}
	public int length() {
		return percentItemsMassive.size();
	}
	
	public String getContentForPrinting() {
		StringBuilder sb = new StringBuilder();
		
		for(int i=1; i<= percentItemsMassive.size(); i++) {
			sb.append("Item["+i+"]="+percentItemsMassive.get(i)+"\n");
		}
		
		return sb.toString();
	}
}
