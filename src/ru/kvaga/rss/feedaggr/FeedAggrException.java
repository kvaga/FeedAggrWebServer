package ru.kvaga.rss.feedaggr;

public class FeedAggrException extends Exception{
	private FeedAggrException(String text) {
		super(text);
	}
	private FeedAggrException(String text, Exception e) {
		super(text, e);
	}
	
	public static class GetFeedsListByUser extends FeedAggrException{
		public GetFeedsListByUser(String text) {
			super(text);
		}
	}
	
	public static class CommonException extends FeedAggrException{
		public CommonException(String text) {
			super(text);
		}
	}
	public static class GetURLContentException extends FeedAggrException{
		public GetURLContentException(String text, String url) {
			super(String.format("An errror was occured during getting a content of the [%s] url. Description: %s",  url, text));
		}
		public GetURLContentException(String text, String url, Exception e) {
			super(String.format("An errror was occured during getting a content of the [%s] url. %s",  url, text),e);
		}
		public GetURLContentException(String url, Exception e) {
			super(url, e);
		}
	}
	
	public static class GetSubstringForHtmlBodySplitException extends FeedAggrException{
		public GetSubstringForHtmlBodySplitException(String text){
			super(String.format("Can't get substring for html body splitting from string: %s. Tried to find {*} and {%}", text));
		}
	}
	
	public static class SplitHTMLContent extends FeedAggrException{
		public SplitHTMLContent(String htmlContent, String stringForSplitting) {
			super("Can't split html content \n"
					//+ "["+(htmlContent.length()>=500?htmlContent.substring(0, htmlContent.length()-1): htmlContent)+"]\n"
					+ "[<<<STANZA>>>]"
					+ "String for splitting ["+stringForSplitting+"]\n"
					+ "Resulted massive size is less than 2");
		}
		public SplitHTMLContent(String url, String htmlContent, String stringForSplitting) {
			super("Can't split html content for url ["+url+"]\n"
//					+ "["+(htmlContent.length()>=500?htmlContent.substring(0, htmlContent.length()-1): htmlContent)+"]\n"
+ "[<<<STANZA>>>]"

					+ "String for splitting ["+stringForSplitting+"]\n"
					+ "Resulted massive size is less than 2");
		}
	}
}
