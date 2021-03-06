package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.Orderinfo;
import com.feeye.bean.Passergeninfo;
import com.feeye.util.FingerPrintUtil;
import com.feeye.util.slideImageUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
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
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xs
 * @description 8L????????????
 * @date 2019/10/10
 */
public class L8CreateOrderService {
    private static final Logger logger = LoggerFactory.getLogger(L8CreateOrderService.class);
    private static final int timeout = 30000;
    //    String dlyAccount = PropertiesUtils.getPropertiesValue("config", "dlyAccount");
    String csrfToken = ""; //??????????????????
    String dailiyunAccount = "feeyeapp:feeye789";

    private String StartCreateOrder(String orderJson, int retryCount, int requestType) {
        long nowDateTime = new Date().getTime(); //?????????????????????????????????????????????????????????????????????
        String createOrderBack = "";

//        String cancelUrl = PropertiesUtils.getPropertiesValue("config", "cancelUrl");
        if (StringUtils.isEmpty(orderJson)) {
            return "ERROR:???????????????";
        }
        logger.info("????????????????????????" + orderJson);

        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicScheme proxyAuth = new BasicScheme();
        try {
            proxyAuth.processChallenge(new BasicHeader("Proxy-Authenticate", "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            logger.error("error", e1);
        }
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(dailiyunProxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        String[] dailiyunAccounts = this.dailiyunAccount.split(":");
        credsProvider.setCredentials(new AuthScope(dailiyunProxy), new UsernamePasswordCredentials(dailiyunAccounts[0], dailiyunAccounts[1]));

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

        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if ((value != null) &&
                            (param.equalsIgnoreCase("timeout"))) {
                        return Long.parseLong(value) * 1000L;
                    }
                }
                return 60000L;
            }
        };

        SSLContext sslcontext = SSLContexts.createDefault();
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1.2"},
                null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

//        Registry <ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.create()
//                .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                .register("https", factory).build();

//        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
//        connectionManager.setMaxTotal(100);
//        connectionManager.setDefaultMaxPerRoute(20);

        SocketConfig socketConfig = SocketConfig.custom().
                setSoKeepAlive(false).setSoLinger(1)
                .setSoReuseAddress(true)
                .setSoTimeout(timeout)
                .setTcpNoDelay(true)
                .build();

        CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setKeepAliveStrategy(myStrategy)
//                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                .setDefaultCookieStore(cookieStore)
                .setDefaultSocketConfig(socketConfig)
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        String childrenUser = "";
        String billNo = "";
        String order_id = "";

        try {
            //json??????
            JSONObject jsonObject = JSON.parseObject(orderJson);
            Orderinfo orderJsonBean = jsonObject.toJavaObject(Orderinfo.class);
            order_id = orderJsonBean.getId();
            childrenUser = orderJsonBean.getUsername();
            try {
                billNo = orderJsonBean.getBillNo();
            } catch (Exception e) {
                logger.error("????????????billNo");
            }

            /*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "???????????????");
                return "????????????";
            }*/

//            sendOrderStatus(childrenUser, order_id, "????????????");
            String cookie = "";
            for (int i = 0; i < 10; i++) {
                cookie = login(httpclient, defaultRequestConfig, cookieStore, orderJsonBean);
                if (StringUtils.isEmpty(cookie)) {
                    continue;
                }
                if (cookie.contains("???????????????") || cookie.contains("????????????????????????") || cookie.contains("???????????????????????????")) {
                    logger.info(order_id + cookie);
                    if (retryCount > 3) {
                        retryCount++;
                        return StartCreateOrder(orderJson, 0, 0);
                    }
                }
                if (cookie.contains("ERROR")) {
//                    sendOrderStatus(childrenUser, order_id, cookie);
                    sendCreateOrderInfo("error", "???????????????" + cookie, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return cookie;
                }
                break;
            }

//            sendOrderStatus(childrenUser, order_id, "????????????");
            String queryFlightsBack = "";
            for (int i = 0; i < 5; i++) {
                queryFlightsBack = flightSearch(httpclient, defaultRequestConfig, context, cookie, orderJsonBean, "", 0);
                if (StringUtils.isEmpty(queryFlightsBack)) {
                    continue;
                }
                if (queryFlightsBack.contains("???????????????IP,????????????")) {
                    if (retryCount < 3) {
                        retryCount++;
                        Thread.sleep(3 * 1000);
                        return StartCreateOrder(orderJson, retryCount, 0);
                    }
                }
                if (queryFlightsBack.contains("ERROR")) {
//                    sendOrderStatus(childrenUser, order_id, SearchFlightBack);
                    sendCreateOrderInfo("error", "???????????????" + queryFlightsBack, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return queryFlightsBack;
                }
                break;
            }

/*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "???????????????");
                return "????????????";
            }*/

            //????????????
//            sendOrderStatus(childrenUser, order_id, "????????????");
            String parseFlightsBack = parseFlights(queryFlightsBack, orderJsonBean);
            logger.info(order_id + "??????????????????: " + parseFlightsBack);
            if (StringUtils.isEmpty(parseFlightsBack)) {
                return "ERROR:??????????????????";
            }
            if (parseFlightsBack.contains("ERROR")) {
//                    sendOrderStatus(childrenUser, order_id, parseFlightsBack);
                sendCreateOrderInfo("error", "???????????????" + parseFlightsBack, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return parseFlightsBack;
            }

            /*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "???????????????");
                return "????????????";
            }*/
//            sendOrderStatus(childrenUser, order_id, "????????????");
            String shoppingCartId = "";
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < 3; i++) {
                map = passenger(httpclient, defaultRequestConfig, cookie, parseFlightsBack, order_id);
                if (map == null || map.size() < 0) {
                    continue;
                }
//                if (StringUtils.isNotEmpty(map.get("ERROR"))) {
//                    logger.info(order_id + cookie);
//                    if (retryCount > 3) {
//                        retryCount++;
//                        return StartCreateOrder(orderJson, 0, 0);
//                    }
//                }
                if (StringUtils.isNotEmpty(map.get("ERROR"))) {
//                    sendOrderStatus(childrenUser, order_id, shoppingCartId);
                    sendCreateOrderInfo("error", "???????????????" + cookie, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return cookie;
                }
                break;
            }

            shoppingCartId = map.get("shoppingCartId");


                        /*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "???????????????");
                return "????????????";
            }*/
//            sendOrderStatus(childrenUser, order_id, "??????????????????????????????");
            String passengerInfoAddBack = "";
            for (int i = 0; i < 3; i++) {
                passengerInfoAddBack = passengerInfoAdd(httpclient, defaultRequestConfig, cookie, orderJsonBean, shoppingCartId);
                if (StringUtils.isEmpty(passengerInfoAddBack)) {
                    continue;
                }
                if (passengerInfoAddBack.contains("ERROR")) {
//                    sendOrderStatus(childrenUser, order_id, shoppingCartId);
                    sendCreateOrderInfo("error", "???????????????" + cookie, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return cookie;
                }
                break;
            }

                        /*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "???????????????");
                return "????????????";
            }*/
//            sendOrderStatus(childrenUser, order_id, "????????????");

            //???????????????????????????,??????????????????????????????????????????????????????????????????????????????,??????????????????
            float sysPrice = Float.parseFloat(orderJsonBean.getFlights().get(0).getPrice());
            float totalFare = Float.parseFloat(map.get("totalFare"));
            if (sysPrice != totalFare - 50) {
                String errorMessage = "????????????" + (totalFare - 50) + "????????????????????????" + sysPrice + "????????????????????????";
                logger.info(order_id + "???????????????" + errorMessage);
                sendCreateOrderInfo("error", errorMessage, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return "ERROR:" + errorMessage;
            }

            String orderId = "";
            for (int i = 0; i < 3; i++) {
                orderId = pay(httpclient, defaultRequestConfig, cookie);
                if (StringUtils.isEmpty(passengerInfoAddBack)) {
                    continue;
                }
                if (orderId.contains("ERROR")) {
//                    sendOrderStatus(childrenUser, order_id, shoppingCartId);
                    sendCreateOrderInfo("error", "???????????????" + cookie, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return orderId;
                }
                break;
            }

            createOrderBack = orderQuery(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderId);
            if (StringUtils.isEmpty(createOrderBack) || createOrderBack.contains("ERROR")) {
                return createOrderBack;
            }
            JSONObject createOrderObj = JSON.parseObject(createOrderBack);
            //???????????????
            String orderNo = createOrderObj.getJSONObject("data").getJSONArray("offers").getJSONObject(0).getJSONObject("detail").getString("orderNo");
            //????????????Id
            orderId = createOrderObj.getJSONObject("data").getJSONArray("offers").getJSONObject(0).getJSONObject("detail").getString("orderId");
            //?????????????????????
            String totalAmount = createOrderObj.getJSONObject("data").getJSONArray("offers").getJSONObject(0).getJSONObject("detail").getString("totalAmount");
            Map<String, String> orderMap = new HashMap<String, String>();
            orderMap.put("orderNo", orderNo);
            orderMap.put("orderId", orderId);
            orderMap.put("totalAmount", totalAmount);
            logger.info(order_id + "????????????????????????:" + orderNo + "???????????????:" + totalAmount);

//            sendOrderStatus(childrenUser, order_id, "????????????");
            if (StringUtils.isNotEmpty(orderNo) && StringUtils.isNotEmpty(totalAmount)) {
                String payUrl = payOrder(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderMap);
                logger.info(order_id + "??????????????????:" + payUrl);
                //TODO ????????????????????????

                //??????????????????
                createOrderBack = orderQuery(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderId);
                //??????????????????
                String orderInfo = parseOrderDetial(createOrderBack, orderJsonBean);

                //????????????
                int count = 30;
                List<Passergeninfo> passengers = orderJsonBean.getPassengers();
                while (count > 0) {
                    try {
                        if (StringUtils.isNotEmpty(orderInfo)) {
                            logger.info(order_id + "????????????????????????:" + orderInfo);
                            String[] ticketnos = orderInfo.split("@_@");
                            if (ticketnos.length == 2) {
                                sendOrderStatus(childrenUser, order_id, "????????????");
                                String[] ticketCount = ticketnos[0].split("#_#");
                                if ((ticketCount.length == passengers.size()) && (!ticketnos[0].contains("null"))) {
                                    sendCreateOrderInfo("success", "", totalAmount, childrenUser, orderNo, order_id, "", "true",
                                            ticketnos[0], "????????????", ticketnos[1], "false", "true", billNo, requestType);
                                    return "SUCCESS";
                                }
                            }
                        } else {
                            Thread.sleep(5 * 1000);
                            //??????????????????
                            createOrderBack = orderQuery(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderId);
                            //??????????????????
                            orderInfo = parseOrderDetial(createOrderBack, orderJsonBean);
                        }
                    } catch (Exception e) {
                        logger.error("error", e);
                    }
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        logger.error("error", e);
                    }
                    count--;
                }
                if (count <= 0) {
                    sendCreateOrderInfo("error", "?????????????????????", totalAmount, childrenUser, orderNo, order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return "SUCCESS#@@#?????????????????????";
                }
            } else {
                sendCreateOrderInfo("error", "???????????????????????????????????????????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return "ERROR:???????????????????????????????????????????????????";
            }
            return "SUCCESS";
        } catch (Exception e) {
            sendCreateOrderInfo("error", "????????????", "", childrenUser, "", order_id, "", "",
                    null, "", "", "false", "true", "", requestType);
            logger.error("error", e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (Exception e) {
                    logger.error("error", e);
                }
            }
        }
        return "SUCCESS";
    }

    /**
     * ??????
     *
     * @param httpclient
     * @param config
     * @param cookieStore
     * @param orderJson
     * @return
     */
    private String login(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, Orderinfo orderJson) {
        CloseableHttpResponse response = null;
        InputStream is = null;
        String cookie = "";
        String order_id = orderJson.getId();
        String childrenUser = orderJson.getUsername();
        String username = orderJson.getOtheraccount().split("_")[0];
        String password = orderJson.getOtheraccount().split("_")[1];
        try {
            HttpHost host = new HttpHost("www.luckyair.net", 80, "http");
            //????????????
            HttpGet get = new HttpGet("http://www.luckyair.net");
            get.setConfig(config);
            get.setHeader("Host", "www.luckyair.net");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(get);
            //??????cookie
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
            logger.info(order_id + " ?????????????????????cookie: " + cookie);
            response.close();

            //???????????????
            Long time = System.currentTimeMillis();
            get = new HttpGet("/api/validate/slideCaptcha?t=" + time);
            get.setConfig(config);
            get.setHeader("Host", "www.luckyair.net");
            get.setHeader("Cookie", cookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            response = httpclient.execute(host, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            Header[] headers = response.getAllHeaders();
            sb = new StringBuffer();
            for (int i = 0; i < headers.length; i++) {
                if ("set-Cookie".equalsIgnoreCase(headers[i].getName())) {
                    sb.append(headers[i].getValue().split(";")[0] + ";");
                }
            }
            cookie = sb.toString() + "csrfToken=" + csrfToken;
            response.close();

            //?????????????????????
            if (StringUtils.isNotEmpty(back) && back.contains("bgImage") && back.contains("blockImage")) {
                JSONObject jsonObject = JSON.parseObject(back);
                String bgImageUrl = jsonObject.getJSONObject("data").getString("bgImage");
                String blockImageUrl = jsonObject.getJSONObject("data").getString("blockImage");
                get = new HttpGet(bgImageUrl);
                RequestConfig defConfig = RequestConfig.custom()
                        .setSocketTimeout(timeout)
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setStaleConnectionCheckEnabled(true)
                        .build();
                get.setConfig(defConfig);
                get.setHeader("Host", "www.luckyair.net");
                get.setHeader("Cookie", cookie);
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                response = httpclient.execute(host, get);

                String session = UUID.randomUUID().toString().replaceAll("-", "");
                String fileUri = "C://testImg//" + session + ".jpg";
                is = response.getEntity().getContent();
                OutputStream os = new FileOutputStream(fileUri);
                IOUtils.copy(is, os);
                try {
                    is.close();
                    os.close();
                    response.close();
                } catch (Exception e) {
                    logger.error("error", e);
                }

                //?????????????????????
                HttpPost post = new HttpPost("/api/validate/slideCaptchaCheck");
                post.setConfig(config);
                //?????????????????????
                int left = slideImageUtil.findXDiffRectangeImage(fileUri);
                //?????????????????????
                File file = new File(fileUri);
                if (file.exists()) {
                    file.delete();
                }
                String param = "{\"left\":" + left + ",\"_csrf\":\"" + csrfToken + "\"}";
                StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
                post.setEntity(entity);
                post.setHeader("Host", "www.luckyair.net");
                post.setHeader("Origin", "http://www.luckyair.net");
                post.setHeader("Referer", "http://www.luckyair.net/login.html");
                post.setHeader("Accept", "application/json, text/plain, */*");
                post.setHeader("Content-Type", "application/json;charset=UTF-8");
                post.setHeader("Cookie", cookie);
                post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
                post.setHeader("Proxy-Connection", "keep-alive");
                response = httpclient.execute(host, post);
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                logger.info(order_id + " ???????????????????????????:" + back);
                if (StringUtils.isNotEmpty(back) && back.contains("true")) {
                    //??????cookie
                    headers = response.getAllHeaders();
                    sb = new StringBuffer();
                    for (int i = 0; i < headers.length; i++) {
                        if ("set-Cookie".equalsIgnoreCase(headers[i].getName())) {
                            sb.append(headers[i].getValue().split(";")[0] + ";");
                        }
                    }
                    cookie = sb.toString() + "csrfToken=" + csrfToken;
                    logger.info(order_id + "?????????????????????cookie: " + cookie);
                } else {
                    logger.info(order_id + "???????????????????????????,??????");
                    return null;
                }
            } else {
                logger.info(order_id + "???????????????????????????,??????");
                return null;
            }
            response.close();

            //??????
            HttpPost post = new HttpPost("/api/member/login");
            post.setConfig(config);
            String[] userAccount = orderJson.getAccount().split("_");
            JSONObject loginParam = new JSONObject();
            loginParam.put("username", userAccount[0]);
            loginParam.put("password", userAccount[1]);
            loginParam.put("type", "nlogin");  //????????????
//            String desc = FingerPrintUtil.getDesc();
            loginParam.put("desc", "coBPtm4BZy5Ly7E1arnlj7fr8kmJ5AHM0FEfLlBAKvYXYl/elXICeY115jwY6dt2");
            loginParam.put("_csrf", csrfToken);
            StringEntity entity = new StringEntity(loginParam.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("Referer", "http://www.luckyair.net/login.html");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Cookie", cookie);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info(order_id + " ????????????:" + back);
            if (StringUtils.isNotEmpty(back) && back.contains("????????????")) {
                JSONObject json = JSON.parseObject(back);
                String message = json.getString("message");
                System.out.println(order_id + message);
                logger.info(order_id + message);
                return "ERROR:" + message;
            } else if (StringUtils.isNotEmpty(back) && back.contains("10001")) {
                logger.info(order_id + "??????????????????,??????");
                return null;
            } else {
                //??????cookie
                headers = response.getAllHeaders();
                sb = new StringBuffer();
                for (int i = 0; i < headers.length; i++) {
                    if ("set-Cookie".equalsIgnoreCase(headers[i].getName())) {
                        sb.append(headers[i].getValue().split(";")[0] + ";");
                    }
                }
                cookie = sb.toString() + "csrfToken=" + csrfToken;
                logger.info(order_id + "?????????????????????cookie: " + cookie);
                return cookie;
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
     * @param httpclient
     * @param config
     * @param cookie
     * @param orderJson
     * @param verifyCode ?????????
     * @param retry      ????????????
     * @return
     */
    private String flightSearch(CloseableHttpClient httpclient, RequestConfig config, HttpClientContext context, String cookie,
                                Orderinfo orderJson, String verifyCode, int retry) {
        CloseableHttpResponse response = null;
        String order_id = orderJson.getId();
        Map<String, String> map = new HashMap<>(); //???cookie???map
        String back = "";
        HttpHost host = new HttpHost("www.luckyair.net", 80, "http");
        try {
            if (StringUtils.isNotEmpty(cookie)) {
                try {
                    String[] c = cookie.split(";");
                    for (int i = 0; i < c.length; i++) {
                        String[] split = c[i].split("=");
                        if (split[0].contains("N_V_IN")) {
                            String regEx = "N_V_IN=(.*?);";
                            map.put("N_V_IN", getSubUtil(c[i] + ";", regEx));
                        }
                        map.put(split[0], split[1]);
                    }
                } catch (Exception e) {
                    logger.error("error", e);
                }
            }

            String depCode = orderJson.getFlights().get(0).getDeparture();
            String arrCode = orderJson.getFlights().get(0).getArrival();
            String flightDate = orderJson.getFlights().get(0).getDepartureDate();

            HttpPost post = new HttpPost("/api/recommand/query");
            post.setConfig(config);
            String param = "{\"_csrf\":\"" + csrfToken + "\"}";
            StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.luckyair.net");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post, context);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                if ("Set-Cookie".equals(headers[i].getName())) {
                    if (headers[i].getValue().contains("N_V_IN")) {
                        String nvinCookie = headers[i].getValue().split(";")[0] + ";";
                        String regEx = "N_V_IN=(.*?);";
                        getSubUtil(nvinCookie, regEx);
                        map.put("N_V_IN", getSubUtil(nvinCookie, regEx));
                    }
                }
            }
            cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
            response.close();

            post = new HttpPost("/api/flight/fareCalendar");
            post.setConfig(config);
            param = "{\"depCode\":\"" + depCode + "\",\"arrCode\":\"" + arrCode + "\",\"dateStart\":\"" + flightDate + "\",\"dateEnd\":\"2019-11-30\",\"airlineCode\":\"8L\",\"_csrf\":\"" + csrfToken + "\"}";
            entity = new StringEntity(param.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.luckyair.net");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post, context);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                if ("Set-Cookie".equals(headers[i].getName())) {
                    if (headers[i].getValue().contains("N_V_IN")) {
                        String nvinCookie = headers[i].getValue().split(";")[0] + ";";
                        String regEx = "N_V_IN=(.*?);";
                        getSubUtil(nvinCookie, regEx);
                        map.put("N_V_IN", getSubUtil(nvinCookie, regEx));
                    }
                }
            }
            cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
            response.close();

            //????????????????????????
            HttpGet get = new HttpGet("/flight/search.html");
            get.setConfig(config);
            get.setHeader("Host", "www.luckyair.net");
            get.setHeader("Content-Type", "application/x-www-form-urlencoded");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            get.setHeader("Cookie", cookie);
            response = httpclient.execute(host, get);
            headers = response.getAllHeaders();
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            for (int i = 0; i < headers.length; i++) {
                if ("Set-Cookie".equals(headers[i].getName())) {
                    if (headers[i].getValue().contains("N_V_IN")) {
                        String nvinCookie = headers[i].getValue().split(";")[0] + ";";
                        String regEx = "N_V_IN=(.*?);";
                        getSubUtil(nvinCookie, regEx);
                        map.put("N_V_IN", getSubUtil(nvinCookie, regEx));
                    }
                }
            }
            cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
            response.close();

            JSONObject paramObj = new JSONObject();
            JSONObject passenger = new JSONObject();
            int AdtNum = 0;
            int CnnNum = 0;
            int InfNum = 0;
            for (Passergeninfo pb : orderJson.getPassengers()) {
                JSONObject passengersObj = new JSONObject();
                if ("??????".equals(pb.getPassengerType())) {
                    AdtNum++;
                }
                if ("??????".equals(pb.getPassengerType())) {
                    CnnNum++;
                }
                if ("??????".equals(pb.getPassengerType())) {
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
//            paramObj.put("isInternational", false);
            paramObj.put("passenger", passenger);
            paramObj.put("originDestinations", originDestinations);
            paramObj.put("_csrf", csrfToken);
            //????????????
            String desc = FingerPrintUtil.getDesc();
            paramObj.put("desc", desc);
            //?????????????????????
            if (StringUtils.isNotEmpty(verifyCode)) {
                paramObj.put("vc", verifyCode);
            }
            post = new HttpPost("http://www.luckyair.net/api/flight/search");
            post.setConfig(config);
            entity = new StringEntity(paramObj.toString(), Charset.forName("UTF-8"));
            System.out.println(order_id + " ??????????????????:" + paramObj.toString());
            logger.info(order_id + " ??????????????????:" + paramObj.toString());
            post.setEntity(entity);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.luckyair.net/flight/search.html");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post, context);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info(order_id + " ??????????????????:" + back);
            if (back.contains("10001")) {
                if (retry < 3) {
                    retry++;
                    return flightSearch(httpclient, config, context, cookie, orderJson, "", retry);
                } else {
                    return "REEOR: ???????????????IP,????????????";
                }
            }
            if (back.contains("10000")) {
                //?????????????????????
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
                //???????????????????????????
                File imageFile = new File(fileUri);
                FileEntity fileEntity = new FileEntity(imageFile);
                String codeUrl = "http://127.0.0.1:1111";
                HttpPost codePost = new HttpPost(codeUrl);
                codePost.setEntity(fileEntity);
                response = httpclient.execute(codePost);
                verifyCode = EntityUtils.toString(response.getEntity());
                System.out.println("?????????????????????:" + verifyCode);
                logger.info(order_id + "?????????????????????:" + verifyCode);
                return flightSearch(httpclient, config, context, cookie, orderJson, verifyCode, retry);
            }
            if (back.contains("10002")) {
                return flightSearch(httpclient, config, context, cookie, orderJson, "", retry);
            }
            return back;
        } catch (NoHttpResponseException e) {
            logger.error("error", e);
            return "retry";
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
        return null;
    }


    /**
     * ????????????
     *
     * @param back
     * @param orderJson
     * @return
     */
    private static String parseFlights(String back, Orderinfo orderJson) {
        try {
            if (StringUtils.isNotEmpty(back)) {
                String flightNo = orderJson.getFlights().get(0).getFlightNo();
                float price = Float.valueOf(orderJson.getFlights().get(0).getPrice());
                String departure = orderJson.getFlights().get(0).getDeparture();
                String arrival = orderJson.getFlights().get(0).getArrival();
                String departureDate = orderJson.getFlights().get(0).getDepartureDate();
                String cabin = orderJson.getFlights().get(0).getCabin();

                int AdtNum = 0;
                int ChdNum = 0;
                int InfNum = 0;
                for (Passergeninfo pb : orderJson.getPassengers()) {
                    JSONObject passengersObj = new JSONObject();
                    if ("??????".equals(pb.getPassengerType())) {
                        AdtNum++;
                    }
                    if ("??????".equals(pb.getPassengerType())) {
                        ChdNum++;
                    }
                    if ("??????".equals(pb.getPassengerType())) {
                        InfNum++;
                    }
                }

                JSONObject jsonObject = JSON.parseObject(back);
                if (jsonObject.containsKey("data")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data.containsKey("offers")) {
                        JSONObject offers = data.getJSONArray("offers").getJSONObject(0);
                        if (offers.containsKey("detail")) {
                            JSONArray itinerarys = offers.getJSONObject("detail").getJSONArray("itinerarys");
                            for (int i = 0; i < itinerarys.size(); i++) {
                                JSONObject json = itinerarys.getJSONObject(i);
                                JSONObject orgDsts = json.getJSONArray("orgDsts").getJSONObject(0);
                                JSONArray productPrices = json.getJSONArray("productPrices");
                                for (int j = 0; j < productPrices.size(); j++) {
                                    JSONObject services = productPrices.getJSONObject(j).getJSONObject("detail").getJSONArray("services").getJSONObject(0);
                                    String flightNumber = services.getString("flightNo");
                                    String totalItineraryFare = productPrices.getJSONObject(j).getJSONObject("detail").getString("totalItineraryFare");
                                    //???????????????
                                    if (!flightNo.equalsIgnoreCase(flightNumber)) {
                                        continue;
                                    }
                                    //????????????
                                    if (price != Float.parseFloat(totalItineraryFare)) {
                                        continue;
                                    }
                                    String offerItemId = productPrices.getJSONObject(j).getJSONObject("detail").getString("offerItemId");
                                    String serviceId = services.getString("serviceId");
                                    //????????????
                                    JSONArray paramArr = new JSONArray();
                                    JSONObject paramObj = new JSONObject();
                                    paramArr.set(0, paramObj);
                                    paramObj.put("type", "OW"); //??????
                                    paramObj.put("depCode", orgDsts.getString("departureCode"));
                                    paramObj.put("arrCode", orgDsts.getString("arrivalCode"));
                                    paramObj.put("depTime", orgDsts.getString("departureDate"));
                                    paramObj.put("departureDate", orgDsts.getString("departureDate"));
                                    paramObj.put("departureTime", orgDsts.getString("departureTime"));
                                    paramObj.put("arrivalDate", orgDsts.getString("arrivalDate"));
                                    paramObj.put("arrivalTime", orgDsts.getString("arrivalTime"));
                                    // ADT|1#CHD|0#INF|0
                                    String passengers = "ADT|" + AdtNum + "#CHD|" + ChdNum + "#INF|" + InfNum;
                                    paramObj.put("passengers", passengers);

                                    JSONArray offerItemIds = new JSONArray();
                                    JSONObject offerItemObj = new JSONObject();
                                    offerItemIds.set(0, offerItemObj);
                                    offerItemObj.put("offerItemId", offerItemId);
                                    JSONArray offerServicesArr = new JSONArray();
                                    JSONObject offerServicesObj = new JSONObject();
                                    offerServicesObj.put("offerServiceId", serviceId);
                                    offerServicesArr.set(0, offerServicesObj);
                                    offerItemObj.put("offerServices", offerServicesArr);
                                    paramObj.put("offerItemIds", offerItemIds);
                                    return paramArr.toString();
                                }
                            }
                        }
                    }
                }
            } else {
                return "ERROR:???????????????????????????";
            }
        } catch (Exception e) {
            logger.info("error", e);
        }
        return null;
    }


    /**
     * ????????????
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param parseFlightsBack
     * @return
     */
    private Map<String, String> passenger(CloseableHttpClient httpclient, RequestConfig config, String cookie, String parseFlightsBack, String order_id) {
        CloseableHttpResponse response = null;
        Map<String, String> map = new HashMap<>();
        try {
            HttpHost host = new HttpHost("www.luckyair.net", 80, "http");
            HttpPost post = new HttpPost("/flight/passenger.html");
            post.setConfig(config);
            List<BasicNameValuePair> nameValueParis = new ArrayList<BasicNameValuePair>();
            nameValueParis.add(new BasicNameValuePair("_csrf", csrfToken));
            nameValueParis.add(new BasicNameValuePair("products", parseFlightsBack));
            post.setEntity(new UrlEncodedFormEntity(nameValueParis, "utf-8"));
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Referer", "http://www.luckyair.net/flight/search.html");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info(order_id + " ??????????????????: " + back);
            if (StringUtils.isNotEmpty(back) && back.contains("shoppingCartId")) {
                /*
                var shoppingCart = {"shoppingCartId":"0a0d89a6121f439ebaecddf932ff3f85","totalFare":"590","currency":"CNY","offers":[{"type":"Flight","detail":{"passengers":[],"itinerarys":[{"itineraryType":"OW","stopTime":null,"orgDsts":[{"serviceId":"OW-TICKET-0e62ecef50fc4aec891d32c18b36737f#OW-KMG-SZX-20191201-8L9979-WEB-U#SRV-1","flightNo":"8L9979","planeType":null,"airlineCode":"8L","airlineName":"????????????","stopCityCode":null,"stopCityName":null,"departureCode":"KMG","departureName":"??????","departureDate":"2019-12-01","departureTime":"2019-12-01 17:25:00","departureTerminal":"--","departureAirPortName":"????????????????????????","arrivalCode":"SZX","arrivalName":"??????","arrivalAirPortName":"????????????????????????","arrivalTerminal":"T3","arrivalDate":"2019-12-01","arrivalTime":"2019-12-01 19:35:00","productCode":"WEB","cabinCode":"U","reCabinCode":null,"mileage":"1245"}]}],"ticketCartItems":[{"offerItemType":"TICKET","detail":{"offerItemId":"OW-TICKET-0e62ecef50fc4aec891d32c18b36737f#OW-KMG-SZX-20191201-8L9979-WEB-U","serviceId":"OW-TICKET-0e62ecef50fc4aec891d32c18b36737f#OW-KMG-SZX-20191201-8L9979-WEB-U#SRV-1","departureCode":"KMG","arrivalCode":"SZX","deductible":null,"passengerPrices":[{"totalFare":"540","currency":"CNY","count":"1","passengerType":"ADT"}],
                "taxs":[{"currency":"CNY","code":"YQ","amount":0,"name":"??????"},{"currency":"CNY","code":"CN","amount":50,"name":"??????"}]}}],"ancillaryCartItems":null}}]}
                 */
                String regex = "var shoppingCart = \\{\"shoppingCartId\":\"(.*?)\",\"totalFare\":\"(.*?)\",";
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(back);
                while (m.find()) {
                    if (StringUtils.isNotEmpty(m.group(1))) {
                        String shoppingCartId = m.group(1);
                        map.put("shoppingCartId", shoppingCartId);
                    }
                    if (StringUtils.isNotEmpty(m.group(2))) {
                        String totalFare = m.group(2);
                        map.put("totalFare", totalFare);
                    }
                    return map;
                }
            } else if (StringUtils.isNotEmpty(back) && back.contains("chooseError")) {
//                var chooseError = {"message":"???????????????????????????????????????????????????????????????????????????????????????????????????"}
                String regex = "var chooseError = \\{\"message\":\"(.*?)\"";
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(back);
                while (m.find()) {
                    if (StringUtils.isNotEmpty(m.group(1))) {
                        String errrorMessage = m.group(1);
                        map.put("ERROR", errrorMessage);
                    }
                }
                return map;
            } else {
                map.put("ERROR", "??????????????????");
                return map;
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
     * @param cookie
     * @param id
     * @return
     */
    private String passengerInfoAdd(CloseableHttpClient httpclient, RequestConfig config, String cookie, Orderinfo orderJson, String shoppingCartId) {
        CloseableHttpResponse response = null;
        String order_id = orderJson.getId();
        try {
            HttpHost host = new HttpHost("www.luckyair.net", 80, "http");
            HttpPost post = new HttpPost("/api/order/passengerInfo/add");
            post.setConfig(config);
            //????????????
            JSONObject param = new JSONObject();
            JSONArray passengersArr = new JSONArray();
            for (Passergeninfo pb : orderJson.getPassengers()) {
                JSONObject passengersObj = new JSONObject();
                if ("??????".equals(pb.getPassengerType())) {
                    passengersObj.put("passengerType", "ADT");
                } else if ("??????".equals(pb.getPassengerType())) {
                    passengersObj.put("passengerType", "CHD");
                } else if ("??????".equals(pb.getPassengerType())) {
                    passengersObj.put("passengerType", "INF");
                }
                String passengerName = pb.getPassengerName();
                passengersObj.put("fullName", pb.getPassengerName().replaceAll(" ", ""));
                if ("?????????".equals(pb.getPassengercardType())) {
                    passengersObj.put("certificateType", "NI");
                } else if ("??????".equals(pb.getPassengercardType())) {
                    passengersObj.put("certificateType", "PP");
                } else if ("??????".equals(pb.getPassengercardType())) {
                    passengersObj.put("certificateType", "ID");
                }
                passengersObj.put("certificateNo", pb.getIdcard());
                passengersObj.put("birthday", pb.getBirthday().substring(0, 10));
                // "birthdayObj":"1992-05-14T16:00:00.000Z",
                passengersObj.put("birthdayObj", pb.getBirthday().substring(0, 10));
                passengersObj.put("icommPassenger", true);
                passengersArr.add(passengersObj);
            }
            param.put("passengers", passengersArr);
            //???????????????
            JSONObject customer = new JSONObject();
            String linkMan = orderJson.getLinkMan(); //?????????
            String mobile = orderJson.getMobile(); //???????????????
            customer.put("fullName", linkMan);
            customer.put("areaCode", "86");
            customer.put("mobile", mobile);
            customer.put("email", null);
            param.put("customer", customer);
            param.put("shoppingCartId", shoppingCartId);
            param.put("_csrf", csrfToken);
            StringEntity entity = new StringEntity(param.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.luckyair.net/flight/passenger.html");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(order_id + "??????????????????: " + back);
            logger.info(order_id + "??????????????????: " + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success"))) {
                return back;
            } else if ("false".equals(jsonObject.getString("success"))) {
                String message = jsonObject.getString("message");
                return "ERROR: " + message;
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
     * @param httpclient
     * @param config
     * @param cookie
     * @return
     */
    private String pay(CloseableHttpClient httpclient, RequestConfig config, String cookie) {
        CloseableHttpResponse response = null;
        String submitResult = "";
        try {
            HttpHost host = new HttpHost("www.luckyair.net", 80, "http");
            HttpPost post = new HttpPost("/pay/pay.html");
            post.setConfig(config);
            List<BasicNameValuePair> nameValueParis = new ArrayList<BasicNameValuePair>();
            nameValueParis.add(new BasicNameValuePair("_csrf", csrfToken));
            String desc = FingerPrintUtil.getDesc();
            nameValueParis.add(new BasicNameValuePair("desc", desc));
            post.setEntity(new UrlEncodedFormEntity(nameValueParis, "utf-8"));
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Referer", "http://www.luckyair.net/extrafollow/book.html");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("??????pay????????????: " + back);
            if (StringUtils.isNotEmpty(back) && back.contains("submitResult") && back.contains("true")) {
//                var submitResult = {"success":true,"data":82507208}
                String regex = "var submitResult = \\{\"success\":true,\"data\":(.*?)\\}";
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(back);
                while (m.find()) {
                    if (StringUtils.isNotEmpty(m.group(1))) {
                        submitResult = m.group(1);
                        return submitResult;
                    }
                }
            } else if (StringUtils.isNotEmpty(back) && back.contains("?????????????????????,?????????????????????")) {
//                var shoppingCart = {"success":false,"message":"?????????????????????,?????????????????????","code":"100010006"}
                return "ERROR:?????????????????????,?????????????????????";
            } else {
                return "ERROR:??????????????????";
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
     * @param cookie
     * @param orderjson
     * @param orderId    ????????????id
     * @return
     */
    private String orderQuery(CloseableHttpClient httpclient, RequestConfig config, String cookie, Orderinfo orderjson, String orderId) {
        CloseableHttpResponse response = null;
        String order_id = orderjson.getId();
        try {
            String param = "{\"airline\":\"8L\",\"orderId\":" + orderId + ",\"_csrf\":\"" + csrfToken + "\"}";
            HttpHost host = new HttpHost("www.luckyair.net", 80, "http");
            HttpPost post = new HttpPost("/api/order/query");
            post.setConfig(config);
            StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.luckyair.net/pay/pay.html");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("??????????????????:" + back);
            logger.info(order_id + "??????????????????" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("offers")) {
                return back;
            } else {
                logger.info(order_id + "??????????????????:" + back);
                return "ERROR:??????????????????";
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
     * @param back
     * @param orderjson
     * @return
     */
    private String parseOrderDetial(String back, Orderinfo orderjson) {
        Map<String, String> map = new HashMap<>();
        try {
            if (StringUtils.isNotEmpty(back)) {
                JSONObject json = JSON.parseObject(back);
                StringBuffer sb = new StringBuffer();
                String order_id = orderjson.getId();
                //???????????????
                String orderNo = json.getJSONObject("data").getJSONArray("offers").getJSONObject(0).getJSONObject("detail").getString("orderNo");
                //???????????????
                String paymentNo = json.getJSONObject("data").getJSONArray("offers").getJSONObject(0).getJSONObject("detail").getJSONArray("payments").getJSONObject(0).getString("paymentNo");
                //?????????????????????
                JSONArray travelersArr = json.getJSONObject("data").getJSONArray("offers").getJSONObject(0).getJSONObject("detail").getJSONArray("travelers");
                List<Passergeninfo> passengers = orderjson.getPassengers();
                for (int i = 0; i < passengers.size(); i++) {
                    String passengerName = passengers.get(i).getPassengerName().replace(" ", "");
                    //??????????????????????????????????????????
                    if (passengerName.equalsIgnoreCase(travelersArr.getJSONObject(i).getString("fullName"))) {
                        //?????????
                        String idcard = passengers.get(i).getIdcard();
                        //??????
                        String ticketNumber = travelersArr.getJSONObject(i).getJSONArray("flightSegments").getJSONObject(0).getString("ticketNo");
                        sb.append(passengerName).append("##").append(idcard).append("##").append(ticketNumber)
                                .append("##").append(paymentNo).append("#_#");
                    }
                }
                logger.info(order_id + sb.toString());
                return sb.toString() + "@_@" + paymentNo;
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
        return "";
    }


    /**
     * ????????????
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param orderJson
     * @param paramMap
     * @return
     */
    private String payOrder(CloseableHttpClient httpclient, RequestConfig config, String cookie, Orderinfo orderjson, Map paramMap) {
        CloseableHttpResponse response = null;
        String order_id = orderjson.getId();
        String locationValue = "";
        try {
//            {"orderId":82497790,"orderNo":"20191031191247616546","payType":"YEEPAY","amount":590,"bankId":"","orderType":"TICKET","extraCommonParam":"orderId:82497790","returnUrl":"/order/payResult.html","_csrf":"BFTS9C--amk1HzvHhI6bHDmB"}
            //????????????
            JSONObject paramObj = new JSONObject();
            paramObj.put("orderId", paramMap.get("orderId"));
            paramObj.put("orderNo", paramMap.get("orderNo"));
            paramObj.put("payType", "YEEPAY"); //????????????
            paramObj.put("amount", paramMap.get("totalAmount"));
            paramObj.put("bankId", "");
            paramObj.put("orderType", "TICKET");
            paramObj.put("extraCommonParam", "orderId:" + paramMap.get("orderId"));
            paramObj.put("returnUrl", "/order/payResult.html");
            paramObj.put("_csrf", csrfToken);
            HttpHost host = new HttpHost("www.luckyair.net", 80, "http");
            HttpPost post = new HttpPost("/api/payment/payOrder");
            post.setConfig(config);
            StringEntity entity = new StringEntity(paramObj.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.luckyair.net/pay/pay.html");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("??????????????????: " + back);
            logger.info(order_id + "??????????????????: " + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("url")) {
                JSONObject json = JSON.parseObject(back);
                locationValue = json.getJSONObject("data").getString("url");
                if (StringUtils.isNotEmpty(locationValue)) {
                    RequestConfig defconfig = RequestConfig.custom()
                            .setSocketTimeout(timeout)
                            .setConnectTimeout(timeout)
                            .setConnectionRequestTimeout(timeout)
                            .setRedirectsEnabled(false) //?????????????????????
                            .setStaleConnectionCheckEnabled(true)
                            .build();
                    // ???????????????
                    HttpHost yeepayHost = new HttpHost("www.yeepay.com", 443, "https");
                    HttpGet get = new HttpGet(locationValue);
                    get.setConfig(defconfig);
                    get.setHeader("Host", "www.yeepay.com");
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
                    response = httpclient.execute(yeepayHost, get);
                    Header[] locationAfter = response.getHeaders("Location");
                    for (int i = 0; i < locationAfter.length; i++) {
                        locationValue = locationAfter[i].getValue();
                        logger.info(order_id + "???????????????Location:" + locationValue);
                    }
                    if (StringUtils.isEmpty(locationValue)) {
                        logger.info(order_id + "???????????????" + locationValue);
                        logger.info(order_id + "???????????????" + back);
                        return "";
                    }
                    // ???????????????
                    get = new HttpGet(locationValue);
                    get.setConfig(defconfig);
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
                    response = httpclient.execute(get);
                    locationAfter = response.getHeaders("Location");
                    for (int i = 0; i < locationAfter.length; i++) {
                        locationValue = locationAfter[i].getValue();
                        logger.info(order_id + "???????????????Location:" + locationValue);
                    }
                    if (StringUtils.isEmpty(locationValue)) {
                        logger.info(order_id + "???????????????" + locationValue);
                        logger.info(order_id + "???????????????" + back);
                        return "";
                    }
                    // ???????????????
                    get = new HttpGet(locationValue);
                    get.setConfig(defconfig);
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
                    response = httpclient.execute(get);
                    back = EntityUtils.toString(response.getEntity(), "utf-8");
                    if (StringUtils.isEmpty(locationValue) || !back.contains("?????????????????????")) {
                        logger.info(order_id + "???????????????" + locationValue);
                        logger.info(order_id + "???????????????" + back);
                        return "";
                    }
                }
                return locationValue;
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

    //????????????????????????
    public static String sendOrderStatus(String childrenUser, String orderId, String status) {
        try {
            String orderUrl = "";
//            String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrlStatus");
            StringBuffer buffer = new StringBuffer();
            buffer.append("<feeye-official>");
//            buffer.append("<official>" + Constant.8L.toString() + "</official> ");
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
    public static String sendCreateOrderInfo(String result, String message, String price, String childrenUser, String newOrderId, String orderId,
                                             String isPassuccess, String isPassenge, String passengeMessage, String payStatus, String payTransactionid,
                                             String ifUsedCoupon, String isSuccess, String billNo, int requestType) {
        try {
            String orderUrl = "";
//            String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrl");
            StringBuffer buffer = new StringBuffer();
            buffer.append("<feeye-official>");
            buffer.append("<official>" + "8L" + "</official> ");
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


    public static void main(String[] args) throws IOException {
        String orderJson = "{\n" +
                "    \"account\": \"feeye123456_feeye123.\",\n" +
                "    \"airline\": \"8L\",\n" +
                "    \"billNo\": \"FY191105200749157295566996587784\",\n" +
                "    \"childrenMobile\": \"18617070230\",\n" +
                "    \"code\": \"\",\n" +
                "    \"codePassword\": \"\",\n" +
                "    \"creditNo\": \"feeye123\",\n" +
                "    \"cvv\": \"13532989123\",\n" +
                "    \"drawerType\": \"GW\",\n" +
                "    \"email\": \"754118982@qq.com\",\n" +
                "    \"flights\": [\n" +
                "        {\n" +
                "            \"airline\": \"8L\",\n" +
                "            \"arrival\": \"KMG\",\n" +
                "            \"cabin\": \"A\",\n" +
                "            \"departure\": \"HAK\",\n" +
                "            \"departureDate\": \"2020-02-22\",\n" +
                "            \"departureTime\": \"15:40\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"8L9974\",\n" +
                "            \"price\": \"460\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"42289336\",\n" +
                "    \"ifUsedCoupon\": false,\n" +
                "    \"isOutticket\": \"true\",\n" +
                "    \"linkMan\": \"??????\",\n" +
                "    \"matchCabin\": false,\n" +
                "    \"mobile\": \"15897736493\",\n" +
                "    \"orderNo\": \"1572939830102\",\n" +
                "    \"orderTime\": \"2019-11-05 20:07:06\",\n" +
                "    \"otheraccount\": \"112069_/JfzhBV4r5IXtkhj7zBfxJblWP+G5Nn5\",\n" +
                "    \"passengers\": [\n" +
                "        {\n" +
                "            \"birthday\": \"1957-03-29\",\n" +
                "            \"id\": \"50737339\",\n" +
                "            \"idcard\": \"630104195703292515\",\n" +
                "            \"passengerCardType\": \"?????????\",\n" +
                "            \"passengerName\": \"??????\",\n" +
                "            \"passengerType\": \"??????\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"birthday\": \"2008-07-29\",\n" +
                "            \"id\": \"50737341\",\n" +
                "            \"idcard\": \"630103200807290044\",\n" +
                "            \"passengerCardType\": \"?????????\",\n" +
                "            \"passengerName\": \"?????????\",\n" +
                "            \"passengerType\": \"??????\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"birthday\": \"1957-11-20\",\n" +
                "            \"id\": \"50737340\",\n" +
                "            \"idcard\": \"630103195711201621\",\n" +
                "            \"passengerCardType\": \"?????????\",\n" +
                "            \"passengerName\": \"?????????\",\n" +
                "            \"passengerType\": \"??????\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"payType\": \"ybzh\",\n" +
                "    \"payerMobile\": \"0M82j2zo27IXXwYd5C0C2K469TvV1uI29l780eM6iy2AI1e8c5U33znZtwz6\",\n" +
                "    \"qiangpiao\": \"\",\n" +
                "    \"username\": \"policytest\",\n" +
                "    \"ytype\": \"????????????\"\n" +
                "}";
        JSONObject jsonObject = JSON.parseObject(orderJson);
        Orderinfo orderinfo = jsonObject.toJavaObject(Orderinfo.class);
        L8CreateOrderService service = new L8CreateOrderService();
        service.StartCreateOrder(orderJson, 0, 0);


//        String s = "<form name=\"validataForm\" role=\"search\" action=\"checkSignCode\" method=\"post\">\n" +
//                "<div style=\"margin: 10px auto;display: none;\" id=\"prevent_div\"></div>\n" +
//                "<input type=\"hidden\" name=\"redirect\" value=\"aHR0cDovL3d3dy5sdWNreWFpci5uZXQvcGF5L3BheS5odG1sP2Rlc2M9Y29CUHRtNEJaeTVMeTdFMWFybmxqN0NYVEtQNTN5OFpFWkVhTkxTZDZ3dXNJSnhmaTVMeXByVjJzUmJFZk5nUCZfY3NyZj0ydlhVbWRIWVd5Y1lPSEhGUUQ1UXY1YnQm\" /><br/>\n" +
//                "<input type=\"hidden\" name=\"verify\" value=\"\" /><br/>\n" +
//                "<input type=\"hidden\" name=\"s_p_type\" value=\"32041\" /><br/>\n" +
//                "<input type=\"hidden\" name=\"validate\" value=\"\" /><br/>\n" +
//                "<input type=\"hidden\" name=\"flag\" value=\"1\" /><br/>\n" +
//                "</form>";
//        Document parse = Jsoup.parse(s);
//        String d = parse.select("input[name='s_p_type']").val();
//        String partten = "http://www.luckyair.net/hnatravel/signcode.jsp?flag=1&redirect=aHR0cDovL3d3dy5sdWNreWFpci5uZXQvcGF5L3BheS5odG1sP2Rlc2M9Y29CUHRtNEJaeTVMeTdFMWFybmxqN0NYVEtQNTN5OFpFWkVhTkxTZDZ3dXNJSnhmaTVMeXByVjJzUmJFZk5nUCZfY3NyZj0ydlhVbWRIWVd5Y1lPSEhGUUQ1UXY1YnQm";
//        partten.split("=");
//        System.out.println(d);

        //??????????????????????????????
        String ip_port = DailiyunService.getRandomIp(50);
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
                return 60 * 1000;//?????????????????????????????????????????????60s
            }
        };
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setDefaultCookieStore(cookieStore)
                .setKeepAliveStrategy(myStrategy)
                .build();

//        String payUrl = service.payOrder(httpclient, defaultRequestConfig, "", orderinfo, new HashMap());


//        String back ="{\"success\":true,\"data\":{\"offers\":[{\"type\":\"Flight\",\"detail\":{\"orderId\":\"82569088\",\"orderNo\":\"20191104232738348659\",\"status\":\"WP\",\"orderLimit\":1572883058000,\"createTime\":1572881258000,\"couponAmount\":\"0\",\"paidAmount\":\"0\",\"noPaidAmount\":\"1180.0\",\"totalAmount\":\"1180.0\",\"ticketValidDate\":\"2020-11-05 23:27:38\",\"source\":null,\"customer\":{\"firstName\":null,\"lastName\":null,\"fullName\":\"??????\",\"email\":null,\"areaCode\":\"86\",\"mobile\":\"15897736493\"},\"payments\":[{\"id\":84315965,\"paymentNo\":\"20191104232823192732\",\"payType\":\"YEEPAY\",\"payStatus\":\"PENDING\",\"payAmount\":\"1180\",\"tradeNo\":null,\"currency\":\"RMB\",\"payTime\":1572881303000,\"isMisPay\":null,\"payStatusCn\":\"?????????\",\"payTypeCn\":\"????????????\"}],\"refunds\":[],\"travelers\":[{\"id\":121514504,\"firstName\":null,\"lastName\":null,\"fullName\":\"?????????\",\"type\":\"ADULT\",\"mobile\":\"15897736493\",\"birthDay\":\"1984-07-19\",\"certificateType\":\"NI\",\"certificateNo\":\"411425198407198112\",\"specialCertificateTypeNo\":null,\"infantTravelers\":null,\"flightSegments\":[{\"id\":\"125507176\",\"status\":\"HK\",\"flightNo\":\"8L9979\",\"airlineCode\":\"8L\",\"airlineName\":\"????????????\",\"stopCityCode\":null,\"stopCityName\":null,\"departureCode\":\"KMG\",\"departureName\":\"??????\",\"departureDate\":\"2019-12-01\",\"departureTime\":\"2019-12-01 17:25:00\",\"departureTerminal\":null,\"departureAirPortName\":\"????????????????????????\",\"arrivalCode\":\"SZX\",\"arrivalName\":\"??????\",\"arrivalAirPortName\":\"????????????????????????\",\"arrivalTerminal\":null,\"arrivalDate\":\"2019-12-01\",\"arrivalTime\":\"2019-12-01 19:35:00\",\"aircraftCode\":null,\"cabinCode\":\"U\",\"cabinRebate\":null,\"issCode\":null,\"ticketNo\":null,\"ticketStatus\":null,\"totalAmount\":\"590.0\",\"netFare\":\"540.0\",\"couponAmount\":\"0\",\"airportTax\":\"50.0\",\"fuelTax\":\"0.0\",\"discount\":\"0.29\",\"pnrNo\":\"NCK8DK\",\"productCode\":\"WEB\",\"ancillaries\":[],\"rcabinCode\":null,\"statusCn\":\"?????????\"}],\"typeCn\":\"??????\",\"certificateTypeCn\":\"?????????\"},{\"id\":121514505,\"firstName\":null,\"lastName\":null,\"fullName\":\"??????\",\"type\":\"ADULT\",\"mobile\":\"15897736493\",\"birthDay\":\"1983-11-30\",\"certificateType\":\"NI\",\"certificateNo\":\"350802198311306035\",\"specialCertificateTypeNo\":null,\"infantTravelers\":null,\"flightSegments\":[{\"id\":\"125507177\",\"status\":\"HK\",\"flightNo\":\"8L9979\",\"airlineCode\":\"8L\",\"airlineName\":\"????????????\",\"stopCityCode\":null,\"stopCityName\":null,\"departureCode\":\"KMG\",\"departureName\":\"??????\",\"departureDate\":\"2019-12-01\",\"departureTime\":\"2019-12-01 17:25:00\",\"departureTerminal\":null,\"departureAirPortName\":\"????????????????????????\",\"arrivalCode\":\"SZX\",\"arrivalName\":\"??????\",\"arrivalAirPortName\":\"????????????????????????\",\"arrivalTerminal\":null,\"arrivalDate\":\"2019-12-01\",\"arrivalTime\":\"2019-12-01 19:35:00\",\"aircraftCode\":null,\"cabinCode\":\"U\",\"cabinRebate\":null,\"issCode\":null,\"ticketNo\":null,\"ticketStatus\":null,\"totalAmount\":\"590.0\",\"netFare\":\"540.0\",\"couponAmount\":\"0\",\"airportTax\":\"50.0\",\"fuelTax\":\"0.0\",\"discount\":\"0.29\",\"pnrNo\":\"NCK8DK\",\"productCode\":\"WEB\",\"ancillaries\":[],\"rcabinCode\":null,\"statusCn\":\"?????????\"}],\"typeCn\":\"??????\",\"certificateTypeCn\":\"?????????\"}],\"changes\":[],\"itinerary\":null,\"pnrImport\":false,\"statusCn\":\"????????????\"}}],\"isCharterFlight\":false}}";
//
////        service.parseOrderDetial(back,orderinfo);


        for (int i = 0; i < 1; i++) {
            Long startTime = System.currentTimeMillis();
            service.flightSearch(httpclient, defaultRequestConfig, null, "", orderinfo, "", 0);
            long queryTmie = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("?????????????????????" + queryTmie + "s");
        }


    }


    /**
     * ???????????????????????????
     *
     * @param soap
     * @param rgex ???????????????
     * @return
     */
    private String getSubUtil(String text, String rgex) {
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            if (StringUtils.isNotEmpty(m.group(1))) {
                return m.group(1);
            }
        }
        return "";
    }
}