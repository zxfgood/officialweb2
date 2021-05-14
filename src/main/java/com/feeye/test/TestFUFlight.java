package com.feeye.test;

import com.alibaba.fastjson.JSONObject;
import com.feeye.service.processor.DailiyunService;
import com.feeye.util.FingerPrintUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;

/**
 * @author xs
 * @date 2019/3/24 20:09
 */
public class TestFUFlight {
    public static void main(String[] args)  throws Exception {

        //随机获取一个代理ip
        String ip_port = DailiyunService.getRandomIp(50);
        String ProxyAddr = ip_port.split(":")[0];   //代理IP地址
        int ProxyPort = 57114;               // 端口
        //代理云账号密码
        String proxyUser ="feeyeapp";
        String proxyPass ="feeye789";
        HttpHost targetHost = new HttpHost(ProxyAddr, ProxyPort,"http");

//          //阿布云
//        HttpHost proxy = new HttpHost("http-dyn.abuyun.com", 9020, "http");
//        String proxyUser = "HL7F5JF125K85K8D";
//        String proxyPass = "FC393F432489B2E5";

        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            // 初始化SSL连接
            sslsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials(proxyUser, proxyPass));
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        int timeout = 60000;
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
                .setStaleConnectionCheckEnabled(true)
                .setProxy(targetHost)
                .build();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setDefaultCookieStore(cookieStore)
                .setDefaultCredentialsProvider(credsProvider)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        try {
            HttpHost target = new HttpHost("www.fuzhou-air.cn", 80, "http");
            HttpPost post = new HttpPost("/frontend/api/flight.action");

            // Add AuthCache to the execution context
            HttpClientContext context = HttpClientContext.create();
            context.setCredentialsProvider(credsProvider);
            context.setAuthCache(authCache);
            context.setRequestConfig(defaultRequestConfig);

            post.setConfig(defaultRequestConfig);




            //设置请求头
            post.setHeader("Host","www.fuzhou-air.cn");
            post.setHeader("Connection","keep-alive");
//            post.setHeader("Content-Length","494");
            post.setHeader("Accept","application/json, text/javascript, */*; q=0.01");
            post.setHeader("Origin","http://www.fuzhou-air.cn");
            post.setHeader("X-Requested-With","XMLHttpRequest");
            post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
            post.setHeader("Content-Type","application/text; charset=UTF-8");
            post.setHeader("Referer","http://www.fuzhou-air.cn/b2c/static/flightSearch.html");
            post.setHeader("Accept-Encoding","gzip, deflate");
            post.setHeader("Accept-Language","zh-CN,zh;q=0.9");
            post.setHeader("Cookie","JSESSIONID=533B3FCAE741174F9282B1BD9E2BB6A1.d1; firstload=true; pgv_pvi=3765098496; pgv_si=s4637303808; Hm_lvt_a00f62fdb46e90b43fab5e43afc52e40=1554012295,1554012359; Hm_lpvt_a00f62fdb46e90b43fab5e43afc52e40=1554012359; nofaker_name=true; notice-read-ETweb=false; Hm_lvt_f29176088d5434e4f620bde4925b4e49=1554012295,1554012359,1554012381; FU_historyOfSearch=%7B%22orgCityCode%22%3A%22FOC%22%2C%22dstCityCode%22%3A%22HRB%22%2C%22orgDate%22%3A%222019-04-25%22%2C%22dstDate%22%3A%22%22%2C%22adult%22%3A%221%22%2C%22child%22%3A%220%22%2C%22infant%22%3A%220%22%2C%22trip%22%3A%22ONEWAY%22%7D; Hm_lpvt_f29176088d5434e4f620bde4925b4e49=1554012407");

            //设置请求参数
            String desc = FingerPrintUtil.getDesc();
            JSONObject queryObject = new JSONObject();
            queryObject.put("index", "0");
            queryObject.put("orgCity", "FOC");
            queryObject.put("dstCity", "HRB");
            queryObject.put("flightdate", "2019-04-25");
            //单程
            queryObject.put("tripType", "ONEWAY");
            queryObject.put("times", "648632428");
            queryObject.put("desc","JeUEPgZXFsnSDYkzgko8AvwnmEA3cvHX4bGIcY1ylfDX0JIhwueOTsJoTyMIRrt%2BiMJyj/2d1wSsvDoIm%2Bc3JrJHI%2BXSp9SpsY5vWZC4L1I=");
//            //是否需要验证码
//            if (isVerify) {
//                queryObject.put("vc", verifyCode);
//            }
            StringEntity entity = new StringEntity(queryObject.toString(), Charset.forName("UTF-8"));
            post.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(target, post);
            HttpEntity entity1 = response.getEntity();
            String back = EntityUtils.toString(entity1, "gbk");
            try {
                System.out.println("----------------------------------------");
                System.out.println(back);
            } finally {
                response.close();
            }
        } finally {
//            httpclient.close();
        }
    }
}
