package ru.kvaga.rss.feedaggr;

public class FeedAggrException extends Exception{
	private FeedAggrException(String text) {
		super(text);
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
	}
	
	public static class GetSubstringForHtmlBodySplitException extends FeedAggrException{
		public GetSubstringForHtmlBodySplitException(String text){
			super(String.format("Can't get substring for html body splitting from string: %s. Tried to find {*} and {%}", text));
		}
	}
	
	public static class SplitHTMLContent extends FeedAggrException{
		public SplitHTMLContent(String htmlContent, String stringForSplitting) {
			super("Can't split html content \n"
					+ "["+htmlContent+"]\n"
					+ "String for splitting ["+stringForSplitting+"]\n"
					+ "Resulted massive size is less than 2");
		}
	}
}
