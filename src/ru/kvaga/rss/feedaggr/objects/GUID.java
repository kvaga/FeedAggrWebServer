package ru.kvaga.rss.feedaggr.objects;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class GUID{
	@XmlAttribute
	private String isPermaLink;
	
	@XmlValue
    protected String value;

	GUID(){}
	
	public GUID(String isPermaLink, String value){
		this.isPermaLink=isPermaLink;
		this.value=value;
	}
    public String getValue() {
        return value;
    }
    public static String generateGUID(String anyString) throws NoSuchAlgorithmException {
//    	MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
// 	   messageDigest.update(anyString.getBytes());
// 	   String stringHash = new String(messageDigest.digest());
//    	return stringHash;
 	   return anyString;
    }
    public static String generateGUID(String title, String url) throws NoSuchAlgorithmException {
//    	return generateGUID(title+url);
    	return title+url;
    }
    
  
}