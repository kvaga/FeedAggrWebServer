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

public class ObjectsUtils {
	final static Logger log = LogManager.getLogger(ObjectsUtils.class);

	public static void main(String[] args) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(RSS.class);

        RSS rss = new RSS();
        rss.setVersion("2.0");
        
        Channel channel = new Channel();
        channel.setTitle("Some title");
        channel.setLink("http://yandex.ru");
        channel.setTtl(360);
        channel.setLastBuildDate(new Date());
        channel.setGenerator("Channel's generator");
        channel.setDescription("Channel's description");
        
        ArrayList<Item> item = new ArrayList<Item>();
        Item item1 = new Item();
        item1.setTitle("Item's 1 title");
        item1.setLink("http://yandex.ru/item1");
        item1.setDescription("<![CDATA[Item's 1 description]]>");
        item1.setPubDate(new Date());
        item1.setGuid(new GUID("false", "693b733d9ae3955d1b948dc44e568f61"));
        
        Item item2 = new Item();
        item2.setTitle("Item's 2 title");
        item2.setLink("http://yandex.ru/item2");
        item2.setDescription("<![CDATA[Item's 2 description]]>");
        item2.setPubDate(new Date());
        item2.setGuid(new GUID("false", "7515ef2bb57c3e69bb31a60d2ce89207"));
        item.add(item1);
        item.add(item2);
        
        channel.setItem(item);
        rss.setChannel(channel);
        
//        customer.setId(123);
//        ArrayList<String> name = new ArrayList<String>();
//        name.add("Sasha");
//        name.add("Petya");
//        customer.setName(name);

        /*
        // print xml object
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(rss, System.out);
        */
        
        // print XML Objectfrom xml file
//        readFromXMLFile("C:/eclipseWorkspace/FeedAggrWebServer/WebContent/data/kvaga/54433456543345666542245/qqq.xml");
        printXMLObject(getXMLObjectFromXMLFile(
        		"C:/eclipseWorkspace/FeedAggrWebServer/WebContent/data/kvaga/54433456543345666542245/qqq.xml",
        		new RSS()
        		))
        		;
        ObjectsUtils.saveXMLObjectToFile(rss, rss.getClass(), new File("C:/temp/qqq1.xml"));
	}

	// Save xml file from Object
	public static void saveXMLObjectToFile(Object object, Class _class, File file) throws JAXBException {
//		ObjectsUtils.saveXMLObjectToFile(rss, rss.getClass(), file);
        JAXBContext jc = JAXBContext.newInstance(_class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(object, file);
	}
	// Read XML object from file, then print this object
    public static Object getXMLObjectFromXMLFile(String xmlFile, Object object) throws JAXBException {
	    return getXMLObjectFromXMLFile(new File(xmlFile), object);
	}
    
 // Read XML object from file, then print this object
    public static Object getXMLObjectFromXMLFile(File xmlFile, Object object) throws JAXBException {
    	JAXBContext jaxbContext;
//		File feedXml = new File(xmlFile);
	    jaxbContext = JAXBContext.newInstance(object.getClass());              
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    Object obj = (Object) jaxbUnmarshaller.unmarshal(xmlFile);
	    return obj;
	}
    
	public static void printXMLObject(Object object) throws JAXBException {
    	// For printing
	    StringWriter writer = new StringWriter();
	    JAXBContext jc = JAXBContext.newInstance(object.getClass());
	    Marshaller marshaller = jc.createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    marshaller.marshal(object, writer);
	    log.debug(writer.toString());
    }
    
	// Read XML object from file, then print this object
    private static void readXMLObjectFromXMLFileAndPrint(String xmlFile) {
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
		    System.out.println(writer.toString());
		    
//		    System.out.println(rss);
		}
		catch (JAXBException e) 
		{
		    e.printStackTrace();
		}
		
	}


}
