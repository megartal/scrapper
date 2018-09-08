package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.OTAData;
import com.name.repositories.hotel.HotelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @Author Akbar
 * @DATE 9/8/2018.
 */
@Service
@Profile({"remove"})
@Slf4j
public class RemoveOTAData implements Scrapper {
    private final HotelRepository hotelRepository;

    public RemoveOTAData(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }


    @Override
    public void start() {
        List<Hotel> all = hotelRepository.findAll();
        List<Hotel> modified = new ArrayList<>();
        Iterator<Hotel> hotelIterator = all.iterator();
        while (hotelIterator.hasNext()) {
            Set<OTAData> data = hotelIterator.next().getData();
            Iterator<OTAData> OTAIterator = data.iterator();
            while (OTAIterator.hasNext()) {
                OTAData OTA = OTAIterator.next();
                if (OTA.getOTAName().equals("eghamat24"))
                    OTAIterator.remove();
            }
        }
        hotelRepository.saveAll(all);
        System.exit(0);
    }
}
