package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.Image;
import com.name.services.HotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public ImageDownloader(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Override
    public void start() {
        List<Hotel> hotels = hotelService.getAllHotels();
        for (Hotel hotel : hotels) {
            for (Image image : hotel.getImages()) {
                String src = image.getSrc();
                src = src.replace("co", "com");
                String file = src.replace("https://images.jabama.com/", "");
                try {
                    InputStream in = new URL(src).openStream();
                    Files.copy(in, Paths.get("C:\\Users\\Administrator\\Documents\\images\\" + file + ".jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
