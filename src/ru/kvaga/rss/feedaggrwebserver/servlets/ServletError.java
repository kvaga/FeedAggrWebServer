package ru.kvaga.rss.feedaggrwebserver.servlets;
public class ServletError{
	String error=null;
	public ServletError(String errorDescription) {
		this.error=errorDescription;
	}
}