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
 * @description 福州航空官网出票
 * @date 2019/3/15
 */
public class FUCreateOrderService {
    private static final Logger logger = LoggerFactory.getLogger(FUCreateOrderService.class);
    private static final int timeout = 50000;
    String dailiyunAccount = "feeyeapp:feeye789";
    private static Map<String, String> cookieMap = new HashMap<String, String>();

    /**
     * FU创建订单
     *
     * @param orderJson   订单信息
     * @param retryCount  选择航班整体重试次数
     * @param requestType 请求类型
     * @return createOrderBack 创单返回信息
     */
    private String StartCreateOrder(String orderJson, int retryCount, int requestType) {
        long nowDateTime = new Date().getTime(); //获取当前时间，后面步骤要根据该时间点做超时处理
        String createOrderBack = "";
        String cookie = "";
        String loginId = ""; //FU官网账户id
//        String cancelUrl = PropertiesUtils.getPropertiesValue("config", "cancelUrl");
        String cancelUrl = "";
        if (StringUtils.isEmpty(orderJson)) {
            return "ERROR:数据不完整";
        }
        logger.info("传过来的订单信息" + orderJson);

        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();
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
        org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            logger.error("error", e1);
        }
        //代理云
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];  //代理IP地址
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //代理IP端口
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(dailiyunProxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //代理云账号密码
        String proxyUser = "feeyeapp";
        String proxyPass = "feeye789";
        //TODO 从配置文件读取
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
            //json转换
            JSONObject jsonObject = JSON.parseObject(orderJson);
            OrderJson orderJsonBean = jsonObject.toJavaObject(OrderJson.class);

            String order_id = orderJsonBean.getId();
            String childrenUser = orderJsonBean.getUsername();
            String billNo = "";
            String newOrderNo = "";
            try {
                billNo = orderJsonBean.getBillNo();
            } catch (Exception e) {
                logger.error("未获取到billNo");
            }
            try {
                newOrderNo = orderJsonBean.getNewOrderNo();
            } catch (Exception e) {
                logger.error("未获取到newOrderNo");
            }

/*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "已取消出票");
                sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "已取消出票");
                return "取消出票";
            }*/

            //登录
            for (int i = 0; i < 3; i++) {
                cookie = login(httpclient, defaultRequestConfig, cookieStore, orderJsonBean);
                if (cookie.contains("ERROR")) {
                    sendOrderStatus(childrenUser, order_id, cookie);
                    sendCreateOrderInfo("error", "已取消出票" + cookie, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return cookie;
                }
                if (StringUtils.isNotEmpty(cookie))
                    cookieMap.put("cookie", cookie);
                break;
            }

/*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "已取消出票");
                sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "已取消出票");
                return "取消出票";
            }*/

//            sendOrderStatus(childrenUser, order_id, "FU加载用户信息");
            //加载用户信息,返回loginid
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
                sendOrderStatus(childrenUser, order_id, "已取消出票");
                sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info("已取消出票");
                return "取消出票";
            }
*/

//            sendOrderStatus(childrenUser, order_id, "FU查询航班");
            //查询航班
            String handerFlightsDataBack = "";
            for (int i = 0; i < 3; i++) {
                String queryFlightsBack = queryFlights(httpclient, defaultRequestConfig, orderJsonBean, cookie, context);
                if (queryFlightsBack.contains("ERROR")) {
                    sendOrderStatus(childrenUser, order_id, queryFlightsBack);
                    sendCreateOrderInfo("error", "已取消出票" + queryFlightsBack, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return queryFlightsBack;
                }
                if (StringUtils.isNotEmpty(queryFlightsBack)) {
                    handerFlightsDataBack = handerFlightsData(queryFlightsBack, orderJsonBean);
                    if (handerFlightsDataBack.contains("ERROR")) {
                        sendOrderStatus(childrenUser, order_id, handerFlightsDataBack);
                        sendCreateOrderInfo("error", "已取消出票" + handerFlightsDataBack, "", childrenUser, "", order_id, "", "",
                                null, "", "", "false", "true", billNo, requestType);
                        return handerFlightsDataBack;
                    }
                    break;
                }
            }

            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "已取消出票");
                sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info("已取消出票");
                return "取消出票";
            }

            sendOrderStatus(childrenUser, order_id, "FU进入创单过程");
            //创单过程
            String key = createKey(httpclient, defaultRequestConfig, cookieStore, handerFlightsDataBack);
            if (key.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, key);
                sendCreateOrderInfo("error", "已取消出票" + key, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return key;
            }
            String preOrderBack = preOrder(httpclient, defaultRequestConfig, cookieStore, key);
            if (preOrderBack.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, preOrderBack);
                sendCreateOrderInfo("error", "已取消出票" + preOrderBack, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return preOrderBack;
            }
            String handerDataPreOrderBack = handerDataPreOrder(preOrderBack, orderJsonBean);
            key = createKey(httpclient, defaultRequestConfig, cookieStore, handerDataPreOrderBack);
            if (key.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, key);
                sendCreateOrderInfo("error", "已取消出票" + key, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return key;
            }

            sendOrderStatus(childrenUser, loginId, "FU添加乘机人、联系人");
            String submitOrderBack = submitOrder(httpclient, defaultRequestConfig, cookieStore, key);
            String handerDataSubmitOrderBack = handerDataSubmitOrder(submitOrderBack, orderJsonBean);
            key = createKey(httpclient, defaultRequestConfig, cookieStore, handerDataSubmitOrderBack);
            if (key.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, key);
                sendCreateOrderInfo("error", "已取消出票" + key, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return key;
            }

            String vasOrderBack = vasOrder(httpclient, defaultRequestConfig, cookieStore, key);
            String handerDataVasOrderBack = handerDataVasOrder(vasOrderBack, orderJsonBean);
            key = createKey(httpclient, defaultRequestConfig, cookieStore, handerDataVasOrderBack);
            if (key.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, key);
                sendCreateOrderInfo("error", "已取消出票" + key, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return key;
            }

            sendOrderStatus(childrenUser, order_id, "FU生成订单");
            createOrderBack = ackOrder(httpclient, defaultRequestConfig, cookieStore, key);
            if (createOrderBack.contains("ERROR")) {
                sendOrderStatus(childrenUser, order_id, createOrderBack);
                sendCreateOrderInfo("error", "已取消出票" + createOrderBack, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
            }

            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "已取消出票");
                sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info("已取消出票");
                return "取消出票";
            }

            //处理创单返回数据
            JSONObject createOrderBackObj = JSON.parseObject(createOrderBack);
            String orderNo = createOrderBackObj.getJSONObject("data").getString("orderNo");
            String orderId = createOrderBackObj.getJSONObject("data").getString("id"); //订单id
            String orderAllMoney = createOrderBackObj.getJSONObject("data").getString("orderAllMoney"); //总价格

            sendOrderStatus(childrenUser, order_id, "FU获取支付接口（易宝支付）");
            //获取支付接口
            String locationValue = "";
            for (int i = 0; i < 3; i++) {
                locationValue = uniPay(httpclient, defaultRequestConfig, cookieStore, orderId, orderNo, loginId, order_id);
                if (locationValue.contains("ERROR")) {
                    sendOrderStatus(childrenUser, order_id, locationValue);
                    sendCreateOrderInfo("error", "已取消出票" + locationValue, orderAllMoney, childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return locationValue;
                }
                if (StringUtils.isNotEmpty(locationValue)) {
                    break;
                }
            }

            sendOrderStatus(childrenUser, order_id, "FU易宝支付");
//            //TODO 测试支付
//            if (DateUtil.IsRunningTimeOut(nowDateTime, 15 * 1000 * 60)) {
//                sendOrderStatus(childrenUser, order_id, "出票超时，停止支付");
//                logger.info("出票超时，停止支付");
//                sendCreateOrderInfo("error", "创单超时，停止支付", orderAllMoney, childrenUser, "", order_id,
//                        "", "", null, "", "", "false",
//                        "true", billNo, requestType);
//            }
//            OfficialXykPayService pay = new OfficialXykPayService();
//            if (StringUtils.isNotEmpty(locationValue)) {
//                String cookies = cookieMap.get("cookie");
//                payResultMap = pay.yeePayMFNew(locationValue, orderJson, cookies);
//                return payResultMap.toString();
//            }


            sendOrderStatus(childrenUser, order_id, "FU票号回填");

            //票号回填
            String uniOrderDetailBack = "";
            for (int i = 0; i < 3; i++) {
                uniOrderDetailBack = uniOrderDetail(httpclient, defaultRequestConfig, cookieStore, orderId, loginId);
                if (uniOrderDetailBack.contains("ERROR")) {
                    sendOrderStatus(childrenUser, order_id, uniOrderDetailBack);
                    sendCreateOrderInfo("error", "已取消出票" + uniOrderDetailBack, "", childrenUser, "", order_id, "", "",
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
                                        ticketnos[0], "易宝支付", ticketnos[1], "false", "true", billNo, requestType);
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
                sendCreateOrderInfo("error", "未找到票号信息" + orderAllMoney, "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return "ERROR:未找到票号信息";
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
     * 登录
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

        sendOrderStatus(childrenUser, order_id, "进入FU首页");
        try {
            //目标主机
            HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
            HttpGet get = new HttpGet("http://www.fuzhou-air.cn/");
            get.setConfig(config);
            get.setHeader("Host", "www.fuzhou-air.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
            response = httpclient.execute(host, get);
            //获取cookie
            Header[] headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    cookie += header.getValue() + ";";
                }
            }
            System.out.println("进入FU首页获取的cookie:" + cookie);
            logger.info("进入FU首页获取的cookie" + cookie);

            sendOrderStatus(childrenUser, order_id, "FU处理登录验证码");
            // 获取验证码请求
            host = new HttpHost("fuzhou-air.cn", 80, "http");
            //生成16位的随机小数
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

//            //云速验证码识别
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
//            System.out.println("识别登录验证码:" + result);

//            sendOrderStatus(childrenUser, order_id, "FU官网登录校验验证码");
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


//            //TODO 改为掉接口
            System.out.print("输入验证码:");
            Scanner scan = new Scanner(System.in);
            String result = scan.nextLine();
            System.out.println("验证码：" + result);

            sendOrderStatus(childrenUser, order_id, "FU官网登录");
            //处理登录from表单post请求
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
                System.out.println("FU官网登录成功！" + backstr);
                logger.info("FU官网登录成功");
                sendOrderStatus(childrenUser, order_id, "FU官网登录成功");
            } else if (jsonObject.getString("msg").contains("登录失败")) {
                logger.error("FU官网登录失败！请确认用户名/手机号否正确,或尝试使用其它登录方式进行登录！");
                return "ERROR:FU官网登录失败！请确认用户名/手机号否正确,或尝试使用其它登录方式进行登录！";
            } else if (jsonObject.getString("msg").contains("您输入的用户名或密码有误")) {
                logger.error("FU官网登录用户名或密码有误");
                return "ERROR:FU官网登录用户名或密码有误";
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
     * 加载用户信息
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
                logger.info("FU加载用户信息返回数据");
                System.out.println("加载用户信息返回数据:" + back);
                back = backJson.getJSONObject("data").get("loginId").toString();
            } else {
                System.out.println("FU加载用户信息失败:" + back);
                logger.error("FU加载用户信息失败");
                return "ERROR:FU加载用户信息失败";
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
     * 加载常用旅客信息
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
                logger.info("FU加载常用旅客返回:" + back);
                System.out.println("FU加载常用旅客返回:" + back);
                JSONArray passengers = backJson.getJSONObject("data").getJSONArray("list");
//                int maxPageNumber = Integer.parseInt(backJson.getJSONObject("data").getString("maxPageNumber"));
                for (int i = 0; i < passengers.size(); i++) {
                    String id = passengers.getJSONObject(i).getString("id");
                    buffer.append(id + ";");
                }
                return buffer.toString();
            } else {
                logger.error("FU加载常用旅客信息失败");
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
     * 删除旅客信息
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
                    logger.info("FU删除常用旅客信息成功");
                } else {
                    logger.error("FU删除常用旅客信息失败");
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
     * 查询航班
     *
     * @param orderJson
     * @param cookie
     * @param isVerify   是否需要验证码,默认false
     * @param verifyCode 验证码
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
            //获取加密参数desc
//            String desc = FingerPrintUtil.getDesc();
            JSONObject queryObject = new JSONObject();
            queryObject.put("index", 0);
            queryObject.put("orgCity", orgCity);
            queryObject.put("dstCity", dstCity);
            queryObject.put("flightdate", flightDate);
            //单程
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
            logger.info("查询航班返回：" + back);
            System.out.println("查询航班返回：" + back);
            response.close();

            if (StringUtils.isNotEmpty(back) && back.contains("This page can't be displayed")) {
                Thread.sleep(2 * 1000);
                return queryFlights(httpclient, config, orderJson, cookie, context);
            }
            JSONObject backJson = JSON.parseObject(back);
            if (backJson.size() == 3) {
                JSONArray segmentsArray = backJson.getJSONObject("data").getJSONArray("flights");
                if (segmentsArray.isEmpty()) {
                    logger.error("FU没有查询到相关航班信息" + back);
                    return "ERROR:FU没有查询到相关航班信息";
                } else {
                    logger.error("FU查询到的航班信息" + back);
                    System.out.println("FU查询航班信息:" + back);
                }
            }
            //需要验证码
            if (backJson.size() == 1) {
                if ("10000".equals(backJson.get("sta").toString())) {
                    //请求滑动验证码
                    HttpGet get = new HttpGet("http://www.fuzhou-air.cn/hnatravel/register?verify=gt&s_p_type=32041");
                    get.setHeader("Host", "www.fuzhou-air.cn");
                    get.setHeader("Cookie", cookie);
                    get.setConfig(config);
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                    response = httpclient.execute(get);
                    back = EntityUtils.toString(response.getEntity(), "utf-8");
                    response.close();
                    logger.info("请求极验返回：" + back);
                    JSONObject jsonObject = JSON.parseObject(back);
                    String challenge = jsonObject.getString("challenge");
                    String gt = jsonObject.getString("gt");
                    String result = "";
                    for (int i = 0; i <= 3; i++) {
                        result = GeetestRecognition.recognition(gt, "http://www.fuzhou-air.cn/hnatravel/verify", challenge, "da608f56440c4479ac7aa32e4836a52a");
                        logger.info("极验打码返回: " + result);
                        System.out.println("极验打码返回: " + result);
                        if (StringUtils.isEmpty(result)) {
                            continue;
                        }
                        if (StringUtils.isNotEmpty(result) && result.contains("识别失败")) {
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
                    System.out.println("验证码校验返回:" + back);
                    logger.info("验证码校验返回：" + back);
                    Header[] headersArr = response.getAllHeaders();
                    for (Header header : headersArr) {
                        if ("Set-Cookie".equals(header.getName())) {
                            cookie += header.getValue() + ";";
                        }
                    }
                    response.close();
                    //回调
                    return queryFlights(httpclient, config, orderJson, cookie, context);
                } else if ("10002".equals(backJson.get("sta").toString())) {
                    return queryFlights(httpclient, config, orderJson, cookie, context);
                } else if ("10001".equals(backJson.get("sta").toString())) {
                    //TODO 封ip情况
//                    logger.error("当前ip已被封");
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

    //处理查询航班返回的数据
    private String handerFlightsData(String back, OrderJson orderJson) {
        String handerFlightsDataBack = "";
        int adultNum = 0;
        int childNum = 0;
        int infantNum = 0;
        for (OrderJson.PassengersBean passengers : orderJson.getPassengers()) {
            switch (passengers.getPassengerType()) {
                case "成人":
                    adultNum++;
                    break;
                case "儿童":
                    childNum++;
                    break;
                case "婴儿":
                    infantNum++;
                    break;
                default:
                    break;
            }
        }
        //组装查询参数
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
                //json转object
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
                                //过滤放心飞的舱位
                                if ("放心飞".equals(segmentsBeans.get(i).getProducts().get(j).getCabin().getSignProductName())) {
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
                    return "ERROR:FU没有匹配到符合条件的航班信息";
                }
                handerFlightsDataBack = queryObject.toString();
            } catch (JSONException e) {
                logger.error("json解析异常", e);
            }
        }
        return handerFlightsDataBack;
    }

    /**
     * 获取key
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
                logger.info("FU获取key成功:" + key);
                System.out.println("获取key成功:" + key);
            } else {
                System.out.println("获取key失败:" + back);
                logger.error("FU获取key失败" + back);
                return "ERROR:FU获取key失败";
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

    //获取key,作为下步查询参数(webDriver方式)
    private String queryFlights(OrderJson orderJson, String cookie, WebDriver webDriver) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        InputStream is = null;
        String verifyCode = "";
        String newCookie = "";
        String back = "";

        String childrenUser = orderJson.getUsername();
        String order_id = orderJson.getId();

        try {
            //请求主机
            HttpHost host = new HttpHost("fuzhou-air.cn", 80, "http");
            //解析查询参数
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
                    case "成人":
                        adultNum++;
                        break;
                    case "儿童":
                        childNum++;
                        break;
                    case "婴儿":
                        infantNum++;
                        break;
                    default:
                }
            }
            //拼接重定向请求uri
            String uri = "http://www.fuzhou-air.cn/b2c/static/flightSearch.html?orgCityCode=" + orgCity + "&dstCityCode=" + dstCity + "&orgDate=" +
                    flightDate + "&dstDate=" + "&adult=" + adultNum + "&child=" + childNum + "&infant=" + infantNum + "&trip=ONEWAY";
            webDriver.get(uri);
            //获取返回的cookie
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

            //获取执行快照
            File srcFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(srcFile, new File("C:\\testImg\\screenshote1.png"));
            } catch (IOException e) {
                logger.error("error", e);
            }
            //组装查询参数
            JSONObject queryObject = new JSONObject();
            JSONArray segments = new JSONArray();
            JSONObject segmentsObj = new JSONObject();
            segments.add(0, segmentsObj);
            queryObject.put("segments", segments);
            queryObject.put("adultCount", adultNum);
            queryObject.put("childCount", childNum);
            queryObject.put("infantCount", infantNum);
            queryObject.put("type", "chooseSegment");

            //返回的html源码
            String pageSourceHtml = webDriver.getPageSource();
//            System.out.println(pageSourceHtml);
            //解析html
            Document document = Jsoup.parse(pageSourceHtml);
            //需要破解验证码
            if (document.getElementById("formValid") != null) {
                String backStr = "";
                // TODO 掉破解验证码接口
                //webDriver截图验证码,保存到本地
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
                System.out.print("输入查询航班验证码:");
                Scanner scan = new Scanner(System.in);
                verifyCode = scan.nextLine();
                System.out.println("查询航班验证码：" + verifyCode);
                webDriver.findElements(By.name("tickcode")).get(0).sendKeys(verifyCode);
                webDriver.findElements(By.name("submit")).get(0).click();
                Thread.sleep(3 * 1000);
                File file = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
                try {
                    FileUtils.copyFile(file, new File("C:\\testImg\\screenshote2.png"));
                } catch (IOException e) {
                    logger.error("error", e);
                }
                //设置等待时间
                new WebDriverWait(webDriver, 50).until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[3]/div[1]/div/div[3]/div[3]/div[2]/div[1]/div[8]/span")));
                //解析返回的html,获取航班信息
                String pageSource = webDriver.getPageSource();
//                System.out.println("返回的html" + pageSource);
                //解析html
                Document doc = Jsoup.parse(pageSource);
                Elements flightInfo = doc.getElementsByClass("preBookTick btn-preBook b_rd_4 fs-16");
                for (Element e : flightInfo) {
                    String cabinNaem = e.attributes().get("data-cabin-name");
                    //过滤放心飞
                    if ("放心飞".equals(cabinNaem)) {
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
                    //过滤放心飞的产品
                    if ("放心飞".equals(cabinNaem)) {
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
//                            sendOrderStatus(childrenUser,order_id,"没有查询到符合条件的航班信息");
                            logger.info("没有查询到符合条件的航班信息");
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
     * 创单准备
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
                } else if (backJson.get("msg").toString().contains("您的账户中尚有未出票订单")) {
                    logger.error("您的账户中尚有未出票订单，请先出票后再继续预");
                    return "ERROR:您的账户中尚有未出票订单，请先出票后再继续预订";
                } else if (backJson.get("msg").toString().contains("SessionRequestKey is error")) {
                    logger.error("SessionRequestKey is error");
                    return "ERROR:SessionRequestKey is error";
                } else if (backJson.get("msg").toString().contains("请登录")) {
                    logger.error("未登录");
                    return "ERROR:未登录";
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
     * 提交订单信息
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
                    logger.error("submitOrder方法执行错误");
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
     * 验证订单
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
                    logger.error("vasOrder方法执行错误");
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
     * 确认订单信息
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
                    logger.info("FU创建订单成功:" + ackOrderBack);
                    System.out.println("ackOrderBack:" + ackOrderBack);
                } else {
                    logger.error("FU创单失败");
                    return "ERROR:FU创单失败";
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

    //处理准备创单（preOrder）返回的数据
    public String handerDataPreOrder(String preOrderBack, OrderJson orderJson) {
        String backStr = "";
        //构造json参数
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
                //json转object
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
                //TODO 待确认字段
                segmentsObj.put("couponIssMark", null);
                segmentsObj.put("couponIssDiscount", "0");
                segmentsObj.put("couponIssOrderNum", "0");
                segmentsObj.put("stopCity", segmentInfo.getString("stopCity"));
                segmentsObj.put("dstAirport", segmentInfo.getString("dstAirport"));
                segmentsObj.put("orgAirport", segmentInfo.getString("orgAirport"));
                segmentsObj.put("stopArrTime", segmentInfo.getString("stopArrTime"));
                segmentsObj.put("stopDepTime", segmentInfo.getString("stopDepTime"));
                segmentsObj.put("stopsMsg", segmentInfo.getString("stopsMsg"));
                //TODO 计算 arrTime - depTime的时间差, 转换为 5小时50分钟 格式
                segmentsObj.put("cost", "");
                for (OrderJson.PassengersBean pb : orderJson.getPassengers()) {
                    JSONObject passengersObj = new JSONObject();
                    if ("成人".equals(pb.getPassengerType())) {
                        passengersObj.put("passengerType", "ADULT");
                    } else if ("儿童".equals(pb.getPassengerType())) {
                        passengersObj.put("passengerType", "CHILD");
                    } else if ("婴儿".equals(pb.getPassengerType())) {
                        passengersObj.put("passengerType", "INFANT");
                    }
                    passengersObj.put("isInsure", "0");
                    passengersObj.put("insurance1", "0");
                    passengersObj.put("name", pb.getPassengerName());
                    if ("身份证".equals(pb.getPassengerCardType())) {
                        passengersObj.put("certificateType", "NI");
                    } else if ("护照".equals(pb.getPassengerCardType())) {
                        passengersObj.put("certificateType", "PP");
                    } else if ("其他".equals(pb.getPassengerCardType())) {
                        passengersObj.put("certificateType", "ID");
                    }
                    passengersObj.put("certificateNo", pb.getIdcard());
                    passengersObj.put("certificateNo", pb.getIdcard());
                    //乘机人电话全部设置为联系人电话
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
                logger.error("json解析异常", e);
            }
        }
        return backStr;
    }

    //处理提交订单（submitOrder）返回的数据
    private String handerDataSubmitOrder(String submitOrderBack, OrderJson orderJson) {
        String backStr = "";
        //构造json参数
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
        itinerary.put("canton", "区县");
        itinerary.put("city", "市");
        itinerary.put("name", "");
        itinerary.put("province", "省");
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
                //json转object
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
                //TODO 待确认字段
                segmentsObj.put("couponIssMark", null);
                segmentsObj.put("couponIssDiscount", "0");
                segmentsObj.put("couponIssOrderNum", "0");
                segmentsObj.put("stopCity", segmentInfosBean.getStopCity());
                segmentsObj.put("dstAirport", segmentInfosBean.getDstAirport());
                segmentsObj.put("orgAirport", segmentInfosBean.getOrgAirport());
                segmentsObj.put("stopArrTime", segmentInfosBean.getStopArrTime());
                segmentsObj.put("stopDepTime", segmentInfosBean.getStopDepTime());
                segmentsObj.put("stopsMsg", segmentInfosBean.getStopsMsg());
                //TODO 计算 arrTime - depTime的时间差, 转换为 5小时50分钟 格式
                segmentsObj.put("cost", "");
                List<OrderJson.PassengersBean> passengersBeanList = orderJson.getPassengers();
                for (int i = 0; i < passengersBeanList.size(); i++) {
                    JSONObject passengersObj = new JSONObject();
                    if ("成人".equals(passengersBeanList.get(i).getPassengerType())) {
                        passengersObj.put("passengerType", "ADULT");
                    } else if ("儿童".equals(passengersBeanList.get(i).getPassengerType())) {
                        passengersObj.put("passengerType", "CHILD");
                    } else if ("婴儿".equals(passengersBeanList.get(i).getPassengerType())) {
                        passengersObj.put("passengerType", "INFANT");
                    }
                    passengersObj.put("isInsure", "0");
                    passengersObj.put("insurance1", "0");
                    passengersObj.put("name", passengersBeanList.get(i).getPassengerName());
                    if ("身份证".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "NI");
                    } else if ("护照".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "PP");
                    } else if ("其他".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "ID");
                    }
                    passengersObj.put("certificateNo", passengersBeanList.get(i).getIdcard());
                    //乘机人电话全部设置为联系人电话
                    passengersObj.put("mobilePhone", orderJson.getMobile());
                    passengersObj.put("birthday", passengersBeanList.get(i).getBirthday().substring(0, 10));
                    passengersObj.put("uniqueNo", fUSubmitOrderInfo.getData().getPassengersParams().get(i).getUniqueNo());
                    passengers.add(passengersObj);
                }
                backStr = queryObject.toString();
            } catch (JSONException e) {
                logger.error("json解析异常", e);
            }
        }
        return backStr;
    }

    //处理验证订单（vasOrder）返回的数据
    private String handerDataVasOrder(String vasOrderBack, OrderJson orderJson) {
        String backStr = "";
        //构造json参数
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
        //TODO 后面改为从配置文件读取
//        queryObject.put("desc", "JeUEPgZXFsnSDYkzgko8AhfaZnD%2BEs0QIMdOJLlMbIYnn3u7w27Q1aRZP0QqTXy0Wu/g27R18sBcmJFRbqjnfqYUZc1E/HPXcobO5szVIKbsG2FZpawp4trFiQqTTcwWaaIMDIfhmHCTRkEyJ9o%2BtgSLaqLv4ueKmwigjM6QAvODwuyqU%2BMVjApbM3rZm5Bs75qObyi2kJD/486noWLyzNxFnZYk3JgDKAkTLpevGPgJRfczv807yL8/BM2BS3UAikF0baaUEN1oP/9KUTAahODuKv9Id%2BdPHiGgUHJQUmUATelcgLRmAZyX4HJ93KsRKP4M2Be2pd5w/UuIEB4/1hdiX96VcgJ5jXXmPBjp23Y=");
        queryObject.put("desc", desc);
        if (StringUtils.isNotEmpty(vasOrderBack)) {
            try {
                //json转object
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
                    if ("身份证".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "NI");
                    } else if ("护照".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "PP");
                    } else if ("其他".equals(passengersBeanList.get(i).getPassengerCardType())) {
                        passengersObj.put("certificateType", "ID");
                    }
                    passengers.add(passengersObj);
                }
                backStr = queryObject.toString();
            } catch (JSONException e) {
                logger.error("json解析异常", e);
            }
        }
        return backStr;
    }

    /**
     * 调起支付接口(易宝支付),重定向到易宝支付收银台
     *
     * @param orderJson
     * @param cookie
     * @param createOrderBack 创单返回信息
     * @param loginId
     * @param order_id
     * @return locationValue 重定向地址
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
        String payType = "YEEPAY"; //易宝
        try {
            HttpHost host = new HttpHost("www.fuzhou-air.cn", 80, "http");
            String url = "/payment/UniPay.action?bankId=&payType=" + payType + "&orderId=" + orderId + "&extraOrderId=&orderNo=" + orderNo + "&loginId=" + loginId;
            HttpGet get = new HttpGet(url);
            get.setConfig(config);
            get.setHeader("Host", "www.fuzhou-air.cn");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
            get.setHeader("Cookie", cookieMap.get("cookie"));
            response = httpclient.execute(host, get);
            //返回的状态码
            String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            if ("302".equals(statusCode)) {
                //获取重定向的地址
                Header[] location = response.getHeaders("Location");
                for (int i = 0; i < location.length; i++) {
                    locationValue = location[i].getValue();
                    logger.info(order_id + "Location:" + locationValue);
                }
                if (StringUtils.isEmpty(locationValue)) {
                    logger.info(order_id + "支付请求：" + locationValue);
                    logger.info(order_id + "支付返回：" + back);
                    return "";
                }
            }
            if ("200".equals(statusCode)) {
                back = EntityUtils.toString(response.getEntity());
                Document document = Jsoup.parse(back);
                //获取错误提示信息
                String errorStr = document.select(".errorMsg.fs-20").text();
                return "ERROR:" + errorStr;
            }
            // 第二个请求
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
                logger.info(order_id + "支付请求：" + locationValue);
                logger.info(order_id + "支付返回：" + back);
                return "";
            }
            // 第三个请求
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
                logger.info(order_id + "支付请求：" + locationValue);
                logger.info(order_id + "支付返回：" + back);
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
     * 订单支付
     * @param orderJson
     * @param cookie
     * @param createOrderBack
     * @return
     */
/*    private String orderPay(CloseableHttpClient httpclient, RequestConfig config, OrderJson orderJson, String createOrderBack, String loginId) {
        Map<String, String> payResultMap = new HashMap<String,String>();
        //解析json参数
        String orderId = createOrderBackObj.getJSONObject("data").getJSONArray("passengerList").getJSONObject(0)
                .getJSONArray("passengerSegmentList").getJSONObject(0).get("orderId").toString();
        String orderNo = createOrderBackObj.getJSONObject("data").get("orderNo").toString();
            // 易宝支付
            try {
                sendOrderStatus(childrenUser, order_id, "开始支付订单");
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
                    sendCreateOrderInfo("error", "获取支付重定向链接失败", "", childrenUser, orderNo, order_id,
                            "", "", null, "",
                            "", "false", "true", billNo, requestType);
                    return "ERROR:获取支付重定向链接失败";
                }
            } catch (Exception e) {
                logger.error("error", e);
                sendCreateOrderInfo("error", "获取支付重定向链接失败", "", childrenUser, orderNo, order_id,
                        "", "", null, "", "",
                        "false", "true", billNo, requestType);
                return "ERROR:获取支付重定向链接失败";
            }
            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "已取消出票");
                sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id,
                        "", "", null, "", "", "false",
                        "true", billNo, requestType);
                logger.info("已取消出票");
            }
            if (DateUtil.IsRunningTimeOut(nowDateTime, 15 * 1000 * 60)) {
                sendOrderStatus(childrenUser, order_id, "出票超时，停止支付");
                logger.info("出票超时，停止支付");
                sendCreateOrderInfo("error", "创单超时，停止支付", "", childrenUser, "", order_id,
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
     * 根据订单号查询订单详情
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
                System.out.println("获取订单详情返回:" + orderDetailBack);
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
                logger.error("获取订单信息失败");
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
     * 订单详情参数处理
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
                bankTradeNo = paymentListArray.getJSONObject(0).get("paymentNo").toString(); //支付流水号
            } catch (Exception e) {
                logger.error("error", e);
            }
            for (int i = 0; i < passengerListArray.size(); i++) {
                String passengerName = passengerListArray.getJSONObject(i).get("passengerName").toString();
                String cerNo = passengerListArray.getJSONObject(i).get("cerNo").toString(); //身份证号
                JSONArray segmentListArray = passengerListArray.getJSONObject(i).getJSONArray("flights").getJSONObject(0).getJSONArray("segmentList");
                String ticketNo = segmentListArray.getJSONObject(0).getString("ticketNo").substring(3);
                map.put(String.valueOf(i), passengerName + "##" + cerNo + "##" + ticketNo + "##" + bankTradeNo);
            }
        } catch (JSONException e) {
            logger.error("解析订单详情json错误：" + back);
        }
        return map;
    }

    //发送订单状态信息
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
            logger.error("推送" + status + "情况异常");
        }
        return null;
    }

    //取消出票
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
     * 推送创单情况 String result = request.getParameter("result"); //是否创单成功
     * message = request.getParameter("message"); //失败消息
     * request.getParameter("price"); //采购总金额
     * request.getParameter("childrenUser");//子帐号
     * request.getParameter("newOrderId"); //创建订单成功后的官网订单号
     * request.getParameter("orderId"); //原订单主键
     * request.getParameter("isPassuccess"); //是否支付成功
     * request.getParameter("isPassenge"); //是否票号回填
     * request.getParameterValues("passengeMessage"); // 获取票号回填到系统
     * 格式为:姓名##生份证##票号##银行订单号
     * request.getParameter("payTransactionid"); //获取票号回填的交易号
     * payStatus = request.getParameter("payStatus"); //获取支付方式
     * = request.getParameter("isSuccess"); //是否完结
     * request.getParameter("isautoB2C"); //是否自动出票
     * request.getParameter("ifUsedCoupon"); //是否使用红包
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
            buffer.append("<param name='result'>" + result + "</param>"); //error ,  success(创建成功)
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
            //初始化SSL连接
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
        //代理云
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];  //代理IP地址
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //代理IP端口
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(dailiyunProxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //代理云账号密码
        String proxyUser = "feeyeapp";
        String proxyPass = "feeye789";
        //TODO 从配置文件读取
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

        //json转换
        JSONObject jsonObject = JSON.parseObject(orderJson);
        OrderJson orderJsonBean = jsonObject.toJavaObject(OrderJson.class);

//        //单独测试查航班
        for (int i = 0; i < 10; i++) {
            String queryFlightsBack = service.queryFlights(httpclient, defaultRequestConfig, orderJsonBean, "", context);
        }


//        //测试支付重定向
//        String orderId = "791098";
//        String orderNo = "201904081021480951567";
//        String loginId = "FU1368269063219032823394773";
//        CloseableHttpClient client = HttpClients.createDefault();
//        RequestConfig config =  null;
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        String cookie ="JSESSIONID=A6C1203C725A431A8BCBE66DAD124480.d2";
//
//        service.uniPay(client, config, cookieStore, orderId, orderNo, loginId, "");

//        //测试获取票号
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
////                            ticketnos[0], "易宝支付", ticketnos[1], "false", "true", billNo, requestType);
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
