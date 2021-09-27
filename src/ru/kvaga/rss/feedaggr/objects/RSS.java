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

import ru.kvaga.monitoring.influxdb2.InfluxDB;
import ru.kvaga.rss.feedaggr.objects.utils.ObjectsUtils;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.monitoring.*;




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
	public static RSS getRSSObjectFromXMLFile(File xmlFile) throws JAXBException {
    	long t1 = new Date().getTime();
    	JAXBContext jaxbContext;
	    jaxbContext = JAXBContext.newInstance(RSS.class);              
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    RSS rss = (RSS) jaxbUnmarshaller.unmarshal(xmlFile);
	    MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
	    return rss;
	}
    public static RSS getRSSObjectFromXMLFile(String xmlFile) throws JAXBException {
    	long t1 = new Date().getTime();
    	MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return getRSSObjectFromXMLFile(new File(xmlFile));
	}
    
    public static RSS getRSSObjectByFeedId(String feedId) throws JAXBException {
    	long t1 = new Date().getTime();
    	MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		return getRSSObjectFromXMLFile(new File(ConfigMap.feedsPath + File.separator + feedId + ".xml"));
	}
    
    public synchronized int removeItemsOlderThanXDays(int xDays) {
    	long t1 = new Date().getTime();
    	int countOfDeletedItems=0;
    	ArrayList<Item> updatedListOfItems = new ArrayList<Item>();
    	for(Item item : getChannel().getItem()) {
    		if(item.getPubDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now().minusDays(xDays))) {
    			log.debug("RSS Item ["+item.getGuid()+"] was deleted because is older than ["+xDays+"]");
    			countOfDeletedItems++;
    			continue;
    		}
    		updatedListOfItems.add(item);
    	}
    	getChannel().setItem(updatedListOfItems);
    	MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
    	return countOfDeletedItems;
    }
    
    public synchronized void saveXMLObjectToFile(File file) throws JAXBException {
    	long t1 = new Date().getTime();
        JAXBContext jc = JAXBContext.newInstance(this.getClass());
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(this, file);
        log.debug("Object rss [" + getChannel().getTitle() + "] successfully saved to the [" + file + "] file");
        MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

	}
    public synchronized void saveXMLObjectToFile(String file) throws JAXBException {
    	saveXMLObjectToFile(new File(file));
	}
    
    public String toString() {
    	return "RSS channel title ["+channel.getTitle()+"], link ["+channel.getLink()+"], lastBuildDate ["+channel.getLastBuildDate()+"], version ["+version+"]";
    }
    
    public Date[] getOldestNewestPubDate() {
    	long t1 = new Date().getTime();
    	Date oldest = new Date(), newest=new Date();
    	for(Item item : getChannel().getItem()) {
    		if(item.getPubDate().before(oldest)) {
    			oldest=item.getPubDate();
    		}
    		if(item.getPubDate().after(newest)) {
    			newest=item.getPubDate();
    		}
    	}
        MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
    	return new Date[] {oldest, newest};
    }
   
}







