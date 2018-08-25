package com.name.repositories;

import com.name.documents.CrawlerStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author Akbar
 * @DATE 8/23/2018.
 */
@Repository
public interface CrawlerStatusRepository extends MongoRepository<CrawlerStatus, String> {
    CrawlerStatus findByHotelAndNameOTA(String hotelName, String otaName);
}
