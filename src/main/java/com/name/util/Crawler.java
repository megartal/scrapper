package com.name.util;

import com.google.common.collect.Sets;
import com.name.OTAs.OTA;
import com.name.documents.Hotel;
import com.name.documents.Rate;
import com.name.models.*;
import com.name.services.HotelService;
import com.name.services.RateService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @Author Akbar
 * @DATE 5/24/2018.
 */
@Service
@Getter
@Setter
@Slf4j
public class Crawler {
    private final HotelService hotelService;
    private final RateService rateService;

    public Crawler(HotelService hotelService, RateService rateService) {
        this.hotelService = hotelService;
        this.rateService = rateService;
    }

    public void crawl(OTA ota) {
        List<Hotel> hotels = hotelService.getAllHotels();
        for (Hotel hotel : hotels) {
            log.info(ota.getName() + ": crawling " + hotel.getName() + "started.");
            try {
                ScrapInfo otaScrapInfo = getOTAScrapInfo(hotel, ota.getName());
                List<Room> roomsData = ota.getRoomsData(otaScrapInfo, hotel.getCity());
                processData(roomsData, ota, hotel, otaScrapInfo);
                Thread.sleep(3000);
            } catch (Exception e) {
                log.error("OTA: " + ota.getName() + ", Hotel name: " + hotel.getName() + "\n" + e.getMessage());
                continue;
            }
        }
    }

    private ScrapInfo getOTAScrapInfo(Hotel hotel, String otaName) {
        for (ScrapInfo info : hotel.getScrapInfo()) {
            if (info.getOTAName().equals(otaName)) {
                if (info.getHotelName().equals("empty"))
                    throw new NullPointerException("hotel name is empty");
                return info;
            }
        }
        ScrapInfo scrapInfo = new ScrapInfo(otaName, "empty", false);
        hotel.getScrapInfo().add(scrapInfo);
        hotelService.saveHotel(hotel);
        throw new NullPointerException("there is no OTA info Scrapper.");
    }

    private void processData(List<Room> roomsData, OTA ota, Hotel hotel, ScrapInfo scrapInfo) {
        Set<RoomType> roomTypes = new HashSet<>();
        boolean isReady = true;
        for (Room roomsDatum : roomsData) {
            if (roomsDatum.getRoomType() == 0)
                isReady = false;
            roomTypes.add(new RoomType(roomsDatum.getRoomName(), roomsDatum.getRoomType()));
        }
        ScrapInfo newInfo = new ScrapInfo(ota.getName(), scrapInfo.getHotelName(), roomTypes, isReady);
        hotel.getScrapInfo().remove(newInfo);
        hotel.getScrapInfo().add(newInfo);
        OTAData otaData = new OTAData(ota.getName(), ota.getUrlToCrawl(), new HashSet<>(roomsData));
        hotel.getData().remove(otaData);
        hotel.getData().add(otaData);
        hotelService.updateHotelPrices(hotel);
    }

    private void isChanged(String hotelName, String otaName, String room, Set<Price> oldPrices, Set<Price> newPrices) {
        Rate rate;
        if (Sets.symmetricDifference(oldPrices, newPrices).isEmpty()) {
            rate = new Rate(new Date(), otaName, hotelName, room, false);

        } else {
            rate = new Rate(new Date(), otaName, hotelName, room, true);
        }
        rateService.add(rate);
    }
}
