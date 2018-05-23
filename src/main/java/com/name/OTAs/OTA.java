package com.name.OTAs;

import com.name.models.CrawledData;

import java.util.Map;

/**
 * @Author Akbar
 * @DATE 5/19/2018.
 */
public interface OTA extends Runnable{
    Map<String, CrawledData> getRoomsData(String calledName, String city);
    String getName();
}
