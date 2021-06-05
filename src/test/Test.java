package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.kvaga.monitoring.influxdb.InfluxDB;
import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.FeedAggrException.GetURLContentException;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;

public class Test {

	public static void main(String[] args) throws Exception {

//		InfluxDB influxDB = InfluxDB.getInstance("", 8086, "system_monitoring");
//		InfluxDB influxDB = InfluxDB.getInstance("", 8086, "feedaggrwebserver");

//		influxDB.createDatabase();
//		influxDB.deleteDatabase();

//		influxDB.send("cpu,host=localhost", "100");
		String urls[] = { 
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC0lT9K8Wfuc1KPqm6YjRf1A",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCL1C1f9HWf3Hyct4aqBJi1A",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCEVNTzTFSGkZGTjVE9ipXpg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC7obLw7tkuXm7uIiy8lnW9g",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC_6wQppehr3VtoFmF5vM_cw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCO2w0cpl1wxygHjQH6eEfEg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCQRLRt9lof67ZWts843EOJw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCy0cIs_W_sPbSQMstSv-eUQ",
//				"https://www.youtube.com/c/BadooTech/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC12hmaQ0bw3H8PnrR8GBk_w",
//				"https://www.youtube.com/c/BeGeek101/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UClPSRMRfDffc9vWpQOHYxsw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC5iYCbrAH2x3BF0yWJeH-Hg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCD_cnSHD16eXMsd6MQiEulQ",
				"https://www.youtube.com/channel/UC1Doaazg0eJkPZooD90huPA/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCwvYtR34JMCTDIOqijFDbPQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCu5JLVZtQbpCtFYNnl-QzWA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCMlacPYRR9sILOkkunH6Oig",
//				"https://www.youtube.com/channel/UCjnQreLMbpKsfJwDnHGZLxg/videos",
//				"https://www.youtube.com/channel/UC4Xs0UbAdDaMRmStzhSsSag/videos",
//				"https://www.youtube.com/channel/UC4Xs0UbAdDaMRmStzhSsSag/videos",
//				"https://www.youtube.com/c/DevSecOpsLondonGathering/videos",
//				"https://www.youtube.com/channel/UCFTwx81iyTMps1X6Fx12OiA/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCUYqSjRbCrCHqWUNvOHJwRA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCWnNKC1wrH_NXAXc5bhbFnA",
//				"https://www.youtube.com/c/dynatrace/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCPodzGcTvM7X9vQbpmc7oIQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC0Cae4wgOCEG4XRP_ihez8Q",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCPDis9pjXuqyI7RYLJ-TTSA",
//				"https://www.youtube.com/channel/UC2i1JiX5aNHk6_9wgy_lr2g/videos",
//				"https://www.youtube.com/channel/UCjikgYGfeqU4ZPKQNm1V5tg/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCpNzWUlO6PVb_v7chefBnig",
//				"https://www.youtube.com/c/Grafana/videos", "https://www.youtube.com/c/GreenLearner/videos",
//				"https://www.youtube.com/c/Gremlin/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCEtJi-euMY2-jUZpuiU1POg",
//				"https://www.youtube.com/feeds/videos.xml?playlist_id=PLeBmVLAI4rz1wE9d78WEvnZmgQflV3XEv",
//				"https://www.youtube.com/c/Heisenbugconf/videos", "https://www.youtube.com/user/profyclub/videos",
//				"https://www.youtube.com/channel/UCiw1Vz18EiGOfdlAv6-vWgA/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCRyd7dU2S-qAbAqRnZhyKIg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCZRy6D8kFG7HI9kNha7g7sQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC-WK8QlQJpAROCrO7dRvqcw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCISe6e_phEMAvQMhjd7TJ8Q",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCYrGYT7BswsJGkmG7-IAF8g",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCTk5cqCKvU1B6qYU7X-0Qjg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCYt1sfh5464XaDBH0oH_o7Q",
//				"https://www.youtube.com/c/%D0%93%D1%80%D0%B8%D0%B3%D0%BE%D1%80%D0%B8%D0%B9%D0%9A%D0%B8%D1%81%D0%BB%D0%B8%D0%BD/videos",
//				"https://www.youtube.com/c/JavaGuides/videos", "https://www.youtube.com/c/JavaGuides/videos",
//				"https://www.youtube.com/c/JavaTechie/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCGp4UBwpTNegd_4nCpuBcow",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCC39uo6g4_hfkVruZvmlCoQ",
//				"https://www.youtube.com/channel/UCpWqpFRMq9GYTvQjwUvgZ4Q/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCpWqpFRMq9GYTvQjwUvgZ4Q",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCK9lZ2lHRBgx2LOcqPifukA",
//				"https://www.youtube.com/c/KiaMoscow/videos",
//				"https://www.youtube.com/channel/UC573cTRlneUcTmiVd4bf5Fw/videos",
//				"https://www.youtube.com/channel/UCxMaq7APN-T7tBiJMT0dKWQ/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCtpF_J0JgPtU7f8iw8EhBww",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCs7Sp_-bnuFcFu3D-z16acw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCnM4hUnXcaLamjf90veeaPg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCnBrxKSJgXbxauhgbyrejmw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCVBErcpqaokOf4fI5j73K_w",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCVHjF4F2gI1LsqDYqPajjRA",
//				"https://www.youtube.com/channel/UCIYeN7a15vvr5O2CZuqaBKA/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCyh6-f8xbkxbu9U9kD-K6DA",
//				"https://www.youtube.com/channel/UCvIFtzCy8HIATOioe4-l93Q/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCCLC8dta4gbG9aCM5ynRIrw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCIRR_Z_Ma9CpbgTloKA1t2g",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC4gx8i0BCVKchqndUCazIoA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCGWHd40x0A7wjk8qskyHQcQ",
//				"https://www.youtube.com/channel/UCcFM3JwN_ae72n2TU9JkcMQ/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCZAx62e1yv8JE5gdXCZntaA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC158m8lo-waFW72lXkNGSGg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC6aBcPLwQA6rm_jZ4oMkd6Q",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCvoUJtrJi-M4yRlxjNMJLLg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCNqaLbSH0sNUr-yzXNn3xxA",
//				"https://www.youtube.com/channel/UCJSiGCx1GFwYbLi0godUx8g/videos",
//				"https://www.youtube.com/channel/UCJjGPIDad-scV52otgqe7AA/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC0BqXPUXHD-ih_0wXgkD4Uw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCFEujg9GwhJkGUW-8g_VsEw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCIlr4c5z_IAgE8R2Htw4jKw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UChjC1q6Ami7W0E71TzPZELA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC_bzikURwRp3Vdbl3VL959Q",
//				"https://www.youtube.com/channel/UCAyU2MJN33qz3ak6EuXCcNg/videos",
//				"https://www.youtube.com/c/PerfMatrix/videos",
//				"https://www.youtube.com/channel/UCKotrv7lPVYMJx7mTuY0GDg/videos",
//				"https://www.youtube.com/channel/UCxUk2e3VhNKsuDw6ww-dv1Q/videos",
//				"https://www.youtube.com/channel/UCsy8KV1zZzNOMSu0NDa4FYg/videos",
//				"https://www.youtube.com/channel/UCwzPUk-8ESHdHARdGme6F1g/videos",
//				"https://www.youtube.com/channel/UCrRFCG5YAnb9zDjMqIl6CfQ/videos",
//				"https://www.youtube.com/channel/UCk4WXvEVX43JaDW1oi19Ulg/videos",
//				"https://www.youtube.com/channel/UCHyxplhPAmG0yPFOX-yVctg/videos",
//				"https://www.youtube.com/channel/UCwYhkGPeJRF7WVUx_NJu2cg/videos",
//				"https://www.youtube.com/c/PerformanceTestingbasicsandadvanced/videos",
//				"https://www.youtube.com/channel/UCcAkhdk14QKwJJM-GszkTlA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC4pLFely0-Odea4B2NL1nWA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC-Rwh93V2Hgup-FEZdbiF2Q",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCuE_yaa7RGi_g-SHpuhUQQw",
//				"https://www.youtube.com/user/qdtsru/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC7hjrpB81FSipI1oqtOaZqQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCKDxs_s5a-IeGgnvcbCvGVg",
//				"https://www.youtube.com/channel/UCJDMKEaXJruLBm5WbhmEXQw/videos",
//				"https://www.youtube.com/c/RomanianCoder/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCFrgSsubahnhUzBwnhMrYTw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCuZOBivel1HWBD6etBVULcQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCrbqXPPuy5zV806JvBPnPqA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCFNIJjUlQmC5s1d-QuNS3GA",
//				"https://www.youtube.com/channel/UCsrE2scaIKe8QeezKI0r6JQ/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCglBwn1CkjvSjAJ9EwbYTaw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCTEEi_FsPseE2-r8iqONfnw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCFSQ6b65-OQeaq37hDGDkZw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCyFIhiuxO_YGBblieHpe90Q",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCOkC7aiXyL68dHQmwWV_O0w",
//				"https://www.youtube.com/channel/UC9Uai4lTIOOETjTT3RaKRKQ/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCI2Tj1A3E2litfptZZ9WfJw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCcBbiCpR-eBwL5l6H63lgfg",
//				"https://www.youtube.com/c/SmartmeterIo/videos", "https://www.youtube.com/c/SmartmeterIo/videos",
//				"https://www.youtube.com/channel/UC0h0gNBCFo3RvrTvX7xYJdA/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCu1c-MXuVw8phsH__pHlQLQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC2YsPwsbvYkKyl7rpro6CBw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCxJGMJbjokfnr2-s4_RXPxQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCG5B6oQWWsIt-yO826IwG2Q",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCjwOFZzLPnji1EstaVyyvAw",
//				"https://www.youtube.com/c/SpringBootLearning/videos",
//				"https://www.youtube.com/c/SpringIOConference/videos",
//				"https://www.youtube.com/user/SpringSourceDev/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCZ2h0d4tP0R8klduwj-_bDQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCGWZY-0pONnKmF98dhZy9CQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCj6VfZEQTxX8Tp2My5L9Ykw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC0Prg22AMuZtNoWrI3Rzrww",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCL6n8CTa-HHIHkrPkZmJ00A",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCVIWn7o3162j_lJFJfs7mDA",
//				"https://www.youtube.com/channel/UCacHAxEx5RzwKu_WFjxKPIA/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCftMO-yR5xfDLeWqZzsiSzQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCqVOSiiTgDlAAq_y5AGO9XA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC-g0gSStENkYPXFRsKrlvyA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCAgK0Zbv9NHPQ7u_kQ7Su4A",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCrHxzDkTJAmYiO5n0Kb2UTA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCjAmQ-4NL3UZX0W_nmjn4_w",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCsEELOH4lWfPS8mGbfRQnTw",
//				"https://www.youtube.com/channel/UCZCIt79BUieVRxlaJ5mGS3g/videos",
//				"https://www.youtube.com/c/UnixHost/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCeB2o9T5qb5cG0O70AicDeQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCZbP6ZVs1l6ml877jqpwMFQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCo-LBfjKOUAEUytVpbSLqWQ",
//				"https://www.youtube.com/channel/UCnFPQd-4Ms2kH25Zs5i4Cmg/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC93GxHXgjjvVd6Z-nULOdXQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UClFH_-KB4J5RvB1XQWmCfjQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC5M8zH6ZfS5Znd3tJTjn_DA",
//				"https://www.youtube.com/c/alishevN/videos",
//				"https://www.youtube.com/channel/UCaGHGAZiSwb8if6yfBF4NLA/videos",
//				"https://www.youtube.com/c/jusaf/videos", "https://www.youtube.com/c/k6test/videos",
//				"https://www.youtube.com/channel/UCl6pLifZFu3xl_S2Hoo5smg/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCrRiVfHqBIIvSgKmgnSY66g",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCZ3nkUMvkKBBb1Nj1nO2TWQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC83S32U68Qs5MEgLW_yX9Og",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCKwjp5A6RMV3vdgJutYJTNA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCTUyoZMfksbNIHfWJjwr5aQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCMggmWJO6gGzTAOtC41sJaw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCkWn1ggyW66xSbYnC0GDCBQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC-1kkIbImFC5I6xoWcZZtpA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCeNG2KOlbo1_9hIdwSpo51Q",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCjAZw68tdv7PctCALk1-bZw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCOv-JGdlLuP_JDse2KmS6dA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCb9cuIZc7Vy3vW6Rvr3QDmg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCsKiNBoIWLpIxU6vsAv3v3w",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCr2EXJtFMunvG0CmYE3Tj8A",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCEp6lUkoEsBn6BEPZaPbTHw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCdUwDSicdhcU9N8iap4c4Ow",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCyYdliihJFWMXHikPK3NCQA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC_ErLqdnmXGMH-pccUQjwyA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCVOWQBowdCy_ILKjW58zemQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCVOWQBowdCy_ILKjW58zemQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCyv738YyECeiRLb3uOEJfCw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCQeaXcwLUDeRoNVThZXLkmw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCShIoch7zb0vGqOglEsQ2jg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UComp6sCSIYHbL4FEkNF0EnA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCzlzGhKI5Y1LIeDJI53cWjQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCSmoZUlKH4ZfhQ0pNg4tdbA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCbhXz_OPX3B0eTimt24PGVQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCl59-tR-dJmF-B6iDv1PklA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCo1RPtO57Izwy2wjBmPMjCw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCw4AYruRnCEN1lXGvgNTvdQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC0koLVf8cp_GBpQzzy0hV_w",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCfXZx5TvCCA8vkmX8fRGeQw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCI1smBJHIjec3HFkveM8CnA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCcpxs6keAlNcxXPDI3hX19g",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC63WqTQGkX4j1tj60WDhxww",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCrLt6RxIU_M7Gw28Ui3yBaw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCFxlG9YgE_RGo6BSUpPB5gA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC4axiS76D784-ofoTdo5zOA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCG4yz4wtp2E5S62L06yqC9w",
//				"https://www.youtube.com/channel/UCs-PiE7Ca1EU3GMQrmTZESg/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCiUYF0R_ttUKe3gKN6aWy5A",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC7iIEa8VyKW2qTNTpH89YPA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCF2cYykA5jFZTF6Y5j1zy7A",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCLlSts9lJLf90vFFNx7le4w",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCMu3kOKnECOiWzd80QZfH9A",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCwK4e2gGMFi8slBSnELJBRg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCU99hUbZx58r8Le-Nt3cuGw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCIi2Tk2POJkRgWHD7HGBa7Q",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCH4KR4_UxYIfQDTHaPeMWtg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCK33i1zH7M303oz9fshGSkw",
//				"https://www.youtube.com/user/kvn/videos",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCUqZI2c7HFkWMkleg7gRgig",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCnbSNjQux2JBTxI-Qzx8ZaA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCeuEugTdgT6ZD4RMkf3yIxw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCWXN0I4TuQrPXPtEoye4VZA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCNuItlOR3qXZBtMRwb4GoBg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCrxr3hyCiXCrJrddShmKL-g",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCNArqhG19LWQyhM0DcGgjAQ",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCBzN3JKOWOPo6ic0_UtQXhA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCXmX9R8f8UQJdaLWu80BcOg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCyxifPm6ErHW08oXMpzqATw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCYaAD4VM1-BtJYTezH2GB5g",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCIRBmeJKx5gaPA4-6AOqKMA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UC1c3-bhBuf9brQW-XMUxjnw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCQmAuu6V3kSzdIfrszr5iKg",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCNOjndUve_-AzsvmbPhPbpA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCixlrqz8w-oa4UzdKyHLMaA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCpTxXS8OYBk3y0qXHPZ2Uvw",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCr3T78FIYv4io-d2zCUcZ7A",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCMCgOm8GZkHp8zJ6l7_hIuA",
//				"https://www.youtube.com/feeds/videos.xml?channel_id=UCmyE72X4HsC7XD-z6QIQpzw", 
				};
		String channelId=null;
		String finalURLTemplate="https://www.youtube.com/channel/%s/videos";
		String finalURL=null;
		InfluxDB.getInstance("130.61.122.117", 8086, "feedaggrwebserver", 10);
		
		for (String url : urls) {
			if(url.contains("/feeds/videos.xml?channel")) {
				channelId=Exec.getChannelIdFromXMLURL(url);
			}else if(url.contains("playlist_id")){
				continue;
			}else {
				channelId = Exec.getYoutubeChannelId(url);
			}
			System.err.println("ChannelID: " + channelId);
			finalURL=String.format(finalURLTemplate, channelId);
			System.out.println(finalURL);
		}

	}

	
	
	private static String readString(File file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String s;
		StringBuilder sb = new StringBuilder();
		while ((s = br.readLine()) != null) {
			sb.append(s);
		}
		return sb.toString();
	}

}
