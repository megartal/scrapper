package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.ScrapInfo;
import com.name.services.HotelService;
import com.name.util.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Akbar
 * @since 7/10/2018
 */
@Service
@Profile({"info"})
@Slf4j
public class JainjastHotelInfoScrapperMain implements Scrapper {
    private final HotelService hotelService;
    @Value("${jainjas.url}")
    private String jainastUrlFormat;

    public JainjastHotelInfoScrapperMain(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    private void extractInfo(Hotel hotel, String url) throws Exception {
        String html1 = ApacheHttpClient.getHtml(url);
        Document doc1 = Jsoup.parse(html1);
        Elements script = doc1.getElementsByTag("script");
        for (Element element : script) {
            Elements type = element.getElementsByAttributeValue("type", "text/javascript");
        }

        String html = ApacheHttpClient.postRequest(url, "{\"placeId\":\"%s\",\"from\":\"%s\",\"to\":\"%s\"} ");
        Document doc = Jsoup.parse(html);

        hotelService.saveHotel(hotel);
    }

    @Override
    public void start() {
        Map<Hotel, String> hotels = prepareHotels(jainastUrlFormat);
        for (Map.Entry<Hotel, String> hotel : hotels.entrySet()) {
            try {
                extractInfo(hotel.getKey(), hotel.getValue());
            } catch (Exception e) {
                log.error(e.getMessage());
                continue;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private Map<Hotel, String> prepareHotels(String url) {
        List<Hotel> hotels = hotelService.getAllHotels();
        Map<Hotel, String> urls = new HashMap<>();
        for (Hotel hotel : hotels) {
            Set<ScrapInfo> names = hotel.getScrapInfo();
            for (ScrapInfo result : names) {
                if (result.getOTAName().equals("jainjas")) {
                    urls.put(hotel, String.format(url, (String) result.getHotelName()));
                }
            }
        }
        return urls;
    }
}
