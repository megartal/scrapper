package com.name.services;

import com.name.documents.Match;
import com.name.models.OTAMatch;
import com.name.repositories.MatchRepository;
import org.springframework.stereotype.Service;

/**
 * @Author Akbar
 * @DATE 5/19/2018.
 */
@Service
public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public OTAMatch getOTARoomMatch(String hotel, String OTAName){
        Match match = matchRepository.findByHotelName(hotel);
        if(match == null)
            return null;
        for (OTAMatch otaMatch : match.getOTAs()) {
            if(otaMatch.getOTAname().equals(OTAName)){
                return otaMatch;
            }
        }
        return null;
    }

    public void saveMatch(Match hotelMatch) {
        matchRepository.save(hotelMatch);
    }
}
