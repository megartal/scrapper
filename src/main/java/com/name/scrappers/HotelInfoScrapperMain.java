package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.*;
import com.name.services.HotelService;
import com.name.util.ApacheHttpClient;
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
public class HotelInfoScrapperMain implements Scrapper{

    private final HotelService hotelService;
    @Value("${xpath}")
    String xpath;
    @Value("${url}")
    private String urlFormat;

    public HotelInfoScrapperMain(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    private void extractInfo(Hotel hotel, String url) {
        String html = ApacheHttpClient.getHtml(url);
        Document doc = Jsoup.parse(html);
        String data = doc.select(xpath).get(0).data();

        String baseURL = "https://images.jabama.co/";
        ArrayList<Image> images = new ArrayList<>();
        JSONArray imgs = new JSONArray(new JSONObject(doc.select("script").get(8).data().replace("var hotelDetailResult = ", "")).get("Images").toString());
        for (Object image : imgs) {
            JSONObject obj = (JSONObject) image;
            Image img = new Image(baseURL + obj.get("Id"), (String) obj.get("Caption"));
            images.add(img);
        }
        hotel.setImages(images);


        String description = data.split(",")[4].split(":")[1].replace("\"", "").trim();
        hotel.setDescription(description);

        data = data.replace(data.substring(data.indexOf("\"description"), data.indexOf("\"url")), "");
        String address = (String) new JSONObject(new JSONObject(data).get("address").toString()).get("streetAddress");
        hotel.setAddress(address.trim());

        JSONArray amenityFeature = new JSONArray(new JSONObject(data).get("amenityFeature").toString());
        Set<Amenity> amenities = new HashSet<>();
        for (Object feature : amenityFeature) {
            JSONObject obj = new JSONObject(feature.toString());
            Amenity amenity = new Amenity((String) obj.get("name"), (boolean) obj.get("value"));
            amenities.add(amenity);
        }
        hotel.setAmenities(amenities);

        JSONArray containsPlace = new JSONArray(new JSONObject(data).get("containsPlace").toString());
        Set<EachRoom> rooms = new HashSet<>();
        for (Object room : containsPlace) {
            JSONObject obj = new JSONObject(room.toString());
            EachRoom eachRoom = new EachRoom((String) obj.get("name"), (Integer) obj.get("maximumAttendeeCapacity"));
            rooms.add(eachRoom);
        }
        hotel.setEachRooms(rooms);

        JSONObject geo = new JSONObject(new JSONObject(data).get("geo").toString());
        Location location = new Location((String) geo.get("latitude"), (String) geo.get("longitude"));
        hotel.setLocation(location);

        Integer star = (Integer) new JSONObject(new JSONObject(data).get("starRating").toString()).get("ratingValue");
        hotel.setStars(star);

        String mainImage = (String) new JSONObject(data).get("image");
        hotel.setMainImage(mainImage);

        hotelService.saveHotel(hotel);
    }

    private Map<Hotel, String> prepareHotels(String url) {
        List<Hotel> hotels = hotelService.getAllHotels();
        Map<Hotel, String> urls = new HashMap<>();
        for (Hotel hotel : hotels) {
            Set<Name> names = hotel.getNames();
            for (Name result : names) {
                if (result.getOTA().equals("jabama")) {
                    urls.put(hotel, String.format(url, (String) result.getName()));
                }
            }
        }
        return urls;
    }

    public void start() {
        Map<Hotel, String> hotels = prepareHotels(urlFormat);
        for (Map.Entry<Hotel, String> hotel : hotels.entrySet()) {
            extractInfo(hotel.getKey(), hotel.getValue());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
