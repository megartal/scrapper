package com.name.scrappers;

import com.name.documents.Proxy;
import com.name.repositories.ProxyRepository;
import com.name.util.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Akbar
 * @DATE 8/8/2018.
 */
@Service
@Slf4j
@Profile("proxy")
public class ProxyList implements Scrapper {
    private final ProxyRepository proxyRepository;

    public ProxyList(ProxyRepository proxyRepository) {
        this.proxyRepository = proxyRepository;
    }

    @Override
    public void start() {
        List<Proxy> proxies = new ArrayList<>();
        String html = null;
        try {
            html = ApacheHttpClient.getHtml("https://free-proxy-list.net/", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(html);
        Elements tr = doc.getElementsByTag("tr");
        for (Element element : tr) {
            try {
                Proxy proxy = new Proxy();
                if (element.getElementsByTag("td").size() == 0)
                    continue;
                proxy.setIp(element.getElementsByTag("td").get(0).text());
                proxy.setPort(element.getElementsByTag("td").get(1).text());
                if (element.getElementsByTag("td").get(6).text().equals("yes")) {
                    proxy.setProtocol("https");
                } else {
                    proxy.setProtocol("http");
                }
                proxy.setStatus(true);
                proxies.add(proxy);
            } catch (Exception e) {
                continue;
            }
        }
        proxyRepository.deleteAll();
        proxyRepository.saveAll(proxies);
    }
}
