package com.name.services;

import com.name.documents.Hotel;
import com.name.repositories.hotel.HotelRepository;
import org.springframework.stereotype.Service;

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
}
