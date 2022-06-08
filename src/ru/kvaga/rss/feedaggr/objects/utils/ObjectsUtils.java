package ru.kvaga.rss.feedaggr.objects.utils;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.rss.feedaggr.objects.Channel;
import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.monitoring.*;

public class ObjectsUtils {
	final static Logger log = LogManager.getLogger(ObjectsUtils.class);



	// Save xml file from Object
	/*
	public static synchronized void saveXMLObjectToFile(Object object, Class _class, File file) throws JAXBException {
		long t1 = new Date().getTime();
//		ObjectsUtils.saveXMLObjectToFile(rss, rss.getClass(), file);
        JAXBContext jc = JAXBContext.newInstance(_class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(object, file);
        MonitoringUtils.sendResponseTime2InfluxDB(new Object(){}, new Date().getTime() - t1);
	}
	*/
	// Read XML object from file, then print this object
	/*
    public static synchronized Object getXMLObjectFromXMLFile(String xmlFile, Object object) throws JAXBException {
	    return getXMLObjectFromXMLFile(new File(xmlFile), object);
	}
    */
 // Read XML object from file, then print this object
    /*
    public static synchronized Object getXMLObjectFromXMLFile(File xmlFile, Object object) throws JAXBException {
		long t1 = new Date().getTime();

    	JAXBContext jaxbContext;
//		File feedXml = new File(xmlFile);
	    jaxbContext = JAXBContext.newInstance(object.getClass());              
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    Object obj = (Object) jaxbUnmarshaller.unmarshal(xmlFile);
        MonitoringUtils.sendResponseTime2InfluxDB(new Object(){}, new Date().getTime() - t1);

	    return obj;
	}
    */
	public static synchronized void printXMLObject(Object object) throws JAXBException {
		long t1 = new Date().getTime();

    	// For printing
	    StringWriter writer = new StringWriter();
	    JAXBContext jc = JAXBContext.newInstance(object.getClass());
	    Marshaller marshaller = jc.createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    marshaller.marshal(object, writer);
	    log.debug(writer.toString());
	    MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
    }
    
	// Read XML object from file, then print this object
    private static synchronized void readXMLObjectFromXMLFileAndPrint(String xmlFile) {
		long t1 = new Date().getTime();

    	JAXBContext jaxbContext;
		try
		{
			File feedXml = new File(xmlFile);

		    jaxbContext = JAXBContext.newInstance(RSS.class);              
		 
		    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		    RSS rss = (RSS) jaxbUnmarshaller.unmarshal(feedXml);
		    
		    // For printing
		    StringWriter writer = new StringWriter();
		    JAXBContext jc = JAXBContext.newInstance(RSS.class);
		    /*
//		    JAXBElement<Item[]> root = new JAXBElement<Item[]>(new QName("items"), 
//	                Item[].class, items.toArray(new Item[items.size()]));
*/
		    Marshaller marshaller = jc.createMarshaller();
		    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    marshaller.marshal(rss, writer);
		    MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);

		}
		catch (JAXBException e) 
		{
		   log.error("Exception", e);
		   MonitoringUtils.sendResponseTime2InfluxDB(new Object() {}, new Date().getTime() - t1);
		}
		
	}


}
