package ru.kvaga.rss.feedaggrwebserver.objects.user;

public class URLTranslation {
	private String domain;
	private String regexInURLPatternText;
	private String templateOutUrlText;
	
	public URLTranslation() {}
	public URLTranslation(String domain, String regexInURLPatternText, String templateOutUrlText){
		this.domain=domain;
		this.regexInURLPatternText=regexInURLPatternText;
		this.templateOutUrlText=templateOutUrlText;
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getRegexInURLPatternText() {
		return regexInURLPatternText;
	}
	public void setRegexInURLPatternText(String regexInURLPatternText) {
		this.regexInURLPatternText = regexInURLPatternText;
	}
	public String getTemplateOutUrl() {
		return templateOutUrlText;
	}
	public void setTemplateOutUrl(String templateOutUrlText) {
		this.templateOutUrlText = templateOutUrlText;
	}
	
	public String toString() {
		return "domain: " + domain + ", regexInURLPattern: " + regexInURLPatternText + ", templateOutUrl: " + templateOutUrlText;
	}
	
	public int hashCode() {
	    return (int) domain.hashCode();
	}
	
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    URLTranslation realobj = (URLTranslation) obj;
	    return domain.equals(realobj.domain);
	}
}
