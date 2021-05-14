package com.feeye.service.processor;

import com.feeye.bean.CabinInfo;
import com.feeye.bean.CrawlParam;
import com.feeye.bean.FlightInfo;
import com.feeye.util.PropertiesUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.*;

/**
 * 山东航空数据抓取类
 * 
 * @author xuyx
 * 
 */
public class SCNEWService{

	private static final Logger logger = Logger.getLogger(SCNEWService.class);
  
	public String getFlightPriceInfo(String dept, String arrival,String deptDate,String flightNo,String cabin,String serviceId)  {
		String strResult = "";
		CrawlParam param = new CrawlParam(dept, arrival, deptDate);
		param.setDeparuteDate(deptDate);
		param.setArrival(arrival);
		param.setDeparture(dept);
		List<FlightInfo> list = new ArrayList<FlightInfo>();
		try {
			list = getFlightInfo(param, serviceId, flightNo,0);
			/*if (list.size() > 0) {
				strResult = OfficeFlightInfoUtil.getResult(list, flightNo);
				return strResult;
			}*/
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("error", e);
		}
		return "error:未查找到对应价格";

	}
	 
	 
	 private List<FlightInfo> getFlightInfo(CrawlParam param, String serviceId, String flightNo, int retryCount) {
		List<FlightInfo> list = new ArrayList<FlightInfo>();
		String back = "";
		int timeout = 15000;
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

				HttpHost target = new HttpHost("www.sda.cn" , 80 , "http");
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

				String depCN = PropertiesUtils.getPropertiesValue("allcity" , dep);
				String arrCN = PropertiesUtils.getPropertiesValue("allcity" , arr);

				HttpGet get = new HttpGet("http://www.sda.cn/");
				get.setConfig(defaultRequestConfig);
				get.setHeader("User-Agent" , "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
				get.setHeader("Host" , "www.sda.cn");
				get.setHeader("Cookie" , cookie);
				get.setHeader("Proxy-Authorization" , "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
				get.setHeader("Proxy-Connection" , "keep-alive");
				response = httpclient.execute(get);
				back = EntityUtils.toString(response.getEntity() , "utf-8");
				String newCookie = "";
				Header[] headersArr = response.getAllHeaders();
				for (Header header : headersArr) {
					if ("Set-Cookie".equals(header.getName())) {
						newCookie += header.getValue() + ";";
						logger.info("获取新的cookie:" + newCookie);
					}
				}
				cookie = newCookie;
				try {
					if(response!=null){
						response.close();
					}
				} catch (Exception e) {
				}
				HttpPost post = new HttpPost("http://sc.travelsky.com/scet/queryAv.do?lan=cn");
				List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
				nameValue.add(new BasicNameValuePair("adultNum", "1"));
				nameValue.add(new BasicNameValuePair("cabinStage", "0"));
				nameValue.add(new BasicNameValuePair("childNum", "0"));
				nameValue.add(new BasicNameValuePair("cityCodeDes", arr));
				nameValue.add(new BasicNameValuePair("cityCodeOrg", dep));
				nameValue.add(new BasicNameValuePair("cityNameDes", arrCN));
				nameValue.add(new BasicNameValuePair("cityNameOrg", depCN));
				nameValue.add(new BasicNameValuePair("countrytype", "0"));
				nameValue.add(new BasicNameValuePair("returnDate", depdate));
				nameValue.add(new BasicNameValuePair("takeoffDate", depdate));
				nameValue.add(new BasicNameValuePair("travelType", "0"));
				logger.info("查询航班参数1:"+nameValue.toString());
				post.setEntity(new UrlEncodedFormEntity(nameValue,"UTF-8"));
				post.setConfig(defaultRequestConfig);
				post.setHeader("Host", "sc.travelsky.com");
				post.setHeader("Referer", "http://www.sda.cn/?ticket=ST-2767-F0w5bjIFA1cbld5yrVIo-cas");
				post.setHeader("Cookie", cookie);
				post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
				post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
				post.setHeader("Proxy-Connection", "keep-alive");
				response = httpclient.execute(post);
				back = EntityUtils.toString(response.getEntity(), "gbk");
				logger.info("查询航班返回1:"+back);
				Document doc = Jsoup.parse(back);
				Map<String,String> paramMap = new HashMap<String,String>();
				Elements inputs = doc.getElementsByTag("input");
				for(Element input:inputs) {
					paramMap.put(input.attr("name"), input.val());
				}
				
				response.close();
				
				post = new HttpPost("http://sc.travelsky.com/scet/airAvail.do");
				nameValue = new ArrayList<NameValuePair>();
				for(Map.Entry<String, String> entry:paramMap.entrySet()) {
					if(!"cityNameDes".equals(entry.getKey()) && !"cityNameOrg".equals(entry.getKey())){
						nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
					}
				}
				nameValue.add(new BasicNameValuePair("cityNameDes", arrCN));
				nameValue.add(new BasicNameValuePair("cityNameOrg", depCN));
				
				logger.info("查询航班参数2:"+nameValue.toString());
				post.setEntity(new UrlEncodedFormEntity(nameValue,"UTF-8"));
				post.setConfig(defaultRequestConfig);
				post.setHeader("Host", "sc.travelsky.com");
				post.setHeader("Referer", "http://sc.travelsky.com/scet/queryAv.do?lan=cn");
				post.setHeader("Cookie", cookie);
				post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
				post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
				post.setHeader("Proxy-Connection", "keep-alive");
				response = httpclient.execute(post);
				back = EntityUtils.toString(response.getEntity(), "utf-8");
//				logger.info("查询航班返回2:"+back);
				for(int i=0;i<5;i++){
					if(StringUtils.isNotEmpty(back)){
						break;
					}
					response.close();
					post = new HttpPost("http://sc.travelsky.com/scet/airAvail.do");
					nameValue = new ArrayList<NameValuePair>();
					for(Map.Entry<String, String> entry:paramMap.entrySet()) {
						nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
					}
					logger.info("查询航班参数2:"+nameValue.toString());
					post.setEntity(new UrlEncodedFormEntity(nameValue,"UTF-8"));
					post.setConfig(defaultRequestConfig);
					post.setHeader("Host", "sc.travelsky.com");
					post.setHeader("Referer", "http://sc.travelsky.com/scet/queryAv.do?lan=cn");
					post.setHeader("Cookie", cookie);
					post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
					post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
					post.setHeader("Proxy-Connection", "keep-alive");
					response = httpclient.execute(post);
					back = EntityUtils.toString(response.getEntity(), "utf-8");
					logger.info("查询航班返回3:"+back);
					Thread.sleep(1000);
				}
				list = doCrawl(back , param , cookie , session , serviceId , "" , "" , flightNo , defaultRequestConfig ,
						httpclient);
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


	private List<FlightInfo> doCrawl(String content, CrawlParam param, String cookie, String session, String serviceId,
			String proxyIp, String proxyPort, String flightNo, RequestConfig config, CloseableHttpClient httpclient) {
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
				
				Document doc = Jsoup.parse(content);
				Elements cabinTables = doc.getElementsByClass("__cabin_table__");
				for(Element cabinTable:cabinTables) {
					Elements rdoTriggers = cabinTable.getElementsByClass("rdo-trigger");
					for(Element rdoTrigger:rdoTriggers) {
						String flightnumber = rdoTrigger.attr("data-flightnumber");
						String airline = rdoTrigger.attr("data-airline");
						if(!flightNo.equals(airline + flightnumber)) {
							continue;
						}
						CabinInfo cabinInfo = new CabinInfo();
						String code = rdoTrigger.attr("data-classcode");
						String price = rdoTrigger.attr("data-price");
						cabinInfo.setBaseCabin(getcabintype(code)); // 仓位类型
						cabinInfo.setCabinCode(code); // 仓位
						cabinInfo.setPrice(price); // 价格
						String inventory = rdoTrigger.attr("data-quantity");
						if(StringUtils.isNotEmpty(inventory)){
							inventory = inventory.trim();
						}
						cabinInfo.setLastSeat(inventory); // 座位数
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


	private String getcabintype(String cabincod) {
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
	    }
	
}