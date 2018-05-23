package com.name.OTAs;

import com.name.models.CrawledData;
import com.name.util.ApacheHttpClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author Akbar
 * @DATE 5/19/2018.
 */
@Getter
@Setter
@Service
@Slf4j
public abstract class BaseOTA implements OTA {
    private String name;
    private String urlToCrawl;

    public abstract Map<String, CrawledData> getRoomsData(String calledName, String city);

    protected Document getHtmlDocument(String url) {
        String html = ApacheHttpClient.getHtml(url);
        return Jsoup.parse(html);
    }

    protected Elements getRoomElements(Document doc, String roomDiv) {
        return doc.getElementsByClass(roomDiv);
    }

}
