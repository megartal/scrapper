package com.name.repositories;

import com.name.documents.City;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Akbar
 * @since 4/23/2018
 */
@Repository
public interface CityRepository  extends MongoRepository<City, String> {
    Optional<City> findByCity(String city);

    Optional<City> deleteByCity(String city);

    List<City> findByCrawl(boolean b);
}
