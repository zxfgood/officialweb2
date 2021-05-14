package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.CabinInfo;
import com.feeye.bean.CrawlParam;
import com.feeye.bean.FlightInfo;
import com.feeye.bean.OfficeFlightInfoUtil;
import com.feeye.util.MD5Util;
import com.feeye.util.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * 山东航空数据抓取类
 * 
 * @author xuyx
 * 
 */
public class TianHangService {

	private static final Logger logger = Logger.getLogger(TianHangService.class);
	Map<String,String> tokenMap = new HashMap<>();
	public String getFlightPriceInfo(String dept, String arrival,String deptDate,String flightNo,String cabin,String serviceId)  {
		String strResult = "";
		CrawlParam param = new CrawlParam(dept, arrival, deptDate);
		param.setDeparuteDate(deptDate);
		param.setArrival(arrival);
		param.setDeparture(dept);
		List<FlightInfo> list = new ArrayList<FlightInfo>();
		try {
			list = getFlightInfo(param, serviceId, flightNo,0,cabin);
			if (list.size() > 0) {
				strResult = OfficeFlightInfoUtil.getResult(list, flightNo);
				return strResult;
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("error", e);
		}
		return "error:未查找到对应价格";

	}
	private List<FlightInfo> getFlightInfo(CrawlParam param, String serviceId, String flightNo, int retryCount,String accountMsg) {
		List<FlightInfo> list = new ArrayList<FlightInfo>();
		String back = "";
		int timeout = 15000;
		if (retryCount > 4) {
			logger.info(serviceId);
			return list;
		}
		retryCount++;
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		 long time = System.currentTimeMillis();
		 logger.info( time + "-开始查价格" );
			try {
				String session = UUID.randomUUID().toString();
				BasicCookieStore cookieStore = new BasicCookieStore();// 一个cookies
				String cookie = "";
				org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
				try {
					proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH , "BASIC realm=default"));
				} catch (MalformedChallengeException e1) {
					logger.error("error" , e1);
				}
				//代理云
				String ip_port = DailiyunService.getRandomIp(50);
				String proxyIp = ip_port.split(":")[0];  //代理IP地址
				int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //代理IP端口
				HttpHost dailiyunProxy = new HttpHost(proxyIp , proxyPort , "http");

				BasicAuthCache authCache = new BasicAuthCache();
				authCache.put(dailiyunProxy , proxyAuth);
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				//代理云账号密码
				String dlyAccount = PropertiesUtils.getPropertiesValue("config" , "dlyAccount");
				String[] dlyAccountInfo = dlyAccount.split(":");
				credsProvider.setCredentials(new AuthScope(dailiyunProxy) , new UsernamePasswordCredentials(dlyAccountInfo[0] , dlyAccountInfo[1]));
				RequestConfig defaultRequestConfig = RequestConfig.custom()
						.setSocketTimeout(timeout)
						.setConnectTimeout(timeout)
						.setConnectionRequestTimeout(timeout)
						.setProxy(dailiyunProxy)
						.setStaleConnectionCheckEnabled(true)
						.build();

				HttpHost target = new HttpHost("https://www.tianhangbox.com" , 443 , "https");
				HttpClientContext context = HttpClientContext.create();
				context.setAuthCache(authCache);
				context.setTargetHost(target);
				context.setRequestConfig(defaultRequestConfig);
				context.setCredentialsProvider(credsProvider);

				SocketConfig socketConfig = SocketConfig.custom()
						.setSoKeepAlive(false).setSoLinger(1)
						.setSoReuseAddress(true)
						.setSoTimeout(timeout)
						.setTcpNoDelay(true).build();

				SSLContext sslcontext = SSLContexts.createDefault();
				SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" },
						null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());

				Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.getSocketFactory())
						.register("https", factory)  // 用来配置支持的协议
						.build();
				// 共享连接池
				PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
				connectionManager.setMaxTotal(100);
				connectionManager.setDefaultMaxPerRoute(20);
				httpclient = HttpClientBuilder.create()
						.setConnectionManager(connectionManager)
						.setConnectionManagerShared(true)
						.setDefaultCookieStore(cookieStore)
						.setDefaultSocketConfig(socketConfig)
						.setDefaultCredentialsProvider(credsProvider)
						.build();
				String dep = param.getDeparture().replace("NAY" , "PEK");
				String arr = param.getArrival().replace("NAY" , "PEK");
				String depdate = param.getDeparuteDate();
				String account = accountMsg.split(";")[0];
				String pwd = accountMsg.split(";")[1];
				String token = tokenMap.get(account);
				int i = 0;
				while (StringUtils.isEmpty(token) && i < 3) {
					token = getToken(account,pwd,httpclient,defaultRequestConfig);
					i++;
					Thread.sleep(3000);
				}
				String url = "https://th-api.tianhangbox.com/api/v1/trip_manager/air_ticket_domestic_flight/list?token=" + token + "&arrCityCode=" + arr +"&orgCityCode=" + dep + "&setTime=" + depdate;
				HttpGet get = new HttpGet(url);
				get.setConfig(defaultRequestConfig);
				try {
					response = httpclient.execute(get);
				} catch (SocketTimeoutException e) {
					Thread.sleep(2000);
					response = httpclient.execute(get);
				}
				back = EntityUtils.toString(response.getEntity() , "utf-8");
				logger.info(serviceId + "获取航班信息结果:" + back);
				if (StringUtils.isNotEmpty(back)) {
					JSONObject backObj = JSON.parseObject(back);
					String code = backObj.getString("ret");
					if ("0".equals(code)) {
						String data = backObj.getString("data");
						list = doCrawl(data , flightNo, serviceId,param);
					} else if ("401".equals(code)){
						token = getToken(account,pwd,httpclient,defaultRequestConfig);
						tokenMap.put(account,token);
						this.getFlightInfo(param, serviceId, flightNo, retryCount, accountMsg);
					}
				}
			} catch (Exception e) {
				logger.info("SC错误返回:" + back);
				logger.error("参数组织错误" , e);
			} finally {
				try {
					if(response!=null){
						response.close();
					}
				} catch (Exception e) {
				}
				if (httpclient != null) {
					try {
						httpclient.close();
						logger.info( time + "-关闭查价格" );
					} catch (IOException e) {
					}
				}
			}

			return list;
		}
	@Test
	public void a() throws IOException {
		/*String token = "";
		CloseableHttpResponse response = null;
		String pwd = MD5Util.getMD5("860086");
		String url = "https://th-api.tianhangbox.com/login/appNewLogin";
		HttpPost post = new HttpPost(url);
		List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
		nameValue.add(new BasicNameValuePair("mobilePhone", "18384782738"));
		nameValue.add(new BasicNameValuePair("loginPsw", pwd));
		nameValue.add(new BasicNameValuePair("timestamp", System.currentTimeMillis()+""));
		nameValue.add(new BasicNameValuePair("device", "web"));
		nameValue.add(new BasicNameValuePair("browerInfo", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36"));
		post.setEntity(new UrlEncodedFormEntity(nameValue,"UTF-8"));
		// post.setConfig(config);
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		response = httpclient.execute(post);
		String back = EntityUtils.toString(response.getEntity(), "utf-8");
		System.out.println(back);*/
		getFlightPriceInfo("TYN","DLC","2020-12-24","HU7134","13037284859;1437015921","66666");
	}
	private String getToken(String account,String pwd,CloseableHttpClient httpclient,RequestConfig config) throws IOException {
		String token = "";
		CloseableHttpResponse response = null;
		try {
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
			logger.info(account + "获取token返回:" + back);
			if (StringUtils.isNotEmpty(back)) {
				JSONObject backObj = JSON.parseObject(back);
				String code = backObj.getString("ret");
				if ("0".equals(code)) {
					JSONObject dataObj = backObj.getJSONObject("data");
					token = dataObj.getString("token");
				}
			}
		}catch (Exception e) {
			logger.error(account + "获取tokenerror：",e);
		}finally {
			if (response!=null) {
				response.close();
			}
		}
		return token;
	}
	private List<FlightInfo> doCrawl(String content, String flightNo, String id, CrawlParam param) {
		List<FlightInfo> resList = new ArrayList<FlightInfo>();
		List<CabinInfo> list = new ArrayList<CabinInfo>();
		try {
			if (!StringUtils.isEmpty(content)) {
				if (content.contains("非常抱歉，没有您所需的航班")) {
					return resList;
				}
				FlightInfo flightInfo = new FlightInfo();
				flightInfo.setFlightNo(flightNo);
				flightInfo.setDeparutre(param.getDeparture());// 出发地
				flightInfo.setArrival(param.getArrival()); // 到达地
				flightInfo.setDeparutreDate(param.getDeparuteDate()); // 起飞时间
				flightInfo.setWebType("天航商旅");
				flightInfo.setUpdateTime(new Date());
				JSONObject dataObj = JSON.parseObject(content);
				JSONArray flightListArray = dataObj.getJSONArray("flightAndSeatsList");
				for(int i = 0;i < flightListArray.size();i++) {
					// cabinTable:cabinTables
					JSONObject seatObj = flightListArray.getJSONObject(i);
					JSONObject flightObj = seatObj.getJSONObject("flightEntity");
					String airlineCode = flightObj.getString("airlineCode");
					String flightCode = flightObj.getString("flightNo");
					if (!flightNo.equals(airlineCode + flightCode)) {
						continue;
					}
					JSONArray seatList = seatObj.getJSONArray("seatList");
					for(int j = 0; j < seatList.size();j++ ) {
						JSONObject seat = seatList.getJSONObject(j);
						// set
						CabinInfo cabinInfo = new CabinInfo();
						String code = seat.getString("seatMsg");
						String price = seat.getString("parPrice");
						String seatCode = seat.getString("seatCode");
						cabinInfo.setBaseCabin(code); // 仓位类型
						cabinInfo.setCabinCode(seatCode); // 仓位
						cabinInfo.setPrice(price); // 价格
						String inventory = seat.getString("discount");
						cabinInfo.setLastSeat(inventory); // 座位数
						/*String flightMsg = seatObj.getString("flightEntity");
						String seatMsg = seatList.getString(j);*/
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("flightEntity",flightObj);
						jsonObject.put("seatEntity",seat);
						cabinInfo.setComment(jsonObject.toJSONString());
						list.add(cabinInfo);
					}
				}
				flightInfo.setCabins(list);
				resList.add(flightInfo);
			}
		} catch (Exception e) {
			logger.error("error",e);
		}
		return resList;
	}


	/*private String getcabintype(String cabincod) {
	        String cabintype = "";
	        //A、P 头等舱
	        //R 高级经济舱
	        //Y 全价经济舱
	        //V、Q、U 折扣经济舱
	        //E  特价经济舱

	        switch (cabincod) {
	            case "A":
	            case "P":
	                cabintype = "头等舱";
	                break;
	            case "R":
	                cabintype = "高级经济舱";
	                break;
	            case "Y":
	                cabintype = "全价经济舱";
	                break;
	            case "V":
	            case "Q":
	            case "U":
	                cabintype = "折扣经济舱";
	                break;
	            case "E":
	                cabintype = "特价经济舱";
	                break;
	            default:
	                cabintype = "未知类型" + cabincod;
	                break;
	        }
	        return cabintype;
	    }*/
	
}