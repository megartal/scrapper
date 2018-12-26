package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.ScrapInfo;
import com.name.repositories.hotel.HotelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author Akbar
 * @DATE 12/26/2018.
 */
@Service
@Profile({"hotel-init"})
@Slf4j
public class HotelInitiator implements Scrapper {
    private HotelRepository hotelRepository;

    public HotelInitiator(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public void start() {
        List<Hotel> hotels = hotelRepository.findAll();
        for (Hotel hotel : hotels) {
            if (hotel.getScrapInfo().isEmpty() || hotel.getScrapInfo() == null) {
                hotel.getScrapInfo().add(new ScrapInfo("iranHotelOnline", new Date(), "empty"));
                hotel.getScrapInfo().add(new ScrapInfo("jabama", new Date(), "empty"));
                hotel.getScrapInfo().add(new ScrapInfo("snapptrip", new Date(), "empty"));
                hotel.getScrapInfo().add(new ScrapInfo("jainjas", new Date(), "empty"));
                hotel.getScrapInfo().add(new ScrapInfo("eghamat24", new Date(), "empty"));
                hotelRepository.deleteByName(hotel.getName());
                hotel.setId(UUID.randomUUID().toString());
                hotelRepository.save(hotel);
            }
        }
    }
}
