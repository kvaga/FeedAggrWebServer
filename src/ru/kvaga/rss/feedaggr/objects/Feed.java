package ru.kvaga.rss.feedaggr.objects;

import java.io.File;

public class Feed {
	private String id;
	private File xmlFile;
	private File confFile;
	
	public String toString() {
		return "Feed[id="+id+", xmlFile="+xmlFile+", confFile="+confFile+"]";
	}
	
	public Feed() {}
	public Feed(String id, File xmlFile, File confFile) {
		this.id=id;
		this.xmlFile=xmlFile;
		this.confFile=confFile;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public File getXmlFile() {
		return xmlFile;
	}
	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}
	public File getConfFile() {
		return confFile;
	}
	public void setConfFile(File confFile) {
		this.confFile = confFile;
	}
	
}
