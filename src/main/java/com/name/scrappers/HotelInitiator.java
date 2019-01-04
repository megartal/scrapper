package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.ScrapInfo;
import com.name.repositories.hotel.HotelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Calendar;
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
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -3);
        for (Hotel hotel : hotels) {
            if (hotel.getScrapInfo().isEmpty() || hotel.getScrapInfo() == null) {
                hotel.getScrapInfo().add(new ScrapInfo("iranHotelOnline", c.getTime(), "empty"));
                hotel.getScrapInfo().add(new ScrapInfo("jabama", c.getTime(), "empty"));
                hotel.getScrapInfo().add(new ScrapInfo("snapptrip", c.getTime(), "empty"));
                hotel.getScrapInfo().add(new ScrapInfo("jainjas", c.getTime(), "empty"));
                hotel.getScrapInfo().add(new ScrapInfo("eghamat24", c.getTime(), "empty"));
                hotelRepository.deleteByName(hotel.getName());
                hotel.setId(UUID.randomUUID().toString());
                hotelRepository.save(hotel);
            }
        }
    }
}
