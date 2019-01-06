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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Akbar
 * @DATE 1/6/2019.
 */
@Service
@Slf4j
@Profile("exteraHotelsJainjas")
public class JainjasImageDownloader implements Scrapper {
    public static final String SAMPLE_XLSX_FILE_PATH = "C:\\Users\\Alex\\Desktop\\hotels.xlsx";
    //    public static final String SAMPLE_XLSX_FILE_PATH = "/home/ara/temp/hotels.xlsx";
    public static final String filPath = "E:\\images\\";
    //    public static final String filPath = "/home/ara/temp/images2/";
    private HotelRepository hotelRepository;

    public JainjasImageDownloader(HotelRepository hotelRepository) {
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
                String html = ApacheHttpClient.getHtmlWithoutSSLCertificate("https://jainjas.com/" + hotelURL);
                Document doc = Jsoup.parse(html);
                Elements imageElements = doc.getElementsByClass("lazy");


                for (ScrapInfo scrapInfo : hotel.getScrapInfo()) {
                    if (scrapInfo.getOTAName().equals("jainjas")) {
                        scrapInfo.setHotelName(hotelURL);
                        hotel.getScrapInfo().add(scrapInfo);
                    }
                }
                boolean first = true;
                String firstAlt = "";
                for (Element imageElement : imageElements) {
                    String href = imageElement.getElementsByAttribute("data-src").text();
                    String alt = imageElement.getElementsByAttribute("alt").text();
                    String imageName = UUID.randomUUID().toString() + ".jpg";
                    InputStream in = ApacheHttpClient.getImageWithoutSSLCertificate(href);
                    Files.copy(in, Paths.get(filPath + imageName));
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
