package ru.kvaga.rss.feedaggr.objects;


import java.io.File;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;





@XmlRootElement
public class RSS {
	public static Logger log=LogManager.getLogger(RSS.class);
    public RSS(){ }
    public RSS(String version, Channel channel) {
    	this.version=version;
    	this.channel=channel;
    }
	@XmlAttribute
    private String version="2.0";
    
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
    	long t1 = new Date().getTime();
    	JAXBContext jaxbContext;
		File feedXml = new File(xmlFile);
	    jaxbContext = JAXBContext.newInstance(RSS.class);              
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    RSS rss = (RSS) jaxbUnmarshaller.unmarshal(feedXml);
	    InfluxDB.getInstance().send("response_time,method=RSS.getRSSObjectFromXMLFile", new Date().getTime() - t1);

	    return rss;
	}
    
    public void removeItemsOlderThanXDays(int xDays) {
    	long t1 = new Date().getTime();
    	ArrayList<Item> updatedListOfItems = new ArrayList<Item>();
    	for(Item item : getChannel().getItem()) {
    		if(item.getPubDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now().minusDays(xDays))) {
    			log.debug("RSS Item ["+item.getGuid()+"] was deleted because is older than ["+xDays+"]");
    			continue;
    		}
    		updatedListOfItems.add(item);
    	}
    	getChannel().setItem(updatedListOfItems);
	    InfluxDB.getInstance().send("response_time,method=RSS.removeItemsOlderThanXDays", new Date().getTime() - t1);

    }
    
    public synchronized void saveXMLObjectToFile(File file) throws JAXBException {
    	long t1 = new Date().getTime();
        JAXBContext jc = JAXBContext.newInstance(this.getClass());
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(this, file);
        log.debug("Object rss [" + getChannel().getTitle() + "] successfully saved to the [" + file + "] file");
	    InfluxDB.getInstance().send("response_time,method=RSS.saveXMLObjectToFile", new Date().getTime() - t1);

	}
   
}







