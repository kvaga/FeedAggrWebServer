package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
@XmlRootElement(name="PatternRSSProperties")
public class UserRssItemPropertiesPatterns {
	
	private String domain;
	private String patternTitle;
	private String patternLink;
	private String patternDescription;

	
	public UserRssItemPropertiesPatterns() {}
	public UserRssItemPropertiesPatterns(String domain, String patternTitle, String patternLink, String patternDescription) {
		this.domain=domain;
		this.patternTitle=patternTitle;
		this.patternLink=patternLink;
		this.patternDescription=patternDescription;
	}
	
	
	@XmlAttribute
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	@XmlElement
	public String getPatternTitle() {
		return patternTitle;
	}
	public void setPatternTitle(String patternTitle) {
		this.patternTitle = patternTitle;
	}
	
	@XmlElement
	public String getPatternLink() {
		return patternLink;
	}
	public void setPatternLink(String patternLink) {
		this.patternLink = patternLink;
	}
	
	@XmlElement
	public String getPatternDescription() {
		return patternDescription;
	}
	public void setPatternDescription(String patternDescription) {
		this.patternDescription = patternDescription;
	}
	
	public int hashCode() {
		return new BigInteger(domain.getBytes()).intValue();
	}
	public boolean equals(Object object) {
		if(object instanceof UserRssItemPropertiesPatterns) {
			return ((UserRssItemPropertiesPatterns)object).equals(this);
		}else {
			return false;
		}
	}
	public boolean equals(UserRssItemPropertiesPatterns object) {
		return object.domain.equals(this.domain);
	}
}

