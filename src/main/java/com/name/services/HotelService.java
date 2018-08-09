package com.name.services;

import com.name.documents.Hotel;
import com.name.models.ScrapInfo;
import com.name.repositories.hotel.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @Author Akbar
 * @DATE 5/17/2018.
 */
@Service
public class HotelService {
    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public void updateHotelPrices(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public void saveHotels(List<Hotel> hotels) {
        hotelRepository.saveAll(hotels);
    }

    public void saveHotel(Hotel hotelToPersist) {
        hotelRepository.save(hotelToPersist);
    }

    public List<Hotel> getAllHotelsOfCity(List<String> cities) {
        return hotelRepository.findAllByCities(cities);
    }

    public Hotel getHotelByName(String id) {
        return hotelRepository.findById(id).get();
    }

    public List<Hotel> getObsoleteHotel(int num, String otaName) {
        List<Hotel> all = hotelRepository.findAll();
        Collections.sort(all, new Comparator<Hotel>() {
            public int compare(Hotel o1, Hotel o2) {
                Date ob1 = null;
                Date ob2 = null;
                for (ScrapInfo scrapInfo : o1.getScrapInfo()) {
                    if (scrapInfo.getOTAName().equals(otaName))
                        ob1 = scrapInfo.getCrawlDate();
                }
                for (ScrapInfo scrapInfo : o2.getScrapInfo()) {
                    if (scrapInfo.getOTAName().equals(otaName))
                        ob2 = scrapInfo.getCrawlDate();
                }

                return ob1.compareTo(ob2);
            }
        });
        all = all.subList(0, num);
        return all;
    }

    public void update(Hotel hotel, String otaName) {
        for (ScrapInfo scrapInfo : hotel.getScrapInfo()) {
            if (scrapInfo.getOTAName().equals(otaName)) {
                scrapInfo.setCrawlDate(new Date());
                hotelRepository.save(hotel);
                break;
            }
        }

    }
}
