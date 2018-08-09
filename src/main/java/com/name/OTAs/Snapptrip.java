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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author Akbar
 * @DATE 5/19/2018.
 */
@Component
@Profile({"snapptrip"})
@Getter
@Slf4j
public class Snapptrip extends BaseOTA {
    private final Crawler crawler;
    @Value("${snapptrip.webservice}")
    private String webservice;
    @Value("${snapptrip.urlPattern}")
    private String urlPattern;
    @Value("${snapptrip.roomDiv}")
    private String roomDiv;
    @Value("${snapptrip.roomNameSelector}")
    private String roomNameSelector;
    @Value("${snapptrip.roomPriceSelector}")
    private String roomPriceSelector;
    @Value("${snapptrip.sleep}")
    private int sleep;
    private String name = "snapptrip";

    public Snapptrip(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public List<Room> getRoomsData(ScrapInfo scrapInfo, String city, Proxy proxy) throws Exception {
        List<Room> rooms = new ArrayList<>();
        try {
            Elements roomElements = getRoomElements(getHtmlDocument(createURL(scrapInfo.getHotelName(), city), proxy), getRoomDiv());
            Map<String, Integer> roomTypes = getRoomTypes(scrapInfo.getRoomTypes());
            for (Element roomElement : roomElements) {
                Room room = new Room();
                String roomID = roomElement.attr("id").replace("room_", "");
                int roomType = Integer.parseInt(roomElement.getElementsByClass("bed").text().replace("نفر", "").trim());
                String html = ApacheHttpClient.getHtmlUsingProxy(String.format(getWebservice(), roomID), proxy);
                JSONObject jsonObject = (JSONObject) new JSONObject(html).get("data");
                JSONArray prices = jsonObject.optJSONArray("room_availablity");
                Set<Price> priceList = new HashSet<>();
                String roomName = jsonObject.getString("room_title");
                for (int i = 0; i < prices.length(); i++) {
                    Price price = new Price();
                    JSONObject obj = prices.getJSONObject(i);
                    price.setDate(convertStringToDate((String) obj.get("date")));
                    price.setValue(Integer.parseInt(obj.get("price_off").toString()));
                    price.setAvailable((int) obj.get("available_quantity") > 0 ? true : false);
                    priceList.add(price);
                }
                room.setPrices(priceList);
                room.setRoomName(roomName);
                if (roomTypes.get(roomName) != null) {
                    room.setRoomType(roomTypes.get(roomName));
                } else {
                    room.setRoomType(roomType);
                }
                rooms.add(room);
            }
            return rooms;
        } catch (Exception e) {
            throw e;
        }
    }

    private Date convertStringToDate(String dateString) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = df.parse(dateString);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex.getCause());
        }
        return date;
    }

    protected String createURL(String calledName, String city) {
        calledName = calledName.replace(city, "").trim();
        calledName = calledName.replace(" ", "-").trim();
        try {
            calledName = URLEncoder.encode(calledName, "UTF-8");
            city = URLEncoder.encode(city, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e.getCause());
        }
        String str = getUrlPattern().replace("city", city);
        String modified = str.replace("hotelName", calledName);
        setUrlToCrawl(modified);
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
