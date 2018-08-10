package com.name.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @Author Akbar
 * @DATE 8/10/2018.
 */
public class SnapptripImageDownloader {
    public static void main(String[] args) {
        String filPath = "C:\\Users\\Alex\\Desktop\\images\\";
        String city = "tehran";
        String h = ApacheHttpClient.getHtml("https://www.snapptrip.com/%D8%B1%D8%B2%D8%B1%D9%88-%D9%87%D8%AA%D9%84/%D8%AA%D9%87%D8%B1%D8%A7%D9%86/%D8%A7%D9%82%D8%A7%D9%85%D8%AA%DA%AF%D8%A7%D9%87-%D8%B3%D9%86%D8%AA%DB%8C-%D8%A7%D8%B1%DA%AF-%D8%B3%DB%8C-%D8%B3%D9%86%DA%AF%D8%A7%D9%86", null);
        Document doc = Jsoup.parse(h);
        Elements imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {
            if (img.getElementsByAttribute("data-lazy").size() != 0) {
                String src = img.getElementsByAttribute("data-lazy").get(0).attr("data-lazy").replace("https://cdnsnapptrip.com/images", "").replace("/", "-");
                System.out.println(city + src);
                System.out.println(img.getElementsByAttribute("data-lazy").attr("title"));
//                InputStream in = ApacheHttpClient.getSnapptripImage(img.getElementsByAttribute("data-lazy").get(0).attr("data-lazy"));
//                try {
//                    Files.copy(in, Paths.get(filPath + city + src));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

        }

    }
}
