package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.Amenity;
import com.name.models.Image;
import com.name.models.Location;
import com.name.models.ScrapInfo;
import com.name.services.HotelService;
import com.name.util.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Akbar on 2/19/2018.
 */
@Service
@Profile({"info"})
@Slf4j
public class HotelInfoScrapperMain implements Scrapper {

    private final HotelService hotelService;
    @Value("${jabama.xpath}")
    String xpath;
    @Value("${jabama.url}")
    private String urlFormat;

    @Value("${jainjas.url}")
    private String jainastUrlFormat;

    public HotelInfoScrapperMain(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    private void extractInfo(Hotel hotel, String url) throws Exception {
        String html = ApacheHttpClient.getHtml(url);
        Document doc = Jsoup.parse(html);
        String data = doc.select(xpath).get(0).data();
        String[] splitJson = data.split(",");
        String addressJSON = null;
        String amenityFeatureJSON = null;
        String latitudeJSON = null;
        String longitudeJSON = null;
        String imageJSON = null;
        for (String json : splitJson) {
            if (json.contains("streetAddress")) {
                addressJSON = "{" + json + "}";
            } else if (json.contains("latitude")) {
                latitudeJSON = "{" + json + "}";
            } else if (json.contains("longitude")) {
                longitudeJSON = "{" + json + "}";
            } else if (json.contains("image")) {
                imageJSON = "{" + json + "}";
            }
        }


        String baseURL = "https://images.jabama.com/";
        try {
            ArrayList<Image> images = new ArrayList<>();
            JSONArray imgs = new JSONArray(new JSONObject(doc.select("script").get(8).data().replace("var hotelDetailResult = ", "")).get("Images").toString());
            for (Object image : imgs) {
                JSONObject obj = (JSONObject) image;
                Image img = new Image(baseURL + obj.get("Id"), (String) obj.get("Caption"));
                images.add(img);
            }
            hotel.setImages(images);
        } catch (Exception e) {
            log.error("###images failed!!!");
        }

        try {
            String description = data.split(",")[4].split(":")[1].replace("\"", "").trim();
            hotel.setDescription(description);
        } catch (Exception e) {
            log.error("###description failed!!!");
        }
        data = data.replace(data.substring(data.indexOf("\"description"), data.indexOf("\"url")), "");

        try {
            String address = (String) new JSONObject(addressJSON).get("streetAddress");
            hotel.setAddress(address.trim());
        } catch (Exception e) {
            log.error("###address failed!!!");
        }

        try {
            JSONArray amenityFeature = new JSONArray(new JSONObject(data).get("amenityFeature").toString());
            Set<Amenity> amenities = new HashSet<>();
            for (Object feature : amenityFeature) {
                JSONObject obj = new JSONObject(feature.toString());
                Amenity amenity = new Amenity((String) obj.get("name"), (boolean) obj.get("value"));
                amenities.add(amenity);
            }
            hotel.setAmenities(amenities);
        } catch (Exception e) {
            log.error("###amenities failed!!!");
        }


//        JSONArray containsPlace = new JSONArray(new JSONObject(data).get("containsPlace").toString());
//        Set<EachRoom> rooms = new HashSet<>();
//        for (Object room : containsPlace) {
//            JSONObject obj = new JSONObject(room.toString());
//            EachRoom eachRoom = new EachRoom((String) obj.get("name"), (Integer) obj.get("maximumAttendeeCapacity"));
//            rooms.add(eachRoom);
//        }
//        hotel.setEachRooms(rooms);

        try {
            Location location = new Location((String) new JSONObject(latitudeJSON).get("latitude"), (String) new JSONObject(longitudeJSON).get("longitude"));
            hotel.setLocation(location);
        } catch (Exception e) {
            log.error("###location failed!!!");
        }

        try {
            Integer star = (Integer) new JSONObject(new JSONObject(data).get("starRating").toString()).get("ratingValue");
            hotel.setStars(star);
        } catch (Exception e) {
            log.error("###start failed!!!");
        }

        try {
            String mainImage = new JSONObject(imageJSON).get("image").toString();
            hotel.setMainImage(mainImage);
        } catch (Exception e) {
            log.error("###main image failed!!!");
        }
        hotelService.saveHotel(hotel);
    }

    private Map<Hotel, String> prepareHotels(String url) {
        List<Hotel> hotels = hotelService.getAllHotels();
        Map<Hotel, String> urls = new HashMap<>();
        for (Hotel hotel : hotels) {
            Set<ScrapInfo> names = hotel.getScrapInfo();
            for (ScrapInfo result : names) {
                if (result.getOTAName().equals("jabama")) {
                    urls.put(hotel, String.format(url, (String) result.getHotelName()));
                }
            }
        }
        return urls;
    }

    public void start() {
        Map<Hotel, String> hotels = prepareHotels(urlFormat);
        for (Map.Entry<Hotel, String> hotel : hotels.entrySet()) {
            try {
                extractInfo(hotel.getKey(), hotel.getValue());
            } catch (Exception e) {
                log.error(e.getMessage());
                continue;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
