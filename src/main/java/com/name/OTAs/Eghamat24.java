package com.name.OTAs;

import com.name.models.Price;
import com.name.models.Room;
import com.name.models.ScrapInfo;
import com.name.util.ApacheHttpClient;
import com.name.util.Crawler;
import com.name.util.DateConverter;
import com.name.util.DigitConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
 * @DATE 6/10/2018.
 */
@Service
@Profile({"eghamat24"})
@Getter
@Slf4j
public class Eghamat24 extends BaseOTA {

    private final Crawler crawler;
    @Value("${eghamat24.urlPattern}")
    private String urlPattern;
    @Value("${eghamat24.sleep}")
    private int sleep;
    @Value("${eghamat24.webservice}")
    private String webservice;
    private String name = "eghamat24";

    public Eghamat24(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public List<Room> getRoomsData(ScrapInfo scrapInfo, String city) {
        List<Room> rooms = new ArrayList<>();
        try {
            String html1 = ApacheHttpClient.getHtml(createURL(scrapInfo.getHotelName()));
//            String html1 = ApacheHttpClient.getHtml("https://www.eghamat24.com/TehranHotels/ParsianEsteghlalHotel.html");
            Document htmlDocument = Jsoup.parse(html1);
            int hotelId = Integer.parseInt(htmlDocument.getElementById("hid").attr("value"));
            Elements roomElements = htmlDocument.getElementsByClass("tr");
            for (Element roomElement : roomElements) {
                int roomId;
                String roomName;
                int roomType;
                if (roomElement.getElementsByClass("room-capacity").size() == 0) {
                    continue;
                } else {
                    Elements room_action = roomElement.getElementsByClass("room_action");
                    roomName = roomElement.getElementsByClass("room-info").get(0).getElementsByClass("auto_label").text();
                    roomType = roomElement.getElementsByClass("room-capacity").get(0).getElementsByClass("icon-man").size();
                    if (roomType == 0)
                        continue;
                    roomId = Integer.parseInt(room_action.get(0).getElementsByTag("a").attr("data-id"));
                }
                String currentShamsidate = DateConverter.getCurrentShamsidate();
                String date = currentShamsidate.substring(2).replace("/", "-");
                Set<Price> prices = new HashSet<>();
                for (int i = 0; i < 3; i++) {
                    String html = ApacheHttpClient.getHtml(String.format(webservice, roomId, hotelId, date));
                    Document data = Jsoup.parse(html);
                    Elements elements = data.getElementsByClass("hotel_calender_item");
                    for (Element element : elements) {
                        Price price = new Price();
                        String day = element.getElementsByClass("hotel_calender_item_header").get(0).getElementsByTag("p").text();
                        price.setDate(DateConverter.JalaliToGregorian(day.substring(day.length() - 8, day.length())));
                        Elements roomPrice = element.getElementsByClass("hotel_calender_item_main").get(0).getElementsByClass("new_price");
                        if (roomPrice.size() == 0) {
                            price.setAvailable(false);
                            price.setValue(Integer.MAX_VALUE);
                        } else {
                            price.setAvailable(true);
                            price.setValue(Integer.parseInt(DigitConverter.convertToEnglishDigits(roomPrice.text().replace("تومان", "").replace(",", "")).trim()));
                        }
                        prices.add(price);
                    }
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    c.add(Calendar.DATE, (i + 1) * 10);
                    currentShamsidate = DateConverter.getShamsidate(c.getTime());
                    date = currentShamsidate.substring(2).replace("/", "-");
                }
                Room room = new Room(roomName, roomType, prices);
                rooms.add(room);
                Thread.sleep(1000);
            }
            return rooms;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private String createURL(String hotelName) {
        setUrlToCrawl(String.format(urlPattern, hotelName));
        return getUrlToCrawl();
    }

    @Override
    public void run() {
        while (true) {
            try {
                crawler.crawl(this);
                Thread.sleep(sleep);
            } catch (Exception e) {
                log.error("error in eghamat24: " + e.getMessage());
            }
        }
    }


}
