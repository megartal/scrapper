package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.Image;
import com.name.models.ScrapInfo;
import com.name.repositories.hotel.HotelRepository;
import com.name.util.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Akbar
 * @DATE 1/3/2019.
 */
@Service
@Slf4j
@Profile("exteraHotels")
public class IranHotelOnlineImageDownloader implements Scrapper {

    //    public static final String SAMPLE_XLSX_FILE_PATH = "C:\\Users\\Alex\\Desktop\\hotels.xlsx";
    public static final String SAMPLE_XLSX_FILE_PATH = "/home/ara/temp/hotels.xlsx";
    //    public static final String filPath = "E:\\images\\";
    public static final String filPath = "/home/ara/temp/images1/";
    private HotelRepository hotelRepository;

    public IranHotelOnlineImageDownloader(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public void start() {
        try {
            Map<String, String> excelHotels = getData();
            List<Hotel> newHotels = hotelRepository.findByMainImage("hello");
            for (Hotel hotel : newHotels) {
                String hotelURL = excelHotels.get(hotel.getName());
                if (hotelURL == null || hotelURL.isEmpty())
                    continue;
                hotel.getImages().clear();
                String html = ApacheHttpClient.selfSignedHttpClient("https://www.iranhotelonline.com/hotels/" + hotelURL);
                Document doc = Jsoup.parse(html);
                Element fotorama = doc.getElementsByClass("fotorama").get(0);
                Elements aTag = fotorama.getElementsByTag("a");
                for (ScrapInfo scrapInfo : hotel.getScrapInfo()) {
                    if (scrapInfo.getOTAName().equals("iranHotelOnline")) {
                        scrapInfo.setHotelName(hotelURL);
                        hotel.getScrapInfo().add(scrapInfo);
                    }
                }
                boolean first = true;
                String firstAlt = "";
                for (Element element : aTag) {
                    String href = "https://www.iranhotelonline.com" + element.getElementsByAttribute("href").attr("href");
                    String alt = element.getElementsByAttribute("alt").attr("alt");
                    String imageName = UUID.randomUUID().toString() + ".jpg";
                    ApacheHttpClient.selfSignedHttpClientForImageDownload(href, filPath, imageName);
                    if (first) {
                        hotel.setMainImage(imageName);
                        firstAlt = alt;
                        first = false;
                    } else {
                        hotel.getImages().add(new Image(imageName, alt));
                    }
                    Thread.sleep(1000);
                }
                hotel.getImages().add(new Image(hotel.getMainImage(), firstAlt));
                hotel.getName().trim();
                hotelRepository.save(hotel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private Map<String, String> getData() throws IOException, InvalidFormatException {
        Map<String, String> hotels = new HashMap<>();
        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));
        Sheet hotelsSheet = workbook.getSheet("hotels");
        for (Row hotel : hotelsSheet) {
            String name = hotel.getCell(0).toString();
            String url = hotel.getCell(1).toString();
            hotels.put(name, url);
        }
        return hotels;
    }
}
