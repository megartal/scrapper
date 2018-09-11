package com.name.OTAs;

import com.name.documents.Proxy;
import com.name.models.Price;
import com.name.models.Room;
import com.name.models.ScrapInfo;
import com.name.util.ApacheHttpClient;
import com.name.util.Crawler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author Akbar
 * @DATE 9/9/2018.
 */
@Service
@Profile({"safarme"})
@Getter
@Slf4j
public class Safarme extends BaseOTA {
    private final Crawler crawler;
    @Value("${safarme.urlPattern}")
    private String urlPattern;
    @Value("${safarme.sleep}")
    private int sleep;
    @Value("${safarme.webservice}")
    private String webservice;
    private String name = "safarme";

    public Safarme(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public List<Room> getRoomsData(ScrapInfo scrapInfo, String city, Proxy proxies) throws Exception {
        setUrlToCrawl(urlPattern + scrapInfo.getHotelName());
        List<Room> roomsResult = new ArrayList<>();
        String to;
        String from;
        String params = "{\"ApiSiteID\":\"8078fedf-7cbb-4809-b625-cff5ae5cc4cc\",\"type\":\"2\",\"SuggestID\":\"%s\",\"FromDate\":\"%s\",\n" +
                "\"ToDate\":\"%s\",\"RoomCount\":\"1\",\"PersonCount\":\"1\",\" ExtraBedCount\":\"0\",\"ChildCount\":\"0\",\"InfantCount\":\"0\"} ";
        Map<String, Room> roomMap = new HashMap<>();
        Map<String, Set<Price>> priceListMap = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            Date dt = new Date();
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(dt);
            c2.setTime(dt);
            c1.add(Calendar.DATE, i);
            c2.add(Calendar.DATE, (i + 1));
            from = getDateString(c1.getTime());
            to = getDateString(c2.getTime());
            params = String.format(params, scrapInfo.getHotelId(), from, to);
//            params = String.format(params, "c206d4f9-8342-4213-b247-648a0d283137", from, to);
            String jsonResult;
            JSONObject jsonObject;
            int count = 1;
            try {
                while (true) {
                    jsonResult = ApacheHttpClient.httpsPostRequest(webservice, params);
                    jsonObject = new JSONObject(jsonResult);
                    if ((boolean) jsonObject.get("IsSuccess") == false) {
                        Thread.sleep(8000);
                        count--;
                        if (count < 0) {
                            throw new Exception("there is no response from safarme: safe proxy!");
                        }
                    } else {
                        break;
                    }
                }

                JSONArray rooms = (JSONArray) ((JSONObject) ((JSONObject) jsonObject.get("data")).get("InformationHotel")).get("HotelRooms");
                if (i == 0) {
                    for (Object room : rooms) {
                        JSONObject roomData = (JSONObject) room;
                        roomMap.put((String) roomData.get("RoomID"), new Room((String) roomData.get("RoomTitle"), (Integer) roomData.get("BedCount")));
                        priceListMap.put((String) roomData.get("RoomID"), new HashSet<>(Arrays.asList(new Price(c1.getTime(), ((Integer) roomData.get("Price")) / 10, true))));
                    }
                } else {
                    for (Object room : rooms) {
                        JSONObject roomData = (JSONObject) room;
                        priceListMap.get(roomData.get("RoomID")).add(new Price(c1.getTime(), ((Integer) roomData.get("Price")) / 10, true));
                    }
                }
                Thread.sleep(sleep);
            } catch (Exception e) {
                throw new Exception(e.getMessage() + " safe proxy");
            }

        }
        for (Map.Entry<String, Room> roomEntry : roomMap.entrySet()) {
            roomEntry.getValue().setPrices(priceListMap.get(roomEntry.getKey()));
        }
        roomsResult.addAll(roomMap.values());
        return roomsResult;
    }

    private String getDateString(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR);
    }

    @Override
    public void run() {
        while (true) {
            try {
                crawler.crawl(this);
                Thread.sleep(3000);
            } catch (Exception e) {
                log.error("exception in run method safarme: " + e.getMessage());
            }
        }
    }
}
