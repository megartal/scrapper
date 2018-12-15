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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author Akbar
 * @DATE 5/19/2018.
 */
@Service
@Profile({"jabama"})
@Getter
@Slf4j
public class Jabama extends BaseOTA {
    private final Crawler crawler;
    @Value("${jabama.urlPattern}")
    private String urlPattern;
    @Value("${jabama.userRedirect}")
    private String urlUserRedirect;
    @Value("${jabama.sleep}")
    private int sleep;
    private String name = "jabama";

    public Jabama(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public List<Room> getRoomsData(ScrapInfo scrapInfo, String city, Proxy proxy) throws Exception {
        randomWait();
        List<Room> rooms = new ArrayList<>();
        try {
            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, 32);
            String startDate = DateConverter.getShamsidate(dt).replace("/", "");
            String endDate = DateConverter.getShamsidate(c.getTime()).replace("/", "");
            Map<String, Integer> roomTypes = null;
            if (scrapInfo.getRoomTypes() != null && !scrapInfo.getRoomTypes().isEmpty())
                roomTypes = getRoomTypes(scrapInfo.getRoomTypes());
            Document htmlDocument = getHtmlDocument(createURL(scrapInfo.getHotelName(), startDate, endDate), proxy);
            Elements scripts = htmlDocument.getElementsByTag("script");
            for (Element script : scripts) {
                if (script.data().contains("hotelDetailResult")) {
                    JSONArray roomServices = (JSONArray) new JSONObject(script.data().replace("var hotelDetailResult =", "")).get("RoomServices");
                    for (Object roomService : roomServices) {
                        int roomType = (Integer) ((JSONObject) roomService).get("Capacity");
                        Room room = new Room();
                        JSONObject roomInfo = (JSONObject) roomService;
                        String roomName = (String) roomInfo.get("RoomName");
//                    String roomServiceId = (String) roomInfo.get("RoomServiceId");
//                    String roomId = (String) roomInfo.get("RoomId");
//                    String coins = (String) roomInfo.get("Coins");
                        JSONArray roomInventory = (JSONArray) roomInfo.get("RoomInventory");
                        Set<Price> priceList = new HashSet<>();
                        for (Object priceAndDate : roomInventory) {
                            Price price = new Price();
                            JSONObject d = (JSONObject) priceAndDate;
                            price.setAvailable((Boolean) d.get("HasAvailability"));
                            price.setValue(((Double) Double.parseDouble(d.get("Price").toString())).intValue() / 10);
                            price.setDate(DateConverter.JalaliToGregorian((String) d.get("DateHijri")));
                            priceList.add(price);
                        }
                        room.setPrices(priceList);
//                    room.setRoomId("rc"+roomId+"-"+roomServiceId);
//                    room.setMeta(coins);
                        room.setRoomName(roomName);
                        room.setRoomType(roomType);
                        rooms.add(room);
                    }
                }
            }
            return rooms;
        } catch (Exception e) {
            throw e;
        }
    }

    private void randomWait() throws InterruptedException {
        Random r = new Random();
        int Low = sleep;
        int High = sleep + 15000;
        int rand = r.nextInt(High - Low) + Low;
        Thread.sleep(rand);
    }

    private String createURL(String calledName, String startDate, String endDate) {
        setUrlToCrawl(String.format(getUrlUserRedirect(), calledName));
        return String.format(getUrlPattern(), calledName, startDate, endDate);
    }

    protected Document getHtmlDocument(String url, Proxy proxy) throws Exception {
        String html = ApacheHttpClient.getHtml(url, proxy);
        return Jsoup.parse(html);
    }

    @Override
    public void run() {
        while (true) {
            try {
                crawler.crawl(this);
                Thread.sleep(sleep);
            } catch (Exception e) {
                log.error("error in run method jabama: " + e.getMessage());
            }
        }
    }
}
