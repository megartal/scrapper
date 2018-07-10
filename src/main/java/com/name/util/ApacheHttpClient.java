package com.name.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

    public static String postRequest(String url, String body) throws UnsupportedEncodingException {
        CloseableHttpClient httpClientBuilder = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity(body);
//        StringEntity params =new StringEntity("{\"placeId\":\"%s\",\"from\":\"%s\",\"to\":\"%s\"} ");
        httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
        httpPost.setEntity(params);
        try {
            return IOUtils.toString(httpClientBuilder.execute(httpPost).getEntity().getContent(), "UTF-8");
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
