package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.Orderinfo;
import com.feeye.bean.Passergeninfo;
import com.feeye.util.FingerPrintUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
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
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
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
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xs
 * @description
 * @date 2019/7/3
 */
public class GSCreateOrderService {
    private static final Logger logger = LoggerFactory.getLogger(GSCreateOrderService.class);
    private static final int timeout = 15000;
    //    String dailiyunAccount = "feeyeapp:feeye789";
    String dailiyunAccount = "feeyedaili:feeye123";
    //??????????????????
    String csrfToken = "";

    private String StartCreateOrder(String orderJson, int retryCount, int requestType) {
        long nowDateTime = new Date().getTime(); //?????????????????????????????????????????????????????????????????????
        String createOrderBack = "";
        String cookie = "";
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
        BasicScheme proxyAuth = new BasicScheme();
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            logger.error("error", e1);
        }
        //?????????ip
        String ip_port = DailiyunService.getRandomDailiIp(50);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(dailiyunProxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        String[] dailiyunAccounts = dailiyunAccount.split(":");
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(dailiyunAccounts[0], dailiyunAccounts[1]));

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
                .setDefaultRequestConfig(defaultRequestConfig)
                .setSSLSocketFactory(sslsf)
                .setDefaultCredentialsProvider(credsProvider)
                .setDefaultCookieStore(cookieStore)
                .build();

        CloseableHttpResponse response = null;

        try {
            //json??????
            JSONObject jsonObject = JSON.parseObject(orderJson);
            Orderinfo orderJsonBean = jsonObject.toJavaObject(Orderinfo.class);

            String order_id = orderJsonBean.getId();
            String childrenUser = orderJsonBean.getUsername();
            String billNo = "";
            String newOrderNo = "";
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

            //????????????
//            sendOrderStatus(childrenUser, order_id, "GS????????????");
            String queryFlightsBack = "";
            long start = System.currentTimeMillis();
            for (int i = 0; i < 3; i++) {
                queryFlightsBack = queryFlightsNew(httpclient, defaultRequestConfig, "", orderJsonBean, "", 0, FingerPrintUtil.getDesc(), 0);
                if (queryFlightsBack.contains("retry") && retryCount < 2) {
                    retryCount++;
                    System.out.println("????????????IP,???IP??????");
                    logger.info(order_id + " ????????????IP,???IP??????");
                    return StartCreateOrder(orderJson, retryCount, 0);
                }
                if (queryFlightsBack.contains("ERROR")) {
//                    sendOrderStatus(childrenUser, order_id, SearchFlightBack);
                    sendCreateOrderInfo("error", "???????????????" + queryFlightsBack, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return queryFlightsBack;
                }
                break;
            }
            System.out.println("??????????????????:" + (System.currentTimeMillis() - start) / 1000);

            //????????????
            String airItineraryPriceId = parseFlights(queryFlightsBack, orderJsonBean);
            if (StringUtils.isEmpty(airItineraryPriceId)) {
                return "ERROR:??????????????????";
            }

            //??????
            for (int i = 0; i < 3; i++) {
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

/*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "???????????????");
                sendCreateOrderInfo("error", "???????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "???????????????");
                return "????????????";
            }*/


            //????????????
            String shoppingCartId = airTaxFeeCalculate(httpclient, defaultRequestConfig, cookie, airItineraryPriceId);

            //???????????????
            shoppingCartId = getShoppingCart(httpclient, defaultRequestConfig, cookie, shoppingCartId);

            //??????????????????????????????
            shoppingCartId = createTravelers(httpclient, defaultRequestConfig, cookie, shoppingCartId, orderJsonBean);

            //??????
            createOrderBack = createReservation(httpclient, defaultRequestConfig, cookie, orderJsonBean, shoppingCartId);
            JSONObject createOrderObj = JSON.parseObject(createOrderBack);
            String orderNo = createOrderObj.getJSONObject("data").getJSONObject("reservation").getString("code"); //???????????????
            String totalFare = createOrderObj.getJSONObject("data").getJSONObject("reservation").getString("totalFare"); //???????????????
            logger.info(order_id + "??????????????????:" + orderNo + "???????????????:" + totalFare);

            //????????????
            if (StringUtils.isNotEmpty(orderNo) && StringUtils.isNotEmpty(totalFare)) {
                checkOut(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderNo);
                String payUrl = payOrder(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderNo, totalFare);
                logger.info(order_id + "??????????????????:" + payUrl);
                //TODO????????????????????????

                //????????????????????????????????????
                reservationRetrieve(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderNo);

            } else {
                sendOrderStatus(childrenUser, order_id, "????????????");
                sendCreateOrderInfo("error", "????????????", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return "ERROR:????????????";
            }

            try {
                newOrderNo = orderJsonBean.getNewOrderNo();
            } catch (Exception e) {
                logger.error("????????????newOrderNo");
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
    private String login(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore, Orderinfo orderJson) {
        CloseableHttpResponse response = null;
        InputStream is = null;
        String cookie = "";
        String order_id = orderJson.getId();
        String childrenUser = orderJson.getUsername();
        String username = orderJson.getOtheraccount().split("_")[0];
        String password = orderJson.getOtheraccount().split("_")[1];

//      sendOrderStatus(childrenUser, order_id, "??????GS??????");
        try {
            HttpHost host = new HttpHost("www.tianjin-air.com", 80, "http");
            HttpGet get = new HttpGet("http://www.tianjin-air.com");
            get.setConfig(config);
            get.setHeader("Host", "www.tianjin-air.com");
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
            logger.info("?????????????????????cookie" + cookie);
            response.close();

//          sendOrderStatus(childrenUser, order_id, "??????GS???????????????");
            Long time = System.currentTimeMillis();
            get = new HttpGet("/api/validate/captcha");
            get.setConfig(config);
            get.setHeader("Host", "www.tianjin-air.com");
            get.setHeader("Cookie", cookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            response = httpclient.execute(host, get);

            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
                    if (headers[i].getValue().contains("JSESSIONID")) {
                        String jsessionid = headers[i].getValue().split(";")[0].split("=")[1];
                        sb.append(headers[i].getValue().split(";")[0] + ";");
                    }
                }
            }
            sb.delete(sb.length() - 1, sb.length());
//            System.out.println("???????????????????????????cookie:" + sb);
            cookie = sb.toString();

            String session = UUID.randomUUID().toString().replaceAll("-", "");
            String fileUri = "C://testImg//" + session + ".jpg";
            is = response.getEntity().getContent();
            OutputStream os = new FileOutputStream(fileUri);
            IOUtils.copy(is, os);
            is.close();
            os.close();

            //TODO ???????????????
            System.out.print("???????????????:");
            Scanner scan = new Scanner(System.in);
            String result = scan.nextLine();
            response.close();

/*          //???????????????
            HttpPost post = new HttpPost("http://www.tianjin-air.com/api/validate/captchaCheck");
            post.setConfig(config);
            List <NameValuePair> nameValues = new ArrayList <NameValuePair>();
            JSONObject paramJson = new JSONObject();
            paramJson.put("captcha",result);
            paramJson.put("_csrf",csrfToken);
            StringEntity entity = new StringEntity(paramJson.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Referer", "http://www.tianjin-air.com/member/login.html");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Cookie", cookie);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            response = httpclient.execute(post);
            String  back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(back);
            JSONObject jsonObject = JSON.parseObject(back);
            response.close();*/

//          sendOrderStatus(childrenUser, order_id, "GS????????????");
            String url = URLEncoder.encode("http://www.tianjin-air.com", "UTF-8");
            url = URLEncoder.encode(url, "UTF-8");
            HttpPost post = new HttpPost("/member/login.html?done=" + url);
            post.setConfig(config);
            String[] userAccount = orderJson.getAccount().split("_");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("username", userAccount[0]));
            nameValue.add(new BasicNameValuePair("password", userAccount[1]));
            nameValue.add(new BasicNameValuePair("captcha", result));
            nameValue.add(new BasicNameValuePair("_csrf", csrfToken));
            nameValue.add(new BasicNameValuePair("loginType", "nlogin"));
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
            System.out.println("????????????:" + back);
            logger.info(order_id + "????????????:" + back);
            String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            if ("302".equals(statusCode) && back.contains("Redirecting to")) {
                logger.info(order_id + "????????????");
                return cookie;
            }
            if (StringUtils.isNotEmpty(back) && back.contains("LOGIN_ERRORS")) {
                //????????????
                Document parse = Jsoup.parse(back);
                String loginErrorBack = parse.getElementsByTag("script").get(4).childNodes().get(0).outerHtml();
                String loginErrorMsg = loginErrorBack.substring(loginErrorBack.indexOf("[") + 1, loginErrorBack.indexOf("]"));
                JSONObject json = JSON.parseObject(loginErrorMsg);
                String message = json.getString("message");
                System.out.println(message);
                logger.info(order_id + message);
                return "ERROR:" + message;
            } else {
                return "ERROR: ????????????";
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


    //??????????????????cookie?????????
    private String queryFlightsNew(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                   Orderinfo orderJson, String verifyCode, int retry, String desc, int type) throws Exception {
        CloseableHttpResponse response = null;
        String order_id = orderJson.getId();
        String back = "";
        //???cookie???map
        Map<String, String> map = new HashMap<>();
        HttpHost host = new HttpHost("www.tianjin-air.com", 80, "http");
        try {
            String origin = orderJson.getFlights().get(0).getDeparture();
            String destination = orderJson.getFlights().get(0).getArrival();
            String departureDate = orderJson.getFlights().get(0).getDepartureDate();
            Header[] headers = null;

            // type==0 ???????????????cookie
            if (type == 0) {
                HttpGet get = new HttpGet("http://www.tianjin-air.com");
                get.setConfig(config);
                get.setHeader("Host", "www.tianjin-air.com");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
                get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
                get.setHeader("Proxy-Connection", "keep-alive");
                response = httpclient.execute(get);
                headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
                        if (headers[i].getValue().contains("csrfToken")) {
                            csrfToken = headers[i].getValue().split(";")[0].split("=")[1];
                        }
                        String[] split = headers[i].getValue().split(";")[0].split("=");
                        map.put(split[0], split[1]);
                    }
                }
                //?????????cookie,???????????????
                map.put("_ga", "GA1.2.1714993364.1587521766");
                map.put("_gid", "GA1.2.35487047.1587521766");
                map.put("_gat_TrueMetrics", "1");
                if (!map.containsKey("csrfToken")) {
                    map.put("csrfToken", csrfToken);
                }
                cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
                logger.info(order_id + " ??????cookie:" + cookie);
                response.close();

                for (int i = 0; i < 3; i++) {
                    get = new HttpGet("/api/information/getCitys");
//                    get.setConfig(config);
                    get.setHeader("Host", "www.tianjin-air.com");
                    get.setHeader("Referer", "http://www.tianjin-air.com");
                    get.setHeader("Accept", "application/json, text/plain, */*");
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                    get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
                    get.setHeader("Proxy-Connection", "keep-alive");
                    get.setHeader("Cookie", cookie);
                    response = httpclient.execute(get);
                    back = EntityUtils.toString(response.getEntity(), "utf-8");
                    if (StringUtils.isNotEmpty(back)) {
                        break;
                    }
                }

                headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if ("Set-Cookie".equals(headers[i].getName())) {
                        if (headers[i].getValue().contains("N_V_IN")) {
                            String nvinCookie = headers[i].getValue().split(";")[0] + ";";
                            String regEx = "N_V_IN=(.*?);";
                            map.put("N_V_IN", getSubUtil(nvinCookie, regEx));
                        }
                    }
                }
                cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
                response.close();

                get = new HttpGet("/api/validate/captcha");
                get.setConfig(config);
                get.setHeader("Host", "www.tianjin-air.com");
                get.setHeader("Referer", "http://www.tianjin-air.com");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                get.setHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
                get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
                get.setHeader("Proxy-Connection", "keep-alive");
                get.setHeader("Cookie", cookie);
                response = httpclient.execute(host, get);
                headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if ("Set-Cookie".equals(headers[i].getName())) {
                        if (headers[i].getValue().contains("JSESSIONID")) {
                            String jsessionid = headers[i].getValue().split(";")[0].split("=")[1];
                            map.put("JSESSIONID", jsessionid);
                        }
                    }
                }
                cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
                response.close();

                HttpPost post = new HttpPost("/api/recommendLine/recomendLindSearch");
                post.setConfig(config);
                post.setHeader("Host", "www.tianjin-air.com");
                post.setHeader("Referer", "http://www.tianjin-air.com");
                post.setHeader("Content-Type", "application/json;charset=UTF-8");
                post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
                post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
                post.setHeader("Proxy-Connection", "keep-alive");
                post.setHeader("Cookie", cookie);
                String param = "{\"regions\":[\"SOUTH_CHINA\"],\"size\":10,\"_csrf\":\"" + csrfToken + "\"}";
                StringEntity ent = new StringEntity(param, Charset.forName("UTF-8"));
                post.setEntity(ent);
                response = httpclient.execute(host, post);
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if ("Set-Cookie".equals(headers[i].getName())) {
                        if (headers[i].getValue().contains("N_V_IN")) {
                            String nvinCookie = headers[i].getValue().split(";")[0] + ";";
                            String regEx = "N_V_IN=(.*?);";
                            map.put("N_V_IN", getSubUtil(nvinCookie, regEx));
                        }
                    }
                }
                cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
                response.close();

                get = new HttpGet("http://www.tianjin-air.com/favicon.ico");
                get.setConfig(config);
                get.setHeader("Host", "www.tianjin-air.com");
                get.setHeader("Referer", "http://www.tianjin-air.com");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
                get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
                get.setHeader("Proxy-Connection", "keep-alive");
                get.setHeader("Cookie", cookie);
                response = httpclient.execute(host, get);
                headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if ("Set-Cookie".equals(headers[i].getName())) {
                        if (headers[i].getValue().contains("N_V_IN")) {
                            String nvinCookie = headers[i].getValue().split(";")[0] + ";";
                            String regEx = "N_V_IN=(.*?);";
                            map.put("N_V_IN", getSubUtil(nvinCookie, regEx));
                        }
                    }
                }
                cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
                response.close();

                String url = "/api/fareTrend/airFareTrends?org=" + origin + "&dst=" + destination + "&startDate=" + departureDate + "&endDate=" + departureDate + "&airline=GS";
                get = new HttpGet(url);
                get.setConfig(config);
                get.setHeader("Host", "www.tianjin-air.com");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
                get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
                get.setHeader("Proxy-Connection", "keep-alive");
                get.setHeader("Cookie", cookie);
                response = httpclient.execute(host, get);
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if ("Set-Cookie".equals(headers[i].getName())) {
                        if (headers[i].getValue().contains("N_V_IN")) {
                            String nvinCookie = headers[i].getValue().split(";")[0] + ";";
                            String regEx = "N_V_IN=(.*?);";
                            map.put("N_V_IN", getSubUtil(nvinCookie, regEx));
                        }
                    }
                }
                cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
                response.close();

                post = new HttpPost("/flight/select.html");
                post.setConfig(config);
                List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
                nameValue.add(new BasicNameValuePair("type", "0"));
                nameValue.add(new BasicNameValuePair("origin", origin));
                nameValue.add(new BasicNameValuePair("destination", destination));
                nameValue.add(new BasicNameValuePair("departureDate", departureDate));
                nameValue.add(new BasicNameValuePair("arrivalDate", ""));
                nameValue.add(new BasicNameValuePair("ADT", "1"));
                nameValue.add(new BasicNameValuePair("CNN", "0"));
                nameValue.add(new BasicNameValuePair("INF", "0"));
                nameValue.add(new BasicNameValuePair("PWD", "0"));
                nameValue.add(new BasicNameValuePair("MWD", "0"));
                nameValue.add(new BasicNameValuePair("cabin", "Economy"));
                nameValue.add(new BasicNameValuePair("_csrf", csrfToken));

                post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
                post.setHeader("Host", "www.tianjin-air.com");
                post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                post.setHeader("Origin", "http://www.tianjin-air.com");
                post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                post.setHeader("Content-Type", "application/x-www-form-urlencoded");
                post.setHeader("Referer", "http://www.tianjin-air.com");
                post.setHeader("Accept-Encoding", "gzip, deflate");
                post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
                post.setHeader("Cookie", cookie);
                post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
                post.setHeader("Proxy-Connection", "keep-alive");
                response = httpclient.execute(host, post);
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if ("Set-Cookie".equals(headers[i].getName())) {
                        if (headers[i].getValue().contains("N_V_IN")) {
                            String nvinCookie = headers[i].getValue().split(";")[0] + ";";
                            String regEx = "N_V_IN=(.*?);";
                            map.put("N_V_IN", getSubUtil(nvinCookie, regEx));
                        }
                    }
                }
                cookie = map.toString().replace("{", "").replace("}", "").replaceAll(",", ";");
                response.close();
            }

//            Thread.sleep(2*1000);
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
            originDestinationsObj.put("origin", origin);
            originDestinationsObj.put("destination", destination);
            originDestinationsObj.put("departureDate", departureDate);
            originDestinations.add(0, originDestinationsObj);

            paramObj.put("passenger", passenger);
            paramObj.put("originDestinations", originDestinations);
            paramObj.put("cabin", "Economy");
            paramObj.put("offset", 3);
            paramObj.put("_csrf", csrfToken);
            if (StringUtils.isNotEmpty(verifyCode)) {
                paramObj.put("vc", verifyCode);
            }
            paramObj.put("desc", desc);
//            paramObj.put("desc", "coBPtm4BZy5Ly7E1arnlj8GsThjMa5jcRUEfsoW/0G5o2tz7GPiCNecjXPa7Jydt");
            HttpPost post = new HttpPost("/api/airLowFareSearch/search");
            post.setConfig(config);
            StringEntity entity = new StringEntity(paramObj.toString());
            System.out.println("??????????????????:" + paramObj);
            logger.info(order_id + " ??????????????????: " + paramObj.toString());
            post.setEntity(entity);
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.tianjin-air.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.tianjin-air.com/flight/select.html");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Cookie", cookie);
            System.out.println("????????????cookie: " + cookie);
            logger.info(order_id + " ????????????cookie: " + cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("??????????????????:" + back);
            logger.info("??????????????????:" + back);
            headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                if ("Set-Cookie".equals(headers[i].getName())) {
                    if (headers[i].getValue().contains("pa")) {
                        String paCookie = headers[i].getValue().split(";")[0];
                        cookie = cookie + "; " + paCookie;
                    }
                }
            }
            if (back.contains("10001")) {
                if (retry < 2) {
                    retry++;
                    System.out.println("??????????????????");
                    return queryFlightsNew(httpclient, config, cookie, orderJson, "", retry, desc, 1);
                } else {
                    return "retry";
                }
            }
            if (back.contains("10000")) {
                //???????????????????????????
                HttpGet get = new HttpGet("/hnatravel/imagecodeajax?v=2");
                get.setConfig(config);
                get.setHeader("Host", "www.tianjin-air.com");
                get.setHeader("Cookie", cookie);
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
                response = httpclient.execute(host, get);
                headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if ("Set-Cookie".equals(headers[i].getName())) {
                        if (headers[i].getValue().contains("ci")) {
                            String ciCookie = headers[i].getValue().split(";")[0];
                            cookie = cookie + "; " + ciCookie;
                        }
                    }
                }
                String session = UUID.randomUUID().toString().replaceAll("-", "");
                String fileUri = "C://testImg//" + session + ".jpg";
                InputStream is = response.getEntity().getContent();
                OutputStream os = new FileOutputStream(fileUri);
                IOUtils.copy(is, os);
                is.close();
                os.close();

//                File imageFile = new File(fileUri);
//                FileEntity fileEntity = new FileEntity(imageFile);
//                String codeUrl = "http://123.58.43.45:1111";
////                String codeUrl = "http://127.0.0.1:1111";
//                HttpPost codePost = new HttpPost(codeUrl);
//                codePost.setEntity(fileEntity);
//                response = httpclient.execute(codePost);
//                verifyCode = EntityUtils.toString(response.getEntity());
//                System.out.println("?????????????????????:" + verifyCode);
//                logger.info(order_id + "?????????????????????:" + verifyCode);
//                if (StringUtils.isNotEmpty(verifyCode) && verifyCode.contains("URL Fields")) {
//                    Thread.sleep(3 * 1000);
//                    response = httpclient.execute(codePost);
//                    verifyCode = EntityUtils.toString(response.getEntity());
//                }
//                response.close();

                //TODO ???????????????
                System.out.print("???????????????:");
                Scanner scan = new Scanner(System.in);
                verifyCode = scan.nextLine();
                response.close();

                return queryFlightsNew(httpclient, config, cookie, orderJson, verifyCode, retry, desc, 1);
            }
            if (back.contains("10002")) {
                return queryFlightsNew(httpclient, config, cookie, orderJson, "", retry, desc, 1);
            }
            JSONObject jsonObject = JSON.parseObject(back);
            JSONArray airItinerariesArr = jsonObject.getJSONObject("data").getJSONArray("originDestinations").getJSONObject(0).getJSONArray("airItineraries");
            if (airItinerariesArr.isEmpty()) {
                return "ERROR:SORRY!?????????????????????????????????????????????????????????????????????";
            }
        } catch (NoHttpResponseException | SocketTimeoutException | ConnectTimeoutException e) {
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


    /**
     * ????????????,??????????????????id
     *
     * @param back
     * @param orderJson
     * @return
     */
    private static String parseFlights(String back, Orderinfo orderJson) {
        String id = "";
        float price = 0f;
        String cabin = "";
        if (StringUtils.isNotEmpty(back)) {
            String flightNo = orderJson.getFlights().get(0).getFlightNo().substring(2);
            String departure = orderJson.getFlights().get(0).getDeparture();
            String arrival = orderJson.getFlights().get(0).getArrival();
            String departureDate = orderJson.getFlights().get(0).getDepartureDate();
            price = Float.valueOf(orderJson.getFlights().get(0).getPrice());
            cabin = orderJson.getFlights().get(0).getCabin();

            JSONObject jsonObject = JSON.parseObject(back);
            JSONObject originDestinationsObj = (JSONObject) jsonObject.getJSONObject("data").getJSONArray("originDestinations").get(0);
            JSONArray airItinerariesArr = originDestinationsObj.getJSONArray("airItineraries");

            for (int i = 0; i < airItinerariesArr.size(); i++) {
                JSONObject flightSegments = airItinerariesArr.getJSONObject(i).getJSONArray("flightSegments").getJSONObject(0);
                String flightNumber = flightSegments.getString("flightNumber");
                String departureAirportCode = flightSegments.getString("departureAirportCode");
                String arrivalAirportCode = flightSegments.getString("arrivalAirportCode");
                String depDate = flightSegments.getString("departureDate");
                if (!flightNumber.equals(flightNo)) {
                    continue;
                }
                if (!departure.equals(departureAirportCode) || !arrival.equals(arrivalAirportCode)) {
                    continue;
                }
                if (!departureDate.equals(depDate)) {
                    continue;
                }

                JSONObject airItineraryPricesObj = airItinerariesArr.getJSONObject(i).getJSONObject("airItineraryPrices");
                for (int j = 0; j < airItineraryPricesObj.size(); j++) {
                    JSONArray business = airItineraryPricesObj.getJSONArray("business");
                    if (!business.isEmpty()) {
                        for (int k = 0; k < business.size(); k++) {
                            JSONObject farePrices = (JSONObject) business.getJSONObject(i).getJSONArray("travelerPrices").getJSONObject(0).getJSONArray("farePrices").getJSONObject(0);
                            Float baseFare = Float.valueOf(farePrices.getString("baseFare"));
                            String bookingClass = farePrices.getString("bookingClass"); //??????
                            if (bookingClass.equals(cabin) && baseFare == price) {
                                id = business.getJSONObject(i).getString("id");
                                return id;
                            }
                        }
                    }
                    JSONArray superEconomy = airItineraryPricesObj.getJSONArray("superEconomy");
                    if (!superEconomy.isEmpty()) {
                        for (int k = 0; k < superEconomy.size(); k++) {
                            JSONObject farePrices = (JSONObject) superEconomy.getJSONObject(k).getJSONArray("travelerPrices").getJSONObject(0).getJSONArray("farePrices").getJSONObject(0);
                            Float baseFare = Float.valueOf(farePrices.getString("baseFare"));
                            String bookingClass = farePrices.getString("bookingClass"); //??????
                            if (baseFare == price) {
                                id = superEconomy.getJSONObject(i).getString("id");
                                return id;
                            }
                        }
                    }
                    JSONArray economy = airItineraryPricesObj.getJSONArray("economy");
                    if (!economy.isEmpty()) {
                        for (int k = 0; k < economy.size(); k++) {
                            JSONObject farePrices = (JSONObject) economy.getJSONObject(k).getJSONArray("travelerPrices").getJSONObject(0).getJSONArray("farePrices").getJSONObject(0);
                            Float baseFare = Float.valueOf(farePrices.getString("baseFare"));
                            String bookingClass = farePrices.getString("bookingClass"); //??????
                            if (baseFare == price) {
                                id = economy.getJSONObject(i).getString("id");
                                return id;
                            }
                        }
                    }
                }
            }
        } else {
            return "ERROR:???????????????????????????";
        }
        if (StringUtils.isEmpty(id)) {
            return "ERROR:????????????????????????????????????";
        }
        return null;
    }


    /**
     * ??????????????????
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param airItineraryPriceId
     * @return
     */
    private String airTaxFeeCalculate(CloseableHttpClient httpclient, RequestConfig config, String cookie, String airItineraryPriceId) {
        CloseableHttpResponse response = null;
        try {
            String param = "{\"airItineraryPriceId\":[\"" + airItineraryPriceId + "\"],\"_csrf\":\"" + csrfToken + "\"}";
            HttpHost host = new HttpHost("www.tianjin-air.com", 80, "http");
            HttpPost post = new HttpPost("/api/airTaxFeeCalculate/airTaxFeeCalculate");
            post.setConfig(config);
            StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.tianjin-air.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.tianjin-air.com/flight/select.html");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("GS??????????????????:" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success"))) {
                back = jsonObject.getJSONObject("data").getJSONObject("shoppingCart").getString("id");
            }
            return back;
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
     * ???????????????
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param id
     * @return
     */
    private String getShoppingCart(CloseableHttpClient httpclient, RequestConfig config, String cookie, String id) {
        CloseableHttpResponse response = null;
        try {
            String param = "{\"id\":\"" + id + "\",\"_csrf\":\"" + csrfToken + "\"}";
            HttpHost host = new HttpHost("www.tianjin-air.com", 80, "http");
            HttpPost post = new HttpPost("/api/shoppingCart/getShoppingCart");
            post.setConfig(config);
            StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.tianjin-air.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.tianjin-air.com/flight/passenger.html");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("?????????????????????:" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success"))) {
                back = jsonObject.getJSONObject("data").getJSONObject("shoppingCart").getString("id");
            }
            return back;
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
     * ??????????????????????????????
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param shoppingCartId ?????????id
     * @return
     */
    private String createTravelers(CloseableHttpClient httpclient, RequestConfig config, String cookie, String shoppingCartId, Orderinfo orderJson) {
        CloseableHttpResponse response = null;
        try {
            String departureDate = orderJson.getFlights().get(0).getDepartureDate();
            String linkMan = orderJson.getLinkMan(); //?????????
            String mobile = orderJson.getMobile(); //???????????????

            List<Passergeninfo> passengers = orderJson.getPassengers();
            JSONObject paramObj = new JSONObject();
            JSONArray passengersArr = new JSONArray();
            JSONObject customerObj = new JSONObject();
            customerObj.put("firstName", linkMan.substring(1, linkMan.length()));
            customerObj.put("lastName", linkMan.substring(0, 1));
            customerObj.put("mobile", mobile);
            //TODO ?????????????????????????????????????????????
            customerObj.put("email", "");
            int i = 0;
            for (Passergeninfo pb : orderJson.getPassengers()) {
                JSONObject passengersObj = new JSONObject();
                i++;
                if ("??????".equals(pb.getPassengerType())) {
                    passengersObj.put("type", "ADT");
                } else if ("??????".equals(pb.getPassengerType())) {
                    passengersObj.put("type", "CNN");
                } else if ("??????".equals(pb.getPassengerType())) {
                    passengersObj.put("type", "INF");
                }
                passengersObj.put("addToCommon", false);
                String passengerName = pb.getPassengerName();
                if (passengerName.contains("/")) { //??????
                    String[] split = passengerName.split("/");
                    passengersObj.put("firstName", split[0]);
                    passengersObj.put("lastName", split[1]);
                } else {
                    passengersObj.put("lastName", pb.getPassengerName().substring(0, 1));
                    passengersObj.put("firstName", pb.getPassengerName().substring(1, pb.getPassengerName().length()));
                }
                if ("?????????".equals(pb.getPassengercardType())) {
                    passengersObj.put("idType", "ID_CARD");
                } else if ("??????".equals(pb.getPassengercardType())) {
                    passengersObj.put("idType", "2.DOC");
                } else if ("??????".equals(pb.getPassengercardType())) {
                    passengersObj.put("idType", "OTHER_ID");
                }
                passengersObj.put("idNo", pb.getIdcard());
                //?????????????????????????????????????????????
                passengersObj.put("mobile", orderJson.getMobile());
                passengersObj.put("birthday", pb.getBirthday().substring(0, 10));
                passengersObj.put("id", i);
                passengersArr.add(passengersObj);
            }
            paramObj.put("shoppingCartId", shoppingCartId);
            paramObj.put("countryType", 0);
            paramObj.put("departureDate", departureDate);
            paramObj.put("_csrf", csrfToken);
            paramObj.put("customer", customerObj);
            paramObj.put("passengers", passengersArr);

            HttpHost host = new HttpHost("www.tianjin-air.com", 80, "http");
            HttpPost post = new HttpPost("/api/traveler/createTravelers");
            post.setConfig(config);
            StringEntity entity = new StringEntity(paramObj.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.tianjin-air.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.tianjin-air.com/flight/passenger.html");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("???????????????????????????:" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success"))) {
                back = jsonObject.getJSONObject("data").getJSONObject("shoppingCart").getString("id");
            } else if ("false".equals(jsonObject.getString("success"))) {
                String message = jsonObject.getString("message");
                logger.info("???????????????????????????:" + message);
                return "ERROR:" + message;
            }
            return back;
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
     * @param shoppingCartId ?????????id
     * @param orderJson
     * @return
     */
    private String createReservation(CloseableHttpClient httpclient, RequestConfig config, String cookie, Orderinfo orderjson, String shoppingCartId) {
        CloseableHttpResponse response = null;
        String order_id = orderjson.getId();
        try {
            String desc = FingerPrintUtil.getDesc();
            String param = "{\"shoppingCartId\":\"" + shoppingCartId + "\",\"_csrf\":\"" + csrfToken + "\",\"desc\":\"" + desc + "\"}";
            HttpHost host = new HttpHost("www.tianjin-air.com", 80, "http");
            HttpPost post = new HttpPost("/api/reservationCreate/createReservation");
            post.setConfig(config);
            StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.tianjin-air.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.tianjin-air.com/flight/product.html");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("GS??????????????????:" + back);
            logger.info(order_id + "GS??????????????????" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("????????????")) {
                logger.info(order_id + "GS??????????????????");
            } else {
                logger.info(order_id + "GS??????????????????:" + back);
                return "ERROR:GS??????????????????";
            }
            return back;
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
     * ?????????????????????
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param reservationCode ?????????
     * @param orderJson
     * @return
     */
    private String checkOut(CloseableHttpClient httpclient, RequestConfig config, String cookie, Orderinfo orderjson, String reservationCode) {
        CloseableHttpResponse response = null;
        String order_id = orderjson.getId();
        try {
            JSONObject paramObj = new JSONObject();
            paramObj.put("reservationCode", reservationCode);
            paramObj.put("_csrf", csrfToken);
            HttpHost host = new HttpHost("www.tianjin-air.com", 80, "http");
            HttpPost post = new HttpPost("/api/pay/checkOut");
            post.setConfig(config);
            StringEntity entity = new StringEntity(paramObj.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.tianjin-air.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.tianjin-air.com/flight/payment.html");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("GS???????????????check??????:" + back);
            logger.info(order_id + "GS???????????????check??????" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("????????????")) {
                logger.info(order_id + "GS???????????????check??????");
            }
            return back;
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
     * ????????????(??????), ??????????????????
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param reservationCode ?????????
     * @param orderJson
     * @return
     */
    private String payOrder(CloseableHttpClient httpclient, RequestConfig config, String cookie, Orderinfo orderjson, String reservationCode, String totalAmount) {
        CloseableHttpResponse response = null;
        String order_id = orderjson.getId();
        String locationValue = "";
        try {
            JSONObject paramObj = new JSONObject();
            paramObj.put("reservationCode", reservationCode);
            paramObj.put("totalAmount", totalAmount);
            paramObj.put("bankId", "5"); //??????
            paramObj.put("payPurpose", "PAY_FOR_ORDER");
            paramObj.put("fareFamilyCode", "GTH");
            paramObj.put("intDom", "DOM");
            paramObj.put("restTime", "1766000");
            paramObj.put("_csrf", csrfToken);

            HttpHost host = new HttpHost("www.tianjin-air.com", 80, "http");
            HttpPost post = new HttpPost("/api/pay/payOrder");
            post.setConfig(config);
            StringEntity entity = new StringEntity(paramObj.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.tianjin-air.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.tianjin-air.com/flight/payment.html");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info(order_id + "GS???????????????check??????" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("????????????") && back.contains("url")) {
                locationValue = jsonObject.getJSONObject("data").getString("url");
                logger.info(order_id + "GS??????????????????:" + locationValue);
                if (StringUtils.isNotEmpty(locationValue)) {
                    RequestConfig rConfig = RequestConfig.custom()
                            .setSocketTimeout(timeout)
                            .setConnectTimeout(timeout)
                            .setConnectionRequestTimeout(timeout)
                            .setRedirectsEnabled(false)
                            .setStaleConnectionCheckEnabled(true) //?????????????????????
                            .build();
                    // ???????????????
                    HttpHost yeepayHost = new HttpHost("www.yeepay.com", 443, "https");
                    HttpGet get = new HttpGet(locationValue);
                    get.setConfig(rConfig);
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
                    HttpHost cashdeskHost = new HttpHost("cashdesk.yeepay.com", 443, "https");
                    get = new HttpGet(locationValue);
                    get.setConfig(rConfig);
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
                    response = httpclient.execute(cashdeskHost, get);
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
                }
            } else {
                logger.info(order_id + "????????????????????????:" + back);
                return "ERROR:????????????????????????";
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
        return locationValue;
    }


    /**
     * ??????????????????
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param reservationCode ?????????
     * @param orderJson
     * @return
     */
    private String reservationRetrieve(CloseableHttpClient httpclient, RequestConfig config, String cookie, Orderinfo orderjson, String reservationCode) {
        CloseableHttpResponse response = null;
        String order_id = orderjson.getId();
        try {
            String param = "{\"reservationCode\":\"" + reservationCode + "\",\"_csrf\":\"" + csrfToken + "\"}";
            HttpHost host = new HttpHost("www.tianjin-air.com", 80, "http");
            HttpPost post = new HttpPost("/api/order/reservationRetrieve");
            post.setConfig(config);
            StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Host", "www.tianjin-air.com");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.tianjin-air.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.tianjin-air.com/order/orderDetail.html?reservationCode=" + reservationCode);
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info(order_id + "GS????????????????????????:" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("????????????")) {
                JSONArray travelersArr = jsonObject.getJSONObject("data").getJSONObject("reservation").getJSONArray("travelers");
                // TODO ??????????????????
                for (int i = 0; i < travelersArr.size(); i++) {
                    String passengerName = travelersArr.getJSONObject(i).getString("lastName") + travelersArr.getJSONObject(i).getString("firstName");
                    String idNo = travelersArr.getJSONObject(i).getString("idNo"); //?????????
                    //??????????????????????????????????????????,?????????????????????????????????????????????
                    List<Passergeninfo> passengers = orderjson.getPassengers();
                    for (int j = 0; j < passengers.size(); j++) {
                        if (passengers.get(j).getPassengerName().equals(passengerName)) {
                            idNo = passengers.get(j).getIdcard();
                        }
                    }
                    String ticketNumber = travelersArr.getJSONObject(i).getJSONArray("flightSegments").getJSONObject(0).getString("ticketNumber"); //??????
                }
                return back;
            } else {
                logger.info(order_id + "GS????????????????????????:" + back);
                return "ERROR:????????????????????????";
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
//            buffer.append("<official>" + Constant.GS.toString() + "</official> ");
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
            buffer.append("<official>" + "GS" + "</official> ");
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


    public static void main(String[] args) throws Exception {
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
                "            \"cabin\": \"A\",\n" +
                "            \"departure\": \"TSN\",\n" +
                "            \"departureDate\": \"2020-05-31\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"GS7895\",\n" +
                "            \"price\": \"694.0\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"32550175\",\n" +
                "    \"idCardNo\": \"410781197310154713\",\n" +
                "    \"idCardType\": \"IDCARD\",\n" +
                "    \"ifUsedCoupon\": false,\n" +
                "    \"isOutticket\": \"false\",\n" +
                "    \"linkMan\": \"??????\",\n" +
                "    \"matchCabin\": false,\n" +
                "    \"mobile\": \"15897736493\",\n" +
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

        JSONObject jsonObject = JSON.parseObject(orderJson);
        Orderinfo orderinfo = jsonObject.toJavaObject(Orderinfo.class);
        GSCreateOrderService service = new GSCreateOrderService();

        //????????????
//        service.StartCreateOrder(orderJson, 0, 0);

        //??????????????????????????????
        String ip_port = DailiyunService.getRandomNewDailiIp(50);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        System.out.println("??????IP?????????" + ip_port);
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
        CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setDefaultCookieStore(cookieStore)
                .setKeepAliveStrategy(myStrategy)
                .build();

        HttpClientContext context = new HttpClientContext();

        //??????????????????????????????
        Long startTime = System.currentTimeMillis();
        String result = "";
        for (int i = 0; i < 3; i++) {
            result = service.queryFlightsNew(client, defaultRequestConfig, "", orderinfo, "", 0, FingerPrintUtil.getDesc(), 0);
            if (!result.contains("retry")) {
                break;
            }
        }
        Long endTime = System.currentTimeMillis();
        if (!result.contains("sta")) {
            System.out.println("?????????????????????=====?????????????????????" + (endTime - startTime) / 1000 + "s");
        } else {
            System.out.println("?????????????????????" + (endTime - startTime) / 1000 + "s");
        }

//        String s ="N_V_IN=/ieECLAeRLdnpcmcywd6SVqKEppl1Y1FkDpbDhwhstP7wqVujxds96V0TXLEyEDFDMWO2Jh+38dBuTTqbjPjcg==;";
//        String regEx = "N_V_IN=(.*?);";
//        System.out.println(service.getSubUtil(s, regEx));
//
//        File file = new File("C:/GS.txt");
//        String text = FileUtils.readFileToString(file,"UTF-8");
//        System.out.println("??????????????????:" + service.parseFlights(text, orderinfo));


        /********************??????webDriver????????????*****************************/
//        WebDriver driver = PhantomjsDriverUtil.getProxyWebDriver(proxyIp, String.valueOf(proxyPort), "feeyeapp", "feeye789");
//        WebDriver driver = WebDriverObtain.getChromeDriver(proxyIp, String.valueOf(proxyPort), "feeyeapp", "feeye789");
//        for (int i = 0; i <1 ; i++) {
//            String cookie = service.queryFlights(orderinfo, "", driver);
//            Thread.sleep(20 * 1000);
//        }
        /********************??????webDriver????????????*****************************/
    }

    /**
     * ????????????(WebDriver??????)
     *
     * @param orderJson
     * @param cookie
     * @param webDriver
     * @return
     */
    public String queryFlights(Orderinfo orderJson, String cookie, WebDriver webDriver) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String back = "";
        String order_id = orderJson.getId();
        String origin = orderJson.getFlights().get(0).getDeparture();
        String destination = orderJson.getFlights().get(0).getArrival();
        String departureDate = orderJson.getFlights().get(0).getDepartureDate();
        String flightNo = orderJson.getFlights().get(0).getFlightNo();
        float price = Float.parseFloat(orderJson.getFlights().get(0).getPrice());
        try {
//            webDriver.manage().window().setSize(new Dimension(1920,1080));
//            webDriver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS); //??????????????????????????????
            webDriver.get("http://www.tianjin-air.com");
//            Thread.sleep(2 * 1000);

//            WebElement flightSearchForm = webDriver.findElements(By.cssSelector("form[name='flightSearchForm']")).get(0);
//            List <WebElement> inputs = flightSearchForm.findElements(By.tagName("input"));;

//            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
//            String js = "$(\"form[name='flightSearchForm'] :input\").attr('type','text')";
//            javascriptExecutor.executeScript(js);

            //????????????????????????????????????
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[1]")).sendKeys("0");
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[2]")).sendKeys(origin);
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[3]")).sendKeys(destination);
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[4]")).sendKeys(departureDate);
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[5]")).sendKeys("");
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[6]")).sendKeys("1");
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[7]")).sendKeys("0");
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[8]")).sendKeys("0");
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[9]")).sendKeys("0");
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[10]")).sendKeys("0");
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[11]")).sendKeys("Economy");
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]/input[12]")).sendKeys("");
//            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/form[1]")).submit();

/*            webDriver.findElements(By.cssSelector("input[name='type']")).get(1).sendKeys("0");
            webDriver.findElements(By.cssSelector("input[name='origin']")).get(1).sendKeys(origin);
            webDriver.findElements(By.cssSelector("input[name='destination']")).get(1).sendKeys(destination);
            webDriver.findElements(By.cssSelector("input[name='departureDate']")).get(1).sendKeys(departureDate);
            webDriver.findElements(By.cssSelector("input[name='arrivalDate']")).get(1).sendKeys("");
            webDriver.findElements(By.cssSelector("input[name='ADT']")).get(1).sendKeys("1");
            webDriver.findElements(By.cssSelector("input[name='CNN']")).get(1).sendKeys("0");
            webDriver.findElements(By.cssSelector("input[name='INF']")).get(1).sendKeys("0");
            webDriver.findElements(By.cssSelector("input[name='PWD']")).get(1).sendKeys("0");
            webDriver.findElements(By.cssSelector("input[name='MWD']")).get(1).sendKeys("0");
            webDriver.findElements(By.cssSelector("input[name='cabin']")).get(1).sendKeys("Economy");
//            webDriver.findElements(By.cssSelector("input[name='_csrf']")).get(1).sendKeys("");
            webDriver.findElements(By.cssSelector("form[name='flightSearchForm']")).get(1).submit();*/

            WebElement element = webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/div/div[1]/div/div[2]/div/div/input")); //?????????
            Actions actions = new Actions(webDriver);
            actions.moveToElement(element).click().perform();
            element.sendKeys(origin);
//            Thread.sleep(1 * 1000);
            webDriver.findElement(By.cssSelector(".hyperlink")).click();

            element = webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/div/div[1]/div/div[4]/div/div/input")); //?????????
            actions = new Actions(webDriver);
            element.sendKeys(destination);
//            Thread.sleep(1 * 1000);
            webDriver.findElement(By.cssSelector(".hyperlink")).click();
            actions.moveToElement(element).click().perform();

            element = webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/div/div[1]/div/div[5]/div/div/div/input")); //????????????
            actions = new Actions(webDriver);
            actions.moveToElement(element).click().perform();
            String[] dateSplit = departureDate.split("-");
            List<WebElement> dateList = webDriver.findElements(By.cssSelector(".vdp-datepicker__calendar"));
            for (int i = 0; i < dateList.size(); i++) {
                String month = dateList.get(i).findElement(By.cssSelector(".day__month_btn")).getText().substring(0, 2);
                if (true) {
                    List<WebElement> e = dateList.get(i).findElements(By.cssSelector(".cell.day"));
                    for (int j = e.size() - 1; j > 0; j--) {
                        WebElement date = e.get(j).findElement(By.cssSelector(".date"));
                        String dateStr = date.getText();
                        if (dateSplit[2].equals(dateStr)) {
                            actions.moveToElement(date).click().perform();
                            break;
                        }
                    }
                    break;
                }
            }
//            Thread.sleep(1 * 1000);
            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/div/div[1]/div/div[8]/button")).click(); //????????????

//            Thread.sleep( 1 * 1000);
            back = webDriver.getPageSource();
            List<WebElement> elements = webDriver.findElements(By.cssSelector(".mod-flight")); //??????div
            if (!elements.isEmpty()) {
                //??????cookie
                Set<org.openqa.selenium.Cookie> cookies = webDriver.manage().getCookies();
                for (org.openqa.selenium.Cookie c : cookies) {
                    if ("csrfToken".equals(c.getName())) {
                        csrfToken = c.getValue();
                    }
                    cookie += c.getName() + "=" + c.getValue() + ";";
                }
                logger.info(order_id + "??????????????????cookie:" + cookie);
                System.out.println("??????????????????cookie:" + cookie);

//                Thread.sleep(3 * 1000);
                //????????????
                for (int i = 0; i < elements.size(); i++) {
                    WebElement airline = elements.get(i).findElements(By.cssSelector(".airline")).get(0);
                    String flightInfo = airline.findElement(By.cssSelector(".name")).getText();
                    if (flightInfo.contains(flightNo)) {
                        List<WebElement> itemList = elements.get(i).findElements(By.cssSelector(".item"));
                        for (int j = itemList.size() - 1; j >= 0; j--) {
                            WebElement itemWrap = itemList.get(j).findElement(By.cssSelector(".item-wrap"));
                            float flightPrice = Float.parseFloat(itemWrap.findElement(By.cssSelector(".value")).getText());
                            if (flightPrice == price) {
                                itemWrap.click();
                            }
                        }
                    }
                }
            }

//            System.out.println(order_id + "??????????????????:" + back);
            if (back.contains("???????????????????????????????????????????????????")) {
                return queryFlights(orderJson, "", webDriver);
            } else if (back.contains("??????????????????")) {
                for (int i = 0; i < 10; i++) {
                    //webDriver???????????????,???????????????
                    WebElement codeImgEle = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div[4]/div/div[2]/div/div[2]/img"));
                    String height = codeImgEle.getCssValue("height");
                    String width = codeImgEle.getCssValue("width");
                    File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
                    BufferedImage fullImg = ImageIO.read(screenshot);
                    Point point = codeImgEle.getLocation();
                    int eleWidth = codeImgEle.getSize().getWidth();
                    int eleHeight = codeImgEle.getSize().getHeight();
                    BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
                    ImageIO.write(eleScreenshot, "png", screenshot);
                    String fileUri = "C://testImg//" + "GSverifyCode.png";
                    File imageFile = new File(fileUri);
                    FileUtils.copyFile(screenshot, imageFile);
                    if (screenshot != null) {
                        screenshot.delete();
                    }
                    //???????????????????????????
                    FileEntity fileEntity = new FileEntity(imageFile);
                    String url = "http://127.0.0.1:1111";
                    HttpPost imgPost = new HttpPost(url);
                    imgPost.setEntity(fileEntity);
                    httpClient = HttpClients.createDefault();
                    response = httpClient.execute(imgPost);
                    String verifyCode = EntityUtils.toString(response.getEntity());
                    System.out.println("?????????????????????:" + verifyCode);
                    logger.info(order_id + "?????????????????????:" + verifyCode);
                    webDriver.findElement(By.xpath("//*[@id=\"app\"]/div[4]/div/div[2]/div/div[2]/label/input")).sendKeys(verifyCode); //??????????????????
                    webDriver.findElement(By.xpath("//*[@id=\"app\"]/div[4]/div/div[2]/div/div[3]/button")).click();
                    Thread.sleep(5 * 1000);
                    back = webDriver.getPageSource();
                    elements = webDriver.findElements(By.cssSelector(".mod-flight")); //??????div
                    if (!elements.isEmpty()) {
                        //??????cookie
                        Set<org.openqa.selenium.Cookie> cookies = webDriver.manage().getCookies();
                        for (org.openqa.selenium.Cookie c : cookies) {
                            cookie += c.getName() + "=" + c.getValue() + ";";
                        }
                        logger.info(order_id + "??????????????????cookie:" + cookie);
                        System.out.println("??????????????????cookie:" + cookie);

                        //????????????
                        for (int k = 0; k < elements.size(); k++) {
                            WebElement airline = elements.get(k).findElements(By.cssSelector(".airline")).get(0);
                            String flightInfo = airline.findElement(By.cssSelector(".name")).getText();
                            if (flightInfo.contains(flightNo)) {
                                List<WebElement> itemList = elements.get(i).findElements(By.cssSelector(".item"));
                                for (int j = itemList.size() - 1; j >= 0; j--) {
                                    WebElement itemWrap = null;
                                    try {
                                        itemWrap = itemList.get(j).findElement(By.cssSelector(".item-wrap"));
                                    } catch (NoSuchElementException e) {
                                        logger.error("??????????????????");
                                    }
                                    if (itemWrap != null) {
                                        float flightPrice = Float.parseFloat(itemWrap.findElement(By.cssSelector(".value")).getText());
                                        if (flightPrice == price) {
                                            itemWrap.click();
                                            List<WebElement> cellFooter = webDriver.findElements(By.cssSelector(".cell-footer"));
                                            String productPrice = "";
                                            for (WebElement c : cellFooter) {
                                                try {
                                                    productPrice = c.findElement(By.cssSelector(".value")).getText();
                                                } catch (NoSuchElementException e) {
                                                    logger.error("??????????????????");
                                                }
                                                WebElement priceChoose = null;
                                                if (StringUtils.isNotEmpty(productPrice) && price == Float.parseFloat(productPrice)) {
                                                    try {
                                                        priceChoose = c.findElement(By.cssSelector(".tj-icon tj-icon-quan"));
                                                    } catch (NoSuchElementException e) {
                                                        logger.error("??????????????????");
                                                    }
                                                    priceChoose.click();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(back) && !back.contains("?????????")) {
                        break;
                    }
                }
                System.out.println("???????????????????????????" + back);
                logger.info(order_id + "????????????????????????:" + back);
            } else if (back.contains("??????????????????????????????????????????????????????????????????")) {
                return "ERROR:??????????????????????????????????????????????????????????????????";
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
                if (webDriver != null) {
//                    Thread.sleep(1200 * 1000);
//                    webDriver.quit();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return cookie;
    }


    /**
     * ???????????????????????????
     *
     * @param text
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