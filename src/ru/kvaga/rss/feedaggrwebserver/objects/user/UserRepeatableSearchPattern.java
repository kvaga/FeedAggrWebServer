package ru.kvaga.rss.feedaggrwebserver.objects.user;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
@XmlRootElement(name="Pattern")
public class UserRepeatableSearchPattern {
	
	private String domain;
	
	private String pattern;
	
	public UserRepeatableSearchPattern() {}
	public UserRepeatableSearchPattern(String domain, String pattern) {
		this.domain=domain;
		this.pattern=pattern;
	}
	
	
	@XmlAttribute
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	@XmlValue
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public int hashCode() {
		return new BigInteger(domain.getBytes()).intValue();
	}
	
	public boolean equals(Object object) {
		if(object instanceof UserRepeatableSearchPattern) {
			return ((UserRepeatableSearchPattern)object).equals(this);
		}else {
			return false;
		}
	}
	public boolean equals(UserRepeatableSearchPattern object) {
		return object.domain.equals(this.domain);
//		if(object instanceof UserRepeatableSearchPattern) {
//			System.err.println("["+(object.hashCode()==this.hashCode())+"]["+(object.domain+"]: " + object.hashCode() + " is equal ["+this.domain+"]: " + this.hashCode()));
//			return object.hashCode()==this.hashCode();
//		}else {
//			return false;
//		}
	}
	
}

