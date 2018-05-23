package com.name.repositories;

import com.name.documents.Hotel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Akbar
 * @since 4/23/2018
 */
@Repository
public interface HotelRepository extends MongoRepository<Hotel, String> {
    @Query(value = "{}")
    List<Hotel> findAllHotels();
}
