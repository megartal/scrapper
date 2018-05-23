package com.name.repositories;

import com.name.documents.RawMatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Akbar
 * @since 4/23/2018
 */
@Repository
public interface RawMatchRepository extends MongoRepository<RawMatch, String> {
    RawMatch findByHotelName(String hotelName);
}
