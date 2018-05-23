package com.name.repositories;

import com.name.documents.Match;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Akbar
 * @since 4/23/2018
 */
@Repository
public interface MatchRepository extends MongoRepository<Match, String> {
    Match findByHotelName(String name);
}
