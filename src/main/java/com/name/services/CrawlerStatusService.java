package com.name.services;

import com.name.documents.CrawlerStatus;
import com.name.repositories.CrawlerStatusRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author Akbar
 * @DATE 8/23/2018.
 */
@Service
public class CrawlerStatusService {
    private final CrawlerStatusRepository crawlerStatusRepository;

    public CrawlerStatusService(CrawlerStatusRepository crawlerStatusRepository) {
        this.crawlerStatusRepository = crawlerStatusRepository;
    }

    public void updateFetchTime(String hotelName, String otaName, String status, String e) {
        CrawlerStatus hotelCrawlStatus = crawlerStatusRepository.findByHotelAndNameOTA(hotelName, otaName);
        if (hotelCrawlStatus == null) {
            CrawlerStatus crawlerStatus = new CrawlerStatus();
            crawlerStatus.setHotel(hotelName);
            crawlerStatus.setNameOTA(otaName);
            crawlerStatus.setStatus(status);
            crawlerStatus.setDate(new Date());
            crawlerStatus.setException(e);
            crawlerStatusRepository.save(crawlerStatus);
        } else {
            hotelCrawlStatus.setStatus(status);
            hotelCrawlStatus.setDate(new Date());
            hotelCrawlStatus.setException(e);
            crawlerStatusRepository.save(hotelCrawlStatus);
        }
    }
}
