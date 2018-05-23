package com.name.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * @Author Akbar
 * @DATE 5/19/2018.
 */
public class ApacheHttpClient {
    public static String getHtml(String url) {
        CloseableHttpClient httpClientBuilder = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        try {
            return IOUtils.toString(httpClientBuilder.execute(httpGet).getEntity().getContent(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClientBuilder.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
