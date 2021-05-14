package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.feeye.bean.OrderJson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 创建南航订单
 * @date: 2019/03/06 11:13
 */
public class CZCreateOrderServiceOld {
    private static final Logger log = LoggerFactory.getLogger(CZCreateOrderService.class);
    private static final int timeout = 40000;
    //南航账户
    private static Map<String, String> accountCZ = new HashMap<String, String>();

    //登录
    public String login(OrderJson orderJson) {
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
            post.setHeader("Host", "b2c.csair.com");
            post.setHeader("Referer", "http://b2c.csair.com/B2C40/modules/bookingnew/manage/login.html?returnurl=http%3A%2F%2Fb2c.csair.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            CloseableHttpResponse response = getCloseableHttpClient().execute(host, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("登录返回的信息：" + back);
            log.info("登录返回的信息:" + back);
            //获取请求头中的cookie信息
            Header[] headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    if (header.getValue().contains(";")) {
                        String[] str = header.getValue().split(";");
                        for (String c : str) {
                            cookie += c + ";";
                        }
                    }
                }
            }
            log.info("获取的cookie" + cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cookie;
    }

    //查询是否已登录
    public String checkLogin(String cookie) {
        String newCookie = "";
        String backStr = "";
        try {
            //请求主机
            HttpHost host = new HttpHost("b2c.csair.com", 443, "https");
            HttpPost post = new HttpPost("/portal/user/checkLogin");
            post.setHeader("Host", "b2c.csair.com");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            post.setHeader("Cookie", cookie);
            CloseableHttpResponse httpResponse = getCloseableHttpClient().execute(host, post);
            //获取请求头中的cookie信息
            Header[] headersArr = httpResponse.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    if (header.getValue().contains(";")) {
                        String[] str = header.getValue().split(";");
                        for (String c : str) {
                            newCookie += c + ";";
                        }
                    }
                }
            }
            HttpEntity responseEnty = httpResponse.getEntity();
            backStr = EntityUtils.toString(responseEnty, "utf-8");
            if (StringUtils.isNotEmpty(backStr)) {
                System.out.println("checkLogin:" + backStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newCookie;
    }

    //查询航班信息
    private String queryFlights() {
        HttpPost httpPost = null;
        RequestConfig config = null;
        HttpHost target = null;
        String backStr = "";
        try {
            httpPost = new HttpPost("/portal/flight/direct/query");
            httpPost.setConfig(config);
            //请求主机
            target = new HttpHost("b2c.csair.com", 80, "http");
            String jsonObject = "{\"depCity\":\"CAN\",\"arrCity\":\"NKG\",\"flightDate\":\"20190415\",\"adultNum\":\"1\",\"childNum\":\"0\",\"infantNum\":\"0\"\n" +
                    ",\"cabinOrder\":\"0\",\"airLine\":1,\"flyType\":0,\"international\":\"0\",\"action\":\"0\",\"segType\":\"1\",\"cache\":0,\"preUrl\"\n" +
                    ":\"\",\"isMember\":\"\"}";
            StringEntity entity = new StringEntity(jsonObject, Charset.forName("UTF-8"));
            httpPost.setEntity(entity);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Host", "b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");

            HttpEntity responseEnty = getCloseableHttpClient().execute(target, httpPost).getEntity();
            backStr = EntityUtils.toString(responseEnty, "utf-8");
//            System.out.println("查询航班返回信息:" + backStr + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return backStr;
    }

    //获取httpClient对象
    private CloseableHttpClient getCloseableHttpClient() {
        CloseableHttpClient CloseableHttpClient = null;
        BasicCookieStore cookieStore = null;
        RequestConfig config = null;
        SSLConnectionSocketFactory sslsf = null;
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
        //代理主机
        HttpHost proxy = new HttpHost("http-dyn.abuyun.com", 9020, "http");
        BasicScheme proxyAuth = new BasicScheme();
        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(proxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        String proxyUser = "HL7F5JF125K85K8D";
        String proxyPass = "FC393F432489B2E5";
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            e1.printStackTrace();
        }
        HttpClientBuilder builder = null;
        builder = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(config).setDefaultCredentialsProvider(credsProvider);
        CloseableHttpClient = builder.build();

        return CloseableHttpClient;
    }

    //发送选择的航班信息,获取uuid
    private String postFlight() {
        HttpPost httpPost = null;
        RequestConfig config = null;
        HttpHost target = null;
        String backStr = "";
        try {
            httpPost = new HttpPost("/portal/passenger/flight/postFlight");
            httpPost.setConfig(config);
            //请求主机
            target = new HttpHost("b2c.csair.com", 80, "http");

            JSONObject jsonObject = this.getChoseFlightinfo(queryFlights());
            StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
            httpPost.setEntity(entity);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Host", "b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");

            HttpEntity responseEnty = getCloseableHttpClient().execute(target, httpPost).getEntity();
            backStr = EntityUtils.toString(responseEnty, "utf-8");
            System.out.println("返回的UUID:" + backStr + "\n");
            log.info("返回的UUD:" + backStr + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backStr;
    }

    //获取选择的航班信息
    private String getChoseFlight() {
        HttpPost httpPost = null;
        RequestConfig config = null;
        HttpHost target = null;
        String backStr = "";
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            //检查是否登录
//            map = checkLogin();
            httpPost = new HttpPost("/portal/passenger/flight/getFlight");
            httpPost.setConfig(config);
            //请求主机
            target = new HttpHost("b2c.csair.com", 80, "http");
            //获取请求参数 uuid
            String returnStr = postFlight();
            JSONObject jsonObject = JSONObject.fromObject(returnStr);
            String uuid = jsonObject.get("data").toString();
            //设置请求参数 uuid
            StringBuffer buffer = new StringBuffer();
            buffer.append("{\"uuid\":");
            buffer.append("\"" + uuid + "\"");
            buffer.append("}");
            StringEntity entity = new StringEntity(buffer.toString(), Charset.forName("UTF-8"));
            httpPost.setEntity(entity);

            String refererUrl = "https://b2c.csair.com/B2C40/newTrips/static/main/page/passengers/index.html?" + uuid;
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Host", "b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            httpPost.setHeader("Referer", refererUrl);
            httpPost.setHeader("Cookie", map.get("cookie").toString());

            HttpEntity responseEnty = getCloseableHttpClient().execute(target, httpPost).getEntity();
            backStr = EntityUtils.toString(responseEnty, "utf-8");
            System.out.println("选择航班返回信息:" + backStr);
            log.info("选择航班返回信息" + backStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backStr;
    }

    //保存乘机人信息
    public String svaeContactPersonsInfo() {
        HttpPost httpPost = null;
        RequestConfig config = null;
        HttpHost target = null;
        String backStr = "";
        try {
            Map<String, String> map = new HashMap<String, String>();
//            map = login();
            httpPost = new HttpPost("/portal/svc/commonContacts/saveSvcEContactPersonsInfo");
            httpPost.setConfig(config);
            //请求主机
            target = new HttpHost("b2c.csair.com", 80, "http");
            String jsonObject = "{\"contactName\":\"夏申\",\"mobilePhone\":\"\",\"email\":\"\"}";
            StringEntity entity = new StringEntity(jsonObject, Charset.forName("UTF-8"));
            httpPost.setEntity(entity);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Host", "b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            httpPost.setHeader("Cookie", map.get("cookie"));

            HttpEntity responseEnty = getCloseableHttpClient().execute(target, httpPost).getEntity();
            backStr = EntityUtils.toString(responseEnty, "utf-8");
            System.out.println("保存乘机人返回信息:" + backStr);
            log.info("保存乘机人返回信息" + backStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backStr;
    }

    /****************************************************json解析方法****************************************************/

    //解析返回的字符串
    public JSONObject getChoseFlightinfo(String back) {
        JSONObject choseFlightInfoObject = new JSONObject();
        JSONObject segments = new JSONObject();
        if (StringUtils.isNotEmpty(back)) {
            JSONObject backObj = JSONObject.fromObject(back);
            //航班数组
            JSONObject flightObject = backObj.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONArray("flight").getJSONObject(0); // TODO 遍历
//            flightObject.remove("cabin");
            Object o = backObj.getJSONObject("data").getJSONArray("airports").getJSONObject(0).get("zhName");
            Object o1 = backObj.getJSONObject("data").getJSONArray("airports").getJSONObject(1).get("zhName");
            //depAp
            flightObject.element("depAp", backObj.getJSONObject("data").getJSONArray("airports").getJSONObject(0).get("zhName"));
            //arrAP
            flightObject.element("arrap", backObj.getJSONObject("data").getJSONArray("airports").getJSONObject(1).get("zhName"));
            //depCity
            flightObject.element("depCity", backObj.getJSONObject("data").getJSONArray("citys").getJSONObject(1).get("zhName"));
            //arrCity
            flightObject.element("arrCity", backObj.getJSONObject("data").getJSONArray("citys").getJSONObject(0).get("zhName"));
            //depterm
            flightObject.element("depterm", flightObject.get("departureTerminal"));
            //arrterm
            flightObject.element("arrterm", flightObject.get("arrivalTerminal"));
            //addday
            flightObject.element("addday", 0);
            //langcode
            flightObject.element("langcode", "zh");
            //airlineCode
            flightObject.element("airlineCode", "CZ");
            //planeName
            flightObject.element("planeName", "");
            //planeType
            flightObject.element("planeType", "中型");
            //flytype
            flightObject.element("flytype", 0);
            //id
            flightObject.element("id", backObj.getJSONObject("data").get("id"));
            //tripIndex
            flightObject.element("tripIndex", 0);
            //classtype
            flightObject.element("classtype", "odd");
            //count
            flightObject.element("count", 1);

            //fgprice
            JSONObject fgpriceObj = new JSONObject();
            fgpriceObj.element("F", backObj.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONObject("fbasic").get("adultPrice"));
            fgpriceObj.element("J", backObj.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONObject("jbasic").get("adultPrice"));
            fgpriceObj.element("W", backObj.getJSONObject("data").getJSONArray("segment").getJSONObject(0).getJSONObject("dateFlight").getJSONObject("wbasic").get("adultPrice"));
            flightObject.put("fgprice", fgpriceObj);

            //cabinArray
            JSONArray cabinArray = new JSONArray();
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

            cabinArray = flightObject.getJSONArray("cabin");
            //遍历 cabinArray
            for (int i = 0; i < cabinArray.size(); i++) {
                JSONObject cabinArrayJSONObject = cabinArray.getJSONObject(i);
                //TODO 添加对应参数判断
                //fareReference 拼接
                cabinArrayJSONObject.get("fareReference").toString();
                cabinArrayJSONObject.get("childFareReference").toString();
                cabinArrayJSONObject.get("infantFareReference").toString();

                //fareBasis
                cabinArrayJSONObject.get("adultFareBasis").toString();
                cabinArrayJSONObject.get("childFareBasis").toString();
                cabinArrayJSONObject.get("infantFareBasis").toString();

                ppriceObject.element(cabinArrayJSONObject.get("name").toString(), cabinArrayJSONObject.get("adultPrice"));
                pinfoObject.element(cabinArrayJSONObject.get("name").toString(), cabinArrayJSONObject.get("info"));

//                priceArray.set(0,cabinArrayJSONObject.get("gbAdultPrice"));
//                priceArray.set(1,cabinArrayJSONObject.get("gbChildPrice"));
//                priceArray.set(2,cabinArrayJSONObject.get("gbInfantPrice"));
//
//                fareBasisArray.set(0,cabinArrayJSONObject.get("adultFareBasis"));
//                fareBasisArray.set(1,cabinArrayJSONObject.get("childFareBasis"));
//                fareBasisArray.set(2,cabinArrayJSONObject.get("infantFareBasis"));
//
//                fareReferenceArray.set(0,cabinArrayJSONObject.get("fareReference"));
//                fareReferenceArray.set(1,cabinArrayJSONObject.get("childFareReference"));
//                fareReferenceArray.set(2,cabinArrayJSONObject.get("infantFareReference"));

                String discount = cabinArrayJSONObject.get("discount").toString();
                String gbAdultPrice = cabinArrayJSONObject.get("adultPrice").toString();

                //退费
                for (int j = 0; j < cabinArrayJSONObject.getJSONArray("refund_ad").size(); j++) {
                    JSONObject refundArrayObj = new JSONObject();
                    String refundCharge = cabinArrayJSONObject.getJSONArray("refund_ad").getJSONObject(j).get("refundCharge").toString();
                    refundArrayObj.element("text", refundCharge);
                    refundArrayObj.element("title", "起飞前2小时之后");
                    refundArray.add(refundArrayObj);
                }
                for (int j = 0; j < cabinArrayJSONObject.getJSONArray("refund_ch").size(); j++) {
                    JSONObject refundArrayObj = new JSONObject();
                    String refundCharge = cabinArrayJSONObject.getJSONArray("refund_ch").getJSONObject(j).get("refundCharge").toString();
                    refundArrayObj.element("text", refundCharge);
                    refundArrayObj.element("title", "起飞前2小时之后");
                    ch_refundArray.add(refundArrayObj);
                }

                //改签
                for (int j = 0; j < cabinArrayJSONObject.getJSONArray("reissue_ad").size(); j++) {
                    JSONObject reissueObj = new JSONObject();
                    String reissueCharge = cabinArrayJSONObject.getJSONArray("reissue_ad").getJSONObject(j).get("reissueCharge").toString();
                    reissueObj.element("text", reissueCharge);
                    reissueObj.element("title", "起飞前2小时之后");
                    changeArray.add(reissueObj);
                }
                for (int j = 0; j < cabinArrayJSONObject.getJSONArray("reissue_ch").size(); j++) {
                    JSONObject reissueObj = new JSONObject();
                    String reissueCharge = cabinArrayJSONObject.getJSONArray("reissue_ch").getJSONObject(j).get("reissueCharge").toString();
                    reissueObj.element("text", reissueCharge);
                    reissueObj.element("title", "起飞前2小时之后");
                    ch_changeArray.add(reissueObj);
                }
            }

            //pprice
            flightObject.put("pprice", ppriceObject);
            //pinfo
            flightObject.put("pinfo", pinfoObject);
            //code
            flightObject.put("code", "E");
            //cabinInfo
            //TODO 赋值
            JSONArray cabinInfoArray = new JSONArray();
            flightObject.put("cabinInfo", cabinInfoArray);
            //price
            flightObject.put("price", priceArray);
            //typeName
            flightObject.put("typeName", "快乐飞");
            //discount
            flightObject.put("discount", "55");
            //gbAdultPrice
            flightObject.put("gbAdultPrice", "1890");
            //fareBasis
            flightObject.put("fareBasis", fareBasisArray);
            //fareReference
            flightObject.put("fareReference", fareReferenceArray);
            //changeAndRefund
            changeAndRefundObject.put("change", changeArray);
            changeAndRefundObject.put("refund", refundArray);
            changeAndRefundObject.put("ch_change", ch_changeArray);
            changeAndRefundObject.put("ch_refund", ch_changeArray);
            changeAndRefundObject.put("in_change", in_changeArray.add(new JSONObject().element("text", "免费变更")));
            changeAndRefundObject.put("in_refund", in_refundArray.add(new JSONObject().element("text", "全部未使用，免退票手续费")));
            changeAndRefundObject.put("sign", "不得转签");
            changeAndRefundObject.put("isold", false);
            changeAndRefundObject.put("ticket", "退改 ¥567 起");
            changeAndRefundObject.put("solbool", false);
            changeAndRefundObject.put("changeMessageAdZh", "票面价30%, 票面价50%");
            changeAndRefundObject.put("refundMessageAdZh", "票面价50%, 不得退票");
            changeAndRefundObject.put("changeMessageChZh", "免费, 票面价5%");
            changeAndRefundObject.put("refundMessageChZh", "票面价5%, 票面价15%");
            flightObject.put("changeAndRefund", changeAndRefundObject);
            //showInfo
            flightObject.put("showInfo", "");
            //info
            flightObject.put("info", ">9"); //TODO
            //memcabin
            flightObject.put("memcabin", false);
            //redpur
            flightObject.put("redpur", "");
            //issolbool
            flightObject.put("issolbool", false);

            segments.put("firstflight", flightObject);
            segments.put("segmenttype", 0);
            segments.put("sandsflag", 0);
            //fuel
            JSONArray fuelArray = new JSONArray();
            for (int i = 0; i < 3; i++) {
                fuelArray.add(0);
            }
            segments.put("fuel", fuelArray);
            segments.put("tax", 50);
            segments.put("redpur", "");
            choseFlightInfoObject.put("segments", segments);

            choseFlightInfoObject.put("segtype", "s");
            choseFlightInfoObject.put("adult", 1);
            choseFlightInfoObject.put("child", 0);
            choseFlightInfoObject.put("infant", 0);
            choseFlightInfoObject.put("isMember", false);
            choseFlightInfoObject.put("ispreUrlsol", 0);

            JSONObject paramObject = new JSONObject();
            paramObject.put("it", "0");
            paramObject.put("ct", "0");
            paramObject.put("at", "1");
            paramObject.put("d1", "2019-03-09");
            paramObject.put("c2", "HRB");
            paramObject.put("c1", "CAN-HRB");
            paramObject.put("t", "S");
            paramObject.put("n1", "广州-哈尔滨");
            paramObject.put("isfirst", true);
            paramObject.put("leftStatus", true);
            paramObject.put("rightStatus", false);
            paramObject.put("isBindCardbycabin", new JSONArray());
            choseFlightInfoObject.put("param", paramObject);
        }
        return choseFlightInfoObject;
    }


    public static void main(String[] args) {
        String orderJsonStr = "{\n" +
                "    \"account\": \"15897736493_044177\",\n" +
                "    \"airline\": \"CZ\",\n" +
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
                "            \"departure\": \"CAN\",\n" +
                "            \"departureDate\": \"2019-04-25\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"CZ6262\",\n" +
                "            \"price\": \"3440\"\n" +
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
                "        {\n" +
                "            \"birthday\": \"1975-11-29 00:00:00+08:00\",\n" +
                "            \"id\": \"37617005\",\n" +
                "            \"idcard\": \"532225197511291117\",\n" +
                "            \"passengerCardType\": \"身份证\",\n" +
                "            \"passengerName\": \"尹建飞\",\n" +
                "            \"passengerSex\": \"男\",\n" +
                "            \"passengerType\": \"成人\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"birthday\": \"1983-11-21 00:00:00+08:00\",\n" +
                "            \"id\": \"37617004\",\n" +
                "            \"idcard\": \"350781198311212039\",\n" +
                "            \"passengerCardType\": \"身份证\",\n" +
                "            \"passengerName\": \"梁邵平\",\n" +
                "            \"passengerSex\": \"男\",\n" +
                "            \"passengerType\": \"成人\"\n" +
                "        },\n" +
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
        CZCreateOrderServiceOld object = new CZCreateOrderServiceOld();
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(orderJsonStr);
        OrderJson orderJson = jsonObject.toJavaObject(OrderJson.class);
        object.checkLogin(object.login(orderJson));
    }
}

