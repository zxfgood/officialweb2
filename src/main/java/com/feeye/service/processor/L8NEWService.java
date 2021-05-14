package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.CabinInfo;
import com.feeye.bean.CrawlParam;
import com.feeye.bean.FlightInfo;
import com.feeye.bean.OfficeFlightInfoUtil;
import com.feeye.util.FingerPrintUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author peng.lw
 * @ClassName: L8Processor
 * @Description: TODO(祥鹏航空官网抓取)
 * @date 2015-5-7 下午04:32:44
 */

public class L8NEWService {
    private static final String splitchar = "#@_@#";
    private static final int maxSearchcount = 8;
    private static final String splicchar = "#@_@#";
    private static final int timeout = 20000;

    private static final Logger logger = Logger.getLogger(L8NEWService.class);

    public static Map<String, String> cabin_priceType_map = new HashMap<String, String>();
    //代理云账号密码
    String dlyAccount = //"123:123";
            "feeyeapp:feeye789";

    public String imageCode(String session, String proxyIp, String proxyPort, String jessionId) {
        StringBuffer sb = new StringBuffer();
        String content = "";
        try {
            sb.append("<feeye-official>");
            sb.append("<official>" + Constant.L8.toString() + "</official>");
            sb.append("<url>http://www.luckyair.net/hnatravel/imagecode?code=" + Math.random() + "</url>");
            sb.append("<type>3</type> ");
            sb.append("<codeUrl>true" + "@" + "feeyesb_123456789" + "</codeUrl>");
            sb.append("<codeParseType>2</codeParseType>");//设置验证码破解类型  1  2  yunsu破解
            sb.append("<codeType>5000</codeType>");
            sb.append("<codeUrl>true</codeUrl>");
            sb.append("<method>get</method>");
            sb.append("<session>" + session + "</session> ");
            sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
            sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
            sb.append("<encod>utf-8</encod>");
            sb.append("<headers>");
            sb.append("<head name='Host'>www.luckyair.net</head>");
            sb.append("<head name='Referer'>http://www.luckyair.net/</head>");
            sb.append("<head name='Cookie'>" + jessionId + "</head>");
            sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
            sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
            sb.append("</headers>");
            sb.append("</feeye-official>");
            content = OfficialMain.setRequestParams(sb.toString());
        } catch (Exception e) {
            logger.error("error", e);
        }
        return content;
    }

    private String checkImage(String ticketcode, String proxyIp, String proxyPort, String session, String jessionId) {
        StringBuffer sb = new StringBuffer();
        String content = "";
        try {
            sb.append("<feeye-official>");
            sb.append("<official>" + Constant.L8.toString() + "</official>");
            sb.append("<url>http://www.luckyair.net/hnatravel/checkSignCode</url> ");
            sb.append("<type>3</type> ");
            sb.append("<method>POST</method>");
            sb.append("<session>" + session + "</session> ");
            sb.append("<codeParseType>1</codeParseType>");
            sb.append("<CodeUrl>@</CodeUrl>");
            sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
            sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
            sb.append("<encod>gb2312</encod>");
            sb.append("<params>");
            sb.append("<param name='tickcode'>" + ticketcode + "</param>");
            sb.append("<param name='redirect'>aHR0cDovL3d3dy5sdWNreWFpci5uZXQv</param>");
            sb.append("<param name='flag'>0</param>");
            sb.append("<param name='submit'>提交</param>");
            sb.append("</params>");
            sb.append("<headers>");
            sb.append("<head name='Host'>www.luckyair.net</head>");
            sb.append("<head name='Referer'>http://www.luckyair.net/hnatravel/signcode.jsp?flag=0&redirect=aHR0cDovL3d3dy5sdWNreWFpci5uZXQv</head>");
            sb.append("<head name='Cookie'>" + jessionId + "</head>");
            sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
            sb.append("</headers>");
            sb.append("</feeye-official>");
            content = OfficialMain.setRequestParams(sb.toString());
        } catch (Exception e) {
            logger.error("error", e);
        }
        return content;
    }

    //查询航班参数
    private final String csrfToken = "";

    /**
     * 重定向
     *
     * @param session
     * @param proxyIp
     * @param proxyPort
     * @param cookie
     * @param Location
     * @return
     */
    public String toLocation(String session, String proxyIp, String proxyPort, String cookie, String Location) {
        String content = "";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<feeye-official>");
            sb.append("<official>" + Constant.L8.toString() + "</official>");
            sb.append("<url>" + Location + "</url> ");
            sb.append("<type>3</type> ");
            sb.append("<method>get</method>");
            sb.append("<session>" + session + "</session> ");
            sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
            sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
            sb.append("<encod>utf-8</encod> ");
            sb.append("<headers>");
            sb.append("<head name='Host'>www.luckyair.net</head>");
            sb.append("<head name='Cookie'>" + cookie + "</head>");
            sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
            sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
            sb.append("</headers>");
            sb.append("</feeye-official>");
            content = OfficialMain.setRequestParams(sb.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return content;
    }

    public String imageCode1(String session, String proxyIp, String proxyPort, String cookie, String codeAccount) {
        StringBuffer sb = new StringBuffer();
        String content = "";
        try {
            sb.append("<feeye-official>");
            sb.append("<official>" + Constant.L8.toString() + "</official>");
            sb.append("<url>http://www.luckyair.net/hnatravel/imagecode?code=" + Math.random() + "</url>");
            //sb.append("<codeUrl>true" + "@" + "feeyesb_123456789" + "</codeUrl>");
            sb.append("<codeUrl>true" + "@" + codeAccount + "</codeUrl>");
            sb.append("<codeParseType>2</codeParseType>");//设置验证码破解类型  1  2  yunsu破解
            sb.append("<type>3</type> ");
            sb.append("<codeType>5000</codeType>");
            sb.append("<method>get</method>");
            sb.append("<session>" + session + "</session> ");
            sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
            sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
            sb.append("<encod>utf-8</encod>");
            sb.append("<headers>");
            sb.append("<head name='Host'>www.luckyair.net</head>");
            sb.append("<head name='Referer'>http://www.luckyair.net/</head>");
            sb.append("<head name='Cookie'>" + cookie + "</head>");
            sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
            sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 " +
                    "Firefox/42.0</head>");
            sb.append("</headers>");
            sb.append("</feeye-official>");
            content = OfficialMain.setRequestParams(sb.toString());

        } catch (Exception e) {
            logger.error("error", e);
        }
        return content;
    }

    public String handsecuritycode(String content, String session, String proxyip, String port, String cookie, String codeAccount) {
        String[] arrs = content.split(splitchar);
        String newcookie = cookie;
        String response = "";
        if (arrs.length == 3) {
            response = toLocation(session, proxyip, port, cookie, arrs[2]);
            if (StringUtils.isEmpty(response)) {
                return "响应超时";
            }
            //获取jessionid
            Map<String, String> cookiemap = cookieStrToMap(cookie);
            String jessionid = cookiemap.get("JSESSIONID");
            newcookie = parseCookie(cookiemap);
            cookiemap.put(response.split(splitchar)[0].replaceAll(";", "").split("=")[0], response.split(splitchar)[0].replaceAll(";", "").split("=")[1]);
            int maxretry = 4;
            while ((StringUtils.isNotEmpty(response)) && (response.contains("请输入验证码")) && maxretry >= 0) {
                response = imageCode1(session, proxyip, port, parseCookie(cookiemap), codeAccount);
                if (StringUtils.isEmpty(response)) {
                    continue;
                }
                logger.info("8L检查验证码响应结果1" + response);
                String[] rs2 = response.split("#@_@#");
                String ticketcode = "";
                if (rs2.length == 2) {
                    ticketcode = rs2[1];
                }
                response = checkImage(ticketcode, proxyip, port, session, parseCookie(cookiemap));
                if (StringUtils.isEmpty(response)) {
                    continue;
                }
                arrs = response.split(splitchar);
                //添加sso cookie
                if (arrs.length >= 2) {
                    cookiemap.put(arrs[0].replaceAll(";", "").split("=")[0], arrs[0].replaceAll(";", "").split("=")[1]);
                }
                logger.info("8L检查验证码响应结果2" + response);
                maxretry--;

            }

            if (StringUtils.isEmpty(response)) {
                return "响应超时";
            }
            //如果验证未通过 则继续重试
            logger.info("8L检查验证码响应结果3" + response);
            arrs = response.split(splitchar);
            if (StringUtils.isNotEmpty(arrs[0])) {
                cookiemap.remove("JSESSIONID");
                cookiemap.put("JSESSIONID", jessionid);
                response = toLocation(session, proxyip, port, parseCookie(cookiemap), arrs[2]);
                if (StringUtils.isEmpty(response)) {
                    return "响应超时";
                }
                logger.info("重定向结果" + response);
                //arrs=response.split(splitchar);
                newcookie = parseCookie(cookiemap);
            }
        }
        return newcookie;
    }

    public static void
    main(String[] args) throws IOException {
        long starttime = System.currentTimeMillis();
        //测试
        L8NEWService l8NEWService = new L8NEWService();
        try {
            // for (int i = 0; i < 50; i++) {
            l8NEWService.getFlightPriceInfo("SZX", "KMG", "2018-11-20", "8L9980", "", "", 3, "ONEWAY", "go", "");
            File file = new File("F:\\8L.txt");
            String content = FileUtils.readFileToString(file, "UTF-8");
            CrawlParam param = new CrawlParam("KMG", "WUH", "2019-11-13");
            l8NEWService.doCrawlNew(content, param, "8L9895");

            //  }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("耗时" + (System.currentTimeMillis() - starttime) / 1000 + "s");
    }

    @Test
    public void a() {
        getFlightPriceInfo("KMG", "SZX", "2020-06-01", "8L9979", "", "123", 0, "", "", "");
    }

    /**
     * 查询航班
     *
     * @param dept
     * @param arrival
     * @param dateString
     * @param flightNo
     * @param cabin
     * @param serviceId
     * @param fType
     * @param arriDate
     * @return
     * @throws ServerDownException
     */
    public String getFlightPriceInfo(String dept, String arrival, String dateString, String flightNo, String cabin, String serviceId, int retrycount, String tripType, String fType, String arriDate) {
        String strResult = "";
        CrawlParam param = new CrawlParam(dept, arrival, dateString);
        param.setDeparuteDate(dateString);
        param.setArrival(arrival);
        param.setDeparture(dept);
        List<FlightInfo> list = new ArrayList<FlightInfo>();
        long time = System.currentTimeMillis();
        logger.info(serviceId + "-开始查价格");
        try {
            CloseableHttpClient httpClient = null;
            BasicCookieStore cookieStore = new BasicCookieStore(); // 一个cookies
            //代理云
            String ip_port = DailiyunService.getRandomIp(50);
            String proxyIp = ip_port.split(":")[0];  //代理IP地址
            int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //代理IP端口
            HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

            org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
            BasicAuthCache authCache = new BasicAuthCache();
            authCache.put(dailiyunProxy, proxyAuth);
            String[] dlyAccountInfo = dlyAccount.split(":");
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(dailiyunProxy), new UsernamePasswordCredentials(dlyAccountInfo[0], dlyAccountInfo[1]));
            try {
                proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
            } catch (MalformedChallengeException e1) {
                logger.error("error", e1);
            }
            int timeout = 15000;
            RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout)
                    .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                    //  .setProxy(dailiyunProxy)
                    .setExpectContinueEnabled(false)
                    .setStaleConnectionCheckEnabled(true)
                    .build();

            HttpClientContext context = HttpClientContext.create();
            context.setAuthCache(authCache);
            context.setRequestConfig(config);
            context.setCredentialsProvider(credsProvider);

            SocketConfig socketConfig = SocketConfig.custom()
                    .setSoKeepAlive(false).setSoLinger(1)
                    .setSoReuseAddress(true)
                    .setSoTimeout(timeout)
                    .setTcpNoDelay(true).build();

            SSLContext sslcontext = SSLContexts.createDefault();
            SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1.2"},
                    null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", factory)  // 用来配置支持的协议
                    .build();
            // 共享连接池
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            connectionManager.setMaxTotal(100);
            connectionManager.setDefaultMaxPerRoute(20);
            httpClient = HttpClientBuilder.create()
                    .setConnectionManager(connectionManager)
                    .setConnectionManagerShared(true)
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultSocketConfig(socketConfig)
                    .setDefaultCredentialsProvider(credsProvider)
                    .build();

            try {
                String csrfToken = "";


                HttpGet get = new HttpGet("http://www.luckyair.net/");
                get.setConfig(config);
                get.setHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
                get.setHeader("Host", "www.luckyair.net");

                CloseableHttpResponse response = httpClient.execute(get);

                String content = EntityUtils.toString(response.getEntity(), "utf-8");
                logger.info(serviceId + "-开始查价格返回:" + content);
                response.close();

                StringBuffer sb = new StringBuffer();

                List<org.apache.http.cookie.Cookie> listCookies = cookieStore.getCookies();

                for (int i = 0; i < listCookies.size(); i++) {
                    sb.append(listCookies.get(i).getName() + "=" + listCookies.get(i).getValue() + ";");
                    if (listCookies.get(i).getName().equals("csrfToken")) {
                        csrfToken = listCookies.get(i).getValue();
                    }
                }


                if (StringUtils.isEmpty(csrfToken)) {
                    if (retrycount > 0) {
                        retrycount--;
                        Thread.sleep(2000);
                        return getFlightPriceInfo(dept, arrival, dateString, flightNo, cabin, serviceId, retrycount, tripType, fType, arriDate);
                    }
                }

                String result = flightSearch(param.getDeparture(), param.getArrival(), param.getDeparuteDate(), dlyAccount, csrfToken, httpClient, config, context, sb.toString(), "", 0);
                logger.info(serviceId + "查价格:" + result);
                if (result.contains("10000") || result.contains("10001")) {
                    if (retrycount > 0) {
                        retrycount--;
                        Thread.sleep(2000);
                        return getFlightPriceInfo(dept, arrival, dateString, flightNo, cabin, serviceId, retrycount, tripType, fType, arriDate);
                    }
                }
                //解析航班数据
                list = doCrawlNew(result, param, flightNo);

            } catch (Exception e) {
                logger.info("error" + e);
            } finally {
                logger.info(time + "-关闭查价格");
                httpClient.close();
            }

            if (list == null && retrycount > 0) {
                retrycount--;
                Thread.sleep(2000);
                return getFlightPriceInfo(dept, arrival, dateString, flightNo, cabin, serviceId, retrycount, tripType, fType, arriDate);
            }
            if (list != null && list.size() > 0) {
                strResult = OfficeFlightInfoUtil.getResult(list, flightNo);
                return strResult;
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
        return "error:未查找到对应价格";
    }


    /**
     * 查询航班
     *
     * @param httpclient
     * @param config
     * @param cookie
     * @param orderJson
     * @param verifyCode 验证码
     * @param retry      重试次数
     * @return
     */
    private String flightSearch(String depCode, String arrCode, String flightDate, String dlyAccount, String csrfToken, CloseableHttpClient httpclient, RequestConfig config, HttpClientContext context, String cookie, String verifyCode, int retry) {

        CloseableHttpResponse response = null;
        String back = "";
        try {

//            HttpGet get = new HttpGet("http://www.luckyair.net/api/information/flightcities");
//
//			get.setHeader("User-Agent",
//					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
//			get.setHeader("Host", "www.luckyair.net");
//			get.setHeader("Referer", "http://www.luckyair.net/");
//			get.setHeader("Cookie", cookie);
//			get.setConfig(config);
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//            response = httpclient.execute(get);
//
//			response.close();


            HttpPost post = new HttpPost("http://www.luckyair.net/api/recommand/query");

            String json = "{\"_csrf\":\"" + csrfToken + "\"}";

            StringEntity s = new StringEntity(json);
            s.setContentType("application/json");
            post.setEntity(s);
            post.setConfig(config);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Content-Type", "application/json;charset=utf-8");
            post.setHeader("Referer", "http://www.luckyair.net/");
            post.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            post.setHeader("Cookie", cookie);
            // post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(post);

            response.close();


//			HttpGet get = new HttpGet("http://login.luckyair.net/web/union/keep?time=" + (new Date().getTime()));
//			get.setConfig(config);
//			get.setHeader("User-Agent",
//					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
//			get.setHeader("Host", "www.luckyair.net");
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
//			get.setHeader("Referer", "http://www.luckyair.net/");
//			get.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(get);
//
//			back = EntityUtils.toString(response.getEntity(), "utf-8");
//
//			response.close();


            post = new HttpPost("http://www.luckyair.net/api/flight/fareCalendar");

            json = "{\"depCode\":\"" + depCode + "\",\"arrCode\":\"" + arrCode + "\",\"dateStart\":\"2020-04-01\",\"dateEnd\":\"2020-05-31\",\"airlineCode\":\"8L\",\"_csrf\":\"" + csrfToken + "\"}";

            s = new StringEntity(json);
//			s.setContentType("application/json");
//			s.setContentEncoding("utf-8");
            post.setEntity(s);
            post.setConfig(config);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Content-Type", "application/json;charset=utf-8");
            post.setHeader("Referer", "http://www.luckyair.net/");
            post.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            post.setHeader("Cookie", cookie);
            // post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");

            response.close();

            Thread.sleep(3000);

            //进入查询航班页面
            HttpHost host = new HttpHost("www.luckyair.net", 80, "http");
            post = new HttpPost("/flight/search.html");
            post.setConfig(config);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Cookie", cookie);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes(StandardCharsets.UTF_8)));
            post.setHeader("Proxy-Connection", "keep-alive");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("tripType", "ONEWAY"));
            nameValue.add(new BasicNameValuePair("passenger", "{\"ADT\":1,\"CNN\":0,\"INF\":0,\"MWD\":0,\"PWD\":0}"));
            nameValue.add(new BasicNameValuePair("originDestinations", "[{\"depCode\":\"" + depCode + "\",\"arrCode\":\"" + arrCode + "\",\"flightDate\":\"" + flightDate + "\"}]"));
            nameValue.add(new BasicNameValuePair("isInternational", "false"));
            nameValue.add(new BasicNameValuePair("_csrf", csrfToken));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            response = httpclient.execute(host, post, context);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            response.close();


            JSONObject paramObj = new JSONObject();
            JSONObject passenger = new JSONObject();
            int AdtNum = 1;
            int CnnNum = 0;
            int InfNum = 0;
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
            paramObj.put("passenger", passenger);
            paramObj.put("originDestinations", originDestinations);
            paramObj.put("_csrf", csrfToken);
            //加密参数
            String desc = FingerPrintUtil.getDesc();
            desc = URLDecoder.decode(desc, "utf-8");


            paramObj.put("desc", desc);
            //查询航班验证码
            if (StringUtils.isNotEmpty(verifyCode)) {
                paramObj.put("vc", verifyCode);
            }
            System.out.println("查询参数：" + JSON.toJSONString(paramObj));
            Thread.sleep(2000);
            post = new HttpPost("http://www.luckyair.net/api/flight/search");
            post.setConfig(config);
            StringEntity entity = new StringEntity(paramObj.toString());
//            entity.setContentType("application/json");
//            entity.setContentEncoding("utf-8");
            post.setEntity(entity);

            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3704.400 QQBrowser/10.4.3587.400");
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Referer", "http://www.luckyair.net/flight/search.html");
            post.setHeader("Cookie", cookie);
            // post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(host, post, context);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("查询价格返回:" + back);

            //1000是需要识别验证码
            int count = 0;
            while (back != null && (back.contains("10000")) && count < 3) {
                back = flightResult(httpclient, depCode, arrCode, flightDate, desc, csrfToken, config, cookie, dlyAccount);
                count++;
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


    public String flightResult(CloseableHttpClient httpclient, String dep, String arr, String depDate, String desc, String csrfToken, RequestConfig config, String cookie, String dlyAccount) {

        try {

            HttpGet get = new HttpGet("http://login.luckyair.net/web/union/keep?time=" + (new Date().getTime()));
            get.setConfig(config);
            get.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            get.setHeader("Host", "www.luckyair.net");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes(StandardCharsets.UTF_8)));
            get.setHeader("Referer", "http://www.luckyair.net/flight/search.html");
            get.setHeader("Proxy-Connection", "keep-alive");
            CloseableHttpResponse response = httpclient.execute(get);

            String back = EntityUtils.toString(response.getEntity(), "utf-8");

            response.close();


            get = new HttpGet("http://www.luckyair.net/hnatravel/imagecodeajax?v=1");

            get.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            get.setHeader("Host", "www.luckyair.net");
            get.setHeader("Cookie", cookie);
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes(StandardCharsets.UTF_8)));
            get.setHeader("Referer", "http://www.luckyair.net/flight/search.html");
            get.setConfig(config);
            response = httpclient.execute(get);
            CloseableHttpClient httpC = null;
            String codeValue = "";
            String fileUri = "c://testImg//" + UUID.randomUUID() + ".jpg";

            StringBuffer sb = new StringBuffer();

            try {
                InputStream re = response.getEntity().getContent();
                OutputStream os = null;
                try {
                    os = new FileOutputStream(fileUri);
                    IOUtils.copy(re, os);
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    re.close();
                    os.close();
                }


                Header[] headers = response.getAllHeaders();

                for (int b = 0; b < headers.length; b++) {
                    if ("Set-Cookie".equalsIgnoreCase(headers[b].getName())) {
                        if (headers[b].getValue().contains("ci=")) {
                            sb.append(headers[b].getValue().split(";")[0] + ";");
                        }

                    }
                }
                if (!cookie.contains(sb.toString())) {
                    cookie = sb.toString() + cookie;
                }

                logger.info(cookie);

                response.close();

                HttpPost post = null;
                File imageFile = new File(fileUri);
                FileEntity reqEntity = new FileEntity(imageFile);

                String codeUrl = "http://127.0.0.1:1111";
                post = new HttpPost(codeUrl);
                BasicCookieStore cookieStore = new BasicCookieStore();

                RequestConfig config1 = RequestConfig.custom().setSocketTimeout(timeout)
                        .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).build();

                HttpClientBuilder bd = HttpClients.custom().setDefaultCookieStore(cookieStore)
                        .setDefaultRequestConfig(config1);
                httpC = bd.build();
                post.setEntity(reqEntity);
                response = httpC.execute(post);
                codeValue = EntityUtils.toString(response.getEntity());

            } catch (Exception e) {
                // TODO: handle exception
                logger.error("error", e);
            } finally {
                response.close();
                httpC.close();
            }

            if (StringUtils.isEmpty(codeValue)) {
                return "10000";
            } else {
                File fileOld = new File(fileUri);
                if (fileOld.exists()) {
                    fileOld.delete();
                }
            }

            Thread.sleep(3000);


            HttpPost post = new HttpPost("http://www.luckyair.net/api/flight/search");

            post.setConfig(config);
            //{"tripType":"ONEWAY","originDestinations":[{"depCode":"SZX","arrCode":"KMG","flightDate":"2020-04-24"}],"passenger":{"ADT":1,"CNN":0,"INF":0,"MWD":0,"PWD":0},"desc":"coBPtm4BZy5Ly7E1arnlj4vyFx4IUisDADFJEBhSGfIXYl/elXICeY115jwY6dt2","vc":"u7fps","_csrf":"r5037dCQSfNH83KwYk-aiFF1"}
            String json = "{\"tripType\":\"ONEWAY\",\"originDestinations\":[{\"depCode\":\"" + dep + "\",\"arrCode\":\"" + arr + "\",\"flightDate\":\"" + depDate + "\"}],\"passenger\":{\"ADT\":1,\"CNN\":0,\"INF\":0,\"MWD\":0,\"PWD\":0},\"desc\":\""
                    + desc + "\",\"_csrf\":\"" + csrfToken + "\"";
            json = json + ",\"vc\":\"" + codeValue + "\"";
            json = json + "}";

            StringEntity s = new StringEntity(json);
//			s.setContentEncoding("utf-8");
            s.setContentType("application/json");
            post.setEntity(s);
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes(StandardCharsets.UTF_8)));
            post.setHeader("Cookie", cookie);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("Content-Type", "application/json;charset=utf-8");
            post.setHeader("Referer", "http://www.luckyair.net/flight/search.html");
            post.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");

            response = httpclient.execute(post);

            String tt = EntityUtils.toString(response.getEntity(), "utf-8");

            response.close();

            if (tt == null || !tt.contains("10002")) {
                logger.info(tt + "----" + codeValue);
                return "10000";
            }

            logger.info(tt);

            Header[] headers = response.getAllHeaders();
            sb.setLength(0);
            for (int b = 0; b < headers.length; b++) {
                if ("Set-Cookie".equalsIgnoreCase(headers[b].getName())) {
                    if (headers[b].getValue().contains("pa=")) {
                        sb.append(headers[b].getValue().split(";")[0] + ";");
                    }

                }
            }

            cookie = cookie + sb.toString();
            //cookie = "csrfToken=qtXfzPfF3v94R8JpDGezw1-V; _ga=GA1.2.1284944261.1586254302; _gid=GA1.2.776640070.1586254302; _gat_TrueMetrics=1; ci=C4C5C2E8D7FF341FD47DF52092D7A544; pa=C3nFX3UUzRNrBGI2MVLIlUBOB49J9ZhlHhMTP36tkLX8jGcb";
            logger.info(cookie);
            Thread.sleep(1000);

            post = new HttpPost("http://www.luckyair.net/api/flight/search");

            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes(StandardCharsets.UTF_8)));
            post.setConfig(config);
            //{"tripType":"ONEWAY","originDestinations":[{"depCode":"SZX","arrCode":"KMG","flightDate":"2020-04-24"}],"passenger":{"ADT":1,"CNN":0,"INF":0,"MWD":0,"PWD":0},"desc":"coBPtm4BZy5Ly7E1arnlj4vyFx4IUisDADFJEBhSGfIXYl/elXICeY115jwY6dt2","_csrf":"r5037dCQSfNH83KwYk-aiFF1"}
            json = "{\"tripType\":\"ONEWAY\",\"originDestinations\":[{\"depCode\":\"" + dep + "\",\"arrCode\":\"" + arr + "\",\"flightDate\":\"" + depDate + "\"}],\"passenger\":{\"ADT\":1,\"CNN\":0,\"INF\":0,\"MWD\":0,\"PWD\":0},\"desc\":\"" + desc + "\",\"_csrf\":\"" + csrfToken + "\"}";

            logger.info(json);

            s = new StringEntity(json);
//			s.setContentEncoding("utf-8");
//			s.setContentType("application/json");
            post.setEntity(s);

            post.setHeader("Accept", "application/json, text/plain, */*");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            post.setHeader("Cookie", cookie);
            post.setHeader("Host", "www.luckyair.net");
            post.setHeader("Origin", "http://www.luckyair.net");
            post.setHeader("Content-Type", "application/json;charset=utf-8");
            post.setHeader("Referer", "http://www.luckyair.net/flight/search.html");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");

            response = httpclient.execute(post);

            tt = EntityUtils.toString(response.getEntity(), "utf-8");

            response.close();

            logger.info(tt);


            return tt;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("error", e);
        }
        return "";
    }

    /**
     * 解析查询航班返回的数据
     *
     * @param content
     * @param flightNo
     * @return
     */
    private List<FlightInfo> doCrawlNew(String content, CrawlParam param, String flightNo) {
//        logger.info( "8L解析航班数据" );
        List<FlightInfo> resList = new ArrayList<FlightInfo>();
        List<CabinInfo> CabinInfoList = new ArrayList<CabinInfo>();
        try {
            if (StringUtils.isNotEmpty(content)) {
                FlightInfo flightInfo = new FlightInfo();
                flightInfo.setFlightNo(flightNo);
                flightInfo.setDeparutre(param.getDeparture());// 出发地
                flightInfo.setArrival(param.getArrival()); // 到达地
                flightInfo.setDeparutreDate(param.getDeparuteDate()); // 起飞时间
                flightInfo.setWebType(Constant.L8.toString());
                flightInfo.setUpdateTime(new Date());
                //json转换
                JSONObject result = JSON.parseObject(content);
                if (result.containsKey("data")) {
                    JSONObject data = result.getJSONObject("data");
                    if (data.containsKey("offers")) {
                        JSONObject offers = data.getJSONArray("offers").getJSONObject(0);
                        if (offers.containsKey("detail")) {
                            JSONArray itinerarys = offers.getJSONObject("detail").getJSONArray("itinerarys");
                            for (int i = 0; i < itinerarys.size(); i++) {
                                JSONObject json = itinerarys.getJSONObject(i);
                                JSONArray productPrices = json.getJSONArray("productPrices");
                                for (int j = 0; j < productPrices.size(); j++) {
                                    JSONObject services = productPrices.getJSONObject(j).getJSONObject("detail").getJSONArray("services").getJSONObject(0);
                                    String flightNumber = services.getString("flightNo");
                                    if (!flightNo.contains(flightNumber)) {
                                        continue;
                                    }
                                    String productName = services.getString("productName");
                                    String cabinCode = services.getString("cabinCode");
                                    String totalItineraryFare = productPrices.getJSONObject(j).getJSONObject("detail").getString("totalItineraryFare");
                                    String inventory = services.getString("inventory");
                                    String seat = inventory;
                                    if (StringUtils.isEmpty(inventory)) {
                                        seat = 10 + "";
                                    }
                                    CabinInfo cabinInfo = new CabinInfo();
                                    cabinInfo.setBaseCabin(productName); // 仓位类型
                                    cabinInfo.setCabinCode(cabinCode); // 仓位
                                    cabinInfo.setPrice(totalItineraryFare); // 价格
                                    cabinInfo.setLastSeat(seat); // 座位数
                                    CabinInfoList.add(cabinInfo);
                                }
                                flightInfo.setCabins(CabinInfoList);
                                resList.add(flightInfo);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("8L解析航班数据出错", e);
        }
        return resList;
    }

    /**
     * 根据正则匹配字符串
     *
     * @param soap
     * @param rgex 匹配的模式
     * @return
     */
    private String getSubUtil(String text, String rgex) {
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(m.group(1))) {
                return m.group(1);
            }
        }
        return "";
    }

    private String searchFlight(String session, String proxyIp, String proxyPort, String dep, String arr, String jsessionId, String deptime, String tripType, String fType, String arriDate) {
        StringBuilder sb = new StringBuilder();
        String content = "";
        try {
            sb.append("<feeye-official>");
            sb.append("<official>").append("L8").append("</official>");
            sb.append("<url>http://www.luckyair.net/flight/searchflight2016.action</url>");
            sb.append("<type>").append("3").append("</type> ");
            sb.append("<method>post</method>");
            sb.append("<session>").append(session).append("</session> ");
            sb.append("<proxyIp>").append(proxyIp).append("</proxyIp>");
            sb.append("<proxyPort>").append(proxyPort).append("</proxyPort>");
            sb.append("<encod>utf-8</encod> ");
            sb.append("<params>");
            if (StringUtils.isNotEmpty(tripType) && "RETURN".equals(tripType)) {
                sb.append("<param name='tripType'>ROUNDTRIP</param>");
            } else {
                sb.append("<param name='tripType'>ONEWAY</param>");
            }
            sb.append("<param name='orgCity'>").append(dep).append("</param>");
            sb.append("<param name='orgCity_interNAtional'></param>");
            sb.append("<param name='dstCity'>").append(arr).append("</param>");
            sb.append("<param name='dstCity_interNAtional'></param>");
            sb.append("<param name='flightDate'>").append(deptime).append("</param>");
            sb.append("<param name='returnDate'>").append(arriDate).append("</param>");
            sb.append("<param name='passengerCount.adultNum'>1</param>");
            sb.append("<param name='passengerCount.childNum'>0</param>");
            sb.append("<param name='passengerCount.infantNum'>0</param>");
            sb.append("</params>");
            sb.append("<headers>");
            sb.append("<head name='Host'>www.luckyair.net</head>");
            sb.append("<head name='Origin'>http://www.luckyair.net</head>");
            sb.append("<head name='Referer'>http://www.luckyair.net/</head>");
            sb.append("<head name='Cookie'>").append(jsessionId).append("</head>");
            sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0</head>");
            sb.append("</headers>");
            sb.append("</feeye-official>");
            //  content = new FeeyeHttpClient(ParamsParser.parse(sb.toString())).executeHttpResponse();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return content;
    }

    private void filterdata(List<FlightInfo> list) {
        //过滤JJC产品类型
        for (FlightInfo flightInfo : list) {
            List<CabinInfo> cabinInfos = new ArrayList<>();
            for (CabinInfo cabinInfo : flightInfo.getCabins()) {
                if (!"JJC".contains(cabinInfo.getPriceType())) {
                    // flightInfo.getCabins().remove(cabinInfo);
                    cabinInfos.add(cabinInfo);
                }

            }
            flightInfo.setCabins(cabinInfos);
        }
    }

    public String verifycode(String session, String proxyIp, String proxyPort, String cookie, String locationurl) {
        String addcookie = "";
        String[] rs = null;
        String content = toLocation(session, proxyIp, proxyPort, cookie, locationurl);
        String jsessionid = content.split(splicchar)[0];
        for (String entity : jsessionid.split(";")) {
            if (entity.contains("JSESSIONID")) {
                jsessionid = entity;
            }
        }
        //验证码校验
        if (StringUtils.isNotEmpty(content) && content.contains("请输入验证码")) {
            content = imageCode(session, proxyIp, proxyPort, jsessionid);
            String[] rs2 = content.split("#@_@#");
            String ticketcode = "";
            if (rs2.length == 2) {
                ticketcode = rs2[1];
            }
            logger.info("验证码为：" + ticketcode);
            logger.info("cookie为：" + cookie);
            content = checkImage(ticketcode, proxyIp, proxyPort, session, jsessionid);
            rs = content.split("#@_@#");
            if (rs.length == 2 || rs.length == 3) {
                addcookie = rs[0];
                if (rs.length == 3) {
                    content = toLocation(session, proxyIp, proxyPort, cookie + rs[0], rs[2]);
                    jsessionid = cookie + rs[0];
                }
            }
        }
        return jsessionid;
    }

    public String getflightinfo(String session, String proxyIp, String proxyPort, String dept, String arrival, String dateString, String cookie, String tripType, String fType) {
        String addCookie = "";
        StringBuffer buffer = new StringBuffer();
        buffer.append("<feeye-official>");
        buffer.append("<official>" + Constant.L8.toString() + "</official> ");
        buffer.append("<url>http://www.luckyair.net/flightresult/flightresult2016.action</url> ");
        buffer.append("<type>3</type> ");
        buffer.append("<session>" + session + "</session>");
        buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
        buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
        buffer.append("<method>post</method>");
        buffer.append("<max>20</max> ");
        buffer.append("<encod>utf-8</encod> ");
        buffer.append("<params>");
        buffer.append("<param name='orgCity'>" + dept + "</param>");
        buffer.append("<param name='dstCity'>" + arrival + "</param>");
        buffer.append("<param name='flightDate'>" + dateString + "</param>");
        buffer.append("<param name='index'>1</param>");
        if (StringUtils.isNotEmpty(fType) && "back".equals(fType)) {
            buffer.append("<param name='flightseq'>2</param>");
        } else {
            buffer.append("<param name='flightseq'>1</param>");
        }
        if (StringUtils.isEmpty(tripType)) {
            tripType = "ONEWAY";
        }
        buffer.append("<param name='tripType'>" + tripType + "</param>");
        //buffer.append("<param name='desc'>" + FingerPrintUtil.getDesc(0) + "</param>");
        //buffer.append("<param name='desc'>" + "JeUEPgZXFsnSDYkzgko8As43Edpb0QZI2A6aQdBJeORarv8NE29Wn6mowkISwzeVjoYMDsrk+UMVil0G7C3n8yqYUROCYJsqtXgLyrTppbrFHR0q91mXQn2Q0ltpGD24ZMPWusrwt+p/RpbVkvSwesnNvDLhZTj4/UfT66GN9jrxH9Yx9ynYspf7HpCoBgGXSx7an0tcQwFLAGQN6JC+voYHbgHECWP8ZQ2j5bdNPdvR+G52O2qwgGoYS6mIRu3M/o67y51pv1vETwoFfecMySkKeq6gtQBFDeqs4eItj0+SfWELKrnmKc9dKaHKLCLASgQZlEA6OWirw9oQkSmrSLKUeXBhz5BsLRzagbNLxMQ=" + "</param>");
        String desc = FingerPrintUtil.getDesc();
        // String bbb=JSExeUtil.exeJs("/L8getCipherValue.js");
        try {
            desc = URLDecoder.decode(desc, "utf-8");
            //bbb=URLDecoder.decode(bbb,"utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        //logger.info("desc=="+desc+" asdasd==="+bbb);
        buffer.append("<param name='desc'>" + desc + "</param>");
        /*buffer.append("<param name='desc'>" + FingerPrintUtil.getDesc(0) + "</param>");*/
        buffer.append("</params>");
        buffer.append("<headers>");
        buffer.append("<head name='Referer'>http://www.luckyair.net/flight/searchflight2016.action</head >");
        buffer.append("<head name='Cookie'>" + cookie + "</head>");
        buffer.append("</headers>");
        buffer.append("</feeye-official>");
        logger.info("8L请求参数:" + buffer.toString());
        String content = OfficialMain.setRequestParams(buffer.toString());
        if (StringUtils.isNotEmpty(content)) {
            return content;
        }
        return "";
    }

    /**
     * map 转cookie头字符串
     *
     * @param cookies
     * @return
     */
    private String parseCookie(Map<String, String> cookies) {
        StringBuilder cookiesStr = new StringBuilder();
        for (Entry<String, String> entry : cookies.entrySet()) {
            cookiesStr.append(entry.getKey() + "=" + entry.getValue() + ";");
        }
        return cookiesStr.toString();
    }

    private Map<String, String> cookieStrToMap(String cookieStr) {
        Map<String, String> allcookiesMap = new HashMap<String, String>();
        for (String cok : cookieStr.split(";")) {
            allcookiesMap.put(cok.split("=")[0], cok.split("=")[1]);
        }
        return allcookiesMap;
    }


    /**
     * 解析舱位信息
     */
    private List<FlightInfo> doCrawl(String content) {
        List<FlightInfo> list = new ArrayList<FlightInfo>();
        if (StringUtils.isEmpty(content)) {
            return list;
        }
        if (null == content || content.trim().equals("") || content.contains("哎呀…您访问的页面出错了")
                || content.contains("请输入验证码") || content.contains("http://www.luckyair.net/500.html")) {

            return list;
        }
        try {
            Document document = Jsoup.parse(content);
            Elements datadiv = document.getElementsByAttributeValue("class", "dis_in_div m_t_15 cabin_con");

            String oldpar = "<div\\s.*?\\s*flg=\"(.*?)\"\\s*.*?>";

            Pattern p = Pattern.compile(oldpar);
            //Map<String, String> typemap = getTypeMap();
            for (org.jsoup.nodes.Element data : datadiv) {
                FlightInfo flightinfo = null;
                List<CabinInfo> cabinlist = new ArrayList<CabinInfo>();
                for (int y = 0; y < 3; y++) {
                    String flg = "";
                    switch (y) {
                        case 0:
                            flg = "packageone";
                            break;
                        case 1:
                            flg = "packagetwo";
                            break;
                        case 2:
                            flg = "packagethree";
                            break;
                        default:
                            flg = "packagetwo";
                            break;
                    }
                    if (data.toString().contains("flg=\"" + flg + "\"")) {
                        Elements packagetwo = data.getElementsByAttributeValue("flg", flg);
                        Elements type = packagetwo.get(0).getElementsByAttributeValue("class", "title vm m_l_18 w_99");
                        Map<String, String> map = new HashMap<String, String>();
                        for (int i = 0; i < type.size(); i++) {
                            map.put(i + "", type.get(i).text());
                        }
                        if (packagetwo != null) {
                            Matcher matcher = p.matcher(packagetwo.html());
                            int index = 0;
                            while (matcher.find()) {
                                CabinInfo cabininfo = new CabinInfo();
                                String title = map.get(index + "");
                                String[] cabins = matcher.group(1).split(";");

                                cabininfo.setCabinCode(cabins[0]);
                                cabininfo.setPrice(cabins[8]);
                                //cabininfo.setPriceType(typemap.get(title) == null ? "CYH" : typemap.get(title));
                                cabininfo.setPriceType(cabins[5]);
                                cabininfo.setLastSeat(cabins[cabins.length - 5]);

                                if (flightinfo == null) {
                                    flightinfo = new FlightInfo();
                                    flightinfo.setFlightNo(cabins[1] + cabins[2]);
                                    flightinfo.setDeparutreDate(cabins[6].split(" ")[0]);
                                    flightinfo.setDeparutreTime(cabins[6].split(" ")[1]);    //起飞时间
                                    flightinfo.setArriveDate(cabins[7].split(" ")[0]);
                                    flightinfo.setWebType("8L");
                                    flightinfo.setArriveTime(cabins[7].split(" ")[1]);    //到达时间
                                    flightinfo.setDeparutre(cabins[3]);
                                    flightinfo.setArrival(cabins[4]);
                                }
                                cabinlist.add(cabininfo);
                                index++;
                            }
                        }
                    }
                }
                if (flightinfo != null) {
                    flightinfo.setCabins(cabinlist);
                }
                list.add(flightinfo);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

    public Map<String, String> getTypeMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("轻松行", "QSX");
        map.put("保价护航", "BJHH");
        map.put("随心飞", "SXF");
        map.put("商务行", "SSSWZ");
        map.put("残疾军人/伤残人民警察优惠票 ", "JJC");
        return map;
    }

    public static void getLowestPrice(List<CabinInfo> cabinfo) {
        Collections.sort(cabinfo, new Comparator<CabinInfo>() {
            public int compare(CabinInfo c1, CabinInfo c2) {
                Float price1 = Float.parseFloat(StringUtils.isEmpty(c1.getPrice()) ? "0" : c1.getPrice());
                Float price2 = Float.parseFloat(StringUtils.isEmpty(c2.getPrice()) ? "0" : c2.getPrice());
                return price1.compareTo(price2);
            }
        });
        for (int i = cabinfo.size() - 1; i >= 0; i--) {
            if (i > 0) {
                cabinfo.remove(i);
            }
        }
    }

    private String getPriceType(String code, String cabin) {
        if ("BJHH".equals(code)) {
            return "BJHH"; // 保价护航
        }
        return cabin_priceType_map.get(cabin);
    }

    public void getReturnFlightPriceInfo(String dept, String arrival, String deptDate, String flightNo,
                                         int retrycount, String tripType, String flightNo1, String arriDate, Map<String, String> resultMap) {
        if (StringUtils.isEmpty(flightNo) && StringUtils.isEmpty(flightNo1)) {
            return;
        }
        logger.info("往返查询信息，去程：" + dept + "-" + arrival + "-" + deptDate + "-" + flightNo + "；回程：" + arrival + "-"
                + dept + "-" + arriDate + "-" + flightNo1);
        try {
            String session = UUID.randomUUID().toString();
            StringBuffer sb = new StringBuffer();
            String jsessionId = "";
            String cookie = "";

            String proxyipport = DailiyunService.getRandomIp(30);
            String proxyIp = proxyipport.split(":")[0];
            String proxyPort = proxyipport.split(":")[1];

            sb.append("<feeye-official>");
            sb.append("<official>" + Constant.L8.toString() + "</official>");
            sb.append("<url>http://www.luckyair.net/</url> ");
            sb.append("<type>3</type> ");// 3或者6 使用本地代理 7 阿布云代理
            sb.append("<method>get</method>");
            sb.append("<session>" + session + "</session> ");
            sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
            sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
            sb.append("<encod>utf-8</encod>");
            sb.append("<headers>");
            sb.append("<head name='Host'>www.luckyair.net</head>");
            sb.append("<head name='Referer'>http://www.luckyair.net/</head>");
            sb.append("<head name='Cookie'>" + cookie + "</head>");
            sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
            sb.append(
                    "<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
            sb.append("</headers>");
            sb.append("</feeye-official>");
            String content = OfficialMain.setRequestParams(sb.toString());

            // 添加重试处理
            String[] rs = content.split(splicchar);
            jsessionId = rs[0];
            logger.info("开始查询往返航班");
            // 查询第一步
            content = searchFlight(session, proxyIp, proxyPort, dept, arrival, jsessionId, deptDate, tripType, "",
                    arriDate);
            logger.info("开始查询往返去程航班");
            if (!StringUtils.isEmpty(flightNo)) {
                content = getflightinfo(session, proxyIp, proxyPort, dept, arrival, deptDate, jsessionId, tripType,
                        "go");
                int count = 0;
                while (count < maxSearchcount) {
                    count++;
                    if (StringUtils.isNotEmpty(content)) {
                        rs = content.split(splicchar);
                        if (rs.length == 2 && content.contains("dis_in_div m_t_15 cabin_con")) {
                            // 如果查询
                            content = rs[1];
                            break;
                        } else {
                            content = getflightinfo(session, proxyIp, proxyPort, dept, arrival, deptDate, jsessionId,
                                    tripType, "go");
                        }
                    } else {
                        // 处理其他重定向情况
                        // 重定向结果可能为 http://www.luckyair.net/error.jsp
                        // http://www.luckyair.net/500.html等
                        // 未获取到查询结果、最多查询5次
                        content = getflightinfo(session, proxyIp, proxyPort, dept, arrival, deptDate, jsessionId,
                                tripType, "go");
                    }
                }
                logger.debug("去程航班查询原始结果：\r\n" + content);
                if (!content.contains("dis_in_div m_t_15 cabin_con")) {
                    logger.info("未查询到结果。。。");
                } else {
                    logger.info("查询成功");
                }
                List<FlightInfo> list = doCrawl(content);
                if (list.size() > 0) {
                    filterdata(list);
                    logger.info("去程航班查询结果：\r\n" + JSON.toJSONString(list));
                    logger.info("去程航班解析结果：" + OfficeFlightInfoUtil.getResult(list, flightNo));
                    resultMap.put("go", OfficeFlightInfoUtil.getResult(list, flightNo));
                }
            }
            if (StringUtils.isNotEmpty(flightNo1)) {
                logger.info("开始查询往返回程航班");
                content = getflightinfo(session, proxyIp, proxyPort, arrival, dept, arriDate, jsessionId, tripType,
                        "back");
                int count = 0;
                while (count < maxSearchcount) {
                    count++;
                    if (StringUtils.isNotEmpty(content)) {
                        rs = content.split(splicchar);
                        if (rs.length == 2 && content.contains("dis_in_div m_t_15 cabin_con")) {
                            // 如果查询
                            content = rs[1];
                            break;
                        } else {
                            content = getflightinfo(session, proxyIp, proxyPort, arrival, dept, arriDate, jsessionId,
                                    tripType, "back");
                        }
                    } else {
                        // 处理其他重定向情况
                        // 重定向结果可能为 http://www.luckyair.net/error.jsp
                        // http://www.luckyair.net/500.html等
                        // 未获取到查询结果、最多查询5次
                        content = getflightinfo(session, proxyIp, proxyPort, arrival, dept, arriDate, jsessionId,
                                tripType, "back");
                    }
                }
                logger.debug("回程航班查询原始结果：\r\n" + content);
                if (!content.contains("dis_in_div m_t_15 cabin_con")) {
                    logger.info("未查询到结果。。。");
                } else {
                    logger.info("查询成功");
                }
                List<FlightInfo> list = doCrawl(content);
                if (list.size() > 0) {
                    filterdata(list);
                    logger.info("回程航班查询结果：\r\n" + JSON.toJSONString(list));
                    logger.info("回程航班解析结果：" + OfficeFlightInfoUtil.getResult(list, flightNo1));
                    resultMap.put("back", OfficeFlightInfoUtil.getResult(list, flightNo1));
                }
            }
        } catch (Exception e) {
            logger.error("数据解析异常", e);
        }
    }

}
