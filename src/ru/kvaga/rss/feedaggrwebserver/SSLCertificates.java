package ru.kvaga.rss.feedaggrwebserver;


import java.io.*;
import java.net.URL;

import java.security.*;
import java.security.cert.*;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.AbstractJsseEndpoint;
import javax.net.ssl.*;

import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.AbstractJsseEndpoint;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggrwebserver.ServerUtilsConcurrent;


public class SSLCertificates {
	final static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(SSLCertificates.class);

// unable to find valid certification path to requested target
	public static void main(String[] args) throws Exception {
		String content = ServerUtilsConcurrent.getInstance().getURLContent("https://ya.com/");
		System.out.println(content);
		System.exit(0);
//		String url = "hh.ru";
//		SSLCertificates sslCertificates = new SSLCertificates();
//		String host=url;
//		int port=443;
//		char[] passphrase = System.getProperty("passphrase").toCharArray();
//		String keyStoreFilePath = System.getProperty("keystoreFilePath");
//		sslCertificates.downloadAndApplyCertificates(url, port, keyStoreFilePath, passphrase);
	}
	
	public void downloadAndApplyCertificates(URL url, int port, String keyStoreFilePath,String passphrase) throws Exception {
		downloadAndApplyCertificates(url.toString().replaceAll("https://", "").replaceAll("/.*", ""), port, keyStoreFilePath, passphrase.toCharArray()); 
	}

	/**
	 * Download certificates from host and store them to a truststore
	 * @param host
	 * @param port
	 * @param keyStoreFilePath
	 * @param passphrase
	 * @throws Exception
	 */
	public void downloadAndApplyCertificates(String host, int port, String keyStoreFilePath, char[] passphrase) throws Exception {
		File file = new File(keyStoreFilePath); 
		log.info("Let's download certificate for the host ["+host+"]");
		log.debug("Loading KeyStore " + file + "...");
		InputStream in = new FileInputStream(file);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, passphrase);
		in.close();

		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = context.getSocketFactory();

		log.debug("Opening connection to " + host + ":" + port + "...");
		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		socket.setSoTimeout(10000);
		try {
			log.debug("Starting SSL handshake...");
			socket.startHandshake();
			socket.close();
			log.debug("No errors, certificate is already trusted");
		} catch (SSLException e) {
			log.debug("There is no corresponding certificate in a keystore. Let's add this certificate", e);
		}

		X509Certificate[] chain = tm.chain;
		if (chain == null) {
			log.debug("Could not obtain server certificate chain");
			return;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		log.debug("Server sent " + chain.length + " certificate(s):");
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			log.debug(" " + (i + 1) + " Subject " + cert.getSubjectDN());
			log.debug("   Issuer  " + cert.getIssuerDN());
			sha1.update(cert.getEncoded());
			log.debug("   sha1    " + toHexString(sha1.digest()));
			md5.update(cert.getEncoded());
			log.debug("   md5     " + toHexString(md5.digest()));
			String alias = cert.getSubjectDN().getName().replaceAll(" ", "_").replaceAll("=", "_").replaceAll(",", "_").replaceAll("\\*", "_");
					//host + "-" + (i);
			ks.setCertificateEntry(alias, cert);

			OutputStream out = new FileOutputStream(file);
			ks.store(out, passphrase);
			out.close();
			log.debug(cert);
			log.info("Added certificate to keystore '"+file+"' using alias '" + alias + "'");
		}
		// Fore Tomcat to reread truststore
		ReloadProtocol rp = new ReloadProtocol();
	}
	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

	private static class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			throw new UnsupportedOperationException();
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}
	}

	// Force Tomcat to reread truststore
	private static class ReloadProtocol extends Http11NioProtocol {

        public ReloadProtocol() {
            super();
            RefreshSslConfigThread refresher = new 
                  RefreshSslConfigThread(this.getEndpoint(), this);
            refresher.run();
        }

        @Override
        public void setKeystorePass(String s) {
            super.setKeystorePass(s);
        }

        @Override
        public void setKeyPass(String s) {
            super.setKeyPass(s);
        }

        @Override
        public void setTruststorePass(String p) {
            super.setTruststorePass(p);
        }

        private static class RefreshSslConfigThread extends Thread {

            AbstractJsseEndpoint abstractJsseEndpoint = null;
            Http11NioProtocol protocol = null;

            public RefreshSslConfigThread(AbstractJsseEndpoint abstractJsseEndpoint, Http11NioProtocol protocol) {
                this.abstractJsseEndpoint = abstractJsseEndpoint;
                this.protocol = protocol;
            }

            public void run() {
                int timeBetweenRefreshesInt = 1000000; // time in milli-seconds
//                while (true) {
                    try {
                            abstractJsseEndpoint.reloadSslHostConfigs();
                            log.debug("Config Updated");
                    } catch (Exception e) {
                        log.error("Problem while reloading.", e);
                    }
//                    try {
//                        Thread.sleep(timeBetweenRefreshesInt);
//                    } catch (InterruptedException e) {
//                        System.out.println("Error while sleeping");
//                    }
//                }
            }
       }
}
}
