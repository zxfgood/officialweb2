/*
package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.CabinInfo;
import com.feeye.bean.CrawlParam;
import com.feeye.bean.FU.FUFlightsInfo;
import com.feeye.bean.FlightInfo;
import com.feeye.common.Constant;
import com.feeye.util.PropertiesUtils;
import com.feeye.util.YunSu;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
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
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FUNEWService {
    private static final Logger logger = Logger.getLogger(FUNEWService.class);
    private static final int timeout = 50000;

    public String getFlightPriceInfo(String dept, String arrival, String deptDate, String flightNo, String cabin, String serviceId) {
        String strResult = "";
*/
/*        CrawlParam param = new CrawlParam(dept, arrival, deptDate);
        param.setDeparuteDate(deptDate);
        param.setArrival(arrival);
        param.setDeparture(dept);
        List<FlightInfo> list = new ArrayList();*//*

        try {
            CloseableHttpClient httpclient = null;
            SSLConnectionSocketFactory sslsf = null;
            BasicCookieStore cookieStore = new BasicCookieStore();
            try {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        return true;
                    }
                }).build();
                sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String ip_port = DailiyunService.getRandomIp(50);
            String proxyIp = ip_port.split(":")[0];
            int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
            HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

            BasicScheme proxyAuth = new BasicScheme();
            BasicAuthCache authCache = new BasicAuthCache();
            authCache.put(dailiyunProxy, proxyAuth);

            String dlyAccount = PropertiesUtils.getPropertiesValue("config", "dlyAccount");
            String[] dlyAccountInfo = dlyAccount.split(":");
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(dlyAccountInfo[0], dlyAccountInfo[1]));
            try {
                proxyAuth.processChallenge(new BasicHeader("Proxy-Authenticate", "BASIC realm=default"));
            } catch (MalformedChallengeException e1) {
                logger.error("error", e1);
            }
            RequestConfig config = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000).setConnectionRequestTimeout(50000).setProxy(dailiyunProxy).setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();

            HttpClientContext context = HttpClientContext.create();
            context.setAuthCache(authCache);
            context.setRequestConfig(config);
            context.setCredentialsProvider(credsProvider);

            HttpClientBuilder builder = null;


            builder = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore).setDefaultRequestConfig(config).setDefaultCredentialsProvider(credsProvider).setProxy(dailiyunProxy);
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
            httpclient = builder.build();

*/
/*            list = getFlightInfo(httpclient, config, context, param, serviceId, flightNo, false, "", "");
            if (list.size() > 0) {
                return OfficeFlightInfoUtil.getResult(list, flightNo);
            }*//*

        } catch (Exception e) {
            logger.error("error", e);
        }
        return "error:未查找到对应价格";
    }

    private List <FlightInfo> getFlightInfo(CloseableHttpClient httpClient, RequestConfig config, HttpClientContext context, CrawlParam param, String serviceId, String flightNo, boolean isVerify, String verifyCode, String cookie)
            throws IOException {
        CloseableHttpResponse response = null;
        String session = UUID.randomUUID().toString();
        list = new ArrayList();
        String dep = param.getDeparture();
        String arr = param.getArrival();
        String depdate = param.getDeparuteDate();

        HttpHost host = new HttpHost("www.fuzhou-air.cn", 80, "http");
        HttpGet get = new HttpGet("http://www.fuzhou-air.cn/");


        RequestConfig IndexConfig = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000).setConnectionRequestTimeout(50000).setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();
        get.setConfig(IndexConfig);
        get.setHeader("Host", "www.fuzhou-air.cn");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
        response = httpClient.execute(host, get);
        logger.info("FU进入首页response:" + response);
        response.close();

        String indexCookie = "";
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if ("Set-Cookie".equals(header.getName())) {
                indexCookie = indexCookie + header.getValue() + ";";
            }
        }
        if ((!cookie.contains("ci")) && (!cookie.contains("pa"))) {
            cookie = indexCookie;
        }
        try {
            HttpPost post = new HttpPost("/frontend/api/flight.action");
            post.setConfig(config);


            JSONObject queryObject = new JSONObject();
            queryObject.put("index", "0");
            queryObject.put("orgCity", dep);
            queryObject.put("dstCity", arr);
            queryObject.put("flightdate", depdate);

            queryObject.put("tripType", "ONEWAY");
            queryObject.put("times", "3800810593");
            queryObject.put("desc", "coBPtm4BZy5Ly7E1arnlj0GidvXwFa0v1dqnLAZibE1NpTzA99vSC6y0jxyoBdtf");
            if (isVerify) {
                queryObject.put("vc", verifyCode);
            }
            StringEntity entity = new StringEntity(queryObject.toString(), Charset.forName("UTF-8"));
            StringBuilder sbu = new StringBuilder();
            String[] c = cookie.split(";");
            String c1;
            for (c1:
                 c) {
                if ((!c1.contains("Path")) && (!c1.contains("HttpOnly")) && (!c1.contains("Domain"))) {
                    sbu.append(c1).append(";");
                }
            }
            cookie = sbu.toString();
            post.setEntity(entity);

            String dlyAccount = PropertiesUtils.getPropertiesValue("config", "dlyAccount");
            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            post.setHeader("Content-Type", "application/text; charset=UTF-8");
            post.setHeader("Cookie", cookie);
            post.setHeader("Host", "www.fuzhou-air.cn");
            post.setHeader("Origin", "http://www.fuzhou-air.cn");
            post.setHeader("Referer", "http://www.fuzhou-air.cn/b2c/static/flightSearch.html?orgCityCode=FOC&dstCityCode=ZUH&orgDate=2019-04-25&dstDate=&adult=1&child=0&infant=0&trip=ONEWAY");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
            if (cookie.contains("pa")) {
                response.close();
                response = httpClient.execute(host, post);
            } else {
                response.close();
                response = httpClient.execute(host, post, context);
            }
            Header[] headersArr = response.getAllHeaders();
            String newCookie1 = "";
            Header header;
            for (header:
                 headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    newCookie1 = newCookie1 + header.getValue() + ";";
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
                    logger.info("FU没有查询到相关航班信息：" + back);
                } else {
                    logger.info("FU查询到的航班信息：" + back);
                    list = doCrawl(back, param, flightNo);
                    return list;
                }
            }
            if (backJson.size() == 1) {
                Object newCookie;
                Object fzyAccount;
                if ("10000".equals(backJson.get("sta").toString())) {
                    config = RequestConfig.custom().setConnectTimeout(50000).setConnectionRequestTimeout(50000).setSocketTimeout(50000).setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();
                    if (cookie.contains("ci")) {
                        String ip_port = DailiyunService.getRandomIp(50);
                        proxyIp = ip_port.split(":")[0];
                        proxyPort = Integer.parseInt(ip_port.split(":")[1]);
                        dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");


                        config = RequestConfig.custom().setConnectTimeout(50000).setConnectionRequestTimeout(50000).setSocketTimeout(50000).setProxy(dailiyunProxy).setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();
                        return getFlightInfo(httpClient, config, context, param, serviceId, flightNo, false, "", "");
                    }
                    get = new HttpGet("/hnatravel/imagecodefu?code=0.15237324653420825");
                    get.setConfig(config);
                    get.setHeader("Host", "www.fuzhou-air.cn");
                    get.setHeader("Cookie", cookie);
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
                    response = httpClient.execute(host, get);

                    headersArr = response.getAllHeaders();
                    newCookie = "";
                    String proxyIp = headersArr;
                    int proxyPort = proxyIp.length;
                    for (HttpHost dailiyunProxy = 0; dailiyunProxy < proxyPort; dailiyunProxy++) {
                        Header header = proxyIp[dailiyunProxy];
                        if ("Set-Cookie".equals(header.getName())) {
                            newCookie = (String) newCookie + header.getValue() + ";";
                        }
                    }
                    if (((String) newCookie).contains("JSESSIONID")) {
                        cookie = cookie + (String) newCookie;
                    }
                    if ((!StringUtils.isEmpty((CharSequence) newCookie)) && (!((String) newCookie).contains("JSESSIONID"))) {
                        sbu = new StringBuilder();
                        c = cookie.split(";");
                        proxyIp = c;
                        proxyPort = proxyIp.length;
                        for (dailiyunProxy = 0; dailiyunProxy < proxyPort; dailiyunProxy++) {
                            String c1 = proxyIp[dailiyunProxy];
                            if (c1.contains("ci")) {
                                sbu.append((String) newCookie).append(";");
                            } else {
                                sbu.append(c1).append(";");
                            }
                        }
                        cookie = sbu.toString();
                        logger.info("当前cookie:" + cookie);
                    }
                    String fileUri = "C://testImg//" + session + ".jpg";
                    InputStream is = response.getEntity().getContent();
                    OutputStream os = new FileOutputStream(fileUri);
                    IOUtils.copy(is, os);
                    try {
                        os.close();
                    } catch (IOException e) {
                        logger.error("error", e);
                    }
                    is = new FileInputStream(new File(fileUri));
                    fzyAccount = PropertiesUtils.getPropertiesValue("config", "fzybian");
                    String[] fzyAccountInfo = ((String) fzyAccount).split(":");
                    String validtext = YunSu.getValidCode(is, "3000", fzyAccountInfo[0], fzyAccountInfo[1]);
                    Document document = null;
                    try {
                        document = DocumentHelper.parseText(validtext);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    Element root = document.getRootElement();
                    String error = root.elementText("Error");
                    String result = "";
                    if ((error != null) && (!"".equals(error))) {
                        result = error;
                        logger.error("error" + result);
                    } else {
                        result = root.elementText("Result");
                    }
                    File file = new File(fileUri);
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        is.close();
                    } catch (IOException e) {
                        logger.error("error", e);
                    }
                    logger.info("FU识别查询航班验证码:" + result);
                    response.close();
                    return getFlightInfo(httpClient, config, context, param, serviceId, flightNo, true, result, cookie);
                }
                if ("10002".equals(backJson.get("sta").toString())) {
                    config = RequestConfig.custom().setConnectTimeout(50000).setConnectionRequestTimeout(50000).setSocketTimeout(50000).setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();

                    return getFlightInfo(httpClient, config, context, param, serviceId, flightNo, false, "", cookie);
                }
                if ("10001".equals(backJson.get("sta").toString())) {
                    logger.info("当前ip已被封,换ip重试中");

                    String ip_port = DailiyunService.getRandomIp(50);
                    String proxyIp = ip_port.split(":")[0];
                    int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
                    HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");


                    config = RequestConfig.custom().setConnectTimeout(50000).setConnectionRequestTimeout(50000).setSocketTimeout(50000).setProxy(dailiyunProxy).setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();
                    return getFlightInfo(httpClient, config, context, param, serviceId, flightNo, false, "", "");
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception localException14) {
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception localException15) {
                }
            }
        }
    }

    private List <FlightInfo> doCrawl(String content, CrawlParam param, String flightNo) {
        List <FlightInfo> resList = new ArrayList();
        List <CabinInfo> CabinInfoList = new ArrayList();
        try {
            if (StringUtils.isNotEmpty(content)) {
                FlightInfo flightInfo = new FlightInfo();
                flightInfo.setFlightNo(flightNo);
                flightInfo.setDeparutre(param.getDeparture());
                flightInfo.setArrival(param.getArrival());
                flightInfo.setDeparutreDate(param.getDeparuteDate());
                flightInfo.setWebType(Constant.FU.toString());
                flightInfo.setUpdateTime(new Date());

                JSONObject jsonObject = JSON.parseObject(content);
                FUFlightsInfo fuFlightsInfo = (FUFlightsInfo) jsonObject.toJavaObject(FUFlightsInfo.class);
                List <FUFlightsInfo.DataBean.SegmentsBean> segmentsBeans = fuFlightsInfo.getData().getSegments();
                for (int i = 0; i < segmentsBeans.size(); i++) {
                    String fno = ((FUFlightsInfo.DataBean.SegmentsBean) segmentsBeans.get(i)).getFlightno();
                    if (flightNo.contains(fno)) {
                        for (int j = 0; j < ((FUFlightsInfo.DataBean.SegmentsBean) segmentsBeans.get(i)).getProducts().size(); j++) {
                            if (!"放心飞".equals(((FUFlightsInfo.DataBean.SegmentsBean.ProductsBean) ((FUFlightsInfo.DataBean.SegmentsBean) segmentsBeans.get(i)).getProducts().get(j)).getCabin().getSignProductName())) {
                                String adultFare = ((FUFlightsInfo.DataBean.SegmentsBean.ProductsBean) ((FUFlightsInfo.DataBean.SegmentsBean) segmentsBeans.get(i)).getProducts().get(j)).getCabin().getAdultFare();
                                String cabinCode = ((FUFlightsInfo.DataBean.SegmentsBean.ProductsBean) ((FUFlightsInfo.DataBean.SegmentsBean) segmentsBeans.get(i)).getProducts().get(j)).getCabin().getCabinCode();
                                String inventory = ((FUFlightsInfo.DataBean.SegmentsBean.ProductsBean) ((FUFlightsInfo.DataBean.SegmentsBean) segmentsBeans.get(i)).getProducts().get(j)).getCabin().getInventory();
                                String signProductName = ((FUFlightsInfo.DataBean.SegmentsBean.ProductsBean) ((FUFlightsInfo.DataBean.SegmentsBean) segmentsBeans.get(i)).getProducts().get(j)).getCabin().getSignProductName();

                                CabinInfo cabinInfo = new CabinInfo();
                                cabinInfo.setBaseCabin(signProductName);
                                cabinInfo.setCabinCode(cabinCode);
                                cabinInfo.setPrice(adultFare);
                                cabinInfo.setLastSeat(inventory);
                                CabinInfoList.add(cabinInfo);
                            }
                        }
                        flightInfo.setCabins(CabinInfoList);
                        resList.add(flightInfo);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("FU解析航班数据出错", e);
        }
        return resList;
    }
}
*/
