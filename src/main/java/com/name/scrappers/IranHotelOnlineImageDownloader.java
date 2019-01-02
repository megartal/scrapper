package com.name.scrappers;

import com.name.documents.Hotel;
import com.name.models.Image;
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
    public static final String filPath = "/home/ara/temp/images";
    private HotelRepository hotelRepository;

    public IranHotelOnlineImageDownloader(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public void start() {
        try {
            Map<String, String> excelHotels = getData();
            for (Map.Entry<String, String> entry : excelHotels.entrySet()) {
                Hotel hotel = hotelRepository.findByName(entry.getKey());
                hotel.getImages().clear();
                String html = ApacheHttpClient.selfSignedHttpClient("https://www.iranhotelonline.com/hotels/" + entry.getValue());
                Document doc = Jsoup.parse(html);
                Element fotorama = doc.getElementsByClass("fotorama").get(0);
                Elements aTag = fotorama.getElementsByTag("a");
                for (Element element : aTag) {
                    String href = "https://www.iranhotelonline.com" + element.getElementsByAttribute("href").attr("href");
                    String alt = element.getElementsByAttribute("alt").attr("alt");
                    String imageName = UUID.randomUUID().toString() + ".jpg";
                    ApacheHttpClient.selfSignedHttpClientForImageDownload(href, filPath, imageName);
                    hotel.getImages().add(new Image(imageName, alt));
                    Thread.sleep(3000);
                }
                hotelRepository.deleteByName(hotel.getName());
                hotel.setId(UUID.randomUUID().toString());
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
