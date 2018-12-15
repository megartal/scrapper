package com.name.OTAs;

import com.name.documents.Proxy;
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
 * @DATE 12/11/2018.
 */
@Service
@Profile({"eghamat24v2"})
@Getter
@Slf4j
public class eghamat24v2 extends BaseOTA {
    private final Crawler crawler;
    @Value("${eghamat24.urlPattern}")
    private String urlPattern;
    @Value("${eghamat24.sleep}")
    private int sleep;
    @Value("${eghamat24.webservice}")
    private String webservice;
    private String name = "eghamat24";

    public eghamat24v2(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public List<Room> getRoomsData(ScrapInfo scrapInfo, String city, Proxy proxies) throws Exception {
        randomWait();
        List<Room> rooms = new ArrayList<>();
        try {
//            String html1 = ApacheHttpClient.getHtmlUsingProxy(createURL(scrapInfo.getHotelName()), proxy);
//            String html1 = ApacheHttpClient.getHtml("https://www.eghamat24.com/YazdHotels/SonnatiRoyayehGhadimHotel.html", proxies);
            String html1 = ApacheHttpClient.getHtml(createURL(scrapInfo.getHotelName()), proxies);
            Document htmlDocument = Jsoup.parse(html1);
            Elements roomElements = htmlDocument.getElementsByClass("tr");
            for (Element roomElement : roomElements) {
                if (roomElement.getElementsByClass("room-capacity").size() == 0) {
                    continue;
                } else {
                    int roomType = roomElement.getElementsByClass("room-capacity").get(0).getElementsByClass("icon-man").size();
                    if (roomType == 0)
                        continue;
                    Set<Price> prices = new HashSet<>();
                    String roomName = roomElement.getElementsByClass("room-info").get(0).getElementsByClass("auto_label").text();
                    Elements priceElements = roomElement.getElementsByClass("item_active");
                    for (Element priceElement : priceElements) {
                        String farsiDate = priceElement.getElementsByClass("hotel_calender_item_header")
                                .get(0).getElementsByTag("p").text().replace("شنبه", "").replace("یکشنبه", "")
                                .replace("یک", "").replace("دو", "").replace("سه", "")
                                .replace("چهار", "").replace("پنج", "").replace("پنجشنبه", "").replace("جمعه", "").trim();
                        String farsiPrice = priceElement.getElementsByClass("new_price").text().replace("تومان", "").trim().replace(",", "");
                        Date date = getMiladiDate(farsiDate);
                        int value = Integer.parseInt(DigitConverter.convertToEnglishDigits(farsiPrice));
                        prices.add(new Price(date, value, true));
                    }
                    rooms.add(new Room(roomName, roomType, prices));
                }
            }
            return rooms;
        } catch (Exception e) {
            throw e;
        }
    }

    private Date getMiladiDate(String farsiDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(DateConverter.JalaliToGregorian("13" + DigitConverter.convertToEnglishDigits(farsiDate)));
        return c.getTime();
    }

    private void randomWait() throws InterruptedException {
        Random r = new Random();
        int Low = sleep;
        int High = sleep + 8000;
        int rand = r.nextInt(High - Low) + Low;
        Thread.sleep(rand);
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
                log.error("exception in run method eghamat24 v2 : " + e.getMessage());
            }
        }
    }
}
