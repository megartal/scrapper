package com.name.util;

import com.google.common.collect.Sets;
import com.name.OTAs.OTA;
import com.name.documents.Hotel;
import com.name.documents.Rate;
import com.name.models.*;
import com.name.services.HotelService;
import com.name.services.MatchService;
import com.name.services.RateService;
import com.name.services.RawMatchService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


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
    private final RawMatchService rawMatchService;
    private final MatchService matchService;
    private final RateService rateService;

    public Crawler(HotelService hotelService, RawMatchService rawMatchService, MatchService matchService, RateService rateService) {
        this.hotelService = hotelService;
        this.rawMatchService = rawMatchService;
        this.matchService = matchService;
        this.rateService = rateService;
    }

    public void crawl(OTA ota) {
        List<Hotel> hotels = hotelService.getAllHotels();
        for (Hotel hotel : hotels) {
            log.info(ota.getName() + ": crawling " + hotel.getName() + "started.");
            try {
                OTAMatch otaRoomMatch = matchService.getOTARoomMatch(hotel.getName(), ota.getName());
                String calledName = getHotelNameCalledByOTA(hotel.getNames(), ota.getName());
                Map<String, CrawledData> roomsData = ota.getRoomsData(calledName, hotel.getCity());
                processData(roomsData, otaRoomMatch, ota, hotel);
            } catch (Exception e) {
                log.error("OTA: " + ota.getName() + ", Hotel name: " + hotel.getName() + "\n" + e.getMessage() );
                continue;
            }
        }
    }

    private void processData(Map<String, CrawledData> roomsData, OTAMatch matches, OTA ota, Hotel hotel) {
        if (matches == null) {
            rawMatchService.addNewRawMatch(roomsData, hotel.getName(), ota.getName());
        } else {
            for (RoomMatch roomMatch : matches.getRooms()) {
                if (roomMatch.getName() != null && !roomMatch.getName().isEmpty()) {
                    String roomType = roomMatch.getType();
                    String mainName = roomMatch.getDroom();
                    CrawledData crawledData = roomsData.get(roomMatch.getName());
                    Optional<Type> found = hotel.callTypeMethod(roomType).stream().filter(x -> x.getName().equals(mainName)).findFirst();
                    Set<Type> filteredCollect = hotel.callTypeMethod(roomType).stream().filter(x -> !x.getName().equals(mainName)).collect(Collectors.toSet());
                    if (found.isPresent()) {
                        Optional<OTAData> otaNeedToUpdate = found.get().getOTAs().stream().filter(x -> x.getName().equals(ota.getName())).findFirst();
                        isChanged(hotel.getName(), ota.getName(), mainName, otaNeedToUpdate.get().getPrices(), crawledData.getOtaData().getPrices());
                        found.get().getOTAs().remove(crawledData.getOtaData());
                        found.get().getOTAs().add(crawledData.getOtaData());
                        hotel.callTypeMethod(roomType).clear();
                        filteredCollect.add(found.get());
                        hotel.callTypeMethod(roomType).addAll(filteredCollect);
                    } else {
                        Type type = new Type();
                        type.setName(mainName);
                        type.getOTAs().add(crawledData.getOtaData());
                        hotel.callTypeMethod(roomType).add(type);
                    }
                }
            }
            hotelService.updateHotelPrices(hotel);
        }
    }

    private void isChanged(String hotelName, String otaName, String room, Set<Price> oldPrices, Set<Price> newPrices) {
        Rate rate;
        if(Sets.symmetricDifference(oldPrices,newPrices).isEmpty()){
            rate = new Rate(new Date(), otaName, hotelName, room, false);

        }else {
            rate = new Rate(new Date(), otaName, hotelName, room, true);
        }
            rateService.add(rate);
    }

    private String getHotelNameCalledByOTA(Set<Name> names, String ota) {
        for (Name name : names) {
            if (name.getOTA().equals(ota))
                return name.getName();
        }
        return null;
    }
}
