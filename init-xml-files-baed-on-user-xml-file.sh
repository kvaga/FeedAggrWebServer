user_file=/u01/tomcat/feedaggrwebserver-data/users/user-file.xml
user_dir=$(dirname $user_file)
data_dir=$(dirname $data_dir)
feed_dir="$data_dir/feeds"
echo "user_dir: $user_dir"
echo "data_dir: $data_dir"
echo "feed_dir: $feed_dir"
mkdir -p $feed_dir 2>/dev/null
cat $user_file | grep "<\/id>" | sed 's/\s*<id>//g' | sed 's/<\/id>//g' | while read f; do
file="$feed_dir/$f.xml";
init_content=$(cat <<EOF
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<rss version="2.0">
<channel>
        <description>Restored from init, id: $f</description>
        <generator>Feed Aggr Web Server Generator</generator>
        <lastBuildDate>2022-08-01T23:09:56.753+03:00</lastBuildDate>
        <link>http://$f</link>
        <title>Restored from init, id: $f</title>
        <ttl>365</ttl>
</channel>
</rss>
EOF
)
echo "$init_content" > $file
done
