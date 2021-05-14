package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.feeye.util.*;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
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
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class HOAPPCreateOrderService {
    private static final Logger logger = Logger.getLogger( HOAPPCreateOrderService.class );
    private static final int timeout = 50000;
    String dlyAccount = "feeyeapp:feeye789";
			// PropertiesUtils.getPropertiesValue( "config" , "dlyAccount" );
    String kdlAccount = PropertiesUtils.getPropertiesValue( "config" , "kdlAccount" );
    String userAgent = "";
    String loginCookie = "";
    public static BlockingQueue<String> ipQueue= new LinkedBlockingQueue<String>();
    // Map<String, String> cookieMap = new HashMap<>();
	private static Map<String, Map<String,String>> cookieMap = new HashMap<>();
    Map<String, String> errorCodeMap = new HashMap<>();
	static {
		String ipPorts = "";
		try {
			ipPorts = PropertiesUtils.getPropertiesValue("config", "ipPort");
		} catch (Exception e) {
		}
		if (StringUtils.isNotEmpty(ipPorts)) {
			String[] ips = ipPorts.split(",");
			for (String ip : ips) {
				try {
					ipQueue.put(ip);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getIpPort() {
		synchronized (ipQueue) {
			String ip = ipQueue.poll();
			if (StringUtils.isNotEmpty(ip)) {
				try {
					ipQueue.put(ip);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return ip;
		}
	}
    public static void main(String[] args) throws Exception {
		String orderJSon = "{\n" +
				"    \"account\": \"18625963362_hb2020\",\n" +
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
				"            \"cabin\": \"Q\",\n" +
				"            \"departure\": \"NKG\",\n" +
				"            \"departureDate\": \"2021-03-25\",\n" +
				"            \"departureTime\": \"11:40:00\",\n" +
				"            \"fType\": \"MEMBER_FARE\",\n" +
				"            \"flightNo\": \"HO1719\",\n" +
				"            \"price\": \"630\"\n" +
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
				"            \"birthday\": \"1997-08-01\",\n" +
				"            \"id\": \"75696011\",\n" +
				"            \"idcard\": \"622425199708010327\",\n" +
				"            \"passengerCardType\": \"身份证\",\n" +
				"            \"passengerName\": \"张玮\",\n" +
				"            \"passengerSex\": \"女\",\n" +
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
		new HOAPPCreateOrderService().StartCreateOrder(orderJSon,0,0,0);
    }

    long nowDateTime = new Date().getTime(); // 获取当前时间，后面步骤要根据该时间点做超时处理
    public String StartCreateOrder(String orderJson , int requestType , int retryCount , int exceptionCount) {
        if (StringUtils.isEmpty( orderJson )) {
            return "ERROR:数据不完整";
        }
        logger.info( "获取到的数据HO:" + orderJson );
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
		String dlyAccount = // "guoji:guoji20160309";
				PropertiesUtils.getPropertiesValue("config" , "dlyAccount");
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
		HttpHost target = new HttpHost("hoapp.juneyaoair.com",443,"https");
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
            logger.info(order_id+"取到的IP:"+ip_port);
            String account = json.getString( "account" );
            childrenUser = json.getString( "username" );
            mainUser = json.getString( "mainusername" );
            JSONArray passengers = json.getJSONArray( "passengers" );
            boolean defaultAccount = false; // 是否因为注册失败，使用默认密码登录
            String cancelKey = mainUser + order_id;
            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
              Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
              	logger.info(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        logger.info( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/

            // //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "查询航班" );

            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
              Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
              logger.info(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        logger.info( "已取消出票" );
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
			/*if (loginMap==null || loginMap.size() == 0) {
				String procookie = PropertiesUtil.getProperty("HOcookie.properties", logingKey);
				logger.info(order_id + "帐号:" + logingKey + "HO获取到的登录信息:" + procookie);
				if (StringUtils.isNotEmpty(procookie) && !"null".equals(procookie)) {
					loginMap = JSON.parseObject(procookie,Map.class);
				}
			}*/
            if (loginMap==null || loginMap.size() == 0) {
				for (int i = 0; i < 3; i++) {
				/*loginMap = new HashMap<>();
				String loginMsg = "{\n" +
						"  \"certNumber\": \"362428199508132112\",\n" +
						"  \"cookie\": \"JSESSIONID=0B1536E39447DF31F1C565ECB801C518.tomcat1\",\n" +
						"  \"loginKeyInfo\": \"90505960B52A579FC35C3092728B7BCB\",\n" +
						"  \"id\": \"14140912\",\n" +
						"  \"memberID\": \"3109795220\",\n" +
						"  \"token\": \"2c351d0e11c34b0919a8600c64441c56\"\n" +
						"}\n";*/
				/*com.alibaba.fastjson.JSONObject obj = JSON.parseObject(loginMsg);
				com.alibaba.fastjson.JSONObject objData = obj.getJSONObject("objData");
				com.alibaba.fastjson.JSONObject responseObj = objData.getJSONObject("memberLoginResponse");
				String id = responseObj.getString("id");
				String memberID = responseObj.getString("memberID");
				String certNumber = responseObj.getString("certNumber");
				String loginKeyInfo = responseObj.getString("loginKeyInfo");
				String token = responseObj.getString("token");
				loginMap.put("id",id);
				loginMap.put("memberID",memberID);
				loginMap.put("certNumber",certNumber);
				loginMap.put("loginKeyInfo",loginKeyInfo);
				loginMap.put("token",token);
				loginMap.put("cookie","JSESSIONID=3B5C46E9194A86C8593AE6B7F07D918B");*/
					// loginMap = JSON.parseObject(loginMsg,Map.class);
					// loginMap.clear();
					loginMap = login(defaultRequestConfig , httpclient , orderJson);
					logger.info(order_id + "登陆返回:" + JSON.toJSONString(loginMap));
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
					if (loginMap!=null && StringUtils.isNotEmpty(loginMap.get("memberID"))) {
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
            if (loginMap!=null && StringUtils.isEmpty(loginMap.get("memberID"))) {
                sendCreateOrderInfo( "error" , "登录异常" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                        "true" , billNo , requestType ,mainUser);
                return "ERROR:登录异常";
            }
            /*if (DateUtil.IsRunningTimeOut( nowDateTime , 5 * 1000 * 60 )) {
                //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "创单超时，停止创单" );
                sendCreateOrderInfo( "error" , "创单超时，停止创单" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" ,
                        "false" , "true" , billNo , requestType,mainUser);
                logger.info( order_id + "创单超时，停止创单" );
                return "创单超时，停止创单";
            }*/


            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
              Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
              logger.info(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        logger.info( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/
            /*context.setTargetHost(target);*/
            cookieMap.put(logingKey,loginMap);
			/*String procookie = PropertiesUtil.getProperty("HOcookie.properties", logingKey);
			logger.info(order_id + "帐号:" + logingKey + "HO获取到的登录信息:" + procookie);
			if (StringUtils.isEmpty(procookie) || "null".equals(procookie)) {
				PropertiesUtil.setProperty("HOcookie.properties", logingKey, JSON.toJSONString(loginMap));
			}*/
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "选择航班" );
            String allCheckToken = "";
            for (int i = 0; i < 5; i++) {
                allCheckToken = selectFlights( httpclient , defaultRequestConfig,orderJson);
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
				logger.info(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        logger.info( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "添加联系人" );
            Map<String,String> passIDMap = new HashMap<>();
            for (int i = 0; i < 5; i++) {
                try {
					passIDMap = addContactPax( httpclient , defaultRequestConfig , loginMap , orderJson);
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
                logger.info(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        logger.info( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "查询乘机人信息" );
            int index = 0;
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
            }
            /*if (CreateOrderService.cancelMap.containsKey(cancelKey)) {
                Date cancelTimeDate = CreateOrderService.cancelMap.get(cancelKey);
                Date startTimeDate = CreateOrderService.startTimeMap.get(cancelKey);
              	logger.info(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
                if (cancelTimeDate!= null && startTimeDate!=null) {
                    if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
                        //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
                        sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
                                "true" , billNo , requestType ,mainUser);
                        logger.info( "已取消出票" );
                        return "取消出票";
                    }
                }
            }*/
            /*if (DateUtil.IsRunningTimeOut( nowDateTime , 10 * 1000 * 60 )) {
                //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "创单超时，停止创单" );
                sendCreateOrderInfo( "error" , "创单超时，停止创单" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" ,
                        "false" , "true" , billNo , requestType ,mainUser);
                logger.info( order_id + "创单超时，停止创单" );
                return "创单超时，停止创单";
            }*/

            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "创建订单" );
			Map<String,String> bookingMap = new HashMap<>();
            for (int i = 0; i < 5; i++) {
            	bookingMap = booking( httpclient , defaultRequestConfig , orderJson , passMap , loginMap,allCheckToken);
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
			String orderNo = bookingMap.get("orderNo");
            sendCreateOrderInfo( "success" , "" , "" , childrenUser , account.split( "_" )[0] + "--" + orderNo , order_id , "" ,
                    "" , null , "" , "" , "false" , "true" , billNo , requestType ,mainUser);
            //PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "选择支付方式" );
			String payType = json.getString( "payType" ) == null ? "" : json.getString( "payType" );
            String bankId = "";
            if ("xyk".equals(payType)) {
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
					logger.info(cancelKey + "取消时间:" +cancelTimeDate.getTime() + ",创建时间:" + startTimeDate.getTime());
					if (cancelTimeDate!= null && startTimeDate!=null) {
						if(startTimeDate.getTime() < cancelTimeDate.getTime()) {
							//PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "已取消出票" );
							sendCreateOrderInfo( "error" , "已取消出票" , "" , childrenUser , "" , order_id , "" , "" , null , "" , "" , "false" ,
									"true" , billNo , requestType ,mainUser);
							logger.info( "已取消出票" );
							return "取消出票";
						}
					}
				}*/
				//PushOrderInfoUtil.sendOrderStatus( Constant.HO.toString() , mainUser , childrenUser , order_id , "开始付款" );
                bankId = payOrder(httpclient,defaultRequestConfig,orderJson,bookingMap,loginMap);
                if (StringUtils.isEmpty(bankId) || !bankId.contains("success")) {
					sendCreateOrderInfo( "error" , "支付失败,请到官网确认支付结果:" + bankId , "" , childrenUser ,
							account.split( "_" )[0] + "--" + orderNo , order_id , "" , "" , null , payType , "" , "false" , "true" ,
							billNo , requestType,mainUser);
					return "ERROR:支付失败，只支持信用卡支付";
				}
            } else {
				sendCreateOrderInfo( "error" , "支付失败，只支持信用卡支付" , "" , childrenUser ,
						account.split( "_" )[0] + "--" + orderNo , order_id , "" , "" , null , payType , "" , "false" , "true" ,
						billNo , requestType,mainUser);
				return "ERROR:支付失败，只支持信用卡支付";
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
                sendCreateOrderInfo( "success" , "" , bookingMap.get("payAmount") , childrenUser , orderNo , order_id , "true" , "" , null , payType , bankId.split("#")[1] , "false" , "true" ,
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

                    logger.info( order_id + result.toString() );
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
	@Test
	public void a8() {
    	String realyMsg = "{\n" +
				"    \"channelOrderNo\": \"N2021032517245232270\",\n" +
				"    \"orderNo\": \"ORD21032538509028\",\n" +
				"    \"orderType\": \"D\",\n" +
				"    \"payType\": \"D\",\n" +
				"    \"channelBuyDatetime\": \"20210325172506\",\n" +
				"    \"amount\": \"398.00\",\n" +
				"    \"useScore\": \"\",\n" +
				"    \"appsys\": \"MOBILE\",\n" +
				"    \"channelPriInfo\": \"\",\n" +
				"    \"payMethod\": \"CARDPAY\",\n" +
				"    \"ffpId\": 3393268,\n" +
				"    \"ffpCardNo\": \"2881799616\",\n" +
				"    \"loginKeyInfo\": \"C52F926ACA988133FB6BDAE81943DE57\",\n" +
				"    \"gatewayNo\": \"22411\",\n" +
				"    \"gatewayType\": \"4\",\n" +
				"    \"cardInfo\": \"4063661270845097||2406|147|0|410781198508214713|王辉|18568638588\",\n" +
				"    \"channelCode\": \"MOBILE\",\n" +
				"    \"platformInfo\": \"android\",\n" +
				"    \"clientVersion\": \"6.3.0\",\n" +
				"    \"versionCode\": 63000,\n" +
				"    \"token\": \"ed949821045bcc5c35fb20386e7e8d3f\"\n" +
				"}";
    	String create = "{\n" +
				"    \"platformInfo\": \"android\",\n" +
				"    \"orderType\": \"D\",\n" +
				"    \"amount\": \"398.00\",\n" +
				"    \"orderNo\": \"ORD21032538509028\",\n" +
				"    \"cardInfo\": \"4063661270845097||0624|147|0|410781198508214713|王辉|18568638588\",\n" +
				"    \"useScore\": \"\",\n" +
				"    \"gatewayNo\": \"22411\",\n" +
				"    \"ffpId\": 3393268,\n" +
				"    \"appsys\": \"MOBILE\",\n" +
				"    \"channelOrderNo\": \"N2021032517245232270\",\n" +
				"    \"clientVersion\": \"6.3.0\",\n" +
				"    \"versionCode\": 63000,\n" +
				"    \"token\": \"354d80fc3280e4ef8819baa3613c0d45\",\n" +
				"    \"channelBuyDatetime\": \"20210325172458\",\n" +
				"    \"payType\": \"D\",\n" +
				"    \"gatewayType\": \"4\",\n" +
				"    \"channelPriInfo\": \"\",\n" +
				"    \"payMethod\": \"CARDPAY\",\n" +
				"    \"ffpCardNo\": \"2881799616\",\n" +
				"    \"loginKeyInfo\": \"C52F926ACA988133FB6BDAE81943DE57\",\n" +
				"    \"channelCode\": \"MOBILE\"\n" +
				"}";
		com.alibaba.fastjson.JSONObject reallyObj = JSON.parseObject(realyMsg);
		com.alibaba.fastjson.JSONObject createObj = JSON.parseObject(create);
		System.out.println("真实数据:" + JSON.toJSONString(reallyObj, SerializerFeature.MapSortField));
		System.out.println("创造数据:" + JSON.toJSONString(createObj, SerializerFeature.MapSortField));
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
			logger.info(httpKey + "HO支付网关参数:" + payreqJson);
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
			logger.info(httpKey + "HO支付网关返回:" + payres);
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
			logger.info(httpKey + "HO支付参数:" + reqJson);
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
			logger.info(httpKey + "HO支付返回:" + res);
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
			logger.info(httpKey + "HO点击支付参数:" + payreqJson);
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
			logger.info(httpKey + "HOHO点击支付返回:" + payres);
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



    private Map<String,String> addContactPax(CloseableHttpClient httpclient , RequestConfig defaultRequestConfig , Map<String,String> loginMap , String orderJson ) {
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
			HttpPost post = new HttpPost("https://hoapp.juneyaoair.com/v2/commonPersonService/addCommonPerson");
			String timeStamp = System.currentTimeMillis() + "";
			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put("channelCode","MOBILE");
			reqMap.put("platformInfo","android");
			reqMap.put("clientVersion","6.3.0");
			reqMap.put("versionCode",63000);
			reqMap.put("token",loginMap.get("token"));
			Map<String, Object> requestMap = new HashMap<>();
			reqMap.put("request",requestMap);
			reqMap.put("addFlag",true);
			String depCityCode = flightObj.getString("departure");
			String arrCityCode = flightObj.getString("arrival");
			reqMap.put("depCityCode",depCityCode);
			reqMap.put("depCityCode",arrCityCode);
			Map<String, String> userInfoMap = new HashMap<>();
			userInfoMap.put("ffpCardNo",loginMap.get("memberID"));
			userInfoMap.put("ffpId",loginMap.get("id"));
			userInfoMap.put("loginKeyInfo",loginMap.get("loginKeyInfo"));
			requestMap.put("userInfo",userInfoMap);
			Map<String, Object> commonPersonInfoMap = new HashMap();
			commonPersonInfoMap.put("isGMJC","N");
			commonPersonInfoMap.put("interFlag","D");
			commonPersonInfoMap.put("countryTelCode","86");
			String passerMobile = json.getString("mobile");
					// passFirst.getString("mobile");
			commonPersonInfoMap.put("handphoneNo",passerMobile);
			commonPersonInfoMap.put("channelCustomerNo",loginMap.get("id"));
			commonPersonInfoMap.put("channelCustomerType","CRM");
			commonPersonInfoMap.put("ffCardNo","");
			commonPersonInfoMap.put("saCardNo","");
			List<Map<String, Object>> contactCertList = new ArrayList<>();
			Map<String, Object>  contactCertMap = new HashMap<>();
			String certType = passFirst.getString("passengerCardType");
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
			String certNo = passFirst.getString("idcard");
			contactCertMap.put("certNo",certNo);
			contactCertMap.put("belongCountry","");
			contactCertMap.put("belongCountry","");
			contactCertList.add(contactCertMap);
			commonPersonInfoMap.put("contactCertList",contactCertList);
			requestMap.put("commonPersonInfo",commonPersonInfoMap);
			String passengerName = passFirst.getString("passengerName");
			commonPersonInfoMap.put("passengerName",passengerName);
			String reqJson = JSON.toJSONString(reqMap);
			logger.info(httpKey + "HO添加乘机人参数:" + reqJson);
			StringEntity s = new StringEntity(reqJson, "utf-8");
			post.setConfig(defaultRequestConfig);
			post.setEntity(s);
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
			logger.info(httpKey + "填加乘机人返回:" + res);
			if (StringUtils.isEmpty(res) || !res.contains("resultCode")) {
				Thread.sleep(5000);
				response = httpclient.execute(post);
				res = EntityUtils.toString(response.getEntity(), "utf-8");
				logger.info(httpKey + "填加乘机人返回:" + res);
			}
			if (StringUtils.isNotEmpty(res) && res.contains("resultCode")) {
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
							logger.info(httpKey + "HO添加乘机人参数:" + reqJson);
							s = new StringEntity(reqJson, "utf-8");
							post.setConfig(defaultRequestConfig);
							post.setEntity(s);
							response = httpclient.execute(post);
							res = EntityUtils.toString(response.getEntity(), "utf-8");
							logger.info(httpKey + "填加乘机人返回:" + res);
							if (StringUtils.isEmpty(res) || !res.contains("resultCode")) {
								Thread.sleep(5000);
								response = httpclient.execute(post);
								res = EntityUtils.toString(response.getEntity(), "utf-8");
								logger.info(httpKey + "填加乘机人返回:" + res);
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
			}
        } catch (Exception e) {
            logger.error( httpKey + "error" , e );
            logger.info(order_id+"addContactPax异常返回:"+back);
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
	@Test
	public void d() {

    	logger.info(RandomStringUtils.randomNumeric(5));
	}
	public Map<String,String> queryDetail(CloseableHttpClient httpclient , RequestConfig defaultRequestConfig , String orderJson , Map<String,String>  bookingMap, Map<String,String> loginMap) throws JSONException, IOException {
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
			logger.info(httpKey + "HO查询详情参数:" + reqJson);
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
			logger.info(httpKey + "查询详情返回:" + res);
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
    private Map<String,String> booking(CloseableHttpClient httpclient , RequestConfig defaultRequestConfig , String orderJson , Map<String,String>  passIDMap, Map<String,String> loginMap,String flightMsg) throws JSONException {
        CloseableHttpResponse response = null;
		JSONObject json = new JSONObject(orderJson);
		String order_id = json.getString( "id" );
		String mainUser = json.getString("username");
		String httpKey = mainUser + order_id;
		Map<String,String> backMap = new HashMap<>();
		String res = "";
        try {
        	JSONArray passengerArray = json.getJSONArray("passengers");
            String[] dlyAccountInfo = dlyAccount.split( ":" );
            String proxyUser = dlyAccountInfo[0];
            String proxyPass = dlyAccountInfo[1];
			HttpPost post = new HttpPost("https://hoapp.juneyaoair.com/newOrderService/orderBookingV3");
			String timeStamp = System.currentTimeMillis() + "";
			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put("channelOrderNo", "N" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + RandomStringUtils.randomNumeric(5));
			reqMap.put("channelCustomerNo", loginMap.get("id"));
			reqMap.put("channelPrivateInfo", "HO" + loginMap.get("memberID"));
			reqMap.put("sfCardNo", "HO" + loginMap.get("memberID"));
			reqMap.put("ffpId", loginMap.get("id"));
			reqMap.put("certNo", loginMap.get("certNumber"));
			reqMap.put("ffpLevel", "1");
			reqMap.put("loginKeyInfo", loginMap.get("loginKeyInfo"));
			reqMap.put("routeType", "OW");
			reqMap.put("interFlag", "D");
			reqMap.put("useScoreTotal", 0);
			reqMap.put("useScorePassCheck", "");

			List<Map<String, Object>> passengerInfoList = new ArrayList<>();
			for (int k = 0; k < passengerArray.length(); k++) {
				Map<String, Object> passengerMap = new HashMap<>();
				JSONObject passengerObj = passengerArray.getJSONObject(k);
				String passengerName = passengerObj.getString("passengerName");
				String birthdate = passengerObj.getString("birthday");
				passengerMap.put("birthdate",birthdate);
				passengerMap.put("cNTax",0);
				String cardId = passengerObj.getString("idcard");
				passengerMap.put("certNo",cardId);
				String certType = passengerObj.getString("passengerCardType");
				if ("身份证".equals(certType)) {
					certType = "NI";
				} else if ("护照".equals(certType) || "港澳通行证".equals(certType)) {
					certType = "PP";
				}else if ("军官证".equals(certType)) {
					certType = "ID";
				} else {
					certType = "NI";
				}
				passengerMap.put("certType",certType);
				String channelCustomerNo = loginMap.get("id");
				passengerMap.put("channelCustomerNo",channelCustomerNo);
				passengerMap.put("channelCustomerType","CRM");
				String IDMsg = passIDMap.get(passengerName);

				String commonContactId = IDMsg.split("#")[0];
				passengerMap.put("commonContactId",commonContactId);
				String generalContactCertId = IDMsg.split("#")[1];
				List<Map<String, Object>> contactCertList = new ArrayList<>();
				Map<String, Object> contactCertMap = new HashMap<>();
				contactCertMap.put("certNo",cardId);
				contactCertMap.put("certType",certType);
				contactCertMap.put("generalContactCertId",generalContactCertId);
				contactCertMap.put("generalContactId",commonContactId);
				contactCertMap.put("useScore",true);
				contactCertList.add(contactCertMap);
				Map<String, Object> contactCertMap1 = new HashMap<>();
				contactCertMap1.put("certType","PP");
				contactCertMap1.put("useScore",false);
				contactCertList.add(contactCertMap1);
				Map<String, Object> contactCertMap2 = new HashMap<>();
				contactCertMap2.put("certType","HMT");
				contactCertMap2.put("useScore",false);
				contactCertList.add(contactCertMap2);
				Map<String, Object> contactCertMap3 = new HashMap<>();
				contactCertMap3.put("certType","MTP");
				contactCertMap3.put("useScore",false);
				contactCertList.add(contactCertMap3);
				Map<String, Object> contactCertMap4 = new HashMap<>();
				contactCertMap4.put("certType","ORI");
				contactCertMap4.put("useScore",false);
				contactCertList.add(contactCertMap4);
				Map<String, Object> contactCertMap5 = new HashMap<>();
				contactCertMap5.put("certType","NIPP");
				contactCertMap5.put("useScore",false);
				contactCertList.add(contactCertMap5);
				Map<String, Object> contactCertMap6 = new HashMap<>();
				contactCertMap6.put("certType","CC");
				contactCertMap6.put("useScore",false);
				contactCertList.add(contactCertMap6);
				passengerMap.put("contactCertList",contactCertList);
				passengerMap.put("countryTelCode","86");
				passengerMap.put("ffCardNo","");
				passengerMap.put("generalContactCertId",generalContactCertId);
				passengerMap.put("generalContactId",commonContactId);
				String handphoneNo = json.getString("mobile");
						// passengerObj.getString("mobile");
				passengerMap.put("handphoneNo",handphoneNo);
				passengerMap.put("insuranceAmount",0);
				passengerMap.put("insuranceList",new ArrayList<>());
				passengerMap.put("isBuyInsurance","N");
				passengerMap.put("isOwn",false);
				passengerMap.put("otherTax",0);
				passengerMap.put("passengerName",passengerName);
				String passengerType = passengerObj.getString("passengerType");
				if ("成人".equals(passengerType)) {
					passengerType = "ADT";
				} else if ("儿童".equals(passengerType)) {
					passengerType = "CHD";
				}else {
					passengerType = "ADT";
				}
				passengerMap.put("passengerType",passengerType);
				passengerMap.put("priority",0);
				passengerMap.put("qFee",0);
				passengerMap.put("saCardNo","");

				String sex = "";
				try {
					sex = passengerObj.getString("passengerSex");
				} catch (Exception e) {

				}
				if (StringUtils.isEmpty(sex) && "NI".equals(certType)) {
					char sexNum = cardId.charAt(16);
					if ((Integer.valueOf(sexNum) % 2) == 0) {
						sex = "女";
					} else {
						sex = "男";
					}
				}
				if ("男".equals(sex)) {
					sex = "M";
				} else if ("女".equals(sex)) {
					sex = "M";
				} else {
					sex = "M";
				}
				passengerMap.put("sex",sex);
				passengerMap.put("unlimitedFlyCardNo","");
				passengerMap.put("useScore",false);
				passengerMap.put("yQTax",0);
				passengerInfoList.add(passengerMap);
			}
			reqMap.put("passengerInfoList", passengerInfoList);
			flightMsg = "[" + flightMsg + "]";
			com.alibaba.fastjson.JSONArray flightInfoList = JSON.parseArray(flightMsg);
			reqMap.put("flightInfoList", flightInfoList);
			reqMap.put("couponCode", "");
			reqMap.put("countryTelCode", "86");
			reqMap.put("linker", json.getString("linkMan"));
			reqMap.put("linkerEMail", json.getString("email"));
			reqMap.put("linkerHandphone", json.getString("mobile"));
			reqMap.put("linkerTelphone", json.getString("mobile"));
			reqMap.put("useFareV30", false);
			reqMap.put("fareSource", "");
			reqMap.put("ffpCardNo", loginMap.get("memberID"));
			reqMap.put("isSaveCommon", "Y");
			reqMap.put("buyLounge", false);
			reqMap.put("loungeQueryList", new ArrayList<>());
			reqMap.put("postTripCert", false);
			reqMap.put("tripCertSendInfo", new Object());
			reqMap.put("buyWifi", false);
			reqMap.put("wifiQueryList", new ArrayList<>());
			reqMap.put("deviceId", "174ccc56-77b8-9f94-0000-0177d6cf2587");
			reqMap.put("clientVersion", "6.3.0");
			reqMap.put("appsys", "android");
			reqMap.put("blackBox", "eyJvcyI6ImFuZHJvaWQiLCJ2ZXJzaW9uIjoiMy42LjMiLCJwcm9maWxlX3RpbWUiOjMyNSwiaW50ZXJ2YWxfdGltZSI6MywicGFja2FnZXMiOiJvcmcuenl3eC53YnBhbG1zdGFyLndpZGdldG9uZS51ZXgxMTI5Njg3NiIsImNzaWQiOiIxNjE0MjE2NjQ0NDY2NjIyMzY3IiwidG9rZW5faWQiOiJrZ1ZDTTludlJoTkkwaWRKdXNETGhVYnJqellXSGlxUVArUVBHMzNLNTJEeHFhWlZzVWpqQWFWdlI3U01Cdk9uR1pLbEdCODZ5dThDUFVtMkk1OGF6UT09In0\u003d");
			reqMap.put("flightInfoComb", new Object());
			reqMap.put("channelCode", "MOBILE");
			reqMap.put("platformInfo", "android");
			reqMap.put("versionCode", "63000");
			reqMap.put("token", loginMap.get("token"));
			String reqJson = JSON.toJSONString(reqMap);
			logger.info(httpKey + "下单请求参数:" + reqJson);
			StringEntity s = new StringEntity(reqJson, "utf-8");
			post.setEntity(s);
			post.setHeader("channelCode", "MOBILE");
			post.setHeader("clientVersion", "6.3.0");
			post.setHeader("platformInfo", "android");
			post.setHeader("sign", "S6aCMRBQpVR+GUt2bRw58ve8ZrCK2EzKl5j8BR2zL0Zg9TnHJoDGboB6Z2+IJCS5");
			post.setHeader("timeStamp", timeStamp);
			post.setHeader("token", "www.juneyaoair.com");
			post.setHeader("versionCode", "63000");
			post.setHeader("Host", "hoapp.juneyaoair.com");
			post.setHeader("Cookie", loginMap.get("cookie"));
			post.setHeader("Accept-Encoding", "gzip");
			post.setHeader("Content-Type", "application/json; charset=utf-8");
			post.setHeader("User-Agent", "okhttp/3.10.0");
			post.setConfig(defaultRequestConfig);
			post.setHeader("Proxy-Connection", "keep-alive");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			response = httpclient.execute(post);
			res = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info(httpKey + "下单返回:" + res);
			if (StringUtils.isNotEmpty(res) && res.contains("resultCode")) {
				JSONObject resObj = new JSONObject(res);
				String resultCode = resObj.getString("resultCode");
				if("10001".equals(resultCode)) {
					backMap.put("channelOrderNo",resObj.getString("channelOrderNo"));
					backMap.put("orderNo",resObj.getString("orderNO"));
					backMap.put("payAmount",Float.valueOf(resObj.getString("payAmount")) + "");
				} else {
					backMap.put("error",resObj.getString("errorInfo"));
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
	@Test
	public void a2() {
		FlightRequest request = new FlightRequest();
		request.depCity="SHA";
		request.arrCity="SYX";
		request.internationalFlag="D";
		request.departureDate="2021-02-25";
		request.flightType="OW";
		request.blackBox="eyJvcyI6ImFuZHJvaWQiLCJ2ZXJzaW9uIjoiMy42LjMiLCJwcm9maWxlX3RpbWUiOjMyNSwiaW50ZXJ2YWxfdGltZSI6MywicGFja2FnZXMiOiJvcmcuenl3eC53YnBhbG1zdGFyLndpZGdldG9uZS51ZXgxMTI5Njg3NiIsImNzaWQiOiIxNjE0MjE2NjQ0NDY2NjIyMzY3IiwidG9rZW5faWQiOiJrZ1ZDTTludlJoTkkwaWRKdXNETGhVYnJqellXSGlxUVArUVBHMzNLNTJEeHFhWlZzVWpqQWFWdlI3U01Cdk9uR1pLbEdCODZ5dThDUFVtMkk1OGF6UT09In0\u003d";
		String sign = a(request);
		logger.info(sign);
	}
    private String selectFlights(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig ,String orderJson) throws JSONException {
        String res = "";
        CloseableHttpResponse response = null;
		JSONObject json = new JSONObject(orderJson);
		String order_id = json.getString( "id" );
		String mainUser = json.getString("username");
		String httpKey = mainUser + order_id;
        try {
			String[] dlyAccountInfo = dlyAccount.split( ":" );
			String proxyUser = dlyAccountInfo[0];
			String proxyPass = dlyAccountInfo[1];
			String timeStamp = System.currentTimeMillis()+"";
			JSONObject flightObj = json.getJSONArray("flights").getJSONObject(0);
			String depCity = flightObj.getString("departure");
			String arrCity = flightObj.getString("arrival");
			String departureDate = flightObj.getString("departureDate");
			HttpPost post = new HttpPost("https://hoapp.juneyaoair.com/v2/flight/AvFare");
			FlightRequest request = new FlightRequest();
			depCity = depCity.replace("NAY" , "PEK").replace("PVG", "SHA");
			arrCity = arrCity.replace("NAY" , "PEK").replace("PVG", "SHA");
			request.depCity = depCity;
			request.arrCity = arrCity;
			request.internationalFlag = "D";
			request.departureDate = departureDate;
			request.flightType = "OW";
			request.blackBox="eyJvcyI6ImFuZHJvaWQiLCJ2ZXJzaW9uIjoiMy42LjMiLCJwcm9maWxlX3RpbWUiOjMyNSwiaW50ZXJ2YWxfdGltZSI6MywicGFja2FnZXMiOiJvcmcuenl3eC53YnBhbG1zdGFyLndpZGdldG9uZS51ZXgxMTI5Njg3NiIsImNzaWQiOiIxNjE0MjE2NjQ0NDY2NjIyMzY3IiwidG9rZW5faWQiOiJrZ1ZDTTludlJoTkkwaWRKdXNETGhVYnJqellXSGlxUVArUVBHMzNLNTJEeHFhWlZzVWpqQWFWdlI3U01Cdk9uR1pLbEdCODZ5dThDUFVtMkk1OGF6UT09In0\u003d";
			String sign = a(request);
			String reqjson = "{\"arrAirportCode\":null,\"arrCode\":\""+arrCity+"\",\"blackBox\":\"eyJvcyI6ImFuZHJvaWQiLCJ2ZXJzaW9uIjoiMy42LjMiLCJwcm9maWxlX3RpbWUiOjMyNSwiaW50ZXJ2YWxfdGltZSI6MywicGFja2FnZXMiOiJvcmcuenl3eC53YnBhbG1zdGFyLndpZGdldG9uZS51ZXgxMTI5Njg3NiIsImNzaWQiOiIxNjE0MjE2NjQ0NDY2NjIyMzY3IiwidG9rZW5faWQiOiJrZ1ZDTTludlJoTkkwaWRKdXNETGhVYnJqellXSGlxUVArUVBHMzNLNTJEeHFhWlZzVWpqQWFWdlI3U01Cdk9uR1pLbEdCODZ5dThDUFVtMkk1OGF6UT09In0\u003d\",\"sendCode\":\"" + depCity + "\",\"departureDate\":\"" + departureDate + "\",\"directType\":\"D\",\"ffpCardNo\":null,\"ffpId\":null,\"flightType\":\"OW\",\"tripType\":\"D\",\"loginKeyInfo\":null,\"returnDate\":null,\"sendAirportCode\":null,\"sign\":\""+sign+"\",\"channelCode\":\"MOBILE\",\"clientVersion\":\"6.3.0\",\"platformInfo\":\"android\"}";
			logger.info(httpKey + "航班查询参数:" + reqjson);
			StringEntity s = new StringEntity(reqjson);
			s.setContentEncoding("utf-8");
			s.setContentType("application/json");
			post.setConfig(defaultRequestConfig);
			post.setEntity(s);
			post.setHeader("channelCode","MOBILE");
			post.setHeader("clientVersion","6.3.0");
			post.setHeader("platformInfo","android");
			post.setHeader("sign","S6aCMRBQpVR+GUt2bRw58ve8ZrCK2EzKl5j8BR2zL0Zg9TnHJoDGboB6Z2+IJCS5");
			post.setHeader("timeStamp",timeStamp);
			post.setHeader("token","www.juneyaoair.com");
			post.setHeader("versionCode","63000");
			post.setHeader("Host","hoapp.juneyaoair.com");
			post.setHeader("User-Agent","okhttp/3.10.0");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
            post.setHeader( "Proxy-Connection" , "keep-alive" );
            response = httpclient.execute(post);
			String back = EntityUtils.toString(response.getEntity(),"utf-8");
			logger.info(httpKey + "查询航班信息返回:" + back);
			if (StringUtils.isNotEmpty(back) && back.contains("查询过于频繁")) {
				res = "error:查询过于频繁";
			}if (StringUtils.isNotEmpty(back) && back.contains("成功")) {
				res = parseResult(back,orderJson);
			} else if (StringUtils.isNotEmpty(back) && back.contains("当日无航班或航班已售磬")) {
				res = "error:当日无航班或航班已售磬";
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
		logger.info(httpKey + "选择航班返回:" + res);
        return res;
    }
	@Test
	public void a3() {
    	String back = "";
    	String flingMsg = "{\"yprice\":1050.0,\"fareTaxInfoList\":[],\"useFareV30\":false,\"flightAdvertisement\":[],\"currencyCode\":\"CNY\",\"resultCode\":\"10001\",\"errorInfo\":\"成功\",\"interFlag\":\"D\",\"flightInfoList\":[{\"flightNoIconList\":[{\"flightNo\":\"HO1771\",\"airIcon\":\"https://mediaws.juneyaoair.com/upload/icon/air/ho.png\"}],\"cabinGMJCFareList\":[],\"totalTax\":0.0,\"wifiFlag\":false,\"aircraftModel\":\"空客320(中型)\",\"carrierNoName\":\"吉祥HO1771\",\"days\":0,\"deptCountryNo\":\"CN\",\"labelInfoList\":[],\"fareType\":\"Simple\",\"notVoluntaryChange\":false,\"travelNo\":0,\"duration\":7200000,\"id\":\"0_0_0\",\"depAirport\":\"NKG\",\"arrAirport\":\"TYN\",\"countryNo\":\"CN\",\"flightDate\":\"2021-03-27\",\"flightNo\":\"HO1771\",\"depAirportName\":\"禄口\",\"arrAirportName\":\"武宿\",\"minPrice\":278.0,\"arrCityName\":\"太原\",\"depDateTime\":\"2021-03-27 07:10\",\"arrDateTime\":\"2021-03-27 09:10\",\"cntax\":\"0\",\"yqtax\":\"0\",\"depCity\":\"NKG\",\"arrCity\":\"TYN\",\"flightDirection\":\"G\",\"depCityName\":\"南京\",\"stopNumber\":\"0\",\"stopAirportName\":\"\",\"mealCode\":\"S\",\"cabinFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"2.7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"infFreeBaggage\":{\"baggageDesc\":\"婴儿免费行李额\",\"baggage\":\"购买国内航班婴儿票的旅客不享受免费行李额，但可免费托运婴儿手推车一辆。\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"婴儿可免费托运婴儿手推车一辆\",\"commentList\":null},\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"},{\"desc\":\"起飞后\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"}],\"refundRuleList\":[{\"desc\":\"起飞前\",\"changeFee\":-1.0,\"ruleDesc\":\"仅退税费\"},{\"desc\":\"起飞后\",\"changeFee\":-1.0,\"ruleDesc\":\"仅退税费\"}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"},{\"desc\":\"起飞后\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"}],\"refundRuleList\":[{\"desc\":\"起飞前\",\"changeFee\":-1.0,\"ruleDesc\":\"仅退税费\"},{\"desc\":\"起飞后\",\"changeFee\":-1.0,\"ruleDesc\":\"仅退税费\"}],\"orderDetailBaggage\":null},\"cabinType\":\"UNLIMITED_FARE_V2\",\"cabinLabelList2\":[{\"labelName\":\"最高全额抵扣成人+儿童票款\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_17\",\"tourCode\":\"20210125\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":288.0,\"cabinCode\":\"X\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":300.0,\"changeRules\":[],\"fareID\":\"891103SO\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"27\",\"ei\":\"不得退改签\",\"fareSign\":\"02d630f3c6e5d76df919abde62ad03ec01bb9929\",\"intDiscount\":96,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868553\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"XCRCF\",\"freeChangeTimes\":0,\"baggage\":\"20KG\",\"refundedRules\":[]},{\"discountPriceValue\":0.0,\"showDisCount\":\"3.8\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"infFreeBaggage\":{\"baggageDesc\":\"婴儿免费行李额\",\"baggage\":\"购买国内航班婴儿票的旅客不享受免费行李额，但可免费托运婴儿手推车一辆。\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"婴儿可免费托运婴儿手推车一辆\",\"commentList\":null},\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":41.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":203.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":284.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":122.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":284.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":365.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":144.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":202.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":86.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":202.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":259.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"infRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"HIGH_REBATE\",\"cabinLabelList\":[{\"labelCode\":\"8230c8fc2d28438e813154764686cfa3\",\"labelName\":\"改期券\",\"labelUrl\":\"\",\"labelType\":\"activity\",\"labelNum\":0,\"showType\":\"notShowInBookDetail\"},{\"labelCode\":\"3\",\"labelName\":\"购票可获赠70积分\",\"labelUrl\":\"\",\"labelType\":\"activity\",\"labelNum\":0,\"showType\":\"notShowInBookDetail\"},{\"labelCode\":\"2\",\"labelName\":\"延误赔\",\"labelUrl\":\"\",\"labelType\":\"activity\",\"labelNum\":0,\"showType\":\"notShowInBookDetail\"}],\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"全额行程单\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"activityLabel\":{\"labelInfo\":{\"labelName\":\"高舱高返\",\"labelUrl\":\"\",\"labelType\":\"activity\",\"labelNum\":0,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/av/highRebateFare.png\"},\"labelDetail\":{\"title\":\"差旅惠选产品规则\",\"detail\":[{\"title\":\"预订规则\",\"description\":\"1.旅客预订航班时，选择购买“差旅惠选”产品可获赠改期券，改期券自付款之日起15天内有效，可用于抵扣同一订单内“差旅惠选”客票自愿变更时产生的改期手续费</br>2.在乘机人完成航空出行后三个工作日内，购票人账户将得到相同舱位溢价部分60%的等额积分返还</br>3.若航班延误三个小时及以上，购票人账户将额外获得100积分/张的补偿\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_rule.png\"},{\"title\":\"适用舱位\",\"description\":\"M\\\\U\\\\H\\\\Q\\\\V\\\\W\\\\S\\\\T\\\\Z\\\\E\\\\K\\\\L，具体以实际销售舱位为准\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_seat.png\"},{\"title\":\"适用航线\",\"description\":\"吉祥航空国内航班（不含港澳台地区），不包含代码共享航班和包机航线\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_plane.png\"},{\"title\":\"积分累积\",\"description\":\"购票人账户获赠70积分奖励\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_score.png\"},{\"title\":\"销售渠道\",\"description\":\"APP、微信公众号\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_channel.png\"}]}},\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"travelPrivilegeList\":[{\"privilegeId\":\"8230c8fc2d28438e813154764686cfa3\",\"name\":\"机票改期券\",\"description\":\"购票即享随心改期\",\"type\":\"coupon\",\"number\":1,\"detail\":\"1、旅客预订航班时，选择购买“差旅惠选”产品可获赠改期券，获赠改期券不可转赠，可用于抵扣同一订单内“差旅惠选”客票自愿变更时产生的改期手续费；</br>2、使用有效期：自发放当日起15天内有效；</br>3、适用航班：吉祥航空实际承运的国内航线的机票（018票号）；</br>4、持有吉祥客票及改期券的旅客，可在通过吉祥航空APP“快捷改期”页面办理改期时使用；</br>5、改期券抵扣改期产生的手续费，不抵扣舱位差价，且使用成功后不做退券/退款。如手续费低于券票面价，超出部分不做退回；</br>*温馨提示：已申请的特殊服务（特殊餐食、无陪儿童、轮椅旅客等），改期成功后需重新申请。\",\"labelInfo\":null},{\"privilegeId\":\"3\",\"name\":\"差旅惠选\",\"description\":\"购票账户获赠积分奖励\",\"type\":\"returnscore\",\"number\":70,\"detail\":\"\",\"labelInfo\":null},{\"privilegeId\":\"2\",\"name\":\"延误赔\",\"description\":\"航班延误三小时可获100积分赔付\",\"type\":\"compensation\",\"number\":100,\"detail\":\"\",\"labelInfo\":null}],\"id\":\"0_0_0_15\",\"tourCode\":\"CLHX\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":405.0,\"cabinCode\":\"K\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":300.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":41.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":203.0,\"showFeeRate\":\"50%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":284.0,\"showFeeRate\":\"70%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":284.0,\"showFeeRate\":\"70%\"}],\"fareID\":\"891103\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"38\",\"ei\":\"不得签转\",\"fareSign\":\"c957c1981806c3cafd236aee33038f9285e02865\",\"intDiscount\":135,\"dynamicCabin\":\"\",\"dynamicFareID\":\"891117\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"9\",\"fareBasis\":\"YKA6\",\"freeChangeTimes\":0,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":405.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":122.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":405.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":284.0,\"showFeeRate\":\"70%\",\"usedTicketPrice\":405.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":365.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":405.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":365.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":405.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"2.6\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"infFreeBaggage\":{\"baggageDesc\":\"婴儿免费行李额\",\"baggage\":\"购买国内航班婴儿票的旅客不享受免费行李额，但可免费托运婴儿手推车一辆。\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"婴儿可免费托运婴儿手推车一辆\",\"commentList\":null},\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":28.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":56.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":139.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":195.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":56.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":83.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":195.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":250.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":144.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":202.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":86.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":202.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":259.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"infRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"MEMBER_FARE\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"activityLabel\":{\"labelInfo\":{\"labelName\":\"会员专享\",\"labelUrl\":\"\",\"labelType\":\"activity\",\"labelNum\":0,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/av/memberFare.png\"},\"labelDetail\":{\"title\":\"会员专享运价规则\",\"detail\":[{\"title\":\"适用人群\",\"description\":\"吉祥航空实名制注册会员\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_channel.png\"},{\"title\":\"使用规则\",\"description\":\"预订会员专享价产品时，订单乘机人中需包含该会员账号本人。\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_date.png\"},{\"title\":\"适用航线\",\"description\":\"查询航班时显示有会员专享价标签的吉祥航空承运国内航班。\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_plane.png\"}]}},\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_15\",\"tourCode\":\"20210125\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":278.0,\"cabinCode\":\"K\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":300.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":28.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":56.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":139.0,\"showFeeRate\":\"50%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":195.0,\"showFeeRate\":\"70%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":195.0,\"showFeeRate\":\"70%\"}],\"fareID\":\"891103\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"26\",\"fareSign\":\"a697df226a72413bc89c02900b8ac292ba37b269\",\"intDiscount\":92,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868535\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"9\",\"fareBasis\":\"YKHYJA5\",\"freeChangeTimes\":0,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":56.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":278.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":83.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":278.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":195.0,\"showFeeRate\":\"70%\",\"usedTicketPrice\":278.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":250.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":278.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":250.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":278.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"2.7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"infFreeBaggage\":{\"baggageDesc\":\"婴儿免费行李额\",\"baggage\":\"购买国内航班婴儿票的旅客不享受免费行李额，但可免费托运婴儿手推车一辆。\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"婴儿可免费托运婴儿手推车一辆\",\"commentList\":null},\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":144.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":202.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":86.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":202.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":259.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":144.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":202.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":86.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":202.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":259.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"infRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_15\",\"tourCode\":\"20210125\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":288.0,\"cabinCode\":\"K\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":300.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":144.0,\"showFeeRate\":\"50%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":202.0,\"showFeeRate\":\"70%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":202.0,\"showFeeRate\":\"70%\"}],\"fareID\":\"891103\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"27\",\"fareSign\":\"baa55f1f3a333cccd39e19f7bdfa845ae84f5051\",\"intDiscount\":96,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868553\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"2\",\"fareBasis\":\"YKZJ\",\"freeChangeTimes\":0,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":86.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":202.0,\"showFeeRate\":\"70%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":259.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":259.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"3.8\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"infFreeBaggage\":{\"baggageDesc\":\"婴儿免费行李额\",\"baggage\":\"购买国内航班婴儿票的旅客不享受免费行李额，但可免费托运婴儿手推车一辆。\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"婴儿可免费托运婴儿手推车一辆\",\"commentList\":null},\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"40KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"40KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":40.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":80.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":200.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":280.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":80.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":120.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":280.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":360.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":144.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":202.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":86.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":202.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":259.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"infRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"BAGGAGE_FARE\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"activityLabel\":{\"labelInfo\":{\"labelName\":\"优享行李\",\"labelUrl\":\"\",\"labelType\":\"activity\",\"labelNum\":0,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/av/baggageFare.png\"},\"labelDetail\":{\"title\":\"行李优享\",\"detail\":[{\"title\":\"产品介绍\",\"description\":\"旅客预定“行李无忧”产品可获得40kg免费托运行李额，单件体积不超过40×60×100CM。\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_channel.png\"},{\"title\":\"适用航班\",\"description\":\"吉祥航空实际承运的国内航班（不含港澳台地区），不包含东航互售航班。\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_plane.png\"},{\"title\":\"适用人群\",\"description\":\"成人旅客，儿童、婴儿、关爱旅客不适用。\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_channel.png\"},{\"title\":\"航班日期\",\"description\":\"2021年1月1日~2021年12月31日\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_date.png\"},{\"title\":\"适用舱位\",\"description\":\"B/M/U/H/Q/V/W/S/T/Z/E/K/L，具体以实际销售舱位为准。\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_seat.png\"},{\"title\":\"退票，变更和签转规定\",\"description\":\"1、自愿退票：如旅客自愿退票：退票提交申请后行李额自动失效。<br>2、自愿变更：线上办理客票自愿变更，溢价行李额自动失效。<br>3、自愿签转：不允许自愿签转外航。<br>4、非自愿退票：旅客提交非自愿退票，客票按实际支付金额全额退还。<br>5、非自愿变更：线上办理客票非自愿变更，溢价行李额自动失效。<br>6、非自愿签转：溢价行李额自动失效。<br>如有问题请拨打95520处理。\",\"icon\":\"https://mediaws.juneyaoair.com/upload/icon/av/icon_channel.png\"}]}},\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_15\",\"tourCode\":\"2100076\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":400.0,\"cabinCode\":\"K\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":300.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":40.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":80.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":200.0,\"showFeeRate\":\"50%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":280.0,\"showFeeRate\":\"70%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":280.0,\"showFeeRate\":\"70%\"}],\"fareID\":\"891103\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"38\",\"ei\":\"不得签转\",\"fareSign\":\"0014f778457f46e2ad1c49250cc66a585112a259\",\"intDiscount\":133,\"dynamicCabin\":\"\",\"dynamicFareID\":\"893446\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"40KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"9\",\"fareBasis\":\"KCESA8\",\"freeChangeTimes\":0,\"baggage\":\"40KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":80.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":400.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":120.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":400.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":280.0,\"showFeeRate\":\"70%\",\"usedTicketPrice\":400.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":360.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":400.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":360.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":400.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"5.6\",\"personalizedDisplay\":\"BC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"infFreeBaggage\":{\"baggageDesc\":\"婴儿免费行李额\",\"baggage\":\"购买国内航班婴儿票的旅客不享受免费行李额，但可免费托运婴儿手推车一辆。\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"婴儿可免费托运婴儿手推车一辆\",\"commentList\":null},\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"2件,10KG/件\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"2件,10KG/件\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":60,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"cabinLabel\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":30.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":60.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":120.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":150.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":30.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":60.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":150.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":179.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":30.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":60.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":120.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":150.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":30.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":60.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":150.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":179.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"infRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"额外行李额\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"优先登机\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":2,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"宽敞座椅\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_3\",\"tourCode\":\"20210125\",\"cabinClass\":\"J\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"handBaggeage\":\"2件,10KG/件\",\"priceValue\":598.0,\"cabinCode\":\"A\",\"yprice\":1050.0,\"cabinNumber\":\"2\",\"rsp\":640.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":30.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":60.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":120.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":150.0,\"showFeeRate\":\"25%\"}],\"fareID\":\"825894\",\"upgradeFlag\":true,\"priceShowType\":\"FirstClass\",\"priceRouteType\":\"OW\",\"discount\":\"56\",\"fareSign\":\"63c334b8915b9e874ce4433e9d4cd097a208b3a0\",\"intDiscount\":93,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868539\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"2\",\"fareBasis\":\"AZJ\",\"freeChangeTimes\":0,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":30.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":60.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"25%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":179.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":179.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"9.7\",\"personalizedDisplay\":\"STEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"infFreeBaggage\":{\"baggageDesc\":\"婴儿免费行李额\",\"baggage\":\"购买国内航班婴儿票的旅客不享受免费行李额，但可免费托运婴儿手推车一辆。\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"婴儿可免费托运婴儿手推车一辆\",\"commentList\":null},\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":103,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"标准经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":51.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":51.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":103.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":51.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":51.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":103.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":206.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":27.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":106.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"infRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_6\",\"tourCode\":\"20210125\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":1028.0,\"cabinCode\":\"Y\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":1050.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":51.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":51.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":103.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":103.0,\"showFeeRate\":\"10%\"}],\"fareID\":\"412209\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"97\",\"fareSign\":\"0883edccda44901cf49c846e3c99199144d8b612\",\"intDiscount\":97,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868541\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":true,\"priceProductType\":\"2\",\"fareBasis\":\"YZJ\",\"freeChangeTimes\":99,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":51.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1028.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":51.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1028.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":103.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":1028.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":206.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":1028.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":206.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":1028.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"19.5\",\"personalizedDisplay\":\"SBC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"infFreeBaggage\":{\"baggageDesc\":\"婴儿免费行李额\",\"baggage\":\"购买国内航班婴儿票的旅客不享受免费行李额，但可免费托运婴儿手推车一辆。\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"婴儿可免费托运婴儿手推车一辆\",\"commentList\":null},\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"2件,10KG/件\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"2件,10KG/件\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":205,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"cabinLabel\":\"标准公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":102.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":102.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":205.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":102.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":102.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":102.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":205.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":53.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":105.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"infRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"额外行李额\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"优先登机\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":2,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"宽敞座椅\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_0\",\"tourCode\":\"20210125\",\"cabinClass\":\"J\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"handBaggeage\":\"2件,10KG/件\",\"priceValue\":2048.0,\"cabinCode\":\"J\",\"yprice\":1050.0,\"cabinNumber\":\"8\",\"rsp\":2100.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":102.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":102.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":205.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":205.0,\"showFeeRate\":\"10%\"}],\"fareID\":\"701450\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"195\",\"fareSign\":\"06fb7c76fd048896d7cbb9ae3033da401f816869\",\"intDiscount\":97,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868536\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":true,\"priceProductType\":\"2\",\"fareBasis\":\"JZJ\",\"freeChangeTimes\":99,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":102.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":2048.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":102.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":2048.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":102.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":2048.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":205.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":2048.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":205.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":2048.0,\"ticketUsage\":null}]}],\"cabinCHDINFFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"10\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"2件,10KG/件\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":105,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":53.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":105.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"额外行李额\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"优先登机\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":2,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"宽敞座椅\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_0\",\"tourCode\":\"190331\",\"cabinClass\":\"J\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"handBaggeage\":\"2件,10KG/件\",\"priceValue\":1050.0,\"cabinCode\":\"J\",\"yprice\":1050.0,\"cabinNumber\":\"8\",\"rsp\":2100.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":53.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":53.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":53.0,\"showFeeRate\":\"5%\"}],\"fareID\":\"834488\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"100\",\"fareSign\":\"0d7d22a681dc430d3c058c61a4a81138929e9153\",\"intDiscount\":50,\"dynamicCabin\":\"\",\"dynamicFareID\":\"701450\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"JCH\",\"freeChangeTimes\":99,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":53.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":53.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":53.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":105.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":105.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"2\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"0KG\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"成人可免费托运婴儿手推车一辆\",\"commentList\":null},\"scoreGive\":21,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"额外行李额\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"优先登机\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":2,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"宽敞座椅\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_0\",\"tourCode\":\"190331\",\"cabinClass\":\"J\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"INF\",\"handBaggeage\":\"无免费行李额\",\"priceValue\":210.0,\"cabinCode\":\"J\",\"yprice\":1050.0,\"cabinNumber\":\"8\",\"rsp\":2100.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":0.0,\"showFeeRate\":\"0%\"}],\"fareID\":\"834489\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"20\",\"fareSign\":\"16ac18572584879b5155a281394a652ac07a4d7a\",\"intDiscount\":10,\"dynamicCabin\":\"\",\"dynamicFareID\":\"701450\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"可免费托运婴儿手推车一辆\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":true,\"priceProductType\":\"1\",\"fareBasis\":\"JIN\",\"freeChangeTimes\":99,\"baggage\":\"0KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"10\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"2件,10KG/件\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":105,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"},{\"desc\":\"起飞前4小时之后\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":105.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":263.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":315.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"额外行李额\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"优先登机\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":2,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"宽敞座椅\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_1\",\"tourCode\":\"190331\",\"cabinClass\":\"J\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"refundedComment\":\"全部未使用时,未使用的首个航班起飞前(含)168小时,未使用航段按未使用航段票款5%收取退票费,且有最低收费金额0元;\\n全部未使用时,未使用的首个航班起飞前(含)48～168小时,未使用航段按未使用航段票款10%收取退票费,且有最低收费金额0元;\\n全部未使用时,未使用的首个航班起飞前(含)4～48小时,未使用航段按未使用航段票款25%收取退票费,且有最低收费金额0元;\\n全部未使用时,未使用的首个航班起飞前(含)0～4小时,未使用航段按未使用航段票款30%收取退票费,且有最低收费金额0元;\\n全部未使用时,未使用的首个航班起飞后,未使用航段按未使用航段票款30%收取退票费,且有最低收费金额0元\",\"changedComment\":\"不得签转；免费改期0次；允许升舱；;\\n全部未使用时,未使用的首个航班起飞前(含)168小时,未使用航段按未使用航段票款5%收取改签费,且有最低收费金额0元;\\n全部未使用时,未使用的首个航班起飞前(含)48～168小时,未使用航段按未使用航段票款10%收取改签费,且有最低收费金额0元;\\n全部未使用时,未使用的首个航班起飞前(含)4～48小时,未使用航段按未使用航段票款20%收取改签费,且有最低收费金额0元;\\n全部未使用时,未使用的首个航班起飞前(含)0～4小时,未使用航段按未使用航段票款25%收取改签费,且有最低收费金额0元;\\n全部未使用时,未使用的首个航班起飞后,未使用航段按未使用航段票款25%收取改签费,且有最低收费金额0元\",\"handBaggeage\":\"2件,10KG/件\",\"priceValue\":1050.0,\"cabinCode\":\"C\",\"yprice\":1050.0,\"cabinNumber\":\"2\",\"rsp\":2100.0,\"changeRules\":[],\"fareID\":\"834487\",\"upgradeFlag\":true,\"priceShowType\":\"FirstClass\",\"priceRouteType\":\"OW\",\"discount\":\"100\",\"fareSign\":\"3c6ba4c3e88d168474c50422dfae48865208d5d8\",\"intDiscount\":50,\"dynamicCabin\":\"\",\"dynamicFareID\":\"701450\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"CCH\",\"freeChangeTimes\":0,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":53.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":105.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":263.0,\"showFeeRate\":\"25%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":315.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":315.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1050.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"2\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"0KG\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"成人可免费托运婴儿手推车一辆\",\"commentList\":null},\"scoreGive\":21,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"},{\"desc\":\"起飞前4小时之后\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"额外行李额\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"优先登机\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":2,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"宽敞座椅\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_1\",\"tourCode\":\"190331\",\"cabinClass\":\"J\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"INF\",\"refundedComment\":\"免费退票。\",\"changedComment\":\"免费更改\",\"handBaggeage\":\"无免费行李额\",\"priceValue\":210.0,\"cabinCode\":\"C\",\"yprice\":1050.0,\"cabinNumber\":\"2\",\"rsp\":2100.0,\"changeRules\":[],\"fareID\":\"834490\",\"upgradeFlag\":true,\"priceShowType\":\"FirstClass\",\"priceRouteType\":\"OW\",\"discount\":\"20\",";
		String flightMsg2 = "\"fareSign\":\"d3ee3e1b7277fc7966f3eff4a9fc87c86095f27f\",\"intDiscount\":10,\"dynamicCabin\":\"\",\"dynamicFareID\":\"701450\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"可免费托运婴儿手推车一辆\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":true,\"priceProductType\":\"1\",\"fareBasis\":\"CIN\",\"freeChangeTimes\":99,\"baggage\":\"0KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":210.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"1\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"0KG\",\"handBaggeage\":\"无免费行李额\",\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"成人可免费托运婴儿手推车一辆\",\"commentList\":null},\"scoreGive\":11,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":0.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_6\",\"tourCode\":\"190331\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"INF\",\"handBaggeage\":\"无免费行李额\",\"priceValue\":110.0,\"cabinCode\":\"Y\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":1050.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":0.0,\"showFeeRate\":\"0%\"}],\"fareID\":\"701677\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"10\",\"fareSign\":\"806fa535aba5dfe99de7805b711be8808aa11290\",\"intDiscount\":10,\"dynamicCabin\":\"\",\"dynamicFareID\":\"412209\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"\",\"checkBaggeage\":\"0KG\",\"checkBaggeageRemark\":\"\",\"specialRemark\":\"可免费托运婴儿手推车一辆\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":true,\"priceProductType\":\"1\",\"fareBasis\":\"YIN\",\"freeChangeTimes\":99,\"baggage\":\"0KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":110.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":110.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":110.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":110.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":0.0,\"showFeeRate\":\"0%\",\"usedTicketPrice\":110.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"5\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":53,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":27.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":106.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_6\",\"tourCode\":\"190331\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":530.0,\"cabinCode\":\"Y\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":1050.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":27.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":27.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":27.0,\"showFeeRate\":\"5%\"}],\"fareID\":\"701678\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"50\",\"fareSign\":\"255edf12740627d06ed6bdd90f30fd55093f927b\",\"intDiscount\":50,\"dynamicCabin\":\"\",\"dynamicFareID\":\"412209\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":true,\"priceProductType\":\"1\",\"fareBasis\":\"YCH\",\"freeChangeTimes\":99,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":27.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":530.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":27.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":530.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":53.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":530.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":106.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":530.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":106.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":530.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"5.6\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"2件,10KG/件\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":60,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":30.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":60.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":120.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":150.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":30.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":60.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":150.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":179.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"额外行李额\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"优先登机\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":2,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"宽敞座椅\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_3_0\",\"tourCode\":\"20210125\",\"cabinClass\":\"J\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"handBaggeage\":\"2件,10KG/件\",\"priceValue\":598.0,\"cabinCode\":\"A\",\"yprice\":1050.0,\"cabinNumber\":\"2\",\"rsp\":640.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":30.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":60.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":120.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":150.0,\"showFeeRate\":\"25%\"}],\"fareID\":\"825894\",\"upgradeFlag\":true,\"priceShowType\":\"FirstClass\",\"priceRouteType\":\"OW\",\"discount\":\"56\",\"fareSign\":\"8790f0f624d573c161362a232b6d6db8e001683a\",\"intDiscount\":93,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868539\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"30KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"2\",\"fareBasis\":\"AZJ\",\"freeChangeTimes\":0,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":30.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":60.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"25%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":179.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":179.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":598.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"3.9\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":8,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":42.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":84.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":209.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":293.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":84.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":125.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":293.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":376.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_13_1\",\"tourCode\":\"20210125\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":418.0,\"cabinCode\":\"T\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":430.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":42.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":84.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":209.0,\"showFeeRate\":\"50%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":293.0,\"showFeeRate\":\"70%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":293.0,\"showFeeRate\":\"70%\"}],\"fareID\":\"413352\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"39\",\"fareSign\":\"3385e011606aaccafa578a65b89dd12bc59846fa\",\"intDiscount\":97,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868550\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"2\",\"fareBasis\":\"YTZJ\",\"freeChangeTimes\":0,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":84.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":418.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":125.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":418.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":293.0,\"showFeeRate\":\"70%\",\"usedTicketPrice\":418.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":376.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":418.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":376.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":418.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"3\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":32.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":64.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":160.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":224.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":64.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":96.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":224.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":288.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_14_2\",\"tourCode\":\"2102260\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":320.0,\"cabinCode\":\"E\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":330.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":32.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":64.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":160.0,\"showFeeRate\":\"50%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":224.0,\"showFeeRate\":\"70%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":224.0,\"showFeeRate\":\"70%\"}],\"fareID\":\"413606\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"30\",\"fareSign\":\"e1af5003fe79629e1b19258b5bf54f3367800dbc\",\"intDiscount\":96,\"dynamicCabin\":\"\",\"dynamicFareID\":\"891107\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"2\",\"fareBasis\":\"YEZJ\",\"freeChangeTimes\":0,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":64.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":320.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":96.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":320.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":224.0,\"showFeeRate\":\"70%\",\"usedTicketPrice\":320.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":288.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":320.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":288.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":320.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"5\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":11,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":53.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":106.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":264.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":370.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":106.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":158.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":370.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":475.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_12_0\",\"tourCode\":\"20210125\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":528.0,\"cabinCode\":\"W\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":540.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":53.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":106.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":264.0,\"showFeeRate\":\"50%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":370.0,\"showFeeRate\":\"70%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":370.0,\"showFeeRate\":\"70%\"}],\"fareID\":\"413098\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"50\",\"fareSign\":\"f2b9a47d76c5dc73a05291d626a41caea632f21e\",\"intDiscount\":97,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868548\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"2\",\"fareBasis\":\"YWZJ\",\"freeChangeTimes\":0,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":106.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":528.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":158.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":528.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":370.0,\"showFeeRate\":\"70%\",\"usedTicketPrice\":528.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":475.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":528.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":475.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":528.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"2.7\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"},{\"desc\":\"起飞后\",\"changeFee\":-1.0,\"ruleDesc\":\"不得更改\"}],\"refundRuleList\":[{\"desc\":\"起飞前\",\"changeFee\":-1.0,\"ruleDesc\":\"仅退税费\"},{\"desc\":\"起飞后\",\"changeFee\":-1.0,\"ruleDesc\":\"仅退税费\"}],\"cabinType\":\"CHD_UNLIMITED_FLY\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_17_4\",\"tourCode\":\"20210125\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":288.0,\"cabinCode\":\"X\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":300.0,\"changeRules\":[],\"fareID\":\"891103SO\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"27\",\"ei\":\"不得退改签\",\"fareSign\":\"05136013b6e13141020bd0dd75f3ab3bbd7225bc\",\"intDiscount\":96,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868553\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"XCRCF\",\"freeChangeTimes\":0,\"baggage\":\"20KG\",\"refundedRules\":[]},{\"discountPriceValue\":0.0,\"showDisCount\":\"2.7\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"1件,10KG\",\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":144.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":202.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":86.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时-2天(48小时)以内\",\"changeFee\":202.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":259.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"cabinLabelList2\":[{\"labelName\":\"手提+托运行李\",\"labelUrl\":\"\",\"labelType\":\"norommal\",\"labelNum\":1,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"},{\"labelName\":\"快速出票\",\"labelUrl\":\"\",\"labelType\":\"normal\",\"labelNum\":3,\"pictureUrl\":\"https://mediaws.juneyaoair.com/upload/icon/cabin/duihao@2x.png\"}],\"interFlag\":\"D\",\"flightNo\":\"HO1771\",\"id\":\"0_0_0_15_3\",\"tourCode\":\"20210125\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"handBaggeage\":\"1件,10KG\",\"priceValue\":288.0,\"cabinCode\":\"K\",\"yprice\":1050.0,\"cabinNumber\":\"A\",\"rsp\":300.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":144.0,\"showFeeRate\":\"50%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":202.0,\"showFeeRate\":\"70%\"},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":202.0,\"showFeeRate\":\"70%\"}],\"fareID\":\"891103\",\"upgradeFlag\":true,\"priceShowType\":\"\",\"priceRouteType\":\"OW\",\"discount\":\"27\",\"fareSign\":\"a8138760776707ba6353eb224cd9772bb4a7ebef\",\"intDiscount\":96,\"dynamicCabin\":\"\",\"dynamicFareID\":\"868553\",\"insurenceAmount\":0,\"handBaggeageRemark\":\"单件体积不超过20x55x40CM\",\"checkBaggeage\":\"20KG\",\"checkBaggeageRemark\":\"单件体积不超过100x60x40CM\",\"specialRemark\":\"\",\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"2\",\"fareBasis\":\"YKZJ\",\"freeChangeTimes\":0,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":86.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"2/D\",\"flightTimeCondition\":\"0\",\"fee\":202.0,\"showFeeRate\":\"70%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":259.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/MI\",\"timeConditionEnd\":null,\"flightTimeCondition\":\"1\",\"fee\":259.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":288.0,\"ticketUsage\":null}]}],\"carrierNo\":\"HO1771\",\"depTerm\":\"T1\",\"arrTerm\":\"\",\"ftype\":\"320\",\"minPriceAndTax\":278.0,\"mealCodeNotNull\":\"S\",\"etkt\":true,\"transferDuration\":0,\"flightSpanDay\":0,\"stopDuration\":0,\"virtualFlights\":\"0\",\"mealName\":\"点心\",\"codeShare\":false,\"asr\":true},{\"flightNoIconList\":[{\"flightNo\":\"MU2703\",\"airIcon\":\"https://mediaws.juneyaoair.com/upload/icon/air/mu.png\"}],\"cabinGMJCFareList\":[],\"totalTax\":0.0,\"wifiFlag\":false,\"aircraftModel\":\"空客321(中型)\",\"carrierNoName\":\"东航MU2703\",\"days\":0,\"deptCountryNo\":\"CN\",\"fareType\":\"Simple\",\"notVoluntaryChange\":false,\"saleInfo\":\"东航承运\",\"travelNo\":0,\"duration\":6900000,\"id\":\"MU0_0_0\",\"depAirport\":\"NKG\",\"arrAirport\":\"TYN\",\"countryNo\":\"CN\",\"flightDate\":\"2021-03-27\",\"flightNo\":\"MU2703\",\"depAirportName\":\"禄口\",\"arrAirportName\":\"武宿\",\"minPrice\":540.0,\"arrCityName\":\"太原\",\"depDateTime\":\"2021-03-27 08:10\",\"arrDateTime\":\"2021-03-27 10:05\",\"cntax\":\"50\",\"yqtax\":\"10\",\"depCity\":\"NKG\",\"arrCity\":\"TYN\",\"flightDirection\":\"G\",\"depCityName\":\"南京\",\"stopNumber\":\"0\",\"mealCode\":\"S\",\"cabinFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"4.7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":162.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":216.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":54.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":108.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":216.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":270.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2703\",\"id\":\"MU0_0_0_2\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":540.0,\"cabinCode\":\"N\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":540.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":27.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"15%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":162.0,\"showFeeRate\":\"30%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":216.0,\"showFeeRate\":\"40%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":216.0,\"showFeeRate\":\"40%\"}],\"fareID\":\"MU2703MUAN002\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"47\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"b2c0d31cc6d8e95d1eab17bcdb8883efe2169989\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"N\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":54.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":108.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":216.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":270.0,\"showFeeRate\":\"50%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":270.0,\"showFeeRate\":\"50%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":41.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":203.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":284.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":122.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":243.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":324.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2703\",\"id\":\"MU0_0_0_1\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":810.0,\"cabinCode\":\"E\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":810.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":41.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":203.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":284.0,\"showFeeRate\":\"35%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":284.0,\"showFeeRate\":\"35%\"}],\"fareID\":\"MU2703MUAE001\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"70\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"b75c3336e375a9054f03ee1d70d5cbe8ee80ab00\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"E\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":122.0,\"showFeeRate\":\"15%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":243.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"6\",\"personalizedDisplay\":\"BC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"cabinLabel\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":68.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":136.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":272.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":340.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":68.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":136.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":340.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":408.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2703\",\"id\":\"MU0_0_0_0\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"J\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":1360.0,\"cabinCode\":\"I\",\"yprice\":2280.0,\"cabinNumber\":\"A\",\"rsp\":1360.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":68.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":136.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":272.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":340.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":340.0,\"showFeeRate\":\"25%\"}],\"fareID\":\"MU2703MUAI000\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"60\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"10690638fe8baa378233166f8bfb0af0bad97b02\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"I\",\"freeChangeTimes\":10,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":68.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":136.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":340.0,\"showFeeRate\":\"25%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":408.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":408.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null}]}],\"cabinCHDINFFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"5\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2703\",\"id\":\"MU0_0_0_3\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":580.0,\"cabinCode\":\"Y\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":580.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":58.0,\"showFeeRate\":\"10%\"}],\"fareID\":\"MUCY003\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"50\",\"ei\":\"Q/改期退票收费\",\"fareSign\":\"c25d6ed3ca8e18b5104ba7aabec89f5d6f763075\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"YCHD50\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null}]}],\"carrierNo\":\"MU2703\",\"depTerm\":\"T2\",\"arrTerm\":\"T2\",\"ftype\":\"321\",\"minPriceAndTax\":540.0,\"mealCodeNotNull\":\"S\",\"etkt\":true,\"transferDuration\":0,\"flightSpanDay\":0,\"stopDuration\":0,\"mealName\":\"点心\",\"codeShare\":false,\"asr\":true},{\"flightNoIconList\":[{\"flightNo\":\"MU2136\",\"airIcon\":\"https://mediaws.juneyaoair.com/upload/icon/air/mu.png\"}],\"cabinGMJCFareList\":[],\"totalTax\":0.0,\"wifiFlag\":false,\"aircraftModel\":\"机型737\",\"carrierNoName\":\"东航MU2136\",\"days\":0,\"deptCountryNo\":\"CN\",\"fareType\":\"Simple\",\"notVoluntaryChange\":false,\"saleInfo\":\"东航承运\",\"travelNo\":0,\"duration\":6900000,\"id\":\"MU0_0_1\",\"depAirport\":\"NKG\",\"arrAirport\":\"TYN\",\"countryNo\":\"CN\",\"flightDate\":\"2021-03-27\",\"flightNo\":\"MU2136\",\"depAirportName\":\"禄口\",\"arrAirportName\":\"武宿\",\"minPrice\":540.0,\"arrCityName\":\"太原\",\"depDateTime\":\"2021-03-27 09:50\",\"arrDateTime\":\"2021-03-27 11:45\",\"cntax\":\"50\",\"yqtax\":\"10\",\"depCity\":\"NKG\",\"arrCity\":\"TYN\",\"flightDirection\":\"G\",\"depCityName\":\"南京\",\"stopNumber\":\"0\",\"mealCode\":\"\",\"cabinFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"4.7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":162.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":216.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":54.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":108.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":216.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":270.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2136\",\"id\":\"MU0_0_1_2\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":540.0,\"cabinCode\":\"N\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":540.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":27.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"15%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":162.0,\"showFeeRate\":\"30%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":216.0,\"showFeeRate\":\"40%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":216.0,\"showFeeRate\":\"40%\"}],\"fareID\":\"MU2136MUAN006\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"47\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"07d4a5d0de0fac2062b522e48b29d21a88e56e7c\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"N\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":54.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":108.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":216.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":270.0,\"showFeeRate\":\"50%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":270.0,\"showFeeRate\":\"50%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":41.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":203.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":284.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":122.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":243.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":324.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2136\",\"id\":\"MU0_0_1_1\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":810.0,\"cabinCode\":\"E\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":810.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":41.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":203.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":284.0,\"showFeeRate\":\"35%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":284.0,\"showFeeRate\":\"35%\"}],\"fareID\":\"MU2136MUAE005\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"70\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"f21dabdf2c8845d19f19cb557bcac055939fb2b5\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"E\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":122.0,\"showFeeRate\":\"15%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":243.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"6\",\"personalizedDisplay\":\"BC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"cabinLabel\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":68.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":136.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":272.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":340.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":68.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":136.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":340.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":408.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2136\",\"id\":\"MU0_0_1_0\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"J\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":1360.0,\"cabinCode\":\"I\",\"yprice\":2280.0,\"cabinNumber\":\"4\",\"rsp\":1360.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":68.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":136.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":272.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":340.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":340.0,\"showFeeRate\":\"25%\"}],\"fareID\":\"MU2136MUAI004\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"60\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"12a00147076781263e741510be0450429885d89e\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"I\",\"freeChangeTimes\":10,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":68.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":136.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":340.0,\"showFeeRate\":\"25%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":408.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":408.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1360.0,\"ticketUsage\":null}]}],\"cabinCHDINFFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"5\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2136\",\"id\":\"MU0_0_1_3\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":580.0,\"cabinCode\":\"Y\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":580.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":58.0,\"showFeeRate\":\"10%\"}],\"fareID\":\"MUCY007\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"50\",\"ei\":\"Q/改期退票收费\",\"fareSign\":\"d1b813f1cf60ced23583c78b137b3f380ed92055\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"YCHD50\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null}]}],\"carrierNo\":\"MU2136\",\"depTerm\":\"T2\",\"arrTerm\":\"T2\",\"ftype\":\"737\",\"minPriceAndTax\":540.0,\"mealCodeNotNull\":\"\",\"etkt\":true,\"transferDuration\":0,\"flightSpanDay\":0,\"stopDuration\":0,\"codeShare\":false,\"asr\":true},{\"flightNoIconList\":[{\"flightNo\":\"MU2364\",\"airIcon\":\"https://mediaws.juneyaoair.com/upload/icon/air/mu.png\"}],\"cabinGMJCFareList\":[],\"totalTax\":0.0,\"wifiFlag\":false,\"aircraftModel\":\"机型32L\",\"carrierNoName\":\"东航MU2364\",\"days\":0,\"deptCountryNo\":\"CN\",\"fareType\":\"Simple\",\"notVoluntaryChange\":false,\"saleInfo\":\"东航承运\",\"travelNo\":0,\"duration\":7500000,\"id\":\"MU0_0_2\",\"depAirport\":\"NKG\",\"arrAirport\":\"TYN\",\"countryNo\":\"CN\",\"flightDate\":\"2021-03-27\",\"flightNo\":\"MU2364\",\"depAirportName\":\"禄口\",\"arrAirportName\":\"武宿\",\"minPrice\":480.0,\"arrCityName\":\"太原\",\"depDateTime\":\"2021-03-27 14:25\",\"arrDateTime\":\"2021-03-27 16:30\",\"cntax\":\"50\",\"yqtax\":\"10\",\"depCity\":\"NKG\",\"arrCity\":\"TYN\",\"flightDirection\":\"G\",\"depCityName\":\"南京\",\"stopNumber\":\"0\",\"mealCode\":\"\",\"cabinFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"4.2\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":48.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":96.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\"," ;
		String flightMsg20 = "\"changeFee\":240.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":336.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":96.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":144.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":336.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":432.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2364\",\"id\":\"MU0_0_2_3\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":480.0,\"cabinCode\":\"R\",\"yprice\":1150.0,\"cabinNumber\":\"4\",\"rsp\":480.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":48.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":96.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":240.0,\"showFeeRate\":\"50%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":336.0,\"showFeeRate\":\"70%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":336.0,\"showFeeRate\":\"70%\"}],\"fareID\":\"MU2364MUAR011\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"42\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"f807df92e5f72f9ae3c9d328c5444e63c37a6255\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"R\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":96.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":480.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":144.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":480.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":336.0,\"showFeeRate\":\"70%\",\"usedTicketPrice\":480.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\"," ;
		String flightMsg21 = "\"flightTimeCondition\":\"0\",\"fee\":432.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":480.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":432.0,\"showFeeRate\":\"90%\",\"usedTicketPrice\":480.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"4.7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":162.0,\"ruleDesc\":null}" ;
		String flightMsg3 = ",{\"desc\":\"起飞前4小时之后\",\"changeFee\":216.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":54.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":108.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":216.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":270.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2364\",\"id\":\"MU0_0_2_2\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":540.0,\"cabinCode\":\"N\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":540.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":27.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"15%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":162.0,\"showFeeRate\":\"30%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":216.0,\"showFeeRate\":\"40%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":216.0,\"showFeeRate\":\"40%\"}],\"fareID\":\"MU2364MUAN010\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"47\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"6c60f349e3dd5cfee0a3d1790fb632d601c9c07f\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"N\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":54.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":108.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":216.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":270.0,\"showFeeRate\":\"50%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":270.0,\"showFeeRate\":\"50%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":41.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":203.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":284.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":122.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":243.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":324.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2364\",\"id\":\"MU0_0_2_1\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":810.0,\"cabinCode\":\"E\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":810.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":41.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":203.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":284.0,\"showFeeRate\":\"35%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":284.0,\"showFeeRate\":\"35%\"}],\"fareID\":\"MU2364MUAE009\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"70\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"6bc011e0f2bdee65de60a7feb4e2eea3a9a2816f\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"E\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":122.0,\"showFeeRate\":\"15%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":243.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"6.6\",\"personalizedDisplay\":\"BC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"cabinLabel\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":75.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":150.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":300.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":375.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":75.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":150.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":375.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":450.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2364\",\"id\":\"MU0_0_2_0\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"J\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":1500.0,\"cabinCode\":\"Q\",\"yprice\":2280.0,\"cabinNumber\":\"1\",\"rsp\":1500.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":75.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":300.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":375.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":375.0,\"showFeeRate\":\"25%\"}],\"fareID\":\"MU2364MUAQ008\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"66\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"ff84198d8c83c759fb4d56d569263e830a9c8fe7\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"Q\",\"freeChangeTimes\":10,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":75.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":375.0,\"showFeeRate\":\"25%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":450.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":450.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null}]}],\"cabinCHDINFFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"5\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2364\",\"id\":\"MU0_0_2_4\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":580.0,\"cabinCode\":\"Y\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":580.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":58.0,\"showFeeRate\":\"10%\"}],\"fareID\":\"MUCY012\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"50\",\"ei\":\"Q/改期退票收费\",\"fareSign\":\"6baee54a433ccce3e27aea9a8832d4d1c601451f\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"YCHD50\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null}]}],\"carrierNo\":\"MU2364\",\"depTerm\":\"T2\",\"arrTerm\":\"T2\",\"ftype\":\"32L\",\"minPriceAndTax\":480.0,\"mealCodeNotNull\":\"\",\"etkt\":true,\"transferDuration\":0,\"flightSpanDay\":0,\"stopDuration\":0,\"codeShare\":false,\"asr\":true},{\"flightNoIconList\":[{\"flightNo\":\"MU2783\",\"airIcon\":\"https://mediaws.juneyaoair.com/upload/icon/air/mu.png\"}],\"cabinGMJCFareList\":[],\"totalTax\":0.0,\"wifiFlag\":false,\"aircraftModel\":\"机型32L\",\"carrierNoName\":\"东航MU2783\",\"days\":0,\"deptCountryNo\":\"CN\",\"fareType\":\"Simple\",\"notVoluntaryChange\":false,\"saleInfo\":\"东航承运\",\"travelNo\":0,\"duration\":7200000,\"id\":\"MU0_0_3\",\"depAirport\":\"NKG\",\"arrAirport\":\"TYN\",\"countryNo\":\"CN\",\"flightDate\":\"2021-03-27\",\"flightNo\":\"MU2783\",\"depAirportName\":\"禄口\",\"arrAirportName\":\"武宿\",\"minPrice\":540.0,\"arrCityName\":\"太原\",\"depDateTime\":\"2021-03-27 14:55\",\"arrDateTime\":\"2021-03-27 16:55\",\"cntax\":\"50\",\"yqtax\":\"10\",\"depCity\":\"NKG\",\"arrCity\":\"TYN\",\"flightDirection\":\"G\",\"depCityName\":\"南京\",\"stopNumber\":\"0\",\"mealCode\":\"\",\"cabinFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"4.7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":27.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":162.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":216.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":54.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":108.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":216.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":270.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2783\",\"id\":\"MU0_0_3_2\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":540.0,\"cabinCode\":\"N\",\"yprice\":1150.0,\"cabinNumber\":\"6\",\"rsp\":540.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":27.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"15%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":162.0,\"showFeeRate\":\"30%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":216.0,\"showFeeRate\":\"40%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":216.0,\"showFeeRate\":\"40%\"}],\"fareID\":\"MU2783MUAN015\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"47\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"b835bcd8dd52c0053d962548470e0918a51ce5d6\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"N\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":54.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":108.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":216.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":270.0,\"showFeeRate\":\"50%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":270.0,\"showFeeRate\":\"50%\",\"usedTicketPrice\":540.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":41.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":203.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":284.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":122.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":243.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":324.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2783\",\"id\":\"MU0_0_3_1\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":810.0,\"cabinCode\":\"E\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":810.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":41.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":203.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":284.0,\"showFeeRate\":\"35%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":284.0,\"showFeeRate\":\"35%\"}],\"fareID\":\"MU2783MUAE014\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"70\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"c9de35001c4768fd75740dfe03e4107b882b3cf0\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"E\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":122.0,\"showFeeRate\":\"15%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":243.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"6.6\",\"personalizedDisplay\":\"BC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"cabinLabel\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":75.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":150.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":300.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":375.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":75.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":150.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":375.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":450.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2783\",\"id\":\"MU0_0_3_0\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"J\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":1500.0,\"cabinCode\":\"Q\",\"yprice\":2280.0,\"cabinNumber\":\"2\",\"rsp\":1500.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":75.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":300.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":375.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":375.0,\"showFeeRate\":\"25%\"}],\"fareID\":\"MU2783MUAQ013\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"66\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"613144a700fd122799b38c9a04dda443ffe3ff02\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"Q\",\"freeChangeTimes\":10,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":75.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":375.0,\"showFeeRate\":\"25%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":450.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":450.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null}]}],\"cabinCHDINFFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"5\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU2783\",\"id\":\"MU0_0_3_3\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":580.0,\"cabinCode\":\"Y\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":580.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":58.0,\"showFeeRate\":\"10%\"}],\"fareID\":\"MUCY016\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"50\",\"ei\":\"Q/改期退票收费\",\"fareSign\":\"222a65e82bb86b8bd8763e1735a5bdbbda67aeeb\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"YCHD50\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null}]}],\"carrierNo\":\"MU2783\",\"depTerm\":\"T2\",\"arrTerm\":\"T2\",\"ftype\":\"32L\",\"minPriceAndTax\":540.0,\"mealCodeNotNull\":\"\",\"etkt\":true,\"transferDuration\":0,\"flightSpanDay\":0,\"stopDuration\":0,\"codeShare\":false,\"asr\":true},{\"flightNoIconList\":[{\"flightNo\":\"MU9908\",\"airIcon\":\"https://mediaws.juneyaoair.com/upload/icon/air/mu.png\"}],\"cabinGMJCFareList\":[],\"totalTax\":0.0,\"wifiFlag\":false,\"aircraftModel\":\"机型73E\",\"carrierNoName\":\"东航MU9908\",\"days\":0,\"deptCountryNo\":\"CN\",\"fareType\":\"Simple\",\"notVoluntaryChange\":false,\"saleInfo\":\"东航承运\",\"travelNo\":0,\"duration\":6900000,\"id\":\"MU0_0_4\",\"depAirport\":\"NKG\",\"arrAirport\":\"TYN\",\"countryNo\":\"CN\",\"flightDate\":\"2021-03-27\",\"flightNo\":\"MU9908\",\"depAirportName\":\"禄口\",\"arrAirportName\":\"武宿\",\"minPrice\":810.0,\"arrCityName\":\"太原\",\"depDateTime\":\"2021-03-27 20:05\",\"arrDateTime\":\"2021-03-27 22:00\",\"cntax\":\"50\",\"yqtax\":\"10\",\"depCity\":\"NKG\",\"arrCity\":\"TYN\",\"flightDirection\":\"G\",\"depCityName\":\"南京\",\"stopNumber\":\"0\",\"mealCode\":\"\",\"cabinFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"7\",\"personalizedDisplay\":\"DEC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"chdBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"cabinLabel\":\"折扣经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":41.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":203.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":284.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":81.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":122.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":243.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":324.0,\"ruleDesc\":null}],\"childRule\":{\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"orderDetailBaggage\":null},\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU9908\",\"id\":\"MU0_0_4_1\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":810.0,\"cabinCode\":\"E\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":810.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":41.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":203.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":284.0,\"showFeeRate\":\"35%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":284.0,\"showFeeRate\":\"35%\"}],\"fareID\":\"MU9908MUAE018\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"70\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"cd25a17b40c22a953ebd9bcb7a6f18997fd5e499\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"E\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":81.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":122.0,\"showFeeRate\":\"15%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":243.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":324.0,\"showFeeRate\":\"40%\",\"usedTicketPrice\":810.0,\"ticketUsage\":null}]},{\"discountPriceValue\":0.0,\"showDisCount\":\"6.6\",\"personalizedDisplay\":\"BC\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"30KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"公务舱\",\"cabinLabel\":\"公务舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":75.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":150.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":300.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":375.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":75.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":150.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":375.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":450.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU9908\",\"id\":\"MU0_0_4_0\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"J\",\"cntax\":50.0,\"yqtax\":0.0,\"passengerType\":\"ADT\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":1500.0,\"cabinCode\":\"Q\",\"yprice\":2280.0,\"cabinNumber\":\"2\",\"rsp\":1500.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":75.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":300.0,\"showFeeRate\":\"20%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":375.0,\"showFeeRate\":\"25%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":375.0,\"showFeeRate\":\"25%\"}],\"fareID\":\"MU9908MUAQ017\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"66\",\"ei\":\"Q/不得签转/退改收费\",\"fareSign\":\"4672a52e7f13bd1c7a990ecd4384539c2aaa6f18\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"Q\",\"freeChangeTimes\":10,\"baggage\":\"30KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":75.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":150.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":375.0,\"showFeeRate\":\"25%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":450.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":450.0,\"showFeeRate\":\"30%\",\"usedTicketPrice\":1500.0,\"ticketUsage\":null}]}],\"cabinCHDINFFareList\":[{\"discountPriceValue\":0.0,\"showDisCount\":\"5\",\"changeServiceCharge\":0.0,\"ticketPriceDiff\":0.0,\"taxDiff\":0.0,\"xtax\":0.0,\"yqTaxDiff\":0.0,\"cnTaxDiff\":0.0,\"totalDiff\":0.0,\"adtBaggage\":{\"baggageDesc\":null,\"baggage\":\"20KG\",\"handBaggeage\":\"无行李额\",\"handBaggeageRemark\":null,\"checkBaggeage\":\"无行李额\",\"checkBaggeageRemark\":null,\"specialRemark\":null,\"commentList\":null},\"scoreGive\":0,\"additionalScoreGive\":0,\"cabinClassName\":\"经济舱\",\"changeRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":0.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":58.0,\"ruleDesc\":null}],\"refundRuleList\":[{\"desc\":\"起飞前7天(168小时)之前\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前2(48小时)-7天(168小时)以内\",\"changeFee\":29.0,\"ruleDesc\":null},{\"desc\":\"起飞前4-48小时以内\",\"changeFee\":58.0,\"ruleDesc\":null},{\"desc\":\"起飞前4小时之后\",\"changeFee\":116.0,\"ruleDesc\":null}],\"cabinType\":\"CABIN_NORMAL\",\"interFlag\":\"D\",\"flightNo\":\"MU9908\",\"id\":\"MU0_0_4_2\",\"comment\":\"\",\"tourCode\":\"WEB1004\",\"cabinClass\":\"Y\",\"cntax\":0.0,\"yqtax\":0.0,\"passengerType\":\"CHD\",\"refundedComment\":\"\",\"changedComment\":\"\",\"priceValue\":580.0,\"cabinCode\":\"Y\",\"yprice\":1150.0,\"cabinNumber\":\"A\",\"rsp\":580.0,\"changeRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":0.0,\"showFeeRate\":\"0%\"},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\"},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\"},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":58.0,\"showFeeRate\":\"10%\"}],\"fareID\":\"MUCY019\",\"upgradeFlag\":true,\"priceShowType\":\"Eonomic\",\"priceRouteType\":\"OW\",\"discount\":\"50\",\"ei\":\"Q/改期退票收费\",\"fareSign\":\"4f7467199e17fc4d122a226349494376092a7a6e\",\"intDiscount\":100,\"dynamicCabin\":\"\",\"dynamicFareID\":\"\",\"combineId\":\"\",\"insurenceAmount\":0,\"refundedFlag\":true,\"rescheduledFlag\":true,\"changeAirLineFlag\":false,\"priceProductType\":\"1\",\"fareBasis\":\"YCHD50\",\"freeChangeTimes\":10,\"baggage\":\"20KG\",\"refundedRules\":[{\"timeConditionStart\":\"7/D\",\"timeConditionEnd\":\"\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"2/D\",\"timeConditionEnd\":\"7/D\",\"flightTimeCondition\":\"0\",\"fee\":29.0,\"showFeeRate\":\"5%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"4/H\",\"timeConditionEnd\":\"48/H\",\"flightTimeCondition\":\"0\",\"fee\":58.0,\"showFeeRate\":\"10%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"0/H\",\"timeConditionEnd\":\"4/H\",\"flightTimeCondition\":\"0\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null},{\"timeConditionStart\":\"\",\"timeConditionEnd\":\"0/H\",\"flightTimeCondition\":\"1\",\"fee\":116.0,\"showFeeRate\":\"20%\",\"usedTicketPrice\":580.0,\"ticketUsage\":null}]}],\"carrierNo\":\"MU9908\",\"depTerm\":\"T2\",\"arrTerm\":\"T2\",\"ftype\":\"73E\",\"minPriceAndTax\":810.0,\"mealCodeNotNull\":\"\",\"etkt\":true,\"transferDuration\":0,\"flightSpanDay\":0,\"stopDuration\":0,\"codeShare\":false,\"asr\":true}],\"ticketingDate\":\"2021-03-24 11:17\",\"routetype\":\"OW\"}\n";
    	back = flingMsg + flightMsg2 + flightMsg20 + flightMsg21 + flightMsg3;
    	String orderJson = "{\n" +
				"    \"account\": \"13037284859_feeye123\",\n" +
				"    \"account_no\": \"123\",\n" +
				"    \"airline\": \"HO\",\n" +
				"    \"allPrice\": 0,\n" +
				"    \"bankType\": \"BOC\",\n" +
				"    \"billNo\": \"FY210324111731161655585189361188\",\n" +
				"    \"childrenMobile\": \"18617070230\",\n" +
				"    \"code\": \"\",\n" +
				"    \"codePassword\": \"\",\n" +
				"    \"creditNo\": \"6227612145830440\",\n" +
				"    \"cvv\": \"123\",\n" +
				"    \"drawerType\": \"GW\",\n" +
				"    \"email\": \"1437015921@qq.com\",\n" +
				"    \"expireMonth\": \"12\",\n" +
				"    \"expireYear\": \"2022\",\n" +
				"    \"flights\": [\n" +
				"        {\n" +
				"            \"airline\": \"HO\",\n" +
				"            \"arrival\": \"TYN\",\n" +
				"            \"cabin\": \"K\",\n" +
				"            \"departure\": \"NKG\",\n" +
				"            \"departureDate\": \"2021-03-27\",\n" +
				"            \"departureTime\": \"07:10:00\",\n" +
				"            \"fType\": \"go\",\n" +
				"            \"flightNo\": \"HO1771\",\n" +
				"            \"price\": \"278.0\"\n" +
				"        }\n" +
				"    ],\n" +
				"    \"id\": \"64069833\",\n" +
				"    \"idCardNo\": \"130925198211136613\",\n" +
				"    \"idCardType\": \"IDCARD\",\n" +
				"    \"ifUsedCoupon\": false,\n" +
				"    \"isOutticket\": \"true\",\n" +
				"    \"linkMan\": \"zxf\",\n" +
				"    \"mainusername\": \"policytest\",\n" +
				"    \"matchCabin\": false,\n" +
				"    \"mobile\": \"13037284859\",\n" +
				"    \"orderNo\": \"O321MCF1F1056D107397\",\n" +
				"    \"orderTime\": \"2021-03-24 11:17:32\",\n" +
				"    \"otheraccount\": \"112069_/JfzhBV4r5IXtkhj7zBfxJblWP+G5Nn5\",\n" +
				"    \"ownername\": \"张三\",\n" +
				"    \"passengers\": [\n" +
				"        {\n" +
				"            \"birthday\": \"1995-12-08\",\n" +
				"            \"id\": \"79267775\",\n" +
				"            \"idcard\": \"320724199512084856\",\n" +
				"            \"passengerCardType\": \"身份证\",\n" +
				"            \"passengerName\": \"裘勇\",\n" +
				"            \"passengerSex\": \"男\",\n" +
				"            \"passengerType\": \"成人\"\n" +
				"        }\n" +
				"    ],\n" +
				"    \"payType\": \"xyk\",\n" +
				"    \"payerMobile\": \"13532989542\",\n" +
				"    \"platType\": \"tongcheng\",\n" +
				"    \"qiangpiao\": \"\",\n" +
				"    \"username\": \"policytest\",\n" +
				"    \"xwk\": \"0\",\n" +
				"    \"ytype\": \"MEMBER_FARE\"\n" +
				"}";
    	parseResult(back,orderJson);
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

	public static String a(Object obj) {
		JsonElement parse = new JsonParser().parse(parser(obj));

		parse.getAsJsonObject().remove("sign");
		a(parse);
		StringBuilder stringBuilder = new StringBuilder();
		String str = "X8ofQWCp5xJd&2e5l@*60M%2rTEI$vtW";
		stringBuilder.append(str);
		for (Entry entry : parse.getAsJsonObject().entrySet()) {
			if (!((JsonElement) entry.getValue()).isJsonNull()) {
				stringBuilder.append((String) entry.getKey());
				stringBuilder.append(((JsonElement) entry.getValue()).getAsString());
			}
		}
		stringBuilder.append(str);
		return MD5Util.getMD5(stringBuilder.toString());
	}

	private static Comparator<String> a() {
		return ComparatorTest.c;
	}

	public static void a(JsonElement jsonElement) {
		if (!jsonElement.isJsonNull() && !jsonElement.isJsonPrimitive()) {
			if (jsonElement.isJsonArray()) {
				Iterator<JsonElement> json = jsonElement.getAsJsonArray().iterator();
				while (json.hasNext()) {
					a((JsonElement) json.next());
				}
				return;
			}
			if (jsonElement.isJsonObject()) {
				Map treeMap = new TreeMap(a());
				for (Entry entry : jsonElement.getAsJsonObject().entrySet()) {
					treeMap.put(entry.getKey(), entry.getValue());
				}

				Set<Entry> set = treeMap.entrySet();

				Iterator<Entry> iter = set.iterator();

				while(iter.hasNext()){
					Entry entry2 = iter.next();
					jsonElement.getAsJsonObject().remove((String) entry2.getKey());
					jsonElement.getAsJsonObject().add((String) entry2.getKey(), (JsonElement) entry2.getValue());
					a((JsonElement) entry2.getValue());
				}
			}
		}
	}

	private static Gson a;
	private static Gson b;

	public static String parser(Object obj) {
		return parser(obj, true);
	}

	public static String parser(Object obj, boolean z) {
		return parser(obj, obj.getClass(), z);
	}

	public static String parser(Object obj, Type type, boolean z) {

		GsonBuilder gsonBuilder = new GsonBuilder();
//        if (!(arrayList == null || arrayList.size() == 0)) {
//            int size = arrayList.size();
//            for (int i = 0; i < size; i++) {
//                gsonBuilder.registerTypeAdapter(((GsonTypeAdapter) arrayList.get(i)).a, ((GsonTypeAdapter) arrayList.get(i)).b);
//            }
//        }
		a = gsonBuilder.create();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation().serializeNulls();
		b = gsonBuilder.create();


		if (obj == null) {
			return "";
		}
		return (z ? b : a).toJson(obj, type);
	}

   /* private Map<String,String> login(RequestConfig defaultRequestConfig , CloseableHttpClient httpclient , String orderJson ) {
        CloseableHttpResponse response = null;
		HttpGet get = null;
		HttpPost post = null;
		Map<String,String> resMap = new HashMap<>();
        try {
            String[] dlyAccountInfo = dlyAccount.split( ":" );
            String proxyUser = dlyAccountInfo[0];
            String proxyPass = dlyAccountInfo[1];
            JSONObject orderjson = new JSONObject( orderJson );
            String order_Id = orderjson.getString( "id" );
			String account = orderjson.getString("account");
			String mainusername = orderjson.getString( "mainusername" );
			String httpKey = mainusername + order_Id;
			String username = "";
			String password = "";
			if (account.contains("_") && account.split("_").length == 2) {
				username = account.split("_")[0];
				password = account.split("_")[1];
			}
			String timeStamp = System.currentTimeMillis()+"";
			get = new HttpGet("https://hoapp.juneyaoair.com/geetest/new/initLogin?deviceId=174ccc56-77b8-9f94-0000-0177d6cf2587&clientVersion=6.3.0");
			get.setConfig(defaultRequestConfig);
			get.setHeader("channelCode","MOBILE");
			get.setHeader("clientVersion","6.3.0");
			get.setHeader("platformInfo","android");
			get.setHeader("sign","mEF4DoFnyXPNNpXTKe1reC8C9jW3c5LDR9Ynca8nOoRjI5gTf3rfy4hUeN7kXMBFfkpq4Ihv8W/JYjRhmFURdw==");
			get.setHeader("timeStamp",timeStamp);
			get.setHeader("token","4F7BE0BF1E1867DC63CE131BC842E59B");
			get.setHeader("versionCode","63000");
			get.setHeader("Host","hoapp.juneyaoair.com");
			get.setHeader("User-Agent","okhttp/3.10.0");
			get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			get.setHeader( "Proxy-Connection" , "keep-alive" );
			response = httpclient.execute(get);
			String back = EntityUtils.toString(response.getEntity(),"utf-8");
			logger.info( httpKey + "登陆初始化返回:" + back);
			System.out.println( httpKey + "登陆初始化返回:" + back);
			com.alibaba.fastjson.JSONObject obj = JSON.parseObject(back);
			String challenge = obj.getString("challenge");
			String gt = obj.getString("gt");
			String geetestUrl = "https://api.geetest.com/gettype.php?gt=" + gt + "&client_type=android&lang=zh";
			get = new HttpGet(geetestUrl);
			get.setConfig(defaultRequestConfig);
			get.setHeader("Content-Type","application/x-www-form-urlencoded");
			get.setHeader("Host","api.geetest.com");
			get.setHeader("Accept-Encoding","gzip");
			get.setHeader("User-Agent","Dalvik/2.1.0 (Linux; U; Android 11; IN2010 Build/RP1A.201005.001):");
			get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			get.setHeader( "Proxy-Connection" , "keep-alive" );
			response =  httpclient.execute(get);
			back = EntityUtils.toString(response.getEntity(),"utf-8");
			logger.info( httpKey + "type返回:" + back);
			System.out.println( httpKey + "type返回:" + back);

			get = new HttpGet("https://api.geetest.com/get.php?gt="+gt+"&challenge="+challenge+"&client_type=android&w=0t6IBbnSSqKqjFliQQrDsA..7B8E80ACDE0C3962967CC1ECD56DC45FBB6FDDBF9BEF7D96A40908CF799E738C3B0597CAE2070F70E89F89086C540557F036B2A972D259628DBAAA3C6AB6C49BEC02758C979440AEC8600C1A65C6D1EF3E4EF2E71B3B4466667E0DDFB4F7CA4BE29D5EAE5A5B2D2E6984ACFAA3E97B4644B2CEFBE43929C9DCB3EBAB41ABCCD5");
			get.setConfig(defaultRequestConfig);
			get.setHeader("Content-Type","application/x-www-form-urlencoded");
			get.setHeader("Host","api.geetest.com");
			get.setHeader("User-Agent","Dalvik/2.1.0 (Linux; U; Android 11; IN2010 Build/RP1A.201005.001)");
			get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			get.setHeader( "Proxy-Connection" , "keep-alive" );
			response = httpclient.execute(get);
			back = EntityUtils.toString(response.getEntity(),"utf-8");
			logger.info(httpKey + "登陆api请求结果:" + back);
			System.out.println(httpKey + "登陆api请求结果:" + back);
			post = new HttpPost("https://api.geetest.com/ajax.php?gt="+gt+"&challenge="+challenge+"&client_type=android&lang=zh");
			post.setConfig(defaultRequestConfig);
			post.setHeader("Host","api.geetest.com");
			String json = "{\"gt\":\""+gt+"\",\"challenge\":\""+challenge+"\",\"client_type\":\"android\",\"w\":\"gNsqj)3s1rvCBrEjJoTKmnGvtAc9zTIgGVyujkk2upQIzDt4uSONNSPR(2K3aaGtA1mMKsCZthuCtphBu8iXE71iRpu9CY5(nZ)XGD3jkh6jPQrPEgZINY9a0uwR4iQdNtyHvsDTA4CvJudSlcCgZhwK5Yv9Q8NwKah9zuwhOzAoa0j)yCQVcWCuArL5WTW5AKOlV273DeGkvI7QB8zDX9Nx1DeDeywEkbCz1pV)GPcY4LIBf5Jcw5z3VPRz9bVBu86LtHJWxcWZDgCqq5XoNyr1se)zTR9CUDHRNuWXAkHfaeD(CFXn6yhHkruQMzo3abFiSPM8gnwK3gEffVxie6JCZP7PfIHqnVNX5rc8zSA0pL9Mxu8P3q4jd9361IvVFYkzU7b3dqm3(yxaKj1gC1i4LwZjpjqeEX95StlArl(JzAbedAb9SAMbdZw47TG1ALve0ZDP2G0brYZaZld6MvZd3oqtqAgxZwraiZk9nhb0vO)8RkxZJFCm)O2ezE4hJQawbfjqGP0Vr0dIU2NkObw2oR)nADVMSAB9)YTuMwRPNe6ZqVfOlo0jDAMc6)ADoKeBXPGhNw48ed7Gd41myKLxepnsdqV0LLvI3GWUHw6woYeGT6xRGWEM0s9YiZGv7MBtSEKZgWYY7GgeBSmoKp3wAsXEjJsopBloO5m8rcbXsLRjB)Q)cjdTwnUbIIidKfvPKd)TlSpsqHgGqvzdFbOYLsM3eFOncdg(OJUjjnh5gzxRvL75HnUAeQNJ0s4M)KjoRD88qs9BGmiLBuGlVWCcoRVftm3FzQLZrqb1C4UA2Fo2zUER9MqbShir3MBIrqYM6sljz8PTI1OBBEa37)LCrNve86EyygeD3K7OwzicQMONLBJ54BZXE4SE2KfD5hCaRCRXjmXxo3c3eq1tegbP5h3eoMzICqanoEHmqsdrBRtu9cPsHE7fsgRbaWR(fcxMRzabmxVnn8jjEQEptZVGlUnTFibJZddRQ50CgxeJg1tw6UxI6wLgv5xWBRUyWmdSB3al8hS74PLTUrLyYAUszmWNsN2uXqncfKWI3PRCkn3AIpuzSc8zvTtXvE8oWc(L2Y3Eh4xlDW8XGtOrya4ru0dVc4MfDYj1sT56(APiAFL24Dl0u7L1H2JddT8kKUBL5EKO0S55(1PkY4y3lUazD5ErWLEHEES5Ara9TFlOHrxxsdiG403)7jBYhaqSFhfI4M4ItB9YlMqlgfoKctKodwICSgt5OWM7KHGGr11S(x60tCJPdxke(d7DpTZ(WGq8xBTtntqye8F)jVut4GGPy(RAF5KKQHt9D9r((Li9qhWcOvJCmh0JIxsRBJSv9hpG)esrCDrNyHQtbCHhKJhZ4zkNwI67ClH60WAvMON7gjXje)x3vmzUsS7foZ1AuA6rNqyV4LsGuC3NgD)CYUXN52I3hTdLNklsQaRGQgeLYb0bEsh6lSrwieZAEyNfzKo8GAn1M2aoPakzmwFbM9rraGu84UC59PQnjHzZ6kgt9ZX9HNsmTJn43Sr13i)PUs)27r19zoZs4)PFXvYJ3GiowQidz2xdx3)VCJzNQ0q5Hf3IPisuDrwLHhQH5KzTANaeIptFzjXz5fYIwzFoVIgoDr1Vmsd4kfBMbQsphR1LLvOxxR3To7Z9Yw61DPU)MLrh9qqExcrTCD6(zEcPk76sVvmHrPxI6qb4psZYb)G5YpyXfrGgpXLameIUdeqYUQDnMroe4x54I7z5TIMt0)jBzF2edQVk8F7SkXfDpSn4fKtoOe4YbnEZlPF60iOXTdHvaX0dyK(anlJDhCW5I3v0ZXjEK3p4rc1rwhfgPGNhqpn)t6mEyxYlLayW1TyE7oOV5tjYxLCx2P7X7Umzre(ehBtFIlCXLHuKstBGnAh7r4sX4i9COG7VRzXRy42NCASx0Y7DaLN7DwumdoUDg2lbaDy4kUpVUQYdM6FO5GGMnRqocIHd41xEL0k7JqObFli9vx9SSOecaVT9jiJY9XZ)DNv41WdRbGJCArZ0SpAjCKDMkX2RjYVEPCeqvJA7eMKRJTKcNizLL4guqoW6VTsLpFRpapTzv(fHNeRDThqVVGd6EO1BHd6xNsqFRrQ(ET1EeBHuMmy6e3BJ29REp9(T5YMCJq7nMp5KBqUpMg0C7HDTYDI6AkqTMSCChKZ5xFIZjPbsYChWdNlNi2kBhLjyBJ(NuhVbdB6F(DPpHMxiYWf(sz(vCyOZcgxl4cDQUiaWAkqipCAdMcVy0o0ckI5M8J)vjXzcY4yWxxTaxekk7yD1OIoCXULMvijlIxGNIQQs7WHqByK3Vql4wevxuS79N8LoHWu(XL0aGZ3bZcUrqCQJbIqSLs)fOebG1rZGgeGkvZrR0oQVGzn6E5)ek2IV4IJevmTZNauuWNJHTQOFwGihvzopYx)9txyUgqDFbNI9i99CNgWybWVtUT7zJyF8Mm1aYolDQG9XRcKg6YktB0UtOO4KI0A(HeQt98sH8Gk5fDuIFefcDy5yfwnYFc1caEOAVd97LehRbJQT4ivnr257tp2AUJguBj(FXnhhLNTp(v94dgOatpCYCl3P7lwJSnhf71mGAJpKsLVXTaM26mNRQXwz)Yckv7LsRikGNrOfTGiCyyuxcV3XVHb7mczDBZWzJ3NAnBqRrwqSo7gOaco7V1r6RXg8in28883c4PQ(3w6gaFMThFjXVxTQZIv)yZjnCXSEhfURcb68)8sxNEFzthcbE8p6vXzSDs0MV58Jp(v)30rhurMQiZXFedTe(q0(b7NKsT0mC7I6o1jQqlWTrsoQTjEiNZ2gGoXNPXMIKIPNFZ8Yd35F31MyiPh4p3yWlAjNcixePd6oLMTy)DF8EXnzm723ig4WxEN)Df)5625Q1UYBB3VQzm80wejknpVDHkCZs7IWTWVK)31N3ACIYRFvOrUlkwDHmvqSCFUfdho4xG)ey(yDXtF1HYPEJ8lQMK6eDO10xYM2Cq25drTORlkIZcC1Csj2e2xEKy)GrswAqiNujbH7(Rm(cgv)JMbdH8zoHwWKLu2RfcMJe7r2xf3uQ9E1yhgbzsnKQC4b6ytkB5izlJNvmGElY5V6Zx7uL(AVu1wmNXgKc9N2u7qm3t0Sh(Q)SxntPd1MKL)LITDOkulx3a5Gg4LMlBqvQS5ULCfL(U02UgAALRmwt9yjnfjrnaVmRPUBpoIYdFQI1FXLJqoEHx72U5V)lLKN69O(iw1NSzSdweGDPEvJXdq2LTvD1JpzikRX68q6PxhQX5Rf8gtZUpsVF2sOnJ1kNTSl0ewJ)pKRquWVG5WBLyKjk2cX0PuBW1YDdBhTtCOwPJjwF3K3bjtBcCg3HXFWRG0wtr6qCD2xlj6PKPspCjDLokctJfhiaU2eQ9rbMarTMDS5BXc8iwe44DBGiVz(fzPcF5UWSeFTka9te)yWRFrXWclS5gkItgOif4cTgJMHF2QwqwAtq7muHC4ZpxY7fIfzz3nKnCdWSvkk)f6pL)uGKf7V9vWIzFSkMtr)qwdMbuPwBTs(DIX(fvvTKb2IhB2IustxOgpOyTgouVE0L36tf1S48GStb(6VtJTzLvZ6qDSbe6bGaX(70QeamVTRLQVM76uVDcPqyvgleywKeemwIEAYyJzxgsUcy(B0emPzjB5DTT1YwNDcGihxUcEwAi2HpMpitpu5vzYeFY9eHXYRnslsy9855v8cOXdp2tCtEwvlmtBAyh1HRd4D()VKSuvj4nEpjDZsnoM(M0R5j8H4vQLm)Mffnn)H)4vMWD2)FQEbEQMaT1KWUz00VxqVw7RVAjr)t9TM6fmLGUAGoqhWhZ9iXF)tF76gcRx7gtyHFe7jDbKPvQCNxrJxSW(qa4QXKXUv48NrWLzISM5uPb4LrCZojdr4z(gKPD3n0s(0n875K79reX5R4rgKKMs4iRT)bHSCJn4m(CKAx1Vj7YvqAOt8ObSz45C)rg2cMhuND(6V2)r8xO2j)hBgCGJyj8OKMjXo)K8KosCW8IgjCQ4a5Veqe1N6T0jg8CXLAhLVateMEoukSTdUlYlEDo3xJAGEJIBbtUeIMKkj1BjzLfNEZItCMNrb6QcQ2vL51s(fEFRNzmXIYECFH9qCBsqmuyLT(UIWVGlbgMlowEGoWuR5ZRdiAKGJM(R46Mr5JnXh5ed4g94d(QhHXryiQIJHJG7QQyhFDCDNDrlHcCodNwrQERBoMHmQazQj9VPpXkUnKCOzx6mknWVrFg(d7MIrQ32Cro48I7P0RXV)ek8nMfngiUYBHmsNoq4jDX3mob4Jqw77O8Qro1EwtEqQ8LnYwE8zCwB6sznrweLb6qiJ4oQ0WprBDOyddx2cOJ9y6oRYWLeAImlwMh958zDtFLfOkuLwknHYkG6aw0FHTchXn22JdwJwylxKlaUCVHVfDWm4We4g3r3YnKIIjPOTBb3yMbvD(eyLmrwzF2bWjpeHl)vGdvxvjFBF9d)1TO5L4Fk1C9u11w(d6dD2c72GEhV89U82o(raADHLrzK6m03oxyuRxXyqL1MlMfAtQDU94Q6G9yltJi7hRjdf0ZO1e8XIKhhHqpu2J8joydtTIle739OQQOwnjxBzVZd4XgcrHo2ZOCmPAKcEXF7CDlUoW78renVnGtTvnbKxHDEVZM3JlLFfVOz3bifxwXZ0LgPlDZuE)NmafkqTlAmLm1Dcrfm03m(KqrjnqM5159PMWTu44djbESEdDWuvUcWfz70Yj8gyt5ih2CNBKkX)Y7sHA0Q1q58UeL5N5xZwZjj5VCIGV2UE(wTxJqRdSEsMghGZF1AJYMq8Z9XD8IvcELaa2GykqBCWGbFGZ0wseom)Ls5hGJYacVT6UoU(QRC)hBSH9PHZ5U36F8ER1(2B8DTVEaUIQ4IUvlRQxa39RQZIF4r02LyzBjXOJWze6KCP()7xpbtbziJbT5takVaqmiLsRVIFuBKpcfEuHPwj)e0E6rjbQc)(50gwzE4FjwNTa3Ju7S)kHthPaztCv4JgIg0(k2Rqu9ktZs7zsqfQnKOCiFPCVNOtoAlLGA5TbqM8LrufzeroPek)amWNR)VkzCh4)f8GS4MqHbaN)rsPA8vMONJkTLyJuQaB7r0Z3MuykleHW1dFWQ5N)aRV(DkLAZ9zFh2KVF8yDEgRyMnQ2XBxRTA8QABuPasVnFsmyPepv2gFQfQRTJamYYOhAONGvD24rP98IRKjIW4iS0pwyvnFfUsitDrQb3WrsfGjydo0FMcU)Pmzt2jBOutsPOECZIx5GsSNg1DpPmN6P6laZaqmwN7EUXlgzTNxPPEl0V6Yne7wj0iV1pIHYEKYz)CnkF7yx0gK2dX9dXs3(aBlTcWKOzctGoVMu4NvHT675tRrVFCO1MRs(FBITFw7wg(nDyI)T(jDChOiNyI)(dRUBZKFVWQ(NS34wDe3sUZubqOOYy)D7yi0cZ3Ubk1W9kmmIP3g0VYpihbH83IgLLfFSsQG7iH32J454z9J6dvoqL6uI8oUz089xbO(a(xHg0AsXQ2x2j2Auv9kMT0I)Sv31Utozq0HS63)42XiHHUgEq1gLR1SKoEbdJ3hGnkbACqVHofykHQWxJxCCKPjoNvNSFMDCrPK(hQXxw4I3(nVer5QphbI6awnu)D6GDCaEZF67Av9yGNvUPSmqMREz0p90XBJ6KgdynTGUExcp(jlnC5cnsucR6alSLzbAQo6cFAXESTJ4ewpcjGS5cLNGiBbzt8HLH6m0XoDQzoQ)nBrvikw85(DA6hQCn4rIgTgclurbAm(rSl6QUIcc3jeepZ2vryqqMy6JZh7czG8laGxsw4)Hrfr2kSPZzcGRbrhyb2O)M4j(d2MKhB4VuW((HSjVXYHF1c5vQhy5w(cYjv9j(AW2kcgCdwnrpdLATuf(cTlT40ZjIPwk6v4LOBQyBkiqmJ1LLTb4lKGxh8f9byWMQJGFlkT(eXClOy5RjqQ3y8Po0iSItZfaInN8dEskOLrBo2SOpxdEklInrmJOxTQN5TFViYYrhLINoIlAImet5(Bc5t11j6mKvxoKALpirCDm2z)plCCeA2WxtMPI1YBkkIoeGuzojy54kH5(m2fvHaBzQuIc4faibacDreRUdjltlWEeN9Zs195RZyS8YrRxbMVobtOJivZp1TwC1UODlsYXsIG(1QvmfOP2A5OnEGrS80Jjwa86XKD5yJ7O9eawGhKlAnqcRQ6AGS))a1w1Xha8rM9YZnjVb7NiQ8nWdTWPdAgGLOfc9N7vBAxFY38uIc0K8TvyVh4x1XG2H4ydL4B3Epkmv8b0UhV7DZF(FF6qH(ZdObJQC5CpMtETXq2y4HaUnAQX3yd16VGhvuu3ve8pW0wO)FjY1VQAV53DVyNGhVpBj3ZfXoMVqKGWuXhU2Ensa2yJE5YpTuONz4pwxEfdYZrI4EwuLaZlk73l1FymPfTP3NUoThdK20Qoc3GCWtF64FiL3RT9z8cEGvoSMYPV77fXMWGt81xKKR14RA2xQTNRlMLg)4wXgGWEJnA5yFGs5QcX8nixAQV32(amSM(iTRvMeKQvvLr4Pe3DVP3oGdSRQ6EHIuo7mPEUbtQgHtSYlriWPb7KJr2wdfRRZB71r0xUi2(uwGHtu1KjzKinvuMOfWXs5kJRUL2fqDNWHzZuMHJniQzKLJGOENeSXukuvkKkgBTmjwYOmxfyq0FnCu6pGd3xPRnjp5PJ35pfVWLwoSDLhiDr4tUM3vBvad28kzTY8g0WaCu8TZH)vMJE4wh(HVyHXX9T045i4qnSxgnlxhk7G0cpDR6VRCTyLnRwjWVyLOkFPcN7mjBM2Ir(C2UxF99957v)UunZTJo7X0pGW3jvL0vXg(J0Lh7B74x0DAdqiUza0YQDOsDumg3IoqR1nKl8KA7ZnX9C6y8Cl3ii28QmHCmZfLQ850t3L8gK4yNL1WnnrGBkXFbMx2HSSzAQn6aYQTIsfMrJdQ9vh3j4Qro8dtQT5(Hi6BA(DL3yb0GfLB71(yhnSq68W2XqzfMX7j89g3mixtWbnq5d6RKcXovhUOrIqMI42bh8qWA(W)zWhez3zEBp7YZysF6eCDHJbHRhY)pwRab5ho29KoYiyPDr2o5IQk4f(J)b798sTA0u59)6fkGBWcHr(0cu1o)DlYRJZAYIl1oqpSUtXSBPM8IrvOJOCPFMg2U1b32tH6zRxmPGdx2ujK7z5irabD3pGPDDr3QKC84n6We)Q94l6JInuStSqv1YLc1fywmqh10fOlpqVmX3(7LoAgq4JDCLjAXRHxeJX1eV6VR7oNeo0dXAYQe3vw)FIcyFdiC5j02PnPyCxyYrgLooKQjEHB4GB0jSkGgVTZULSdDgEjq(URuBYGpSzq1gS1J99qkHSUr2(KUDn2I4BIijKJ8h495181uZS(mqeQNHKYToZIW7(Pq35gMN3hKxym8VQ8FzAI54Xrb4mYUoCCNz5VDfDEs8yE8pKDvOO63fw9S4tCRhLpsfxj(hI6CsSRDfYIqUYBec6vAgdSSITbJJUSSaDaZtOjxm8wAUC7kSJO3jkcEH5QWvKOBHpBSDgUqyc2PskCf51wZkA8p9qvu1BKNJnq6GxObIX3fYv)6y6H3moZek2bFXeUSeb3kbdiR((jqKIJ8NJluH6eFt5TEjMwG2JsRYEI2Qdr19XG7P6f1NBHsVxM9IWfHZ5SqPpwBdOZz0XThZCOJiZ9CsbpZcQxMK2b94lUQFW)j02WzJR3wVHP0z1SP4(2wJilXdpgxVdEOUlVzr7A6GaHD1BxqZyuQlRdoxeVcJ1(sDv(EokdHn9NBsMkvATf7L0d5Y77swPOP0qxnrIthyHTdeZhcOtPOaScqwG2bOQAi9r6LDekSh7Re)swVQY1uwlygQZMzEZNqLQBRkiV13NviiqzRHEfart0(XwWAeO3XmpOwtHGQduV)MovEuegi5T91iV4iQn(XPhBwq6uBntKDhpbKZGxx500nB7gtbU8DU0lKMXPx4yyAxCkccDViwuvNK0CoT1b5uynN1UnXuKlBDE5YGqZNX0oxqR75B67vqGFk9By(vEc1HWzCjGVHrkDeKIlKC19m5xHJuTUiSCD(DEVEBH(tRwTdcdRVGcOhL8GDyStrCObPLkhx5osH)St(rmNU2pNZjwDLEdxJQfq(81qCwG2bOQeyMCWdFWTX5q)sIkOu1DMW9FkL8kR69(r5brvSDIXxsTZzrIzz1xjbijb3iClLJN0rQFj2oe3mTlUp304iv3zVHcYwxRyq237OUHWsXvEn(FiAPT9cchKpVDBdlegPTbT2Is1Bv7qZ0PvcKUmHKakBMfRpXyXs6dv49rPNw60zsrzunAqoyJPnRr55BNdxGcMF82bI(EcDWGmbK9GnvWibPsT4lWXvcm2NvUn8cRv96(jhIdpJZVjOXfdMl99H6AMIWrRdAKWUeIFLvEdFjsu(g7t9GdrBeWSUJX8awymlms4x12RS8ZjMSrxZ8ak3jR6Sp7yhg8jfcsmd8503VliyrBuzT5IQey1eMVpDw1PsrU5G15GZGZmmUlaxqAuy8gKVPx(9EN4aE((QfO2yMG0k8xjmCidoXoHjYg6LfQTzB738SDXqKofcYTZrYB8Rqh0vveuBev9qNIvBsnhXc9ZBuuDvmvqXyNPEqfV)oikZ807Lw7FCltR22)a5p8995QWzRznEj10Wuto76Yv62jYgQrXYuolmhzylTBHSh3wuzc88Ywupv)LVg4)mDn1OyvnR(hgfeNpfomXwgG1p3rXCHhLB5HVHiUJmljDQ94DWkUrl2GcqikF4cSPQ0JXl0AS8fbrr)p2R8ASoc9taiGOWwmlxTO0IwGWqrtareknKDjbtsly7ltSF3UMsXxFkp1Jz3QF9(HPx1XLRfqhcTC6czMov)AzmGTZeu4x8NHdMuqdlESAj4gMpftGTH1so73jehubAU4cc857fARVw9)XXCO3CGgwoEWfU(TutoAM(VJosNp837YzmNx2mtpLFVqHLJIs4csjNLWdpwsplIGdVmLg10NJKdzAZ(wLX2Ve)3w9BkfhiPsBHKbYcEyj0nVC7EM5G39qxegxVR1arlYJFxu9iCdCG5zxZNoWjEmMEXQ4C3YjpdHOn5)UyVRpKLA85ZeaEl3TmdvHYinsoIjHd(uUS2G7zYUj2n6wkEB09YUQ9hzbCpKa(zjTL6IeRPdg0LKbET)kBdsyY3rvzEcKEvolg9VLI4dSXuAekK72UfDSm92iu3RP6Lj1NIL5velrghwLp1qnOrCYmhjW((HodwxQeBZ(FlVrL2)wh3gwVA2kAv7Xu5tQcZ8ov0rEs)e8tCBh9uP)88z4mTSRc8Js52G1L7UPuO0OvmGJGsnBQrKWPwZwe8QGMaVYk8YT1TslyuwXVGpspGdh2NR3oJYHwyeuXYBR5kvN6vXgzl(iN4OHlijmJd99zNhRm3CiP(7WtOUjnQHZxXr1TIbRxWZnrP(EKjo3kZURnkOmRu0j5Sf)NJC6YUZpuLDVQPWniNd(fmCvc(w336Lne4wlMjf9ZP8g5BxMN6dYK04O8qzUUvUfiwtEGujJslMRxRfskDoq8NaysljHR3XJzkVxFmCMFmmJ2sFsUEmyyAf3u4tR5X9nIdLc7PmkvdyY(FuJzUfh(YyfKrbk3pHwjbBgWKfsOyc2eL4f2u6XtJv2N7HKkioZrp1ZkQa5JVe7VNXP1y72koWBXf(4(inACk9uP0jlUO)PoC2Ai5gopiqT8N)zoc)p1TGldu(TayE1mozWCDd53lqsjkuOufQziCMtHOYzdAClakoqO8hhXxpSejNZTGKuKwrlILHqM5sl2wJVF2TI4VzqAF21MxXTTLEhEnU3cOrfLJkF1D0uvp1ttlflV1A9pjv943kMcG(MY8gCsB(rag)caYdPSHneSdJejFwQsAjcOBKXocoJ4EB6W8gOhlth)gPj0vvUKd9)do829AcSoxgJhZ8jrj1tr60UZkGHPpNKxt71PPUd5k4BfZYLYiTqaoiBeqqPcQML7(xsjRw3R5uXm9jdmR98j2f0IiIrPpZXqS54DWKkEcUkhirMmE5b0AMLylhXFw)kb2VhNgHQPlxETS3zSC7ixcgQvZsSBhNXUVVPrGb82bUJQcz591NbIGQDCPGh3bU0iYuX6PbxkuKAZ6(eXVFETHO4zD57Sv9hNmAbsrtFyxUEVnrh1KqTanSIjBctqFm3gnnE7)a(DXyUYF5DbdYNyXRjt9xO7i1zhNRIDMS2aT3O1ay1KBUukC8dLk5CxRqqvmLgmP65XsOMpkhEKi0N2u(GDEe1RtsyXVTpLkFUKOm7q4km3vMdrBMx2BILoxkaozPSntrjFlUEGP9RruNnGNnyEuGaTPjoMYSM259ebiidfiadteckMzfo3eY8gdILmrMbV8KnRxtH67CVNMs3SOFbF7aRLnviIfqyjU324wMeyaN1426fxzuPJUTwMFincQDy)5XebnbcwgQd8eemueUFnQOrXLkQltUMj2yJYWGX7fa1UrLX7isqy8YTd02)uS6oK4oyEuQSCkg0nvFD4k1gjJhi2JtyaLHBHDpMdqpSyRSqzf0RkenEnYJiLYd0ybfbARatiZfnD8WXEMauDVTXRoc7PvymhPd4H2LQTO8Hn9QReikC48eiMkY0SNLie(W3J8bRKeEn)ywUPyV6cQaphdRO)CkStlO(5d0RrvRKniYJLWr7b6I6alncbTTWrSQVFBKQyd(zd2C3zdbetFOKouudJr8AsY8t590olvBdaaCgI9PztcV)uZqT064e6DpeZcejLCBsEhlhhoi4BptS61XsmWwId2V)4CqQ89O7QRxWOmHZrRwHryhVRyLoeK11BTOHSdHoSg534kP9YR3YCOnJRuq5qmzyKGioSNQ(w3TM6KtPtcmaS)NxKyE1HUc8quCsRK97BEgJSNF8j1rbWk2oVJpDPUrr16z(W(R4A8Y8y846FmyKPCI8SDsecQVEJ(Z4RY)i4TYvjWTTe1G(THhw8sANl5ZOTdAaP6DcGj2IcYJEmzju514f(ZjZcQElJI4R0xYXpBJKUhH549(Hq4Beg2rnG20ewlk6m74isu0ldLrV4aJByB6Q)OJNWC3yxJH7yDb7MGcGfbGMcfFnFuoT)AZcx1IVbhuqK)E2juBhnIYwmY7R4GYdXBB9Yc6pfnXTF3ASGJ(OSpJwoG1zKsOs2fKq38mfoel1yh9KhS64WCtmRdu2BB5mlteLyJhiabHGxdEwY9h3i4(jLPrxYnKVchJt64RK76gRLM)9LTo9J2owgKvYCG14Crl8Vfa1YXU0tpWct5W7R8SUkwWLs(LKI0YA2nwansg7xjtRN1AqCzebwrEC4C24fJQKoBDtRQpxrTMNrw51pVs)lCUwl(TtRYlPo6nnwXLK9RjqQrHEuectDNa9qufCQEHGgk)ezs(IEI2Eex59m7dkDifmXU6HGrfCXcOOVHVN(U(KE6m8QYZM7ANmMaF6NbBHEPntUyXNo5hNSC3iX3N))kmBU2Yn)rjKdUOV57tYFzN7uLBFtUOn9lwfeCeUdAb5sUGBsx45eCuEt6oqjgALiLtAB8PbqL2CKwq1JxTfdWlKzKWe9qOnJRnU)Ka0NKWJNe8E7sepeB(93(Z5NfiogK(KWTJWUSOfa6n5gPBycNf7Lz7Huf9R4UcJrzVAqAtIG6GAlkd69J2TvMocd90IPdkomVOjw99fOGY2eIWlxRvHzPX75mvMkK3uWU)mGZi4zzLWybmGlw9luweAgXQ0W2AI8Jw0T64OO6v9eZZjZNIgiFYLFNxZf9ExnHZEXkjhka1aX5tHZnTr4M)lwiK1NC(lDudtDVeJBdpdWs8NZuBFCoyXLQOBuag8yRSREwROxr6M91dNQo0gDLRm7()3LMFMrSbUjX4rw9t(SAl3Vh2v9F1z)NKIuzN)RveRNWMJ9sIP)IOXuTiwICttNXJeK(T)MXb(alIPAw7kgvxQBINi94vnfAYyGTHyFGYwsjLEJ(W(FFP7mpm(lTWpx1VriNrkx5qh3hq7Ds8BHJ)1UYGgnzotiLj)B1jlYM08(6k1lIFdutTjJ92cTDijGHWlNWfoBw2S2Ynb0zkJJMLhac49nnalV1BSstZD3LDm61gkTTuFOR9jPj)A7KYEal8D1oJ9AddWS4vm1fJyAEc7h6L)Auki639NzFKBqCKF27CO5LcoFO7bF0C3yDVKdg1Z(bJ5pTkznSvCnnlE8WghXQ983V66huUlQSAv4Bg)lLZl)d28mEKFwfR(vvij)enbANESTKTPJhduUoQlBHaoSJmKSat1bTlp0asrj2Psw(MpPBBhlEfP0OqYPPY0QyJ1fWXQ1ZHY6OtcPB6ImFwTS70Q85TZsDvhgnv0Pp11j65jtogv2oPKcsHvLA5leIOFOEXDJeT4hfi9ySv7gzujc9FGXSDuIVlLLbnDAkhUiIymbi64GHb5NmrmcNfn1katom02mOuMMmrkczUs7yPWpjqSYMkyHMntc3wRiN16JdPAScgp04sAEGHpOviWpPimMnSiFkg64DZe731o2uySfVDDWaar31HR0Sr7aERmoBtGFc2ZKbTBEEiq4z6Xi5JBLiW(O74h70dNAQHy1kr4vhXTaYUtuvRgPy4zI0J940a8yt89RmLTulcfD)mgo814gxZ042fkDlo6zrkdRwY5aKbjIcSP)(qBQrcik5de4AK4MhfFnycc3FLp)HRwJ)lv43aYzV4eXuKOICtaPzBOPdYZ42hqijq47PjCJ9l24wuUFiocC)B7P3(io4TD7GLbkd9Hf9ou7ewd9OJG3o2YBsb4GTjgXQ1(J9Hph8jODqGeq9rWEyle0Ks4l0nIzyRMEsJ0O2I1rS(ODYJp5ZKsP1XS)e6pusA1kek)d2JL2wVNL34tc)z)JH3dzaK7LLXcWhesxSSmCPukVyWMeC(26E9qZrLyvda7qViQK50eXSUTLu)n12UsWPFd4NP7UsopDOp1)GKM1cnvjuS1P3rBZ4OB(6X4ovqYbCNdfBjeSGgjeYeerBgxV(538sAqT4cPQOJQU76DxqCqO)VZUEyKOenw1TCXQvQJDGNz0QFAZVX5x)422XdQtyh)anj3SjZx1Gmjg7ummim14hhUVX7PNB469U9lVZntGLeT)NFNm)vvisyZZjfjZw2Dy2m869(N7TMfppRgzbol0ppUMPXNjpGZWvnWwhsNqLxEdznvbMV(EaA7tdCuHSY3ro)5UFDzYMxahsTRnrabnGy4LAFhfz21X2pEfvY5fVLixbqA8dFfD01Z2JdOtqoS7s1Gb1oDYopbWWRkCNlklFVOQ5u9lgfD3eZXCpDlxLEa9IKoLpYwEXEhnfPOHsgEjYDXm6HlJzoaUgt8t(HoyOgqBJmZYGzuR5FrdAsAejmIQcfvHFE9vtJR5GsFxb4qLa7UZlM7Pq)UGJjcFUJN5GCHJrKUhCw5odK(biHZA2V73)IsC(wy5J((dcc3WMLw520VWL9BaG93gWwBrI1E7jNo(KMXDMAIUnFmqJTIWR5EKkXdtotL6CvwDPnb4LEj9B5Rb36MwMsPeufKLYr7f6uF5t6mj)meeC2kJs1YK5UP5MVh2)sv9SLUAw0wBOLSBNfC6jsOojVisz354(OOM1Vvt5mwx(SHXpCMcZ7S0yZeNE5)HSEoPjefgv9)alqOiW97N1R4iW9tlRaUnFRuqVIcYI4ACz9YoGUKgZrVRiVdkp(fZfltwUX9WmwSCPnKh5bLVjJunZm5(UDjigh0vqUQcJkSSw9axEiDzB4uQkeFScm2hr2008f4MlBAutmd6fzEt(45US0ym1XbSIcZXLyqMARmw1gMOnf73b2xFxVEIMdfNlSWbwc53wEVpJjWQgVKRt(E0HvDOpyL0fzPHheoh9)YeE42UnWWPUMnOCDsRrsE2hcjUSeX8XOKS)Hwg2GdFLWxEvItLOU7cJYlrtgsp)l0WxOqQdaFGeEqpHnjtIj4FqKzbkGsOmUobU)2tIxLXTiNHXvKwKWbWyrrZuhWuHOGJinT04eyzhbLezovr(psea)(cMnJb4cG1Mjh)4h16g44micQPC2pRRe6XudrN7DEH(9fhTwo6Y6lE()55mk1JvWVtbKuMzPK2W6QTE(BPGZNgPtl0TaHhLiu(Lo0qv36G6(Lx9qoYfQnWUbMyW6Q2ejHP9uE1Q3hUsCjhYQchVVktoByXftz5xFFCrZAuEIOwTebxzKn)UE)c32OHIFNCGJU1d)0X045PDSh546tUXkYqOpSd2esSlGqD8CExUef6jPE8xaInGnaFycZVlshKMCg(lAD0OZYWp(BuYn0xZY1KwIfNCphyZaBr76RMoGaNVUISNx1fAnWEev0eDU8gTspe6hT7aao01yVuygkCjEYKjOWKB0F)Te8KOZPJu2fkb2dYdDZ1Yy57kC3M1lO5U)uiIviO7Dpxfcx9LCTcuda3zk6h)cmuFP(rsWF9q2GOE5jmyunp)BndRucP5B64lNpnZkXoStHEMt34yzwRj5QDDFEWRb(BphEYMEGfOqi8ByoEqg1R0btainRxdO3Gk3Qw)8yV2kRxlwrKSuYFIES849Lr8j8abUV5ap4ilaqrPJ8WKrq1bQnfCbkdPqblfNV8dfzqT(EK1Qrw41M240JaUNb9jzbfLks3acygaKWa2MdiHxfOWAEetS(DQk0NwSfM)m64ZoxBaBIfHBEA8yMM5ZTFoZf(MgNPjhAdBIs0xPfD(QsMVF9gipxiRxoB9ADmhQU0Q4PDhjmWlr8nNbUjqDNUnlyON0XyRDfxKcbiAEslPr(5fF1QWSfSPZvmEMVagJji578N(PPeoXzwu)qhktMdrucGmoBVpd7sBtmHDsSjBO2FX7Hq82tVzvEBkrhVyy)3cnz34PA(84mJMh8DL8)rZoUaTZ1chC4ODDgZupXQxX)2DWfVMfIjywFicZ3yaU2)EKgDRHBXSPhRjadv6yEb87NUtYmzH7d0VipUnMMKHnchJWCG1TYTPJLAXklJO35TtIAetpV9oxmJTyOodHd867cVQgIS4aB4z1FVFcAePYq8a44qoTMoacYnBC)DnLehWiQ3Ck0fXxqgjYXOEKycQKU3I(zpLDw)ksDcqfFVh1r(II(UAiScHlcAsw8gpu1WZqsd91VKPfTF)Fe0lsVs2OQghptIjcEqYjqeZK9GROXm7poPy4qA4lB3OJkMJtl3tspyoPf8fhFAVndIYkImeclv(ZETI6f0BThIAuZKgFbcskgRjmYqBxzSI0x6t2T6FBpeSaTHe8Q1VfsKEQVW3VQhux)jakqQvUHP6sa(nPouf6zrgzhm0Wf723DSj4b7lKCbvW6WJ)y8m3RKT4yUaTaCDcR7hivH2MphJotvcyLbmd67F8OLhxrPoZwPiEGLVk6TklQAYes5Y1)CrwEjjRRHgF1A(eFrYp)d8Syvfl6Y0pnzSvUhlhDGekSV2a3jUdA5hD28lH5haSWYxqunFyJdlj889N8wWA7Gjgb5YnJdS8mARCV)l9XvE0xPgQSBSptaZ5uNhh9R4TPWrVnepH4vdqXa5UyIoebyAeF1CBKCA3mKd5DJ2oNK89ySuFYKrgSIOBGPY)Q2wvqnOMVuwEzzTYwatjbZCDJm6lg(s)hIcH1h7MKhNBYWR6NLtJGY28M5MpCfl21v6XoMBcBDZF5Wq10ckXf6vrWmKfbbQuthao)I196VfAFBql9TpJIvIRQSj1QRX5gnHQQF0v0ysbqdP2iDyPC710Ik2sgjQzHPjL4l4uVGDTT1dYZPLxu4d4Jks2k6DU1bNcijHdM0mqqCNknCbEJeO3w)EgjW9XcRYOUa2Q14fXeuM17f6ADMD(0M(Ko4V12f5zY3Qi)GYi9pe9oEDYFN8csNi6vit7LM0ftHFsFDBYVhXhLUzH44eb5jC5SEpNiHDXgDeOnwJGdFON5tjrQght(XosUkH(KFtITeaigvn9pz28uQnAS6EbtlDU)Yc6J74KzKhYH8l0EZ8eU9GnTcR)ccPMwqTqupF3zRcKVAnpVkqWc1la7p5fb3RDONgzlXo3X6AKnrbeI2Gm5Gwdv6hxb4WkAjYnLvyYQ2f2uvQbFK9bq8EIoWAnvka6SPJwEmOf6KqBhjYoUTXb9aGIdJZW961r8aS)kj)VZT6J(w5Yyp9sTxAF)wP(jAW2VzgEfrmcSCuWKQwKkUUD2yVBetOuTcws8ILwEFa5G61cIERP)xg1yrG93Q0z)5ZGmPMtqUUf31LXlx35cx4gAavvriGL3G(EA2velLdeDun)GRqrKTfsZrjXEUHXXkvrIQq1JhLvnKGIC2I13bxIMSMZ0bN1Sh2tNy0QVm8rZ4nX(VAJVhHgrjtK5OPdDomu72ToJu)fUP3aAtxP1xSpo7BIoB02kxqBvYUie0)m5jEwhzyFUy3kbWp5(6QcslFYiBT0RqZy62wIvamyl1XPm0mIn6d0pfYWA(e3QZwy3z8nRXM5yCpR2IbfD8sdNBi4tuSam99PdFc1KAPH3Wp0u(oddDCu(lqw8pqjwUUT8OSHNd34wojKxqZAUvnf9rPDQ6sOMganA8WfXIp028X0dcoIpropnJMEI718crEBgJwB3vwCg7en9I3o0v6yv5fYKTOvvlD8WR0bwdKhSlklDoNwjc7byPoNPO(H8SzQ)G(r7GET6)wB0xfn2wK62)HUmnBs0RGDGXI8z7gesikhAzhC59JbsRUEF3MdeYL7nB)DQPGsuibCY04(nryp1l7tSm6WsRdE3GcMLudB9XvdmlY1b)rjZuWxpby7JKB13D(0C4n8Z4KUKzBJwAf5zdO6kqzpG8QBtQSus5y1LLcHOADCS0E8IqmVyAIXMhcPXd7hhlwHAjyVRLuQN3O)3mnYt7zb53EVUZXk3Mhr)Fsbgm(wX(cA9qS1HpejlJfe49ckDpzBas1rV3sciHhu7hVB0YkEJvUi3nulIsjvzgycCibMWnwssX(oDzd0Yl5LqiFUPsc16WMIk2ySKD6CN0c2qpvNSNqVjQkylq4zUTk)VU1TB6DRFWSUvpJtlcZBCoEuv8QJMhsAcf)MBV2mtNhxVmulxVumncLuFsj9jqND8foM)Nk7GlgkMPpXpF9wlaKdauhGK0MV6DTuh7gxKxH1Ah8lrA65a7(71Cyh1QXNC1VCdK410(t3iKiU90E4pkejiM78wOYP8dQHzTxq70uXkApYFb03gtTdCLlF0Z6Msc0JqmHqYTjL(afMYd(4r24QbEa6WY9XXWVY3t0)XbYaHh5vje1DDePNGmiSlrKzbium)EQlkpKaC3WLp4o7YVT6qhbh7za(kkQTEDMAzz5UTbkD4hh5FKXTzu9GIuvlVCxjc(90pdbtD9GCvHylJ7ca5pQkk4c1nJ5uKxa4uM)J)v(ciEWmvxIXVAGQVcwAof3jIM96pLne5libGBTjhyjmpl0Oy767JwMqXiw6PNKlpZhQn5Ee9OqItePfc8)d8reqb8sGGa4xpCrB(YqdhRHoySmDchjP1XD9kmwuvPa6DZYMf9X8G(YM(nnJTrlTejeSluVnP8hgAuSxi6CiqNgZ1hWt495ZockWFugfJxC(VoLSbxBnl8wYZtGsx6gwyeRcPfa10bssAX5NBaaOKRHsljj)E1xDCLet3bnohVeL9rpenASeh4s9RV05RgXkhlXmYVZ7XdACN664bnF4L7afj4d)5BWschUTaEPFAq5KlIj9yFvcs0f)hNF)oKPQJI)kWT5GXZwmI8hwUPJOP4)LyQeoummeRYtnZH49rB2fXBzYzNJKiaJ0zHWFcaPyhXxY3ObtgoTGcy96mxAP)A1BQTCV3wbPvx69p5bG6Bwf6Ji54u3)zktj9vXFsz)Bz4uyY8KuH2CUn1R4kZ9MWQP7ygWkRXsrdqkrdSsM1sMxNCL7HszQ63hmHJVhROgST9eYGCPZ28cD8(v20ZBGJaRNqy4VCTockioCBgXbzuLsxOBmTS8m7kqNENptrUqTT0qUwCcM(CzDR2ozaRvPh6dFucMFL3E0JTybEpwxL3zljGZsVI)Db2CiU8Y6c0hAf8EJ5LBbsh0HWDXMmesIoos)d0VCyaLxSbhLZLrK0knmWnPzUnrh0qhqrteAwecXFd1ymhStgkqxhlXRAl0Gysj4ljnSI6uent4o(Mp4XQHnc4)Oi0Y8TstRw0uDEQ8(KL6EKEvH978EBoZ9UVUiURMjEMQwuOsymrzG(qVEeLpS)PCdaqudyubxc7HZRPTZd(HhvTiScPfaFmsLwMx0QPQNSe7RY)in0plaKY72ox52P6iBizVrtwOV9Fbo2L7VbGguyq)sIFNs(gxfj3FZFKDXPuvw(BuhuM9blceiEa7XWk1SAlfduzdE7WFdTpETPqJ)UtRqtFEgvQnHcUMTgfTxBbNEbV44TpUMjCJ7qHJefI)izubwQTGd8XbuI3NgoD(WxNvOAj0mU8bK54cRc29WmZ2ChFS3MvDlAy42Afpbu0DkGgR(vi9CTBaqIA1Q)hpHUF8foVBTfVnfi((TvGKfIt229QF7xGbTVRHQt2gOjhkz03hIH(mmmbmm5t2m90wbdnXv4x2H9H9bRMWeqxvI(XzDZ(NQBQi1vaVnwUQsDSMmHyIoGsgWvVRTsCWfD4c2NAw16uxiXjnNPFw1E83fsVo20p)(4xRRZCy7NIgaJld397W8(c7SXF4cwCBOtRJafYwCRu89OA(SHUpjGi4iB3Kff4ZPfym82blZ7nLQiBqUlR54QHPZP42S8V0xjOFVXUfICocohoyTtbPVF1If8DXQ9Zuy2QGhQcIVeoHkD7HxRYNgQAZduoh4(B7KM9DHFtjBpXmUG7ud8JlPOjEb8tfuSoWeWtELCMRpqM1kDzgbOMcetY2MxokoyBSQuMRsy55fyMrg0Slylg5sYIb75OTE2sXiLWCQWcn(lf8zxLmywUBWEI5AmqWzxIVHgEJvlWpng46fITVG0Dv834GnMhF9(vtuvf1wz8hM6dLwWLKYXmnPs1G0s0VpRFjBuc0hdvrVjpcknQbnhtQhXK7igV5macOAGYOWnj4H57W2yK3RlVcP77Ru9(a0hPwDBnSAlu6LBkZkdsEffOGfJc(dAyZ8HnwYrsHdse2zlc7ak8XtJwh57V4Eisz(IBo3AdklYkWH4ADpGY5vtfWqLwOvWs6dCA(1R0IwgChDqDrCT2Y34GFnqEPmZ97bPhMA)dcY9rbbrD(UxfWDQriNHbV11GZ2Qw2GT)VVbmU7Ew6WVyUnRB8y10WTJXBN8SGMDl5pR1Zw6eFOa4WO4E25a6YxDej9zoaifVtczIM9fyF24h6Jd2Ti9p0nPDQPBqSWmNfiv42ZFQNhOonScZgfYNPU)ckbsVEjlntF3f6U2fm8G)Jk5ElKfSOebql6U5fL4rgFfVryrXF8xN8vstz2LsREBML8I8keiMesUp04wD0jJ6P3E9gcTIee0kE3b9Usa6f5vRg(w(ZHjrz3oiJodKcdt0nLO1u5tNQIV4TqmYYttBRZ)Djm2fy22E0s5Q754CanRkCw4U47T3Jxvk1cYeyiWoqTf04ipPkJN2TBJSCi4Zwdcb0h8NCXRuBWuf2RIhXTv8FFtpHOCrKTP2ypR)ybNyJpxyHJ(JuFg4(nFjZ7Xbe47Ix3zDPb49ASOTG7pOTaXcSNdiRsla7Rj7Bsh1SoAFxfZI9ekHXwsEtFzSsaiB2D)uXKXVlG4oOUl32OlL7NEoo)UiEtcCbsZbnsCoKLch5kT4HtQtvRZuICSNuaBIwDJCgIAvikHlgd9H1lTIJNPnkWEa(URGe3DL495Rn1vjlSxm6kOUV0112j2FvQAIOTqTtmu(8USYFLbMW(0NGlB0UtEYqJRDbDrlS56WoQA)b2KPVGrPvQkWWpOaX7gjNetotAHjF0ti3NsJBbfvsFBqNRcqP647k8YreTVjmD75)bx3yqMp5DgQXMaCpL4bf5u8Kmmrh2UBsSMGyK(frkNz8r4Tw0(9Ljcy59TbOq6OcYSHKgG)Qhstg5HKNIKcA55hnOiADyU1eY(fLGLgtu(x05yQvtO)aIz)7WOwokoAjUJGlfRUyAxWH8NuCCo63k0VL0xIL17lN80E9vVzpELoecVXgBByQT8iLJp3h5CZGukBoxPs0qeIcPYSSAg49MUfOP7TKN91O3Xe6v)nzIL76QSKd9My3qK3l)wWwRNJjgi84x8jfMV9M8Ew5Ufu13zEouxXJft7bYveFp(kiuUry4C19eQ7v1CSnvTiPmlaf6fJysEYR4yUAto3bAP3KhOosAvE5N6sd12Tm6oBDqXTBUYrDBsACZ5piUVyKsIUBB(GCivpPeTe1qBJlp82a9QjxG7pxJD4bOOmKNUmqOFmAPbGZ6yVvrIejPFTwGyCJ4T06Tk5Sr(EC(x(WaF1EGzmXPlclgJmaVbX)jwY1Q)(sMOwY0CQIgFRi5a08dqQEwZWJDanZUNhDI8ZcKJcij5xBGQ6kBj5IAu(m)okFYc)KgtZpgB)GOK2orxVoVuLm7hKJ3PXQaTMeF9b97nTSLmVDMMuHMoXjtMSna9gTZ64SrxEuShYpLf5f8956dlE7lkHOG0xJALP6PnqZoTPPBfYj(TZOj5ZEy))EREtVTL)GkliiCvraZs3feAmofRX(2k0MFzSRMuvITSCGzxvDdplsSfeW(bUTRDJ7RJCVjy6C)HdmO7OYUE8WIo9N8k6ytkhiPfC)CkLs1cno9onR(VQL1DSffZCkeiKD6vrfS9NHnrUMbHJshrP0Y17Iz1P8ldzh7DlDJScXNtxHDc7uuXJOI7pn(Px(6wtp8bWewnGNo(udqezJ5SoZmDc1GB87yq7vn4tMaYn(z(dpNHEaDzqWXpa4HwdVKotaMCKaFtO4Hh(mqC0uXIY(hcQXU5a9okDlY8lDM85WPwOJ2ECDT3maf)(ci75EVTVlaWGUKDOMwtvlE2xDV3VtwZxoUQLWae4c2nyYy0xnLh0Y0lAb4vZyn2v0DDdRP7vJYtqbkwRG8x))dWp9vJrRPNoVTq(SRuzRGKblSIZ9sbCDQiuaO24fkEr8)h)98sfp0cMayvqSxtPrLjdCQDj42KjEE6VQf0RvMq5)mMkNcMDS5rrbsjWwuEWshDUxYJSMqv3Lii3JkccdY1cePHubj7KS44NMi7IlPtOgsRM4ARG9)twzyFgpQmt5l2M8nt)MShv7AJlKYcVEIPl7lDvcGVjFh64qsidVpUGdIMybLnDDiIaCCWAHym6rE4Gg517W2RIYFIJtn(rbF(MDGQovjJJrCc4DgZGZTS)K8)9uOStjtu7Bi8BOpwh(WoFicGl3vf)DTSYNlZYTPwrA)8ub0SqBMNkhXXj1b)XrewIMnbmbN7lXqwzLsVqN6MMZD)Ictm60YDS4o809QX2LHR0aLuEtDmFMjG2pGEDcOfxRr2TYR1hxSrS49RfaUK4KioVZzM6(AjFpXUmQqq06RPE(Wvw1sEgVzwi(J8h)glF0QoUxxt)fZWdgTeWalV6AmB7y9ZeAS1pn0w4eiPLaBupRos(Dl8l1qHRTdcP4uPK4h4)G2a0JrP8JY8queu7D8jJzX8C9rHppdn86wWiLGOCZpel2yu(jVUvd)hFZhZ)qESf7hCs2Tc2)6sbieGd8)ilVBBhenRNek5YmmxRZIJ3EzCVDHrN3sV2yyA58S9UemNZ5nu0zZszpKqQ8ESgGedAaLtnor)8HvOmojM3XChdoIKgJxltilyHGHjtJrfI5wblTfjsZ7VI9adD9)gk3LQ3mj7D4sYxNxOjBb8(e3WTKDSP82ioVQmIJGN2wLKFIZBRlzLlc0G2)79utCtergN7fqSAM(czobnUk1g7Exxz)km1y3gxcz3rEqIwGByBzHgCx)1KGLkAQkKBblxlHQw(69tJYiND5S15IDD0il2BSJaLTTk2PfaKVBJDn6a(oXoYdcolTwwN28q3MUnf1LoBl(sckQ4EHr51meaH2rRjRW)wCIpEq3COCCthBvWcmuI27ri9v8rNMuE4xELXJAbMUJGtUn19E2sDrqtwpBqXvyJFFWuaZTNvg9vldAO7EQPEXZHkpkgmx6LzA5buKnlfVrpye6gSoc61NgsIHSp)5oZI7nqabXtWezprzRXw3rwdJdLa8x1QjCV9JE5dNGUhYpqc0ukeFUFcJ5NhSjmE17oRypVLeP5RYw0RoOtUEguK7ImR9jvz0OnfHLuxUVi2msetNxJGCrRIT)Geab6)dGFks(TnINris0RkTGeIewI)ogl07ErmjQoOitXz0xMb1NCTitcdOLCLc)lZGj53mJ0rdjds5fPzzleW5XvsUpV5qfGhL9YjXTwQuUA1cteynPrsN13zrPjbERXr5GvYJUQTKfiz7eNmf4vHesGIlI3MwPSsfcqaa59sXiiGFFzpw32L((YffYdEhVupHCuEcA1DyzzxqIlOFOh78GhGZxwkwCoOlpWKinQZtgxjxYfRWmnIP5RXcgWlUvOnPprfwJBR9c29BspdtdJ6GTXR0XX1rGdNVhngjhWOK7ARKO46Zacw6JDjjqqLbMO3ZYkd3KauorqWeCXFTXCF27uBTGNqV(A)4P0t(aHT85lJYnQxGMWFRhEiY1tldh7Q0sEGrzD9(HlSJvPIWfJEiRv5PTxn()wykjpVBIBzNNzL6fes7rL2j81Eih9ezqeV8DvlO4IZQ0NOiDElHIvLy4VzUBMGhMgmQ280yRZ7qdEVG07fMASoRHafY5stCajPDKfosOAeDDyhPbtGiGarn3x6TIV8ke6k9HpgRqRhxrttKDHM5KRbwupt(d8JQ8kJl(D9B0kA6umW0xE50T3ab)iecakqQY6HkLWCWN()GRzJba1ZN7D5PtFaLfp9)EOo5QioXAjWPqAbT1XZDT3RkPd(NQXYKr9L6HBF8Jr9bEcZHHPJwi(iUH0Qem7DShhFgIJBx3EwOluqU95f4vL1MmWHjav(N3s5UJUT(rPTcL27a5R6MEB6TW9YulN)eGrjLWL4V1rXi5tTUu)enhLEhNxPisHTTK7A8WfM2ooTTmhBahiCM55PTPrUX3biTzZgL2HsDgr1foON28rjWBOxuptFvowyLdvHnk5M)QAOh7SiFLVnX3ACoQGwrkEVUFdXDmlPoSJKsYPmfAmIp0dMXGl9lUTb(3IcJK3RPVaWH0CeFYPUZlfxdt3oJKMLt7xptmEyuEI5ZpLbBjM2S5yKvuQv9us)PI3VIJYMqMQXyEXAfhGhn7WiC(hBTHhLqvNlKc6KE4kBUWL4mYZ28d0lAV6WWvcnytdNDaOmxD0Z0smzbFK0PQ3(TEe8v(kNLrTrRo3Hr1lS3trTDOU5DJrRah690NuNiXhh0aIx)T386EDp3GrNisJycc)LZLTreQ))z)63xeAgtoMkg96oM5zTM)2LQAyNb0vKNk8ulNAnvGuv22s7gCdAQz3JpJiYA81CW)M79wC7sx4KshDgZbbiMo2RK3m1aJaR0)j6rTlbv2)LGJjBG7eblgUTABYK4MPQyZOI(nnbUYJMLIDUsNeSKotMRqbuNuuSRInMO2bPJV1XEbex19MN90yucR99qq385fMXHbJeqIoQsPxCqcZIHfkK60nKgb0gNoZdRzgremg09lBONU2w0Qtoe1XqUh9KdK5pkl0DfwBVzqMZKnIf7Ebexa0kD(650WuN3npRHUFaS6kvMaQDPKCBFNHMSlvhxbsVlFrc08HXslTUlBLAoh(7dezS5sH8HBUujjM(LkCJ6XAXKAqOTTfL2tJ0QZKkWKu4c0IweBJRZithPq8pz6QfFFvLhg00U5HgSUyhCpO2gixVuWgpeo21c1E9ZUIbYO3IFIZQ8UAuHwhGkGwlqrEmvNTRTAmb1d4MU0DWKmxRQmo6(ZLz2gD(89I(7qtfp4CvwYzKrM2oA5etX37lcXTyV)WPmgWU1L6ZVjFQpJ)rp1D0u87FrXpmjUHJO2lu1U6ueIkV)YwakZzMgKOTZfZaK6e(nH3QiMkIR7XPrd6zOGlg9BxMc8Cs63xcVZwjeUmrV2y101CQaPPIs3FsF6SK9BdcgzOFN(72LqQyAY3hoTu)il7eMRfHkuNclIBsFMa5sXKQb(jrUdt7I9lSvfXhNIm(Ep(4AZAW1rPxA1Ks79NxZoOvvakU7BAPP6q77PYsGTKX7gsu6KTIqzYJNlSaVdMbbpV(VS8H658F5uISy3Tk1mVxsBlxRW568ScYlzjTiqA8uw1YD4qt0Zr5O(HLGNfiDLX2GT7ZXA7v9pV(np(UCsfmBrZT4NXe81SrdCwTcKxoNlByOrUrGWguRldaPxvTwLCkxzWMhoP)RPfp4oyOnNEV7Z9ZTviVG(cCNbxIsITPb)Eb00izagGQWVqVLQ(89Ok2J7zPHQ26NAWwvdtI043N4Xoy4yqO5HepDu9TMvmYmN9EDZWZUcKIZAmDDfzLv14bYVbrJqupJEU4Y6ms9CFuqwHIHgDe0ie9lJYkBwRTe8kdLZm6GrLXv(32KqufJ0Ker019NNWmPfd7aJiZMGV5NnU9khU4vrxQ8U26B2VoKbFkucYZEIp8ritCFB2EG8oOpMURfqlopY1D(x4c)COsSBn7P6jIa)mxlgP26nDID4jXlrE3xZFBXlzY(TQ(2lgCaKceelOgM5aGeRPvTtn90p4TG4yjyN6wHxDyeFlhgfVDqBhSSKdlDdsFMke5dQlFrenvo1Hgek0OSLw0CrhlzUYLgNZJY5x64JiKxLg967AWhNfSjKhCc7m87zNU1J8Wk6dssd(jZNGhf0bNX0bxTSsYwFFw8bDn05ap9xBj80FHirl19kUPQLD7HxaFyaooxVYY4VicWmWNtucl5pNadK6wAg)c2C4Jq56hH6EbLBirPfo(G8x9g8fiTNZFUADEeyjjZWpVfwTEp)v4kzQhB07bv(AEZMRIXagQ3pnOxE)dAHmU7IxbfHZ47ZMA9IhlKrPjKqvBPU2YhW5H5lSiFQD(CyFudXybIPHCi4bt7Nb(AJUhM89kjuoST70NMZ5b5bBUkyUiT7UuHtgCoX1M1998IUqPLwyyOy6mZ)3YLA2RoLbM)ZmuL0V6jGBXdt6TWt8F8rnoNtLgFpO4fW9Z0LrbM9euCtb6Vsu7MjRuMqk01YaFgc6pi6wok7KgIscDeQLaMe6WQInFfQqq1XZE5LWF3eMPP3cKEdNvCS7sTSrv7cjSMRPzy99s6ngB6Xrv4AwNzoUvvCP8eQQtYfO94KQx(eZVmxVTb7G)8eAjWZfbIn0hqj8oqSBb5BUPx7VNqYp9uNUVqwvxsjqmwWqWFiHFGygvXOhmP6dEVKn5kmvCZNSPLOfXSIco)nR2jJmdUgUUiXe7RDhErrqaMmBzBQvTL(p(Yn651CSRqoiOQom4wEcwIfZ8wLh9jajQGla27SrgARCL2J(EZV0FDtuWa36LMkmpi8JWi38U8oZq()nMz6Mhu9txGQrfpkGh9pwl7S16uWBm4650IkkJGDTqJzONUmP5dHk7I14iqYkYEIkZYyGn7vG9cgAljjeqZQPhztpbeve0JoV0uv8DQwKTYFwshZ8NvK6gPyfqGWTB7mwrgOcoxvya6A(2Wz2Hg2uTwOriDzm111BamLkas0aGJO)DP(UcNVpWRhnJQsvyXC2ltprL33n4vXvuIWocMiM7Imm82piFW(clmr7gO3RT3RWsMr2eXR(MHv3ZAVfDVbUMI3sgqhXefq2lCUAgmOl4V80I4yEq8lLkJLqYd9a0JkmovRMhZs8IKECq2jcfFK9ItPhF3JOcOstnUjF9MC0luLArcku3orwq)tRMJWaLpBOLJp4Tiyi4iHgD7vSeXnUI6T9)9l5xkUaBBQiI(tIzcfj(C1ZNcp7umhVPbBlAMd1FpgVSL4G5sKFFj(17S7qcO6QaZqkrGArpJVLkLRC)ggMeocDusvn91F(VfunLzih3uGye7pqJFasYx(hDXQxmXOvwO9X9V08kJWKh2PUcmGBchMJb6t)SN2sAwCkFEnoaOKiZC5Z8IXQg5ATFlZAa7Gw8eXowyD9DBFUi)Oyl5nGmMEm9iQKDrN8K7SLEM2NeB1L)3wUzNuNrOedpAW5NJ0IhsINonY2T3ntOvmKsnr8qFt93d(KAJgngeaQvBlWQOWnZ0CYppYAGCcf9C0(iCbyFfZ6zUahSZHwOB8h8waaQ4H1kB9t9fMTgiJ445df5QyCM1Q3zY6MygVcfzlKIxLSwHaRWVWC)pD9OR6MplDqxbnYnGP8QMMy20jk4L30jLg58uWqaKX0PVM9gH(zaFUrUcTNG4VF0g4YHVl8zqR5)KkjhYxDwPX)4GCEejFlLsDAVSenGYDO)HxVkeB2jZgS(TaMEnjWKH5P7nlEByZPFIU8LkOhE1OvBTehpwv)QRTxkaIsLLoZu)BpaktXmBoV0NC7OVNdUyRu(CWIZ8wZ2MYUg(71KIpUAV7ABGrxo1XbhkTtPXHoT6lvi2664oT3bK0aZ9dP(MYGn4InqBHKIn2w14zAqU5bJ0zt(hcfvLXqsd(d0PC2l9h3KpYGHBbtTHDjeILIgppzcCmCRGuFGvo)2QJa4GNcJvh3Wa(jr)DHjEZ5K)GjQI7xToq1IZ7ST486jfcR(t1Gv6DQ84DGfT3FUzCfBr5k80Zg(gh85J(9pW3dZIfQV(Q6JF1OgBaIvTmaDnPM5FURMEl8uUg8OmYMkXySU2EE1iiUOondTqW(GMJ(XHhMiNMytrZeWOKdUldaT9xVpvNm96gkcI038)wyrEktR)zlp0SQD0FlYz789jWoogIdyMSkyLvz7ELY1kxRV1aJOU4iO6PIFJPUv4RVVEGfhmVNtujuvTT5XzfxpzAlgHAGEzhP3tLwm2yGbItllzGBoqj1kVFqflaUo5IV612ejx3pSGg43OKPqbTc(pT3ZnZBaW5WiI7ObF4WXAlk43l(eXTMerUPI6Dwj1fO2NDe5QbxQCJU8z19wUSPmnzNAs6v(jsaKujFLD(rPIDEHRD4)DBMdKqIMEiWy4JEFNo(r8HEXUtq2L5qRwCJNMHjzcw3a(sM8iVYRr1PVzuOu)iF2LvXB8yv1UxDjz2KMEnm5N9rX)N8ZZYG(Xce7nh83CGuZzK)IRo3KtPnuzRnlEYe)nrmd4wqZhKGGWqaISu1UY6UM5lvgAhpS5ysqb)LOOqdnmbNp(kcy0RanmrNFRfFoNXYHTASpX2qeIJAyjSvyWh9yR2vu6csQb3FXUYfYJHR2)MA4WDZ8brg7SmvJP)g)1MCTCkiX2Q033fFIT20RdvJRpJTUHQ)3rQOdaMq3kfhctxA7EDTnbyiWuwkxnk0LjYtmIrKeCeTORwVPVJEKUAWeJzoTm(XQcQggY7bd4CDAr9VGNtz6)vcMw0JalkLeTF1HqqZno6Pk8V462yul80n(kdjDEmw45KDcaSzu7WxW9UXYITyFwCGSf9LoADE6j05cfkQ4TngY(6k4G3jteng)KjklkMBQ9Wjv8zjlSESurjI2ucwNFo)NjNlmXbj2zpWtHhuQChHng5EZgPGJP53iHDE9QuS2mLYt4r1qhfElFUsiv)aIFVnjSQd91VR9axf1Ht7kRAD7jTmkEgCQaNEaFEpi1GnLimSfje0CJTJV3i4w85ttD5DuQOCaYQxxE7ng9sXoZLNIooONPEo4VEFSb7DVfNbuC3HgwcQFhY4bM0AAn0KH3zQ2P9SPZF4MBx(k86lFZUqmxzYdvSZQq0SnflEF(DM0fWvph7pflujBtT6bb)g8VHpNa6l5wuznME(Lrfaf7qawFudneJXySUBDcLei(daN8fdOA4EgY50VvmoOJRJ9fCDTAlgANhGNlIxgai)QiaUijzYEvVVavYLWv(wUVHcYa7SpF0vtBq8b6)dVE8wtX2Sgf2bZygKSjviJY(g5wxH44p2)HmEPH51O3yZo7AZnOt1vBTsBXkCRAfYDT)8w(pouLRrkKbH4)(AocWLpdzeOQ)KMuUyR)75OHQCVAPK)bjHup4c(OYbAXtlIOlcSMZ6pQVIqiglGwXv8UbeuhLGxX(wl9uXWuPZCPN(d6n0IXWEgGT)WTQxd75USS3AVxAoLVK3ob36yXa)oSQO97)GcJ4dQlmHJbp(zSrhL0Ebl9vabZOyEspe1gXgkCWrNbpxsIvUtncsR1)VpJzopiSw3a3mraitjlXnzOIhLyB7uu)pj0HyWeQqZEbXE81zmXSpBVSPSmU)lOxiRKEOBiibj2WP2b58UDa1ho7l7hkitVzvVvXi(JoZTpLOsYyDWawUkojN4CVrUSvGrrQL2Hxj1lECvpmo8yWXzokSVzsAL(rL1dcKP9sNReMWQ0OUwYB4Gad6iOSOf1fbCUiRjLnbv)B1e3g7Xx2gpeNExdh(AFL94BkPwaqensTA5fAk6aWZyCT9uWLCRJvSyKIu(1wyJVPwtk8k2cmOmN5kY1gaxGvuEBqEclVnZakpxdSpHtknc8dvE3dCHpFZ52)WvkM1UAJrzWTfej(CuTIFmuBB1RRNwCv((c3tOn4uwlcWBtZVNNCXZ)VR2T)FvrzPogDvkDS70C(Aoi8OEvY818NmSwNymzoK0FXcWzSby7hkvAMLTHRv)EMJ8oomPrUGRcpo6t2)PbzVBE1YubdPsqd739ApimWBmapo7CteLx7Waszu2RJWZco8HMBOgfA6mEZBynvEIlAwviHlPnXEqf0kYoFyzyNlQTuW2k3p5B2fvKKW6PNk4040jvEjv18ilweqnTAGAg1rZpKHdFx29EcML06fgqfn55lNZXnDetszDA6isD4Q2kLU5p3ryhP0sycjh0R14nvZNWcoxO5loWqsdIlkApBorA1g6MkWyqEgZ8DE80sRcfJhp8A7dMtwIDJ16NBLiJg48Oq39(LFtXBH9NX7MUJVnN53RdC6QUB3nzZgHZvcOwwH)PUbivAlBl5z4m0F1YL3jHn8Uysp3fAIisIexRdgVgPYrgnnQa9r1Fp(n0vOzGLMsIGP0wdrsJZDw4xodSplV(l4JtaofevQ0YSdeDkKHglv(iArDLq(sI4pMKOoUSet9EhqtQf1uqZl8ysq6zjaFhQVssgB4K9pY4lItwzSrvs6KFnSZ4Noxo7CGyfjeQnA)0ezLpcZbAHpi1GgxGB2AphhBr8uMPxK9UKNRiUDS4jtuaXtnAI3vFLieOksCZS6NiD3llJVaXy4ybTZwN53Y1n6u)xUWLJW(cmXtDbF0Ft87jc(sUjA2TeTyhm6Eio0UM9dY08DBDO7MdQ)SI5Wt6hlaGmEuxJy0)e6NnqHEABWNGYjmlO7(mzFtg1hRlG0TlZiyUwZ55COhQ)py2MH2vq3GZc9HuHqDnBNu3q8rwdkp)z6nVBAeK4JQYfRa5iXaLSfDIizv8Z9UNdRqOPXHYLqUpmC3z6oT6vbTszV6E4QVvrQooS5XzPgszPuOsQlGzBiOpZzM0eoGJIVMZnM(vHTReowARkSLH42Ea3sL53Ih8QbhlMIpqEX58FU6kdxgaiDSyhmT25(G5zhhx(B7orKCWVsPOzBei1gnP41JITQSCQBbdwSYIaS)DKbwe8Gjb6xrdrwcjbEBek9HtDrIvENeM7WS5JcjriGUPvbq4lg5Cb4XCnWCM8eL8B4UROpj7JoTTjGO6auSEa0B0QD5rzg04hBohmcw7y6naA3ByVJYpEXyjqnnm0LecIHCqW635WNJzanz9lQoQ2D98KtYpZClhDxW1BGFtG12kO)hR1PF(VhkNmpaY2FW9bhkENc8obQJ2KHsQc(sJme8c5ZM5J395uZREQW15(qWLyGS2umgwPV3Q0hKg6PfDEAdPugZbRRLtnbhTcCiwoJuOkmy0Z8ikKwcGhs0DjCSzE9ghw(VcFIs3vHscSvjX9FxBzaMG)SrWV4GTEOycTRB52hdrR6l1C7fek3YYrD5lLJTaUgGJRotZLndepmS7DxVJ4NYpFh7QfWmkNBMlaBYeGUwwqh(lzwgsKCVa2he0YSNQJPJHjefMoYGolvjyfg(Ke7m37isUKyASHXbxAa3QgCs)J2LB6uLcT0jUYpVSdYW3r7RBDMYmT8tmw7AdULdNcQT3eGcuEblVxFHO4aAw(xR1zFtZNUoatNOPSVet3zQio9xFNIa0KE)JEauEqDNR1hb42t9(oxVW1FriW3VlegdLd5Di3NOdVavAU0Sv3gaS6xNR7gHe2xsN9UrF9buuFi44fJKEU4.\"}";
			logger.info(httpKey + "登陆ajax.php请求参数:" + json);
			System.out.println(httpKey + "登陆ajax.php请求参数:" + json);
			StringEntity s = new StringEntity(json);
			s.setContentEncoding("utf-8");
			s.setContentType("application/json");
			post.setEntity(s);
			post.setHeader("User-Agent","okhttp/3.10.0");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			post.setHeader( "Proxy-Connection" , "keep-alive" );
			response = httpclient.execute(post);
			back = EntityUtils.toString(response.getEntity(),"utf-8");
			logger.info(httpKey + "ajax登陆返回:" + back);
			System.out.println(httpKey + "ajax登陆返回:" + back);
			obj = JSON.parseObject(back);
			com.alibaba.fastjson.JSONObject data = obj.getJSONObject("data");
			String validate = data.getString("validate");

			if(validate == null){
				logger.info(httpKey + "登录失败，请重试");
				resMap.put("error","重新登陆");
				return resMap;
			}
			post = new HttpPost("https://hoapp.juneyaoair.com/member/login");
			json = "{\"request\":{\"blackBox\":\"eyJvcyI6ImFuZHJvaWQiLCJ2ZXJzaW9uIjoiMy42LjMiLCJwcm9maWxlX3RpbWUiOjMyNSwiaW50ZXJ2YWxfdGltZSI6MywicGFja2FnZXMiOiJvcmcuenl3eC53YnBhbG1zdGFyLndpZGdldG9uZS51ZXgxMTI5Njg3NiIsImNzaWQiOiIxNjE0MjE2NjQ0NDY2NjIyMzY3IiwidG9rZW5faWQiOiJrZ1ZDTTludlJoTkkwaWRKdXNETGhVYnJqellXSGlxUVArUVBHMzNLNTJEeHFhWlZzVWpqQWFWdlI3U01Cdk9uR1pLbEdCODZ5dThDUFVtMkk1OGF6UT09In0\u003d\",\"client_type\":\"native\",\"deviceId\":\"174ccc56-77b8-9f94-0000-0177d6cf2587\",\"geetest_challenge\":\""+challenge+"\",\"geetest_seccode\":\""+validate+"|jordan\",\"geetest_validate\":\""+validate+"\",\"password\":\""+ password +"\",\"pushNum\":\"100d855909e62dbd963\",\"systemInfo\":\"6.3.0,63000,dev,10,BMH-AN20\",\"userName\":\""+username+"\"},\"channelCode\":\"MOBILE\",\"clientVersion\":\"6.3.0\",\"platformInfo\":\"android\"}";
			logger.info(httpKey + "登陆请求参数:" + json);
			s = new StringEntity(json);
			s.setContentEncoding("utf-8");
			s.setContentType("application/json");
			post.setEntity(s);
			post.setHeader("channelCode","MOBILE");
			post.setHeader("clientVersion","6.3.0");
			post.setHeader("platformInfo","android");
			post.setHeader("sign","mEF4DoFnyXPNNpXTKe1reC8C9jW3c5LDR9Ynca8nOoRjI5gTf3rfy4hUeN7kXMBFfkpq4Ihv8W/JYjRhmFURdw==");
			post.setHeader("timeStamp",timeStamp);
			post.setHeader("token","4F7BE0BF1E1867DC63CE131BC842E59B");
			post.setHeader("versionCode","63000");
			post.setHeader("Host","hoapp.juneyaoair.com");
			post.setHeader("User-Agent","okhttp/3.10.0");
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			post.setHeader( "Proxy-Connection" , "keep-alive" );
			post.setConfig(defaultRequestConfig);
			response = httpclient.execute(post);
			back = EntityUtils.toString(response.getEntity(),"utf-8");
			logger.info(httpKey + "登陆返回:" + back);
			obj = JSON.parseObject(back);
			String resultCode = obj.getString("resultCode");
			if ("10001".equals(resultCode)) {
				com.alibaba.fastjson.JSONObject objData = obj.getJSONObject("objData");
				com.alibaba.fastjson.JSONObject responseObj = objData.getJSONObject("memberLoginResponse");
				String id = responseObj.getString("id");
				String memberID = responseObj.getString("memberID");
				String certNumber = responseObj.getString("certNumber");
				String loginKeyInfo = responseObj.getString("loginKeyInfo");
				String token = responseObj.getString("token");
				resMap.put("id",id);
				resMap.put("memberID",memberID);
				resMap.put("certNumber",certNumber);
				resMap.put("loginKeyInfo",loginKeyInfo);
				resMap.put("token",token);
				String cookie = "";
				Header[] headers = response.getAllHeaders();
				for (int i = 0; i < headers.length; i++) {
					if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
						String[] splits = headers[i].getValue().split(";");
						for (String v :splits) {
							if (StringUtils.isNotEmpty(v) && v.contains("JSESSIONID")) {
								cookie = v;
								break;
							}
						}
						if (StringUtils.isNotEmpty(cookie)) {
							break;
						}
					}
				}
				resMap.put("cookie",cookie);
			} else {
				resMap.put("error",obj.getString("resultInfo"));
			}

            return resMap;
        } catch (Exception e) {
            logger.error( "HO登陆error:" , e );
            // resMap.put("error",e.getMessage());
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
    }*/
   private Map<String,String> login(RequestConfig defaultRequestConfig , CloseableHttpClient httpclient , String orderJson ) {
	   CloseableHttpResponse response = null;
	   HttpGet get = null;
	   HttpPost post = null;
	   Map<String,String> resMap = new HashMap<>();
	   try {
		   String[] dlyAccountInfo = dlyAccount.split( ":" );
		   String proxyUser = dlyAccountInfo[0];
		   String proxyPass = dlyAccountInfo[1];
		   JSONObject orderjson = new JSONObject(orderJson);
		   String order_Id = orderjson.getString( "id" );
		   String account = orderjson.getString("account");
		   String mainusername = orderjson.getString( "mainusername" );
		   String httpKey = mainusername + order_Id;
		   String username = "";
		   String password = "";
		   if (account.contains("_") && account.split("_").length == 2) {
			   username = account.split("_")[0];
			   password = account.split("_")[1];
		   }
		   String deviceId= UUID.randomUUID().toString();
				   // "563bc07d-f84e-202e-0000-017833f580a0";
		   System.out.println("设备Id:" + deviceId);
		   String timeStamp = System.currentTimeMillis()+"";
		   get = new HttpGet("https://hoapp.juneyaoair.com/geetest/new/initLogin?deviceId=" + deviceId + "&clientVersion=6.3.0");
		   get.setConfig(defaultRequestConfig);
		   get.setHeader("channelCode","MOBILE");
		   get.setHeader("Accept-Encoding","gzip");
		   get.setHeader("clientVersion","6.3.0");
		   get.setHeader("platformInfo","android");
		   get.setHeader("sign","S6aCMRBQpVR+GUt2bRw58ve8ZrCK2EzKl5j8BR2zL0Zg9TnHJoDGboB6Z2+IJCS5");
		   get.setHeader("timeStamp",timeStamp);
		   get.setHeader("token","www.juneyaoair.com");
		   get.setHeader("versionCode","63000");
		   get.setHeader("Host","hoapp.juneyaoair.com");
		   get.setHeader("User-Agent","okhttp/3.10.0");
		   get.setHeader("Cookie" ,"JSESSIONID=6AED7749621BCBD80704FA34F2436C0D");
		   get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   get.setHeader( "Proxy-Connection" , "keep-alive" );
		   response = // HttpUtils.clientPost(null, get,httpclient,null,httpKey);
				   httpclient.execute(get);
		   String back = EntityUtils.toString(response.getEntity(),"utf-8");
		   logger.info( httpKey + "登陆初始化返回:" + back);
		   System.out.println( httpKey + "登陆初始化返回:" + back);
		   Header[] headers = response.getAllHeaders();
		   String JSESSIONID = "";
		   for (int i = 0; i < headers.length; i++) {
			   if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
				   String[] splits = headers[i].getValue().split(";");
				   for (String v :splits) {
					   if (StringUtils.isNotEmpty(v) && v.contains("JSESSIONID")) {
						   JSESSIONID = v;
						   break;
					   }
				   }
				   if (StringUtils.isNotEmpty(JSESSIONID)) {
					   break;
				   }
			   }
		   }
		   com.alibaba.fastjson.JSONObject obj = JSON.parseObject(back);
		   String challenge = obj.getString("challenge");
		   String gt = obj.getString("gt");
		   String geetestUrl = "https://api.geetest.com/gettype.php?gt=" + gt + "&client_type=android&lang=zh";
		   get = new HttpGet(geetestUrl);
		   get.setConfig(defaultRequestConfig);
		   get.setHeader("Content-Type","application/x-www-form-urlencoded");
		   get.setHeader("Accept-Encoding","gzip");
		   get.setHeader("Host","api.geetest.com");
		   get.setHeader("User-Agent","Dalvik/2.1.0 (Linux; U; Android 11; IN2010 Build/RP1A.201005.001)");
		   get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   get.setHeader( "Proxy-Connection" , "keep-alive" );
		   response =  httpclient.execute(get);
				   // HttpUtils.clientPost(null, get,httpclient,null,httpKey);
		   back = EntityUtils.toString(response.getEntity(),"utf-8");
		   logger.info( httpKey + "type返回:" + back);


		   get = new HttpGet("https://api.geetest.com/get.php?gt="+gt+"&challenge="+challenge+"&client_type=android&lang=zh&client_type=android&w=j4vTSXbpl5TP)Su3kVHw0w..668B2E37559B9982BEFDD9DDB53D08A521152A3EA33B42E5194EB22999951D3E2B5B08A349095F0046AF232FC5CAD94D8E1D540F71FBE714225E58769032E2C8C932C47FCC72AEB7599C7681DF8CBD19454943ECFE71CD7E26349183165083BB9126852A1DF9F1438697B2D41FB32DDD144624916139C50E74ED145B486D8631");
		   get.setConfig(defaultRequestConfig);
		   get.setHeader("Content-Type","application/x-www-form-urlencoded");
		   get.setHeader("Accept-Encoding","gzip");
		   get.setHeader("Host","api.geetest.com");
		   get.setHeader("User-Agent","Dalvik/2.1.0 (Linux; U; Android 11; IN2010 Build/RP1A.201005.001)");
		   get.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   get.setHeader( "Proxy-Connection" , "keep-alive" );
		   response = httpclient.execute(get);
		   back = EntityUtils.toString(response.getEntity(),"utf-8");
		   logger.info(httpKey + "登陆api请求结果:" + back);
		   post = new HttpPost("https://api.geetest.com/ajax.php?gt="+gt+"&challenge="+challenge+"&client_type=android&lang=zh");
		   post.setConfig(defaultRequestConfig);
		   post.setHeader("Host","api.geetest.com");
		   String json = "{\"gt\":\""+gt+"\",\"challenge\":\""+challenge+"\",\"client_type\":\"android\",\"w\":\"gNsqj)3s1rvCBrEjJoTKmnGvtAc9zTIgGVyujkk2upQIzDt4uSONNSPR(2K3aaGtA1mMKsCZthuCtphBu8iXE71iRpu9CY5(nZ)XGD3jkh6jPQrPEgZINY9a0uwR4iQdNtyHvsDTA4CvJudSlcCgZhwK5Yv9Q8NwKah9zuwhOzAoa0j)yCQVcWCuArL5WTW5AKOlV273DeGkvI7QB8zDX9Nx1DeDeywEkbCz1pV)GPcY4LIBf5Jcw5z3VPRz9bVBu86LtHJWxcWZDgCqq5XoNyr1se)zTR9CUDHRNuWXAkHfaeD(CFXn6yhHkruQMzo3abFiSPM8gnwK3gEffVxie6JCZP7PfIHqnVNX5rc8zSA0pL9Mxu8P3q4jd9361IvVFYkzU7b3dqm3(yxaKj1gC1i4LwZjpjqeEX95StlArl(JzAbedAb9SAMbdZw47TG1ALve0ZDP2G0brYZaZld6MvZd3oqtqAgxZwraiZk9nhb0vO)8RkxZJFCm)O2ezE4hJQawbfjqGP0Vr0dIU2NkObw2oR)nADVMSAB9)YTuMwRPNe6ZqVfOlo0jDAMc6)ADoKeBXPGhNw48ed7Gd41myKLxepnsdqV0LLvI3GWUHw6woYeGT6xRGWEM0s9YiZGv7MBtSEKZgWYY7GgeBSmoKp3wAsXEjJsopBloO5m8rcbXsLRjB)Q)cjdTwnUbIIidKfvPKd)TlSpsqHgGqvzdFbOYLsM3eFOncdg(OJUjjnh5gzxRvL75HnUAeQNJ0s4M)KjoRD88qs9BGmiLBuGlVWCcoRVftm3FzQLZrqb1C4UA2Fo2zUER9MqbShir3MBIrqYM6sljz8PTI1OBBEa37)LCrNve86EyygeD3K7OwzicQMONLBJ54BZXE4SE2KfD5hCaRCRXjmXxo3c3eq1tegbP5h3eoMzICqanoEHmqsdrBRtu9cPsHE7fsgRbaWR(fcxMRzabmxVnn8jjEQEptZVGlUnTFibJZddRQ50CgxeJg1tw6UxI6wLgv5xWBRUyWmdSB3al8hS74PLTUrLyYAUszmWNsN2uXqncfKWI3PRCkn3AIpuzSc8zvTtXvE8oWc(L2Y3Eh4xlDW8XGtOrya4ru0dVc4MfDYj1sT56(APiAFL24Dl0u7L1H2JddT8kKUBL5EKO0S55(1PkY4y3lUazD5ErWLEHEES5Ara9TFlOHrxxsdiG403)7jBYhaqSFhfI4M4ItB9YlMqlgfoKctKodwICSgt5OWM7KHGGr11S(x60tCJPdxke(d7DpTZ(WGq8xBTtntqye8F)jVut4GGPy(RAF5KKQHt9D9r((Li9qhWcOvJCmh0JIxsRBJSv9hpG)esrCDrNyHQtbCHhKJhZ4zkNwI67ClH60WAvMON7gjXje)x3vmzUsS7foZ1AuA6rNqyV4LsGuC3NgD)CYUXN52I3hTdLNklsQaRGQgeLYb0bEsh6lSrwieZAEyNfzKo8GAn1M2aoPakzmwFbM9rraGu84UC59PQnjHzZ6kgt9ZX9HNsmTJn43Sr13i)PUs)27r19zoZs4)PFXvYJ3GiowQidz2xdx3)VCJzNQ0q5Hf3IPisuDrwLHhQH5KzTANaeIptFzjXz5fYIwzFoVIgoDr1Vmsd4kfBMbQsphR1LLvOxxR3To7Z9Yw61DPU)MLrh9qqExcrTCD6(zEcPk76sVvmHrPxI6qb4psZYb)G5YpyXfrGgpXLameIUdeqYUQDnMroe4x54I7z5TIMt0)jBzF2edQVk8F7SkXfDpSn4fKtoOe4YbnEZlPF60iOXTdHvaX0dyK(anlJDhCW5I3v0ZXjEK3p4rc1rwhfgPGNhqpn)t6mEyxYlLayW1TyE7oOV5tjYxLCx2P7X7Umzre(ehBtFIlCXLHuKstBGnAh7r4sX4i9COG7VRzXRy42NCASx0Y7DaLN7DwumdoUDg2lbaDy4kUpVUQYdM6FO5GGMnRqocIHd41xEL0k7JqObFli9vx9SSOecaVT9jiJY9XZ)DNv41WdRbGJCArZ0SpAjCKDMkX2RjYVEPCeqvJA7eMKRJTKcNizLL4guqoW6VTsLpFRpapTzv(fHNeRDThqVVGd6EO1BHd6xNsqFRrQ(ET1EeBHuMmy6e3BJ29REp9(T5YMCJq7nMp5KBqUpMg0C7HDTYDI6AkqTMSCChKZ5xFIZjPbsYChWdNlNi2kBhLjyBJ(NuhVbdB6F(DPpHMxiYWf(sz(vCyOZcgxl4cDQUiaWAkqipCAdMcVy0o0ckI5M8J)vjXzcY4yWxxTaxekk7yD1OIoCXULMvijlIxGNIQQs7WHqByK3Vql4wevxuS79N8LoHWu(XL0aGZ3bZcUrqCQJbIqSLs)fOebG1rZGgeGkvZrR0oQVGzn6E5)ek2IV4IJevmTZNauuWNJHTQOFwGihvzopYx)9txyUgqDFbNI9i99CNgWybWVtUT7zJyF8Mm1aYolDQG9XRcKg6YktB0UtOO4KI0A(HeQt98sH8Gk5fDuIFefcDy5yfwnYFc1caEOAVd97LehRbJQT4ivnr257tp2AUJguBj(FXnhhLNTp(v94dgOatpCYCl3P7lwJSnhf71mGAJpKsLVXTaM26mNRQXwz)Yckv7LsRikGNrOfTGiCyyuxcV3XVHb7mczDBZWzJ3NAnBqRrwqSo7gOaco7V1r6RXg8in28883c4PQ(3w6gaFMThFjXVxTQZIv)yZjnCXSEhfURcb68)8sxNEFzthcbE8p6vXzSDs0MV58Jp(v)30rhurMQiZXFedTe(q0(b7NKsT0mC7I6o1jQqlWTrsoQTjEiNZ2gGoXNPXMIKIPNFZ8Yd35F31MyiPh4p3yWlAjNcixePd6oLMTy)DF8EXnzm723ig4WxEN)Df)5625Q1UYBB3VQzm80wejknpVDHkCZs7IWTWVK)31N3ACIYRFvOrUlkwDHmvqSCFUfdho4xG)ey(yDXtF1HYPEJ8lQMK6eDO10xYM2Cq25drTORlkIZcC1Csj2e2xEKy)GrswAqiNujbH7(Rm(cgv)JMbdH8zoHwWKLu2RfcMJe7r2xf3uQ9E1yhgbzsnKQC4b6ytkB5izlJNvmGElY5V6Zx7uL(AVu1wmNXgKc9N2u7qm3t0Sh(Q)SxntPd1MKL)LITDOkulx3a5Gg4LMlBqvQS5ULCfL(U02UgAALRmwt9yjnfjrnaVmRPUBpoIYdFQI1FXLJqoEHx72U5V)lLKN69O(iw1NSzSdweGDPEvJXdq2LTvD1JpzikRX68q6PxhQX5Rf8gtZUpsVF2sOnJ1kNTSl0ewJ)pKRquWVG5WBLyKjk2cX0PuBW1YDdBhTtCOwPJjwF3K3bjtBcCg3HXFWRG0wtr6qCD2xlj6PKPspCjDLokctJfhiaU2eQ9rbMarTMDS5BXc8iwe44DBGiVz(fzPcF5UWSeFTka9te)yWRFrXWclS5gkItgOif4cTgJMHF2QwqwAtq7muHC4ZpxY7fIfzz3nKnCdWSvkk)f6pL)uGKf7V9vWIzFSkMtr)qwdMbuPwBTs(DIX(fvvTKb2IhB2IustxOgpOyTgouVE0L36tf1S48GStb(6VtJTzLvZ6qDSbe6bGaX(70QeamVTRLQVM76uVDcPqyvgleywKeemwIEAYyJzxgsUcy(B0emPzjB5DTT1YwNDcGihxUcEwAi2HpMpitpu5vzYeFY9eHXYRnslsy9855v8cOXdp2tCtEwvlmtBAyh1HRd4D()VKSuvj4nEpjDZsnoM(M0R5j8H4vQLm)Mffnn)H)4vMWD2)FQEbEQMaT1KWUz00VxqVw7RVAjr)t9TM6fmLGUAGoqhWhZ9iXF)tF76gcRx7gtyHFe7jDbKPvQCNxrJxSW(qa4QXKXUv48NrWLzISM5uPb4LrCZojdr4z(gKPD3n0s(0n875K79reX5R4rgKKMs4iRT)bHSCJn4m(CKAx1Vj7YvqAOt8ObSz45C)rg2cMhuND(6V2)r8xO2j)hBgCGJyj8OKMjXo)K8KosCW8IgjCQ4a5Veqe1N6T0jg8CXLAhLVateMEoukSTdUlYlEDo3xJAGEJIBbtUeIMKkj1BjzLfNEZItCMNrb6QcQ2vL51s(fEFRNzmXIYECFH9qCBsqmuyLT(UIWVGlbgMlowEGoWuR5ZRdiAKGJM(R46Mr5JnXh5ed4g94d(QhHXryiQIJHJG7QQyhFDCDNDrlHcCodNwrQERBoMHmQazQj9VPpXkUnKCOzx6mknWVrFg(d7MIrQ32Cro48I7P0RXV)ek8nMfngiUYBHmsNoq4jDX3mob4Jqw77O8Qro1EwtEqQ8LnYwE8zCwB6sznrweLb6qiJ4oQ0WprBDOyddx2cOJ9y6oRYWLeAImlwMh958zDtFLfOkuLwknHYkG6aw0FHTchXn22JdwJwylxKlaUCVHVfDWm4We4g3r3YnKIIjPOTBb3yMbvD(eyLmrwzF2bWjpeHl)vGdvxvjFBF9d)1TO5L4Fk1C9u11w(d6dD2c72GEhV89U82o(raADHLrzK6m03oxyuRxXyqL1MlMfAtQDU94Q6G9yltJi7hRjdf0ZO1e8XIKhhHqpu2J8joydtTIle739OQQOwnjxBzVZd4XgcrHo2ZOCmPAKcEXF7CDlUoW78renVnGtTvnbKxHDEVZM3JlLFfVOz3bifxwXZ0LgPlDZuE)NmafkqTlAmLm1Dcrfm03m(KqrjnqM5159PMWTu44djbESEdDWuvUcWfz70Yj8gyt5ih2CNBKkX)Y7sHA0Q1q58UeL5N5xZwZjj5VCIGV2UE(wTxJqRdSEsMghGZF1AJYMq8Z9XD8IvcELaa2GykqBCWGbFGZ0wseom)Ls5hGJYacVT6UoU(QRC)hBSH9PHZ5U36F8ER1(2B8DTVEaUIQ4IUvlRQxa39RQZIF4r02LyzBjXOJWze6KCP()7xpbtbziJbT5takVaqmiLsRVIFuBKpcfEuHPwj)e0E6rjbQc)(50gwzE4FjwNTa3Ju7S)kHthPaztCv4JgIg0(k2Rqu9ktZs7zsqfQnKOCiFPCVNOtoAlLGA5TbqM8LrufzeroPek)amWNR)VkzCh4)f8GS4MqHbaN)rsPA8vMONJkTLyJuQaB7r0Z3MuykleHW1dFWQ5N)aRV(DkLAZ9zFh2KVF8yDEgRyMnQ2XBxRTA8QABuPasVnFsmyPepv2gFQfQRTJamYYOhAONGvD24rP98IRKjIW4iS0pwyvnFfUsitDrQb3WrsfGjydo0FMcU)Pmzt2jBOutsPOECZIx5GsSNg1DpPmN6P6laZaqmwN7EUXlgzTNxPPEl0V6Yne7wj0iV1pIHYEKYz)CnkF7yx0gK2dX9dXs3(aBlTcWKOzctGoVMu4NvHT675tRrVFCO1MRs(FBITFw7wg(nDyI)T(jDChOiNyI)(dRUBZKFVWQ(NS34wDe3sUZubqOOYy)D7yi0cZ3Ubk1W9kmmIP3g0VYpihbH83IgLLfFSsQG7iH32J454z9J6dvoqL6uI8oUz089xbO(a(xHg0AsXQ2x2j2Auv9kMT0I)Sv31Utozq0HS63)42XiHHUgEq1gLR1SKoEbdJ3hGnkbACqVHofykHQWxJxCCKPjoNvNSFMDCrPK(hQXxw4I3(nVer5QphbI6awnu)D6GDCaEZF67Av9yGNvUPSmqMREz0p90XBJ6KgdynTGUExcp(jlnC5cnsucR6alSLzbAQo6cFAXESTJ4ewpcjGS5cLNGiBbzt8HLH6m0XoDQzoQ)nBrvikw85(DA6hQCn4rIgTgclurbAm(rSl6QUIcc3jeepZ2vryqqMy6JZh7czG8laGxsw4)Hrfr2kSPZzcGRbrhyb2O)M4j(d2MKhB4VuW((HSjVXYHF1c5vQhy5w(cYjv9j(AW2kcgCdwnrpdLATuf(cTlT40ZjIPwk6v4LOBQyBkiqmJ1LLTb4lKGxh8f9byWMQJGFlkT(eXClOy5RjqQ3y8Po0iSItZfaInN8dEskOLrBo2SOpxdEklInrmJOxTQN5TFViYYrhLINoIlAImet5(Bc5t11j6mKvxoKALpirCDm2z)plCCeA2WxtMPI1YBkkIoeGuzojy54kH5(m2fvHaBzQuIc4faibacDreRUdjltlWEeN9Zs195RZyS8YrRxbMVobtOJivZp1TwC1UODlsYXsIG(1QvmfOP2A5OnEGrS80Jjwa86XKD5yJ7O9eawGhKlAnqcRQ6AGS))a1w1Xha8rM9YZnjVb7NiQ8nWdTWPdAgGLOfc9N7vBAxFY38uIc0K8TvyVh4x1XG2H4ydL4B3Epkmv8b0UhV7DZF(FF6qH(ZdObJQC5CpMtETXq2y4HaUnAQX3yd16VGhvuu3ve8pW0wO)FjY1VQAV53DVyNGhVpBj3ZfXoMVqKGWuXhU2Ensa2yJE5YpTuONz4pwxEfdYZrI4EwuLaZlk73l1FymPfTP3NUoThdK20Qoc3GCWtF64FiL3RT9z8cEGvoSMYPV77fXMWGt81xKKR14RA2xQTNRlMLg)4wXgGWEJnA5yFGs5QcX8nixAQV32(amSM(iTRvMeKQvvLr4Pe3DVP3oGdSRQ6EHIuo7mPEUbtQgHtSYlriWPb7KJr2wdfRRZB71r0xUi2(uwGHtu1KjzKinvuMOfWXs5kJRUL2fqDNWHzZuMHJniQzKLJGOENeSXukuvkKkgBTmjwYOmxfyq0FnCu6pGd3xPRnjp5PJ35pfVWLwoSDLhiDr4tUM3vBvad28kzTY8g0WaCu8TZH)vMJE4wh(HVyHXX9T045i4qnSxgnlxhk7G0cpDR6VRCTyLnRwjWVyLOkFPcN7mjBM2Ir(C2UxF99957v)UunZTJo7X0pGW3jvL0vXg(J0Lh7B74x0DAdqiUza0YQDOsDumg3IoqR1nKl8KA7ZnX9C6y8Cl3ii28QmHCmZfLQ850t3L8gK4yNL1WnnrGBkXFbMx2HSSzAQn6aYQTIsfMrJdQ9vh3j4Qro8dtQT5(Hi6BA(DL3yb0GfLB71(yhnSq68W2XqzfMX7j89g3mixtWbnq5d6RKcXovhUOrIqMI42bh8qWA(W)zWhez3zEBp7YZysF6eCDHJbHRhY)pwRab5ho29KoYiyPDr2o5IQk4f(J)b798sTA0u59)6fkGBWcHr(0cu1o)DlYRJZAYIl1oqpSUtXSBPM8IrvOJOCPFMg2U1b32tH6zRxmPGdx2ujK7z5irabD3pGPDDr3QKC84n6We)Q94l6JInuStSqv1YLc1fywmqh10fOlpqVmX3(7LoAgq4JDCLjAXRHxeJX1eV6VR7oNeo0dXAYQe3vw)FIcyFdiC5j02PnPyCxyYrgLooKQjEHB4GB0jSkGgVTZULSdDgEjq(URuBYGpSzq1gS1J99qkHSUr2(KUDn2I4BIijKJ8h495181uZS(mqeQNHKYToZIW7(Pq35gMN3hKxym8VQ8FzAI54Xrb4mYUoCCNz5VDfDEs8yE8pKDvOO63fw9S4tCRhLpsfxj(hI6CsSRDfYIqUYBec6vAgdSSITbJJUSSaDaZtOjxm8wAUC7kSJO3jkcEH5QWvKOBHpBSDgUqyc2PskCf51wZkA8p9qvu1BKNJnq6GxObIX3fYv)6y6H3moZek2bFXeUSeb3kbdiR((jqKIJ8NJluH6eFt5TEjMwG2JsRYEI2Qdr19XG7P6f1NBHsVxM9IWfHZ5SqPpwBdOZz0XThZCOJiZ9CsbpZcQxMK2b94lUQFW)j02WzJR3wVHP0z1SP4(2wJilXdpgxVdEOUlVzr7A6GaHD1BxqZyuQlRdoxeVcJ1(sDv(EokdHn9NBsMkvATf7L0d5Y77swPOP0qxnrIthyHTdeZhcOtPOaScqwG2bOQAi9r6LDekSh7Re)swVQY1uwlygQZMzEZNqLQBRkiV13NviiqzRHEfart0(XwWAeO3XmpOwtHGQduV)MovEuegi5T91iV4iQn(XPhBwq6uBntKDhpbKZGxx500nB7gtbU8DU0lKMXPx4yyAxCkccDViwuvNK0CoT1b5uynN1UnXuKlBDE5YGqZNX0oxqR75B67vqGFk9By(vEc1HWzCjGVHrkDeKIlKC19m5xHJuTUiSCD(DEVEBH(tRwTdcdRVGcOhL8GDyStrCObPLkhx5osH)St(rmNU2pNZjwDLEdxJQfq(81qCwG2bOQeyMCWdFWTX5q)sIkOu1DMW9FkL8kR69(r5brvSDIXxsTZzrIzz1xjbijb3iClLJN0rQFj2oe3mTlUp304iv3zVHcYwxRyq237OUHWsXvEn(FiAPT9cchKpVDBdlegPTbT2Is1Bv7qZ0PvcKUmHKakBMfRpXyXs6dv49rPNw60zsrzunAqoyJPnRr55BNdxGcMF82bI(EcDWGmbK9GnvWibPsT4lWXvcm2NvUn8cRv96(jhIdpJZVjOXfdMl99H6AMIWrRdAKWUeIFLvEdFjsu(g7t9GdrBeWSUJX8awymlms4x12RS8ZjMSrxZ8ak3jR6Sp7yhg8jfcsmd8503VliyrBuzT5IQey1eMVpDw1PsrU5G15GZGZmmUlaxqAuy8gKVPx(9EN4aE((QfO2yMG0k8xjmCidoXoHjYg6LfQTzB738SDXqKofcYTZrYB8Rqh0vveuBev9qNIvBsnhXc9ZBuuDvmvqXyNPEqfV)oikZ807Lw7FCltR22)a5p8995QWzRznEj10Wuto76Yv62jYgQrXYuolmhzylTBHSh3wuzc88Ywupv)LVg4)mDn1OyvnR(hgfeNpfomXwgG1p3rXCHhLB5HVHiUJmljDQ94DWkUrl2GcqikF4cSPQ0JXl0AS8fbrr)p2R8ASoc9taiGOWwmlxTO0IwGWqrtareknKDjbtsly7ltSF3UMsXxFkp1Jz3QF9(HPx1XLRfqhcTC6czMov)AzmGTZeu4x8NHdMuqdlESAj4gMpftGTH1so73jehubAU4cc857fARVw9)XXCO3CGgwoEWfU(TutoAM(VJosNp837YzmNx2mtpLFVqHLJIs4csjNLWdpwsplIGdVmLg10NJKdzAZ(wLX2Ve)3w9BkfhiPsBHKbYcEyj0nVC7EM5G39qxegxVR1arlYJFxu9iCdCG5zxZNoWjEmMEXQ4C3YjpdHOn5)UyVRpKLA85ZeaEl3TmdvHYinsoIjHd(uUS2G7zYUj2n6wkEB09YUQ9hzbCpKa(zjTL6IeRPdg0LKbET)kBdsyY3rvzEcKEvolg9VLI4dSXuAekK72UfDSm92iu3RP6Lj1NIL5velrghwLp1qnOrCYmhjW((HodwxQeBZ(FlVrL2)wh3gwVA2kAv7Xu5tQcZ8ov0rEs)e8tCBh9uP)88z4mTSRc8Js52G1L7UPuO0OvmGJGsnBQrKWPwZwe8QGMaVYk8YT1TslyuwXVGpspGdh2NR3oJYHwyeuXYBR5kvN6vXgzl(iN4OHlijmJd99zNhRm3CiP(7WtOUjnQHZxXr1TIbRxWZnrP(EKjo3kZURnkOmRu0j5Sf)NJC6YUZpuLDVQPWniNd(fmCvc(w336Lne4wlMjf9ZP8g5BxMN6dYK04O8qzUUvUfiwtEGujJslMRxRfskDoq8NaysljHR3XJzkVxFmCMFmmJ2sFsUEmyyAf3u4tR5X9nIdLc7PmkvdyY(FuJzUfh(YyfKrbk3pHwjbBgWKfsOyc2eL4f2u6XtJv2N7HKkioZrp1ZkQa5JVe7VNXP1y72koWBXf(4(inACk9uP0jlUO)PoC2Ai5gopiqT8N)zoc)p1TGldu(TayE1mozWCDd53lqsjkuOufQziCMtHOYzdAClakoqO8hhXxpSejNZTGKuKwrlILHqM5sl2wJVF2TI4VzqAF21MxXTTLEhEnU3cOrfLJkF1D0uvp1ttlflV1A9pjv943kMcG(MY8gCsB(rag)caYdPSHneSdJejFwQsAjcOBKXocoJ4EB6W8gOhlth)gPj0vvUKd9)do829AcSoxgJhZ8jrj1tr60UZkGHPpNKxt71PPUd5k4BfZYLYiTqaoiBeqqPcQML7(xsjRw3R5uXm9jdmR98j2f0IiIrPpZXqS54DWKkEcUkhirMmE5b0AMLylhXFw)kb2VhNgHQPlxETS3zSC7ixcgQvZsSBhNXUVVPrGb82bUJQcz591NbIGQDCPGh3bU0iYuX6PbxkuKAZ6(eXVFETHO4zD57Sv9hNmAbsrtFyxUEVnrh1KqTanSIjBctqFm3gnnE7)a(DXyUYF5DbdYNyXRjt9xO7i1zhNRIDMS2aT3O1ay1KBUukC8dLk5CxRqqvmLgmP65XsOMpkhEKi0N2u(GDEe1RtsyXVTpLkFUKOm7q4km3vMdrBMx2BILoxkaozPSntrjFlUEGP9RruNnGNnyEuGaTPjoMYSM259ebiidfiadteckMzfo3eY8gdILmrMbV8KnRxtH67CVNMs3SOFbF7aRLnviIfqyjU324wMeyaN1426fxzuPJUTwMFincQDy)5XebnbcwgQd8eemueUFnQOrXLkQltUMj2yJYWGX7fa1UrLX7isqy8YTd02)uS6oK4oyEuQSCkg0nvFD4k1gjJhi2JtyaLHBHDpMdqpSyRSqzf0RkenEnYJiLYd0ybfbARatiZfnD8WXEMauDVTXRoc7PvymhPd4H2LQTO8Hn9QReikC48eiMkY0SNLie(W3J8bRKeEn)ywUPyV6cQaphdRO)CkStlO(5d0RrvRKniYJLWr7b6I6alncbTTWrSQVFBKQyd(zd2C3zdbetFOKouudJr8AsY8t590olvBdaaCgI9PztcV)uZqT064e6DpeZcejLCBsEhlhhoi4BptS61XsmWwId2V)4CqQ89O7QRxWOmHZrRwHryhVRyLoeK11BTOHSdHoSg534kP9YR3YCOnJRuq5qmzyKGioSNQ(w3TM6KtPtcmaS)NxKyE1HUc8quCsRK97BEgJSNF8j1rbWk2oVJpDPUrr16z(W(R4A8Y8y846FmyKPCI8SDsecQVEJ(Z4RY)i4TYvjWTTe1G(THhw8sANl5ZOTdAaP6DcGj2IcYJEmzju514f(ZjZcQElJI4R0xYXpBJKUhH549(Hq4Beg2rnG20ewlk6m74isu0ldLrV4aJByB6Q)OJNWC3yxJH7yDb7MGcGfbGMcfFnFuoT)AZcx1IVbhuqK)E2juBhnIYwmY7R4GYdXBB9Yc6pfnXTF3ASGJ(OSpJwoG1zKsOs2fKq38mfoel1yh9KhS64WCtmRdu2BB5mlteLyJhiabHGxdEwY9h3i4(jLPrxYnKVchJt64RK76gRLM)9LTo9J2owgKvYCG14Crl8Vfa1YXU0tpWct5W7R8SUkwWLs(LKI0YA2nwansg7xjtRN1AqCzebwrEC4C24fJQKoBDtRQpxrTMNrw51pVs)lCUwl(TtRYlPo6nnwXLK9RjqQrHEuectDNa9qufCQEHGgk)ezs(IEI2Eex59m7dkDifmXU6HGrfCXcOOVHVN(U(KE6m8QYZM7ANmMaF6NbBHEPntUyXNo5hNSC3iX3N))kmBU2Yn)rjKdUOV57tYFzN7uLBFtUOn9lwfeCeUdAb5sUGBsx45eCuEt6oqjgALiLtAB8PbqL2CKwq1JxTfdWlKzKWe9qOnJRnU)Ka0NKWJNe8E7sepeB(93(Z5NfiogK(KWTJWUSOfa6n5gPBycNf7Lz7Huf9R4UcJrzVAqAtIG6GAlkd69J2TvMocd90IPdkomVOjw99fOGY2eIWlxRvHzPX75mvMkK3uWU)mGZi4zzLWybmGlw9luweAgXQ0W2AI8Jw0T64OO6v9eZZjZNIgiFYLFNxZf9ExnHZEXkjhka1aX5tHZnTr4M)lwiK1NC(lDudtDVeJBdpdWs8NZuBFCoyXLQOBuag8yRSREwROxr6M91dNQo0gDLRm7()3LMFMrSbUjX4rw9t(SAl3Vh2v9F1z)NKIuzN)RveRNWMJ9sIP)IOXuTiwICttNXJeK(T)MXb(alIPAw7kgvxQBINi94vnfAYyGTHyFGYwsjLEJ(W(FFP7mpm(lTWpx1VriNrkx5qh3hq7Ds8BHJ)1UYGgnzotiLj)B1jlYM08(6k1lIFdutTjJ92cTDijGHWlNWfoBw2S2Ynb0zkJJMLhac49nnalV1BSstZD3LDm61gkTTuFOR9jPj)A7KYEal8D1oJ9AddWS4vm1fJyAEc7h6L)Auki639NzFKBqCKF27CO5LcoFO7bF0C3yDVKdg1Z(bJ5pTkznSvCnnlE8WghXQ983V66huUlQSAv4Bg)lLZl)d28mEKFwfR(vvij)enbANESTKTPJhduUoQlBHaoSJmKSat1bTlp0asrj2Psw(MpPBBhlEfP0OqYPPY0QyJ1fWXQ1ZHY6OtcPB6ImFwTS70Q85TZsDvhgnv0Pp11j65jtogv2oPKcsHvLA5leIOFOEXDJeT4hfi9ySv7gzujc9FGXSDuIVlLLbnDAkhUiIymbi64GHb5NmrmcNfn1katom02mOuMMmrkczUs7yPWpjqSYMkyHMntc3wRiN16JdPAScgp04sAEGHpOviWpPimMnSiFkg64DZe731o2uySfVDDWaar31HR0Sr7aERmoBtGFc2ZKbTBEEiq4z6Xi5JBLiW(O74h70dNAQHy1kr4vhXTaYUtuvRgPy4zI0J940a8yt89RmLTulcfD)mgo814gxZ042fkDlo6zrkdRwY5aKbjIcSP)(qBQrcik5de4AK4MhfFnycc3FLp)HRwJ)lv43aYzV4eXuKOICtaPzBOPdYZ42hqijq47PjCJ9l24wuUFiocC)B7P3(io4TD7GLbkd9Hf9ou7ewd9OJG3o2YBsb4GTjgXQ1(J9Hph8jODqGeq9rWEyle0Ks4l0nIzyRMEsJ0O2I1rS(ODYJp5ZKsP1XS)e6pusA1kek)d2JL2wVNL34tc)z)JH3dzaK7LLXcWhesxSSmCPukVyWMeC(26E9qZrLyvda7qViQK50eXSUTLu)n12UsWPFd4NP7UsopDOp1)GKM1cnvjuS1P3rBZ4OB(6X4ovqYbCNdfBjeSGgjeYeerBgxV(538sAqT4cPQOJQU76DxqCqO)VZUEyKOenw1TCXQvQJDGNz0QFAZVX5x)422XdQtyh)anj3SjZx1Gmjg7ummim14hhUVX7PNB469U9lVZntGLeT)NFNm)vvisyZZjfjZw2Dy2m869(N7TMfppRgzbol0ppUMPXNjpGZWvnWwhsNqLxEdznvbMV(EaA7tdCuHSY3ro)5UFDzYMxahsTRnrabnGy4LAFhfz21X2pEfvY5fVLixbqA8dFfD01Z2JdOtqoS7s1Gb1oDYopbWWRkCNlklFVOQ5u9lgfD3eZXCpDlxLEa9IKoLpYwEXEhnfPOHsgEjYDXm6HlJzoaUgt8t(HoyOgqBJmZYGzuR5FrdAsAejmIQcfvHFE9vtJR5GsFxb4qLa7UZlM7Pq)UGJjcFUJN5GCHJrKUhCw5odK(biHZA2V73)IsC(wy5J((dcc3WMLw520VWL9BaG93gWwBrI1E7jNo(KMXDMAIUnFmqJTIWR5EKkXdtotL6CvwDPnb4LEj9B5Rb36MwMsPeufKLYr7f6uF5t6mj)meeC2kJs1YK5UP5MVh2)sv9SLUAw0wBOLSBNfC6jsOojVisz354(OOM1Vvt5mwx(SHXpCMcZ7S0yZeNE5)HSEoPjefgv9)alqOiW97N1R4iW9tlRaUnFRuqVIcYI4ACz9YoGUKgZrVRiVdkp(fZfltwUX9WmwSCPnKh5bLVjJunZm5(UDjigh0vqUQcJkSSw9axEiDzB4uQkeFScm2hr2008f4MlBAutmd6fzEt(45US0ym1XbSIcZXLyqMARmw1gMOnf73b2xFxVEIMdfNlSWbwc53wEVpJjWQgVKRt(E0HvDOpyL0fzPHheoh9)YeE42UnWWPUMnOCDsRrsE2hcjUSeX8XOKS)Hwg2GdFLWxEvItLOU7cJYlrtgsp)l0WxOqQdaFGeEqpHnjtIj4FqKzbkGsOmUobU)2tIxLXTiNHXvKwKWbWyrrZuhWuHOGJinT04eyzhbLezovr(psea)(cMnJb4cG1Mjh)4h16g44micQPC2pRRe6XudrN7DEH(9fhTwo6Y6lE()55mk1JvWVtbKuMzPK2W6QTE(BPGZNgPtl0TaHhLiu(Lo0qv36G6(Lx9qoYfQnWUbMyW6Q2ejHP9uE1Q3hUsCjhYQchVVktoByXftz5xFFCrZAuEIOwTebxzKn)UE)c32OHIFNCGJU1d)0X045PDSh546tUXkYqOpSd2esSlGqD8CExUef6jPE8xaInGnaFycZVlshKMCg(lAD0OZYWp(BuYn0xZY1KwIfNCphyZaBr76RMoGaNVUISNx1fAnWEev0eDU8gTspe6hT7aao01yVuygkCjEYKjOWKB0F)Te8KOZPJu2fkb2dYdDZ1Yy57kC3M1lO5U)uiIviO7Dpxfcx9LCTcuda3zk6h)cmuFP(rsWF9q2GOE5jmyunp)BndRucP5B64lNpnZkXoStHEMt34yzwRj5QDDFEWRb(BphEYMEGfOqi8ByoEqg1R0btainRxdO3Gk3Qw)8yV2kRxlwrKSuYFIES849Lr8j8abUV5ap4ilaqrPJ8WKrq1bQnfCbkdPqblfNV8dfzqT(EK1Qrw41M240JaUNb9jzbfLks3acygaKWa2MdiHxfOWAEetS(DQk0NwSfM)m64ZoxBaBIfHBEA8yMM5ZTFoZf(MgNPjhAdBIs0xPfD(QsMVF9gipxiRxoB9ADmhQU0Q4PDhjmWlr8nNbUjqDNUnlyON0XyRDfxKcbiAEslPr(5fF1QWSfSPZvmEMVagJji578N(PPeoXzwu)qhktMdrucGmoBVpd7sBtmHDsSjBO2FX7Hq82tVzvEBkrhVyy)3cnz34PA(84mJMh8DL8)rZoUaTZ1chC4ODDgZupXQxX)2DWfVMfIjywFicZ3yaU2)EKgDRHBXSPhRjadv6yEb87NUtYmzH7d0VipUnMMKHnchJWCG1TYTPJLAXklJO35TtIAetpV9oxmJTyOodHd867cVQgIS4aB4z1FVFcAePYq8a44qoTMoacYnBC)DnLehWiQ3Ck0fXxqgjYXOEKycQKU3I(zpLDw)ksDcqfFVh1r(II(UAiScHlcAsw8gpu1WZqsd91VKPfTF)Fe0lsVs2OQghptIjcEqYjqeZK9GROXm7poPy4qA4lB3OJkMJtl3tspyoPf8fhFAVndIYkImeclv(ZETI6f0BThIAuZKgFbcskgRjmYqBxzSI0x6t2T6FBpeSaTHe8Q1VfsKEQVW3VQhux)jakqQvUHP6sa(nPouf6zrgzhm0Wf723DSj4b7lKCbvW6WJ)y8m3RKT4yUaTaCDcR7hivH2MphJotvcyLbmd67F8OLhxrPoZwPiEGLVk6TklQAYes5Y1)CrwEjjRRHgF1A(eFrYp)d8Syvfl6Y0pnzSvUhlhDGekSV2a3jUdA5hD28lH5haSWYxqunFyJdlj889N8wWA7Gjgb5YnJdS8mARCV)l9XvE0xPgQSBSptaZ5uNhh9R4TPWrVnepH4vdqXa5UyIoebyAeF1CBKCA3mKd5DJ2oNK89ySuFYKrgSIOBGPY)Q2wvqnOMVuwEzzTYwatjbZCDJm6lg(s)hIcH1h7MKhNBYWR6NLtJGY28M5MpCfl21v6XoMBcBDZF5Wq10ckXf6vrWmKfbbQuthao)I196VfAFBql9TpJIvIRQSj1QRX5gnHQQF0v0ysbqdP2iDyPC710Ik2sgjQzHPjL4l4uVGDTT1dYZPLxu4d4Jks2k6DU1bNcijHdM0mqqCNknCbEJeO3w)EgjW9XcRYOUa2Q14fXeuM17f6ADMD(0M(Ko4V12f5zY3Qi)GYi9pe9oEDYFN8csNi6vit7LM0ftHFsFDBYVhXhLUzH44eb5jC5SEpNiHDXgDeOnwJGdFON5tjrQght(XosUkH(KFtITeaigvn9pz28uQnAS6EbtlDU)Yc6J74KzKhYH8l0EZ8eU9GnTcR)ccPMwqTqupF3zRcKVAnpVkqWc1la7p5fb3RDONgzlXo3X6AKnrbeI2Gm5Gwdv6hxb4WkAjYnLvyYQ2f2uvQbFK9bq8EIoWAnvka6SPJwEmOf6KqBhjYoUTXb9aGIdJZW961r8aS)kj)VZT6J(w5Yyp9sTxAF)wP(jAW2VzgEfrmcSCuWKQwKkUUD2yVBetOuTcws8ILwEFa5G61cIERP)xg1yrG93Q0z)5ZGmPMtqUUf31LXlx35cx4gAavvriGL3G(EA2velLdeDun)GRqrKTfsZrjXEUHXXkvrIQq1JhLvnKGIC2I13bxIMSMZ0bN1Sh2tNy0QVm8rZ4nX(VAJVhHgrjtK5OPdDomu72ToJu)fUP3aAtxP1xSpo7BIoB02kxqBvYUie0)m5jEwhzyFUy3kbWp5(6QcslFYiBT0RqZy62wIvamyl1XPm0mIn6d0pfYWA(e3QZwy3z8nRXM5yCpR2IbfD8sdNBi4tuSam99PdFc1KAPH3Wp0u(oddDCu(lqw8pqjwUUT8OSHNd34wojKxqZAUvnf9rPDQ6sOMganA8WfXIp028X0dcoIpropnJMEI718crEBgJwB3vwCg7en9I3o0v6yv5fYKTOvvlD8WR0bwdKhSlklDoNwjc7byPoNPO(H8SzQ)G(r7GET6)wB0xfn2wK62)HUmnBs0RGDGXI8z7gesikhAzhC59JbsRUEF3MdeYL7nB)DQPGsuibCY04(nryp1l7tSm6WsRdE3GcMLudB9XvdmlY1b)rjZuWxpby7JKB13D(0C4n8Z4KUKzBJwAf5zdO6kqzpG8QBtQSus5y1LLcHOADCS0E8IqmVyAIXMhcPXd7hhlwHAjyVRLuQN3O)3mnYt7zb53EVUZXk3Mhr)Fsbgm(wX(cA9qS1HpejlJfe49ckDpzBas1rV3sciHhu7hVB0YkEJvUi3nulIsjvzgycCibMWnwssX(oDzd0Yl5LqiFUPsc16WMIk2ySKD6CN0c2qpvNSNqVjQkylq4zUTk)VU1TB6DRFWSUvpJtlcZBCoEuv8QJMhsAcf)MBV2mtNhxVmulxVumncLuFsj9jqND8foM)Nk7GlgkMPpXpF9wlaKdauhGK0MV6DTuh7gxKxH1Ah8lrA65a7(71Cyh1QXNC1VCdK410(t3iKiU90E4pkejiM78wOYP8dQHzTxq70uXkApYFb03gtTdCLlF0Z6Msc0JqmHqYTjL(afMYd(4r24QbEa6WY9XXWVY3t0)XbYaHh5vje1DDePNGmiSlrKzbium)EQlkpKaC3WLp4o7YVT6qhbh7za(kkQTEDMAzz5UTbkD4hh5FKXTzu9GIuvlVCxjc(90pdbtD9GCvHylJ7ca5pQkk4c1nJ5uKxa4uM)J)v(ciEWmvxIXVAGQVcwAof3jIM96pLne5libGBTjhyjmpl0Oy767JwMqXiw6PNKlpZhQn5Ee9OqItePfc8)d8reqb8sGGa4xpCrB(YqdhRHoySmDchjP1XD9kmwuvPa6DZYMf9X8G(YM(nnJTrlTejeSluVnP8hgAuSxi6CiqNgZ1hWt495ZockWFugfJxC(VoLSbxBnl8wYZtGsx6gwyeRcPfa10bssAX5NBaaOKRHsljj)E1xDCLet3bnohVeL9rpenASeh4s9RV05RgXkhlXmYVZ7XdACN664bnF4L7afj4d)5BWschUTaEPFAq5KlIj9yFvcs0f)hNF)oKPQJI)kWT5GXZwmI8hwUPJOP4)LyQeoummeRYtnZH49rB2fXBzYzNJKiaJ0zHWFcaPyhXxY3ObtgoTGcy96mxAP)A1BQTCV3wbPvx69p5bG6Bwf6Ji54u3)zktj9vXFsz)Bz4uyY8KuH2CUn1R4kZ9MWQP7ygWkRXsrdqkrdSsM1sMxNCL7HszQ63hmHJVhROgST9eYGCPZ28cD8(v20ZBGJaRNqy4VCTockioCBgXbzuLsxOBmTS8m7kqNENptrUqTT0qUwCcM(CzDR2ozaRvPh6dFucMFL3E0JTybEpwxL3zljGZsVI)Db2CiU8Y6c0hAf8EJ5LBbsh0HWDXMmesIoos)d0VCyaLxSbhLZLrK0knmWnPzUnrh0qhqrteAwecXFd1ymhStgkqxhlXRAl0Gysj4ljnSI6uent4o(Mp4XQHnc4)Oi0Y8TstRw0uDEQ8(KL6EKEvH978EBoZ9UVUiURMjEMQwuOsymrzG(qVEeLpS)PCdaqudyubxc7HZRPTZd(HhvTiScPfaFmsLwMx0QPQNSe7RY)in0plaKY72ox52P6iBizVrtwOV9Fbo2L7VbGguyq)sIFNs(gxfj3FZFKDXPuvw(BuhuM9blceiEa7XWk1SAlfduzdE7WFdTpETPqJ)UtRqtFEgvQnHcUMTgfTxBbNEbV44TpUMjCJ7qHJefI)izubwQTGd8XbuI3NgoD(WxNvOAj0mU8bK54cRc29WmZ2ChFS3MvDlAy42Afpbu0DkGgR(vi9CTBaqIA1Q)hpHUF8foVBTfVnfi((TvGKfIt229QF7xGbTVRHQt2gOjhkz03hIH(mmmbmm5t2m90wbdnXv4x2H9H9bRMWeqxvI(XzDZ(NQBQi1vaVnwUQsDSMmHyIoGsgWvVRTsCWfD4c2NAw16uxiXjnNPFw1E83fsVo20p)(4xRRZCy7NIgaJld397W8(c7SXF4cwCBOtRJafYwCRu89OA(SHUpjGi4iB3Kff4ZPfym82blZ7nLQiBqUlR54QHPZP42S8V0xjOFVXUfICocohoyTtbPVF1If8DXQ9Zuy2QGhQcIVeoHkD7HxRYNgQAZduoh4(B7KM9DHFtjBpXmUG7ud8JlPOjEb8tfuSoWeWtELCMRpqM1kDzgbOMcetY2MxokoyBSQuMRsy55fyMrg0Slylg5sYIb75OTE2sXiLWCQWcn(lf8zxLmywUBWEI5AmqWzxIVHgEJvlWpng46fITVG0Dv834GnMhF9(vtuvf1wz8hM6dLwWLKYXmnPs1G0s0VpRFjBuc0hdvrVjpcknQbnhtQhXK7igV5macOAGYOWnj4H57W2yK3RlVcP77Ru9(a0hPwDBnSAlu6LBkZkdsEffOGfJc(dAyZ8HnwYrsHdse2zlc7ak8XtJwh57V4Eisz(IBo3AdklYkWH4ADpGY5vtfWqLwOvWs6dCA(1R0IwgChDqDrCT2Y34GFnqEPmZ97bPhMA)dcY9rbbrD(UxfWDQriNHbV11GZ2Qw2GT)VVbmU7Ew6WVyUnRB8y10WTJXBN8SGMDl5pR1Zw6eFOa4WO4E25a6YxDej9zoaifVtczIM9fyF24h6Jd2Ti9p0nPDQPBqSWmNfiv42ZFQNhOonScZgfYNPU)ckbsVEjlntF3f6U2fm8G)Jk5ElKfSOebql6U5fL4rgFfVryrXF8xN8vstz2LsREBML8I8keiMesUp04wD0jJ6P3E9gcTIee0kE3b9Usa6f5vRg(w(ZHjrz3oiJodKcdt0nLO1u5tNQIV4TqmYYttBRZ)Djm2fy22E0s5Q754CanRkCw4U47T3Jxvk1cYeyiWoqTf04ipPkJN2TBJSCi4Zwdcb0h8NCXRuBWuf2RIhXTv8FFtpHOCrKTP2ypR)ybNyJpxyHJ(JuFg4(nFjZ7Xbe47Ix3zDPb49ASOTG7pOTaXcSNdiRsla7Rj7Bsh1SoAFxfZI9ekHXwsEtFzSsaiB2D)uXKXVlG4oOUl32OlL7NEoo)UiEtcCbsZbnsCoKLch5kT4HtQtvRZuICSNuaBIwDJCgIAvikHlgd9H1lTIJNPnkWEa(URGe3DL495Rn1vjlSxm6kOUV0112j2FvQAIOTqTtmu(8USYFLbMW(0NGlB0UtEYqJRDbDrlS56WoQA)b2KPVGrPvQkWWpOaX7gjNetotAHjF0ti3NsJBbfvsFBqNRcqP647k8YreTVjmD75)bx3yqMp5DgQXMaCpL4bf5u8Kmmrh2UBsSMGyK(frkNz8r4Tw0(9Ljcy59TbOq6OcYSHKgG)Qhstg5HKNIKcA55hnOiADyU1eY(fLGLgtu(x05yQvtO)aIz)7WOwokoAjUJGlfRUyAxWH8NuCCo63k0VL0xIL17lN80E9vVzpELoecVXgBByQT8iLJp3h5CZGukBoxPs0qeIcPYSSAg49MUfOP7TKN91O3Xe6v)nzIL76QSKd9My3qK3l)wWwRNJjgi84x8jfMV9M8Ew5Ufu13zEouxXJft7bYveFp(kiuUry4C19eQ7v1CSnvTiPmlaf6fJysEYR4yUAto3bAP3KhOosAvE5N6sd12Tm6oBDqXTBUYrDBsACZ5piUVyKsIUBB(GCivpPeTe1qBJlp82a9QjxG7pxJD4bOOmKNUmqOFmAPbGZ6yVvrIejPFTwGyCJ4T06Tk5Sr(EC(x(WaF1EGzmXPlclgJmaVbX)jwY1Q)(sMOwY0CQIgFRi5a08dqQEwZWJDanZUNhDI8ZcKJcij5xBGQ6kBj5IAu(m)okFYc)KgtZpgB)GOK2orxVoVuLm7hKJ3PXQaTMeF9b97nTSLmVDMMuHMoXjtMSna9gTZ64SrxEuShYpLf5f8956dlE7lkHOG0xJALP6PnqZoTPPBfYj(TZOj5ZEy))EREtVTL)GkliiCvraZs3feAmofRX(2k0MFzSRMuvITSCGzxvDdplsSfeW(bUTRDJ7RJCVjy6C)HdmO7OYUE8WIo9N8k6ytkhiPfC)CkLs1cno9onR(VQL1DSffZCkeiKD6vrfS9NHnrUMbHJshrP0Y17Iz1P8ldzh7DlDJScXNtxHDc7uuXJOI7pn(Px(6wtp8bWewnGNo(udqezJ5SoZmDc1GB87yq7vn4tMaYn(z(dpNHEaDzqWXpa4HwdVKotaMCKaFtO4Hh(mqC0uXIY(hcQXU5a9okDlY8lDM85WPwOJ2ECDT3maf)(ci75EVTVlaWGUKDOMwtvlE2xDV3VtwZxoUQLWae4c2nyYy0xnLh0Y0lAb4vZyn2v0DDdRP7vJYtqbkwRG8x))dWp9vJrRPNoVTq(SRuzRGKblSIZ9sbCDQiuaO24fkEr8)h)98sfp0cMayvqSxtPrLjdCQDj42KjEE6VQf0RvMq5)mMkNcMDS5rrbsjWwuEWshDUxYJSMqv3Lii3JkccdY1cePHubj7KS44NMi7IlPtOgsRM4ARG9)twzyFgpQmt5l2M8nt)MShv7AJlKYcVEIPl7lDvcGVjFh64qsidVpUGdIMybLnDDiIaCCWAHym6rE4Gg517W2RIYFIJtn(rbF(MDGQovjJJrCc4DgZGZTS)K8)9uOStjtu7Bi8BOpwh(WoFicGl3vf)DTSYNlZYTPwrA)8ub0SqBMNkhXXj1b)XrewIMnbmbN7lXqwzLsVqN6MMZD)Ictm60YDS4o809QX2LHR0aLuEtDmFMjG2pGEDcOfxRr2TYR1hxSrS49RfaUK4KioVZzM6(AjFpXUmQqq06RPE(Wvw1sEgVzwi(J8h)glF0QoUxxt)fZWdgTeWalV6AmB7y9ZeAS1pn0w4eiPLaBupRos(Dl8l1qHRTdcP4uPK4h4)G2a0JrP8JY8queu7D8jJzX8C9rHppdn86wWiLGOCZpel2yu(jVUvd)hFZhZ)qESf7hCs2Tc2)6sbieGd8)ilVBBhenRNek5YmmxRZIJ3EzCVDHrN3sV2yyA58S9UemNZ5nu0zZszpKqQ8ESgGedAaLtnor)8HvOmojM3XChdoIKgJxltilyHGHjtJrfI5wblTfjsZ7VI9adD9)gk3LQ3mj7D4sYxNxOjBb8(e3WTKDSP82ioVQmIJGN2wLKFIZBRlzLlc0G2)79utCtergN7fqSAM(czobnUk1g7Exxz)km1y3gxcz3rEqIwGByBzHgCx)1KGLkAQkKBblxlHQw(69tJYiND5S15IDD0il2BSJaLTTk2PfaKVBJDn6a(oXoYdcolTwwN28q3MUnf1LoBl(sckQ4EHr51meaH2rRjRW)wCIpEq3COCCthBvWcmuI27ri9v8rNMuE4xELXJAbMUJGtUn19E2sDrqtwpBqXvyJFFWuaZTNvg9vldAO7EQPEXZHkpkgmx6LzA5buKnlfVrpye6gSoc61NgsIHSp)5oZI7nqabXtWezprzRXw3rwdJdLa8x1QjCV9JE5dNGUhYpqc0ukeFUFcJ5NhSjmE17oRypVLeP5RYw0RoOtUEguK7ImR9jvz0OnfHLuxUVi2msetNxJGCrRIT)Geab6)dGFks(TnINris0RkTGeIewI)ogl07ErmjQoOitXz0xMb1NCTitcdOLCLc)lZGj53mJ0rdjds5fPzzleW5XvsUpV5qfGhL9YjXTwQuUA1cteynPrsN13zrPjbERXr5GvYJUQTKfiz7eNmf4vHesGIlI3MwPSsfcqaa59sXiiGFFzpw32L((YffYdEhVupHCuEcA1DyzzxqIlOFOh78GhGZxwkwCoOlpWKinQZtgxjxYfRWmnIP5RXcgWlUvOnPprfwJBR9c29BspdtdJ6GTXR0XX1rGdNVhngjhWOK7ARKO46Zacw6JDjjqqLbMO3ZYkd3KauorqWeCXFTXCF27uBTGNqV(A)4P0t(aHT85lJYnQxGMWFRhEiY1tldh7Q0sEGrzD9(HlSJvPIWfJEiRv5PTxn()wykjpVBIBzNNzL6fes7rL2j81Eih9ezqeV8DvlO4IZQ0NOiDElHIvLy4VzUBMGhMgmQ280yRZ7qdEVG07fMASoRHafY5stCajPDKfosOAeDDyhPbtGiGarn3x6TIV8ke6k9HpgRqRhxrttKDHM5KRbwupt(d8JQ8kJl(D9B0kA6umW0xE50T3ab)iecakqQY6HkLWCWN()GRzJba1ZN7D5PtFaLfp9)EOo5QioXAjWPqAbT1XZDT3RkPd(NQXYKr9L6HBF8Jr9bEcZHHPJwi(iUH0Qem7DShhFgIJBx3EwOluqU95f4vL1MmWHjav(N3s5UJUT(rPTcL27a5R6MEB6TW9YulN)eGrjLWL4V1rXi5tTUu)enhLEhNxPisHTTK7A8WfM2ooTTmhBahiCM55PTPrUX3biTzZgL2HsDgr1foON28rjWBOxuptFvowyLdvHnk5M)QAOh7SiFLVnX3ACoQGwrkEVUFdXDmlPoSJKsYPmfAmIp0dMXGl9lUTb(3IcJK3RPVaWH0CeFYPUZlfxdt3oJKMLt7xptmEyuEI5ZpLbBjM2S5yKvuQv9us)PI3VIJYMqMQXyEXAfhGhn7WiC(hBTHhLqvNlKc6KE4kBUWL4mYZ28d0lAV6WWvcnytdNDaOmxD0Z0smzbFK0PQ3(TEe8v(kNLrTrRo3Hr1lS3trTDOU5DJrRah690NuNiXhh0aIx)T386EDp3GrNisJycc)LZLTreQ))z)63xeAgtoMkg96oM5zTM)2LQAyNb0vKNk8ulNAnvGuv22s7gCdAQz3JpJiYA81CW)M79wC7sx4KshDgZbbiMo2RK3m1aJaR0)j6rTlbv2)LGJjBG7eblgUTABYK4MPQyZOI(nnbUYJMLIDUsNeSKotMRqbuNuuSRInMO2bPJV1XEbex19MN90yucR99qq385fMXHbJeqIoQsPxCqcZIHfkK60nKgb0gNoZdRzgremg09lBONU2w0Qtoe1XqUh9KdK5pkl0DfwBVzqMZKnIf7Ebexa0kD(650WuN3npRHUFaS6kvMaQDPKCBFNHMSlvhxbsVlFrc08HXslTUlBLAoh(7dezS5sH8HBUujjM(LkCJ6XAXKAqOTTfL2tJ0QZKkWKu4c0IweBJRZithPq8pz6QfFFvLhg00U5HgSUyhCpO2gixVuWgpeo21c1E9ZUIbYO3IFIZQ8UAuHwhGkGwlqrEmvNTRTAmb1d4MU0DWKmxRQmo6(ZLz2gD(89I(7qtfp4CvwYzKrM2oA5etX37lcXTyV)WPmgWU1L6ZVjFQpJ)rp1D0u87FrXpmjUHJO2lu1U6ueIkV)YwakZzMgKOTZfZaK6e(nH3QiMkIR7XPrd6zOGlg9BxMc8Cs63xcVZwjeUmrV2y101CQaPPIs3FsF6SK9BdcgzOFN(72LqQyAY3hoTu)il7eMRfHkuNclIBsFMa5sXKQb(jrUdt7I9lSvfXhNIm(Ep(4AZAW1rPxA1Ks79NxZoOvvakU7BAPP6q77PYsGTKX7gsu6KTIqzYJNlSaVdMbbpV(VS8H658F5uISy3Tk1mVxsBlxRW568ScYlzjTiqA8uw1YD4qt0Zr5O(HLGNfiDLX2GT7ZXA7v9pV(np(UCsfmBrZT4NXe81SrdCwTcKxoNlByOrUrGWguRldaPxvTwLCkxzWMhoP)RPfp4oyOnNEV7Z9ZTviVG(cCNbxIsITPb)Eb00izagGQWVqVLQ(89Ok2J7zPHQ26NAWwvdtI043N4Xoy4yqO5HepDu9TMvmYmN9EDZWZUcKIZAmDDfzLv14bYVbrJqupJEU4Y6ms9CFuqwHIHgDe0ie9lJYkBwRTe8kdLZm6GrLXv(32KqufJ0Ker019NNWmPfd7aJiZMGV5NnU9khU4vrxQ8U26B2VoKbFkucYZEIp8ritCFB2EG8oOpMURfqlopY1D(x4c)COsSBn7P6jIa)mxlgP26nDID4jXlrE3xZFBXlzY(TQ(2lgCaKceelOgM5aGeRPvTtn90p4TG4yjyN6wHxDyeFlhgfVDqBhSSKdlDdsFMke5dQlFrenvo1Hgek0OSLw0CrhlzUYLgNZJY5x64JiKxLg967AWhNfSjKhCc7m87zNU1J8Wk6dssd(jZNGhf0bNX0bxTSsYwFFw8bDn05ap9xBj80FHirl19kUPQLD7HxaFyaooxVYY4VicWmWNtucl5pNadK6wAg)c2C4Jq56hH6EbLBirPfo(G8x9g8fiTNZFUADEeyjjZWpVfwTEp)v4kzQhB07bv(AEZMRIXagQ3pnOxE)dAHmU7IxbfHZ47ZMA9IhlKrPjKqvBPU2YhW5H5lSiFQD(CyFudXybIPHCi4bt7Nb(AJUhM89kjuoST70NMZ5b5bBUkyUiT7UuHtgCoX1M1998IUqPLwyyOy6mZ)3YLA2RoLbM)ZmuL0V6jGBXdt6TWt8F8rnoNtLgFpO4fW9Z0LrbM9euCtb6Vsu7MjRuMqk01YaFgc6pi6wok7KgIscDeQLaMe6WQInFfQqq1XZE5LWF3eMPP3cKEdNvCS7sTSrv7cjSMRPzy99s6ngB6Xrv4AwNzoUvvCP8eQQtYfO94KQx(eZVmxVTb7G)8eAjWZfbIn0hqj8oqSBb5BUPx7VNqYp9uNUVqwvxsjqmwWqWFiHFGygvXOhmP6dEVKn5kmvCZNSPLOfXSIco)nR2jJmdUgUUiXe7RDhErrqaMmBzBQvTL(p(Yn651CSRqoiOQom4wEcwIfZ8wLh9jajQGla27SrgARCL2J(EZV0FDtuWa36LMkmpi8JWi38U8oZq()nMz6Mhu9txGQrfpkGh9pwl7S16uWBm4650IkkJGDTqJzONUmP5dHk7I14iqYkYEIkZYyGn7vG9cgAljjeqZQPhztpbeve0JoV0uv8DQwKTYFwshZ8NvK6gPyfqGWTB7mwrgOcoxvya6A(2Wz2Hg2uTwOriDzm111BamLkas0aGJO)DP(UcNVpWRhnJQsvyXC2ltprL33n4vXvuIWocMiM7Imm82piFW(clmr7gO3RT3RWsMr2eXR(MHv3ZAVfDVbUMI3sgqhXefq2lCUAgmOl4V80I4yEq8lLkJLqYd9a0JkmovRMhZs8IKECq2jcfFK9ItPhF3JOcOstnUjF9MC0luLArcku3orwq)tRMJWaLpBOLJp4Tiyi4iHgD7vSeXnUI6T9)9l5xkUaBBQiI(tIzcfj(C1ZNcp7umhVPbBlAMd1FpgVSL4G5sKFFj(17S7qcO6QaZqkrGArpJVLkLRC)ggMeocDusvn91F(VfunLzih3uGye7pqJFasYx(hDXQxmXOvwO9X9V08kJWKh2PUcmGBchMJb6t)SN2sAwCkFEnoaOKiZC5Z8IXQg5ATFlZAa7Gw8eXowyD9DBFUi)Oyl5nGmMEm9iQKDrN8K7SLEM2NeB1L)3wUzNuNrOedpAW5NJ0IhsINonY2T3ntOvmKsnr8qFt93d(KAJgngeaQvBlWQOWnZ0CYppYAGCcf9C0(iCbyFfZ6zUahSZHwOB8h8waaQ4H1kB9t9fMTgiJ445df5QyCM1Q3zY6MygVcfzlKIxLSwHaRWVWC)pD9OR6MplDqxbnYnGP8QMMy20jk4L30jLg58uWqaKX0PVM9gH(zaFUrUcTNG4VF0g4YHVl8zqR5)KkjhYxDwPX)4GCEejFlLsDAVSenGYDO)HxVkeB2jZgS(TaMEnjWKH5P7nlEByZPFIU8LkOhE1OvBTehpwv)QRTxkaIsLLoZu)BpaktXmBoV0NC7OVNdUyRu(CWIZ8wZ2MYUg(71KIpUAV7ABGrxo1XbhkTtPXHoT6lvi2664oT3bK0aZ9dP(MYGn4InqBHKIn2w14zAqU5bJ0zt(hcfvLXqsd(d0PC2l9h3KpYGHBbtTHDjeILIgppzcCmCRGuFGvo)2QJa4GNcJvh3Wa(jr)DHjEZ5K)GjQI7xToq1IZ7ST486jfcR(t1Gv6DQ84DGfT3FUzCfBr5k80Zg(gh85J(9pW3dZIfQV(Q6JF1OgBaIvTmaDnPM5FURMEl8uUg8OmYMkXySU2EE1iiUOondTqW(GMJ(XHhMiNMytrZeWOKdUldaT9xVpvNm96gkcI038)wyrEktR)zlp0SQD0FlYz789jWoogIdyMSkyLvz7ELY1kxRV1aJOU4iO6PIFJPUv4RVVEGfhmVNtujuvTT5XzfxpzAlgHAGEzhP3tLwm2yGbItllzGBoqj1kVFqflaUo5IV612ejx3pSGg43OKPqbTc(pT3ZnZBaW5WiI7ObF4WXAlk43l(eXTMerUPI6Dwj1fO2NDe5QbxQCJU8z19wUSPmnzNAs6v(jsaKujFLD(rPIDEHRD4)DBMdKqIMEiWy4JEFNo(r8HEXUtq2L5qRwCJNMHjzcw3a(sM8iVYRr1PVzuOu)iF2LvXB8yv1UxDjz2KMEnm5N9rX)N8ZZYG(Xce7nh83CGuZzK)IRo3KtPnuzRnlEYe)nrmd4wqZhKGGWqaISu1UY6UM5lvgAhpS5ysqb)LOOqdnmbNp(kcy0RanmrNFRfFoNXYHTASpX2qeIJAyjSvyWh9yR2vu6csQb3FXUYfYJHR2)MA4WDZ8brg7SmvJP)g)1MCTCkiX2Q033fFIT20RdvJRpJTUHQ)3rQOdaMq3kfhctxA7EDTnbyiWuwkxnk0LjYtmIrKeCeTORwVPVJEKUAWeJzoTm(XQcQggY7bd4CDAr9VGNtz6)vcMw0JalkLeTF1HqqZno6Pk8V462yul80n(kdjDEmw45KDcaSzu7WxW9UXYITyFwCGSf9LoADE6j05cfkQ4TngY(6k4G3jteng)KjklkMBQ9Wjv8zjlSESurjI2ucwNFo)NjNlmXbj2zpWtHhuQChHng5EZgPGJP53iHDE9QuS2mLYt4r1qhfElFUsiv)aIFVnjSQd91VR9axf1Ht7kRAD7jTmkEgCQaNEaFEpi1GnLimSfje0CJTJV3i4w85ttD5DuQOCaYQxxE7ng9sXoZLNIooONPEo4VEFSb7DVfNbuC3HgwcQFhY4bM0AAn0KH3zQ2P9SPZF4MBx(k86lFZUqmxzYdvSZQq0SnflEF(DM0fWvph7pflujBtT6bb)g8VHpNa6l5wuznME(Lrfaf7qawFudneJXySUBDcLei(daN8fdOA4EgY50VvmoOJRJ9fCDTAlgANhGNlIxgai)QiaUijzYEvVVavYLWv(wUVHcYa7SpF0vtBq8b6)dVE8wtX2Sgf2bZygKSjviJY(g5wxH44p2)HmEPH51O3yZo7AZnOt1vBTsBXkCRAfYDT)8w(pouLRrkKbH4)(AocWLpdzeOQ)KMuUyR)75OHQCVAPK)bjHup4c(OYbAXtlIOlcSMZ6pQVIqiglGwXv8UbeuhLGxX(wl9uXWuPZCPN(d6n0IXWEgGT)WTQxd75USS3AVxAoLVK3ob36yXa)oSQO97)GcJ4dQlmHJbp(zSrhL0Ebl9vabZOyEspe1gXgkCWrNbpxsIvUtncsR1)VpJzopiSw3a3mraitjlXnzOIhLyB7uu)pj0HyWeQqZEbXE81zmXSpBVSPSmU)lOxiRKEOBiibj2WP2b58UDa1ho7l7hkitVzvVvXi(JoZTpLOsYyDWawUkojN4CVrUSvGrrQL2Hxj1lECvpmo8yWXzokSVzsAL(rL1dcKP9sNReMWQ0OUwYB4Gad6iOSOf1fbCUiRjLnbv)B1e3g7Xx2gpeNExdh(AFL94BkPwaqensTA5fAk6aWZyCT9uWLCRJvSyKIu(1wyJVPwtk8k2cmOmN5kY1gaxGvuEBqEclVnZakpxdSpHtknc8dvE3dCHpFZ52)WvkM1UAJrzWTfej(CuTIFmuBB1RRNwCv((c3tOn4uwlcWBtZVNNCXZ)VR2T)FvrzPogDvkDS70C(Aoi8OEvY818NmSwNymzoK0FXcWzSby7hkvAMLTHRv)EMJ8oomPrUGRcpo6t2)PbzVBE1YubdPsqd739ApimWBmapo7CteLx7Waszu2RJWZco8HMBOgfA6mEZBynvEIlAwviHlPnXEqf0kYoFyzyNlQTuW2k3p5B2fvKKW6PNk4040jvEjv18ilweqnTAGAg1rZpKHdFx29EcML06fgqfn55lNZXnDetszDA6isD4Q2kLU5p3ryhP0sycjh0R14nvZNWcoxO5loWqsdIlkApBorA1g6MkWyqEgZ8DE80sRcfJhp8A7dMtwIDJ16NBLiJg48Oq39(LFtXBH9NX7MUJVnN53RdC6QUB3nzZgHZvcOwwH)PUbivAlBl5z4m0F1YL3jHn8Uysp3fAIisIexRdgVgPYrgnnQa9r1Fp(n0vOzGLMsIGP0wdrsJZDw4xodSplV(l4JtaofevQ0YSdeDkKHglv(iArDLq(sI4pMKOoUSet9EhqtQf1uqZl8ysq6zjaFhQVssgB4K9pY4lItwzSrvs6KFnSZ4Noxo7CGyfjeQnA)0ezLpcZbAHpi1GgxGB2AphhBr8uMPxK9UKNRiUDS4jtuaXtnAI3vFLieOksCZS6NiD3llJVaXy4ybTZwN53Y1n6u)xUWLJW(cmXtDbF0Ft87jc(sUjA2TeTyhm6Eio0UM9dY08DBDO7MdQ)SI5Wt6hlaGmEuxJy0)e6NnqHEABWNGYjmlO7(mzFtg1hRlG0TlZiyUwZ55COhQ)py2MH2vq3GZc9HuHqDnBNu3q8rwdkp)z6nVBAeK4JQYfRa5iXaLSfDIizv8Z9UNdRqOPXHYLqUpmC3z6oT6vbTszV6E4QVvrQooS5XzPgszPuOsQlGzBiOpZzM0eoGJIVMZnM(vHTReowARkSLH42Ea3sL53Ih8QbhlMIpqEX58FU6kdxgaiDSyhmT25(G5zhhx(B7orKCWVsPOzBei1gnP41JITQSCQBbdwSYIaS)DKbwe8Gjb6xrdrwcjbEBek9HtDrIvENeM7WS5JcjriGUPvbq4lg5Cb4XCnWCM8eL8B4UROpj7JoTTjGO6auSEa0B0QD5rzg04hBohmcw7y6naA3ByVJYpEXyjqnnm0LecIHCqW635WNJzanz9lQoQ2D98KtYpZClhDxW1BGFtG12kO)hR1PF(VhkNmpaY2FW9bhkENc8obQJ2KHsQc(sJme8c5ZM5J395uZREQW15(qWLyGS2umgwPV3Q0hKg6PfDEAdPugZbRRLtnbhTcCiwoJuOkmy0Z8ikKwcGhs0DjCSzE9ghw(VcFIs3vHscSvjX9FxBzaMG)SrWV4GTEOycTRB52hdrR6l1C7fek3YYrD5lLJTaUgGJRotZLndepmS7DxVJ4NYpFh7QfWmkNBMlaBYeGUwwqh(lzwgsKCVa2he0YSNQJPJHjefMoYGolvjyfg(Ke7m37isUKyASHXbxAa3QgCs)J2LB6uLcT0jUYpVSdYW3r7RBDMYmT8tmw7AdULdNcQT3eGcuEblVxFHO4aAw(xR1zFtZNUoatNOPSVet3zQio9xFNIa0KE)JEauEqDNR1hb42t9(oxVW1FriW3VlegdLd5Di3NOdVavAU0Sv3gaS6xNR7gHe2xsN9UrF9buuFi44fJKEU4.\"}";
		   logger.info(httpKey + "登陆ajax.php请求参数:" + json);
		   System.out.println(httpKey + "登陆ajax.php请求参数:" + json);
		   StringEntity s = new StringEntity(json);
		   s.setContentEncoding("utf-8");
		   s.setContentType("application/json");
		   post.setEntity(s);
		   post.setHeader("Content-Type","application/x-www-form-urlencoded");
		   post.setHeader("Accept-Encoding","gzip");
		   post.setHeader("User-Agent","Dalvik/2.1.0 (Linux; U; Android 11; IN2010 Build/RP1A.201005.001)");
		   post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   post.setHeader( "Proxy-Connection" , "keep-alive" );
		   response = httpclient.execute(post);
				   // HttpUtils.clientPost(null, post,httpclient,null,httpKey);
		   back = EntityUtils.toString(response.getEntity(),"utf-8");
		   logger.info(httpKey + "ajax登陆返回:" + back);
		   obj = JSON.parseObject(back);
 		   com.alibaba.fastjson.JSONObject data = obj.getJSONObject("data");
		   String validate = data.getString("validate");

		   /*if(validate == null){
			   logger.info(httpKey + "登录失败，请重试");
			   resMap.put("error","重新登陆");
			   return resMap;
		   }*/
		   post = new HttpPost("https://hoapp.juneyaoair.com/member/login");
		   json = "{\"request\":{\"blackBox\":\"eyJvcyI6ImFuZHJvaWQiLCJ2ZXJzaW9uIjoiMy42LjMiLCJwcm9maWxlX3RpbWUiOjMyNSwiaW50ZXJ2YWxfdGltZSI6MywicGFja2FnZXMiOiJvcmcuenl3eC53YnBhbG1zdGFyLndpZGdldG9uZS51ZXgxMTI5Njg3NiIsImNzaWQiOiIxNjE0MjE2NjQ0NDY2NjIyMzY3IiwidG9rZW5faWQiOiJrZ1ZDTTludlJoTkkwaWRKdXNETGhVYnJqellXSGlxUVArUVBHMzNLNTJEeHFhWlZzVWpqQWFWdlI3U01Cdk9uR1pLbEdCODZ5dThDUFVtMkk1OGF6UT09In0\u003d\",\"client_type\":\"native\",\"deviceId\":\"" + deviceId + "\",\"geetest_challenge\":\""+challenge+"\",\"geetest_seccode\":\""+validate+"|jordan\",\"geetest_validate\":\""+validate+"\",\"password\":\""+ password +"\",\"pushNum\":\"100d855909e62dbd963\",\"systemInfo\":\"6.3.0,63000,dev,10,BMH-AN20\",\"userName\":\""+username+"\"},\"channelCode\":\"MOBILE\",\"clientVersion\":\"6.3.0\",\"platformInfo\":\"android\"}";
		   logger.info(httpKey + "登陆请求参数:" + json);
		   System.out.println(httpKey + "登陆请求参数:" + json);;
		   s = new StringEntity(json);
		   s.setContentEncoding("utf-8");
		   s.setContentType("application/json");
		   post.setEntity(s);
		   post.setHeader("channelCode","MOBILE");
		   post.setHeader("clientVersion","6.3.0");
		   post.setHeader("platformInfo","android");
		   post.setHeader("sign","S6aCMRBQpVR+GUt2bRw58ve8ZrCK2EzKl5j8BR2zL0Zg9TnHJoDGboB6Z2+IJCS5");
		   post.setHeader("timeStamp",timeStamp);
		   post.setHeader("token","www.juneyaoair.com");
		   post.setHeader("versionCode","63000");
		   post.setHeader("Host","hoapp.juneyaoair.com");
		   post.setHeader("User-Agent","okhttp/3.10.0");
		   post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
		   post.setHeader( "Proxy-Connection" , "keep-alive" );
		   post.setConfig(defaultRequestConfig);
		   response = httpclient.execute(post);
				   // HttpUtils.clientPost(null, post,httpclient,null,httpKey);
		   back = EntityUtils.toString(response.getEntity(),"utf-8");
		   logger.info(httpKey + "登陆返回:" + back);
		   System.out.println(httpKey + "登陆返回:" + back);
		   obj = JSON.parseObject(back);
		   String resultCode = obj.getString("resultCode");
		   if ("10001".equals(resultCode)) {
			   com.alibaba.fastjson.JSONObject objData = obj.getJSONObject("objData");
			   com.alibaba.fastjson.JSONObject responseObj = objData.getJSONObject("memberLoginResponse");
			   String id = responseObj.getString("id");
			   String memberID = responseObj.getString("memberID");
			   String certNumber = responseObj.getString("certNumber");
			   String loginKeyInfo = responseObj.getString("loginKeyInfo");
			   String token = responseObj.getString("token");
			   resMap.put("id",id);
			   resMap.put("memberID",memberID);
			   resMap.put("certNumber",certNumber);
			   resMap.put("loginKeyInfo",loginKeyInfo);
			   resMap.put("token",token);
			   resMap.put("deviceId",deviceId);
			   String cookie = "";
			   headers = response.getAllHeaders();
			   for (int i = 0; i < headers.length; i++) {
				   if ("Set-Cookie".equalsIgnoreCase(headers[i].getName())) {
					   String[] splits = headers[i].getValue().split(";");
					   for (String v :splits) {
						   if (StringUtils.isNotEmpty(v) && v.contains("JSESSIONID")) {
							   cookie = v;
							   break;
						   }
					   }
					   if (StringUtils.isNotEmpty(cookie)) {
						   break;
					   }
				   }
			   }
			   resMap.put("cookie",cookie);
		   } else {
			   resMap.put("error",obj.getString("resultInfo"));
		   }

		   return resMap;
	   } catch (Exception e) {
		   logger.error( "HO登陆error:" , e );
		   // resMap.put("error",e.getMessage());
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


	public Map<String,String> personQuery(RequestConfig defaultRequestConfig , CloseableHttpClient httpclient , String orderJson,Map<String,String> loginMap) throws JSONException {
		CloseableHttpResponse response = null;
		JSONObject orderjson = new JSONObject( orderJson );
		String order_Id = orderjson.getString( "id" );
		String mainusername = orderjson.getString( "mainusername" );
		String httpKey = mainusername + order_Id;
		HttpPost post = null;
		Map<String,String> passMsgMap = new HashMap<>();
		try{
			String[] dlyAccountInfo = dlyAccount.split( ":" );
			String proxyUser = dlyAccountInfo[0];
			String proxyPass = dlyAccountInfo[1];
			post = new HttpPost("https://hoapp.juneyaoair.com/v2/commonPersonService/commonPersonQuerys");
			String timeStamp = System.currentTimeMillis() + "";
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("channelCustomerNo",loginMap.get("id"));
			requestMap.put("loginKeyInfo",loginMap.get("loginKeyInfo"));
			requestMap.put("pageNo",1);
			requestMap.put("pageSize",100);
			requestMap.put("depCityCode","");
			requestMap.put("arrCityCode","");
			requestMap.put("isGmjc","N");
			requestMap.put("departureDate","");
			requestMap.put("returnDate","");
			List<String> cabinTypes = new ArrayList<>();
			cabinTypes.add("MEMBER_FARE");
			requestMap.put("cabinTypes",cabinTypes);
			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put("channelCode","MOBILE");
			reqMap.put("platformInfo","android");
			reqMap.put("clientVersion","6.3.0");
			reqMap.put("versionCode",63000);
			reqMap.put("token",loginMap.get("token"));
			reqMap.put("request",requestMap);
			String reqJson = JSON.toJSONString(reqMap);
			logger.info(httpKey + "HO查询联系人参数:" + reqJson);
			StringEntity s = new StringEntity(reqJson, "utf-8");
			post.setConfig(defaultRequestConfig);
			post.setEntity(s);
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
			post.setHeader( "Proxy-Authorization" , "Basic " + Base64.encodeBase64String(new StringBuilder( proxyUser ).append( ":" ).append( proxyPass ).toString().getBytes( "utf-8" ) ) );
			post.setHeader( "Proxy-Connection" , "keep-alive" );
			response = httpclient.execute(post);
			String back = EntityUtils.toString(response.getEntity(),"utf-8");
			logger.info(httpKey + "查询常用联系人信息返回:" + back);
			if (StringUtils.isEmpty(back) || !back.contains("resultCode")) {
				return passMsgMap;
			}
			JSONObject backObj = new JSONObject(back);
			String resultCode = backObj.getString("resultCode");
			if ("10001".equals(resultCode)) {
				JSONArray passengers = orderjson.getJSONArray("passengers");
				JSONArray commonPersonInfoList = backObj.getJSONObject("objData").getJSONArray("commonPersonInfoList");
				for (int k = 0; k < passengers.length(); k++) {
					JSONObject passObj = passengers.getJSONObject(k);
					String name = passObj.getString("passengerName");
					for (int j = 0; j < commonPersonInfoList.length(); j++) {
						JSONObject commonPerson = commonPersonInfoList.getJSONObject(j);
						if (name.equals(commonPerson.getString("passengerName"))) {
							String commonContactId =  commonPerson.getString("commonContactId");
							String generalContactCertId = "";
							String generalContactId = "";
							JSONArray contactCertList = commonPerson.getJSONArray("contactCertList");
							for (int i = 0; i < contactCertList.length(); i++) {
								JSONObject contactCertObj = contactCertList.getJSONObject(i);
								generalContactCertId = contactCertObj.getString("generalContactCertId");
								if (StringUtils.isNotEmpty(generalContactCertId)) {
									generalContactId = contactCertObj.getString("generalContactId");
									break;
								}
							}
							String value = commonContactId + "#" + generalContactCertId + "#" + generalContactId;
							passMsgMap.put(name,value);
							break;
						}
					}
				}
			} else {
				String resultInfo = backObj.getString("resultInfo");
				passMsgMap.put("error", resultInfo);
			}
		} catch (Exception e) {
			// logger.info(httpKey + "查询常用联系人error:",e);
			e.printStackTrace();
			logger.error(httpKey + "查询常用联系人error:",e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				if (post != null) {
					post.releaseConnection();
				}
			} catch (IOException e) {
			}
		}
		return passMsgMap;
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
            logger.info( orderId + ":" + result + message );
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

            logger.info( buffer.toString() );
            String content = OfficialMain.setRequestParams( buffer.toString() );
            if (content != null) {
                String rs1[] = content.split( "#@_@#" );
                if (rs1.length == 2) {
                    content = rs1[1];
                    return content;
                }
                if (rs1.length == 3) {
                    logger.info( rs1[2] );
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
            logger.info( orderId + status );
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
                    logger.info( rs1[2] );
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
            logger.info( orderId + accountInfo );
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
                    logger.info( rs1[2] );
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
