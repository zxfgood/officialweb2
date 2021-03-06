package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.*;
import com.feeye.util.FingerPrintUtil;
import com.feeye.util.GeetestRecognition;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
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
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author xs
 * @description ????????????????????????
 * @date 2019/3/15
 */
public class FUCreateOrderService {
    private static final Logger logger = LoggerFactory.getLogger(FUCreateOrderService.class);
    private static final int timeout = 50000;
    String dailiyunAccount = "feeyeapp:feeye789";
    private static Map<String, String> cookieMap = new HashMap<String, String>();

    /**
     * FU????????????
     *
     * @param orderJson   ????????????
     * @param retryCount  ??????????????????????????????
     * @param requestType ????????????
     * @return createOrderBack ??????????????????
     */
    private String StartCreateOrder(String orderJson, int retryCount, int requestType) {
        long nowDateTime = new Date().getTime(); //?????????????????????????????????????????????????????????????????????
        String createOrderBack = "";
        String cookie = "";
        String loginId = ""; //FU????????????id
//        String cancelUrl = PropertiesUtils.getPropertiesValue("config", "cancelUrl");
        String cancelUrl = "";
        if (StringUtils.isEmpty(orderJson)) {
            return "ERROR:???????????????";
        }
        logger.info("????????????????????????" + orderJson);

        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            //?????????SSL??????
            sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            logger.error("error", e);
        }
        org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            logger.error("error", e1);
        }
        //?????????
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];  //??????IP??????
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //??????IP??????
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(dailiyunProxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //?????????????????????
        String proxyUser = "feeyeapp";
        String proxyPass = "feeye789";
        //TODO ?????????????????????
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
//                .setProxy(dailiyunProxy)
                .setStaleConnectionCheckEnabled(true)
                .build();

        HttpClientContext context = HttpClientContext.create();
        context.setAuthCache(authCache);
        context.setRequestConfig(defaultRequestConfig);
        context.setCredentialsProvider(credsProvider);

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setSSLSocketFactory(sslsf)
                .setDefaultCredentialsProvider(credsProvider)
                .setDefaultCookieStore(cookieStore)
                .build();

        CloseableHttpResponse response = null;

        try {
            //json??????
            JSONObject jsonObject = JSON.parseObject(orderJson);
            OrderJson orderJsonBean = jsonObject.toJavaObject(OrderJson.class);

            String order_id = orderJsonBean.getId();
            String childrenUser = orderJsonBean.getUsername();
            String billNo = "";
            String newOrderNo = "";
            try {
                billNo = orderJsonBean.getBillNo();
            } catch (Exception e) {
                logger.error("????????????billNo");
            }
            try {
                newOrderNo = orderJsonBean.getNewOrderNo();
            } catch (Exception e) {
                logger.error("????????????newOrderNo");
            }

/*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "???????????????");
                return "????????????";
            }*/

            //??????
            for (int i = 0; i < 3; i++) {
                cookie = login(httpclient, defaultRequestConfig, cookieStore, orderJsonBean);
                if (cookie.contains("ERROR")) {
                    sendOrderStatus(childrenUser, order_id, cookie);
                    sendCreateOrderInfo("error", "???????????????" + cookie, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return cookie;
                }
                if (StringUtils.isNotEmpty(cookie))
                    cookieMap.put("cookie", cookie);
                break;
            }

/*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "???????????????");
                return "????????????";
            }*/

//            sendOrderStatus(childrenUser, order_id, "FU??????????????????");
            //??????????????????,??????loginid
            for (int i = 0; i < 3; i++) {
                loginId = loadUserMsg(httpclient, defaultRequestConfig, cookieStore, cookie);
                if (loginId.contains("ERROR")) {
                    sendOrderStatus(childrenUser, order_id, loginId);
                    return loginId;
                }
                if (StringUtils.isNotEmpty(loginId))
                    break;
            }

/*
            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info("???????????????");
                return "????????????";
            }
*/

//            sendOrderStatus(childrenUser, order_id, "FU????????????");
            //????????????
            String handerFlightsDataBack = "";
            for (int i = 0; i < 3; i++) {
                String queryFlightsBack = queryFlights(httpclient, defaultRequestConfig, orderJsonBean, cookie, context);
                if (queryFlightsBack.contains("ERROR")) {
                    sendOrderStatus(childrenUser, order_id, queryFlightsBack);
                    sendCreateOrderInfo("error", "???????????????" + queryFlightsBack, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return queryFlightsBack;
                }
                if (StringUtils.isNotEmpty(queryFlightsBack)) {
                    handerFlightsDataBack = handerFlightsData(queryFlightsBack, orderJsonBean);
                    if (handerFlightsDataBack.contains("ERROR")) {
                        sendOrderStatus(childrenUser, order_id, handerFlightsDataBack);
                        sendCreateOrderInfo("error", "???????????????" + handerFlightsDataBack, "", childrenUser, "", order_id, "", "",
                                null, "", "", "false", "true", billNo, requestType);
                        return handerFlightsDataBack;
                    }
                    break;
                }
            }

            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info("???????????????");
                return "????????????";
            }

            sendOrderStatus(childrenUser, order_id, "FU??????????????????");
            //????????????
            String key = createKey(httpclient, defaultRequestConfig, cookieStore, handerFlightsDataBack);
            if (key.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, key);
                sendCreateOrderInfo("error", "???????????????" + key, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return key;
            }
            String preOrderBack = preOrder(httpclient, defaultRequestConfig, cookieStore, key);
            if (preOrderBack.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, preOrderBack);
                sendCreateOrderInfo("error", "???????????????" + preOrderBack, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return preOrderBack;
            }
            String handerDataPreOrderBack = handerDataPreOrder(preOrderBack, orderJsonBean);
            key = createKey(httpclient, defaultRequestConfig, cookieStore, handerDataPreOrderBack);
            if (key.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, key);
                sendCreateOrderInfo("error", "???????????????" + key, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return key;
            }

            sendOrderStatus(childrenUser, loginId, "FU???????????????????????????");
            String submitOrderBack = submitOrder(httpclient, defaultRequestConfig, cookieStore, key);
            String handerDataSubmitOrderBack = handerDataSubmitOrder(submitOrderBack, orderJsonBean);
            key = createKey(httpclient, defaultRequestConfig, cookieStore, handerDataSubmitOrderBack);
            if (key.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, key);
                sendCreateOrderInfo("error", "???????????????" + key, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return key;
            }

            String vasOrderBack = vasOrder(httpclient, defaultRequestConfig, cookieStore, key);
            String handerDataVasOrderBack = handerDataVasOrder(vasOrderBack, orderJsonBean);
            key = createKey(httpclient, defaultRequestConfig, cookieStore, handerDataVasOrderBack);
            if (key.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, key);
                sendCreateOrderInfo("error", "???????????????" + key, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return key;
            }

            sendOrderStatus(childrenUser, order_id, "FU????????????");
            createOrderBack = ackOrder(httpclient, defaultRequestConfig, cookieStore, key);
            if (createOrderBack.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, createOrderBack);
                sendCreateOrderInfo("error", "???????????????" + createOrderBack, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
            }

            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info("???????????????");
                return "????????????";
            }

            //????????????????????????
            JSONObject createOrderBackObj = JSON.parseObject(createOrderBack);
            String orderNo = createOrderBackObj.getJSONObject("data").getString("orderNo");
            String orderId = createOrderBackObj.getJSONObject("data").getString("id"); //??????id
            String orderAllMoney = createOrderBackObj.getJSONObject("data").getString("orderAllMoney"); //?????????

            sendOrderStatus(childrenUser, order_id, "FU????????????????????????????????????");
            //??????????????????
            String locationValue = "";
            for (int i = 0; i < 3; i++) {
                locationValue = uniPay(httpclient, defaultRequestConfig, cookieStore, orderId, orderNo, loginId, order_id);
                if (locationValue.contains("ERROR")) {
                    sendOrderStatus(childrenUser, order_id, locationValue);
                    sendCreateOrderInfo("error", "???????????????" + locationValue, orderAllMoney, childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return locationValue;
                }
                if (StringUtils.isNotEmpty(locationValue)) {
                    break;
                }
            }

            sendOrderStatus(childrenUser, order_id, "FU????????????");
//            //TODO ????????????
//            if (DateUtil.IsRunningTimeOut(nowDateTime, 15 * 1000 * 60)) {
//                sendOrderStatus(childrenUser, order_id, "???????????????????????????");
//                logger.info("???????????????????????????");
//                sendCreateOrderInfo("error", "???????????????????????????", orderAllMoney, childrenUser, "", order_id,
//                        "", "", null, "", "", "false",
//                        "true", billNo, requestType);
//            }
//            OfficialXykPayService pay = new OfficialXykPayService();
//            if (StringUtils.isNotEmpty(locationValue)) {
//                String cookies = cookieMap.get("cookie");
//                payResultMap = pay.yeePayMFNew(locationValue, orderJson, cookies);
//                return payResultMap.toString();
//            }


            sendOrderStatus(childrenUser, order_id, "FU????????????");

            //????????????
            String uniOrderDetailBack = "";
            for (int i = 0; i < 3; i++) {
                uniOrderDetailBack = uniOrderDetail(httpclient, defaultRequestConfig, cookieStore, orderId, loginId);
                if (uniOrderDetailBack.contains("ERROR")) {
                    sendOrderStatus(childrenUser, order_id, uniOrderDetailBack);
                    sendCreateOrderInfo("error", "???????????????" + uniOrderDetailBack, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return uniOrderDetailBack;
                }
                if (StringUtils.isNotEmpty(uniOrderDetailBack)) {
                    break;
                }
            }

            int count = 20;
            List<OrderJson.PassengersBean> passengers = orderJsonBean.getPassengers();
            while (count > 0) {
                try {
                    if (StringUtils.isNotEmpty(uniOrderDetailBack)) {
                        String[] ticketnos = uniOrderDetailBack.split("@_@");
                        if (ticketnos.length == 2) {
                            String[] ticketCount = ticketnos[0].split("#_#");
                            if (ticketCount.length == passengers.size() && !ticketnos[0].contains("null")) {
                                sendCreateOrderInfo("success", "", orderAllMoney, childrenUser, "", order_id, "", "true",
                                        ticketnos[0], "????????????", ticketnos[1], "false", "true", billNo, requestType);
                            }
                        } else {
                            uniOrderDetailBack = uniOrderDetail(httpclient, defaultRequestConfig, cookieStore, orderId, loginId);
                        }
                    }
                } catch (Exception e) {
                    logger.error("error", e);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.error("error", e);
                }
                count--;
            }
            if (count <= 0) {
                sendCreateOrderInfo("error", "?????????????????????" + orderAllMoney, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return "ERROR:?????????????????????";
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    logger.error("error", e);
                }
            }
        }
        return "SUCCESS";
    }


    /**
     * ??????
     *
     * @param orderJson
     * @return cookie
     */
    private String login(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, OrderJson orderJson) {
        CloseableHttpResponse response = null;
        InputStream is = null;
        String cookie = "";
        String order_id = orderJson.getId();
        String childrenUser = orderJson.getUsername();

        String username = orderJson.getOtheraccount().split("_")[0];
        String password = orderJson.getOtheraccount().split("_")[1];

        sendOrderStatus(childrenUser, order_id, "??????FU??????");
        try {
            //????????????
            HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
            HttpGet get = new HttpGet("http://www.fuzhou-air.cn/");
            get.setConfig(config);
            get.setHeader("Host", "www.fuzhou-air.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
            response = httpclient.execute(host, get);
            //??????cookie
            Header[] headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    cookie += header.getValue() + ";";
                }
            }
            System.out.println("??????FU???????????????cookie:" + cookie);
            logger.info("??????FU???????????????cookie" + cookie);

            sendOrderStatus(childrenUser, order_id, "FU?????????????????????");
            // ?????????????????????
            host = new HttpHost("fuzhou-air.cn", 80, "http");
            //??????16??????????????????
            String random = String.format("%.16f", Math.random());
            get = new HttpGet("/jcaptcha?.tmp=" + random);
            get.setConfig(config);
            get.setHeader("Host", "www.fuzhou-air.cn");
            get.setHeader("Cookie", cookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
            response = httpclient.execute(host, get);

            String session = UUID.randomUUID().toString().replaceAll("-", "");
            String fileUri = "C://testImg//" + session + ".jpg";
            is = response.getEntity().getContent();
            OutputStream os = new FileOutputStream(fileUri);
            IOUtils.copy(is, os);
            os.close();

//            //?????????????????????
//            is = new FileInputStream(new File(fileUri));
//            String validtext = YunSu.getValidCode(is, "", username, password);
//            System.out.println(validtext);
//            org.dom4j.Document document = DocumentHelper.parseText(validtext);
//            org.dom4j.Element root = document.getRootElement();
//            String error = root.elementText("Error");
//            String result = "";
//            if (error != null && !"".equals(error)) {
//                result = error;
//                return "ERROR:" +result;
//            } else {
//                result = root.elementText("Result");
//            }
//            File file = new File(fileUri);
//            if(file.exists()) {
//                file.delete();
//            }
//            System.out.println("?????????????????????:" + result);

//            sendOrderStatus(childrenUser, order_id, "FU???????????????????????????");
//            HttpPost post = new HttpPost("/frontend/api/doAjaxLoginjCaptcha.action");
//            post.setConfig(config);
//            List <NameValuePair> nameValuePair = new ArrayList <NameValuePair>();
//            nameValuePair.add(new BasicNameValuePair("kaptcha", result));
//            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
//            post.setHeader("Host", "www.fuzhou-air.cn");
//            post.setHeader("Referer", "http://www.fuzhou-air.cn/frontend/order/myFlightOrderDetail.jsp?id=854617&loginId=FU1368269063219032823394773&uniOrder=true");
//            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
//            post.setHeader("Cookie", cookie);
//            response = httpclient.execute(host, post);


//            //TODO ???????????????
            System.out.print("???????????????:");
            Scanner scan = new Scanner(System.in);
            String result = scan.nextLine();
            System.out.println("????????????" + result);

            sendOrderStatus(childrenUser, order_id, "FU????????????");
            //????????????from??????post??????
            HttpPost post = new HttpPost("/login!doCheck.action");
            post.setConfig(config);
            String[] userAccount = orderJson.getAccount().split("_");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("userName", userAccount[0]));
            nameValue.add(new BasicNameValuePair("password", userAccount[1]));
            nameValue.add(new BasicNameValuePair("remember", "false"));
            nameValue.add(new BasicNameValuePair("kaptcha", result));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "www.fuzhou-air.cn");
            post.setHeader("Referer", "http://www.fuzhou-air.cn/");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
            post.setHeader("Cookie", cookie);
            response = httpclient.execute(host, post);

            String backstr = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject jsonObject = JSON.parseObject(backstr);
            if (StringUtils.isNotEmpty(jsonObject.getString("data"))) {
                System.out.println("FU?????????????????????" + backstr);
                logger.info("FU??????????????????");
                sendOrderStatus(childrenUser, order_id, "FU??????????????????");
            } else if (jsonObject.getString("msg").contains("????????????")) {
                logger.error("FU???????????????????????????????????????/??????????????????,????????????????????????????????????????????????");
                return "ERROR:FU???????????????????????????????????????/??????????????????,????????????????????????????????????????????????";
            } else if (jsonObject.getString("msg").contains("????????????????????????????????????")) {
                logger.error("FU????????????????????????????????????");
                return "ERROR:FU????????????????????????????????????";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cookie;
    }

    /**
     * ??????????????????
     *
     * @param cookie
     * @return loginId
     */
    private String loadUserMsg(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, String cookie) {
        CloseableHttpResponse response = null;
        String back = "";
        try {
            HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
            HttpPost post = new HttpPost("/frontend/api/loadUserMsg.action");
            post.setConfig(config);
            post.setHeader("Content-Type", "application/text; charset=UTF-8");
            post.setHeader("Host", "www.fuzhou-air.cn");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
            post.setHeader("Cookie", cookie);
            StringEntity entity = new StringEntity("{}", Charset.forName("UTF-8"));
            post.setEntity(entity);
            response = httpclient.execute(host, post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject backJson = JSON.parseObject(back);
            if (StringUtils.isNotEmpty(backJson.getString("data"))) {
                logger.info("FU??????????????????????????????");
                System.out.println("??????????????????????????????:" + back);
                back = backJson.getJSONObject("data").get("loginId").toString();
            } else {
                System.out.println("FU????????????????????????:" + back);
                logger.error("FU????????????????????????");
                return "ERROR:FU????????????????????????";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return back;
    }

    /**
     * ????????????????????????
     *
     * @param httpclient
     * @param config
     * @param cookieStore
     * @param cookie
     * @return
     */
    private String commonpassengers(CloseableHttpClient httpclient, RequestConfig config, String cookie) {
        CloseableHttpResponse response = null;
        StringBuffer buffer = new StringBuffer();
        try {
            HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
            HttpPost post = new HttpPost("/frontend/member/api/commonpassengers.action");
            post.setConfig(config);
            post.setHeader("Content-Type", "application/text; charset=UTF-8");
            post.setHeader("Host", "www.fuzhou-air.cn");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "http://www.fuzhou-air.cn/frontend/commonpassenger/myCommonPassenger_new.jsp");
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            String param = "{\"pName\":\"\",\"currentPageNum\":\"1\"}";
            StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
            post.setEntity(entity);
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject backJson = JSON.parseObject(back);
            if (StringUtils.isNotEmpty(backJson.getString("data")) && "0".equals(backJson.getString("code"))) {
                logger.info("FU????????????????????????:" + back);
                System.out.println("FU????????????????????????:" + back);
                JSONArray passengers = backJson.getJSONObject("data").getJSONArray("list");
//                int maxPageNumber = Integer.parseInt(backJson.getJSONObject("data").getString("maxPageNumber"));
                for (int i = 0; i < passengers.size(); i++) {
                    String id = passengers.getJSONObject(i).getString("id");
                    buffer.append(id + ";");
                }
                return buffer.toString();
            } else {
                logger.error("FU??????????????????????????????");
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
        return null;
    }

    /**
     * ??????????????????
     *
     * @param httpclient
     * @param config
     * @param id
     * @return
     */
    private String doRemove(CloseableHttpClient httpclient, RequestConfig config, String cookie, String id) {
        CloseableHttpResponse response = null;
        try {
            if (StringUtils.isNotEmpty(id)) {
                HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
                HttpPost post = new HttpPost("/frontend/member/api/commonpassengers!doRemove.action");
                post.setConfig(config);
                post.setHeader("Content-Type", "application/text; charset=UTF-8");
                post.setHeader("Host", "www.fuzhou-air.cn");
                post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
                post.setHeader("Cookie", cookie);
                post.setHeader("Referer", "http://www.fuzhou-air.cn/frontend/commonpassenger/myCommonPassenger_new.jsp");
                String param = "{\"id\":\"" + id + "\"}";
                StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
                post.setEntity(entity);
                response = httpclient.execute(host, post);
                String back = EntityUtils.toString(response.getEntity(), "utf-8");
                JSONObject backJson = JSON.parseObject(back);
                if (StringUtils.isEmpty(backJson.getString("data")) && "0".equals(backJson.getString("code"))) {
                    logger.info("FU??????????????????????????????");
                } else {
                    logger.error("FU??????????????????????????????");
                }
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
        return null;
    }

    /**
     * ????????????
     *
     * @param orderJson
     * @param cookie
     * @param isVerify   ?????????????????????,??????false
     * @param verifyCode ?????????
     * @param context
     * @return back
     */
    private String queryFlights(CloseableHttpClient httpclient, RequestConfig config, OrderJson orderJson, String cookie, HttpClientContext context) {
        CloseableHttpResponse response = null;
        String back = "";
        try {
            String orgCity = orderJson.getFlights().get(0).getDeparture();
            String dstCity = orderJson.getFlights().get(0).getArrival();
            String flightDate = orderJson.getFlights().get(0).getDepartureDate();

            HttpHost host = new HttpHost("www.fuzhou-air.cn", 80, "http");
            HttpPost post = new HttpPost("http://www.fuzhou-air.cn/frontend/api/flight.action");
            //??????????????????desc
//            String desc = FingerPrintUtil.getDesc();
            JSONObject queryObject = new JSONObject();
            queryObject.put("index", 0);
            queryObject.put("orgCity", orgCity);
            queryObject.put("dstCity", dstCity);
            queryObject.put("flightdate", flightDate);
            //??????
            queryObject.put("tripType", "ONEWAY");
            queryObject.put("type", 1);
            String desc = "coBPtm4BZy5Ly7E1arnljzFfP/0YwhbW1uAZN2ggN4uSK3htzLUUBjZHlu5VIIm%2B";
            queryObject.put("desc", desc);
            StringEntity entity = new StringEntity(queryObject.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setConfig(config);

            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Cookie", cookie);
            post.setHeader("Host", "www.fuzhou-air.cn");
            post.setHeader("Origin", "http://www.fuzhou-air.cn");
            post.setHeader("Referer", "http://www.fuzhou-air.cn/b2c/search/searchflight.jsp?type=TKT&orgCityCode=FOC&dstCityCode=HAK&orgDate=2019-12-31&dstDate=&adult=1&child=0&infant=0&trip=ONEWAY");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info("?????????????????????" + back);
            System.out.println("?????????????????????" + back);
            response.close();

            if (StringUtils.isNotEmpty(back) && back.contains("This page can't be displayed")) {
                Thread.sleep(2 * 1000);
                return queryFlights(httpclient, config, orderJson, cookie, context);
            }
            JSONObject backJson = JSON.parseObject(back);
            if (backJson.size() == 3) {
                JSONArray segmentsArray = backJson.getJSONObject("data").getJSONArray("flights");
                if (segmentsArray.isEmpty()) {
                    logger.error("FU?????????????????????????????????" + back);
                    return "ERROR:FU?????????????????????????????????";
                } else {
                    logger.error("FU????????????????????????" + back);
                    System.out.println("FU??????????????????:" + back);
                }
            }
            //???????????????
            if (backJson.size() == 1) {
                if ("10000".equals(backJson.get("sta").toString())) {
                    //?????????????????????
                    HttpGet get = new HttpGet("http://www.fuzhou-air.cn/hnatravel/register?verify=gt&s_p_type=32041");
                    get.setHeader("Host", "www.fuzhou-air.cn");
                    get.setHeader("Cookie", cookie);
                    get.setConfig(config);
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                    response = httpclient.execute(get);
                    back = EntityUtils.toString(response.getEntity(), "utf-8");
                    response.close();
                    logger.info("?????????????????????" + back);
                    JSONObject jsonObject = JSON.parseObject(back);
                    String challenge = jsonObject.getString("challenge");
                    String gt = jsonObject.getString("gt");
                    String result = "";
                    for (int i = 0; i <= 3; i++) {
                        result = GeetestRecognition.recognition(gt, "http://www.fuzhou-air.cn/hnatravel/verify", challenge, "da608f56440c4479ac7aa32e4836a52a");
                        logger.info("??????????????????: " + result);
                        System.out.println("??????????????????: " + result);
                        if (StringUtils.isEmpty(result)) {
                            continue;
                        }
                        if (StringUtils.isNotEmpty(result) && result.contains("????????????")) {
                            continue;
                        }
                        break;
                    }
                    JSONObject resultJson = JSON.parseObject(result);
                    int status = resultJson.getInteger("status");
                    if (status != 0) {
                        String msg = resultJson.getString("msg");
                        return "error:" + msg;
                    }
                    JSONObject dataObj = resultJson.getJSONObject("data");
                    String geetest_validate = dataObj.getString("validate");
                    String geetest_challenge = dataObj.getString("challenge");

                    post = new HttpPost("/hnatravel/verify");
                    String validate = geetest_challenge + "," + geetest_validate + "," + geetest_validate + "|jordan";
                    List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
                    nameValue.add(new BasicNameValuePair("verify", "gt"));
                    nameValue.add(new BasicNameValuePair("validate", validate));
                    nameValue.add(new BasicNameValuePair("s_p_type", "32041"));
                    post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
                    post.setConfig(config);
                    post.setHeader("Cookie", cookie);
                    post.setHeader("Host", "www.fuzhou-air.cn");
                    post.setHeader("Origin", "http://www.fuzhou-air.cn");
                    post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                    response = httpclient.execute(host, post);
                    back = EntityUtils.toString(response.getEntity(), "utf-8");
                    System.out.println("?????????????????????:" + back);
                    logger.info("????????????????????????" + back);
                    Header[] headersArr = response.getAllHeaders();
                    for (Header header : headersArr) {
                        if ("Set-Cookie".equals(header.getName())) {
                            cookie += header.getValue() + ";";
                        }
                    }
                    response.close();
                    //??????
                    return queryFlights(httpclient, config, orderJson, cookie, context);
                } else if ("10002".equals(backJson.get("sta").toString())) {
                    return queryFlights(httpclient, config, orderJson, cookie, context);
                } else if ("10001".equals(backJson.get("sta").toString())) {
                    //TODO ???ip??????
//                    logger.error("??????ip?????????");
                    Thread.sleep(3 * 1000);
                    return queryFlights(httpclient, config, orderJson, cookie, context);
                }
            }
        } catch (IOException e) {
            logger.error("error", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
        return back;
    }

    //?????????????????????????????????
    private String handerFlightsData(String back, OrderJson orderJson) {
        String handerFlightsDataBack = "";
        int adultNum = 0;
        int childNum = 0;
        int infantNum = 0;
        for (OrderJson.PassengersBean passengers : orderJson.getPassengers()) {
            switch (passengers.getPassengerType()) {
                case "??????":
                    adultNum++;
                    break;
                case "??????":
                    childNum++;
                    break;
                case "??????":
                    infantNum++;
                    break;
                default:
                    break;
            }
        }
        //??????????????????
        JSONObject queryObject = new JSONObject();
        JSONArray flights = new JSONArray();
        JSONArray segments = new JSONArray();
        JSONObject segmentsObj = new JSONObject();
        JSONObject segmentsObjs = new JSONObject();
        segmentsObjs.put("segments", segments);
        segments.add(0, segmentsObj);
        flights.add(0, segmentsObjs);
        queryObject.put("flights", flights);
        queryObject.put("adultCount", String.valueOf(adultNum));
        queryObject.put("childCount", String.valueOf(childNum));
        queryObject.put("infantCount", String.valueOf(infantNum));
        queryObject.put("type", "chooseSegment");
        if (StringUtils.isNotEmpty(back)) {
            try {
                //json???object
                JSONObject jsonObject = JSON.parseObject(back);
                FUFlightsInfo fuFlightsInfo = jsonObject.toJavaObject(FUFlightsInfo.class);
                String dstCityCode = fuFlightsInfo.getData().getDstCity().getCode();
                String orgCityCode = fuFlightsInfo.getData().getOrgCity().getCode();

                String flightNoStr = orderJson.getFlights().get(0).getFlightNo();
                Double price = Double.valueOf(orderJson.getFlights().get(0).getPrice());
                List<FUFlightsInfo.DataBean.FlightsBean.SegmentsBean> segmentsBeans = fuFlightsInfo.getData().getFlights().get(0).getSegments();
                for (int i = 0; i < segmentsBeans.size(); i++) {
                    String flightno = segmentsBeans.get(i).getFlightno();
                    if (flightNoStr.substring(2).equals(flightno)) {
                        for (int j = 0; j < segmentsBeans.get(i).getProducts().size(); j++) {
                            Double adultFare = Double.valueOf(segmentsBeans.get(i).getProducts().get(j).getCabin().getAdultFare());
                            if (price.equals(adultFare)) {
                                //????????????????????????
                                if ("?????????".equals(segmentsBeans.get(i).getProducts().get(j).getCabin().getSignProductName())) {
                                    continue;
                                }
                                segmentsObj.put("index", "0");
                                segmentsObj.put("airlineCode", "FU");
                                segmentsObj.put("flightNo", flightno);
                                segmentsObj.put("cabinCode", segmentsBeans.get(i).getProducts().get(j).getCabin().getCabinCode());
                                segmentsObj.put("depCode", orgCityCode);
                                segmentsObj.put("arrCode", dstCityCode);
                                segmentsObj.put("productCode", segmentsBeans.get(i).getProducts().get(j).getCabin().getCode());
                                segmentsObj.put("inventory", segmentsBeans.get(i).getProducts().get(j).getCabin().getInventory());
                                segmentsObj.put("aircraftStyle", segmentsBeans.get(i).getProducts().get(j).getCabin().getAircraftStyle());
                                segmentsObj.put("depTime", segmentsBeans.get(i).getProducts().get(j).getCabin().getDepTime());
                                segmentsObj.put("arrTime", segmentsBeans.get(i).getProducts().get(j).getCabin().getArrTime());
                                segmentsObj.put("adultfare", segmentsBeans.get(i).getProducts().get(j).getCabin().getAdultFare());
                                segmentsObj.put("stopCity", segmentsBeans.get(i).getStopsMsg().split(",")[0]);
                                segmentsObj.put("stopArrTime", "-");
                                segmentsObj.put("stopDepTime", "-");
                                segmentsObj.put("depTerm", segmentsBeans.get(i).getDepTerm());
                                segmentsObj.put("arriTerm", segmentsBeans.get(i).getArriTerm());
                                segmentsObj.put("cabinState", segmentsBeans.get(i).getProducts().get(j).getCabin().getCabinState());
                                segmentsObj.put("referenceCabin", segmentsBeans.get(i).getProducts().get(j).getCabin().getReferenceCabin());
                            }
                        }
                    }
                }
                if (segmentsObj.isEmpty()) {
                    return "ERROR:FU??????????????????????????????????????????";
                }
                handerFlightsDataBack = queryObject.toString();
            } catch (JSONException e) {
                logger.error("json????????????", e);
            }
        }
        return handerFlightsDataBack;
    }

    /**
     * ??????key
     *
     * @param paramJson
     * @param cookie
     * @return key
     */
    private String createKey(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, String paramJson) {
        CloseableHttpResponse response = null;
        String key = "";
        try {
            HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
            HttpPost post = new HttpPost("/frontend/reserve/api/createKey.action");
            post.setConfig(config);
            StringEntity entity = new StringEntity(paramJson, Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Content-Type", "application/text; charset=UTF-8");
            post.setHeader("Host", "www.fuzhou-air.cn");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
            post.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(host, post);

            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject backJson = JSON.parseObject(back);
            if (StringUtils.isNotEmpty(backJson.getString("data"))) {
                key = backJson.getJSONObject("data").get("key").toString();
                logger.info("FU??????key??????:" + key);
                System.out.println("??????key??????:" + key);
            } else {
                System.out.println("??????key??????:" + back);
                logger.error("FU??????key??????" + back);
                return "ERROR:FU??????key??????";
            }
        } catch (IOException e) {
            logger.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
        return key;
    }

    //??????key,????????????????????????(webDriver??????)
    private String queryFlights(OrderJson orderJson, String cookie, WebDriver webDriver) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        InputStream is = null;
        String verifyCode = "";
        String newCookie = "";
        String back = "";

        String childrenUser = orderJson.getUsername();
        String order_id = orderJson.getId();

        try {
            //????????????
            HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
            //??????????????????
            String flightNo = orderJson.getFlights().get(0).getFlightNo();
            String orgCity = orderJson.getFlights().get(0).getDeparture();
            String dstCity = orderJson.getFlights().get(0).getArrival();
            String flightDate = orderJson.getFlights().get(0).getDepartureDate();
            Double price = Double.valueOf(orderJson.getFlights().get(0).getPrice());
            int adultNum = 0;
            int childNum = 0;
            int infantNum = 0;
            for (OrderJson.PassengersBean passengers : orderJson.getPassengers()) {
                switch (passengers.getPassengerType()) {
                    case "??????":
                        adultNum++;
                        break;
                    case "??????":
                        childNum++;
                        break;
                    case "??????":
                        infantNum++;
                        break;
                    default:
                }
            }
            //?????????????????????uri
            String uri = "http://www.fuzhou-air.cn/b2c/static/flightSearch.html?orgCityCode=" + orgCity + "&dstCityCode=" + dstCity + "&orgDate=" +
                    flightDate + "&dstDate=" + "&adult=" + adultNum + "&child=" + childNum + "&infant=" + infantNum + "&trip=ONEWAY";
            webDriver.get(uri);
            //???????????????cookie
            Set<Cookie> cookies = webDriver.manage().getCookies();
            for (Cookie c : cookies) {
                if (!"JSESSIONID".equals(c.getName()))
                    cookie += c.getName() + "=" + c.getValue() + ";";
            }
            System.out.println("cooike" + cookie);
            cookieMap.put("cookie", cookie);
            Thread.sleep(3 * 1000);
            webDriver.get(uri);
            Set<Cookie> c = webDriver.manage().getCookies();

            //??????????????????
            File srcFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(srcFile, new File("C:\\testImg\\screenshote1.png"));
            } catch (IOException e) {
                logger.error("error", e);
            }
            //??????????????????
            JSONObject queryObject = new JSONObject();
            JSONArray segments = new JSONArray();
            JSONObject segmentsObj = new JSONObject();
            segments.add(0, segmentsObj);
            queryObject.put("segments", segments);
            queryObject.put("adultCount", adultNum);
            queryObject.put("childCount", childNum);
            queryObject.put("infantCount", infantNum);
            queryObject.put("type", "chooseSegment");

            //?????????html??????
            String pageSourceHtml = webDriver.getPageSource();
//            System.out.println(pageSourceHtml);
            //??????html
            Document document = Jsoup.parse(pageSourceHtml);
            //?????????????????????
            if (document.getElementById("formValid") != null) {
                String backStr = "";
                // TODO ????????????????????????
                //webDriver???????????????,???????????????
                WebElement codeImgEle = webDriver.findElement(By.name("randImage"));
                String height = codeImgEle.getCssValue("height");
                String width = codeImgEle.getCssValue("width");
                System.out.println(height + ":" + width);
                String random = "0." + (long) ((Math.random() + 1) * 10000000000000000L);
                File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
                BufferedImage fullImg = ImageIO.read(screenshot);
                Point point = codeImgEle.getLocation();
                int eleWidth = codeImgEle.getSize().getWidth();
                int eleHeight = codeImgEle.getSize().getHeight();
                BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
                ImageIO.write(eleScreenshot, "png", screenshot);
                String fileUri = "C://testImg//" + random + ".jpg";
                File screenshotLocation = new File(fileUri);
                FileUtils.copyFile(screenshot, screenshotLocation);
                System.out.print("???????????????????????????:");
                Scanner scan = new Scanner(System.in);
                verifyCode = scan.nextLine();
                System.out.println("????????????????????????" + verifyCode);
                webDriver.findElements(By.name("tickcode")).get(0).sendKeys(verifyCode);
                webDriver.findElements(By.name("submit")).get(0).click();
                Thread.sleep(3 * 1000);
                File file = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
                try {
                    FileUtils.copyFile(file, new File("C:\\testImg\\screenshote2.png"));
                } catch (IOException e) {
                    logger.error("error", e);
                }
                //??????????????????
                new WebDriverWait(webDriver, 50).until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[3]/div[1]/div/div[3]/div[3]/div[2]/div[1]/div[8]/span")));
                //???????????????html,??????????????????
                String pageSource = webDriver.getPageSource();
//                System.out.println("?????????html" + pageSource);
                //??????html
                Document doc = Jsoup.parse(pageSource);
                Elements flightInfo = doc.getElementsByClass("preBookTick btn-preBook b_rd_4 fs-16");
                for (Element e : flightInfo) {
                    String cabinNaem = e.attributes().get("data-cabin-name");
                    //???????????????
                    if ("?????????".equals(cabinNaem)) {
                        continue;
                    }
                    String flightNoStr = e.attributes().get("data-seg-flightno");
                    String flightDateStr = e.attributes().get("data-flightdate");
                    String depCode = e.attributes().get("data-cabin-depcode");
                    String arrCode = e.attributes().get("data-cabin-arrcode");
                    Double adultfare = Double.valueOf(e.attributes().get("data-cabin-adultfare"));
                    if (flightNo.substring(2).equals(flightNoStr) && flightDate.equals(flightDateStr) && orgCity.equals(depCode) && arrCode.equals(dstCity)) {
                        if (price.equals(adultfare)) {
                            segmentsObj.put("index", 0);
                            segmentsObj.put("airlineCode", "FU");
                            segmentsObj.put("flightNo", flightNoStr);
                            segmentsObj.put("cabinCode", e.attributes().get("data-cabin-cabincode"));
                            segmentsObj.put("depCode", orgCity);
                            segmentsObj.put("arrCode", dstCity);
                            segmentsObj.put("productCode", e.attributes().get("data-cabin-productcode"));
                            segmentsObj.put("inventory", e.attributes().get("data-cabin-inventory"));
                            segmentsObj.put("aircraftStyle", e.attributes().get("data-seg-aircraftstyle"));
                            segmentsObj.put("depTime", e.attributes().get("data-seg-depTime"));
                            segmentsObj.put("arrTime", e.attributes().get("data-seg-arrTime"));
                            segmentsObj.put("adultfare", price.toString());
                            segmentsObj.put("stopCity", e.attributes().get("data-seg-stop-city"));
                            segmentsObj.put("stopArrTime", "-");
                            segmentsObj.put("stopDepTime", "-");
                            segmentsObj.put("depTerm", "");
                            segmentsObj.put("arriTerm", "");
                            segmentsObj.put("cabinState", e.attributes().get("data-cabin-cabinState"));
                            segmentsObj.put("referenceCabin", e.attributes().get("data-cabin-referenceCabin"));
                        }
                        break;
                    }
                }
            } else {
                Elements flightInfo = document.getElementsByClass("preBookTick btn-preBook b_rd_4 fs-16");
                for (Element e : flightInfo) {
                    String cabinNaem = e.attributes().get("data-cabin-name");
                    //????????????????????????
                    if ("?????????".equals(cabinNaem)) {
                        continue;
                    }
                    String flightNoStr = e.attributes().get("data-seg-flightno");
                    String flightDateStr = e.attributes().get("data-flightdate");
                    String depCode = e.attributes().get("data-cabin-depcode");
                    String arrCode = e.attributes().get("data-cabin-arrcode");
                    Double adultfare = Double.valueOf(e.attributes().get("data-cabin-adultfare"));
                    if (flightNo.substring(2).equals(flightNoStr) && flightDate.equals(flightDateStr) && orgCity.equals(depCode) && arrCode.equals(dstCity)) {
                        if (price.equals(adultfare)) {
                            segmentsObj.put("index", 0);
                            segmentsObj.put("airlineCode", "FU");
                            segmentsObj.put("flightNo", flightNoStr);
                            segmentsObj.put("cabinCode", orderJson.getFlights().get(0).getCabin());
                            segmentsObj.put("cabinCode", e.attributes().get("data-cabin-cabinCode"));
                            segmentsObj.put("depCode", orgCity);
                            segmentsObj.put("arrCode", dstCity);
                            segmentsObj.put("productCode", e.attributes().get("data-cabin-productcode"));
                            segmentsObj.put("inventory", e.attributes().get("data-cabin-inventory"));
                            segmentsObj.put("aircraftStyle", e.attributes().get("data-cabin-aircraftStyle"));
                            segmentsObj.put("depTime", e.attributes().get("data-seg-depTime"));
                            segmentsObj.put("arrTime", e.attributes().get("data-seg-arrTime"));
                            segmentsObj.put("adultfare", price.toString());
                            segmentsObj.put("stopCity", e.attributes().get("data-seg-stop-city"));
                            segmentsObj.put("stopArrTime", "-");
                            segmentsObj.put("stopDepTime", "-");
                            segmentsObj.put("depTerm", "");
                            segmentsObj.put("arriTerm", "");
                            segmentsObj.put("cabinState", e.attributes().get("data-cabin-cabinState"));
                            segmentsObj.put("referenceCabin", e.attributes().get("data-cabin-referenceCabin"));
                        } else {
//                            sendOrderStatus(childrenUser,order_id,"??????????????????????????????????????????");
                            logger.info("??????????????????????????????????????????");
                        }
                        break;
                    }
                }
            }
            back = queryObject.toString();
        } catch (IOException e) {
            logger.error("error", e);
        } catch (InterruptedException e) {
            logger.error("error", e);
        }
        return back;
    }

    /**
     * ????????????
     *
     * @param key
     * @param cookie
     * @param orderJson
     * @return preOrderBack
     */
    private String preOrder(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, String key) {
        CloseableHttpResponse response = null;
        String preOrderBack = "";
        try {
            if (StringUtils.isNotEmpty(key)) {
                HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
                String url = "/frontend/reserve/api/preOrder.action?skey=" + key;
                HttpGet get = new HttpGet(url);
                get.setConfig(config);
                get.setHeader("Host", "www.fuzhou-air.cn");
                get.setHeader("Cookie", cookieMap.get("cookie"));
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
                response = httpclient.execute(host, get);
                preOrderBack = EntityUtils.toString(response.getEntity(), "utf-8");
                JSONObject backJson = JSON.parseObject(preOrderBack);
                if (StringUtils.isNotEmpty(backJson.getString("data"))) {
                    System.out.println("preOrderBack:" + preOrderBack);
                    logger.info("preOrderBack:" + preOrderBack);
                } else if (backJson.get("msg").toString().contains("????????????????????????????????????")) {
                    logger.error("??????????????????????????????????????????????????????????????????");
                    return "ERROR:?????????????????????????????????????????????????????????????????????";
                } else if (backJson.get("msg").toString().contains("SessionRequestKey is error")) {
                    logger.error("SessionRequestKey is error");
                    return "ERROR:SessionRequestKey is error";
                } else if (backJson.get("msg").toString().contains("?????????")) {
                    logger.error("?????????");
                    return "ERROR:?????????";
                } else {
                    return preOrder(httpclient, config, cookieStore, key);
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
        return preOrderBack;
    }

    /**
     * ??????????????????
     *
     * @param key
     * @param cookie
     * @param orderJson
     * @return submitOrderBack
     */
    private String submitOrder(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, String key) {
        CloseableHttpResponse response = null;
        String submitOrderBack = "";
        try {
            if (StringUtils.isNotEmpty(key)) {
                HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
                String url = "/frontend/reserve/api/submitOrder.action?skey=" + key;
                HttpGet get = new HttpGet(url);
                get.setConfig(config);
                get.setHeader("Host", "www.fuzhou-air.cn");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
                get.setHeader("Cookie", cookieMap.get("cookie"));
                response = httpclient.execute(host, get);
                submitOrderBack = EntityUtils.toString(response.getEntity(), "utf-8");
                JSONObject backJson = JSON.parseObject(submitOrderBack);
                if (StringUtils.isNotEmpty(backJson.getString("data"))) {
                    logger.info("submitOrderBack:" + submitOrderBack);
                    System.out.println("submitOrderBack:" + submitOrderBack);
                } else {
                    logger.error("submitOrder??????????????????");
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
        return submitOrderBack;
    }

    /**
     * ????????????
     *
     * @param key
     * @param cookie
     * @param orderJson
     * @return
     */
    private String vasOrder(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, String key) {
        CloseableHttpResponse response = null;
        String vasOrderBack = "";
        try {
            if (StringUtils.isNotEmpty(key)) {
                HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
                String url = "/frontend/reserve/api/vasOrder.action?skey=" + key;
                HttpGet get = new HttpGet(url);
                get.setConfig(config);
                get.setHeader("Host", "www.fuzhou-air.cn");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
                get.setHeader("Cookie", cookieMap.get("cookie"));
                response = httpclient.execute(host, get);
                vasOrderBack = EntityUtils.toString(response.getEntity(), "utf-8");
                JSONObject backJson = JSON.parseObject(vasOrderBack);
                if (StringUtils.isNotEmpty(backJson.getString("data"))) {
                    logger.info("vasOrderBack" + vasOrderBack);
                    System.out.println("vasOrderBack:" + vasOrderBack);
                } else {
                    logger.error("vasOrder??????????????????");
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
        return vasOrderBack;
    }

    /**
     * ??????????????????
     *
     * @param key
     * @param cookie
     * @param orderJson
     * @return
     */
    private String ackOrder(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, String key) {
        CloseableHttpResponse response = null;
        String ackOrderBack = "";
        try {
            if (StringUtils.isNotEmpty(key)) {
                HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
                String url = "/frontend/reserve/api/ackOrder.action?skey=" + key;
                HttpGet get = new HttpGet(url);
                get.setConfig(config);
                get.setHeader("Host", "www.fuzhou-air.cn");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
                get.setHeader("Cookie", cookieMap.get("cookie"));
                response = httpclient.execute(host, get);
                ackOrderBack = EntityUtils.toString(response.getEntity(), "utf-8");
                JSONObject backJson = JSON.parseObject(ackOrderBack);
                if (StringUtils.isNotEmpty(backJson.getString("data"))) {
                    logger.info("FU??????????????????:" + ackOrderBack);
                    System.out.println("ackOrderBack:" + ackOrderBack);
                } else {
                    logger.error("FU????????????");
                    return "ERROR:FU????????????";
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
        return ackOrderBack;
    }

    //?????????????????????preOrder??????????????????
    public String handerDataPreOrder(String preOrderBack, OrderJson orderJson) {
        String backStr = "";
        //??????json??????
        JSONObject queryObject = new JSONObject();
        JSONArray flightsegments = new JSONArray();
        JSONObject segmentsObjs = new JSONObject();
        JSONArray segments = new JSONArray();
        JSONObject segmentsObj = new JSONObject();
        segments.add(0, segmentsObj);
        segmentsObjs.put("segments", segments);
        flightsegments.add(0, segmentsObjs);
        JSONArray passengers = new JSONArray();
        JSONObject itinerary = new JSONObject();
        itinerary.put("billflag", false);

        queryObject.put("flightsegments", flightsegments);
        queryObject.put("segments", new JSONArray());
        queryObject.put("passengers", passengers);
        queryObject.put("type", "pngAndSegment");
        queryObject.put("contactEmail", "");
        queryObject.put("contactMobile", "");
        queryObject.put("contactName", "");
        queryObject.put("contactAddress", "");
        queryObject.put("itinerary", itinerary);

        if (StringUtils.isNotEmpty(preOrderBack)) {
            try {
                //json???object
                JSONObject jsonObject = JSON.parseObject(preOrderBack);
                JSONObject segmentInfo = (JSONObject) jsonObject.getJSONObject("data").getJSONArray("flights").getJSONObject(0).getJSONArray("segments").get(0);
                FUPreOrderInfo fUPreOrderInfo = jsonObject.toJavaObject(FUPreOrderInfo.class);
//                FUPreOrderInfo.DataBean.SegmentInfosBean segmentInfosBean = fUPreOrderInfo.getData().getSegmentInfos().get(0);
                //segments
                segmentsObj.put("index", segmentInfo.getString("index"));
                segmentsObj.put("airlineCode", segmentInfo.getString("airlineCode"));
                segmentsObj.put("flightNo", segmentInfo.getString("flightNo"));
                segmentsObj.put("cabinCode", segmentInfo.getString("cabinCode"));
                segmentsObj.put("depCode", segmentInfo.getString("depCode"));
                segmentsObj.put("arrCode", segmentInfo.getString("arrCode"));
                segmentsObj.put("childMinus", segmentInfo.getString("childMinus"));
                segmentsObj.put("productCode", segmentInfo.getString("productCode"));
                segmentsObj.put("inventory", segmentInfo.getString("inventory"));
                segmentsObj.put("aircraftStyle", segmentInfo.getString("aircraftStyle"));
                segmentsObj.put("depTime", segmentInfo.getString("depTime"));
                segmentsObj.put("arrTime", segmentInfo.getString("arrTime"));
                //TODO ???????????????
                segmentsObj.put("couponIssMark", null);
                segmentsObj.put("couponIssDiscount", "0");
                segmentsObj.put("couponIssOrderNum", "0");
                segmentsObj.put("stopCity", segmentInfo.getString("stopCity"));
                segmentsObj.put("dstAirport", segmentInfo.getString("dstAirport"));
                segmentsObj.put("orgAirport", segmentInfo.getString("orgAirport"));
                segmentsObj.put("stopArrTime", segmentInfo.getString("stopArrTime"));
                segmentsObj.put("stopDepTime", segmentInfo.getString("stopDepTime"));
                segmentsObj.put("stopsMsg", segmentInfo.getString("stopsMsg"));
                //TODO ?????? arrTime - depTime????????????, ????????? 5??????50?????? ??????
                segmentsObj.put("cost", "");
                for (OrderJson.PassengersBean pb : orderJson.getPassengers()) {
                    JSONObject passengersObj = new JSONObject();
                    if ("??????".equals(pb.getPassengerType())) {
                        passengersObj.put("passengerType", "ADULT");
                    } else if ("??????".equals(pb.getPassengerType())) {
                        passengersObj.put("passengerType", "CHILD");
                    } else if ("??????".equals(pb.getPassengerType())) {
                        passengersObj.put("passengerType", "INFANT");
                    }
                    passengersObj.put("isInsure", "0");
                    passengersObj.put("insurance1", "0");
                    passengersObj.put("name", pb.getPassengerName());
                    if ("?????????".equals(pb.getPassengerCardType())) {
                        passengersObj.put("certificateType", "NI");
                    } else if ("??????".equals(pb.getPassengerCardType())) {
                        passengersObj.put("certificateType", "PP");
                    } else if ("??????".equals(pb.getPassengerCardType())) {
                        passengersObj.put("certificateType", "ID");
                    }
                    passengersObj.put("certificateNo", pb.getIdcard());
                    passengersObj.put("certificateNo", pb.getIdcard());
                    //?????????????????????????????????????????????
                    passengersObj.put("mobilePhone", orderJson.getMobile());
                    passengersObj.put("birthday", pb.getBirthday().substring(0, 10));
                    passengers.add(passengersObj);
                }
                queryObject.put("type", "pngAndSegment");
                queryObject.put("contactEmail", "");
                queryObject.put("contactMobile", orderJson.getMobile());
                queryObject.put("contactName", orderJson.getLinkMan());
                queryObject.put("contactAddress", "");
                backStr = queryObject.toString();
            } catch (JSONException e) {
                logger.error("json????????????", e);
            }
        }
        return backStr;
    }

    //?????????????????????submitOrder??????????????????
    private String handerDataSubmitOrder(String submitOrderBack, OrderJson orderJson) {
        String backStr = "";
        //??????json??????
        JSONObject queryObject = new JSONObject();
        JSONArray flights = new JSONArray();
        JSONObject segmentsObjs = new JSONObject();
        flights.add(0, segmentsObjs);
        JSONArray segments = new JSONArray();
        JSONObject segmentsObj = new JSONObject();
        segments.add(0, segmentsObj);
        JSONArray passengers = new JSONArray();
        segmentsObjs.put("segments", segments);
        JSONObject itinerary = new JSONObject();
        itinerary.put("billflag", false);
        itinerary.put("address", "");
        itinerary.put("canton", "??????");
        itinerary.put("city", "???");
        itinerary.put("name", "");
        itinerary.put("province", "???");
        itinerary.put("tel", "");
        itinerary.put("postcode", "");

        queryObject.put("flights", flights);
        queryObject.put("segments", new JSONArray());
        queryObject.put("loungeInfos", new JSONArray());
        queryObject.put("extraProducts", new JSONArray());
        queryObject.put("passengers", passengers);
        queryObject.put("itinerary", itinerary);
        queryObject.put("type", "AuxMarketing");
        queryObject.put("contactEmail", "");
        queryObject.put("contactMobile", orderJson.getMobile());
        queryObject.put("contactName", orderJson.getLinkMan());
        queryObject.put("contactAddress", "");
        if (StringUtils.isNotEmpty(submitOrderBack)) {
            try {
                //json???object
                JSONObject jsonObject = JSON.parseObject(submitOrderBack);
                FUSubmitOrderInfo fUSubmitOrderInfo = jsonObject.toJavaObject(FUSubmitOrderInfo.class);
                FUSubmitOrderInfo.DataBean.FlightsBean.SegmentsBean segmentInfosBean = fUSubmitOrderInfo.getData().getFlights().get(0).getSegments().get(0);
                //segments
                segmentsObj.put("index", segmentInfosBean.getIndex());
                segmentsObj.put("airlineCode", segmentInfosBean.getAirlineCode());
                segmentsObj.put("flightNo", segmentInfosBean.getFlightNo());
                segmentsObj.put("cabinCode", segmentInfosBean.getCabinCode());
                segmentsObj.put("depCode", segmentInfosBean.getDepCode());
                segmentsObj.put("arrCode", segmentInfosBean.getArrCode());
                segmentsObj.put("childMinus", segmentInfosBean.getChildMinus());
                segmentsObj.put("productCode", segmentInfosBean.getProductCode());
                segmentsObj.put("inventory", segmentInfosBean.getInventory());
                segmentsObj.put("aircraftStyle", segmentInfosBean.getAircraftStyle());
                segmentsObj.put("depTime", segmentInfosBean.getDepTime());
                segmentsObj.put("arrTime", segmentInfosBean.getArrTime());
                //TODO ???????????????
                segmentsObj.put("couponIssMark", null);
                segmentsObj.put("couponIssDiscount", "0");
                segmentsObj.put("couponIssOrderNum", "0");
                segmentsObj.put("stopCity", segmentInfosBean.getStopCity());
                segmentsObj.put("dstAirport", segmentInfosBean.getDstAirport());
                segmentsObj.put("orgAirport", segmentInfosBean.getOrgAirport());
                segmentsObj.put("stopArrTime", segmentInfosBean.getStopArrTime());
                segmentsObj.put("stopDepTime", segmentInfosBean.getStopDepTime());
                segmentsObj.put("stopsMsg", segmentInfosBean.getStopsMsg());
                //TODO ?????? arrTime - depTime????????????, ????????? 5??????50?????? ??????
                segmentsObj.put("cost", "");
                List<OrderJson.PassengersBean> passengersBeanList = orderJson.getPassengers();
                for (int i = 0; i < passengersBeanList.size(); i++) {
                    JSONObject passengersObj = new JSONObject();
                    if ("??????".equals(passengersBeanList.get(i).getPassengerType())) {
                        passengersObj.put("passengerType", "ADULT");
                    } else if ("??????".equals(passengersBeanList.get(i).getPassengerType())) {
                        passengersObj.put("passengerType", "CHILD");
                    } else if ("??????".equals(passengersBeanList.get(i).getPassengerType())) {
                        passengersObj.put("passengerType", "INFANT");
                    }
                    passengersObj.put("isInsure", "0");
                    passengersObj.put("insurance1", "0");
                    passengersObj.put("name", passengersBeanList.get(i).getPassengerName());
                    if ("?????????".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "NI");
                    } else if ("??????".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "PP");
                    } else if ("??????".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "ID");
                    }
                    passengersObj.put("certificateNo", passengersBeanList.get(i).getIdcard());
                    //?????????????????????????????????????????????
                    passengersObj.put("mobilePhone", orderJson.getMobile());
                    passengersObj.put("birthday", passengersBeanList.get(i).getBirthday().substring(0, 10));
                    passengersObj.put("uniqueNo", fUSubmitOrderInfo.getData().getPassengersParams().get(i).getUniqueNo());
                    passengers.add(passengersObj);
                }
                backStr = queryObject.toString();
            } catch (JSONException e) {
                logger.error("json????????????", e);
            }
        }
        return backStr;
    }

    //?????????????????????vasOrder??????????????????
    private String handerDataVasOrder(String vasOrderBack, OrderJson orderJson) {
        String backStr = "";
        //??????json??????
        JSONObject queryObject = new JSONObject();
        JSONArray passengers = new JSONArray();
        queryObject.put("disCoupon", new JSONObject());
        queryObject.put("cashCoupon", new JSONObject());
        queryObject.put("passengers", passengers);
        queryObject.put("contactEmail", "");
        queryObject.put("contactMobile", orderJson.getMobile());
        queryObject.put("contactName", orderJson.getLinkMan());
        queryObject.put("contactAddress", "");
        queryObject.put("type", "coupons");
        queryObject.put("times", "3800810593");
        String desc = FingerPrintUtil.getDesc();
        //TODO ?????????????????????????????????
//        queryObject.put("desc", "JeUEPgZXFsnSDYkzgko8AhfaZnD%2BEs0QIMdOJLlMbIYnn3u7w27Q1aRZP0QqTXy0Wu/g27R18sBcmJFRbqjnfqYUZc1E/HPXcobO5szVIKbsG2FZpawp4trFiQqTTcwWaaIMDIfhmHCTRkEyJ9o%2BtgSLaqLv4ueKmwigjM6QAvODwuyqU%2BMVjApbM3rZm5Bs75qObyi2kJD/486noWLyzNxFnZYk3JgDKAkTLpevGPgJRfczv807yL8/BM2BS3UAikF0baaUEN1oP/9KUTAahODuKv9Id%2BdPHiGgUHJQUmUATelcgLRmAZyX4HJ93KsRKP4M2Be2pd5w/UuIEB4/1hdiX96VcgJ5jXXmPBjp23Y=");
        queryObject.put("desc", desc);
        if (StringUtils.isNotEmpty(vasOrderBack)) {
            try {
                //json???object
                JSONObject jsonObject = JSON.parseObject(vasOrderBack);
                FUvasOrderInfo fUvasOrderInfo = jsonObject.toJavaObject(FUvasOrderInfo.class);
                List<OrderJson.PassengersBean> passengersBeanList = orderJson.getPassengers();
                for (int i = 0; i < passengersBeanList.size(); i++) {
                    JSONObject passengersObj = new JSONObject();
                    passengersObj.put("name", passengersBeanList.get(i).getPassengerName());
                    passengersObj.put("birthday", passengersBeanList.get(i).getBirthday().substring(0, 10));
                    passengersObj.put("uniqueNo", fUvasOrderInfo.getData().getPassengersParams().get(i).getUniqueNo());
                    passengersObj.put("mobilePhone", orderJson.getMobile());
                    passengersObj.put("certificateNo", passengersBeanList.get(i).getIdcard());
                    if ("?????????".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "NI");
                    } else if ("??????".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "PP");
                    } else if ("??????".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "ID");
                    }
                    passengers.add(passengersObj);
                }
                backStr = queryObject.toString();
            } catch (JSONException e) {
                logger.error("json????????????", e);
            }
        }
        return backStr;
    }

    /**
     * ??????????????????(????????????),?????????????????????????????????
     *
     * @param orderJson
     * @param cookie
     * @param createOrderBack ??????????????????
     * @param loginId
     * @param order_id
     * @return locationValue ???????????????
     */
    private String uniPay(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore,
                          String orderId, String orderNo, String loginId, String order_id) {
        CloseableHttpResponse response = null;
        config = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setRedirectsEnabled(false)
                .setStaleConnectionCheckEnabled(true)
                .build();
        String back = "";
        String locationValue = "";
        String payType = "YEEPAY"; //??????
        try {
            HttpHost host = new HttpHost("www.fuzhou-air.cn", 80, "http");
            String url = "/payment/UniPay.action?bankId=&payType=" + payType + "&orderId=" + orderId + "&extraOrderId=&orderNo=" + orderNo + "&loginId=" + loginId;
            HttpGet get = new HttpGet(url);
            get.setConfig(config);
            get.setHeader("Host", "www.fuzhou-air.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
            get.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(host, get);
            //??????????????????
            String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            if ("302".equals(statusCode)) {
                //????????????????????????
                Header[] location = response.getHeaders("Location");
                for (int i = 0; i < location.length; i++) {
                    locationValue = location[i].getValue();
                    logger.info(order_id + "Location:" + locationValue);
                }
                if (StringUtils.isEmpty(locationValue)) {
                    logger.info(order_id + "???????????????" + locationValue);
                    logger.info(order_id + "???????????????" + back);
                    return "";
                }
            }
            if ("200".equals(statusCode)) {
                back = EntityUtils.toString(response.getEntity());
                Document document = Jsoup.parse(back);
                //????????????????????????
                String errorStr = document.select(".errorMsg.fs-20").text();
                return "ERROR:" + errorStr;
            }
            // ???????????????
            HttpHost yeepayHost = new HttpHost("www.yeepay.com", 443, "https");
            get = new HttpGet(locationValue);
            get.setConfig(config);
            get.setHeader("Host", " www.yeepay.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
            response = httpclient.execute(yeepayHost, get);
            Header[] locationAfter = response.getHeaders("Location");
            for (int i = 0; i < locationAfter.length; i++) {
                locationValue = locationAfter[i].getValue();
                logger.info("resultLocation:" + locationValue);
            }
            if (StringUtils.isEmpty(locationValue)) {
                logger.info(order_id + "???????????????" + locationValue);
                logger.info(order_id + "???????????????" + back);
                return "";
            }
            // ???????????????
            HttpHost cashdeskHost = new HttpHost("www.cashdesk.yeepay.com", 80, "http");
            get = new HttpGet(locationValue);
            get.setConfig(config);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
            response = httpclient.execute(cashdeskHost, get);
            locationAfter = response.getHeaders("Location");
            for (int i = 0; i < locationAfter.length; i++) {
                locationValue = locationAfter[i].getValue();
                logger.info("resultLocation:" + locationValue);
            }
            if (StringUtils.isEmpty(locationValue)) {
                logger.info(order_id + "???????????????" + locationValue);
                logger.info(order_id + "???????????????" + back);
                return "";
            }
        } catch (IOException e) {
            logger.error("error", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
        return locationValue;
    }

    /**
     * ????????????
     * @param orderJson
     * @param cookie
     * @param createOrderBack
     * @return
     */
/*    private String orderPay(CloseableHttpClient httpclient, RequestConfig config, OrderJson orderJson, String createOrderBack, String loginId) {
        Map<String, String> payResultMap = new HashMap<String,String>();
        //??????json??????
        String orderId = createOrderBackObj.getJSONObject("data").getJSONArray("passengerList").getJSONObject(0)
                .getJSONArray("passengerSegmentList").getJSONObject(0).get("orderId").toString();
        String orderNo = createOrderBackObj.getJSONObject("data").get("orderNo").toString();
            // ????????????
            try {
                sendOrderStatus(childrenUser, order_id, "??????????????????");
                int index = 0;
                for (; index < 3; index++) {
                    try {
                        locationValue = uniPay(orderJson, cookie, orderId, orderNo, loginId, order_id);
                        break;
                    } catch (Exception e) {
                        logger.error("error", e);
                    }
                }
                if (index == 3) {
                    sendCreateOrderInfo("error", "?????????????????????????????????", "", childrenUser, orderNo, order_id,
                            "", "", null, "",
                            "", "false", "true", billNo, requestType);
                    return "ERROR:?????????????????????????????????";
                }
            } catch (Exception e) {
                logger.error("error", e);
                sendCreateOrderInfo("error", "?????????????????????????????????", "", childrenUser, orderNo, order_id,
                        "", "", null, "", "",
                        "false", "true", billNo, requestType);
                return "ERROR:?????????????????????????????????";
            }
            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id,
                        "", "", null, "", "", "false",
                        "true", billNo, requestType);
                logger.info("???????????????");
            }
            if (DateUtil.IsRunningTimeOut(nowDateTime, 15 * 1000 * 60)) {
                sendOrderStatus(childrenUser, order_id, "???????????????????????????");
                logger.info("???????????????????????????");
                sendCreateOrderInfo("error", "???????????????????????????", "", childrenUser, "", order_id,
                        "", "", null, "", "", "false",
                        "true", billNo, requestType);
            }
            OfficialXykPayService pay = new OfficialXykPayService();
            if (StringUtils.isNotEmpty(locationValue)) {
                payResultMap = pay.yeePayMFNew(locationValue, orderJson, cookie);
                return payResultMap.toString();
            }
        return "";
    }*/

    /**
     * ?????????????????????????????????
     *
     * @param cookie
     * @param orderNo
     * @return back
     */
    private String uniOrderDetail(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, String orderId, String loginId) {
        String orderDetailBack = "";
        StringBuffer result = new StringBuffer();
        CloseableHttpResponse response = null;
        if (StringUtils.isNotEmpty(orderId) && StringUtils.isNotEmpty(loginId))
            try {
                HttpHost host = new HttpHost("www.fuzhou-air.cn", 80, "http");
                HttpPost post = new HttpPost("/frontend/order/api/uniOrderDetail.action");
                post.setConfig(config);
                post.setHeader("Host", "www.fuzhou-air.cn");
                post.setHeader("Cookie", "JSESSIONID=9024C26A96D6F1AD958D6C33393D3B62.d2;");
                post.setHeader("Referer", "http://www.fuzhou-air.cn/frontend/order/flightOrder.jsp");
                post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
                JSONObject paramObject = new JSONObject();
                paramObject.put("id", orderId);
                paramObject.put("loginId", loginId);
                StringEntity entity = new StringEntity(paramObject.toString(), Charset.forName("UTF-8"));
                post.setEntity(entity);
                response = httpclient.execute(host, post);
                orderDetailBack = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println("????????????????????????:" + orderDetailBack);
                Map<String, String> mapResult = parseUniOrderDetailBack(orderDetailBack);
                if (mapResult.isEmpty()) {
                    return null;
                }
                for (String value : mapResult.values()) {
                    result.append(value).append("#_#");
                    ;
                }
                logger.info(result.toString());
                return result.toString() + "@_@";
            } catch (IOException e) {
                logger.error("????????????????????????");
            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("error", e);
                }
            }
        return orderDetailBack;
    }

    /**
     * ????????????????????????
     *
     * @param back
     * @return map
     */
    private Map<String, String> parseUniOrderDetailBack(String back) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject backObj = JSON.parseObject(back);
            JSONArray passengerListArray = backObj.getJSONObject("data").getJSONArray("passengerList");
            JSONArray paymentListArray = backObj.getJSONObject("data").getJSONArray("paymentList");
            String bankTradeNo = "";
            try {
                bankTradeNo = paymentListArray.getJSONObject(0).get("paymentNo").toString(); //???????????????
            } catch (Exception e) {
                logger.error("error", e);
            }
            for (int i = 0; i < passengerListArray.size(); i++) {
                String passengerName = passengerListArray.getJSONObject(i).get("passengerName").toString();
                String cerNo = passengerListArray.getJSONObject(i).get("cerNo").toString(); //????????????
                JSONArray segmentListArray = passengerListArray.getJSONObject(i).getJSONArray("flights").getJSONObject(0).getJSONArray("segmentList");
                String ticketNo = segmentListArray.getJSONObject(0).getString("ticketNo").substring(3);
                map.put(String.valueOf(i), passengerName + "##" + cerNo + "##" + ticketNo + "##" + bankTradeNo);
            }
        } catch (JSONException e) {
            logger.error("??????????????????json?????????" + back);
        }
        return map;
    }

    //????????????????????????
    public static String sendOrderStatus(String childrenUser, String orderId, String status) {
        try {
            String orderUrl = "";
//            String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrlStatus");

            StringBuffer buffer = new StringBuffer();
            buffer.append("<feeye-official>");
//            buffer.append("<official>" + Constant.FU.toString() + "</official> ");
            buffer.append("<url>" + orderUrl + "</url> ");
            buffer.append("<type>0</type> ");
            buffer.append("<method>post</method>");
            buffer.append("<max>20</max> ");
            buffer.append("<encod>utf-8</encod> ");
            buffer.append("<params>");
            buffer.append("<param name='childrenUser'>" + childrenUser + "</param>");
            buffer.append("<param name='orderId'>" + orderId + "</param>");
            buffer.append("<param name='orderStatus'>" + status + "</param>");
            buffer.append("</params>");
            buffer.append("</feeye-official>");

            String content = OfficialMain.setRequestParams(buffer.toString());
            if (content != null) {
                String rs1[] = content.split("#@_@#");
                if (rs1.length == 2) {
                    content = rs1[1];
                    return content;
                }
                if (rs1.length == 3) {
                    logger.info(rs1[2]);
                    return rs1[2];
                }
            }
        } catch (Exception e) {
            logger.error("??????" + status + "????????????");
        }
        return null;
    }

    //????????????
    public String cancel(String url, String id, String childrenUser) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        InputStream re = null;
        HttpGet get = null;
        HttpPost post = null;
        String result = null;
        try {
            BasicCookieStore cookieStore = new BasicCookieStore();
            Integer timeout = Integer.parseInt("60000");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .build();
            client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            List<BasicNameValuePair> nameValueParis = new ArrayList<BasicNameValuePair>();
            nameValueParis.add(new BasicNameValuePair("orderId", id));
            nameValueParis.add(new BasicNameValuePair("codetype", "order"));
            nameValueParis.add(new BasicNameValuePair("childrenUser", childrenUser));
            post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(nameValueParis, "utf-8"));
            post.setConfig(requestConfig);
            response = client.execute(post);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject jo = JSON.parseObject(result);
            result = jo.getString("msg");
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (re != null) {
                    re.close();
                }
                if (response != null) {
                    response.close();
                }
                if (get != null) {
                    get.releaseConnection();
                }
                if (post != null) {
                    post.releaseConnection();
                }
                if (client != null) {
                    client.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return result;
    }


    /**
     * ?????????????????? String result = request.getParameter("result"); //??????????????????
     * message = request.getParameter("message"); //????????????
     * request.getParameter("price"); //???????????????
     * request.getParameter("childrenUser");//?????????
     * request.getParameter("newOrderId"); //???????????????????????????????????????
     * request.getParameter("orderId"); //???????????????
     * request.getParameter("isPassuccess"); //??????????????????
     * request.getParameter("isPassenge"); //??????????????????
     * request.getParameterValues("passengeMessage"); // ???????????????????????????
     * ?????????:??????##?????????##??????##???????????????
     * request.getParameter("payTransactionid"); //??????????????????????????????
     * payStatus = request.getParameter("payStatus"); //??????????????????
     * = request.getParameter("isSuccess"); //????????????
     * request.getParameter("isautoB2C"); //??????????????????
     * request.getParameter("ifUsedCoupon"); //??????????????????
     */
    public static String sendCreateOrderInfo(String result, String message, String price, String childrenUser,
                                             String newOrderId, String orderId, String isPassuccess, String isPassenge, String passengeMessage,
                                             String payStatus, String payTransactionid, String ifUsedCoupon, String isSuccess, String billNo, int requestType) {
        try {
            String orderUrl = "";
//            String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrl");
            StringBuffer buffer = new StringBuffer();
            buffer.append("<feeye-official>");
            buffer.append("<official>" + "FU" + "</official> ");
            buffer.append("<url>" + orderUrl + "</url> ");
            buffer.append("<type>0</type> ");
            buffer.append("<method>post</method>");
            buffer.append("<max>20</max> ");
            buffer.append("<encod>utf-8</encod> ");
            buffer.append("<params>");
            buffer.append("<param name='result'>" + result + "</param>"); //error ,  success(????????????)
            buffer.append("<param name='message'>" + message + "</param>");
            buffer.append("<param name='price'>" + price + "</param>");
            buffer.append("<param name='childrenUser'>" + childrenUser + "</param>");
            buffer.append("<param name='newOrderId'>" + newOrderId + "</param>");
            buffer.append("<param name='orderId'>" + orderId + "</param>");
            buffer.append("<param name='isPassuccess'>" + isPassuccess + "</param>");
            buffer.append("<param name='isPassenge'>" + isPassenge + "</param>");
            buffer.append("<param name='passengeMessageOther'>" + passengeMessage + "</param>");
            buffer.append("<param name='payStatus'>" + payStatus + "</param>");
            buffer.append("<param name='payTransactionid'>" + payTransactionid + "</param>");
            buffer.append("<param name='ifUsedCoupon'>" + ifUsedCoupon + "</param>");
            buffer.append("<param name='isSuccess'>" + isSuccess + "</param>");
            buffer.append("<param name='billNo'>" + billNo + "</param>");
            buffer.append("<param name='dicountMoney'>" + 0 + "</param>");
            buffer.append("<param name='requestType'>" + requestType + "</param>");
            buffer.append("</params>");
            buffer.append("</feeye-official>");

            logger.info(buffer.toString());
            String content = OfficialMain.setRequestParams(buffer.toString());
            if (content != null) {
                String rs1[] = content.split("#@_@#");
                if (rs1.length == 2) {
                    content = rs1[1];
                    return content;
                }
                if (rs1.length == 3) {
                    logger.info(rs1[2]);
                    return rs1[2];
                }
            }

        } catch (Exception e) {
            logger.error("error", e);
        }
        return null;
    }


    public static void main(String[] args) throws InterruptedException {
        String orderJson = "{\n" +
                "    \"account\": \"13682690632_feeye123\",\n" +
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
                "            \"airline\": \"FU\",\n" +
                "            \"arrival\": \"HRB\",\n" +
                "            \"cabin\": \"\",\n" +
                "            \"departure\": \"FOC\",\n" +
                "            \"departureDate\": \"2019-12-31\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"FU6638\",\n" +
                "            \"price\": \"920.0\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"32550175\",\n" +
                "    \"idCardNo\": \"410781197310154713\",\n" +
                "    \"idCardType\": \"IDCARD\",\n" +
                "    \"ifUsedCoupon\": false,\n" +
                "    \"isOutticket\": \"false\",\n" +
                "    \"linkMan\": \"??????\",\n" +
                "    \"matchCabin\": false,\n" +
                "    \"mobile\": \"13072660551\",\n" +
                "    \"orderNo\": \"406786710\",\n" +
                "    \"orderTime\": \"2019-03-11 00:12:13\",\n" +
                "    \"otheraccount\": \"fyyzm1_wo4feizhiyou\",\n" +
                "    \"ownername\": \"??????\",\n" +
                "    \"passengers\": [\n" +
                "        {\n" +
                "            \"birthday\": \"1984-07-19 00:00:00+08:00\",\n" +
                "            \"id\": \"37617002\",\n" +
                "            \"idcard\": \"411425198407198112\",\n" +
                "            \"passengerCardType\": \"?????????\",\n" +
                "            \"passengerName\": \"?????????\",\n" +
                "            \"passengerSex\": \"???\",\n" +
                "            \"passengerType\": \"??????\"\n" +
                "        },\n" +
//                "        {\n" +
//                "            \"birthday\": \"1975-11-29 00:00:00+08:00\",\n" +
//                "            \"id\": \"37617005\",\n" +
//                "            \"idcard\": \"532225197511291117\",\n" +
//                "            \"passengerCardType\": \"?????????\",\n" +
//                "            \"passengerName\": \"?????????\",\n" +
//                "            \"passengerSex\": \"???\",\n" +
//                "            \"passengerType\": \"??????\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"birthday\": \"1983-11-21 00:00:00+08:00\",\n" +
//                "            \"id\": \"37617004\",\n" +
//                "            \"idcard\": \"350781198311212039\",\n" +
//                "            \"passengerCardType\": \"?????????\",\n" +
//                "            \"passengerName\": \"?????????\",\n" +
//                "            \"passengerSex\": \"???\",\n" +
//                "            \"passengerType\": \"??????\"\n" +
//                "        },\n" +
                "        {\n" +
                "            \"birthday\": \"1983-11-30 00:00:00+08:00\",\n" +
                "            \"id\": \"37617003\",\n" +
                "            \"idcard\": \"350802198311306035\",\n" +
                "            \"passengerCardType\": \"?????????\",\n" +
                "            \"passengerName\": \"??????\",\n" +
                "            \"passengerSex\": \"???\",\n" +
                "            \"passengerType\": \"??????\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"payType\": \"xyk\",\n" +
                "    \"payerMobile\": \"18530203144\",\n" +
                "    \"qiangpiao\": \"2\",\n" +
                "    \"username\": \"fzybian\",\n" +
                "    \"ytype\": \"\"\n" +
                "}";

        FUCreateOrderService service = new FUCreateOrderService();

//        for (int i = 0; i <3 ; i++) {
//            String str = service.StartCreateOrder(orderJson, 1, 1);
//            System.out.println(str);
//            Thread.sleep(5000);
//        }


        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            //?????????SSL??????
            sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            logger.error("error", e);
        }
        org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            logger.error("error", e1);
        }
        //?????????
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];  //??????IP??????
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //??????IP??????
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(dailiyunProxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //?????????????????????
        String proxyUser = "feeyeapp";
        String proxyPass = "feeye789";
        //TODO ?????????????????????
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setProxy(dailiyunProxy)
                .setStaleConnectionCheckEnabled(true)
                .build();

        HttpClientContext context = HttpClientContext.create();
        context.setAuthCache(authCache);
        context.setRequestConfig(defaultRequestConfig);
        context.setCredentialsProvider(credsProvider);

        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setDefaultCredentialsProvider(credsProvider)
                .setDefaultCookieStore(cookieStore)
                .build();

        CloseableHttpResponse response = null;

        //json??????
        JSONObject jsonObject = JSON.parseObject(orderJson);
        OrderJson orderJsonBean = jsonObject.toJavaObject(OrderJson.class);

//        //?????????????????????
        for (int i = 0; i < 10; i++) {
            String queryFlightsBack = service.queryFlights(httpclient, defaultRequestConfig, orderJsonBean, "", context);
        }


//        //?????????????????????
//        String orderId = "791098";
//        String orderNo = "201904081021480951567";
//        String loginId = "FU1368269063219032823394773";
//        CloseableHttpClient client = HttpClients.createDefault();
//        RequestConfig config =  null;
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        String cookie ="JSESSIONID=A6C1203C725A431A8BCBE66DAD124480.d2";
//
//        service.uniPay(client, config, cookieStore, orderId, orderNo, loginId, "");

//        //??????????????????
//        String cookie = "JSESSIONID=9024C26A96D6F1AD958D6C33393D3B62.d2;";
//        CloseableHttpClient client = HttpClients.createDefault();
//        RequestConfig requestConfig = null;
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        String s = service.uniOrderDetail(client, requestConfig, cookieStore,"794644", "WUXINHAI");
//        if (StringUtils.isNotEmpty(s)) {
//            String[] ticketnos = s.split("@_@");
//            if (ticketnos.length == 2) {
//                String[] ticketCount = ticketnos[0].split("#_#");
////                if (!ticketnos[0].contains("null")) {
////                    sendCreateOrderInfo("success", "", orderAllMoney, childrenUser, "", order_id, "", "true",
////                            ticketnos[0], "????????????", ticketnos[1], "false", "true", billNo, requestType);
////                }
////            } else {
////                uniOrderDetailBack = uniOrderDetail(httpclient, defaultRequestConfig, cookieStore, orderId, loginId);
//            }
//        }

//        String returnUrl ="https://cashdesk.yeepay.com/bc-cashier/bcnewpc/request/10012416145/201903272004204336148913";
//        System.out.println(returnUrl.split("/")[6]);
//        System.out.println(returnUrl.split("/")[7]);

//        for (int i = 0; i < 50; i++) {
//            System.out.println(FingerPrintUtil.getDesc());
//        }
    }
}
