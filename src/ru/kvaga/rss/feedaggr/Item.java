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
}
