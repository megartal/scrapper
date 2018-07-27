package com.name.repositories.hotel;

import com.name.documents.Hotel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Akbar
 * @since 4/23/2018
 */
@Repository
public interface HotelRepository extends MongoRepository<Hotel, String>, HotelRepositoryCustom {

}
