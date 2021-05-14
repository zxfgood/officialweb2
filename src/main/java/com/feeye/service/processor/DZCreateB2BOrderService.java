package com.feeye.service.processor;

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
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import java.util.*;

public class DZCreateB2BOrderService {
    private static final int timeout = 120000;

    public static void main(String[] args) {
        String back = "{\r\n" +
                "    \"account\": \"123_34213\",\r\n" +
                "    \"airline\": \"DZ\",\r\n" +
                "    \"bigpnr\": \"SADWER\",\r\n" +
                "    \"billNo\": \"FY190226114235155115255528332490\",\r\n" +
                "    \"childrenMobile\": \"\",\r\n" +
                "    \"code\": \"\",\r\n" +
                "    \"codePassword\": \"\",\r\n" +
                "    \"drawerType\": \"singleOfficial\",\r\n" +
                "    \"email\": \"\",\r\n" +
                "    \"flights\": [\r\n" +
                "        {\r\n" +
                "            \"airline\": \"DZ\",\r\n" +
                "            \"arrival\": \"NAY\",\r\n" +
                "            \"cabin\": \"Q\",\r\n" +
                "            \"departure\": \"SZX\",\r\n" +
                "            \"departureDate\": \"2019-02-25\",\r\n" +
                "            \"fType\": \"go\",\r\n" +
                "            \"flightNo\": \"DZ1234\",\r\n" +
                "            \"price\": \"870\"\r\n" +
                "        }\r\n" +
                "    ],\r\n" +
                "    \"id\": \"40430\",\r\n" +
                "    \"ifUsedCoupon\": false,\r\n" +
                "    \"isOutticket\": \"true\",\r\n" +
                "    \"linkMan\": \"213\",\r\n" +
                "    \"matchCabin\": false,\r\n" +
                "    \"mobile\": \"12312\",\r\n" +
                "    \"orderNo\": \"1551078764086\",\r\n" +
                "    \"orderTime\": \"2019-02-26 11:42:31\",\r\n" +
                "    \"otheraccount\": \"\",\r\n" +
                "    \"passengers\": [\r\n" +
                "        {\r\n" +
                "            \"id\": \"51410\",\r\n" +
                "            \"passengerName\": \"CES\",\r\n" +
                "            \"passengerType\": \"成人\"\r\n" +
                "        }\r\n" +
                "    ],\r\n" +
                "    \"payType\": \"yfk\",\r\n" +
                "    \"qiangpiao\": \"2\",\r\n" +
                "    \"username\": \"policytest\",\r\n" +
                "    \"ytype\": \"\"\r\n" +
                "}\r\n" +
                "";
        new DZCreateB2BOrderService().StartCreateOrder(back, 0);
    }

    public String StartCreateOrder(String orderJson, int retryCount) {
        if (StringUtils.isEmpty(orderJson)) {
            return "ERROR:数据不完整";
        }
        System.out.println("传过来的数据有" + orderJson);
        String session = UUID.randomUUID().toString();
        // 登陆账号
        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();// 一个cookies
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // 初始化SSL连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHost proxy = new HttpHost("http-dyn.abuyun.com", 9020, "http");
        org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(proxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        String proxyUser = "HL7F5JF125K85K8D";
        String proxyPass = "FC393F432489B2E5";
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
        }

        int timeout = 40000;
        RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout)
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setProxy(proxy)
                .setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();
        HttpClientBuilder builder = null;
        builder = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(config).setDefaultCredentialsProvider(credsProvider);
        CloseableHttpClient httpclient = builder.build();
        String cookie = "";
        try {
            JSONObject json = new JSONObject(orderJson);
            String bigpnr = json.getString("bigpnr");
            cookie = login(httpclient, config, cookieStore, cookie, orderJson, session);
            if (StringUtils.isEmpty(cookie)) {
                if (retryCount < 5) {
                    retryCount++;
                    return StartCreateOrder(orderJson, retryCount);
                } else {
                    return null;
                }
            }
//			searchFlights(httpclient, config, cookie, orderJson);
//				System.out.println("登录后的cookie:"+cookie);
            Map<String, String> PnrQueryParamMap = null;
            int index = 0;
            for (; index < 5; index++) {
                PnrQueryParamMap = PNRQuery(httpclient, config, cookie, orderJson);
                if (PnrQueryParamMap == null || PnrQueryParamMap.size() == 0) {
                    continue;
                }
                break;
            }
            if (index == 5) {
                return "";
            }
            String order_pnr_id = "";
            String orderno = "";
            Map<String, String> PnrHostinParamMap = PnrHostin(httpclient, config, cookie, orderJson, PnrQueryParamMap);
            if (PnrHostinParamMap == null) {
                PnrHostinParamMap = new HashMap<String, String>();
                Map<String, String> frontOrderQueryMap = frontOrderQuery(httpclient, config, cookie, orderJson);
                String result = frontOrderDetailQuery(httpclient, config, cookie, orderJson, frontOrderQueryMap);
                Document doc = Jsoup.parse(result);
                order_pnr_id = doc.getElementsByClass("order_pnr_id").get(0).val();
                orderno = frontOrderQueryMap.get("recNo");
                String payOrders = orderno + "|" + bigpnr + "-" + order_pnr_id + "^";
                PnrHostinParamMap.put("payOrders", payOrders);
                PnrHostinParamMap.put("payMethod", "1");
                PnrHostinParamMap.put("etdzType", "1");
                PnrHostinParamMap.put("paymethodradio1", "1");
                PnrHostinParamMap.put("vpPasswd", "b2b456789@");
                PnrHostinParamMap.put("weblinkPswd", "");
                PnrHostinParamMap.put("cdpvpaypswd", "");

            }
            String orderInfo = virtualPay(httpclient, config, cookie, orderJson, PnrHostinParamMap);
            if (orderInfo.contains("操作失败")) {
                orderTicketOut(httpclient, config, cookie, orderJson, bigpnr, orderno, order_pnr_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void orderTicketOut(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson,
                                String bigpnr, String orderno, String order_pnr_id) {
        try {
            HttpPost post = new HttpPost("/dzb2b/OrderTicketOut.do");
            HttpHost target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("recNo", orderno));
            nameValue.add(new BasicNameValuePair("bpnrNo", bigpnr));
            nameValue.add(new BasicNameValuePair("orderPnrId", order_pnr_id));
            post.setConfig(config);
            System.out.println("手动出票请求参数:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "dzb2b.travelsky.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Origin", "https://dzb2b.travelsky.com");
            post.setHeader("Referer", "https://dzb2b.travelsky.com/dzb2b/PnrHostin.do");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("手动出票返回:" + back);
            Document document = Jsoup.parse(back);
            Elements trs = document.getElementsByClass("tableinfo1").get(0).getElementsByTag("tr");
            for (int i = 1; i < trs.size(); i++) {
                String passengerName = trs.get(i).getElementsByTag("td").get(0).text();
                String passengerInfo = trs.get(i).getElementsByTag("td").get(2).text();
                String ticketNo = trs.get(i).getElementsByTag("td").get(4).text();
                System.out.println("乘客：" + passengerName + "-" + passengerInfo + ",票号：" + ticketNo);
            }
            String recNo = document.getElementsByAttributeValue("name", "recNo").val();
            String pnrNo = document.getElementsByAttributeValue("name", "pnrNo").val();
            System.out.println("订单号：" + recNo + ",编码：" + pnrNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String frontOrderDetailQuery(CloseableHttpClient httpclient, RequestConfig config,
                                         String cookie, String orderJson, Map<String, String> paramMap) {
        try {
            HttpPost post = new HttpPost("/dzb2b/FrontOrderDetailQuery.do");
            HttpHost target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setConfig(config);
            System.out.println("查询订单详情请求参数:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "dzb2b.travelsky.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Origin", "https://dzb2b.travelsky.com");
            post.setHeader("Referer", "https://dzb2b.travelsky.com/dzb2b/PnrHostin.do");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("查询订单详情返回:" + back);
            return back;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> frontOrderQuery(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                                String orderJson) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            JSONObject json = new JSONObject(orderJson);
            String bigPnr = json.getString("bigpnr");
            HttpPost post = new HttpPost("/dzb2b/FrontOrderQuery.do");
            HttpHost target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("dateBegin", ""));
            nameValue.add(new BasicNameValuePair("dateEnd", ""));
            nameValue.add(new BasicNameValuePair("isGrp", "0"));
            nameValue.add(new BasicNameValuePair("dateType", "0"));
            nameValue.add(new BasicNameValuePair("orderType", "A"));
            nameValue.add(new BasicNameValuePair("recNo", ""));
            nameValue.add(new BasicNameValuePair("orderStatus", "1"));
            nameValue.add(new BasicNameValuePair("departure", ""));
            nameValue.add(new BasicNameValuePair("arrival", ""));
            nameValue.add(new BasicNameValuePair("pnrNo", bigPnr));
            nameValue.add(new BasicNameValuePair("bankId", "A"));
            nameValue.add(new BasicNameValuePair("billNo", ""));
            nameValue.add(new BasicNameValuePair("tktNo", ""));
            nameValue.add(new BasicNameValuePair("passName", ""));
            nameValue.add(new BasicNameValuePair("bargainStatus", ""));
            nameValue.add(new BasicNameValuePair("userId", ""));
            nameValue.add(new BasicNameValuePair("searchArea", ""));
            nameValue.add(new BasicNameValuePair("queryType", "newquery"));
            nameValue.add(new BasicNameValuePair("begin", "1"));
            nameValue.add(new BasicNameValuePair("length", ""));
            post.setConfig(config);
            System.out.println("查询请求参数:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "dzb2b.travelsky.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Origin", "https://dzb2b.travelsky.com");
            post.setHeader("Referer", "https://dzb2b.travelsky.com/dzb2b/PnrHostin.do");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("查询返回:" + back);
            Document doc = Jsoup.parse(back);
            String recNo = doc.getElementsByAttributeValue("name", "showdetail").html();
            String userId = doc.getElementsByAttributeValue("value", recNo).attr("id");
            String queryType = doc.getElementById("queryType").val();
            resultMap.put("recNo", recNo);
            resultMap.put("userId", userId);
            resultMap.put("queryType", queryType);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String virtualPay(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson,
                              Map<String, String> paramMap) {
        try {
            HttpPost post = new HttpPost("/dzb2b/VirtualPay.do");
            HttpHost target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setConfig(config);
            System.out.println("支付请求参数:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "dzb2b.travelsky.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Origin", "https://dzb2b.travelsky.com");
            post.setHeader("Referer", "https://dzb2b.travelsky.com/dzb2b/PnrHostin.do");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("支付返回:" + back);

            if (back.contains("操作失败")) {
                String errorMsg = "操作失败！";
                Document document = Jsoup.parse(back);
                Elements fb = document.getElementsByClass("ts_m1_dis");
                for (Element fbEle : fb) {
                    errorMsg = errorMsg + fbEle.html() + " ";
                }
                return errorMsg;
            }
            Document document = Jsoup.parse(back);
            Elements trs = document.getElementsByClass("tableinfo1").get(0).getElementsByTag("tr");
            for (int i = 1; i < trs.size(); i++) {
                String passengerName = trs.get(i).getElementsByTag("td").get(0).text();
                String passengerInfo = trs.get(i).getElementsByTag("td").get(2).text();
                String ticketNo = trs.get(i).getElementsByTag("td").get(4).text();
                System.out.println("乘客：" + passengerName + "-" + passengerInfo + ",票号：" + ticketNo);
            }
            String recNo = document.getElementsByAttributeValue("name", "recNo").val();
            String pnrNo = document.getElementsByAttributeValue("name", "pnrNo").val();
            System.out.println("订单号：" + recNo + ",编码：" + pnrNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> PnrHostin(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson, Map<String, String> paramMap) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            HttpPost post = new HttpPost("/dzb2b/PnrHostin.do");
            HttpHost target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setConfig(config);
            System.out.println("PnrHostin请求参数:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "dzb2b.travelsky.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "https://dzb2b.travelsky.com/dzb2b/PNRQuery.do");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Origin", "https://dzb2b.travelsky.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("PnrHostin返回：" + back);
            Thread.sleep(2000);
            Document document = Jsoup.parse(back);
            resultMap.put("payOrders", document.getElementById("payOrders").val());
            resultMap.put("etdzType", "1");
            resultMap.put("payMethod", "1");
            resultMap.put("vpPasswd", "b2b456789@");
            resultMap.put("cdpvpaypswd", "");
            resultMap.put("payOperType", document.getElementById("payOperType").val());
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> PNRQuery(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                         String orderJson) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            HttpGet get = new HttpGet("/dzb2b/PnrImportIndex.do");
            get.setConfig(config);
            HttpHost target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            get.setHeader("Host", "dzb2b.travelsky.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            get.setHeader("Cookie", cookie);
            CloseableHttpResponse response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("首页返回：" + back);
            Thread.sleep(2000);
            HttpPost post = new HttpPost("/dzb2b/PNRQuery.do");
            post.setConfig(config);
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("BPnrNo", "PB49HV"));
            nameValue.add(new BasicNameValuePair("discountCode", ""));
            nameValue.add(new BasicNameValuePair("interFit", ""));
            nameValue.add(new BasicNameValuePair("orderType", "0"));
            nameValue.add(new BasicNameValuePair("vipCode", ""));
            System.out.println("pnr查询请求参数：" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "dzb2b.travelsky.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "https://dzb2b.travelsky.com/dzb2b/PnrImportIndex.do");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(target, post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("pnr查询返回：" + back);
            Thread.sleep(2000);
            Document document = Jsoup.parse(back);
            resultMap.put("adusign", document.getElementById("adu").val());
            resultMap.put("chdsign", document.getElementById("chd").val());
            resultMap.put("infsign", document.getElementById("inf").val());
            resultMap.put("linkerMobile", document.getElementById("linkerMobile").val());
            resultMap.put("priceData", document.getElementById("priceData").val());
            resultMap.put("subsystem", document.getElementById("subsystem").val());
            resultMap.put("rand4page", document.getElementById("rand4page").val());
            resultMap.put("sellerCode", document.getElementById("sellerCode").val());
            resultMap.put("insPayAmont", document.getElementById("insPayAmont").val());
            resultMap.put("insAgentAmont", document.getElementById("insAgentAmont").val());
            resultMap.put("tourCode", document.getElementById("tourCode").val());
            resultMap.put("randomNum", document.getElementById("rand4page").val());
            resultMap.put("vipCode", document.getElementsByAttributeValue("name", "vipCode").val());
            resultMap.put("BPnrNo", document.getElementsByAttributeValue("name", "BPnrNo").val());
            String adultPrice = document.getElementById("adradio").attr("adultPrice");
            //比较运价
//			if(Float.parseFloat(adultPrice)!=Float.parseFloat(price)) {
//				return null;
//			}
            System.out.println("adultPrice:" + adultPrice);
            post = new HttpPost("/dzb2b/DomesticPnrPriceCompute.do");
            nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("adult", "0"));
            nameValue.add(new BasicNameValuePair("child", "-1"));
            nameValue.add(new BasicNameValuePair("doajax", "yes"));
            nameValue.add(new BasicNameValuePair("gm", "-1"));
            nameValue.add(new BasicNameValuePair("infant", "-1"));
            nameValue.add(new BasicNameValuePair("jc", "-1"));
            nameValue.add(new BasicNameValuePair("randomNum", document.getElementById("rand4page").val()));
            nameValue.add(new BasicNameValuePair("vipCode", ""));
            System.out.println("价格动态比较请求参数：" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "dzb2b.travelsky.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "https://dzb2b.travelsky.com/dzb2b/PNRQuery.do");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            post.setConfig(config);
            response = httpclient.execute(target, post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("校验价格返回:" + back);
            Thread.sleep(2000);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void searchFlights(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson) {
        HttpGet get = null;
        HttpHost target = null;
        try {
            get = new HttpGet("/dzb2b/FrontUserQuery.do");
            get.setConfig(config);
            target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            get.setHeader("Host", "dzb2b.travelsky.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(back);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String login(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore,
                        String cookie, String orderJson, String session) {


        HttpGet get = null;
        HttpPost post = null;
        HttpHost target = null;
        String childrenUser = "";
        String order_id = "";
        InputStream re = null;

        CloseableHttpResponse response = null;
        try {
            get = new HttpGet("/dzb2b/cas/authenticate.do?redirectUrl=https://dzb2b.travelsky.com/dzb2b/ssoClientLogin.do");
            get.setConfig(config);
            target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            get.setHeader("Host", "dzb2b.travelsky.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(target, get);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer buf = new StringBuffer();

            if (null != listCookie && listCookie.size() > 0) {
                for (int i = 0; i < listCookie.size(); i++) {
                    buf.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                }
            }
            cookie = buf.toString();
            System.out.println(cookie);
            // 获取验证码请求
            get = new HttpGet(
                    "/dzb2b/VerificationCode.do");
            get.setConfig(config);
            target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            get.setHeader("Host", "dzb2b.travelsky.com");
            get.setHeader("Cookie", cookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(target, get);
            String fileUri = "C://testImg//" + session + ".jpg";
            re = response.getEntity().getContent();
            OutputStream os = new FileOutputStream(fileUri);
            IOUtils.copy(re, os);
            os.close();
            System.out.print("输入验证码");
            Scanner scan = new Scanner(System.in);
            String vrtifycode = scan.nextLine();
            System.out.println("验证码：" + vrtifycode);

            config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout).setRedirectsEnabled(true).build();
            post = new HttpPost("/dzb2b/cas/login.do");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("loginname", "cgo259001"));
            nameValue.add(new BasicNameValuePair("loginpass", "b2b456789@"));
            nameValue.add(new BasicNameValuePair("serviceURL", ""));
            nameValue.add(new BasicNameValuePair("vrtifycode", vrtifycode));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "dzb2b.travelsky.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "https://dzb2b.travelsky.com/dzb2b/cas/authenticate.do?redirectUrl=https://dzb2b.travelsky.com/dzb2b/ssoClientLogin.do");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(target, post);
            Header[] headersArr = response.getAllHeaders();
            String newCookie = "";
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    newCookie += header.getValue() + ";";
                }
            }
            System.out.println("重定向前新增的cookie:" + newCookie);
            if (!newCookie.contains("DZB2BCOOKIE")) {
                return null;
            }
            cookie += newCookie;

            String[] co = cookie.split("\\;");
            String cc = "";
            for (String c : co) {
                if (!c.contains("Path") && !c.contains("HttpOnly") && !c.contains("Expires") && !c.contains("X-LB")) {
                    cc = cc + c + ";";
                }
            }
            cookie = cc.replaceAll(" ", "").replaceAll("\\;\\;", "\\;");


            System.out.println("cookiee:" + cookie);
            Header[] location = response.getHeaders("Location");
            String locationValue = "";
            for (int i = 0; i < location.length; i++) {
                locationValue = location[i].getValue();
                System.out.println("Location:" + locationValue);
            }


            get = new HttpGet(locationValue);
            get.setConfig(config);
            target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            get.setHeader("Host", "dzb2b.travelsky.com");
            get.setHeader("Cookie", cookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(target, get);
            headersArr = response.getAllHeaders();
            newCookie = "";
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    newCookie += header.getValue() + ";";
                }
            }

            co = cookie.split("\\;");
            cc = "";
            for (String c : co) {
                if (!c.contains("JSESSIONID") && !c.contains("Path") && !c.contains("HttpOnly") && !c.contains("DZB2BCOOKIE_AGENT_LOGIN_LANGUAGE") && !c.contains("Expires") && !c.contains("X-LB")) {
                    cc = cc + c + ";";
                }
            }
            cookie = cc + newCookie;

            co = cookie.split("\\;");
            cc = "";
            for (String c : co) {
                if (!c.contains("Path") && !c.contains("HttpOnly") && !c.contains("Expires")) {
                    cc = cc + c + ";";
                }
            }

            cookie = (cc + "HttpOnly").replaceAll(" ", "").replaceAll("\\;\\;", "\\;");
            System.out.println("重定向后新增的cookie:" + cookie);


            return cookie;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (re != null) {
                try {
                    re.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;

    }
}
