package com.name.util;

import com.google.common.collect.Sets;
import com.name.OTAs.OTA;
import com.name.documents.Hotel;
import com.name.documents.Proxy;
import com.name.documents.Rate;
import com.name.models.*;
import com.name.services.CrawlerStatusService;
import com.name.services.HotelService;
import com.name.services.ProxyService;
import com.name.services.RateService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @Author Akbar
 * @DATE 5/24/2018.
 */
@Service
@Getter
@Setter
@Slf4j
public class Crawler {
    private final HotelService hotelService;
    private final RateService rateService;
    private final ProxyService proxyService;
    private final CrawlerStatusService crawlerStatusService;

    public Crawler(HotelService hotelService, RateService rateService, ProxyService proxyService, CrawlerStatusService crawlerStatus) {
        this.hotelService = hotelService;
        this.rateService = rateService;
        this.proxyService = proxyService;
        this.crawlerStatusService = crawlerStatus;
    }

    public void crawl(OTA ota) {
//        List<Hotel> hotels = hotelService.getAllHotels();
        Iterable<Hotel> hotels = hotelService.getObsoleteHotel(30, ota.getName());
//        log.info(ota.getName() + ": pick " + hotels + " hotel to crawl.");
//        Hotel hotelByName = hotelService.getHotelByName("7da3a06b-ac86-4b95-9667-5f8943fcfd00");
//        ArrayList<Hotel> hotels = new ArrayList<>();
//        hotels.add(hotelByName);
        List<Proxy> proxies = proxyService.getHttpsProxies();
        if (proxies.size() < 10)
            updateProxies();

        log.info("num of proxies: " + proxies.size());
//        List<Proxy> proxies = proxyService.getHttpProxies();
        int count = 0;
        for (Hotel hotel : hotels) {
            Proxy proxy = proxies.get(count);
            count++;
            if (count > (proxies.size() - 2))
                count = 0;
            try {
                ScrapInfo otaScrapInfo = getOTAScrapInfo(hotel, ota.getName());
                if (otaScrapInfo.getHotelName().equals("nist")) {
                    log.info(ota.getName() + ": crawling " + hotel.getName() + " nist.");
                    hotelService.update(hotel, ota.getName());
                    continue;
                }
                if (hotel.getImages().isEmpty()) {
                    log.info(ota.getName() + ": crawling " + hotel.getName() + " no data.");
                    hotelService.update(hotel, ota.getName());
                    continue;
                }
                if (hotel.getMainImage() == null || hotel.getMainImage().isEmpty()) {
                    log.info(ota.getName() + ": crawling " + hotel.getName() + " no data.");
                    hotelService.update(hotel, ota.getName());
//                    crawlerStatusService.updateFetchTime(hotel.getName(), ota.getName(), "NoData", null);
                    continue;
                }
                log.info(ota.getName() + ": crawling " + hotel.getName() + "started.");
                List<Room> roomsData = ota.getRoomsData(otaScrapInfo, hotel.getCity(), proxy);
                processData(roomsData, ota, hotel, otaScrapInfo);
                log.info(ota.getName() + ": crawling " + hotel.getName() + " finished.");
                hotelService.update(hotel, ota.getName());
                log.info(ota.getName() + ": success");
            } catch (Exception e) {
                log.error("OTA: " + ota.getName() + ", Hotel name: " + hotel.getName() + "\n" + e.getMessage());
                if (e.getMessage().contains("hotel name is empty")) {
                    hotelService.update(hotel, ota.getName());
                    continue;
                }
                hotelService.update(hotel, ota.getName());
                if (!e.getMessage().contains("safe proxy")) {
                    proxyService.update(proxy);
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }
        }
    }

    private ScrapInfo getOTAScrapInfo(Hotel hotel, String otaName) {
        for (ScrapInfo info : hotel.getScrapInfo()) {
            if (info.getOTAName().equals(otaName)) {
                if (info.getHotelName().equals("empty"))
                    throw new NullPointerException("hotel name is empty");
                return info;
            }
        }
        log.info(hotel.getName() + " has no scrap info!");
//        ScrapInfo scrapInfo = new ScrapInfo(otaName, "empty", false);
//        hotel.getScrapInfo().add(scrapInfo);
//        hotelService.saveHotel(hotel);

        throw new NullPointerException("there is no OTA info Scrapper.");
    }

    private void processData(List<Room> roomsData, OTA ota, Hotel hotel, ScrapInfo scrapInfo) {
        Set<RoomType> roomTypes = new HashSet<>();
        boolean isReady = true;
        for (Room roomsDatum : roomsData) {
            if (roomsDatum.getRoomType() == 0)
                isReady = false;
            roomTypes.add(new RoomType(roomsDatum.getRoomName(), roomsDatum.getRoomType()));
        }
        ScrapInfo newInfo;
        if (scrapInfo.getHotelId() == null) {
            newInfo = new ScrapInfo(ota.getName(), scrapInfo.getHotelName(), roomTypes, isReady);
        } else {
            newInfo = new ScrapInfo(ota.getName(), scrapInfo.getHotelName(), scrapInfo.getHotelId(), roomTypes, isReady);
        }
        hotel.getScrapInfo().remove(newInfo);
        hotel.getScrapInfo().add(newInfo);
        OTAData otaData = new OTAData(ota.getName(), ota.getUrlToCrawl(), new HashSet<>(roomsData));
        hotel.getData().remove(otaData);
        hotel.getData().add(otaData);
        hotelService.updateHotelPrices(hotel);
    }

    private void isChanged(String hotelName, String otaName, String room, Set<Price> oldPrices, Set<Price> newPrices) {
        Rate rate;
        if (Sets.symmetricDifference(oldPrices, newPrices).isEmpty()) {
            rate = new Rate(new Date(), otaName, hotelName, room, false);

        } else {
            rate = new Rate(new Date(), otaName, hotelName, room, true);
        }
        rateService.add(rate);
    }

    public void updateProxies() {
        List<Proxy> proxies = new ArrayList<>();
        String html = null;
        try {
            html = ApacheHttpClient.getHtml("https://free-proxy-list.net/", null);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        Document doc = Jsoup.parse(html);
        Elements tr = doc.getElementsByTag("tr");
        for (Element element : tr) {
            try {
                Proxy proxy = new Proxy();
                if (element.getElementsByTag("td").size() == 0)
                    continue;
                proxy.setIp(element.getElementsByTag("td").get(0).text());
                proxy.setPort(element.getElementsByTag("td").get(1).text());
                if (element.getElementsByTag("td").get(6).text().equals("yes")) {
                    proxy.setProtocol("https");
                } else {
                    proxy.setProtocol("http");
                }
                proxy.setStatus(true);
                proxies.add(proxy);
            } catch (Exception e) {
                continue;
            }
        }
        proxyService.deleteAll();
        proxyService.saveAll(proxies);
    }
}
