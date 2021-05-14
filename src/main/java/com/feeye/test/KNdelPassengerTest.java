package com.feeye.test;

import com.feeye.service.processor.DailiyunService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author xs
 * @description
 * @date 2019/11/18
 */
public class KNdelPassengerTest {
    private static final Logger logger = LoggerFactory.getLogger(KNdelPassengerTest.class);


    public static void main(String[] args) {

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
        //代理云ip
        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(dailiyunProxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("feeyeapp", "feeye789"));

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(20 *1000)
                .setConnectTimeout(20 *1000)
                .setConnectionRequestTimeout(20 *1000)
                .setProxy(dailiyunProxy)
                .build();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setSSLSocketFactory(sslsf)
                .setDefaultCredentialsProvider(credsProvider)
                .setDefaultCookieStore(cookieStore)
                .build();

        getJsContent("", "decrypt", "laKfhCrTEallBLGOtqiFlcsJuaTjdOZlcUL2TaNqyehzCfypp5vWzZTctUnmI3oz");


//        {"mode":"query","memberId":"025891600"}
//        {"mode":"query","memberId":"100064856"}
//        {"mode":"query","memberId":"027728256"}
//        {"mode":"delete","memberId":"100074671","id":48446354}
//        {"mode":"delete","memberId":"100229835","id":48412012}


        new Thread(new Runnable() {
            @Override
            public void run() {
                String cookie = "_gscu_1693774232=791364106l2r2u14; _gscbrs_1693774232=1; tokenId=44B61C478ED1174F88E62B56718BA8591140995B687A5B9838E9F93B378F94E17934A710375489209C543BB680540D23CE1FA01E1E2428D859118F2D963E48A8; tokenUUID=aefa21a16c2341ed83fed9e0c540fbe5; __jsluid_s=f7bea4874efbbe85457b1b77bf45f330; JSESSIONID=AE68C80FC720A892AC6F4A2947A9C9FE; _gscs_1693774232=791364106sml0814|pv:2; udmp_cm_sign_1693774232=1";
                KNdelPassengerTest.deletePassengerInfo(httpclient, defaultRequestConfig, cookie, "025891600");
            }
        }).start();


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    String s ="_gscu_1693774232=747775977fxwef16; _gscbrs_1693774232=1; _gscs_1693774232=79136620qscfsh19|pv:1; udmp_cm_sign_1693774232=1; tokenId=EE9B57FBBBDFC339293C0B576CC2A9947BD35DAAA06FC11238E9F93B378F94E11FBE8D44CD1704B9B417D814C9ECD8CFAF99FC02447309E1475FC39F0EE12C7B; tokenUUID=daaf2bc9e3334b03b2d2db45f03c2f22; JSESSIONID=E8617B7E3E07200756F2DBA339315CF7; __jsluid_s=6f38a197a9efc0216d79dbcf9fe29d0f";
//                    KNdelPassengerTest.deletePassengerInfo(httpclient, defaultRequestConfig, s, "100074671");
//                }
//            }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = "_gscu_1693774232=79136897n8lgxg59; _gscs_1693774232=7913689733gvik59|pv:1; _gscbrs_1693774232=1; tokenId=F6802A5344BDC244A7EF14B2D7565A63290FB499D2717FBA38E9F93B378F94E11FBE8D44CD1704B9A84CB1772CF7DBF6AF99FC02447309E1475FC39F0EE12C7B; tokenUUID=de32fb7d134b4c3b9d4fea59a7c30eb2; __jsluid_s=13fc7643c5b1e362fa5bd6dd84e6da39; JSESSIONID=EB1DFA6016D1559609125A5898B96666";
                KNdelPassengerTest.deletePassengerInfo(httpclient, defaultRequestConfig, s, "100229835");
            }
        }).start();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String s = "__jsluid_s=f477ba83341c7e34dae577dac2784292; JSESSIONID=B68A1B6E417CA4224EF65F1B2017395A; _gscu_1693774232=790844017ar3bi14; _gscbrs_1693774232=1; udmp_cm_sign_1693774232=1; _gscs_1693774232=t791018466nfn8z14|pv:3; tokenId=EE9B57FBBBDFC339534946DC540E3A10D9DA5ACC5D922F1B38E9F93B378F94E1AA158445A11E4C4D0CEF1CEF122F934AAF99FC02447309E1475FC39F0EE12C7B; tokenUUID=6699e62510864566a77bc5c707473f2b";
//                KNdelPassengerTest.deletePassengerInfo(httpclient, defaultRequestConfig, s, "100074671");
//            }
//        }).start();


    }


    /**
     * 删除账号下的乘机人信息
     *
     * @param httpclient
     * @param defaultRequestConfig
     * @param cookie
     * @param memberId
     * @return
     * @throws Exception
     */
    private static void deletePassengerInfo(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie, String memberId) {
        CloseableHttpResponse response = null;
        try {
            // 获取账号下的常用乘客表
            HttpHost target = new HttpHost("higo.flycua.com", 443, "https");
            HttpPost post = new HttpPost("/ffp/member/profile");
            String jsonObject = "{\"mode\":\"query\",\"memberId\":\"" + memberId + "\"}";
            jsonObject = getJsContent("", "encrypt", jsonObject);
            StringEntity entity = new StringEntity(jsonObject, Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setConfig(defaultRequestConfig);
            post.setHeader("Referer", "https://higo.flycua.com/hh/html/passenger.html?rand=0.1313124611206259&from=null");
            post.setHeader("Cookie", cookie);
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            post.setHeader("Host", "higo.flycua.com");
            response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            JSONObject json = new JSONObject(back);
            String errordesc = json.getString("errordesc");
            back = getJsContent("", "decrypt", errordesc);
            if (StringUtils.isNotEmpty(back) && back.contains("html")) {
                //代理云ip
                String ip_port = DailiyunService.getRandomIp(50);
                String proxyIp = ip_port.split(":")[0];
                int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
                HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");
                System.out.println("换代理IP获取乘机人列表" + proxyIp);
                defaultRequestConfig = RequestConfig.custom()
                        .setSocketTimeout(20 * 1000)
                        .setConnectTimeout(20 * 1000)
                        .setConnectionRequestTimeout(20 * 1000)
                        .setProxy(dailiyunProxy)
                        .build();
                deletePassengerInfo(httpclient, defaultRequestConfig, cookie, memberId);
            }
            if (StringUtils.isNotEmpty(back) && back.contains("please login first")) {
                logger.info("账号未登录");
            }
            json = new JSONObject(back);
            String errorcode = json.getString("errorcode");
            if (!"0000".equals(errorcode)) {
                logger.info("乘机人返回内容不正确");
            }
            JSONArray passengerArr = json.getJSONArray("passenger");
            System.out.println("账号下乘机人数量: " + passengerArr.length());
            for (int i = 0; i < passengerArr.length(); i++) {
                JSONObject passengerObj = passengerArr.getJSONObject(i);
                String passengerId = passengerObj.getString("id");
//                Thread.sleep(2 * 1000);
                //清空乘客表
                try {
                    post = new HttpPost("/ffp/member/profile");
                    jsonObject = "{\"mode\":\"delete\",\"memberId\":\"" + memberId + "\",\"id\":" + passengerId + "}";
                    String param = getJsContent("", "encrypt", jsonObject);
                    entity = new StringEntity(param, Charset.forName("UTF-8"));
                    post.setEntity(entity);
                    post.setConfig(defaultRequestConfig);
                    post.setHeader("Referer", "https://higo.flycua.com/hh/html/passenger.html?rand=0.1313124611206259&from=null");
                    post.setHeader("Cookie", cookie);
                    post.setHeader("Content-Type", "application/json;charset=UTF-8");
                    post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
                    post.setHeader("Host", "higo.flycua.com");
                    response = httpclient.execute(target, post);
                    back = EntityUtils.toString(response.getEntity(), "utf-8");
                    if (StringUtils.isNotEmpty(back) && back.contains("html")) {
//                        Thread.sleep(3 * 1000);
                        //代理云ip
                        String ip_port = DailiyunService.getRandomIp(50);
                        String proxyIp = ip_port.split(":")[0];
                        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
                        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");
                        System.out.println("换代理IP删除乘机人" + proxyIp);
                        defaultRequestConfig = RequestConfig.custom()
                                .setSocketTimeout(20 * 1000)
                                .setConnectTimeout(20 * 1000)
                                .setConnectionRequestTimeout(20 * 1000)
                                .setProxy(dailiyunProxy)
                                .build();
                        continue;
                    }
                } catch (Exception e) {
                    logger.error("删除常用乘机人错误", e);
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consumeQuietly(response.getEntity());
                    response.close();
                } catch (Exception e) {
                }
            }
        }
    }


    /**
     * 执行指定的js文件中的方法
     * @param jsUrl js文件地址
     * @param jsMether 执行的js方法
     * @param param 传入参数
     * @return
     */
    public static String getJsContent(String jsUrl, String jsMether, String param) {
        String result = "";
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            String jsFileName = "C:\\新建文件夹\\aes.min.js";
            FileReader reader = new FileReader(jsFileName);
            engine.eval(reader);
            if ((engine instanceof Invocable)) {
                Invocable invoke = (Invocable) engine;
                result = (String) invoke.invokeFunction(jsMether, param);
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return result;
    }
}
