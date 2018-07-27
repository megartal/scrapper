package com.name.scrappers;

import com.name.documents.City;
import com.name.documents.Hotel;
import com.name.models.Image;
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
 * @author Akbar
 * @since 7/10/2018
 */
@Service
@Slf4j
@Profile("images")
public class ImageDownloader implements Scrapper {
    private final HotelService hotelService;
    private final CityService cityService;

    public ImageDownloader(HotelService hotelService, CityService cityService) {
        this.hotelService = hotelService;
        this.cityService = cityService;
    }

    @Override
    public void start() {
        List<City> cities = cityService.getAllCities();
        String filPath = "/home/ara/node-apps/ara/public/images/hotels/";
//        String filPath = "C:\\Users\\Alex\\WebstormProjects\\diringo\\public\\images\\hotels\\";

        List<String> nameOfCities = new ArrayList<>();
        cities.stream().forEach(x -> nameOfCities.add(x.getCity()));
        List<Hotel> hotels = hotelService.getAllHotelsOfCity(nameOfCities);
        for (Hotel hotel : hotels) {
            for (Image image : hotel.getImages()) {
                String src = image.getSrc();
                String url = "https://jainjas.com/storage/images/place/" + src;
                try {
                    InputStream in = ApacheHttpClient.getImageWithoutSSLCertificate(url);
                    src = src.replace("/", "-");
                    if (Files.isReadable(Paths.get(filPath + src))) {
                        log.info(src + "already there");
                        continue;
                    } else {
                        Files.copy(in, Paths.get(filPath + src));
                        log.info(src);
                    }
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }
}
