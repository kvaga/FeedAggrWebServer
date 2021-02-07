package ru.kvaga.rss.feedaggr.objects;


import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;





@XmlRootElement
public class RSS {

    public RSS(){ }
    public RSS(String version, Channel channel) {
    	this.version=version;
    	this.channel=channel;
    }
	@XmlAttribute
    private String version;
    
    private Channel channel = new Channel();

    public String getVeString() {
    	return version;
    }
    
    public void setVersion(String version) {
    	this.version=version;
    }
    
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	// Read XML object from file, then print this object
    public static RSS getRSSObjectFromXMLFile(String xmlFile) throws JAXBException {
    	JAXBContext jaxbContext;
		File feedXml = new File(xmlFile);
	    jaxbContext = JAXBContext.newInstance(RSS.class);              
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    RSS rss = (RSS) jaxbUnmarshaller.unmarshal(feedXml);
	    return rss;
	}
}








