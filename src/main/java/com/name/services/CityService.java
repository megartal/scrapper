package com.name.services;

import com.name.documents.City;
import com.name.repositories.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Akbar
 * @DATE 5/26/2018.
 */
@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> getAllCities() {
        return cityRepository.findByCrawl(true);
    }

    public void updateCity(City crawledCity) {
        City city = cityRepository.findByCity(crawledCity.getCity()).get();
        city.setCrawl(false);
        cityRepository.save(city);
    }
}
