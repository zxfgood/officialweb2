package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.feeye.bean.Orderinfo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
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
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author xs
 * @description
 * @date 2020/4/21
 */
public class SCLogin {
    private static final Logger logger = LoggerFactory.getLogger(GSCreateOrderService.class);
    //    private static String dailiyunAccount = "feeyeapp:feeye789";
    private static final String dailiyunAccount = "feeyedaili:feeye123";
    private static final int timeout = 15000;

    private String login(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
                         BasicCookieStore cookieStore) {
        CloseableHttpResponse response = null;
        String newCookie = "";
        try {
            //登录页面
            HttpGet get = new HttpGet("http://218.56.48.139:8084/login?service=http%3A%2F%2Fwww.shandongair.com.cn%2Fssoaction.shtml");
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "218.56.48.139:8084");
            get.setHeader("Referer", "http://218.56.48.139:8084/login?service=http%3A%2F%2Fwww.shandongair.com.cn%2Fssoaction.shtml");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            get.setHeader("Proxy-Authorization", "Basic " + org.apache.commons.codec.binary.Base64.encodeBase64String(dailiyunAccount.getBytes(StandardCharsets.UTF_8)));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(get);
            Header[] headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    newCookie += header.getValue() + ";";
                }
            }
            String jsessionId = "";
            if (StringUtils.isNotEmpty(newCookie)) {
                for (String c : newCookie.split(";")) {
                    if (c.contains("JSESSIONID")) {
                        jsessionId = c.split("=")[1];
                    }
                }
            }
            response.close();
            System.out.println("登录页面返回cookie: " + newCookie);

            //登录验证码
            get = new HttpGet("http://218.56.48.139:8084/image.jsp");
            //请求验证码不使用代理
            RequestConfig config = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .build();
            get.setConfig(config);
            get.setHeader("Host", "218.56.48.139:8084");
            get.setHeader("Referer", "http://218.56.48.139:8084/login?service=http%3A%2F%2Fwww.shandongair.com.cn%2Fssoaction.shtml");
            get.setHeader("Cookie", newCookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(get);
            String fileUri = "C://testImg//" + System.currentTimeMillis() + ".jpg";
            InputStream is = response.getEntity().getContent();
            OutputStream os = new FileOutputStream(fileUri);
            IOUtils.copy(is, os);
            is.close();
            os.close();
            response.close();

            //TODO 改为掉接口
            System.out.print("输入验证码:");
            Scanner scan = new Scanner(System.in);
            String resultCode = scan.nextLine();
            response.close();

            HttpPost post = new HttpPost("http://218.56.48.139:8084/login;jsessionid=" + jsessionId + "?service=http%3A%2F%2Fwww.shandongair.com.cn%2Fssoaction.shtml");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("_eventId", "submit"));
            nameValue.add(new BasicNameValuePair("code", resultCode));
            nameValue.add(new BasicNameValuePair("lt", "e1s1"));
            nameValue.add(new BasicNameValuePair("password", "feeye123"));
            nameValue.add(new BasicNameValuePair("username", "xs15897736493"));
            logger.info("login参数:" + nameValue.toString());
            System.out.println("login参数:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setConfig(defaultRequestConfig);
            post.setHeader("Host", "218.56.48.139:8084");
            post.setHeader("Referer", "http://218.56.48.139:8084/login?service=http%3A%2F%2Fwww.shandongair.com.cn%2Fssoaction.shtml");
            post.setHeader("Cookie", newCookie);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes(StandardCharsets.UTF_8)));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("login返回: " + back);
            logger.info("login返回: " + back);
            if (StringUtils.isNotEmpty(back) && back.contains("您提供的凭证有误")) {
                return "error: 用户名或者密码错误，请核对！";
            }
            headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    newCookie += header.getValue() + ";";
                }
            }
            System.out.println("登录后返回cookie: " + newCookie);

            headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    newCookie += header.getValue() + ";";
                }
            }
            String locationValue = "";
            org.apache.http.Header[] location = response.getHeaders("Location");
            // 提交订单后重定向请求
            for (int i = 0; i < location.length; i++) {
                locationValue = location[i].getValue();
                logger.info("login重定向1:" + locationValue);
            }
            response.close();
            defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout).setRedirectsEnabled(false).build();
            get = new HttpGet(locationValue);
            //ticket=
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "www.shandongair.com.cn");
            get.setHeader("Referer", "http://218.56.48.139:8084/login?service=http%3A%2F%2Fwww.shandongair.com.cn%2Fssoaction.shtml");
            get.setHeader("Cookie", newCookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            get.setHeader("Proxy-Authorization", "Basic " + org.apache.commons.codec.binary.Base64.encodeBase64String(dailiyunAccount.getBytes(StandardCharsets.UTF_8)));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(get);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info("login重定向1返回:" + back);
            if (StringUtils.isNotEmpty(back) && back.contains("验证码输入有错误")) {
                return null;
            }
            headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    newCookie += header.getValue() + ";";
                }
            }

            location = response.getHeaders("Location");
            // 提交订单后重定向请求
            for (int i = 0; i < location.length; i++) {
                locationValue = location[i].getValue();
                logger.info("login重定向2:" + locationValue);
            }
            response.close();
            //ssoaction
            get = new HttpGet(locationValue);
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "www.sda.cn");
            get.setHeader("Referer", "http://218.56.48.139:8084/login?service=http%3A%2F%2Fwww.shandongair.com.cn%2Fssoaction.shtml");
            get.setHeader("Cookie", newCookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            get.setHeader("Proxy-Authorization", "Basic " + org.apache.commons.codec.binary.Base64.encodeBase64String(dailiyunAccount.getBytes(StandardCharsets.UTF_8)));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(get);

            headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    newCookie += header.getValue() + ";";
                }
            }
            response.close();

            location = response.getHeaders("Location");
            // 提交订单后重定向请求
            for (int i = 0; i < location.length; i++) {
                locationValue = location[i].getValue();
                logger.info("login重定向2:" + locationValue);
            }
//            //service

//            get = new HttpGet(locationValue);
//            get.setConfig(defaultRequestConfig);
//            get.setHeader("Host", "218.56.48.439:8084");
//            get.setHeader("Referer", "http://218.56.48.139:8084/login?service=http%3A%2F%2Fwww.shandongair.com.cn%2Fssoaction.shtml");
//            get.setHeader("Cookie", newCookie);
//            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//            get.setHeader("Proxy-Authorization", "Basic " + org.apache.commons.codec.binary.Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
//            get.setHeader("Proxy-Connection", "keep-alive");
//            response = httpclient.execute(get);
//
//            back = EntityUtils.toString(response.getEntity(), "utf-8");
//            logger.info("登录service:"+back);
//
//            location = response.getHeaders("Location");
//            // 提交订单后重定向请求
//            for (int i = 0; i < location.length; i++) {
//                locationValue = location[i].getValue();
//                logger.info("login重定向2:" + locationValue);
//            }

            //ticket
            get = new HttpGet(locationValue);
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "www.sda.cn");
            get.setHeader("Referer", "http://218.56.48.139:8084/login?service=http%3A%2F%2Fwww.shandongair.com.cn%2Fssoaction.shtml");
            get.setHeader("Cookie", newCookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            get.setHeader("Proxy-Authorization", "Basic " + org.apache.commons.codec.binary.Base64.encodeBase64String(dailiyunAccount.getBytes(StandardCharsets.UTF_8)));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(get);

            back = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info("登录service:" + back);

            return newCookie;

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
        return newCookie;
    }

    private boolean checkIsLogin(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie) {
        CloseableHttpResponse response = null;
        boolean loginStatus = false;
        try {
            HttpGet get = new HttpGet("http://218.56.48.139:8083/front/member/index.jsp");
            get.setConfig(defaultRequestConfig);
            get.setHeader("Host", "www.sda.cn");
            get.setHeader("Referer", "http://218.56.48.139:8084/login?service=http%3A%2F%2F218.56.48.139%3A8083%2Ffront%2Fmember%2Findex.jsp");
            get.setHeader("Cookie", cookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            get.setHeader("Proxy-Authorization", "Basic " + org.apache.commons.codec.binary.Base64.encodeBase64String(dailiyunAccount.getBytes(StandardCharsets.UTF_8)));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            if (StringUtils.isNotEmpty(back) && back.contains("/memberCenter/memberinfo.do?method=forwardupdate")) {
                get = new HttpGet("http://218.56.48.139:8083/memberCenter/memberinfo.do?method=forwardupdate");
                get.setConfig(defaultRequestConfig);
                get.setHeader("Host", "www.sda.cn");
                get.setHeader("Referer", "http://218.56.48.139:8083/front/member/index.jsp");
                get.setHeader("Cookie", cookie);
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
                get.setHeader("Proxy-Authorization", "Basic " + org.apache.commons.codec.binary.Base64.encodeBase64String(dailiyunAccount.getBytes(StandardCharsets.UTF_8)));
                get.setHeader("Proxy-Connection", "keep-alive");
                response = httpclient.execute(get);
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println("checkIsLogin返回: " + back);
                if (StringUtils.isNotEmpty(back) && back.contains("您好")) {
                    loginStatus = true;
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
        return loginStatus;
    }


    // 判断访问目标网站是否需要代理
    private boolean isNeedProxy(String urlAddres) {
        boolean result = true;
        URL url;
        try {
            url = new URL(urlAddres);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);
            int i = connection.getContentLength();
            if (i > 0) {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) {
        String orderJson = "{\n" +
                "    \"account\": \"15897736493_feeye123\",\n" +
                "    \"airline\": \"GS\",\n" +
                "    \"bankType\": \"CMB\",\n" +
                "    \"childrenMobile\": \"\",\n" +
                "    \"code\": \"\",\n" +
                "    \"codePassword\": \"\",\n" +
                "    \"creditNo\": \"5187188380513124\",\n" +
                "    \"cvv\": \"369\",\n" +
                "    \"drawerType\": \"GW\",\n" +
                "    \"expireMonth\": \"05\",\n" +
                "    \"expireYear\": \"2028\",\n" +
                "    \"flights\": [\n" +
                "        {\n" +
                "            \"airline\": \"GS\",\n" +
                "            \"arrival\": \"CAN\",\n" +
                "            \"cabin\": \"N\",\n" +
                "            \"departure\": \"TSN\",\n" +
                "            \"departureDate\": \"2020-05-31\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"GS7895\",\n" +
                "            \"price\": \"938.0\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"32550175\",\n" +
                "    \"idCardNo\": \"410781197310154713\",\n" +
                "    \"idCardType\": \"IDCARD\",\n" +
                "    \"ifUsedCoupon\": false,\n" +
                "    \"isOutticket\": \"false\",\n" +
                "    \"linkMan\": \"华勇\",\n" +
                "    \"matchCabin\": false,\n" +
                "    \"mobile\": \"15897736493\",\n" +
                "    \"orderNo\": \"406786710\",\n" +
                "    \"orderTime\": \"2019-03-11 00:12:13\",\n" +
                "    \"otheraccount\": \"fyyzm1_wo4feizhiyou\",\n" +
                "    \"ownername\": \"王涛\",\n" +
                "    \"passengers\": [\n" +
                "        {\n" +
                "            \"birthday\": \"1984-07-19 00:00:00+08:00\",\n" +
                "            \"id\": \"37617002\",\n" +
                "            \"idcard\": \"411425198407198112\",\n" +
                "            \"passengerCardType\": \"身份证\",\n" +
                "            \"passengerName\": \"邵玉风\",\n" +
                "            \"passengerSex\": \"男\",\n" +
                "            \"passengerType\": \"成人\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"birthday\": \"1983-11-30 00:00:00+08:00\",\n" +
                "            \"id\": \"37617003\",\n" +
                "            \"idcard\": \"350802198311306035\",\n" +
                "            \"passengerCardType\": \"身份证\",\n" +
                "            \"passengerName\": \"林炜\",\n" +
                "            \"passengerSex\": \"男\",\n" +
                "            \"passengerType\": \"成人\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"payType\": \"xyk\",\n" +
                "    \"payerMobile\": \"18530203144\",\n" +
                "    \"qiangpiao\": \"2\",\n" +
                "    \"username\": \"fzybian\",\n" +
                "    \"ytype\": \"\"\n" +
                "}";

        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(orderJson);
        Orderinfo orderinfo = jsonObject.toJavaObject(Orderinfo.class);

        SCLogin scLogin = new SCLogin();

        //单独测试查询航班接口
        String ip_port = DailiyunService.getRandomNewDailiIp(50);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setProxy(dailiyunProxy)
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
                .setDefaultRequestConfig(defaultRequestConfig)
                .setDefaultCookieStore(cookieStore)
                .setKeepAliveStrategy(myStrategy)
                .build();

        System.out.println(scLogin.isNeedProxy("https://www.hbhk.com.cn"));

        String cookie = scLogin.login(client, defaultRequestConfig, cookieStore);
        System.out.println("是否登录成功: " + scLogin.checkIsLogin(client, defaultRequestConfig, cookie));

    }
}