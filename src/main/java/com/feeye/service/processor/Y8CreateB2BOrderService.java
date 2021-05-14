package com.feeye.service.processor;

import com.feeye.util.PropertiesUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
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
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class Y8CreateB2BOrderService {
	private static final Logger logger = Logger.getLogger(Y8CreateB2BOrderService.class);
	private static final int timeout = 60000;
	private static Map<String, String> accountY8b2b = new HashMap<String, String>();
	String dlyAccount = PropertiesUtils.getPropertiesValue("config", "dlyAccount");
	
	public static void main(String[] args) throws Exception {
		String back = "{\n" +
				"  \"account\": \"LVMAMA03_LVMAMA-wnz665868@\",\n" +
				"  \"airline\": \"Y8\",\n" +
				"  \"bigpnr\": \"NZWFJD\",\n" +
				"  \"billNo\": \"FY200529171109159074346923218285\",\n" +
				"  \"childrenMobile\": \"\",\n" +
				"  \"code\": \"\",\n" +
				"  \"codePassword\": \"\",\n" +
				"  \"creditNo\": \"\",\n" +
				"  \"cvv\": \"E357951+\",\n" +
				"  \"drawerType\": \"singleOfficial\",\n" +
				"  \"email\": \"\",\n" +
				"  \"flights\": [\n" +
				"    {\n" +
				"      \"airline\": \"3U\",\n" +
				"      \"arrival\": \"CTU\",\n" +
				"      \"cabin\": \"R\",\n" +
				"      \"departure\": \"NNG\",\n" +
				"      \"departureDate\": \"2020-05-30\",\n" +
				"      \"departureTime\": \"11:40:00+08:00\",\n" +
				"      \"fType\": \"go\",\n" +
				"      \"flightNo\": \"3U8774\",\n" +
				"      \"price\": \"240\"\n" +
				"    }\n" +
				"  ],\n" +
				"  \"id\": \"51118046\",\n" +
				"  \"ifUsedCoupon\": false,\n" +
				"  \"isOutticket\": \"true\",\n" +
				"  \"linkMan\": \"梁丽丽\",\n" +
				"  \"mainusername\": \"policytest\",\n" +
				"  \"matchCabin\": false,\n" +
				"  \"mobile\": \"13818225824\",\n" +
				"  \"orderNo\": \"577784965\",\n" +
				"  \"orderTime\": \"2020-05-29 17:11:09\",\n" +
				"  \"otheraccount\": \"119833_0WHLSGcqiam0CqYnkYMNOiC75Ne28ZrA\",\n" +
				"  \"passengers\": [\n" +
				"    {\n" +
				"      \"birthday\": \"1974-04-04 00:00:00+08:00\",\n" +
				"      \"id\": \"62209018\",\n" +
				"      \"idcard\": \"432501197404044550\",\n" +
				"      \"passengerCardType\": \"身份证\",\n" +
				"      \"passengerName\": \"蒋志辉\",\n" +
				"      \"passengerSex\": \"男\",\n" +
				"      \"passengerType\": \"成人\"\n" +
				"    }\n" +
				"  ],\n" +
				"  \"payType\": \"yfk\",\n" +
				"  \"platType\": \"xiecheng\",\n" +
				"  \"qiangpiao\": \"2\",\n" +
				"  \"username\": \"shxtly\"\n" +
				"}";
		new Y8CreateB2BOrderService().StartCreateOrder(back, 0, 0, 0);
		 StringBuilder sb = new StringBuilder();
			File file = new File("C:\\Users\\Administrator\\Desktop\\汇付签约.html");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ( (line = reader.readLine())!=null ) {
				if(line !=""){
					sb.append(line);
				}else{
					continue;
				}
			}
			String content = sb.toString();
		new U3CreateB2BOrderService().parseDetail(content, "", "");
	}

	public String StartCreateOrder(String orderJson, int retryCount, int loginRetryCount, int requestType) {
		String cancelUrl = PropertiesUtils.getPropertiesValue("config", "cancelUrl");
		if (StringUtils.isEmpty(orderJson)) {
			return "ERROR:数据不完整";
		}
		logger.info("传过来的数据有" + orderJson);
		String session = UUID.randomUUID().toString();
		SSLConnectionSocketFactory sslsf = null;
		BasicCookieStore cookieStore = new BasicCookieStore();// 一个cookies
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (Exception e) {
			logger.error("error", e);
		}
		BasicScheme proxyAuth = new BasicScheme();
		try {
			proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH , "BASIC realm=default"));
		} catch (MalformedChallengeException e1) {
			logger.error("error" , e1);
		}
		//代理云ip
		String ip_port = DailiyunService.getRandomIp(50);
		String proxyIp = ip_port.split(":")[0];
		int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
		HttpHost dailiyunProxy = new HttpHost(proxyIp , proxyPort , "http");

		BasicAuthCache authCache = new BasicAuthCache();
		authCache.put(dailiyunProxy , proxyAuth);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		String[] dailiyunAccounts = dlyAccount.split(":");
		credsProvider.setCredentials(new AuthScope(dailiyunProxy) , new UsernamePasswordCredentials(dailiyunAccounts[0] , dailiyunAccounts[1]));

		RequestConfig config = RequestConfig.custom()
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.setProxy(dailiyunProxy)
				.setStaleConnectionCheckEnabled(true)
				.build();

		HttpClientContext context = HttpClientContext.create();
		context.setAuthCache(authCache);
		context.setRequestConfig(config);
		context.setCredentialsProvider(credsProvider);

		//配置连接策略
		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response , HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator
						( response.headerIterator( HTTP.CONN_KEEP_ALIVE ) );
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase
							( "timeout" )) {
						return Long.parseLong( value ) * 1000;
					}
				}
				return 60 * 1000;//如果没有约定，则默认定义时长为60s
			}
		};

//		SSLContext sslcontext = SSLContexts.createDefault();
//		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" },
//				null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", sslsf)  // 用来配置支持的协议
				.build();
		// 共享连接池
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		//最大连接数
		connectionManager.setMaxTotal(100);
		//每个连接的路由数
		connectionManager.setDefaultMaxPerRoute(20);
		SocketConfig socketConfig = SocketConfig.custom()
				.setSoKeepAlive(false)
				.setSoLinger(1)
				.setSoReuseAddress(true)
				.setSoTimeout(timeout)
				.setTcpNoDelay(true).build();

		CloseableHttpClient httpclient = HttpClientBuilder.create()
				.setKeepAliveStrategy(myStrategy)
				.setConnectionManager(connectionManager)
				.setConnectionManagerShared(true)
				.setDefaultCookieStore(cookieStore)
				.setDefaultSocketConfig(socketConfig)
				.setDefaultCredentialsProvider(credsProvider)
				.build();

		String cookie = "";
		String mainUser = "";
		String childrenUser = "";
		String order_id = "";
		String billNo = "";
		try {
			JSONObject json = new JSONObject(orderJson);
			JSONArray passengers = json.getJSONArray("passengers");
			order_id = json.getString("id");
			String bigpnr = json.getString("bigpnr");
			mainUser = json.getString( "mainusername" );
			String platType = "";
			try {
				platType = json.getString("platType");
			} catch (Exception e) {
			}
			childrenUser = json.getString("username");
			String payPassword = json.getString("cvv");
			String account = json.getString("account");
			String key = account.split("_")[0] + "@_@" + account.split("_")[1];
			String price = "";
			String ticketPrice = "";
			try {
				billNo = json.getString("billNo");
			} catch (Exception e) {
			}
			if("shxtly".equals(mainUser)&&!"xiecheng".equals(platType)){
				sendCreateOrderInfo("error", "当前平台暂无出票权限，请联系管理员", "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + "当前平台暂无出票权限，请联系管理员");
				return "当前用户暂无权限出票";
			}
			if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
				//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
				sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + "已取消出票");
				return "取消出票";
			}
			//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "检查登录状态" );
			cookie = accountY8b2b.get(key);
			if (StringUtils.isEmpty(cookie)) {
				//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "开始登录" );
				cookie = login(httpclient, config, cookieStore, cookie, orderJson, session, context);
				if(StringUtils.isNotEmpty(cookie)&&!cookie.contains("error")&&!cookie.contains("ERROR")&&!cookie.contains("任务超时")){
					accountY8b2b.put(key, cookie);
				}
			}
			if (StringUtils.isEmpty(cookie)) {
				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
					//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
							"true", billNo, requestType, ticketPrice);
					logger.info(order_id + "已取消出票");
					return "取消出票";
				}
				accountY8b2b.remove(key);
				if (loginRetryCount < 5) {
					loginRetryCount++;
					return StartCreateOrder(orderJson, retryCount, loginRetryCount, requestType);
				} else {
					/*sendCreateOrderInfo("error", "登录失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",
							"true", billNo, requestType, ticketPrice);*/
					logger.info(order_id + "登录失败");
					return "error:登录失败";
				}
			}
			if (cookie.contains("error") || cookie.contains("ERROR") || cookie.contains("任务超时")) {
				sendCreateOrderInfo("error", cookie, "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + cookie);
				return "error:" + cookie;
			}
			if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
				//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
				sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + "已取消出票");
				return "取消出票";
			}
			Map<String, String> PnrQueryParamMap = null;
			//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "PNR查询" );
			PnrQueryParamMap = PNRQuery(httpclient, config, cookie, orderJson);
			if (PnrQueryParamMap == null || PnrQueryParamMap.size() == 0) {
				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
					//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
							"true", billNo, requestType, ticketPrice);
					logger.info(order_id + "已取消出票");
					return "取消出票";
				}
				if (retryCount < 5) {
					accountY8b2b.remove(key);
					retryCount++;
					return StartCreateOrder(orderJson, retryCount, 0, requestType);
				} else {
					accountY8b2b.remove(key);
					sendCreateOrderInfo("error", "PNR查询失败", "", childrenUser, "", order_id, "", "", null, "", "",
							"false", "true", billNo, requestType, ticketPrice);
					logger.info(order_id + "PNR查询失败");
					return "error:PNR查询失败";
				}
			}
			config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setProxy(dailiyunProxy).setExpectContinueEnabled(false)
					.setStaleConnectionCheckEnabled(true).build();
			if (PnrQueryParamMap != null && StringUtils.isNotEmpty(PnrQueryParamMap.get("error"))) {
				sendCreateOrderInfo("error", PnrQueryParamMap.get("error"), "", childrenUser, "", order_id, "", "",
						null, "", "", "false", "true", billNo, requestType, ticketPrice);
				logger.info(order_id + PnrQueryParamMap.get("error"));
				return "取消出票";
			}
			ticketPrice = PnrQueryParamMap.get("adultPrice");
			PnrQueryParamMap.remove("adultPrice");
			try {
				price = PnrQueryParamMap.get("fareAmount");
				PnrQueryParamMap.remove("fareAmount");
			} catch (Exception e) {
			}
			String order_pnr_id = "";
			String orderNo = "";
			if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
				//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
				sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + "已取消出票");
				return "取消出票";
			}
			//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "PNR入库" );
			Map<String, String> PnrHostinParamMap = PnrHostin(httpclient, config, cookie, orderJson, PnrQueryParamMap,context);
			if (PnrHostinParamMap == null) {
				PnrHostinParamMap = new HashMap<String, String>();
				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
					//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
							"true", billNo, requestType, ticketPrice);
					logger.info(order_id + "已取消出票");
					return "取消出票";
				}
				//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "查询订单" );
				Map<String, String> frontOrderQueryMap = null;
				int index = 0;
				for (; index < 3; index++) {
					frontOrderQueryMap = frontOrderQuery(httpclient, config, cookie, orderJson,context);
					if (frontOrderQueryMap != null) {
						break;
					}
				}
				if (frontOrderQueryMap == null) {
					if (retryCount < 10) {
						retryCount++;
						return StartCreateOrder(orderJson, retryCount, 0, requestType);
					} else {
						accountY8b2b.remove(key);
						sendCreateOrderInfo("error", "查询订单异常", "", childrenUser, "", order_id, "", "", null, "", "",
								"false", "true", billNo, requestType, ticketPrice);
						logger.info(order_id + "查询订单异常");
						return "error:查询订单异常";
					}
				}
				if (frontOrderQueryMap != null && "未查询到数据".equals(frontOrderQueryMap.get("error"))) {
					if (retryCount < 10) {
						retryCount++;
						return StartCreateOrder(orderJson, retryCount, 0, requestType);
					} else {
						accountY8b2b.remove(key);
						sendCreateOrderInfo("error", frontOrderQueryMap.get("error"), "", childrenUser, "", order_id,
								"", "", null, "", "", "false", "true", billNo, requestType, ticketPrice);
						logger.info(order_id + frontOrderQueryMap.get("error"));
						return "error:" + frontOrderQueryMap.get("error");
					}
				}
				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
					//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
							"true", billNo, requestType, ticketPrice);
					logger.info(order_id + "已取消出票");
					return "取消出票";
				}
				String result = "";
				index = 0;
				for (; index < 3; index++) {
					result = frontOrderDetailQuery(httpclient, config, cookie, orderJson, frontOrderQueryMap, context);
					if (StringUtils.isNotEmpty(result)) {
						break;
					}
				}
				if (StringUtils.isEmpty(result)) {
					if (retryCount < 10) {
						retryCount++;
						return StartCreateOrder(orderJson, retryCount, 0, requestType);
					} else {
						accountY8b2b.remove(key);
						sendCreateOrderInfo("error", "查询详情异常", "", childrenUser, "", order_id, "", "", null, "", "",
								"false", "true", billNo, requestType, ticketPrice);
						logger.info(order_id + "查询详情异常");
						return "error:查询详情异常";
					}
				}
				Document doc = Jsoup.parse(result);
				price = doc.getElementsByClass("tableinfo1").get(1).getElementsByTag("tr").get(1).getElementsByTag("td")
						.get(7).text();
				order_pnr_id = doc.getElementsByClass("order_pnr_id").get(0).val();
				orderNo = frontOrderQueryMap.get("recNo");
				String payOrders = orderNo + "|" + bigpnr + "-" + order_pnr_id + "^";
				PnrHostinParamMap.put("payOrders", payOrders);
				PnrHostinParamMap.put("payMethod", "1");
				PnrHostinParamMap.put("etdzType", "1");
				PnrHostinParamMap.put("paymethodradio1", "1");
				PnrHostinParamMap.put("vpPasswd", payPassword);
				PnrHostinParamMap.put("weblinkPswd", "");
				PnrHostinParamMap.put("cdpvpaypswd", "");
			} else {
				Map<String, String> frontOrderQueryMap = frontOrderQuery(httpclient, config, cookie, orderJson, context);
				orderNo = frontOrderQueryMap.get("recNo");
			}
			sendCreateOrderInfo("success", "", "", childrenUser, orderNo, order_id, "", "", null, "", "", "false",
					"true", billNo, requestType, ticketPrice);
			if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
				//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
				sendCreateOrderInfo("error", "已取消出票", "", childrenUser, orderNo, order_id, "", "", null, "", "",
						"false", "true", billNo, requestType, ticketPrice);
				logger.info(order_id + "已取消出票");
				return "取消出票";
			}
			// 进行支付
			String orderInfo = "";
			//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "开始支付" );
			for (int i = 0; i < 3; i++) {
				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
					//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, orderNo, order_id, "", "", null, "", "",
							"false", "true", billNo, requestType, ticketPrice);
					logger.info(order_id + "已取消出票");
					return "取消出票";
				}
				orderInfo = virtualPay(httpclient, config, cookie, orderJson, PnrHostinParamMap,context);
				if ("success".equalsIgnoreCase(orderInfo)) {
					//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "查询订单" );
					if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
						//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
						sendCreateOrderInfo("error", "已取消出票", "", childrenUser, orderNo, order_id, "", "", null, "", "",
								"false", "true", billNo, requestType, ticketPrice);
						logger.info(order_id + "已取消出票");
						return "取消出票";
					}
					Map<String, String> frontOrderQueryMap = frontOrderQuery(httpclient, config, cookie, orderJson, context);
					if (frontOrderQueryMap != null && "未查询到数据".equals(frontOrderQueryMap.get("error"))) {
						if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
							//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
							sendCreateOrderInfo("error", "已取消出票", "", childrenUser, orderNo, order_id, "", "", null, "",
									"", "false", "true", billNo, requestType, ticketPrice);
							logger.info(order_id + "已取消出票");
							return "取消出票";
						}
						if (retryCount < 10) {
							retryCount++;
							return StartCreateOrder(orderJson, retryCount, 0, requestType);
						} else {
							accountY8b2b.remove(key);
							sendCreateOrderInfo("error", "支付未知异常，请确认是否已经支付", "", childrenUser, orderNo, order_id, "",
									"", null, "", "", "false", "true", billNo, requestType, ticketPrice);
							logger.info(order_id + "支付未知异常，请确认是否已经支付");
							return "error:支付未知异常，请确认是否已经支付";
						}
					}
					String result = frontOrderDetailQuery(httpclient, config, cookie, orderJson, frontOrderQueryMap, context);
					Document doc = Jsoup.parse(result);
					Element table = doc.getElementsByClass("tableinfo1").get(0);
					Element tr = table.getElementsByTag("tr").get(1);
					String tdHtml = tr.getElementsByTag("td").get(7).html();
					if (tdHtml.contains("出票完成")) {
						StringBuilder sbu = new StringBuilder();
						Document document = Jsoup.parse(result);
						Element msgTable = doc.getElementsByClass("tableinfo1").get(4);
						Elements checkInChooses = msgTable.getElementsByTag("tr");
						String recNo = frontOrderQueryMap.get("recNo");
						String pnrNo = json.getString("bigpnr");
						for(int index=1;index<checkInChooses.size();index++){
							Element checkInChoose = checkInChooses.get(index);
							String passengerName = checkInChoose.getElementById("psgname" + (index-1)).text();
							// String passengerName = document.getElementsByAttributeValue("name", "psgName").get(index).val();
							String passengerInfo = document.getElementsByAttributeValue("name", "docNo").get(index).val();
							// String ticketNo = document.getElementsByAttributeValue("name", "tktNumber").get(index).val();
							String ticketNo = checkInChoose.getElementsByClass("tktdetail").get(0).attr("tktno");
							sbu.append(passengerName).append("##").append(passengerInfo).append("##").append(ticketNo).append("##")
							.append(recNo).append("##").append(pnrNo).append("#_#");
							logger.info("乘客："+passengerName+"-"+passengerInfo+",票号："+ticketNo);
							System.out.println("乘客："+passengerName+"-"+passengerInfo+",票号："+ticketNo);
							logger.info("订单号：" + recNo + ",编码：" + pnrNo);
						}
						orderInfo =  sbu.toString() + "@_@";
						break;
					} else if (tdHtml.contains("等待出票")) {
						//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "手动出票" );
						frontOrderQueryMap = frontOrderQuery(httpclient, config, cookie, orderJson, context);
						result = frontOrderDetailQuery(httpclient, config, cookie, orderJson, frontOrderQueryMap, context);
						doc = Jsoup.parse(result);
						order_pnr_id = doc.getElementsByClass("order_pnr_id").get(0).val();
						orderInfo = orderTicketOut(httpclient, config, cookie, orderJson, bigpnr, orderNo,
								order_pnr_id,context);
						break;
					} else if (tdHtml.contains("等待支付")) {
						//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "重新支付" );
						orderInfo = virtualPay(httpclient, config, cookie, orderJson, PnrHostinParamMap, context);
						if (StringUtils.isEmpty(orderInfo)) {
							continue;
						}
					}
				}
				if("虚拟账户余额不足".equals(orderInfo)){
					sendCreateOrderInfo("error", orderInfo, "", childrenUser, orderNo, order_id, "", "", null, "", "", "false",
							"true", billNo, requestType,ticketPrice);
				}
				if (orderInfo.contains("操作失败")) {
					//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "手动出票" );
					Map<String,String> frontOrderQueryMap = frontOrderQuery(httpclient, config, cookie, orderJson, context);
					String result = frontOrderDetailQuery(httpclient, config, cookie, orderJson, frontOrderQueryMap, context);
					Document doc = Jsoup.parse(result);
					order_pnr_id = doc.getElementsByClass("order_pnr_id").get(0).val();
					orderInfo = orderTicketOut(httpclient, config, cookie, orderJson, bigpnr, orderNo, order_pnr_id, context);
				}
				break;
			}
			sendCreateOrderInfo("success", "", "", childrenUser, orderNo, order_id, "true", "", null, "预付款", "",
					"false", "true", billNo, requestType, ticketPrice);
			//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "获取票号" );
			if(StringUtils.isEmpty(orderInfo)){
				Map<String, String> frontOrderQueryMap = frontOrderQuery(httpclient, config, cookie, orderJson, context);
				String detailresult = frontOrderDetailQuery(httpclient, config, cookie, orderJson, frontOrderQueryMap, context);
				orderInfo = parseDetail(detailresult,orderJson,orderNo);
			}
			int count = 5;
			while (count > 0) {
				try {
					if (StringUtils.isNotEmpty(orderInfo)) {
						String back = orderInfo + orderNo;
						String ticketnos[] = back.split("@_@");
						if (ticketnos.length == 2) {
							//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "回填票号" );
							// 判断获取的票号数是否与乘客数量相符合,不相符合重新
							String[] ticketCount = ticketnos[0].split("#_#");
							if (ticketCount.length == passengers.length() && !ticketnos[0].contains("null")) {
								sendCreateOrderInfo("success", "", price, childrenUser, orderNo, order_id, "", "true",
										ticketnos[0], "预付款", ticketnos[1], "false", "true", billNo, requestType,
										ticketPrice);
								return "SUCCESS";
							}
						}
					} else {
						Map<String, String> frontOrderQueryMap = frontOrderQuery(httpclient, config, cookie, orderJson, context);
						String detailresult = frontOrderDetailQuery(httpclient, config, cookie, orderJson,
								frontOrderQueryMap,context);
						orderInfo = parseDetail(detailresult, orderJson, orderNo);
					}
				} catch (Exception e) {
					logger.error("error",e);
				}
				orderInfo = "";
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					logger.error("error", e);
				}
				count--;
			}
			if (count <= 0) {
				sendCreateOrderInfo("error", "未找到票号信息", "", childrenUser, orderNo, order_id, "", "", null, "", "",
						"false", "true",billNo,requestType,ticketPrice);
				return "SUCCESS#@@#未找到票号信息";
			}
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if(httpclient!=null){
				try {
					httpclient.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}
		return "";
	}

	private String parseDetail(String detailresult, String orderJson,String orderNo) {
		try {
			StringBuilder sbu = new StringBuilder();
			JSONObject json = new JSONObject(orderJson);
			String bigpnr = json.getString("bigpnr");
			JSONArray passengers = json.getJSONArray("passengers");
			Document doc = Jsoup.parse(detailresult);
			for(int i=0;i<passengers.length();i++){
				JSONObject passenger = passengers.getJSONObject(i);
				String sysPassengerName = passenger.getString("passengerName");
				for(int j=0;j<passengers.length();j++){
					String passengerName = doc.getElementById("psgname"+j).text();
					if(sysPassengerName.equals(passengerName)){
						String passengerInfo = doc.getElementById("docid"+j).text().replace(" ", "").replace("/", "");
						String ticketNo = doc.getElementsByClass("tktdetail").get(j).attr("tktno");
						sbu.append(passengerName).append("##").append(passengerInfo).append("##").append(ticketNo).append("##").append(orderNo).append("##").append(bigpnr).append("#_#");
					}
				}
			}
			return sbu.toString()+"@_@";
		} catch (Exception e) {
			logger.error("error",e);
		}
		return null;
	}
	private String orderTicketOut(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson,
			String bigpnr, String orderno, String order_pnr_id, HttpClientContext context) {
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost("/y8b2b/OrderTicketOut.do");
			HttpHost target = new HttpHost("y8b2b.travelsky.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("recNo", orderno));
			nameValue.add(new BasicNameValuePair("bpnrNo", bigpnr));
			nameValue.add(new BasicNameValuePair("orderPnrId", order_pnr_id));
			post.setConfig(config);
			logger.info("手动出票请求参数:" + nameValue.toString());
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			post.setHeader("Host", "y8b2b.travelsky.com");
			post.setHeader("Cookie", cookie);
			post.setHeader("Origin", "https://y8b2b.travelsky.com");
			post.setHeader("Referer", "https://y8b2b.travelsky.com/y8b2b/PnrHostin.do");
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, post, context);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info("手动出票返回:" + back);
			StringBuilder sbu = new StringBuilder();
			Document document = Jsoup.parse(back);
			Elements trs = document.getElementsByClass("tableinfo1").get(0).getElementsByTag("tr");
			for (int i = 1; i < trs.size(); i++) {
				String passengerName = trs.get(i).getElementsByTag("td").get(0).text();
				String passengerInfo = trs.get(i).getElementsByTag("td").get(2).text();
				String ticketNo = trs.get(i).getElementsByTag("td").get(4).text();
				String recNo = document.getElementsByAttributeValue("name", "recNo").val();
				String pnrNo = document.getElementsByAttributeValue("name", "pnrNo").val();
				sbu.append(passengerName).append("##").append(passengerInfo).append("##").append(ticketNo).append("##")
						.append(recNo).append("##").append(pnrNo).append("#_#");
				logger.info("乘客：" + passengerName + "-" + passengerInfo + ",票号：" + ticketNo);
			}
			return sbu.toString() + "@_@";
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if(response!=null){
				try {
					response.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}
		return null;
	}

	private String frontOrderDetailQuery(CloseableHttpClient httpclient, RequestConfig config, String cookie,
			String orderJson, Map<String, String> paramMap, HttpClientContext context) {
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost("/y8b2b/FrontOrderDetailQuery.do");
			HttpHost target = new HttpHost("y8b2b.travelsky.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
				nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			post.setConfig(config);
			logger.info("查询订单详情请求参数:" + nameValue.toString());
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			post.setHeader("Host", "y8b2b.travelsky.com");
			post.setHeader("Cookie", cookie);
			post.setHeader("Origin", "https://y8b2b.travelsky.com");
			post.setHeader("Referer", "https://y8b2b.travelsky.com/y8b2b/FrontOrderQuery.do");
			post.setHeader("Proxy-Authorization" , "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, post, context);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("查询订单详情返回:" + back);
			return back;
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}
		return null;
	}

	private Map<String, String> frontOrderQuery(CloseableHttpClient httpclient, RequestConfig config, String cookie,
			String orderJson, HttpClientContext context) {
		Map<String, String> resultMap = new HashMap<String, String>();
		CloseableHttpResponse response = null;
		try {
			JSONObject json = new JSONObject(orderJson);
			String bigPnr = json.getString("bigpnr");
			HttpPost post = new HttpPost("/y8b2b/FrontOrderQuery.do");
			HttpHost target = new HttpHost("y8b2b.travelsky.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("dateBegin", ""));
			nameValue.add(new BasicNameValuePair("dateEnd", ""));
			nameValue.add(new BasicNameValuePair("isGrp", "A"));
			nameValue.add(new BasicNameValuePair("dateType", "0"));
			nameValue.add(new BasicNameValuePair("orderType", "A"));
			nameValue.add(new BasicNameValuePair("recNo", ""));
			nameValue.add(new BasicNameValuePair("orderStatus", "A"));
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
			logger.info("查询请求参数:" + nameValue.toString());
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			post.setHeader("Host", "y8b2b.travelsky.com");
			post.setHeader("Cookie", cookie);
			post.setHeader("Origin", "https://y8b2b.travelsky.com");
			post.setHeader("Referer", "https://y8b2b.travelsky.com/y8b2b/FrontOrderQuery.do");
			post.setHeader("Proxy-Authorization" , "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, post, context);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("查询返回:" + back);
			if (back.contains("未查询到数据")) {
				resultMap.put("error", "未查询到数据");
				return resultMap;
			}
			Document doc = Jsoup.parse(back);
			String recNo = doc.getElementsByAttributeValue("name", "showdetail").html();
			String userId = doc.getElementsByAttributeValue("value", recNo).attr("id");
			// String queryType = doc.getElementById("queryType").val();
			resultMap.put("recNo", recNo);
			resultMap.put("userId", userId);
			resultMap.put("queryType", "recNodetailquery");
			resultMap.put("isCDS", "");
			response.close();
			return resultMap;
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}
		return null;
	}

	private String virtualPay(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson,
			Map<String, String> paramMap, HttpClientContext context) {
		CloseableHttpResponse response = null;
		try {
			JSONObject json = new JSONObject(orderJson);
			JSONArray passengers = json.getJSONArray("passengers");
			HttpPost post = new HttpPost("/y8b2b/VirtualPay.do");
			HttpHost target = new HttpHost("y8b2b.travelsky.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
				nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			logger.info("支付请求参数:" + nameValue.toString());
			post.setConfig(config);
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			post.setHeader("Host", "y8b2b.travelsky.com");
			post.setHeader("Cookie", cookie);
			post.setHeader("Origin", "https://y8b2b.travelsky.com");
			post.setHeader("Referer", "https://y8b2b.travelsky.com/y8b2b/PnrHostin.do");
			post.setHeader("Proxy-Authorization" , "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, post, context);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info("支付返回:" + back);
			if(back.contains("操作失败")&&back.contains("余额不足")){
				return "虚拟账户余额不足";
			}
			if(back.contains("操作失败")) {
				String errorMsg = "操作失败！";
				Document document = Jsoup.parse(back);
				Elements fb = document.getElementsByClass("ts_m1_dis");
				for(Element fbEle:fb) {
					errorMsg = errorMsg +  fbEle.html() + " ";
				}
				return errorMsg;
			} else if(back.contains("已完成出票")) {
				back = "success";
				return back;
			}
			StringBuilder sbu = new StringBuilder();
			Document document = Jsoup.parse(back);
			Elements trs = document.getElementsByClass("tableinfo1").get(0).getElementsByTag("tr");
			for (int i = 1; i < trs.size()-1; i++) {
				String passengerName = trs.get(i).getElementsByAttributeValue("name", "psgName").val();
				String passengerInfo = trs.get(i).getElementsByAttributeValue("name", "docNo").val();
				String ticketNo = trs.get(i).getElementsByAttributeValue("name", "tktNumber").val();
				String recNo = document.getElementsByAttributeValue("name", "recNo").val();
				String pnrNo = document.getElementsByAttributeValue("name", "pnrNo").val();
				logger.info("乘客：" + passengerName + "-" + passengerInfo + ",票号：" + ticketNo);
				if(StringUtils.isNotEmpty(ticketNo)){
					sbu.append(passengerName).append("##").append(passengerInfo).append("##").append(ticketNo).append("##")
						.append(recNo).append("##").append(pnrNo).append("#_#");
				}
			}
			if(sbu.toString().split("#_#").length==passengers.length()){
				return sbu.toString() + "@_@";
			}else{
				return "操作失败";
			}
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if(response!=null){
				try {
					response.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}
		return null;
	}

	private Map<String, String> PnrHostin(CloseableHttpClient httpclient, RequestConfig config, String cookie,
			String orderJson, Map<String, String> paramMap, HttpClientContext context) {
		Map<String, String> resultMap = new HashMap<String, String>();
		CloseableHttpResponse response = null;
		try {
			JSONObject json = new JSONObject(orderJson);
			String cvv = json.getString("cvv");
			HttpPost post = new HttpPost("/y8b2b/PnrHostin.do");
			HttpHost target = new HttpHost("y8b2b.travelsky.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
				nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			post.setConfig(config);
			logger.info("PnrHostin请求参数:" + nameValue.toString());
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			post.setHeader("Host", "y8b2b.travelsky.com");
			post.setHeader("Cookie", cookie);
			post.setHeader("Referer", "https://y8b2b.travelsky.com/y8b2b/PNRQuery.do");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Origin", "https://y8b2b.travelsky.com");
			post.setHeader("Proxy-Authorization" , "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, post,context);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info("PnrHostin返回：" + back);
			Document document = Jsoup.parse(back);
	/*		String randomNum = document.getElementsByAttributeValue("name", "randomNum").get(0).val();
			response.close();
			Thread.sleep(800);

			post = new HttpPost("/y8b2b/PnrHostin.do");
			nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("randomNum", randomNum));
			post.setConfig(config);
			logger.info("randomNum请求参数:" + nameValue.toString());
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			post.setHeader("Host", "y8b2b.travelsky.com");
			post.setHeader("Cookie", cookie);
			post.setHeader("Referer", "https://y8b2b.travelsky.com/y8b2b/PnrHostin.do");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Origin", "https://y8b2b.travelsky.com");
			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, post, context);
			back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info("randomNum返回：" + back);
			response.close();

			Thread.sleep(2000);
			document = Jsoup.parse(back);*/
			resultMap.put("payOrders", document.getElementById("payOrders").val());
			resultMap.put("etdzType", "1");
			resultMap.put("payMethod", "1");
			resultMap.put("vpPasswd", cvv);
			resultMap.put("cdpvpaypswd", "");
			resultMap.put("payOperType", document.getElementById("payOperType").val());
			return resultMap;
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}
		return null;
	}

	private Map<String, String> PNRQuery(CloseableHttpClient httpclient, RequestConfig config, String cookie,
			String orderJson) {
		Map<String, String> resultMap = new HashMap<String, String>();
		CloseableHttpResponse response = null;
		try {
			config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
					.setStaleConnectionCheckEnabled(true).build();
			JSONObject json = new JSONObject(orderJson);
			JSONArray passengers = json.getJSONArray("passengers");
			String price = json.getJSONArray("flights").getJSONObject(0).getString("price");
			String bigPnr = json.getString("bigpnr");
			String mainUserName = json.getString("mainusername");
			HttpGet get = new HttpGet("/y8b2b/PnrImportIndex.do");
			get.setConfig(config);
			HttpHost target = new HttpHost("y8b2b.travelsky.com", 443, "https");
			get.setHeader("Host", "y8b2b.travelsky.com");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			get.setHeader("Cookie", cookie);
			response = httpclient.execute(target, get);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("首页返回：" + back);
			System.out.println("首页返回：" + back);
			Thread.sleep(2000);
			response.close();
			HttpPost post = new HttpPost("/y8b2b/PNRQuery.do");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("BPnrNo", bigPnr));
			nameValue.add(new BasicNameValuePair("interFit", ""));
			nameValue.add(new BasicNameValuePair("orderType", "0"));
			nameValue.add(new BasicNameValuePair("isSotoChecked", ""));
			// logger.info("pnr查询请求参数：" + nameValue.toString());
			post.setConfig(config);
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			post.setHeader("Host", "y8b2b.travelsky.com");
			post.setHeader("Cookie", cookie);
			post.setHeader("Referer", "https://y8b2b.travelsky.com/y8b2b/PnrImportIndex.do");
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, post);
			back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("pnr查询返回：" + back);
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
			Elements elements = document.getElementsByAttributeValue("name","adradio");
			float lowerPrice = 0;
			String value = "-1";
			for(Element element:elements){
				String adultPrice = element.attr("adultPrice");
				if("shxtly".equals(mainUserName)){
					try {
						if(Double.parseDouble(adultPrice)==(Float.parseFloat(price) / passengers.length())){
							lowerPrice = Float.parseFloat(adultPrice);
							value = element.attr("value");
							break;
						}
					} catch (Exception e) {
						logger.error("error",e);
					}
				}else{
					if(lowerPrice==0){
						lowerPrice = Float.parseFloat(adultPrice);
						value = element.attr("value");
					}
					if(lowerPrice>Float.parseFloat(adultPrice)){
						lowerPrice = Float.parseFloat(adultPrice);
						value = element.attr("value");
					}
				}
			}
			if(lowerPrice==0){
				resultMap.put("error", "当前所选运价"+(Float.parseFloat(price) / passengers.length())+"不存在");
				return resultMap;
			}
			String adultPrice = lowerPrice+"";
			String proxyPrice = document.getElementsByClass("tableinfo2").get(0).getElementsByTag("tr").get(1)
					.getElementsByTag("td").get(2).text().split("\\(")[0];
			// 比较运价
			if (Float.parseFloat(adultPrice) > (Float.parseFloat(price) / passengers.length())) {
				resultMap.put("error", "当前运价" + adultPrice + "高于票面价" + price);
				return resultMap;
			}
			resultMap.put("adultPrice", (Float.parseFloat(adultPrice) - Float.parseFloat(proxyPrice)) + ""); // 保存采购价
			response.close();
			post = new HttpPost("/y8b2b/DomesticPnrPriceCompute.do");
			nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("adult", value));
			nameValue.add(new BasicNameValuePair("child", "-1"));
			nameValue.add(new BasicNameValuePair("doajax", "yes"));
			nameValue.add(new BasicNameValuePair("gm", "-1"));
			nameValue.add(new BasicNameValuePair("infant", "-1"));
			nameValue.add(new BasicNameValuePair("jc", "-1"));
			nameValue.add(new BasicNameValuePair("randomNum", document.getElementById("rand4page").val()));
			nameValue.add(new BasicNameValuePair("vipCode", ""));
			logger.info("价格动态比较请求参数：" + nameValue.toString());
			post.setConfig(config);
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			post.setHeader("Host", "y8b2b.travelsky.com");
			post.setHeader("Cookie", cookie);
			post.setHeader("Referer", "https://y8b2b.travelsky.com/y8b2b/PNRQuery.do");
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, post);
			back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info("校验价格返回:" + back);
			try {
				document = Jsoup.parse(back);
				try {
					Element tableFNFC = document.getElementsByClass("tableFNFC").get(0);
					Element td = tableFNFC.getElementsByTag("tr").get(1).getElementsByTag("td").get(0);
					String checkPrice = td.text();
					if (Float.parseFloat(checkPrice) > (Float.parseFloat(price) / passengers.length())) {
						resultMap.put("error", "当前所选运价" + checkPrice + "高于票面价" + price);
						return resultMap;
					}
				} catch (Exception e) {
					logger.error("error");
				}
				String fareAmount = document.getElementById("fareAmont").val();
				resultMap.put("fareAmount", fareAmount); //保存结算价
			} catch (Exception e) {
				logger.error("error",e);
			}
			response.close();
			Thread.sleep(2000);
			return resultMap;
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}
		return null;
	}

	public String login(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore,
			String cookie, String orderJson, String session, HttpClientContext context) {

		HttpGet get = null;
		HttpPost post = null;
		HttpHost target = null;
		String mainUser = "";
		String childrenUser = "";
		String order_id = "";
		InputStream re = null;

		CloseableHttpResponse response = null;
		try {
			JSONObject json = new JSONObject(orderJson);
			String account = json.getString("account");
			String otheraccount = json.getString("otheraccount");
			String username = "b";
			String password = "b";
			if (otheraccount.contains("_") && otheraccount.split("_").length == 2) {
				username = otheraccount.split("_")[0];
				password = otheraccount.split("_")[1];
			}
			String userAccount[] = account.split("-");
			order_id = json.getString("id");
			mainUser = json.getString( "mainusername" );
			childrenUser = json.getString("username");
			get = new HttpGet(
					"/y8b2b/cas/authenticate.do?redirectUrl=https://y8b2b.travelsky.com/y8b2b/ssoClientLogin.do");
			get.setConfig(config);
			target = new HttpHost("y8b2b.travelsky.com", 443, "https");
			get.setHeader("Host", "y8b2b.travelsky.com");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			get.setHeader("Proxy-Authorization" , "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
			response = httpclient.execute(target, get, context);
			// String back = EntityUtils.toString(response.getEntity(),
			// "utf-8");
			List<Cookie> listCookie = cookieStore.getCookies();
			StringBuffer buf = new StringBuffer();

			if (null != listCookie && listCookie.size() > 0) {
				for (int i = 0; i < listCookie.size(); i++) {
					buf.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
				}
			}
			cookie = buf.toString();
			//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "识别验证码" );
			// 获取验证码请求
			response.close();
			// ?agentType=domestic&0.5855130690663959
			get = new HttpGet("/y8b2b/VerificationCode.do");
			get.setConfig(config);
			get.setHeader("Host", "y8b2b.travelsky.com");
			get.setHeader("Cookie", cookie);
			get.setHeader("Proxy-Authorization" , "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, get, context);
			String fileUri = "C://testImg//" + session + ".jpg";
			re = response.getEntity().getContent();
			OutputStream os = new FileOutputStream(fileUri);
			IOUtils.copy(re, os);
			os.close();

			File screenshotLocation = new File(fileUri);
//			InputStream is = new FileInputStream(new File(fileUri));
			Scanner input=new Scanner(System.in);
			//斐斐打码
			String result = input.next();
					// Api.getValidCode(username , password , "40300" , fileUri);
			logger.info("调用斐斐打码返回：" +result);
			String[] split = result.split(";");
			String resultCode = split[0]; //返回的验证码
			if(result.contains("error") || result.contains("ERROR") ) {
				return "普通验证码平台错误提示:"+result;
			}
//			String validtext = YunSu.getValidCode(is, "4030", username, password);
			try {
				if (screenshotLocation.exists()) {
					screenshotLocation.delete();
				}
			} catch (Exception e) {
				logger.error("error", e);
			}
//			org.dom4j.Document document = DocumentHelper.parseText(validtext);
//			org.dom4j.Element root = document.getRootElement();
//			String error = root.elementText("Error");
//			String vrtifycode = "";
//			if (error != null && !"".equals(error)) {
//				vrtifycode = error;
//				if (vrtifycode.contains("点数不足")) {
//					return "点数不足";
//				}
//				if (vrtifycode.contains("任务超时")) {
//					return "任务超时";
//				}
//			} else {
//				vrtifycode = root.elementText("Result");
//			}

			config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setRedirectsEnabled(true).build();
			response.close();
			post = new HttpPost("/y8b2b/cas/login.do");
			post.setConfig(config);
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("loginname", userAccount[0]));
			nameValue.add(new BasicNameValuePair("loginpass", userAccount[1]));
			nameValue.add(new BasicNameValuePair("redirectUrl", "https://y8b2b.travelsky.com/y8b2b/ssoClientLogin.do"));
			nameValue.add(new BasicNameValuePair("serviceURL", ""));
			nameValue.add(new BasicNameValuePair("vrtifycode", resultCode));
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			post.setHeader("Host", "y8b2b.travelsky.com");
			post.setHeader("Cookie", cookie);
			post.setHeader("Referer",
					"https://y8b2b.travelsky.com/y8b2b/cas/authenticate.do?redirectUrl=https://y8b2b.travelsky.com/y8b2b/ssoClientLogin.do");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Origin", "https://y8b2b.travelsky.com");
			post.setHeader("Accept-Encoding", "gzip, deflate, br");
			post.setHeader("Proxy-Authorization" , "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
			post.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
			response = httpclient.execute(target, post, context);
			String res = EntityUtils.toString(response.getEntity(),"utf-8");
			System.out.println(res);
			Header[] headersArr = response.getAllHeaders();
			String newCookie = "";
			for (Header header : headersArr) {
				if ("Set-Cookie".equals(header.getName())) {
					newCookie += header.getValue() + ";";
				}
			}
			if (!newCookie.contains("Y8B2BCOOKIE")) {
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

			Header[] location = response.getHeaders("Location");
			String locationValue = "";
			for (int i = 0; i < location.length; i++) {
				locationValue = location[i].getValue();
			}
			response.close();
			get = new HttpGet(locationValue);
			get.setConfig(config);
			get.setHeader("Host", "y8b2b.travelsky.com");
			get.setHeader("Cookie", cookie);
			get.setHeader("Proxy-Authorization" , "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
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
				if (!c.contains("JSESSIONID") && !c.contains("Path") && !c.contains("HttpOnly")
						&& !c.contains("TUB2BCOOKIE_AGENT_LOGIN_LANGUAGE") && !c.contains("Expires")
						&& !c.contains("X-LB")) {
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

			return cookie;
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if (re != null) {
				try {
					re.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
			if (response != null) {
				try {
					response.close();
				} catch (Exception e) {
					logger.error("error", e);
				}
			}
		}
		return null;

	}

	private String cancel(String url, String id, String childrenUser) {
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		InputStream re = null;
		HttpGet get = null;
		HttpPost post = null;
		String result = null;
		try {
			BasicCookieStore cookieStore = new BasicCookieStore();
			Integer timeout = Integer.parseInt("60000");
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.build();
			client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
			List<BasicNameValuePair> nameValueParis = new ArrayList<BasicNameValuePair>();
			nameValueParis.add(new BasicNameValuePair("orderId", id));
			nameValueParis.add(new BasicNameValuePair("codetype", "order"));
			nameValueParis.add(new BasicNameValuePair("childrenUser", childrenUser));
			post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(nameValueParis, "utf-8"));
			post.setConfig(requestConfig);
			/*response = client.execute(post);
			result = EntityUtils.toString(response.getEntity(), "utf-8");
			JSONObject jo = new JSONObject(result);
			result = jo.getString("msg");*/
		} catch (Exception e) {
			logger.error("error", e);
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
				logger.error(e);
			}
		}
		return result;
	}

	/**
	 * 推送创单情况 String childrenUser = request.getParameter("childrenUser");//子帐号
	 * String orderId = request.getParameter("orderId"); //原订单主键ID String
	 * payStatus = request.getParameter("payStatus"); //获取支付方式
	 */
	public static String sendOrderStatus(String childrenUser, String orderId, String status) {
		try {
			String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrlStatus");

			StringBuffer buffer = new StringBuffer();
			buffer.append("<feeye-official>");
			buffer.append("<official>y8b2b</official> ");
			buffer.append("<url>" + orderUrl + "</url> ");
			buffer.append("<type>0</type> ");
			buffer.append("<method>post</method>");
			buffer.append("<max>20</max> ");
			buffer.append("<encod>utf-8</encod> ");
			buffer.append("<params>");
			buffer.append("<param name='childrenUser'>" + childrenUser + "</param>");
			buffer.append("<param name='orderId'>" + orderId + "</param>");
			buffer.append("<param name='orderStatus'>" + status + "</param>");

			buffer.append("</params>");
			buffer.append("</feeye-official>");

			String content = OfficialMain.setRequestParams(buffer.toString());
			if (content != null) {
				String rs1[] = content.split("#@_@#");
				if (rs1.length == 2) {
					content = rs1[1];
					return content;
				}
				if (rs1.length == 3) {
					logger.info(rs1[2]);
					return rs1[2];
				}
			}
		} catch (Exception e) {
			logger.error("推送\"" + status + "\"情况异常");
		}
		return null;
	}

	/**
	 * 推送创单情况 String result = request.getParameter("result"); //是否创单成功 String
	 * message = request.getParameter("message"); //失败消息 String price =
	 * request.getParameter("price"); //采购总金额 String childrenUser =
	 * request.getParameter("childrenUser");//子帐号 String newOrderId =
	 * request.getParameter("newOrderId"); //创建订单成功后的官网订单号 String orderId =
	 * request.getParameter("orderId"); //原订单主键ID String isPassuccess =
	 * request.getParameter("isPassuccess"); //是否支付成功 String isPassenge =
	 * request.getParameter("isPassenge"); //是否票号回填 String[] passengeMessage =
	 * request.getParameterValues("passengeMessage"); // 获取票号回填到系统
	 * 格式为:姓名##生份证##票号##银行订单号 String payTransactionid =
	 * request.getParameter("payTransactionid"); //获取票号回填的交易号 SC时代表联系电话 String
	 * payStatus = request.getParameter("payStatus"); //获取支付方式 String isSuccess
	 * = request.getParameter("isSuccess"); //是否完结 String isautoB2C =
	 * request.getParameter("isautoB2C"); //是否自动出票 String ifUsedCoupon =
	 * request.getParameter("ifUsedCoupon"); //是否使用红包
	 */
	public static String sendCreateOrderInfo(String result, String message, String price, String childrenUser,
			String newOrderId, String orderId, String isPassuccess, String isPassenge, String passengeMessage,
			String payStatus, String payTransactionid, String ifUsedCoupon, String isSuccess, String billNo,
			int requestType, String ticketPrice) {
		try {
			String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrl");
			StringBuffer buffer = new StringBuffer();
			buffer.append("<feeye-official>");
			buffer.append("<official>y8b2b</official> ");
			buffer.append("<url>" + orderUrl + "</url> ");
			buffer.append("<type>0</type> ");
			buffer.append("<method>post</method>");
			buffer.append("<max>20</max> ");
			buffer.append("<encod>utf-8</encod> ");
			buffer.append("<params>");
			buffer.append("<param name='result'>" + result + "</param>"); // error
																			// ,
																			// success(创建成功)
			buffer.append("<param name='message'>" + message + "</param>");
			buffer.append("<param name='price'>" + price + "</param>");
			buffer.append("<param name='childrenUser'>" + childrenUser + "</param>");
			buffer.append("<param name='newOrderId'>" + newOrderId + "</param>");
			buffer.append("<param name='orderId'>" + orderId + "</param>");
			buffer.append("<param name='isPassuccess'>" + isPassuccess + "</param>");
			buffer.append("<param name='isPassenge'>" + isPassenge + "</param>");
			buffer.append("<param name='passengeMessageOther'>" + passengeMessage + "</param>");
			buffer.append("<param name='payStatus'>" + payStatus + "</param>");
			buffer.append("<param name='payTransactionid'>" + payTransactionid + "</param>");
			buffer.append("<param name='ifUsedCoupon'>" + ifUsedCoupon + "</param>");
			buffer.append("<param name='isSuccess'>" + isSuccess + "</param>");
			buffer.append("<param name='billNo'>" + billNo + "</param>");
			buffer.append("<param name='dicountMoney'>" + 0 + "</param>");
			buffer.append("<param name='requestType'>" + requestType + "</param>");
			buffer.append("<param name='ticketPrice'>" + ticketPrice + "</param>");

			buffer.append("</params>");
			buffer.append("</feeye-official>");

			logger.info(buffer.toString());
			String content = "";
					// OfficialMain.setRequestParams(buffer.toString());
			if (content != null) {
				String rs1[] = content.split("#@_@#");
				if (rs1.length == 2) {
					content = rs1[1];
					return content;
				}
				if (rs1.length == 3) {
					logger.info(rs1[2]);
					return rs1[2];
				}
			}

		} catch (Exception e) {
			logger.error("error", e);
		}
		return null;
	}
}
