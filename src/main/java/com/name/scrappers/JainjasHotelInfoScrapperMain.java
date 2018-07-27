package com.name.scrappers;

import com.name.documents.City;
import com.name.documents.Hotel;
import com.name.models.Amenity;
import com.name.models.Image;
import com.name.models.Location;
import com.name.models.ScrapInfo;
import com.name.services.CityService;
import com.name.services.HotelService;
import com.name.util.ApacheHttpClient;
import com.name.util.DateConverter;
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
 * @author Akbar
 * @since 7/10/2018
 */
@Service
@Profile({"jainjasinfo"})
@Slf4j
public class JainjasHotelInfoScrapperMain implements Scrapper {
    private final HotelService hotelService;
    private final CityService cityService;
    @Value("${url}")
    private String jainastUrlFormat;

    public JainjasHotelInfoScrapperMain(HotelService hotelService, CityService cityService) {
        this.hotelService = hotelService;
        this.cityService = cityService;
    }

    private void extractInfo(Hotel hotel, String url) throws Exception {
        if (url.contains("nist"))
            return;
        try {
            String html1 = ApacheHttpClient.getHtmlWithoutSSLCertificate(url);
            Document doc1 = Jsoup.parse(html1);
            String[] values = doc1.getElementsByAttributeValue("type", "text/javascript").get(0).data().split(";");
            String value = values[3];
            String modelId = value.split("'")[1];
            String currentShamsidate = DateConverter.getCurrentShamsidate();
            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, 2);
            dt = c.getTime();
            String shamsidateAfter2Days = DateConverter.getShamsidate(dt);
            String params = "placeId=" + modelId + "&from=" + currentShamsidate + "&to=" + shamsidateAfter2Days;
            String jsonResult = ApacheHttpClient.postRequest("https://jainjas.com/Place/Rooms", params);
            String jsonResult2 = ApacheHttpClient.getHtmlWithoutSSLCertificate("https://jainjas.com/Place/GetPlaceDetail?placeId=" + modelId + "&from=%DB%B1%DB%B3%DB%B9%DB%B7/%DB%B0%DB%B5/%DB%B0%DB%B4&to=%DB%B1%DB%B3%DB%B9%DB%B7/%DB%B0%DB%B5/%DB%B0%DB%B6");
            JSONObject jsonObject = new JSONObject(jsonResult);
            JSONObject jsonObject2 = new JSONObject(jsonResult2);

            //amenities
            JSONArray placeAttributes = new JSONArray(jsonObject2.get("PlaceAttributes").toString());
            Set<Amenity> amenities = new HashSet<>();
            for (Object placeAttribute : placeAttributes) {
                JSONObject amenity = (JSONObject) (((JSONObject) placeAttribute).get("AttributeDefinition"));
                amenities.add(new Amenity((String) amenity.get("Name"), (String) amenity.get("Icon")));
            }
            hotel.setAmenities(amenities);

            //images
            JSONArray placeImages = new JSONArray(jsonObject2.get("PlaceImages").toString());
            ArrayList<Image> images = new ArrayList<>();
            for (Object placeImage : placeImages) {
                String fileSrc = ((JSONObject) placeImage).get("PlaceId") + "/" + ((JSONObject) placeImage).get("FileName");
                String description = (String) ((JSONObject) placeImage).get("Description");
                images.add(new Image(fileSrc, description));
            }
            hotel.setImages(images);

            //star
            if (!jsonObject.isNull("Class")) {
                Integer star = (Integer) jsonObject.get("Class");
                hotel.setStars(star);
            }

            //category
            if (!jsonObject.isNull("PlaceCategoryKey")) {
                String placeCategoryName = (String) jsonObject.get("PlaceCategoryKey");
                hotel.setCategory(placeCategoryName);
            }

//            //address
//            String addressLine1 = (String) jsonObject.get("AddressLine1");
//            String addressLine2 = (String) jsonObject.get("AddressLine2");
//            String address = addressLine1 + "\n" + addressLine2;
//            hotel.setAddress(address);

//            //description
//            String description = (String) jsonObject.get("Description");
//            hotel.setDescription(description);

            //location
            if (!jsonObject.isNull("Latitude") && !jsonObject.isNull("Longitude")) {
                Location location = new Location(((Double) jsonObject.get("Latitude")).toString(), ((Double) jsonObject.get("Longitude")).toString());
                hotel.setLocation(location);
            }

            //warnings
            if (!jsonObject.isNull("Warnings")) {
                String warnings = (String) jsonObject.get("Warnings");
                hotel.setWarning(warnings);
            }


            //information
            if (!jsonObject.isNull("Information")) {
                String information = (String) jsonObject.get("Information");
                hotel.setInformation(information);
            }

            //regulations
            if (!jsonObject.isNull("RulesAndRegulation")) {
                String rules = (String) jsonObject.get("RulesAndRegulation");
                hotel.setRules(rules);
            }

            //grade
            if (!jsonObject.isNull("Grade")) {
                Integer grade = (Integer) jsonObject.get("Grade");
                hotel.setGrade(grade);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }

        log.info(hotel.getName());
        hotelService.saveHotel(hotel);
    }

    @Override
    public void start() {
        Map<Hotel, String> hotels = prepareHotels(jainastUrlFormat);
        for (Map.Entry<Hotel, String> hotel : hotels.entrySet()) {
            try {
                extractInfo(hotel.getKey(), hotel.getValue());
            } catch (Exception e) {
                log.error(e.getMessage());
                continue;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private Map<Hotel, String> prepareHotels(String url) {
        List<City> cities = cityService.getAllCities();
        List<String> nameOfCities = new ArrayList<>();
        cities.stream().forEach(x -> nameOfCities.add(x.getCity()));
        List<Hotel> hotels = hotelService.getAllHotelsOfCity(nameOfCities);
        Map<Hotel, String> urls = new HashMap<>();
        for (Hotel hotel : hotels) {
            Set<ScrapInfo> names = hotel.getScrapInfo();
            for (ScrapInfo result : names) {
                if (result.getOTAName().equals("jainjas")) {
                    urls.put(hotel, String.format(url, (String) result.getHotelName()));
                }
            }
        }
        return urls;
    }
}
