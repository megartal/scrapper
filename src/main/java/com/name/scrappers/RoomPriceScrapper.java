package com.name.scrappers;

import com.name.OTAs.OTA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Akbar on 3/13/2018.
 */
@Service
@Profile({"rooms"})
@Slf4j
public class RoomPriceScrapper implements Scrapper {

    @Value("${crawler.threads}")
    private int numOfThreads;
    private final List<OTA> OTAs;
    private ExecutorService executor;

    public RoomPriceScrapper(List<OTA> OTAS) {
        this.OTAs = OTAS;
    }

    public void start() {
        executor = Executors.newFixedThreadPool(numOfThreads);
        log.info("###### Rooms price scrapper started. ######");
        for (OTA ota : OTAs) {
            executor.execute(ota);
        }
    }
}
