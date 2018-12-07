package com.name.scrappers;

import com.name.documents.AutoComplete;
import com.name.documents.City;
import com.name.documents.Hotel;
import com.name.repositories.AutoCompleteRepository;
import com.name.repositories.CityRepository;
import com.name.repositories.hotel.HotelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Akbar
 * @DATE 8/25/2018.
 */
@Service
@Profile({"auto"})
public class ProvideAutoCompleteData implements Scrapper {
    private final CityRepository cityRepository;
    private final HotelRepository hotelRepository;
    private final AutoCompleteRepository autoCompleteRepository;

    public ProvideAutoCompleteData(CityRepository cityRepository, HotelRepository hotelRepository, AutoCompleteRepository autoCompleteRepository) {
        this.cityRepository = cityRepository;
        this.hotelRepository = hotelRepository;
        this.autoCompleteRepository = autoCompleteRepository;
    }

    @Override
    public void start() {
        List<City> cities = cityRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        for (City city : cities) {
            AutoComplete autoComplete = new AutoComplete(city.getCity(), city.getCity(), city.getDistrict(), "شهر", "");
            autoCompleteRepository.save(autoComplete);
        }

        for (Hotel hotel : hotels) {
            AutoComplete autoComplete = null;
            if (hotel.getName().contains("اقامتگاه سنتی")) {
                autoComplete = new AutoComplete(hotel.getName(), hotel.getCity(), hotel.getDistrict(), "اقامتگاه سنتی", "sonati");
            } else if (hotel.getName().contains("مهمانسرا")) {
                autoComplete = new AutoComplete(hotel.getName(), hotel.getCity(), hotel.getDistrict(), "مهمانسرا", "sara");
            } else if (hotel.getName().contains("آپارتمان")) {
                autoComplete = new AutoComplete(hotel.getName(), hotel.getCity(), hotel.getDistrict(), " هتل آپارتمان", "aparteman");
            } else {
                autoComplete = new AutoComplete(hotel.getName(), hotel.getCity(), hotel.getDistrict(), "هتل", "hotel");
            }
            autoCompleteRepository.save(autoComplete);
        }
    }
}
