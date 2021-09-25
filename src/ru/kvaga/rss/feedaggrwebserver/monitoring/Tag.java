package ru.kvaga.rss.feedaggrwebserver.monitoring;

public class Tag {
	private String name;
	private String value;
	public Tag(String name, String value) {
		this.name=name;
		this.value=value;
	}
	public Tag(String name, int value) {
		this.name=name;
		this.value=Integer.toString(value);
	}
	public Tag(String name, long value) {
		this.name=name;
		this.value=Long.toString(value); 
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
