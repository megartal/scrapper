package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.ScrapInfo;
import com.name.repositories.hotel.HotelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author Akbar
 * @DATE 12/10/2018.
 */
@Service
@Profile({"init"})
@Slf4j
public class OTAInitiator implements Scrapper {
    private HotelRepository hotelRepository;

    public OTAInitiator(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public void start() {
        List<Hotel> hotels = hotelRepository.findAll();
        for (Hotel hotel : hotels) {
            hotel.getScrapInfo().add(new ScrapInfo("iranHotelOnline", new Date(), "empty"));
//            hotel.getScrapInfo().remove(new ScrapInfo("iranHotelOnline", "empty"));
            hotelRepository.save(hotel);
        }
    }
}
