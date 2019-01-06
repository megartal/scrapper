package com.name.OTAs;

import com.name.documents.Proxy;
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

import java.math.BigDecimal;
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
    @Value("${jainjas.sleep}")
    private int sleep;
    private String name = "jainjas";

    public Jainjas(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public List<Room> getRoomsData(ScrapInfo scrapInfo, String city, Proxy proxy) throws Exception {
        randomWait(sleep, 20000);
        try {
            randomWait(2000, 3000);
            String html1 = ApacheHttpClient.getHtmlWithoutSSLCertificate(createURL(scrapInfo.getHotelName()));
            Document doc1 = Jsoup.parse(html1);
            String[] values = doc1.getElementsByAttributeValue("type", "text/javascript").get(0).data().split(";");
            String value = values[3];
            String modelId = value.split("'")[1];
            String currentShamsidate = DateConverter.getCurrentShamsiDate();
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
                    Integer price;
                    String date = (String) (((JSONObject) roomPrice).get("Date"));
                    if ((((JSONObject) roomPrice).get("PassengerPrice")) instanceof Double) {
                        price = new BigDecimal((((JSONObject) roomPrice).get("PassengerPrice")).toString()).intValue();
                    } else {
                        price = (Integer) (((JSONObject) roomPrice).get("PassengerPrice"));
                    }
                    Integer availableNumber = (Integer) (((JSONObject) roomPrice).get("AvailableNumber"));
                    boolean available = false;
                    if (availableNumber > 0)
                        available = true;
                    prices.add(new Price(DateConverter.JalaliToGregorian(date), price, available));
                }
                resultRooms.add(new Room(roomName, roomType, prices));
            }
            return resultRooms;
        } catch (Exception e) {
            throw e;
        }
    }

    private String createURL(String hotelName) {
        setUrlToCrawl(String.format(jainastUrlFormat, hotelName));
        return getUrlToCrawl();
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
