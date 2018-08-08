package com.name.util;

import com.google.common.collect.Sets;
import com.name.OTAs.OTA;
import com.name.documents.Hotel;
import com.name.documents.Proxy;
import com.name.documents.Rate;
import com.name.models.*;
import com.name.services.HotelService;
import com.name.services.ProxyService;
import com.name.services.RateService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;


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
    private final ProxyService proxyService;

    public Crawler(HotelService hotelService, RateService rateService, ProxyService proxyService) {
        this.hotelService = hotelService;
        this.rateService = rateService;
        this.proxyService = proxyService;
    }

    public void crawl(OTA ota) {
        List<Hotel> hotels = hotelService.getAllHotels();
//        Hotel hotelByName = hotelService.getHotelByName("b93fea95-a148-4637-babc-c3d107ffe83f");
//        ArrayList<Hotel> hotels = new ArrayList<>();
//        hotels.add(hotelByName);
        List<Proxy> proxies = proxyService.getHttpsProxies();
        log.info("num of proxies: " + proxies.size());
//        List<Proxy> proxies = proxyService.getHttpProxies();
        int count = 0;
        for (Hotel hotel : hotels) {
            Proxy proxy = proxies.get(count);
            count++;
            if (count > 50)
                count = 0;
            try {
                ScrapInfo otaScrapInfo = getOTAScrapInfo(hotel, ota.getName());
                if (otaScrapInfo.getHotelName().equals("nist")) {
                    log.info(ota.getName() + ": crawling " + hotel.getName() + " nist.");
                    continue;
                }
                if (hotel.getImages().isEmpty()) {
                    log.info(ota.getName() + ": crawling " + hotel.getName() + " no data.");
                    continue;
                }
                if (hotel.getMainImage() == null || hotel.getMainImage().isEmpty()) {
                    log.info(ota.getName() + ": crawling " + hotel.getName() + " no data.");
                    continue;
                }
                log.info(ota.getName() + ": crawling " + hotel.getName() + "started.");
                List<Room> roomsData = ota.getRoomsData(otaScrapInfo, hotel.getCity(), proxy);
                processData(roomsData, ota, hotel, otaScrapInfo);
                Random r = new Random();
                int Low = 70000;
                int High = 100000;
                int rand = r.nextInt(High - Low) + Low;
                Thread.sleep(rand);
            } catch (Exception e) {
                log.error("OTA: " + ota.getName() + ", Hotel name: " + hotel.getName() + "\n" + e.getMessage());
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e1) {
                }
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
