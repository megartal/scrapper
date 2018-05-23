package com.name.services;

import com.name.documents.Hotel;
import com.name.repositories.HotelRepository;
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
        return hotelRepository.findAllHotels();
    }

    public void updateHotelPrices(Hotel hotel) {
        hotelRepository.save(hotel);
    }
}
