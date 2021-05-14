package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.feeye.bean.CabinInfo;
import com.feeye.bean.FlightInfo;
import com.feeye.bean.Orderinfo;
import com.feeye.bean.Passergeninfo;
import com.feeye.util.Api;
import com.feeye.util.BtoaEncode;
import com.feeye.util.FingerPrintUtil;
import com.feeye.util.PropertiesUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
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
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import java.awt.image.BufferedImage;
import java.io.*;
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
public class PNNewCreateOrderService {
    private static final Logger logger = LoggerFactory.getLogger(PNNewCreateOrderService.class);
    private static final int timeout = 15000;
    String dailiyunAccount = "feeyeapp:feeye789";
    //登录请求参数
    String csrfToken = "";

    public static void main(String[] args) {
        String orderJson = "{\r" +
                "  \"account\": \"13037284859_1437015921.x\",\r" +
                "  \"airline\": \"PN\",\r" +
                "  \"bigpnr\": \"MKR4EV\",\r" +
                "  \"childrenMobile\": \"\",\r" +
                "  \"code\": \"\",\r" +
                "  \"codePassword\": \"\",\r" +
                "  \"creditNo\": \"WT138025_B2A\",\r" +
                "  \"cvv\": \"fzy123...\",\r" +
                "  \"drawerType\": \"singleOfficial\",\r" +
                "  \"email\": \"\",\r" +
                "  \"flights\": [\r" +
                "    {\r" +
                "      \"airline\": \"PN\",\r" +
                "      \"arrival\": \"SZX\",\r" +
                "      \"cabin\": \"V\",\r" +
                "      \"departure\": \"CKG\",\r" +
                "      \"departureDate\": \"2021-04-20\",\r" +
                "      \"departureTime\": \"23:45:00\",\r" +
                "      \"fType\": \"go\",\r" +
                "      \"flightNo\": \"PN6229\",\r" +
                "      \"price\": \"5000\"\r" +
                "    }\r" +
                "  ],\r" +
                "  \"id\": \"68158812\",\r" +
                "  \"ifUsedCoupon\": false,\r" +
                "  \"isOutticket\": \"true\",\r" +
                "  \"linkMan\": \"侯平辉\",\r" +
                "  \"mainusername\": \"fzybian\",\r" +
                "  \"matchCabin\": false,\r" +
                "  \"mobile\": \"18530203144\",\r" +
                "  \"orderNo\": \"OJHHFFU1F10F03309859\",\r" +
                "  \"orderTime\": \"2021-03-29 15:53:22\",\r" +
                "  \"otheraccount\": \"112069_/JfzhBV4r5IXtkhj7zBfxJblWP+G5Nn5\",\r" +
                "  \"passengers\": [\r" +
                "    {\r" +
                "      \"birthday\": \"1990-09-10\",\r" +
                "      \"id\": \"85936079\",\r" +
                "      \"idcard\": \"441900199009103536\",\r" +
                "      \"passengerCardType\": \"身份证\",\r" +
                "      \"passengerName\": \"詹嘉伟\",\r" +
                "      \"passengerSex\": \"男\",\r" +
                "      \"passengerType\": \"成人\"\r" +
                "    },\r" +
                "    {\r" +
                "      \"birthday\": \"1985-10-29\",\r" +
                "      \"id\": \"85936078\",\r" +
                "      \"idcard\": \"441900198510293551\",\r" +
                "      \"passengerCardType\": \"身份证\",\r" +
                "      \"passengerName\": \"林仲才\",\r" +
                "      \"passengerSex\": \"男\",\r" +
                "      \"passengerType\": \"成人\"\r" +
                "    }\r" +
                "  ],\r" +
                "  \"payType\": \"yfk\",\r" +
                "  \"platType\": \"tongcheng\",\r" +
                "  \"qiangpiao\": \"2\",\r" +
                "  \"username\": \"fzybian\"\r" +
                "}";
        PNNewCreateOrderService pnNewCreateOrderService = new PNNewCreateOrderService();
        int i = 0;
        while (i < 3) {
            pnNewCreateOrderService.StartCreateOrder(orderJson,0,0);
        }

    }

    @Test
    public void d() {
        String reallyMsg = "{\"cabinClass\":\"ANY\",\"currencyCode\":\"CNY\",\"bounds\":[{\"departureDate\":\"2021-04-11\",\"destination\":{\"code\":\"SZX\",\"context\":\"IATA\"},\"origin\":{\"code\":\"CKG\",\"context\":\"IATA\"}}],\"searchType\":\"ONEWAY\",\"passengerCounts\":[{\"count\":1,\"passengerType\":\"ADT\"}]}";
        JSONObject reallyObj = JSON.parseObject(reallyMsg);
        System.out.println(JSON.toJSONString(reallyObj, SerializerFeature.MapSortField));
    }
    private String StartCreateOrder(String orderJson, int retryCount, int requestType) {
        long nowDateTime = new Date().getTime(); //获取当前时间，后面步骤要根据该时间点做超时处理
        String createOrderBack = "";
        String cookie = "";
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
        BasicScheme proxyAuth = new BasicScheme();
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            logger.error("error", e1);
        }
        String ip_port = DailiyunService.getRandomDailiIp(500);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        HttpHost dailiyunProxy = new HttpHost(proxyIp , proxyPort , "http");
        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(dailiyunProxy , proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        String[] dlyAccountInfo = dailiyunAccount.split(":");
        credsProvider.setCredentials(new AuthScope(dailiyunProxy) , new UsernamePasswordCredentials(dlyAccountInfo[0] , dlyAccountInfo[1]));
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setProxy(dailiyunProxy)
                .setStaleConnectionCheckEnabled(true)
                .build();
        HttpClientContext context = HttpClientContext.create();
        HttpHost target = new HttpHost("new.westair.cn",443,"https");
        context.setAuthCache( authCache );
        context.setTargetHost( target );
        context.setRequestConfig( defaultRequestConfig );
        context.setCredentialsProvider( credsProvider );
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoKeepAlive(false).setSoLinger(1)
                .setSoReuseAddress(true)
                .setSoTimeout(timeout)
                .setTcpNoDelay(true).build();
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslsf )  // 用来配置支持的协议
                .build();
        // 共享连接池
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                // .setDefaultCookieStore(cookieStore)
                .setDefaultSocketConfig(socketConfig)
                .setDefaultCredentialsProvider(credsProvider)
                .disableRedirectHandling()
                .build();

        CloseableHttpResponse response = null;

        try {
            //json转换
            JSONObject jsonObject = JSON.parseObject(orderJson);
            Orderinfo orderJsonBean = jsonObject.toJavaObject(Orderinfo.class);

            String order_id = orderJsonBean.getId();
            String childrenUser = orderJsonBean.getUsername();
            String billNo = "";
            String newOrderNo = "";
            try {
                billNo = orderJsonBean.getBillNo();
            } catch (Exception e) {
                logger.error("未获取到billNo");
            }

            /*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "已取消出票");
                sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "已取消出票");
                return "取消出票";
            }*/

            //查询航班
//            sendOrderStatus(childrenUser, order_id, "GS查询航班");
            /*String queryFlightsBack = "";
            long start = System.currentTimeMillis();
            for (int i = 0; i < 3; i++) {
                queryFlightsBack = queryFlightsNew(httpclient, defaultRequestConfig, "", orderJsonBean, "", 0, FingerPrintUtil.getDesc(), 0);
                if (queryFlightsBack.contains("retry") && retryCount < 2) {
                    retryCount++;
                    System.out.println("查航班封IP,换IP重试");
                    logger.info(order_id + " 查航班封IP,换IP重试");
                    return StartCreateOrder(orderJson, retryCount, 0);
                }
                if (queryFlightsBack.contains("ERROR")) {
//                    sendOrderStatus(childrenUser, order_id, SearchFlightBack);
                    sendCreateOrderInfo("error", "已取消出票" + queryFlightsBack, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return queryFlightsBack;
                }
                break;
            }
            System.out.println("查询航班耗时:" + (System.currentTimeMillis() - start) / 1000);

            //解析航班
            String airItineraryPriceId = parseFlights(queryFlightsBack, orderJsonBean);
            if (StringUtils.isEmpty(airItineraryPriceId)) {
                return "ERROR:解析航班错误";
            }*/

            String airItineraryPriceId = "123";

            //登录
            for (int i = 0; i < 10; i++) {
                cookie = login(defaultRequestConfig,orderJsonBean);
                /*if (StringUtils.isEmpty(cookie)) {
                    continue;
                }
                if (cookie.contains("验证码有误") || cookie.contains("图片验证码不正确") || cookie.contains("图片验证码不能为空")) {
                    logger.info(order_id + cookie);
                    if (retryCount > 3) {
                        retryCount++;
                        return StartCreateOrder(orderJson, 0, 0);
                    }
                }
                if (cookie.contains("ERROR")) {
//                    sendOrderStatus(childrenUser, order_id, cookie);
                    sendCreateOrderInfo("error", "登录失败" + cookie, "", childrenUser, "", order_id, "", "",
                            null, "", "", "false", "true", billNo, requestType);
                    return cookie;
                }
                break;*/
            }

/*            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
                sendOrderStatus(childrenUser, order_id, "已取消出票");
                sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                logger.info(order_id + "已取消出票");
                return "取消出票";
            }*/
            // String orderNo2 = "202104021610288525";
            // orderDetail(defaultRequestConfig,cookie,orderNo2,orderJson);
          /*  String flightId = queryFlightsNew(defaultRequestConfig,cookie,orderJsonBean);
            resultSets(defaultRequestConfig,cookie,flightId,orderJsonBean);
            String bookid = getBookingId(defaultRequestConfig,cookie,orderJsonBean);
            String productID = getProductId(defaultRequestConfig,cookie,flightId,bookid,orderJsonBean);*/
            /*if (StringUtils.isNotEmpty(productID) && productID.contains("重新登录")) {
                cookie = login(defaultRequestConfig,orderJsonBean);
                productID = getProductId(defaultRequestConfig,cookie,flightId,bookid,orderJsonBean);
            }*/
            // addCustomers(defaultRequestConfig,cookie,bookid,orderJson);
            // addContact(defaultRequestConfig,cookie,bookid,orderJson);
            // reservation(defaultRequestConfig,cookie,bookid,orderJson);

            //计算税费
            String shoppingCartId =  "";
                    // airTaxFeeCalculate(httpclient, defaultRequestConfig, cookie, airItineraryPriceId);

            //获取购物车
            shoppingCartId = getShoppingCart(httpclient, defaultRequestConfig, cookie, shoppingCartId);

            //添加乘客和联系人信息
            shoppingCartId = createTravelers(httpclient, defaultRequestConfig, cookie, shoppingCartId, orderJsonBean);

            //创单
            createOrderBack = createReservation(httpclient, defaultRequestConfig, cookie, orderJsonBean, shoppingCartId);
            JSONObject createOrderObj = JSON.parseObject(createOrderBack);
            String orderNo = createOrderObj.getJSONObject("data").getJSONObject("reservation").getString("code"); //官网订单号
            String totalFare = createOrderObj.getJSONObject("data").getJSONObject("reservation").getString("totalFare"); //支付总价格
            logger.info(order_id + "创单的订单号:" + orderNo + "支付总价格:" + totalFare);

            //支付跳转
            if (StringUtils.isNotEmpty(orderNo) && StringUtils.isNotEmpty(totalFare)) {
                checkOut(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderNo);
                String payUrl = payOrder(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderNo, totalFare);
                logger.info(order_id + "易宝支付地址:" + payUrl);
                //TODO调用易宝支付接口

                //获取订单详情，取票号信息
                reservationRetrieve(httpclient, defaultRequestConfig, cookie, orderJsonBean, orderNo);

            } else {
                sendOrderStatus(childrenUser, order_id, "创单失败");
                sendCreateOrderInfo("error", "创单失败", "", childrenUser, "", order_id, "", "",
                        null, "", "", "false", "true", billNo, requestType);
                return "ERROR:创单失败";
            }

            try {
                newOrderNo = orderJsonBean.getNewOrderNo();
            } catch (Exception e) {
                logger.error("未获取到newOrderNo");
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

    public static boolean GenerateImage(String imgStr,String imageName) {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = imageName;//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    /**
     * 登录
     *
     * @param orderJson
     * @return cookie
     */
    private String login(RequestConfig config,Orderinfo orderJson) {
        CloseableHttpResponse response = null;
        InputStream is = null;
        String cookie = "";
        String order_id = orderJson.getId();
        String childrenUser = orderJson.getUsername();
        String username = orderJson.getOtheraccount().split("_")[0];
        String password = orderJson.getOtheraccount().split("_")[1];
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("https://new.westair.cn/air/api/uc/v1/profile/profile/authenticaiton/captchas");
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            post.setHeader("Referer", "https://new.westair.cn/");
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");

            StringEntity entity = new StringEntity("{\"authenticationType\":\"THIRD_PARTY\"}", "UTF-8");
            post.setEntity(entity);
            response = httpclient.execute(post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("获取验证码图片返回:" + back);
            JSONObject backObj = JSON.parseObject(back);
            String question = backObj.getJSONObject("questionImage").getString("content");
            String captchaId = backObj.getString("captchaId");
            String fileUri = "C://testImg//" + System.currentTimeMillis() + ".jpg";
            GenerateImage(question,fileUri);
            username = orderJson.getOtheraccount().split("_")[0];
            password = orderJson.getOtheraccount().split("_")[1];
            /*String question_vrtifycode = Api.getValidCode(username , password , "40400" , fileUri);
            logger.info("调用斐斐打码返回：" +question_vrtifycode);
            if(question_vrtifycode.contains("未知错误")){
                for (int i = 0; i < 3; i++) {
                    question_vrtifycode = Api.getValidCode(username , password , "40200",fileUri);
                    if(!question_vrtifycode.contains("未知错误")){
                        break;
                    }
                    Thread.sleep(2000);
                }
            }*/
            System.out.print("输入验证码:");
            Scanner scan = new Scanner(System.in);
            String question_vrtifycode = scan.nextLine();
            String value = PropertiesUtils.getPropertiesValue("question", question_vrtifycode);
            File file = new File(fileUri);
            if (file.exists()) {
                file.delete();
            }
            JSONArray answerImageList = backObj.getJSONArray("answerImageList");
            List<String> resultList = new ArrayList<>();
            for (int i = 0 ; i < answerImageList.size(); i++) {
                JSONObject answerObj = answerImageList.getJSONObject(i);
                String id = answerObj.getString("id");
                String answerContent = answerObj.getString("content");
                String answerfileUri = "C://testImg//" + System.currentTimeMillis() + ".jpg";
                GenerateImage(answerContent,answerfileUri);
                String answer_vrtifycode = Api.getValidCode(username , password , "40200" , answerfileUri);
                System.out.println("打码结果:" + answer_vrtifycode);
                if (StringUtils.isNotEmpty(value) && value.contains(answer_vrtifycode)) {
                    resultList.add(id);
                }
                File answerfile = new File(answerfileUri);
                if (answerfile.exists()) {
                    answerfile.delete();
                }
            }
           // resultList.add("2");
           if (resultList.size() > 0) {
               post = new HttpPost("https://new.westair.cn/air/api/uc/v1/user/authentication");
               post.setConfig(config);
               Map<String, String> reqMap = new HashMap<>();
               String accountNumber = "13037284859";
               reqMap.put("accountNumber",accountNumber);
               reqMap.put("loginType","PHONE_NUMBER");
               String loginPwd = "1437015921.x";
               reqMap.put("password", BtoaEncode.botaEncodePassword(loginPwd));
               String reqParam = JSON.toJSONString(reqMap);
               System.out.println("登录的请求参数:" + reqParam);
               entity = new StringEntity(reqParam, "UTF-8");
               post.setEntity(entity);
               post.setHeader("Host", "new.westair.cn");
               post.setHeader("Connection", "keep-alive");
               post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
               post.setHeader("Accept-Language", "zh-CN");
               post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
               post.setHeader("Tenant", "PN");
               post.setHeader("Content-Type", "application/json");
               post.setHeader("Accept", "application/json");
               post.setHeader("username","mul141");
               Map<String,Object> captchaMap = new HashMap<>();
               captchaMap.put("id",captchaId);
               captchaMap.put("answers",resultList);
               Map<String, Object> mateMap = new HashMap<>();
               mateMap.put("captcha",captchaMap);
               String mateMsg = JSON.toJSONString(mateMap);
               System.out.println("登录的mate信息:" + mateMsg);
               post.setHeader("Mfa-Mate",mateMsg);
               post.setHeader("Sales-Channel", "IBE");
               post.setHeader("IP-Address", "1.1.1.1");
               post.setHeader("Origin", "https://new.westair.cn");
               post.setHeader("Sec-Fetch-Site", "same-origin");
               post.setHeader("Sec-Fetch-Mode", "cors");
               post.setHeader("Sec-Fetch-Dest", "empty");
               post.setHeader("Referer", "https://new.westair.cn/");
               post.setHeader("Accept-Encoding", "gzip, deflate, br");
               post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
               post.setHeader("Proxy-Connection", "keep-alive");
               response = httpclient.execute(post);
               back = EntityUtils.toString(response.getEntity(), "utf-8");
               System.out.println("登录返回:" + back);
               logger.info(order_id + "登录返回:" + back);
               if (StringUtils.isNotEmpty(back) && back.contains("token")) {
                   cookie = JSON.parseObject(back).getString("token");
               }
           } else {
               return "图片验证码不正确";
           }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return cookie;
    }


    //获取查询航班cookie新方法
    private String queryFlightsNew(RequestConfig config, String logintoken,Orderinfo orderJson) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        String order_id = orderJson.getId();
        String back = "";
        try {
            String origin = orderJson.getFlights().get(0).getDeparture();
            String destination = orderJson.getFlights().get(0).getArrival();
            String departureDate = orderJson.getFlights().get(0).getDepartureDate();
            destination = destination.replaceAll("SHA", "PVG");
            String year = departureDate.split("-")[0];
            String month = departureDate.split("-")[1];
            String day = departureDate.split("-")[2];
            String data = year.substring(2, 4) + month + day + "-100-0";
            httpclient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("https://new.westair.cn/air/api/v1/tRetailAPI/flight/resultSets");
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            // post.setHeader("Cookie", "user-token="+logintoken);
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/results/" + origin + "-" + destination + "-" + data;
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("cabinClass","ANY");
            reqMap.put("currencyCode","CNY");
            reqMap.put("searchType","ONEWAY");
            List<JSONObject> list = new ArrayList<>();
            String passengerCountMsg = "{\r" +
                    "      \"count\": 1,\r" +
                    "      \"passengerType\": \"ADT\"\r" +
                    "    }";
            list.add(JSON.parseObject(passengerCountMsg));
            reqMap.put("passengerCounts",list);
            List<Map<String, Object>> bounds = new ArrayList<>();
            Map<String, Object> boundsMap = new HashMap<>();
            boundsMap.put("departureDate",departureDate);
            Map<String, String> destinationMap = new HashMap<>();
            destinationMap.put("code",destination);
            destinationMap.put("context","IATA");
            Map<String, String> originMap = new HashMap<>();
            originMap.put("code",origin);
            originMap.put("context","IATA");
            boundsMap.put("destination",destinationMap);
            boundsMap.put("origin",originMap);
            bounds.add(boundsMap);
            reqMap.put("bounds",bounds);
            String flightIdParam = JSON.toJSONString(reqMap,SerializerFeature.MapSortField);
            System.out.println("航班ID请求参数:" + flightIdParam);
            StringEntity entity = new StringEntity(flightIdParam, "UTF-8");
            post.setEntity(entity);
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("获取航班Id信息返回:" + back);
            if (StringUtils.isNotEmpty(back) && back.contains("id")) {
                String flightId = JSON.parseObject(back).getString("id");
                return flightId;
                /*HttpGet get = new HttpGet("https://new.westair.cn/air/api/v1/tRetailAPI/flight/resultSets/" + flightId);
                get.setConfig(config);
                get.setHeader("Host", "new.westair.cn");
                get.setHeader("Connection", "keep-alive");
                get.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
                get.setHeader("Accept-Language", "zh-CN");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
                get.setHeader("Tenant", "PN");
                get.setHeader("Content-Type", "application/json");
                get.setHeader("Accept", "application/json");
                get.setHeader("Sales-Channel", "IBE");
                get.setHeader("IP-Address", "1.1.1.1");
                get.setHeader("Origin", "https://new.westair.cn");
                get.setHeader("Sec-Fetch-Site", "same-origin");
                get.setHeader("Sec-Fetch-Mode", "cors");
                get.setHeader("Sec-Fetch-Dest", "empty");
                get.setHeader("Referer", referer);
                get.setHeader("Accept-Encoding", "gzip, deflate, br");
                get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
                get.setHeader("Proxy-Connection", "keep-alive");
                response = httpclient.execute(get);
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println("获取航班信息返回:" + back);
                String priceMsg = parseFlightMsg("PN6229","V1","BASIC","862",back);
                if (StringUtils.isNotEmpty(priceMsg) && priceMsg.contains("SUCCESS")) {
                    back = flightId;
                }*/
            }
         } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
    }

    private String parseFlightMsg(String flightNo,String cabin,String priceType,String price,String flightMsg) {
        String back = "";
        try {
            JSONObject data = JSON.parseObject(flightMsg);
            JSONArray flightNoList = data.getJSONArray("flightSegments");
            JSONArray flightOptions = data.getJSONArray("flightOptions");
            for(int i = 0; i < flightNoList.size(); ++i) {
                JSONObject flightData = flightNoList.getJSONObject(i);
                Integer id = flightData.getInteger("id");
                JSONObject operating = flightData.getJSONObject("operatingAirlineInfo");
                String flightNoMsg = operating.getString("airlineCode") + operating.getString("flightNumber");
                if (!flightNo.equals(flightNoMsg)) {
                    continue;
                }
                for(int j = 0; j < flightOptions.size(); ++j) {
                    JSONObject flightOp = flightOptions.getJSONObject(j);
                    Integer id2 = flightOp.getInteger("id");
                    if (id == id2) {
                        JSONArray prices = flightOp.getJSONArray("prices");

                        for(int k = 0; k < prices.size(); ++k) {
                            JSONObject cabinObj = prices.getJSONObject(k);
                            JSONArray fareInfos = cabinObj.getJSONArray("fareInfos");
                            if (fareInfos != null && fareInfos.size() >= 1) {
                                JSONObject fareInfo = fareInfos.getJSONObject(0);
                                String cabinCode = fareInfo.getString("rbd");
                                String fareFamilyCode = cabinObj.getString("fareFamilyCode");
                                if (cabin.equals(cabinCode)) {
                                    if (priceType.equals(fareFamilyCode)) {
                                        int gwPrice = cabinObj.getJSONObject("total").getInteger("amount") - 50;
                                        if (Integer.valueOf(price).intValue() < gwPrice) {
                                            return back = "error:官网变价,官网价格：" + gwPrice;
                                        } else {
                                            String quan = fareInfo.getString("rbdQuantity");
                                            if ("0".equals(quan)) {
                                                return "error:此航班无座位数";
                                            } else {
                                                back = "SUCCESS#" + gwPrice;
                                                return back;
                                            }
                                        }
                                    }
                                }


                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取价格error:",e);
        }
        return back;
    }

    @Test
    public void getBookingIdtest() {
        String back = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost("https://new.westair.cn/air/api/v1/tRetailAPI/bookings");
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/results/CKG-SZX-210411-100-0";
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            String logintoken = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyVHlwZSI6IlBIT05FX05VTUJFUiIsImFjY291bnRUeXBlIjoiMSIsImNhY2hlS2V5IjoiYWY5MzM5MDktZTM4MS00OTI2LTlmMWUtMTUwMjZlYzQwODk3Iiwic3ViIjoiemhhbmd4aWFvZmVuZyIsImNyZWF0ZWRUaW1lIjoxNjE3MTU2MTA3MTc3LCJleHAiOjE2MTcyNDI1MDd9.-Ozap8-aPK9iIk3lBjsM_mklpUfZKZ8xyj-C5xf3CUcKJE7WE3T17fELjobldn-Bjydk6fV3zGc-x9lQZTyxYg";
            post.setHeader("Cookie","user-token=" + logintoken);
            /*post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");*/
            CloseableHttpClient httpclient = HttpClientBuilder.create().build();
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("获取订单Id信息返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("bookingId")) {
                    back = JSON.parseObject(back).getString("bookingId");
                } else if (back.contains("apiErrors")) {
                    back = JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
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
    }
    private List<FlightInfo> parseFlight(String flightNo,String flightMsg) {
        List<FlightInfo> flightInfos = new ArrayList<>();
        try {
            JSONObject data = JSON.parseObject(flightMsg);
            JSONArray flightNoList = data.getJSONArray("flightSegments");
            JSONArray flightOptions = data.getJSONArray("flightOptions");
            for(int i = 0; i < flightNoList.size(); ++i) {
                JSONObject flightData = flightNoList.getJSONObject(i);
                Integer id = flightData.getInteger("id");
                new FlightInfo();
                FlightInfo flightInfo = new FlightInfo();
                JSONObject departureData = flightData.getJSONObject("departure");
                flightInfo.setDeparutre(departureData.getString("iataCode"));
                JSONObject arrivalData = flightData.getJSONObject("arrival");
                flightInfo.setArrival(arrivalData.getString("iataCode"));
                flightInfo.setWebType("PN");
                String deptime = departureData.getString("dateTime").substring(11, 16);
                String arrtime = arrivalData.getString("dateTime").substring(11, 16);
                flightInfo.setDeparutreTime(deptime);
                flightInfo.setArriveTime(arrtime);
                JSONObject operating = flightData.getJSONObject("operatingAirlineInfo");
                String flightNoMsg = operating.getString("airlineCode") + operating.getString("flightNumber");
                if (!flightNo.equals(flightNoMsg)) {
                    continue;
                }
                flightInfo.setFlightNo(flightNo);
                flightInfo.setPlaneType(flightData.getString("equipmentType"));
                List<CabinInfo> cabinInfos = new ArrayList();
                flightInfo.setCabins(cabinInfos);

                for(int j = 0; j < flightOptions.size(); ++j) {
                    JSONObject flightOp = flightOptions.getJSONObject(j);
                    Integer id2 = flightOp.getInteger("id");
                    if (id == id2) {
                        JSONArray prices = flightOp.getJSONArray("prices");

                        for(int k = 0; k < prices.size(); ++k) {
                            JSONObject cabin = prices.getJSONObject(k);
                            JSONArray fareInfos = cabin.getJSONArray("fareInfos");
                            if (fareInfos != null && fareInfos.size() >= 1) {
                                JSONObject fareInfo = fareInfos.getJSONObject(0);
                                CabinInfo cabinInfo = new CabinInfo();
                                cabinInfo.setCabinCode(fareInfo.getString("rbd"));
                                String fareFamilyCode = cabin.getString("fareFamilyCode");
                                cabinInfo.setPriceType(fareFamilyCode);
                                cabinInfo.setPrice(String.valueOf(cabin.getJSONObject("total").getInteger("amount") - 50));
                                String quan = fareInfo.getString("rbdQuantity");
                                if ("A".equals(quan)) {
                                    cabinInfo.setLastSeat("10");
                                } else {
                                    cabinInfo.setLastSeat(quan);
                                }

                                if (!cabinInfo.getLastSeat().equalsIgnoreCase("0")) {
                                    cabinInfos.add(cabinInfo);
                                }
                            }
                        }
                    }
                }

                flightInfos.add(flightInfo);
            }
        } catch (Exception e) {
            logger.error("获取价格error:",e);
        }
        return flightInfos;
    }
    private String getBookingId( RequestConfig config,String logintoken,Orderinfo orderJson) {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient =null;
        try {
            String origin = orderJson.getFlights().get(0).getDeparture();
            String destination = orderJson.getFlights().get(0).getArrival();
            String departureDate = orderJson.getFlights().get(0).getDepartureDate();
            destination = destination.replaceAll("SHA", "PVG");
            String year = departureDate.split("-")[0];
            String month = departureDate.split("-")[1];
            String day = departureDate.split("-")[2];
            String data = year.substring(2, 4) + month + day + "-100-0";
            HttpPost post = new HttpPost("https://new.westair.cn/air/api/v1/tRetailAPI/bookings");
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/results/" + origin + "-" + destination + "-" + data;
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Cookie","user-token=" + logintoken);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
           httpclient = HttpClientBuilder.create().build();
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("获取订单Id信息返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("bookingId")) {
                    back = JSON.parseObject(back).getString("bookingId");
                } else if (back.contains("apiErrors")) {
                    back = JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
    }
    private String resultSets(RequestConfig config,String logintoken,String flightId,Orderinfo orderJson) {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            String origin = orderJson.getFlights().get(0).getDeparture();
            String destination = orderJson.getFlights().get(0).getArrival();
            String departureDate = orderJson.getFlights().get(0).getDepartureDate();
            destination = destination.replaceAll("SHA", "PVG");
            String year = departureDate.split("-")[0];
            String month = departureDate.split("-")[1];
            String day = departureDate.split("-")[2];
            String data = year.substring(2, 4) + month + day + "-100-0";
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/flight/resultSets/" + flightId;
            HttpGet post = new HttpGet(url);
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/results/" + origin + "-" + destination + "-" + data;
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Cache-Control", "no-cache");
            post.setHeader("Pragma", "no-cache");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            String cookie = "user-token=" + logintoken;
            post.setHeader("Cookie",cookie);
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("航班点击信息返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("productId")) {
                    back = JSON.parseObject(back).getString("productId");
                } else if (back.contains("apiErrors")) {
                    back = JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
    }
    private String getProductId(RequestConfig config,String logintoken,String flightId,String bookingId,Orderinfo orderJson) {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            String origin = orderJson.getFlights().get(0).getDeparture();
            String destination = orderJson.getFlights().get(0).getArrival();
            String departureDate = orderJson.getFlights().get(0).getDepartureDate();
            destination = destination.replaceAll("SHA", "PVG");
            String year = departureDate.split("-")[0];
            String month = departureDate.split("-")[1];
            String day = departureDate.split("-")[2];
            String data = year.substring(2, 4) + month + day + "-100-0";
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/bookings/" + bookingId + "/products/flight";
            HttpPost post = new HttpPost(url);
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/results/" + origin + "-" + destination + "-" + data;
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            String cookie = "user-token=" + logintoken;
            post.setHeader("Cookie",cookie);
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("priceId",1);
            reqMap.put("resultId",1);
            reqMap.put("resultSetId",flightId);
            String reqParam = JSON.toJSONString(reqMap,SerializerFeature.MapSortField);
            System.out.println("获取产品ID参数信息:" + reqParam);
            StringEntity entity = new StringEntity(reqParam, "UTF-8");
            post.setEntity(entity);
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("获取产品ID信息返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("productId")) {
                    back = JSON.parseObject(back).getString("productId");
                } else if (back.contains("apiErrors")) {
                    back = JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
    }
    @Test
    public void sysMsg() {
        String a = // "{\"name\":{\"surname\":\"张\",\"firstName\":\"晓峰\"},\"phones\":[{\"number\":\"13037284859\",\"phoneType\":\"MOBILE\",\"areaCode\":null}],\"email\":\"1437015921@qq.com\"}";
                // "{\"customers\":[{\"name\":{\"surname\":\"张\",\"firstName\":\"晓峰\"},\"dateOfBirth\":\"1995-08-13\",\"passengerType\":\"ADT\",\"products\":[1000],\"travelDocument\":{\"docId\":\"362428199508132112\",\"docType\":\"PRC_IDENTITY_CARD\"},\"phone\":{\"number\":\"13037284859\",\"phoneType\":\"MOBILE\",\"areaCode\":null}}]}";
                "{\"url\":\"https://easypay.travelsky.com.cn/easypay/airlinepay.servlet?OrderAmount=912.0&AppType=B2C&BankId=YEEPAY&BillNo=2021040164351864&Ext1=&Ext2=&Lan=CN&Msg=SuperPNR_ID%3D202104011421371327&OrderCurtype=CNY&OrderNo=202104011422080723&OrderType=1|0|&OrgId=PNB2C&OrderDate=20210401&OrderTime=142140&ordername=Airlines+e-ticket&Orderinfo=&usrid=zhangxiaofeng&username=&gateid=&Version=1.0&ReturnId=id_pay_pn_pro&SIGNATURE=f375e53e00cd268d803082e2bacdb571\",\"paymentIds\":[5],\"postUrl\":\"https://easypay.travelsky.com.cn/easypay/airlinepay.servlet\",\"postParameters\":[{\"key\":\"OrderAmount\",\"value\":\"912.0\"},{\"key\":\"AppType\",\"value\":\"B2C\"},{\"key\":\"BankId\",\"value\":\"YEEPAY\"},{\"key\":\"BillNo\",\"value\":\"2021040164351864\"},{\"key\":\"Ext1\",\"value\":\"\"},{\"key\":\"Ext2\",\"value\":\"\"},{\"key\":\"Lan\",\"value\":\"CN\"},{\"key\":\"Msg\",\"value\":\"SuperPNR_ID=202104011421371327\"},{\"key\":\"OrderCurtype\",\"value\":\"CNY\"},{\"key\":\"OrderNo\",\"value\":\"202104011422080723\"},{\"key\":\"OrderType\",\"value\":\"1|0|\"},{\"key\":\"OrgId\",\"value\":\"PNB2C\"},{\"key\":\"OrderDate\",\"value\":\"20210401\"},{\"key\":\"OrderTime\",\"value\":\"142140\"},{\"key\":\"ordername\",\"value\":\"Airlines e-ticket\"},{\"key\":\"Orderinfo\",\"value\":\"\"},{\"key\":\"usrid\",\"value\":\"zhangxiaofeng\"},{\"key\":\"username\",\"value\":\"\"},{\"key\":\"gateid\",\"value\":\"\"},{\"key\":\"Version\",\"value\":\"1.0\"},{\"key\":\"ReturnId\",\"value\":\"id_pay_pn_pro\"},{\"key\":\"SIGNATURE\",\"value\":\"f375e53e00cd268d803082e2bacdb571\"}]}";
        JSONObject obj = JSON.parseObject(a);
        System.out.println(JSON.toJSONString(obj,SerializerFeature.MapSortField));
    }
    private String addCustomers(RequestConfig config,String logintoken,String bookingId,String orderJson) {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/bookings/" + bookingId + "/customers";
            HttpPost post = new HttpPost(url);
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/psginfo";
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            String cookie = "user-token=" + logintoken;
            post.setHeader("Cookie",cookie);
            Map<String, Object> reqMap = new HashMap<>();
            List<Map<String, Object>> customers = new ArrayList<>();
            Map<String, Object> customer = new HashMap<>();
            String birthdDay = "1995-08-13";
            customer.put("dateOfBirth",birthdDay);
            Map<String, String> nameMap = new HashMap<>();
            String name = "张三";
            nameMap.put("firstName",name.substring(1));
            nameMap.put("surname",name.substring(0,1));
            customer.put("name",nameMap);
            customer.put("passengerType","ADT");
            Map<String, String> phoneMap = new HashMap<>();
            String number = "13037284859";
            phoneMap.put("number",number);
            phoneMap.put("phoneType","MOBILE");
            customer.put("phone",phoneMap);
            List<Integer> products = new ArrayList<>();
            products.add(1000);
            customer.put("products",products);
            Map<String, String> travelDocumentMap = new HashMap<>();
            String cardId = "362428199508132112";
            travelDocumentMap.put("docId",cardId);
            travelDocumentMap.put("docType","PRC_IDENTITY_CARD");
            customer.put("travelDocument",travelDocumentMap);
            customers.add(customer);
            reqMap.put("customers",customers);
            String reqParam = JSON.toJSONString(reqMap,SerializerFeature.MapSortField);
            System.out.println("添加乘客参数信息:" + reqParam);
            StringEntity entity = new StringEntity(reqParam, "UTF-8");
            post.setEntity(entity);
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("添加乘客返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("customerIds")) {
                    back = "success";
                } else if (back.contains("apiErrors")) {
                    back = "error:" + JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
    }

    private String addContact(RequestConfig config,String logintoken,String bookingId,String orderJson) {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/bookings/" + bookingId + "/contact";
            HttpPost post = new HttpPost(url);
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/psginfo";
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            String cookie = "user-token=" + logintoken;
            post.setHeader("Cookie",cookie);
            Map<String, Object> reqMap = new HashMap<>();
            String email = "1437015921@qq.com";
            reqMap.put("email",email);
            String contactName = "张晓峰";
            Map<String, String> nameMap = new HashMap<>();
            nameMap.put("firstName",contactName.substring(1));
            nameMap.put("surname",contactName.substring(0,1));
            reqMap.put("name",nameMap);
            List<Map<String, String>> phones = new ArrayList<>();
            Map<String, String> phoneMap = new HashMap<>();
            String conntactNumber = "13037284859";
            phoneMap.put("number",conntactNumber);
            phoneMap.put("phoneType","MOBILE");
            phones.add(phoneMap);
            reqMap.put("phones",phones);
            String reqParam = JSON.toJSONString(reqMap,SerializerFeature.MapSortField);
            System.out.println("添加联系人参数信息:" + reqParam);
            StringEntity entity = new StringEntity(reqParam, "UTF-8");
            post.setEntity(entity);
            response = httpclient.execute(post);
            /*back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("添加联系人信息返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("customerIds")) {
                    back = "success";
                } else if (back.contains("apiErrors")) {
                    back = "error:" + JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }*/
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
    }

    private String reservation(RequestConfig config,String logintoken,String bookingId,String orderJson) {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/bookings/" + bookingId + "/reservation";
            HttpPost post = new HttpPost(url);
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/psginfo";
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            String cookie = "user-token=" + logintoken;
            post.setHeader("Cookie",cookie);
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("添加联系人信息返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("bookingId")) {
                    return back;
                } else if (back.contains("apiErrors")) {
                    back = "error:" + JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
    }

    private String orderDetail(RequestConfig config,String logintoken,String orderNo,String orderJson) {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/bookingHistory/retrieval";
            HttpPost post = new HttpPost(url);
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/orderDetail";
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            String cookie = "user-token=" + logintoken;
            post.setHeader("Cookie",cookie);
            Map<String, String> reqMap = new HashMap<>();
            reqMap.put("bookingReference",orderNo);
            String retrievalParam = JSON.toJSONString(reqMap);
            System.out.println("查询详情获取Booid参数:" + retrievalParam);
            StringEntity entity = new StringEntity(retrievalParam, "UTF-8");
            post.setEntity(entity);
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("查询详情获取Booid结果:" + back);
            String bookingId = JSON.parseObject(back).getString("bookingId");
            url = "https://new.westair.cn/air/api/v1/tRetailAPI/bookings/" + bookingId;
            HttpGet get = new HttpGet(url);
            get.setConfig(config);
            get.setHeader("Host", "new.westair.cn");
            get.setHeader("Connection", "keep-alive");
            get.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            get.setHeader("Accept-Language", "zh-CN");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            get.setHeader("Tenant", "PN");
            get.setHeader("Accept", "application/json");
            get.setHeader("Sales-Channel", "IBE");
            get.setHeader("IP-Address", "1.1.1.1");
            get.setHeader("Origin", "https://new.westair.cn");
            get.setHeader("Sec-Fetch-Site", "same-origin");
            get.setHeader("Sec-Fetch-Mode", "cors");
            get.setHeader("Sec-Fetch-Dest", "empty");
            get.setHeader("Referer", referer);
            get.setHeader("Accept-Encoding", "gzip, deflate, br");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            get.setHeader("Proxy-Connection", "keep-alive");
            get.setHeader("Cookie",cookie);
            response = httpclient.execute(get);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("查询订单信息返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("additionalBookingStatus")) {
                    return back;
                } else if (back.contains("apiErrors")) {
                    back = "error:" + JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
    }
    private String getTransactionId(RequestConfig config,String logintoken,String bookingId,String orderJson) {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/callback/transactions/EZPAY";
            HttpPost post = new HttpPost(url);
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-Type","application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/pay/" + bookingId;
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            String cookie = "user-token=" + logintoken;
            post.setHeader("Cookie",cookie);
            StringEntity entity = new StringEntity("{}", "UTF-8");
            post.setEntity(entity);
            response = httpclient.execute(post);
            String res = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("查询订单信息返回:" + res);
            if (StringUtils.isNotEmpty(res)) {
                if (res.contains("transactionId")) {
                    String transactionId = JSON.parseObject(res).getString("transactionId");
                    back = transactionId;
                } else if (res.contains("apiErrors")) {
                    back = "error:" + JSON.parseObject(res).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return back;
    }
    private Map<String, String> payInit(RequestConfig config,String logintoken,String bookingId,String orderJson) {
        Map<String, String> backMap = new HashMap<>();
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/ezpay/authoriseRedirect";
            HttpPost post = new HttpPost(url);
            post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-Type","application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/pay/" + bookingId;
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            String cookie = "user-token=" + logintoken;
            post.setHeader("Cookie",cookie);
            Map<String, String> reqMap = new HashMap<>();
            reqMap.put("bookingId",bookingId);
            reqMap.put("bankId","YEEPAY");
            String reqParam = JSON.toJSONString(reqMap);
            System.out.println("支付请求参数:" + reqParam);
            StringEntity entity = new StringEntity(reqParam, "UTF-8");
            post.setEntity(entity);
            response = httpclient.execute(post);
            String res = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("查询订单信息返回:" + res);
            if (StringUtils.isNotEmpty(res)) {
                if (res.contains("transactionId")) {
                    String transactionId = JSON.parseObject(res).getString("transactionId");
                    // back = transactionId;
                } else if (res.contains("apiErrors")) {
                    // back = "error:" + JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return backMap;
    }
    @Test
    public void orderDetailTest() {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            String bookingId = "71a9ba10-58b4-b31d-1354-545bbca3cdeb";
            String logintoken = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyVHlwZSI6IlBIT05FX05VTUJFUiIsImFjY291bnRUeXBlIjoiMSIsImNhY2hlS2V5IjoiNzRiM2I0ZWUtNTcwOC00YmE0LWE3MmQtZDYxYjk5YmViY2Q5Iiwic3ViIjoiemhhbmd4aWFvZmVuZyIsImNyZWF0ZWRUaW1lIjoxNjE3MjU1NzQ2MjIxLCJleHAiOjE2MTczNDIxNDZ9.gcFfHJYiDZUAFAg7vyjZC02t5MBRF9t7uRI4EB2gbZGxtJx6vIt5AKmBj0mVzAuXXaBjnXKgbO4LUbpliawI2g";
                    // "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyVHlwZSI6IlBIT05FX05VTUJFUiIsImFjY291bnRUeXBlIjoiMSIsImNhY2hlS2V5IjoiNzRiM2I0ZWUtNTcwOC00YmE0LWE3MmQtZDYxYjk5YmViY2Q5Iiwic3ViIjoiemhhbmd4aWFvZmVuZyIsImNyZWF0ZWRUaW1lIjoxNjE3MjU1NzQ2MjIxLCJleHAiOjE2MTczNDIxNDZ9.gcFfHJYiDZUAFAg7vyjZC02t5MBRF9t7uRI4EB2gbZGxtJx6vIt5AKmBj0mVzAuXXaBjnXKgbO4LUbpliawI2geyJhbGciOiJIUzUxMiJ9.eyJ1c2VyVHlwZSI6IlBIT05FX05VTUJFUiIsImFjY291bnRUeXBlIjoiMSIsImNhY2hlS2V5IjoiNzRiM2I0ZWUtNTcwOC00YmE0LWE3MmQtZDYxYjk5YmViY2Q5Iiwic3ViIjoiemhhbmd4aWFvZmVuZyIsImNyZWF0ZWRUaW1lIjoxNjE3MjU1NzQ2MjIxLCJleHAiOjE2MTczNDIxNDZ9.gcFfHJYiDZUAFAg7vyjZC02t5MBRF9t7uRI4EB2gbZGxtJx6vIt5AKmBj0mVzAuXXaBjnXKgbO4LUbpliawI2g";
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/bookings/" + bookingId;
            HttpGet post = new HttpGet(url);
            // post.setConfig(config);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/orderDetail";
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            String cookie = "user-token=" + logintoken;
            post.setHeader("Cookie",cookie);
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("获取订单信息返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("additionalBookingStatus")) {
                    // return back;
                } else if (back.contains("apiErrors")) {
                    back = "error:" + JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        // return back;
    }
    @Test
    public void getProductIdTest() {
        String back = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {


            String bookingId = "a8b4e34a-9ffb-4939-a5b1-eec917fbc4b8";
            String logintoken = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyVHlwZSI6IlBIT05FX05VTUJFUiIsImFjY291bnRUeXBlIjoiMSIsImNhY2hlS2V5IjoiOTFhZjliOWQtMGJhMi00MDVkLThlMDQtNGQyMGRmYzU4Y2VhIiwic3ViIjoiemhhbmd4aWFvZmVuZyIsImNyZWF0ZWRUaW1lIjoxNjE3MTcyNzE0OTA2LCJleHAiOjE2MTcyNTkxMTR9.pkn3WDEUZ3tdKhqtyBXRyaRwsxFcpKmIdrOBn3ziw7rMhg92VWwrJVqt11wRU_wV_qeJ0s9lc430JWA2NRLhIw";
            String flightId = "9393bc74-d08e-4414-9af8-97bed30c5914";
            httpclient = HttpClientBuilder.create().build();
            String url = "https://new.westair.cn/air/api/v1/tRetailAPI/bookings/" + bookingId + "/products/flight";
            HttpPost post = new HttpPost(url);
            post.setHeader("Host", "new.westair.cn");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            post.setHeader("Tenant", "PN");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Sales-Channel", "IBE");
            post.setHeader("IP-Address", "1.1.1.1");
            post.setHeader("Origin", "https://new.westair.cn");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Dest", "empty");
            String referer = "https://new.westair.cn/flights/results/CKG-SZX-210411-100-0";
            post.setHeader("Referer", referer);
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dailiyunAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            post.setHeader("Cookie","user-token=" + logintoken);
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("priceId",1);
            reqMap.put("resultId",1);
            reqMap.put("resultSetId",flightId);
            String reqParam = JSON.toJSONString(reqMap,SerializerFeature.MapSortField);
            System.out.println("获取产品ID参数信息:" + reqParam);
            StringEntity entity = new StringEntity(reqParam, "UTF-8");
            post.setEntity(entity);
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("获取产品ID信息返回:" + back);
            if (StringUtils.isNotEmpty(back)) {
                if (back.contains("productId")) {
                    back = JSON.parseObject(back).getString("productId");
                } else if (back.contains("apiErrors")) {
                    back = JSON.parseObject(back).getJSONArray("apiErrors").getJSONObject(0).getString("userMessage");
                }
            }
        }  catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient!=null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
    }
    /**
     * 获取购物车
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
            System.out.println("获取购物车返回:" + back);
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
     * 添加乘客和联系人信息
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param shoppingCartId 购物车id
     * @return
     */
    private String createTravelers(CloseableHttpClient httpclient, RequestConfig config, String cookie, String shoppingCartId, Orderinfo orderJson) {
        CloseableHttpResponse response = null;
        try {
            String departureDate = orderJson.getFlights().get(0).getDepartureDate();
            String linkMan = orderJson.getLinkMan(); //联系人
            String mobile = orderJson.getMobile(); //联系人电话

            List<Passergeninfo> passengers = orderJson.getPassengers();
            JSONObject paramObj = new JSONObject();
            JSONArray passengersArr = new JSONArray();
            JSONObject customerObj = new JSONObject();
            customerObj.put("firstName", linkMan.substring(1, linkMan.length()));
            customerObj.put("lastName", linkMan.substring(0, 1));
            customerObj.put("mobile", mobile);
            //TODO 从订单系统中获取联系人邮箱信息
            customerObj.put("email", "");
            int i = 0;
            for (Passergeninfo pb : orderJson.getPassengers()) {
                JSONObject passengersObj = new JSONObject();
                i++;
                if ("成人".equals(pb.getPassengerType())) {
                    passengersObj.put("type", "ADT");
                } else if ("儿童".equals(pb.getPassengerType())) {
                    passengersObj.put("type", "CNN");
                } else if ("婴儿".equals(pb.getPassengerType())) {
                    passengersObj.put("type", "INF");
                }
                passengersObj.put("addToCommon", false);
                String passengerName = pb.getPassengerName();
                if (passengerName.contains("/")) { //护照
                    String[] split = passengerName.split("/");
                    passengersObj.put("firstName", split[0]);
                    passengersObj.put("lastName", split[1]);
                } else {
                    passengersObj.put("lastName", pb.getPassengerName().substring(0, 1));
                    passengersObj.put("firstName", pb.getPassengerName().substring(1, pb.getPassengerName().length()));
                }
                if ("身份证".equals(pb.getPassengercardType())) {
                    passengersObj.put("idType", "ID_CARD");
                } else if ("护照".equals(pb.getPassengercardType())) {
                    passengersObj.put("idType", "2.DOC");
                } else if ("其他".equals(pb.getPassengercardType())) {
                    passengersObj.put("idType", "OTHER_ID");
                }
                passengersObj.put("idNo", pb.getIdcard());
                //乘机人电话全部设置为联系人电话
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
            System.out.println("添加乘客联系人返回:" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success"))) {
                back = jsonObject.getJSONObject("data").getJSONObject("shoppingCart").getString("id");
            } else if ("false".equals(jsonObject.getString("success"))) {
                String message = jsonObject.getString("message");
                logger.info("添加乘客联系人返回:" + message);
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
     * 创建订单信息
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param shoppingCartId 购物车id
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
            System.out.println("GS创建订单返回:" + back);
            logger.info(order_id + "GS创建订单返回" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("操作成功")) {
                logger.info(order_id + "GS创建订单成功");
            } else {
                logger.info(order_id + "GS创建订单失败:" + back);
                return "ERROR:GS创建订单失败";
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
     * 订单支付前检查
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param reservationCode 订单号
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
            System.out.println("GS订单支付前check返回:" + back);
            logger.info(order_id + "GS订单支付前check返回" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("操作成功")) {
                logger.info(order_id + "GS订单支付前check成功");
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
     * 订单支付(易宝), 获取支付地址
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param reservationCode 订单号
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
            paramObj.put("bankId", "5"); //易宝
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
            logger.info(order_id + "GS订单支付前check返回" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("操作成功") && back.contains("url")) {
                locationValue = jsonObject.getJSONObject("data").getString("url");
                logger.info(order_id + "GS支付易宝地址:" + locationValue);
                if (StringUtils.isNotEmpty(locationValue)) {
                    RequestConfig rConfig = RequestConfig.custom()
                            .setSocketTimeout(timeout)
                            .setConnectTimeout(timeout)
                            .setConnectionRequestTimeout(timeout)
                            .setRedirectsEnabled(false)
                            .setStaleConnectionCheckEnabled(true) //禁止自动重定向
                            .build();
                    // 第二个请求
                    HttpHost yeepayHost = new HttpHost("www.yeepay.com", 443, "https");
                    HttpGet get = new HttpGet(locationValue);
                    get.setConfig(rConfig);
                    get.setHeader("Host", "www.yeepay.com");
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
                    response = httpclient.execute(yeepayHost, get);
                    Header[] locationAfter = response.getHeaders("Location");
                    for (int i = 0; i < locationAfter.length; i++) {
                        locationValue = locationAfter[i].getValue();
                        logger.info(order_id + "第二个请求Location:" + locationValue);
                    }
                    if (StringUtils.isEmpty(locationValue)) {
                        logger.info(order_id + "支付请求：" + locationValue);
                        logger.info(order_id + "支付返回：" + back);
                        return "";
                    }
                    // 第三个请求
                    HttpHost cashdeskHost = new HttpHost("cashdesk.yeepay.com", 443, "https");
                    get = new HttpGet(locationValue);
                    get.setConfig(rConfig);
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
                    response = httpclient.execute(cashdeskHost, get);
                    locationAfter = response.getHeaders("Location");
                    for (int i = 0; i < locationAfter.length; i++) {
                        locationValue = locationAfter[i].getValue();
                        logger.info(order_id + "第三个请求Location:" + locationValue);
                    }
                    if (StringUtils.isEmpty(locationValue)) {
                        logger.info(order_id + "支付请求：" + locationValue);
                        logger.info(order_id + "支付返回：" + back);
                        return "";
                    }
                }
            } else {
                logger.info(order_id + "获取支付地址失败:" + back);
                return "ERROR:获取支付地址失败";
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
     * 获取订单详情
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param reservationCode 订单号
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
            logger.info(order_id + "GS获取订单详情返回:" + back);
            JSONObject jsonObject = JSON.parseObject(back);
            if ("true".equals(jsonObject.getString("success")) && back.contains("操作成功")) {
                JSONArray travelersArr = jsonObject.getJSONObject("data").getJSONObject("reservation").getJSONArray("travelers");
                // TODO 获取票号信息
                for (int i = 0; i < travelersArr.size(); i++) {
                    String passengerName = travelersArr.getJSONObject(i).getString("lastName") + travelersArr.getJSONObject(i).getString("firstName");
                    String idNo = travelersArr.getJSONObject(i).getString("idNo"); //证件号
                    //由于官网上证件号做了部分隐藏,这里去创单传过来的来证件号回填
                    List<Passergeninfo> passengers = orderjson.getPassengers();
                    for (int j = 0; j < passengers.size(); j++) {
                        if (passengers.get(j).getPassengerName().equals(passengerName)) {
                            idNo = passengers.get(j).getIdcard();
                        }
                    }
                    String ticketNumber = travelersArr.getJSONObject(i).getJSONArray("flightSegments").getJSONObject(0).getString("ticketNumber"); //票号
                }
                return back;
            } else {
                logger.info(order_id + "GS获取订单详情失败:" + back);
                return "ERROR:获取订单详情失败";
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


    //发送订单状态信息
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


   /* public static void main(String[] args) throws Exception {
        String orderJson = "{\r" +
                "    \"account\": \"15897736493_feeye123\",\r" +
                "    \"airline\": \"GS\",\r" +
                "    \"bankType\": \"CMB\",\r" +
                "    \"childrenMobile\": \"\",\r" +
                "    \"code\": \"\",\r" +
                "    \"codePassword\": \"\",\r" +
                "    \"creditNo\": \"5187188380513124\",\r" +
                "    \"cvv\": \"369\",\r" +
                "    \"drawerType\": \"GW\",\r" +
                "    \"expireMonth\": \"05\",\r" +
                "    \"expireYear\": \"2028\",\r" +
                "    \"flights\": [\r" +
                "        {\r" +
                "            \"airline\": \"GS\",\r" +
                "            \"arrival\": \"CAN\",\r" +
                "            \"cabin\": \"A\",\r" +
                "            \"departure\": \"TSN\",\r" +
                "            \"departureDate\": \"2020-05-31\",\r" +
                "            \"fType\": \"go\",\r" +
                "            \"flightNo\": \"GS7895\",\r" +
                "            \"price\": \"694.0\"\r" +
                "        }\r" +
                "    ],\r" +
                "    \"id\": \"32550175\",\r" +
                "    \"idCardNo\": \"410781197310154713\",\r" +
                "    \"idCardType\": \"IDCARD\",\r" +
                "    \"ifUsedCoupon\": false,\r" +
                "    \"isOutticket\": \"false\",\r" +
                "    \"linkMan\": \"华勇\",\r" +
                "    \"matchCabin\": false,\r" +
                "    \"mobile\": \"15897736493\",\r" +
                "    \"orderNo\": \"406786710\",\r" +
                "    \"orderTime\": \"2019-03-11 00:12:13\",\r" +
                "    \"otheraccount\": \"fyyzm1_wo4feizhiyou\",\r" +
                "    \"ownername\": \"王涛\",\r" +
                "    \"passengers\": [\r" +
                "        {\r" +
                "            \"birthday\": \"1984-07-19 00:00:00+08:00\",\r" +
                "            \"id\": \"37617002\",\r" +
                "            \"idcard\": \"411425198407198112\",\r" +
                "            \"passengerCardType\": \"身份证\",\r" +
                "            \"passengerName\": \"邵玉风\",\r" +
                "            \"passengerSex\": \"男\",\r" +
                "            \"passengerType\": \"成人\"\r" +
                "        },\r" +
//                "        {\r" +
//                "            \"birthday\": \"1983-11-21 00:00:00+08:00\",\r" +
//                "            \"id\": \"37617004\",\r" +
//                "            \"idcard\": \"350781198311212039\",\r" +
//                "            \"passengerCardType\": \"身份证\",\r" +
//                "            \"passengerName\": \"梁邵平\",\r" +
//                "            \"passengerSex\": \"男\",\r" +
//                "            \"passengerType\": \"成人\"\r" +
//                "        },\r" +
                "        {\r" +
                "            \"birthday\": \"1983-11-30 00:00:00+08:00\",\r" +
                "            \"id\": \"37617003\",\r" +
                "            \"idcard\": \"350802198311306035\",\r" +
                "            \"passengerCardType\": \"身份证\",\r" +
                "            \"passengerName\": \"林炜\",\r" +
                "            \"passengerSex\": \"男\",\r" +
                "            \"passengerType\": \"成人\"\r" +
                "        }\r" +
                "    ],\r" +
                "    \"payType\": \"xyk\",\r" +
                "    \"payerMobile\": \"18530203144\",\r" +
                "    \"qiangpiao\": \"2\",\r" +
                "    \"username\": \"fzybian\",\r" +
                "    \"ytype\": \"\"\r" +
                "}";

        JSONObject jsonObject = JSON.parseObject(orderJson);
        Orderinfo orderinfo = jsonObject.toJavaObject(Orderinfo.class);
        PNNewCreateOrderService service = new PNNewCreateOrderService();

        //创单测试
//        service.StartCreateOrder(orderJson, 0, 0);

        //单独测试查询航班接口
        String ip_port = DailiyunService.getRandomNewDailiIp(50);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        System.out.println("代理IP信息：" + ip_port);
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

        HttpClientContext context = new HttpClientContext();

        //单独测试查询航班接口
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
            System.out.println("已查到航班数据=====查询航班耗时：" + (endTime - startTime) / 1000 + "s");
        } else {
            System.out.println("查询航班耗时：" + (endTime - startTime) / 1000 + "s");
        }

//        String s ="N_V_IN=/ieECLAeRLdnpcmcywd6SVqKEppl1Y1FkDpbDhwhstP7wqVujxds96V0TXLEyEDFDMWO2Jh+38dBuTTqbjPjcg==;";
//        String regEx = "N_V_IN=(.*?);";
//        System.out.println(service.getSubUtil(s, regEx));
//
//        File file = new File("C:/GS.txt");
//        String text = FileUtils.readFileToString(file,"UTF-8");
//        System.out.println("解析航班返回:" + service.parseFlights(text, orderinfo));


        *//********************测试webDriver方式开始*****************************//*
//        WebDriver driver = PhantomjsDriverUtil.getProxyWebDriver(proxyIp, String.valueOf(proxyPort), "feeyeapp", "feeye789");
//        WebDriver driver = WebDriverObtain.getChromeDriver(proxyIp, String.valueOf(proxyPort), "feeyeapp", "feeye789");
//        for (int i = 0; i <1 ; i++) {
//            String cookie = service.queryFlights(orderinfo, "", driver);
//            Thread.sleep(20 * 1000);
//        }
        *//********************测试webDriver方式结束*****************************//*
    }*/

    /**
     * 查询航班(WebDriver方式)
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
//            webDriver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS); //设置页面加载超时时间
            webDriver.get("http://www.tianjin-air.com");
//            Thread.sleep(2 * 1000);

//            WebElement flightSearchForm = webDriver.findElements(By.cssSelector("form[name='flightSearchForm']")).get(0);
//            List <WebElement> inputs = flightSearchForm.findElements(By.tagName("input"));;

//            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
//            String js = "$(\"form[name='flightSearchForm'] :input\").attr('type','text')";
//            javascriptExecutor.executeScript(js);

            //模拟提交首页搜索航班表单
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

            WebElement element = webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/div/div[1]/div/div[2]/div/div/input")); //出发地
            Actions actions = new Actions(webDriver);
            actions.moveToElement(element).click().perform();
            element.sendKeys(origin);
//            Thread.sleep(1 * 1000);
            webDriver.findElement(By.cssSelector(".hyperlink")).click();

            element = webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/div/div[1]/div/div[4]/div/div/input")); //目的地
            actions = new Actions(webDriver);
            element.sendKeys(destination);
//            Thread.sleep(1 * 1000);
            webDriver.findElement(By.cssSelector(".hyperlink")).click();
            actions.moveToElement(element).click().perform();

            element = webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/div/div[1]/div/div[5]/div/div/div/input")); //去程日期
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
            webDriver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div/div/div[1]/div/div[8]/button")).click(); //点击搜索

//            Thread.sleep( 1 * 1000);
            back = webDriver.getPageSource();
            List<WebElement> elements = webDriver.findElements(By.cssSelector(".mod-flight")); //航班div
            if (!elements.isEmpty()) {
                //获取cookie
                Set<org.openqa.selenium.Cookie> cookies = webDriver.manage().getCookies();
                for (org.openqa.selenium.Cookie c : cookies) {
                    if ("csrfToken".equals(c.getName())) {
                        csrfToken = c.getValue();
                    }
                    cookie += c.getName() + "=" + c.getValue() + ";";
                }
                logger.info(order_id + "查询航班返回cookie:" + cookie);
                System.out.println("查询航班返回cookie:" + cookie);

//                Thread.sleep(3 * 1000);
                //选择航班
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

//            System.out.println(order_id + "查询航班返回:" + back);
            if (back.contains("价格更新没这么快，逛逛首页了解更多")) {
                return queryFlights(orderJson, "", webDriver);
            } else if (back.contains("请输入验证码")) {
                for (int i = 0; i < 10; i++) {
                    //webDriver截图验证码,保存到本地
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
                    //识别查询航班验证码
                    FileEntity fileEntity = new FileEntity(imageFile);
                    String url = "http://127.0.0.1:1111";
                    HttpPost imgPost = new HttpPost(url);
                    imgPost.setEntity(fileEntity);
                    httpClient = HttpClients.createDefault();
                    response = httpClient.execute(imgPost);
                    String verifyCode = EntityUtils.toString(response.getEntity());
                    System.out.println("查询航班验证码:" + verifyCode);
                    logger.info(order_id + "查询航班验证码:" + verifyCode);
                    webDriver.findElement(By.xpath("//*[@id=\"app\"]/div[4]/div/div[2]/div/div[2]/label/input")).sendKeys(verifyCode); //验证码输入框
                    webDriver.findElement(By.xpath("//*[@id=\"app\"]/div[4]/div/div[2]/div/div[3]/button")).click();
                    Thread.sleep(5 * 1000);
                    back = webDriver.getPageSource();
                    elements = webDriver.findElements(By.cssSelector(".mod-flight")); //航班div
                    if (!elements.isEmpty()) {
                        //获取cookie
                        Set<org.openqa.selenium.Cookie> cookies = webDriver.manage().getCookies();
                        for (org.openqa.selenium.Cookie c : cookies) {
                            cookie += c.getName() + "=" + c.getValue() + ";";
                        }
                        logger.info(order_id + "查询航班返回cookie:" + cookie);
                        System.out.println("查询航班返回cookie:" + cookie);

                        //选择航班
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
                                        logger.error("该元素不存在");
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
                                                    logger.error("该元素不存在");
                                                }
                                                WebElement priceChoose = null;
                                                if (StringUtils.isNotEmpty(productPrice) && price == Float.parseFloat(productPrice)) {
                                                    try {
                                                        priceChoose = c.findElement(By.cssSelector(".tj-icon tj-icon-quan"));
                                                    } catch (NoSuchElementException e) {
                                                        logger.error("该元素不存在");
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
                    if (StringUtils.isNotEmpty(back) && !back.contains("验证码")) {
                        break;
                    }
                }
                System.out.println("查询航班返回页面：" + back);
                logger.info(order_id + "查询航班返回页面:" + back);
            } else if (back.contains("您所选择的航段当天已售罄或无此航班，十分抱歉")) {
                return "ERROR:您所选择的航段当天已售罄或无此航班，十分抱歉";
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
     * 根据正则匹配字符串
     *
     * @param text
     * @param rgex 匹配的模式
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

    @Test
    public void a() {
        String a = "1.白露 \r" +
                " 2.陈定容 \r" +
                " 3.程敏 \r" +
                " 4.陈坤伦 \r" +
                " 5.何建春\r" +
                " 6.何秀琼 \r" +
                " 7.何学银 \r" +
                " 8.胡蓉华 \r" +
                " 9.李如菊 \r" +
                " 10.刘文秀\r" +
                "11.罗成德 \r" +
                "12.罗欣妍CHD \r" +
                "13.马桂芳 \r" +
                "14.马三春\r" +
                "15.施天敏 \r" +
                "16.施兴 \r" +
                "17.田华珍 \r" +
                "18.王建华 \r" +
                "19.王仕菊\r" +
                "20.魏庆林 \r" +
                "21.魏新龙 \r" +
                "22.吴刚 \r" +
                "23.吴睿晨CHD\r" +
                "24.夏天秀 \r" +
                "25.徐大国 \r" +
                "26.徐明会 \r" +
                "27.杨德芳\r" +
                "28.杨桂荣 \r" +
                "29.杨秀芳 \r" +
                "30.杨玉琼 \r" +
                "31.张明蓉\r" +
                "32.张清学 \r" +
                "33.赵莲玉 \r" +
                "34.朱仕琼\r" +
                "35.  DR6541 W   TH15APR  BZXTAO RR34  1555 1750      E --T1\r" +
                "36.  DR6542 U   TU20APR  TAOBZX RR34  1900 2120      E T1--\r" +
                "37.86-15126024004\r" +
                "38.T\r" +
                "39.SSR FOID DR HK1 NI513027194509260042/P15\r" +
                "40.SSR FOID DR HK1 NI513027193910080019/P16\r" +
                "41.SSR FOID DR HK1 NI513701198509180029/P1\r" +
                "42.SSR FOID DR HK1 NI513027195407032422/P19\r" +
                "43.SSR FOID DR HK1 NI513027194903106123/P13\r" +
                "44.SSR FOID DR HK1 NI513027196302050020/P30\r" +
                "45.SSR FOID DR HK1 NI513027195806110010/P25\r" +
                "46.SSR FOID DR HK1 NI513027194710250030/P32\r" +
                "47.SSR FOID DR HK1 NI513027195012200217/P7\r" +
                "48.SSR FOID DR HK1 NI513027194909230248/P31\r" +
                "49.SSR FOID DR HK1 NI513028194911184435/P4\r" +
                "50.SSR FOID DR HK1 NI511902201701027120/P12\r" +
                "51.SSR FOID DR HK1 NI513027196803170020/P29\r" +
                "52.SSR FOID DR HK1 NI513027196501150059/P11\r" +
                "53.SSR FOID DR HK1 NI513027195807280249/P24\r" +
                "54.SSR FOID DR HK1 NI513001195307030012/P14\r" +
                "55.SSR FOID DR HK1 NI500107201607248512/P23\r" +
                "56.SSR FOID DR HK1 NI513027196610020026/P5\r" +
                "57.SSR FOID DR HK1 NI513027196411130011/P22\r" +
                "58.SSR FOID DR HK1 NI513027195410150243/P33\r" +
                "59.SSR FOID DR HK1 NI513027195801120228/P18\r" +
                "60.SSR FOID DR HK1 NI513027195410270229/P17\r" +
                "61.SSR FOID DR HK1 NI513027195312010124/P3\r" +
                "62.SSR FOID DR HK1 NI513027195106020014/P27\r" +
                "63.SSR FOID DR HK1 NI513027195806010220/P8\r" +
                "64.SSR FOID DR HK1 NI513027196409070224/P34\r" +
                "65.SSR FOID DR HK1 NI513027195504200029/P2\r" +
                "66.SSR FOID DR HK1 NI513027195709210028/P6\r" +
                "67.SSR FOID DR HK1 NI513027195710270220/P26\r" +
                "68.SSR FOID DR HK1 NI513027195611050214/P21\r" +
                "69.SSR FOID DR HK1 NI51302719530910762X/P9\r" +
                "70.SSR FOID DR HK1 NI513027195511300044/P28\r" +
                "71.SSR FOID DR HK1 NI513027195404130078/P20\r" +
                "72.SSR FOID DR HK1 NI513027195810210049/P10\r" +
                "73.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303237/1/P34\r" +
                "74.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303236/1/P33\r" +
                "75.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303235/1/P32\r" +
                "76.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303234/1/P31\r" +
                "77.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303233/1/P30\r" +
                "78.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303232/1/P29\r" +
                "79.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303231/1/P28\r" +
                "80.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303230/1/P27\r" +
                "81.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303229/1/P26\r" +
                "82.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303228/1/P25\r" +
                "83.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303227/1/P24\r" +
                "84.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303226/1/P23\r" +
                "85.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303225/1/P22\r" +
                "86.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303224/1/P21\r" +
                "87.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303223/1/P20\r" +
                "88.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303222/1/P19\r" +
                "89.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303221/1/P18\r" +
                "90.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303220/1/P17\r" +
                "91.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303219/1/P16\r" +
                "92.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303218/1/P15\r" +
                "93.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303217/1/P14\r" +
                "94.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303216/1/P13\r" +
                "95.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303215/1/P12\r" +
                "96.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303214/1/P11\r" +
                "97.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303213/1/P10\r" +
                "98.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303212/1/P9\r" +
                "99.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303211/1/P8\r" +
                "100.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303210/1/P7\r" +
                "101.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303209/1/P6\r" +
                "102.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303208/1/P5\r" +
                "103.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303207/1/P4\r" +
                "104.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303206/1/P3\r" +
                "105.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303205/1/P2\r" +
                "106.SSR TKNE DR HK1 BZXTAO 6541 W15APR 2992330303204/1/P1\r" +
                "107.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303237/2/P34\r" +
                "108.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303236/2/P33\r" +
                "109.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303235/2/P32\r" +
                "110.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303234/2/P31\r" +
                "111.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303233/2/P30\r" +
                "112.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303232/2/P29\r" +
                "113.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303231/2/P28\r" +
                "114.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303230/2/P27\r" +
                "115.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303229/2/P26\r" +
                "116.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303228/2/P25\r" +
                "117.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303227/2/P24\r" +
                "118.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303226/2/P23\r" +
                "119.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303225/2/P22\r" +
                "120.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303224/2/P21\r" +
                "121.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303223/2/P20\r" +
                "122.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303222/2/P19\r" +
                "123.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303221/2/P18\r" +
                "124.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303220/2/P17\r" +
                "125.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303219/2/P16\r" +
                "126.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303218/2/P15\r" +
                "127.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303217/2/P14\r" +
                "128.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303216/2/P13\r" +
                "129.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303215/2/P12\r" +
                "130.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303214/2/P11\r" +
                "131.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303213/2/P10\r" +
                "132.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303212/2/P9\r" +
                "133.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303211/2/P8\r" +
                "134.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303210/2/P7\r" +
                "135.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303209/2/P6\r" +
                "136.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303208/2/P5\r" +
                "137.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303207/2/P4\r" +
                "138.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303206/2/P3\r" +
                "139.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303205/2/P2\r" +
                "140.SSR TKNE DR HK1 TAOBZX 6542 U20APR 2992330303204/2/P1\r" +
                "141.SSR CHLD DR HK1 02JAN17/P12\r" +
                "142.SSR CHLD DR HK1 24JUL16/P23\r" +
                "143.OSI DR CTCT86/15126024004\r" +
                "144.OSI DR CTCM17787865616 /P10\r" +
                "145.OSI DR CTCM18589358955 /P20\r" +
                "146.OSI DR CTCM13888781381 /P28\r" +
                "147.RMK 订单号:B2B20210406613905\r" +
                "148.RMK 86/17787865616/P10\r" +
                "149.RMK 86/18589358955/P20\r" +
                "150.RMK 86/13888781381/P28\r" +
                "151.FN/M/FCNY700.00/SCNY700.00/C0.00/TEXEMPTCN/TEXEMPTYQ/ACNY700.00/P12/        23\r" +
                "152.FN/M/FCNY700.00/SCNY700.00/C0.00/XCNY100.00/TCNY100.00CN/TEXEMPTYQ/         ACNY800.00\r" +
                "153.TN/299-2330303204/P1\r" +
                "154.TN/299-2330303205/P2\r" +
                "155.TN/299-2330303206/P3\r" +
                "156.TN/299-2330303207/P4\r" +
                "157.TN/299-2330303208/P5\r" +
                "158.TN/299-2330303209/P6\r" +
                "159.TN/299-2330303210/P7\r" +
                "160.TN/299-2330303211/P8\r" +
                "161.TN/299-2330303212/P9\r" +
                "162.TN/299-2330303213/P10\r" +
                "163.TN/299-2330303214/P11\r" +
                "164.TN/299-2330303215/P12\r" +
                "165.TN/299-2330303216/P13\r" +
                "166.TN/299-2330303217/P14\r" +
                "167.TN/299-2330303218/P15\r" +
                "168.TN/299-2330303219/P16\r" +
                "169.TN/299-2330303220/P17\r" +
                "170.TN/299-2330303221/P18\r" +
                "171.TN/299-2330303222/P19\r" +
                "172.TN/299-2330303223/P20\r" +
                "173.TN/299-2330303224/P21\r" +
                "174.TN/299-2330303225/P22\r" +
                "175.TN/299-2330303226/P23\r" +
                "176.TN/299-2330303227/P24\r" +
                "177.TN/299-2330303228/P25\r" +
                "178.TN/299-2330303229/P26\r" +
                "179.TN/299-2330303230/P27\r" +
                "180.TN/299-2330303231/P28\r" +
                "181.TN/299-2330303232/P29\r" +
                "182.TN/299-2330303233/P30\r" +
                "183.TN/299-2330303234/P31\r" +
                "184.TN/299-2330303235/P32\r" +
                "185.TN/299-2330303236/P33\r" +
                "186.TN/299-2330303237/P34\r" +
                "187.FP/CASH,CNY\r" +
                "188.LUM777";
        String[] msgArray = a.split("\r");
        Map<String, String> idNameMap = new HashMap<>();
        int j = 0;
        Pattern pattern = Pattern.compile("([0-9].[\\u4e00-\\u9fa5]{2,})");
        for (int i = 0; i < msgArray.length;i++) {
            String msg = msgArray[i].trim();
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                String id = msg.split("\\.")[0];
                String name = msg.split("\\.")[1];;
                idNameMap.put(id,name);
            } else {
                j = i;
                break;
            }
        }
        Map<String, String> idCradMap = new HashMap<>();
        Pattern patternCard = Pattern.compile("(?=NI|PP|ID[A-Z]{0,}\\d+).*");
        for (int k = j;k < msgArray.length;k++) {
            String msg = msgArray[k].trim();
            Matcher matcher = patternCard.matcher(msg);
            if (StringUtils.isNotEmpty(msg) && msg.contains("SSR FOID")) {
                if (matcher.find()) {
                    String cardMsg = matcher.group(0);
                    String[] cardMsgArry = cardMsg.split("/");
                    String card =cardMsgArry[0];
                    String id = cardMsgArry[1];
                    idCradMap.put(id.replace("P",""),card);
                }
            }
        }
        Map<String, String> idTicketNoMap = new HashMap<>();
        Pattern patternTicketNo = Pattern.compile("([0-9]{3}[-]{0,1}[0-9]{10}/P?).*");
        for (int k = j;k < msgArray.length;k++) {
            String msg = msgArray[k].trim();
            Matcher matcher = patternTicketNo.matcher(msg);
            if (StringUtils.isNotEmpty(msg) && msg.contains("TN/")) {
                if (matcher.find()) {
                    String cardMsg = matcher.group(0);
                    String[] cardMsgArry = cardMsg.split("/");
                    String card =cardMsgArry[0];
                    String id = cardMsgArry[1];
                    idTicketNoMap.put(id.replace("P",""),card);
                }
            }
        }
        Map<String, String> lineMap = new HashMap<>();
        Pattern patternLine = Pattern.compile("\\s([A-Z]{2})\\s[A-Z0-9]{2,}\\s([A-Z]{6})\\s([0-9]{4})\\s([A-Z]{1}[0-9]{2}[A-Z]{3}).*");
        Set<String> flightNoSet = new HashSet<>();
        for (int k = j;k < msgArray.length;k++) {
            String msg = msgArray[k].trim();
            Matcher matcher = patternLine.matcher(msg);
            if (StringUtils.isNotEmpty(msg) && msg.contains("SSR TKNE")) {
                if (matcher.find()) {
                    String air = matcher.group(1);
                    String line = matcher.group(2);
                    String flightCode  = matcher.group(3);
                    String date = matcher.group(4);
                    String cabin = date.substring(0,1);
                    date = date.substring(1);
                    String flightNo = air + flightCode;
                    flightNoSet.add(flightNo);
                    lineMap.put(line,flightNo+"##" + cabin + "##" + date);
                }
            }
        }
        Map<String, String> flightDateMap = new HashMap<>();
        Pattern patternDate = Pattern.compile("\\s([0-9]{4})\\s([0-9]{4}\\s[A-Z])");
        for (int i =0; i < msgArray.length;i++) {
            String msg = msgArray[i];
            if (StringUtils.isNotEmpty(msg)) {
                for (String flightNo:flightNoSet) {
                    if (msg.contains(flightNo)) {
                        Matcher matcher = patternDate.matcher(msg);
                        if (matcher.find()) {
                            String departureDate = matcher.group(0);
                            String arrivalDate = matcher.group(1);
                            flightDateMap.put(flightNo,departureDate + ":" +arrivalDate);
                        }
                    }
                }
            }
        }
    }
}