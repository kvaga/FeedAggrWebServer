#/bin/bash
TOMCAT_DIR_LOGS=/u02/tomcat/apache-tomcat-<version>/logs
CATALINA_LOG_FILE=$TOMCAT_DIR_LOGS/catalina.out
function log_error(){
    echo "[$(date +%Y-%m-%d_%H-%M-%S)][ERROR] $1";
}
function log(){
    echo "[$(date +%Y-%m-%d_%H-%M-%S)][INFO ] $1";
}
function catalina_log_file_backup(){
CATALINA_LOG_BACKUP="$CATALINA_LOG_FILE-$(date +%Y-%m-%d_%H-%M-%S).tar.gz"
tar cvfz $CATALINA_LOG_BACKUP $CATALINA_LOG_FILE || { log_error "Couldn't create [$CATALINA_LOG_BACKUP] backup of the [$CATALINA_LOG_FILE] file"; return 1;}
echo "" > $CATALINA_LOG_FILE || { log_error "Couldn't clear the [$CATALINA_LOG_FILE] file"; return 1;}
}

function delete_log_files_older_than(){
    than_days=5
    cc=0
    for file in $(find $TOMCAT_DIR_LOGS -type f -mtime +$than_days); do
        rm $file && cc=$(($cc+1))
    done
    log "Deleted [$cc] files from the [$TOMCAT_DIR_LOGS] directory"
}
delete_log_files_older_than
