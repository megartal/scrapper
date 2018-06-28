package com.name.scrappers;

import com.name.documents.City;
import com.name.documents.Hotel;
import com.name.documents.Match;
import com.name.models.EachRoom;
import com.name.models.Name;
import com.name.models.OTAMatch;
import com.name.services.CityService;
import com.name.services.HotelService;
import com.name.services.MatchService;
import com.name.util.ApacheHttpClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Akbar on 2/18/2018.
 */
@Service
@Profile({"names"})
@Slf4j
@Getter
@Setter
public class ScrapHotelNamesMain implements Scrapper {

    private final CityService cityService;
    private final HotelService hotelService;
    private final MatchService matchService;

    @Value("${hotel_name_scrapper.resultsInEachPage}")
    private int resultInEachPage;
    @Value("${hotel_name_scrapper.url}")
    private String url;
    @Value("${hotel_name_scrapper.selector}")
    private String selector;
    @Value("${hotel_name_scrapper.roomDiv}")
    private String roomDiv;
    @Value("${hotel_name_scrapper.webservice}")
    private String webservice;


    public ScrapHotelNamesMain(CityService cityService, HotelService hotelService, MatchService matchService) {
        this.cityService = cityService;
        this.hotelService = hotelService;
        this.matchService = matchService;
    }

    @Override
    public void start() {
        List<City> cities = cityService.getAllCities();
        getHotelNames(cities);
    }

    public void getHotelNames(List<City> cities) {
        for (City rawCity : cities) {
            String city = null;
            try {
                city = URLEncoder.encode(rawCity.getCity(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e.getCause());
            }
            int pageNum = 1;
            while (true) {
                String result = ApacheHttpClient.getHtml(createURL(city, String.valueOf(pageNum)));
                Document doc = Jsoup.parse(result);
                if (doc.select(String.format(getSelector(), 1)) == null || doc.select(String.format(getSelector(), 1)).html().isEmpty()) {
                    break;
                } else {
                    for (int i = 1; i <= getResultInEachPage(); i++) {
                        String hotelName = doc.select(String.format(getSelector(), i)).html();
                        String url = "https://www.snapptrip.com" + doc.select(String.format(getSelector(), i)).attr("href");
                        if (hotelName != "" && hotelName != null && !hotelName.isEmpty()) {
                            Hotel hotel = new Hotel();
                            Set<Name> names = new HashSet<>();
                            names.add(new Name("snapptrip", hotelName, url));
                            hotel.setCity(rawCity.getCity());
                            hotel.setName(hotelName);
                            hotel.setNames(names);
                            Set<EachRoom> roomsName = getRoomsName(url);
                            hotel.setEachRooms(roomsName);
                            Match hotelMatch = getHotelMatch(hotelName);
                            matchService.saveMatch(hotelMatch);
                            hotelService.saveHotel(hotel);
                            log.info("###### city: " + rawCity + ", hotel: " + hotelName + " ######");
                        } else {
                            log.info("###### is empty! ######");
                        }
                    }
                }
                pageNum++;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Match getHotelMatch(String hotelName) {
        Match match = new Match();
        match.setHotelName(hotelName);
        Set<OTAMatch> otaMatches = new HashSet<>();
        OTAMatch otaMatch = new OTAMatch();
        otaMatch.setOTAname("snapptrip");
        otaMatches.add(otaMatch);
        match.setOTAs(otaMatches);
        return match;
    }

    private Set<EachRoom> getRoomsName(String url) {
        Elements roomElements = getRoomElements(getHtmlDocument(url), getRoomDiv());
        Set<EachRoom> eachRooms = new HashSet<>();
        for (Element roomElement : roomElements) {
            String roomID = roomElement.attr("id").replace("room_", "");
            String html = ApacheHttpClient.getHtml(String.format(getWebservice(), roomID));
            JSONObject jsonObject = (JSONObject) new JSONObject(html).get("data");
            String roomName = jsonObject.getString("room_title");
            eachRooms.add(new EachRoom(roomName));
        }
        return eachRooms;
    }

    protected Document getHtmlDocument(String url) {
        String html = ApacheHttpClient.getHtml(url);
        return Jsoup.parse(html);
    }

    protected Elements getRoomElements(Document doc, String roomDiv) {
        return doc.getElementsByClass(roomDiv);
    }

    private String createURL(String city, String page) {
        String str = getUrl().replace("city", city);
        str = str.replace("pageNumber", page);
        return str;
    }
}
