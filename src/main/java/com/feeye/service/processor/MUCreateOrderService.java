//package com.feeye.service.processor;
//
//import com.feeye.util.*;
//import com.google.common.collect.Maps;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.*;
//import org.apache.http.auth.AUTH;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.MalformedChallengeException;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.protocol.HttpClientContext;
//import org.apache.http.config.Registry;
//import org.apache.http.config.RegistryBuilder;
//import org.apache.http.config.SocketConfig;
//import org.apache.http.conn.ConnectionKeepAliveStrategy;
//import org.apache.http.conn.socket.ConnectionSocketFactory;
//import org.apache.http.conn.socket.PlainConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLContextBuilder;
//import org.apache.http.conn.ssl.TrustStrategy;
//import org.apache.http.cookie.Cookie;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.auth.BasicScheme;
//import org.apache.http.impl.client.*;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.message.BasicHeaderElementIterator;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
//import org.dom4j.DocumentHelper;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import javax.net.ssl.SSLContext;
//import javax.script.Invocable;
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//import java.io.*;
//import java.net.URLEncoder;
//import java.nio.charset.Charset;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class MUCreateOrderService {
//	private static final Logger logger = Logger.getLogger(MUCreateOrderService.class);
//	private static final int timeout = 60000;
//	String dlyAccount = PropertiesUtils.getPropertiesValue("config", "dlyAccount");
//
//	public static void main(String[] args) throws Exception {
//		String back = "{\r\n" + "    \"id\": \"30448688\",\r\n" + "    \"account\": \"13282080473_t13282080\",\r\n"
//				+ "    \"airline\": \"MU\",\r\n" + "    \"orderNo\": \"mhw181126132424720001\",\r\n"
//				+ "    \"username\": \"CML01\",\r\n" + "    \"matchCabin\": false,\r\n"
//				+ "    \"childrenMobile\": \"\",\r\n" + "    \"payType\": \"ybhy\",\r\n" + "    \"code\": \"\",\r\n"
//				+ "    \"orderTime\": \"2018-11-27 00:06:22\",\r\n" + "    \"codePassword\": \"\",\r\n"
//				+ "    \"flights\": [\r\n" + "        {\r\n" + "            \"airline\": \"MU\",\r\n"
//				+ "            \"departure\": \"SZX\",\r\n" + "            \"arrival\": \"SHA\",\r\n"
//				+ "            \"departureDate\": \"2019-09-13\",\r\n" + "            \"price\": \"730\",\r\n"
//				+ "            \"flightNo\": \"MU5332\",\r\n" + "            \"cabin\": \"V\",\r\n"
//				+ "            \"fType\": \"go\"\r\n" + "        }\r\n" + "    ],\r\n"
//				+ "    \"mobile\": \"13532989542\",\r\n" + "    \"linkMan\": \"罗轩\",\r\n"
//				+ "    \"email\": \"471804731\",\r\n" + "    \"isOutticket\": \"true\",\r\n"
//				+ "    \"ytype\": \"航信\",\r\n" + "    \"passengers\": [\r\n" + "        {\r\n"
//				+ "            \"passengerName\": \"罗轩\",\r\n" + "            \"idcard\": \"441423199501081014\",\r\n"
//				+ "            \"passengerType\": \"成人\",\r\n" + "            \"passengercardType\": \"身份证\",\r\n"
//				+ "            \"birthday\": \"1995-01-08\",\r\n" + "            \"passengerSex\": \"男\"\r\n"
//				+ "        },\r\n" + "		{\r\n" + "            \"passengerName\": \"洪培珊\",\r\n"
//				+ "            \"idcard\": \"441423199501251044\",\r\n" + "            \"passengerType\": \"成人\",\r\n"
//				+ "            \"passengercardType\": \"身份证\",\r\n" + "            \"birthday\": \"1995-01-25\",\r\n"
//				+ "            \"passengerSex\": \"女\"\r\n" + "        }\r\n" + "    ],\r\n"
//				+ "    \"ifUsedCoupon\": false,\r\n" + "    \"drawerType\": \"tuNiu\",\r\n"
//				+ "    \"qiangpiao\": \"\",\r\n" + "    \"newOrderNo\": \"null\",\r\n"
//				+ "    \"otheraccount\": \"\",\r\n" + "    \"creditNo\": \"5254980012170051\",\r\n"
//				+ "    \"expireMonth\": \"12\",\r\n" + "    \"expireYear\": \"2022\",\r\n"
//				+ "    \"ownername\": \"罗轩\",\r\n" + "    \"idCardNo\": \"441423199501081014\",\r\n"
//				+ "    \"payerMobile\": \"13532989542\",\r\n" + "    \"cvv\": \"051\"\r\n" + "}";
//		// Map<String, String> resultMap =
//		// ParseFlightInFo.checkFlightIsExist("MF8050", null, back);
//		// String status = resultMap.get("status");
//		// logger.info(status);
//		new MUCreateOrderService().StartCreateOrder(back, 0, 0, 0);
//
//	}
//
//	/**
//	 * 主要流程： 先判断乘机人是否为会员，如果是会员，则不用确认所选价格是否为会员价，直接登录会员账号出票 如果不是会员，则注册会员再登陆会员账号出票
//	 *
//	 * @param retryCount
//	 */
//	public String StartCreateOrder(String orderJson, int requestType, int retryCount, int exceptionCount) {
//		if (StringUtils.isEmpty(orderJson)) {
//			return "ERROR:数据不完整";
//		}
//		logger.info("获取到的数据MU:" + orderJson);
//		SSLConnectionSocketFactory sslsf = null;
//		BasicCookieStore cookieStore = new BasicCookieStore();// 一个cookies
//		try {
//			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
//				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//					return true;
//				}
//			}).build();
//			sslsf = new SSLConnectionSocketFactory(sslContext,
//					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			// 初始化SSL连接
//		} catch (Exception e) {
//			logger.error("error", e);
//		}
//		BasicScheme proxyAuth = new BasicScheme();
//		try {
//			proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH , "BASIC realm=default"));
//		} catch (MalformedChallengeException e1) {
//			logger.error("error" , e1);
//		}
//		//代理云ip
//		String ip_port = DailiyunService.getRandomIp(50);
//		String proxyIp = ip_port.split(":")[0];
//		int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
//		HttpHost dailiyunProxy = new HttpHost(proxyIp , proxyPort , "http");
//
//		BasicAuthCache authCache = new BasicAuthCache();
//		authCache.put(dailiyunProxy , proxyAuth);
//		CredentialsProvider credsProvider = new BasicCredentialsProvider();
//		String[] dailiyunAccounts = dlyAccount.split(":");
//		credsProvider.setCredentials(new AuthScope(dailiyunProxy) , new UsernamePasswordCredentials(dailiyunAccounts[0] , dailiyunAccounts[1]));
//
//		RequestConfig defaultRequestConfig = RequestConfig.custom()
//				.setSocketTimeout(timeout)
//				.setConnectTimeout(timeout)
//				.setConnectionRequestTimeout(timeout)
//				.setProxy(dailiyunProxy)
//				.setStaleConnectionCheckEnabled(true)
//				.build();
//
//		//配置连接策略
//		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
//			@Override
//			public long getKeepAliveDuration(HttpResponse response , HttpContext context) {
//				HeaderElementIterator it = new BasicHeaderElementIterator
//						( response.headerIterator( HTTP.CONN_KEEP_ALIVE ) );
//				while (it.hasNext()) {
//					HeaderElement he = it.nextElement();
//					String param = he.getName();
//					String value = he.getValue();
//					if (value != null && param.equalsIgnoreCase
//							( "timeout" )) {
//						return Long.parseLong( value ) * 1000;
//					}
//				}
//				return 60 * 1000;//如果没有约定，则默认定义时长为60s
//			}
//		};
//
////		SSLContext sslcontext = SSLContexts.createDefault();
////		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" },
////				null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
//
//		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//				.register("http", PlainConnectionSocketFactory.getSocketFactory())
//				.register("https", sslsf)  // 用来配置支持的协议
//				.build();
//		// 共享连接池
//		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
//		connectionManager.setMaxTotal(10);
//		connectionManager.setDefaultMaxPerRoute(10);
//		SocketConfig socketConfig = SocketConfig.custom()
//				.setSoKeepAlive(false)
//				.setSoLinger(1)
//				.setSoReuseAddress(true)
//				.setSoTimeout(timeout)
//				.setTcpNoDelay(true).build();
//
//		CloseableHttpClient httpclient = HttpClientBuilder.create()
//				.setKeepAliveStrategy(myStrategy)
//				.setConnectionManager(connectionManager)
//				.setConnectionManagerShared(true)
//				.setDefaultCookieStore(cookieStore)
//				.setDefaultSocketConfig(socketConfig)
//				.setDefaultCredentialsProvider(credsProvider)
//				.build();
//
//		String childrenUser = "";
//		String order_id = "";
//		String billNo = "";
//		try {
//			HttpHost target = new HttpHost("passport.ceair.com", 443, "https");
//			HttpClientContext context = HttpClientContext.create();
//			context.setAuthCache(authCache);
//			context.setTargetHost(target);
//			context.setRequestConfig(defaultRequestConfig);
//			context.setCredentialsProvider(credsProvider);
//			String cookie = "";
//			JSONObject json = new JSONObject(orderJson);
//			try {
//				billNo = json.getString("billNo");
//			} catch (Exception e) {
//			}
//			order_id = json.getString("id");
//			String account = json.getString("account");
//			childrenUser = json.getString("username");
//			// String policyType = "";
//			// try {
//			// policyType = json.getString("ytype");
//			// } catch (JSONException e) {
//			//
//			// }
//			// boolean isMemberPrice = policyType.contains("会员");
//			// boolean hasRegister = false;
//			JSONArray passengers = json.getJSONArray("passengers");
//			/*
//			 * 先判断乘机人有没有存在东航会员，member为1的表示有会员，account传过来的就是会员账号密码
//			 */
//
//			boolean defaultAccount = false; // 是否因为注册失败，使用默认密码登录
//			// 登录
//			sendOrderStatus(childrenUser, order_id, "开始登陆", requestType);
//			String loginCookie = "";
//			for (int i = 0; i < 5; i++) {
//				cookie = login(defaultRequestConfig, httpclient, orderJson, cookieStore, null, account, context);
//				if (StringUtils.isEmpty(cookie)) {
//					if (exceptionCount < 10) {
//						exceptionCount++;
//						return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
//					} else {
//						sendCreateOrderInfo("error", "登录异常", "", childrenUser, "", order_id, "", "", null, "", "",
//								"false", "true", billNo, requestType);
//						return "ERROR:登录异常";
//					}
//				}
//				if (StringUtils.isNotEmpty(cookie) && cookie.contains("error")) {
//					if (cookie.contains("识别失败") || cookie.contains("识别超时")) {
//						continue;
//					} else {
//						sendCreateOrderInfo("error", cookie, "", childrenUser, "", order_id, "", "", null, "", "",
//								"false", "true", billNo, requestType);
//						return cookie;
//					}
//				}
//				if (StringUtils.isNotEmpty(cookie) && cookie.contains("com.ceair.cesso")) {
//					break;
//				}
//				if (StringUtils.isNotEmpty(cookie) && (cookie.contains("密码输入错误"))) {
//					if (retryCount < 2) {
//						retryCount++;
//						return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
//					} else {
//						JSONObject errorJson = new JSONObject(cookie);
//						String resultMessage = errorJson.getString("resultMessage");
//						if (defaultAccount) {
//							resultMessage = "该会员已被注册，尝试使用默认密码登录失败，具体失败原因" + resultMessage;
//						} else {
//							resultMessage = "密码输入错误";
//						}
//						sendCreateOrderInfo("error", resultMessage, "", childrenUser, "", order_id, "", "", null, "",
//								"", "false", "true", billNo, requestType);
//						return "ERROR:登录失败";
//					}
//				}
//				if (StringUtils.isNotEmpty(cookie) && (cookie.contains("验证码错误"))) {
//					if (retryCount < 2) {
//						retryCount++;
//						return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
//					} else {
//						JSONObject errorJson = new JSONObject(cookie);
//						String resultMessage = errorJson.getString("resultMessage");
//						resultMessage = "登录失败，具体失败原因" + resultMessage;
//						sendCreateOrderInfo("error", resultMessage, "", childrenUser, "", order_id, "", "", null, "",
//								"", "false", "true", billNo, requestType);
//						return "ERROR:登录失败";
//					}
//				}
//				if (StringUtils.isNotEmpty(cookie) && cookie.contains("验证码点数不足")) {
//					String errorMsg = "验证码点数不足";
//					if (defaultAccount) {
//						errorMsg = "该会员已被注册，尝试使用默认密码登录失败，具体失败原因：" + errorMsg;
//					}
//					sendCreateOrderInfo("error", errorMsg, "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:验证码点数不足";
//				}
//
//			}
//			if (StringUtils.isEmpty(cookie)) {
//				sendCreateOrderInfo("error", "登录异常", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:登录异常";
//			}
//			loginCookie = cookie;
//			if (defaultAccount) {
//				// 用默认密码登录成功也将密码保存进系统
//				if (StringUtils.isNotEmpty(account)) {
//					JSONObject passenger = passengers.getJSONObject(0);
//					String passengerName = passenger.getString("passengerName");
//					// 要将账号密码发送回订单保存
//					if (requestType == 1) {
//						sendAccountInfo("", passengerName, account, order_id, childrenUser, "0", "", billNo);
//					} else {
//						sendAccountInfo("", passengerName, account, "", childrenUser, "0", "", billNo);
//					}
//				}
//			}
//			if (requestType == 1) {
//				return "注册结束";
//			}
//			// 进入查询航班页面
//			sendOrderStatus(childrenUser, order_id, "查询航班", requestType);
//			String back = "";
//			target = new HttpHost("www.ceair.com", 80, "http");
//			context = HttpClientContext.create();
//			context.setAuthCache(authCache);
//			context.setTargetHost(target);
//			context.setRequestConfig(defaultRequestConfig);
//			context.setCredentialsProvider(credsProvider);
//			for (int i = 0; i < 5; i++) {
//				back = findFlights(httpclient, defaultRequestConfig, cookieStore, cookie, orderJson, context,0);
//				if (StringUtils.isEmpty(back)) {
//					continue;
//				}
//				if (back.contains("503 Service Unavailable")) {
//					continue;
//				}
//				if (StringUtils.isNotEmpty(back)) {
//					break;
//				}
//				sendOrderStatus(childrenUser, order_id, "查询航班，重新查询", requestType);
//			}
//			if (StringUtils.isEmpty(back)) {
//				sendCreateOrderInfo("error", "查询航班失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:查询航班失败";
//			}
//			/*
//			 * 东航要自己筛选cookie
//			 */
//			List<Cookie> listCookie = cookieStore.getCookies();
//			StringBuffer buf = new StringBuffer();
//			if (null != listCookie && listCookie.size() > 0) {
//				for (int i = 0; i < listCookie.size(); i++) {
//					buf.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
//				}
//			}
//			cookie = buf.toString();
//			logger.info(order_id + "cookie:" + cookie);
//			String[] cookies = cookie.split(";");
//			String newCookie = "";
//			for (String cos : cookies) {
//				if (!cos.contains("cessoServer")) {
//					newCookie = cos + ";" + newCookie;
//				}
//			}
//			cookie = newCookie;
//			logger.info(order_id + "newCookie:" + cookie);
//
//			cookieStore.clear();
//			sendOrderStatus(childrenUser, order_id, "解析航班数据", requestType);
//			String selectConds = parseResult(back, orderJson);
//
//			if (StringUtils.isEmpty(selectConds)) {
//				logger.info(order_id + "解析航班数据失败的结果:" + back);
//				if (retryCount < 5) {
//					return StartCreateOrder(orderJson, requestType, ++retryCount, exceptionCount);
//				} else {
//					sendCreateOrderInfo("error", "解析航班数据失败", "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:解析航班数据失败";
//				}
//			}
//			sendOrderStatus(childrenUser, order_id, "选择航班", requestType);
//			String allCheckToken = "";
//			for (int i = 0; i < 5; i++) {
//				allCheckToken = selectFlights(httpclient, defaultRequestConfig, cookie, selectConds, context);
//				if (StringUtils.isNotEmpty(allCheckToken) && "retry".equals(allCheckToken)) {
//					sendOrderStatus(childrenUser, order_id, "选择航班失败，重新选择", requestType);
//					continue;
//				}
//				if (StringUtils.isNotEmpty(allCheckToken) && "MUSTLOGIN".equals(allCheckToken) && retryCount < 5) {
//					return StartCreateOrder(orderJson, requestType, ++retryCount, exceptionCount);
//				}
//				if (StringUtils.isNotEmpty(allCheckToken)) {
//					break;
//				}
//				sendOrderStatus(childrenUser, order_id, "选择航班失败，重新选择", requestType);
//			}
//			if (StringUtils.isEmpty(allCheckToken)) {
//				sendCreateOrderInfo("error", "选择不到该航班", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:选择不到该航班";
//			}
//			if ("error".equals(allCheckToken)) {
//				sendCreateOrderInfo("error", "选择航班失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:选择航班失败";
//			}
//			sendOrderStatus(childrenUser, order_id, "添加联系人", requestType);
//			// 添加联系人
//			String sessionVersion = "";
//			for (int i = 0; i < 5; i++) {
//				try {
//					sessionVersion = addContactPax(httpclient, defaultRequestConfig, cookie, orderJson, allCheckToken,
//							context);
//				} catch (Exception e) {
//					continue;
//				}
//				break;
//			}
//			if (StringUtils.isEmpty(sessionVersion)) {
//				if (retryCount < 3) {
//					retryCount++;
//					return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
//				} else {
//					sendCreateOrderInfo("error", "添加联系人失败", "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:添加联系人失败";
//				}
//			}
//			sendOrderStatus(childrenUser, order_id, "添加乘机人", requestType);
//			for (int i = 0; i < 5; i++) {
//				String sessionVersion1 = savePassengerInfo(httpclient, defaultRequestConfig, cookie, orderJson,
//						allCheckToken, sessionVersion, context);
//				if (StringUtils.isEmpty(sessionVersion1)) {
//					continue;
//				}
//				if (StringUtils.isNotEmpty(sessionVersion1) && sessionVersion1.contains("503 Service Unavailable")) {
//					continue;
//				}
//				if (StringUtils.isNotEmpty(sessionVersion1)) {
//					break;
//				}
//			}
//			sendOrderStatus(childrenUser, order_id, "预订订单", requestType);
//			int index = 0;
//			for (; index < 5; index++) {
//				try {
//					String result = booking(httpclient, defaultRequestConfig, cookie, allCheckToken, sessionVersion,
//							context);
//					if (StringUtils.isNotEmpty(result)
//							&& (result.contains("503 Service") || result.contains("403 Forbidden"))) {
//						if (retryCount < 3) {
//							retryCount++;
//							return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
//						}
//					}
//				} catch (Exception e) {
//					continue;
//				}
//				break;
//			}
//			if (index == 5) {
//				sendCreateOrderInfo("error", "预订失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:预订失败";
//			}
//			String checkResult = "";
//			for (int i = 0; i < 3; i++) {
//				checkResult = checkCaptcha(httpclient, defaultRequestConfig, cookie, allCheckToken, orderJson, context);
//				if (!"retry".equals(checkResult)) {
//					break;
//				}
//			}
//			if ("retry".equals(checkResult)) {
//				if (exceptionCount < 5) {
//					return StartCreateOrder(orderJson, requestType, retryCount, ++exceptionCount);
//				} else {
//					sendCreateOrderInfo("error", "滑动验证码校验失败", "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:滑动验证码校验失败";
//				}
//			}
//			boolean flag = true;
//			if ("noCaptcha".equals(checkResult)) {
//				flag = false;
//			}
//			logger.info(order_id + "创单的cookie:" + cookie);
//			Map<String, String> resultMap = new HashMap<String, String>();
//			sendOrderStatus(childrenUser, order_id, "创建订单", requestType);
//			for (int i = 0; i < 5; i++) {
//				try {
//					resultMap = creatOrder(httpclient, defaultRequestConfig, cookie, allCheckToken, context, orderJson,
//							flag);
//				} catch (Exception e) {
//					continue;
//				}
//				if (resultMap == null) {
//					sendCreateOrderInfo("error", "创建订单异常", "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:创建订单异常";
//				}
//				if (resultMap.get("error") != null) {
//					String errorMsg = resultMap.get("error");
//					if (("会话超时".equals(errorMsg)) || errorMsg.contains( "机票购买失败" )) {
//						if (exceptionCount < 8) {
//							logger.info( order_id + errorMsg );
//							exceptionCount++;
//							return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
//						}
//					}
//					sendCreateOrderInfo("error", errorMsg, "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:" + errorMsg;
//				}
//				break;
//			}
//			String orderNo = resultMap.get("orderNo"); // 官网订单号
//			String nextUrl = resultMap.get("nextUrl");
//			if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(nextUrl)) {
//				sendCreateOrderInfo("error", "创建订单未知错误，请到官网确认是否创建成功", "", childrenUser, "", order_id, "", "", null, "",
//						"", "false", "true", billNo, requestType);
//				return "ERROR:创建订单未知错误，请到官网确认是否创建成功";
//			}
//			sendCreateOrderInfo("success", "", "", childrenUser, account.split("_")[0] + "--" + orderNo, order_id, "",
//					"", null, "", "", "false", "true", billNo, requestType);
//
//			sendOrderStatus(childrenUser, order_id, "选择支付方式", requestType);
//			for (int i = 0; i < 5; i++) {
//				try {
//					resultMap = payInit(httpclient, defaultRequestConfig, cookie, nextUrl, allCheckToken);
//					if (resultMap == null) {
//						continue;
//					}
//					back = resultMap.get("back");
//					cookie = resultMap.get("cookie");
//					if (StringUtils.isEmpty(back) || StringUtils.isEmpty(cookie)) {
//						continue;
//					}
//					if (back.contains("503 Service Unavailable")) {
//						continue;
//					}
//					break;
//				} catch (Exception e) {
//					logger.error("error", e);
//				}
//				sendOrderStatus(childrenUser, order_id, "选择支付方式异常，重新选择支付方式", requestType);
//			}
//			if (resultMap == null) {
//				sendCreateOrderInfo("error", "选择支付方式异常", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:选择支付方式异常";
//			}
//			back = resultMap.get("back");
//			cookie = resultMap.get("cookie");
//			logger.info(order_id + "payInitCookie:" + cookie);
//			if (StringUtils.isEmpty(back) || StringUtils.isEmpty(cookie)) {
//				sendCreateOrderInfo("error", "选择支付方式未知异常", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:选择支付方式未知异常";
//			}
//			try {
//				resultMap = parsePayInfoResult(back);
//			} catch (Exception e) {
//				logger.error("error", e);
//				sendCreateOrderInfo("error", "选择支付方式异常", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:选择支付方式异常";
//			}
//			if (resultMap == null || resultMap.size() == 0) {
//				sendCreateOrderInfo("error", "选择支付方式失败", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:选择支付方式失败";
//			}
//			sendOrderStatus(childrenUser, order_id, "支付前准备", requestType);
//			for (int i = 0; i < 5; i++) {
//				try {
//					back = preparePay(httpclient, defaultRequestConfig, resultMap, cookie, nextUrl);
//					if (StringUtils.isEmpty(back)) {
//						continue;
//					}
//					if (back.contains("503 Service Unavailable")) {
//						continue;
//					}
//					break;
//				} catch (Exception e) {
//					logger.error("error", e);
//				}
//				sendOrderStatus(childrenUser, order_id, "支付前准备失败，重新请求", requestType);
//			}
//			resultMap = null;
//			if (StringUtils.isEmpty(back)) {
//				sendCreateOrderInfo("error", "支付前准备失败", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:支付前准备失败";
//			}
//			try {
//				resultMap = parsePreparePayResult(back);
//			} catch (Exception e) {
//				logger.error("error", e);
//				sendCreateOrderInfo("error", "支付前准备异常", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:支付前准备异常";
//			}
//			if (resultMap == null || resultMap.size() == 0) {
//				sendCreateOrderInfo("error", "支付前准备失败", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:支付前准备失败";
//			}
//			sendOrderStatus(childrenUser, order_id, "跳转支付", requestType);
//			for (int i = 0; i < 5; i++) {
//				try {
//					Map<String, String> resultMap1 = receiver(httpclient, defaultRequestConfig, resultMap, cookie);
//					if (resultMap1 == null || resultMap1.size() == 0) {
//						try {
//							resultMap = parsePreparePayResult(back);
//						} catch (Exception e) {
//							logger.error("error", e);
//							sendCreateOrderInfo("error", "支付前准备异常", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:支付前准备异常";
//						}
//						if (resultMap == null || resultMap.size() == 0) {
//							sendCreateOrderInfo("error", "支付前准备失败", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:支付前准备失败";
//						}
//						continue;
//					}
//					cookie = resultMap1.get("cookie");
//					back = resultMap1.get("back");
//					if (StringUtils.isEmpty(cookie) || StringUtils.isEmpty(back)) {
//						try {
//							resultMap = parsePreparePayResult(back);
//						} catch (Exception e) {
//							logger.error("error", e);
//							sendCreateOrderInfo("error", "支付前准备异常", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:支付前准备异常";
//						}
//						if (resultMap == null || resultMap.size() == 0) {
//							sendCreateOrderInfo("error", "支付前准备失败", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:支付前准备失败";
//						}
//						continue;
//					}
//					if (back.contains("503 Service Unavailable")) {
//						try {
//							resultMap = parsePreparePayResult(back);
//						} catch (Exception e) {
//							logger.error("error", e);
//							sendCreateOrderInfo("error", "支付前准备异常", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:支付前准备异常";
//						}
//						if (resultMap == null || resultMap.size() == 0) {
//							sendCreateOrderInfo("error", "支付前准备失败", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:支付前准备失败";
//						}
//						continue;
//					}
//					resultMap = resultMap1;
//					break;
//				} catch (Exception e) {
//					logger.error("error", e);
//				}
//				sendOrderStatus(childrenUser, order_id, "跳转支付失败，重新请求", requestType);
//			}
//			if (resultMap == null || resultMap.size() == 0) {
//				sendCreateOrderInfo("error", "跳转支付失败", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:跳转支付失败";
//			}
//			cookie = resultMap.get("cookie");
//			back = resultMap.get("back");
//			if (StringUtils.isEmpty(cookie) || StringUtils.isEmpty(back)) {
//				sendCreateOrderInfo("error", "跳转支付未知异常", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:跳转支付未知异常";
//			}
//			resultMap = null;
//			try {
//				resultMap = parseReceiverResult(back);
//			} catch (Exception e) {
//				logger.error("error", e);
//				sendCreateOrderInfo("error", "跳转支付结果解析异常", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:跳转支付结果解析异常";
//			}
//			if (resultMap == null || resultMap.size() == 0) {
//				sendCreateOrderInfo("error", "跳转支付结果解析失败", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:跳转支付结果解析失败";
//			}
//
//			// 区分支付方式
//			// 银行订单号
//			String bankId = "";
//			String payType = json.getString("payType") == null ? "" : json.getString("payType");
//			if ("ybhy".equals(payType)) {
//				sendOrderStatus(childrenUser, order_id, "获取支付请求参数", requestType);
//				Map<String, String> paramMap = new HashMap<String, String>();
//				paramMap.putAll(resultMap);
//				String merId = paramMap.get("p1_MerId");
//				String orderId = paramMap.get("p2_Order");
//				for (int i = 0; i < 5; i++) {
//					try {
//						Map<String, String> resultMap1 = node(httpclient, defaultRequestConfig, paramMap, cookie,
//								orderJson);
//						if (resultMap1 == null || resultMap1.size() == 0) {
//							break;
//						}
//						String errorMsg = "";
//						try {
//							resultMap1.get("error");
//						} catch (Exception e) {
//						}
//						if (StringUtils.isNotEmpty(errorMsg)) {
//							continue;
//						}
//						back = resultMap1.get("back");
//						if (StringUtils.isEmpty(back)) {
//							continue;
//						}
//						if (back.contains("503 Service Unavailable")) {
//							continue;
//						}
//						resultMap = resultMap1;
//						break;
//					} catch (Exception e) {
//						logger.error("error", e);
//					}
//					sendOrderStatus(childrenUser, order_id, "获取支付请求参数失败，重新获取", requestType);
//				}
//				if (resultMap == null || resultMap.size() == 0) {
//					sendCreateOrderInfo("error", "获取支付请求参数失败", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//							order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//					return "ERROR:获取支付请求参数失败";
//				}
//				String errorMsg = "";
//				try {
//					resultMap.get("error");
//				} catch (Exception e) {
//				}
//				if (StringUtils.isNotEmpty(errorMsg)) {
//					sendCreateOrderInfo("error", errorMsg, "", childrenUser, account.split("_")[0] + "--" + orderNo,
//							order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//					return errorMsg;
//				}
//				cookie = resultMap.get("cookie");
//				back = resultMap.get("back");
//				sendOrderStatus(childrenUser, order_id, "开始支付", requestType);
//				for (int i = 0; i < 2; i++) {
//					bankId = payOrderMU(httpclient, defaultRequestConfig, orderJson, cookie, merId, orderId);
//					if (StringUtils.isNotEmpty(bankId) && bankId.contains("error")) {
//						sendCreateOrderInfo("error", bankId, "", childrenUser, account.split("_")[0] + "--" + orderNo,
//								order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//						return bankId;
//					}
//					if (StringUtils.isNotEmpty(bankId) && bankId.contains("业务接口维护中")) {
//						for (int j = 0; j < 5; j++) {
//							try {
//								Map<String, String> resultMap1 = node(httpclient, defaultRequestConfig, paramMap,
//										cookie, orderJson);
//								if (resultMap1 == null || resultMap1.size() == 0) {
//									continue;
//								}
//								back = resultMap1.get("back");
//								if (StringUtils.isEmpty(back)) {
//									continue;
//								}
//								if (back.contains("503 Service Unavailable")) {
//									continue;
//								}
//								resultMap = resultMap1;
//								break;
//							} catch (Exception e) {
//								logger.error("error", e);
//							}
//							sendOrderStatus(childrenUser, order_id, "获取支付请求参数失败，重新获取", requestType);
//						}
//						if (resultMap == null || resultMap.size() == 0) {
//							sendCreateOrderInfo("error", "获取支付请求参数失败", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:获取支付请求参数失败";
//						}
//						cookie = resultMap.get("cookie");
//						back = resultMap.get("back");
//						continue;
//					} else if (StringUtils.isNotEmpty(bankId) && bankId.contains("不支持该支付方式")) {
//						sendCreateOrderInfo("error", "不支持该支付方式", "", childrenUser,
//								account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false", "true",
//								billNo, requestType);
//						return "ERROR:不支持该支付方式";
//					}
//					break;
//				}
//				if (StringUtils.isNotEmpty(bankId) && bankId.contains("业务接口维护中")) {
//					sendCreateOrderInfo("error", "支付未知异常，业务接口维护中", "", childrenUser,
//							account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false", "true",
//							billNo, requestType);
//					return "ERROR:支付未知异常，业务接口维护中";
//				}
//			} else if ("hf".equals(payType)) {
//				OfficialHfPayService hfPayService = new OfficialHfPayService();
//				String payResult = hfPayService.hfPayMU(orderJson , resultMap);
//				org.dom4j.Document document = DocumentHelper.parseText(payResult);
//				org.dom4j.Element rootElement = document.getRootElement();
//                if (StringUtils.equals("000000", rootElement.elementText("RespCode"))) { //支付成功
//                    String money = rootElement.elementText("OrdAmt"); //支付总金额
//                    String bankNo = rootElement.elementText("TrxId");  //交易流水号
//                    String returnUrl = rootElement.elementText("RetUrl");  //回调通知商户url
//                    resultMap.put("money",money);
//                    resultMap.put("bankNo",bankNo);
//                    resultMap.put("returnUrl",returnUrl);
//
//                    //通知航司
//                    if (resultMap == null || resultMap.size() == 0) {
//                        return null;
//                    }
//                    returnUrl = resultMap.get("returnUrl");
//                    logger.info(order_id + "通知航司请求地址:" + returnUrl);
//                    if(StringUtils.isNotEmpty(returnUrl)){
//                    	HttpGet get = new HttpGet(returnUrl);
//                        get.setConfig(defaultRequestConfig);
//                        get.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//                        get.setHeader("Accept-Encoding","gzip, deflate, br");
//                        get.setHeader("Accept-Language","zh-CN,zh;q=0.9");
//                        get.setHeader("Connection","keep-alive");
//                        get.setHeader("Cookie",cookie);
//                        get.setHeader("Host","www.ceair.com");
//                        get.setHeader("Upgrade-Insecure-Requests","1");
//                        get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//                        CloseableHttpResponse response = httpclient.execute(get);
//                        back = EntityUtils.toString(response.getEntity(),"utf-8");
//                        logger.info(order_id + "通知航司返回：" + back);
//                        bankId = "success";
//                    }
//                } else {
//                    String errorMsg = rootElement.elementText("ErrorMsg");
//                    sendCreateOrderInfo("error", errorMsg, "", childrenUser, account.split("_")[0] + "--" + orderNo,
//    						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//    				return "ERROR:"+errorMsg;
//                }
//			} else {
//				sendCreateOrderInfo("error", "不支持该支付方式", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:不支持该支付方式";
//			}
//
//			if ("xyk".equals(payType)) {
//				payType = "信用卡";
//			} else if ("ybhy".equals(payType)) {
//				payType = "易宝会员";
//			} else if ("hf".equals(payType)) {
//				payType = "汇付";
//			} else {
//				payType = "易宝";
//			}
//			if (StringUtils.isEmpty(bankId)) {
//				sendCreateOrderInfo("error", "支付未知异常，请到官网检查是否已支付", "", childrenUser,
//						account.split("_")[0] + "--" + orderNo, order_id, "", "", null, payType, "", "false", "true",
//						billNo, requestType);
//				return "ERROR:支付未知异常，请到官网检查是否已支付";
//			}
//
//			// 回填
//			String result = null;
//			String payPrice = "";
//			String payTransactionid = "";
//			int detailIndex = 0;
//			for (; detailIndex < 15; detailIndex++) {
//				StringBuffer sb = new StringBuffer();
//				try {
//					Map<String, String> map = queryOrderDetails(httpclient, defaultRequestConfig, loginCookie, orderNo, bankId);
//					if (map == null || map.size() == 0) {
//						Thread.sleep(5000);
//						continue;
//					}
//					if (map.get("error") != null && "未登录或登录超时".equals(map.get("error"))) {
//						login(defaultRequestConfig, httpclient, orderJson, cookieStore, null, account, context);
//						map = queryOrderDetails(httpclient, defaultRequestConfig, loginCookie, orderNo, bankId);
//						if (map == null || map.size() == 0) {
//							Thread.sleep(5000);
//							continue;
//						}
//					}
//					if (map.get("payPrice") != null) {
//						payPrice = map.get("payPrice");
//						map.remove("payPrice");
//					}
//					if (map.get("payTransactionid") != null) {
//						payTransactionid = map.get("payTransactionid");
//						map.remove("payTransactionid");
//					}
//					sendCreateOrderInfo("success", "", payPrice, childrenUser, account.split("_")[0] + "--" + orderNo,
//							order_id, "true", "", null, payType, "", "false", "true", billNo, requestType);
//					sendOrderStatus(childrenUser, order_id, "获取票号信息", requestType);
//					for (String value : map.values()) {
//						if (StringUtils.isEmpty(value)) {
//							continue;
//						}
//						sb.append(value).append("#_#");
//					}
//					result = sb.toString() + "@_@" + payTransactionid;
//
//					logger.info(result.toString());
//					String ticketnos[] = result.split("@_@");
//					if (ticketnos.length == 2) {
//						sendOrderStatus(childrenUser, order_id, "回填票号", requestType);
//						// 判断获取的票号数是否与乘客数量相符合,不相符合重新
//						String[] ticketCount = ticketnos[0].split("#_#");
//						if (ticketCount.length == passengers.length() && !ticketnos[0].contains("null")) {
//							sendCreateOrderInfo("success", "", payPrice, childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "true", ticketnos[0],
//									payType, ticketnos[1], "false", "true", billNo, requestType);
//							return "SUCCESS";
//						}
//					}
//				} catch (Exception e) {
//					logger.error("error", e);
//				}
//				Thread.sleep(5000);
//			}
//			if (detailIndex == 10) {
//				sendCreateOrderInfo("error", "", payPrice, childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "false", "true", "", payType, "", "false", "true", billNo, requestType);
//				return "error:未获取到票号";
//			}
//		} catch (Exception e) {
//			logger.error("error", e);
//			sendCreateOrderInfo("error", "创单失败", "", childrenUser, "", order_id, "", "", null, "", "", "false", "true",
//					billNo, requestType);
//		} finally {
//			if (httpclient != null) {
//				try {
//					httpclient.close();
//				} catch (IOException e) {
//					logger.error("error", e);
//				}
//			}
//		}
//		return "SUCCESS";
//	}
//
//	private String payOrderMU(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String orderJson,
//			String cookie, String merId, String orderId) {
//		CloseableHttpResponse response = null;
//		try {
//			JSONObject orderjson = new JSONObject(orderJson);
//			String userAccount = orderjson.getString("creditNo");
//			String tradePassword = orderjson.getString("cvv");
//			// https://cashdesk.yeepay.com/bc-cashier/bcaccountpay/pay?customerNo=10011419692&customerRequestNo=2019082231125853&userAccount=MTI0MTQxNA%3D%3D&tradePassword=MjE0MjE0
//			HttpHost target = new HttpHost("cashdesk.yeepay.com", 443, "https");
//			HttpGet get = new HttpGet("/bc-cashier/bcaccountpay/pay?customerNo=" + merId + "&customerRequestNo="
//					+ orderId + "&userAccount=" + new String(Base64.encodeBase64(userAccount.getBytes("utf-8")))
//					+ "&tradePassword=" + new String(Base64.encodeBase64(tradePassword.getBytes("utf-8"))) + "");
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "cashdesk.yeepay.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer",
//					"http://www.ceair.com/booking/code_pop.html?_=0.7953163150618398&data=scheckCode&mobileNum=15897736493");
//			get.setHeader("Cookie", cookie);
//			response = httpclient.execute(target, get);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("易宝会员支付返回:" + back);
//			JSONObject json = null;
//			try {
//				json = new JSONObject(back);
//				String bizStatus = json.getString("bizStatus");
//				if ("FAIL".equalsIgnoreCase(bizStatus)) {
//					String errormsg = json.getString("errormsg");
//					return "error:" + errormsg;
//				}
//			} catch (Exception e) {
//			}
//			return "success";
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return null;
//	}
//
//	private void sendVerificationCode(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie,
//			String orderJson, HttpClientContext context) {
//		CloseableHttpResponse response = null;
//		try {
//			JSONObject orderjson = new JSONObject(orderJson);
//			String otheraccount = orderjson.getString("otheraccount");
//			String loginAccount = otheraccount.split("_")[0].split("@FeeYe@")[0];
//			String loginPassword = otheraccount.split("_")[0].split("@FeeYe@")[1];
//			Map<String, String> phoneMap = YiKa.getPhoneNumber(loginAccount, loginPassword);
//			String phoneNumber = phoneMap.get("phoneNumber");
//			String token = phoneMap.get("token");
//			logger.info("千万卡获取到手机号码:" + phoneNumber + "，token:" + token);
//			String[] dlyAccountInfo = dlyAccount.split(":");
//			String proxyUser = dlyAccountInfo[0];
//			String proxyPass = dlyAccountInfo[1];
//			HttpHost target = new HttpHost("www.ceair.com", 80, "http");
//			HttpGet get = new HttpGet("/booking/verification-code!sendVerificationCode.shtml?_="
//					+ System.currentTimeMillis() + "&telNum=" + phoneNumber);
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "www.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer",
//					"http://www.ceair.com/booking/code_pop.html?_=0.7953163150618398&data=scheckCode&mobileNum=15897736493");
//			get.setHeader("Cookie", cookie);
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, get);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("sendVerificationCode:" + back);
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (Exception e2) {
//				}
//			}
//
//			Thread.sleep(5 * 1000);
//			String code = "";
//			code = YiKa.getSmsCode(token, phoneNumber);
//			if (code == null) {
//				code = YiKa.getSmsCode(token, phoneNumber);
//			}
//			String mobile = orderjson.getString("mobile");
//			// Scanner scanner = new Scanner(System.in);
//			// logger.info("验证码：");
//			// String code = scanner.nextLine();
//
//			get = new HttpGet("/booking/verification-code!checkValidationCode.shtml?_=" + System.currentTimeMillis()
//					+ "&telNum=" + mobile + "&checkCode=" + code);
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "www.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer",
//					"http://www.ceair.com/booking/code_pop.html?_=0.7953163150618398&data=scheckCode&mobileNum=13532989542");
//			get.setHeader("Cookie", cookie);
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, get);
//			back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("checkValidationCode:" + back);
//			if (!"success".equals(back)) {
//				sendVerificationCode(httpclient, defaultRequestConfig, cookie, orderJson, context);
//			}
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (Exception e2) {
//				}
//			}
//		}
//	}
//
//	private String checkCaptcha(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie,
//			String allCheckToken, String orderJson, HttpClientContext context) {
//		CloseableHttpResponse response = null;
//		try {
//			String[] dlyAccountInfo = dlyAccount.split(":");
//			String proxyUser = dlyAccountInfo[0];
//			String proxyPass = dlyAccountInfo[1];
//			JSONObject orderjson = new JSONObject(orderJson);
//			String otheraccount = orderjson.getString("otheraccount");
//			HttpHost target = new HttpHost("www.ceair.com", 80, "http");
//			HttpPost post = new HttpPost("/otabooking/validate-geek-check!startCaptcha.shtml");
//			post.setConfig(defaultRequestConfig);
//			post.setHeader("Host", "www.ceair.com");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Referer", "http://www.ceair.com/booking/confirm.html?allCheckToken=" + allCheckToken);
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			post.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, post);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			// 无返回结果，表示不用验证码
//			if (StringUtils.isEmpty(back)) {
//				return "noCaptcha";
//			}
//			logger.info("startCaptcha:" + back);
//			JSONObject json = new JSONObject(back);
//			String challenge = json.getString("challenge");
//			String gt = json.getString("gt");
//			String gestResult = GeetestRecognition.recognition(gt,
//					"https://passport.ceair.com/cesso/geet!geetInit.shtml", challenge, otheraccount.split("_")[1]);
//			logger.info("生单前极验打码返回:" + gestResult);
//			JSONObject resultJson = new JSONObject(gestResult);
//			int status = resultJson.getInt("status");
//			if (status != 0) {
//				// String = resultJson.getString("msg");
//				return "retry";
//			}
//			JSONObject dataObj = resultJson.getJSONObject("data");
//			String geetest_challenge = dataObj.getString("challenge");
//			String geetest_validate = dataObj.getString("validate");
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (Exception e2) {
//				}
//			}
//
//			post = new HttpPost("/otabooking/validate-geek-check!verifyLogin.shtml");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			nameValue.add(new BasicNameValuePair("geetest_challenge", geetest_challenge));
//			nameValue.add(new BasicNameValuePair("geetest_seccode", geetest_validate + "|jordan"));
//			nameValue.add(new BasicNameValuePair("geetest_validate", geetest_validate));
//			logger.info("校验请求参数：" + nameValue.toString());
//			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			post.setHeader("Proxy-Connection", "keep-alive");
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "www.ceair.com");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Referer", "http://www.ceair.com/booking/confirm.html?allCheckToken=" + allCheckToken);
//			post.setHeader("Cookie", cookie);
//			response = httpclient.execute(target, post);
//			back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("verifyLogin:" + back);
//			return back;
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (Exception e2) {
//				}
//			}
//		}
//		return "retry";
//	}
//
//	private Map<String, String> queryOrderDetails(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//			String cookie, String orderNo, String backId) {
//		CloseableHttpResponse response = null;
//		try {
//			Map<String, String> mapResult = new HashMap<String, String>();
//			HttpHost target = new HttpHost("ecrm.ceair.com", 80, "http");
//			HttpPost post = new HttpPost("/traveller/optmember/order-query!queryOrderDetails.shtml");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			nameValue.add(new BasicNameValuePair("orderType", "AIR"));
//			nameValue.add(new BasicNameValuePair("orderNo", orderNo));
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "ecrm.ceair.com");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Referer", "http://ecrm.ceair.com/order/detail.html?orderNo=" + orderNo + "&orderType=AIR");
//			post.setHeader("Cookie", cookie);
//			response = httpclient.execute(target, post);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info(orderNo + "queryOrderDetails:" + back);
//			if (StringUtils.isEmpty(back)) {
//				return null;
//			}
//			Header[] headerArr = response.getAllHeaders();
//			String contentKey = "";
//			for (Header header : headerArr) {
//				if ("Content-Key".equalsIgnoreCase(header.getName())) {
//					contentKey = header.getValue().split(",")[0];
//				}
//			}
//			CryptoTools des = new CryptoTools(contentKey);// 自定义密钥
//			// logger.info("加密前的字符："+test);
//			back = des.decode(back);
//			logger.info(orderNo + "解密后的字符：" + back);
//			if (StringUtils.isNotEmpty(back) && back.contains("未登录或登录超时")) {
//				mapResult.put("error", "未登录或登录超时");
//				return mapResult;
//			}
//			mapResult = parseDetailResult(back, backId);
//			if (mapResult.size() == 0) {
//				boolean isSuccess = false;
//				int requestCount = 0;
//				while (!isSuccess && requestCount < 3) {
//					response = httpclient.execute(target, post);
//					back = EntityUtils.toString(response.getEntity(), "utf-8");
//					if (back != null) {
//						requestCount++;
//						Thread.sleep(500);
//					} else {
//						mapResult = parseDetailResult(back, backId);
//						isSuccess = true;
//					}
//				}
//			}
//			return mapResult;
//
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return null;
//	}
//
//	private Map<String, String> parseDetailResult(String back, String backId) throws Exception {
//		Map<String, String> resultMap = Maps.newHashMap();
//		JSONObject jsonObj = new JSONObject(back);
//		String payTransactionid = jsonObj.getJSONArray("orderPayInfoDtoList").getJSONObject(0).getString("bankSerial");
//		long payPrice = jsonObj.getJSONArray("orderPayInfoDtoList").getJSONObject(0).getLong("payAmt");
//		resultMap.put("payTransactionid", payTransactionid);
//		resultMap.put("payPrice", payPrice + "");
//		JSONArray paxInfoDtoList = jsonObj.getJSONArray("paxInfoDtoList");
//		for (int i = 0; i < paxInfoDtoList.length(); i++) {
//			JSONObject json = paxInfoDtoList.getJSONObject(i);
//			JSONObject paxDetailDto = json.getJSONObject("paxDetailDto");
//			String paxName = paxDetailDto.getString("paxName");
//			String identityId = paxDetailDto.getJSONArray("paxIdInfoList").getJSONObject(0).getString("idNo");
//			String ticketNo = json.getJSONArray("tripInfoDtoList").getJSONObject(0).getJSONObject("airSegInfoDto")
//					.getString("tktNo");
//			if (StringUtils.isEmpty(ticketNo)) {
//				ticketNo = "null";
//			}
//			String value = paxName.replace("/", "") + "##" + identityId + "##" + ticketNo + "##" + backId;
//			resultMap.put(paxName, value);
//		}
//		return resultMap;
//	}
//
//	private static void yeepayCreateOrder(String frpId, String requestId, String orderamount, String cookie,
//			String orderJson, String url) {
//		CloseableHttpClient client = null;
//		CloseableHttpResponse response = null;
//		try {
//			JSONObject json = new JSONObject(orderJson);
//			String creditNo = json.getString("creditNo") == null ? "" : json.getString("creditNo");
//			String expireMonth = json.getString("expireMonth") == null ? "" : json.getString("expireMonth");
//			String expireYear = json.getString("expireYear") == null ? "" : json.getString("expireYear");
//			String ownername = json.getString("ownername") == null ? "" : json.getString("ownername");
//			String idCardNo = json.getString("idCardNo") == null ? "" : json.getString("idCardNo");
//			String payerMobile = json.getString("payerMobile") == null ? "" : json.getString("payerMobile");
//			String cvv = json.getString("cvv") == null ? "" : json.getString("cvv");
//			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
//				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//					return true;
//				}
//			}).build();
//
//			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
//					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//			BasicCookieStore cookieStore = new BasicCookieStore();
//			client = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore).build();
//			Integer timeout = Integer.valueOf(Integer.parseInt("70000"));
//
//			RequestConfig.Builder builder = RequestConfig.custom();
//			builder.setSocketTimeout(timeout.intValue());
//			builder.setConnectTimeout(timeout.intValue());
//
//			RequestConfig requestConfig = builder.build();
//			String text = "";
//			String newCookie = "";
//			Header[] headersArr = null;
//			// //提交订单后 跳转的页面请求
//
//			HttpPost post = new HttpPost("https://www.yeepay.com/app-merchant-proxy/createOrder.action");
//
//			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair("frpId", frpId));
//			nameValuePairs.add(new BasicNameValuePair("requestId", requestId));
//			nameValuePairs.add(new BasicNameValuePair("orderamount", orderamount));
//			nameValuePairs.add(new BasicNameValuePair("x", "37"));
//			nameValuePairs.add(new BasicNameValuePair("y", "16"));
//			post.setConfig(requestConfig);
//			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			post.setHeader("Referer", url);
//			post.setHeader("Host", "www.yeepay.com");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Connection", "keep-alive");
//			response = client.execute(post);
//			headersArr = response.getAllHeaders();
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equals(header.getName())) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie += newCookie;
//			text = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("易宝支付第一个请求返回：" + text);
//			Document doc = Jsoup.parse(text);
//			String customerId = doc.getElementById("customerId").val();
//			post = new HttpPost("https://www.yeepay.com/app-merchant-proxy/eposCreditCardVerify.action");
//			nameValuePairs.clear();
//			nameValuePairs.add(new BasicNameValuePair("businessType", "EPOS"));
//			nameValuePairs.add(new BasicNameValuePair("creditNo", creditNo));
//			nameValuePairs.add(new BasicNameValuePair("customerId", customerId));
//			nameValuePairs.add(new BasicNameValuePair("requestId", requestId));
//			post.setConfig(requestConfig);
//			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			post.setHeader("Referer", "https://www.yeepay.com/app-merchant-proxy/createOrder.action");
//			post.setHeader("Host", "www.yeepay.com");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Connection", "keep-alive");
//			response = client.execute(post);
//			text = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("易宝支付第二个请求返回：" + text);
//			headersArr = response.getAllHeaders();
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equals(header.getName())) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie += newCookie;
//
//			text = text.substring(1, text.length() - 1);
//			post = new HttpPost("https://www.yeepay.com/app-merchant-proxy/toEposAuthorizeConfirm.action");
//			JSONObject jo = new JSONObject(text);
//			String needParams = jo.getString("needParams");
//			nameValuePairs.clear();
//			String formatCreditNo = creditNo.substring(0, 4) + " " + creditNo.substring(4, 8) + " "
//					+ creditNo.substring(8, 12) + " " + creditNo.substring(12, 16);
//			nameValuePairs.add(new BasicNameValuePair("amount", orderamount));
//			nameValuePairs.add(new BasicNameValuePair("creditCardInfo.cvv", cvv));// 暂时写死
//			nameValuePairs.add(new BasicNameValuePair("creditNo", formatCreditNo));
//			nameValuePairs.add(new BasicNameValuePair("creditOrderType", "AUTHANDCONFIRM"));
//			nameValuePairs.add(new BasicNameValuePair("customerId", customerId));
//			nameValuePairs.add(new BasicNameValuePair("expireMonth", expireMonth));
//			nameValuePairs.add(new BasicNameValuePair("expireYear", expireYear));
//			nameValuePairs.add(new BasicNameValuePair("frpIdString", frpId));
//			nameValuePairs.add(new BasicNameValuePair("idCardNo", idCardNo));
//			nameValuePairs.add(new BasicNameValuePair("idCardType", "IDCARD"));
//			nameValuePairs.add(new BasicNameValuePair("name", ownername));
//			nameValuePairs.add(new BasicNameValuePair("needParams", needParams));
//			nameValuePairs.add(new BasicNameValuePair("needboccvv", ""));
//			nameValuePairs.add(new BasicNameValuePair("payerMobile", payerMobile));
//			nameValuePairs.add(new BasicNameValuePair("productName", ""));
//			nameValuePairs.add(new BasicNameValuePair("requestId", requestId));
//			nameValuePairs.add(new BasicNameValuePair("yeepayAgreement", "yeepayAgreement"));
//			post.setConfig(requestConfig);
//			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			post.setHeader("Referer", "https://www.yeepay.com/app-merchant-proxy/createOrder.action");
//			post.setHeader("Host", "www.yeepay.com");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Connection", "keep-alive");
//			response = client.execute(post);
//			text = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("易宝支付第三个请求返回：" + text);
//			headersArr = response.getAllHeaders();
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equals(header.getName())) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie += newCookie;
//
//			doc = Jsoup.parse(text);
//			String webworkToken = doc.getElementsByAttributeValue("name", "webwork.token").get(0).val();
//			String webworkTokenName = doc.getElementsByAttributeValue("name", "webwork.token.name").get(0).val();
//
//			nameValuePairs.clear();
//			post = new HttpPost("https://www.yeepay.com/app-merchant-proxy/eposAuthorizeConfirm.action");
//			nameValuePairs.add(new BasicNameValuePair("amount", orderamount));
//			nameValuePairs.add(new BasicNameValuePair("creditCardInfo.cvv", cvv));// 暂时写死
//			nameValuePairs.add(new BasicNameValuePair("creditNo", formatCreditNo));
//			nameValuePairs.add(new BasicNameValuePair("expireMonth", expireMonth));
//			nameValuePairs.add(new BasicNameValuePair("expireYear", expireYear));
//			nameValuePairs.add(new BasicNameValuePair("frpIdString", frpId));
//			nameValuePairs.add(new BasicNameValuePair("idCardNo", idCardNo));
//			nameValuePairs.add(new BasicNameValuePair("idCardType", "IDCARD"));
//			nameValuePairs.add(new BasicNameValuePair("name", ownername));
//			nameValuePairs.add(new BasicNameValuePair("needboccvv", ""));
//			nameValuePairs.add(new BasicNameValuePair("payerMobile", payerMobile));
//			nameValuePairs.add(new BasicNameValuePair("productName", ""));
//			nameValuePairs.add(new BasicNameValuePair("requestId", requestId));
//			nameValuePairs.add(new BasicNameValuePair("webwork.token", webworkToken));
//			nameValuePairs.add(new BasicNameValuePair("webwork.token.name", webworkTokenName));
//			post.setConfig(requestConfig);
//			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			post.setHeader("Referer", "https://www.yeepay.com/app-merchant-proxy/toEposAuthorizeConfirm.action");
//			post.setHeader("Host", "www.yeepay.com");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Connection", "keep-alive");
//			response = client.execute(post);
//			text = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("易宝支付第四个请求返回：" + text);
//			headersArr = response.getAllHeaders();
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equals(header.getName())) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie += newCookie;
//			try {
//				doc = Jsoup.parse(text);
//				text = doc.getElementsByAttributeValue("name", "r6_Order").get(0).val();
//			} catch (Exception e) {
//				text = null;
//				logger.info("支付异常页面:" + text);
//				logger.error("error", e);
//			}
//		} catch (Exception e) {
//			logger.error("error", e);
//		}
//	}
//
//	private Map<String, String> node(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//			Map<String, String> paramMap, String cookie, String orderJson) {
//		CloseableHttpResponse response = null;
//		try {
//			defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
//					.setRedirectsEnabled(false).setConnectionRequestTimeout(timeout).build();
//			JSONObject orderjson = new JSONObject(orderJson);
//			String order_id = orderjson.getString("id");
//			Map<String, String> resultMap = new HashMap<String, String>();
//			HttpHost target = new HttpHost("www.yeepay.com", 443, "https");
//			HttpPost post = new HttpPost("/app-merchant-proxy/node");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
//				nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//			}
//			logger.info("nodeParam:" + nameValue.toString());
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "www.yeepay.com");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Referer", "http://up.ceair.com:8039/cur/payment/receiver");
//			// 这里不传cookie，才能拿到正确的Set-Cookie
//			post.setHeader("Cookie", "");
//			response = httpclient.execute(target, post);
//
//			String locationValue = "";
//			Header[] location = response.getHeaders("Location");
//			// 提交订单后重定向请求
//			for (int i = 0; i < location.length; i++) {
//				locationValue = location[i].getValue();
//				logger.info(order_id + "node重定向1:" + locationValue);
//			}
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//			if (StringUtils.isEmpty(locationValue)) {
//				resultMap.put("error", "重定向1失败");
//				return resultMap;
//			}
//			defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
//					.setRedirectsEnabled(false).setConnectionRequestTimeout(timeout).setCircularRedirectsAllowed(true)
//					.build();
//			HttpGet get = new HttpGet(locationValue);
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "cashdesk.yeepay.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "http://up.ceair.com:8039/cur/payment/receiver");
//			get.setHeader("Cookie", cookie);
//			response = httpclient.execute(get);
//			locationValue = "";
//			location = response.getHeaders("Location");
//			// 提交订单后重定向请求
//			for (int i = 0; i < location.length; i++) {
//				locationValue = location[i].getValue();
//				logger.info(order_id + "node重定向2:" + locationValue);
//			}
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//			if (StringUtils.isEmpty(locationValue)) {
//				resultMap.put("error", "重定向2失败");
//				return resultMap;
//			}
//			get = new HttpGet(locationValue);
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "cashdesk.yeepay.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "http://up.ceair.com:8039/cur/payment/receiver");
//			get.setHeader("Cookie", cookie);
//			response = httpclient.execute(get);
//			// String back = EntityUtils.toString(response.getEntity(),
//			// "utf-8");
//			// if (StringUtils.isEmpty(back)) {
//			// return null;
//			// }
//			// Header[] headersArr = response.getAllHeaders();
//			// String newCookie = "";
//			// for (Header header : headersArr) {
//			// if ("Set-Cookie".equalsIgnoreCase(header.getName())
//			// || "Set_Cookie".equalsIgnoreCase(header.getName())) {
//			// newCookie += header.getValue() + ";";
//			// }
//			// }
//			// cookie = newCookie;
//			// resultMap.put("cookie", cookie);
//			// resultMap.put("back", back);
//			defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
//					// .setRedirectsEnabled(false)
//					.setConnectionRequestTimeout(timeout)
//					// .setCircularRedirectsAllowed(true)
//					.build();
//			return resultMap;
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return null;
//	}
//
//	private Map<String, String> parseReceiverResult(String back) {
//		Map<String, String> resultMap = new HashMap<String, String>();
//		try {
//			Document doc = Jsoup.parse(back);
//			Elements input = doc.getElementsByTag("input");
//			for (Element element : input) {
//				String key = element.attr("id");
//				String value = element.val();
//				resultMap.put(key, value);
//			}
//			logger.info("解析后的结果:" + resultMap.toString());
//			return resultMap;
//		} catch (Exception e) {
//			logger.error("error", e);
//		}
//		return null;
//	}
//
//	private Map<String, String> receiver(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//			Map<String, String> paramMap, String cookie) {
//		CloseableHttpResponse response = null;
//		try {
//			Map<String, String> resultMap = new HashMap<String, String>();
//			HttpPost post = new HttpPost("http://up.ceair.com:8039/cur/payment/receiver");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
//				nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//			}
//			logger.info("receiverParam:" + nameValue);
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "up.ceair.com:8039");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			response = httpclient.execute(post);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			Header[] headersArr = response.getAllHeaders();
//			String newCookie = "";
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equalsIgnoreCase(header.getName()) && header.getValue().contains("JSESSIONID")) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie = newCookie + cookie;
//			resultMap.put("cookie", cookie);
//			resultMap.put("back", back);
//			return resultMap;
//
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return null;
//	}
//
//	private Map<String, String> parsePreparePayResult(String back) {
//		Map<String, String> resultMap = new HashMap<String, String>();
//		try {
//			Document doc = Jsoup.parse(back);
//			Elements input = doc.getElementsByTag("input");
//			for (Element element : input) {
//				String key = element.attr("name");
//				String value = element.val();
//				resultMap.put(key, value);
//			}
//			logger.info("解析后的结果:" + resultMap.toString());
//			return resultMap;
//		} catch (Exception e) {
//			logger.error("error", e);
//		}
//		return null;
//	}
//
//	private String preparePay(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//			Map<String, String> paramMap, String cookie, String lastUrl) {
//		CloseableHttpResponse response = null;
//		try {
//			HttpHost target = new HttpHost("unipay.ceair.com", 443, "https");
//			HttpPost post = new HttpPost("/unipay/preparepay/pay!doPayFl.shtml");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
//				nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//			}
//			nameValue.add(new BasicNameValuePair("card_no", ""));
//			logger.info("doPayFlParam:" + nameValue.toString());
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "unipay.ceair.com");
//			post.setHeader("Referer", lastUrl);
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			response = httpclient.execute(target, post);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			return back;
//
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return null;
//	}
//
//	private Map<String, String> parsePayInfoResult(String back) {
//		Map<String, String> resultMap = new HashMap<String, String>();
//		try {
//			Document doc = Jsoup.parse(back);
//			String accountId = doc.getElementsByAttributeValue("name", "accountId").get(0).val();
//			String announcement = doc.getElementById("announcement").val();
//			String assignBank = doc.getElementsByAttributeValue("name", "assignBank").get(0).val();
//			String bankname = "UNI_YEEPAY_002"; // 易宝支付
//			String broker_ecard_bin = doc.getElementById("broker_ecard_bin").val();
//			String card_length_err = doc.getElementById("card_length_err").val();
//			String card_no = ""; // 有两个
//			String card_no_null = doc.getElementById("card_no_null").val();
//			String choose_a_bank = doc.getElementById("choose_a_bank").val();
//			String countDown = doc.getElementsByAttributeValue("name", "countDown").get(0).val();
//			String credit_back_no = doc.getElementById("credit_back_no").val();
//			String credit_bin_support = doc.getElementById("credit_bin_support").val();
//			String credit_card_no_null = doc.getElementById("credit_card_no_null").val();
//			String credit_phone_no = doc.getElementById("credit_phone_no").val();
//			String credit_regist_id_no = doc.getElementById("credit_regist_id_no").val();
//			String cvv_code = "";
//			String dynamicPointPay = doc.getElementsByAttributeValue("name", "dynamicPointPay").get(0).val();
//			String ecard_bin = doc.getElementById("ecard_bin").val();
//			String ecoupon_bin = doc.getElementById("ecoupon_bin").val();
//			String email = "";
//			String email_addr_err = doc.getElementById("email_addr_err").val();
//			String hour_set = doc.getElementById("hour_set").val();
//			String id_no = "";
//			String id_no_err = doc.getElementById("id_no_err").val();
//			String id_no_null = doc.getElementById("id_no_null").val();
//			String id_type = "0"; // 0身份证 1护照 2军官证 3士兵证 4港澳通行证 6户口本 7其他 12外国人居留证
//			String internal_flag = doc.getElementById("internal_flag").val();
//			String minute_set = doc.getElementById("minute_set").val();
//			String month = "01";
//			String name_err = doc.getElementById("name_err").val();
//			String name_null = doc.getElementById("name_null").val();
//			String new_broker_bin = doc.getElementById("new_broker_bin").val();
//			String order_expired = doc.getElementById("order_expired").val();
//			String owner_mobile = "";
//			String owner_name = "";
//			String password_length_notice = doc.getElementById("password_length_notice").val();
//			String payBank_bankCode = "UNI_YEEPAY_002";
//			String payBank_bankGate = "YEEPAY";
//			String payBank_bankSubCode = "SUBYEEPAY002";
//			String payBank_cardBin = doc.getElementById("payBank_cardBin").val();
//			String payBank_effectMonth = doc.getElementById("payBank_effectMonth").val();
//			String payBank_effectYear = doc.getElementById("payBank_effectYear").val();
//			String payBank_email = doc.getElementById("payBank_email").val();
//			String payBank_idNo = doc.getElementById("payBank_idNo").val();
//			String payBank_idType = doc.getElementById("payBank_idType").val();
//			String payBank_payType = doc.getElementById("payBank_payType").val();
//			String payBank_pointPass = doc.getElementById("payBank_pointPass").val();
//			String payBank_promoId = doc.getElementById("payBank_promoId").val();
//			String payInfo_payAmount = doc.getElementById("payInfoPayAmount").val();
//			String payInfo_payType = doc.getElementById("payInfoPayType").val();
//			String pay_as_total = doc.getElementById("pay_as_total").val();
//			String pay_page_noamount = doc.getElementById("pay_page_noamount").val();
//			String phone_no_err = doc.getElementById("phone_no_err").val();
//			String score_bin = doc.getElementById("score_bin").val();
//			String score_coupon_bin = doc.getElementById("score_coupon_bin").val();
//			String score_kong_bin = doc.getElementById("score_kong_bin").val();
//			String second_set = doc.getElementById("second_set").val();
//			String timeout_notice = doc.getElementById("timeout_notice").val();
//			String unseccess_payment = doc.getElementById("unseccess_payment").val();
//			String unsure_payment = doc.getElementById("unsure_payment").val();
//			String user_lastBank = doc.getElementById("user_lastBank").val();
//			String user_usedBank = doc.getElementById("user_usedBank").val();
//			String validate_fail = doc.getElementById("validate_fail").val();
//			String verify_code_err = doc.getElementById("verify_code_err").val();
//			String verify_code_null = doc.getElementById("verify_code_null").val();
//			String year = "2019";
//			String payBank_couponNo = "";
//			String payBank_cvvNo = "";
//			String payBank_ownerMobile = "";
//			String payBank_ownerName = "";
//			String pay_password = "";
//			resultMap.put("accountId", accountId);
//			resultMap.put("announcement", announcement);
//			resultMap.put("assignBank", assignBank);
//			resultMap.put("bankname", bankname);
//			resultMap.put("broker_ecard_bin", broker_ecard_bin);
//			resultMap.put("card_length_err", card_length_err);
//			resultMap.put("card_no", card_no);
//			resultMap.put("card_no_null", card_no_null);
//			resultMap.put("choose_a_bank", choose_a_bank);
//			resultMap.put("countDown", countDown);
//			resultMap.put("credit_back_no", credit_back_no);
//			resultMap.put("credit_bin_support", credit_bin_support);
//			resultMap.put("credit_card_no_null", credit_card_no_null);
//			resultMap.put("credit_phone_no", credit_phone_no);
//			resultMap.put("credit_regist_id_no", credit_regist_id_no);
//			resultMap.put("cvv_code", cvv_code);
//			resultMap.put("dynamicPointPay", dynamicPointPay);
//			resultMap.put("ecard_bin", ecard_bin);
//			resultMap.put("ecoupon_bin", ecoupon_bin);
//			resultMap.put("email", email);
//			resultMap.put("email_addr_err", email_addr_err);
//			resultMap.put("hour_set", hour_set);
//			resultMap.put("id_no", id_no);
//			resultMap.put("id_no_err", id_no_err);
//			resultMap.put("id_no_null", id_no_null);
//			resultMap.put("id_type", id_type);
//			resultMap.put("internal_flag", internal_flag);
//			resultMap.put("minute_set", minute_set);
//			resultMap.put("month", month);
//			resultMap.put("name_err", name_err);
//			resultMap.put("name_null", name_null);
//			resultMap.put("new_broker_bin", new_broker_bin);
//			resultMap.put("order_expired", order_expired);
//			resultMap.put("owner_mobile", owner_mobile);
//			resultMap.put("owner_name", owner_name);
//			resultMap.put("password_length_notice", password_length_notice);
//			resultMap.put("payBank.bankCode", payBank_bankCode);
//			resultMap.put("payBank.bankGate", payBank_bankGate);
//			resultMap.put("payBank.bankSubCode", payBank_bankSubCode);
//			resultMap.put("payBank.cardBin", payBank_cardBin);
//			resultMap.put("payBank.couponNo", payBank_couponNo);
//			resultMap.put("payBank.cvvNo", payBank_cvvNo);
//			resultMap.put("payBank.effectMonth", payBank_effectMonth);
//			resultMap.put("payBank.effectYear", payBank_effectYear);
//			resultMap.put("payBank.email", payBank_email);
//			resultMap.put("payBank.idNo", payBank_idNo);
//			resultMap.put("payBank.idType", payBank_idType);
//			resultMap.put("payBank.ownerMobile", payBank_ownerMobile);
//			resultMap.put("payBank.ownerName", payBank_ownerName);
//			resultMap.put("payBank.payType", payBank_payType);
//			resultMap.put("payBank.pointPass", payBank_pointPass);
//			resultMap.put("payBank.promoId", payBank_promoId);
//			resultMap.put("payInfo.payAmount", payInfo_payAmount);
//			resultMap.put("payInfo.payType", payInfo_payType);
//			resultMap.put("pay_as_total", pay_as_total);
//			resultMap.put("pay_page_noamount", pay_page_noamount);
//			resultMap.put("pay_password", pay_password);
//			resultMap.put("phone_no_err", phone_no_err);
//			resultMap.put("score_bin", score_bin);
//			resultMap.put("score_coupon_bin", score_coupon_bin);
//			resultMap.put("score_kong_bin", score_kong_bin);
//			resultMap.put("second_set", second_set);
//			resultMap.put("timeout_notice", timeout_notice);
//			resultMap.put("unseccess_payment", unseccess_payment);
//			resultMap.put("unsure_payment", unsure_payment);
//			resultMap.put("user_lastBank", user_lastBank);
//			resultMap.put("user_usedBank", user_usedBank);
//			resultMap.put("validate_fail", validate_fail);
//			resultMap.put("verify_code_err", verify_code_err);
//			resultMap.put("verify_code_null", verify_code_null);
//			resultMap.put("year", year);
//		} catch (Exception e) {
//			logger.error("error", e);
//		}
//		return resultMap;
//	}
//
//	private Map<String, String> payInit(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//			String cookie, String nextUrl, String allCheckToken) {
//		CloseableHttpResponse response = null;
//		try {
//			HttpGet get = new HttpGet(nextUrl);
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "unipay.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "http://www.ceair.com/booking/confirm.html?allCheckToken=" + allCheckToken);
//			get.setHeader("Cookie", cookie);
//			response = httpclient.execute(get);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			String cookies[] = cookie.split(";");
//			String co = "";
//			for (String str : cookies) {
//				if (str.contains("JSESSIONID") || StringUtils.isEmpty(str)) {
//					continue;
//				}
//				co = str + ";" + co;
//			}
//			cookie = co;
//			Header[] headersArr = response.getAllHeaders();
//			String newCookie = "";
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equalsIgnoreCase(header.getName()) && header.getValue().contains("JSESSIONID")) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie += newCookie;
//			logger.info("新增的cookie:" + cookie);
//			Map<String, String> resultMap = new HashMap<String, String>();
//			resultMap.put("cookie", cookie);
//			resultMap.put("back", back);
//			return resultMap;
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return null;
//	}
//
//	private String addContactPax(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie,
//			String orderJson, String allCheckToken, HttpClientContext context) {
//		CloseableHttpResponse response = null;
//		try {
//			long time = System.currentTimeMillis();
//			JSONObject json = new JSONObject(orderJson);
//			String order_id = json.getString("id");
//			String[] dlyAccountInfo = dlyAccount.split(":");
//			String proxyUser = dlyAccountInfo[0];
//			String proxyPass = dlyAccountInfo[1];
//			HttpHost target = new HttpHost("www.ceair.com", 80, "http");
//			HttpGet get = new HttpGet("/otabooking/paxinfo-input!init.shtml?_=" + time);
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "www.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "http://www.ceair.com/booking/confirm.html?allCheckToken=" + allCheckToken);
//			get.setHeader("Cookie", cookie);
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, get, context);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			String sessionVersion = "";
//			if (StringUtils.isNotEmpty(back)) {
//				String regex = "var sessionVersion = \"([0-9]+)\";";
//				Pattern pattern = Pattern.compile(regex);
//				Matcher m = pattern.matcher(back);
//				while (m.find()) {
//					if (StringUtils.isNotEmpty(m.group(1))) {
//						sessionVersion = m.group(1);
//						logger.info(order_id + "sessionVersion:" + sessionVersion);
//					}
//				}
//			}
//			if (StringUtils.isEmpty(sessionVersion)) {
//				JSONObject resultObj = new JSONObject(back);
//				sessionVersion = resultObj.getString("sessionVersion");
//				logger.info(order_id + "sessionVersion:" + sessionVersion);
//			}
//			return sessionVersion;
//
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (Exception e2) {
//			}
//		}
//		return null;
//	}
//
//	private Map<String, String> creatOrder(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//			String cookie, String allCheckToken, HttpClientContext context, String orderJson, boolean flag) {
//		CloseableHttpResponse response = null;
//		String order_id = "";
//		try {
//			JSONObject json = new JSONObject( orderJson );
//			order_id = json.getString( "id" );
//		} catch (JSONException e) {
//			logger.error( "error" , e );
//		}
//		try {
//			String[] dlyAccountInfo = dlyAccount.split(":");
//			String proxyUser = dlyAccountInfo[0];
//			String proxyPass = dlyAccountInfo[1];
//			Map<String, String> resultMap = new HashMap<String, String>();
//			HttpHost target = new HttpHost("www.ceair.com", 80, "http");
//			HttpGet get = new HttpGet(
//					"/otabooking/paxinfo-input!getBooingInfojsonView.shtml?_=" + System.currentTimeMillis());
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "www.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "http://www.ceair.com/booking/confirm.html?allCheckToken=" + allCheckToken);
//			get.setHeader("Cookie", cookie);
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, get, context);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (Exception e) {
//			}
//			get = new HttpGet("/otabooking/paxinfo-input!loadDiscountDetailNew.shtml?allCheckToken=" + allCheckToken
//					+ "&_=" + System.currentTimeMillis());
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "www.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "http://www.ceair.com/booking/confirm.html?allCheckToken=" + allCheckToken);
//			get.setHeader("Cookie", cookie);
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, get, context);
//			back = EntityUtils.toString(response.getEntity(), "utf-8");
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (Exception e) {
//			}
//			get = new HttpGet("/otabooking/paxinfo-input!getTalentInfoNew.shtml?drList=%7B%22checked%22%3Afalse%7D&_="
//					+ System.currentTimeMillis());
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "www.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "http://www.ceair.com/booking/confirm.html?allCheckToken=" + allCheckToken);
//			get.setHeader("Cookie", cookie);
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, get, context);
//			back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info(order_id + "getTalentInfoNew:" + back);
//			if (StringUtils.isNotEmpty(back) && back.contains("会话超时")) {
//				resultMap.put("error", "会话超时");
//				return resultMap;
//			}
//
//			if (flag) {
//				sendVerificationCode(httpclient, defaultRequestConfig, cookie, orderJson, context);
//			}
//
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (Exception e) {
//			}
//			get = new HttpGet(
//					"/booking/verification-code!getBookingVerficationCode.shtml?_=" + System.currentTimeMillis());
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "www.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "http://www.ceair.com/booking/confirm.html?allCheckToken=" + allCheckToken);
//			get.setHeader("Cookie", cookie);
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, get, context);
//			back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info(order_id +"getBookingVerficationCode:" + back);
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (Exception e) {
//			}
//			get = new HttpGet("http://www.ceair.com/otabooking/booking!booking.shtml?checkToken=" + back
//					+ "&allCheckToken=" + allCheckToken);
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "www.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "http://www.ceair.com/booking/confirm.html?allCheckToken=" + allCheckToken);
//			get.setHeader("Cookie", cookie);
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, get, context);
//			back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info(order_id +"创建订单返回:" + back);
//			if (StringUtils.isNotEmpty(back) && back.contains("验证未能通过")) {
//				resultMap.put("error", "验证未能通过，请人工操作");
//				return resultMap;
//			}
//			JSONObject json = new JSONObject(back);
//			String airResultCode = json.getString("airResultCode");
//			if (StringUtils.isEmpty(airResultCode)) {
//				try {
//					String airResultMsg = json.getString("resultMessage");
//					resultMap.put("error", airResultMsg);
//					return resultMap;
//				} catch (Exception e) {
//				}
//				resultMap.put("error", "创单异常");
//				return resultMap;
//			}
//			if (StringUtils.isNotEmpty( airResultCode ) && airResultCode.contains( "BK20000" )) {
//				resultMap.put( "error" , "机票购买失败，请稍后再试 BK20000" );
//				return resultMap;
//			}
//			if (!"1001".equals(airResultCode)) {
//				String airResultMsg = json.getString("airResultMsg");
//				resultMap.put("error", airResultMsg);
//				return resultMap;
//			}
//			String nextUrl = json.getString("nextUrl");
//			long orderNo = json.getLong("orderNo");
//			resultMap.put("nextUrl", nextUrl);
//			resultMap.put("orderNo", orderNo + "");
//			return resultMap;
//
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (Exception e) {
//			}
//		}
//		return null;
//	}
//
//	private String booking(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie,
//			String allCheckToken, String sessionVersion, HttpClientContext context) {
//		CloseableHttpResponse response = null;
//		try {
//			String[] dlyAccountInfo = dlyAccount.split(":");
//			String proxyUser = dlyAccountInfo[0];
//			String proxyPass = dlyAccountInfo[1];
//			HttpHost target = new HttpHost("www.ceair.com", 80, "http");
//			HttpPost post = new HttpPost("/otabooking/paxinfo-input!showCrossSellBookingView.shtml");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			nameValue.add(new BasicNameValuePair("allCheckToken", allCheckToken));
//			nameValue.add(new BasicNameValuePair("sessionVersion", sessionVersion));
//			nameValue.add(new BasicNameValuePair("travelItineraryInfo", "{}"));
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "www.ceair.com");
//			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			post.setHeader("Referer", "http://www.ceair.com/booking/passenger.html?allCheckToken=" + allCheckToken);
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			post.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, post, context);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info(back);
//			return back;
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (Exception e2) {
//				}
//			}
//		}
//		return null;
//	}
//
//	private String savePassengerInfo(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie,
//			String orderJson, String allCheckToken, String sessionVersion, HttpClientContext context) {
//		StringBuffer allPaxInfo = new StringBuffer();
//		allPaxInfo.append("[");
//		String contactInfo = "";
//		String[] dlyAccountInfo = dlyAccount.split(":");
//		String proxyUser = dlyAccountInfo[0];
//		String proxyPass = dlyAccountInfo[1];
//		CloseableHttpResponse response = null;
//		try {
//			JSONObject json = new JSONObject(orderJson);
//			String order_id = json.getString("id");
//			String mobile = json.getString("mobile");
//			String linkMan = json.getString("linkMan");
//			JSONArray passengers = json.getJSONArray("passengers");
//			for (int i = 0; i < passengers.length(); i++) {
//				JSONObject passenger = passengers.getJSONObject(i);
//				String gender = "M";
//				try {
//					gender = passenger.getString("passengerSex");
//					if ("男".equals(gender)) {
//						gender = "M";
//					} else {
//						gender = "F";
//					}
//				} catch (Exception e) {
//				}
//				String idNo = passenger.getString("idcard");
//				String idType = "";
//				try {
//					idType = passenger.getString("passengerCardType");
//				} catch (Exception e) {
//				}
//				if ("身份证".equals(idType)) {
//					idType = "NI"; // 待确认
//				} else {
//					idType = "OTHER";
//				}
//				String birthday = passenger.getString("birthday");
//				if (StringUtils.isNotEmpty(birthday) && !"null".equals(birthday)) {
//					birthday = birthday.substring(0, 10);
//				}
//				if ("NI".equals(idType)) {
//					birthday = "";
//				}
//				String paxType = passenger.getString("passengerType");
//				if ("儿童".equals(paxType)) {
//					paxType = "CHD";
//				} else {
//					paxType = "ADT";
//				}
//				String paxName = passenger.getString("passengerName").trim();
//				allPaxInfo.append("{\"uuid\":" + i + ",\"benePaxListIndex\":\"\",\"birthday\":\"" + birthday
//						+ "\",\"docaCity\":\"Park\",\"docaNationCode\":\"\",\"docaPostCode\":\"19019\",\"docaState\":\"PA\",\"docaStreet\":\"Shinfield Road Reading RG2 7ED\",\"email\":\"\",\"ffpAirline\":\"\",\"ffpLevel\":\"\",\"ffpNo\":\"\",\"gender\":\""
//						+ gender + "\",\"idNo\":\"" + idNo + "\",\"idType\":\"" + idType
//						+ "\",\"id\":\"\",\"idValidDt\":\"\",\"idIssueNation\":\"\",\"nationality\":\"\",\"infCarrierName\":\"\",\"insurance\":false,\"insureInfos\":[],\"mobile\":\""
//						+ mobile + "\",\"contactInfo\":\"\",\"contacts\":\"mobile\",\"cardId\":\"\",\"paxType\":\""
//						+ paxType + "\",\"paxName\":\"" + paxName
//						+ "\",\"paxNameCn\":\"\",\"paxNameFirst\":\"\",\"paxNameLast\":\"\",\"isBeneficariesAssigned\":false,\"isBeneficiary\":\"\",\"paxOrigin\":\"0\",\"idDetails\":[{\"id\":\"\",\"idNo\":\""
//						+ idNo + "\",\"idType\":\"" + idType + "\"}]},");
//				String newAllPaxInfo = "[{\"uuid\":" + i + ",\"benePaxListIndex\":\"\",\"birthday\":\"" + birthday
//						+ "\",\"docaCity\":\"Park\",\"docaNationCode\":\"\",\"docaPostCode\":\"19019\",\"docaState\":\"PA\",\"docaStreet\":\"Shinfield Road Reading RG2 7ED\",\"email\":\"\",\"ffpAirline\":\"\",\"ffpLevel\":\"\",\"ffpNo\":\"\",\"gender\":\""
//						+ gender + "\",\"idNo\":\"" + idNo + "\",\"idType\":\"" + idType
//						+ "\",\"id\":\"\",\"idValidDt\":\"\",\"idIssueNation\":\"\",\"nationality\":\"\",\"infCarrierName\":\"\",\"insurance\":false,\"insureInfos\":[],\"mobile\":\""
//						+ mobile + "\",\"contactInfo\":\"\",\"contacts\":\"mobile\",\"cardId\":\"\",\"paxType\":\""
//						+ paxType + "\",\"paxName\":\"" + paxName
//						+ "\",\"paxNameCn\":\"\",\"paxNameFirst\":\"\",\"paxNameLast\":\"\",\"isBeneficariesAssigned\":false,\"isBeneficiary\":\"\",\"paxOrigin\":\"0\",\"idDetails\":[{\"id\":\"\",\"idNo\":\""
//						+ idNo + "\",\"idType\":\"" + idType + "\"}]}]";
//				HttpHost target = new HttpHost("www.ceair.com", 80, "http");
//				HttpPost post = new HttpPost("/otabooking/paxinfo-input!checkDataNew.shtml");
//				post.setConfig(defaultRequestConfig);
//				List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//				nameValue.add(new BasicNameValuePair("allPaxInfo", newAllPaxInfo));
//				nameValue.add(new BasicNameValuePair("sessionVersion", sessionVersion));
//				logger.info(order_id + "checkDataNew:" + nameValue.toString());
//				post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//				post.setHeader("Host", "www.ceair.com");
//				post.setHeader("Accept", "text/plain, */*; q=0.01");
//				post.setHeader("Accept-Encoding", "gzip, deflate");
//				post.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//				post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//				post.setHeader("Referer", "http://www.ceair.com/booking/passenger.html?allCheckToken=" + allCheckToken);
//				post.setHeader("User-Agent",
//						"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//				post.setHeader("Cookie", cookie);
//				post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//						new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//				post.setHeader("Proxy-Connection", "keep-alive");
//				response = httpclient.execute(target, post, context);
//				String back = EntityUtils.toString(response.getEntity(), "utf-8");
//				try {
//					if (response != null) {
//						response.close();
//					}
//				} catch (Exception e) {
//					logger.error("error", e);
//				}
//				if (StringUtils.isNotEmpty(back) && back.contains("503 Service Unavailable")) {
//					return back;
//				}
//			}
//			if (allPaxInfo != null && allPaxInfo.length() > 0) {
//				allPaxInfo.delete(allPaxInfo.length() - 1, allPaxInfo.length());
//				allPaxInfo.append("]");
//			}
//			contactInfo = "{\"contactName\":\"" + linkMan + "\",\"contactMobile\":\"" + mobile
//					+ "\",\"contactEmail\":\"\",\"id\":\"\"}";
//			HttpHost target = new HttpHost("www.ceair.com", 80, "http");
//			HttpPost post = new HttpPost("/otabooking/paxinfo-input!showBookingInfoNew.shtml");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			nameValue.add(new BasicNameValuePair("allPaxInfo", allPaxInfo.toString()));
//			nameValue.add(new BasicNameValuePair("contactInfo", contactInfo));
//			nameValue.add(new BasicNameValuePair("nonmember", "0"));
//			nameValue.add(new BasicNameValuePair("sessionVersion", sessionVersion));
//			nameValue.add(new BasicNameValuePair("useScore", "false"));
//			logger.info(order_id + "showBookingInfoNew:" + nameValue.toString());
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "www.ceair.com");
//			post.setHeader("Accept", "text/plain, */*; q=0.01");
//			post.setHeader("Accept-Encoding", "gzip, deflate");
//			post.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			post.setHeader("Referer", "http://www.ceair.com/booking/passenger.html?allCheckToken=" + allCheckToken);
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			post.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, post, context);
//			// String back = EntityUtils.toString(response.getEntity(),
//			// "utf-8");
//		} catch (Exception e) {
//			logger.error("error", e);
//			sessionVersion = "";
//		} finally {
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (Exception e2) {
//			}
//		}
//		return sessionVersion;
//	}
//
//	private String selectFlights(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie,
//			String selectConds, HttpClientContext context) {
//		String allCheckToken = "";
//		CloseableHttpResponse response = null;
//		try {
//			String[] dlyAccountInfo = dlyAccount.split(":");
//			String proxyUser = dlyAccountInfo[0];
//			String proxyPass = dlyAccountInfo[1];
//			HttpHost target = new HttpHost("www.ceair.com", 80, "http");
//			HttpPost post = new HttpPost("/otabooking/flight-confirm!flightConfirm.shtml");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			nameValue.add(new BasicNameValuePair("selectConds", selectConds));
//			logger.info("请求参数：" + nameValue.toString());
//			cookie = cookie.replaceAll("Path=/;", "").replaceAll("HttpOnly;", "").replaceAll("Domain=.ceair.com;", "");
//			logger.info("cookie参数：" + cookie);
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "www.ceair.com");
//			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			post.setHeader("Referer", "http://www.ceair.com/booking/szx-sha-181206_CNY.html");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Accept", "text/plain, */*; q=0.01");
//			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			post.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, post, context);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info("flightConfirmResult:" + back);
//			if (StringUtils.isNotEmpty(back) && back.contains("503 Service Unavailable")) {
//				return "retry";
//			}
//			if (StringUtils.isNotEmpty(back) && back.contains("MUSTLOGIN")) {
//				return "MUSTLOGIN";
//			}
//			if (StringUtils.isNotEmpty(back)) {
//				String regex = "flight_booking_passenger.html;([0-9a-zA-Z]{1,})|FFP";
//				Pattern pattern = Pattern.compile(regex);
//				Matcher m = pattern.matcher(back);
//				while (m.find()) {
//					if (StringUtils.isNotEmpty(m.group(1))) {
//						allCheckToken = m.group(1);
//						logger.info("allCheckToken:" + allCheckToken);
//					}
//				}
//			}
//			if (StringUtils.isNotEmpty(allCheckToken)) {
//				return allCheckToken;
//			}
//			return "error";
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (Exception e2) {
//				// TODO: handle exception
//			}
//		}
//		return "";
//	}
//
//	private String parseResult(String back, String orderJson) {
//		try {
//			JSONObject json = new JSONObject(orderJson);
//			JSONObject flight = json.getJSONArray("flights").getJSONObject(0);
//			String sys_flightNo = flight.getString("flightNo");
//			float sys_price = Float.parseFloat(flight.getString("price"));
//			String selectConds = "{\"fscKey\":\"sys_fscKey\",\"selcon\":[{\"airPriceUnitIndex\":sys_airPriceUnitIndex,\"snk\":\"sys_snk\"}]}";
//			JSONObject backJson = new JSONObject(back);
//			String fscKey = backJson.getString("fscKey");
//			JSONArray searchProductArr = backJson.getJSONArray("searchProduct");
//			int i = 0;
//			for (; i < searchProductArr.length(); i++) {
//				JSONObject searchProduct = searchProductArr.getJSONObject(i);
//				String snk = searchProduct.getString("snk");
//				String flightNo = "";
//				String regEx = "0000([0-9A-Z]{2}[0-9]{3,4})[A-Z]+";
//				Pattern pattern = Pattern.compile(regEx);
//				Matcher m = pattern.matcher(snk);
//				while (m.find()) {
//					if (StringUtils.isNotEmpty(m.group(1))) {
//						flightNo = m.group(1);
//					}
//				}
//				if (StringUtils.isEmpty(flightNo) || !flightNo.equals(sys_flightNo)) {
//					continue;
//				}
//				float salePrice = searchProduct.getLong("salePrice");
//				if (salePrice != sys_price) {
//					continue;
//				}
//
//				JSONObject cabin = searchProduct.getJSONObject("cabin");
//				String cabinCode = cabin.getString("cabinCode"); // 舱位
//				String cabinStatus = cabin.getString("cabinStatus"); // 座位数
//				String member = "";
//				try {
//					member = searchProduct.getString("member"); // 是否有会员价
//				} catch (Exception e) {
//				}
//				String productName = searchProduct.getString("productName"); // 舱位类型
//				long index = searchProduct.getLong("index");
//
//				logger.info("cabinCode:" + cabinCode + ",cabinStatus:" + cabinStatus + ",member:" + member
//						+ ",productName:" + productName + ",index:" + index + ",flightNo:" + flightNo + ",salePrice:"
//						+ salePrice);
//
//				selectConds = selectConds.replace("sys_fscKey", fscKey).replace("sys_airPriceUnitIndex", index + "")
//						.replace("sys_snk", snk);
//				break;
//			}
//			if (i == searchProductArr.length()) {
//				return "";
//			}
//			logger.info("selectConds:" + selectConds);
//			return selectConds;
//		} catch (Exception e) {
//			logger.error("error", e);
//		}
//		return "";
//	}
//
//	private String findFlights(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//			BasicCookieStore cookieStore, String cookie, String orderJson, HttpClientContext context, int retry) {
//		CloseableHttpResponse response = null;
//		try {
//			String[] dlyAccountInfo = dlyAccount.split(":");
//			String proxyUser = dlyAccountInfo[0];
//			String proxyPass = dlyAccountInfo[1];
//			JSONObject json = new JSONObject(orderJson);
//			JSONArray flights = json.getJSONArray("flights");
//			String deptCd = flights.getJSONObject(0).getString("departure");
//			String arrCd = flights.getJSONObject(0).getString("arrival");
//			String deptDt = flights.getJSONObject(0).getString("departureDate");
//			HttpHost target = new HttpHost("www.ceair.com", 80, "http");
//			HttpPost post = new HttpPost("/otabooking/flight-search!doFlightSearch.shtml");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			nameValue.add(new BasicNameValuePair("_", MD5Util.getMD5(System.currentTimeMillis() + "")));
//			nameValue.add(new BasicNameValuePair("searchCond",
//					"{\"adtCount\":1,\"chdCount\":0,\"infCount\":0,\"currency\":\"CNY\",\"tripType\":\"OW\",\"recommend\":false,\"reselect\"\r\n"
//							+ ":\"\",\"page\":\"0\",\"sortType\":\"a\",\"sortExec\":\"a\",\"segmentList\":[{\"deptCd\":\""
//							+ deptCd + "\",\"arrCd\":\"" + arrCd + "\",\"deptDt\":\"" + deptDt + "\"\r\n"
//							+ ",\"deptAirport\":\"\",\"arrAirport\":\"\",\"deptCdTxt\":\"\",\"arrCdTxt\":\"\",\"deptCityCode\":\""
//							+ deptCd + "\",\"arrCityCode\"\r\n" + ":\"" + arrCd + "\"}],\"version\":\"A.1.0\"}"));
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "www.ceair.com");
//			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			post.setHeader("Referer", "http://www.ceair.com/booking/szx-sha-181206_CNY.html");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			post.setHeader("Proxy-Connection", "keep-alive");
//			response = httpclient.execute(target, post, context);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			if (StringUtils.isNotEmpty( back ) && back.contains( "航线查询失败" ) && retry < 5) {
//				logger.info( "换代理ip重试查询航班" );
//				//代理云ip
//				String ip_port = DailiyunService.getRandomIp( 50 );
//				String proxyIp = ip_port.split( ":" )[0];
//				int proxyPort = Integer.parseInt( ip_port.split( ":" )[1] );
//				HttpHost dailiyunProxy = new HttpHost( proxyIp , proxyPort , "http" );
//				defaultRequestConfig = RequestConfig.custom()
//						.setSocketTimeout( timeout )
//						.setConnectTimeout( timeout )
//						.setConnectionRequestTimeout( timeout )
//						.setProxy( dailiyunProxy )
//						.setStaleConnectionCheckEnabled( true )
//						.build();
//				retry++;
//				return findFlights( httpclient , defaultRequestConfig , cookieStore , cookie , orderJson , context , retry );
//			} else {
//				return back;
//			}
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (Exception e) {
//			}
//		}
//		return null;
//	}
//
//	private String login(RequestConfig defaultRequestConfig, CloseableHttpClient httpclient, String orderJson,
//			BasicCookieStore cookieStore, HttpHost proxy, String account, HttpClientContext context) {
//		CloseableHttpResponse response = null;
//		try {
//			String[] dlyAccountInfo = dlyAccount.split(":");
//			String proxyUser = dlyAccountInfo[0];
//			String proxyPass = dlyAccountInfo[1];
//			JSONObject orderjson = new JSONObject(orderJson);
//			String order_Id = orderjson.getString("id");
//			String otheraccount = orderjson.getString("otheraccount");
//			HttpHost target = new HttpHost("passport.ceair.com", 443, "https");
//			HttpGet get = new HttpGet("https://passport.ceair.com/");
//			get.setHeader("Host", "passport.ceair.com");
//			get.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			get.setHeader("Proxy-Connection", "keep-alive");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			response = httpclient.execute(target, get, context);
//			List<Cookie> listCookie = cookieStore.getCookies();
//			StringBuffer buf = new StringBuffer();
//
//			if (null != listCookie && listCookie.size() > 0) {
//				for (int i = 0; i < listCookie.size(); i++) {
//					buf.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
//				}
//			}
//			String cookie = buf.toString();
//			logger.info(order_Id + "cookie:" + cookie);
//			// String back = EntityUtils.toString(response.getEntity(),
//			// "utf-8");
//			// logger.info(back);
//
//			String token = "";
//			ScriptEngineManager manager = new ScriptEngineManager();
//			ScriptEngine engine = manager.getEngineByName("js");
//			String jsFileName = "C:\\新建文件夹\\donghang.js";
//			FileReader reader = new FileReader(jsFileName); // 执行指定脚本
//			engine.eval(reader);
//			if (engine instanceof Invocable) {
//				Invocable invoke = (Invocable) engine;
//				token = (String) invoke.invokeFunction("c", "donghang");
//				logger.info(order_Id + "参数加密后返回:" + token);
//			}
//			try {
//				if (reader != null) {
//					reader.close();
//				}
//			} catch (Exception e) {
//			}
//			String apdid_data = "{\"time\":" + System.currentTimeMillis() + ",\"token\":\"" + token + "\"}";
//
//			cookie = cookie + "apdid_data=" + URLEncoder.encode(apdid_data, "utf-8") + ";";
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//					logger.error("error", e);
//				}
//			}
//			HttpPost post = new HttpPost("/cesso/geet!geetInit.shtml");
//			post.setHeader("Host", "passport.ceair.com");
//			post.setHeader("Referer", "https://passport.ceair.com/");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			post.setHeader("Proxy-Connection", "keep-alive");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			response = httpclient.execute(target, post, context);
//			String result = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info(order_Id + "验证码参数返回:" + result);
//			JSONObject json = new JSONObject(result);
//			String challenge = json.getString("challenge");
//			String gt = json.getString("gt");
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//					logger.error("error", e);
//				}
//			}
//			post = new HttpPost("/cesso/login-static!check.shtml");
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			nameValue.add(new BasicNameValuePair("local", "zh_CN"));
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "passport.ceair.com");
//			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			post.setHeader("Referer", "https://passport.ceair.com/");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			post.setHeader("Proxy-Connection", "keep-alive");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			response = httpclient.execute(target, post, context);
//			result = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info(order_Id + "check返回:" + result);
//			Header[] headersArr = response.getAllHeaders();
//			String newCookie = "";
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equals(header.getName())) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie = cookie + newCookie;
//
//			String gestResult = GeetestRecognition.recognition(gt,
//					"https://passport.ceair.com/cesso/geet!geetInit.shtml", challenge, otheraccount.split("_")[1]);
//			logger.info(order_Id + "极验打码返回:" + gestResult);
//			JSONObject resultJson = new JSONObject(gestResult);
//			int status = resultJson.getInt("status");
//			if (status != 0) {
//				String msg = resultJson.getString("msg");
//				return "error:" + msg;
//			}
//			JSONObject dataObj = resultJson.getJSONObject("data");
//			String geetest_challenge = dataObj.getString("challenge");
//			String geetest_validate = dataObj.getString("validate");
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//					logger.error("error", e);
//				}
//			} // msg status
//			post = new HttpPost("/cesso/login-static!auth.shtml");
//			nameValue = new ArrayList<NameValuePair>();
//			nameValue.add(new BasicNameValuePair("at", "1"));
//			nameValue.add(new BasicNameValuePair("geetest_challenge", geetest_challenge));
//			nameValue.add(new BasicNameValuePair("geetest_seccode", geetest_validate + "|jordan"));
//			nameValue.add(new BasicNameValuePair("geetest_validate", geetest_validate));
//			nameValue.add(new BasicNameValuePair("ltv", "1"));
//			nameValue.add(new BasicNameValuePair("password", account.split("_")[1]));
//			nameValue.add(new BasicNameValuePair("token", token));
//			nameValue.add(new BasicNameValuePair("user", account.split("_")[0]));
//			nameValue.add(new BasicNameValuePair("validateType", "geek"));
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "passport.ceair.com");
//			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			post.setHeader("Referer", "https://passport.ceair.com/");
//			post.setHeader("Cookie", cookie);
//			post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(
//					new StringBuilder(proxyUser).append(":").append(proxyPass).toString().getBytes("utf-8")));
//			post.setHeader("Proxy-Connection", "keep-alive");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			response = httpclient.execute(target, post, context);
//			result = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info(order_Id + "登录返回:" + result);
//
//			headersArr = response.getAllHeaders();
//			newCookie = "";
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equals(header.getName())) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie = cookie + newCookie;
//			logger.info(order_Id + "最终取到的cookie:" + cookie);
//			return cookie;
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//					logger.error("error", e);
//				}
//			}
//		}
//		return "";
//	}
//
//	private String tologin(RequestConfig defaultRequestConfig, CloseableHttpClient httpclient, String orderJson,
//			BasicCookieStore cookieStore, String cookie, String account) {
//		InputStream re = null;
//		try {
//			JSONObject json = new JSONObject(orderJson);
//			String orderId = json.getString("id");
//			String otheraccount = json.getString("otheraccount");
//			String otherusername = "b";
//			String otherpassword = "b";
//			if (otheraccount.contains("_") && otheraccount.split("_").length == 2) {
//				otherusername = otheraccount.split("_")[0];
//				otherpassword = otheraccount.split("_")[1];
//			}
//			HttpHost target = new HttpHost("passport.ceair.com", 443, "https");
//			String password = account.split("_")[1];
//			String user = account.split("_")[0];
//			String random = "0." + (long) ((Math.random() + 1) * 10000000000000000L);
//			HttpGet get = new HttpGet("/cesso/kaptcha.servlet?_" + random);
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Host", "easternmiles.ceair.com");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Referer", "https://passport.ceair.com/cesso/login.html");
//			get.setHeader("Cookie", cookie);
//			CloseableHttpResponse response = httpclient.execute(target, get);
//			re = response.getEntity().getContent();
//			OutputStream os = null;
//			String fileUri = "C://testImg//" + random + ".jpg";
//			os = new FileOutputStream(fileUri);
//			IOUtils.copy(re, os);
//			os.close();
//
//			InputStream is = new FileInputStream(new File(fileUri));
//
//			String validtext = YunSu.getValidCode(is, "", otherusername, otherpassword);
//			File file = new File(fileUri);
//			if (file.exists()) {
//				file.delete();
//			}
//			logger.info("调用接口获取验证码返回：" + validtext);
//			org.dom4j.Document document = DocumentHelper.parseText(validtext);
//			org.dom4j.Element root = document.getRootElement();
//			String error = root.elementText("Error");
//			String result = "";
//			if (error != null && !"".equals(error)) {
//				result = error;
//				if (result.contains("点数不足")) {
//					return "验证码点数不足";
//				} else if (result.contains("密码错误")) {
//					return result;
//				}
//			} else {
//				result = root.elementText("Result");
//			}
//
//			// Scanner scan = new Scanner(System.in);
//			// validcode = scan.nextLine();
//
//			HttpPost post = new HttpPost("/cesso/login-static!auth.shtml");
//			post.setConfig(defaultRequestConfig);
//			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//			nameValue.add(new BasicNameValuePair("at", "1"));
//			nameValue.add(new BasicNameValuePair("ltv", "1"));
//			nameValue.add(new BasicNameValuePair("password", password));
//			nameValue.add(new BasicNameValuePair("token", ""));
//			nameValue.add(new BasicNameValuePair("user", user));
//			nameValue.add(new BasicNameValuePair("validcode", result));
//			post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//			post.setHeader("Host", "easternmiles.ceair.com");
//			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			post.setHeader("Referer", "https://passport.ceair.com/cesso/login.html");
//			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			post.setHeader("Cookie", cookie);
//			response = httpclient.execute(target, post);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
//			logger.info(orderId + back);
//			if (StringUtils.isNotEmpty(back) && back.contains("接口连接异常")) {
//				return back;
//			}
//			if (StringUtils.isNotEmpty(back) && back.contains("验证码错误")) {
//				return back;
//			}
//			if (StringUtils.isNotEmpty(back) && back.contains("密码输入错误")) {
//				return back;
//			}
//			Header[] headersArr = response.getAllHeaders();
//			String newCookie = "";
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equalsIgnoreCase(header.getName()) && header.getValue().contains("com.ceair.cesso")) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie += newCookie;
//			logger.info("返回的cookie:" + cookie);
//			return cookie;
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			if (re != null) {
//				try {
//					re.close();
//				} catch (Exception e2) {
//				}
//			}
//		}
//		return "";
//	}
//
//	private Map<String, String> register(RequestConfig defaultRequestConfig, CloseableHttpClient httpclient,
//			String orderJson, int requestType) throws Exception {
//		Map<String, String> resultMap = new HashMap<String, String>();
//		// 身份证 复姓
//		JSONObject jsonObj = new JSONObject(orderJson);
//		String billNo = "";
//		try {
//			billNo = jsonObj.getString("billNo");
//		} catch (Exception e) {
//		}
//		String order_id = jsonObj.getString("id");
//		String childrenUser = jsonObj.getString("username");
//		JSONArray passengers = jsonObj.getJSONArray("passengers");
//		String phoneNumber = "";
//		String token = "";
//		String resultPassword = "";
//		String resultIdCard = "";
//		String orderId = jsonObj.getString("id");
//		int index = 0;
//		while (index < passengers.length()) {
//			JSONObject passenger = passengers.getJSONObject(index);
//			String idCard = passenger.getString("idcard");
//			String passengerName = passenger.getString("passengerName");
//			resultIdCard = idCard;
//			try {
//				/*
//				 * 先判断该账号是否注册过会员
//				 */
//				boolean ifContinue = valid(defaultRequestConfig, httpclient, idCard);
//				if (!ifContinue) {
//					if (requestType == 1) {
//						sendAccountInfo("", passengerName, "", order_id, childrenUser, "1", "该账号已被注册", billNo);
//					} else {
//						sendAccountInfo("", passengerName, "", "", childrenUser, "1", "该账号已被注册", billNo);
//					}
//					index++;
//					continue;
//				}
//			} catch (Exception e) {
//				logger.error("error", e);
//				continue;
//			}
//			try {
//				String email = System.currentTimeMillis() + ((int) (1 + Math.random() * 10)) + "@163.com";
//				String queryPassword = "12345678"; // 密码 统一密码
//				resultPassword = queryPassword;
//				// Map<String, String> phoneMap = YiMa.getPhoneNumber();
//				Map<String, String> phoneMap = null;
//				phoneNumber = phoneMap.get("phoneNumber");
//				logger.info("千万卡获取到手机号码:" + phoneNumber);
//				token = phoneMap.get("token");
//				String birthday = passenger.getString("birthday");
//				if (StringUtils.isNotEmpty(birthday) && !"null".equals(birthday)) {
//					birthday = birthday.substring(0, 10);
//				}
//				if (StringUtils.isEmpty(passengerName) || passengerName.contains("/")) {
//					continue;
//				}
//				String firstCnName = passengerName.substring(0, 1);
//				String firstEnName = "";
//				String lastCnName = passengerName.substring(1);
//				String lastEnName = "";
//				ScriptEngineManager manager = new ScriptEngineManager();
//				ScriptEngine engine = manager.getEngineByName("js");
//				String jsFileName = "C:\\新建文件夹\\plugins.min.js";
//				FileReader reader = new FileReader(jsFileName); // 执行指定脚本
//				engine.eval(reader);
//				String phone = phoneNumber;
//				if (engine instanceof Invocable) {
//					Invocable invoke = (Invocable) engine;
//					firstEnName = (String) invoke.invokeFunction("ConvertSpell", firstCnName);
//					logger.info("firstEnName:" + firstEnName);
//					lastEnName = (String) invoke.invokeFunction("ConvertSpell", lastCnName);
//					logger.info("lastEnName:" + lastEnName);
//					phoneNumber = (String) invoke.invokeFunction("getPhoneNum", phoneNumber);
//					logger.info("phoneNumber:" + phoneNumber);
//					birthday = (String) invoke.invokeFunction("getPhoneNum", birthday);
//					logger.info("birthday:" + birthday);
//					email = (String) invoke.invokeFunction("getPhoneNum", email);
//					logger.info("email:" + email);
//					idCard = (String) invoke.invokeFunction("getPhoneNum", idCard);
//					logger.info("idCard:" + idCard);
//					queryPassword = (String) invoke.invokeFunction("getPhoneNum", queryPassword);
//					logger.info("queryPassword:" + queryPassword);
//				}
//				HttpHost target = new HttpHost("easternmiles.ceair.com", 443, "https");
//				HttpPost post = new HttpPost("/mpf/register/sendCode?locale=cn");
//				post.setConfig(defaultRequestConfig);
//				String json = "{\"cardType\":\"IDCRD\",\"nationality\":\"CN\",\"contactLanguage\":\"ZH\",\"billSendWay\":\"EML\",\"homeCountryCode\":\"CN\"\r\n"
//						+ ",\"homeProvinceCode\":\"GD\",\"firstCnName\":\"" + firstCnName + "\",\"firstEnName\":\""
//						+ firstEnName + "\",\"lastCnName\":\"" + lastCnName + "\",\"lastEnName\":\"" + lastEnName
//						+ "\"\r\n" + ",\"birthday\":\"" + birthday
//						+ "\",\"sex\":\"M\",\"adultType\":2,\"phoneNumber\":\"" + phoneNumber + "\"\r\n"
//						+ ",\"email\":\"" + email
//						+ "\",\"mobileSendWayOne\":true,\"emailSendWayOne\":true,\"homeCityCode\":\"深圳\",\"homeAdress\"\r\n"
//						+ ":\"广东深圳\",\"locale\":\"zh-CN\"}";
//				logger.info(orderId + json);
//				StringEntity entity = new StringEntity(json, Charset.forName("UTF-8"));
//				post.setEntity(entity);
//				post.setHeader("Host", "easternmiles.ceair.com");
//				post.setHeader("Content-Type", "application/json;charset=utf-8");
//				post.setHeader("Referer", "https://easternmiles.ceair.com/mpf/");
//				post.setHeader("User-Agent",
//						"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//				post.setHeader("Cookie", "UUID=C574DC8E4F684FD3B5372BFF9504CD8B");
//				CloseableHttpResponse response = httpclient.execute(target, post);
//				String back = EntityUtils.toString(response.getEntity(), "utf-8");
//				logger.info(orderId + "返回内容：" + back);
//				JSONObject resultObj = new JSONObject(back);
//				long id = resultObj.getLong("id");
//				logger.info("id:" + id);
//
//				if (id == 0) {
//					response = httpclient.execute(target, post);
//					back = EntityUtils.toString(response.getEntity(), "utf-8");
//					logger.info("重试：" + back);
//					resultObj = new JSONObject(back);
//					id = resultObj.getLong("id");
//					logger.info("id:" + id);
//				}
//
//				String code = "";
//				Thread.sleep(5 * 1000);
//				String smsInfo = "";
//				smsInfo = YiKa.getSmsCode(token, phone);
//				if (smsInfo == null) {
//					smsInfo = YiKa.getSmsCode(token, phone);
//				}
//				logger.info(orderId + ",获取的验证码信息:" + smsInfo);
//				code = smsInfo.split("_")[1];
//				post = new HttpPost("/mpf/register/registerMember?locale=cn");
//				json = "{\"cardType\":\"IDCRD\",\"nationality\":\"CN\",\"contactLanguage\":\"ZH\",\"billSendWay\":\"EML\",\"homeCountryCode\":\"CN\"\r\n"
//						+ ",\"homeProvinceCode\":\"GD\",\"firstCnName\":\"" + firstCnName + "\",\"firstEnName\":\""
//						+ firstEnName + "\",\"lastCnName\":\"" + lastCnName + "\",\"lastEnName\":\"" + lastEnName
//						+ "\"\r\n" + ",\"birthday\":\"" + birthday
//						+ "\",\"sex\":\"M\",\"adultType\":2,\"phoneNumber\"\r\n" + ":\"" + phoneNumber
//						+ "\",\"email\":\"" + email + "\"\r\n"
//						+ ",\"mobileSendWayOne\":true,\"emailSendWayOne\":true,\"homeCityCode\":\"深圳\",\"homeAdress\":\"广东深圳\",\"locale\":\"zh-CN\"\r\n"
//						+ ",\"id\":" + id + ",\"code\":\"" + code + "\",\"idCard\":\"" + idCard + "\"\r\n"
//						+ ",\"passport\":null,\"militaryCard\":null,\"extEnterPermits\":null,\"otherCard\":null,\"queryPassword\":\""
//						+ queryPassword + "\"\r\n" + "}";
//				logger.info(orderId + json);
//				entity = new StringEntity(json, Charset.forName("UTF-8"));
//				post.setEntity(entity);
//				post.setHeader("Host", "easternmiles.ceair.com");
//				post.setHeader("Content-Type", "application/json;charset=utf-8");
//				post.setHeader("Accept", "application/json, text/plain, */*");
//				post.setHeader("Connection", "keep-alive");
//				post.setHeader("Referer", "https://easternmiles.ceair.com/mpf/");
//				post.setHeader("User-Agent",
//						"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//				post.setHeader("Cookie", "UUID=C574DC8E4F684FD3B5372BFF9504CD8B");
//				response = httpclient.execute(target, post);
//				back = EntityUtils.toString(response.getEntity(), "utf-8");
//				logger.info(orderId + "返回内容：" + back);
//				String accountInfo = resultIdCard + "_" + resultPassword;
//				resultMap.put("accountInfo", accountInfo);
//				resultMap.put("phone", phone);
//				resultMap.put("passengerName", passengerName);
//				return resultMap;
//			} catch (Exception e) {
//				index++;
//				if (requestType == 1) {
//					sendAccountInfo("", passengerName, "", order_id, childrenUser, "1", "注册异常", billNo);
//				} else {
//					sendAccountInfo("", passengerName, "", "", childrenUser, "1", "注册异常", billNo);
//				}
//				logger.error("error", e);
//				continue;
//				// if (e.getCause() == null) {
//				// logger.error("error", e);
//				// }
//				// if (e.getCause().toString().contains("BOFid.card.existEOF"))
//				// {
//				// logger.info("该账号已注册");
//				// index++;
//				// } else if
//				// (e.getCause().toString().contains("BOFcaptcha.overTimeEOF"))
//				// {
//				// logger.info("验证码超时");
//				// } else if
//				// (e.getCause().toString().contains("BOFcommunicateRecord.notExistEOF"))
//				// {
//				// logger.info("验证码失效");
//				// } else if
//				// (e.getCause().toString().contains("BOFcaptcha.wrongEOF")) {
//				// logger.info("验证码错误");
//				// } else if
//				// (e.getCause().toString().contains("BOFemail.existEOF")) {
//				// logger.info("邮箱已注册");
//				// index++;
//				// } else if
//				// (e.getCause().toString().contains("BOFcaptcha.hasVerifiedEOF"))
//				// {
//				// logger.info("权限错误");
//				// index++;
//				// } else {
//				// logger.error("error", e);
//				// }
//			} catch (Throwable e) {
//				logger.error("error", e);
//			}
//		}
//		return null;
//	}
//
//	private boolean valid(RequestConfig defaultRequestConfig, CloseableHttpClient httpclient, String cardNo1)
//			throws Exception {
//		HttpHost target = new HttpHost("easternmiles.ceair.com", 443, "https");
//		HttpPost post = new HttpPost("/mpf/password/valid?locale=cn");
//		post.setConfig(defaultRequestConfig);
//		String json = "{\"cardType1\":2,\"cardType2\":1,\"cardNo1\":\"" + cardNo1 + "\"}";
//		logger.info(json);
//		StringEntity entity = new StringEntity(json, Charset.forName("UTF-8"));
//		post.setEntity(entity);
//		post.setHeader("Host", "easternmiles.ceair.com");
//		post.setHeader("Content-Type", "application/json;charset=utf-8");
//		post.setHeader("Referer", "https://easternmiles.ceair.com/mpf/");
//		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		post.setHeader("Cookie", "UUID=C574DC8E4F684FD3B5372BFF9504CD8B");
//		CloseableHttpResponse response = httpclient.execute(target, post);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		logger.info("validResponse:" + back);
//		JSONObject resultObj = new JSONObject(back);
//		String memberId = "";
//		try {
//			memberId = resultObj.getString("memberId");
//		} catch (Exception e) {
//		}
//		if (StringUtils.isNotEmpty(memberId) && !"null".equalsIgnoreCase(memberId)) {
//			logger.info("memberId:" + memberId);
//			return false;
//		}
//		return true;
//	}
//
//	public String getRand() {
//		Random r = new Random();
//		long rand = Math.abs(r.nextLong());
//		String randStr = "0." + rand;
//		if (randStr.length() > 18) {
//			randStr = randStr.substring(0, 18);
//		}
//		return randStr;
//	}
//
//	/**
//	 * 推送创单情况 String result = request.getParameter("result"); //是否创单成功 String
//	 * message = request.getParameter("message"); //失败消息 String price =
//	 * request.getParameter("price"); //采购总金额 String childrenUser =
//	 * request.getParameter("childrenUser");//子帐号 String newOrderId =
//	 * request.getParameter("newOrderId"); //创建订单成功后的官网订单号 String orderId =
//	 * request.getParameter("orderId"); //原订单主键ID String isPassuccess =
//	 * request.getParameter("isPassuccess"); //是否支付成功 String isPassenge =
//	 * request.getParameter("isPassenge"); //是否票号回填 String passengeMessage =
//	 * request.getParameterValues("passengeMessage"); // 获取票号回填到系统
//	 * 格式为:姓名A##生份证##票号@_@姓名B##生份证##票号 String payTransactionid =
//	 * request.getParameter("payTransactionid"); //获取票号回填的交易号 SC时代表联系电话 String
//	 * payStatus = request.getParameter("payStatus"); //获取支付方式 String isSuccess
//	 * = request.getParameter("isSuccess"); //是否完结 String isautoB2C =
//	 * request.getParameter("isautoB2C"); //是否自动出票 String ifUsedCoupon =
//	 * request.getParameter("ifUsedCoupon"); //是否使用红包
//	 */
//	/*
//	 * payStatus(易宝信用卡、易宝会员)
//	 */
//	public static String sendCreateOrderInfo(String result, String message, String price, String childrenUser,
//			String newOrderId, String orderId, String isPassuccess, String isPassenge, String passengeMessage,
//			String payStatus, String payTransactionid, String ifUsedCoupon, String isSuccess, String billNo,
//			int requestType) {
//		try {
//			if (requestType == 1) {
//				return null;
//			}
//			logger.info(orderId + ":" + result + message);
//			String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrl");
//			StringBuffer buffer = new StringBuffer();
//			buffer.append("<feeye-official>");
//			buffer.append("<official>" + "MU" + "</official> ");
//			buffer.append("<url>" + orderUrl + "</url> ");
//			buffer.append("<type>0</type> ");
//			buffer.append("<method>post</method>");
//			buffer.append("<max>20</max> ");
//			buffer.append("<encod>utf-8</encod> ");
//			buffer.append("<params>");
//			buffer.append("<param name='result'>" + result + "</param>");
//			buffer.append("<param name='message'>" + message + "</param>");
//			buffer.append("<param name='price'>" + price + "</param>");
//			buffer.append("<param name='childrenUser'>" + childrenUser + "</param>");
//			buffer.append("<param name='newOrderId'>" + newOrderId + "</param>");
//			buffer.append("<param name='orderId'>" + orderId + "</param>");
//			buffer.append("<param name='isPassuccess'>" + isPassuccess + "</param>");
//			buffer.append("<param name='isPassenge'>" + isPassenge + "</param>");
//			buffer.append("<param name='passengeMessageOther'>" + passengeMessage + "</param>");
//			buffer.append("<param name='payStatus'>" + payStatus + "</param>");
//			buffer.append("<param name='payTransactionid'>" + payTransactionid + "</param>");
//			buffer.append("<param name='ifUsedCoupon'>" + ifUsedCoupon + "</param>");
//			buffer.append("<param name='isSuccess'>" + isSuccess + "</param>");
//			buffer.append("<param name='billNo'>" + billNo + "</param>");
//			buffer.append("<param name='dicountMoney'>" + 0 + "</param>");
//
//			buffer.append("</params>");
//			buffer.append("</feeye-official>");
//
//			logger.info(buffer.toString());
//			String content = OfficialMain.setRequestParams(buffer.toString());
//			if (content != null) {
//				String rs1[] = content.split("#@_@#");
//				if (rs1.length == 2) {
//					content = rs1[1];
//					return content;
//				}
//				if (rs1.length == 3) {
//					logger.info(rs1[2]);
//					return rs1[2];
//				}
//			}
//
//		} catch (Exception e) {
//			logger.error("error", e);
//		}
//		return null;
//	}
//
//	/**
//	 * 推送创单情况 String childrenUser = request.getParameter("childrenUser");//子帐号
//	 * String orderId = request.getParameter("orderId"); //原订单主键ID String
//	 * payStatus = request.getParameter("payStatus"); //获取支付方式
//	 */
//	public static String sendOrderStatus(String childrenUser, String orderId, String status, int requestType) {
//		try {
//			if (requestType == 1) {
//				return null;
//			}
//			logger.info(orderId + status);
//			String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrlStatus");
//
//			StringBuffer buffer = new StringBuffer();
//			buffer.append("<feeye-official>");
//			buffer.append("<official>MU</official> ");
//			buffer.append("<url>" + orderUrl + "</url> ");
//			buffer.append("<type>0</type> ");
//			buffer.append("<method>post</method>");
//			buffer.append("<max>20</max> ");
//			buffer.append("<encod>utf-8</encod> ");
//			buffer.append("<params>");
//			buffer.append("<param name='childrenUser'>" + childrenUser + "</param>");
//			buffer.append("<param name='orderId'>" + orderId + "</param>");
//			buffer.append("<param name='orderStatus'>" + status + "</param>");
//
//			buffer.append("</params>");
//			buffer.append("</feeye-official>");
//
//			String content = OfficialMain.setRequestParams(buffer.toString());
//			if (content != null) {
//				String rs1[] = content.split("#@_@#");
//				if (rs1.length == 2) {
//					content = rs1[1];
//					return content;
//				}
//				if (rs1.length == 3) {
//					logger.info(rs1[2]);
//					return rs1[2];
//				}
//			}
//		} catch (Exception e) {
//			logger.error("推送\"" + status + "\"情况异常");
//		}
//		return null;
//	}
//
//	/**
//	 * 推送会员账号 String phone = request.getParameter("phone");//注册手机号 String name =
//	 * request.getParameter("name"); //注册的乘客名 accountInfo =
//	 * request.getParameter("accountInfo"); //注册的账号密码 格式：身份证_密码
//	 *
//	 * @param rejStatus
//	 * @param rejCase
//	 */
//	public static String sendAccountInfo(String phone, String name, String accountInfo, String orderId,
//			String childrenUser, String rejStatus, String rejCase, String billNo) {
//		try {
//			logger.info(orderId + accountInfo);
//			String orderUrl = PropertiesUtils.getPropertiesValue("config", "sendAccountInfo");
//
//			StringBuffer buffer = new StringBuffer();
//			buffer.append("<feeye-official>");
//			buffer.append("<official>MU</official> ");
//			buffer.append("<url>" + orderUrl + "</url> ");
//			buffer.append("<type>0</type> ");
//			buffer.append("<method>post</method>");
//			buffer.append("<max>20</max> ");
//			buffer.append("<encod>utf-8</encod> ");
//			buffer.append("<params>");
//			buffer.append("<param name='phone'>" + phone + "</param>");
//			buffer.append("<param name='id'>" + orderId + "</param>");
//			buffer.append("<param name='name'>" + name + "</param>");
//			buffer.append("<param name='accountInfo'>" + accountInfo + "</param>");
//			buffer.append("<param name='childrenUser'>" + childrenUser + "</param>");
//			buffer.append("<param name='rejStatus'>" + rejStatus + "</param>");
//			buffer.append("<param name='rejCase'>" + rejCase + "</param>");
//			buffer.append("<param name='billNo'>" + billNo + "</param>");
//			buffer.append("</params>");
//			buffer.append("</feeye-official>");
//
//			String content = OfficialMain.setRequestParams(buffer.toString());
//			if (content != null) {
//				String rs1[] = content.split("#@_@#");
//				if (rs1.length == 2) {
//					content = rs1[1];
//					return content;
//				}
//				if (rs1.length == 3) {
//					logger.info(rs1[2]);
//					return rs1[2];
//				}
//			}
//		} catch (Exception e) {
//			logger.error("推送\"" + accountInfo + "\"情况异常");
//		}
//		return null;
//	}
//
//	private String cancel(String url, String id, String childrenUser) {
//		CloseableHttpClient client = null;
//		CloseableHttpResponse response = null;
//		InputStream re = null;
//		HttpGet get = null;
//		HttpPost post = null;
//		String result = null;
//		try {
//			BasicCookieStore cookieStore = new BasicCookieStore();
//			Integer timeout = Integer.parseInt("60000");
//			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
//					.build();
//			client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//			List<BasicNameValuePair> nameValueParis = new ArrayList<BasicNameValuePair>();
//			nameValueParis.add(new BasicNameValuePair("orderId", id));
//			nameValueParis.add(new BasicNameValuePair("codetype", "order"));
//			nameValueParis.add(new BasicNameValuePair("childrenUser", childrenUser));
//			post = new HttpPost(url);
//			post.setEntity(new UrlEncodedFormEntity(nameValueParis, "utf-8"));
//			post.setConfig(requestConfig);
//			response = client.execute(post);
//			result = EntityUtils.toString(response.getEntity(), "utf-8");
//			JSONObject jo = new JSONObject(result);
//			result = jo.getString("msg");
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			try {
//				if (re != null) {
//					re.close();
//				}
//				if (response != null) {
//					response.close();
//				}
//				if (get != null) {
//					get.releaseConnection();
//				}
//				if (post != null) {
//					post.releaseConnection();
//				}
//				if (client != null) {
//					client.close();
//				}
//			} catch (Exception e) {
//				logger.error("error", e);
//			}
//		}
//		return result;
//	}
//}
