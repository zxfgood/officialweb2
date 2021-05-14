package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.feeye.bean.CabinInfo;
import com.feeye.util.Api;
import com.feeye.util.DateUtil;
import com.feeye.util.FingerPrintUtil;
import com.feeye.util.PropertiesUtils;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QnrCreateOrderService {
    private static final Logger logger = Logger.getLogger( QnrCreateOrderService.class );
    private static final int timeout = 50000;
    String dlyAccount = "feeyeapp:feeye789";
	private static Map<String, Map<String,String>> cookieMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
		String orderJSon = "{\n" +
				"    \"account\": \"13037284859_feeye123\",\n" +
				"    \"airline\": \"HO\",\n" +
				"    \"billNo\": \"FY210223193347161408002781846545\",\n" +
				"    \"childrenMobile\": \"18617070230\",\n" +
				"    \"code\": \"b3ced52479dd27e01bcf4501935ba5de\",\n" +
				"    \"codePassword\": \"\",\n" +
				"    \"creditNo\": \"213\",\n" +
				"    \"cvv\": \"123213213\",\n" +
				"    \"drawerType\": \"GW\",\n" +
				"    \"email\": \"1437015921@qq.com\",\n" +
				"    \"flights\": [\n" +
				"        {\n" +
				"            \"airline\": \"HO\",\n" +
				"            \"arrival\": \"DYG\",\n" +
				"            \"cabin\": \"D\",\n" +
				"            \"departure\": \"NKG\",\n" +
				"            \"departureDate\": \"2021-05-17\",\n" +
				"            \"departureTime\": \"11:40:00\",\n" +
				"            \"fType\": \"MEMBER_FARE\",\n" +
				"            \"flightNo\": \"KN5977\",\n" +
				"            \"price\": \"10000\"\n" +
				"        }\n" +
				"    ],\n" +
				"    \"id\": \"61603305\",\n" +
				"    \"ifUsedCoupon\": false,\n" +
				"    \"isOutticket\": \"true\",\n" +
				"    \"linkMan\": \"张晓峰\",\n" +
				"    \"mainusername\": \"policytest\",\n" +
				"    \"matchCabin\": false,\n" +
				"    \"mobile\": \"13479647334\",\n" +
				"    \"orderNo\": \"OFG08T6VE105BM008183\",\n" +
				"    \"orderTime\": \"2021-02-23 19:34:56\",\n" +
				"    \"otheraccount\": \"112069_/JfzhBV4r5IXtkhj7zBfxJblWP+G5Nn5\",\n" +
				"    \"passengers\": [\n" +
				"        {\n" +
				"            \"birthday\": \"1992-05-02\",\n" +
				"            \"id\": \"75696011\",\n" +
				"            \"idcard\": \"140424199205021273\",\n" +
				"            \"passengerCardType\": \"身份证\",\n" +
				"            \"passengerName\": \"李春丰\",\n" +
				"            \"passengerSex\": \"男\",\n" +
				"            \"mobile\": \"13479647334\",\n" +
				"            \"passengerType\": \"成人\"\n" +
				"        }\n" +
				"    ],\n" +
				"    \"payType\": \"hf\",\n" +
				"    \"platType\": \"tongcheng\",\n" +
				"    \"qiangpiao\": \"\",\n" +
				"    \"username\": \"policytest\",\n" +
				"    \"ytype\": \"特价专享\"\n" +
				"}";
		String key = "policytest61603305";
		// HttpUtils.executorMap.put(key,HttpUtils.createThreadPoolExecutor(10));
		new QnrCreateOrderService().StartCreateOrder(orderJSon,0,0,0);
		// new QnrCreateOrderService().loginTest();
    }

    long nowDateTime = new Date().getTime(); // 获取当前时间，后面步骤要根据该时间点做超时处理
    public String StartCreateOrder(String orderJson , int requestType , int retryCount , int exceptionCount) {
        if (StringUtils.isEmpty( orderJson )) {
            return "ERROR:数据不完整";
        }
        System.out.println( "获取到的数据HO:" + orderJson );
        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();// 一个cookies
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial( null , new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain , String authType) throws CertificateException {
                    return true;
                }
            } ).build();
            sslsf = new SSLConnectionSocketFactory( sslContext ,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
            // 初始化SSL连接
        } catch (Exception e) {
            logger.error( "error" , e );
        }
        BasicScheme proxyAuth = new BasicScheme();
        try {
            proxyAuth.processChallenge( new BasicHeader( AUTH.PROXY_AUTH , "BASIC realm=default" ) );
        } catch (MalformedChallengeException e1) {
            logger.error( "error" , e1 );
        }
        String mainUser = "";
        try {
        	 JSONObject json = new JSONObject( orderJson );
        	 mainUser = json.getString( "mainusername" );
		} catch (Exception e) {
		}
      //代理云ip
        String ip_port = "";
        if("yunfei".equals(mainUser)){
        	ip_port = "tps115.kdlapi.com:15818";
        }
        try {
      			if(StringUtils.isEmpty(ip_port)){
      				ip_port = // "192.168.1.100:809";
							DailiyunService.getRandomDailiIp(50);
	      			if(StringUtils.isNotEmpty(ip_port)){
	      				ip_port += ":808";
	      			}
      			}
//      			ip_port = DailiyunService.getRandomDailiIp(500);
      		} catch (Exception e) {
      		}
      		if(StringUtils.isEmpty(ip_port)){
      			if(retryCount<5){
      				try {
      					Thread.sleep(2000);
      				} catch (InterruptedException e) {
      				}
      				return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
      			}
      			try {
      				JSONObject json = new JSONObject(orderJson);
      				String childrenUser = json.getString("username");
      				String order_id = json.getString("id");
      				String billNo = "";
      				try {
      	                billNo = json.getString( "billNo" );
      	            } catch (Exception e) {
      	            }
      				sendCreateOrderInfo( "error" , "获取代理IP异常" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                             "true" , billNo , requestType ,mainUser);
      				return "获取代理IP异常";
      			} catch (Exception e) {
      			}
      		}
        String proxyIp = ip_port.split( ":" )[0];
        int proxyPort = Integer.parseInt( ip_port.split( ":" )[1] );
        HttpHost dailiyunProxy = new HttpHost(proxyIp , proxyPort , "http");

		BasicAuthCache authCache = new BasicAuthCache();
		authCache.put(dailiyunProxy , proxyAuth);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		//代理云账号密码
		/*String dlyAccount = d
				PropertiesUtils.getPropertiesValue("config" , "dlyAccount");*/
		String[] dlyAccountInfo = dlyAccount.split(":");
		credsProvider.setCredentials(new AuthScope(dailiyunProxy) , new UsernamePasswordCredentials(dlyAccountInfo[0] , dlyAccountInfo[1]));
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.setProxy(dailiyunProxy)
				.setStaleConnectionCheckEnabled(true)
				.build();
		HttpClientContext context = HttpClientContext.create();
		HttpHost target = new HttpHost("m.flight.qunar.com",443,"https");
		context.setAuthCache( authCache );
		context.setTargetHost( target );
		context.setRequestConfig( defaultRequestConfig );
		context.setCredentialsProvider( credsProvider );
		SocketConfig socketConfig = SocketConfig.custom()
				.setSoKeepAlive(false).setSoLinger(1)
				.setSoReuseAddress(true)
				.setSoTimeout(timeout)
				.setTcpNoDelay(true).build();
		System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", sslsf )  // 用来配置支持的协议
				.build();
		// 共享连接池
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(100);
		connectionManager.setDefaultMaxPerRoute(20);
		CloseableHttpClient httpclient = HttpClientBuilder.create()
				.setConnectionManager(connectionManager)
				.setConnectionManagerShared(true)
				.setDefaultCookieStore(cookieStore)
				.setDefaultSocketConfig(socketConfig)
				.setDefaultCredentialsProvider(credsProvider)
				.disableRedirectHandling()
				.build();

        String childrenUser = "";
        String order_id = "";
        String billNo = "";
        try {
            String cookie = "";
            JSONObject json = new JSONObject( orderJson );
            try {
                billNo = json.getString( "billNo" );
            } catch (Exception e) {
            }
            order_id = json.getString( "id" );
            System.out.println(order_id+"取到的IP:"+ip_port);
            String account = json.getString( "account" );
            childrenUser = json.getString( "username" );
            mainUser = json.getString( "mainusername" );
            JSONArray passengers = json.getJSONArray( "passengers" );
            boolean defaultAccount = false; // 是否因为注册失败，使用默认密码登录
            String cancelKey = mainUser + order_id;
            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
              Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
              	System.out.println(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        System.out.println( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/

            // //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "查询航班" );

            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
              Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
              System.out.println(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        System.out.println( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/
            defaultRequestConfig = RequestConfig.custom()
    				.setSocketTimeout(timeout)
    				.setConnectTimeout(timeout)
    				.setConnectionRequestTimeout(timeout)
    				.setProxy(dailiyunProxy)
    				.setStaleConnectionCheckEnabled(true)
    				.build();
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "开始登陆" );
           	String accountMsg = json.getString("account");
           	String accout = accountMsg.split("_")[0];
           	String logingKey = mainUser + accout;
            Map<String,String> loginMap = cookieMap.get(logingKey);
           /* String msg = "{\n" +
					"  \"cookie\": \"fid=edd864ca-f188-42df-871f-b1a3e8e4dbd1; Domain=qunar.com; Expires=Mon, 30-May-2089 09:12:07 GMT; Path=/;QN43=2; Domain=.qunar.com; Expires=Tue, 10-Aug-2021 05:58:01 GMT; Path=/;QN42=%E9%9A%8F%E9%A3%8E%E4%B9%8B%E4%BA%BA; Domain=.qunar.com; Expires=Tue, 10-Aug-2021 05:58:01 GMT; Path=/;_q=U.pnkerrk1334; Domain=.qunar.com; Expires=Tue, 10-Aug-2021 05:58:01 GMT; Path=/;_t=27122758; Domain=.qunar.com; Expires=Tue, 10-Aug-2021 05:58:01 GMT; Path=/;csrfToken=JdehjpBLci4EEBikmGavNdoF6ZcrdVnw; Domain=.qunar.com; Expires=Tue, 10-Aug-2021 05:58:01 GMT; Path=/;_s=s_UW5KN3JYI2HF42M5SRO4RVJD7E; Domain=.qunar.com; Expires=Tue, 10-Aug-2021 05:58:01 GMT; Path=/;_i=ueHP7nCrvJ-XmmtYUeFQRlPBnpVX; Domain=.qunar.com; Expires=Mon, 30-May-2089 09:12:08 GMT; Path=/;_v=--pKAcC7yHM079BH5yP2OZ_f4BwUepyyq3NMScmpx91tiGXpGtzOw_1LsLzi_L0VO7hpJ3IcaKKUacI7n5HKKbHYHuHtOMyEJkJDluhaze2ArttuYA5iy3aUh-DyJlavRwbF2LF9_IlOC_SKV-3OnCkeW1GtcC1_ra39SNJjH2HG;Path=/;Domain=.qunar.com;Expires=Tue, 10 Aug 2021 05:58:01 GMT;HTTPOnly;_vi=9A6UqNXPp8IADH7gQDTlR8zR5NdwvIIpQ225hPlidMD_o-5F6VqFeX1zMv9YT65LVFssUiCuhhDtL_RiaSzbAXVy7Fp9lwOZKCV4VQBXo3QCOzwCJ33Ny3Fxb3z4XMua_v3sgu4-EsEvQUtKbzt4DeWMBUwchsZF3Pd18agnvLo7;Path=/;Domain=.qunar.com;Expires=Tue, 10 Aug 2021 05:58:01 GMT;HTTPOnly\"\n" +
					"}";
            loginMap = JSON.parseObject(msg,Map.class);*/
            if (loginMap==null || loginMap.size() == 0) {
				for (int i = 0; i < 3; i++) {
					loginMap = login(defaultRequestConfig , httpclient , orderJson);
					System.out.println(order_id + "登陆返回:" + JSON.toJSONString(loginMap));
					System.out.println(logingKey + "=" + JSON.toJSONString(loginMap));
					if (loginMap!=null && loginMap.containsKey("error")) {
						String errorMsg = loginMap.get("error");
						if(cookie.contains("密码输入错误") || cookie.contains("锁定")){
							sendCreateOrderInfo( "error" , errorMsg , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" ,
									"false" , "true" , billNo , requestType ,mainUser);
							return "“error:登录失败";
						}else {
							if(exceptionCount < 5){
								exceptionCount++;
								try {
									if(httpclient!=null){
										httpclient.close();
									}
								} catch (Exception e) {
								}
								return StartCreateOrder( orderJson , requestType , retryCount , exceptionCount );
							}
							sendCreateOrderInfo( "error" , "登陆失败" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" ,
									"false" , "true" , billNo , requestType ,mainUser);
							return "登陆失败";
						}
					}
					if (loginMap!=null && StringUtils.isNotEmpty(loginMap.get("cookie"))) {
						break;
					}
					Thread.sleep(3000);
				}
			}
            if (loginMap==null || loginMap.size() == 0) {
				try {
					if(httpclient!=null){
						httpclient.close();
					}
				} catch (Exception e) {
				}
				return StartCreateOrder( orderJson , requestType , retryCount , exceptionCount );
			}
            if (loginMap!=null && StringUtils.isEmpty(loginMap.get("cookie"))) {
                sendCreateOrderInfo( "error" , "登录异常" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                        "true" , billNo , requestType ,mainUser);
                return "ERROR:登录异常";
            }
            /*if (DateUtil.IsRunningTimeOut( nowDateTime , 5 * 1000 * 60 )) {
                //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "创单超时，停止创单" );
                sendCreateOrderInfo( "error" , "创单超时，停止创单" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" ,
                        "false" , "true" , billNo , requestType,mainUser);
                System.out.println( order_id + "创单超时，停止创单" );
                return "创单超时，停止创单";
            }*/


            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
              Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
              System.out.println(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        System.out.println( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/
            /*context.setTargetHost(target);*/
            cookieMap.put(logingKey,loginMap);
			/*String procookie = PropertiesUtil.getProperty("HOcookie.properties", logingKey);
			System.out.println(order_id + "帐号:" + logingKey + "HO获取到的登录信息:" + procookie);
			if (StringUtils.isEmpty(procookie) || "null".equals(procookie)) {
				PropertiesUtil.setProperty("HOcookie.properties", logingKey, JSON.toJSONString(loginMap));
			}*/
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "选择航班" );
            String allCheckToken = "";
            for (int i = 0; i < 5; i++) {
                allCheckToken = selectFlights( httpclient , defaultRequestConfig,orderJson,loginMap);
				if (StringUtils.isNotEmpty(allCheckToken ) && allCheckToken.contains("查询过于频繁")) {
					return StartCreateOrder( orderJson , requestType , retryCount , exceptionCount );
				}
                if (StringUtils.isNotEmpty(allCheckToken ) && allCheckToken.contains("error")) {
					if (StringUtils.isEmpty( allCheckToken )) {
						sendCreateOrderInfo( "error" , allCheckToken , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
								"true" , billNo , requestType ,mainUser);
						return "ERROR:选择不到该航班";
					}
                }
                if (StringUtils.isNotEmpty(allCheckToken)) {
                	break;
				}
                Thread.sleep(3000);
                //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "选择航班失败，重新选择" );
            }
            if (StringUtils.isEmpty( allCheckToken )) {
                sendCreateOrderInfo( "error" , "选择不到该航班" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                        "true" , billNo , requestType ,mainUser);
                return "ERROR:选择不到该航班";
            }
            if (StringUtils.isNotEmpty(allCheckToken ) && allCheckToken.contains("error")) {
                sendCreateOrderInfo( "error" , allCheckToken , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                        "true" , billNo , requestType ,mainUser);
                return "ERROR:选择航班失败";
            }
            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
              Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
				System.out.println(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        System.out.println( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "添加联系人" );
            Map<String,String> passIDMap = new HashMap<>();
            for (int i = 0; i < 5; i++) {
                try {
					passIDMap = addContactPax( httpclient , defaultRequestConfig , loginMap , orderJson,allCheckToken);
					if (passIDMap.size() == 0) {
						continue;
					}
					if (passIDMap.containsKey("error")) {
						sendCreateOrderInfo( "error" , passIDMap.get("error") , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" ,
								"false" , "true" , billNo , requestType ,mainUser);
						return "ERROR:添加联系人失败";
					}
					if (passIDMap.size() > 0) {
						break;
					}
                } catch (Exception e) {
                    continue;
                }
                break;
            }
            if (passIDMap.size() == 0) {
                    sendCreateOrderInfo( "error" , "添加联系人失败" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" ,
                            "false" , "true" , billNo , requestType ,mainUser);
                    return "ERROR:添加联系人失败";
            }
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "添加乘机人" );
            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
                Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
                System.out.println(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        System.out.println( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "查询乘机人信息" );
            /*int index = 0;
			Map<String,String> passMap = new HashMap<>();
			JSONArray passengerArray = new JSONObject(orderJson).getJSONArray("passengers");
            for (; index < 5; index++) {
                try {
					passMap = personQuery(defaultRequestConfig , httpclient , orderJson,loginMap);
					if (passMap.containsKey("error")) {
						sendCreateOrderInfo( "error" , "查询乘客信息失败:" + passMap.get("error") , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
								"true" , billNo , requestType ,mainUser);
						return "ERROR:查询乘客信息失败";
					}
					if (passengerArray.length() != passMap.size()) {
						Thread.sleep(6000);
						continue;
					} else {
						break;
					}
                } catch (Exception e) {
                    continue;
                }
            }
            if (passMap.size() == 0 || passMap.containsKey("error")) {
                sendCreateOrderInfo( "error" , "查询乘客信息失败" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                        "true" , billNo , requestType ,mainUser);
                return "ERROR:查询乘客信息失败";
            }*/
            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
                Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
              	System.out.println(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        System.out.println( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/
            /*if (DateUtil.IsRunningTimeOut( nowDateTime , 10 * 1000 * 60 )) {
                //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "创单超时，停止创单" );
                sendCreateOrderInfo( "error" , "创单超时，停止创单" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" ,
                        "false" , "true" , billNo , requestType ,mainUser);
                System.out.println( order_id + "创单超时，停止创单" );
                return "创单超时，停止创单";
            }*/

            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "创建订单" );
			Map<String,Object> bookingMap = new HashMap<>();
            for (int i = 0; i < 5; i++) {
            	bookingMap = booking( httpclient , defaultRequestConfig , orderJson , passIDMap , loginMap,allCheckToken);
            	if (bookingMap.size() == 0) {
            		Thread.sleep(5000);
            		continue;
				}
            	if (bookingMap.containsKey("error")) {
					sendCreateOrderInfo( "error" , "创建订单未知错误:" + bookingMap.get("error") , "" , childrenUser , "" , order_id , "" , "" , null , "" ,
							"" , "false" , "true" , billNo , requestType ,mainUser);
					return "ERROR:创建订单未知错误，请到官网确认是否创建成功";
				}
				break;
			}

            if (bookingMap.size() == 0 || bookingMap.containsKey("error")) {
                sendCreateOrderInfo( "error" , "创建订单未知错误:" + bookingMap.get("error") , "" , childrenUser , "" , order_id , "" , "" , null , "" ,
                        "" , "false" , "true" , billNo , requestType ,mainUser);
                return "ERROR:创建订单未知错误，请到官网确认是否创建成功";
            }
            System.out.println(order_id + "创建订单的参数:" + JSON.toJSONString(bookingMap));
			String orderNo = "";
					// (String) bookingMap.get("orderNo");
            sendCreateOrderInfo( "success" , "" , "" , childrenUser , account.split( "_" )[0] + "--" + orderNo , order_id , "" ,
                    "" , null , "" , "" , "false" , "true" , billNo , requestType ,mainUser);
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "选择支付方式" );
			String payType = json.getString( "payType" ) == null ? "" : json.getString( "payType" );
            String bankId = "";
            createOrder(httpclient,defaultRequestConfig,orderJson,bookingMap,loginMap,allCheckToken);
            if ("zfbjk".equals(payType)) {
            	String payInitMsg = "";
				//PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "开始支付" );
            	// queryDetail(httpclient,defaultRequestConfig,orderJson,bookingMap,loginMap);
				/*for (int i = 0;i < 4;i ++) {
					payInitMsg = payInit(httpclient,defaultRequestConfig,orderJson,bookingMap,loginMap);
					if ("success".equals(payInitMsg)) {
						break;
					}
					if (StringUtils.isNotEmpty(payInitMsg) && payInitMsg.contains("error")) {
						sendCreateOrderInfo( "error" , "进入支付失败,:" + payInitMsg , "" , childrenUser ,
								account.split( "_" )[0] + "--" + orderNo , order_id , "" , "" , null , payType , "" , "false" , "true" ,
								billNo , requestType,mainUser);
						return "ERROR:支付失败," + payInitMsg;
					}
					Thread.sleep(2000);
				}
				if (!"success".equals(payInitMsg)) {
					sendCreateOrderInfo( "error" , "进入支付失败,:" + payInitMsg , "" , childrenUser ,
							account.split( "_" )[0] + "--" + orderNo , order_id , "" , "" , null , payType , "" , "false" , "true" ,
							billNo , requestType,mainUser);
					return "ERROR:支付失败," + payInitMsg;
				}*/
				/*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
					Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
					Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
					System.out.println(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
					if (cancelTimeDate!= null && startTimeDate!=null) {
						if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
							//PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
							sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
									"true" , billNo , requestType ,mainUser);
							System.out.println( "已取消出票" );
							return "取消出票";
						}
					}
				}*/
				//PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "开始付款" );
                /*bankId = payOrder(httpclient,defaultRequestConfig,orderJson,bookingMap,loginMap);
                if (StringUtils.isEmpty(bankId) || !bankId.contains("success")) {
					sendCreateOrderInfo( "error" , "支付失败,请到官网确认支付结果:" + bankId , "" , childrenUser ,
							account.split( "_" )[0] + "--" + orderNo , order_id , "" , "" , null , payType , "" , "false" , "true" ,
							billNo , requestType,mainUser);
					return "ERROR:支付失败，只支持信用卡支付";
				}*/
            } else {
				sendCreateOrderInfo( "error" , "支付失败，只支持信用卡支付" , "" , childrenUser ,
						account.split( "_" )[0] + "--" + orderNo , order_id , "" , "" , null , payType , "" , "false" , "true" ,
						billNo , requestType,mainUser);
				return "ERROR:支付失败，只支持支付宝支付";
			}

            if ("xyk".equals( payType )) {
                payType = "信用卡";
            } else if ("ybhy".equals( payType )) {
                payType = "易宝会员";
            } else if ("hf".equals( payType )) {
                payType = "汇付";
            } else if ("zfbjk".equals( payType )) {
            	payType = "支付宝接口";
            } else {
                payType = "易宝";
            }
            if (StringUtils.isEmpty( bankId )) {
                sendCreateOrderInfo( "error" , "支付未知异常，请到官网检查是否已支付" , "" , childrenUser ,
                        orderNo , order_id , "" , "" , null , payType , "" , "false" , "true" ,
                        billNo , requestType,mainUser);
                return "ERROR:支付未知异常，请到官网检查是否已支付";
            }
            String payTransactionid1 = "";
            if (StringUtils.isNotEmpty( bankId ) && bankId.contains( "success" ) || bankId.contains( "SUCCESS" )) {
                sendCreateOrderInfo( "success" , "" , (String) bookingMap.get("payAmount"), childrenUser , orderNo , order_id , "true" , "" , null , payType , bankId.split("#")[1] , "false" , "true" ,
                        billNo , requestType,mainUser);
            }
            String payTransactionid = bankId.split("#")[1];
            // 回填
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "获取票号信息" );
            String result = null;
            String payPrice = "";
            Map<String, String> map = null;
            for (int detailIndex = 0; detailIndex < 20; detailIndex++) {
				Thread.sleep(30000);
                StringBuffer sb = new StringBuffer();
                try {
                    map = queryDetail( httpclient , defaultRequestConfig ,orderJson, bookingMap , loginMap);
                    if (map == null || map.size() == 0) {
						continue;
                    }
                    if (map.get( "payPrice" ) != null) {
                        payPrice = map.get( "payPrice" );
                        map.remove( "payPrice" );
                    }
                    /*if (map.get( "payTransactionid" ) != null) {
                        payTransactionid = map.get( "payTransactionid" );
                        map.remove( "payTransactionid" );
                    }*/
                    if(StringUtils.isNotEmpty(payTransactionid1)){
                    	payTransactionid = payTransactionid1;
                    }
                    sendCreateOrderInfo( "success" , "" , payPrice , childrenUser , account.split( "_" )[0] + "--" + orderNo ,
                            order_id , "true" , "" , null , payType , "" , "false" , "true" , billNo , requestType,mainUser);
                    for (String value : map.values()) {
                        if (StringUtils.isEmpty( value )) {
                            continue;
                        }
                        sb.append( value ).append( "#_#" );
                    }
                    result = sb.toString() + "@_@" + payTransactionid;

                    System.out.println( order_id + result.toString() );
                    String ticketnos[] = result.split( "@_@" );
                    if (ticketnos.length == 2) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "回填票号" );
                        // 判断获取的票号数是否与乘客数量相符合,不相符合重新
                        String[] ticketCount = ticketnos[0].split( "#_#" );
                        if (ticketCount.length == passengers.length() && !ticketnos[0].contains( "null" )) {
                            sendCreateOrderInfo( "success" , "" , payPrice , childrenUser , orderNo , order_id , "" , "true" , ticketnos[0] ,
                                    payType , ticketnos[1] , "false" , "true" , billNo , requestType,mainUser);
                            return "SUCCESS";
                        }
                    }
                } catch (Exception e) {
                    logger.error( "error" , e );
                }
            }
            if (map == null || map.size() == 0) {
                sendCreateOrderInfo( "error" , "未获取到票号" , payPrice , childrenUser , orderNo ,
                        order_id , "false" , "true" , "" , payType , "" , "false" , "true" , billNo , requestType,mainUser);
                return "error:未获取到票号";
            }
        } catch (Exception e) {
            logger.error( "error" , e );
            if(retryCount<5){
            	try {
					if(httpclient!=null){
						httpclient.close();
					}
				} catch (Exception e1) {
				}
            	return StartCreateOrder(orderJson, requestType, ++retryCount, exceptionCount);
            }
            sendCreateOrderInfo( "error" , "创单失败" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" , "true" ,
                    billNo , requestType,mainUser);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    logger.error( "error" , e );
                }
            }
        }
        return "SUCCESS";
    }


	public String payOrder(CloseableHttpClient httpclient , RequestConfig defaultRequestConfig , String orderJson , Map<String,String>  bookingMap, Map<String,String> loginMap) {
		CloseableHttpResponse response = null;
		String order_id = "";
		String mainusername = "";
		String[] dlyAccountInfo = dlyAccount.split( ":" );
		String proxyUser = dlyAccountInfo[0];
		String proxyPass = dlyAccountInfo[1];
		JSONObject json = null;
		String back = "";
		try {
			json = new JSONObject( orderJson );
			order_id = json.getString( "id" );
			mainusername = json.getString( "mainusername" );
		} catch (JSONException e) {
			logger.error( "error" , e );
		}
		String httpKey = mainusername + order_id;
		try{
			HttpPost post = new HttpPost("https://hoapp.juneyaoair.com/paymentService/queryGateway");
			Map<String, Object> payreqMap = new HashMap<>();
			payreqMap.put("paymentChannelCode","MOBILE");
			payreqMap.put("channelCode","MOBILE");
			payreqMap.put("platformInfo","android");
			payreqMap.put("clientVersion","6.3.0");
			payreqMap.put("versionCode",63000);
			payreqMap.put("token",loginMap.get("token"));
			payreqMap.put("ffpCardNo",loginMap.get("memberID"));
			payreqMap.put("ffpId",loginMap.get("id"));
			payreqMap.put("loginKeyInfo",loginMap.get("loginKeyInfo"));
			String payreqJson = JSON.toJSONString(payreqMap);
			System.out.println(httpKey + "HO支付网关参数:" + payreqJson);
			StringEntity s = new StringEntity(payreqJson, "utf-8");
			post.setEntity(s);
			post.setConfig(defaultRequestConfig);
			post.setHeader("channelCode", "MOBILE");
			post.setHeader("clientVersion", "6.3.0");
			post.setHeader("platformInfo", "android");
			post.setHeader("sign", "S6aCMRBQpVR+GUt2bRw58ve8ZrCK2EzKl5j8BR2zL0Zg9TnHJoDGboB6Z2+IJCS5");
			String timeStamp1 = System.currentTimeMillis() + "";
			post.setHeader("timeStamp", timeStamp1);
			post.setHeader("token", loginMap.get("token"));
			post.setHeader("versionCode", "63000");
			post.setHeader("Host", "hoapp.juneyaoair.com");
			post.setHeader("Accept-Encoding", "gzip");
			post.setHeader("Content-Type", "application/json; charset=utf-8");
			post.setHeader("User-Agent", "okhttp/3.10.0");
			post.setHeader("Proxy-Connection", "keep-alive");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			response = // HttpUtils.clientPost(null,post,httpclient,null,mainusername + order_id);
			         httpclient.execute(post);
					String payres = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(httpKey + "HO支付网关返回:" + payres);
			if (StringUtils.isEmpty(payres)) {
				back = "error:官网支付网关无信息返回，请手动确认是否支付";
			}
			if (payres.contains("resultCode")) {
				JSONObject payObj = new JSONObject(payres);
				String resultCode = payObj.getString("resultCode");
				if ("10001".equals(resultCode)) {
					back = "success";
				} else {
					back = "error:" + payObj.getString("errorInfo");
					return  back;
				}
			} else {
				if (back.length() > 255) {
					back = back.substring(0,200);
				}
				return back = "error:官网支付网关支付返回异常信息:" + back;
			}
			post = new HttpPost("https://hoapp.juneyaoair.com/paymentService/orderPay");
			String timeStamp = System.currentTimeMillis() + "";
			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put("channelCode","MOBILE");
			reqMap.put("platformInfo","android");
			reqMap.put("clientVersion","6.3.0");
			reqMap.put("versionCode",63000);
			reqMap.put("token",loginMap.get("token"));
			reqMap.put("channelOrderNo",bookingMap.get("channelOrderNo"));
			reqMap.put("orderNo",bookingMap.get("orderNo"));
			reqMap.put("orderType","D");
			reqMap.put("payType","D");
			reqMap.put("channelBuyDatetime", DateUtil.getCurrentDate("YYYYMMddHHmmss"));
			String payAmount = bookingMap.get("payAmount");
			double d = Double.valueOf(payAmount);
			DecimalFormat df = new DecimalFormat("#,#00.00#");
			reqMap.put("amount",df.format(d));
			reqMap.put("useScore","");
			reqMap.put("appsys","MOBILE");
			reqMap.put("channelPriInfo","");
			reqMap.put("payMethod","CARDPAY");
			reqMap.put("ffpId",Integer.valueOf(loginMap.get("id")));
			reqMap.put("ffpCardNo",loginMap.get("memberID"));
			reqMap.put("loginKeyInfo",loginMap.get("loginKeyInfo"));
			String bankType = json.getString("bankType"); // 银行
			String gatewayNo = "";
			if ("ICBC".equals(bankType)) {
				gatewayNo = "22403";
			} else if ("CCB".equals(bankType)) {
				gatewayNo = "22406";
			}else if ("ABC".equals(bankType)) {
				gatewayNo = "22409";
			}else if ("PINGAN".equals(bankType)) {
				gatewayNo = "22407";
			}else if ("CIB".equals(bankType)) {
				gatewayNo = "22405";
			}else if ("BOC".equals(bankType)) {
				gatewayNo = "22404";
			}else if ("CMB".equals(bankType)) {
				gatewayNo = "22408";
			}else if ("GDB".equals(bankType)) {
				gatewayNo = "22411";
			}else if ("CMBC".equals(bankType)) {
				gatewayNo = "22410";
			}else if ("ECITIC".equals(bankType)) {
				gatewayNo = "22402";
			}else if ("BCM".equals(bankType)) {
				gatewayNo = "22401";
			}else if ("CEB".equals(bankType)) {
				gatewayNo = "22415";
			}
			reqMap.put("gatewayNo",gatewayNo);
			reqMap.put("gatewayType","4");
			String creditNo = json.getString("creditNo");

			String expireMonth = json.getString("expireMonth"); // 有效月
			String expireYear = json.getString("expireYear");
			String expireDate = expireYear.substring(2) + expireMonth;
			String cvv = json.getString("cvv");
			String ownername = json.getString("ownername");
			String idCardNo = json.getString("idCardNo");
			String payerMobile = json.getString("payerMobile");
			reqMap.put("cardInfo",creditNo + "||" +expireDate +"|" + cvv + "|0|" + idCardNo + "|" + ownername + "|" + payerMobile);
			String reqJson = JSON.toJSONString(reqMap);
			System.out.println(httpKey + "HO支付参数:" + reqJson);
			StringEntity pays = new StringEntity(reqJson, "utf-8");
			post.setEntity(pays);
			post.setConfig(defaultRequestConfig);
			post.setHeader("channelCode", "MOBILE");
			post.setHeader("clientVersion", "6.3.0");
			post.setHeader("platformInfo", "android");
			post.setHeader("sign", "S6aCMRBQpVR+GUt2bRw58ve8ZrCK2EzKl5j8BR2zL0Zg9TnHJoDGboB6Z2+IJCS5");
			post.setHeader("timeStamp", timeStamp);
			post.setHeader("token", "www.juneyaoair.com");
			post.setHeader("versionCode", "63000");
			post.setHeader("Host", "hoapp.juneyaoair.com");
			post.setHeader("Accept-Encoding", "gzip");
			post.setHeader("Content-Type", "application/json; charset=utf-8");
			post.setHeader("User-Agent", "okhttp/3.10.0");
			post.setHeader("Proxy-Connection", "keep-alive");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			response = httpclient.execute(post);
			String res = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(httpKey + "HO支付返回:" + res);
			if (StringUtils.isEmpty(res)) {
				back = "error:官网支付无信息返回，请手动确认是否支付";
			}
			if (res.contains("resultCode")) {
				JSONObject payObj = new JSONObject(res);
				String resultCode = payObj.getString("resultCode");
				if ("10001".equals(resultCode)) {
					String paymentTransId = payObj.getString("paymentTransId");
					back = "success#" + paymentTransId;
				} else {
					back = "error:" + payObj.getString("errorInfo");
				}
			} else {
				if (back.length() > 255) {
					back = back.substring(0,200);
				}
 				back = "error:官网支付支付返回异常信息:" + back;
			}
		} catch (Exception e) {
			logger.error(httpKey + "支付error:",e);
			back = "error:" + e.getMessage();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e2) {
			}
		}
		return back;
	}
	public String payInit(CloseableHttpClient httpclient , RequestConfig defaultRequestConfig , String orderJson , Map<String,String>  bookingMap, Map<String,String> loginMap) {
		CloseableHttpResponse response = null;
		String order_id = "";
		String mainusername = "";
		String[] dlyAccountInfo = dlyAccount.split( ":" );
		String proxyUser = dlyAccountInfo[0];
		String proxyPass = dlyAccountInfo[1];
		JSONObject json = null;
		String back = "";
		try {
			json = new JSONObject( orderJson );
			order_id = json.getString( "id" );
			mainusername = json.getString( "mainusername" );
		} catch (JSONException e) {
			logger.error( "error" , e );
		}
		String httpKey = mainusername + order_id;
		try{
			String timeStamp = System.currentTimeMillis() + "";
			HttpPost post = new HttpPost("https://hoapp.juneyaoair.com/nemPaymentService/initPayMethod?t=" + timeStamp);
			Map<String, Object> reqMap = new HashMap<>();
			String payAmount = bookingMap.get("payAmount");
			double d = Double.valueOf(payAmount);
			DecimalFormat df = new DecimalFormat("#,#00.00#");
			reqMap.put("amount",df.format(d));
			reqMap.put("appsys","MOBILE");
			reqMap.put("blackBox", "eyJvcyI6ImFuZHJvaWQiLCJ2ZXJzaW9uIjoiMy42LjMiLCJwcm9maWxlX3RpbWUiOjE3NSwiaW50ZXJ2YWxfdGltZSI6NDU2MCwicGFja2FnZXMiOiJvcmcuenl3eC53YnBhbG1zdGFyLndpZGdldG9uZS51ZXgxMTI5Njg3NiIsImNzaWQiOiIxNjE2NTYxOTQxMjQ0MTQ1NDQyIiwidG9rZW5faWQiOiJoNThXOWpZeU1DT0JzTFVPc3VPeE9ZS1FTc3Vadk80QUJPdjg3YlBka2JCODlod3pORTVyQWRUb0Nna2xuWDNOWlpZemQ1Y3NQXC9LRDRsc0E3SGJTNVE9PSJ9");
			reqMap.put("channelCode","MOBILE");
			reqMap.put("channelBuyDatetime", DateUtil.getCurrentDate("YYYYMMddHHmmss"));
			reqMap.put("channelOrderNo",bookingMap.get("channelOrderNo"));
			reqMap.put("channelPriInfo","%3FproductType%3DNormal%26type%3DPaymentFlight");
			reqMap.put("clientVersion","6.3.0");
			reqMap.put("ffpId",loginMap.get("id"));
			reqMap.put("ffpCardNo",loginMap.get("memberID"));
			reqMap.put("loginKeyInfo",loginMap.get("loginKeyInfo"));
			reqMap.put("orderNo",bookingMap.get("orderNo"));
			reqMap.put("orderType","D");
			reqMap.put("payMethod","");
			reqMap.put("payType","D");
			reqMap.put("platformInfo","android");
			reqMap.put("token",loginMap.get("token"));
			reqMap.put("useScore","");
			reqMap.put("versionCode",63000);
			String payreqJson = JSON.toJSONString(reqMap);
			System.out.println(httpKey + "HO点击支付参数:" + payreqJson);
			StringEntity s = new StringEntity(payreqJson, "utf-8");
			post.setEntity(s);
			post.setConfig(defaultRequestConfig);
			post.setHeader("channelCode", "MOBILE");
			post.setHeader("clientVersion", "6.3.0");
			post.setHeader("platformInfo", "android");
			post.setHeader("sign", "S6aCMRBQpVR+GUt2bRw58ve8ZrCK2EzKl5j8BR2zL0Zg9TnHJoDGboB6Z2+IJCS5");
			post.setHeader("timeStamp", timeStamp);
			post.setHeader("token", loginMap.get("token"));
			post.setHeader("versionCode", "63000");
			post.setHeader("Host", "hoapp.juneyaoair.com");
			post.setHeader("Accept-Encoding", "gzip");
			post.setHeader("Content-Type", "application/json; charset=utf-8");
			post.setHeader("User-Agent", "okhttp/3.10.0");
			post.setHeader("Proxy-Connection", "keep-alive");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			response = httpclient.execute(post);
			String payres = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(httpKey + "HOHO点击支付返回:" + payres);
			if (payres.contains("resultCode")) {
				JSONObject payObj = new JSONObject(payres);
				String resultCode = payObj.getString("resultCode");
				if ("10001".equals(resultCode)) {
					back = "success";
				} else {
					back = "error:" + payObj.getString("errorInfo");
					return  back;
				}
			} else {
				if (back.length() > 255) {
					back = back.substring(0,200);
				}
				return back = "error:官网支付网关支付返回异常信息:" + back;
			}
		} catch (Exception e) {
			logger.error(httpKey + "支付error:",e);
			back = "error:" + e.getMessage();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e2) {
			}
		}
		return back;
	}
    private Map<String, String> parseDetailResult(String back , String backId) throws Exception {
        Map<String, String> resultMap = Maps.newHashMap();
        JSONObject jsonObj = new JSONObject( back );
        String payTransactionid = jsonObj.getJSONArray( "orderPayInfoDtoList" ).getJSONObject( 0 ).getString( "bankSerial" );
        long payPrice = jsonObj.getJSONArray( "orderPayInfoDtoList" ).getJSONObject( 0 ).getLong( "payAmt" );
        resultMap.put( "payTransactionid" , payTransactionid );
        resultMap.put( "payPrice" , payPrice + "" );
        JSONArray paxInfoDtoList = jsonObj.getJSONArray( "paxInfoDtoList" );
        for (int i = 0; i < paxInfoDtoList.length(); i++) {
            JSONObject json = paxInfoDtoList.getJSONObject( i );
            JSONObject paxDetailDto = json.getJSONObject( "paxDetailDto" );
            String paxName = paxDetailDto.getString( "paxName" );
            String identityId = paxDetailDto.getJSONArray( "paxIdInfoList" ).getJSONObject( 0 ).getString( "idNo" );
            String ticketNo = json.getJSONArray( "tripInfoDtoList" ).getJSONObject( 0 ).getJSONObject( "airSegInfoDto" )
                    .getString( "tktNo" );
            if (StringUtils.isEmpty( ticketNo )) {
                ticketNo = "null";
            }
            String value = paxName.replace( "/" , "" ) + "##" + identityId + "##" + ticketNo + "##" + backId;
            resultMap.put( paxName , value );
        }
        return resultMap;
    }

	@Test
	public void addContactPaxTest() {
		CloseableHttpResponse response = null;
		String back = "";
		String order_id = "";
		String mainusername = "";
		String[] dlyAccountInfo = dlyAccount.split( ":" );
		String proxyUser = dlyAccountInfo[0];
		String proxyPass = dlyAccountInfo[1];
		JSONObject json = null;
		String httpKey = mainusername + order_id;
		Map<String,String> passIDMap = new HashMap<>();
		try {
			CloseableHttpClient httpclient = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost("https://m.flight.qunar.com/webapi/contact/save");
			String certType = "身份证";
					// passFirst.getString("passengerCardType");
			if ("身份证".equals(certType)) {
				certType = "1";
			} else if ("护照".equals(certType)) {
				certType = "2";
			}else if ("港澳通行证".equals(certType)) {
				certType = "3";
			} else {
				certType = "1";
			}
			String certNo = "130133198111171178";
					// passFirst.getString("idcard");
			String passengerName = "付亮亮";
					// passFirst.getString("passengerName");
			Map<String,String> credentialsMap = new HashMap<>();
			credentialsMap.put("credentialsType",certType);
			credentialsMap.put("credentialsNo",certNo);
			credentialsMap.put("issuedCountry","中国");
			List<Map> credentialsList = new ArrayList<>();
			credentialsList.add(credentialsMap);
			String reqJson = JSON.toJSONString(credentialsList);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(new BasicNameValuePair("name",passengerName));
			nameValuePairs.add(new BasicNameValuePair("source","flight"));
			String gender = "1";
			if ("1".equals(certType)) {
				char sexNum = certNo.charAt(16);
				if ((Integer.valueOf(sexNum)%2) == 0) {
					gender = "2";
				} else {
					gender = "1";
				}
			}
			nameValuePairs.add(new BasicNameValuePair("gender",gender));
			String birthday = "1981-11-17";
					// passFirst.getString("birthday");
			nameValuePairs.add(new BasicNameValuePair("birthday",birthday));
			nameValuePairs.add(new BasicNameValuePair("ticketType","1"));
			nameValuePairs.add(new BasicNameValuePair("nationality","中国"));
			nameValuePairs.add(new BasicNameValuePair("email",""));
			String csrfToken = "i0fpHCduiwkbldLQbUGgidsKnGepJpzw";
					// cabinInfo.getDlyId();
			nameValuePairs.add(new BasicNameValuePair("csrfToken",csrfToken));
			nameValuePairs.add(new BasicNameValuePair("credentialsList",reqJson));
			nameValuePairs.add(new BasicNameValuePair("mobile","86-13037284859"));
			System.out.println(httpKey + "去哪儿添加乘机人参数:" + JSON.toJSONString(nameValuePairs));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			post.setHeader("Host", "m.flight.qunar.com");
			post.setHeader("Connection", "keep-alive");
			post.setHeader("Accept", "application/json, text/javascript");
			post.setHeader("X-Requested-With", "XMLHttpRequest");
			post.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Origin", "https://m.flight.qunar.com");
			String bookingParamKey = "f_domestic_uniform_booking_KN5977_TYL1_73619778-fedb-4c51-98f5-9c3984ed1b9b";
			String choroToken = UUID.randomUUID().toString();
			String token = "E4BDD850C5A6698A395CA9B84DCF486C";
			String tokens = "[{\"token\":\""+ token +"\",\"expCode\":\"Freechange02\",\"expType\":\"Freechange02\"}]";
			String depCity = "北京";
			String arrCity = "上海";
			String departureDate = "2021-05-17";
			String searchParam = "{\"sc\":\""+ depCity +"\",\"dc\":\""+arrCity+"\",\"st\":\""+ departureDate +"\",\"dt\":\"\",\"t\":"+ System.currentTimeMillis() +",\"cb\":4,\"ft\":\"oneway\"}";
			String referer = "https://m.flight.qunar.com/flight/tts/book?bookingParamKey=" + bookingParamKey +
					"&realVendorType=1&searchParam=" + URLEncoder.encode(searchParam,"UTF-8")+ "&" +
					"choroToken=" + choroToken + "&" +
					"tokens=" + URLEncoder.encode(tokens,"UTF-8") +"&cat=&";
					// "https://m.flight.qunar.com/flight/tts/book?bookingParamKey=f_domestic_uniform_booking_KN5977_ABC1_f6bbdf9d-dd86-4725-925a-8e8164aa5d63&realVendorType=1&searchParam=%257B%2522sc%2522%253A%2522%25E5%258C%2597%25E4%25BA%25AC%2522%252C%2522dc%2522%253A%2522%25E4%25B8%258A%25E6%25B5%25B7%2522%252C%2522st%2522%253A%25222021-05-17%2522%252C%2522dt%2522%253A%2522%2522%252C%2522t%2522%253A1620875996482%252C%2522cb%2522%253A4%252C%2522ft%2522%253A%2522oneway%2522%257D&choroToken=8cb7fa90-99d3-473f-b244-8ebdd3cd3de6&tokens=%5B%7B%22token%22%3A%2210BFDD112CAFB1B0CC7C17A27C4CC1C5%22%2C%22expCode%22%3A%22Freechange02%22%2C%22expType%22%3A%22Freechange02%22%7D%5D&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&";
			post.setHeader("Referer", referer);
			post.setHeader("Accept-Encoding", "gzip, deflate, br");
			post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
			String cookie = "QN1=00001380306c2b450bc8b429; QN300=auto_4e0d874a; QN99=4905; QunarGlobal=10.86.213.148_-1d98207b_175cf9bc46c_6611|1605507962745; QN601=40440cd22bbc086230da18f7ddc722da; _i=VInJO_2sQ2tqWBm1mmT4BfwJWjxq; QN48=000031802f102b450bd81c37; fid=029cad0c-dc91-4f40-854d-eb7f221f7849; quinn=4a59aba0e9e761fb0ce4277cc1bacc4d16e7d7d59fd85fa0eb8a2f2c0a5bb02b3d33c8650a9cdb4e776a4696e43cc5e9; SC1=66e88fdf578c8052bc254b83e0cf5e87; SC18=; QN269=FA581840A94F11EBB550FA163EEFDE16; QN205=auto_4e0d874a; QN277=auto_4e0d874a; QN66=qunar; QN275=8+AByHPuwtDZIqGmOAFwKP5dbpYf9KyJD/wLv/n4NQk=; QN163=0; QN6=auto_4e0d874a; QN235=2021-05-13; _tt=tc_94934149fb3bcccd_179639de0b6_b1ff; _RGUID=8cb7fa90-99d3-473f-b244-8ebdd3cd3de6; _RDG=28e3f74f456d162d1a22386e0aca2af3c1; _RSG=_SqqP2f.to8ctJpJthZ49B; _RF1=183.14.28.53; QN25=af146dfb-f4cf-4eb6-8f89-110621c6bf10-9f992f90; QN43=2; QN42=%E9%9A%8F%E9%A3%8E%E4%B9%8B%E4%BA%BA; _q=U.pnkerrk1334; _t=27124062; csrfToken=i0fpHCduiwkbldLQbUGgidsKnGepJpzw; _s=s_7PIRW6673MTK3E25O5QB72TLBE; _v=wpTDCOkKM3uDIs6muVKxocGwOGSPB9P9sSaiZljsAE-7g1OfXMifL34Bvq84oUENkFrSPogN5MuK_CUvBV6oX4jUDKJwqyxxWPV5HHvTuoI_5cWSxvK-ViZE4ViStUFEfw8L35eXufGFjHwJTN7B7c8__ASq1eOoW5ct9cfrkxH7; _vi=ax3TTyIFZ3GBJ80oYbvPuJnYdoEEIWSD6fc9KTuRLNlfK0hla5A_fdsUiio7w3JaM9eDcg85QIFV4VSjuJWu-vVlpEwSqn0SjlsmpK9qsz8ztKa-6ZU8IHREb6ebh7PJEnKzp-Qbb6rgOeBTQvGifzKp-5Iy4EMtOMy6oTuGgDv1; QN44=pnkerrk1334; QN267=277895125b795caf6; QN271=46dc9438-725a-4676-8afc-e077a187217e; QN621=1490067914133%3DDEFAULT%26fr%3Df_athena_urban; Alina=89b082c0-945c19-874bdc73-26b34604-33ab609cc92f; F235=1620887876186; QN667=C; QN668=51%2C56%2C52%2C50%2C58%2C59%2C50%2C53%2C56%2C58%2C58%2C52%2C52";
					// "QN1=00001380306c2b450bc8b429; QN300=auto_4e0d874a; QN99=4905; QunarGlobal=10.86.213.148_-1d98207b_175cf9bc46c_6611|1605507962745; QN601=40440cd22bbc086230da18f7ddc722da; _i=VInJO_2sQ2tqWBm1mmT4BfwJWjxq; QN48=000031802f102b450bd81c37; fid=029cad0c-dc91-4f40-854d-eb7f221f7849; quinn=4a59aba0e9e761fb0ce4277cc1bacc4d16e7d7d59fd85fa0eb8a2f2c0a5bb02b3d33c8650a9cdb4e776a4696e43cc5e9; SC1=66e88fdf578c8052bc254b83e0cf5e87; SC18=; QN269=FA581840A94F11EBB550FA163EEFDE16; QN205=auto_4e0d874a; QN277=auto_4e0d874a; Alina=a279b8ac-925339-8449bb47-51919d87-4995609a1741; QN66=qunar; QN275=8+AByHPuwtDZIqGmOAFwKP5dbpYf9KyJD/wLv/n4NQk=; QN163=0; QN6=auto_4e0d874a; QN235=2021-05-13; _tt=tc_94934149fb3bcccd_179639de0b6_b1ff; _RDG=28e3f74f456d162d1a22386e0aca2af3c1; _RSG=_SqqP2f.to8ctJpJthZ49B; _RF1=183.14.28.53; F235=1620873986319; QN25=af146dfb-f4cf-4eb6-8f89-110621c6bf10-9f992f90; QN43=2; QN42=%E9%9A%8F%E9%A3%8E%E4%B9%8B%E4%BA%BA; _q=U.pnkerrk1334; _t=27124062; csrfToken=i0fpHCduiwkbldLQbUGgidsKnGepJpzw; _s=s_7PIRW6673MTK3E25O5QB72TLBE; _v=wpTDCOkKM3uDIs6muVKxocGwOGSPB9P9sSaiZljsAE-7g1OfXMifL34Bvq84oUENkFrSPogN5MuK_CUvBV6oX4jUDKJwqyxxWPV5HHvTuoI_5cWSxvK-ViZE4ViStUFEfw8L35eXufGFjHwJTN7B7c8__ASq1eOoW5ct9cfrkxH7; _vi=ax3TTyIFZ3GBJ80oYbvPuJnYdoEEIWSD6fc9KTuRLNlfK0hla5A_fdsUiio7w3JaM9eDcg85QIFV4VSjuJWu-vVlpEwSqn0SjlsmpK9qsz8ztKa-6ZU8IHREb6ebh7PJEnKzp-Qbb6rgOeBTQvGifzKp-5Iy4EMtOMy6oTuGgDv1; QN44=pnkerrk1334; QN267=277895125b795caf6; QN271=46dc9438-725a-4676-8afc-e077a187217e; QN621=1490067914133%3DDEFAULT%26fr%3Df_athena_urban; QN668=51%2C56%2C52%2C50%2C58%2C58%2C57%2C58%2C56%2C58%2C50%2C55%2C56; QN667=B";
					// "QN1=00001380306c2b450bc8b429; QN300=auto_4e0d874a; QN99=4905; QunarGlobal=10.86.213.148_-1d98207b_175cf9bc46c_6611|1605507962745; QN601=40440cd22bbc086230da18f7ddc722da; _i=VInJO_2sQ2tqWBm1mmT4BfwJWjxq; QN48=000031802f102b450bd81c37; fid=029cad0c-dc91-4f40-854d-eb7f221f7849; quinn=4a59aba0e9e761fb0ce4277cc1bacc4d16e7d7d59fd85fa0eb8a2f2c0a5bb02b3d33c8650a9cdb4e776a4696e43cc5e9; SC1=66e88fdf578c8052bc254b83e0cf5e87; SC18=; QN269=FA581840A94F11EBB550FA163EEFDE16; QN205=auto_4e0d874a; QN277=auto_4e0d874a; Alina=a279b8ac-925339-8449bb47-51919d87-4995609a1741; QN66=qunar; QN275=8+AByHPuwtDZIqGmOAFwKP5dbpYf9KyJD/wLv/n4NQk=; QN43=2; QN42=%E9%9A%8F%E9%A3%8E%E4%B9%8B%E4%BA%BA; _q=U.pnkerrk1334; _t=27122750; csrfToken=aifF0b0hxvhKNygwOAbGfoegCMWpbs5c; _s=s_RLXWZHEYVN37R5A5CUX2ZM4DTM; _v=ICTdlR3lmeVHwFxfT-1LkoAPNfVcIpU58SMsHqJnI0mzZRl6571ypuj3lUmzZaILxtcHOZyPdpwSfgVokS2qmM0mt-60sbUX_VAaapa_ZPe9Xmf2X-ewNdlqPy-4IWjqZ9SZtFPYjHzPW8IiFPIHFyF355LJRp2IlBKZvYuiDlRu; QN44=pnkerrk1334; QN163=0; QN6=auto_4e0d874a; QN667=C; QN235=2021-05-13; _tt=tc_94934149fb3bcccd_179639de0b6_b1ff; _RF1=183.14.28.53; _RSG=_SqqP2f.to8ctJpJthZ49B; _RDG=28e3f74f456d162d1a22386e0aca2af3c1; _RGUID=8cb7fa90-99d3-473f-b244-8ebdd3cd3de6; F235=1620873986319; QN666=2; _vi=0OF44EOBM1ADPhRV83l1mxO6a5lcGAzPaSweZCG91L6wiV2IsHxOY6yrInZqYS6LlMIWSATIXo0gbWSm3VuL30hvY9MBMHn3WtaOfBQ07SCroWQAhmraf6MgOL_g7jE_lb4vAcMbrBkxHOW9KlKvt34iLsBYS2x9oojx1SG95myf; QN267=27789512537cdefc1; QN271=c971ee1b-2530-42f6-aabf-27360fca01aa; QN668=51%2C56%2C52%2C50%2C58%2C57%2C55%2C59%2C59%2C50%2C55%2C52%2C56; QN621=1490067914133%3DDEFAULT%26fr%3Df_athena_urban";
					// loginMap.get("cookie");
			post.setHeader("Cookie", cookie);
			///post.setHeader("Proxy-Connection", "keep-alive");
			// post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			response = httpclient.execute(post);
			String res = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(httpKey + "填加乘机人返回:" + res);
			if (StringUtils.isEmpty(res) || !res.contains("ret")) {
				Thread.sleep(5000);
				response = httpclient.execute(post);
				res = EntityUtils.toString(response.getEntity(), "utf-8");
				System.out.println(httpKey + "填加乘机人返回:" + res);
			}
			/*if (StringUtils.isNotEmpty(res) && res.contains("resultCode")) {
				JSONObject resObj = new JSONObject(res);
				String resultCode = resObj.getString("resultCode");
				if ("10001".equals(resultCode)) {
					JSONObject objData =  resObj.getJSONArray("objData").getJSONObject(0);
					String commonContactId = objData.getString("commonContactId");
					String passengername = objData.getString("passengerName");
					passIDMap.put(passengername,commonContactId+"");
					if (passengers.length() > 1) {
						for (int i = 1; i < passengers.length(); i++) {
							Thread.sleep(6000);
							JSONObject passObj = passengers.getJSONObject(i);
							passerMobile = json.getString("mobile");
							// passObj.getString("mobile");
							passengerName = passObj.getString("passengerName");
							certNo = passObj.getString("idcard");
							certType = passObj.getString("passengerCardType");
							if ("身份证".equals(certType)) {
								certType = "NI";
							} else if ("护照".equals(certType) || "港澳通行证".equals(certType)) {
								certType = "PP";
							}else if ("军官证".equals(certType)) {
								certType = "ID";
							} else {
								certType = "NI";
							}
							contactCertMap.put("certType",certType);
							commonPersonInfoMap.put("handphoneNo",passerMobile);
							commonPersonInfoMap.put("passengerName",passengerName);
							contactCertMap.put("certNo", certNo);
							reqJson = JSON.toJSONString(reqMap);
							System.out.println(httpKey + "HO添加乘机人参数:" + reqJson);
							s = new StringEntity(reqJson, "utf-8");
							post.setConfig(defaultRequestConfig);
							post.setEntity(s);
							response = httpclient.execute(post);
							res = EntityUtils.toString(response.getEntity(), "utf-8");
							System.out.println(httpKey + "填加乘机人返回:" + res);
							if (StringUtils.isEmpty(res) || !res.contains("resultCode")) {
								Thread.sleep(5000);
								response = httpclient.execute(post);
								res = EntityUtils.toString(response.getEntity(), "utf-8");
								System.out.println(httpKey + "填加乘机人返回:" + res);
								if (StringUtils.isNotEmpty(res) && res.contains("resultCode")) {
									resObj = new JSONObject(res);
									resultCode = resObj.getString("resultCode");
									if ("10001".equals(resultCode)) {
										objData = resObj.getJSONArray("objData").getJSONObject(0);
										commonContactId = objData.getString("commonContactId");
										passengername = objData.getString("passengerName");
										passIDMap.put(passengername, commonContactId + "");
									} else {
										String resultInfo = resObj.getString("resultInfo");
										if (StringUtils.isNotEmpty(resultInfo) && resultInfo.contains("身份证重复")) {
											passIDMap.put(passengername, "123");
										} else {
											passIDMap.put("error",resultInfo);
											return passIDMap;
										}

									}
								}
							}
						}
					}
				} else {
					String resultInfo = resObj.getString("resultInfo");
					if (StringUtils.isNotEmpty(resultInfo) && resultInfo.contains("身份证重复")) {
						passIDMap.put(passengerName, "123");
					} else {
						passIDMap.put("error",resultInfo);
						return passIDMap;
					}
				}
			}*/
		} catch (Exception e) {
			logger.error( httpKey + "error" , e );
			System.out.println(order_id+"addContactPax异常返回:"+back);
			passIDMap.put("error",e.getMessage());
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e2) {
			}
		}
	}

    private Map<String,String> addContactPax(CloseableHttpClient httpclient , RequestConfig defaultRequestConfig , Map<String,String> loginMap , String orderJson,String flightMsg) {
        CloseableHttpResponse response = null;
        String back = "";
		String order_id = "";
		String mainusername = "";
		String[] dlyAccountInfo = dlyAccount.split( ":" );
		String proxyUser = dlyAccountInfo[0];
		String proxyPass = dlyAccountInfo[1];
		JSONObject json = null;
		try {
			json = new JSONObject( orderJson );
			order_id = json.getString( "id" );
			mainusername = json.getString( "mainusername" );
		} catch (JSONException e) {
			logger.error( "error" , e );
		}
		String httpKey = mainusername + order_id;
		Map<String,String> passIDMap = new HashMap<>();
        try {
			JSONObject flightObj = json.getJSONArray("flights").getJSONObject(0);
			JSONArray passengers = json.getJSONArray("passengers");
			JSONObject passFirst = passengers.getJSONObject(0);
			HttpPost post = new HttpPost("https://m.flight.qunar.com/webapi/contact/save");
			String certType = passFirst.getString("passengerCardType");
			if ("身份证".equals(certType)) {
				certType = "1";
			} else if ("护照".equals(certType)) {
				certType = "2";
			}else if ("港澳通行证".equals(certType)) {
				certType = "3";
			} else {
				certType = "1";
			}
			String certNo = passFirst.getString("idcard");
			String passengerName = passFirst.getString("passengerName");
			Map<String,String> credentialsMap = new HashMap<>();
			credentialsMap.put("credentialsType",certType);
			credentialsMap.put("credentialsNo",certNo);
			credentialsMap.put("issuedCountry","中国");
			List<Map> credentialsList = new ArrayList<>();
			credentialsList.add(credentialsMap);
			String reqJson = JSON.toJSONString(credentialsList);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(new BasicNameValuePair("name",passengerName));
			nameValuePairs.add(new BasicNameValuePair("source","flight"));
			String gender = "1";
			if ("1".equals(certType)) {
				char sexNum = certNo.charAt(16);
				if ((Integer.valueOf(sexNum)%2) == 0) {
					gender = "2";
				} else {
					gender = "1";
				}
			}
			CabinInfo cabinInfo = JSON.parseObject(flightMsg,CabinInfo.class);
			nameValuePairs.add(new BasicNameValuePair("gender",gender));
			String birthday = passFirst.getString("birthday");
			nameValuePairs.add(new BasicNameValuePair("birthday",birthday));
			nameValuePairs.add(new BasicNameValuePair("ticketType","1"));
			nameValuePairs.add(new BasicNameValuePair("nationality","中国"));
			nameValuePairs.add(new BasicNameValuePair("email",""));
			String csrfToken = cabinInfo.getDlyId();
			nameValuePairs.add(new BasicNameValuePair("csrfToken",csrfToken));
			nameValuePairs.add(new BasicNameValuePair("credentialsList",reqJson));
			nameValuePairs.add(new BasicNameValuePair("mobile","86-" + json.getString("mobile")));
			System.out.println(httpKey + "去哪儿添加乘机人参数:" + JSON.toJSONString(nameValuePairs));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			post.setConfig(defaultRequestConfig);
			post.setHeader("Host", "m.flight.qunar.com");
			post.setHeader("Connection", "keep-alive");
			post.setHeader("Accept", "application/json, text/javascript");
			post.setHeader("X-Requested-With", "XMLHttpRequest");
			post.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Origin", "https://m.flight.qunar.com");
			String choroToken = UUID.randomUUID().toString();
			String bookingParamKey = cabinInfo.getRequestId();
			String tokens = cabinInfo.getComment();
			String depCity = "北京";
			String arrCity = "上海";
			String departureDate = flightObj.getString("departureDate");
			String searchParam = "{\"sc\":\""+ depCity +"\",\"dc\":\""+arrCity+"\",\"st\":\""+ departureDate +"\",\"dt\":\"\",\"t\":"+ System.currentTimeMillis() +",\"cb\":4,\"ft\":\"oneway\"}";
			String referer = "https://m.flight.qunar.com/flight/tts/book?bookingParamKey=" + bookingParamKey +
					"&realVendorType=1&searchParam=" + URLEncoder.encode(searchParam,"UTF-8")+ "&" +
					"choroToken=" + choroToken + "&" +
					"tokens=" + URLEncoder.encode(tokens,"UTF-8") +"&cat=&";
			System.out.println(httpKey + "请求的referer:" + referer);
			post.setHeader("Referer", referer);
			post.setHeader("Accept-Encoding", "gzip, deflate, br");
			post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
			String cookie = loginMap.get("cookie");
			post.setHeader("Cookie", cookie);
			post.setHeader("Proxy-Connection", "keep-alive");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			response = httpclient.execute(post);
			String res = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(httpKey + "填加乘机人返回:" + res);
			if (StringUtils.isNotEmpty(res) && res.contains("ret")) {
				com.alibaba.fastjson.JSONObject resObj = JSON.parseObject(res);
				boolean ret = resObj.getBooleanValue("ret");
				if (ret) {
					passIDMap.put(certNo,resObj.getString("data"));
				} else {
					passIDMap.put("error",resObj.getString("errmsg"));
				}
			}
        } catch (Exception e) {
            logger.error( httpKey + "error" , e );
            System.out.println(order_id+"addContactPax异常返回:"+back);
			passIDMap.put("error",e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e2) {
            }
        }
        return passIDMap;
    }

	public Map<String,String> queryDetail(CloseableHttpClient httpclient , RequestConfig defaultRequestConfig , String orderJson , Map<String, Object> bookingMap, Map<String,String> loginMap) throws JSONException, IOException {
		CloseableHttpResponse response = null;
		String[] dlyAccountInfo = dlyAccount.split( ":" );
		String proxyUser = dlyAccountInfo[0];
		String proxyPass = dlyAccountInfo[1];
		JSONObject json = new JSONObject( orderJson );
		String order_id = json.getString( "id" );
		String mainusername  = json.getString( "mainusername" );

		String httpKey = mainusername + order_id;
		Map<String,String> backMap = new HashMap<>();
		try{
			HttpPost post = new HttpPost("https://hoapp.juneyaoair.com/orderService/queryOrderDetail");
			String timeStamp = System.currentTimeMillis() + "";
			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put("channelCode","MOBILE");
			reqMap.put("platformInfo","android");
			reqMap.put("clientVersion","6.3.0");
			reqMap.put("versionCode",63000);
			reqMap.put("token",loginMap.get("token"));
			reqMap.put("channelOrderNo",bookingMap.get("channelOrderNo"));
			reqMap.put("orderNo",bookingMap.get("orderNo"));
			reqMap.put("customerNo",loginMap.get("id"));
			reqMap.put("loginKeyInfo",loginMap.get("loginKeyInfo"));
			reqMap.put("cardNo","HO" + loginMap.get("memberID"));
			String reqJson = JSON.toJSONString(reqMap);
			System.out.println(httpKey + "HO查询详情参数:" + reqJson);
			StringEntity s = new StringEntity(reqJson, "utf-8");
			post.setEntity(s);
			post.setConfig(defaultRequestConfig);
			post.setHeader("channelCode", "MOBILE");
			post.setHeader("clientVersion", "6.3.0");
			post.setHeader("platformInfo", "android");
			post.setHeader("sign", "S6aCMRBQpVR+GUt2bRw58ve8ZrCK2EzKl5j8BR2zL0Zg9TnHJoDGboB6Z2+IJCS5");
			post.setHeader("timeStamp", timeStamp);
			post.setHeader("token", "www.juneyaoair.com");
			post.setHeader("versionCode", "63000");
			post.setHeader("Host", "hoapp.juneyaoair.com");
			post.setHeader("Accept-Encoding", "gzip");
			post.setHeader("Content-Type", "application/json; charset=utf-8");
			post.setHeader("User-Agent", "okhttp/3.10.0");
			post.setHeader("Proxy-Connection", "keep-alive");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			response = httpclient.execute(post);
			String res = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(httpKey + "查询详情返回:" + res);
			if (StringUtils.isNotEmpty(res) && res.contains("resultCode")) {
				JSONObject resObj = new JSONObject(res);
				String resultCode = resObj.getString("resultCode");
				if ("10001".equals(resultCode)) {
					String orderPayState = resObj.getString("orderPayState");
					if ("pay".equalsIgnoreCase(orderPayState)) {
						String orderAmount = resObj.getString("orderAmount");
						backMap.put("payPrice",orderAmount);
						JSONArray passengerList = resObj.getJSONArray("orderPassengerInfoList");
						for (int k = 0; k < passengerList.length(); k++) {
							JSONObject passengerObj = passengerList.getJSONObject(k);
							String passengerName = passengerObj.getString("passengerName");
							String certNo = passengerObj.getString("certNo");
							String ticketNo = passengerObj.getString("eticketNo") ;
							if (StringUtils.isNotEmpty(ticketNo)) {
								String value = passengerName + "##" + certNo + "##" + ticketNo + "##" + "##";
								backMap.put(passengerName,value);
							}
					}
					}
				}
			}
		} catch (Exception e) {
			logger.error(httpKey + "查询订单详情异常:",e);
		} finally {
			if (response!=null) {
				response.close();
			}
		}
		return backMap;
	}

	public static int getAge(String strDate) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date birthDay = sdf.parse(strDate);
		Calendar cal = Calendar.getInstance();
		if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}
		int yearNow = cal.get(Calendar.YEAR);  //当前年份
		int monthNow = cal.get(Calendar.MONTH);  //当前月份
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
		cal.setTime(birthDay);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		int age = yearNow - yearBirth;   //计算整岁数
		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
			}else{
				age--;//当前月份在生日之前，年龄减一1
			}
		}
		return age;
    }
	@Test
	public void bookTest() throws IOException {
		String bookingParamKey = "";
		String tokens = "";
		String departureDate = "2021-05-17";
		String flightNo = "KN5977";
		String depCity = "北京";
		String arrCity = "上海";
		String searchParam = "{\"sc\":\""+ depCity +"\",\"dc\":\""+arrCity+"\",\"st\":\""+ departureDate +"\",\"dt\":\"\",\"t\":"+ System.currentTimeMillis() +",\"cb\":4,\"ft\":\"oneway\"}";
		String bookingURl = "https://m.flight.qunar.com/flight/tts/book?" +
				"bookingParamKey=" +bookingParamKey +
				"realVendorType=1&" +
				"searchParam=" + URLEncoder.encode(searchParam,"UTF-8") +
				"&choroToken=" + UUID.randomUUID().toString()+
				"&tokens=" + URLEncoder.encode(tokens) +
				"&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&";
		System.out.println("去哪儿booking的URl:" + bookingURl);
		HttpGet get = new HttpGet(bookingURl);
		get.setHeader("Host", "m.flight.qunar.com");
		get.setHeader("Upgrade-Insecure-Requests", "1");
		get.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
		get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		get.setHeader("Sec-Fetch-Dest", "document");
		String referer = "https://m.flight.qunar.com/ncs/page/flightdetail?startCity="+URLEncoder.encode(depCity)+"&destCity=" + URLEncoder.encode(arrCity)+ "&startDate="+departureDate+"&code="+flightNo+"&touchToken="+cabinInfo.getPakId()+"&from=f_athena_urban&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&extparams={%22ex_track%22:%22AthenaUrban%22}&_firstScreen=1&_gogokid=12";
		get.setHeader("Referer", referer);
		get.setHeader("Accept-Encoding", "gzip, deflate, br");
		get.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
		String cookie = "";
		get.setHeader("Cookie", cookie);
		get.setHeader("Proxy-Connection", "keep-alive");
		get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		CloseableHttpResponse response = httpclient.execute(get);
		String back = EntityUtils.toString(response.getEntity(), "utf-8");
		System.out.println("booking返回:" + back);
	}
    private Map<String,Object> booking(CloseableHttpClient httpclient , RequestConfig defaultRequestConfig , String orderJson , Map<String,String>  passIDMap, Map<String,String> loginMap,String flightMsg) throws JSONException {
        CloseableHttpResponse response = null;
		JSONObject json = new JSONObject(orderJson);
		String order_id = json.getString( "id" );
		String mainUser = json.getString("username");
		String httpKey = mainUser + order_id;
		Map<String,Object> backMap = new HashMap<>();
        try {
        	JSONArray passengers = json.getJSONArray("passengers");
        	Set<String> names = new HashSet<>();
        	for (int k = 0; k < passengers.length();k++) {
        		JSONObject obj = passengers.getJSONObject(k);
        		names.add(obj.getString("passengerName"));
			}
        	JSONObject flightObj = json.getJSONArray("flights").getJSONObject(0);
            String[] dlyAccountInfo = dlyAccount.split( ":" );
            String proxyUser = dlyAccountInfo[0];
            String proxyPass = dlyAccountInfo[1];
            CabinInfo cabinInfo = JSON.parseObject(flightMsg,CabinInfo.class);
			String bookingParamKey = cabinInfo.getRequestId();
			String tokens = cabinInfo.getComment();
			String departureDate = flightObj.getString("departureDate");
			String flightNo = flightObj.getString("flightNo");
			String depCity = "北京";
			String arrCity = "上海";
			String searchParam = "{\"sc\":\""+ depCity +"\",\"dc\":\""+arrCity+"\",\"st\":\""+ departureDate +"\",\"dt\":\"\",\"t\":"+ System.currentTimeMillis() +",\"cb\":4,\"ft\":\"oneway\"}";
            String bookingURl = "https://m.flight.qunar.com/flight/tts/book?" +
					"bookingParamKey=" +bookingParamKey +
					"realVendorType=1&" +
					"searchParam=" + URLEncoder.encode(searchParam,"UTF-8") +
					"&choroToken=" + UUID.randomUUID().toString()+
					"&tokens=" + URLEncoder.encode(tokens) +
					"&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&";
            System.out.println(httpKey + "去哪儿booking的URl:" + bookingURl);
			HttpGet get = new HttpGet(bookingURl);
			get.setConfig(defaultRequestConfig);
			get.setHeader("Host", "m.flight.qunar.com");
			get.setHeader("Upgrade-Insecure-Requests", "1");
			get.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
			get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
			get.setHeader("Sec-Fetch-Dest", "document");
			String referer = "https://m.flight.qunar.com/ncs/page/flightdetail?startCity="+URLEncoder.encode(depCity)+"&destCity=" + URLEncoder.encode(arrCity)+ "&startDate="+departureDate+"&code="+flightNo+"&touchToken="+cabinInfo.getPakId()+"&from=f_athena_urban&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&extparams={%22ex_track%22:%22AthenaUrban%22}&_firstScreen=1&_gogokid=12";
			get.setHeader("Referer", referer);
			get.setHeader("Accept-Encoding", "gzip, deflate, br");
			get.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
			get.setHeader("Cookie", loginMap.get("cookie"));
			get.setHeader("Proxy-Connection", "keep-alive");
			get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			response = httpclient.execute(get);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(httpKey + "booking返回:" + back);
			if (StringUtils.isNotEmpty(back) && back.contains("segments")) {
				String infos[] = back.split("<script type=\"text/javascript\">receive\\(");
				if(infos.length >= 4){
					// List<Map<String,Object>> passengerList = new ArrayList<>();
					com.alibaba.fastjson.JSONArray passengerList = new com.alibaba.fastjson.JSONArray();
					com.alibaba.fastjson.JSONArray jsoninsurances = null;
					com.alibaba.fastjson.JSONObject jsonautoFillContacter = null;
					com.alibaba.fastjson.JSONObject jsonpriceInfo = null;
					com.alibaba.fastjson.JSONObject jsonstaticData = null;
					for (int j = 1; j < infos.length; j++) {

						String jsonValue = infos[j].replace(")</script>", "");

						if(jsonValue.contains("\"module\":\"passenger\"")){

							com.alibaba.fastjson.JSONObject jsObject = com.alibaba.fastjson.JSONObject.parseObject(jsonValue);
							com.alibaba.fastjson.JSONObject jsonconfig = jsObject.getJSONObject("config");
							com.alibaba.fastjson.JSONArray jsoncommonListData = jsonconfig.getJSONArray("commonListData");
							com.alibaba.fastjson.JSONObject passenger = jsoncommonListData.getJSONObject(0);
							String name = passenger.getString("name");
							String birthday = passenger.getString("birthday");
							if (names.contains(name)) {
								passenger.put("selected", true);
								passenger.put("invalidDay", null);
								passenger.put("age", getAge(birthday));
								passengerList.add(passenger);
							}

							// newJson.append("\"passenger\":["+passenger.toJSONString()+"],");

						}else if(jsonValue.contains("\"module\":\"insure\"")){

							com.alibaba.fastjson.JSONObject jsObject = com.alibaba.fastjson.JSONObject.parseObject(jsonValue);
							com.alibaba.fastjson.JSONObject jsonconfig = jsObject.getJSONObject("config");
							jsoninsurances = jsonconfig.getJSONArray("insurances");

						}else if(jsonValue.contains("\"module\":\"contacter\"")){

							com.alibaba.fastjson.JSONObject jsObject = com.alibaba.fastjson.JSONObject.parseObject(jsonValue);
							com.alibaba.fastjson.JSONObject jsonconfig = jsObject.getJSONObject("config");
							jsonautoFillContacter = jsonconfig.getJSONObject("autoFillContacter");
							//newJson.append("\"contacter\":"+jsonautoFillContacter.toJSONString()+",");

						}else if(jsonValue.contains("\"module\":\"priceDetail\"")){

							com.alibaba.fastjson.JSONObject jsObject = com.alibaba.fastjson.JSONObject.parseObject(jsonValue);
							com.alibaba.fastjson.JSONObject jsonconfig = jsObject.getJSONObject("config");
							jsonpriceInfo = jsonconfig.getJSONObject("priceInfo");
							// newJson.append("\"priceBase\":"+jsonpriceInfo.toJSONString()+",");

						}else if(jsonValue.contains("\"module\":\"submit\"")){

							com.alibaba.fastjson.JSONObject jsObject = com.alibaba.fastjson.JSONObject.parseObject(jsonValue);
							com.alibaba.fastjson.JSONObject jsonconfig = jsObject.getJSONObject("config");
							jsonstaticData = jsonconfig.getJSONObject("staticData");
							if (jsonstaticData!=null) {
								com.alibaba.fastjson.JSONObject jsonadult = jsonstaticData.getJSONObject("priceInfo").getJSONObject("adult");
								com.alibaba.fastjson.JSONObject jsongoPrice = jsonadult.getJSONObject("goPrice");
								String constructionFee = jsongoPrice.getString("constructionFee");
								backMap.put("constructionFee",constructionFee);
								String fuelFee = jsongoPrice.getString("fuelFee");
								backMap.put("fuelFee",fuelFee);
								String basePrice = jsongoPrice.getString("basePrice");
								backMap.put("basePrice",basePrice);
								String sellPriceType = jsongoPrice.getString("sellPriceType");
								backMap.put("sellPriceType",sellPriceType);
								String productPriceArray = jsonadult.getString("productPriceArray");
								//尊享飞服务 ¥29
								Pattern pp = Pattern.compile("([\u4E00-\u9FA5]{1,10}) ¥(\\d{1,6})");
								Matcher matcherP=pp.matcher(productPriceArray);
								String sellPriceTypeCN = "";
								String productPrice = "";
								if(matcherP.find()){
									sellPriceTypeCN = matcherP.group(1);
									productPrice = matcherP.group(2);
									backMap.put("sellPriceTypeCN",sellPriceTypeCN);
									backMap.put("productPrice",productPrice);
								}
							}

							// newJson.append("\"staticData\":"+jsonstaticData.toJSONString()+",");

						}

					}
					backMap.put("passenger",passengerList);
					backMap.put("insure",jsoninsurances);
					backMap.put("contacter",jsonautoFillContacter);
					backMap.put("priceBase",jsonpriceInfo);
					backMap.put("staticData",jsonstaticData);
				}
			}
        } catch (Exception e) {
            logger.error( httpKey + "error" , e );
            backMap.put("error",e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e2) {
                }
            }
        }
        return backMap;
    }

	private Map<String,Object> createOrder(CloseableHttpClient httpclient , RequestConfig defaultRequestConfig , String orderJson , Map<String,Object>  bookMap, Map<String,String> loginMap,String flightMsg) throws JSONException {
		CloseableHttpResponse response = null;
		JSONObject json = new JSONObject(orderJson);
		String order_id = json.getString( "id" );
		String mainUser = json.getString("username");
		String httpKey = mainUser + order_id;
		Map<String,Object> backMap = new HashMap<>();
		try {
			System.out.println();
			JSONArray passengers = json.getJSONArray("passengers");
			int aduCount = 0;
			int chiCount = 0;
			int babyCount = 0;
			for (int k = 0; k < passengers.length();k++) {
				JSONObject obj = passengers.getJSONObject(k);
				String passengerType = obj.getString("passengerType");
				if ("成人".equals(passengerType)) {
					aduCount++;
				}else if ("儿童".equals(passengerType)) {
					chiCount++;
				}else if ("婴儿".equals(passengerType)) {
					babyCount++;
				}
			}
			JSONObject flightObj = json.getJSONArray("flights").getJSONObject(0);
			String[] dlyAccountInfo = dlyAccount.split( ":" );
			String proxyUser = dlyAccountInfo[0];
			String proxyPass = dlyAccountInfo[1];
			CabinInfo cabinInfo = JSON.parseObject(flightMsg,CabinInfo.class);
			String bookingParamKey = cabinInfo.getRequestId();
			String tokens = cabinInfo.getComment();
			String departureDate = flightObj.getString("departureDate");
			String flightNo = flightObj.getString("flightNo");
			String depCity = "北京";
			String arrCity = "上海";
			String searchParam = "{\"sc\":\""+ depCity +"\",\"dc\":\""+arrCity+"\",\"st\":\""+ departureDate +"\",\"dt\":\"\",\"t\":"+ System.currentTimeMillis() +",\"cb\":4,\"ft\":\"oneway\"}";
			int allPrice = Integer.parseInt(bookMap.get("constructionFee").toString())+Integer.parseInt(bookMap.get("fuelFee").toString())+Integer.parseInt(bookMap.get("basePrice").toString())+Integer.parseInt(bookMap.get("productPrice").toString());
			allPrice = allPrice * passengers.length();
			Map<String, Object> reqMap = new HashMap<>();
			Map<String, Object> priceMap = new HashMap<>();
			priceMap.put("amount",allPrice);
			priceMap.put("insures",new ArrayList<>());
			priceMap.put("insureMinus",new ArrayList<>());
			priceMap.put("XProducts",new ArrayList<>());
			priceMap.put("baggage",new ArrayList<>());
			priceMap.put("common",new Object());
			priceMap.put("marketingList",new Object());
			priceMap.put("vipPriceList",new Object());
			Map<String,Object> ADUMap = new HashMap<>();
			ADUMap.put("fuel",bookMap.get("fuelFee"));
			ADUMap.put("name","成人");
			ADUMap.put("tax",0);
			ADUMap.put("minus",new Object());
			ADUMap.put("number",aduCount);
			Map<String,Object> ticket = new HashMap<>();
			ticket.put("price",bookMap.get("basePrice"));
			ticket.put("num",aduCount);
			ticket.put("name","票价");
			ticket.put("preText","");
			ticket.put("tag","default");
			ticket.put("priceTag","TYL1");
			List<Map> tickets = new ArrayList<>();
			tickets.add(ticket);
			ADUMap.put("ticket",tickets);
			priceMap.put("ADU",ADUMap);
			Map<String,Object> CHIMap = new HashMap<>();
			CHIMap.put("name","儿童");
			CHIMap.put("tax",0);
			CHIMap.put("fuel",0);
			CHIMap.put("ticket",new ArrayList<>());
			CHIMap.put("minus",new Object());
			CHIMap.put("number",chiCount);
			priceMap.put("CHI",CHIMap);
			Map<String,Object> BABYMap = new HashMap<>();
			CHIMap.put("name","婴儿");
			CHIMap.put("tax",0);
			CHIMap.put("fuel",0);
			CHIMap.put("ticket",new ArrayList<>());
			CHIMap.put("minus",new Object());
			CHIMap.put("number",babyCount);
			priceMap.put("BABY",BABYMap);
			Map<String, Object> hotelMap = new HashMap<>();
			hotelMap.put("selectHotelIndex",0);
			hotelMap.put("maxRoomCount",2);
			hotelMap.put("minRoomCount",1);
			hotelMap.put("choseRoomNum",0);
			hotelMap.put("roomInventory",1);
			hotelMap.put("maxAvailNightsNum",1);
			hotelMap.put("totalNightsNum",1);
			Map<String,Object> configmap = new HashMap<>();
			configmap.put("hotelInfos", new Object());
			hotelMap.put("adultNum",aduCount);
			hotelMap.put("adultNum",aduCount);
			hotelMap.put("config",configmap);
			reqMap.put("hotel",hotelMap);
			reqMap.put("price",priceMap);
			reqMap.put("allPrice",allPrice);
			reqMap.put("cashCoupon",new ArrayList<>());
			reqMap.put("cat","touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip");
			reqMap.put("choroToken",UUID.randomUUID().toString());
			Map<String, String> contacterMap = new HashMap<>();
			contacterMap.put("email","");
			contacterMap.put("id","new");
			String linkMan = json.getString("linkMan");
			contacterMap.put("name",linkMan);
			String linkerMobile = json.getString("mobile");
			contacterMap.put("mobile","86-" + linkerMobile);
			reqMap.put("contacter",contacterMap);
			reqMap.put("insure",bookMap.get("insure"));
			Map<String, Object> insureMap = new HashMap<>();
			com.alibaba.fastjson.JSONArray passengerArray = (com.alibaba.fastjson.JSONArray) bookMap.get("passenger");
			Map<String, Object> qmallMap = new HashMap<>();
			for (int k = 0; k < passengerArray.size();k++ ) {
				com.alibaba.fastjson.JSONObject obj = passengerArray.getJSONObject(k);
				String id = obj.getString("id");
				Map<String,Boolean> map = new HashMap<>();
				map.put("combine_ins",false);
				map.put("delay_ins",false);
				insureMap.put(id,map);
				qmallMap.put(id,new ArrayList<>());
			}
			reqMap.put("insureMap",insureMap);
			reqMap.put("insureMinusMap",new ArrayList<>());
			reqMap.put("invoice","");
			reqMap.put("ouid","");
			reqMap.put("priceBase",bookMap.get("priceBase"));
			reqMap.put("qmallMap",qmallMap);
			reqMap.put("passenger",passengerArray);
			reqMap.put("serverStorage",new ArrayList<>());
			reqMap.put("staticData",bookMap.get("staticData"));
			Map<String,String> viewData = new HashMap<>();
			viewData.put("viewTotalPrice",allPrice + "");
			reqMap.put("viewData",viewData);
			reqMap.put("XProductMap",new ArrayList<>());
			String reqParam = JSON.toJSONString(reqMap,SerializerFeature.SortField);
			System.out.println(httpKey + "生单请求参数:" + reqParam);
			String creatOrderUrl = "https://m.flight.qunar.com/flight/tts/createOrder?slen="+reqParam.length()+"&_t="+System.currentTimeMillis()+"&newBooking=true";
			HttpPost post = new HttpPost(creatOrderUrl);
			post.setConfig(defaultRequestConfig);
			post.setHeader("Host", "m.flight.qunar.com");
			post.setHeader("Upgrade-Insecure-Requests", "1");
			post.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
			post.setHeader("Accept", "application/json");
			post.setHeader("Sec-Fetch-Dest", "empty");
			String referer = "https://m.flight.qunar.com/flight/tts/book?bookingParamKey="+bookingParamKey+"&realVendorType=1&searchParam="+ URLEncoder.encode(searchParam,"UTF-8")+"&choroToken="+UUID.randomUUID().toString()+"&tokens=" + URLEncoder.encode(tokens)+ "&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&";
			System.out.println(httpKey + "生单的referer:" + referer);
			post.setHeader("Referer", referer);
			post.setHeader("Accept-Encoding", "gzip, deflate, br");
			post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
			post.setHeader("Cookie", loginMap.get("cookie"));
			post.setHeader("Proxy-Connection", "keep-alive");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			response = httpclient.execute(post);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(httpKey + "booking返回:" + back);
			if (StringUtils.isNotEmpty(back) && back.contains("status")) {

			}
		} catch (Exception e) {
			logger.error( httpKey + "error" , e );
			backMap.put("error",e.getMessage());
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (Exception e2) {
				}
			}
		}
		return backMap;
	}

    private String selectFlights(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String orderJson, Map<String, String> loginMap) throws JSONException {
        String res = "";
        CloseableHttpResponse response = null;
		JSONObject json = new JSONObject(orderJson);
		String order_id = json.getString( "id" );
		String mainUser = json.getString("username");
		String httpKey = mainUser + order_id;
        try {
        	String cookie = loginMap.get("cookie");
			String[] dlyAccountInfo = dlyAccount.split( ":" );
			String proxyUser = dlyAccountInfo[0];
			String proxyPass = dlyAccountInfo[1];
			JSONObject flightObj = json.getJSONArray("flights").getJSONObject(0);
			String flightNo = flightObj.getString("flightNo");
			String dep = flightObj.getString("departure");
			String arr = flightObj.getString("arrival");
			String depCity = "北京";
			String arrCity = "上海";
			String departureDate = flightObj.getString("departureDate");
			HttpPost post = new HttpPost("https://touch.qunar.com/lowFlightInterface/api/getFlightAsyncInfo");
			Map<String, Object> reqMap = new HashMap<>();
			Map<String,Object> bMap = new HashMap<>();
			bMap.put("arr",arrCity);
			bMap.put("cat","touchjpgg_lowFlight-search-box");
			bMap.put("catCopy","touchjpgg_lowFlight-search-box");
			bMap.put("days",4);
			bMap.put("dep",depCity);
			bMap.put("endDate",departureDate);
			bMap.put("env",1);
			bMap.put("flightType","1");
			bMap.put("monitorLowestPrice","0");
			bMap.put("monitorTime","0");
			bMap.put("orderByDirect",false);
			bMap.put("orderByPrice",2);
			bMap.put("orderByTime",0);
			bMap.put("queryTimes",0);
			bMap.put("scrollToload",true);
			bMap.put("showDistribution",true);
			bMap.put("showFullDate",true);
			bMap.put("showLeto",true);
			bMap.put("simpleData","yes");
			bMap.put("startDate",departureDate);
			bMap.put("t","f_urInfo_flight");
			bMap.put("tag","-1");
			String month = departureDate.split("-")[1].substring(1);
			bMap.put("tagNames",month + "月");
			bMap.put("timeout",8000);
			String day = departureDate.substring(5);
			String userSelectStr = day.split("-")[0] + "月";
			userSelectStr = userSelectStr + day.split("-")[1] + "日";
			bMap.put("userSelectStr",userSelectStr);
			bMap.put("weekList",new ArrayList<>());
			bMap.put("weeks",new ArrayList<>());
			reqMap.put("b",bMap);
			reqMap.put("c",new Object());
			String reqjson = JSON.toJSONString(reqMap,SerializerFeature.SortField);
			System.out.println(httpKey + "航班getFlightAsyncInfo查询参数:" + reqjson);
			// reqjson = "{\"b\":{\"env\":1,\"queryTimes\":0,\"dep\":\"北京\",\"arr\":\"上海\",\"showDistribution\":true,\"flightType\":\"1\",\"startDate\":\"2021-05-17\",\"endDate\":\"2021-05-17\",\"userSelectStr\":\"05月17日\",\"tag\":\"-1\",\"tagNames\":\"5月\",\"days\":4,\"timeout\":8000,\"simpleData\":\"yes\",\"weeks\":[],\"weekList\":[],\"catCopy\":\"touchjpgg_lowFlight-search-box\",\"cat\":\"touchjpgg_lowFlight-search-box\",\"orderByTime\":0,\"orderByPrice\":2,\"orderByDirect\":false,\"monitorLowestPrice\":\"0\",\"scrollToload\":true,\"showLeto\":true,\"monitorTime\":\"0\",\"showFullDate\":true,\"t\":\"f_urInfo_flight\"},\"c\":{}}";
			StringEntity s = new StringEntity(reqjson,"utf-8");
			post.setConfig(defaultRequestConfig);
			post.setEntity(s);
			post.setHeader("accept","*/*");
			post.setHeader("accept-encoding","gzip, deflate, br");
			post.setHeader("accept-language","zh-CN,zh;q=0.9");
			post.setHeader("content-type","application/json; charset=UTF-8");
			post.setHeader("Host","touch.qunar.com");
			post.setHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
			post.setHeader("cookie",cookie);
			post.setHeader("origin","https://touch.qunar.com");
			String referer = "https://touch.qunar.com/lowFlight/flightList?env=0&dep="+ URLEncoder.encode(depCity) +"&arr="+ URLEncoder.encode(arrCity) +"&flightType=1&catCopy=touchjpgg&cat=touchjpgg_lowFlight-search-box&tag=-1&weeks=&days=&tagNames=&accurateDateNames="+URLEncoder.encode(userSelectStr) +"&startDate="+departureDate+"&endDate="+departureDate+"&monitorTime=0&monitorLowestPrice=0&hywebkit=1";
			System.out.println(httpKey + "航班查询getFlightAsyncInforeferer:" + referer);
		    post.setHeader("referer",referer);
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
            post.setHeader( "Proxy-Connection" , "keep-alive" );
            response = httpclient.execute(post);
			String back = EntityUtils.toString(response.getEntity(),"utf-8");
			System.out.println(httpKey + "航班getFlightAsyncInfo返回:" + back);
			if (StringUtils.isNotEmpty(back)) {
				if (back.contains("bstatus")) {
					com.alibaba.fastjson.JSONObject resObj = JSON.parseObject(back);
					com.alibaba.fastjson.JSONObject bstatusObj = resObj.getJSONObject("bstatus");
					int code = bstatusObj.getIntValue("code");
					if(code == 0) {
						Header[] headers = response.getAllHeaders();
						for (int i = 0; i < headers.length; i++) {
							if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
								cookie = cookie + headers[i].getValue();
							}
						}
						com.alibaba.fastjson.JSONArray flightList = resObj.getJSONObject("data").getJSONArray("flightList");
						String touchToken = "";

						for (int i = 0 ; i < flightList.size(); i++) {
							com.alibaba.fastjson.JSONObject flightInfoObj = flightList.getJSONObject(i);
							com.alibaba.fastjson.JSONObject binfoObj = flightInfoObj.getJSONObject("binfo");
							String airCode = binfoObj.getString("airCode");
							if (flightNo.equals(airCode)) {
								String prefix = flightInfoObj.getString("prefix");
								String array[] = prefix.split("&");
								for (String ss:array) {
									if (StringUtils.isNotEmpty(ss) && ss.contains("touchToken")) {
										touchToken = ss.split("=")[1];
										break;
									}
								}
							}
							if (StringUtils.isNotEmpty(touchToken)) {
								break;
							}
						}
						if (StringUtils.isNotEmpty(touchToken)) {
							String deailUrl = "startCity="+depCity +"&destCity="+ arrCity +"&startDate="+departureDate+"&code="+flightNo+"&touchToken="+touchToken+ "&from=f_athena_urban&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&extparams={%22ex_track%22:%22AthenaUrban%22}&_firstScreen=1&_gogokid=12";
							HttpGet get = new HttpGet("https://m.flight.qunar.com/ncs/page/flightdetail?" + URLEncoder.encode(deailUrl,"utf-8"));
							get.setConfig(defaultRequestConfig);
							get.setHeader("Host","m.flight.qunar.com");
							get.setHeader("Upgrade-Insecure-Requests","1");
							get.setHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
							get.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
							get.setHeader("Referer","https://touch.qunar.com/");
							get.setHeader("Accept-Encoding","gzip, deflate, br");
							get.setHeader("Cookie",cookie);
							get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
							get.setHeader( "Proxy-Connection" , "keep-alive" );
							CloseableHttpResponse  response1 = httpclient.execute(get);
							HttpEntity entity = response1.getEntity();
							back = EntityUtils.toString(entity,"utf-8");
							System.out.println(httpKey + "查询价格详情界面:" + back);
							String newCookie = "";
							headers = response1.getAllHeaders();
							for (int i = 0; i < headers.length; i++) {
								if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
									newCookie = headers[i].getValue();
									break;
								}
							}
							if (StringUtils.isNotEmpty(newCookie)) {
								cookie = cookie+""+newCookie;
							}
							String c[] =cookie.split(";");
							String qtime = "";
							for (int j = 0; j < c.length; j++) {
								if(c[j].contains("QN668")){
									qtime = URLDecoder.decode(c[j].split("=")[1],"utf-8");
								}
							}
							Document doc = Jsoup.parse(back);
							String qunar_api_token = doc.getElementById("qunar_api_token").text();
							String ss [] = back.split("'\\),'");

							StringBuffer sb = new StringBuffer();
							for (int k = 0; k < ss.length; k++) {
								int len = ss[k].split("','").length;
								if(len > 10 && len < 15){
									String cc [] = ss[k].split("','");
									for (int j = 0; j < cc.length; j++) {
										if(j < 11){
											cc[j].split("':'");
											sb.append(cc[j].split("':'")[1]+":");
										}
									}
								}
							}
							String header = FingerPrintUtil.getQunarHeader(sb.toString());
							String m = FingerPrintUtil.getQunarM(qunar_api_token,qtime);
							String token = FingerPrintUtil.getQunarToken(qunar_api_token,qtime);
							System.out.println(httpKey + "查询航班详情qunar_api_token:"+ qunar_api_token + ".查询航班详情m:" + m + ",详情token:" + token + ",查询航班详情header:" + header);
							String ps [] = token.split(":");
							post = new HttpPost("https://m.flight.qunar.com/flight/api/touchInnerOta");
							post.setConfig(defaultRequestConfig);
							post.setHeader(ps[0],ps[1]);
							post.setHeader("csht","");
							post.setHeader("Accept","application/json, text/javascript");
							post.setHeader("Accept-Encoding","gzip, deflate, br");
							post.addHeader("Accept-Language","zh-CN,zh;q=0.9");
							post.setHeader("Content-Type","application/x-www-form-urlencoded");
							post.setHeader("Host","m.flight.qunar.com");
							post.setHeader("Origin","https://m.flight.qunar.com");
							post.setHeader("pre",header);
							referer = "https://m.flight.qunar.com/ncs/page/flightdetail?startCity="+URLEncoder.encode(depCity)+"&destCity="+URLEncoder.encode(arrCity)+"&startDate="+departureDate+"&code="+flightNo+"&touchToken="+touchToken+"&from=f_athena_urban&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&extparams={%22ex_track%22:%22AthenaUrban%22}&_firstScreen=1&_gogokid=12";
							post.setHeader("Referer",referer);
							post.setHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
							post.setHeader("X-Requested-With","XMLHttpRequest");
							post.setHeader("wps","6");
							post.setHeader("Cookie",cookie);
							post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
							post.setHeader( "Proxy-Connection" , "keep-alive" );
							long time = System.currentTimeMillis();
							List<NameValuePair> nameValuePairs = new ArrayList<>();
							nameValuePairs.add(new BasicNameValuePair("backDate",""));
							nameValuePairs.add(new BasicNameValuePair("cat","touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip"));
							nameValuePairs.add(new BasicNameValuePair("code",flightNo));
							nameValuePairs.add(new BasicNameValuePair("destCity",arrCity));
							nameValuePairs.add(new BasicNameValuePair("extparams","{\"ex_track\":\"AthenaUrban\"}"));
							nameValuePairs.add(new BasicNameValuePair("from","f_athena_urban"));
							nameValuePairs.add(new BasicNameValuePair("startCity",depCity));
							nameValuePairs.add(new BasicNameValuePair("startDate",departureDate));
							nameValuePairs.add(new BasicNameValuePair("touchToken",touchToken));
							nameValuePairs.add(new BasicNameValuePair("reqSource","touch"));
							nameValuePairs.add(new BasicNameValuePair("_v","2"));
							nameValuePairs.add(new BasicNameValuePair("st",time+""));
							nameValuePairs.add(new BasicNameValuePair("__m__",m));
							post.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8"));
							response = httpclient.execute(post);
							back = EntityUtils.toString(response.getEntity(),"utf-8");
							System.out.println(httpKey + "查询价格详情返回:" + back);
							System.out.println(httpKey + "查询价格详情的cookie:" + cookie);
							// csrfToken
							// _RGUID
							List<CabinInfo> listCabin = new ArrayList<CabinInfo>();
							String infos[] = back.split("\\\\\\\\\\\\\"title\\\\\\\\\\\\\"");
							for (int j = 0; j < infos.length; j++) {
								back = infos[j]+"\\\\\\\\\\\\\"title\\\\\\\\\\\\\"";
								Pattern p7=Pattern.compile("\\\\\\\\\\\\\"price\\\\\\\\\\\\\":\\\\\\\\\\\\\"(\\d{1,6})");
								Matcher matcher7=p7.matcher(back);
								CabinInfo cabinInfo = null;
								if(matcher7.find()){
									String price=matcher7.group(1);
									cabinInfo = new CabinInfo();
									cabinInfo.setPrice(price);
								}
								Pattern p6=Pattern.compile("(\\\\\"insurDesc\\\\\":\\\\\"([\u4E00-\u9FA5]{1,5})\\\\\"){0,1},\\\\\"insurPrice\\\\\":\\\\\"(\\d{1,4}){0,1}");
								Matcher matcher6=p6.matcher(back);
								while(matcher6.find()){
									String type=matcher6.group(2);
									String bx=matcher6.group(3);
									if(cabinInfo != null){
										if("航意险".equals(type)){
											cabinInfo.setPrice(Float.parseFloat(cabinInfo.getPrice())+Float.parseFloat(bx)+"");
											cabinInfo.setBaseCabin(Float.parseFloat(bx) + "元航意险");
										}

									}
								}
								Pattern p2=Pattern.compile("\\\\\\\\\\\\\"name\\\\\\\\\\\\\":\\\\\\\\\\\\\"([\u4E00-\u9FA5]{0,10})\\\\\\\\\\\\\",");
								Matcher matcher2=p2.matcher(back);
								while(matcher2.find()){
									String title=matcher2.group(1);
									if(cabinInfo != null){
										cabinInfo.setPriceType(title);
									}
								}
								Pattern p3=Pattern.compile("\\\\\\\"bookingParamKey\\\\\\\":\\\\\\\"(f_domestic_uniform_booking_[a-zA-Z0-9]{6}_[a-zA-Z0-9]{4}_[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})");
								Matcher matcher3=p3.matcher(back);
								while(matcher3.find()){
									String bookingParamKey=matcher3.group(1);
									if(cabinInfo != null){
										cabinInfo.setRequestId(bookingParamKey);
									}
								}
								Pattern p4=Pattern.compile("\\\\\\\\\\\\\"cabin\\\\\\\\\\\\\":\\\\\\\\\\\\\"([a-zA-Z0-9]{1,2})");
								Matcher matcher4=p4.matcher(back);
								while(matcher4.find()){
									String cabin=matcher4.group(1);
									if(cabinInfo != null){
										cabinInfo.setCabinCode(cabin);
									}
								}
								Pattern p5= Pattern.compile("\\\\\"pid\\\\\":\\d{1,15},\\\\\"token\\\\\":\\\\\"([a-zA-Z0-9]{32})\\\\\",\\\\\"expCode\\\\\":\\\\\"([a-zA-Z0-9]{1,20})\\\\\",\\\\\"productTypeCode\\\\\":\\\\\"([a-zA-Z0-9]{1,20})\\\\\"");
								Matcher matcher5=p5.matcher(back);
								while(matcher5.find()){
									String ss1=matcher5.group(1);
									String ss2=matcher5.group(2);
									String ss3=matcher5.group(3);
									if(cabinInfo != null){
										cabinInfo.setComment("[{\"token\":\""+ss1+"\",\"expCode\":\""+ss2+"\",\"expType\":\""+ss3+"\"}]");
									}
								}
								if(cabinInfo != null){
									listCabin.add(cabinInfo);
								}
							}
							String orderCabin = flightObj.getString("cabin");
							String orderPrice = flightObj.getString("price");
							for (CabinInfo cabinInfo:listCabin) {
								if (orderCabin.equals(cabinInfo.getCabinCode())) {
									if (Float.valueOf(orderPrice).intValue() >= Float.valueOf(cabinInfo.getPrice())) {
										String cookieArray[] = cookie.split(";");
										String csrfToken = "";
										for (String ck:cookieArray) {
											if (ck.contains("csrfToken")) {
												csrfToken = ck.split("=")[1];
											}
										}
										cabinInfo.setPakId(touchToken);
										cabinInfo.setDlyId(csrfToken);
										res = JSON.toJSONString(cabinInfo);
										loginMap.put("cookie",cookie);
										break;
									} else {
										res = "error:官网变价,官网价格" + cabinInfo.getPrice();
									}
								}
							}
						}
					}
				}
			}
        } catch (Exception e) {
            logger.error( "error" , e );
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e2) {
            }
        }
		System.out.println(httpKey + "选择航班返回:" + res);
        return res;
    }

    @Test
	public void touchInnerOtatest() throws IOException {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("/flight/api/touchInnerOta");
		post.setHeader("Host","m.flight.qunar.com");
		post.setHeader("csht","");
		String header = "4c330b35-1f5b57-55458892-42994c00-f41f268357f7";
		post.setHeader("pre",header);
		post.setHeader("eaaba9","7f8f6d9ca9052d3f1587dfb7d7f60d18c3a9b688");
		post.setHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
		post.setHeader("Content-Type","application/x-www-form-urlencoded");
		post.setHeader("Accept","application/json, text/javascript");
		post.setHeader("Accept-Encoding","gzip, deflate, br");
		post.addHeader("Accept-Language","zh-CN,zh;q=0.9");
		post.setHeader("X-Requested-With","XMLHttpRequest");
		post.setHeader("Origin","https://m.flight.qunar.com");
		post.setHeader("Sec-Fetch-Site","same-origin");
		post.setHeader("Sec-Fetch-Mode","cors");
		post.setHeader("Sec-Fetch-Dest","empty");
		post.setHeader("Connection","keep-alive");
		post.setHeader("wps","21");
		String referer = "https://m.flight.qunar.com/ncs/page/flightdetail?startCity=%E5%8C%97%E4%BA%AC&destCity=%E4%B8%8A%E6%B5%B7&startDate=2021-05-17&code=KN5977&touchToken=14B8568A20647F60787A1B75A19CEB95&from=f_athena_urban&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&extparams={%22ex_track%22:%22AthenaUrban%22}&_firstScreen=1&_gogokid=12";
		post.setHeader("Referer",referer);
		String cookie = "QN1=00001380306c2b450bc8b429; QN300=auto_4e0d874a; QN99=4905; QunarGlobal=10.86.213.148_-1d98207b_175cf9bc46c_6611|1605507962745; QN601=40440cd22bbc086230da18f7ddc722da; _i=VInJO_2sQ2tqWBm1mmT4BfwJWjxq; QN48=000031802f102b450bd81c37; fid=029cad0c-dc91-4f40-854d-eb7f221f7849; quinn=4a59aba0e9e761fb0ce4277cc1bacc4d16e7d7d59fd85fa0eb8a2f2c0a5bb02b3d33c8650a9cdb4e776a4696e43cc5e9; SC1=66e88fdf578c8052bc254b83e0cf5e87; SC18=; QN269=FA581840A94F11EBB550FA163EEFDE16; QN205=auto_4e0d874a; QN277=auto_4e0d874a; Alina=a279b8ac-925339-8449bb47-51919d87-4995609a1741; QN66=qunar; QN275=8+AByHPuwtDZIqGmOAFwKP5dbpYf9KyJD/wLv/n4NQk=; QN43=2; QN42=%E9%9A%8F%E9%A3%8E%E4%B9%8B%E4%BA%BA; _q=U.pnkerrk1334; _t=27122750; csrfToken=aifF0b0hxvhKNygwOAbGfoegCMWpbs5c; _s=s_RLXWZHEYVN37R5A5CUX2ZM4DTM; _v=ICTdlR3lmeVHwFxfT-1LkoAPNfVcIpU58SMsHqJnI0mzZRl6571ypuj3lUmzZaILxtcHOZyPdpwSfgVokS2qmM0mt-60sbUX_VAaapa_ZPe9Xmf2X-ewNdlqPy-4IWjqZ9SZtFPYjHzPW8IiFPIHFyF355LJRp2IlBKZvYuiDlRu; QN44=pnkerrk1334; QN267=277895125b0b8f2e7; _vi=ednxKfL6Gub-Yh8umGERZSD9eZ8Q7BFlIeGAyyk6D_j3_3xfSkY9Z0od6S-ewf6cxCWEoKK74hTLxyKuNpqkwEEQ5UYP2P3nAknjEAeA3xWz0uomX6iFNgFdzOYr53kfKCWshEAOUsm3rTyUqCm5RB2nt7kgTRFFviOAecINrqtE; QN163=0; QN6=auto_4e0d874a; QN271=bd16eb21-06dd-4ab8-a57e-634d47d74579; QN667=C; QN621=1490067914133%3DDEFAULT%26fr%3Df_athena_urban; F235=1620868931570; QN668=51%2C56%2C52%2C50%2C58%2C57%2C53%2C51%2C50%2C51%2C59%2C52%2C59";
				// "QN1=00001380306c2b450bc8b429; QN300=auto_4e0d874a; QN99=4905; QunarGlobal=10.86.213.148_-1d98207b_175cf9bc46c_6611|1605507962745; QN601=40440cd22bbc086230da18f7ddc722da; _i=VInJO_2sQ2tqWBm1mmT4BfwJWjxq; QN48=000031802f102b450bd81c37; fid=029cad0c-dc91-4f40-854d-eb7f221f7849; quinn=4a59aba0e9e761fb0ce4277cc1bacc4d16e7d7d59fd85fa0eb8a2f2c0a5bb02b3d33c8650a9cdb4e776a4696e43cc5e9; SC1=66e88fdf578c8052bc254b83e0cf5e87; SC18=; QN269=FA581840A94F11EBB550FA163EEFDE16; QN205=auto_4e0d874a; QN277=auto_4e0d874a; Alina=a279b8ac-925339-8449bb47-51919d87-4995609a1741; QN66=qunar; QN275=8+AByHPuwtDZIqGmOAFwKP5dbpYf9KyJD/wLv/n4NQk=; QN43=2; QN42=%E9%9A%8F%E9%A3%8E%E4%B9%8B%E4%BA%BA; _q=U.pnkerrk1334; _t=27122750; csrfToken=aifF0b0hxvhKNygwOAbGfoegCMWpbs5c; _s=s_RLXWZHEYVN37R5A5CUX2ZM4DTM; _v=ICTdlR3lmeVHwFxfT-1LkoAPNfVcIpU58SMsHqJnI0mzZRl6571ypuj3lUmzZaILxtcHOZyPdpwSfgVokS2qmM0mt-60sbUX_VAaapa_ZPe9Xmf2X-ewNdlqPy-4IWjqZ9SZtFPYjHzPW8IiFPIHFyF355LJRp2IlBKZvYuiDlRu; QN44=pnkerrk1334; QN267=277895125b0b8f2e7; _vi=ednxKfL6Gub-Yh8umGERZSD9eZ8Q7BFlIeGAyyk6D_j3_3xfSkY9Z0od6S-ewf6cxCWEoKK74hTLxyKuNpqkwEEQ5UYP2P3nAknjEAeA3xWz0uomX6iFNgFdzOYr53kfKCWshEAOUsm3rTyUqCm5RB2nt7kgTRFFviOAecINrqtE; QN163=0; QN6=auto_4e0d874a; QN271=bd16eb21-06dd-4ab8-a57e-634d47d74579; QN667=C; QN668=51%2C56%2C52%2C50%2C58%2C56%2C58%2C59%2C53%2C50%2C56%2C53%2C55; QN621=1490067914133%3DDEFAULT%26fr%3Df_athena_urban; F235=1620868930668";
				// "QN1=00001380306c2b450bc8b429; QN300=auto_4e0d874a; QN99=4905; QunarGlobal=10.86.213.148_-1d98207b_175cf9bc46c_6611|1605507962745; QN601=40440cd22bbc086230da18f7ddc722da; _i=VInJO_2sQ2tqWBm1mmT4BfwJWjxq; QN48=000031802f102b450bd81c37; fid=029cad0c-dc91-4f40-854d-eb7f221f7849; quinn=4a59aba0e9e761fb0ce4277cc1bacc4d16e7d7d59fd85fa0eb8a2f2c0a5bb02b3d33c8650a9cdb4e776a4696e43cc5e9; SC1=66e88fdf578c8052bc254b83e0cf5e87; SC18=; QN269=FA581840A94F11EBB550FA163EEFDE16; QN205=auto_4e0d874a; QN277=auto_4e0d874a; Alina=a279b8ac-925339-8449bb47-51919d87-4995609a1741; QN66=qunar; QN275=8+AByHPuwtDZIqGmOAFwKP5dbpYf9KyJD/wLv/n4NQk=; QN6=auto_4e0d874a; QN163=0; QN667=B; F235=1620783502737; QN621=1490067914133%3DDEFAULT%26fr%3Df_athena_urban; QN25=f068b0d5-7ea1-4f8a-8026-9d26a72c3015-9f992f90; QN43=2; QN42=%E9%9A%8F%E9%A3%8E%E4%B9%8B%E4%BA%BA; _q=U.pnkerrk1334; _t=27122750; csrfToken=aifF0b0hxvhKNygwOAbGfoegCMWpbs5c; _s=s_RLXWZHEYVN37R5A5CUX2ZM4DTM; _v=ICTdlR3lmeVHwFxfT-1LkoAPNfVcIpU58SMsHqJnI0mzZRl6571ypuj3lUmzZaILxtcHOZyPdpwSfgVokS2qmM0mt-60sbUX_VAaapa_ZPe9Xmf2X-ewNdlqPy-4IWjqZ9SZtFPYjHzPW8IiFPIHFyF355LJRp2IlBKZvYuiDlRu; _vi=HbKOyKhF5fHP-wRMkNGD9iZkvsm4JueMIBVGfee0OTbRK8nsJVjs2pyjh5KCE_VaicG7CiqhFCERiFagwSv8oeo3h_rg8BTgb3a_-TGrcvYnIKJJAW79kzjs0yNat_PXnOo41K8wFQEWsFZ5jXz5YmgREqxxHmyAoUyYiMcZkU4M; QN44=pnkerrk1334; QN267=277895125b679253b; QN271=9421ed46-7693-4619-bec6-d1f1f4b791d6; QN668=51%2C56%2C52%2C50%2C58%2C52%2C53%2C54%2C51%2C52%2C58%2C55%2C53";
		post.setHeader("Cookie",cookie);
		long time = 1620873101750L;
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		nameValuePairs.add(new BasicNameValuePair("backDate",""));
		nameValuePairs.add(new BasicNameValuePair("cat","touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip"));
		nameValuePairs.add(new BasicNameValuePair("code",""));
		nameValuePairs.add(new BasicNameValuePair("destCity","上海"));
		nameValuePairs.add(new BasicNameValuePair("extparams","{\"ex_track\":\"AthenaUrban\"}"));
		nameValuePairs.add(new BasicNameValuePair("from","f_athena_urban"));
		nameValuePairs.add(new BasicNameValuePair("startCity","北京"));
		nameValuePairs.add(new BasicNameValuePair("startDate","2021-05-17"));
		String touchToken = "14B8568A20647F60787A1B75A19CEB95";
		nameValuePairs.add(new BasicNameValuePair("touchToken",touchToken));
		nameValuePairs.add(new BasicNameValuePair("reqSource","touch"));
		nameValuePairs.add(new BasicNameValuePair("_v","2"));
		nameValuePairs.add(new BasicNameValuePair("st",time+""));
		String m = "5ed0ec2f389883637608a27e254b3131";
		nameValuePairs.add(new BasicNameValuePair("__m__",m));
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs,"UTF-8");
		HttpEntity entity = new StringEntity("backDate=&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&code=KN5977&destCity=%E4%B8%8A%E6%B5%B7&extparams=%7B%22ex_track%22%3A%22AthenaUrban%22%7D&from=f_athena_urban&startCity=%E5%8C%97%E4%BA%AC&startDate=2021-05-17&touchToken=14B8568A20647F60787A1B75A19CEB95&reqSource=touch&_v=2&st=1620868930404&__m__=aaf97354184be5b6148016fd276efb0f");
		post.setEntity(entity);
		HttpHost target = new HttpHost("m.flight.qunar.com", 443, "https");
		CloseableHttpResponse response = httpclient.execute(target,post);
		String back = EntityUtils.toString(response.getEntity());
		System.out.println("查询价格详情返回:" + back);
	}

    private String parseResult(String back , String orderJson) {
		String res = "";
        try {
            JSONObject json = new JSONObject( orderJson );
            JSONObject flight = json.getJSONArray( "flights" ).getJSONObject( 0 );
            String sys_flightNo = flight.getString( "flightNo" );
            float sys_price = Float.parseFloat( flight.getString( "price" ) );
            String sys_cabin = flight.getString("cabin");
            String fType = json.getString("ytype");
			com.alibaba.fastjson.JSONObject obj = JSON.parseObject(back);
			com.alibaba.fastjson.JSONArray flightInfoLists = obj.getJSONArray("flightInfoList");
			for (int i = 0; i < flightInfoLists.size(); i++) {
				com.alibaba.fastjson.JSONObject flightInfoList = flightInfoLists.getJSONObject(i);
				String flightNo = flightInfoList.getString("flightNo");
				if(sys_flightNo.equals(flightNo)){
					com.alibaba.fastjson.JSONArray cabinFareLists = flightInfoList.getJSONArray("cabinFareList");
					for (int j = 0; j < cabinFareLists.size(); j++) {
						com.alibaba.fastjson.JSONObject cabinFareList = cabinFareLists.getJSONObject(j);
						String cabinCode = cabinFareList.getString("cabinCode");
						String cabinType = cabinFareList.getString("cabinType");
						String priceValue = cabinFareList.getString("priceValue");
						if ("改期券".equals(fType)) {
							fType = "HIGH_REBATE";
						} else if ("会员专享".equals(fType)) {
							fType = "MEMBER_FARE";
						}else if ("青年特惠".equals(fType)) {
							fType = "YOUTH_FARE";
						}else if ("行李优享".equals(fType)) {
							fType = "BAGGAGE_FARE";
						}else if ("无特惠".equals(fType)) {
							fType = "CABIN_NORMAL";
						}
						if(sys_cabin.equals(cabinCode) && fType.equals(cabinType)){
							if (sys_price >= Float.valueOf(priceValue)) {
								com.alibaba.fastjson.JSONArray newCabinFareList = new com.alibaba.fastjson.JSONArray();
								cabinFareList.put("refundedComment","");
								cabinFareList.put("changedComment","");
								cabinFareList.put("combineId","");
								cabinFareList.put("combineRuleInfo","");
								cabinFareList.put("travelPrivilegeList","");
								cabinFareList.put("changeDesc","");
								cabinFareList.put("activityAvailable","");
								cabinFareList.put("scoreAvailable",false);
								cabinFareList.put("totalTax",0);
								cabinFareList.put("validityPeriod","");
								cabinFareList.put("carrierNo","");
								cabinFareList.put("priceValueCombDiff","");
								cabinFareList.put("minStay","");
								cabinFareList.put("priceValueComb","");
								cabinFareList.put("cabinLabelBrand","");
								cabinFareList.put("combineRuleInfo","");
								cabinFareList.put("refundedComment","");
								cabinFareList.put("otherTaxList","");
								cabinFareList.put("cabinLabelList","");
								cabinFareList.put("cabinLabelRound","");
								cabinFareList.put("segmentCabinInfos","");
								cabinFareList.put("isTranFlight",false);
								cabinFareList.put("couponAvailable",false);
								cabinFareList.put("cabinComb","");
								cabinFareList.put("fares","");
								cabinFareList.put("advertisements","");
								cabinFareList.put("scoreGiftInfo","");
								cabinFareList.put("brandPriceRule","");
								cabinFareList.put("minPriceCabin",false);
								cabinFareList.put("cabinLabel","");
								cabinFareList.put("combineId","");
								cabinFareList.put("weightLimit","");
								cabinFareList.put("taxInfo","");
								// cabinFareList.put("ei","");
								cabinFareList.put("scoreUseInfoList","");
								cabinFareList.put("comment","");
								cabinFareList.put("changedComment","");
								cabinFareList.put("cabinCombClassName","");
								cabinFareList.remove("travelPrivilegeList");
								cabinFareList.remove("xtax");
								cabinFareList.remove("activityAvailable");
								cabinFareList.remove("validityPeriod");
								cabinFareList.remove("carrierNo");
								cabinFareList.remove("cabinCombClassName");
								cabinFareList.remove("refundedComment");
								cabinFareList.remove("otherTaxList");
								cabinFareList.remove("cabinLabelList");
								cabinFareList.remove("cabinLabelRound");
								cabinFareList.remove("segmentCabinInfos");
								cabinFareList.remove("advertisements");
								cabinFareList.remove("scoreGiftInfo");
								cabinFareList.remove("brandPriceRule");
								cabinFareList.remove("taxInfo");
								cabinFareList.remove("scoreUseInfoList");
								cabinFareList.remove("checkBaggeage");
								cabinFareList.remove("changeDesc");
								cabinFareList.remove("cabinLabel");
								cabinFareList.remove("minStay");
								// cabinFareList.remove("ei");
								cabinFareList.remove("priceValueComb");
								cabinFareList.remove("cabinLabelBrand");
								cabinFareList.remove("combineRuleInfo");
								cabinFareList.remove("flightNo");
								cabinFareList.remove("checkBaggeageRemark");
								cabinFareList.remove("cabinComb");
								cabinFareList.remove("fares");
								cabinFareList.remove("handBaggeageRemark");
								cabinFareList.remove("combineId");
								cabinFareList.remove("handBaggeage");
								cabinFareList.remove("weightLimit");
								cabinFareList.remove("specialRemark");
								cabinFareList.remove("comment");
								cabinFareList.remove("changedComment");
								cabinFareList.put("cnTaxDiff",cabinFareList.getIntValue("cnTaxDiff"));
								cabinFareList.put("cntax",cabinFareList.getIntValue("cntax"));
								cabinFareList.put("discountPriceValue",cabinFareList.getIntValue("discountPriceValue"));
								cabinFareList.put("priceValue",cabinFareList.getIntValue("priceValue"));
								cabinFareList.put("rsp",cabinFareList.getIntValue("rsp"));
								cabinFareList.put("taxDiff",cabinFareList.getIntValue("taxDiff"));
								cabinFareList.put("cnTaxDiff",cabinFareList.getIntValue("cnTaxDiff"));
								cabinFareList.put("ticketPriceDiff",cabinFareList.getIntValue("ticketPriceDiff"));
								cabinFareList.put("totalDiff",cabinFareList.getIntValue("totalDiff"));
								cabinFareList.put("yprice",cabinFareList.getIntValue("yprice"));
								cabinFareList.put("yqTaxDiff",cabinFareList.getIntValue("yqTaxDiff"));
								cabinFareList.put("changeServiceCharge",0);
								cabinFareList.put("refundFee",0);
								cabinFareList.put("changeFee",0);
								cabinFareList.put("refundFeeAfter",0);
								cabinFareList.put("changeFeeAfter",0);
								cabinFareList.put("yqtax",cabinFareList.getIntValue("yqtax"));
								if (cabinFareList.containsKey("childRule")) {
									com.alibaba.fastjson.JSONObject childRule = cabinFareList.getJSONObject("childRule");
									com.alibaba.fastjson.JSONArray changeRuleList = childRule.getJSONArray("changeRuleList");
									for (int k = 0 ; k < changeRuleList.size(); k++) {
										com.alibaba.fastjson.JSONObject changeRule = changeRuleList.getJSONObject(k);
										changeRule.put("changeFee",changeRule.getIntValue("changeFee"));
									}
									com.alibaba.fastjson.JSONArray refundRuleList = childRule.getJSONArray("refundRuleList");
									for (int k = 0 ; k < refundRuleList.size(); k++) {
										com.alibaba.fastjson.JSONObject refundRule = refundRuleList.getJSONObject(k);
										refundRule.put("changeFee",refundRule.getIntValue("changeFee"));
									}
								}
								if (cabinFareList.containsKey("refundedRules")) {
									com.alibaba.fastjson.JSONArray refundedRules = cabinFareList.getJSONArray("refundedRules");
									for (int k = 0; k < refundedRules.size();k++) {
										com.alibaba.fastjson.JSONObject refundedRule = refundedRules.getJSONObject(k);
										refundedRule.put("fee",refundedRule.getIntValue("fee"));
										refundedRule.put("usedTicketPrice",refundedRule.getIntValue("usedTicketPrice"));
									}
								}
								if (cabinFareList.containsKey("changeRuleList")) {
									com.alibaba.fastjson.JSONArray changeRules= cabinFareList.getJSONArray("changeRuleList");
									for (int k = 0; k < changeRules.size();k++) {
										com.alibaba.fastjson.JSONObject refundedRule = changeRules.getJSONObject(k);
										refundedRule.put("changeFee",refundedRule.getIntValue("changeFee"));
									}
								}
								if (cabinFareList.containsKey("changeRules")) {
									com.alibaba.fastjson.JSONArray changeRules2= cabinFareList.getJSONArray("changeRules");
									for (int k = 0; k < changeRules2.size();k++) {
										com.alibaba.fastjson.JSONObject refundedRule = changeRules2.getJSONObject(k);
										refundedRule.put("fee",refundedRule.getIntValue("fee"));
									}
								}
								if (cabinFareList.containsKey("cabinLabelList2")) {
									com.alibaba.fastjson.JSONArray cabinLabelList2 = cabinFareList.getJSONArray("cabinLabelList2");
									for (int k = 0; k < cabinLabelList2.size();k++) {
										com.alibaba.fastjson.JSONObject cabinLabel = cabinLabelList2.getJSONObject(k);
										cabinLabel.put("labelNum",cabinLabel.getString("labelNum"));
									}
								}
								if (cabinFareList.containsKey("infRule")) {
									com.alibaba.fastjson.JSONObject infRule = cabinFareList.getJSONObject("infRule");
									com.alibaba.fastjson.JSONArray infchangeRuleList = infRule.getJSONArray("changeRuleList");
									for (int k = 0; k < infchangeRuleList.size();k++) {
										com.alibaba.fastjson.JSONObject infchangeRule = infchangeRuleList.getJSONObject(k);
										infchangeRule.put("changeFee", infchangeRule.getIntValue("changeFee"));
									}
									com.alibaba.fastjson.JSONArray infrefundRuleList = infRule.getJSONArray("refundRuleList");
									for (int k = 0; k < infrefundRuleList.size();k++) {
										com.alibaba.fastjson.JSONObject infrefundRule = infrefundRuleList.getJSONObject(k);
										infrefundRule.put("changeFee", infrefundRule.getIntValue("changeFee"));
									}
								}
								newCabinFareList.add(cabinFareList);
								flightInfoList.put("cabinFareList",newCabinFareList);
								flightInfoList.put("hotSaleLabelList","");
								flightInfoList.put("transferInfo","");
								flightInfoList.put("stopDepTime","");
								flightInfoList.put("stopAirport","");
								flightInfoList.put("stopArrTime","");
								flightInfoList.put("saleInfo","");
								flightInfoList.put("flightInfoReturnList","");
								flightInfoList.put("minDiffPrice","0");
								com.alibaba.fastjson.JSONArray cabinCHDINFFareList = flightInfoList.getJSONArray("cabinCHDINFFareList");
								for (int k = 0; k < cabinCHDINFFareList.size(); k++) {
									cabinCHDINFFareList.getJSONObject(k).remove("travelPrivilegeList");
									cabinCHDINFFareList.getJSONObject(k).remove("xtax");
									cabinCHDINFFareList.getJSONObject(k).remove("activityAvailable");
									cabinCHDINFFareList.getJSONObject(k).remove("activityLabel");
									cabinCHDINFFareList.getJSONObject(k).remove("validityPeriod");
									cabinCHDINFFareList.getJSONObject(k).remove("carrierNo");
									cabinCHDINFFareList.getJSONObject(k).remove("priceValueCombDiff");
									cabinCHDINFFareList.getJSONObject(k).remove("cabinCombClassName");
									cabinCHDINFFareList.getJSONObject(k).remove("refundedComment");
									cabinCHDINFFareList.getJSONObject(k).remove("otherTaxList");
									cabinCHDINFFareList.getJSONObject(k).remove("cabinLabelList");
									cabinCHDINFFareList.getJSONObject(k).remove("cabinLabelRound");
									cabinCHDINFFareList.getJSONObject(k).remove("personalizedDisplay");
									cabinCHDINFFareList.getJSONObject(k).remove("segmentCabinInfos");
									cabinCHDINFFareList.getJSONObject(k).remove("chdBaggage");
									cabinCHDINFFareList.getJSONObject(k).remove("infFreeBaggage");
									cabinCHDINFFareList.getJSONObject(k).remove("advertisements");
									cabinCHDINFFareList.getJSONObject(k).remove("scoreGiftInfo");
									cabinCHDINFFareList.getJSONObject(k).remove("brandPriceRule");
									cabinCHDINFFareList.getJSONObject(k).remove("childRule");
									cabinCHDINFFareList.getJSONObject(k).remove("taxInfo");
									cabinCHDINFFareList.getJSONObject(k).remove("scoreUseInfoList");
									cabinCHDINFFareList.getJSONObject(k).remove("checkBaggeage");
									cabinCHDINFFareList.getJSONObject(k).remove("changeDesc");
									cabinCHDINFFareList.getJSONObject(k).remove("cabinLabel");
									cabinCHDINFFareList.getJSONObject(k).remove("infRule");
									cabinCHDINFFareList.getJSONObject(k).remove("minStay");
									cabinCHDINFFareList.getJSONObject(k).remove("ei");
									cabinCHDINFFareList.getJSONObject(k).remove("priceValueComb");
									cabinCHDINFFareList.getJSONObject(k).remove("cabinLabelBrand");
									cabinCHDINFFareList.getJSONObject(k).remove("combineRuleInfo");
									cabinCHDINFFareList.getJSONObject(k).remove("flightNo");
									cabinCHDINFFareList.getJSONObject(k).remove("checkBaggeageRemark");
									cabinCHDINFFareList.getJSONObject(k).remove("cabinComb");
									cabinCHDINFFareList.getJSONObject(k).remove("fares");
									cabinCHDINFFareList.getJSONObject(k).remove("handBaggeageRemark");
									cabinCHDINFFareList.getJSONObject(k).remove("combineId");
									cabinCHDINFFareList.getJSONObject(k).remove("handBaggeage");
									cabinCHDINFFareList.getJSONObject(k).remove("weightLimit");
									cabinCHDINFFareList.getJSONObject(k).remove("specialRemark");
									cabinCHDINFFareList.getJSONObject(k).remove("comment");
									cabinCHDINFFareList.getJSONObject(k).remove("changedComment");
									cabinCHDINFFareList.getJSONObject(k).put("cnTaxDiff",cabinFareList.getIntValue("cnTaxDiff"));
									cabinCHDINFFareList.getJSONObject(k).put("cntax",cabinFareList.getIntValue("cntax"));
									cabinCHDINFFareList.getJSONObject(k).put("discountPriceValue",cabinFareList.getIntValue("discountPriceValue"));
									cabinCHDINFFareList.getJSONObject(k).put("priceValue",cabinFareList.getIntValue("priceValue"));
									cabinCHDINFFareList.getJSONObject(k).put("rsp",cabinFareList.getIntValue("rsp"));
									cabinCHDINFFareList.getJSONObject(k).put("taxDiff",cabinFareList.getIntValue("taxDiff"));
									cabinCHDINFFareList.getJSONObject(k).put("cnTaxDiff",cabinFareList.getIntValue("cnTaxDiff"));
									cabinCHDINFFareList.getJSONObject(k).put("ticketPriceDiff",cabinFareList.getIntValue("ticketPriceDiff"));
									cabinCHDINFFareList.getJSONObject(k).put("totalDiff",cabinFareList.getIntValue("totalDiff"));
									cabinCHDINFFareList.getJSONObject(k).put("yprice",cabinFareList.getIntValue("yprice"));
									cabinCHDINFFareList.getJSONObject(k).put("yqTaxDiff",cabinFareList.getIntValue("yqTaxDiff"));
									cabinCHDINFFareList.getJSONObject(k).put("changeServiceCharge",0);
									if (cabinCHDINFFareList.getJSONObject(k).containsKey("changeRuleList")) {
										com.alibaba.fastjson.JSONArray chdchangeRuleList = cabinCHDINFFareList.getJSONObject(k).getJSONArray("changeRuleList");
										for (int n = 0 ; n < chdchangeRuleList.size(); n++) {
											com.alibaba.fastjson.JSONObject changeRule = chdchangeRuleList.getJSONObject(n);
											changeRule.put("changeFee",changeRule.getIntValue("changeFee"));
										}
									}
									if (cabinCHDINFFareList.getJSONObject(k).containsKey("changeRules")) {
										com.alibaba.fastjson.JSONArray chdchangeRules = cabinCHDINFFareList.getJSONObject(k).getJSONArray("changeRules");
										for (int n = 0 ; n < chdchangeRules.size(); n++) {
											com.alibaba.fastjson.JSONObject changeRule = chdchangeRules.getJSONObject(n);
											changeRule.put("fee",changeRule.getIntValue("fee"));
										}
									}
									if (cabinCHDINFFareList.getJSONObject(k).containsKey("refundRuleList")) {
										com.alibaba.fastjson.JSONArray chdrefundRuleList = cabinCHDINFFareList.getJSONObject(k).getJSONArray("refundRuleList");
										for (int n = 0 ; n < chdrefundRuleList.size(); n++) {
											com.alibaba.fastjson.JSONObject refundRule = chdrefundRuleList.getJSONObject(n);
											refundRule.put("changeFee",refundRule.getIntValue("changeFee"));
										}
									}

									if (cabinCHDINFFareList.getJSONObject(k).containsKey("refundedRules")) {
										com.alibaba.fastjson.JSONArray chdrefundedRules = cabinCHDINFFareList.getJSONObject(k).getJSONArray("refundedRules");
										for (int n = 0; n < chdrefundedRules.size();n++) {
											com.alibaba.fastjson.JSONObject refundedRule = chdrefundedRules.getJSONObject(n);
											refundedRule.put("fee",refundedRule.getIntValue("fee"));
											refundedRule.put("usedTicketPrice",refundedRule.getIntValue("usedTicketPrice"));
										}
									}
								}
								flightInfoList.put("minPrice",flightInfoList.getIntValue("minPrice"));
								flightInfoList.put("minPriceAndTax",flightInfoList.getIntValue("minPriceAndTax"));
								flightInfoList.put("totalTax",flightInfoList.getIntValue("totalTax"));
								flightInfoList.put("minDiffPrice",flightInfoList.getIntValue("minDiffPrice"));
								flightInfoList.remove("notVoluntaryChange");
								flightInfoList.remove("minPriceAndTax");
								flightInfoList.remove("deptCountryNo");
								flightInfoList.remove("travelNo");
								flightInfoList.remove("fareType");
								flightInfoList.remove("transferDuration");
								flightInfoList.remove("flightSpanDay");
								flightInfoList.remove("stopDuration");
								flightInfoList.remove("mealCodeNotNull");
								flightInfoList.remove("saleInfo");
								flightInfoList.remove("stopAirport");
								flightInfoList.remove("stopArrTime");
								flightInfoList.remove("stopDepTime");
								flightInfoList.remove("transferInfo");
								res = flightInfoList.toJSONString();
								break;
							} else {
								res = "error:官网变价,预付" + sys_price + "实付:" + priceValue;
							}
						}
					}
					if (StringUtils.isNotEmpty(res)) {
						break;
					}
				}else{
					res = "error:查无此航班";
				}
			}
        } catch (Exception e) {
            logger.error( "error" , e );
        }
        return res;
    }


   private Map<String,String> login(RequestConfig defaultRequestConfig , CloseableHttpClient httpclient , String orderMsg ) {
	   CloseableHttpResponse response = null;
	   HttpGet get = null;
	   HttpPost post = null;
	   Map<String,String> resMap = new HashMap<>();
	   com.alibaba.fastjson.JSONObject orderjson = JSON.parseObject(orderMsg);
	   String order_Id = orderjson.getString( "id" );
	   String mainusername = orderjson.getString( "mainusername" );
	   String httpKey = mainusername + order_Id;
	   InputStream is = null;
	   try {
		   String username = "";
		   String password = "";
		   String account = orderjson.getString("account");
		   if (account.contains("_") && account.split("_").length == 2) {
			   username = account.split("_")[0];
			   password = account.split("_")[1];
		   }
		   String[] dlyAccountInfo = dlyAccount.split( ":" );
		   String proxyUser = dlyAccountInfo[0];
		   String proxyPass = dlyAccountInfo[1];
		   get = new HttpGet("https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F");
		   get.setConfig(defaultRequestConfig);
		   get.setHeader("Host","user.qunar.com");
		   get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
		   get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   get.setHeader( "Proxy-Connection" , "keep-alive" );
		   response = // HttpUtils.clientPost(null, get,httpclient,null,httpKey);
				   httpclient.execute(get);
		   String back = EntityUtils.toString(response.getEntity(),"utf-8");
		  //  System.out.println("登陆初始化返回:" + back);
		   String codeUrls[] = back.split("https\\:\\/\\/user\\.qunar\\.com\\/captcha\\/api\\/image\\?k\\=\\{en7mni\\(z&p\\=ucenter_login&c\\=");
		   String codeUrl = "";
		   if(codeUrls.length > 1){
			   codeUrl = "https://user.qunar.com/captcha/api/image?k=%7Ben7mni(z&p=ucenter_login&c="+(codeUrls[1].substring(0,32));
		   }
		   Header[] headers = response.getAllHeaders();
		   String cookie = "";
		   for (int i = 0; i < headers.length; i++) {
			   if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
				   cookie = headers[i].getValue();
				   break;
			   }
		   }
		   get = new HttpGet(codeUrl);
		   get.setConfig(defaultRequestConfig);
		   get.setHeader("Host","user.qunar.com");
		   get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
		   get.setHeader("Cookie",cookie);
		   get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   get.setHeader( "Proxy-Connection" , "keep-alive" );
		   String session = UUID.randomUUID().toString().replaceAll("-", "");
		   String fileUri = "C://testImg//" + session + ".jpg";
		   response = httpclient.execute(get);
		   is = response.getEntity().getContent();
		   OutputStream os = new FileOutputStream(fileUri);
		   IOUtils.copy(is, os);
		   os.close();
		   String otheraccount = orderjson.getString("otheraccount");
		   String feifeiusername = otheraccount.split("_")[0];
		   String feifeipassword = otheraccount.split("_")[1];
		   String val = Api.getValidCode(feifeiusername , feifeipassword , "20400" , fileUri);
		   int f = 0;
		   while (StringUtils.isEmpty(val) && f < 5) {
			   val = Api.getValidCode(username , password , "40200" , fileUri);
			   f++;
		   }
		   System.out.println("打码结果:" + val);
		   File file = new File(fileUri);
		   if (file.exists()) {
			   file.delete();
		   }
		   headers = response.getAllHeaders();
		   for (int i = 0; i < headers.length; i++) {
			   if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
				   cookie = headers[i].getValue();
				   break;
			   }
		   }

		   /*get = new HttpGet("https://user.qunar.com/passport/addICK.jsp?ssl");
		   get.setConfig(defaultRequestConfig);
		   get.setHeader("Referer","https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F%3Fex_track%3Dauto_4e0d874a");
		   get.setHeader("Host","user.qunar.com");
		   get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
		   get.setHeader("Cookie",cookie);
		   get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   get.setHeader( "Proxy-Connection" , "keep-alive" );
		   response = httpclient.execute(get);
		   headers = response.getAllHeaders();
		   for (int i = 0; i < headers.length; i++) {
			   if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
				   cookie = cookie + headers[i].getValue();
			   }
		   }
		   back = EntityUtils.toString(response.getEntity(),"utf-8");
		   System.out.println(httpKey + "addICK结果:" + back);*/
		   get = new HttpGet("https://rmcsdf.qunar.com/js/df.js?org_id=ucenter.login&js_type=0");
		   get.setHeader("Host","rmcsdf.qunar.com");
		   get.setHeader("Referer","https://user.qunar.com/");
		   get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
		   get.setHeader("Cookie",cookie);
		   get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   get.setHeader( "Proxy-Connection" , "keep-alive" );
		   response = httpclient.execute(get);
		   headers = response.getAllHeaders();
		   for (int i = 0; i < headers.length; i++) {
			   if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
				   cookie = headers[i].getValue();
				   break;
			   }
		   }
		   back = EntityUtils.toString(response.getEntity(),"utf-8");
		   String s [] = back.split("https\\:\\/\\/rmcsdf\\.qunar.com\\/js\\/device\\.js\\?orgId=ucenter.login&sessionId=");

		   String qn271 = "";
		   if(s.length > 1){
			   qn271 = s[1].substring(0,"f4d84875-23c5-409c-94db-a15ec5351dd7".length());
		   }
		   get = new HttpGet("https://rmcsdf.qunar.com/js/device.js?orgId=ucenter.login&sessionId="+qn271+"&auto=false");
		   get.setConfig(defaultRequestConfig);
		   get.setHeader("Host","rmcsdf.qunar.com");
		   get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
		   get.setHeader("Cookie",cookie);
		   get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   get.setHeader( "Proxy-Connection" , "keep-alive" );
		   response = httpclient.execute(get);
		   headers = response.getAllHeaders();
		   for (int i = 0; i < headers.length; i++) {
			   if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
				   cookie = headers[i].getValue();
				   break;
			   }
		   }

		   get = new HttpGet("https://rmcsdf.qunar.com/api/device/challenge.json?callback=callback_"+System.currentTimeMillis()+"&sessionId="+qn271+"&domain=qunar.com&orgId=ucenter.login");
		   get.setConfig(defaultRequestConfig);
		   get.setHeader("Host","rmcsdf.qunar.com");
		   get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
		   get.setHeader("Cookie",cookie);
		   get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   get.setHeader( "Proxy-Connection" , "keep-alive" );
		   response = httpclient.execute(get);
		   headers = response.getAllHeaders();
		   for (int i = 0; i < headers.length; i++) {
			   if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
				   cookie = headers[i].getValue();
				   break;
			   }
		   }
		   back = EntityUtils.toString(response.getEntity(),"utf-8");
		   System.out.println(httpKey + "challenge结果:" + back);
		   post = new HttpPost("https://user.qunar.com/passport/loginx.jsp");
		   post.setConfig(defaultRequestConfig);
		   post.setHeader("Host","user.qunar.com");
		   post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
		   post.setHeader("Cookie",cookie);
		   post.setHeader("Accept","application/json, text/javascript, */*; q=0.01");
		   post.setHeader("X-Requested-With","XMLHttpRequest");
		   post.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		   post.setHeader("Origin","https://user.qunar.com");
		   post.setHeader("Referer","https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F%3Fex_track%3Dauto_4e0d874a");
		   post.setHeader("Accept-Encoding","gzip, deflate, br");
		   List<NameValuePair> nameValuePairs = new ArrayList<>();
		   nameValuePairs.add(new BasicNameValuePair("loginType","0"));
		   nameValuePairs.add(new BasicNameValuePair("ret","https://www.qunar.com"));
		   nameValuePairs.add(new BasicNameValuePair("username",username));
		   nameValuePairs.add(new BasicNameValuePair("password",password));
		   nameValuePairs.add(new BasicNameValuePair("remember","1"));
		   nameValuePairs.add(new BasicNameValuePair("vcode",val));
		   post.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
		   post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   post.setHeader( "Proxy-Connection" , "keep-alive" );
		   response = httpclient.execute(post);
		   back = EntityUtils.toString(response.getEntity(),"utf-8");
		   System.out.println(httpKey + "登录结果:" + back);
		   headers = response.getAllHeaders();
		   for (int i = 0; i < headers.length; i++) {
			   if ("set-cookie".equalsIgnoreCase(headers[i].getName())) {
				   cookie = cookie  +";" + headers[i].getValue();
			   }
		   }
		   System.out.println("登录获取到的cookie:" + cookie);
		   if (StringUtils.isNotEmpty(back)) {
				com.alibaba.fastjson.JSONObject resObj = JSON.parseObject(back);
				boolean ret = resObj.getBoolean("ret");
				if (ret) {
					resMap.put("cookie",cookie);
				} else {
					String errmsg = resObj.getString("errmsg");
					resMap.put("error",errmsg);
				}
		   }
	   } catch (Exception e) {
		   logger.error( "HO登陆error:" , e );
		   e.printStackTrace();
	   } finally {
		   try {
			   if (response != null) {
				   response.close();
			   }
			   if (get != null) {
				   get.releaseConnection();
			   }
			   if (post != null) {
				   post.releaseConnection();
			   }
		   } catch (IOException e) {
		   }
	   }
	   return resMap;
   }

	@Test
	public void loginTest() {
		CloseableHttpResponse response = null;
		HttpGet get = null;
		HttpPost post = null;
		InputStream is = null;
		Map<String,String> resMap = new HashMap<>();
		try {
			CloseableHttpClient httpclient = HttpClientBuilder.create().build();
			get = new HttpGet("https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F");
			get.setHeader("Host","user.qunar.com");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
			response = // HttpUtils.clientPost(null, get,httpclient,null,httpKey);
					httpclient.execute(get);
			String back = EntityUtils.toString(response.getEntity(),"utf-8");
			System.out.println("登陆初始化返回:" + back);
			String codeUrls[] = back.split("https\\:\\/\\/user\\.qunar\\.com\\/captcha\\/api\\/image\\?k\\=\\{en7mni\\(z&p\\=ucenter_login&c\\=");
			String codeUrl = "";
			if(codeUrls.length > 1){
				codeUrl = "https://user.qunar.com/captcha/api/image?k=%7Ben7mni(z&p=ucenter_login&c="+(codeUrls[1].substring(0,32));
			}
			Header[] headers = response.getAllHeaders();
			String cookie = "";
			for (int i = 0; i < headers.length; i++) {
				if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
					cookie = headers[i].getValue();
					break;
				}
			}
			get = new HttpGet(codeUrl);
			get.setHeader("Host","user.qunar.com");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
			get.setHeader("Cookie",cookie);
			String session = UUID.randomUUID().toString().replaceAll("-", "");
			String fileUri = "C://testImg//" + session + ".jpg";
			response = httpclient.execute(get);
			is = response.getEntity().getContent();
			OutputStream os = new FileOutputStream(fileUri);
			IOUtils.copy(is, os);
			os.close();
			Scanner input = new Scanner(System.in);
			String val = null;       // 记录输入度的字符串

			System.out.print("请输入：");
			val = input.next();       // 等待输入值
			System.out.println("您输入的是："+val);
			headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
					cookie = headers[i].getValue();
					break;
				}
			}

			get = new HttpGet("https://user.qunar.com/passport/addICK.jsp?ssl");
			get.setHeader("Referer","https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F%3Fex_track%3Dauto_4e0d874a");
			get.setHeader("Host","user.qunar.com");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
			get.setHeader("Cookie",cookie);
			response = httpclient.execute(get);
			headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
					cookie = cookie + headers[i].getValue();
				}
			}
			back = EntityUtils.toString(response.getEntity(),"utf-8");
			System.out.println("addICK结果:" + back);
			get = new HttpGet("https://rmcsdf.qunar.com/js/df.js?org_id=ucenter.login&js_type=0");
			get.setHeader("Host","rmcsdf.qunar.com");
			get.setHeader("Referer","https://user.qunar.com/");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
			get.setHeader("Cookie",cookie);
			response = httpclient.execute(get);
			headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
					cookie = headers[i].getValue();
					break;
				}
			}
			back = EntityUtils.toString(response.getEntity(),"utf-8");
			String s [] = back.split("https\\:\\/\\/rmcsdf\\.qunar.com\\/js\\/device\\.js\\?orgId=ucenter.login&sessionId=");

			String qn271 = "";
			if(s.length > 1){
				qn271 = s[1].substring(0,"f4d84875-23c5-409c-94db-a15ec5351dd7".length());
			}
			get = new HttpGet("https://rmcsdf.qunar.com/js/device.js?orgId=ucenter.login&sessionId="+qn271+"&auto=false");
			get.setHeader("Host","rmcsdf.qunar.com");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
			get.setHeader("Cookie",cookie);
			response = httpclient.execute(get);
			headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
					cookie = headers[i].getValue();
					break;
				}
			}

			get = new HttpGet("https://rmcsdf.qunar.com/api/device/challenge.json?callback=callback_"+System.currentTimeMillis()+"&sessionId="+qn271+"&domain=qunar.com&orgId=ucenter.login");
			get.setHeader("Host","rmcsdf.qunar.com");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
			get.setHeader("Cookie",cookie);
			response = httpclient.execute(get);
			headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
					cookie = headers[i].getValue();
					break;
				}
			}
			back = EntityUtils.toString(response.getEntity(),"utf-8");
			System.out.println("challenge结果:" + back);
			post = new HttpPost("https://user.qunar.com/passport/loginx.jsp");
			post.setHeader("Host","user.qunar.com");
			post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
			post.setHeader("Cookie",cookie);
			post.setHeader("Accept","application/json, text/javascript, */*; q=0.01");
			post.setHeader("X-Requested-With","XMLHttpRequest");
			post.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			post.setHeader("Origin","https://user.qunar.com");
			post.setHeader("Referer","https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F%3Fex_track%3Dauto_4e0d874a");
			post.setHeader("Accept-Encoding","gzip, deflate, br");
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(new BasicNameValuePair("loginType","0"));
			nameValuePairs.add(new BasicNameValuePair("ret","https://www.qunar.com"));
			nameValuePairs.add(new BasicNameValuePair("username","13037284859"));
			nameValuePairs.add(new BasicNameValuePair("password","feeye123"));
			nameValuePairs.add(new BasicNameValuePair("remember","1"));
			nameValuePairs.add(new BasicNameValuePair("vcode",val));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			response = httpclient.execute(post);
			back = EntityUtils.toString(response.getEntity(),"utf-8");
			System.out.println("登录结果:" + back);
			headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
					cookie = headers[i].getValue();
				}
			}
			System.out.println("登录获取到的cookie:" + cookie);
			if (StringUtils.isNotEmpty(back) && back.contains("redirect")) {

			}
		} catch (Exception e) {
			logger.error( "HO登陆error:" , e );
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				if (get != null) {
					get.releaseConnection();
				}
				if (post != null) {
					post.releaseConnection();
				}
			} catch (IOException e) {
			}
		}
	}

    /**
     * 推送创单情况 String result = request.getParameter("result"); //是否创单成功 String
     * message = request.getParameter("message"); //失败消息 String price =
     * request.getParameter("price"); //采购总金额 String childrenUser =
     * request.getParameter("childrenUser");//子帐号 String newOrderId =
     * request.getParameter("newOrderId"); //创建订单成功后的官网订单号 String orderId =
     * request.getParameter("orderId"); //原订单主键ID String isPassuccess =
     * request.getParameter("isPassuccess"); //是否支付成功 String isPassenge =
     * request.getParameter("isPassenge"); //是否票号回填 String passengeMessage =
     * request.getParameterValues("passengeMessage"); // 获取票号回填到系统
     * 格式为:姓名A##生份证##票号@_@姓名B##生份证##票号 String payTransactionid =
     * request.getParameter("payTransactionid"); //获取票号回填的交易号 SC时代表联系电话 String
     * payStatus = request.getParameter("payStatus"); //获取支付方式 String isSuccess
     * = request.getParameter("isSuccess"); //是否完结 String isautoB2C =
     * request.getParameter("isautoB2C"); //是否自动出票 String ifUsedCoupon =
     * request.getParameter("ifUsedCoupon"); //是否使用红包
     */
	/*
	 * payStatus(易宝信用卡、易宝会员)
	 */
    public static String sendCreateOrderInfo(String result , String message , String price , String childrenUser ,
                                             String newOrderId , String orderId , String isPassuccess , String isPassenge , String passengeMessage ,
                                             String payStatus , String payTransactionid , String ifUsedCoupon , String isSuccess , String billNo ,
                                             int requestType,String mainUser) {
        try {
            if (requestType == 1) {
                return null;
            }
            System.out.println( orderId + ":" + result + message );
			String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrl");
			String newUser = "";
			try {
				newUser = PropertiesUtils.getPropertiesValue( "config" , "newUser" );
				/*if (!StringUtil.isEmpty(newUser) && newUser.contains(mainUser)) {
					orderUrl = PropertiesUtils.getPropertiesValue( "config" , "newOrderUrl");
				}*/
			} catch (Exception e2) {
				logger.error(orderId + "推送error:",e2);
			}
            StringBuffer buffer = new StringBuffer();
            buffer.append( "<feeye-official>" );
            buffer.append( "<official>" + "HO" + "</official> " );
            buffer.append( "<url>" + orderUrl + "</url> " );
            buffer.append( "<type>0</type> " );
            buffer.append( "<method>post</method>" );
            buffer.append( "<max>20</max> " );
            buffer.append( "<encod>utf-8</encod> " );
            buffer.append( "<params>" );
            buffer.append( "<param name='result'>" + result + "</param>" );
            buffer.append( "<param name='message'>" + message + "</param>" );
            buffer.append( "<param name='price'>" + price + "</param>" );
            buffer.append( "<param name='childrenUser'>" + childrenUser + "</param>" );
            buffer.append( "<param name='newOrderId'>" + newOrderId + "</param>" );
            buffer.append( "<param name='orderId'>" + orderId + "</param>" );
            buffer.append( "<param name='isPassuccess'>" + isPassuccess + "</param>" );
            buffer.append( "<param name='isPassenge'>" + isPassenge + "</param>" );
            buffer.append( "<param name='passengeMessageOther'>" + passengeMessage + "</param>" );
            buffer.append( "<param name='payStatus'>" + payStatus + "</param>" );
            buffer.append( "<param name='payTransactionid'>" + payTransactionid + "</param>" );
            buffer.append( "<param name='ifUsedCoupon'>" + ifUsedCoupon + "</param>" );
            buffer.append( "<param name='isSuccess'>" + isSuccess + "</param>" );
            buffer.append( "<param name='billNo'>" + billNo + "</param>" );
            buffer.append( "<param name='dicountMoney'>" + 0 + "</param>" );

            buffer.append( "</params>" );
            buffer.append( "</feeye-official>" );

            System.out.println( buffer.toString() );
            String content = OfficialMain.setRequestParams( buffer.toString() );
            if (content != null) {
                String rs1[] = content.split( "#@_@#" );
                if (rs1.length == 2) {
                    content = rs1[1];
                    return content;
                }
                if (rs1.length == 3) {
                    System.out.println( rs1[2] );
                    return rs1[2];
                }
            }

        } catch (Exception e) {
            logger.error( "error" , e );
        }
        return null;
    }

    /**
     * 推送创单情况 String childrenUser = request.getParameter("childrenUser");//子帐号
     * String orderId = request.getParameter("orderId"); //原订单主键ID String
     * payStatus = request.getParameter("payStatus"); //获取支付方式
     */
    public static String sendOrderStatus(String childrenUser , String orderId , String status , int requestType) {
        try {
            if (requestType == 1) {
                return null;
            }
            System.out.println( orderId + status );
            String orderUrl = PropertiesUtils.getPropertiesValue( "config" , "orderUrlStatus" );

            StringBuffer buffer = new StringBuffer();
            buffer.append( "<feeye-official>" );
            buffer.append( "<official>MU</official> " );
            buffer.append( "<url>" + orderUrl + "</url> " );
            buffer.append( "<type>0</type> " );
            buffer.append( "<method>post</method>" );
            buffer.append( "<max>20</max> " );
            buffer.append( "<encod>utf-8</encod> " );
            buffer.append( "<params>" );
            buffer.append( "<param name='childrenUser'>" + childrenUser + "</param>" );
            buffer.append( "<param name='orderId'>" + orderId + "</param>" );
            buffer.append( "<param name='orderStatus'>" + status + "</param>" );

            buffer.append( "</params>" );
            buffer.append( "</feeye-official>" );

            String content = OfficialMain.setRequestParams( buffer.toString() );
            if (content != null) {
                String rs1[] = content.split( "#@_@#" );
                if (rs1.length == 2) {
                    content = rs1[1];
                    return content;
                }
                if (rs1.length == 3) {
                    System.out.println( rs1[2] );
                    return rs1[2];
                }
            }
        } catch (Exception e) {
            logger.error( "推送\"" + status + "\"情况异常" );
        }
        return null;
    }

    /**
     * 推送会员账号 String phone = request.getParameter("phone");//注册手机号 String name =
     * request.getParameter("name"); //注册的乘客名 accountInfo =
     * request.getParameter("accountInfo"); //注册的账号密码 格式：身份证_密码
     *
     * @param rejStatus
     * @param rejCase
     */
    public static String sendAccountInfo(String phone , String name , String accountInfo , String orderId ,
                                         String childrenUser , String rejStatus , String rejCase , String billNo) {
        try {
            System.out.println( orderId + accountInfo );
            String orderUrl = PropertiesUtils.getPropertiesValue( "config" , "sendAccountInfo" );

            StringBuffer buffer = new StringBuffer();
            buffer.append( "<feeye-official>" );
            buffer.append( "<official>MU</official> " );
            buffer.append( "<url>" + orderUrl + "</url> " );
            buffer.append( "<type>0</type> " );
            buffer.append( "<method>post</method>" );
            buffer.append( "<max>20</max> " );
            buffer.append( "<encod>utf-8</encod> " );
            buffer.append( "<params>" );
            buffer.append( "<param name='phone'>" + phone + "</param>" );
            buffer.append( "<param name='id'>" + orderId + "</param>" );
            buffer.append( "<param name='name'>" + name + "</param>" );
            buffer.append( "<param name='accountInfo'>" + accountInfo + "</param>" );
            buffer.append( "<param name='childrenUser'>" + childrenUser + "</param>" );
            buffer.append( "<param name='rejStatus'>" + rejStatus + "</param>" );
            buffer.append( "<param name='rejCase'>" + rejCase + "</param>" );
            buffer.append( "<param name='billNo'>" + billNo + "</param>" );
            buffer.append( "</params>" );
            buffer.append( "</feeye-official>" );

            String content = OfficialMain.setRequestParams( buffer.toString() );
            if (content != null) {
                String rs1[] = content.split( "#@_@#" );
                if (rs1.length == 2) {
                    content = rs1[1];
                    return content;
                }
                if (rs1.length == 3) {
                    System.out.println( rs1[2] );
                    return rs1[2];
                }
            }
        } catch (Exception e) {
            logger.error( "推送\"" + accountInfo + "\"情况异常" );
        }
        return null;
    }

    private String cancel(String url , String id , String childrenUser) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        InputStream re = null;
        HttpGet get = null;
        HttpPost post = null;
        String result = null;
        try {
            BasicCookieStore cookieStore = new BasicCookieStore();
            Integer timeout = Integer.parseInt( "60000" );
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout( timeout ).setConnectTimeout( timeout )
                    .build();
            client = HttpClients.custom().setDefaultCookieStore( cookieStore ).build();
            List<BasicNameValuePair> nameValueParis = new ArrayList<BasicNameValuePair>();
            nameValueParis.add( new BasicNameValuePair( "orderId" , id ) );
            nameValueParis.add( new BasicNameValuePair( "codetype" , "order" ) );
            nameValueParis.add( new BasicNameValuePair( "childrenUser" , childrenUser ) );
            post = new HttpPost( url );
            post.setEntity( new UrlEncodedFormEntity( nameValueParis , "utf-8" ) );
            post.setConfig( requestConfig );
            response = client.execute( post );
            result = EntityUtils.toString( response.getEntity() , "utf-8" );
            JSONObject jo = new JSONObject( result );
            result = jo.getString( "msg" );
        } catch (Exception e) {
            logger.error( "error" , e );
        } finally {
            try {
                if (re != null) {
                    re.close();
                }
                if (response != null) {
                    response.close();
                }
                if (get != null) {
                    get.releaseConnection();
                }
                if (post != null) {
                    post.releaseConnection();
                }
                if (client != null) {
                    client.close();
                }
            } catch (Exception e) {
                logger.error( "error" , e );
            }
        }
        return result;
    }
}
