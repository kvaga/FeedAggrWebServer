user_file=/u02/tomcat/feedaggrwebserver-data/users/user-file.xml
user_dir=$(dirname $user_file)
data_dir=$(dirname $user_dir)
feed_dir="$data_dir/feeds"
echo "user_dir: $user_dir"
echo "data_dir: $data_dir"
echo "feed_dir: $feed_dir"
mkdir -p $feed_dir 2>/dev/null

function create_init_feed_empty_files(){
cat $user_file | grep "<\/id>" | sed 's/\s*<id>//g' | sed 's/<\/id>//g' | while read f; do
file="$feed_dir/$f.xml";
touch $file
done
}

function get_url_from_main_file_by_id(){
cat /u02/tomcat/feedaggrwebserver-data/users/kvaga.xml | grep "$1</id>" -A 7 | while read a; do echo "$a"; done | grep "</userFeedUrl>" | sed "s/.*<userFeedUrl>//g" | sed 's/<\/userFeedUrl>//g'
}
function fullfill_url_in_feed_empty_files(){
count=$(ls $feed_dir | wc -l)
counter=0
ls $feed_dir | while read f; do
id=$(basename $f .xml)
url=$(get_url_from_main_file_by_id $id)
init_content=$(cat <<EOF
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<rss version="2.0">
<channel>
        <description>Restored from init, id: $id</description>
                <generator>Feed Aggr Web Server Generator</generator>
                        <lastBuildDate>2022-08-01T23:09:56.753+03:00</lastBuildDate>
                                <link>$url</link>
                                        <title>Restored from init, id: $id</title>
                                                <ttl>365</ttl>
                                                </channel>
                                                </rss>
EOF
)
echo "$init_content" > $feed_dir/$f && counter=$(($counter +1))
echo -ne "Process: $(($counter * 100 / $count))%\r"
done
echo -ne "\n"
}

fullfill_url_in_feed_empty_files
