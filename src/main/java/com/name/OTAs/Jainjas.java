package com.name.OTAs;

import com.name.models.Price;
import com.name.models.Room;
import com.name.models.ScrapInfo;
import com.name.util.ApacheHttpClient;
import com.name.util.Crawler;
import com.name.util.DateConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author Akbar
 * @DATE 7/10/2018.
 */
@Component
@Profile({"jainjas"})
@Getter
@Slf4j
public class Jainjas extends BaseOTA {
    private final Crawler crawler;
    @Value("${jainjas.urlPattern}")
    private String jainastUrlFormat;
    @Value("${snapptrip.sleep}")
    private int sleep;
    private String name = "jainjas";

    public Jainjas(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public List<Room> getRoomsData(ScrapInfo scrapInfo, String city) {
        try {
            String html1 = ApacheHttpClient.getHtmlWithoutSSLCertificate("https://jainjas.com/hotel_eram-kish-hotel");
            Document doc1 = Jsoup.parse(html1);
            String[] values = doc1.getElementsByAttributeValue("type", "text/javascript").get(0).data().split(";");
            String value = values[3];
            String modelId = value.split("'")[1];
            String currentShamsidate = DateConverter.getCurrentShamsidate();
            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, 30);
            dt = c.getTime();
            String shamsidateAfter30Days = DateConverter.getShamsidate(dt);
            String params = "placeId=" + modelId + "&from=" + currentShamsidate + "&to=" + shamsidateAfter30Days;
            String jsonResult = ApacheHttpClient.postRequest("https://jainjas.com/Place/Rooms", params);
            JSONObject jsonObject = new JSONObject(jsonResult);

            //rooms
            JSONArray rooms = new JSONArray(jsonObject.get("Rooms").toString());
            List<Room> resultRooms = new ArrayList<>();
            for (Object room : rooms) {
                String roomName = (String) (((JSONObject) room).get("Name"));
                Integer roomType = (Integer) (((JSONObject) room).get("Capacity"));
                JSONArray roomPrices = (JSONArray) ((JSONObject) room).get("RoomPrices");
                Set<Price> prices = new HashSet<>();
                for (Object roomPrice : roomPrices) {
                    String date = (String) (((JSONObject) room).get("Date"));
                    Integer price = (Integer) (((JSONObject) room).get("PassengerPrice"));
                    Integer availableNumber = (Integer) (((JSONObject) room).get("AvailableNumber"));
                    boolean available = false;
                    if (availableNumber > 0)
                        available = true;
                    prices.add(new Price(DateConverter.JalaliToGregorian(date), price, available));
                }
                resultRooms.add(new Room(roomName, roomType, prices));
            }
            return resultRooms;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                crawler.crawl(this);
                Thread.sleep(sleep);
            } catch (Exception e) {
                log.error("error in snapptrip: " + e.getMessage());

            }
        }
    }
}
