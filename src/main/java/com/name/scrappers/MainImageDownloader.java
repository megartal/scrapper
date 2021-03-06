package com.name.scrappers;

import com.name.documents.City;
import com.name.documents.Hotel;
import com.name.services.CityService;
import com.name.services.HotelService;
import com.name.util.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Akbar
 * @DATE 7/30/2018.
 */
@Service
@Slf4j
@Profile("main-images")
public class MainImageDownloader implements Scrapper {
    private final HotelService hotelService;
    private final CityService cityService;

    public MainImageDownloader(HotelService hotelService, CityService cityService) {
        this.hotelService = hotelService;
        this.cityService = cityService;
    }

    @Override
    public void start() {
        try {
            List<City> cities = cityService.getAllCities();
            String filPath = "/home/ara/node-apps/ara/public/images/hotels/";
//        String filPath = "C:\\Users\\Alex\\WebstormProjects\\diringo\\public\\images\\hotels\\";

            List<String> nameOfCities = new ArrayList<>();
            cities.stream().forEach(x -> nameOfCities.add(x.getCity()));
            List<Hotel> hotels = hotelService.getAllHotelsOfCity(nameOfCities);
            for (Hotel hotel : hotels) {
                String url = hotel.getMainImage();
                if (url == null)
                    continue;
                try {
                    String src = url.replace("https://images.jabama.com/", "mainImage-");
                    src = src + ".jpg";
                    if (Files.isReadable(Paths.get(filPath + src))) {
                        log.info(url + "already there");
                        continue;
                    } else {
                        InputStream in = ApacheHttpClient.getImageWithoutSSLCertificate(url);
                        Files.copy(in, Paths.get(filPath + src));
                        log.info(src);
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
