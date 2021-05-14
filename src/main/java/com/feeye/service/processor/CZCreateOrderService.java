package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.CreateOrderInfo;
import com.feeye.bean.OrderJson;
import com.feeye.bean.Orderinfo;
import com.feeye.bean.Passergeninfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 创建南航订单
 * @date: 2019/03/06 11:13
 */
public class CZCreateOrderService {
    private static final Logger log = LoggerFactory.getLogger(CZCreateOrderService.class);
    private static final int timeout = 60000;
    private static Map<String, String> cookieMap = new HashMap<String, String>();

    //创建订单
    public String StartCreateOrder(String orderJson, int retryCount) {
        String backStr = "";
        String cookie = "";
        // String cancelUrl = PropertiesUtils.getPropertiesValue("config", "cancelUrl");
        String cancelUrl = "";
        if (StringUtils.isEmpty(orderJson)) {
            return "ERROR:数据不完整";
        }
        log.info("传过来的订单信息" + orderJson);

        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            //初始化SSL连接
            sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            log.error("error", e1);
        }
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //代理云
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];  //代理IP地址
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //代理IP端口
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");
        //代理云账号密码
        String proxyUser = "feeyeapp";
        String proxyPass = "feeye789";
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setProxy(dailiyunProxy)
                .setStaleConnectionCheckEnabled(true)
                .build();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setSSLSocketFactory(sslsf)
                .setDefaultCredentialsProvider(credsProvider)
                .setDefaultCookieStore(cookieStore)
                .build();
        CloseableHttpResponse response = null;
        try {
            //json字符串转java对象
            JSONObject jsonObject = JSON.parseObject(orderJson);
            OrderJson orderinfo = jsonObject.toJavaObject(OrderJson.class);

            //获取getJsessionId
            getJsessionId(httpclient, defaultRequestConfig, cookieStore);

            //登录
            for (int i = 0; i < 3; i++) {
                cookie = login(httpclient, defaultRequestConfig, cookieStore, orderinfo);
                if (StringUtils.isNotEmpty(cookie))
                    break;
                if (cookie.contains("ERROR")) {
                    return cookie;
                }
            }

            //检查是否登录
            String checkLoginBack = "";
            for (int i = 0; i < 3; i++) {
                checkLoginBack = checkLogin(httpclient, defaultRequestConfig, cookieStore, orderinfo, 0);
                if (checkLoginBack.contains("true")) {
                    break;
                }
            }

            //查询航班
            String queryFlightsBack = "";
            for (int i = 0; i < 3; i++) {
                queryFlightsBack = queryFlights(httpclient, defaultRequestConfig, cookieStore, orderinfo);
                if (StringUtils.isNotEmpty(queryFlightsBack)) {
                    break;
                }
            }

            if (queryFlightsBack.contains("ERROR")) {
                return queryFlightsBack;
            }

//            //请求以下两个接口，获取Jsessionid
//            if (StringUtils.isNotEmpty(queryFlightsBack)) {
//                this.getCouponCondition(httpclient, defaultRequestConfig, cookieStore, orderinfo);
//                this.findMinPriceAirLine(httpclient, defaultRequestConfig, cookieStore, orderinfo);
//            }

            //解析航班数据
            String postFlightParam = postFlightParam(queryFlightsBack, orderinfo);
            if (postFlightParam.contains("没有匹配到符合条件的航班信息")) {
                return "ERROR" + postFlightParam;
            }

            //检查是否登录
            for (int i = 0; i < 3; i++) {
                checkLoginBack = checkLogin(httpclient, defaultRequestConfig, cookieStore, orderinfo, 0);
                if (checkLoginBack.contains("true")) {
                    break;
                }
            }

            //获取请求参数 uuid
            String postFlightBack = postFlight(httpclient, defaultRequestConfig, cookieStore, orderinfo, postFlightParam);

            //检查是否登录
            for (int i = 0; i < 3; i++) {
                checkLoginBack = checkLogin(httpclient, defaultRequestConfig, cookieStore, orderinfo, 0);
                if (checkLoginBack.contains("true")) {
                    break;
                }
            }

            //选择航班
            String choseFligthBack = choseFlight(httpclient, defaultRequestConfig, cookieStore, orderinfo, postFlightBack);

            //创建订单
            String createOrderBack = "";
            for (int i = 0; i < 3; i++) {
                createOrderBack = createOrder(httpclient, defaultRequestConfig, cookieStore, orderinfo, choseFligthBack);
                if (createOrderBack.contains("系统异常")) {
                    continue;
                } else {
                    break;
                }
            }

            //支付跳转
            String paymentBack = payment(httpclient, defaultRequestConfig, cookieStore, orderinfo, createOrderBack);

            //汇付支付
            if (StringUtils.isNotEmpty(paymentBack)) {
                Map<String, String> payResultMap = new HashMap<>();
//                OfficialHfPayService pay = new OfficialHfPayService();
//                payResultMap = pay.fhPayCZ(paymentBack,orderJson,httpclient,defaultRequestConfig);
            }

        } catch (Exception e) {
            log.error("error", e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "SUCCESS";
    }

    //获取固定的jsessionId,后面请求都会带上此jsessionId
    public String getJsessionId(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore) {
        CloseableHttpResponse response = null;
        String cookie = "";
        String backStr = "";
        try {
            //请求主机
            HttpHost host = new HttpHost("b2c.csair.com", 443, "https");
            String url = "/B2C40/user/createSid.ao?callback=jQuery110202488539716529321_" + System.currentTimeMillis() + "&_=" + System.currentTimeMillis();
            HttpGet get = new HttpGet(url);
            get.setConfig(config);
            get.setHeader("Host", "b2c.csair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            response = httpclient.execute(host, get);
            backStr = EntityUtils.toString(response.getEntity(), "utf-8");

            //获取cookie
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer sb = new StringBuffer();
            if (CollectionUtils.isNotEmpty(listCookie)) {
                for (int i = 0; i < listCookie.size(); i++) {
                    sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                    cookie = sb.toString();
                }
                System.out.println("getJsessionId-cookie:" + cookie);
                cookieMap.put("cookie", cookie);
            }
        } catch (Exception e) {
            log.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("error", e);
            }
        }
        return backStr;
    }

    //登录
    public String login(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, OrderJson orderJson) {
        CloseableHttpResponse response = null;
        String cookie = "";
        try {
            //目标主机
            HttpHost host = new HttpHost("b2c.csair.com", 443, "https");
            //处理登录from表单post请求
            HttpPost post = new HttpPost("/portal/user/login");
            String[] userAccount = orderJson.getAccount().split("_");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("userId", userAccount[0]));
            nameValue.add(new BasicNameValuePair("passWord", userAccount[1]));
            nameValue.add(new BasicNameValuePair("memberType", "1"));
            nameValue.add(new BasicNameValuePair("loginType", "1"));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setConfig(config);
            post.setHeader("Host", "b2c.csair.com");
            post.setHeader("Referer", "http://b2c.csair.com/B2C40/modules/bookingnew/manage/login.html");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            post.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject jsonObject = JSON.parseObject(back);
            if (jsonObject.get("success").equals(true)) {
//                System.out.println("登录返回的信息:" + back);
                log.info("登录返回的信息:" + back);
                //获取cookie
                List<Cookie> listCookie = cookieStore.getCookies();
                StringBuffer sb = new StringBuffer();
                if (CollectionUtils.isNotEmpty(listCookie)) {
                    for (int i = 0; i < listCookie.size(); i++) {
                        sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                    }
                }
                cookie = sb.toString();
                cookieMap.put("cookie", cookie);
                System.out.println("官网登录成功的cookie:" + cookie);
                log.info("登录cookie:" + cookie);
            } else if (jsonObject.get("success").equals(false)) {
                log.error("账号或密码错误");
                return "ERROR:账号或密码错误!";
            } else {
                log.error("官网登录失败" + back);
                return "ERROR:官网登录失败!";
            }
        } catch (Exception e) {
            log.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("error", e);
            }
        }
        return cookie;
    }

    //是否已登录
    public String checkLogin(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, OrderJson orderinfo, int retry) {
        CloseableHttpResponse response = null;
        String backStr = "";
        String newCookie = "";
        try {
            //请求主机
            HttpHost host = new HttpHost("b2c.csair.com", 443, "https");
            HttpPost post = new HttpPost("/portal/user/checkLogin");
            post.setConfig(config);
            post.setHeader("Host", "b2c.csair.com");
            post.setHeader("Referer", "https://b2c.csair.com/B2C40/modules/ordernew/orderManagementFrame.html?_=" + System.currentTimeMillis());
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            post.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(host, post);
            backStr = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject jsonObject = JSON.parseObject(backStr);
            System.out.println("checkLogin返回:" + backStr);
            if (jsonObject.get("success").equals(true)) {
                //获取cookie
                List<Cookie> listCookie = cookieStore.getCookies();
                StringBuffer sb = new StringBuffer();
                if (CollectionUtils.isNotEmpty(listCookie)) {
                    for (int i = 0; i < listCookie.size(); i++) {
                        sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                        newCookie = sb.toString();
                    }
                    System.out.println("checkLogin-cookie:" + newCookie);
                    cookieMap.put("cookie", newCookie); //更新cookie
                }
            } else if (jsonObject.get("success").equals(false) && retry < 3) {
                //重新登录
                login(httpclient, config, cookieStore, orderinfo);
                checkLogin(httpclient, config, cookieStore, orderinfo, retry);
                retry++;
            } else {
                log.error("检查登录失败!");
                return "ERROR:检查登录失败";
            }
        } catch (Exception e) {
            log.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("error", e);
            }
        }
        return backStr;
    }

    //查询航班信息
    private String queryFlights(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, OrderJson orderinfo) {
        String back = "";
        try {
            HttpPost httpPost = new HttpPost("/portal/flight/direct/query");
            //请求主机
            HttpHost httpHost = new HttpHost("b2c.csair.com", 443, "https");
            //解析查询参数
            JSONObject queryObject = new JSONObject();
            queryObject.put("depCity", orderinfo.getFlights().get(0).getDeparture());
            queryObject.put("arrCity", orderinfo.getFlights().get(0).getArrival());
            queryObject.put("flightDate", orderinfo.getFlights().get(0).getDepartureDate().replaceAll("-", ""));
            //设置成人 儿童 婴儿数量
            int adultNum = 0;
            int childNum = 0;
            int infantNum = 0;
            for (OrderJson.PassengersBean passengers : orderinfo.getPassengers()) {
                switch (passengers.getPassengerType()) {
                    case "成人":
                        adultNum++;
                        break;
                    case "儿童":
                        childNum++;
                        queryObject.put("childNum", childNum);
                        break;
                    case "婴儿":
                        infantNum++;
                        queryObject.put("infantNum", infantNum);
                        break;
                    default:
                        break;
                }
            }
            queryObject.put("adultNum", String.valueOf(adultNum));
            queryObject.put("childNum", String.valueOf(childNum));
            queryObject.put("infantNum", String.valueOf(infantNum));
            queryObject.put("cabinOrder", "0");
            queryObject.put("airLine", 1);
            queryObject.put("flyType", 0);
            queryObject.put("international", "0");
            queryObject.put("action", "0");
            queryObject.put("segType", "1");
            queryObject.put("cache", 0);
            queryObject.put("preUrl", "");
            queryObject.put("isMember", "");

            StringEntity entity = new StringEntity(queryObject.toString(), Charset.forName("UTF-8"));
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Host", "b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            httpPost.setHeader("Cookie", cookieMap.get("cookie"));
            CloseableHttpResponse response = httpclient.execute(httpHost, httpPost);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            if (StringUtils.isNotEmpty(back)) {
                JSONObject backJson = JSON.parseObject(back);
                if (backJson.get("success").equals(true)) {
                    System.out.println("航班查询成功");
                    log.info("查询航班成功" + back);
                    //获取cookie
                    String newCookie = "";
                    List<Cookie> listCookie = cookieStore.getCookies();
                    StringBuffer sb = new StringBuffer();
                    if (CollectionUtils.isNotEmpty(listCookie)) {
                        for (int i = 0; i < listCookie.size(); i++) {
                            sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                            newCookie = sb.toString();
                        }
                        System.out.println("选择航班cookie:" + newCookie);
                        cookieMap.put("cookie", newCookie); //更新cookie
                    }
                } else if (backJson.get("success").equals(false)) {
                    log.info("查询航班失败" + backJson.getString("errorMsg"));
                    return backJson.getString("errorMsg");
                }
            } else {
                System.out.println("查询航班失败！");
            }
        } catch (IOException e) {
            log.error("CZ官网查询航班错误", e);
        }
        return back;
    }


    //查询优惠券使用条件
    public String getCouponCondition(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, Orderinfo orderinfo) {
        String newCookie = "";
        String backStr = "";
        try {
            //请求主机
            HttpHost host = new HttpHost("b2c.csair.com", 443, "https");
            HttpPost post = new HttpPost("/portal/apply/discount/getCouponCondition");
            post.setConfig(config);
            post.setHeader("Host", "b2c.csair.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            post.setHeader("Cookie", cookieMap.get("cookie"));
            String flightDate = orderinfo.getFlights().get(0).getDepartureDate();
            String depCity = orderinfo.getFlights().get(0).getDeparture();
            String arrCity = orderinfo.getFlights().get(0).getArrival();
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("flightDate", flightDate));
            nameValue.add(new BasicNameValuePair("depCity", depCity));
            nameValue.add(new BasicNameValuePair("arrCity", arrCity));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(host, post);
            backStr = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject jsonObject = JSON.parseObject(backStr);
            System.out.println("getCouponCondition返回:" + backStr);
            //获取cookie
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer sb = new StringBuffer();
            if (CollectionUtils.isNotEmpty(listCookie)) {
                for (int i = 0; i < listCookie.size(); i++) {
                    sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                    newCookie = sb.toString();
                }
                System.out.println("getCouponCondition-cookie:" + newCookie);
                cookieMap.put("cookie", newCookie); //更新cookie
            }
        } catch (Exception e) {
            log.error("error", e);
        }
        return backStr;
    }

    //查询航线的最低价
    public String findMinPriceAirLine(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, Orderinfo orderinfo) {
        String newCookie = "";
        String backStr = "";
        try {
            //请求主机
            HttpHost host = new HttpHost("b2c.csair.com", 443, "https");
            String flightDate = orderinfo.getFlights().get(0).getDepartureDate();
            String depCity = orderinfo.getFlights().get(0).getDeparture();
            String arrCity = orderinfo.getFlights().get(0).getArrival();
            String url = "/portal/cityWidget/findMinPriceAirLine?dep=" + depCity + "&arr=" + arrCity + "&flightDate=" + flightDate + "&channel=B2C";
            HttpGet get = new HttpGet(url);
            get.setConfig(config);
            get.setHeader("Host", "b2c.csair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            get.setHeader("Cookie", cookieMap.get("cookie"));
            CloseableHttpResponse response = httpclient.execute(host, get);
            backStr = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject jsonObject = JSON.parseObject(backStr);
            System.out.println("findMinPriceAirLine返回:" + backStr);
            //获取cookie
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer sb = new StringBuffer();
            if (CollectionUtils.isNotEmpty(listCookie)) {
                for (int i = 0; i < listCookie.size(); i++) {
                    sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                    newCookie = sb.toString();
                }
                System.out.println("findMinPriceAirLine-cookie:" + newCookie);
                cookieMap.put("cookie", newCookie); //更新cookie
            }
        } catch (Exception e) {
            log.error("error", e);
        }
        return backStr;
    }


    //发送选择的航班信息,获取下一步请求参数 uuid
    private String postFlight(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, OrderJson orderinfo, String queryFlightsBack) {
        String back = "";
        String newCookie = "";
        try {
            //请求主机
            HttpHost httpHost = new HttpHost("b2c.csair.com", 443, "https");
            if (StringUtils.isNotEmpty(queryFlightsBack)) {
                HttpPost httpPost = new HttpPost("/portal/passenger/flight/postFlight");
                StringEntity entity = new StringEntity(queryFlightsBack.toString(), Charset.forName("UTF-8"));
                httpPost.setEntity(entity);
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Host", "b2c.csair.com");
                httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
                httpPost.setHeader("Cookie", cookieMap.get("cookie"));
                CloseableHttpResponse response = httpclient.execute(httpHost, httpPost);
                //获取cookie
                List<Cookie> listCookie = cookieStore.getCookies();
                StringBuffer sb = new StringBuffer();
                if (CollectionUtils.isNotEmpty(listCookie)) {
                    for (int i = 0; i < listCookie.size(); i++) {
                        sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                        newCookie = sb.toString();
                    }
                    System.out.println("postFlight-cookie:" + newCookie);
                    cookieMap.put("cookie", newCookie); //更新cookie
                }
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println("返回的UUID:" + back);
                log.info("返回的UUD:" + back);
            }
        } catch (Exception e) {
            log.error("CZ官网获取uuid参数错误", e);
        }
        return back;
    }

    //获取选择的航班信息
    private String choseFlight(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, OrderJson orderinfo, String queryFlightsBack) {
        String back = "";
        String newCookie = "";
        CloseableHttpResponse response = null;
        try {
            JSONObject jsonObject = JSON.parseObject(queryFlightsBack);
            String uuid = jsonObject.get("data").toString();
            //设置请求参数 uuid
            StringBuffer buffer = new StringBuffer();
            buffer.append("{\"uuid\":");
            buffer.append("\"" + uuid + "\"");
            buffer.append("}");
            //请求主机
            HttpHost httpHost = new HttpHost("b2c.csair.com", 443, "https");
            HttpPost httpPost = new HttpPost("/portal/passenger/flight/getFlight");
            StringEntity entity = new StringEntity(buffer.toString(), Charset.forName("UTF-8"));
            httpPost.setEntity(entity);
            String refererUrl = "https://b2c.csair.com/B2C40/newTrips/static/main/page/passengers/index.html?" + uuid;
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Host", "b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            httpPost.setHeader("Referer", refererUrl);
            httpPost.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(httpHost, httpPost);
            //获取cookie
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer sb = new StringBuffer();
            if (CollectionUtils.isNotEmpty(listCookie)) {
                for (int i = 0; i < listCookie.size(); i++) {
                    sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                    newCookie = sb.toString();
                }
                System.out.println("choseFlight-cookie:" + newCookie);
                cookieMap.put("cookie", newCookie); //更新cookie
            }
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject object = JSON.parseObject(back);
            if (object.get("success").equals(true)) {
                System.out.println("选择航班返回信息:" + back);
                log.info("选择航班返回信息" + back);
            } else if (object.getString("errorMsg").contains("登录异常")) {
                log.error("选择航班返回信息:登录异常,请重新登录");
                return "ERROR:登录异常,请重新登录";
            }
        } catch (Exception e) {
            log.error("CZ官网选择航班错误", e);
        }
        return back;
    }

    //创建订单
    public String createOrder(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, OrderJson orderinfo, String choseFligthBack) {
        String back = "";
        String newCookie = "";
        CloseableHttpResponse response = null;
        //开始组装查询参数
        JSONObject backObj = JSONObject.parseObject(choseFligthBack);
        CreateOrderInfo createOrderInfo = new CreateOrderInfo();
        createOrderInfo.setDomesticIndicate("1");
        createOrderInfo.setContact("/B2C");
//        createOrderInfo.setReferralCode("");
        //订单联系人
        createOrderInfo.setOrderContact(orderinfo.getLinkMan() + "|||");
//        createOrderInfo.setTransactor("");
//        createOrderInfo.setBookUser("");
//        createOrderInfo.setBookUserName("");
//        createOrderInfo.setBookAgentName("");
//        createOrderInfo.setBookAgent("");
//        createOrderInfo.setBookAgentName("");
//        createOrderInfo.setBookAgent4log("");
//        createOrderInfo.setUnionPayCardNo("");
//        createOrderInfo.setUnionPayType("");
//        createOrderInfo.setCreateDate("");
//        createOrderInfo.setGroupName("");
        createOrderInfo.setGroupFlag("0");
        String seatNum = String.valueOf(orderinfo.getPassengers().size());
        createOrderInfo.setSeatNum(seatNum);
        createOrderInfo.setAdultNum(backObj.getJSONObject("data").getString("adult"));
        createOrderInfo.setChildNum(backObj.getJSONObject("data").getString("child"));
        createOrderInfo.setInfantNum(backObj.getJSONObject("data").getString("infant"));
        createOrderInfo.setSegType(backObj.getJSONObject("data").getString("segtype"));
//        createOrderInfo.setGxhType("");
        createOrderInfo.setMerchantId("CZBTOC");
        createOrderInfo.setInsertSands("Y");
        createOrderInfo.setUpcabinFlag("false");
        createOrderInfo.setVisibleFlag("false");
        createOrderInfo.setVip("ORDINARY");
        //TODO 动态从返回的筛选航班中获取
        createOrderInfo.setRemitLimitDate("2019-04-25 07:30:00");
        createOrderInfo.setAgreeAlterNation("NN");
//        createOrderInfo.setEndorSement("");
//        createOrderInfo.setChildenDorsement("");
//        createOrderInfo.setInfantenDorsement("");
//        createOrderInfo.setUserTime("");
        createOrderInfo.setDiscountFlag("N");
        createOrderInfo.setAttribute3("zh");
//        createOrderInfo.setPayBeforePnrFlag("");
//        createOrderInfo.setReportUuid("");
//        createOrderInfo.setCreateDate("");
        //passengers
        List<CreateOrderInfo.PassengersBean> passengerList = new ArrayList<CreateOrderInfo.PassengersBean>();
        List<OrderJson.PassengersBean> passengers = orderinfo.getPassengers();
        for (OrderJson.PassengersBean passenger : passengers) {
            CreateOrderInfo.PassengersBean passengersBean = new CreateOrderInfo.PassengersBean();
            passengersBean.setName(passenger.getPassengerName());
            switch (passenger.getPassengerType()) {
                case "成人":
                    passengersBean.setType("0");
                    break;
                case "儿童":
                    passengersBean.setType("1");
                    break;
                case "婴儿":
                    passengersBean.setType("2");
                    break;
                default:
            }
            switch (passenger.getPassengerCardType()) {
                case "身份证":
                    passengersBean.setIdType("NI");
                    break;
                case "护照":
                    passengersBean.setIdType("PP");
                    break;
                case "其他":
                    passengersBean.setIdType("ID");
                    break;
                default:
            }
            passengersBean.setIdCard(passenger.getIdcard());
            passengersBean.setInsurance("N");
            passengerList.add(passengersBean);
        }
        createOrderInfo.setSeatNum(String.valueOf(passengers.size()));
        createOrderInfo.setPassengers(passengerList);
        //segments
        CreateOrderInfo.SegmentsBean segments = new CreateOrderInfo.SegmentsBean();
        segments.setIszslConpun("false");
//        segments.setChildOrAdult("");
        //segmentDetails
        CreateOrderInfo.SegmentsBean.SegmentDetailsBean segmentDetails = new CreateOrderInfo.SegmentsBean.SegmentDetailsBean();
        //realSegment
        CreateOrderInfo.SegmentsBean.SegmentDetailsBean.RealSegmentBean realSegment = new CreateOrderInfo.SegmentsBean.SegmentDetailsBean.RealSegmentBean();
        realSegment.setRealSegOrder("1");
        realSegment.setDepAirport(orderinfo.getFlights().get(0).getDeparture());
        realSegment.setArrAirport(orderinfo.getFlights().get(0).getArrival());
        segmentDetails.setSegOrder("1");
        segmentDetails.setRealSegment(realSegment);
        //TODO 待确认赋值
        segmentDetails.setPreUrl("COMMON");
        segmentDetails.setLeftSeats(">9");
        //TODO舱位信息
//        segmentDetails.setBookingClassAvails("");
        segmentDetails.setCodeShare("false");
        segmentDetails.setIsDirect("true");
        segmentDetails.setCarrier("CZ");
        segmentDetails.setFlightNo(orderinfo.getFlights().get(0).getFlightNo().substring(2));
        segmentDetails.setDepairPort(orderinfo.getFlights().get(0).getDeparture());
        segmentDetails.setArrairPort(orderinfo.getFlights().get(0).getArrival());

        JSONObject firstflight = new JSONObject();
        firstflight = backObj.getJSONObject("data").getJSONArray("segments").getJSONObject(0).getJSONObject("firstflight");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String depDateTime = firstflight.getString("depDate").concat(firstflight.getString("depTime"));
        String arrDateTime = firstflight.getString("arrDate").concat(firstflight.getString("arrTime"));
        try {
            segmentDetails.setDepTime(sdf.format(new SimpleDateFormat("yyyyMMddHHmm").parse(depDateTime)));
            segmentDetails.setArrTime(sdf.format(new SimpleDateFormat("yyyyMMddHHmm").parse(arrDateTime)));
        } catch (ParseException e) {
            log.error("日期时间转换错误", e);
        }
        segmentDetails.setDepDateTime(firstflight.getString("depDate"));
        segmentDetails.setArrDateTime(firstflight.getString("arrDate"));
        segmentDetails.setSandsFlag("0");
        segmentDetails.setPlaneType(firstflight.getString("plane"));
        segmentDetails.setAirportTax(backObj.getJSONObject("data").getJSONArray("segments").getJSONObject(0).getString("tax"));
        segmentDetails.setCabin(firstflight.getString("code"));
        segmentDetails.setAdultPrice(firstflight.getJSONArray("price").get(0).toString());
        segmentDetails.setChildPrice(firstflight.getJSONArray("price").get(1).toString());
        segmentDetails.setInfantPrice(firstflight.getJSONArray("price").get(2).toString());
        segmentDetails.setAdultFareBasis(firstflight.getJSONArray("fareBasis").get(0).toString());
        segmentDetails.setChildFareBasis(firstflight.getJSONArray("fareBasis").get(1).toString());
        segmentDetails.setInfantFareBasis(firstflight.getJSONArray("fareBasis").get(2).toString());
        segmentDetails.setFareReference(firstflight.getJSONArray("fareReference").get(0).toString());
        segmentDetails.setChildFareReference(firstflight.getJSONArray("fareReference").get(1).toString());
        segmentDetails.setInfantFareReference(firstflight.getJSONArray("fareReference").get(2).toString());
        segmentDetails.setAdultFuelTax(backObj.getJSONObject("data").getJSONArray("segments").getJSONObject(0).getJSONArray("fuel").get(0).toString());
        segmentDetails.setChildFuelTax(backObj.getJSONObject("data").getJSONArray("segments").getJSONObject(0).getJSONArray("fuel").get(1).toString());
        segmentDetails.setInfantFuelTax(backObj.getJSONObject("data").getJSONArray("segments").getJSONObject(0).getJSONArray("fuel").get(2).toString());
        segmentDetails.setDiscount("0");
        segmentDetails.setId(firstflight.get("id").toString());
        segmentDetails.setTerm(firstflight.get("depterm").toString());
        segmentDetails.setDestinationTerm(firstflight.get("arrterm").toString());
        segmentDetails.setSegType("S");
//        segmentDetails.setCooperateCode("");
        segments.setSegmentDetails(Collections.singletonList(segmentDetails));
        createOrderInfo.setSegments(segments);
        //pnrLists
        JSONArray pnrLists = new JSONArray();
        //discounts
        JSONArray discounts = new JSONArray();
        createOrderInfo.setPnrLists(pnrLists);
        createOrderInfo.setDiscounts(discounts);

        String createOrderInfoJson = JSON.toJSONString(createOrderInfo);
        StringEntity entity = new StringEntity(createOrderInfoJson, Charset.forName("UTF-8"));
        try {
            HttpPost httpPost = new HttpPost("/portal/data/order/direct/createOrder");
            //请求主机
            HttpHost post = new HttpHost("b2c.csair.com", 443, "https");
            httpPost.setConfig(config);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Host", "b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            httpPost.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(post, httpPost);
            //获取cookie
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer sb = new StringBuffer();
            if (CollectionUtils.isNotEmpty(listCookie)) {
                for (int i = 0; i < listCookie.size(); i++) {
                    sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                    newCookie = sb.toString();
                }
                System.out.println("createOrder-cookie:" + newCookie);
                cookieMap.put("cookie", newCookie); //更新cookie
            }
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("创建订单返回信息:" + back);
            log.info("创建订单返回信息" + back);
        } catch (Exception e) {
            log.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return back;
    }


    //保存乘机人信息
    public String svaeContactPersonsInfo(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, OrderJson orderJson) {
        String backStr = "";
        try {
            HttpPost httpPost = new HttpPost("/portal/svc/commonContacts/saveSvcEContactPersonsInfo");
            //请求主机
            HttpHost target = new HttpHost("b2c.csair.com", 80, "http");
            String jsonObject = "{\"contactName\":\"夏申\",\"mobilePhone\":\"\",\"email\":\"\"}";
            StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Host", "b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            httpPost.setHeader("Cookie", cookieMap.get("cookie"));
            CloseableHttpResponse response = httpclient.execute(target, httpPost);
            backStr = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("保存乘机人返回信息:" + backStr);
            log.info("保存乘机人返回信息" + backStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backStr;
    }

    //支付跳转
    private String payment(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, OrderJson orderinfo, String createOrderBack) {
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        String backStr = "";
        String newCookie = "";
        try {
            httpPost = new HttpPost("/portal/uppPayment/payment");
            //请求主机
            HttpHost target = new HttpHost("b2c.csair.com", 443, "https");
            //请求参数
            JSONObject backJson = JSON.parseObject(createOrderBack);
            String orderNo = backJson.getString("data"); //官网订单号
            String contactName = orderinfo.getLinkMan().replace("|", "");
            String phone = orderinfo.getMobile();
            String billType = "O"; //先写死
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("orderNo", orderNo));
            nameValue.add(new BasicNameValuePair("contactName", contactName));
            nameValue.add(new BasicNameValuePair("phone", phone));
            nameValue.add(new BasicNameValuePair("billType", "O"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            httpPost.setHeader("Origin", "https://b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            httpPost.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(target, httpPost);
            backStr = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("payment:" + backStr);
            StringBuffer sb = new StringBuffer();
            Document document = Jsoup.parse(backStr);
            Elements input = document.getElementsByTag("input");
            for (Element e : input) {
                sb.append(e.attr("name") + "=" + e.val() + "&");
            }
            sb.delete(sb.length() - 1, sb.length());
            response.close();
            target = new HttpHost("upp.csair.com", 443, "https");
            httpPost = new HttpPost("/upp_payment/pay/uppShowPay.upp?wt_saletype=CSAIRIBE&version=1");
            httpPost.setConfig(config);
            StringEntity entity = new StringEntity(sb.toString(), Charset.forName("UTF-8"));
            httpPost.setEntity(entity);
            httpPost.setHeader("Origin", "https://b2c.csair.com");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Referer", "https://b2c.csair.com/portal/uppPayment/payment");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            httpPost.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(target, httpPost);
            backStr = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("uppShowPay.upp:" + backStr);
            log.info("uppShowPay.upp:" + backStr);
            //清空Stringbuffer
            sb.delete(0, sb.length());
            //获取cookie
            List<Cookie> listCookie = cookieStore.getCookies();
            if (CollectionUtils.isNotEmpty(listCookie)) {
                for (int i = 0; i < listCookie.size(); i++) {
                    sb.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                    newCookie = sb.toString();
                }
                System.out.println("uppShowPay.upp-cookie:" + newCookie);
                cookieMap.put("cookie", newCookie); //更新cookie
            }
            //清空Stringbuffer
            sb.delete(0, sb.length());
            //解析html
            Document dc = Jsoup.parse(backStr);
            Elements inputElements = dc.select("#sytForm input[type=hidden]");
            for (Element e : inputElements) {
                if (StringUtils.isEmpty(e.attr("name")))
                    continue;
                if ("paychannels".equals(e.attr("name"))) {
                    continue;
                }
                if ("IDTYPE".equals(e.attr("name"))) {
                    continue;
                }
                if ("posFlag".equals(e.attr("name"))) {
                    continue;
                }
                sb.append(e.attr("name") + "=" + e.val() + "&");
            }
            sb.delete(sb.length() - 1, sb.length());
            sb.append("&paychannels=");
            sb.append("&one_verifycode=");
            sb.append("&radioChannel=135"); //汇付支付
            sb.append("&posFlag=false");
            sb.append("&cmdType=UOPF");
            sb.append("&ACCOUNTNO=");
            sb.append("&EXPIREMONTH=0");
            sb.append("&EXPIREYEAR=0");
            sb.append("&CVV=");
            sb.append("&BUYERNAME=");
            sb.append("&IDTYPE=IDCARD");
            sb.append("&IDCARD=");
            sb.append("&MOBILE=");
            sb.append("&CODENAME=");
            sb.append("&PaymentType=3"); //支付方式
            sb.append("&cardTypeWp=");
            sb.append("&CardTypeDetial=");
            sb.append("&PayerFirstName=");
            sb.append("&ExpiryDateM=0");
            sb.append("&ExpiryDateY=0");
            sb.append("&ExpiryDate=20");
            sb.append("&CountryCode=00");
            sb.append("&CardNum=");
            sb.append("&inputCardNoType=");
            sb.append("&PayerLastName=");
            sb.append("&cvv=");
            sb.append("&address1=");
            sb.append("&City=");
            sb.append("&address2=");
            sb.append("&PostalCode=");
            sb.append("&email=");
            sb.append("&phoneCode=00");
            sb.append("&mobilePhone=");
            sb.append("&PhoneNumber=");
            sb.append("&encodeMsg=");
            sb.append("&one_carid=");
            sb.append("&one_moth=0");
            sb.append("&one_year=0");
            sb.append("&one_carcode=");
            sb.append("&one_tel=");
            sb.append("&one_telcode=");
            response.close();
            httpPost = new HttpPost("/upp_payment/gateway/v20/b2cdopay.upp");
            httpPost.setConfig(config);
            StringEntity entitys = new StringEntity(sb.toString(), Charset.forName("UTF-8"));
            httpPost.setEntity(entitys);
            httpPost.setHeader("Host", "upp.csair.com");
            httpPost.setHeader("Origin", "https://upp.csair.com");
            httpPost.setHeader("Upgrade-Insecure-Requests", "1");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Referer", "https://upp.csair.com/upp_payment/pay/uppShowPay.upp?wt_saletype=CSAIRIBE&version=1");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            httpPost.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(target, httpPost);
            backStr = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("b2cdopay.upp:" + backStr);
        } catch (Exception e) {
            log.error("官网支付跳转错误", e);
        }
        return backStr;
    }

    /****************************************************解析返回json参数方法****************************************************/
    //解析查询所有航班返回的字符串
    public String postFlightParam(String back, OrderJson orderinfo) {
        JSONObject postFlightParam = new JSONObject();
        //segments
        JSONArray segments = new JSONArray();
        //firstflighObj
        JSONObject firstflight = new JSONObject();
        //cabinArray
        JSONArray cabinArray = new JSONArray();
        //fgprice
        JSONObject fgpriceObj = new JSONObject();
        //ppriceObject
        JSONObject ppriceObject = new JSONObject();
        //pinfoObject
        JSONObject pinfoObject = new JSONObject();
        //priceArray
        JSONArray priceArray = new JSONArray();
        //fareBasisArray
        JSONArray fareBasisArray = new JSONArray();
        //fareReferenceArray
        JSONArray fareReferenceArray = new JSONArray();
        //changeAndRefund
        JSONObject changeAndRefundObject = new JSONObject();
        //change
        JSONArray changeArray = new JSONArray();
        //refund
        JSONArray refundArray = new JSONArray();
        //ch_change
        JSONArray ch_changeArray = new JSONArray();
        //ch_refund
        JSONArray ch_refundArray = new JSONArray();
        //in_change
        JSONArray in_changeArray = new JSONArray();
        //in_refund
        JSONArray in_refundArray = new JSONArray();
        if (StringUtils.isNotEmpty(back)) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(back);
                //航班数组
                JSONArray flightArray = jsonObject.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONArray("flight");
                String flightNo = orderinfo.getFlights().get(0).getFlightNo();
                Double price = Double.valueOf(orderinfo.getFlights().get(0).getPrice());
                String cabin = orderinfo.getFlights().get(0).getCabin();
                //遍历所有航班
                for (int i = 0; i < flightArray.size(); i++) {
                    if (flightNo.equals(flightArray.getJSONObject(i).get("flightNo"))) {
                        firstflight = flightArray.getJSONObject(i);
                        cabinArray = firstflight.getJSONArray("cabin");
                        //遍历航下的所有舱位
                        for (int j = 0; j < cabinArray.size(); j++) {
                            String name = cabinArray.getJSONObject(j).getString("name");   //舱位号
                            Double adultPrice = Double.valueOf(cabinArray.getJSONObject(j).getString("adultPrice"));
                            String info = cabinArray.getJSONObject(j).getString("info");
                            ppriceObject.put(name, adultPrice);
                            pinfoObject.put(name, info);
                            //depterm
                            firstflight.put("depterm", flightArray.getJSONObject(i).getString("departureTerminal"));
                            //arrterm
                            firstflight.put("arrterm", flightArray.getJSONObject(i).getString("arrivalTerminal"));
                            if (price.equals(adultPrice)) {
                                //获取指定的
                                String nameChose = cabinArray.getJSONObject(j).getString("name");
                                String adultPriceChose = cabinArray.getJSONObject(j).getString("adultPrice");
                                String childPrice = cabinArray.getJSONObject(j).getString("childPrice");
                                String infantPrice = cabinArray.getJSONObject(j).getString("infantPrice");
                                String discount = cabinArray.getJSONObject(j).getString("discount");
                                String adultFareBasis = cabinArray.getJSONObject(j).getString("adultFareBasis");
                                String childFareBasis = cabinArray.getJSONObject(j).getString("childFareBasis");
                                String infantFareBasis = cabinArray.getJSONObject(j).getString("infantFareBasis");
                                String fareReference = cabinArray.getJSONObject(j).getString("fareReference");
                                String childFareReference = cabinArray.getJSONObject(j).getString("childFareReference");
                                String infantFareReference = cabinArray.getJSONObject(j).getString("infantFareReference");
                                String refundMessageAdZh = cabinArray.getJSONObject(j).getString("refundMessageAdZh");
                                String refundMessageChZh = cabinArray.getJSONObject(j).getString("refundMessageChZh");
                                String changeMessageAdZh = cabinArray.getJSONObject(j).getString("changeMessageAdZh");
                                String changeMessageChZh = cabinArray.getJSONObject(j).getString("changeMessageChZh");
                                String gbAdultPrice = cabinArray.getJSONObject(j).getString("gbAdultPrice");
                                String infoChose = cabinArray.getJSONObject(j).getString("info");
                                String brandType = cabinArray.getJSONObject(j).getString("brandType");

                                firstflight.put("discount", discount);
                                firstflight.put("code", nameChose);
                                firstflight.put("gbAdultPrice", gbAdultPrice);
                                firstflight.put("info", infoChose);

                                priceArray.set(0, adultPriceChose);
                                priceArray.set(1, childPrice);
                                priceArray.set(2, infantPrice);

                                fareBasisArray.set(0, adultFareBasis);
                                fareBasisArray.set(1, childFareBasis);
                                fareBasisArray.set(2, infantFareBasis);

                                fareReferenceArray.set(0, fareReference);
                                fareReferenceArray.set(1, childFareReference);
                                fareReferenceArray.set(2, infantFareReference);

                                changeAndRefundObject.put("change", changeArray);
                                changeAndRefundObject.put("refund", refundArray);
                                changeAndRefundObject.put("ch_change", ch_changeArray);
                                changeAndRefundObject.put("ch_refund", ch_changeArray);
                                changeAndRefundObject.put("in_change", in_changeArray.add(new JSONObject().put("text", "免费变更")));
                                changeAndRefundObject.put("in_refund", in_refundArray.add(new JSONObject().put("text", "全部未使用，免退票手续费")));
                                changeAndRefundObject.put("sign", "不得转签");
                                changeAndRefundObject.put("isold", false);
                                changeAndRefundObject.put("ticket", "退改 ?567 起");
                                changeAndRefundObject.put("solbool", false);
                                changeAndRefundObject.put("refundMessageAdZh", refundMessageAdZh);
                                changeAndRefundObject.put("refundMessageChZh", refundMessageChZh);
                                changeAndRefundObject.put("changeMessageAdZh", changeMessageAdZh);
                                changeAndRefundObject.put("changeMessageChZh", changeMessageChZh);
                                break;
                            }
                        }
                    }
                }
                if (priceArray.isEmpty()) {
                    System.out.println("解析航班返回: 没有匹配到符合条件的航班信息");
                    log.error("解析航班返回: 没有匹配到符合条件的航班信息");
                    return "解析航班返回: 没有匹配到符合条件的航班信息";
                }

                String key1 = jsonObject.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONObject("fbasic").getString("name");
                String adultPrice1 = jsonObject.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONObject("fbasic").getString("adultPrice");
                String key2 = jsonObject.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONObject("jbasic").getString("name").toString();
                String adultPrice2 = jsonObject.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONObject("jbasic").getString("adultPrice");
                String key3 = jsonObject.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONObject("wbasic").getString("name").toString();
                String adultPrice3 = jsonObject.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONObject("wbasic").getString("adultPrice");
                fgpriceObj.put(key1, adultPrice1);
                fgpriceObj.put(key2, adultPrice2);
                fgpriceObj.put(key3, adultPrice3);
                //depAp
                firstflight.put("depAp", jsonObject.getJSONObject("data").getJSONArray("airports").getJSONObject(1).getString("zhName"));
                //arrAP
                firstflight.put("arrap", jsonObject.getJSONObject("data").getJSONArray("airports").getJSONObject(0).getString("zhName"));
                //depCity
                firstflight.put("depCity", jsonObject.getJSONObject("data").getJSONArray("citys").getJSONObject(0).getString("zhName"));
                //arrCity
                firstflight.put("arrCity", jsonObject.getJSONObject("data").getJSONArray("citys").getJSONObject(1).getString("zhName"));
                //addday
                firstflight.put("addday", 0);
                //langcode
                firstflight.put("langcode", "zh");
                //airlineCode
                firstflight.put("airlineCode", "CZ");
                //planeName
                firstflight.put("planeName", jsonObject.getJSONObject("data").getJSONArray("planes").getJSONObject(0).getString("enName"));
                //planeType
                firstflight.put("planeType", "中型");
                //flytype
                firstflight.put("flytype", 0);
                //id
                firstflight.put("id", jsonObject.getJSONObject("data").getString("id"));
                //tripIndex
                firstflight.put("tripIndex", 0);
                //classtype
                firstflight.put("classtype", "odd");
                //count
                firstflight.put("count", 1);
                //pprice
                firstflight.put("pprice", ppriceObject);
                //pinfo
                firstflight.put("pinfo", pinfoObject);
                //cabinInfo
                //TODO 赋值
                JSONArray cabinInfoArray = new JSONArray();
                firstflight.put("cabinInfo", cabinInfoArray);
                //price
                firstflight.put("price", priceArray);
                //typeName
                //TODO 待确认
                firstflight.put("typeName", "快乐飞");
                //fareBasis
                firstflight.put("fareBasis", fareBasisArray);
                //fareReference
                firstflight.put("fareReference", fareReferenceArray);
                //changeAndRefund
                firstflight.put("changeAndRefund", changeAndRefundObject);
                //showInfo
                firstflight.put("showInfo", "");

                firstflight.put("memcabin", false);
                //redpur
                firstflight.put("redpur", "");
                //issolbool
                firstflight.put("issolbool", false);
                //airLine
                firstflight.put("airLine", "中国南方航空公司");
                firstflight.remove("cabin");


                JSONObject segmentsObj = new JSONObject();
                segmentsObj.put("segmenttype", 0);
                segmentsObj.put("sandsflag", 0);
                segmentsObj.put("tax", 50);
                segmentsObj.put("redpur", "");
                segmentsObj.put("firstflight", firstflight);

                JSONArray fuelArray = new JSONArray();
                for (int j = 0; j < 3; j++) {
                    fuelArray.add(0);
                }
                segmentsObj.put("fuel", fuelArray);
                segments.add(0, segmentsObj);

                postFlightParam.put("segments", segments);
                postFlightParam.put("segtype", "S");
                int adultCount = 0;
                int childCount = 0;
                int infantCount = 0;
                for (OrderJson.PassengersBean passengers : orderinfo.getPassengers()) {
                    switch (passengers.getPassengerType()) {
                        case "成人":
                            adultCount++;
                            break;
                        case "儿童":
                            childCount++;
                            break;
                        case "婴儿":
                            infantCount++;
                            break;
                        default:
                    }
                }
                postFlightParam.put("adult", String.valueOf(adultCount));
                postFlightParam.put("child", String.valueOf(childCount));
                postFlightParam.put("infant", String.valueOf(infantCount));
                postFlightParam.put("isMember", false);
                postFlightParam.put("ispreUrlsol", 0);

                JSONObject paramObject = new JSONObject();
                String c1 = orderinfo.getFlights().get(0).getDeparture() + "-" + orderinfo.getFlights().get(0).getArrival();
                String c2 = orderinfo.getFlights().get(0).getArrival();
                String d1 = orderinfo.getFlights().get(0).getDeparture();
                int num = orderinfo.getPassengers().size();
                paramObject.put("at", "1");
                paramObject.put("c1", c1);
                paramObject.put("c2", c2);
                paramObject.put("ct", "0");
                paramObject.put("d1", d1);
                paramObject.put("isBindCardbycabin", new JSONArray());
                paramObject.put("isfirst", true);
                paramObject.put("it", "0");
                paramObject.put("leftStatus", false);
                //TODO 解析城市三段码
                paramObject.put("n1", "广州-哈尔滨");
                paramObject.put("num", num);
                paramObject.put("t", "S");
                paramObject.put("rightStatus", false);
                postFlightParam.put("param", paramObject);
            } catch (JSONException e) {
                log.error("json解析异常", e);
            }
        }
        return postFlightParam.toString();
    }

    public static void main(String[] args) {
        String orderJson = "{\n" +
                "    \"account\": \"13682690632_119617\",\n" +
                "    \"airline\": \"MF\",\n" +
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
                "            \"airline\": \"CZ\",\n" +
                "            \"arrival\": \"HRB\",\n" +
                "            \"cabin\": \"\",\n" +
                "            \"departure\": \"FOC\",\n" +
                "            \"departureDate\": \"2019-12-31\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"CZ5017\",\n" +
                "            \"price\": \"1250.0\"\n" +
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
//                "            \"birthday\": \"1975-11-29 00:00:00+08:00\",\n" +
//                "            \"id\": \"37617005\",\n" +
//                "            \"idcard\": \"532225197511291117\",\n" +
//                "            \"passengerCardType\": \"身份证\",\n" +
//                "            \"passengerName\": \"尹建飞\",\n" +
//                "            \"passengerSex\": \"男\",\n" +
//                "            \"passengerType\": \"成人\"\n" +
//                "        },\n" +
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

        CZCreateOrderService o = new CZCreateOrderService();
        String s = o.StartCreateOrder(orderJson, 3);
        System.out.println(s);
    }
}
