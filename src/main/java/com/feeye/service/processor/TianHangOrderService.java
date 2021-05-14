package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feeye.util.MD5Util;
import com.feeye.util.PropertiesUtils;
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
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class TianHangOrderService {
	private static final Logger logger = Logger.getLogger(TianHangOrderService.class);
	private static final int timeout = 60000;
	private static Map<String, String> tokenMap = new HashMap<String, String>();
	String dlyAccount = PropertiesUtils.getPropertiesValue("config", "dlyAccount");
	
	public static void main(String[] args) throws Exception {
		String back = "{\n" +
				"  \"account\": \"18384782738_860086\",\n" +
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
				"      \"airline\": \"HU\",\n" +
				"      \"arrival\": \"PEK\",\n" +
				"      \"cabin\": \"R\",\n" +
				"      \"departure\": \"SZX\",\n" +
				"      \"departureDate\": \"2020-06-24\",\n" +
				"      \"departureTime\": \"11:40:00+08:00\",\n" +
				"      \"fType\": \"go\",\n" +
				"      \"flightNo\": \"HU7740\",\n" +
				"      \"price\": \"480\"\n" +
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
				"      \"birthday\": \"1995-08-13\",\n" +
				"      \"id\": \"62209018\",\n" +
				"      \"idcard\": \"652201199810022721\",\n" +
				"      \"passengerCardType\": \"身份证\",\n" +
				"      \"passengerName\": \"蒋志辉\",\n" +
				"      \"passengerSex\": \"男\",\n" +
				"      \"passengerType\": \"成人\",\n" +
				"      \"mobile\": \"13037284859\"\n" +
				"    }\n" +
				"  ],\n" +
				"  \"payType\": \"yfk\",\n" +
				"  \"platType\": \"xiecheng\",\n" +
				"  \"qiangpiao\": \"2\",\n" +
				"  \"username\": \"shxtly\",\n" +
				"  \"ytype\": {\n" +
				"    \"flightEntity\": {\n" +
				"      \"date\": \"2020-06-24\",\n" +
				"      \"orgCity\": \"SZX\",\n" +
				"      \"dstCity\": \"PEK\",\n" +
				"      \"orgCityCode\": \"SZX\",\n" +
				"      \"arrCityCode\": \"BJS\",\n" +
				"      \"distance\": 2077,\n" +
				"      \"basePrice\": 0,\n" +
				"      \"codeShare\": false,\n" +
				"      \"shareNum\": \"\",\n" +
				"      \"shareAirlineShortName\": null,\n" +
				"      \"flightNo\": \"7710\",\n" +
				"      \"depTime\": \"07:25\",\n" +
				"      \"depModifyTime\": \"\",\n" +
				"      \"arriTime\": \"10:35\",\n" +
				"      \"arriModifyTime\": \"\",\n" +
				"      \"meal\": \"\",\n" +
				"      \"planeType\": \"空客338\",\n" +
				"      \"planeSize\": \"大\",\n" +
				"      \"planeType2\": \"338\",\n" +
				"      \"stopnum\": 0,\n" +
				"      \"stopCity\": null,\n" +
				"      \"orgJetquay\": \"\",\n" +
				"      \"dstJetquay\": \"T2\",\n" +
				"      \"dstDate\": \"2020-06-24\",\n" +
				"      \"orgCityName\": \"深圳\",\n" +
				"      \"dstCityName\": \"北京\",\n" +
				"      \"orgAirportShortName\": \"宝安国际机场\",\n" +
				"      \"dstAirportShortName\": \"首都国际机场\",\n" +
				"      \"orgAirportName\": \"深圳宝安\",\n" +
				"      \"dstAirportName\": \"北京首都\",\n" +
				"      \"airlineShortName\": \"海南航空\",\n" +
				"      \"airlineName\": \"海南航空股份有限公司\",\n" +
				"      \"airlineCode\": \"HU\",\n" +
				"      \"mealCode\": \"\",\n" +
				"      \"tv\": \"\",\n" +
				"      \"week\": \"WED\",\n" +
				"      \"officeId\": \"SZX123\",\n" +
				"      \"timeDifference\": \"3小时10分\",\n" +
				"      \"data\": null,\n" +
				"      \"depTimestamp\": 1592954700000,\n" +
				"      \"arrTimestamp\": 1592966100000\n" +
				"    },\n" +
				"    \"seatEntity\": {\n" +
				"      \"seatCode\": \"T\",\n" +
				"      \"subClass\": \"T\",\n" +
				"      \"seatStatus\": \"A\",\n" +
				"      \"discount\": 19,\n" +
				"      \"seatMsg\": \"特价经济舱\",\n" +
				"      \"parPrice\": 480,\n" +
				"      \"seatType\": \"0\",\n" +
				"      \"settlePrice\": 480,\n" +
				"      \"seatLevel\": \"3\",\n" +
				"      \"cancelRuleList\": [\n" +
				"        {\n" +
				"          \"time\": \"未获取到退票规则，请以航司最新规定为准\",\n" +
				"          \"fee\": null\n" +
				"        }\n" +
				"      ],\n" +
				"      \"changeRule\": \"不得签转\",\n" +
				"      \"rebookRuleList\": [\n" +
				"        {\n" +
				"          \"time\": \"未获取到改期规则，请以航司最新规定为准\",\n" +
				"          \"fee\": null\n" +
				"        }\n" +
				"      ],\n" +
				"      \"baggageRule\": \"免费托运行李额20KG (仅供参考，以实际出票后订单为准)\",\n" +
				"      \"memberPrice\": 480,\n" +
				"      \"fareBasis\": \"Y19STJ\",\n" +
				"      \"adtAgencyFare\": 5,\n" +
				"      \"childCabin\": \"T\",\n" +
				"      \"childCabinPrice\": 960,\n" +
				"      \"childCabinName\": \"\",\n" +
				"      \"infatCabin\": null,\n" +
				"      \"infatCabinPrice\": 0,\n" +
				"      \"accountFareTag\": null,\n" +
				"      \"adtTaxeCn\": 50,\n" +
				"      \"adtTaxeYq\": 0,\n" +
				"      \"chdTaxeCn\": 0,\n" +
				"      \"chdTaxeYq\": 0,\n" +
				"      \"source\": \"SZX123\",\n" +
				"      \"group\": null,\n" +
				"      \"data\": null,\n" +
				"      \"tags\": null,\n" +
				"      \"airlineProductDesc\": null,\n" +
				"      \"reschedualFeeAdt\": null,\n" +
				"      \"diffPriceAdt\": null,\n" +
				"      \"reschedualPayAmountAdt\": null,\n" +
				"      \"reschedualChd\": null,\n" +
				"      \"diffPriceChd\": null,\n" +
				"      \"reschedualPayAmountChd\": null,\n" +
				"      \"chdServiceFee\": 0,\n" +
				"      \"chdChangeServiceFee\": 0,\n" +
				"      \"serviceFee\": 0,\n" +
				"      \"changeServiceFee\": 0,\n" +
				"      \"discountServiceFee\": 0,\n" +
				"      \"discountChangeServiceFee\": 0,\n" +
				"      \"rulePromotion\": null,\n" +
				"      \"addNoteTitle\": null,\n" +
				"      \"addNoteContent\": null,\n" +
				"      \"canUse\": 1,\n" +
				"      \"canUseStr\": null,\n" +
				"      \"publicCanUse\": 0,\n" +
				"      \"overproofType\": 0,\n" +
				"      \"rank\": 0\n" +
				"    }\n" +
				"  }\n" +
				"}";
		new TianHangOrderService().StartCreateOrder(back, 0, 0, 0);
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
//				.setProxy(dailiyunProxy)
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

		// String cookie = "";
		String mainUser = "";
		String childrenUser = "";
		String order_id = "";
		String billNo = "";
		try {
			// JSONObject json = new JSONObject(orderJson);
			JSONObject json = JSON.parseObject(orderJson);
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
			String key = account.split("_")[0];
			String password = account.split("_")[1];
			String price = "";
			String ticketPrice = "";
			try {
				billNo = json.getString("billNo");
			} catch (Exception e) {
			}
			if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
				//PushOrderInfoUtil.sendOrderStatus( "tianhang" , mainUser , childrenUser , order_id , "已取消出票" );
				sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + "已取消出票");
				return "取消出票";
			}
			String token = tokenMap.get(key);
			// PushOrderInfoUtil.sendOrderStatus( "tianhang" , mainUser , childrenUser , order_id , "开始登陆" );
			int i = 0;
			if (StringUtils.isEmpty(token)) {
				while (StringUtils.isEmpty(token) && i < 3) {
					token = getToken(key,password,httpclient,order_id);
					if (StringUtils.isNotEmpty(token)) {
						tokenMap.put(key,token);
						break;
					}
					Thread.sleep(3000);
				}
			}
			if (StringUtils.isEmpty(token)) {
				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
					//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
							"true", billNo, requestType, ticketPrice);
					logger.info(order_id + "已取消出票");
					return "取消出票";
				}
				if (i < 3) {
					loginRetryCount++;
					return StartCreateOrder(orderJson, retryCount, loginRetryCount, requestType);
				} else {
					sendCreateOrderInfo("error", "登录失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",
							"true", billNo, requestType, ticketPrice);
					logger.info(order_id + "登录失败");
					return "error:登录失败";
				}
			}
			if (token.contains("error") || token.contains("ERROR") || token.contains("任务超时")) {
				sendCreateOrderInfo("error", token, "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + token);
				return "error:" + token;
			}
			if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
				//PushOrderInfoUtil.sendOrderStatus( "tianhang" , mainUser , childrenUser , order_id , "已取消出票" );
				sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + "已取消出票");
				return "取消出票";
			}
			//PushOrderInfoUtil.sendOrderStatus( "tianhang" , mainUser , childrenUser , order_id , "下单验价" );
			String flightMsg = json.getString("ytype");
			if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
				//PushOrderInfoUtil.sendOrderStatus( "y8b2b" , mainUser , childrenUser , order_id , "已取消出票" );
				sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + "已取消出票");
				return "取消出票";
			}
			String back = airPata(httpclient,order_id,token,flightMsg);
			int count = 0;
			while (StringUtils.isEmpty(back) && count < 3) {
				Thread.sleep(4000);
				back = airPata(httpclient,order_id,token,flightMsg);
				if (StringUtils.isNotEmpty(back)) {
					break;
				}
				count++;
			}
			if (StringUtils.isEmpty(back) && count == 3) {
				sendCreateOrderInfo("error", "下单验价没有信息", "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + "下单验价没有信息");
				return "error:" + "下单验价没有信息";
			}
			JSONObject priceObj = JSON.parseObject(back);
			String code = priceObj.getString("ret");
			if ("401".equals(code)) {
				token = getToken(key,password,httpclient,order_id);
				count = 0;
				while (StringUtils.isEmpty(token) && count < 3) {
					Thread.sleep(3000);
					token = getToken(key,password,httpclient,order_id);
					if (StringUtils.isNotEmpty(token)) {
						tokenMap.put(key,token);
						break;
					}
					count++;
				}
				back = airPata(httpclient,order_id,token,flightMsg);
				count = 0;
				while (StringUtils.isEmpty(back) && count < 3) {
					Thread.sleep(4000);
					back = airPata(httpclient,order_id,token,flightMsg);
					if (StringUtils.isNotEmpty(back)) {
						break;
					}
					count++;
				}
			}
			priceObj = JSON.parseObject(back);
			code = priceObj.getString("ret");
			if ("0".equals(code)) {
				price = json.getJSONArray("flights").getJSONObject(0).getString("price");
				JSONObject dataObj = priceObj.getJSONArray("data").getJSONObject(0);
				String orderPrice = dataObj.getString("settlePrice");
				if (Float.valueOf(price).floatValue() <= Float.valueOf(orderPrice).floatValue()) {
					List<Map<String,Object>> psgList = new ArrayList<>();
					for (int j = 0; j < passengers.size(); j++) {
						Map<String,Object> psgMap = new HashMap<>();
						JSONObject pass = passengers.getJSONObject(i);
						psgMap.put("noUseReason","");
						psgMap.put("canUse",passengers.size());
						psgMap.put("tripLevel",new JSONObject());
						psgMap.put("employeeId",0);
						psgMap.put("psgName",pass.getString("passengerName"));
						psgMap.put("certType","1");
						psgMap.put("psgType","ADT");
						String certNo = pass.getString("idcard");
						psgMap.put("certNo",certNo);
						char sex = certNo.charAt(16);
						if ((Integer.valueOf(sex) % 2) == 0) {
							psgMap.put("sex","2");
						} else {
							psgMap.put("sex","1");
						}

						psgMap.put("englishfirstname","");
						psgMap.put("englishlastname","");
						psgMap.put("bornDate",pass.getString("birthday"));
						psgMap.put("psgMobile",pass.getString("mobile"));
						psgMap.put("chineseName",pass.getString("passengerName"));
						psgMap.put("language","中");
						psgMap.put("beneficiaryDetailList",new ArrayList<>());
						psgMap.put("identifyId",getRandomString(8));
						psgList.add(psgMap);
					}
					String psgData = JSON.toJSONString(psgList);
					JSONObject flightObj = JSON.parseObject(flightMsg);
					JSONObject filghtEntityObj = flightObj.getJSONObject("flightEntity");
					String dept = filghtEntityObj.getString("orgCity");
					String arriv = filghtEntityObj.getString("dstCity");
					// PushOrderInfoUtil.sendOrderStatus( "tianhang" , mainUser , childrenUser , order_id , "开始下单" );
					String bookBack = bookOrder(httpclient,order_id,token,flightMsg,psgData,dept,arriv);
					count = 0;
					while (StringUtils.isEmpty(bookBack) && count < 3) {
						Thread.sleep(4000);
						bookBack  = bookOrder(httpclient,order_id,token,flightMsg,psgData,dept,arriv);
						if (StringUtils.isNotEmpty(bookBack)) {
							break;
						}
						count++;
					}
					if (StringUtils.isEmpty(bookBack) && count == 3) {
						sendCreateOrderInfo("error", "下单失败" + orderPrice, "", childrenUser, "", order_id, "", "", null, "", "", "false",
								"true", billNo, requestType, ticketPrice);
						logger.info(order_id + "下单失败" + orderPrice);
						return "error:" + "下单失败";
					}  else {
						JSONObject bookObj = JSON.parseObject(bookBack);
						code = bookObj.getString("ret");
						if ("401".equals(code)) {
							token = getToken(key,password,httpclient,order_id);
							count = 0;
							while (StringUtils.isEmpty(token) && count < 3) {
								Thread.sleep(3000);
								token = getToken(key,password,httpclient,order_id);
								if (StringUtils.isNotEmpty(token)) {
									tokenMap.put(key,token);
									break;
								}
								count++;
							}
							bookBack = bookOrder(httpclient,order_id,token,flightMsg,psgData,dept,arriv);
							count = 0;
							while (StringUtils.isEmpty(bookBack) && count < 3) {
								Thread.sleep(4000);
								bookBack  = bookOrder(httpclient,order_id,token,flightMsg,psgData,dept,arriv);
								if (StringUtils.isNotEmpty(bookBack)) {
									break;
								}
								count++;
							}
						}
						bookObj = JSON.parseObject(bookBack);
						code = bookObj.getString("ret");
						if ("0".equals(code)) {
							String paywd = json.getString("cvv");
							String accountMsg = key + "##" + password;
							JSONObject dtOrder = bookObj.getJSONObject("data").getJSONObject("dtOrder");
							String totalSalePrice = dtOrder.getString("payprice");
							String id = dtOrder.getString("id");
							String orderNo = dtOrder.getString("orderNo");
							// PushOrderInfoUtil.sendOrderStatus( "tianhang" , mainUser , childrenUser , order_id , "开始支付" );
							back = payOrder(httpclient,order_id,token,accountMsg,paywd,totalSalePrice,orderNo);
							while (StringUtils.isEmpty(back) && count < 3) {
								Thread.sleep(4000);
								back = payOrder(httpclient,order_id,token,accountMsg,paywd,totalSalePrice,orderNo);
								if (StringUtils.isNotEmpty(back)) {
									break;
								}
								count++;
							}
							if (StringUtils.isEmpty(back) && count == 3) {
								sendCreateOrderInfo("error", "支付失败", "", childrenUser, "", order_id, "", "", null, "false", "", "false",
										"true", billNo, requestType, ticketPrice);
								logger.info(order_id + "支付失败");
								return "error:" + "支付失败";
							} else {
								JSONObject backObj = JSON.parseObject(back);
								code = backObj.getString("ret");
								if ("0".equals(code)) {
									// PushOrderInfoUtil.sendOrderStatus( "tianhang" , mainUser , childrenUser , order_id , "支付成功，查询票号" );
									sendCreateOrderInfo("error", "支付成功", "", childrenUser, "", order_id, "true", "", null, "false", "", "false",
											"true", billNo, requestType, ticketPrice);
									Thread.sleep(30000);
									id = "988069";
									back = queryOrder(httpclient,order_id,token,accountMsg,id);
									count = 0;
									Boolean breakFlag = false;
									while (count < 6) {
										StringBuffer sbu = new StringBuffer();
										if(StringUtils.isNotEmpty(back)) {
											backObj = JSON.parseObject(back);
											code = backObj.getString("ret");
											if ("0".equals(code)) {
												JSONObject ordObj = backObj.getJSONObject("data").getJSONObject("ord");
												JSONArray psgMsgArray = ordObj.getJSONArray("psgFlightList");
												for (int k = 0; k < psgMsgArray.size(); k++) {
													JSONObject psgMsg = psgMsgArray.getJSONObject(i);
													String certNo = psgMsg.getString("certNo");
													for (int j = 0; j < passengers.size(); j++) {
														JSONObject pass = passengers.getJSONObject(i);
														String dbCerNo = pass.getString("idcard");
														if (dbCerNo.equalsIgnoreCase(certNo)) {
															String tktNo = psgMsg.getString("tktNo");
															String psgName = psgMsg.getString("psgName");
															String pnrno = psgMsg.getString("pnrno");
															sbu.append(psgName).append("##").append(certNo).append("##").append(tktNo).append("##")
																	.append(orderNo).append("##").append(pnrno).append("#_#");
															logger.info("乘客："+psgName+"-"+certNo+",票号："+tktNo);
															breakFlag = true;
															break;
														}
													}
												}
											}
										}
										if (breakFlag) {
											// PushOrderInfoUtil.sendOrderStatus( "tianhang" , mainUser , childrenUser , order_id , "票号回填" );
											back = sbu.toString() + orderNo;
											String ticketnos[] = back.split("@_@");
											sendCreateOrderInfo("success", "", price, childrenUser,orderNo , order_id, "true", "true",
													ticketnos[0], "天航商旅", ticketnos[1], "false", "true", billNo, requestType,
													ticketPrice);
											return "SUCCESS";
										} else {
											Thread.sleep(30000);
											back = queryOrder(httpclient,id,order_id,accountMsg,id);
											count++;
										}
									}
									if (count == 6 && !breakFlag) {
										sendCreateOrderInfo("error", "查询票号失败", "", childrenUser, "", order_id, "true", "", null, "false", "", "false",
												"true", billNo, requestType, ticketPrice);
										logger.info(order_id + "查询票号失败");
										return "error:" + "查询票号失败";
									}
								} else {
									sendCreateOrderInfo("error", "支付失败", "", childrenUser, "", order_id, "", "", null, "false", "", "false",
											"true", billNo, requestType, ticketPrice);
									logger.info(order_id + "支付失败");
									return "error:" + "支付失败";
								}
							}
							/*JSONObject dtOrderObj = bookObj.getJSONObject("data");
							String id = dtOrderObj.getJSONObject("dtOrder").getString("id");
							String accountMsg = key + "##" + password;
							back = ticketOrder(httpclient,order_id,token,id,accountMsg);
							count = 0;
							while (StringUtils.isEmpty(back) && count < 3) {
								Thread.sleep(4000);
								back = ticketOrder(httpclient,order_id,token,id,accountMsg);
								if (StringUtils.isNotEmpty(back)) {
									break;
								}
								count++;
							}
							JSONObject backObj = JSON.parseObject(back);
							code = backObj.getString("ret");
							if ("0".equals(code)) {
								JSONObject ordObj = backObj.getJSONObject("data").getJSONObject("ord");
								String orderNo = ordObj.getString("orderNo");
								String totalSalePrice = ordObj.getString("totalSalePrice");
								// PushOrderInfoUtil.sendOrderStatus( "tianhang" , mainUser , childrenUser , order_id , "开始创单" );
								back = preCreate(httpclient,order_id,token,accountMsg,orderNo,",",totalSalePrice);
								count = 0;
								while (StringUtils.isEmpty(back) && count < 3) {
									Thread.sleep(3000);
									back = preCreate(httpclient,order_id,token,accountMsg,orderNo,"",totalSalePrice);
									if (StringUtils.isNotEmpty(back)) {
										break;
									}
									count++;
								}
								if (StringUtils.isEmpty(back) && count == 3) {
									sendCreateOrderInfo("error", "创单失败", "", childrenUser, "", order_id, "", "", null, "false", "", "false",
											"true", billNo, requestType, ticketPrice);
									logger.info(order_id + "创单失败");
									return "error:" + "创单失败";
								} else {
									backObj = JSON.parseObject(back);
									code = backObj.getString("ret");
									if ("0".equals(code)) {
										String paywd = json.getString("cvv");
										back = payOrder(httpclient,order_id,token,accountMsg,paywd,totalSalePrice);
										while (StringUtils.isEmpty(back) && count < 3) {
											Thread.sleep(4000);
											back = ticketOrder(httpclient,order_id,token,id,accountMsg);
											if (StringUtils.isNotEmpty(back)) {
												break;
											}
											count++;
										}
										if (StringUtils.isEmpty(back) && count == 3) {
											sendCreateOrderInfo("error", "支付失败", "", childrenUser, "", order_id, "", "", null, "false", "", "false",
													"true", billNo, requestType, ticketPrice);
											logger.info(order_id + "支付失败");
											return "error:" + "支付失败";
										} else {
											backObj = JSON.parseObject(back);
											code = backObj.getString("ret");
											if ("0".equals(code)) {
												sendCreateOrderInfo("error", "支付成功", "", childrenUser, "", order_id, "true", "", null, "false", "", "false",
														"true", billNo, requestType, ticketPrice);
											} else {
												sendCreateOrderInfo("error", "支付失败", "", childrenUser, "", order_id, "", "", null, "false", "", "false",
														"true", billNo, requestType, ticketPrice);
												logger.info(order_id + "支付失败");
												return "error:" + "支付失败";
											}
										}
									} else {
										sendCreateOrderInfo("error", "创单失败", "", childrenUser, "", order_id, "", "", null, "false", "", "false",
												"true", billNo, requestType, ticketPrice);
										logger.info(order_id + "创单失败");
										return "error:" + "创单失败";
									}
								}
							} else {
								sendCreateOrderInfo("error", "支付失败", "", childrenUser, "", order_id, "", "", null, "false", "", "false",
										"true", billNo, requestType, ticketPrice);
								logger.info(order_id + "支付失败");
								return "error:" + "下单失败";
							}*/
						} else {
							sendCreateOrderInfo("error", "下单失败" + orderPrice, "", childrenUser, "", order_id, "", "", null, "", "", "false",
									"true", billNo, requestType, ticketPrice);
							logger.info(order_id + "下单失败" + orderPrice);
							return "error:" + "下单失败";
						}
					}
				} else {
					sendCreateOrderInfo("error", "下单失败，应付" + price + "实付" + orderPrice, "", childrenUser, "", order_id, "", "", null, "", "", "false",
							"true", billNo, requestType, ticketPrice);
					logger.info(order_id + "下单失败，应付" + price + "实付" + orderPrice);
					return "error:" + "下单失败，应付" + price + "实付" + orderPrice;
				}
			} else {
				sendCreateOrderInfo("error", "下单验价失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",
						"true", billNo, requestType, ticketPrice);
				logger.info(order_id + "下单验价失败");
				return "error:" + "下单验价失败";
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
	@Test
	public void a() {
		String id = "undefined";
		String orderNo = "B202006171800033961496";
		System.out.println(MD5Util.getMD5(id + orderNo + "paycommon_request_param_check_ok"));
	}
	public String getRandomString(int length){
		//定义组成随机字符串的所有字符
		String str="0123456789";
		Random random=new Random();
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<length;i++){
			//产生62以内的随机数，因为组成随机字符串的字符有62个
			int number=random.nextInt(10);
			//将str的第number个字符加到sb的末尾
			sb.append(str.charAt(number));
		}
		return sb.toString();

	}
	private String airPata(CloseableHttpClient httpclient, String orderId, String token, String flightMsg) {
		CloseableHttpResponse response = null;
		String back = "";
		try {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
					.setStaleConnectionCheckEnabled(true).build();
			// https://th-api.tianhangbox.com
			HttpPost post = new HttpPost("/order/airPata");
			post.setConfig(config);
			post.setHeader("Host","th-api.tianhangbox.com");
			post.setHeader("Origin","https://www.tianhangbox.com");
			HttpHost target = new HttpHost("th-api.tianhangbox.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("flight", flightMsg));
			nameValue.add(new BasicNameValuePair("token",token));
			nameValue.add(new BasicNameValuePair("timestamp", System.currentTimeMillis()+""));
			nameValue.add(new BasicNameValuePair("device","web"));
			nameValue.add(new BasicNameValuePair("browerInfo","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36"));
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			response = httpclient.execute(target,post);
			back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info(orderId + "验价返回：" + back);
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
		return back;
	}

	private String bookOrder(CloseableHttpClient httpclient, String orderId, String token, String flightMsg,String psgData,String dept,String arrival) {
		CloseableHttpResponse response = null;
		String back = "";
		try {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
					.setStaleConnectionCheckEnabled(true).build();
			HttpPost post = new HttpPost("/api/v1/trip_manager/air_ticket_domestic_order/book");
			post.setConfig(config);
			post.setHeader("Host","th-api.tianhangbox.com");
			post.setHeader("Origin","https://www.tianhangbox.com");
			HttpHost target = new HttpHost("th-api.tianhangbox.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("orgCityCode", dept));
			nameValue.add(new BasicNameValuePair("arrCityCode", arrival));
			nameValue.add(new BasicNameValuePair("book_goFlightVo", flightMsg));
			nameValue.add(new BasicNameValuePair("book_backFlightVo", ""));
			nameValue.add(new BasicNameValuePair("insuIds", ""));
			nameValue.add(new BasicNameValuePair("psgData", psgData));
			nameValue.add(new BasicNameValuePair("phoneReceiveMsg", "13760132320"));
			nameValue.add(new BasicNameValuePair("businessStatus", "0"));
			nameValue.add(new BasicNameValuePair("femaleMemberId", "0"));
			nameValue.add(new BasicNameValuePair("postData", "{\n" +
					"  \"invoice\": \"1\",\n" +
					"  \"reimbursement\": \"0\",\n" +
					"  \"travelItinerary\": \"1\",\n" +
					"  \"postCyle\": \"1\",\n" +
					"  \"postType\": \"1\",\n" +
					"  \"mobile\": \"\",\n" +
					"  \"consignee\": \"\",\n" +
					"  \"provinceName\": \"\",\n" +
					"  \"cityName\": \"\",\n" +
					"  \"postStreet\": \"\",\n" +
					"  \"address\": \"\"\n" +
					"}"));
			nameValue.add(new BasicNameValuePair("reason",""));
			nameValue.add(new BasicNameValuePair("tripNo",""));
			nameValue.add(new BasicNameValuePair("wayId",""));
			nameValue.add(new BasicNameValuePair("levelId","0"));
			nameValue.add(new BasicNameValuePair("settleCompanyId","0"));
			nameValue.add(new BasicNameValuePair("deptId","0"));
			nameValue.add(new BasicNameValuePair("overproofReason",""));
			nameValue.add(new BasicNameValuePair("token",token));
			nameValue.add(new BasicNameValuePair("timestamp", System.currentTimeMillis()+""));
			nameValue.add(new BasicNameValuePair("device","web"));
			nameValue.add(new BasicNameValuePair("browerInfo","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36"));
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			response = httpclient.execute(target, post);
			back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info(orderId + "下单返回：" + back);
		} catch (Exception e) {
			logger.error(orderId + "error", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error(orderId + "error", e);
				}
			}
		}
		return back;
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
	private String getToken(String account,String pwd,CloseableHttpClient httpclient,String orderId) throws IOException {
		String token = "";
		CloseableHttpResponse response = null;
		try {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
					.setStaleConnectionCheckEnabled(true).build();
			String url = "https://th-api.tianhangbox.com/login/appNewLogin";
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("mobilePhone", account));
			pwd = MD5Util.getMD5(pwd);
			nameValue.add(new BasicNameValuePair("loginPsw", pwd));
			nameValue.add(new BasicNameValuePair("timestamp", System.currentTimeMillis()+""));
			nameValue.add(new BasicNameValuePair("device", "web"));
			nameValue.add(new BasicNameValuePair("browerInfo", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36"));
			post.setEntity(new UrlEncodedFormEntity(nameValue,"UTF-8"));
			post.setConfig(config);
			response = httpclient.execute(post);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info(orderId + "获取token返回:" + back);
			if (StringUtils.isNotEmpty(back)) {
				JSONObject backObj = JSON.parseObject(back);
				String code = backObj.getString("ret");
				if ("0".equals(code)) {
					JSONObject dataObj = backObj.getJSONObject("data");
					token = dataObj.getString("token");
				}
			}
		}catch (Exception e) {
			logger.error(orderId + "获取tokenerror：",e);
		}finally {
			if (response!=null) {
				response.close();
				response = null;
			}
		}
		return token;
	}
	private String ticketOrder(CloseableHttpClient httpclient, String orderId, String token,String id,String accountMsg) {
		CloseableHttpResponse response = null;
		try {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
					.setStaleConnectionCheckEnabled(true).build();
			HttpPost post = new HttpPost("https://th-api.tianhangbox.com/order/newappGetDometicketOrder");
			post.setConfig(config);
			HttpHost target = new HttpHost("https://www.tianhangbox.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("orderId", id));
			nameValue.add(new BasicNameValuePair("token",token));
			nameValue.add(new BasicNameValuePair("timestamp", System.currentTimeMillis()+""));
			nameValue.add(new BasicNameValuePair("device","web"));
			nameValue.add(new BasicNameValuePair("browerInfo","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36"));
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			response = httpclient.execute(target, post);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info(orderId + "验价返回：" + back);
			if (StringUtils.isNotEmpty(back)) {
				JSONObject backObj = JSON.parseObject(back);
				String code = backObj.getString("ret");
				if ("401".equals(code)) {
					String account = accountMsg.split("##")[0];
					String pwd = accountMsg.split("##")[1];
					token = getToken(account,pwd,httpclient,orderId);
					int count = 0;
					while (StringUtils.isEmpty(token) && count < 3) {
						Thread.sleep(3000);
						token = getToken(account,pwd,httpclient,orderId);
						if (StringUtils.isNotEmpty(token)) {
							tokenMap.put(account,token);
							break;
						}
						count++;
					}
					nameValue.add(new BasicNameValuePair("token",token));
					post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
					response = httpclient.execute(target, post);
					back = EntityUtils.toString(response.getEntity(), "utf-8");
				}
			}
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
	private String preCreate(CloseableHttpClient httpclient, String orderId, String token,String accountMsg,String orderNo,String requestVerify,String amount) {
		CloseableHttpResponse response = null;
		try {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
					.setStaleConnectionCheckEnabled(true).build();
			HttpPost post = new HttpPost("https://th-api.tianhangbox.com/pay/alipay/precreate");
			post.setConfig(config);
			HttpHost target = new HttpHost("https://www.tianhangbox.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("subject", "机票订单"));
			nameValue.add(new BasicNameValuePair("outTradeNo", orderNo));
			nameValue.add(new BasicNameValuePair("amount", amount));
			nameValue.add(new BasicNameValuePair("businessStatus", "0"));
			nameValue.add(new BasicNameValuePair("businessType", "dometicketOrderPay"));
			nameValue.add(new BasicNameValuePair("outTradeNo", orderNo));
			nameValue.add(new BasicNameValuePair("body", orderNo));
			nameValue.add(new BasicNameValuePair("requestVerify", requestVerify));
			nameValue.add(new BasicNameValuePair("token",token));
			nameValue.add(new BasicNameValuePair("timestamp", System.currentTimeMillis()+""));
			nameValue.add(new BasicNameValuePair("device","web"));
			nameValue.add(new BasicNameValuePair("browerInfo","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36"));
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			response = httpclient.execute(target, post);
			String back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info(orderId + "支付返回：" + back);
			if (StringUtils.isNotEmpty(back)) {
				JSONObject backObj = JSON.parseObject(back);
				String code = backObj.getString("ret");
				if ("401".equals(code)) {
					String account = accountMsg.split("##")[0];
					String pwd = accountMsg.split("##")[1];
					token = getToken(account,pwd,httpclient,orderId);
					int count = 0;
					while (StringUtils.isEmpty(token) && count < 3) {
						Thread.sleep(3000);
						token = getToken(account,pwd,httpclient,orderId);
						if (StringUtils.isNotEmpty(token)) {
							tokenMap.put(account,token);
							break;
						}
						count++;
					}
					nameValue.add(new BasicNameValuePair("token",token));
					post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
					response = httpclient.execute(target, post);
					back = EntityUtils.toString(response.getEntity(), "utf-8");
				}
			}
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
	private String payOrder(CloseableHttpClient httpclient, String orderId, String token,String accountMsg,String payPwd,String amount,String orderNo) {
		CloseableHttpResponse response = null;
		String back = "";
		try {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
					.setStaleConnectionCheckEnabled(true).build();
			HttpPost post = new HttpPost("/pay/newappthepay");
			post.setConfig(config);
			post.setHeader("Host","th-api.tianhangbox.com");
			post.setHeader("Origin","https://www.tianhangbox.com");
			HttpHost target = new HttpHost("th-api.tianhangbox.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("outTradeNo", orderNo));
			nameValue.add(new BasicNameValuePair("totalFee", amount));
			nameValue.add(new BasicNameValuePair("businessStatus", "0"));
			nameValue.add(new BasicNameValuePair("payPwd", MD5Util.getMD5(payPwd)));
			nameValue.add(new BasicNameValuePair("businessType", "dometicketOrderPay"));
			nameValue.add(new BasicNameValuePair("token",token));
			nameValue.add(new BasicNameValuePair("timestamp", System.currentTimeMillis()+""));
			nameValue.add(new BasicNameValuePair("device","web"));
			nameValue.add(new BasicNameValuePair("browerInfo","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36"));
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			response = httpclient.execute(target, post);
			back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info(orderId + "支付返回：" + back);
			if (StringUtils.isNotEmpty(back)) {
				JSONObject backObj = JSON.parseObject(back);
				String code = backObj.getString("ret");
				if ("401".equals(code)) {
					String account = accountMsg.split("##")[0];
					String pwd = accountMsg.split("##")[1];
					token = getToken(account,pwd,httpclient,orderId);
					int count = 0;
					while (StringUtils.isEmpty(token) && count < 3) {
						Thread.sleep(3000);
						token = getToken(account,pwd,httpclient,orderId);
						if (StringUtils.isNotEmpty(token)) {
							tokenMap.put(account,token);
							break;
						}
						count++;
					}
					nameValue.add(new BasicNameValuePair("token",token));
					post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
					response = httpclient.execute(target, post);
					back = EntityUtils.toString(response.getEntity(), "utf-8");
				}
			}
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
		return back;
	}

	private String queryOrder(CloseableHttpClient httpclient, String orderId, String token,String accountMsg,String id) {
		CloseableHttpResponse response = null;
		String back = "";
		try {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
					.setStaleConnectionCheckEnabled(true).build();
			HttpPost post = new HttpPost("/order/newappGetDometicketOrder");
			post.setConfig(config);
			post.setHeader("Host","th-api.tianhangbox.com");
			post.setHeader("Origin","https://www.tianhangbox.com");
			HttpHost target = new HttpHost("th-api.tianhangbox.com", 443, "https");
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			nameValue.add(new BasicNameValuePair("orderId", id));
			nameValue.add(new BasicNameValuePair("token",token));
			nameValue.add(new BasicNameValuePair("timestamp", System.currentTimeMillis()+""));
			nameValue.add(new BasicNameValuePair("device","web"));
			nameValue.add(new BasicNameValuePair("browerInfo","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36"));
			post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
			response = httpclient.execute(target, post);
			back = EntityUtils.toString(response.getEntity(), "utf-8");
			logger.info(orderId + "查询票号返回：" + back);
			if (StringUtils.isNotEmpty(back)) {
				JSONObject backObj = JSON.parseObject(back);
				String code = backObj.getString("ret");
				if ("401".equals(code)) {
					String account = accountMsg.split("##")[0];
					String pwd = accountMsg.split("##")[1];
					token = getToken(account,pwd,httpclient,orderId);
					int count = 0;
					while (StringUtils.isEmpty(token) && count < 3) {
						Thread.sleep(3000);
						token = getToken(account,pwd,httpclient,orderId);
						if (StringUtils.isNotEmpty(token)) {
							tokenMap.put(account,token);
							break;
						}
						count++;
					}
					nameValue.add(new BasicNameValuePair("token",token));
					post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
					response = httpclient.execute(target, post);
					back = EntityUtils.toString(response.getEntity(), "utf-8");
				}
			}
		} catch (Exception e) {
			logger.error(orderId + "error", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}
		return back;
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
	@Test
	public void d() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		System.out.println("123");
	}

	@Test
	public void d3() throws IOException {
		CloseableHttpClient client = HttpClients.custom().build();
		HttpGet get = new HttpGet("http://121.204.249.109:8000/api/sign/username=feeye123&password=feeye123");
		CloseableHttpResponse response = client.execute(get);
		String back = EntityUtils.toString(response.getEntity(),"utf-8");
		System.out.println(back);

	}
}
