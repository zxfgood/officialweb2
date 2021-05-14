package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.Orderinfo;
import com.feeye.bean.Passergeninfo;
import com.feeye.util.FingerPrintUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author xs
 * @description 8L官网出票
 * @date 2019/10/10
 */
public class L8NewFlight {
    private static final Logger logger = LoggerFactory.getLogger(L8NewFlight.class);
    private static final int timeout = 60000;
    String dailiyunAccount = "feeyeapp:feeye789";
    String csrfToken = ""; //登录请求参数


    /**
     * 登录
     *
     * @param orderJson
     * @return cookie
     */
    private String login(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, Orderinfo orderJson) {
        CloseableHttpResponse response = null;
        InputStream is = null;
        String cookie = "";
        String order_id = orderJson.getId();
        String childrenUser = orderJson.getUsername();
        String username = orderJson.getOtheraccount().split("_")[0];
        String password = orderJson.getOtheraccount().split("_")[1];

//      sendOrderStatus(childrenUser, order_id, "进入首页");
        try {
            HttpHost host = new HttpHost("www.luckyair.net", 80, "http");
            HttpGet get = new HttpGet("www.luckyair.ne");
            get.setConfig(config);
            get.setHeader("Host", "www.luckyair.ne");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(get);
            //获取cookie
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer sb = new StringBuffer();
            if (CollectionUtils.isNotEmpty(listCookie)) {
                for (int i = 0; i < listCookie.size(); i++) {
                    if ("csrfToken".equals(listCookie.get(i).getName())) {
                        csrfToken = listCookie.get(i).getValue();
                    }
                    sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                    cookie = sb.toString();
                }
            }
            logger.info("进入首页获取的cookie" + cookie);
            response.close();

//          sendOrderStatus(childrenUser, order_id, "处理登录验证码");
            Long time = System.currentTimeMillis();
            get = new HttpGet("/api/validate/captcha?t" + time);
            RequestConfig defConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setStaleConnectionCheckEnabled(true)
                    .build();
            get.setConfig(config);
            get.setHeader("Host", "www.tianjin-air.com");
            get.setHeader("Cookie", cookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            response = httpclient.execute(host, get);

            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                if ("Set-Cookie".equals(headers[i].getName())) {
                    sb.append(headers[i].getValue().split(";")[0] + ";");
                }
            }
            sb.delete(sb.length() - 1, sb.length());
            System.out.println("请求登录验证码后的cookie:" + sb);
            cookie = sb.toString();

            String session = UUID.randomUUID().toString().replaceAll("-", "");
            String fileUri = "C://testImg//" + session + ".jpg";
            is = response.getEntity().getContent();
            OutputStream os = new FileOutputStream(fileUri);
            IOUtils.copy(is, os);
            is.close();
            os.close();


            //TODO 改为掉接口
            System.out.print("输入验证码:");
            Scanner scan = new Scanner(System.in);
            String result = scan.nextLine();
            response.close();


//          sendOrderStatus(childrenUser, order_id, "官网登录");
            String url = URLEncoder.encode("http://www.luckyair.net", "UTF-8");
            url = URLEncoder.encode(url, "UTF-8");
            HttpPost post = new HttpPost("/member/login.html?done=" + url);
            post.setConfig(config);
            String[] userAccount = orderJson.getAccount().split("_");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("username", userAccount[0]));
            nameValue.add(new BasicNameValuePair("password", userAccount[1]));
            nameValue.add(new BasicNameValuePair("captcha", result));
            nameValue.add(new BasicNameValuePair("_csrf", csrfToken));
            nameValue.add(new BasicNameValuePair("type", "nlogin"));

            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Referer", "http://www.tianjin-air.com/member/login.html");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Cookie", cookie);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("登录返回:" + back);
            logger.info(order_id + "登录返回:" + back);
            String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            if ("302".equals(statusCode) && back.contains("Redirecting to")) {
                logger.info(order_id + "登录成功");
                return cookie;
            }
            if (StringUtils.isNotEmpty(back) && back.contains("LOGIN_ERRORS")) {
                //登录失败
                Document parse = Jsoup.parse(back);
                String loginErrorBack = parse.getElementsByTag("script").get(4).childNodes().get(0).outerHtml();
                String loginErrorMsg = loginErrorBack.substring(loginErrorBack.indexOf("[") + 1, loginErrorBack.indexOf("]"));
                JSONObject json = JSON.parseObject(loginErrorMsg);
                String message = json.getString("message");
                System.out.println(message);
                logger.info(order_id + message);
                return "ERROR:" + message;
            } else {
                return "ERROR: 登录失败";
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return cookie;
    }


    /**
     * 查询航班信息
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param orderJson
     * @param verifyCode 验证码
     * @param retry      重试次数
     * @return
     */
    private String flightSearch(CloseableHttpClient httpclient, RequestConfig config, HttpClientContext context, String cookie,
                                Orderinfo orderJson, String verifyCode, int retry) {
        CloseableHttpResponse response = null;
        String order_id = orderJson.getId();
        String back = "";
        HttpHost host = new HttpHost("http://www.luckyair.net", 80, "http");
        try {
            String depCode = orderJson.getFlights().get(0).getDeparture();
            String arrCode = orderJson.getFlights().get(0).getArrival();
            String flightDate = orderJson.getFlights().get(0).getDepartureDate();

            //进入首页
            HttpGet get = new HttpGet("http://www.luckyair.net");
            get.setConfig(config);
            get.setHeader("Host", "www.luckyair.net");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            response = httpclient.execute(get);

            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                if ("set-Cookie".equalsIgnoreCase(headers[i].getName())) {
                    if (headers[i].getValue().contains("csrfToken")) {
                        csrfToken = headers[i].getValue().split(";")[0].split("=")[1];
                        cookie = headers[i].getValue();
                    }
                }
            }
            response.close();

            //查询航班
            JSONObject paramObj = new JSONObject();
            JSONObject passenger = new JSONObject();

            int AdtNum = 0;
            int CnnNum = 0;
            int InfNum = 0;
            for (Passergeninfo pb : orderJson.getPassengers()) {
                JSONObject passengersObj = new JSONObject();
                if ("成人".equals(pb.getPassengerType())) {
                    AdtNum++;
                }
                if ("儿童".equals(pb.getPassengerType())) {
                    CnnNum++;
                }
                if ("婴儿".equals(pb.getPassengerType())) {
                    InfNum++;
                }
            }
            passenger.put("ADT", AdtNum);
            passenger.put("CNN", CnnNum);
            passenger.put("INF", InfNum);
            passenger.put("PWD", 0);
            passenger.put("MWD", 0);

            JSONArray originDestinations = new JSONArray();
            JSONObject originDestinationsObj = new JSONObject();
            originDestinationsObj.put("depCode", depCode);
            originDestinationsObj.put("arrCode", arrCode);
            originDestinationsObj.put("flightDate", flightDate);
            originDestinations.add(0, originDestinationsObj);
            paramObj.put("tripType", "ONEWAY");
            paramObj.put("isInternational", false);
            paramObj.put("passenger", passenger);
            paramObj.put("originDestinations", originDestinations);
            paramObj.put("_csrf", csrfToken);
            //加密参数
            String desc = FingerPrintUtil.getDesc();
//            paramObj.put("desc", "coBPtm4BZy5Ly7E1arnlj8wpl7R6QT%2BSC8f6tWB051gXYl/elXICeY115jwY6dt2");
            paramObj.put("desc", desc);
            //查询航班验证码
            if (StringUtils.isNotEmpty(verifyCode)) {
                paramObj.put("vc", verifyCode);
            }
            HttpPost post = new HttpPost("http://www.luckyair.net/api/flight/search");
            post.setConfig(config);
            String s = "{\"tripType\":\"ONEWAY\",\"originDestinations\":[{\"depCode\":\"KMG\",\"arrCode\":\"LUM\",\"flightDate\":\"2019-10-15\"}],\"passenger\":{\"ADT\":1},\"desc\":\"coBPtm4BZy5Ly7E1arnlj8wpl7R6QT%2BSC8f6tWB051gXYl/elXICeY115jwY6dt2\",\"_csrf\":\"27pVALzS9A68DXtlG7JnZ8MQ\"}";
            StringEntity entity = new StringEntity(paramObj.toString(), Charset.forName("UTF-8"));
            System.out.println("查询航班参数:" + paramObj.toString());
            logger.info("查询航班参数:" + paramObj.toString());
            post.setEntity(entity);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.luckyair.net/flight/search.html");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(post, context);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("查询航班返回:" + back);
            logger.info("查询航班返回:" + back);

            if (back.contains("10001")) {
                if (retry < 2) {
                    retry++;
                    return flightSearch(httpclient, config, context, cookie, orderJson, "", retry);
                } else {
                    return "REEOR: 查询航班封IP,重试创单";
                }
            }

            if (back.contains("10000")) {
                //查询航班验证码
                get = new HttpGet("http://www.luckyair.net/hnatravel/imagecodeajax?v=2");
                get.setConfig(config);
                get.setHeader("Host", "www.luckyair.net");
                get.setHeader("Cookie", cookie);
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
                response = httpclient.execute(get);
                headers = response.getAllHeaders();
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < headers.length; i++) {
                    if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
                        buffer.append(headers[i].getValue() + ";");
                    }
                }
                cookie = buffer.toString() + cookie;
                String session = UUID.randomUUID().toString().replaceAll("-", "");
                String fileUri = "C://testImg//" + session + ".jpg";
                InputStream is = response.getEntity().getContent();
                OutputStream os = new FileOutputStream(fileUri);
                IOUtils.copy(is, os);
                try {
                    is.close();
                    os.close();
                } catch (Exception e) {
                    logger.error("error", e);
                }
                //识别查询航班验证码
                File imageFile = new File(fileUri);
                FileEntity fileEntity = new FileEntity(imageFile);
                String codeUrl = "http://127.0.0.1:1111";
                HttpPost codePost = new HttpPost(codeUrl);
                codePost.setEntity(fileEntity);
                response = httpclient.execute(codePost);
                verifyCode = EntityUtils.toString(response.getEntity());
                System.out.println("查询航班验证码:" + verifyCode);
                logger.info(order_id + "查询航班验证码:" + verifyCode);
                response.close();
                return flightSearch(httpclient, config, context, cookie, orderJson, verifyCode, retry);
            }
            if (back.contains("10002")) {
                return flightSearch(httpclient, config, context, cookie, orderJson, "", retry);
            }

            JSONObject jsonObject = JSON.parseObject(back);

        } catch (NoHttpResponseException e) {
            logger.error("error", e);
            return flightSearch(httpclient, config, null, "", orderJson, "", retry);
        } catch (SocketTimeoutException e) {
            logger.error("error", e);
            return "retry";
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
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
                "            \"arrival\": \"WUH\",\n" +
                "            \"cabin\": \"A\",\n" +
                "            \"departure\": \"KMG\",\n" +
                "            \"departureDate\": \"2019-11-13\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"8L9895\",\n" +
                "            \"price\": \"560.0\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"32550175\",\n" +
                "    \"idCardNo\": \"410781197310154713\",\n" +
                "    \"idCardType\": \"IDCARD\",\n" +
                "    \"ifUsedCoupon\": false,\n" +
                "    \"isOutticket\": \"false\",\n" +
                "    \"linkMan\": \"华勇\",\n" +
                "    \"matchCabin\": false,\n" +
                "    \"mobile\": \"13072660551\",\n" +
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
//                "        {\n" +
//                "            \"birthday\": \"1983-11-21 00:00:00+08:00\",\n" +
//                "            \"id\": \"37617004\",\n" +
//                "            \"idcard\": \"350781198311212039\",\n" +
//                "            \"passengerCardType\": \"身份证\",\n" +
//                "            \"passengerName\": \"梁邵平\",\n" +
//                "            \"passengerSex\": \"男\",\n" +
//                "            \"passengerType\": \"成人\"\n" +
//                "        },\n" +
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

        JSONObject jsonObject = JSON.parseObject(orderJson);
        Orderinfo orderinfo = jsonObject.toJavaObject(Orderinfo.class);
        L8NewFlight service = new L8NewFlight();

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
                .setDefaultRequestConfig(defaultRequestConfig)
                .setDefaultCookieStore(cookieStore)
                .setKeepAliveStrategy(myStrategy)
                .build();

        HttpClientContext context = new HttpClientContext();

        for (int i = 0; i < 1; i++) {
            Long startTime = System.currentTimeMillis();
            service.flightSearch(client, defaultRequestConfig, context, "", orderinfo, "", 0);
            long queryTmie = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("查询航班耗时：" + queryTmie + "s");
        }
    }
}
