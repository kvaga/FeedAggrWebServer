
# Installation
* Authentication - this application uses tomcat's authentication process
	* In the tomcat-users.xml file set role and username/password
```
		  <role rolename="feedaggrwebserver"/>
		  <user username="your-user" password="your-user-password" roles="feedaggrwebserver"/>
```
	* In the web.xml file set constructions
```
	<security-constraint>
	    <!-- OMIT auth-constraint for showFeed-->
	    <web-resource-collection>
	        <web-resource-name>showFeed</web-resource-name>
	        <url-pattern>/showFeed</url-pattern>
	    </web-resource-collection>
	</security-constraint>
	<security-constraint>
		<web-resource-collection>
			<web-resource-name>Wildcard means whole app requires authentication</web-resource-name>
			<url-pattern>/*</url-pattern>
			<http-method>GET</http-method>
			<http-method>POST</http-method>
		</web-resource-collection>
		<auth-constraint>
			<role-name>feedaggrwebserver</role-name>
		</auth-constraint>
```
* Logging
Add this row to the bin/setenv.sh
```bash
...
export CATALINA_OPTS="$CATALINA_OPTS -Dlog4j.configurationFile=conf/log4j.xml"
...
```
# Troubleshooting
* If you encounter an error like 
```bash
...
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
...
```
for specific URLs then you have to create a trust store and add there all certificates (server's, CA's, ROOT's) for the specific URL. This script can help you for adding

```bash
** Create trustStore
$JAVA_HOME/bin/keytool -genkey -alias someAlias -keyalg RSA -keystore conf/keystore.jks -validity 3650

** Import specific certificate
CERT=DigiCert.cer \
&& $JAVA_HOME/bin/keytool -import -alias $CERT -keystore conf/keystore.jks -file /tmp/certs/$CERT && ll /tmp/certs/
```
Please pay attention that you have to specify the location of the trustStore. If you use tomcat then create a the file $CATALINA_HOME/bin/setenv.sh
```bash
export CATALINA_OPTS="$CATALINA_OPTS -Djavax.net.ssl.trustStore=conf/keystore.jks"
export CATALINA_OPTS="$CATALINA_OPTS -Djavax.net.ssl.trustStorePassword=******"
```
* Test mode: you cat set java parameter -DTEST_MODE for the test mode