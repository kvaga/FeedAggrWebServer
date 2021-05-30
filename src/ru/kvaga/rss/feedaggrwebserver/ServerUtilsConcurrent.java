package ru.kvaga.rss.feedaggrwebserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.rss.feedaggr.FeedAggrException;

public final class ServerUtilsConcurrent {
	private static Logger log = LogManager.getLogger(ServerUtilsConcurrent.class);
    private static ServerUtilsConcurrent instance;
    
    private ExecutorService executor = null;

	private ServerUtilsConcurrent() {
		executor = Executors.newFixedThreadPool(10);
		log.info("ServerUtilsConcurrent was initialized");
	}
	
	public static ServerUtilsConcurrent getInstance() {
		if(instance==null) {
			instance=new ServerUtilsConcurrent();
		}
		return instance;
	}
	
	public String getURLContent(String urlText) throws FeedAggrException.GetURLContentException, InterruptedException, ExecutionException {
	    Future<String> futureCall = executor.submit(new GetURLContentTask(urlText));
		return futureCall.get();
	}

}

class GetURLContentTask implements Callable<String>{
	private static Logger log = LogManager.getLogger(GetURLContentTask.class);
	private String urlText;
	GetURLContentTask(String urlText){
		this.urlText=urlText;
	}
	public String call() throws Exception {
		long t1 = new Date().getTime();
		String body = null;
		String charset; // You should determine it based on response header.
		HttpURLConnection con=null;

		try {
			URL url = new URL(urlText);
			con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//			con.setRequestProperty("accept-encoding", "gzip, deflate, br");
			con.setRequestProperty("accept-encoding", "gzip");

			con.setRequestProperty("accept-language", "en-GB,en;q=0.9,ru-RU;q=0.8,ru;q=0.7,en-US;q=0.6");
			con.setRequestProperty("cache-control", "max-age=0");
			con.setRequestProperty("sec-ch-ua",
					"\"Google Chrome\";v=\"87\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"87\"");
			con.setRequestProperty("sec-ch-ua-mobile", "?0");
			con.setRequestProperty("sec-fetch-dest", "document");
			con.setRequestProperty("sec-fetch-mode", "navigate");
			con.setRequestProperty("sec-fetch-site", "none");
			con.setRequestProperty("sec-fetch-user", "?1");
			con.setRequestProperty("upgrade-insecure-requests", "1");
			con.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");

			log.debug("Connection response code: " + con.getResponseCode());
			log.debug("ContentType: " + con.getContentType());

			
			if (con.getContentType().toLowerCase().contains("charset=utf-8")) {
				charset = "UTF-8";
			} else if(con.getContentType().toLowerCase().contains("application/json")) {
				charset = "UTF-8";
			}else if(con.getContentType().toLowerCase().contains("text/html")) {
				charset = "UTF-8";
			} else {
				throw new FeedAggrException.GetURLContentException(urlText,
						String.format("Received unsupported contentType: %s. ", con.getContentType()));
			}
			String encoding=con.getContentEncoding();
			if (encoding!=null && encoding.equals("gzip")) {
				try (InputStream gzippedResponse = con.getInputStream();
						InputStream ungzippedResponse = new GZIPInputStream(gzippedResponse);
						Reader reader = new InputStreamReader(ungzippedResponse, charset);
						Writer writer = new StringWriter();) {
					char[] buffer = new char[10240];
					for (int length = 0; (length = reader.read(buffer)) > 0;) {
						writer.write(buffer, 0, length);
					}
					body = writer.toString();
					writer.close();
				}

			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String s;
				StringBuilder sb = new StringBuilder();
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				body = sb.toString();
				br.close();
			}
			con.disconnect();

			
		} catch (Exception e) {
			log.error("GetURLContentException: couldn't get a content for the ["+urlText+"] URL", e);
			if(con!=null) {
				con.disconnect();
			}
			InfluxDB.getInstance().send("response_time,method=GetURLContentTask.call", new Date().getTime() - t1);
			throw new FeedAggrException.GetURLContentException(e.getMessage(),urlText);
		}
		InfluxDB.getInstance().send("response_time,method=GetURLContentTask.call", new Date().getTime() - t1);
		return body;
	}

}
