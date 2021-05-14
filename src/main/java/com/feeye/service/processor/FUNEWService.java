package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feeye.util.FingerPrintUtil;
import com.feeye.util.YunSu;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
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
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;

public class FUNEWService {
    private static final Logger logger = Logger.getLogger(FUNEWService.class);

    private String getFlightInfo(String serviceId, String flightNo, boolean isVerify, String verifyCode, String cookie) throws IOException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        String session = UUID.randomUUID().toString();
        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore(); // 一个cookies
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // 初始化SSL连接
        } catch (Exception e) {
            logger.error("error", e);
        }

        //代理云
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];  //代理IP地址
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //代理IP端口
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(dailiyunProxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("feeyeapp", "feeye789"));
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            logger.error("error", e1);
        }

        int timeout = 40000;
        RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout)
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setProxy(dailiyunProxy)
                .setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();

        HttpClientContext context = HttpClientContext.create();
        context.setAuthCache(authCache);
        context.setRequestConfig(config);
        context.setCredentialsProvider(credsProvider);

        HttpClientBuilder builder = null;
        builder = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(config).setDefaultCredentialsProvider(credsProvider)
                .setProxy(dailiyunProxy);
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
        httpclient = builder.build();

//        String dep = param.getDeparture();
//        String arr = param.getArrival();
//        String depdate = param.getDeparuteDate();


        //先请求首页获取jsessionId
        HttpHost host = new HttpHost("www.fuzhou-air.cn", 80, "http");
        HttpGet get = new HttpGet("http://www.fuzhou-air.cn/");
        get.setHeader("Host", "www.fuzhou-air.cn");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
        CloseableHttpClient client = HttpClients.createDefault();
        response = client.execute(host, get);
        //获取cookie
        String indexCookie = "";
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if ("Set-Cookie".equals(header.getName())) {
                indexCookie += header.getValue() + ";";
            }
        }
        System.out.println("进入FU首页获取的cookie" + indexCookie);
        logger.info("进入FU首页获取的cookie" + cookie);
        if (!cookie.contains("ci") && !cookie.contains("pa"))
            cookie = indexCookie;
        try {
            //开始查询航班信息
            HttpPost post = new HttpPost("/frontend/api/flight.action");
            post.setConfig(config);
            //获取加密参数desc
            String desc = FingerPrintUtil.getDesc();
            JSONObject queryObject = new JSONObject();
            queryObject.put("index", "0");
            queryObject.put("orgCity", "FOC");
            queryObject.put("dstCity", "HRB");
            queryObject.put("flightdate", "2019-04-25");
            //单程
            queryObject.put("tripType", "ONEWAY");
            queryObject.put("times", "3800810593");
            queryObject.put("desc", "coBPtm4BZy5Ly7E1arnlj0GidvXwFa0v1dqnLAZibE1NpTzA99vSC6y0jxyoBdtf");
            //是否需要验证码
            if (isVerify) {
                queryObject.put("vc", verifyCode);
            }
            StringEntity entity = new StringEntity(queryObject.toString(), Charset.forName("UTF-8"));
            StringBuilder sbu = new StringBuilder();
            String[] c = cookie.split(";");
            for (String c1 : c) {
                if (c1.contains("Path") || c1.contains("HttpOnly") || c1.contains("Domain")) {
                    continue;
                }
                sbu.append(c1).append(";");
            }
            cookie = sbu.toString();
            post.setEntity(entity);
            //设置请求头
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String("feeyeapp:feeye789".getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            post.setHeader("Content-Type", "application/text; charset=UTF-8");
            post.setHeader("Cookie", cookie);
            post.setHeader("Host", "www.fuzhou-air.cn");
            post.setHeader("Origin", "http://www.fuzhou-air.cn");
            post.setHeader("Referer", "http://www.fuzhou-air.cn/b2c/static/flightSearch.html?orgCityCode=FOC&dstCityCode=ZUH&orgDate=2019-04-25&dstDate=&adult=1&child=0&infant=0&trip=ONEWAY");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
            if (cookie.contains("pa")) {
                config = RequestConfig.custom().setSocketTimeout(timeout)
                        .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                        .setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();
                post.setConfig(config);
                response = httpclient.execute(host, post);
            } else {
                response = httpclient.execute(host, post, context);
            }
            Header[] headersArr = response.getAllHeaders();
            String newCookie1 = "";

            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    newCookie1 += header.getValue() + ";";
                }
            }
            if (!StringUtils.isEmpty(newCookie1)) {
                cookie = cookie + newCookie1;
            }
            String back = "";
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject backJson = JSON.parseObject(back);
            logger.info("FU查询航班返回：" + back);
            if (backJson.size() == 3) {
                JSONArray segmentsArray = backJson.getJSONObject("data").getJSONArray("segments");
                if (segmentsArray.isEmpty()) {
                    logger.info("FU没有查询到相关航班信息" + back);
                } else {
                    logger.info("FU查询到的航班信息" + back);
                    System.out.println(back);
//                    list = doCrawl(back , param , cookie , session , serviceId , "" , "" , flightNo , config , httpclient);
//                    return list;
                }
            }
            //查询航班次数过多，需要验证码
            if (backJson.size() == 1) {
                if ("10000".equals(backJson.get("sta").toString())) {
                    config = RequestConfig.custom()
                            .setConnectTimeout(35000)
                            .setConnectionRequestTimeout(35000)
                            .setSocketTimeout(60000)
                            .setExpectContinueEnabled(false)
                            .setStaleConnectionCheckEnabled(true)
                            .build();
                    response.close();
                    get = new HttpGet("/hnatravel/imagecodefu?code=0.15237324653420825");
//                    get.setConfig(config);
                    get.setHeader("Host", "www.fuzhou-air.cn");
                    get.setHeader("Cookie", cookie);
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
                    //查询航班验证码用默认的httpClient,减少请求时间
//                    CloseableHttpClient client = HttpClients.createDefault();
                    response = client.execute(host, get);
                    //返回的cookie (如果需要输入验证码，回调后会添加新的key为pa的cookie)
                    headersArr = response.getAllHeaders();
                    String newCookie = "";
                    for (Header header : headersArr) {
                        if ("Set-Cookie".equals(header.getName())) {
                            newCookie += header.getValue() + ";";
                        }
                    }
                    if (newCookie.contains("JSESSIONID")) {
                        cookie = cookie + newCookie;
                    }
                    if (!StringUtils.isEmpty(newCookie) && !newCookie.contains("JSESSIONID")) {
                        sbu = new StringBuilder();
                        c = cookie.split(";");
                        for (String c1 : c) {
                            if (c1.contains("ci")) {
                                sbu.append(newCookie).append(";");
                            } else {
                                sbu.append(c1).append(";");
                            }
                        }
                        cookie = sbu.toString();
                        logger.info("当前cookie: " + cookie);
                    }
                    //查询航班验证码
                    String fileUri = "C://testImg//" + session + ".jpg";
                    InputStream is = response.getEntity().getContent();
                    OutputStream os = new FileOutputStream(fileUri);
                    IOUtils.copy(is, os);
                    os.close();
                    //云速验证码识别
                    is = new FileInputStream(new File(fileUri));
                    String validtext = YunSu.getValidCode(is, "3000", "fyyzm1", "wo4feizhiyou");
                    org.dom4j.Document document = null;
                    try {
                        document = DocumentHelper.parseText(validtext);
                    } catch (DocumentException e) {
                        logger.error("error", e);
                    }
                    org.dom4j.Element root = document.getRootElement();
                    String error = root.elementText("Error");
                    String result = "";
                    if (error != null && !"".equals(error)) {
                        result = error;
                        logger.error("error" + result);
                    } else {
                        result = root.elementText("Result");
                    }
                    File file = new File(fileUri);
                    if (file.exists()) {
                        file.delete();
                    }
                    logger.info("FU识别查询航班验证码:" + result);
                    //回调
                    return getFlightInfo(serviceId, flightNo, true, result, cookie);
                } else if ("10002".equals(backJson.get("sta").toString())) {
                    //验证码输入正确,回调不带验证码参数
                    response.close();
                    config = RequestConfig.custom()
                            .setConnectTimeout(35000)
                            .setConnectionRequestTimeout(35000)
                            .setSocketTimeout(60000)
                            .setExpectContinueEnabled(false)
                            .setStaleConnectionCheckEnabled(true)
                            .build();
                    return getFlightInfo(serviceId, flightNo, false, "", cookie);
                } else if ("10001".equals(backJson.get("sta").toString())) {
                    //TODO 封ip情况
                    logger.error("当前ip已被封,换ip重试中");
//                    return getFlightInfo(param , serviceId , flightNo , false , "");
                }
            }
        } catch (IOException e) {
            logger.error("error", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e2) {
                }
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (Exception e2) {
                }
            }
        }
        return "";
    }

//    /**
//     * 解析查询航班返回的数据
//     *
//     * @param content
//     * @param flightNo
//     * @return
//     */
//    private List<FlightInfo>
//
//    doCrawl(String content , CrawlParam param , String cookie , String session , String serviceId ,
//                                     String proxyIp , String proxyPort , String flightNo , RequestConfig config , CloseableHttpClient httpclient) {
//        List<FlightInfo> resList = new ArrayList<FlightInfo>();
//        List<CabinInfo> CabinInfoList = new ArrayList<CabinInfo>();
//        try {
//            if (StringUtils.isNotEmpty(content)) {
//                FlightInfo flightInfo = new FlightInfo();
//                flightInfo.setFlightNo(flightNo);
//                flightInfo.setDeparutre(param.getDeparture());// 出发地
//                flightInfo.setArrival(param.getArrival()); // 到达地
//                flightInfo.setDeparutreDate(param.getDeparuteDate()); // 起飞时间
//                flightInfo.setWebType(Constant.FU.toString());
//                flightInfo.setUpdateTime(new Date());
//                //json转换
//                JSONObject jsonObject = JSON.parseObject(content);
//                FUFlightsInfo fuFlightsInfo = jsonObject.toJavaObject(FUFlightsInfo.class);
//                List<FUFlightsInfo.DataBean.SegmentsBean> segmentsBeans = fuFlightsInfo.getData().getSegments();
//                for (int i = 0; i < segmentsBeans.size(); i++) {
//                    String fno = segmentsBeans.get(i).getFlightno();
//                    if (!flightNo.contains(fno)) {
//                        continue;
//                    }
//                    for (int j = 0; j < segmentsBeans.get(i).getProducts().size(); j++) {
//                        String adultFare = segmentsBeans.get(i).getProducts().get(j).getCabin().getAdultFare();
//                        String cabinCode = segmentsBeans.get(i).getProducts().get(j).getCabin().getCabinCode();
//                        String inventory = segmentsBeans.get(i).getProducts().get(j).getCabin().getInventory();
//                        String signProductName = segmentsBeans.get(i).getProducts().get(j).getCabin().getSignProductName();
//
//                        CabinInfo cabinInfo = new CabinInfo();
//                        cabinInfo.setBaseCabin(signProductName); // 仓位类型
//                        cabinInfo.setCabinCode(cabinCode); // 仓位
//                        cabinInfo.setPrice(adultFare); // 价格
//                        cabinInfo.setLastSeat(inventory); // 座位数
//                        CabinInfoList.add(cabinInfo);
//                    }
//                    flightInfo.setCabins(CabinInfoList);
//                    resList.add(flightInfo);
//                }
//            }
//        } catch (Exception e) {
//            logger.error("FU解析航班数据出错" , e);
//        }
//        return resList;
//    }


    public static void main(String[] args) throws Exception {
//        //测试代码
//        List<FlightInfo> resList = new ArrayList<FlightInfo>();
//        File file = new File("F:/FlightInfo.txt");
//        StringBuilder text = new StringBuilder();
//        String sr = null;
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        while ((sr = br.readLine()) != null) {
//            text.append(sr);
//        }
//        CrawlParam param = new CrawlParam();
//        param.setDeparture("FOC");
//        param.setArrival("HRB");
//        param.setDeparuteDate("2019-04-25");
//        List<FlightInfo> list = new FUNEWService().doCrawl(text.toString(), param, "", "", "", "", "", "FU6651", null, null);
//        if (list.size() > 0) {
//            String strResult = OfficeFlightInfoUtil.getResult(list, "FU6651");
//            System.out.println(strResult);
//        }

        FUNEWService funewService = new FUNEWService();
        funewService.getFlightInfo("", "", false, "", "");
    }
}