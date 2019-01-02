package com.name.util;

import com.name.documents.Proxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.*;

/**
 * @Author Akbar
 * @DATE 5/19/2018.
 */
@Slf4j
public class ApacheHttpClient {
    private static final ExecutorService THREADPOOL
            = Executors.newCachedThreadPool();

    private static <T> T call(Callable<T> c, long timeout, TimeUnit timeUnit) throws Exception {
        FutureTask<T> t = new FutureTask<T>(c);
        THREADPOOL.execute(t);
        return t.get(timeout, timeUnit);
    }

    public static String getHtmlUsingProxy(String url, Proxy proxy) throws Exception {
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
            return call(new Callable<String>() {
                public String call() throws Exception {
                    return IOUtils.toString(httpclient.execute(httpGet).getEntity().getContent(), "UTF-8");
                }
            }, 1, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.info("https get timeout exception.");
            throw e;
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static String getHtml(String url, Proxy proxy) throws Exception {
        CloseableHttpClient httpClientBuilder = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        try {
            return IOUtils.toString(httpClientBuilder.execute(httpGet).getEntity().getContent(), "UTF-8");
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                httpClientBuilder.close();
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static String postRequest(String url, String params) throws Exception {
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
            throw e;
        }
    }

    public static String httpsPostRequest(String url, String params) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

        try {

            HttpPost request = new HttpPost(url);
            StringEntity entity = new StringEntity(params);
            request.addHeader("content-type", "application/json");
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw ex;
        } finally {
            httpClient.close();
        }
    }

    public static String getHtmlWithoutSSLCertificate(String s) throws Exception {
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
            throw e;
        }
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

    public static InputStream getImageWithoutSSLCertificate(String s) throws Exception {
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
            throw e;
        }
    }

    public static String simpleConnection(String url) throws IOException {
        StringBuilder source = new StringBuilder();
        URL url1 = new URL(url);
        HttpURLConnection c = (HttpURLConnection) url1.openConnection();
        BufferedInputStream in = new BufferedInputStream(c.getInputStream());
        Reader r = new InputStreamReader(in);

        int i;
        while ((i = r.read()) != -1) {
            source.append((char) i);
        }
        return source.toString();
    }

    public static String selfSignedHttpClient(String url) {
        try (CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {
            HttpGet httpget = new HttpGet(url);
            return IOUtils.toString(httpclient.execute(httpget).getEntity().getContent(), "UTF-8");
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void selfSignedHttpClientForImageDownload(String url, String filePath, String imageName) {
        try (CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {
            HttpGet httpget = new HttpGet(url);
            InputStream in = httpclient.execute(httpget).getEntity().getContent();
            Files.copy(in, Paths.get(filePath + imageName));
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CloseableHttpClient createAcceptSelfSignedCertificateClient() throws
            KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, (chain, authType) -> true);
        SSLConnectionSocketFactory sslsf = new
                SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
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
