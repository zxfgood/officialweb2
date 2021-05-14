package com.feeye.util;

import com.feeye.service.processor.DailiyunService;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author xs
 * @description
 * @date 2019/10/14
 */
public class httpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(httpClientUtil.class);

    public static void main(String[] args) throws IOException {
//        httpClientUtil.isNeedProxy();
        httpClientUtil.httpClientTest();

    }

    public static void httpClientTest () throws IOException {
        int timeout = 30000;

        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            //初始化SSL连接
            sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            logger.error("error", e);
        }

        BasicScheme proxyAuth = new BasicScheme();
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e) {
            logger.error("error",e);
        }
        //代理云ip
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        HttpHost proxy = new HttpHost(proxyIp, proxyPort, "http");
        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(proxy, proxyAuth);

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(proxy),
                new UsernamePasswordCredentials("feeyeapp", "feeye789"));

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setProxy(proxy)
                .build();

        String dailiyunAccount = "feeyeapp:feeye789";

        HttpClientContext context = HttpClientContext.create();
        context.setAuthCache(authCache);
        context.setRequestConfig(defaultRequestConfig);
        context.setCredentialsProvider(credsProvider);

        CloseableHttpClient httpclient = HttpClients.custom()
//                .setDefaultRequestConfig(defaultRequestConfig)
                .setSSLSocketFactory(sslsf)
                .setDefaultCredentialsProvider(credsProvider)
                .build();


        HttpPost post = new HttpPost("https://wx.flycua.com/h5/pip/book/flightSearch.json");
        post.setConfig(defaultRequestConfig);
        post.setHeader("Host", "wx.flycua.com");
        post.setHeader("Referer", "https://wx.flycua.com/h5/");
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
        post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
        post.setHeader("Proxy-Connection", "keep-alive");
        String param = "{\"tripType\":\"OW\",\"orgCode\":\"PKX\",\"dstCode\":\"SHA\",\"takeoffdate1\":\"2019-10-18\",\"takeoffdate2\":\"\"}";
        StringEntity ent = new StringEntity(param, Charset.forName("UTF-8"));
        post.setEntity(ent);
        CloseableHttpResponse response = httpclient.execute(post);
        String  back = EntityUtils.toString(response.getEntity(),"utf-8");
        System.out.println(back);

    }

    private static boolean isNeedProxy() {
        boolean result = true;
        URL url;
        try {
            url = new URL("http://www.fuzhou-air.cn/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);
            // int i = connection.getResponseCode();
            int i = connection.getContentLength();
            if (i > 0) {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
