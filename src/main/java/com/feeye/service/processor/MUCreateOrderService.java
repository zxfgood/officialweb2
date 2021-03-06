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
//				+ "    \"mobile\": \"13532989542\",\r\n" + "    \"linkMan\": \"??????\",\r\n"
//				+ "    \"email\": \"471804731\",\r\n" + "    \"isOutticket\": \"true\",\r\n"
//				+ "    \"ytype\": \"??????\",\r\n" + "    \"passengers\": [\r\n" + "        {\r\n"
//				+ "            \"passengerName\": \"??????\",\r\n" + "            \"idcard\": \"441423199501081014\",\r\n"
//				+ "            \"passengerType\": \"??????\",\r\n" + "            \"passengercardType\": \"?????????\",\r\n"
//				+ "            \"birthday\": \"1995-01-08\",\r\n" + "            \"passengerSex\": \"???\"\r\n"
//				+ "        },\r\n" + "		{\r\n" + "            \"passengerName\": \"?????????\",\r\n"
//				+ "            \"idcard\": \"441423199501251044\",\r\n" + "            \"passengerType\": \"??????\",\r\n"
//				+ "            \"passengercardType\": \"?????????\",\r\n" + "            \"birthday\": \"1995-01-25\",\r\n"
//				+ "            \"passengerSex\": \"???\"\r\n" + "        }\r\n" + "    ],\r\n"
//				+ "    \"ifUsedCoupon\": false,\r\n" + "    \"drawerType\": \"tuNiu\",\r\n"
//				+ "    \"qiangpiao\": \"\",\r\n" + "    \"newOrderNo\": \"null\",\r\n"
//				+ "    \"otheraccount\": \"\",\r\n" + "    \"creditNo\": \"5254980012170051\",\r\n"
//				+ "    \"expireMonth\": \"12\",\r\n" + "    \"expireYear\": \"2022\",\r\n"
//				+ "    \"ownername\": \"??????\",\r\n" + "    \"idCardNo\": \"441423199501081014\",\r\n"
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
//	 * ??????????????? ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ???????????????????????????????????????????????????????????????
//	 *
//	 * @param retryCount
//	 */
//	public String StartCreateOrder(String orderJson, int requestType, int retryCount, int exceptionCount) {
//		if (StringUtils.isEmpty(orderJson)) {
//			return "ERROR:???????????????";
//		}
//		logger.info("??????????????????MU:" + orderJson);
//		SSLConnectionSocketFactory sslsf = null;
//		BasicCookieStore cookieStore = new BasicCookieStore();// ??????cookies
//		try {
//			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
//				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//					return true;
//				}
//			}).build();
//			sslsf = new SSLConnectionSocketFactory(sslContext,
//					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			// ?????????SSL??????
//		} catch (Exception e) {
//			logger.error("error", e);
//		}
//		BasicScheme proxyAuth = new BasicScheme();
//		try {
//			proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH , "BASIC realm=default"));
//		} catch (MalformedChallengeException e1) {
//			logger.error("error" , e1);
//		}
//		//?????????ip
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
//		//??????????????????
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
//				return 60 * 1000;//?????????????????????????????????????????????60s
//			}
//		};
//
////		SSLContext sslcontext = SSLContexts.createDefault();
////		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" },
////				null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
//
//		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//				.register("http", PlainConnectionSocketFactory.getSocketFactory())
//				.register("https", sslsf)  // ???????????????????????????
//				.build();
//		// ???????????????
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
//			// boolean isMemberPrice = policyType.contains("??????");
//			// boolean hasRegister = false;
//			JSONArray passengers = json.getJSONArray("passengers");
//			/*
//			 * ????????????????????????????????????????????????member???1?????????????????????account????????????????????????????????????
//			 */
//
//			boolean defaultAccount = false; // ???????????????????????????????????????????????????
//			// ??????
//			sendOrderStatus(childrenUser, order_id, "????????????", requestType);
//			String loginCookie = "";
//			for (int i = 0; i < 5; i++) {
//				cookie = login(defaultRequestConfig, httpclient, orderJson, cookieStore, null, account, context);
//				if (StringUtils.isEmpty(cookie)) {
//					if (exceptionCount < 10) {
//						exceptionCount++;
//						return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
//					} else {
//						sendCreateOrderInfo("error", "????????????", "", childrenUser, "", order_id, "", "", null, "", "",
//								"false", "true", billNo, requestType);
//						return "ERROR:????????????";
//					}
//				}
//				if (StringUtils.isNotEmpty(cookie) && cookie.contains("error")) {
//					if (cookie.contains("????????????") || cookie.contains("????????????")) {
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
//				if (StringUtils.isNotEmpty(cookie) && (cookie.contains("??????????????????"))) {
//					if (retryCount < 2) {
//						retryCount++;
//						return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
//					} else {
//						JSONObject errorJson = new JSONObject(cookie);
//						String resultMessage = errorJson.getString("resultMessage");
//						if (defaultAccount) {
//							resultMessage = "?????????????????????????????????????????????????????????????????????????????????" + resultMessage;
//						} else {
//							resultMessage = "??????????????????";
//						}
//						sendCreateOrderInfo("error", resultMessage, "", childrenUser, "", order_id, "", "", null, "",
//								"", "false", "true", billNo, requestType);
//						return "ERROR:????????????";
//					}
//				}
//				if (StringUtils.isNotEmpty(cookie) && (cookie.contains("???????????????"))) {
//					if (retryCount < 2) {
//						retryCount++;
//						return StartCreateOrder(orderJson, requestType, retryCount, exceptionCount);
//					} else {
//						JSONObject errorJson = new JSONObject(cookie);
//						String resultMessage = errorJson.getString("resultMessage");
//						resultMessage = "?????????????????????????????????" + resultMessage;
//						sendCreateOrderInfo("error", resultMessage, "", childrenUser, "", order_id, "", "", null, "",
//								"", "false", "true", billNo, requestType);
//						return "ERROR:????????????";
//					}
//				}
//				if (StringUtils.isNotEmpty(cookie) && cookie.contains("?????????????????????")) {
//					String errorMsg = "?????????????????????";
//					if (defaultAccount) {
//						errorMsg = "????????????????????????????????????????????????????????????????????????????????????" + errorMsg;
//					}
//					sendCreateOrderInfo("error", errorMsg, "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:?????????????????????";
//				}
//
//			}
//			if (StringUtils.isEmpty(cookie)) {
//				sendCreateOrderInfo("error", "????????????", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:????????????";
//			}
//			loginCookie = cookie;
//			if (defaultAccount) {
//				// ??????????????????????????????????????????????????????
//				if (StringUtils.isNotEmpty(account)) {
//					JSONObject passenger = passengers.getJSONObject(0);
//					String passengerName = passenger.getString("passengerName");
//					// ???????????????????????????????????????
//					if (requestType == 1) {
//						sendAccountInfo("", passengerName, account, order_id, childrenUser, "0", "", billNo);
//					} else {
//						sendAccountInfo("", passengerName, account, "", childrenUser, "0", "", billNo);
//					}
//				}
//			}
//			if (requestType == 1) {
//				return "????????????";
//			}
//			// ????????????????????????
//			sendOrderStatus(childrenUser, order_id, "????????????", requestType);
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
//				sendOrderStatus(childrenUser, order_id, "???????????????????????????", requestType);
//			}
//			if (StringUtils.isEmpty(back)) {
//				sendCreateOrderInfo("error", "??????????????????", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:??????????????????";
//			}
//			/*
//			 * ?????????????????????cookie
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
//			sendOrderStatus(childrenUser, order_id, "??????????????????", requestType);
//			String selectConds = parseResult(back, orderJson);
//
//			if (StringUtils.isEmpty(selectConds)) {
//				logger.info(order_id + "?????????????????????????????????:" + back);
//				if (retryCount < 5) {
//					return StartCreateOrder(orderJson, requestType, ++retryCount, exceptionCount);
//				} else {
//					sendCreateOrderInfo("error", "????????????????????????", "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:????????????????????????";
//				}
//			}
//			sendOrderStatus(childrenUser, order_id, "????????????", requestType);
//			String allCheckToken = "";
//			for (int i = 0; i < 5; i++) {
//				allCheckToken = selectFlights(httpclient, defaultRequestConfig, cookie, selectConds, context);
//				if (StringUtils.isNotEmpty(allCheckToken) && "retry".equals(allCheckToken)) {
//					sendOrderStatus(childrenUser, order_id, "?????????????????????????????????", requestType);
//					continue;
//				}
//				if (StringUtils.isNotEmpty(allCheckToken) && "MUSTLOGIN".equals(allCheckToken) && retryCount < 5) {
//					return StartCreateOrder(orderJson, requestType, ++retryCount, exceptionCount);
//				}
//				if (StringUtils.isNotEmpty(allCheckToken)) {
//					break;
//				}
//				sendOrderStatus(childrenUser, order_id, "?????????????????????????????????", requestType);
//			}
//			if (StringUtils.isEmpty(allCheckToken)) {
//				sendCreateOrderInfo("error", "?????????????????????", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:?????????????????????";
//			}
//			if ("error".equals(allCheckToken)) {
//				sendCreateOrderInfo("error", "??????????????????", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:??????????????????";
//			}
//			sendOrderStatus(childrenUser, order_id, "???????????????", requestType);
//			// ???????????????
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
//					sendCreateOrderInfo("error", "?????????????????????", "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:?????????????????????";
//				}
//			}
//			sendOrderStatus(childrenUser, order_id, "???????????????", requestType);
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
//			sendOrderStatus(childrenUser, order_id, "????????????", requestType);
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
//				sendCreateOrderInfo("error", "????????????", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				return "ERROR:????????????";
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
//					sendCreateOrderInfo("error", "???????????????????????????", "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:???????????????????????????";
//				}
//			}
//			boolean flag = true;
//			if ("noCaptcha".equals(checkResult)) {
//				flag = false;
//			}
//			logger.info(order_id + "?????????cookie:" + cookie);
//			Map<String, String> resultMap = new HashMap<String, String>();
//			sendOrderStatus(childrenUser, order_id, "????????????", requestType);
//			for (int i = 0; i < 5; i++) {
//				try {
//					resultMap = creatOrder(httpclient, defaultRequestConfig, cookie, allCheckToken, context, orderJson,
//							flag);
//				} catch (Exception e) {
//					continue;
//				}
//				if (resultMap == null) {
//					sendCreateOrderInfo("error", "??????????????????", "", childrenUser, "", order_id, "", "", null, "", "",
//							"false", "true", billNo, requestType);
//					return "ERROR:??????????????????";
//				}
//				if (resultMap.get("error") != null) {
//					String errorMsg = resultMap.get("error");
//					if (("????????????".equals(errorMsg)) || errorMsg.contains( "??????????????????" )) {
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
//			String orderNo = resultMap.get("orderNo"); // ???????????????
//			String nextUrl = resultMap.get("nextUrl");
//			if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(nextUrl)) {
//				sendCreateOrderInfo("error", "???????????????????????????????????????????????????????????????", "", childrenUser, "", order_id, "", "", null, "",
//						"", "false", "true", billNo, requestType);
//				return "ERROR:???????????????????????????????????????????????????????????????";
//			}
//			sendCreateOrderInfo("success", "", "", childrenUser, account.split("_")[0] + "--" + orderNo, order_id, "",
//					"", null, "", "", "false", "true", billNo, requestType);
//
//			sendOrderStatus(childrenUser, order_id, "??????????????????", requestType);
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
//				sendOrderStatus(childrenUser, order_id, "???????????????????????????????????????????????????", requestType);
//			}
//			if (resultMap == null) {
//				sendCreateOrderInfo("error", "????????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:????????????????????????";
//			}
//			back = resultMap.get("back");
//			cookie = resultMap.get("cookie");
//			logger.info(order_id + "payInitCookie:" + cookie);
//			if (StringUtils.isEmpty(back) || StringUtils.isEmpty(cookie)) {
//				sendCreateOrderInfo("error", "??????????????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:??????????????????????????????";
//			}
//			try {
//				resultMap = parsePayInfoResult(back);
//			} catch (Exception e) {
//				logger.error("error", e);
//				sendCreateOrderInfo("error", "????????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:????????????????????????";
//			}
//			if (resultMap == null || resultMap.size() == 0) {
//				sendCreateOrderInfo("error", "????????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:????????????????????????";
//			}
//			sendOrderStatus(childrenUser, order_id, "???????????????", requestType);
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
//				sendOrderStatus(childrenUser, order_id, "????????????????????????????????????", requestType);
//			}
//			resultMap = null;
//			if (StringUtils.isEmpty(back)) {
//				sendCreateOrderInfo("error", "?????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:?????????????????????";
//			}
//			try {
//				resultMap = parsePreparePayResult(back);
//			} catch (Exception e) {
//				logger.error("error", e);
//				sendCreateOrderInfo("error", "?????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:?????????????????????";
//			}
//			if (resultMap == null || resultMap.size() == 0) {
//				sendCreateOrderInfo("error", "?????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:?????????????????????";
//			}
//			sendOrderStatus(childrenUser, order_id, "????????????", requestType);
//			for (int i = 0; i < 5; i++) {
//				try {
//					Map<String, String> resultMap1 = receiver(httpclient, defaultRequestConfig, resultMap, cookie);
//					if (resultMap1 == null || resultMap1.size() == 0) {
//						try {
//							resultMap = parsePreparePayResult(back);
//						} catch (Exception e) {
//							logger.error("error", e);
//							sendCreateOrderInfo("error", "?????????????????????", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:?????????????????????";
//						}
//						if (resultMap == null || resultMap.size() == 0) {
//							sendCreateOrderInfo("error", "?????????????????????", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:?????????????????????";
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
//							sendCreateOrderInfo("error", "?????????????????????", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:?????????????????????";
//						}
//						if (resultMap == null || resultMap.size() == 0) {
//							sendCreateOrderInfo("error", "?????????????????????", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:?????????????????????";
//						}
//						continue;
//					}
//					if (back.contains("503 Service Unavailable")) {
//						try {
//							resultMap = parsePreparePayResult(back);
//						} catch (Exception e) {
//							logger.error("error", e);
//							sendCreateOrderInfo("error", "?????????????????????", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:?????????????????????";
//						}
//						if (resultMap == null || resultMap.size() == 0) {
//							sendCreateOrderInfo("error", "?????????????????????", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:?????????????????????";
//						}
//						continue;
//					}
//					resultMap = resultMap1;
//					break;
//				} catch (Exception e) {
//					logger.error("error", e);
//				}
//				sendOrderStatus(childrenUser, order_id, "?????????????????????????????????", requestType);
//			}
//			if (resultMap == null || resultMap.size() == 0) {
//				sendCreateOrderInfo("error", "??????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:??????????????????";
//			}
//			cookie = resultMap.get("cookie");
//			back = resultMap.get("back");
//			if (StringUtils.isEmpty(cookie) || StringUtils.isEmpty(back)) {
//				sendCreateOrderInfo("error", "????????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:????????????????????????";
//			}
//			resultMap = null;
//			try {
//				resultMap = parseReceiverResult(back);
//			} catch (Exception e) {
//				logger.error("error", e);
//				sendCreateOrderInfo("error", "??????????????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:??????????????????????????????";
//			}
//			if (resultMap == null || resultMap.size() == 0) {
//				sendCreateOrderInfo("error", "??????????????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:??????????????????????????????";
//			}
//
//			// ??????????????????
//			// ???????????????
//			String bankId = "";
//			String payType = json.getString("payType") == null ? "" : json.getString("payType");
//			if ("ybhy".equals(payType)) {
//				sendOrderStatus(childrenUser, order_id, "????????????????????????", requestType);
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
//					sendOrderStatus(childrenUser, order_id, "?????????????????????????????????????????????", requestType);
//				}
//				if (resultMap == null || resultMap.size() == 0) {
//					sendCreateOrderInfo("error", "??????????????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//							order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//					return "ERROR:??????????????????????????????";
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
//				sendOrderStatus(childrenUser, order_id, "????????????", requestType);
//				for (int i = 0; i < 2; i++) {
//					bankId = payOrderMU(httpclient, defaultRequestConfig, orderJson, cookie, merId, orderId);
//					if (StringUtils.isNotEmpty(bankId) && bankId.contains("error")) {
//						sendCreateOrderInfo("error", bankId, "", childrenUser, account.split("_")[0] + "--" + orderNo,
//								order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//						return bankId;
//					}
//					if (StringUtils.isNotEmpty(bankId) && bankId.contains("?????????????????????")) {
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
//							sendOrderStatus(childrenUser, order_id, "?????????????????????????????????????????????", requestType);
//						}
//						if (resultMap == null || resultMap.size() == 0) {
//							sendCreateOrderInfo("error", "??????????????????????????????", "", childrenUser,
//									account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false",
//									"true", billNo, requestType);
//							return "ERROR:??????????????????????????????";
//						}
//						cookie = resultMap.get("cookie");
//						back = resultMap.get("back");
//						continue;
//					} else if (StringUtils.isNotEmpty(bankId) && bankId.contains("????????????????????????")) {
//						sendCreateOrderInfo("error", "????????????????????????", "", childrenUser,
//								account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false", "true",
//								billNo, requestType);
//						return "ERROR:????????????????????????";
//					}
//					break;
//				}
//				if (StringUtils.isNotEmpty(bankId) && bankId.contains("?????????????????????")) {
//					sendCreateOrderInfo("error", "??????????????????????????????????????????", "", childrenUser,
//							account.split("_")[0] + "--" + orderNo, order_id, "", "", null, "", "", "false", "true",
//							billNo, requestType);
//					return "ERROR:??????????????????????????????????????????";
//				}
//			} else if ("hf".equals(payType)) {
//				OfficialHfPayService hfPayService = new OfficialHfPayService();
//				String payResult = hfPayService.hfPayMU(orderJson , resultMap);
//				org.dom4j.Document document = DocumentHelper.parseText(payResult);
//				org.dom4j.Element rootElement = document.getRootElement();
//                if (StringUtils.equals("000000", rootElement.elementText("RespCode"))) { //????????????
//                    String money = rootElement.elementText("OrdAmt"); //???????????????
//                    String bankNo = rootElement.elementText("TrxId");  //???????????????
//                    String returnUrl = rootElement.elementText("RetUrl");  //??????????????????url
//                    resultMap.put("money",money);
//                    resultMap.put("bankNo",bankNo);
//                    resultMap.put("returnUrl",returnUrl);
//
//                    //????????????
//                    if (resultMap == null || resultMap.size() == 0) {
//                        return null;
//                    }
//                    returnUrl = resultMap.get("returnUrl");
//                    logger.info(order_id + "????????????????????????:" + returnUrl);
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
//                        logger.info(order_id + "?????????????????????" + back);
//                        bankId = "success";
//                    }
//                } else {
//                    String errorMsg = rootElement.elementText("ErrorMsg");
//                    sendCreateOrderInfo("error", errorMsg, "", childrenUser, account.split("_")[0] + "--" + orderNo,
//    						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//    				return "ERROR:"+errorMsg;
//                }
//			} else {
//				sendCreateOrderInfo("error", "????????????????????????", "", childrenUser, account.split("_")[0] + "--" + orderNo,
//						order_id, "", "", null, "", "", "false", "true", billNo, requestType);
//				return "ERROR:????????????????????????";
//			}
//
//			if ("xyk".equals(payType)) {
//				payType = "?????????";
//			} else if ("ybhy".equals(payType)) {
//				payType = "????????????";
//			} else if ("hf".equals(payType)) {
//				payType = "??????";
//			} else {
//				payType = "??????";
//			}
//			if (StringUtils.isEmpty(bankId)) {
//				sendCreateOrderInfo("error", "??????????????????????????????????????????????????????", "", childrenUser,
//						account.split("_")[0] + "--" + orderNo, order_id, "", "", null, payType, "", "false", "true",
//						billNo, requestType);
//				return "ERROR:??????????????????????????????????????????????????????";
//			}
//
//			// ??????
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
//					if (map.get("error") != null && "????????????????????????".equals(map.get("error"))) {
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
//					sendOrderStatus(childrenUser, order_id, "??????????????????", requestType);
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
//						sendOrderStatus(childrenUser, order_id, "????????????", requestType);
//						// ??????????????????????????????????????????????????????,??????????????????
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
//				return "error:??????????????????";
//			}
//		} catch (Exception e) {
//			logger.error("error", e);
//			sendCreateOrderInfo("error", "????????????", "", childrenUser, "", order_id, "", "", null, "", "", "false", "true",
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
//			logger.info("????????????????????????:" + back);
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
//			logger.info("??????????????????????????????:" + phoneNumber + "???token:" + token);
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
//			// logger.info("????????????");
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
//			// ???????????????????????????????????????
//			if (StringUtils.isEmpty(back)) {
//				return "noCaptcha";
//			}
//			logger.info("startCaptcha:" + back);
//			JSONObject json = new JSONObject(back);
//			String challenge = json.getString("challenge");
//			String gt = json.getString("gt");
//			String gestResult = GeetestRecognition.recognition(gt,
//					"https://passport.ceair.com/cesso/geet!geetInit.shtml", challenge, otheraccount.split("_")[1]);
//			logger.info("???????????????????????????:" + gestResult);
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
//			logger.info("?????????????????????" + nameValue.toString());
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
//			CryptoTools des = new CryptoTools(contentKey);// ???????????????
//			// logger.info("?????????????????????"+test);
//			back = des.decode(back);
//			logger.info(orderNo + "?????????????????????" + back);
//			if (StringUtils.isNotEmpty(back) && back.contains("????????????????????????")) {
//				mapResult.put("error", "????????????????????????");
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
//			// //??????????????? ?????????????????????
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
//			logger.info("????????????????????????????????????" + text);
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
//			logger.info("????????????????????????????????????" + text);
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
//			nameValuePairs.add(new BasicNameValuePair("creditCardInfo.cvv", cvv));// ????????????
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
//			logger.info("????????????????????????????????????" + text);
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
//			nameValuePairs.add(new BasicNameValuePair("creditCardInfo.cvv", cvv));// ????????????
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
//			logger.info("????????????????????????????????????" + text);
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
//				logger.info("??????????????????:" + text);
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
//			// ????????????cookie????????????????????????Set-Cookie
//			post.setHeader("Cookie", "");
//			response = httpclient.execute(target, post);
//
//			String locationValue = "";
//			Header[] location = response.getHeaders("Location");
//			// ??????????????????????????????
//			for (int i = 0; i < location.length; i++) {
//				locationValue = location[i].getValue();
//				logger.info(order_id + "node?????????1:" + locationValue);
//			}
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//			if (StringUtils.isEmpty(locationValue)) {
//				resultMap.put("error", "?????????1??????");
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
//			// ??????????????????????????????
//			for (int i = 0; i < location.length; i++) {
//				locationValue = location[i].getValue();
//				logger.info(order_id + "node?????????2:" + locationValue);
//			}
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//				}
//			}
//			if (StringUtils.isEmpty(locationValue)) {
//				resultMap.put("error", "?????????2??????");
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
//			logger.info("??????????????????:" + resultMap.toString());
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
//			logger.info("??????????????????:" + resultMap.toString());
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
//			String bankname = "UNI_YEEPAY_002"; // ????????????
//			String broker_ecard_bin = doc.getElementById("broker_ecard_bin").val();
//			String card_length_err = doc.getElementById("card_length_err").val();
//			String card_no = ""; // ?????????
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
//			String id_type = "0"; // 0????????? 1?????? 2????????? 3????????? 4??????????????? 6????????? 7?????? 12??????????????????
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
//			logger.info("?????????cookie:" + cookie);
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
//			if (StringUtils.isNotEmpty(back) && back.contains("????????????")) {
//				resultMap.put("error", "????????????");
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
//			logger.info(order_id +"??????????????????:" + back);
//			if (StringUtils.isNotEmpty(back) && back.contains("??????????????????")) {
//				resultMap.put("error", "????????????????????????????????????");
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
//				resultMap.put("error", "????????????");
//				return resultMap;
//			}
//			if (StringUtils.isNotEmpty( airResultCode ) && airResultCode.contains( "BK20000" )) {
//				resultMap.put( "error" , "???????????????????????????????????? BK20000" );
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
//					if ("???".equals(gender)) {
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
//				if ("?????????".equals(idType)) {
//					idType = "NI"; // ?????????
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
//				if ("??????".equals(paxType)) {
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
//			logger.info("???????????????" + nameValue.toString());
//			cookie = cookie.replaceAll("Path=/;", "").replaceAll("HttpOnly;", "").replaceAll("Domain=.ceair.com;", "");
//			logger.info("cookie?????????" + cookie);
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
//				String cabinCode = cabin.getString("cabinCode"); // ??????
//				String cabinStatus = cabin.getString("cabinStatus"); // ?????????
//				String member = "";
//				try {
//					member = searchProduct.getString("member"); // ??????????????????
//				} catch (Exception e) {
//				}
//				String productName = searchProduct.getString("productName"); // ????????????
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
//			if (StringUtils.isNotEmpty( back ) && back.contains( "??????????????????" ) && retry < 5) {
//				logger.info( "?????????ip??????????????????" );
//				//?????????ip
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
//			String jsFileName = "C:\\???????????????\\donghang.js";
//			FileReader reader = new FileReader(jsFileName); // ??????????????????
//			engine.eval(reader);
//			if (engine instanceof Invocable) {
//				Invocable invoke = (Invocable) engine;
//				token = (String) invoke.invokeFunction("c", "donghang");
//				logger.info(order_Id + "?????????????????????:" + token);
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
//			logger.info(order_Id + "?????????????????????:" + result);
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
//			logger.info(order_Id + "check??????:" + result);
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
//			logger.info(order_Id + "??????????????????:" + gestResult);
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
//			logger.info(order_Id + "????????????:" + result);
//
//			headersArr = response.getAllHeaders();
//			newCookie = "";
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equals(header.getName())) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie = cookie + newCookie;
//			logger.info(order_Id + "???????????????cookie:" + cookie);
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
//			logger.info("????????????????????????????????????" + validtext);
//			org.dom4j.Document document = DocumentHelper.parseText(validtext);
//			org.dom4j.Element root = document.getRootElement();
//			String error = root.elementText("Error");
//			String result = "";
//			if (error != null && !"".equals(error)) {
//				result = error;
//				if (result.contains("????????????")) {
//					return "?????????????????????";
//				} else if (result.contains("????????????")) {
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
//			if (StringUtils.isNotEmpty(back) && back.contains("??????????????????")) {
//				return back;
//			}
//			if (StringUtils.isNotEmpty(back) && back.contains("???????????????")) {
//				return back;
//			}
//			if (StringUtils.isNotEmpty(back) && back.contains("??????????????????")) {
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
//			logger.info("?????????cookie:" + cookie);
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
//		// ????????? ??????
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
//				 * ???????????????????????????????????????
//				 */
//				boolean ifContinue = valid(defaultRequestConfig, httpclient, idCard);
//				if (!ifContinue) {
//					if (requestType == 1) {
//						sendAccountInfo("", passengerName, "", order_id, childrenUser, "1", "?????????????????????", billNo);
//					} else {
//						sendAccountInfo("", passengerName, "", "", childrenUser, "1", "?????????????????????", billNo);
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
//				String queryPassword = "12345678"; // ?????? ????????????
//				resultPassword = queryPassword;
//				// Map<String, String> phoneMap = YiMa.getPhoneNumber();
//				Map<String, String> phoneMap = null;
//				phoneNumber = phoneMap.get("phoneNumber");
//				logger.info("??????????????????????????????:" + phoneNumber);
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
//				String jsFileName = "C:\\???????????????\\plugins.min.js";
//				FileReader reader = new FileReader(jsFileName); // ??????????????????
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
//						+ "\",\"mobileSendWayOne\":true,\"emailSendWayOne\":true,\"homeCityCode\":\"??????\",\"homeAdress\"\r\n"
//						+ ":\"????????????\",\"locale\":\"zh-CN\"}";
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
//				logger.info(orderId + "???????????????" + back);
//				JSONObject resultObj = new JSONObject(back);
//				long id = resultObj.getLong("id");
//				logger.info("id:" + id);
//
//				if (id == 0) {
//					response = httpclient.execute(target, post);
//					back = EntityUtils.toString(response.getEntity(), "utf-8");
//					logger.info("?????????" + back);
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
//				logger.info(orderId + ",????????????????????????:" + smsInfo);
//				code = smsInfo.split("_")[1];
//				post = new HttpPost("/mpf/register/registerMember?locale=cn");
//				json = "{\"cardType\":\"IDCRD\",\"nationality\":\"CN\",\"contactLanguage\":\"ZH\",\"billSendWay\":\"EML\",\"homeCountryCode\":\"CN\"\r\n"
//						+ ",\"homeProvinceCode\":\"GD\",\"firstCnName\":\"" + firstCnName + "\",\"firstEnName\":\""
//						+ firstEnName + "\",\"lastCnName\":\"" + lastCnName + "\",\"lastEnName\":\"" + lastEnName
//						+ "\"\r\n" + ",\"birthday\":\"" + birthday
//						+ "\",\"sex\":\"M\",\"adultType\":2,\"phoneNumber\"\r\n" + ":\"" + phoneNumber
//						+ "\",\"email\":\"" + email + "\"\r\n"
//						+ ",\"mobileSendWayOne\":true,\"emailSendWayOne\":true,\"homeCityCode\":\"??????\",\"homeAdress\":\"????????????\",\"locale\":\"zh-CN\"\r\n"
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
//				logger.info(orderId + "???????????????" + back);
//				String accountInfo = resultIdCard + "_" + resultPassword;
//				resultMap.put("accountInfo", accountInfo);
//				resultMap.put("phone", phone);
//				resultMap.put("passengerName", passengerName);
//				return resultMap;
//			} catch (Exception e) {
//				index++;
//				if (requestType == 1) {
//					sendAccountInfo("", passengerName, "", order_id, childrenUser, "1", "????????????", billNo);
//				} else {
//					sendAccountInfo("", passengerName, "", "", childrenUser, "1", "????????????", billNo);
//				}
//				logger.error("error", e);
//				continue;
//				// if (e.getCause() == null) {
//				// logger.error("error", e);
//				// }
//				// if (e.getCause().toString().contains("BOFid.card.existEOF"))
//				// {
//				// logger.info("??????????????????");
//				// index++;
//				// } else if
//				// (e.getCause().toString().contains("BOFcaptcha.overTimeEOF"))
//				// {
//				// logger.info("???????????????");
//				// } else if
//				// (e.getCause().toString().contains("BOFcommunicateRecord.notExistEOF"))
//				// {
//				// logger.info("???????????????");
//				// } else if
//				// (e.getCause().toString().contains("BOFcaptcha.wrongEOF")) {
//				// logger.info("???????????????");
//				// } else if
//				// (e.getCause().toString().contains("BOFemail.existEOF")) {
//				// logger.info("???????????????");
//				// index++;
//				// } else if
//				// (e.getCause().toString().contains("BOFcaptcha.hasVerifiedEOF"))
//				// {
//				// logger.info("????????????");
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
//	 * ?????????????????? String result = request.getParameter("result"); //?????????????????? String
//	 * message = request.getParameter("message"); //???????????? String price =
//	 * request.getParameter("price"); //??????????????? String childrenUser =
//	 * request.getParameter("childrenUser");//????????? String newOrderId =
//	 * request.getParameter("newOrderId"); //??????????????????????????????????????? String orderId =
//	 * request.getParameter("orderId"); //???????????????ID String isPassuccess =
//	 * request.getParameter("isPassuccess"); //?????????????????? String isPassenge =
//	 * request.getParameter("isPassenge"); //?????????????????? String passengeMessage =
//	 * request.getParameterValues("passengeMessage"); // ???????????????????????????
//	 * ?????????:??????A##?????????##??????@_@??????B##?????????##?????? String payTransactionid =
//	 * request.getParameter("payTransactionid"); //?????????????????????????????? SC????????????????????? String
//	 * payStatus = request.getParameter("payStatus"); //?????????????????? String isSuccess
//	 * = request.getParameter("isSuccess"); //???????????? String isautoB2C =
//	 * request.getParameter("isautoB2C"); //?????????????????? String ifUsedCoupon =
//	 * request.getParameter("ifUsedCoupon"); //??????????????????
//	 */
//	/*
//	 * payStatus(??????????????????????????????)
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
//	 * ?????????????????? String childrenUser = request.getParameter("childrenUser");//?????????
//	 * String orderId = request.getParameter("orderId"); //???????????????ID String
//	 * payStatus = request.getParameter("payStatus"); //??????????????????
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
//			logger.error("??????\"" + status + "\"????????????");
//		}
//		return null;
//	}
//
//	/**
//	 * ?????????????????? String phone = request.getParameter("phone");//??????????????? String name =
//	 * request.getParameter("name"); //?????????????????? accountInfo =
//	 * request.getParameter("accountInfo"); //????????????????????? ??????????????????_??????
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
//			logger.error("??????\"" + accountInfo + "\"????????????");
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
