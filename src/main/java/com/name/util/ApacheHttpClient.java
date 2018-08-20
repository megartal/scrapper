package com.name.util;

import com.name.documents.Proxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

import javax.net.ssl.*;
import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Author Akbar
 * @DATE 5/19/2018.
 */
@Slf4j
public class ApacheHttpClient {
    public static String getHtmlUsingProxy(String url, Proxy proxy) {
        int timeout = 100000;
        int managerTimeout = 100000;
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setSocketTimeout(timeout)
                .setConnectionRequestTimeout(managerTimeout)
                .build();
        HttpHost prxy = new HttpHost(proxy.getIp(), Integer.parseInt(proxy.getPort()), HttpHost.DEFAULT_SCHEME_NAME);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(prxy);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                .setConnectTimeout(timeout * 2)
                .setSocketTimeout(timeout * 2)
                .setConnectionRequestTimeout(managerTimeout * 2)
                .build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        try {
            return IOUtils.toString(httpclient.execute(httpGet).getEntity().getContent(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getHtml(String url, Proxy proxy) {
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

    public static String postRequest(String url, String params) throws UnsupportedEncodingException {
        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            BufferedReader in;
            // add reuqest header
            con.setRequestMethod("POST");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            if (responseCode >= 400)
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            else
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String getHtmlWithoutSSLCertificate(String s) {
        // configure the SSLContext with a TrustManager
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            URL url = new URL(s);
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
            String html = IOUtils.toString(conn.getInputStream(), "UTF-8");
            conn.disconnect();
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getSnapptripImage(String s) {
        CloseableHttpClient httpClientBuilder = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(s);
        try {
            return httpClientBuilder.execute(httpGet).getEntity().getContent();
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

    public static InputStream getImageWithoutSSLCertificate(String s) {
        // configure the SSLContext with a TrustManager
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            URL url = new URL(s);
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
            return conn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
