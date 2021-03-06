//package com.feeye.service.processor;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.net.ssl.SSLContext;
//import javax.script.Invocable;
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//
//import org.apache.commons.codec.binary.Base64;
//import org.apache.http.Header;
//import org.apache.http.HttpHost;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.protocol.HttpClientContext;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLContextBuilder;
//import org.apache.http.conn.ssl.TrustStrategy;
//import org.apache.http.cookie.Cookie;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.BasicAuthCache;
//import org.apache.http.impl.client.BasicCookieStore;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//
//import com.alibaba.fastjson.JSON;
//import com.feeye.entity.AccountInfo;
//import com.feeye.entity.CabinInfo;
//import com.feeye.entity.FlightInfo;
//import com.feeye.entity.OrderInfo;
//import com.feeye.entity.PaxInfo;
//import com.feeye.handler.OutticketHandler;
//import com.feeye.handler.SqliteHander;
//import com.feeye.init.SysData;
//import com.feeye.page.frame.OrderFrame;
//import com.feeye.util.DateUtil;
//import com.feeye.util.InitUtil;
//import com.feeye.util.StringUtil;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//
///**
// * @description: This is a class!
// * @author: domcj
// * @date: 2019/01/24 11:09
// */
//public class KNAppOutticketService {
//
//    private static final Logger logger = Logger.getLogger(KNAppOutticketService.class);
//    private static Map<String, String> memberIdMap = Maps.newHashMap();
//    private static Map<String, String> contactIdMap = Maps.newHashMap();
//
//    public void getTicketNo(OrderInfo orderInfo, AccountInfo accountInfo) {
//        Map<String, Object> initPara = InitUtil.getInitPara();
//        if ("false".equals(initPara.get("result"))) {
//            logger.info(orderInfo.getOrderNo() + "??????????????????ip");
//            return;
//        }
//        String orderNo = orderInfo.getOrderNo();
//        HttpClientBuilder builder = (HttpClientBuilder) initPara.get("builder");
//        BasicAuthCache authCache = (BasicAuthCache) initPara.get("authCache");
//        CredentialsProvider credsProvider = (CredentialsProvider) initPara.get("credsProvider");
//        RequestConfig defaultRequestConfig = (RequestConfig) initPara.get("defaultRequestConfig");
//        CloseableHttpClient httpclient = builder.build();
//        CloseableHttpResponse response = null;
//        try {
//            int count = 40;
//            boolean back = false;
//            String cookie = accountInfo.getLoginState();
//            while (count > 0) {
//                logger.info(orderNo + "--??????????????????");
//                try {
//                    //???????????????
//                    if (StringUtil.isEmpty(cookie)) {
//                        cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, orderNo, false);
//                    }
//                    if (cookie == null) {
//                        logger.info(orderNo + "--????????????");
//                        break;
//                    }
//                    back = getDetail(httpclient, defaultRequestConfig, cookie, orderInfo);
//                } catch (Exception e) {
//                    logger.error("error", e);
//                    logger.info(orderNo + "????????????????????????");
//                    Thread.sleep(20 * 1000);
//                    --count;
//                    continue;
//                }
//                try {
//                    Thread.sleep(20 * 1000);
//                } catch (InterruptedException e) {
//                    logger.error("error", e);
//                }
//                --count;
//            }
//            if (count <= 0) {
//                return;
//            }
//        } catch (Exception e) {
//            logger.error("error", e);
//            logger.info(orderNo + "--????????????????????????");
//            return;
//        }
//    }
//
//    public String login(AccountInfo accountInfo) {
//        Map<String, Object> initPara = InitUtil.getInitPara();
//        if ("false".equals(initPara.get("result"))) {
//            logger.info(accountInfo.getAccount() + "??????????????????ip");
//            return null;
//        }
//        String orderNo = accountInfo.getAccount();
//        HttpClientBuilder builder = (HttpClientBuilder) initPara.get("builder");
//        BasicAuthCache authCache = (BasicAuthCache) initPara.get("authCache");
//        CredentialsProvider credsProvider = (CredentialsProvider) initPara.get("credsProvider");
//        RequestConfig defaultRequestConfig = (RequestConfig) initPara.get("defaultRequestConfig");
//        CloseableHttpClient httpclient = builder.build();
//        CloseableHttpResponse response = null;
//        logger.info(orderNo + "--????????????");
//        return getLoginState(httpclient, accountInfo, defaultRequestConfig, orderNo, true);
//    }
//
//    public void startAddPax(OrderInfo orderInfo, AccountInfo accountInfo) {
////		Set<String> strings = SysData.paxIdMap.keySet();
//        String memberKey = accountInfo.getAccount() + "_" + accountInfo.getPassword();
//        boolean success = false;
//        try {
//            Map<String, Object> initPara = InitUtil.getInitPara();
//            if ("false".equals(initPara.get("result"))) {
//                logger.info(orderInfo.getOrderNo() + "??????????????????ip");
//                return;
//            }
//            String orderNo = orderInfo.getOrderNo();
//            HttpClientBuilder builder = (HttpClientBuilder) initPara.get("builder");
//            BasicAuthCache authCache = (BasicAuthCache) initPara.get("authCache");
//            CredentialsProvider credsProvider = (CredentialsProvider) initPara.get("credsProvider");
//            RequestConfig defaultRequestConfig = (RequestConfig) initPara.get("defaultRequestConfig");
//            CloseableHttpClient httpclient = builder.build();
//            CloseableHttpResponse response = null;
//
//            logger.info(orderNo + "--????????????");
//            String cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, orderNo, false);
//            if (cookie == null) {
//                logger.info(orderNo + "--????????????");
//                return;
//            }
//            if (SysData.grabOrderMap.get(orderInfo.getId() + "") == null) {
//                logger.info(orderNo + "--???????????????");
//                return;
//            }
//            logger.info(orderNo + "--????????????id");
////			String memberId = null;
//            String memberId = memberIdMap.get(memberKey);
//            if (memberId == null) {
//                memberId = "";
//                try {
//                    cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, orderNo, false);
//                    memberId = getMemberId(httpclient, cookie, defaultRequestConfig, orderNo);
//                    if ("????????????????????????".equals(memberId) || "please login first".equals(memberId)) {
////					this.synchronAccounInfo(accountInfo, "");
//                        logger.info(orderNo + "--????????????????????????");
//                        return;
//                    }
//                } catch (Exception e) {
//                    logger.info(orderNo + "--????????????Id??????");
//                    return;
//                }
//                if (StringUtil.isNotEmpty(memberId)) {
//                    memberIdMap.put(memberKey, memberId);
//                }
//            }
//            logger.info(orderNo + "--????????????");
//            String back = "";
//            List<String> passengerId = new ArrayList<String>();
//            try {
//                cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, orderNo, false);
//                if (cookie == null) {
//                    logger.info(orderNo + "--????????????");
//                    return;
//                }
//                back = addPassengers(orderInfo, accountInfo, httpclient, cookie, defaultRequestConfig, passengerId);
////				if (back.contains("??????????????????????????????")) {
////					logger.info(orderNo + "--?????????????????????");
////					return;
////				}
//                Thread.sleep(2 * 1000);
//                cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, orderNo, false);
//                if (cookie == null) {
//                    logger.info(orderNo + "--????????????");
//                    return;
//                }
//                passengerId = getSamePassengerList(httpclient, cookie, orderInfo, defaultRequestConfig, memberId);
//                if (passengerId != null && passengerId.size() != 0 && "please login first".equals(passengerId.get(0))) {
//                    logger.info(orderNo + "--????????????");
////					this.synchronAccounInfo(accountInfo, "");
//                    return;
//                }
//            } catch (Exception e) {
//                logger.error(orderNo + "--??????????????????");
//                return;
//            }
//            if (SysData.grabOrderMap.get(orderInfo.getId() + "") == null) {
//                logger.info(orderNo + "--???????????????");
//                return;
//            }
//            if (passengerId.size() != orderInfo.getPaxInfos().size()) {
//                logger.error(orderNo + "--????????????????????????");
//                return;
//            }
//            SysData.paxIdMap.put(orderInfo.getId() + "-" + accountInfo.getId(), passengerId);
//            success = true;
//        } catch (Throwable e) {
//            e.printStackTrace();
//        } finally {
//            OrderInfo grabInfo = SysData.grabOrderMap.get(orderInfo.getId() + "");
//            if (!success && grabInfo != null && StringUtil.isEmpty(grabInfo.getOutPrice())) {
//                AccountInfo info = SysData.accountMap.get(accountInfo.getAirCompany()).get(accountInfo.getId());
//                if (info != null) {
//                    this.startAddPax(orderInfo, info);
//                }
//            }
//        }
//    }
//
//    private String addPassengers(OrderInfo orderInfo, AccountInfo accountInfo, CloseableHttpClient httpclient, String cookie, RequestConfig defaultRequestConfig, List<String> passengerId) throws Exception {
//        List<Map<String, String>> passengerInfoList = new ArrayList<Map<String, String>>();
//        String mobile = accountInfo.getTelPhone();
//        if (StringUtil.isNotEmpty(mobile)) {
//            mobile = mobile.replaceAll(",", "");
//        }
//        List<PaxInfo> paxInfos = orderInfo.getPaxInfos();
//        String back = "";
//        for (int i = 0; i < paxInfos.size(); i++) {
//            PaxInfo paxInfo = paxInfos.get(i);
//            Map<String, String> passengerInfoMap = new HashMap<String, String>();
//            String name = paxInfo.getPaxName();
//            String credentials = paxInfo.getPaxType();
//            if ("??????".equals(credentials)) {
//                credentials = "ADT";
//            } else if ("??????".equals(credentials)) {
//                credentials = "CHD";
//            } else {
//                credentials = "INF";
//            }
//            String birthday = paxInfo.getBirth();
//            if (StringUtil.isNotEmpty(birthday) && !"null".equals(birthday)) {
//                birthday = birthday.substring(0, 10);
//            }
//            String sex = paxInfo.getSex();
//            if ("???".equals(sex)) {
//                sex = "0";
//            } else {
//                sex = "1";
//            }
//            String idType = paxInfo.getCardType();
//            String idExpires = "";
//            if ("?????????".equals(idType)) {
//                idType = "NI";
//            } else if ("??????".equals(idType)) {
//                idType = "PP";
//                idExpires = "2101-01-30";
//            } else {
//                idType = "ID";
//            }
//            String numid = paxInfo.getCardNo();
//            String nameType = "CN";
//            if (StringUtil.isNotEmpty(name) && name.contains("/")) {
//                nameType = "EN";
//            }
//            passengerInfoMap.put("birthday", birthday);
//            passengerInfoMap.put("idNo", numid);
//            passengerInfoMap.put("idType", idType);
//            passengerInfoMap.put("name", name);
//            passengerInfoMap.put("nation", nameType);
//            passengerInfoMap.put("passengerType", credentials);
//            passengerInfoMap.put("sex", sex);
//            passengerInfoMap.put("idExpires", idExpires);
//            passengerInfoList.add(passengerInfoMap);
//        }
//        for (int i = 0; i < passengerInfoList.size(); i++) {
//            Map<String, String> passengerInfo = passengerInfoList.get(i);
//            HttpPost post = new HttpPost("/h5/pip/book/mergePassenger.json");
//            String jsonObject = "{\"passengerBo\":{\"name\":\"" + passengerInfo.get("name").replaceAll(" ", "") + "\",\"idNo\":\"" + passengerInfo.get("idNo") + "\",\"idType\":\"" + passengerInfo.get("idType") + "\",\"mobile\":\"" + mobile + "\",\"birthday\":\"" + passengerInfo.get("birthday") + "\",\"passengerType\":\"" + passengerInfo.get("passengerType") + "\",\"sex\":\"" + passengerInfo.get("sex") + "\",\"nation\":\"" + passengerInfo.get("nation") + "\",\"idExpires\":\"" + passengerInfo.get("idExpires") + "\"},\"isCn\":true}";
//            post.setConfig(defaultRequestConfig);
//            HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//            post.setConfig(defaultRequestConfig);
//            StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//            post.setEntity(entity);
//            post.setHeader("Cookie", cookie);
//            post.setHeader("Referer", "https://wx.flycua.com/h5/home.html");
//            post.setHeader("Origin", "https://wx.flycua.com");
//            post.setHeader("Content-Type", "application/json;charset=UTF-8");
//            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//            post.setHeader("Host", "wx.flycua.com");
//            if (!SysData.useAbuyunProxy) {
//                post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                        new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//                post.setHeader("Proxy-Connection", "keep-alive");
//            }
//            post.setHeader("isB2C", "NO");
//            post.setHeader("isWechat", "H5");
//            CloseableHttpResponse response = httpclient.execute(target, post);
//            back = EntityUtils.toString(response.getEntity(), "utf-8");
//            logger.info(orderInfo.getOrderNo() + "--?????????????????????:" + back);
//        }
//        return back;
//    }
//
//    private synchronized String getLoginState(CloseableHttpClient httpclient, AccountInfo accountInfo, RequestConfig defaultRequestConfig, String orderNo, boolean handeLogin) {
//        AccountInfo accountInfo1 = SysData.accountMap.get(accountInfo.getAirCompany()).get(accountInfo.getId());
//        if (accountInfo1 == null) {
//            return null;
//        }
//        String cookie = accountInfo1.getLoginState();
//        if (StringUtil.isNotEmpty(cookie)) {
//            return handeLogin ? "????????????" : cookie;
//        }
//        try {
//            for (int i = 0; i < 5; i++) {
//                cookie = getCookie(httpclient, cookie, accountInfo, defaultRequestConfig);
//                if (StringUtil.isEmpty(cookie) || cookie.contains("????????????") || cookie.contains("????????????")) {
//                    continue;
//                }
//                if (cookie.contains("JSESSIONID")) {
//                    break;
//                }
//                if (cookie.contains("???????????????????????????")) {
//                    logger.info(orderNo + "--???????????????????????????");
//                    return !handeLogin ? null : "???????????????????????????";
//                }
//                if (cookie.contains("?????????????????????")) {
//                    logger.info(orderNo + "--?????????????????????");
//                    return !handeLogin ? null : "?????????????????????";
//                }
//            }
//        } catch (Exception e) {
//            logger.error("error", e);
//            return !handeLogin ? null : "????????????";
//        }
//        if (StringUtil.isEmpty(cookie) || cookie.contains("????????????") || cookie.contains("????????????")) {
//            logger.info(orderNo + "--????????????");
//            return !handeLogin ? null : "????????????";
//        }
//        this.synchronAccounInfo(accountInfo, cookie);
//        return handeLogin ? "true" : cookie;
//    }
//
//    private String getCookie(CloseableHttpClient httpclient, String cookie, AccountInfo accountInfo, RequestConfig defaultRequestConfig) throws Exception {
//        // HttpPost post = new HttpPost("/member/member-login!popLogin.shtml");
//        String back = "";
//        String newCookie = "";
//        /////////////////////////////////////////////////////////////////////////
//        HttpPost post = new HttpPost("https://higo.flycua.com/ffp/member/login");
//        post.setConfig(defaultRequestConfig);
//        HttpHost target = new HttpHost("www.flycua.com", 443, "https");
//        // String account = "NrcZ9YVVM/N5PuZaHJfqltN6wPGOIfrHwwFNJ4DTKCGZjbgpjiMPjBVBguz512LrxG/9mpnFFtRYXdzyKfh8kUc/Kw5gfdrdYW772VpVfBk5Hy/RVF2n9wDm5rXeZ/T62S0iZK2dDQ1Bqf5V9QKC4A==";
//        // NrcZ9YVVM/N5PuZaHJfqltN6wPGOIfrHwwFNJ4DTKCFkG3VNDPnRVsCDhhU17dRzxG/9mpnFFtRYXdzyKfh8kUc/Kw5gfdrdYW772VpVfBk5Hy/RVF2n9wDm5rXeZ/T62S0iZK2dDQ1Bqf5V9QKC4A==
//        // String a = aesInvokeFunction("decrypt", account);
//        String accountMsg = "{\"mode\":\"memberLogin\",\"memberId\":"+ accountInfo.getAccount()+",\"password\":"+ accountInfo.getPassword() +",\"verificationCode\":\"\",\"openId\":\"\"}";
//        String account = aesInvokeFunction("encrypt", accountMsg);
//        StringEntity entity = new StringEntity(account, Charset.forName("UTF-8"));
//        // post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//        post.setEntity(entity);
//        post.setHeader("Host", "higo.flycua.com");
//        // post.setHeader("Cookie", "tokenId=13B9AEF1F8B7CD36DE7621145423944DA4AF77654043DAD638E9F93B378F94E1F1F8B81B4FBF4ABF96C3073EA3712CDB0C92CD37F13F17C47FAEC3B4D1348D48; Domain=.flycua.com; Path=/");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        post.setHeader("Referer", "https://higo.flycua.com/hh/html/cuaLoginNew.html");
//        post.setHeader("Origin", "https://higo.flycua.com");
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//        }
//        post.setHeader("Proxy-Connection", "keep-alive");
//
//        CloseableHttpResponse response = httpclient.execute(post);
//
//        back = EntityUtils.toString(response.getEntity(), "utf-8");
//        JSONObject message = new JSONObject(back);
//        String backDesc = message.get("errordesc").toString();
//        back = aesInvokeFunction("decrypt", backDesc);
//        JSONObject backMess = new JSONObject(back);
//        String memberId = backMess.get("memberId").toString();
//        String errorcode = backMess.get("errorcode").toString();
//        if (errorcode.equals("0000")) {
//            InitUtil.orderRemind(1L,"????????????","????????????", false, KNAppOutticketService.class.toString());
//        }
//        logger.info("????????????:" + back);
//        if (StringUtil.isEmpty(back)) {
//            return "";
//        }
//        if (back.contains("?????????????????????")) {
//            return "?????????????????????";
//        }
//        if (back.contains("???????????????????????????")) {
//            return "???????????????????????????";
//        }
//        if (back.contains("????????????")) {
//            return "????????????";
//        }
//        if (back.contains("????????????")) {
//            return "????????????";
//        }
//        Header[] headersArr = response.getAllHeaders();
//        // String newCookie = "";
//        for (Header header : headersArr) {
//            if (("Set-Cookie".equals(header.getName())) &&
//                    (header.getValue().contains(";")))
//            {
//                String[] str = header.getValue().split(";");
//                for (String c : str) {
//                    if ((c.contains("CUA_SSO_TOKEN")) || (c.contains("LOGIN_COOKIE_TIME"))|| (c.contains("tokenId"))) {
//                        newCookie = newCookie + c + ";";
//                    }
//                }
//            }
//        }
//        post = new HttpPost("/h5/sso/loginStatus.json");
//        post.setHeader("Host", "m.flycua.com");
//        post.setHeader("Cookie", newCookie);
//        post.setHeader("Referer", "https://m.flycua.com/h5/home.html");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        target = new HttpHost("m.flycua.com", 443, "https");
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(new StringBuilder(SysData.proxyUser)
//                    .append(":").append(SysData.proxyPwd).toString().getBytes("utf-8")));
//        }
//        post.setHeader("Proxy-Connection", "keep-alive");
//        post.setHeader("isB2C", "NO");
//        post.setHeader("isWechat", "H5");
//        response = httpclient.execute(target, post);
//
//        back = EntityUtils.toString(response.getEntity(), "utf-8");
//        logger.info("????????????1:" + back);
//        if (StringUtil.isEmpty(back)) {
//            return "";
//        }
//        if (back.contains("????????????")) {
//            return "????????????";
//        }
//        headersArr = response.getAllHeaders();
//        for (Header header : headersArr) {
//            if (("Set-Cookie".equals(header.getName())) &&
//                    (header.getValue().contains(";")))
//            {
//                String[] str = header.getValue().split(";");
//                for (String c : str) {
//                    if ((c.contains("JSESSIONID")) || (c.contains("CUA_SSO_TOKEN")) || (c.contains("LOGIN_COOKIE_TIME"))) {
//                        newCookie = newCookie + c + ";";
//                    }
//                }
//            }
//        }
//        if (StringUtil.isNotEmpty(newCookie)) {
//            newCookie = newCookie + "memberId=" + memberId;
//            return newCookie;
//        }
//        return "";
//    }
//
//    public static void updateCookie() {
//        OrderFrame.freshService.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Map<Long, AccountInfo> map = SysData.accountMap.get("KN");
//                    if (map != null && !map.isEmpty()) {
//                        List<AccountInfo> accountInfos = Lists.newArrayList();
//                        for (Long id : map.keySet()) {
//                            AccountInfo info = map.get(id);
//                            if (StringUtil.isNotEmpty(info.getLoginState()) && info.isKeepLogin()) {
//                                accountInfos.add(info);
//                            }
//                        }
//                        if (accountInfos.isEmpty()) {
//                            return;
//                        }
//                        Map<String, Object> initPara = InitUtil.getInitPara();
//                        HttpClientBuilder builder = (HttpClientBuilder) initPara.get("builder");
//                        RequestConfig defaultRequestConfig = (RequestConfig) initPara.get("defaultRequestConfig");
//                        CloseableHttpClient httpclient = builder.build();
//                        for (AccountInfo accountInfo : accountInfos) {
//                            String result = null;
//                            try {
//                                result = checkLogin(httpclient, accountInfo.getLoginState());
//                            } catch (Exception e) {
//                                logger.error("????????????????????????", e);
//                            }
//                            if ("????????????".equals(result)) {
//                                AccountInfo info = SysData.accountMap.get("KN").get(accountInfo.getId());
//                                if (info != null) {
//                                    info.setLoginState("");
//                                }
//                            }
//                        }
//                    }
//                } catch (Throwable e) {
//                    logger.error("????????????????????????", e);
//                }
//            }
//        }, 5, 2, TimeUnit.SECONDS);
//    }
//
//    private static String checkLogin(CloseableHttpClient httpclient, String newCookie) throws Exception {
//
//        HttpPost post = new HttpPost("/h5/sso/loginStatus.json");
//        post.setHeader("Host", "wx.flycua.com");
//        post.setHeader("Cookie", newCookie);
//        post.setHeader("Referer", "https://wx.flycua.com/h5/home.html");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//        CloseableHttpResponse response = httpclient.execute(target, post);
//
//        Header[] headersArr = response.getAllHeaders();
//
//        response = httpclient.execute(target, post);
//
//        String back = EntityUtils.toString(response.getEntity(), "utf-8");
//        logger.info("????????????1:" + back);
//        if (StringUtil.isEmpty(back)) {
//            return "";
//        }
//        if (back.contains("????????????")) {
//            return "????????????";
//        }
//        headersArr = response.getAllHeaders();
//        for (Header header : headersArr) {
//            if ("Set-Cookie".equals(header.getName())) {
//                if (header.getValue().contains(";")) {
//                    String[] str = header.getValue().split(";");
//                    for (String c : str) {
//                        if (c.contains("JSESSIONID") || c.contains("CUA_SSO_TOKEN") || c.contains("LOGIN_COOKIE_TIME"))
//                            newCookie += c + ";";
//                    }
//                }
//            }
//        }
//        if (StringUtil.isNotEmpty(newCookie)) {
//            return newCookie;
//        }
//        return "";
//    }
//
//    public List<String> getFlightPriceInfo(String dep, String arrival, String depTime, String flightNo) {
//        List<String> result = Lists.newArrayList();
//        Map<String, Object> initPara = InitUtil.getProxyPara();
//        if ("false".equals(initPara.get("result"))) {
//            result.add("??????????????????ip");
//            return result;
//        }
//        HttpClientBuilder builder = (HttpClientBuilder) initPara.get("builder");
//        BasicAuthCache authCache = (BasicAuthCache) initPara.get("authCache");
//        CredentialsProvider credsProvider = (CredentialsProvider) initPara.get("credsProvider");
//        RequestConfig defaultRequestConfig = (RequestConfig) initPara.get("defaultRequestConfig");
//        CloseableHttpClient httpclient = builder.build();
//        CloseableHttpResponse response = null;
//        String content = "";
//        for (int i = 0; i < 5; i++) {
//            try {
//                HttpPost post = new HttpPost("/h5/pip/book/flightSearch.json");
//                HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//                HttpClientContext context = HttpClientContext.create();
//                context.setAuthCache(authCache);
//                context.setTargetHost(target);
//                context.setRequestConfig(defaultRequestConfig);
//                context.setCredentialsProvider(credsProvider);
//                post.setConfig(defaultRequestConfig);
//                String jsonObject = "{\"tripType\":\"OW\",\"orgCode\":\"" + dep.replace("BJS", "NAY").replace("SHA", "PVG") + "\",\"dstCode\":\"" + arrival.replace("BJS", "NAY").replace("SHA", "PVG") + "\",\"takeoffdate1\":\"" + depTime + "\",\"takeoffdate2\":\"\"}";
//                StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//                post.setEntity(entity);
//                post.setHeader("Referer", "https://wx.flycua.com/h5/");
//                post.setHeader("Origin", "https://wx.flycua.com");
//                if (!SysData.useAbuyunProxy) {
//                    post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                            new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//                    post.setHeader("Proxy-Connection", "keep-alive");
//                }
//                post.setHeader("Content-Type", "application/json;charset=UTF-8");
//                post.setHeader("Host", "wx.flycua.com");
//                post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//                post.setHeader("isB2C", "NO");
//                post.setHeader("isWechat", "H5");
//
//                response = httpclient.execute(target,post,context);
//                content = EntityUtils.toString(response.getEntity(),"utf-8");
//            } catch (Exception e) {
//                logger.error("error", e);
//            }
//            if (StringUtil.isNotEmpty(content) && !content.contains("??????????????????") && !content.contains("503 Service Temporarily Unavailable") && !content.contains("??????????????????????????????") && !content.contains("????????????")) {
//                break;
//            }
//        }
//        List<FlightInfo> resultList = new ArrayList<FlightInfo>();
//        try {
//            resultList = doParse(content, dep, arrival, depTime);
//        } catch (Exception e) {
//            logger.error("error", e);
//        }
//        if (resultList.size() > 0) {
//            result.add(getResult(resultList, flightNo));
//        } else {
//            result.add("error:?????????????????????");
//        }
//        result.add(content);
//        return result;
//    }
//
//    public String getResult(List<FlightInfo> resList, String flightNo) {
//        boolean result = false;
//        String strResult = "";
//
//        String strcabin = "";
//        String strprice = "";
//        String strtype = "";
//        String strseat = "";
//        FlightInfo flightinfo = new FlightInfo();
//        String depTime = "";
//        for (int i = 0; i < resList.size(); i++) {
//            String resflightno = resList.get(i).getFlightNo();
//            depTime = resList.get(0).getDeparutreDate();
//            if (resflightno != null && resflightno.equals(flightNo)) {
//                flightinfo = resList.get(i);
//            }
//        }
//        List<CabinInfo> cabins = flightinfo.getCabins();
//        Collections.sort(cabins, new Comparator<CabinInfo>() {
//            public int compare(CabinInfo p1, CabinInfo p2) {
//                Float price1 = Float.parseFloat(p1.getPrice());
//                Float price2 = Float.parseFloat(p2.getPrice());
//                return price1.compareTo(price2);
//            }
//        });
//        String airline = resList.get(0).getFlightNo().substring(0, 2);
//        for (CabinInfo cabininfo : cabins) {
//            result = true;
//            strcabin = strcabin + "-" + cabininfo.getCabinCode();
//            strprice = strprice + "-" + cabininfo.getPrice();
//            String seat = cabininfo.getLastSeat();
//            seat = seat == null ? "*" : seat;
//            strseat = strseat + "-" + seat;
//            String type = cabininfo.getBaseCabin();
//            strtype = strtype + "-" + type;
//        }
//        if (result && strcabin.length() >= 1) {
//            strcabin = strcabin.substring(1);
//            strprice = strprice.substring(1);
//            strtype = strtype.substring(1);
//            strseat = strseat.substring(1);
//        }
//        strResult = "{'result':'" + result + "','strcabin':'" + strcabin + "','strprice':'" + strprice + "','strtype':'" + strtype + "','strseat':'" + strseat + "','depTime':'" + depTime + "'}";
//        return strResult;
//    }
//
//
//    private List<FlightInfo> doParse(String content, String dept, String arrival, String dateString) {
//        List<FlightInfo> resultList = new ArrayList<FlightInfo>();
//        //??????json
//        try {
//            JSONObject jsonObject = new JSONObject(content);
//            JSONObject goFlightInfo = jsonObject.getJSONObject("goFlightInfo");
//            JSONObject dstCity = goFlightInfo.getJSONObject("dstCity");
//            String dep = dstCity.getString("airportCode");
//            JSONObject orgCity = goFlightInfo.getJSONObject("orgCity");
//            String arr = orgCity.getString("airportCode");
//            //???????????????????????????????????????
//            JSONObject flightInfoObj = null;
//            JSONArray flightInfoArr = goFlightInfo.getJSONArray("flightInfo");
//            for (int i = 0; i < flightInfoArr.length(); i++) {
//                String hbh = "";
//                flightInfoObj = flightInfoArr.getJSONObject(i);
//                String departTime = flightInfoObj.getString("departTime");
//                JSONObject flightSegsObj = null;
//                JSONArray flightSegsArr = flightInfoObj.getJSONArray("flightSegs");
//                if (flightSegsArr.length() == 1) {
//                    flightSegsObj = flightSegsArr.getJSONObject(0);
//                    hbh = flightSegsObj.getString("flightNo");
//                    FlightInfo flightInfo = new FlightInfo();
//                    flightInfo.setWebType("KN");
//                    flightInfo.setDeparutre(dep);
//                    flightInfo.setArrival(arr);
//                    flightInfo.setFlightNo(hbh);
//                    flightInfo.setDeparutreDate(departTime);
//                    flightInfo.setUpdateTime(new Date());
//                    JSONArray brandSeg = flightSegsObj.getJSONArray("brandSeg");
//                    for (int j = 0; j < brandSeg.length(); j++) {
//                        CabinInfo cabinInfo = new CabinInfo();
//                        JSONObject brandSegObj = brandSeg.getJSONObject(j);
//                        String brandCode = brandSegObj.getString("brandCode"); //????????????
//                        String cabinCode = brandSegObj.getString("cabinCode");      //??????
//                        String remaindNum = brandSegObj.getString("remaindNum"); //?????????
//                        JSONObject brandInfo = brandSegObj.getJSONObject("brandInfo");
//                        String redPaperRuleText = brandInfo.getString("text");
//                        JSONArray priceObj = brandSegObj.getJSONArray("price");
//                        for (int priceIndex = 0; priceIndex < priceObj.length(); priceIndex++) {
//                            JSONObject priceO = priceObj.getJSONObject(priceIndex);
//                            String psyType = priceO.getString("psgType");
//                            if (!"ADT".equals(psyType)) {
//                                continue;
//                            }
//                            String price = priceO.getString("price");
//                            cabinInfo.setPrice(price);
//                        }
//                        cabinInfo.setCabinCode(cabinCode);
//                        cabinInfo.setPriceType(brandCode);
//                        cabinInfo.setBaseCabin(redPaperRuleText);
//                        if (StringUtil.isNotEmpty(remaindNum) && ">10???".equals(remaindNum)) {
//                            remaindNum = "10";
//                        } else if (StringUtil.isNotEmpty(remaindNum)) {
//                            String regEx = "??????([0-9])???";
//                            Pattern pattern = Pattern.compile(regEx);
//                            Matcher m = pattern.matcher(remaindNum);
//                            while (m.find()) {
//                                if (StringUtil.isNotEmpty(m.group(1))) {
//                                    remaindNum = m.group(1);
//                                }
//                            }
//                        }
//                        cabinInfo.setLastSeat(remaindNum);
//                        //if (temp.length == 1) {
//                        flightInfo.getCabins().add(cabinInfo);
//                    }
//                    resultList.add(flightInfo);
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error("????????????:" + content);
//            return resultList;
//        }
//        return resultList;
//    }
//
//    public static void main(String[] args) {
//        /*OrderInfo orderInfo = new OrderInfo();
//        orderInfo.setId(1L);
//        orderInfo.setOrderNo("402188280");
//        orderInfo.setOrderStatus("?????????");
//        orderInfo.setFlightNo("KN2975");
//        orderInfo.setDep("NAY");
//        orderInfo.setArr("HLD");
//        orderInfo.setDepTime("2019-03-04 09:05:00");
//        orderInfo.setOutPrice("648");
//        orderInfo.setGrabTime("2019-03-04 09:05:00");
//        List<PaxInfo> paxInfos = Lists.newArrayList();
//        orderInfo.setPaxInfos(paxInfos);
//        PaxInfo paxInfo = new PaxInfo();
//        paxInfos.add(paxInfo);
//        paxInfo.setPaxName("??????");
//        paxInfo.setPaxType("??????");
//        paxInfo.setSex("???");
//        paxInfo.setBirth("1966-11-26");
//        paxInfo.setCardType("?????????");
//        paxInfo.setCardNo("110105196611267741");
//        paxInfo.setSellPrice("700");
//
//        AccountInfo info = new AccountInfo();
//        info.setAirCompany("KN");
//        info.setAccount("15595168130");
//        info.setPassword("feeye123");
//        info.setContact("??????");
//        info.setTelPhone("15595168130");
//        info.setId(1L);
//
//        SysData.abuyunUser = "HL7F5JF125K85K8D";
//        SysData.abuyunPwd = "FC393F432489B2E5";
//        SysData.grabOrderMap.put(orderInfo.getId() + "", orderInfo);
//        Map<Long, AccountInfo> map = Maps.newHashMap();
//        map.put(info.getId(), info);
//        SysData.accountMap.put("KN", map);
//        new KNAppOutticketService().startCreatOrder(orderInfo, info, null, 0);*/
//    }
//    public void startCreatOrder(OrderInfo orderInfo, AccountInfo accountInfo, List<String> paxIds, int retryTimes) {
//        boolean isSuccess = false;
//        String memberKey = accountInfo.getAccount() + "_" + accountInfo.getPassword();
//        String order_id = orderInfo.getOrderNo();
//        CloseableHttpClient httpclient = null;
//        try {
//            long startTime = new Date().getTime();
//            logger.info(orderInfo.getOrderNo() + "--??????????????????");
//            String grabStatus = "????????????";
//            String logContent = "????????????";
//            if (++retryTimes == 1) {
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, logContent, false, "KNAppOutticketService");
//            }
//            Map<String, Object> initPara = InitUtil.getInitPara();
//            if ("false".equals(initPara.get("result"))) {
//                logger.info(orderInfo.getOrderNo() + "??????????????????ip");
//                grabStatus = "????????????";
//                logContent = "??????????????????ip";
//                // System.out.println("??????IP???" + "123");
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, logContent, false, "KNAppOutticketService");
//                return;
//            }
//            HttpClientBuilder builder = (HttpClientBuilder) initPara.get("builder");
//            BasicAuthCache authCache = (BasicAuthCache) initPara.get("authCache");
//            CredentialsProvider credsProvider = (CredentialsProvider) initPara.get("credsProvider");
//            RequestConfig defaultRequestConfig = (RequestConfig) initPara.get("defaultRequestConfig");
//            BasicCookieStore cookieStore = (BasicCookieStore) initPara.get("cookieStore");
//            httpclient = builder.build();
//            CloseableHttpResponse response = null;
//
//            String cookie = "";
//            String back = "";
//            Map<String, String> resultMap = null;
//            Map<String, String> verifyPostParam = SysData.verifyParamMap.get(orderInfo.getId());
//            if (verifyPostParam == null || verifyPostParam.isEmpty()) {
//                int index = 0;
//                verifyPostParam = Maps.newHashMap();
//                for (; index < 3; index++) {
//                    logger.info(order_id + "??????????????????");
//                    grabStatus = "??????????????????";
//                    logContent = "??????????????????";
//                    InitUtil.orderRemind(orderInfo.getId(), grabStatus, logContent, false, "KNAppOutticketService");
//                    // ????????????
//                    resultMap = flightSearch(defaultRequestConfig, orderInfo, httpclient, cookieStore);
//                    if (resultMap == null || resultMap.size() == 0) {
//                        logger.info(order_id + "--??????????????????");
//                        grabStatus = "??????????????????";
//                        InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                        return;
//                    }
//                    cookie = resultMap.get("cookie");
//                    back = resultMap.get("result");
//                    logger.info(order_id + "--????????????????????????:" + back);
//                    if (StringUtil.isNotEmpty(back) && !back.contains("503 Service") && !back.contains("???????????????")) {
//                        break;
//                    }
//                }
//                if (index == 3) {
//                    logger.info(order_id + "--??????????????????");
//                    grabStatus = "??????????????????";
//                    InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                    return;
//                }
//                logger.info(order_id + "--??????????????????");
//                try {
//                    parseFlightInfo(back, orderInfo, verifyPostParam);
//                } catch (Exception e) {
//                    logger.info(order_id + "--????????????????????????");
//                    grabStatus = "????????????????????????";
//                    InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                    return;
//                }
//            }
//            String billNo = "";
//            if (SysData.grabOrderMap.get(orderInfo.getId() + "") == null) {
//                logger.info(order_id + "--???????????????");
//                return;
//            }
//            if (verifyPostParam == null || verifyPostParam.size() == 0) {
//                logger.info(order_id + "--?????????????????????");
//                grabStatus = "?????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//            if (cookie == null) {
//                logger.info(order_id + "--????????????");
//                grabStatus = "????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            if (SysData.grabOrderMap.get(orderInfo.getId() + "") == null) {
//                logger.info(order_id + "--???????????????");
//                return;
//            }
//            if (verifyPostParam.get("shoppingKey") == null || StringUtil.isEmpty(verifyPostParam.get("shoppingKey"))) {
//                grabStatus = "?????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                logger.info(order_id + "--?????????????????????");
//                return;
//            }
//            ////////////////////////////////////////////////////
//            cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//            if (cookie == null) {
//                return;
//            }
//            ///////////////////////////////////////////////
//            back = flightSearchagain(cookie, orderInfo, builder, authCache, credsProvider, defaultRequestConfig, httpclient);
//            logger.info(order_id + "--????????????");
//            //????????????
//            logger.info(order_id + "--????????????");
//            grabStatus = "????????????";
//            InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//            try {
//                for (int i = 0; i < 5; i++) {
//                    cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                    if (cookie == null) {
//                        logger.info(order_id + "--");
//                        return;
//                    }
//                    resultMap = selectFlight(verifyPostParam, cookie, httpclient, defaultRequestConfig);
//                    if (resultMap == null || resultMap.size() == 0) {
//                        // login(accountInfo);
//                        continue;
//                    }
//                    String error = resultMap.get("error");
//                    if (resultMap != null && StringUtil.isEmpty(error)) {
//                        login(accountInfo);
//                        break;
//                    }
//                    if ("????????????".equals(error)) {
//                        grabStatus = "";
//                        InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                        // login(accountInfo);
//                        // return;
//                        continue;
//                    }
//                    if ("??????????????????????????????????????????".equals(error)) {
//                        grabStatus = "?????????????????????";
//                        InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                        // login(accountInfo);
//                        // return;
//                        continue;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.error(order_id + "??????????????????", e);
//                grabStatus = "??????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            if (resultMap == null || resultMap.size() == 0) {
//                logger.info(order_id + "??????????????????");
//                grabStatus = "??????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            if (resultMap.get("error") != null) {
//                logger.info(order_id + "??????????????????," + resultMap.get("error"));
//                grabStatus = "??????????????????";
//                logContent = "??????????????????," + resultMap.get("error");
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, logContent, false, "KNAppOutticketService");
//                return;
//            }
//            if (SysData.grabOrderMap.get(orderInfo.getId() + "") == null) {
//                logger.info(order_id + "--???????????????");
//                return;
//            }
//            if (paxIds == null) {
//                //????????????
//                logger.info(order_id + "--????????????id");
//                grabStatus = "????????????id";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                String memberId = memberIdMap.get(memberKey);
//                if (memberId == null) {
//                    memberId = "";
//                    try {
//                        cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                    }catch (Exception e) {
//                        logger.info(order_id + "--????????????????????????");
//                        return;
//                    }
//                    try {
//                        memberId = getMemberId(httpclient, cookie, defaultRequestConfig, order_id);
//                        if ("????????????????????????".equals(memberId) || "please login first".equals(memberId)) {
//                            logger.info(order_id + "--????????????????????????");
//                            return;
//                        }
//                        if (StringUtil.isNotEmpty(memberId)) {
//                            memberIdMap.put(memberKey, memberId);
//                        }
//                    } catch (Exception e) {
//                        logger.info(order_id + "--????????????Id??????");
//                        return;
//                    }
//                    if (StringUtil.isNotEmpty(memberId)) {
//                        memberIdMap.put(memberKey, memberId);
//                    }
//                }
//                logger.info(order_id + "--????????????");
//                paxIds = new ArrayList<String>();
//                try {
//                    cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                    if (cookie == null) {
//                        logger.info(order_id + "--????????????");
//                        return;
//                    }
//                    back = addPassengers(orderInfo, accountInfo, httpclient, cookie, defaultRequestConfig, paxIds);
//                    Thread.sleep(2 * 1000);
//                    cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                    if (cookie == null) {
//                        logger.info(order_id + "--????????????");
//                        return;
//                    }
//                    paxIds = getSamePassengerList(httpclient, cookie, orderInfo, defaultRequestConfig, memberId);
//                    if (paxIds != null && paxIds.size() != 0 && "please login first".equals(paxIds.get(0))) {
//                        logger.info(order_id + "--????????????");
//                        return;
//                    }
//                } catch (Exception e) {
//                    logger.error(order_id + "--??????????????????");
//                    return;
//                }
//                if (SysData.grabOrderMap.get(orderInfo.getId() + "") == null) {
//                    logger.info(order_id + "--???????????????");
//                    return;
//                }
//                if (paxIds.size() != orderInfo.getPaxInfos().size()) {
//                    logger.error(order_id + "--????????????????????????");
//                    return;
//                }
//                ;
//            }
//            if (paxIds.size() != orderInfo.getPaxInfos().size()) {
//                logger.info(order_id + "--?????????????????????id");
//                grabStatus = "?????????????????????id";
//                paxIds = null;
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            //?????????????????????????????????
//            logger.info(order_id + "--??????????????????");
//            grabStatus = "??????????????????";
//            InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//            try {
//                cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                if (cookie == null) {
//                    grabStatus = "";
//                    InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                    return;
//                }
//                resultMap = ancilSearch(cookie, defaultRequestConfig, httpclient, resultMap, orderInfo);
//            } catch (Exception e) {
//                logger.info(order_id + "????????????????????????");
//                logger.error("error", e);
//                grabStatus = "????????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            if (resultMap == null || resultMap.size() == 0) {
//                logger.info(order_id + "????????????????????????");
//                grabStatus = "????????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            if (SysData.grabOrderMap.get(orderInfo.getId() + "") == null) {
//                logger.info(order_id + "--???????????????");
//                return;
//            }
//            //???????????????Id
//            logger.info(order_id + "--???????????????Id");
//            grabStatus = "???????????????Id";
//            InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
////			String contactId = contactIdMap.get(memberKey);
//            String contactId = "";
//            try {
//                cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                contactId = getContactId(cookie, defaultRequestConfig, httpclient, orderInfo, accountInfo);
//                if (StringUtil.isNotEmpty(contactId) && !contactId.contains("ERROR:")) {
//                    contactIdMap.put(memberKey, contactId);
//                }
//            } catch (Exception e) {
//                logger.info(order_id + "???????????????Id??????");
//                logger.error("error", e);
//                grabStatus = "???????????????Id??????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            if (StringUtil.isEmpty(contactId)) {
//                logger.info(order_id + "???????????????Id??????");
//                grabStatus = "???????????????Id??????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            if (contactId.contains("ERROR:")) {
//                String errorMsg = back.split("\\:")[1];
//                logger.info(order_id + errorMsg);
//                grabStatus = errorMsg;
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            //????????????
//            logger.info(order_id + "--??????????????????");
//            grabStatus = "??????????????????";
//            InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//            try {
//                cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                back = orderConfirm(cookie, defaultRequestConfig, httpclient, resultMap, orderInfo, accountInfo, contactId, paxIds);
//            } catch (Exception e) {
//                logger.info(order_id + "????????????????????????");
//                grabStatus = "????????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                logger.error("error", e);
//                return;
//            }
//            if (StringUtil.isEmpty(back)) {
//                logger.info(order_id + "????????????????????????");
//                grabStatus = "????????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            logger.info(order_id + "--??????????????????");
//            grabStatus = "??????????????????";
//            InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//            resultMap.clear();
//
//            try {
//                resultMap = selectPayMethod(back, "zfb");
//            } catch (Exception e) {
//                logger.info(order_id + "????????????????????????");
//                grabStatus = "????????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                logger.error("error", e);
//                return;
//            }
//            if (resultMap == null || resultMap.size() == 0) {
//                logger.info(order_id + "????????????????????????");
//                grabStatus = "????????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            String paymentType = resultMap.get("paymentType");
//            String paymentCode = resultMap.get("paymentCode");
//            if (SysData.grabOrderMap.get(orderInfo.getId() + "") == null) {
//                logger.info(order_id + "--???????????????");
//                return;
//            }
//            //??????????????????
//            logger.info(order_id + "--??????????????????");
//            grabStatus = "??????????????????";
//            InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//            int i = 0;
//            for (; i < 3; i++) {
//                try {
//                    cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                    // cookie = "JSESSIONID=E655C810B089AEA69B2224BC13BCA16D; Secure; __jsluid=9c5cf0fa70a4fbab655c8d1bb4ea605a; _gscu_1693774232=53479146hbijp056; _gscu_1166988165=53480333upz7k311; _gscbrs_1693774232=1; tokenId=9CBC7A21DFEF8141A77D78ACA51554D6A4AF77654043DAD638E9F93B378F94E16B05CE1060BCDC4130E6831A2C98188C307DC72E02E976D0D24F7AB558AC80A8; Secure; TY_SESSION_ID=777ab908-bdb9-4e18-b74d-7f431113795b; _gscs_1693774232=t5478797074qz7590|pv:6";
//                    back = createOrder(httpclient, defaultRequestConfig, cookie);
//                } catch (Exception e) {
//                    logger.info(order_id + "????????????????????????");
//                    logger.error("error", e);
//                }
//                if (StringUtil.isEmpty(back)) {
//                    logger.info(order_id + "?????????????????????????????????");
//                }
//                if (StringUtil.isNotEmpty(back) && back.contains("503 Service")) {
//                    logger.info(order_id + "????????????503??????");
//                    // break;
//                    // continue;
//                }
//                if (StringUtil.isNotEmpty(back) && !back.contains("503 Service")) {
//                    // logger.info(order_id + "????????????503??????");
//                    break;
//                    // continue;
//                }
//            }
//            if (i == 3) {
//                logger.info(order_id + "????????????:" + back);
//                String result = getOrderList(httpclient, cookie, defaultRequestConfig);
//                if (result.contains("?????????")) {
//                    InitUtil.orderRemind(orderInfo.getId(), "??????????????????????????????" ,"??????????????????????????????", false, "KNAppOutticketService");
//                } else {
//                    grabStatus = "????????????";
//                    InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                    return;
//                }
//            }
//            if (back.contains("???3??????????????????")) {
//                InitUtil.orderRemind(orderInfo.getId(), "??????????????????", "??????????????????3????????????????????????????????????????????????????????????", false, "KNAppOutticketService");
//                ThreadPoolExecutor executor = OutticketHandler.taskServiceMap.get(orderInfo.getBirths());
//                if (executor != null) {
//                    executor.shutdownNow();
//                }
//                return ;
//            }
//            logger.info(order_id + "createOrderResult:" + back);
//            JSONObject createOrderObj = new JSONObject(back);
//            String orderStatus = "";
//            try {
//                orderStatus = createOrderObj.getString("orderStatus");
//            } catch (Exception e) {
//                logger.error("createOrderResultError" + e);
//                logger.info(order_id + "createOrderResultError:" + back);
//                grabStatus = "????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            resultMap.put("orderStatus", orderStatus);
//
//            if (SysData.grabOrderMap.get(orderInfo.getId() + "") == null) {
//                logger.info(order_id + "--???????????????");
//                return;
//            }
//
//            String orderNo = "";
//
//            if (StringUtil.isNotEmpty(orderStatus) && "BOOKED".equalsIgnoreCase(orderStatus)) {
//                orderNo = createOrderObj.getString("orderNo");
//                logger.info(order_id + "--orderNo:" + orderNo);
//            }
//            if (DateUtil.IsRunningTimeOut(startTime, 7 * 60 * 1000)) {
//                logger.info(order_id + "???????????????????????????");
//                grabStatus = "???????????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//			/*
//			 * String paymentType = "";
//			String paymentCode = "";
//			 */
//            //?????????????????????
//            logger.info(order_id + "--????????????");
//            grabStatus = "????????????";
//            InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//            try {
//                cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                back = payment(defaultRequestConfig, httpclient, cookie, paymentType, paymentCode, orderNo);
//            } catch (Exception e) {
//                logger.error("createOrderResultError" + e);
//                logger.info(order_id + "--?????????????????????????????????????????????????????????????????????");
//                grabStatus = "????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, "?????????????????????????????????????????????????????????????????????", false, "KNAppOutticketService");
//                return;
//            }
//            if (StringUtil.isEmpty(back)) {
//                logger.info(order_id + "??????????????????????????????????????????????????????????????????????????????");
//                grabStatus = "????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, "?????????????????????????????????????????????????????????????????????", false, "KNAppOutticketService");
//                return;
//            }
//            JSONObject paymentObj = new JSONObject(back);
//            String transactionNumber = ""; //???????????????
//            String url = "";
//            boolean flag = false;
//            try {
//                // ??????????????????????????????
//                if (back.contains(url)) {
//                    url = paymentObj.getString("url");
//                    String[] str = url.split("&");
//                    for (String s : str) {
//                        if (s.contains("transactionNumber")) {
//                            transactionNumber = s.split("=")[1];
//                            logger.info("???????????????:" + transactionNumber);
//                            flag = true;
//                        }
//                    }
//                } else {
//                    InitUtil.orderRemind(orderInfo.getId(), "??????????????????", "??????????????????????????????????????????????????????????????????", false, "KNAppOutticketService");
//                    return ;
//                }
//
//            } catch (Exception e) {
//                logger.info(order_id + "???????????????Error:" + back);
//                grabStatus = "????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, "?????????????????????????????????????????????????????????????????????", false, "KNAppOutticketService");
//                return;
//            }
//            if (StringUtil.isNotEmpty(transactionNumber)) {
//                isSuccess = true;
//                grabStatus = "????????????";
//                orderInfo.setOrderNoNew(transactionNumber);
//                orderInfo.setOrderStatus("???????????????");
//                String[] fileds = {"orderNoNew", "orderStatus"};
//                SqliteHander.modifyObjInfo(orderInfo, fileds);
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus + ",???????????????", true, "KNAppOutticketService");
//                String content = "?????????(+" + orderInfo.getOrderNo() + ")--????????????,???????????????";
//                InitUtil.sendSMS(accountInfo.getTelPhone(), content);
//            }
//            logger.info(order_id + "??????????????????url:" + url);
//            if (url == null || StringUtil.isEmpty(url)) {
//                logger.info(order_id + "????????????????????????");
//                grabStatus = "????????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            //?????????????????????????????????????????????
//            logger.info(order_id + "--????????????");
//
//            if (url == null || StringUtil.isEmpty(url)) {
//                logger.info(order_id + "error:????????????????????????");
//                grabStatus = "????????????????????????";
//                InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                return;
//            }
//            //?????????????????????????????????????????????
//            if (flag) {
//                resultMap.clear();
//                cookie = getLoginState(httpclient, accountInfo, defaultRequestConfig, order_id, false);
//                resultMap = this.aliPayM(url, cookie);
//                if (resultMap == null || resultMap.size() == 0) {
//                    logger.info(order_id + "error:????????????");
//                    grabStatus = "????????????????????????";
//                    InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, false, "KNAppOutticketService");
//                    return;
//                } else {
//                    String location = resultMap.get("locationValue");
//                    grabStatus = "????????????????????????";
//                    logContent = "????????????--" + location;
//                    InitUtil.orderRemind(orderInfo.getId(), grabStatus, logContent, false, "KNAppOutticketService");
//                    orderInfo.setLocation(location);
//                    orderInfo.setCookie(cookie);
//                    orderInfo.setOrderStatus("???????????????");
//                    orderInfo.setAccount(accountInfo.getAccount());
//                    String[] fileds = {"location", "cookie", "orderStatus", "account"};
//                    isSuccess = true;
//                    InitUtil.orderRemind(orderInfo.getId(), grabStatus, grabStatus, true, "KNAppOutticketService");
//                    SqliteHander.modifyObjInfo(orderInfo, fileds);
//                    logger.info(order_id + "???????????????");
//                }
//            }
//        } catch (Exception e) {
//            logger.error(e);
//            logger.info(order_id + "--????????????");
//        } finally {
//            /*try {
//                // httpclient.close();
//            } catch (IOException e) {
//                logger.error(e);
//            }*/
//            OrderInfo grabInfo = SysData.grabOrderMap.get(orderInfo.getId() + "");
//            if (!isSuccess && grabInfo != null) {
//                if (checkTime(grabInfo.getGrabTime())) {
//                    this.startCreatOrder(orderInfo, accountInfo, paxIds, retryTimes);
////					InitUtil.orderRemind(orderInfo.getId(), "????????????,????????????", "????????????,????????????", false, "KNAppOutticketService");
//                } else {
//                    try {
//                        SysData.verifyParamMap.remove(orderInfo.getId());
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                    try {
//                        SysData.grabOrderMap.get(orderInfo.getId() + "").setGrabOver(false);  //??????????????????
//                        SysData.grabOrderMap.get(orderInfo.getId() + "").setOutPrice("");
//                        SysData.grabOrderMap.get(orderInfo.getId() + "").setAppPrice("");
//                        SysData.grabOrderMap.get(orderInfo.getId() + "").setGrabStatus("????????????");
//                        SysData.grabOrderMap.get(orderInfo.getId() + "").setGrabTime(null);
//                        OutticketHandler.grabState.remove(orderInfo.getId());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
////					String grabStatus = "????????????";
////					orderInfo.setOrderStatus("????????????");
////					InitUtil.orderRemind(orderInfo.getId(), grabStatus, "????????????", false, "KNAppOutticketService");
//                }
//
//            }
//        }
//        // return "sucess";
//    }
//
//    private String priceCalender(OrderInfo orderInfo, String cookie, CloseableHttpClient httpclient, RequestConfig defaultRequestConfig) {
//        String back = null;
//        try {
//            HttpPost post = new HttpPost("/h5/pip/book/PriceCalender.json");
//            com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
//            com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
//            com.alibaba.fastjson.JSONObject json1 = new com.alibaba.fastjson.JSONObject();
//            jsonArray.add(json1);
//            json1.put("orgCode", orderInfo.getDep());
//            json1.put("dstCode", orderInfo.getArr());
//            json1.put("takeoffTime", orderInfo.getDepTime().substring(0, 10));
//            json1.put("ioBount", "0");
//            json.put("odInfo", jsonArray);
//            //		String jsonObject = "{\"odInfo\":[{\"orgCode\":"+orderInfo.getDep()+"\"goPricePointUUID\":[{\"tripType\":\"I\",\"transpart\":\"1\",\"pricePointUUID\":\""+verifyPostParam.get("pricePointUUID")+"\"}]}";
//            post.setConfig(defaultRequestConfig);
//            HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//            StringEntity entity = new StringEntity(JSON.toJSONString(json), Charset.forName("UTF-8"));
//            post.setEntity(entity);
//            post.setHeader("Referer", "https://wx.flycua.com/h5/");
//            post.setHeader("Cookie", cookie);
//            post.setHeader("Content-Type", "application/json;charset=UTF-8");
//            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//            if (!SysData.useAbuyunProxy) {
//                post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                        new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//                post.setHeader("Proxy-Connection", "keep-alive");
//            }
//            post.setHeader("Host", "wx.flycua.com");
//            post.setHeader("isB2C", "NO");
//            post.setHeader("isWechat", "H5");
//            CloseableHttpResponse response = httpclient.execute(target, post);
//            back = EntityUtils.toString(response.getEntity(), "utf-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return back;
//    }
//
//    private boolean checkTime(String grabTime) {
//        long time = 0L;
//        try {
//            time = SysData.sdf_datetime.parse(grabTime).getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return false;
//        }
//        if ((System.currentTimeMillis() - time) / 1000 / 60 > 10) {
//            return false;
//        }
//        return true;
//    }
//
//    public Map<String, String> aliPayM(String url, String cookie) {
//        CloseableHttpClient httpclient = null;
//        CloseableHttpResponse response = null;
//        HttpGet get = null;
//        Map<String, String> resultMap = new HashMap<String, String>();
//        try {
//            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
//                    null, new TrustStrategy() {
//                        public boolean isTrusted(X509Certificate[] chain,
//                                                 String authType) throws CertificateException {
//                            return true;
//                        }
//                    }).build();
//
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                    sslContext,
//                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            BasicCookieStore cookieStore = new BasicCookieStore();
//            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
//                    .setDefaultCookieStore(cookieStore).build();
//            Integer timeout = Integer.valueOf(Integer.parseInt("70000"));
//
//            RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout)
//                    .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
//                    .setRedirectsEnabled(false)
//                    .setStaleConnectionCheckEnabled(true).build();
//
//            url = url.replaceAll(" ", "%20");
//            logger.info("????????????????????????url:" + url);
//            get = new HttpGet(url.substring(19));
//            defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout)
//                    .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
//                    .setRedirectsEnabled(false)
//                    .setStaleConnectionCheckEnabled(true).build();
//            get.setConfig(defaultRequestConfig);
//            HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//            get.setHeader("Host", "wx.flycua.com");
//            get.setHeader("Cookie", cookie);
//            get.setHeader("Content-Type", "text/html;charset=UTF-8");
//            get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            response = httpclient.execute(target, get);
//            Header[] location = response.getHeaders("Location");
//            String locationValue = "";
//            for (int i = 0; i < location.length; i++) {
//                locationValue = location[i].getValue();
//            }
//            logger.info("??????????????????url:" + locationValue);
//
//            resultMap.put("locationValue", locationValue);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("error", e);
//        }
//        return null;
//    }
//
//    private String createOrder(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//                               String cookie) throws Exception {
//        HttpPost post = new HttpPost("/h5/pip/book/createOrder.json");
//        String jsonObject = "{\"isRedPaper\":\"no\"}";
//        post.setConfig(defaultRequestConfig);
//        HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//        post.setConfig(defaultRequestConfig);
//        StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//        post.setEntity(entity);
//        post.setHeader("Referer", "https://wx.flycua.com/h5/home.html");
//        post.setHeader("Origin", "https://wx.flycua.com");
//        post.setHeader("Content-Type", "application/json;charset=UTF-8");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        post.setHeader("Host", "wx.flycua.com");
//        post.setHeader("Cookie", cookie);
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        post.setHeader("isB2C", "NO");
//        post.setHeader("isWechat", "H5");
//
//        CloseableHttpResponse response = httpclient.execute(target, post);
//        String back = EntityUtils.toString(response.getEntity(), "utf-8");
//
//        return back;
//    }
//
//    private String flightSearchagain(String cookie, OrderInfo orderInfo, HttpClientBuilder builder, BasicAuthCache authCache, CredentialsProvider credsProvider, RequestConfig defaultRequestConfig, CloseableHttpClient httpClient) {
//        String content = null;
//        for (int i = 0; i < 5; i++) {
//            try {
//                HttpPost post = new HttpPost("/h5/pip/book/flightSearch.json");
//                HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//                HttpClientContext context = HttpClientContext.create();
//                context.setAuthCache(authCache);
//                context.setTargetHost(target);
//                context.setRequestConfig(defaultRequestConfig);
//                context.setCredentialsProvider(credsProvider);
//                post.setConfig(defaultRequestConfig);
//                String jsonObject = "{\"tripType\":\"OW\",\"orgCode\":\"" + orderInfo.getDep().replace("BJS", "NAY").replace("SHA", "PVG") + "\",\"dstCode\":\"" + orderInfo.getArr().replace("BJS", "NAY").replace("SHA", "PVG") + "\",\"takeoffdate1\":\"" + orderInfo.getDepTime().substring(0, 10) + "\",\"takeoffdate2\":\"\"}";
//                StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//                post.setEntity(entity);
//                post.setHeader("Referer", "https://wx.flycua.com/h5/");
//                post.setHeader("Origin", "https://wx.flycua.com");
//                if (!SysData.useAbuyunProxy) {
//                    post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                            new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//                    post.setHeader("Proxy-Connection", "keep-alive");
//                }
//                post.setHeader("Content-Type", "application/json;charset=UTF-8");
//                post.setHeader("Host", "wx.flycua.com");
//                post.setHeader("Cookie", cookie);
//                post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//                post.setHeader("isB2C", "NO");
//                post.setHeader("isWechat", "H5");
//
//                CloseableHttpResponse response = httpClient.execute(target, post, context);
//                content = EntityUtils.toString(response.getEntity(), "utf-8");
//            } catch (Exception e) {
//                logger.error("error", e);
//            }
//            if (StringUtil.isNotEmpty(content) && !content.contains("??????????????????") && !content.contains("503 Service Temporarily Unavailable") && !content.contains("??????????????????????????????") && !content.contains("????????????")) {
//                break;
//            }
//        }
//        return content;
//    }
//
//    private Map<String, String> flightSearch(RequestConfig defaultRequestConfig, OrderInfo orderInfo, CloseableHttpClient httpclient,
//                                             BasicCookieStore cookieStore) throws Exception {
//
//        Map<String, Object> initPara = InitUtil.getProxyPara();
//        HttpClientBuilder builder = (HttpClientBuilder) initPara.get("builder");
//        BasicAuthCache authCache = (BasicAuthCache) initPara.get("authCache");
//        CredentialsProvider credsProvider = (CredentialsProvider) initPara.get("credsProvider");
//        defaultRequestConfig = (RequestConfig) initPara.get("defaultRequestConfig");
//        httpclient = builder.build();
//        CloseableHttpResponse response = null;
//        String content = "";
//
//        Map<String, String> resultMap = new HashMap<String, String>();
//        try {
//            try {
//                HttpPost post = new HttpPost("/h5/pip/book/flightSearch.json");
//                HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//                HttpClientContext context = HttpClientContext.create();
//                context.setAuthCache(authCache);
//                context.setTargetHost(target);
//                context.setRequestConfig(defaultRequestConfig);
//                context.setCredentialsProvider(credsProvider);
//                post.setConfig(defaultRequestConfig);
//                String jsonObject = "{\"tripType\":\"OW\",\"orgCode\":\"" + orderInfo.getDep().replace("BJS", "NAY").replace("SHA", "PVG") + "\",\"dstCode\":\"" + orderInfo.getArr().replace("BJS", "NAY").replace("SHA", "PVG") + "\",\"takeoffdate1\":\"" + orderInfo.getDepTime().substring(0, 10) + "\",\"takeoffdate2\":\"\"}";
//                StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//                post.setEntity(entity);
//                post.setHeader("Referer", "https://wx.flycua.com/h5/");
//                post.setHeader("Origin", "https://wx.flycua.com");
//                if (!SysData.useAbuyunProxy) {
//                    post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                            new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//                    post.setHeader("Proxy-Connection", "keep-alive");
//                }
//                post.setHeader("Content-Type", "application/json;charset=UTF-8");
//                post.setHeader("Host", "wx.flycua.com");
//                post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//                post.setHeader("isB2C", "NO");
//                post.setHeader("isWechat", "H5");
//
//                response = httpclient.execute(target, post, context);
//                content = EntityUtils.toString(response.getEntity(), "utf-8");
//            } catch (Exception e) {
//                logger.error("error", e);
//            } finally {
//                response.close();
//            }
//            List<Cookie> listCookie = cookieStore.getCookies();
//            String cookie = "";
//            for (int i = 0; i < listCookie.size(); i++) {
//                cookie += listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";";
//            }
//            resultMap.put("cookie", cookie);
//            resultMap.put("result", content);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("error", e);
//        }
//        return null;
//    }
//
//    public static void parseFlightInfo(String back, OrderInfo orderInfo, Map<String, String> verifyPostParam) throws Exception {
//        String flightNo = orderInfo.getFlightNo();
//        String price = orderInfo.getOutPrice();
//        JSONObject jo = new JSONObject(back);
//        String shoppingKey = jo.getString("shoppingKey");
//        JSONObject goFlightInfo = jo.getJSONObject("goFlightInfo");
//        //???????????????????????????????????????
//        JSONObject flightInfoObj = null;
//        JSONArray flightInfoArr = goFlightInfo.getJSONArray("flightInfo");
//        for (int i = 0; i < flightInfoArr.length(); i++) {
//            flightInfoObj = flightInfoArr.getJSONObject(i);
//            JSONObject flightSegsObj = null;
//            JSONArray flightSegsArr = flightInfoObj.getJSONArray("flightSegs");
//            for (int j = 0; j < flightSegsArr.length(); j++) {
//                flightSegsObj = flightSegsArr.getJSONObject(j);
//                String grabFlightNo = flightSegsObj.getString("flightNo");
//                if (!flightNo.equals(grabFlightNo)) {
//                    continue;
//                }
//                JSONObject brandSegObj = null;
//                JSONArray brandSegArr = flightSegsObj.getJSONArray("brandSeg");
//                for (int z = 0; z < brandSegArr.length(); z++) {
//                    brandSegObj = brandSegArr.getJSONObject(z);
//                    JSONObject priceObj = null;
//                    JSONArray priceArr = brandSegObj.getJSONArray("price");
//                    for (int q = 0; q < priceArr.length(); q++) {
//                        priceObj = priceArr.getJSONObject(q);
//                        String psgType = priceObj.getString("psgType");
//                        if (!"ADT".equals(psgType)) {
//                            continue;
//                        }
//                        String grabPrice = priceObj.getString("price");
//                        double grabPriceDoubleType = 0d;
//                        double priceDoubleType = 0d;
//                        if (StringUtil.isNotEmpty(grabPrice) && StringUtil.isNotEmpty(price)) {
//                            grabPriceDoubleType = Double.parseDouble(grabPrice);
//                            priceDoubleType = Double.parseDouble(price);
//                        }
//                        if (grabPriceDoubleType == priceDoubleType) {
//                            String cabinCode = brandSegObj.getString("cabinCode");      //??????
//                            String pricePointUUID = brandSegObj.getString("pricePointUUID");
//                            verifyPostParam.put("shoppingKey", shoppingKey);
//                            verifyPostParam.put("pricePointUUID", pricePointUUID);
//                            verifyPostParam.put("cabinCode", cabinCode);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private Map<String, String> selectFlight(Map<String, String> verifyPostParam, String cookie,
//                                             CloseableHttpClient httpclient, RequestConfig defaultRequestConfig) throws Exception {
//        Map<String, String> resultMap = new HashMap<String, String>();
//        HttpPost post = new HttpPost("/h5/pip/book/verify.json");
//        String jsonObject = "{\"shoppingKey\":\"" + verifyPostParam.get("shoppingKey") + "\",\"goPricePointUUID\":[{\"tripType\":\"I\",\"transpart\":\"1\",\"pricePointUUID\":\"" + verifyPostParam.get("pricePointUUID") + "\"}]}";
//        post.setConfig(defaultRequestConfig);
//        HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//        StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//        post.setEntity(entity);
//        post.setHeader("Referer", "https://wx.flycua.com/h5/home.html");
//        post.setHeader("Origin", "https://wx.flycua.com");
//        // cookie = "Secure; JSESSIONID=6616EA2C8FD0E8AE614FCCF3D13DC31C; Secure; _gscu_1693774232=53479146hbijp056; _gscu_1166988165=53480333upz7k311; __jsluid=df402ff8eac3c4071290581e554bf091; _gscbrs_1693774232=1; TY_SESSION_ID=8ddca21a-a484-410f-bddf-0a241dea91b9; tokenId=31DE632B9F896C9CA77D78ACA51554D6A4AF77654043DAD638E9F93B378F94E1FBA1ACD68493A9D2E8F83763E6AE37FD0C92CD37F13F17C47FAEC3B4D1348D48; Secure; _gscs_1693774232=t54701463mq3qbf82|pv:9";
//        post.setHeader("Cookie", cookie);
//        post.setHeader("Content-Type", "application/json;charset=UTF-8");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        post.setHeader("Host", "wx.flycua.com");
//        post.setHeader("isB2C", "NO");
//        post.setHeader("isWechat", "H5");
//        CloseableHttpResponse response = httpclient.execute(target, post);
//        String back = EntityUtils.toString(response.getEntity(), "utf-8");
//        //outPrint(target,post,null,cookie,jsonObject,back);
//        if (StringUtil.isNotEmpty(back) && back.contains("503 Service Temporarily Unavailable")) {
//            resultMap.put("error", "503????????????");
//            return resultMap;
//        }
//        if (StringUtil.isNotEmpty(back) && back.contains("????????????")) {
//            resultMap.put("error", "????????????");
//            return resultMap;
//        }
//        JSONObject verifyObj = null;
//        try {
//            verifyObj = new JSONObject(back);
//            String departTime = "";
//            String arrayTime = "";
//            JSONObject goFlightInfosObj = verifyObj.getJSONObject("goFlightInfos");
//            JSONObject flightSegsObj = null;
//            JSONArray flightSegsArr = goFlightInfosObj.getJSONArray("flightSegs");
//            for (int i = 0; i < flightSegsArr.length(); i++) {
//                flightSegsObj = flightSegsArr.getJSONObject(i);
//                departTime = flightSegsObj.getString("departTime");
//                arrayTime = flightSegsObj.getString("arrivalTime");
//                resultMap.put("departTime", departTime);
//                resultMap.put("arrayTime", arrayTime);
//            }
//        } catch (Exception e) {
//            logger.error("verifyObjError", e);
//            logger.info("verifyObjError:" + back);
//            String errorMsg = getErrorMsg(verifyObj);
//            resultMap.put("error", errorMsg);
//        }
//        return resultMap;
//    }
//
//    private String getErrorMsg(JSONObject verifyObj) throws Exception {
//        JSONObject object = verifyObj.getJSONObject("commonRes");
//        String errorMsg = object.getString("message");
//        return errorMsg;
//    }
//
//    private Map<String, String> ancilSearch(String cookie, RequestConfig defaultRequestConfig,
//                                            CloseableHttpClient httpclient, Map<String, String> paramMap, OrderInfo orderInfo) throws Exception {
//        Map<String, String> resultMap = new HashMap<String, String>();
//        String arrival = orderInfo.getArr();
//        String departure = orderInfo.getDep();
//        String flightNo = orderInfo.getFlightNo();
//        String arrayTime = paramMap.get("arrayTime");
//        String departTime = paramMap.get("departTime");
//        HttpPost post = new HttpPost("/h5/pip/book/ancilSearch.json");
//        String jsonObject = "{\"ancillaryType\":\"ALL\",\"adultNum\":\"1\",\"childNum\":\"0\",\"infantNum\":\"0\",\"odInfo\":[{\"airline\":\"KN\",\"arriveTime\":\"" + arrayTime + "\",\"takeoffTime\":\"" + departTime + "\",\"dstCode\":\"" + arrival + "\",\"orgCode\":\"" + departure + "\",\"ioBount\":\"O\",\"flightNo\":\"" + flightNo + "\"}]}";
//        logger.info("??????????????????:" + jsonObject);
//        HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//        post.setConfig(defaultRequestConfig);
//        StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//        post.setEntity(entity);
//        post.setHeader("Referer", "https://wx.flycua.com/h5/");
//        post.setHeader("Origin", "https://wx.flycua.com");
//        post.setHeader("Content-Type", "application/json;charset=UTF-8");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        post.setHeader("Host", "wx.flycua.com");
//        post.setHeader("Cookie", cookie);
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        post.setHeader("isB2C", "NO");
//        post.setHeader("isWechat", "H5");
//        CloseableHttpResponse response = httpclient.execute(target, post);
//        String back = EntityUtils.toString(response.getEntity(), "utf-8");
//        logger.info("?????????????????????:" + back);
//        JSONObject ancilSearchObj = new JSONObject(back);
//        String ancilShoppingKey = ancilSearchObj.getString("ancilShoppingKey");
//        JSONObject goAncilTypeObj = null;
//        JSONArray goAncilTypeArr = ancilSearchObj.getJSONArray("goAncilType");
//        String orgAirportName = "";
//        String dstAirportName = "";
//        for (int i = 0; i < goAncilTypeArr.length(); i++) {
//            goAncilTypeObj = goAncilTypeArr.getJSONObject(i);
//            JSONArray ancilODInfoArr = goAncilTypeObj.getJSONArray("ancilODInfo");
//            JSONObject ancilODInfoObj = ancilODInfoArr.getJSONObject(0);
//            JSONObject orgAirport = ancilODInfoObj.getJSONObject("orgAirport");
//            JSONObject dstAirport = ancilODInfoObj.getJSONObject("dstAirport");
//            orgAirportName = orgAirport.getString("airportName");
//            dstAirportName = dstAirport.getString("airportName");
//        }
//        logger.info("ancilShoppingKey:" + ancilShoppingKey);
//        logger.info("ancilSearch:" + back);
//        resultMap.put("ancilShoppingKey", ancilShoppingKey);
//        resultMap.put("ancilSearch", back);
//        resultMap.put("orgAirportName", orgAirportName);
//        resultMap.put("dstAirportName", dstAirportName);
//        return resultMap;
//    }
//
//    private String getContactId(String cookie, RequestConfig defaultRequestConfig, CloseableHttpClient httpclient, OrderInfo orderInfo, AccountInfo accountInfo) throws Exception {
//        String linkMan = accountInfo.getContact();
//        String mobile = accountInfo.getTelPhone();
//        if (StringUtil.isNotEmpty(mobile)) {
//            mobile = mobile.replace(",", "");
//        }
//        HttpPost post = new HttpPost("/h5/pip/book/verifyContact.json");
//        String jsonObject = "{\"contactName\":\"" + linkMan + "\",\"contactMobile\":\"" + mobile + "\"}";
//        post.setConfig(defaultRequestConfig);
//        HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//        post.setConfig(defaultRequestConfig);
//        StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//        post.setEntity(entity);
//        post.setHeader("Referer", "https://wx.flycua.com/h5/home.html");
//        post.setHeader("Origin", "https://wx.flycua.com");
//        post.setHeader("Content-Type", "application/json;charset=UTF-8");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        post.setHeader("Host", "wx.flycua.com");
//        post.setHeader("Cookie", cookie);
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        post.setHeader("isB2C", "NO");
//        post.setHeader("isWechat", "H5");
//        CloseableHttpResponse response = httpclient.execute(target, post);
//        String back = EntityUtils.toString(response.getEntity(), "utf-8");
//        logger.info(orderInfo.getOrderNo() + "verifyContactResult:" + back);
//        JSONObject verifyContactObj = new JSONObject(back);
//        String msg = "";
//        try {
//            msg = verifyContactObj.getString("msg").toString();
//        } catch (Exception e) {
//            logger.error("verifyContactResultError" + e);
//            logger.info(orderInfo.getOrderNo() + "verifyContactResultError:" + back);
//            return "ERROR:";
//        }
//        if (msg == null || "null".equalsIgnoreCase(msg)) {
//            logger.info(orderInfo.getOrderNo() + "?????????????????????id:" + back);
//            return "ERROR:?????????????????????id,????????????????????????????????????????????????????????????????????????????????????";
//        }
//        return msg;
//    }
//
//    private String orderConfirm(String cookie, RequestConfig defaultRequestConfig, CloseableHttpClient httpclient,
//                                Map<String, String> resultMap, OrderInfo orderInfo, AccountInfo accountInfo, String msg, List<String> passengerId) throws Exception {
//        String departureDate = orderInfo.getDepTime().substring(0, 10);
//        String linkMan = accountInfo.getContact();
//        String mobile = accountInfo.getTelPhone();
//        if (StringUtil.isNotEmpty(mobile)) {
//            mobile = mobile.replaceAll(",", "");
//        }
//        String flightNo = orderInfo.getFlightNo();
//        String arrival = orderInfo.getArr();
//        String departure = orderInfo.getDep();
//        String ancilShoppingKey = resultMap.get("ancilShoppingKey");
//        String dstAirportName = resultMap.get("dstAirportName");
//        String orgAirportName = resultMap.get("orgAirportName");
//        HttpPost post = new HttpPost("/h5/pip/book/orderConfirm.json");
//        StringBuffer orderConfirmPostParam = new StringBuffer();
//        String flightWeek = dateToWeek(departureDate);
//        orderConfirmPostParam.append("{\"ancillaryShoppingKey\":\"" + ancilShoppingKey + "\",\"contactName\":\"" + linkMan + "\",\"contactMobile\":\"" + mobile + "\",\"contactId\":\"" + msg + "\",\"passengerIds\":[");
//        for (int i = 0; i < passengerId.size(); i++) {
//            orderConfirmPostParam.append("{\"id\":" + passengerId.get(i) + ",\"ancillaryGroups\":[{\"ancillaries\":[],\"flightNo\":\"" + flightNo + "\",\"dstAirport\":{\"airportCode\":\"" + arrival + "\",\"airportName\":\"" + dstAirportName + "\"},\"flightDate\":\"" + departureDate + "\",\"flightWeek\":\"" + flightWeek + "\",\"orgAirport\":{\"airportCode\":\"" + departure + "\",\"airportName\":\"" + orgAirportName + "\"},\"transpart\":\"1\",\"tripTypeString\":\"??????\"}]},");
//        }
//        orderConfirmPostParam.delete(orderConfirmPostParam.length() - 1, orderConfirmPostParam.length());
//        orderConfirmPostParam.append("]}");
//        // append(",\"carGroups\":[]")
//        post.setConfig(defaultRequestConfig);
//        HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//        StringEntity entity = new StringEntity(orderConfirmPostParam.toString(), Charset.forName("UTF-8"));
//        post.setEntity(entity);
//        post.setHeader("Referer", "https://wx.flycua.com/h5/");
//        post.setHeader("Content-Type", "application/json;charset=UTF-8");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        post.setHeader("Host", "wx.flycua.com");
//        post.setHeader("Cookie", cookie);
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        post.setHeader("isB2C", "NO");
//        post.setHeader("isWechat", "H5");
//        CloseableHttpResponse response = httpclient.execute(target, post);
//        String back = EntityUtils.toString(response.getEntity(), "utf-8");
//        return back;
//    }
//
//    public static String dateToWeek(String datetime) {
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
//        String[] weekDays = {"??????", "??????", "??????", "??????", "??????", "??????", "??????"};
//        Calendar cal = Calendar.getInstance(); // ??????????????????
//        Date datet = null;
//        try {
//            datet = f.parse(datetime);
//            cal.setTime(datet);
//        } catch (ParseException e) {
//            logger.error(e);
//        }
//        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // ?????????????????????????????????
//        if (w < 0)
//            w = 0;
//        return weekDays[w];
//    }
//
//    private Map<String, String> selectPayMethod(String back, String payType) throws Exception {
//        Map<String, String> resultMap = new HashMap<String, String>();
//        JSONObject orderConfirmObj = new JSONObject(back);
//        JSONObject paymentMethodsObj = null;
//        String paymentType = "";
//        String paymentCode = "";
//        JSONArray paymentMethodsArr = orderConfirmObj.getJSONArray("paymentMethods");
//        for (int i = 0; i < paymentMethodsArr.length(); i++) {
//            paymentMethodsObj = paymentMethodsArr.getJSONObject(i);
//            String providerName = paymentMethodsObj.getString("providerName");
//            if (StringUtil.isNotEmpty(payType) && "zfb".equals(payType)) {
//                if (!"?????????WAP".equals(providerName)) {
//                    continue;
//                }
//            } else {
//                if (!"????????????".equals(providerName)) {
//                    continue;
//                }
//            }
//            paymentType = paymentMethodsObj.getString("paymentType");
//            paymentCode = paymentMethodsObj.getString("paymentCode");
//        }
//        resultMap.put("paymentType", paymentType);
//        resultMap.put("paymentCode", paymentCode);
//        return resultMap;
//    }
//
//    private String payment(RequestConfig defaultRequestConfig, CloseableHttpClient httpclient, String cookie,
//                           String paymentType, String paymentCode, String orderNo) throws Exception {
//        HttpPost post = new HttpPost("/h5/pay/payment.json");
//        String jsonObject = "{\"paymentType\":\"" + paymentType + "\",\"paymentCode\":\"" + paymentCode + "\",\"orderNum\":\"" + orderNo + "\"}";
//        post.setConfig(defaultRequestConfig);
//        HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//        StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//        post.setEntity(entity);
//        post.setHeader("Referer", "https://wx.flycua.com/h5/");
//        post.setHeader("Content-Type", "application/json;charset=UTF-8");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        post.setHeader("Host", "wx.flycua.com");
//        post.setHeader("Cookie", cookie);
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        post.setHeader("isB2C", "NO");
//        post.setHeader("isWechat", "H5");
//        CloseableHttpResponse response = httpclient.execute(target, post);
//        String back = EntityUtils.toString(response.getEntity(), "utf-8");
//        logger.info("???????????????:" + back);
//        return back;
//    }
//
//    private boolean getDetail(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie, OrderInfo orderInfo) throws Exception {
//        //????????????
//        boolean isSuccees = true;
//        HttpPost post = new HttpPost("/h5/pip/ticketOrder/orderDetail.json");
//        HttpHost target = new HttpHost("wx.flycua.com", 443, "https");
//        post.setConfig(defaultRequestConfig);
//        String jsonObject = "{\"orderNo\":\"" + orderInfo.getOrderNoNew() + "\"}";
//        logger.info("???????????????" + jsonObject + ",Cookie:" + cookie);
//        StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//        post.setEntity(entity);
//        post.setConfig(defaultRequestConfig);
//        post.setHeader("Content-Type", "application/json;charset=UTF-8");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        post.setHeader("Host", "wx.flycua.com");
//        post.setHeader("Cookie", cookie);
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        post.setHeader("isB2C", "NO");
//        post.setHeader("isWechat", "H5");
//        String back = "";
//        try {
//            CloseableHttpResponse response = httpclient.execute(target, post);
//            back = EntityUtils.toString(response.getEntity(), "utf-8");
//        } catch (Exception e) {
//            post.abort();
//        }
//        logger.info(orderInfo.getOrderNo() + "--??????????????????????????????:" + back);
//        JSONObject backObj = new JSONObject(back);
//        JSONObject pipOrderDetail = backObj.getJSONObject("pipOrderDetail");
//        JSONObject components = pipOrderDetail.getJSONObject("components");
//        JSONObject air = components.getJSONObject("AIR");
//        JSONArray goItinerarysIterator = air.getJSONArray("goItinerarys");
//        Map<String, String> passengerMap = new HashMap<String, String>();
//        for (int i = 0; i < goItinerarysIterator.length(); i++) {
//            JSONObject goItinerarysIteratorObj = goItinerarysIterator.getJSONObject(i);
//            JSONArray passengers = goItinerarysIteratorObj.getJSONArray("passengers");
//            for (int j = 0; j < passengers.length(); j++) {
//                JSONObject passenger = passengers.getJSONObject(j);
//                String name = passenger.getString("name"); //?????????
//                JSONObject details = passenger.getJSONArray("details").getJSONObject(0);
//                String ticketNo = details.getString("ticketNo"); //??????
//                if (StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(ticketNo)) {
//                    passengerMap.put(name, ticketNo);
//                }
//            }
//        }
//        JSONObject payInfos = pipOrderDetail.getJSONArray("payInfos").getJSONObject(0);
//        String transactionNo = payInfos.getString("transactionNo"); //???????????????
//        List<PaxInfo> paxInfos = orderInfo.getPaxInfos();
//        for (int i = 0; i < paxInfos.size(); i++) {
//            PaxInfo paxInfo = paxInfos.get(i);
//            String passengerName = paxInfo.getPaxName();
//            String idcard = paxInfo.getCardNo();
//            String ticketNo = passengerMap.get(passengerName);
//            if (StringUtil.isNotEmpty(ticketNo)) {
//                paxInfo.setTicketNo(ticketNo);
//            } else {
//                logger.info(orderInfo.getOrderNo() + "--" + paxInfo.getPaxName() + "--??????????????????");
//                isSuccees = false;
//            }
//        }
//        if (StringUtil.isNotEmpty(transactionNo)) {
//            orderInfo.setPayCode(transactionNo);
//        }
//        return isSuccees;
//    }
//
//    private List<String> getSamePassengerList(CloseableHttpClient httpclient, String cookie, OrderInfo orderInfo, RequestConfig defaultRequestConfig, String memberId) throws Exception {
//        List<String> passengerIds = new ArrayList<String>();
//        Map<String, String> passengersMap = new HashMap<String, String>();
//        for (PaxInfo paxInfo : orderInfo.getPaxInfos()) {
//            String passengerName = paxInfo.getPaxName();
//            String idCard = paxInfo.getCardNo();
//            passengersMap.put(passengerName.trim() + "@" + idCard.trim(), passengerName + "@" + idCard);
//        }
//
//        //?????????????????????
//        HttpPost post = new HttpPost("/ffp/member/profile");
//        String jsonObject = "{\"mode\":\"query\",\"memberId\":\"" + memberId + "\"}";
//        jsonObject = aesInvokeFunction("encrypt", jsonObject);
//        HttpHost target = new HttpHost("higo.flycua.com", 443, "https");
//        StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//        post.setEntity(entity);
//        post.setHeader("Referer", "https://higo.flycua.com/hh/html/passenger.html");
//        post.setHeader("Cookie", cookie);
//        post.setHeader("Content-Type", "application/json;charset=UTF-8");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        post.setHeader("Host", "higo.flycua.com");
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        CloseableHttpResponse response = httpclient.execute(target, post);
//        String back = EntityUtils.toString(response.getEntity(), "utf-8");
////		logger.info("???????????????????????????:"+back);
//        //outPrint(target,post,null,cookie,jsonObject,back);
//        JSONObject json = new JSONObject(back);
//        String errordesc = json.getString("errordesc");
//        back = aesInvokeFunction("decrypt", errordesc);
//        if (StringUtil.isNotEmpty(back) && back.contains("please login first")) {
//            passengerIds.add("please login first");
//            return passengerIds;
//        }
//        json = new JSONObject(back);
//        String errorcode = json.getString("errorcode");
//        if (!"0000".equals(errorcode)) {
//            return null;
//        }
//        JSONArray passengerArr = json.getJSONArray("passenger");
//        for (int i = 0; i < passengerArr.length(); i++) {
//            JSONObject passengerObj = passengerArr.getJSONObject(i);
//            String name = passengerObj.getString("name");
//            String idno = passengerObj.getString("idno");
//            String id = passengerObj.getString("id");
//            if (passengersMap.get(name.trim() + "@" + idno.trim()) != null) {
//                logger.info("????????????????????????" + id);
//                // id = "42375800";
//                passengerIds.add(id);
//            }
//        }
//        return passengerIds;
//    }
//
//    private String getMemberId(CloseableHttpClient httpclient, String cookie,
//                               RequestConfig defaultRequestConfig, String orderId) throws Exception {
//        // ????????????id
//        HttpPost post = new HttpPost("/ffp/ssologin");
//        String jsonObject = "{\"mode\":\"login\"}";
//        jsonObject = aesInvokeFunction("encrypt", jsonObject);
//        HttpHost target = new HttpHost("higo.flycua.com", 443, "https");
//        StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//        post.setConfig(defaultRequestConfig);
//        post.setEntity(entity);
//        post.setHeader("Referer", "https://higo.flycua.com/hh/index.html");
//        post.setHeader("Cookie", cookie);
//        post.setHeader("Content-Type", "application/json;charset=UTF-8");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        post.setHeader("Host", "higo.flycua.com");
//        if (!SysData.useAbuyunProxy) {
//            post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                    new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//            post.setHeader("Proxy-Connection", "keep-alive");
//        }
//        CloseableHttpResponse response = httpclient.execute(target, post);
//        String back = EntityUtils.toString(response.getEntity());
//
//        JSONObject json = new JSONObject(back);
//        String errordesc = json.getString("errordesc");
//        errordesc = aesInvokeFunction("decrypt", errordesc);
//        if (StringUtil.isNotEmpty(errordesc) && errordesc.contains("????????????????????????")) {
//            return "????????????????????????";
//        }
//        if (StringUtil.isNotEmpty(errordesc) && errordesc.contains("????????????????????????")) {
//            return "????????????????????????";
//        }
//        if (StringUtil.isNotEmpty(errordesc) && errordesc.contains("please login first")) {
//            return "please login first";
//        }
//        JSONObject errordescJson = new JSONObject(errordesc);
//        String memberId = errordescJson.getString("memberId");
//        String[] c = cookie.split(";");
//        for (String a: c) {
//            if (a.contains(memberId)) {
//                memberId = a.split("=")[1];
//            }
//        }
//        return memberId;
//    }
//
//    private String aesInvokeFunction(String function, String param) {
//        try {
//            ScriptEngineManager manager = new ScriptEngineManager();
//            ScriptEngine engine = manager.getEngineByName("js");
//            String path = SysData.exeRealPath + "\\aes.min.js";
//            FileReader reader = new FileReader(path); // ??????????????????
//            engine.eval(reader);
//            if (engine instanceof Invocable) {
//                Invocable invoke = (Invocable) engine;
//                param = (String) invoke.invokeFunction(function, param);
//            }
//        } catch (Exception e) {
//            param = "";
//        }
////		logger.info("??????JS??????:" + param);
//        return param;
//    }
//
//    public synchronized void synchronAccounInfo(AccountInfo accountInfo, String cookie) {
//        AccountInfo accountInfo1 = SysData.accountMap.get(accountInfo.getAirCompany()).get(accountInfo.getId());
//        if (accountInfo1 != null) {
//            if (StringUtil.isNotEmpty(cookie)) {
//                accountInfo1.setLoginState(cookie);
//                accountInfo1.setLoginTime(new Date().getTime() + "");
//            } else {
//                String loginTime = accountInfo1.getLoginTime();
//                if ((new Date().getTime() - Long.valueOf(loginTime)) / 1000 > 30) {
//                    accountInfo1.setLoginState(cookie);
//                }
//            }
//        }
//    }
//
//    public String getOrderList(CloseableHttpClient httpclient, String cookie, RequestConfig defaultRequestConfig) throws IOException {
//        // ????????????id
//        String back = "";
//        CloseableHttpResponse response = null;
//        try {
//            HttpPost post = new HttpPost("https://wx.flycua.com/h5/ticketorder/orderListData.html");
//            // String jsonObject = "{\"mode\":\"login\"}";
//            // jsonObject = aesInvokeFunction("encrypt", jsonObject);
//            HttpHost target = new HttpHost("higo.flycua.com", 443, "https");
//            // StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//            List <NameValuePair> nameValue = new ArrayList <NameValuePair>();
//            nameValue.add(new BasicNameValuePair("orderType", "WITHOUTCANCELED"));
//            nameValue.add(new BasicNameValuePair("beginDate", ""));
//            nameValue.add(new BasicNameValuePair("endDate", ""));
//            nameValue.add(new BasicNameValuePair("queryType", "false"));
//            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
//            post.setConfig(defaultRequestConfig);
//            post.setHeader("Referer", "https://wx.flycua.com/h5/ticketorder/orderList.html");
//            post.setHeader("Cookie", cookie);
//            post.setHeader("Content-Type", "application/json;charset=UTF-8");
//            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//            post.setHeader("Host", "wx.flycua.com");
//            if (!SysData.useAbuyunProxy) {
//                post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//                        new StringBuilder(SysData.abuyunUser).append(":").append(SysData.abuyunPwd).toString().getBytes("utf-8")));
//                post.setHeader("Proxy-Connection", "keep-alive");
//            }
//            response = httpclient.execute(target, post);
//            back = EntityUtils.toString(response.getEntity());
//        } catch (Exception e) {
//            logger.error("????????????????????????" + e);
//        } finally {
//            response.close();
//        }
//        return back;
//    }
//}
