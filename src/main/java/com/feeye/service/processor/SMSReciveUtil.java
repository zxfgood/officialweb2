package com.feeye.service.processor;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xs
 * 手机短信验证码接收工具类
 * @description
 * @date 2019/10/21
 */
public class SMSReciveUtil {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SMSReciveUtil.class);
    private static final int timeout = 20000;
    private final String[] phoneArray = {"19965412404", "17109324198", "17109324205", "17109324122", "17109324121", "17109324199", "17109324200", "17109324197", "17109324203", "18866478714"};

    public static void main(String[] args) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("1", "123");
        map.put("1", "456");
        map.put("1", "4567");

        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        getSMSContent("18866478574", 1571820260000L);
                    }
                }
            }).start();
        }
    }

    public static String getSMSContent(String tel, Long sendTime) {
        SSLContext sslcontext = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            sslcontext = createIgnoreVerifySSL();
            //设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            BasicScheme proxyAuth = new BasicScheme();
            try {
                proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
            } catch (MalformedChallengeException e1) {
                logger.error("error", e1);
            }

            String ip_port = DailiyunService.getRandomIp(50);
            String proxyIp = ip_port.split(":")[0];  //代理IP地址
            int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //代理IP端口
            HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            //代理云账号密码
            String proxyUser = "feeyeapp";
            String proxyPass = "feeye789";
            //TODO 从配置文件读取
            credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));


            RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
//                    .setProxy(dailiyunProxy)
                    .build();
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setDefaultRequestConfig(config)
                    .setDefaultCredentialsProvider(credsProvider)
                    .setConnectionManager(connManager)
                    .build();

            //获取可用的短信接码手机号
            String phoneNumber = "";
            try {
                List<String> telList = new ArrayList<String>();
                HttpGet get = new HttpGet("https://www.yinsiduanxin.com");
                get.setConfig(config);
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
                response = httpclient.execute(get);
                String back = EntityUtils.toString(response.getEntity(), "utf-8");
                if (StringUtils.isNotEmpty(back)) {
                    Document doc = Jsoup.parse(back);
                    Elements elements = doc.getElementsByClass("layuiadmin-big-font card-phone");
                    for (int i = 0; i < elements.size(); i++) {
                        //截取手机号
                        String telNumber = elements.get(i).text().substring(4);
                        telList.add(i, telNumber);
                    }
                    //随机取一个手机号码
                    Random random = new Random();
                    int i = random.nextInt(telList.size());
                    phoneNumber = telList.get(i);
                    System.out.println("短信接码手机号: " + phoneNumber);
                    logger.info("短信接码手机号: " + phoneNumber);
                }
            } catch (Exception e) {
                logger.error("error", e);
            }

            HttpGet get = new HttpGet("https://www.yinsiduanxin.com/message/" + tel + ".html");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
            get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String("feeyeapp:feeye789".getBytes(StandardCharsets.UTF_8)));
            get.setHeader("Proxy-Connection", "keep-alive");
            get.setConfig(config);
            response = httpclient.execute(get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            if (StringUtils.isNotEmpty(back)) {
                Document doc = Jsoup.parse(back);
                Elements elements = doc.getElementsByClass("layui-table");
                if (elements != null && elements.size() > 0) {
                    elements = elements.get(0).getElementsByTag("tr");
                } else {
                    logger.info("手机接码平台返回错误信息：" + back);
                }
                for (int i = 1; i < elements.size(); i++) {
                    String smsContext = elements.get(i).text();
                    System.out.println("第" + i + "次 " + smsContext);
                    break;
//                    if (StringUtils.isNotEmpty(smsContext) && smsContext.contains("东方航空")) {
////                        String reciveTimeStr = elements.get(i).getElementsByTag("script").html().split(";")[0];
////                        reciveTimeStr = reciveTimeStr.replace("gettime = diff_time(\"", "").replace("\")", "");
//                        String reciveTimeStr = elements.get(i).getElementsByTag("td").get(2).text();
//                        String context = elements.get(i).getElementsByTag("td").get(1).text();
//                        Long reciveTime = parseTimeString2Long(reciveTimeStr);
//                        //计算时间差值
//                        long differTime = (reciveTime - sendTime) / 1000;
//                        if (differTime > 0 && differTime < 50) {
//                            result = getMathResult(context);
//                            System.out.println("短信内容：" + smsContext + " 计算结果：" + result);
//                            logger.info("短信内容：" + smsContext + " 计算结果：" + result);
//                            return result;
//                        }
//                    }
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
        return result;
    }


    /**
     * 绕过ssl验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * 计算验证码结果
     *
     * @param sms
     * @return
     */
    public static String getMathResult(String sms) {
        System.out.println(sms);
        String result = "";
        String regex = "【东方航空】验证码为:([0-9]+.[0-9]+)，尊敬的旅客您好，请保护好您的短信验证码并输入计算结果。";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(sms);
        while (m.find()) {
            if (org.apache.commons.lang.StringUtils.isNotEmpty(m.group(1))) {
                String smsCode = m.group(1);
                logger.info("smsCode:" + smsCode);
                if (smsCode.contains("+")) {
                    String[] results = smsCode.split("\\+");
                    result = ((int) (Float.parseFloat(results[0]) + Float.parseFloat(results[1]))) + "";
                    logger.info("result:" + result);
                } else if (smsCode.contains("-")) {
                    String[] results = smsCode.split("-");
                    result = ((int) (Float.parseFloat(results[0]) - Float.parseFloat(results[1]))) + "";
                    logger.info("result:" + result);
                } else if (smsCode.contains("×")) {
                    String[] results = smsCode.split("×");
                    result = ((int) (Float.parseFloat(results[0]) * Float.parseFloat(results[1]))) + "";
                    logger.info("result:" + result);
                } else if (smsCode.contains("÷")) {
                    String[] results = smsCode.split("÷");
                    result = ((int) (Float.parseFloat(results[0]) / Float.parseFloat(results[1]))) + "";
                    logger.info("result:" + result);
                } else {
                    String[] results = smsCode.split("\\+");
                    result = ((int) (Float.parseFloat(results[0]) + Float.parseFloat(results[1]))) + "";
                }
            }
        }
        return result;
    }

    /**
     * 时间转换
     *
     * @param timeString
     * @return
     */
    public static Long parseTimeString2Long(String timeString) {
        if ((timeString == null) || (timeString.equals(""))) {
            return null;
        }
        Date date = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = new Date(dateFormat.parse(timeString).getTime());
        } catch (Exception e) {
            logger.error("error", e);
        }
        return date.getTime();
    }
}
