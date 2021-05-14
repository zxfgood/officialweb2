package com.feeye.service.processor;

import com.feeye.bean.CabinInfo;
import com.feeye.bean.CrawlParam;
import com.feeye.bean.FlightInfo;
import com.feeye.bean.OfficeFlightInfoUtil;
import com.feeye.util.WebDriverObtain;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 山东航空数据抓取类
 *
 * @author xuyx
 */
public class SCMService {

    private static final Logger logger = Logger.getLogger(SCMService.class);

    public String getFlightPriceInfo(String dept, String arrival, String deptDate, String flightNo, String cabin, String serviceId) {
        String strResult = "";
        CrawlParam param = new CrawlParam(dept, arrival, deptDate);
        param.setDeparuteDate(deptDate);
        param.setArrival(arrival);
        param.setDeparture(dept);
        List<FlightInfo> list = new ArrayList<FlightInfo>();
        try {
            list = getFlightInfo(param, serviceId, flightNo, 0);
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

    @Test
    public void a() {
        getFlightPriceInfo("TNA", "SZX", "2020-05-07", "", "S", "ZXF");
    }

    public static void main(String[] args) {
        SCMService scnewService = new SCMService();
        scnewService.getFlightPriceInfo("TNA", "SZX", "2020-05-07", "", "S", "ZXF");
    }

    private List<FlightInfo> getFlightInfo(CrawlParam param, String serviceId, String flightNo, int retryCount) {
        List<FlightInfo> list = new ArrayList<FlightInfo>();
        String back = "";
        int timeout = 15000;
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        long time = System.currentTimeMillis();
        logger.info(time + "-开始查价格");
        try {
            String session = UUID.randomUUID().toString();
            BasicCookieStore cookieStore = new BasicCookieStore();// 一个cookies
            String cookie = "";
            org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
            try {
                proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
            } catch (MalformedChallengeException e1) {
                logger.error("error", e1);
            }
            //代理云
            String ip_port = DailiyunService.getRandomIp(50);
            String proxyIp = ip_port.split(":")[0];  //代理IP地址
            int proxyPort = Integer.parseInt(ip_port.split(":")[1]);  //代理IP端口
            HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

            BasicAuthCache authCache = new BasicAuthCache();
            authCache.put(dailiyunProxy, proxyAuth);
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            //代理云账号密码
            String dlyAccount = "feeyeapp:feeye789";
            // PropertiesUtils.getPropertiesValue("config" , "dlyAccount");
            String[] dlyAccountInfo = dlyAccount.split(":");
            credsProvider.setCredentials(new AuthScope(dailiyunProxy), new UsernamePasswordCredentials(dlyAccountInfo[0], dlyAccountInfo[1]));
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setProxy(dailiyunProxy)
                    .setStaleConnectionCheckEnabled(true)
                    .build();

            HttpHost target = new HttpHost("flights.sda.cn", 443, "https");
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
            SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1.2"},
                    null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

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
            String dep = param.getDeparture().replace("NAY", "PEK");
            String arr = param.getArrival().replace("NAY", "PEK");
				/*String depdate = param.getDeparuteDate();

				String depCN = PropertiesUtils.getPropertiesValue("allcity" , dep);
				String arrCN = PropertiesUtils.getPropertiesValue("allcity" , arr);*/
            HttpHost target2 = new HttpHost("flights.sda.cn", 443, "https");
            String url = "/flight/search/tna-szx-200507-1";
            // "https://flights.sda.cn/tRtApi/flightCache/calendarSearch?from=TNA&to=SZX&departureDate=2020-05-06&returnDate=2020-07-05&minDiscountLevel=0";
            HttpGet get = new HttpGet(url);
            get.setConfig(defaultRequestConfig);
            get.setHeader("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.129 Safari/537.36");
            get.setHeader("Host", "flights.sda.cn");
            // Referer: https://flights.sda.cn/flight/search/tna-szx-200507-1
            // mfaMeta: {"captcha":{"id":null},"sms":{}}
            // get.setHeader("mfaMeta" , "{\"captcha\":{\"id\":null},\"sms\":{}}");
            // get.setHeader("Referer" , "https://flights.sda.cn/flight/search/tna-szx-200507-1");
            cookie = "Webtrends=183.14.132.129.1588065176868953; OZ_1K_671=etime=1588729089&ozu_sid=-&ozs=189735&flag=2&compid=671; OZ_1S_671=etime=1588729089&ozu_sid=-&ozs=189735&flag=2&compid=671; looyu_id=31f9746fb25e204929aa6f5974303ab8_20003719%3A4; OZ_SI_671=sTime=1588729089&sIndex=2; OZ_1U_671=vid=vea7f389484a58.0&ctime=1588729115&ltime=1588729089; OZ_1Y_671=erefer=https%3A//www.baidu.com/other.php%3Fsc.K60000jzbxau-D2_EGn7_RQS4-MPB9tE2ISgPEs8U7BW-S2GzhAzB_soMsCK3yd4UYysEv-lVTVQ5m14i2_BYUzZdOGTEkXRNZwoSxfUwcNIDNmuVEJHkBATHzo4712uYyJcX4ECZgKsbAzXSt6qm_ayCK0I3Qxac84ETE1TdCLMHW1rBWDZKoWrkwGA4hVYxTIrlJSFOx_32unxIHN0BZz1TlOb.7b_ar4Mbe83mv2qhOk3e_OH3dJN9h9mozX5ZG0.TLFWgv-b5HDkrfK1ThPGujYknHb0THY0IAYqzUvvJeJqEqR0IgP-T-qYXgK-5H00mywxIZ-suHYv0ZIEThfqzUvvJeJqEqR0ThPv5HD0IgF_gv-b5HDdnHTLPjndnjf0UgNxpyfqnHD4PHfdPjD0UNqGujYknjmzPjmvr0KVIZK_gv-b5HDkPHnY0ZKvgv-b5H00mLFW5HcLPWfk%26ck%3D3333.2.114.224.163.249.174.304%26dt%3D1588729085%26wd%3D%26tpl%3Dtpl_11534_22017_18147%26l%3D1517743504%26us%3DlinkName%253D%2525E6%2525A0%252587%2525E5%252587%252586%2525E5%2525A4%2525B4%2525E9%252583%2525A8-%2525E4%2525B8%2525BB%2525E6%2525A0%252587%2525E9%2525A2%252598%2526linkText%253D%2525E5%2525B1%2525B1%2525E4%2525B8%25259C%2525E8%252588%2525AA%2525E7%2525A9%2525BA%2525E5%2525AE%252598%2525E6%252596%2525B9%2525E7%2525BD%252591%2525E7%2525AB%252599%2526linkType%253D&eurl=http%3A//www.sda.cn/%3Fozs%3D189735-671&etime=1588729089&ctime=1588729115&ltime=1588729089&compid=671; lang_type=zh_CN; zh_CN_air_his=tna-szx-200507-1%3Btna-szx-200604-1%3Bszx-szx-200604-1%3Bszx-xmn-200528-1%3Bszx-xmn-200617-1; OZ_1U_3072=vid=vea7f39515fba1.0&ctime=1588734648&ltime=1588734416; OZ_1Y_3072=erefer=http%3A//www.sda.cn/%3Fozs%3D189735-671&eurl=https%3A//new.sda.cn/&etime=1588729116&ctime=1588734648&ltime=1588734416&compid=3072; OZ_SI_3072=sTime=1588729116&sIndex=18";
            get.setHeader("Cookie", cookie);
            get.setHeader("Sec-Fetch-Dest", "document");
            get.setHeader("Sec-Fetch-Mode", "navigate");
            get.setHeader("Sec-Fetch-Site", "none");
            get.setHeader("Sec-Fetch-User", "?1");
            get.setHeader("Upgrade-Insecure-Requests", "1");
            get.setHeader("Proxy-Authorization", "Basic " + org.apache.commons.codec.binary.Base64.encodeBase64String(dlyAccount.getBytes(StandardCharsets.UTF_8)));
            get.setHeader("Proxy-Connection", "keep-alive");
            response = httpclient.execute(target, get);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
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
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
            }
            url = "https://flights.sda.cn/tRtApi/flightCache/calendarSearch?from=TNA&to=SZX&departureDate=2020-05-06&returnDate=2020-07-05&minDiscountLevel=0";
            HttpGet post = new HttpGet(url);
			/*	List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
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
				logger.info("查询航班参数1:"+nameValue.toString());*/
            // post.setEntity(new UrlEncodedFormEntity(nameValue,"UTF-8"));
            post.setConfig(defaultRequestConfig);
            post.setHeader("Host", "flights.sda.cn");
            post.setHeader("Referer", "https://flights.sda.cn/flight/search/tna-szx-200507-1");
            post.setHeader("Cookie", cookie);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            // post.setHeader("Proxy-Authorization", "Basic " + Base64.encodeBase64String(dlyAccount.getBytes("utf-8")));
            post.setHeader("Proxy-Connection", "keep-alive");
            post.setHeader("mfaMeta", "{\"captcha\":{\"id\":null},\"sms\":{}}");
            response = httpclient.execute(post);
            back = EntityUtils.toString(response.getEntity(), "gbk");
            logger.info("查询航班返回1:" + back);
            Document doc = Jsoup.parse(back);
            Map<String, String> paramMap = new HashMap<String, String>();
            Elements inputs = doc.getElementsByTag("input");
            for (Element input : inputs) {
                paramMap.put(input.attr("name"), input.val());
            }

            response.close();
				
				/*post = new HttpPost("http://sc.travelsky.com/scet/airAvail.do");
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
						httpclient);*/
        } catch (Exception e) {
            logger.info("SC错误返回:" + back);
            logger.error("参数组织错误", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                    logger.info(time + "-关闭查价格");
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
                //flightInfo.setWebType(Constant.SC.toString());
                flightInfo.setUpdateTime(new Date());

                Document doc = Jsoup.parse(content);
                Elements cabinTables = doc.getElementsByClass("__cabin_table__");
                for (Element cabinTable : cabinTables) {
                    Elements rdoTriggers = cabinTable.getElementsByClass("rdo-trigger");
                    for (Element rdoTrigger : rdoTriggers) {
                        String flightnumber = rdoTrigger.attr("data-flightnumber");
                        String airline = rdoTrigger.attr("data-airline");
                        if (!flightNo.equals(airline + flightnumber)) {
                            continue;
                        }
                        CabinInfo cabinInfo = new CabinInfo();
                        String code = rdoTrigger.attr("data-classcode");
                        String price = rdoTrigger.attr("data-price");
                        cabinInfo.setBaseCabin(getcabintype(code)); // 仓位类型
                        cabinInfo.setCabinCode(code); // 仓位
                        cabinInfo.setPrice(price); // 价格
                        String inventory = rdoTrigger.attr("data-quantity");
                        if (StringUtils.isNotEmpty(inventory)) {
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
            logger.error("error", e);
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

    @Test
    public void test() throws InterruptedException {
        WebDriver webDriver = WebDriverObtain.getGeckoDriver("");
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.get("https://flights.sda.cn/login");
        Thread.sleep(3000000);
		/*JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
		webDriver.findElement()*/
    }
}