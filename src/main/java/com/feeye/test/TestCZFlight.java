package com.feeye.test;

import java.net.URLDecoder;

/**
 * @author xs
 * @date 2019/3/28 22:41
 */
public class TestCZFlight {
    public static void main(String[] args) throws Exception{
        String s = "324-2335065486%2B%C2%ED%C3%C0%B7%BC%3B324-2335065487%2B%CB%EF%D2%E5"; //加密串
        System.out.println(URLDecoder.decode(s,"gb2312"));

//        String str = "324-2335065486+马美芳";
//        System.out.println(URLEncoder.encode(str,"gb2312"));

        String s2 = "324-2335065486+马美芳;324-2335065487+孙义";
        StringBuffer ticketNo = new StringBuffer();
        String[] splits = s2.split(";");
        if(splits.length ==1) {
            String[] split = s2.split("\\+");
            ticketNo.append(split[0]);
        }else {
            for (int i = 0; i <splits.length ; i++) {
                String[] split = splits[i].split("\\+");
                ticketNo.append(split[0]+";");
            }
            ticketNo.delete(ticketNo.length()-1,ticketNo.length());
        }
        System.out.println(ticketNo.toString());
    }


//        //随机获取一个代理ip
//        String ip_port = DailiyunService.getRandomIp(50);
//        String ProxyAddr = ip_port.split(":")[0];   //代理IP地址
//        int ProxyPort = 57114;               // 端口
//        //代理云账号密码
//        String proxyUser = "feeyeapp";
//        String proxyPass = "feeye789";
//        HttpHost targetHost = new HttpHost(ProxyAddr, ProxyPort, "http");
//        SSLConnectionSocketFactory sslsf = null;
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        try {
//            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
//                // 信任所有
//                public boolean isTrusted(X509Certificate[] chain, String authType) {
//                    return true;
//                }
//            }).build();
//            // 初始化SSL连接
//            sslsf = new SSLConnectionSocketFactory(sslContext,
//                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(
//                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
//                new UsernamePasswordCredentials(proxyUser, proxyPass));
//        // Create AuthCache instance
//        AuthCache authCache = new BasicAuthCache();
//        // Generate BASIC scheme object and add it to the local auth cache
//        BasicScheme basicAuth = new BasicScheme();
//        authCache.put(targetHost, basicAuth);
//
//        int timeout = 60000;
//        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
//                .setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
//                .setStaleConnectionCheckEnabled(true)
//                .setProxy(targetHost)
//                .build();
//
//        CloseableHttpClient httpclient = HttpClients.custom()
//                .setSSLSocketFactory(sslsf)
//                .setDefaultCookieStore(cookieStore)
//                .setDefaultCredentialsProvider(credsProvider)
//                .setDefaultRequestConfig(defaultRequestConfig)
//                .build();
//        try {
//            HttpPost post = new HttpPost("/portal/flight/direct/query");
//            //请求主机
//            HttpHost target = new HttpHost("b2c.csair.com", 80, "http");
//            String str = "{\"depCity\":\"CAN\",\"arrCity\":\"NKG\",\"flightDate\":\"20190415\",\"adultNum\":\"1\",\"childNum\":\"0\",\"infantNum\":\"0\"\n" +
//                    ",\"cabinOrder\":\"0\",\"airLine\":1,\"flyType\":0,\"international\":\"0\",\"action\":\"0\",\"segType\":\"1\",\"cache\":0,\"preUrl\"\n" +
//                    ":\"\",\"isMember\":\"\"}";
//            post.setHeader("Content-Type", "application/json");
//            post.setHeader("Host", "b2c.csair.com");
//            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
//            StringEntity entity = new StringEntity(str, Charset.forName("UTF-8"));
//            post.setEntity(entity);
//            CloseableHttpResponse response = httpclient.execute(target, post);
//            String back = EntityUtils.toString(response.getEntity(), "utf-8");
//            System.out.println(back);
//        } catch (IOException e) {
//        }
    }

