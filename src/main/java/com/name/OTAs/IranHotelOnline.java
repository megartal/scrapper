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
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author Akbar
 * @DATE 12/10/2018.
 */
@Service
@Profile({"iranHotelOnline"})
@Getter
@Slf4j
public class IranHotelOnline extends BaseOTA {
    private final Crawler crawler;
    @Value("${iranHotelOnline.urlPattern}")
    private String urlPattern;
    @Value("${iranHotelOnline.userRedirect}")
    private String userRedirect;
    @Value("${iranHotelOnline.sleep}")
    private int sleep;
    private String name = "iranHotelOnline";

    public IranHotelOnline(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public List<Room> getRoomsData(ScrapInfo scrapInfo, String city, Proxy proxies) throws Exception {
        randomWait(sleep, 20000);
        Map<String, Room> rooms = new HashMap<>();
        try {
            for (int i = 0; i < 3; i++) {
                randomWait(2000, 5000);
                String startDate = getRequiredDate(i * 10);
                Document htmlDocument = getHtmlDocument(createURL(scrapInfo.getHotelName(), startDate, 10), proxies);
                Elements elementsByRoom = htmlDocument.getElementsByClass("room-information");
                for (Element roomElement : elementsByRoom) {
                    String roomName = roomElement.getElementsByClass("view-name-room").get(0).getElementsByClass("more").text();
                    int roomType = Integer.parseInt(roomElement.getElementsByClass("p-capacity").get(0).getElementsByTag("img").attr("src").replace("/persian/Images/person/", "").replace("person.jpg", ""));
                    Room room = rooms.getOrDefault(roomName, new Room(roomName, roomType));
                    Elements elementsByRoomPrice = roomElement.getElementsByClass("price-item");
                    Set<Price> prices = new HashSet<>();
                    for (Element priceElement : elementsByRoomPrice) {
                        Date date = parseShamsiDate(priceElement.getElementsByClass("txt-value").get(0).text());
                        try {
                            int price = Integer.parseInt(priceElement.getElementsByClass("n-price").get(0).text().replace(",", "")) / 10;
                            prices.add(new Price(date, price, true));
                        } catch (Exception e) {
                            prices.add(new Price(date, Integer.MAX_VALUE - 1, false));
                        }
                    }
                    room.getPrices().addAll(prices);
                    rooms.put(roomName, room);
                }
            }
            return new ArrayList<>(rooms.values());
        } catch (Exception e) {
            throw e;
        }
    }

    private Date parseShamsiDate(String val) {
        String[] currentShamsiDateSplit = DateConverter.getCurrentShamsiDate().split("/");
        int year = Integer.parseInt(currentShamsiDateSplit[0]);
        ArrayList<String> valSplit = new ArrayList<>(Arrays.asList(val.split(" ")));
        int month = 0;
        int day = 0;
        for (String valSpl : valSplit) {
            if (DateConverter.farsiMonthStringToInt.keySet().contains(valSpl)) {
                month = DateConverter.farsiMonthStringToInt.get(valSpl);
            }
            if (StringUtil.isNumeric(valSpl)) {
                day = Integer.parseInt(valSpl);
            }
        }
        return DateConverter.JalaliToGregorian(year + "/" + month + "/" + day);
    }

    private Document getHtmlDocument(String url, Proxy proxy) throws Exception {
        String html = ApacheHttpClient.selfSignedHttpClient(url);
        return Jsoup.parse(html);
    }

    private String createURL(String hotelName, String startDate, int span) {
        setUrlToCrawl(String.format(userRedirect, hotelName));
        return String.format(getUrlPattern(), hotelName, startDate, span);
    }

    private String getRequiredDate(int day) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, day);
        return DateConverter.getShamsidate(c.getTime());
    }

    @Override
    public String getName() {
        return "iranHotelOnline";
    }

    @Override
    public void run() {
        while (true) {
            try {
                crawler.crawl(this);
                Thread.sleep(sleep);
            } catch (Exception e) {
                log.error("error in run method iranHotelOnline: " + e.getMessage());
            }
        }
    }

}
