package com.name.scrappers;

import com.name.documents.City;
import com.name.documents.Hotel;
import com.name.models.ScrapInfo;
import com.name.services.CityService;
import com.name.services.HotelService;
import com.name.util.ApacheHttpClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Akbar on 2/18/2018.
 */
@Service
@Profile({"names"})
@Slf4j
@Getter
@Setter
public class ScrapHotelNamesMain implements Scrapper {

    private final CityService cityService;
    private final HotelService hotelService;

    @Value("${hotel_name_scrapper.resultsInEachPage}")
    private int resultInEachPage;
    @Value("${hotel_name_scrapper.url}")
    private String url;
    @Value("${hotel_name_scrapper.selector}")
    private String selector;
    @Value("${hotel_name_scrapper.roomDiv}")
    private String roomDiv;
    @Value("${hotel_name_scrapper.webservice}")
    private String webservice;

    public ScrapHotelNamesMain(CityService cityService, HotelService hotelService) {
        this.cityService = cityService;
        this.hotelService = hotelService;
    }

    @Override
    public void start() {
        List<City> cities = cityService.getAllCities();
        try {
            getHotelNames(cities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getHotelNames(List<City> cities) throws Exception {
        for (City rawCity : cities) {
            String city = null;
            try {
                city = URLEncoder.encode(rawCity.getCity(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e.getCause());
            }
            int pageNum = 1;
            while (true) {
                String result = ApacheHttpClient.getHtml(createURL(city, String.valueOf(pageNum)), null);
                Document doc = Jsoup.parse(result);
                if (doc.select(String.format(getSelector(), 1)) == null || doc.select(String.format(getSelector(), 1)).html().isEmpty()) {
                    break;
                } else {
                    for (int i = 1; i <= getResultInEachPage(); i++) {
                        String hotelName = doc.select(String.format(getSelector(), i)).html();
                        if (hotelName != "" && hotelName != null && !hotelName.isEmpty()) {
                            Hotel hotel = new Hotel();
                            Set<ScrapInfo> names = new HashSet<>();
                            names.add(new ScrapInfo("snapptrip", hotelName));
                            hotel.setCity(rawCity.getCity());
                            hotel.setName(hotelName);
                            hotel.setScrapInfo(names);
                            hotelService.saveHotel(hotel);
                            log.info("###### city: " + rawCity + ", hotel: " + hotelName + " ######");
                        } else {
                            log.info("###### is empty! ######");
                        }
                    }
                }
                pageNum++;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            cityService.updateCity(rawCity);
        }
    }

    private String createURL(String city, String page) {
        String str = getUrl().replace("city", city);
        str = str.replace("pageNumber", page);
        return str;
    }
}
