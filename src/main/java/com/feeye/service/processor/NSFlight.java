package com.feeye.service.processor;

import com.feeye.util.MD5Util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xs
 * @description
 * @date 2020/4/23
 */
public class NSFlight {
    private static final Logger logger = LoggerFactory.getLogger(GSCreateOrderService.class);
    private static String dailiyunAccount = "feeyeapp:feeye789";
    private static int timeout = 10000;

    private String queryFlight(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig) {
        CloseableHttpResponse response = null;
        String back = "";
        String dep = "SJW";
        String arr = "HGH";
        String flightDate = "2020-05-30";

        HttpHost host = new HttpHost("www.hbhk.com.cn", 443, "https");
        try {
            String v = "6.1";
            float version = Float.parseFloat(v) / 10;
            //登录页面
            HttpGet get = new HttpGet("https://www.hbhk.com.cn");
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "www.hbhk.com.cn");
            get.setHeader("Referer", "www.hbhk.com.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT +" + version + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(get);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            Document doc = Jsoup.parse(back);
            if (StringUtils.isNotEmpty(back) && !back.contains("html")) {
                System.out.println("NS首页返回: " + back);
            }

            String url = doc.getElementsByAttributeValue("rel", "preload").get(3).attr("href");
            Element swiper = doc.getElementsByAttributeValue("class", "swiper-wrapper").get(0);
            String imgOneUrl = swiper.getElementsByAttributeValue("class", "swiper-slide").get(0).getElementsByTag("img").attr("src");
            String imgTwoUrl = swiper.getElementsByAttributeValue("class", "swiper-slide").get(1).getElementsByTag("img").attr("src");
            String imgThreeUrl = swiper.getElementsByAttributeValue("class", "swiper-slide").get(2).getElementsByTag("img").attr("src");

            //请求图片
            get = new HttpGet(imgOneUrl);
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "www.hbhk.com.cn");
            get.setHeader("Referer", "www.hbhk.com.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT +" + version + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, get);
            response.close();

            get = new HttpGet(imgTwoUrl);
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "www.hbhk.com.cn");
            get.setHeader("Referer", "www.hbhk.com.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT +" + version + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, get);
            response.close();

            get = new HttpGet(imgThreeUrl);
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "www.hbhk.com.cn");
            get.setHeader("Referer", "www.hbhk.com.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT +" + version + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, get);
            response.close();

            get = new HttpGet(url);
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "www.hbhk.com.cn");
            get.setHeader("Referer", "www.hbhk.com.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT +" + version + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, get);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            String device_code = "";
            try {
                device_code = back.split("device_code:\"")[1].split("\"")[0];
            } catch (Exception e) {
                logger.error("获取device_code异常", e);
            }
            if (StringUtils.isEmpty(device_code)) {
//                requestRes.put("content", "device_code没拿到");
//                return requestRes;
            }
            String searchStr = dep + "1" + arr + flightDate.replace("-", "").substring(4) + "buyticket1.4.0";
            String token = MD5Util.getMD5(searchStr).toUpperCase();
            response.close();


            HttpPost post = new HttpPost("/mid_website/system/init");
            String json = "{\"in_json\":{\"citytimestamp\":\"\"},\"common_para\":{\"device_code\":\"" + device_code + "\",\"channel\":\"web\",\"device_type\":\"web\",\"version\":\"1.4.0\"}}";
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("json", json));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setConfig(defaultRequestConfig);
            post.setHeader("Host", "www.hbhk.com.cn");
            post.setHeader("Referer", "www.hbhk.com.cn");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT +" + version + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
//            System.out.println("init: " + back);
            logger.info("init: " + back);
            response.close();

            post = new HttpPost("/mid_website/flight/queryRecommendedRouteList");
            json = "{\"in_json\":{},\"common_para\":{\"device_code\":\"" + device_code + "\",\"channel\":\"web\",\"device_type\":\"web\",\"version\":\"1.4.0\"}}";
            nameValue.clear();
            nameValue.add(new BasicNameValuePair("json", json));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setConfig(defaultRequestConfig);
            post.setHeader("Host", "www.hbhk.com.cn");
            post.setHeader("Referer", "www.hbhk.com.cn");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
//            post.setHeader("Cookie", "");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT +" + version + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
//            System.out.println("init: " + back);
            response.close();


            post = new HttpPost("/mid_website/flight/queryFlight");
            json = "{\"in_json\":{\"departureCiry\":\"" + dep + "\",\"depsName\":\"" + "石家庄" + "\",\"arrivalCity\":\"" + arr + "\",\"arrName\":\"" + "杭州" + "\",\"adtNum\":\"1\",\"chdNum\"" +
                    ":\"0\",\"infNum\":\"0\",\"type\":\"buyticket\",\"takeOffDate\":\"" + flightDate.replaceAll("-", "") + "\",\"\":null,\"token\":\"" + token + "\"" +
                    "},\"common_para\":{\"device_code\":\"" + device_code + "\",\"channel\":\"web\",\"device_type\":\"web\",\"version\":\"1.4.0\"}}";
            nameValue.clear();
            nameValue.add(new BasicNameValuePair("json", json));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setConfig(defaultRequestConfig);
            post.setHeader("Host", "www.hbhk.com.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT +" + version + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("NS查航班返回: " + result);

        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e2) {
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        //单独测试查询航班接口
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
//                .setProxy(dailiyunProxy)
                .build();

        BasicCookieStore cookieStore = new BasicCookieStore();
        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator
                        (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase
                            ("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return 60 * 1000;//如果没有约定，则默认定义时长为60s
            }
        };
        CloseableHttpClient client = HttpClients.custom()
                //设置代理
                .setRoutePlanner(new DefaultProxyRoutePlanner(dailiyunProxy))
                .setDefaultCookieStore(cookieStore)
                .setKeepAliveStrategy(myStrategy)
                .build();

        NSFlight nsFlight = new NSFlight();
        nsFlight.queryFlight(client, defaultRequestConfig);

    }
}
