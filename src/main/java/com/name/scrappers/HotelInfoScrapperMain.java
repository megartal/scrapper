package com.name.scrappers;

//import com.diringo.client.ApacheHttpClient;
//import com.diringo.client.MongodbClient;
//import com.diringo.common.ConfigLoader;
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.model.Updates;
//import org.apache.commons.lang.math.NumberUtils;
//import org.bson.Document;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by Tahoe on 2/19/2018.
 */
@Service
@Profile({"info"})
public class HotelInfoScrapperMain implements Scrapper{
//    public static void main(String[] args) {
//        String urlFormat = ConfigLoader.getConfig().getString("scrapper.hotel_info_scrapper.url");
//
//        Map<String, String> urls = prepareHotels(urlFormat);
//        for (Map.Entry<String, String> url : urls.entrySet()) {
//            Map<String, Object> document = extractInfo(url.getValue());
//            persist(url.getKey(), document);
//            try {
//                Thread.sleep(60000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private static void persist(String name, Map<String, Object> doc) {
//        MongodbClient.getCollection("hotel").
//                updateOne(new Document("name", name),
//                        Updates.combine(Updates.set("address", doc.get("address")),
//                                Updates.set("info", doc.get("info")),
//                                Updates.set("location", new Document("lat", doc.get("lat")).append("lng", doc.get("lng"))),
//                                Updates.set("stars", doc.get("stars")), Updates.set("images", doc.get("images")),
//                                Updates.currentDate("lastModified")));
//    }

//    private static Map<String, Object> extractInfo(String url) {
//        String addressXpath = ConfigLoader.getConfig().getString("scrapper.hotel_info_scrapper.address.xpath");
//        String infoXpath = ConfigLoader.getConfig().getString("scrapper.hotel_info_scrapper.description.xpath");
//        String locationXpath = ConfigLoader.getConfig().getString("scrapper.hotel_info_scrapper.location.id");
//        String starsXpath = ConfigLoader.getConfig().getString("scrapper.hotel_info_scrapper.stars.xpath");
//        String imagesXpath = ConfigLoader.getConfig().getString("scrapper.hotel_info_scrapper.images.xpath");
//
//        Map<String, Object> data = new HashMap<>();
//        String html = ApacheHttpClient.getHtml(url);
//        org.jsoup.nodes.Document doc = Jsoup.parse(html);
//        data.put("address", doc.select(addressXpath).text());
//        data.put("info", doc.select(infoXpath).text());
//        String s = doc.toString();
//        String lat = s.substring(s.indexOf("lat:") + 5, s.indexOf("lat:") + 16).replaceAll("[^\\d.]", "");
//        String lng = s.substring(s.indexOf("lng:") + 5, s.indexOf("lng:") + 16).replaceAll("[^\\d.]", "");
//        data.put("lat", NumberUtils.isNumber(lat) ? lat : "");
//        data.put("lng", NumberUtils.isNumber(lng) ? lng : "");
//        data.put("stars", doc.select(starsXpath).attr("class").split(" ")[1].replace("s", " ").trim());
//        Elements elementsByTag = doc.getElementsByClass("fotorama").tagName("img").get(0).getElementsByTag("img");
//        ArrayList<Document> images = new ArrayList<>();
//        for (Element element : elementsByTag) {
//            if (!element.attr("src").isEmpty()) {
//                images.add(new Document("src", element.attr("src")).append("title", element.attr("alt")));
//            }
//        }
//        data.put("images", images);
//        return data;
//    }

//    private static Map<String, String> prepareHotels(String url) {
//        FindIterable<Document> documents = MongodbClient.getCollection("hotel").find(new Document("name", "هتل پارسیان استقلال تهران")).projection(new Document("names", 1).append("name", 1));
//        Map<String, String> urls = new HashMap<>();
//        for (Document document : documents) {
//            String name = (String) document.get("name");
//            ArrayList<Document> results = (ArrayList<Document>) document.get("names");
//            for (Document result : results) {
//                if (result.get("OTA").equals("jabama")) {
//                    urls.put(name, String.format(url, (String) result.get("name")));
//                }
//            }
//        }
//        return urls;
//    }

    public void start() {
        System.out.println("hello from info");
    }

}
