package com.name.scrappers;

import com.name.documents.City;
import com.name.documents.Hotel;
import com.name.models.ScrapInfo;
import com.name.repositories.CityRepository;
import com.name.repositories.hotel.HotelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author Akbar
 * @DATE 5/20/2018.
 */
@Service
@Profile({"insert"})
public class InsertData implements Scrapper{
    private final HotelRepository hotelRepository;
    private final CityRepository cityRepository;

    public InsertData(HotelRepository hotelRepository, CityRepository cityRepository) {
        this.hotelRepository = hotelRepository;
        this.cityRepository = cityRepository;
    }


    @Override
    public void start() {
//        insertNewHotel();
//        insertMatch();
//        insertCity();
        hotelRepository.deleteAll();
    }

    private void insertCity() {
        List<City> cities = new ArrayList<>();
        cities.add(new City("تهران", "تهران"));
        cities.add(new City("تبریز", "آذربایجان شرقی"));
        cityRepository.saveAll(cities);
    }

    private void insertNewHotel() {
        Hotel hotel = new Hotel();
        hotel.setName("هتل پارسیان استقلال تهران");
        hotel.setCity("تهران");
        ScrapInfo name = new ScrapInfo();
        name.setHotelName("هتل پارسیان استقلال تهران");
        name.setOTAName("snapptrip");
        Set<ScrapInfo> names = new HashSet<>();
        names.add(name);

        ScrapInfo name2 = new ScrapInfo();
        name2.setHotelName("tehran-esteghlal");
        name2.setOTAName("jabama");
        names.add(name2);

        hotel.setScrapInfo(names);
        hotelRepository.save(hotel);
    }
}
