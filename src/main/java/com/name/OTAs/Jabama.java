package com.name.OTAs;

import com.name.models.CrawledData;
import com.name.models.OTAData;
import com.name.models.Price;
import com.name.util.Crawler;
import com.name.util.DateConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
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
    @Value("${jabama.redirect}")
    private String redirect;
    @Value("${jabama.sleep}")
    private int sleep;
    private String name = "jabama";

    public Jabama(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public Map<String, CrawledData> getRoomsData(String calledName, String city) {
        Map<String, CrawledData> crawledDataList = new HashMap<>();
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 32);
        String startDate = DateConverter.getShamsidate(dt).replace("/", "");
        String endDate = DateConverter.getShamsidate(c.getTime()).replace("/", "");
        Document htmlDocument = getHtmlDocument(createURL(calledName, startDate, endDate));
        Elements scripts = htmlDocument.getElementsByTag("script");
        for (Element script : scripts) {
            if (script.data().contains("hotelDetailResult")) {
                JSONArray roomServices = (JSONArray) new JSONObject(script.data().replace("var hotelDetailResult =", "")).get("RoomServices");
                for (Object roomService : roomServices) {
                    CrawledData crawledData = new CrawledData();
                    JSONObject roomInfo = (JSONObject) roomService;
                    String roomName = (String) roomInfo.get("RoomName");
                    JSONArray roomInventory = (JSONArray) roomInfo.get("RoomInventory");
                    Set<Price> priceList = new HashSet<>();
                    OTAData otaData = new OTAData();
                    for (Object priceAndDate : roomInventory) {
                        Price price = new Price();
                        JSONObject d = (JSONObject) priceAndDate;
                        price.setAvailable((Boolean) d.get("HasAvailability"));
                        price.setValue(d.get("Price").toString());
                        price.setDate((String) d.get("DateHijri"));
                        priceList.add(price);
                    }
                    otaData.setPrices(priceList);
                    otaData.setRedirect(String.format(redirect, calledName));
                    otaData.setName(getName());
                    crawledData.setRoomName(roomName);
                    crawledData.setOtaData(otaData);
                    crawledDataList.put(roomName, crawledData);
                }
                return crawledDataList;
            }
        }
        return null;
    }

    private String createURL(String calledName, String startDate, String endDate) {
        setUrlToCrawl(String.format(getUrlPattern(), calledName, startDate, endDate));
        return getUrlToCrawl();
    }

    @Override
    public void run() {
        while (true) {
            try {
                crawler.crawl(this);
                Thread.sleep(sleep);
            } catch (Exception e) {
                log.error("error in jabama: " + e.getMessage());
            }
        }
    }
}
