package com.name.services;

import com.name.documents.RawMatch;
import com.name.models.CrawledData;
import com.name.models.OTAMatch;
import com.name.models.RoomMatch;
import com.name.repositories.RawMatchRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author Akbar
 * @DATE 5/20/2018.
 */
@Service
public class RawMatchService {
    private final RawMatchRepository rawMatchRepository;

    public RawMatchService(RawMatchRepository rawMatchRepository) {
        this.rawMatchRepository = rawMatchRepository;
    }

    public void addNewRawMatch(Map<String, CrawledData> roomsData, String hotelName, String ota) {
        RawMatch rawMatch = getRawMatchByHotelName(hotelName);
        OTAMatch otaMatch = new OTAMatch();
        Set<RoomMatch> roomMatchs = new HashSet<>();
        for (CrawledData roomsDatum : roomsData.values()) {
            roomMatchs.add(new RoomMatch(roomsDatum.getRoomName()));
        }
        otaMatch.setOTAname(ota);
        otaMatch.setRooms(roomMatchs);
        if (rawMatch == null)
            rawMatch = new RawMatch();
        rawMatch.getOTAs().add(otaMatch);
        rawMatchRepository.save(rawMatch);
    }


    public RawMatch getRawMatchByHotelName(String hotelName){
        return rawMatchRepository.findByHotelName(hotelName);
    }
}
