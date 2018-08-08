package com.name.OTAs;

import com.name.documents.Proxy;
import com.name.models.Room;
import com.name.models.ScrapInfo;

import java.util.List;

/**
 * @Author Akbar
 * @DATE 5/19/2018.
 */
public interface OTA extends Runnable{
    List<Room> getRoomsData(ScrapInfo scrapInfo, String city, Proxy proxies);
    String getName();

    String getUrlToCrawl();
}
