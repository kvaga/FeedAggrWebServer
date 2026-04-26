#!/bin/bash
PORT=443
KEYSTORE="conf/keystore.jks"
FEEDAGGWEBSERVER_CONFIG_PATH="conf/feedaggrwebserver.conf"
function import_certificate_from_site_into_jks(){
        if [ -z $1 ]; then
                echo "Usage: import_certificate_from_site_into_jks <url>"
                return 1;
        fi
        alias=$(echo $1 | sed 's/https:\/\///g' | sed 's/\/.*//g')
        echo $alias
        # Configuration
        url="$1"
        alias="$alias"
        if [ ! -f $KEYSTORE ]; then
                echo "[ERROR] Keystore [$KEYSTORE] was not found"
                return 1
        fi
        if [ ! -f $FEEDAGGWEBSERVER_CONFIG_PATH ]; then
                echo "[ERROR] FeedAggrWebServer's config [$FEEDAGGWEBSERVER_CONFIG_PATH] file was not found"
                return 1
        fi
        storepass=$(cat $FEEDAGGWEBSERVER_CONFIG_PATH | grep ssl.trustStorePassword | sed 's/.*=//g')
        if [ "storepass" == "" ]; then
                echo "[ERROR] Couldn't find a keystore password"
                return 1
        fi
        # 1. Get the certificate from the URL and save to a temporary file
        echo "Fetching certificate from $url..."
        openssl s_client -connect ${alias}:${PORT} -servername ${alias} </dev/null | \
        openssl x509 -outform PEM > temp_cert.pem

        # 2. Import the certificate into the JKS keystore
        echo "Importing certificate into $KEYSTORE..."
        keytool -import -alias "$alias" -file temp_cert.pem -keystore "$KEYSTORE" \
        -storepass "$storepass" -noprompt

        # 3. Clean up
        rm temp_cert.pem
        echo "Done."
}
import_certificate_from_site_into_jks $@
