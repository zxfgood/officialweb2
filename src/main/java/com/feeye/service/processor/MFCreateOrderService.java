//package com.feeye.service.processor;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//import javax.imageio.ImageIO;
//import javax.net.ssl.SSLContext;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.Header;
//import org.apache.http.HttpHost;
//import org.apache.http.NameValuePair;
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
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLContextBuilder;
//import org.apache.http.conn.ssl.TrustStrategy;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.BasicCookieStore;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
//import org.dom4j.DocumentHelper;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Dimension;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.Point;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedCondition;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import com.feeye.common.Constant;
//import com.feeye.service.official.OfficialMain;
//import com.feeye.util.DateUtil;
//import com.feeye.util.ParseFlightInFo;
//import com.feeye.util.PhantomjsDriverUtil;
//import com.feeye.util.PropertiesUtils;
//import com.feeye.util.YunSu;
//
//public class MFCreateOrderService {
//	private static final Logger logger = Logger.getLogger(MFCreateOrderService.class);
//	private static Map<String,String> accountMF = new HashMap<String,String>();
//	private static Map<String,String> accountStatus = new HashMap<String,String>();
//	private static final int timeout = 70000;
//
//	public String StartCreateOrder(String orderJson, int retryCount, int requestType, int retry) {
//		long nowDateTime = new Date().getTime(); //获取当前时间，后面步骤要根据该时间点做超时处理
//		String cancelUrl = PropertiesUtils.getPropertiesValue("config", "cancelUrl");
//		if (StringUtils.isEmpty(orderJson)) {
//			return "ERROR:数据不完整";
//		}
//		logger.info("传过来的数据有" + orderJson);
//		// 登陆账号
//		InputStream re = null;
//		SSLConnectionSocketFactory sslsf = null;
//		BasicCookieStore cookieStore = new BasicCookieStore();//一个cookies
//		String cookie = "";
//		try {
//			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
//				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//					return true;
//				}
//			}).build();
//			sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			//初始化SSL连接
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
//		try {
//			proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
//		} catch (MalformedChallengeException e1) {
//			logger.error("error",e1);
//		}
//
//		String childrenUser = "";
//		String order_id = "";
//		CredentialsProvider credsProvider = new BasicCredentialsProvider();
//
//		String proxyUser="HL7F5JF125K85K8D";
//		String proxyPass="FC393F432489B2E5";
//		credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));
//
//
//		HttpHost httpHost = new HttpHost("http-dyn.abuyun.com", 9020, "http");
//
//		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout)
//				.setConnectTimeout(timeout)
//				.setConnectionRequestTimeout(timeout).setProxy(httpHost)
//				.setStaleConnectionCheckEnabled(true).build();
//
//
//		CloseableHttpClient httpclient = HttpClients.custom()
//				.setDefaultRequestConfig(defaultRequestConfig)
//				.setSSLSocketFactory(sslsf)
//				.setDefaultCredentialsProvider(credsProvider)
//				.setDefaultCookieStore(cookieStore).build();
//		CloseableHttpResponse response = null;
//		String billNo = "";
//		int index = 0;
//		try {
//			String back = "";
//			JSONObject json = new JSONObject(orderJson);
//			JSONArray flights = json.getJSONArray("flights");
//			String fltno = flights.getJSONObject(0).getString("flightNo");
//			String price = flights.getJSONObject(0).getString("price");
//			childrenUser = json.getString("username");
//			String dep = flights.getJSONObject(0).getString("departure");
//			String arr = flights.getJSONObject(0).getString("arrival");
//			String cabin = flights.getJSONObject(0).getString("cabin");
//			String depCN = PropertiesUtils.getPropertiesValue("allcity", dep);
//			String arrCN = PropertiesUtils.getPropertiesValue("allcity", arr);
//			String depdate = flights.getJSONObject(0).getString("departureDate");
//			String account = json.getString("account");
//			String key = account.split("_")[0]+"@_@"+account.split("_")[1];
//			order_id = json.getString("id");
//			String officialCabin = "";
//			try {
//				billNo = json.getString("billNo");
//			} catch (Exception e) {}
//			String newOrderNo = "";
//			try {
//				newOrderNo = json.getString("newOrderNo");
//			} catch (Exception e) {}
//			JSONArray passengers = json.getJSONArray("passengers");
//
//			if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//				sendOrderStatus(childrenUser, order_id, "已取消出票");
//				sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
//						null, "", "", "false", "true",billNo,requestType);
//				logger.info(order_id + "已取消出票");
//				return "取消出票";
//			}
//			String locationValue = "";
//			boolean isLogined = false;
//			cookie = accountMF.get(key);
//			if(StringUtils.isNotEmpty(cookie)){
//				for(int i=0;i<8;i++){
//					synchronized (accountStatus) {
//						String status = accountStatus.get(cookie);
//						if(StringUtils.isEmpty(status)||"0".equals(status)){
//							isLogined = true;
//							accountStatus.put(cookie, "1");
//							break;
//						}
//					}
//					Thread.sleep(3*1000);
//				}
//			}
//			if(!isLogined){
//				cookie = "";
//				sendOrderStatus(childrenUser, order_id, "账号未登录，开始进行账号登录");
//				for (int i = 0; i < 3; i++) {
//					cookie = getCookieNewMethod(json, httpclient, defaultRequestConfig, re);
//					logger.info(order_id + "--" + cookie);
//					if (StringUtils.isNotEmpty(cookie)) {
//						String[] cookies = cookie.split(";");
//						String newCookie = "";
//						for(String cos:cookies) {
//							if(cos.contains("JSESSIONID")) {
//								newCookie = cos;
//								break;
//							}
//						}
//						cookie = newCookie;
//						accountMF.put(key, cookie);
//						accountStatus.put(cookie, "1");
//						break;
//					}
//					int flag = i + 1;
//					sendOrderStatus(childrenUser, order_id, "登录失败，重试第" + flag + "次登录");
//				}
//				if (StringUtils.isEmpty(cookie)) {
//					sendCreateOrderInfo("error", "登录失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//							"true",billNo,requestType);
//					return "ERROR:登录失败";
//				}
//				if ("用户名或密码不正确".equals(cookie)) {
//					sendCreateOrderInfo("error", "用户名或密码不正确", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//							"true",billNo,requestType);
//					return "ERROR:用户名或密码不正确";
//				}
//				if ("验证码点数不足".equals(cookie)) {
//					sendCreateOrderInfo("error", "验证码点数不足", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//							"true",billNo,requestType);
//					return "ERROR:验证码点数不足";
//				}
//			}else {
//				sendOrderStatus(childrenUser, order_id, "账号已登录");
//			}
//			logger.info("cookie:"+cookie);
//			try {
//				if(requestType == 1){
//					int count = 30;
//					while (count > 0) {
//						try {
//							sendOrderStatus(childrenUser, order_id, "获取票号信息");
//							back = getDetail(httpclient, defaultRequestConfig, cookie, newOrderNo, 0);
//							if(StringUtils.isEmpty(back)){
//								try {
//									Thread.sleep(5000);
//								} catch (InterruptedException e) {
//									logger.error("error", e);
//								}
//								count--;
//								continue;
//							}
//							back = back + "123";
//						} catch (Exception e) {
//							logger.error("error", e);
//						}
//						if (back != null) {
//							String ticketnos[] = back.split("@_@");
//							if (ticketnos.length == 2) {
//								sendOrderStatus(childrenUser, order_id, "回填票号");
//								// 判断获取的票号数是否与乘客数量相符合,不相符合重新
//								String[] ticketCount = ticketnos[0].split("#_#");
//								if (ticketCount.length == passengers.length() && !ticketnos[0].contains("null")) {
//									sendCreateOrderInfo("success", "", "", childrenUser, newOrderNo, order_id, "",
//											"true", ticketnos[0], "易宝", "", "false", "true",billNo,requestType);
//									return "SUCCESS";
//								}
//							}
//						}
//						try {
//							Thread.sleep(5000);
//						} catch (InterruptedException e) {
//							logger.error("error", e);
//						}
//						count--;
//					}
//					if (count <= 0) {
//						sendCreateOrderInfo("error", "未找到票号信息", "", childrenUser, newOrderNo, order_id, "", "", null, "", "",
//								"false", "true",billNo,requestType);
//						return "SUCCESS#@@#未找到票号信息";
//					}
//				}
//			} catch (Exception e) {
//				sendCreateOrderInfo("error", "获取票号异常", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true",billNo,requestType);
//				logger.error("error",e);
//				return "SUCCESS#@@#获取票号异常";
//			}
//
//			try {
//
//				// 进入查询航班页面
//				String url = "https://new.xiamenair.com/ticket.html?tripType=OW&orgCode="+dep+"&dstCode="+arr+"&orgCityName="+depCN+"&dstCityName="+arrCN+"orgDate="+depdate+"&dstDate=&isInter=false&orgIsintl=0&dstIsintl=0";
//				sendOrderStatus(childrenUser, order_id, "查询航班信息");
//				try {
//					logger.info(order_id+"进去航班查询界面");
//					for(;index<5;index++){
//						try {
//							back = findFlights(httpclient, defaultRequestConfig, cookieStore, cookie, json);
//							if(back.contains("400 Bad request")&&retryCount<3){
//								accountMF.remove(key);
//								accountStatus.remove(cookie);
//								retryCount = retryCount+1;
//								return StartCreateOrder(orderJson, retryCount, requestType, retry);
//							}
//							break;
//						} catch (Exception e) {
//							logger.error("error",e);
//						}
//					}
//				} catch (Exception e) {
//					logger.error("error",e);
//				}
//
//				if(index==5){
//					sendCreateOrderInfo("error", "查询航班失败", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info(order_id+"查询航班失败");
//					return "查询航班失败";
//				}
//
//				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//					sendOrderStatus(childrenUser, order_id, "已取消出票");
//					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info("已取消出票");
//					return "取消出票";
//				}
//				// 解析航班信息
//				logger.info(order_id+"筛选航班");
//				Map<String,String>  resultMap = ParseFlightInFo.CheckNewMfFlightIsExist(fltno, price, back, dep, arr, depdate);
//				if(resultMap==null||resultMap.size()==0){
//					if(retryCount<4){
//						retryCount = retryCount+1;
//						return StartCreateOrder(orderJson, retryCount, requestType, retry);
//					}
//					sendCreateOrderInfo("error","无对应价格或航班", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					return "error:无对应价格或航班";
//				}
//				String resultString = resultMap.get("selectedFlightsJson");
//				try {
//					officialCabin = resultMap.get("cabin");
//				} catch (Exception e) {}
//				boolean matchCabin = false;
//				try {
//					matchCabin = json.getBoolean("matchCabin");
//				} catch (Exception e) {}
//				try {
//					if(matchCabin){
//						if(StringUtils.isNotEmpty(cabin)&&StringUtils.isNotEmpty(officialCabin)&&cabin.equalsIgnoreCase(officialCabin)){
//							String errorMsg =  "原舱位"+cabin+"与官网舱位"+officialCabin+"不匹配，停止出票";
//							sendCreateOrderInfo("error",errorMsg, "", childrenUser, "", order_id, "", "",
//									null, "", "", "false", "true",billNo,requestType);
//							return "error:"+errorMsg;
//						}
//					}
//				} catch (Exception e) {
//					logger.error("error",e);
//				}
//
//				if (StringUtils.isEmpty(resultString)) {
//					sendCreateOrderInfo("error", "无对应航班", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//							"true",billNo,requestType);
//					return "ERROR:#@@#:无对应航班";
//				}
//				sendOrderStatus(childrenUser, order_id, "选择航班信息");
//				// 选择航班
//				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//					sendOrderStatus(childrenUser, order_id, "已取消出票");
//					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info("已取消出票");
//					return "取消出票";
//				}
//				logger.info(order_id+"选择航班");
//				Map<String, Object> mapResult = null;
//				index = 0;
//				for(;index<5;index++){
//					try {
//						mapResult = flights(httpclient, defaultRequestConfig, cookieStore, cookie,
//								resultString, url);
//						break;
//					} catch (Exception e) {
//						logger.error("error",e);
//					}
//				}
//				if(index==5){
//					sendCreateOrderInfo("error", "选择航班失败", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info(order_id+"选择航班失败");
//					return "选择航班失败";
//				}
////				if (mapResult == null || mapResult.size() == 0) {
////					accountMF.remove(key);
////					if(retryCount<2){
////						retryCount = retryCount+1;
////						sendOrderStatus(childrenUser, order_id, "登录状态失效，正在重试");
////						return StartCreateOrder(orderJson, retryCount, requestType);
////					}
////					if (mapResult == null) {
////						sendCreateOrderInfo("error", "登录状态失效，请重试创单", "", childrenUser, "", order_id, "", "", null, "",
////								"", "false", "true",billNo,requestType);
////					} else {
////						sendCreateOrderInfo("error", "无对应航班", "", childrenUser, "", order_id, "", "", null, "", "",
////								"false", "true",billNo,requestType);
////					}
////					return "ERROR:#@@#:无对应航班";
////				}
//				sendOrderStatus(childrenUser, order_id, "填写乘客信息");
//				// 创建乘客信息
//				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//					sendOrderStatus(childrenUser, order_id, "已取消出票");
//					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info("已取消出票");
//					return "取消出票";
//				}
//				index = 0;
//				for(;index<5;index++){
//					try {
//						resultString = createJson(httpclient,defaultRequestConfig,cookieStore,cookie,orderJson,url,depdate);
//						break;
//					} catch (Exception e) {
//						logger.error("error",e);
//					}
//				}
//				if(index==5){
//					sendCreateOrderInfo("error", "提交乘客信息失败", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info(order_id+"提交乘客信息失败");
//					return "提交乘客信息失败";
//				}
//				//增值服务
//				sendOrderStatus(childrenUser, order_id, "提交增值服务");
//				index = 0;
//				for(;index<5;index++){
//					try {
//						addService(httpclient,defaultRequestConfig,cookie);
//						break;
//					} catch (Exception e) {
//						sendOrderStatus(childrenUser, order_id, "提交增值服务异常");
//						logger.error("error",e);
//					}
//				}
//				if(index==5){
//					sendCreateOrderInfo("error", "提交增值服务失败", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info(order_id+"提交增值服务失败");
//					return "提交增值服务失败";
//				}
//				sendOrderStatus(childrenUser, order_id, "确认订单信息");
//				// 确认订单
//				int createCount = 0;
//				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//					sendOrderStatus(childrenUser, order_id, "已取消出票");
//					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info("已取消出票");
//					return "取消出票";
//				}
//				if(DateUtil.IsRunningTimeOut(nowDateTime,15*1000*60)){
//					sendOrderStatus(childrenUser, order_id, "创单超时，停止创单");
//					sendCreateOrderInfo("error", "创单超时，停止创单", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info("创单超时，停止创单");
//					return "创单超时，停止创单";
//				}
//				sendOrderStatus(childrenUser, order_id, "开始校验航班信息");
//				index = 0;
//				for(;index<5;index++){
//					boolean flag = false;
//					try {
//						flag = selectedOffer(httpclient,defaultRequestConfig,cookie,orderJson);
//						if(!flag&&retry<3){
//							retry++;
//							accountMF.remove(key);
//							accountStatus.remove(cookie);
//							Thread.sleep(500);
//							return StartCreateOrder(orderJson, retryCount, requestType, retry);
//						}
//						if(!flag){
//							sendCreateOrderInfo("error", "校验航班信息失败，请重新创单", "", childrenUser, "", order_id, "", "",
//									null, "", "", "false", "true",billNo,requestType);
//							return "ERROR:#@@#校验航班信息失败，请重新创单";
//						}
//						if(flag){
//							break;
//						}
//					} catch (Exception e) {
//						logger.error("error",e);
//					}
//				}
//				if(index==5){
//					sendCreateOrderInfo("error", "校验航班信息异常", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info(order_id+"校验航班信息异常");
//					return "校验航班信息异常";
//				}
//				sendOrderStatus(childrenUser, order_id, "开始校验乘客信息");
//				index = 0;
//				for(;index<5;index++){
//					boolean flag = false;
//					try {
//						flag = orderParam(httpclient,defaultRequestConfig,cookie,orderJson);
//						if(!flag&&retry<3){
//							retry++;
//							accountMF.remove(key);
//							accountStatus.remove(cookie);
//							Thread.sleep(500);
//							return StartCreateOrder(orderJson, retryCount, requestType, retry);
//						}
//						if(!flag){
//							sendCreateOrderInfo("error", "校验乘客信息失败，请重新创单", "", childrenUser, "", order_id, "", "",
//									null, "", "", "false", "true",billNo,requestType);
//							return "ERROR:#@@#校验乘客信息失败，请重新创单";
//						}
//						if(flag){
//							break;
//						}
//					} catch (Exception e) {
//						logger.error("error",e);
//					}
//				}
//				if(index==5){
//					sendCreateOrderInfo("error", "校验乘客信息异常", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info(order_id+"校验乘客信息异常");
//					return "校验乘客信息异常";
//				}
//				sendOrderStatus(childrenUser, order_id, "开始创建订单");
//				index = 0;
//				Map<String, String> confirmResultMap = null;
//				for(;index<5;index++){
//					try {
//						confirmResultMap = confirmOrder(httpclient, defaultRequestConfig, cookieStore,
//								cookie, resultString, mapResult, orderJson, createCount);
//						try {
//							if(confirmResultMap!=null&&StringUtils.isNotEmpty(confirmResultMap.get("msg"))){
//								String errorMsg = confirmResultMap.get("msg");
//								if("生单结果异常，请确认是否创单成功".equals(errorMsg)||(StringUtils.isNotEmpty(errorMsg)&&errorMsg.contains("不能重复预定"))){
//									//查询订单列表确认是否创单成功
//									confirmResultMap = new HashMap<String,String>();
//									int point = 0;
//									for(;point<3;point++){
//										try {
//											confirmResultMap = searchOrder(httpclient,defaultRequestConfig,cookie,orderJson);
//											break;
//										} catch (Exception e) {
//											logger.error("error",e);
//										}
//									}
//									if(confirmResultMap != null){
//										break;
//									}
//								}
//								sendCreateOrderInfo("error", errorMsg, "", childrenUser, "", order_id, "", "",
//										null, "", "", "false", "true",billNo,requestType);
//								logger.info(order_id+errorMsg);
//								return errorMsg;
//							}
//						} catch (Exception e) {
//							logger.error("error",e);
//						}
//						break;
//					} catch (Exception e) {
//						logger.error("error",e);
//					}
//				}
//				if(index==5){
//					sendCreateOrderInfo("error", "创单异常", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info(order_id+"创单异常");
//					return "创单异常";
//				}
//				if(confirmResultMap == null){
//					sendCreateOrderInfo("error", "创单未知异常,请到官网确认是否已创单", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					return "ERROR:#@@#:创单未知异常,请到官网确认是否已创单";
//				}
//				if (confirmResultMap.get("msg") != null) {
//					String errorMsg = confirmResultMap.get("msg");
//					logger.info(errorMsg);
//					if(confirmResultMap.get("msg").equals("官网价格有变动")){
//						errorMsg = "官网"+officialCabin + "舱价格有变动，请人工确认";
//					}
//					sendCreateOrderInfo("error", errorMsg, "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					return "ERROR:#@@#:" + errorMsg + "";
//				}
//				String orderNo = confirmResultMap.get("orderId");
//				String allPrice = confirmResultMap.get("totalPrice");
//				if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//					sendOrderStatus(childrenUser, order_id, "已取消出票");
//					sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					logger.info("已取消出票");
//					return "取消出票";
//				}
//				if(StringUtils.isEmpty(orderNo)||"null".equals(orderNo)){
//					sendCreateOrderInfo("error", "未获取到订单号，请到官网确认是否已创单", "", childrenUser, "", order_id, "", "",
//							null, "", "", "false", "true",billNo,requestType);
//					return "未获取到订单号，请到官网确认是否已创单";
//				}
//				accountStatus.remove(cookie);
//				sendCreateOrderInfo("success", "", "", childrenUser, orderNo, order_id, "", "", null, "", "", "false",
//						"true",billNo,requestType);
//
//
//				// 开始支付
//				Map<String,String> payInfoResult = officialPay(httpclient, defaultRequestConfig, cookie, orderJson, orderNo, allPrice, childrenUser, order_id, billNo, requestType, cancelUrl, nowDateTime);
//				String payTransactionid = "";
//				String errorMsg = "";
//				String stopMsg = "";
//				try {
//					payTransactionid = payInfoResult.get("bankNo");
//					errorMsg = payInfoResult.get("error");
//					stopMsg = payInfoResult.get("stop");
//				} catch (Exception e) {}
//				if(StringUtils.isNotEmpty(stopMsg)){
//					return stopMsg;
//				}
//				if(StringUtils.isEmpty(payTransactionid)){
//					//这里调用详情，查看是否已经支付
//					index= 0;
//					for(;index<3;index++){
//						try {
//							String orderInfo = getDetail(httpclient, defaultRequestConfig, cookie, orderNo, 1);
//							if(StringUtils.isEmpty(orderInfo)){
//								continue;
//							}
//							//解析详情结果，看是不是已经支付了，并获取支付流水号
//							payTransactionid = parseOrderInfo(orderInfo);
//							if("NOT_ENTITLED".equals(payTransactionid)){
//								//如果还未支付，再走一遍支付
//								payInfoResult = officialPay(httpclient, defaultRequestConfig, cookie, orderJson, orderNo, allPrice, childrenUser, order_id, billNo, requestType, cancelUrl, nowDateTime);
//								if(payInfoResult==null){
//									continue;
//								}
//								try {
//									payTransactionid = payInfoResult.get("bankNo");
//									errorMsg = payInfoResult.get("error");
//									stopMsg = payInfoResult.get("stop");
//								} catch (Exception e) {}
//								if(StringUtils.isNotEmpty(stopMsg)){
//									return stopMsg;
//								}
//								if(StringUtils.isNotEmpty(payTransactionid)){
//									break;
//								}
//								continue;
//							}
//							if(StringUtils.isEmpty(payTransactionid)){
//								sendCreateOrderInfo("error", "支付未知异常，请到官网检查是否已支付", "", childrenUser, orderNo, order_id, "", "", null, "", "",
//										"false", "true",billNo,requestType);
//								return "支付未知异常，请到官网检查是否已支付";
//							}
//							break;
//						} catch (Exception e) {
//							logger.error("error",e);
//						}
//					}
//					if(index==3){
//						if(StringUtils.isEmpty(errorMsg)){
//							sendCreateOrderInfo("error", "支付未知异常，请到官网检查是否已支付", "", childrenUser, orderNo, order_id, "", "", null, "", "",
//									"false", "true",billNo,requestType);
//						}else{
//							sendCreateOrderInfo("error", errorMsg, "", childrenUser, orderNo, order_id, "", "", null, "", "",
//									"false", "true",billNo,requestType);
//						}
//						return "error:支付未知异常，请到官网检查是否已支付";
//					}
//				}
//				int count = 30;
//				sendCreateOrderInfo("success", "", "", childrenUser, orderNo, order_id, "true", "", null, "易宝",
//						"", "false", "true",billNo,requestType);
//				while (payTransactionid != null && count > 0) {
//					try {
//						sendCreateOrderInfo("success", "", "", childrenUser, orderNo, order_id, "", "", null, "易宝",
//								"", "false", "true",billNo,requestType);
//						sendOrderStatus(childrenUser, order_id, "获取票号信息");
//						back = getDetail(httpclient, defaultRequestConfig, cookie, orderNo, 0);
//						if(StringUtils.isEmpty(back)){
//							try {
//								Thread.sleep(5000);
//							} catch (InterruptedException e) {
//								logger.error("error", e);
//							}
//							count--;
//							continue;
//						}
//						back = back + payTransactionid;
//					} catch (Exception e) {
//						logger.error("error", e);
//						if (StringUtils.isNotEmpty(orderNo)) {
//							sendCreateOrderInfo("success", "", "", childrenUser, orderNo, order_id, "", "", null, "易宝",
//									"", "false", "true",billNo,requestType);
//						}
//					}
//					if (back != null) {
//						String ticketnos[] = back.split("@_@");
//						if (ticketnos.length == 2) {
//							sendOrderStatus(childrenUser, order_id, "回填票号");
//							// 判断获取的票号数是否与乘客数量相符合,不相符合重新
//							String[] ticketCount = ticketnos[0].split("#_#");
//							if (ticketCount.length == passengers.length() && !ticketnos[0].contains("null")) {
//								sendCreateOrderInfo("success", "", allPrice + "", childrenUser, orderNo, order_id, "",
//										"true", ticketnos[0], "易宝", ticketnos[1], "false", "true",billNo,requestType);
//								return "SUCCESS";
//							}
//						}
//					}
//					try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e) {
//						logger.error("error", e);
//					}
//					count--;
//				}
//				if (count <= 0) {
//					sendCreateOrderInfo("success", "", "", childrenUser, orderNo, order_id, "", "", null, "", "",
//							"false", "true",billNo,requestType);
//					return "SUCCESS#@@#未找到票号信息";
//				}
//			} catch (Exception e) {
//				sendCreateOrderInfo("error", "创单失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true",billNo,requestType);
//				logger.error("error", e);
//			}
//
//		} catch (Exception e) {
//			sendCreateOrderInfo("error", "创单失败", "", childrenUser, "", order_id, "", "", null, "", "", "false", "true",billNo,requestType);
//			logger.error("error", e);
//		} finally {
//			if (re != null) {
//				try {
//					re.close();
//				} catch (IOException e) {
//					logger.error("error", e);
//				}
//			}
//
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//					logger.error("error", e);
//				}
//			}
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
//	private Map<String, String> officialPay(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie,
//			String orderJson,String orderNo,String allPrice,String childrenUser,String order_id,String billNo,int requestType,
//			String cancelUrl,long nowDateTime) {
//		Map<String, String> map = null;
//		Map<String, String> payResultMap = new HashMap<String,String>();
//		try {
//			for (int i = 0; i < 5; i++) {
//				try {
//					map = cashiers(httpclient, defaultRequestConfig, cookie, orderNo, allPrice);
//					if (map == null) {
//						continue;
//					}
//					break;
//				} catch (Exception e) {
//					logger.error("error", e);
//				}
//				Thread.sleep(500);
//			}
//			if (map == null) {
//				sendCreateOrderInfo("error", "选择易宝支付失败", "", childrenUser, orderNo, order_id, "", "", null, "", "",
//						"false", "true", billNo, requestType);
//				payResultMap.put("stop", "选择易宝支付失败");
//				return payResultMap;
//			}
//			// 易宝支付
//			String locationValue = "";
//			try {
//				sendOrderStatus(childrenUser, order_id, "支付订单");
//				int index = 0;
//				for (; index < 3; index++) {
//					try {
//						locationValue = yeePay(httpclient, defaultRequestConfig, cookie, map, order_id);
//						break;
//					} catch (Exception e) {
//						logger.error("error", e);
//					}
//				}
//				if (index == 3) {
//					sendCreateOrderInfo("error", "获取支付重定向链接失败", "", childrenUser, orderNo, order_id, "", "", null, "",
//							"", "false", "true", billNo, requestType);
//					payResultMap.put("stop", "获取支付重定向链接失败");
//					return payResultMap;
//				}
//			} catch (Exception e) {
//				logger.error("error", e);
//				sendCreateOrderInfo("error", "获取支付重定向链接失败", "", childrenUser, orderNo, order_id, "", "", null, "", "",
//						"false", "true", billNo, requestType);
//				payResultMap.put("stop", "获取支付重定向链接失败");
//				return payResultMap;
//
//			}
//			if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//				sendOrderStatus(childrenUser, order_id, "已取消出票");
//				sendCreateOrderInfo("error", "已取消出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				logger.info("已取消出票");
//				payResultMap.put("stop", "已取消出票");
//				return payResultMap;
//			}
//			if (DateUtil.IsRunningTimeOut(nowDateTime, 15 * 1000 * 60)) {
//				sendOrderStatus(childrenUser, order_id, "出票超时，停止支付");
//				logger.info("出票超时，停止支付");
//				sendCreateOrderInfo("error", "创单超时，停止支付", "", childrenUser, "", order_id, "", "", null, "", "", "false",
//						"true", billNo, requestType);
//				payResultMap.put("stop", "创单超时，停止支付");
//				return payResultMap;
//			}
//			OfficialXykPayService pay = new OfficialXykPayService();
//			if (StringUtils.isNotEmpty(locationValue)) {
//				// payTransactionid = pay.payAllOrder(locationValue, cookie,
//				// orderJson, null);
//				payResultMap = pay.yeePayMFNew(locationValue, orderJson, cookie);
//				return payResultMap;
//			}
//		} catch (Exception e) {
//			logger.error("error",e);
//		}
//		return null;
//	}
//
//	private String parseOrderInfo(String orderInfo) {
//		String paytransId = "";
//		try {
//			JSONObject json = new JSONObject(orderInfo);
//			JSONObject result = json.getJSONObject("result");
//			JSONObject order = result.getJSONObject("order");
//			JSONObject orderItemList = order.getJSONArray("orderItemList").getJSONObject(0);
//			String statusCode = orderItemList.getString("statusCode");
//			if("NOT_ENTITLED".equals(statusCode)){
//				return statusCode;
//			}
//			JSONObject paymentList = order.getJSONArray("paymentList").getJSONObject(0);
//			JSONObject payMethodOnline = paymentList.getJSONObject("payMethodOnline");
//			paytransId = payMethodOnline.getString("paymentNo");
//			return paytransId;
//		} catch (Exception e) {
//			logger.error("error",e);
//		}
//		return paytransId;
//	}
//
//	private Map<String, String> searchOrder(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//			String cookie, String orderJson) throws Exception {
//		Map<String, String> resultMap = new HashMap<String, String>();
//		JSONObject json = new JSONObject(orderJson);
//		JSONArray flights = json.getJSONArray("flights");
//		JSONArray passengers = json.getJSONArray("passengers");
//		String fltno = flights.getJSONObject(0).getString("flightNo");
//		String system_price = flights.getJSONObject(0).getString("price");
//		String dep = flights.getJSONObject(0).getString("departure");
//		String arr = flights.getJSONObject(0).getString("arrival");
//		String depdate = flights.getJSONObject(0).getString("departureDate");
//		String system_segmentId = fltno + "-" + dep + "-" + arr + "-" + depdate;
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//		HttpPost post = new HttpPost("/api/iorder/combine/list/search");
//		String paramString = "{\"pageSize\":10,\"pageNum\":1,\"startCreateTime\":null,\"endCreateTime\":null,\"startFlightTime\":\""+depdate+"\",\"endFlightTime\":null,\"orgCity\":\""
//				+ dep + "\",\"dstCity\":\"" + arr
//				+ "\",\"orderNo\":null,\"pageIndex\":null,\"pageTotal\":null,\"orderListPageItemList\":null}";
//		post.setConfig(defaultRequestConfig);
//		StringEntity entity = new StringEntity(paramString, Charset.forName("UTF-8"));
//		post.setEntity(entity);
//		post.setHeader("Cookie", cookie);
//		post.setHeader("Host", "new.xiamenair.com");
//		post.setHeader("Content-Type", "application/json;charset=UTF-8");
//		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		CloseableHttpResponse response = httpclient.execute(target, post);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		if (StringUtils.isEmpty(back)) {
//			return null;
//		}
//		JSONObject backJson = new JSONObject(back);
//		JSONObject result = backJson.getJSONObject("result");
//		JSONArray orderListRespon = result.getJSONArray("orderListRespon");
//		for (int i = 0; i < orderListRespon.length(); i++) {
//			try {
//				JSONObject orderListResponObj = orderListRespon.getJSONObject(i);
//				JSONObject orderItem = orderListResponObj.getJSONObject("orderItem");
//				JSONObject order = orderItem.getJSONObject("order");
//				JSONObject orderItermList = order.getJSONArray("orderItemList").getJSONObject(0);
//				JSONObject price1 = orderItermList.getJSONObject("price");
//				JSONObject price = order.getJSONObject("price");
//				long baseAmount = price1.getLong("baseAmount");
//				if (Float.parseFloat(system_price) != baseAmount) {
//					continue;
//				}
//				JSONObject orderServiceList = orderItermList.getJSONArray("orderServiceList").getJSONObject(0);
//				String segmentId = orderServiceList.getString("segmentId");
//				if (!system_segmentId.equals(segmentId)) {
//					continue;
//				}
//				String bookingStatusCode = orderServiceList.getString("bookingStatusCode");
//				if (!"BOOKING_SUCCESS".equalsIgnoreCase(bookingStatusCode)) {
//					continue;
//				}
//				String statusCode = orderItermList.getString("statusCode");
//				if (!"NOT_ENTITLED".equalsIgnoreCase(statusCode)) {
//					continue;
//				}
//				String orderId = order.getString("orderId");
//				String orderInfo = "";
//				int index = 0;
//				for (; index < 3; index++) {
//					try {
//						orderInfo = getDetail(httpclient, defaultRequestConfig, cookie, orderId, 1);
//						if (StringUtils.isEmpty(orderInfo)) {
//							continue;
//						}
//						break;
//					} catch (Exception e) {
//						logger.error("error", e);
//					}
//				}
//				if (index == 3) {
//					return null;
//				}
//				if (StringUtils.isEmpty(orderInfo)) {
//					continue;
//				}
//				for (int j = 0; j < passengers.length(); j++) {
//					JSONObject passenger = passengers.getJSONObject(j);
//					String passengerName = passenger.getString("passengerName").replaceAll(" ", "").toUpperCase();
//					String documentNo = passenger.getString("idcard");
//					if (!orderInfo.contains(passengerName) || !orderInfo.contains(documentNo)) {
//						continue;
//					}
//				}
//				long totalAmount = price.getLong("totalAmount");
//				resultMap.put("orderId", orderId);
//				resultMap.put("totalPrice", totalAmount + "");
//				return resultMap;
//			} catch (Exception e) {
//				logger.error("error", e);
//			}
//		}
//		return null;
//	}
//
//	private boolean orderParam(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie,
//			String orderJson) throws Exception {
//		JSONObject json = new JSONObject(orderJson);
//		JSONArray passengers = json.getJSONArray("passengers");
//		String order_id = json.getString("id");
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//		HttpGet get = new HttpGet("/api/caches/order-param");
//		get.setConfig(defaultRequestConfig);
//		get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		get.setHeader("Host", "new.xiamenair.com");
//		get.setHeader("Cookie", cookie);
//		CloseableHttpResponse response = httpclient.execute(target, get);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		logger.info(order_id + "确认乘客返回结果："+back);
//		for (int i = 0; i < passengers.length(); i++) {
//			JSONObject jObject = passengers.getJSONObject(i);
//			String passengerName = jObject.getString("passengerName").replaceAll(" ", "").toUpperCase();
//			String documentNo = jObject.getString("idcard");
//			if(!back.contains(passengerName)){
//				return false;
//			}
//			if(!back.contains(documentNo)){
//				return false;
//			}
//		}
//		return true;
//	}
//
//	private boolean selectedOffer(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie,
//			String orderJson) throws Exception {
//		JSONObject json = new JSONObject(orderJson);
//		String order_id = json.getString("id");
//		JSONArray flights = json.getJSONArray("flights");
//		String fltno = flights.getJSONObject(0).getString("flightNo");
//		String system_price = flights.getJSONObject(0).getString("price");
//		String dep = flights.getJSONObject(0).getString("departure");
//		String arr = flights.getJSONObject(0).getString("arrival");
//		String depdate = flights.getJSONObject(0).getString("departureDate");
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//		HttpGet get = new HttpGet("/api/caches/selected-offer/get");
//		get.setConfig(defaultRequestConfig);
//		get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		get.setHeader("Host", "new.xiamenair.com");
//		get.setHeader("Cookie", cookie);
//		CloseableHttpResponse response = httpclient.execute(target, get);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		logger.info(order_id + "确认航班返回结果："+back);
//		if(StringUtils.isNotEmpty(back)&&(back.contains("Login or register please")||back.contains("RedisHgetNullException"))){
//			return false;
//		}
//		JSONObject backJson = new JSONObject(back);
//		JSONObject result = backJson.getJSONObject("result");
//		JSONObject searchParam = result.getJSONObject("searchParam");
//		JSONObject odList = searchParam.getJSONArray("odList").getJSONObject(0);
//		String dst = odList.getString("dst");
//		if(!arr.equals(dst)){
//			return false;
//		}
//		String org = odList.getString("org");
//		if(!dep.equals(org)){
//			return false;
//		}
//		String orgDate = odList.getString("orgDate");
//		if(!depdate.equals(orgDate)){
//			return false;
//		}
//		JSONObject selectedOdDetailList = result.getJSONArray("selectedOdDetailList").getJSONObject(0);
//		JSONObject adtItem = selectedOdDetailList.getJSONObject("adtItem");
//		JSONObject price = adtItem.getJSONObject("price");
//		float base = price.getLong("base");
//		if(Float.parseFloat(system_price)!=base){
//			return false;
//		}
//		if(!back.contains(fltno)){
//			return false;
//		}
//		return true;
//	}
//
//	private void addService(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig, String cookie) throws Exception {
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//
//		HttpGet get = new HttpGet("/api/iorder/add-service");
//		get.setConfig(defaultRequestConfig);
//		get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		get.setHeader("Host", "new.xiamenair.com");
//		get.setHeader("Cookie", cookie);
//		CloseableHttpResponse response = httpclient.execute(target, get);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//
//		String paramString = "{\"addServiceResult\":{\"selectedService\":{\"insurance\":[]}},\"addServiceResultFromWeb\":[0,0]}";
//		HttpPost post = new HttpPost("/api/caches/add-service");
//
//		post.setConfig(defaultRequestConfig);
//		StringEntity entity = new StringEntity(paramString,Charset.forName("UTF-8"));
//		post.setEntity(entity);
//		post.setHeader("Cookie",cookie);
//		post.setHeader("Host","new.xiamenair.com");
//		post.setHeader("Content-Type","application/json;charset=UTF-8");
//		post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		response = httpclient.execute(target,post);
//		back = EntityUtils.toString(response.getEntity(), "utf-8");
//	}
//
//	private boolean isLogined(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,String cookie) throws Exception {
//		try {
//			HttpGet get = new HttpGet("https://new.xiamenair.com/api/users/me?"+System.currentTimeMillis());
//			get.setConfig(defaultRequestConfig);
//			get.setHeader("Content-Type","application/json;charset=UTF-8");
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//			get.setHeader("Host", "new.xiamenair.com");
//			get.setHeader("Cookie", cookie);
//			CloseableHttpResponse response = httpclient.execute(get);
//			String result = EntityUtils.toString(response.getEntity(), "utf-8");
//			JSONObject resultJson = new JSONObject(result);
//			JSONObject reslutObj = resultJson.getJSONObject("result");
//			boolean isAuth = reslutObj.getBoolean("isAuth");
//			String userName = null;
//			try {
//				userName = reslutObj.getString("userName");
//			} catch (Exception e) {}
//			if(isAuth&&StringUtils.isNotEmpty(userName)){
//				return true;
//			}
//		} catch (Exception e) {
//			logger.error("error",e);
//		}
//		return false;
//	}
//
//	private String getCookieNewMethod(JSONObject json, CloseableHttpClient httpclient,
//			RequestConfig defaultRequestConfig, InputStream re) {
//		WebDriver webDriver = null;
//		String content = "";
//		try {
////			webDriver = PhantomjsDriverUtil.getProxyWebDriver("http-dyn.abuyun.com", "9020", "HL7F5JF125K85K8D",
////					"FC393F432489B2E5");
//			webDriver = PhantomjsDriverUtil.getWebDriver();
//			webDriver.manage().window().setSize(new Dimension(1920, 1080));
//			webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
//			content = loggin(webDriver, json, httpclient, defaultRequestConfig, re);
//		} catch (Exception e) {
//			logger.error("error", e);
//		} finally {
//			try {
//				if(webDriver!=null){
//					webDriver.quit();
//				}
//			} catch (Exception e2) {
//				// TODO: handle exception
//			}
//		}
//		try {
//			if(webDriver!=null){
//				webDriver.quit();
//			}
//		} catch (Exception e2) {
//			// TODO: handle exception
//		}
//		return content;
//	}
//
//	public String loggin(WebDriver webDriver, JSONObject json, CloseableHttpClient httpclient,
//			RequestConfig defaultRequestConfig, InputStream re) throws Exception {
//		final String fltno = json.getJSONArray("flights").getJSONObject(0).getString("flightNo");
//		String dep = json.getJSONArray("flights").getJSONObject(0).getString("departure");
//		String arr = json.getJSONArray("flights").getJSONObject(0).getString("arrival");
//		String depdate = json.getJSONArray("flights").getJSONObject(0).getString("departureDate");
//		String order_id = json.getString("id");
//		String childrenUser = json.getString("username");
//		String account = json.getString("account");
//		String otheraccount = json.getString("otheraccount");
//		String username = "b";
//		String password = "b";
//		if (otheraccount.contains("_") && otheraccount.split("_").length == 2) {
//			username = otheraccount.split("_")[0];
//			password = otheraccount.split("_")[1];
//		}
//		String userAccount[] = account.split("_");
//		try {
//			webDriver.get("https://new.xiamenair.com");
//			webDriver.findElement(By.xpath("/html/body/div/div[1]/div/div[2]/div[2]/span[1]")).click();
//			sendOrderStatus(childrenUser, order_id, "进入登录页面");
//			new WebDriverWait(webDriver, 5).until(ExpectedConditions.presenceOfElementLocated(By.className("account")));
//			WebElement accountEle = webDriver.findElement(By.className("account"));
//			accountEle.clear();
//			accountEle.sendKeys(userAccount[0]);
//			WebElement passwordEle = webDriver.findElement(By.className("password"));
//			passwordEle.clear();
//			passwordEle.sendKeys(userAccount[1]);
//			Set<org.openqa.selenium.Cookie> set = webDriver.manage().getCookies();
//			StringBuilder str = new StringBuilder();
//			for (org.openqa.selenium.Cookie cookie : set) {
//				str.append(cookie).append(";");
//			}
//			String random = "0." + (long) ((Math.random() + 1) * 10000000000000000L);
//
//			WebElement codeImgEle = webDriver.findElements(By.className("code-img")).get(0);
//			File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
//			BufferedImage fullImg = ImageIO.read(screenshot);
//			Point point = codeImgEle.getLocation();
//			int eleWidth = codeImgEle.getSize().getWidth();
//			int eleHeight = codeImgEle.getSize().getHeight();
//			BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
//			ImageIO.write(eleScreenshot, "png", screenshot);
//			String fileUri = "C://testImg//" + random + ".jpg";
//			File screenshotLocation = new File(fileUri);
//			FileUtils.copyFile(screenshot, screenshotLocation);
//
//			InputStream is = new FileInputStream(new File(fileUri));
//
//			String validtext = YunSu.getValidCode(is, "", username, password);
//			logger.info("调用接口获取验证码返回：" + validtext);
//			try {
//				if(screenshotLocation.exists()){
//					screenshotLocation.delete();
//				}
//			} catch (Exception e) {
//				logger.error("error",e);
//			}
//			org.dom4j.Document document = DocumentHelper.parseText(validtext);
//			org.dom4j.Element root = document.getRootElement();
//			String error = root.elementText("Error");
//			String result = "";
//			if (error != null && !"".equals(error)) {
//				result = error;
//				if (result.contains("点数不足")) {
//					return null;
//				}
//				if(result.contains("任务超时")){
//					return null;
//				}
//			} else {
//				result = root.elementText("Result");
//			}
//
//			WebElement codeEle = webDriver.findElement(By.className("code1"));
//			codeEle.clear();
//			codeEle.sendKeys(result);
//
//			JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
//			String js = "document.getElementById('login-btn-1').disabled=''";
//			javascriptExecutor.executeScript(js);
//
//			webDriver.findElement(By.id("privacy-checkbox-1")).click();
//
//			WebElement submitbutton = webDriver.findElement(By.id("login-btn-1"));
//
//			submitbutton.click();
//			(new WebDriverWait(webDriver, 20)).until(new ExpectedCondition<Boolean>() {
//				@Override
//				public Boolean apply(WebDriver webDriver) {
//					Set<org.openqa.selenium.Cookie> set = webDriver.manage().getCookies();
//					StringBuilder str = new StringBuilder();
//					for (org.openqa.selenium.Cookie cookie : set) {
//						str.append(cookie).append(";");
//					}
//					if (StringUtils.isNotEmpty(str.toString()) && str.toString().contains("JSESSIONID")) {
//						return true;
//					}
//					return false;
//				}
//			});
//			webDriver.get("https://new.xiamenair.com/nticket.html?tripType=OW&orgCode=" + dep + "&dstCode=" + arr + "&orgCityName=??&dstCityName=??&orgDate=" + depdate	+ "&dstDate=&isInter=false&orgIsintl=0&dstIsintl=0&adtNum=1&chdNum=0&JFCabinFirst=false");
//			(new WebDriverWait(webDriver, 7)).until(new ExpectedCondition<Boolean>() {
//				@Override
//				public Boolean apply(WebDriver webDriver) {
//					Set<org.openqa.selenium.Cookie> set = webDriver.manage().getCookies();
//					StringBuilder str = new StringBuilder();
//					for (org.openqa.selenium.Cookie cookie : set) {
//						str.append(cookie).append(";");
//					}
//					if (StringUtils.isNotEmpty(str.toString()) && str.toString().contains("JSESSIONID")
//							&& webDriver.getPageSource().contains(fltno)) {
//						return true;
//					}
//					return false;
//				}
//			});
//			set = webDriver.manage().getCookies();
//			str = new StringBuilder();
//			for (org.openqa.selenium.Cookie cookie : set) {
//				str.append(cookie).append(";");
//			}
//			if (StringUtils.isNotEmpty(str.toString()) && str.toString().contains("JSESSIONID")) {
//				return str.toString();
//			}
//		}catch (Exception e) {
//			logger.error("error",e);
//		}finally {
//			if(re != null) {
//				try {
//					re.close();
//				} catch (IOException e) {
//					logger.error("error",e);
//				}
//			}
//		}
//      return null;
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
//			logger.error("error",e);
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
//				logger.error(e);
//			}
//		}
//		return result;
//	}
//
//	private String getDetail(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderNo, int type) throws Exception {
//		StringBuffer result = new StringBuffer();
//		HttpGet get = new HttpGet("/api/iorder/" + orderNo + "/detail");
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//		get.setConfig(config);
//		get.setHeader("Cookie", cookie);
//		get.setHeader("Host", "new.xiamenair.com");
//		get.setHeader("Referer", "https://new.xiamenair.com/confirm.html");
//		get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		CloseableHttpResponse response = httpclient.execute(target, get);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		if(type==0){
//			Map<String, String> mapResult = parseDetailResult(back);
//			if (mapResult.size() == 0) {
//				return null;
//			}
//			for (String value : mapResult.values()) {
//				result.append(value).append("#_#");
//			}
////			logger.info(result.toString());
//			return result.toString() + "@_@";
//		}else {
//			return back;
//		}
//	}
//
//	private Map<String, String> parseDetailResult(String back) {
//
//		Map<String, String> map = new HashMap<String, String>();
//		Map<String, String> passengerMap = new HashMap<String, String>();
//		String pnr = "";
//		try {
//			JSONObject backObj = new JSONObject(back);
//			JSONObject resultObj = backObj.getJSONObject("result");
//			String bankTradeNo = "";
//			try {
//				JSONObject orderRefData = resultObj.getJSONObject("orderRefData");
//				JSONObject paxPnrList = orderRefData.getJSONArray("paxPnrList").getJSONObject(0);
//				pnr = paxPnrList.getString("pnr");
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			try {
//				JSONObject order = resultObj.getJSONObject("order");
//				JSONObject paymentList = order.getJSONArray("paymentList").getJSONObject(0);
//				JSONObject payMethodOnline = paymentList.getJSONObject("payMethodOnline");
//				bankTradeNo = payMethodOnline.getString("paymentNo");
//			} catch (Exception e) {
//				logger.error("error",e);
//			}
//			JSONObject orderRefData = resultObj.getJSONObject("orderRefData");
//			JSONArray passengerListArr = orderRefData.getJSONArray("passengerList");
//			for(int i=0;i<passengerListArr.length();i++){
//				JSONObject passengerList = passengerListArr.getJSONObject(i);
//				String id = passengerList.getString("id");
//				JSONObject identityDocuments = passengerList.getJSONArray("identityDocuments").getJSONObject(0);
//				String name = identityDocuments.getString("name");
//				String identityDocId = identityDocuments.getString("identityDocId");
//				passengerMap.put(id,name+"##"+identityDocId);
//			}
//			JSONArray passengersJourneysServicesArr = resultObj.getJSONArray("passengersJourneysServices");
//			for(int i=0;i<passengersJourneysServicesArr.length();i++){
//				JSONObject passengersJourneysServices = passengersJourneysServicesArr.getJSONArray(i).getJSONObject(0);
//				JSONObject ticketDocument = null;
//				try {
//					ticketDocument = passengersJourneysServices.getJSONObject("ticketDocument");
//				} catch (Exception e) {}
//				if(ticketDocument==null){
//					continue;
//				}
//				String passengerId = ticketDocument.getString("paxIdRef");
//				String ticketDocNbr = ticketDocument.getString("ticketDocNbr");
//				if (StringUtils.isNotEmpty(ticketDocNbr)) {
//					try {
//						String value = passengerMap.get(passengerId);
//						if(StringUtils.isEmpty(value)){
//							continue;
//						}
//						String[] info = value.split("##");
////						map.put(info[0],
////								info[0].replace("/", "") + "##" + info[1] + "##" + ticketDocNbr + "##" + bankTradeNo);
//						map.put(info[0],
//								info[0].replace("/", "") + "##" + info[1] + "##" + ticketDocNbr + "##" + bankTradeNo + "##" + pnr);
//					} catch (Exception e) {
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.info(back);
//		}
//		return map;
//	}
//
//	private String yeePay(CloseableHttpClient httpclient, RequestConfig config, String cookie, Map<String, String> map, String order_id) throws Exception {
//		// 第一个请求
//		HttpPost post = new HttpPost("/xmair-trade-app/rest/v1/pay/submitPay");
//
//		List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//		nameValue.clear();
//		nameValue.add(new BasicNameValuePair("amount", map.get("amount")));
//		nameValue.add(new BasicNameValuePair("appId", map.get("appId")));
//		nameValue.add(new BasicNameValuePair("bankCode", map.get("bankCode")));
//		nameValue.add(new BasicNameValuePair("cardType", map.get("cardType")));
//		nameValue.add(new BasicNameValuePair("channelCode", "YEEPAY"));
//		nameValue.add(new BasicNameValuePair("channelId", "4"));
//		nameValue.add(new BasicNameValuePair("channelType", "NCPC"));
//		nameValue.add(new BasicNameValuePair("insuranceAmt", map.get("insuranceAmt")));
//		nameValue.add(new BasicNameValuePair("locale", map.get("locale")));
//		nameValue.add(new BasicNameValuePair("orderExpdate", map.get("orderExpdate")));
//		nameValue.add(new BasicNameValuePair("orderId", map.get("orderId")));
//		nameValue.add(new BasicNameValuePair("payOrderNo", map.get("payOrderNo")));
//		nameValue.add(new BasicNameValuePair("payType", "THIRD_PAY"));
//		nameValue.add(new BasicNameValuePair("returnUrl", map.get("returnUrl")));
//		nameValue.add(new BasicNameValuePair("sign", map.get("sign")));
//		nameValue.add(new BasicNameValuePair("style", map.get("style")));
//		post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//		if (StringUtils.isNotEmpty(map.get("cookie"))) {
//			cookie = map.get("cookie");
//		}
//		HttpHost target = new HttpHost("pay.xiamenair.com", 443, "https");
//		post.setConfig(config);
//		post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//		post.setHeader("Cookie", cookie);
//		post.setHeader("Host", "pay.xiamenair.com");
//		post.setHeader("Referer", "https://pay.xiamenair.com/xmair-trade-app/rest/v1/pay/initCashiers");
//		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		CloseableHttpResponse response = httpclient.execute(target, post);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		Header[] location = response.getHeaders("Location");
//		String locationValue = "";
//		for (int i = 0; i < location.length; i++) {
//
//			locationValue = location[i].getValue();
//			logger.info(order_id + "Location:" + locationValue);
//		}
//		// 第二个请求
//		if(StringUtils.isEmpty(locationValue)){
//			logger.info(order_id + "支付请求："+nameValue.toString());
//			logger.info(order_id + "支付返回："+back);
//			return "";
//		}
//		config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
//				.setConnectionRequestTimeout(timeout).setRedirectsEnabled(false).build();
//		HttpGet get = new HttpGet(locationValue.substring(25));
//		get.setConfig(config);
//		get.setHeader("Cookie", cookie);
//		get.setHeader("Host", "pay.xiamenair.com");
//		get.setHeader("Referer", "https://pay.xiamenair.com/xmair-trade-app/rest/v1/pay/initCashiers");
//		get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		response = httpclient.execute(target, get);
//		back = EntityUtils.toString(response.getEntity(), "utf-8");
//		location = response.getHeaders("Location");
//		locationValue = "";
//		for (int i = 0; i < location.length; i++) {
//			locationValue = location[i].getValue();
//			logger.info("resultLocation:" + locationValue);
//		}
//		return locationValue;
//	}
//
//	private Map<String, String> cashiers(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderNo,String allPrice) throws Exception {
//		String cashiersJson = "{\"orderId\":\""+orderNo+"\",\"totalAmount\":"+allPrice+",\"currency\":\"CNY\",\"productName\":\"厦门航空\",\"payMethod\":\"PC_CASHIER\",\"description\":\"\",\"paymentData\":{\"payAmount\":"+allPrice+",\"plat\":\"PCWEB\",\"returnUrl\":\"https://new.xiamenair.com/api/ipay/ticket/res\",\"productUrl\":\"https://new.xiamenair.com:/nodetails.html?orderNo="+orderNo+"\",\"openId\":\"\"}}";
//		HttpPost post = new HttpPost("/api/ipay/cashiers");
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//		post.setConfig(config);
//		StringEntity entity = new StringEntity(cashiersJson,Charset.forName("UTF-8"));
//		post.setEntity(entity);
//		post.setHeader("Cookie",cookie);
//		post.setHeader("Host","new.xiamenair.com");
//		post.setHeader("Referer","https://new.xiamenair.com/confirm-state.html?orderNo="+orderNo+"");
//		post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		CloseableHttpResponse response = httpclient.execute(target,post);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		logger.info("orderNo:"+orderNo+",返回："+back);
//		Map<String, String> resultMap = parseCashiersResult(back);
//
//		// 支付订单初始化
//		Map<String,String> map = initCashiers(httpclient, config,cookie, resultMap, orderNo);
//		return map;
//	}
//
//	private Map<String,String> initCashiers(CloseableHttpClient httpclient, RequestConfig config,
//			String cookie, Map<String, String> resultMap, String orderNo) throws Exception {
//		Map<String,String> map = new HashMap<String,String>();
//		HttpPost post = new HttpPost("/xmair-trade-app/rest/v1/pay/initCashiers");
//		HttpHost target = new HttpHost("pay.xiamenair.com", 443, "https");
//		List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//		for (Map.Entry<String, String> entry : resultMap.entrySet()) {
//			nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//		}
//		post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//		post.setConfig(config);
//		post.setHeader("Cookie", cookie);
//		post.setHeader("Host", "pay.xiamenair.com");
//		post.setHeader("Referer", "https://new.xiamenair.com/confirm-state.html?orderNo=" + orderNo + "");
//		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		CloseableHttpResponse response = httpclient.execute(target, post);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		map = parseInitCashiersResult(back);
//		Header[] headersArr = response.getAllHeaders();
//		String newCookie = "";
//		if (StringUtils.isNotEmpty(cookie) && cookie.contains(";")) {
//			String[] cs = cookie.split(";");
//			for (String co : cs) {
//				if (co.contains("JSESSIONID") || co.contains("path")) {
//					continue;
//				}
//				newCookie = co + ";";
//			}
//		}
//		for (Header header : headersArr) {
//			if ("Set-Cookie".equals(header.getName())) {
//				newCookie += header.getValue() + ";";
//			}
//		}
//		cookie = newCookie;
//		map.put("cookie", cookie);
//		return map;
//	}
//
//	private Map<String, String> parseInitCashiersResult(String back) throws Exception {
//		Document doc = Jsoup.parse(back);
//		String amount = doc.getElementById("amount").val();
//		String insuranceAmt = doc.getElementById("insuranceAmt").val();
//		String appId = doc.getElementById("appId").val();
//		String orderId = doc.getElementById("orderId").val();
//		String payOrderNo = doc.getElementById("payOrderNo").val();
//		String channelId = doc.getElementById("channelId").val();
//		String sign = doc.getElementById("sign").val();
//		String channelCode = doc.getElementById("channelCode").val();
//		String channelType = doc.getElementById("channelType").val();
//		String payType = doc.getElementById("payType").val();
//		String bankCode = doc.getElementById("bankCode").val();
//		String cardType = doc.getElementById("cardType").val();
//		String locale = doc.getElementById("locale").val();
//		String orderExpdate = doc.getElementById("orderExpdate").val();
//		String returnUrl = doc.getElementById("returnUrl").val();
//		String style = doc.getElementById("style").val();
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("amount", amount);
//		map.put("insuranceAmt", insuranceAmt);
//		map.put("appId", appId);
//		map.put("orderId", orderId);
//		map.put("payOrderNo", payOrderNo);
//		map.put("channelId", channelId);
//		map.put("sign", sign);
//		map.put("channelCode", channelCode);
//		map.put("channelType", channelType);
//		map.put("payType", payType);
//		map.put("bankCode", bankCode);
//		map.put("cardType", cardType);
//		map.put("locale", locale);
//		map.put("orderExpdate", orderExpdate);
//		map.put("returnUrl", returnUrl);
//		map.put("style", style);
//		return map;
//	}
//
//	private Map<String, String> parseCashiersResult(String back) throws Exception {
//		JSONObject backObj = new JSONObject(back);
//		JSONObject resultObj = backObj.getJSONObject("result");
//		JSONObject returnData = resultObj.getJSONObject("returnData");
//		JSONObject dataMap = returnData.getJSONObject("dataMap");
//		String amount = dataMap.getString("amount")==null?"":dataMap.getString("amount");
//		String payOrderNo = dataMap.getString("payOrderNo")==null?"":dataMap.getString("payOrderNo");
//		String orderId = dataMap.getString("orderId")==null?"":dataMap.getString("orderId");
//		String sign = dataMap.getString("sign")==null?"":dataMap.getString("sign");
//		String productName = dataMap.getString("productName")==null?"":dataMap.getString("productName");
//		String appId = dataMap.getString("appId")==null?"":dataMap.getString("appId");
//		String currency = dataMap.getString("currency")==null?"":dataMap.getString("currency");
//		String orderExpdate = dataMap.getString("orderExpdate")==null?"":dataMap.getString("orderExpdate");
//		String priskParam = dataMap.getString("priskParam")==null?"":dataMap.getString("priskParam");
//		String returnUrl = dataMap.getString("returnUrl")==null?"":dataMap.getString("returnUrl");
//		String method = dataMap.getString("method")==null?"":dataMap.getString("method");
//		String format = dataMap.getString("format")==null?"":dataMap.getString("format");
//		String v = dataMap.getString("v")==null?"":dataMap.getString("v");
//		String identityId = dataMap.getString("identityId")==null?"":dataMap.getString("identityId");
//		String notifyUrl = dataMap.getString("notifyUrl")==null?"":dataMap.getString("notifyUrl");
//		String productUrl = dataMap.getString("productUrl")==null?"":dataMap.getString("productUrl");
//		String locale = dataMap.getString("locale")==null?"":dataMap.getString("locale");
//		String ts = dataMap.getString("ts")==null?"":dataMap.getString("ts");
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("amount", amount);
//		map.put("payOrderNo", payOrderNo);
//		map.put("orderId", orderId);
//		map.put("sign", sign);
//		map.put("productName", productName);
//		map.put("appId", appId);
//		map.put("currency", currency);
//		map.put("orderExpdate", orderExpdate);
//		map.put("priskParam", priskParam);
//		map.put("returnUrl", returnUrl);
//		map.put("method", method);
//		map.put("format", format);
//		map.put("v", v);
//		map.put("identityId", identityId);
//		map.put("notifyUrl", notifyUrl);
//		map.put("productUrl", productUrl);
//		map.put("locale", locale);
//		map.put("ts", ts);
//		return map;
//	}
//
//	private Map<String, String> confirmOrder(CloseableHttpClient httpclient, RequestConfig config,
//			BasicCookieStore cookieStore, String cookie, String resultString, Map<String, Object> mapResult,
//			String orderJson, int createCount) throws Exception {
//		JSONObject json = new JSONObject(orderJson);
//		String orderid = json.getString("id");
//		Map<String, String> resultMap = new HashMap<String, String>();
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//		HttpPost post = new HttpPost("/api/iorder/save");
//
//		post.setConfig(config);
//		StringEntity entity = new StringEntity("{}", Charset.forName("UTF-8"));
//		post.setEntity(entity);
//		post.setHeader("Cookie", cookie);
//		post.setHeader("Host", "new.xiamenair.com");
//		post.setHeader("Content-Type", "application/json;charset=UTF-8");
//		post.setHeader("Referer", "https://new.xiamenair.com/confirm.html");
//		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		CloseableHttpResponse response = httpclient.execute(target, post);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		logger.info(orderid + "--创建订单返回结果：" + back);
//		if(StringUtils.isNotEmpty(back)&&back.contains("field-PASSENGER_INFO is null")){
//			logger.info(orderid + "--创建订单返回重试结果");
//			resultMap.put("msg", "生单结果异常，请确认是否创单成功");
//			return resultMap;
//		}
//		JSONObject backObj = new JSONObject(back);
//		String msg = backObj.getString("msg");
//		float totalAmount = 0;
//		createCount++;
//		if (StringUtils.isNotEmpty(msg) && "SUCCESS".equals(msg)) {
//			JSONObject resultObj = backObj.getJSONObject("result");
//			JSONObject orderObj = resultObj.getJSONObject("order");
//			String orderId = orderObj.getString("orderId");
//			resultMap.put("orderId", orderId);
//			totalAmount = orderObj.getJSONObject("price").getLong("totalAmount");
//		} else if (msg.contains("订单创建失败") || msg.contains("调用规则引擎接口失败") || msg.contains("pnr封口异常")
//				|| msg.contains("com.github.fge.jsonschema.core.report.ListProcessingReport")) {
//			// 加入重试机制，最多重试三次
//			if (createCount < 3) {
//				logger.info(orderid + "--" + msg + "--|重试创单");
//				return confirmOrder(httpclient, config, cookieStore, cookie, resultString, mapResult, orderJson,
//						createCount);
//			}
//			logger.info(orderid + "--" + msg);
//			resultMap.put("msg", msg);
//		} else if (msg.contains("repeat order")) {
//			if (createCount < 3) {
//				logger.info(orderid + "--" + msg + "--|重试创单");
//				return confirmOrder(httpclient, config, cookieStore, cookie, resultString, mapResult, orderJson,
//						createCount);
//			}
//			logger.info(orderid + "--" + msg);
//			resultMap.put("msg", "重新创单失败，官网返回repeat order，请重新出票或转人工出票");
//		} else {
//			logger.info(orderid + "--" + msg);
//			resultMap.put("msg", msg);
//		}
//		resultMap.put("totalPrice", ((int) (totalAmount)) + "");
//		return resultMap;
//	}
//
//	private Map<String, String> createConfirmJson(String resultString, Map<String, Object> mapResult, String orderJson)
//			throws Exception {
//		int amount = (Integer) (mapResult.get("amount") == null ? 0 : mapResult.get("amount")); // 成人总价
//		int chdAmount = (Integer) (mapResult.get("chdAmount") == null ? 0 : mapResult.get("chdAmount")); // 儿童总价
//		int airportTax = (Integer) (mapResult.get("airportTax") == null ? 0 : mapResult.get("airportTax")); // 成人机建费单价
//		int chdAirportTax = (Integer) (mapResult.get("chdAirportTax") == null ? 0 : mapResult.get("chdAirportTax")); // 儿童机建费单价
//		int fuelTax = (Integer) (mapResult.get("fuelTax") == null ? 0 : mapResult.get("fuelTax")); // 成人燃油费单价
//		int chdFuelTax = (Integer) (mapResult.get("chdFuelTax") == null ? 0 : mapResult.get("chdFuelTax")); // 儿童燃油费单价
//		int accidentInsurancePrice = (Integer) (mapResult.get("accidentInsurancePrice") == null ? 0
//				: mapResult.get("accidentInsurancePrice")); // 航空意外保险费单价
//		int delayInsurancePrice = (Integer) (mapResult.get("delayInsurancePrice") == null ? 0
//				: mapResult.get("delayInsurancePrice")); // 航空延误保险费单价
//		int totalPrice = 0;
//		int adultNum = 0;
//		int chdNum = 0;
//		JSONObject json = new JSONObject(orderJson);
//		JSONArray passengers = json.getJSONArray("passengers");
//		for (int i = 0; i < passengers.length(); i++) {
//			JSONObject jObject = passengers.getJSONObject(i);
//			String passengetType = jObject.getString("passengerType");
//			if ("成人".equals(passengetType)) {
//				totalPrice = totalPrice + amount + airportTax + fuelTax;
//				adultNum++;
//			} else if ("儿童".equals(passengetType)) {
//				totalPrice = totalPrice + chdAmount + chdAirportTax + chdFuelTax;
//				chdNum++;
//			}
//		}
//		String priceInfo = "\"priceInfo\":{\"totalPrice\":" + totalPrice + ",\"adultGoFlightPrice\":" + amount
//				+ ",\"adultGoFlightNum\":" + adultNum + ","
//				+ "\"adultBackFlightPrice\":0,\"adultBackFlightNum\":0,\"kidGoFlightPrice\":" + chdAmount
//				+ ",\"kidGoFlightNum\":" + chdNum + ","
//				+ "\"kidBackFlightPrice\":0,\"kidBackFlightNum\":0,\"adultConsFee\":" + airportTax
//				+ ",\"adultConsNum\":" + adultNum + ",\"kidConsFee\":" + chdAirportTax + "," + "\"kidConsNum\":"
//				+ chdNum + ",\"adultFuelTax\":" + fuelTax + ",\"adultFuelNum\":" + adultNum + ",\"kidFuelTax\":"
//				+ chdFuelTax + ",\"kidFuelNum\":" + chdNum + "," + "\"accidentInsurancePrice\":"
//				+ accidentInsurancePrice + ",\"accidentInsuranceNum\":0,\"delayInsurancePrice\":" + delayInsurancePrice
//				+ ",\"delayInsuranceNum\":0,"
//				+ "\"baggageFeeTotalPrice\":0,\"couponPriceTotalPrice\":0,\"postFee\":0,\"kidBuyAdultGoFlightNum\":0,\"kidBuyAdultBackFlightNum\":0},\"opportunities\"";
//		resultString = resultString.replace("\"opportunities\"", priceInfo);
//		Map<String, String> resultMap = new HashMap<String, String>();
//		resultMap.put("resultString", resultString);
//		resultMap.put("totalPrice", totalPrice + "");
//		return resultMap;
//	}
//
//	private String createJson(CloseableHttpClient httpclient, RequestConfig defaultRequestConfig,
//			BasicCookieStore cookieStore, String cookie, String orderJson, String url, String depdate) throws Exception {
//		StringBuffer passengerList = new StringBuffer();
//		StringBuffer contactInfo = new StringBuffer();
//
//		JSONObject json = new JSONObject(orderJson);
//		String linkMan = json.getString("linkMan");
//		String mobile = json.getString("mobile");
//		JSONArray passengers = json.getJSONArray("passengers");
//		StringBuffer passengerBuf = new StringBuffer();
//		String childPriceTypeD = "Y/J/F";
//		for (int i = 0; i < passengers.length(); i++) {
//			JSONObject jObject = passengers.getJSONObject(i);
//			String passengerName = jObject.getString("passengerName").replaceAll(" ", "").toUpperCase();
//			String passengetType = jObject.getString("passengerType");
//			String documentNo = jObject.getString("idcard");
//			String selectDocument = "99"; // 证件类型 默认身份证
//			String idType = jObject.getString("passengerCardType");
//			if ("护照".equals(idType)) {
//				selectDocument = "02";
//			}else if("身份证".equals(idType)){
//				selectDocument = "01";
//			}
//
//			String birthday = jObject.getString("birthday");
//			if (StringUtils.isNotEmpty(birthday) && !"null".equals(birthday)) {
//				birthday = birthday.substring(0, 10);
//			}
//			String passengerType = "A";
//			if ("成人".equals(passengetType)) {
//				passengerType = "A";
//			}
//			if ("儿童".equals(passengetType)) {
//				passengerType = "C";
//				childPriceTypeD = "ADTCABIN";
//			}
//			if("99".equals(selectDocument)){
//				passengerBuf.append("{\"anonymousInd\":false,\"name\":\"" + passengerName + "\",\"passengerType\":\""
//						+ passengerType + "\",\"birthDate\":null,\"memberships\":null,"
//						+ "\"identityDocuments\":[{\"name\":\"" + passengerName
//						+ "\",\"identityDocType\":\"" + selectDocument + "\",\"identityDocId\":\"" + documentNo
//						+ "\"}],\"contactInfo\":{\"phone\":\"" + mobile + "\"}},");
//			}else {
//				passengerBuf.append("{\"anonymousInd\":false,\"name\":\"" + passengerName + "\",\"passengerType\":\""
//						+ passengerType + "\",\"birthDate\":\"" + birthday
//						+ "\",\"memberships\":null,\"identityDocuments\":[{\"name\":\"" + passengerName
//						+ "\",\"identityDocType\":\"" + selectDocument + "\",\"identityDocId\":\"" + documentNo
//						+ "\"}],\"contactInfo\":{\"phone\":\"" + mobile + "\"}},");
//			}
//		}
//		if (passengerBuf != null && passengerBuf.length() > 0) {
//			passengerBuf.delete(passengerBuf.length() - 1, passengerBuf.length());
//		}
//		passengerList.append("[").append(passengerBuf).append("]");
//		contactInfo.append("{\"name\":\"" + linkMan + "\",\"phone\":\"" + mobile + "\",\"email\":\"\"}");
//		String paramString = "{\"dOrI\":\"d\",\"passengerList\":" + passengerList.toString() + ",\"orderContactInfo\":"
//				+ contactInfo.toString() + ",\"childPriceTypeD\":\"" + childPriceTypeD
//				+ "\",\"isUSA\":false,\"firstRouteStartTime\":\"" + depdate + "\",\"firstRouteArriveTime\":\"" + depdate
//				+ "\"}";
//
//		HttpPost post = new HttpPost("/api/caches/order-param");
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//		post.setConfig(defaultRequestConfig);
//		StringEntity entity = new StringEntity(paramString, Charset.forName("UTF-8"));
//		logger.info("添加乘客参数:"+paramString);
//		post.setEntity(entity);
//		post.setHeader("Cookie", cookie);
//		post.setHeader("Host", "new.xiamenair.com");
//		post.setHeader("Referer", url);
//		post.setHeader("Content-Type", "application/json;charset=UTF-8");
//		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		CloseableHttpResponse response = httpclient.execute(target, post);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		return null;}
//
//	private String cancelNullParam(String resultString) {
//		resultString = resultString.replaceAll("\"flightStopInfo\":null,", "").replaceAll("\"flightStopInfo\":null",
//				"");
//		resultString = resultString.replaceAll("\"stopQuantity\":null,", "").replaceAll("\"stopQuantity\":null", "");
//		resultString = resultString.replaceAll("\"stopDuration\":null,", "").replaceAll("\"stopDuration\":null", "");
//		resultString = resultString.replaceAll("\"productDesc\":null,", "").replaceAll("\"productDesc\":null", "");
//		resultString = resultString.replaceAll("\"refundDesc\":null,", "").replaceAll("\"refundDesc\":null", "");
//		resultString = resultString.replaceAll("\"restNum\":null,", "").replaceAll("\"restNum\":null", "");
//		return resultString;
//	}
//
//	private Map<String, Object> flights(CloseableHttpClient httpclient, RequestConfig config,
//			BasicCookieStore cookieStore, String cookie, String resultString, String url) throws Exception {
//		HttpPost post = new HttpPost("/api/caches/selected-offer/post");
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//		post.setConfig(config);
//		StringEntity entity = new StringEntity(resultString,Charset.forName("UTF-8"));
//		post.setEntity(entity);
//		post.setHeader("Cookie",cookie);
//		post.setHeader("Host","new.xiamenair.com");
//		post.setHeader("Referer",url);
//		post.setHeader("Content-Type","application/json;charset=UTF-8");
//		post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		CloseableHttpResponse response = httpclient.execute(target,post);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		return null;
//	}
//
//	private Map<String, Object> parseFlightResult(String back) throws Exception {
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		JSONObject backObj = new JSONObject(back);
//		JSONObject resultObj = backObj.getJSONObject("result");
//		JSONObject selectedFlights = resultObj.getJSONObject("selectedFlights");
//		JSONObject flightListObj = null;
//		JSONArray flightListArr = selectedFlights.getJSONArray("flightList");
//		for (int i = 0; i < flightListArr.length(); i++) {
//			flightListObj = flightListArr.getJSONObject(i);
//			JSONObject cabinObj = flightListObj.getJSONObject("cabin");
//			JSONObject productListObj = null;
//
//			try {
//				int amount = cabinObj.getInt("amount"); // 成人总价
//				int chdAmount = cabinObj.getInt("chdAmount"); // 儿童总价
//				int airportTax = cabinObj.getInt("airportTax"); // 成人机建费单价
//				int chdAirportTax = cabinObj.getInt("chdAirportTax"); // 儿童机建费单价
//				int fuelTax = cabinObj.getInt("fuelTax"); // 成人燃油费单价
//				int chdFuelTax = cabinObj.getInt("chdFuelTax"); // 儿童燃油费单价
//				resultMap.put("amount", amount);
//				resultMap.put("chdAmount", chdAmount);
//				resultMap.put("airportTax", airportTax);
//				resultMap.put("chdAirportTax", chdAirportTax);
//				resultMap.put("fuelTax", fuelTax);
//				resultMap.put("chdFuelTax", chdFuelTax);
//			} catch (Exception e) {
//			}
//			try {
//				JSONArray productListArr = cabinObj.getJSONArray("productList");
//				if (productListArr != null) {
//					for (int j = 0; j < productListArr.length(); j++) {
//						productListObj = productListArr.getJSONObject(j);
//						String productCode = productListObj.getString("productCode");
//						String productName = productListObj.getString("productName");
//						if ("航空延误险".equals(productName)) {
//							int delayInsurancePrice = productListObj.getInt("price"); // 航空延误保险费单价
//							resultMap.put("延误险", productCode);
//							resultMap.put("delayInsurancePrice", delayInsurancePrice);
//						}
//						if ("航空意外险".equals(productName)) {
//							int accidentInsurancePrice = productListObj.getInt("price"); // 航空意外保险费单价
//							resultMap.put("意外险", productCode);
//							resultMap.put("accidentInsurancePrice", accidentInsurancePrice);
//						}
//					}
//				}
//			} catch (Exception e) {
//			}
//		}
//		return resultMap;
//	}
//
//	private String findFlights(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore,
//			String cookie, JSONObject json) throws Exception {
//		String dep = json.getJSONArray("flights").getJSONObject(0).getString("departure");
//		String arr = json.getJSONArray("flights").getJSONObject(0).getString("arrival");
//		String depCN = PropertiesUtils.getPropertiesValue("allcity", dep);
//		String arrCN = PropertiesUtils.getPropertiesValue("allcity", arr);
//		String depdate = json.getJSONArray("flights").getJSONObject(0).getString("departureDate");
//		String url = "/nticket.html?tripType=OW&orgCode="+dep+"&dstCode="+arr+"&orgCityName="+depCN+"&dstCityName="+arrCN+"orgDate="+depdate+"&dstDate=&isInter=false&orgIsintl=0&dstIsintl=0&adtNum=1&chdNum=0&JFCabinFirst=false";
//		logger.info(url);
//		HttpHost target = new HttpHost("new.xiamenair.com", 443, "https");
//		HttpPost post = new HttpPost("/api/offers");
//
//		post.setConfig(config);
//		String jsonObject = "{\"ecip\":{\"shoppingPreference\":{\"connectionPreference\":{\"maxConnectionQuantity\":2},\"flightPreference\":{\"cabinCombineMode\":\"Cabin\",\"lowestFare\":false}},\"cabinClasses\":[\"Economy\",\"Business\",\"First\"],\"passengerCount\":{\"adult\":1,\"child\":1,\"infant\":0},\"itineraries\":[{\"departureDate\":\""+depdate+"\",\"origin\":{\"airport\":{\"code\":\""+dep+"\"}},\"destination\":{\"airport\":{\"code\":\""+arr+"\"}}}]},\"jfCabinFirst\":false,\"dOrI\":\"D\"}";
//		StringEntity entity = new StringEntity(jsonObject.toString(),Charset.forName("UTF-8"));
//		post.setEntity(entity);
//		post.setHeader("Referer","https://new.xiamenair.com"+url.replace("\"", "%22").replace("{", "%7b").replace("}", "%7d").replace(" ", "%20"));
//		post.setHeader("Cookie",cookie);
//		post.setHeader("Host","new.xiamenair.com");
//		post.setHeader("Content-Type","application/json;charset=UTF-8");
//		post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//		CloseableHttpResponse response = httpclient.execute(target,post);
//		String back = EntityUtils.toString(response.getEntity(), "utf-8");
//		return back;
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
//*
//	 * 推送创单情况 String childrenUser = request.getParameter("childrenUser");//子帐号
//	 * String orderId = request.getParameter("orderId"); //原订单主键ID String
//	 * payStatus = request.getParameter("payStatus"); //获取支付方式
//
//
//	public static String sendOrderStatus(String childrenUser, String orderId, String status) {
//		try {
//			String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrlStatus");
//
//			StringBuffer buffer = new StringBuffer();
//			buffer.append("<feeye-official>");
//			buffer.append("<official>" + Constant.MF.toString() + "</official> ");
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
//	public static void main(String[] args) {
//		MFCreateOrderService.sendCreateOrderInfo("success", "", "510", "policytest", "b2b20190213655072", "40420", "", "true", "高雅杰##150202199612311529##299-2315526265##150202199612311529##SF0ESA#_#", "积分支付", "7E8C51D820DD3BF2E053F862010AD7D0", "false", "true", "", 0);
//	}
//*
//	 * 推送创单情况 String result = request.getParameter("result"); //是否创单成功 String
//	 * message = request.getParameter("message"); //失败消息 String price =
//	 * request.getParameter("price"); //采购总金额 String childrenUser =
//	 * request.getParameter("childrenUser");//子帐号 String newOrderId =
//	 * request.getParameter("newOrderId"); //创建订单成功后的官网订单号 String orderId =
//	 * request.getParameter("orderId"); //原订单主键ID String isPassuccess =
//	 * request.getParameter("isPassuccess"); //是否支付成功 String isPassenge =
//	 * request.getParameter("isPassenge"); //是否票号回填 String[] passengeMessage =
//	 * request.getParameterValues("passengeMessage"); // 获取票号回填到系统
//	 * 格式为:姓名##生份证##票号##银行订单号 String payTransactionid =
//	 * request.getParameter("payTransactionid"); //获取票号回填的交易号 SC时代表联系电话 String
//	 * payStatus = request.getParameter("payStatus"); //获取支付方式 String isSuccess
//	 * = request.getParameter("isSuccess"); //是否完结 String isautoB2C =
//	 * request.getParameter("isautoB2C"); //是否自动出票 String ifUsedCoupon =
//	 * request.getParameter("ifUsedCoupon"); //是否使用红包
//
//
//	public static String sendCreateOrderInfo(String result, String message, String price, String childrenUser,
//			String newOrderId, String orderId, String isPassuccess, String isPassenge, String passengeMessage,
//			String payStatus, String payTransactionid, String ifUsedCoupon, String isSuccess, String billNo, int requestType) {
//		try {
//			String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrl");
//			StringBuffer buffer = new StringBuffer();
//			buffer.append("<feeye-official>");
//			buffer.append("<official>" + "MF" + "</official> ");
//			buffer.append("<url>" + orderUrl + "</url> ");
//			buffer.append("<type>0</type> ");
//			buffer.append("<method>post</method>");
//			buffer.append("<max>20</max> ");
//			buffer.append("<encod>utf-8</encod> ");
//			buffer.append("<params>");
//			buffer.append("<param name='result'>" + result + "</param>"); //error ,  success(创建成功)
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
//			buffer.append("<param name='requestType'>" + requestType + "</param>");
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
//}
