package com.name.scrappers;
//
//import com.diringo.client.ApacheHttpClient;
//import com.diringo.common.ConfigLoader;
//import org.apache.commons.lang.text.StrSubstitutor;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;

/**
 * Created by Akbar on 2/18/2018.
 */
public class HotelListScrapper {
//    private List<Map<String, String>> hotels;
//    private List<String> cities;
//    private int resultInEachPage = ConfigLoader.getConfig().getInt("scrapper.hotel_name_scrapper.resultsInEachPage");
//    private String url = ConfigLoader.getConfig().getString("scrapper.hotel_name_scrapper.url");
//    private String selector = ConfigLoader.getConfig().getString("scrapper.hotel_name_scrapper.selector");
//
//    public HotelListScrapper(List<String> cities) {
//        this.cities = cities;
//    }
//
//    public List<String> getCities() {
//        return cities;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public String getSelector() {
//        return selector;
//    }
//
//    public void setCities(List<String> cities) {
//        this.cities = cities;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public void setSelector(String selector) {
//        this.selector = selector;
//    }
//
//    public int getResultInEachPage() {
//        return resultInEachPage;
//    }
//
//    public List<Map<String, String>> getHotelNames() {
//        List<Map<String, String>> hotels = new ArrayList<>();
//        for (String rawCity : getCities()) {
//            String city = null;
//            try {
//                city = URLEncoder.encode(rawCity, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            int pageNum = 1;
//            while (true) {
//                String result = ApacheHttpClient.getHtml(createURL(city, pageNum));
//                Document doc = Jsoup.parse(result);
//                if (doc.select(String.format(getSelector(), 1)) == null || doc.select(String.format(getSelector(), 1)).html().isEmpty()) {
//                    break;
//                } else {
//                    for (int i = 1; i <= getResultInEachPage(); i++) {
//                        String hotelName = doc.select(String.format(getSelector(), i)).html();
//                        if (hotelName != "" && hotelName != null && !hotelName.isEmpty()) {
//                            //hotelName.replace(rawCity, "").trim();
//                            Map<String, String> hotel = new HashMap<>();
//                            hotel.put(rawCity, hotelName);
//                            hotels.add(hotel);
//                            System.out.println("city: " + rawCity + ", hotel: " + hotelName);
//                        } else {
//                            System.out.println("is empty!");
//                        }
//                    }
//                }
//                pageNum++;
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return hotels;
//    }
//
//    private String createURL(String city, int page) {
//        Map<String, String> map = new HashMap<>();
//        map.put("city", city);
//        map.put("page", page + "");
//        return StrSubstitutor.replace(getUrl(), map);
//    }
}
