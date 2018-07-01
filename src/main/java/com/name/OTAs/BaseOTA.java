package com.name.OTAs;

import com.name.models.Room;
import com.name.models.RoomType;
import com.name.models.ScrapInfo;
import com.name.util.ApacheHttpClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public abstract List<Room> getRoomsData(ScrapInfo scrapInfo, String city);

    protected Document getHtmlDocument(String url) {
        String html = ApacheHttpClient.getHtml(url);
        return Jsoup.parse(html);
    }

    protected Map<String, Integer> getRoomTypes(Set<RoomType> roomType) {
        Map<String, Integer> map = new HashMap<>();
        for (RoomType type : roomType) {
            map.put(type.getRoomName(), type.getType());
        }
        return map;
    }

    protected Elements getRoomElements(Document doc, String roomDiv) {
        return doc.getElementsByClass(roomDiv);
    }

}
