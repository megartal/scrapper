//package com.name.OTAs;
//
//import com.name.models.CrawledData;
//import com.name.models.OTAData;
//import com.name.models.Price;
//import com.name.util.Crawler;
//import com.name.util.DateConverter;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
///**
// * @Author Akbar
// * @DATE 6/10/2018.
// */
//@Service
//@Profile({"eghamat24"})
//@Getter
//@Slf4j
//public class Eghamat24 extends BaseOTA {
//
//    @Value("${eghamat24.urlPattern}")
//    private String urlPattern;
//    @Value("${jabama.redirect}")
//    private String redirect;
//    @Value("${jabama.sleep}")
//    private int sleep;
//    private final Crawler crawler;
//
//    private String name = "eghamat24";
//
//    public Eghamat24(Crawler crawler) {
//        this.crawler = crawler;
//    }
//
//    @Override
//    public Map<String, CrawledData> getRoomsData(String calledName, String city) {
//        Map<String, CrawledData> crawledDataList = new HashMap<>();
//        Document htmlDocument = getHtmlDocument(createURL(calledName, city));
//        Elements roomElements = getRoomElements(htmlDocument, );
//        for (Element roomElement : roomElements) {
//            Document room = new Document();
////            roomAndPrice.put(roomElement.getElementsByClass(getRoomNameSelector()).text(),
////                    roomElement.getElementsByClass(getRoomPriceSelector()).text());
//            String roomID = roomElement.attr("id").replace("room_", "");
//            String html = ApacheHttpClient.getHtml(String.format(getWebservice(), roomID));
//            JSONObject jsonObject = (JSONObject) new JSONObject(html).get("data");
//            JSONArray prices = jsonObject.optJSONArray("room_availablity");
//            List<Document> doc = new ArrayList<>();
//            String roomName = jsonObject.getString("room_title");
//            for (int i = 0; i < prices.length(); i++) {
//                Document document = new Document();
//                JSONObject obj = prices.getJSONObject(i);
//
//                document.append("date", DateConverter.getShamsidate(convertStringToDate((String) obj.get("date"))));
//                document.append("val", obj.get("price_off"));
//                document.append("avail", (int) obj.get("available_quantity") > 0 ? true : false);
//                doc.add(document);
//            }
//            room.append("price", doc).append("redirect", getRedirectURL()).append("name", getName());
//            OTARooms.put(roomName, room);
//        }
//        return OTARooms;
//    }
//
//    private String createURL(String hotel, String city) {
//        String url = urlPattern.replace("cityReplace", city);
//        return url.replace("hotelReplace", hotel);
//    }
//
//    @Override
//    public void run() {
//        while (true) {
//            try {
//                crawler.crawl(this);
//                Thread.sleep(sleep);
//            } catch (Exception e) {
//                log.error("error in eghamat24: " + e.getMessage());
//            }
//        }
//    }
//}
