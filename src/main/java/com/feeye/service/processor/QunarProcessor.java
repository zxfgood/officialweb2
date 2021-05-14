/*
package com.feeye.service.processor;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feeye.grab.bean.GrabResult;
import com.feeye.grab.bean.grabCondition.GrabCondition;
import com.feeye.official.proxy.bean.CabinInfo;
import com.feeye.official.proxy.common.ParamsParser;
import com.feeye.official.proxy.components.FeeyeHttpClient;
import com.feeye.official.proxy.exception.GrabException;
import com.feeye.official.proxy.inter.Executor;
import com.feeye.official.proxy.util.Base64;
import com.feeye.official.proxy.util.FingerPrintUtil;
import com.feeye.official.proxy.util.PropertiesUtil;

*
 * 去哪儿抓取
 * @author zhengxs
 *


public class QunarProcessor extends Executor{
	private static final Logger logger = Logger.getLogger(QunarProcessor.class);
	private String departure;
	private String arrival;
	public QunarProcessor(GrabCondition grabCondition) {
		super(grabCondition);
	}
	
	public static void main(String[] args) throws Exception {

		GrabCondition grabCondition = new GrabCondition();
		grabCondition.setAirline("TC");
		grabCondition.setGrabType(3);
		grabCondition.setFlightline("SHA-PEK");
		grabCondition.setFlyDate("2021-05-15");
		grabCondition.setProxyIP("120.38.34.41:57114");
		QunarProcessor jdp = new QunarProcessor(grabCondition);
		
		Map<String, Object> doRequest = jdp.doRequest();
String content = FileUtil.readFile("C:\\Users\\Administrator\\Desktop\\A67179.txt");
		Map<String, Object> doRequest = new HashMap<String,Object>();
		doRequest.put("content", content);

		GrabResult grabResult = jdp.doParse(doRequest);
		logger.info("查询回来的结果:"+JSON.toJSONString(grabResult));
	}
	
	@Override
	protected Map<String, Object> doRequest() throws GrabException {
		Map<String, Object> requestRes = new HashMap<String, Object>();
		try {
			String[] pairs = grabCondition.getFlightline().split("-");
			departure = pairs[0];
			arrival = pairs[1];
			
			String proxyIp = "";
			String proxyPort = "";
			if(grabCondition.getProxyIP() !=null){
				pairs = grabCondition.getProxyIP().split(":");
				proxyIp = pairs[0];
				proxyPort = pairs[1];
			}
			
			
			String deparName = PropertiesUtil.getProperty("/G5AirCity.properties", departure, true).split(",")[0];
			String arrivalName = PropertiesUtil.getProperty("/G5AirCity.properties", arrival, true).split(",")[0];
			if(StringUtils.isEmpty(deparName) || StringUtils.isEmpty(arrivalName)){
				logger.info("机场三字码转换成中文城市名时没能找到："+grabCondition.getFlightline());
				requestRes.put("content", "");
				return requestRes;
			}
			
			String deparCn = URLEncoder.encode(deparName, "utf-8");
			String arrivalCn = URLEncoder.encode(arrivalName, "utf-8");
			
			String depCityName = departure;
			String arrCityName = arrival;
			
			if(departure.equals("PEK") || departure.equals("PKX") ){
				depCityName = "BJS";
			}else if(departure.equals("PVG")){
				depCityName = "SHA";
			}
			
			if(arrival.equals("PEK") || arrival.equals("PKX") ){
				arrCityName = "BJS";
			}else if(arrival.equals("PVG")){
				arrCityName = "SHA";
			}
			
			String cookie = "";
			
			long ltime = System.currentTimeMillis();
			
			
			StringBuilder buffer = new StringBuilder();
			
			
			buffer.append("<feeye-official>");
			buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
			buffer.append("<url>/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F</url> ");
			buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
			buffer.append("<isHttps>true</isHttps>");
			buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
			buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
			buffer.append("<method>get</method>");
			buffer.append("<encod>utf-8</encod> ");
			buffer.append("<headers>");
			buffer.append("<head name='Host'>user.qunar.com</head >");
			buffer.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36</head>");
			buffer.append("</headers>");
			buffer.append("</feeye-official>");
			
			//发送第一次请求
			String content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
			
            String rs1[] = content1.split("#@_@#");
			
			if(rs1.length == 2){
				content1 = rs1[1];
				cookie = rs1[0];
			}
			
			String codeUrls[] = content1.split("https\\:\\/\\/user\\.qunar\\.com\\/captcha\\/api\\/image\\?k\\=\\{en7mni\\(z&p\\=ucenter_login&c\\=");
			String codeUrl = "";
			if(codeUrls.length > 1){
				codeUrl = "/captcha/api/image?k=%7Ben7mni(z&p=ucenter_login&c="+(codeUrls[1].substring(0,32));
			}
			
			
			buffer.setLength(0);
			
			buffer.append("<feeye-official>");
			buffer.append("<official>"+grabCondition.getAirline()+"</official> ");
			buffer.append("<url>"+codeUrl+"</url> ");
			buffer.append("<isHttps>true</isHttps>");
			buffer.append("<type>"+grabCondition.getGrabType()+"</type> ");
			buffer.append("<proxyIp>"+proxyIp+"</proxyIp>");
			buffer.append("<proxyPort>"+proxyPort+"</proxyPort>");
			buffer.append("<codeSave>1</codeSave>");
			buffer.append("<method>get</method>");
			buffer.append("<encod>UTF-8</encod> ");
			buffer.append("<headers>");
			buffer.append("<head name='Cookie'>"+cookie+"</head >");
			buffer.append("<head name='Host'>user.qunar.com</head >");
			buffer.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36</head>");
			buffer.append("</headers>");
			buffer.append("</feeye-official>");
			content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
            rs1 = content1.split("#@_@#");
			
		    cookie+= rs1[0];
			
			Scanner input = new Scanner(System.in);
			String val = null;       // 记录输入度的字符串
			
			System.out.print("请输入：");
			val = input.next();       // 等待输入值
			System.out.println("您输入的是："+val);
			
			input.close(); // 关闭资源
			
			
			buffer.setLength(0);
			
			
			buffer.append("<feeye-official>");
			buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
			buffer.append("<url>/passport/addICK.jsp?ssl</url> ");
			buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
			buffer.append("<isHttps>true</isHttps>");
			buffer.append("<redict>1</redict>");
			buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
			buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
			buffer.append("<method>get</method>");
			buffer.append("<encod>utf-8</encod> ");
			buffer.append("<headers>");
			
			buffer.append("<head name='Referer'>https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F</head >");
			
			buffer.append("<head name='Cookie'>"+cookie+"</head >");
			buffer.append("<head name='Host'>user.qunar.com</head >");
			buffer.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36</head>");
			buffer.append("</headers>");
			buffer.append("</feeye-official>");
			
			//发送第一次请求
			content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
			
            rs1 = content1.split("#@_@#");
			
			if(rs1.length > 1){
				content1 = rs1[1];
				cookie+= rs1[0];
			}
			
            buffer.setLength(0);
			

			
			buffer.append("<feeye-official>");
			buffer.append("<official>"+grabCondition.getAirline()+"</official> ");
			buffer.append("<url>/js/df.js?org_id=ucenter.login&js_type=0</url> ");
			buffer.append("<isHttps>true</isHttps>");
			buffer.append("<type>"+grabCondition.getGrabType()+"</type> ");
			buffer.append("<proxyIp>"+proxyIp+"</proxyIp>");
			buffer.append("<proxyPort>"+proxyPort+"</proxyPort>");
			buffer.append("<method>get</method>");
			buffer.append("<encod>UTF-8</encod> ");
			buffer.append("<headers>");
			buffer.append("<head name='Cookie'>"+cookie+"</head >");
			buffer.append("<head name='Host'>rmcsdf.qunar.com</head >");
			buffer.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36</head>");
			buffer.append("</headers>");
			buffer.append("</feeye-official>");
			content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
            rs1 = content1.split("#@_@#");
            String jessionID = "";
            if(rs1.length > 1){
				content1 = rs1[1];
				jessionID = rs1[0];
			}
           
            String s [] = content1.split("https\\:\\/\\/rmcsdf\\.qunar.com\\/js\\/device\\.js\\?orgId=ucenter.login&sessionId=");
  
            String qn271 = "";
            if(s.length > 1){
            	qn271 = s[1].substring(0,"f4d84875-23c5-409c-94db-a15ec5351dd7".length());
            }
            
            
            
            
            buffer.setLength(0);
			
			buffer.append("<feeye-official>");
			buffer.append("<official>"+grabCondition.getAirline()+"</official> ");
			buffer.append("<url>/js/device.js?orgId=ucenter.login&sessionId="+qn271+"&auto=false</url> ");
			buffer.append("<isHttps>true</isHttps>");
			buffer.append("<type>"+grabCondition.getGrabType()+"</type> ");
			buffer.append("<proxyIp>"+proxyIp+"</proxyIp>");
			buffer.append("<proxyPort>"+proxyPort+"</proxyPort>");
			buffer.append("<method>get</method>");
			buffer.append("<encod>UTF-8</encod> ");
			buffer.append("<headers>");
			buffer.append("<head name='Cookie'>"+jessionID+";"+cookie+"</head >");
			buffer.append("<head name='Host'>rmcsdf.qunar.com</head >");
			buffer.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36</head>");
			buffer.append("</headers>");
			buffer.append("</feeye-official>");
			content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
            rs1 = content1.split("#@_@#");
            jessionID = "";
            if(rs1.length > 1){
				jessionID = rs1[0];
			}
           
            		
            buffer.setLength(0);
			
			buffer.append("<feeye-official>");
			buffer.append("<official>"+grabCondition.getAirline()+"</official> ");
			buffer.append("<url>/api/device/challenge.json?callback=callback_"+System.currentTimeMillis()+"&sessionId="+qn271+"&domain=qunar.com&orgId=ucenter.login</url> ");
			buffer.append("<isHttps>true</isHttps>");
			buffer.append("<type>"+grabCondition.getGrabType()+"</type> ");
			buffer.append("<proxyIp>"+proxyIp+"</proxyIp>");
			buffer.append("<proxyPort>"+proxyPort+"</proxyPort>");
			buffer.append("<method>get</method>");
			buffer.append("<encod>UTF-8</encod> ");
			buffer.append("<headers>");
			buffer.append("<head name='Cookie'>"+jessionID+";"+cookie+"</head >");
			buffer.append("<head name='Host'>rmcsdf.qunar.com</head >");
			buffer.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36</head>");
			buffer.append("</headers>");
			buffer.append("</feeye-official>");
			content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
            rs1 = content1.split("#@_@#");
            if(rs1.length > 1){
				content1 = rs1[1];
				cookie+= rs1[0];
			}
            		
			buffer.setLength(0);
			
			
			buffer.append("<feeye-official>");
			buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
			buffer.append("<url>/passport/loginx.jsp</url> ");
			buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
			buffer.append("<isHttps>true</isHttps>");
			buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
			buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
			buffer.append("<method>post</method>");
			buffer.append("<encod>utf-8</encod> ");
			
			buffer.append("<params>");
			buffer.append("<param name='loginType'>0</param>");
			buffer.append("<param name='ret'>https://www.qunar.com/</param>");
			buffer.append("<param name='username'>18617070230</param>");
			buffer.append("<param name='password'>feeye123</param>");
			buffer.append("<param name='remember'>1</param>");
			buffer.append("<param name='vcode'>"+val+"</param>");
			buffer.append("</params>");
			
			cookie = cookie+"; QN271="+qn271;
			
			logger.info(cookie);
			
			buffer.append("<headers>");
			buffer.append("<head name='Sec-Fetch-Site'>same-origin</head >");
			buffer.append("<head name='Sec-Fetch-Mode'>cors</head >");
			buffer.append("<head name='Sec-Fetch-Dest'>empty</head >");
			buffer.append("<head name='Content-Type'>application/x-www-form-urlencoded; charset=UTF-8</head >");
			buffer.append("<head name='Host'>user.qunar.com</head >");
			buffer.append("<head name='Origin'>https://user.qunar.com</head >");
			buffer.append("<head name='Cookie'>"+jessionID+";"+cookie+"</head >");
			buffer.append("<head name='Referer'>https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F</head >");
			buffer.append("<head name='User-Agent'>Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1</head>");
			buffer.append("</headers>");
			buffer.append("</feeye-official>");
			
			//发送第一次请求
			content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
			
			if(content1.contains("验证码验证失败")){
				logger.info("验证码验证失败");
				return null;
			}
			
			logger.info(content1);
			
			rs1 = content1.split("#@_@#");
			
			
			if(rs1.length > 1){
				content1 = rs1[1];
				cookie+= rs1[0];
			}
			
			cookie = jessionID+"; "+cookie+"; QN271="+qn271;
			
			logger.info(cookie);

			
			buffer.setLength(0);
			
//			String url = "/lowFlight/flightList?dep=%E5%8C%97%E4%BA%AC&arr=%E5%B9%BF%E5%B7%9E&flightType=1&startDate=2021-05-22&endDate=&tag=-1&tagNames=5%E6%9C%8822%E6%97%A5&days=-1&cat=touchjpgg_lowFlight-cheap-ticket_urban_information_flow_oneWayTrip&monitorLowestPrice=372&monitorTime="+ltime+"&hybridid=flight_tejia&bizSource=SUPER_LOW";
//			
//			buffer.append("<feeye-official>");
//			buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//			buffer.append("<url>"+url+"</url> ");
//			buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
//			buffer.append("<isHttps>true</isHttps>");
//			buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//			buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
//			buffer.append("<method>get</method>");
//			buffer.append("<encod>utf-8</encod> ");
//			buffer.append("<headers>");
//			buffer.append("<head name='Cookie'>"+cookie+"</head >");
//			buffer.append("<head name='Host'>touch.qunar.com</head >");
//			buffer.append("<head name='User-Agent'>Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Mobile Safari/537.36</head>");
//			buffer.append("</headers>");
//			buffer.append("</feeye-official>");
//			
//			//发送第一次请求
//			content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
//			rs1 = content1.split("#@_@#");
//			
//			if(rs1.length > 1){
//				content1 = rs1[1];
//				cookie+= rs1[0];
//			}
			
			//String oldCookie = "QN1=00007f00254031fe33589878; QN25=4acdb360-6ebf-4333-b5cc-bd22c4482383-9f992f90; _i=DFiEuMLtLc6wCTFL-gtUQ3NRrJRw; _vi=PDsAmvqAKJhfYNmkoJzGvw2bM4KngNKJtxpMywnu-oImfpktPSY1esLrVVKfW_Rx7_5HGuIbtJQvvSG27dF4-qlyWvru789BKbEW1n655lw3shwxlXehww80fQwUHKx2E-RImmVSMgceksnivQohR72-BTF8ncF_ORtH9JEh0_xS; fid=4a1725d7-3e2c-48f8-b63b-d1917c47386d; ; QN271=ccae1170-f1b8-4694-bd78-b39aefa3c8fcQN43=2; QN42=yoyo963852741; _q=U.ghhbqhd4853; _t=27102901; csrfToken=ia6viiJCEGcs4wwX0fuOKlrjQXhGSIGm; _s=s_MNZ3NM2G5TYIFGIPRWZQHTYOJA; _v=g6Gn4HQs4XX_xysyibOqXaJsFibl7cvTdw4XrFPVVTFixyYQ_TJii8tID-UA5eHMWigocXPsLf7yI1qK7AAIChD-hSwCjm87jeQaWzVGBKorFn9JqWjp3ZYBEoYWQWYyvV7VYdiN3RCrXjd9h6cL9Y1UGM-5NUz5dRZInCUuZKLh; _vi=lknzn7TmjOuAi57Dj14LM0EBurckT_biAygB3GeVE_0W8H8OPhN0BII5VTp1rRf5hvQ29R7ToqOv8fwBh6n2lwFQSpsG16isFGsB23PDlKJgfecStWri3wCplJMZlxWuHACr6gqfMGcxrbMusgAUliEVMnBpgPKonU_vNmkEOYQE; ; QN271=ccae1170-f1b8-4694-bd78-b39aefa3c8fcQN48=2515d377-c5cc-4a60-a93e-b011c48096e7; QN300=touch.qunar.com; QN48=000073002f1031fe3470f75b; QN667=A; QN48=76a887bc-26dc-4271-aaf2-4a625d37cc88; F234=1619607694072; F235=1619607694072; QN668=51%2C56%2C51%2C59%2C56%2C50%2C57%2C56%2C59%2C54%2C50%2C59%2C50; ";
			
			String url = "https://touch.qunar.com/lowFlight/flightList?env=1&queryTimes=0&dep=%E5%8C%97%E4%BA%AC&arr=%E5%B9%BF%E5%B7%9E&showDistribution=true&flightType=1&accurateDateNames=5%E6%9C%8822%E6%97%A5&startDate=2021-05-22&endDate=2021-05-22&tag=-1&tagNames=&userSelectStr=5%E6%9C%8822%E6%97%A5&catCopy=&cat=&fromNative=&orderByTime=0&orderByPrice=2&orderByDirect=false&monitorLowestPrice=0&scrollToload=true&showLeto=true&weeks=&weekList=&direct=false&monitorTime=0&roundOrderByVacation=0&roundOrderByDirect=0&selectMap=";
			
			logger.info("开始查询航班列表******");
			
			//查询航班列表
			
			buffer.setLength(0);
			
			buffer.append("<feeye-official>");
			buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
			buffer.append("<url>/lowFlightInterface/api/getFlightAsyncInfo</url> ");
			buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
			buffer.append("<isHttps>true</isHttps>");
			buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
			buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
			buffer.append("<method>POST</method>");
			buffer.append("<encod>utf-8</encod> ");
			                       //{"b":{"env":1,"queryTimes":0,"dep":"北京","arr":"广州","showDistribution":true,"flightType":"1","startDate":"2021-05-22","endDate":"2021-05-22","userSelectStr":"05月22日","tag":"-1","tagNames":"5月","days":4,"timeout":8000,"simpleData":"yes","weeks":[],"weekList":[],"catCopy":"touchjpgg_lowFlight-search-box","cat":"touchjpgg_lowFlight-search-box","orderByTime":0,"orderByPrice":2,"orderByDirect":false,"monitorLowestPrice":"0","scrollToload":true,"showLeto":true,"monitorTime":"0","showFullDate":true,"t":"f_urInfo_flight"},"c":{}}
			String json = "{\"b\":{\"env\":1,\"queryTimes\":0,\"dep\":\"北京\",\"arr\":\"广州\",\"showDistribution\":true,\"flightType\":\"1\",\"startDate\":\"2021-05-22\",\"endDate\":\"2021-05-22\",\"userSelectStr\":\"05月22日\",\"tag\":\"-1\",\"tagNames\":\"5月\",\"days\":4,\"timeout\":8000,\"simpleData\":\"yes\",\"weeks\":[],\"weekList\":[],\"catCopy\":\"touchjpgg_lowFlight-search-box\",\"cat\":\"touchjpgg_lowFlight-search-box\",\"orderByTime\":0,\"orderByPrice\":2,\"orderByDirect\":false,\"monitorLowestPrice\":\"0\",\"scrollToload\":true,\"showLeto\":true,\"monitorTime\":\"0\",\"showFullDate\":true,\"t\":\"f_urInfo_flight\"},\"c\":{}}";
			
			buffer.append("<json>");
			buffer.append(json);
			buffer.append("</json>");
			
			buffer.append("<headers>");
			buffer.append("<head name='Host'>touch.qunar.com</head >");
			buffer.append("<head name='Origin'>https://touch.qunar.com</head >");
			buffer.append("<head name='Cookie'>"+cookie+"</head >");
			buffer.append("<head name='Content-Type'>application/json; charset=UTF-8</head >");
			buffer.append("<head name='Referer'>"+url+"</head >");
			buffer.append("<head name='User-Agent'>Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Mobile Safari/537.36</head>");
			buffer.append("</headers>");
			buffer.append("</feeye-official>");
			
			//发送第一次请求
			content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
			
			rs1 = content1.split("#@_@#");
			
			if(rs1.length > 1){
				content1 = rs1[1];
				cookie+= rs1[0];
			}
			logger.info(content1);
			String flightNoStr = "CZ3106";
			JSONObject resultJson = JSON.parseObject(content1);
			JSONArray flightList = resultJson.getJSONObject("data").getJSONArray("flightList");
			for (int i = 0; i < flightList.size(); i++) {
				JSONObject flightObject = flightList.getJSONObject(i);
				
				String flightNo = flightObject.getJSONObject("binfo").getString("flightNo");
				
				if(!flightNoStr.equals(flightNo)){
					continue;
				}
				
				String prefix = flightObject.getString("prefix");
				String urls [] = prefix.split("\\?");
				String u [] = urls[1].split("&");
				String newUrl = urls[0]+"?";
				String  touchToken = "";
				for (int j = 0; j < u.length; j++) {
					if(u[j].contains("extparams")){
						newUrl+="&extparams=%7b%22ex_track%22%3a%22AthenaUrban%22%7d";
					}else{
						if(j == 0){
						    newUrl+= u[j];
						}else{
							newUrl+= "&"+u[j];
						}
					}
					if(u[j].contains("touchToken")){
						touchToken = u[j].split("=")[1];
					}
				}
				
				
				
				logger.info(newUrl);
				
				String deail = "https://m.flight.qunar.com/ncs/page/flightdetail?startCity=%E5%8C%97%E4%BA%AC&destCity=%E5%B9%BF%E5%B7%9E&startDate=2021-05-22&code="+flightNoStr+"&touchToken="+touchToken+"&from=f_athena_urban&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&extparams={%22ex_track%22:%22AthenaUrban%22}&_firstScreen=1&_gogokid=12";
				
                buffer.setLength(0);
				buffer.append("<feeye-official>");
				buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
				buffer.append(
						"<url>/ncs/page/flightdetail?startCity=%E5%8C%97%E4%BA%AC&destCity=%E5%B9%BF%E5%B7%9E&startDate=2021-05-22&backDate=&code="
								+ flightNoStr + "&touchToken=" + touchToken
								+ "&from=f_athena_urban&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&extparams=%7b%22ex_track%22%3a%22AthenaUrban%22%7d</url> ");
				buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
				buffer.append("<isHttps>true</isHttps>");
				buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
				buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
				buffer.append("<method>get</method>");
				buffer.append("<encod>utf-8</encod> ");
				
				buffer.append("<headers>");
				buffer.append("<head name='Host'>m.flight.qunar.com</head >");
				buffer.append("<head name='Cookie'>"+cookie+"</head >");
				buffer.append("<head name='Referer'>"+url+"</head >");
				buffer.append("<head name='User-Agent'>Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Mobile Safari/537.36</head>");
				buffer.append("</headers>");
				buffer.append("</feeye-official>");
				
				//发送第一次请求
				content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
				rs1 = content1.split("#@_@#");
				String newCookie = rs1[0];
				content1 = rs1[1];
				
Pattern p=Pattern.compile("/flight_touch_react/prd/[a-zA-z]{1,20}@\\w{5,64}.js");
				
				Pattern p1=Pattern.compile("/quinn/prd/[a-zA-z]{1,20}@\\w{5,64}.js");

				
				String bundleUrl = "";
				String indexUrl = "";
				String flightDetailUrl = "";
				String qlognUrl = "";
				
Matcher matcher=p.matcher(content1);
				
				while(matcher.find()){
					String startStr=matcher.group();
					if(startStr.contains("bundle")){
						bundleUrl = startStr;
					}else if(startStr.contains("index")){
						indexUrl  = startStr;
					}else if(startStr.contains("flightDetail")){
						flightDetailUrl = startStr;
					}
				}
				
                Matcher matcher1=p1.matcher(content1);
				
				while(matcher1.find()){
					String startStr=matcher1.group();
					if(startStr.contains("qlogn")){
						qlognUrl = startStr;
					}
				}

				
				
				if(!StringUtils.isEmpty(newCookie)){
					cookie = cookie+""+newCookie;
				}
				
				String c[] =cookie.split(";");
				String qtime = "";
				for (int j = 0; j < c.length; j++) {
					if(c[j].contains("QN668")){
						qtime = URLDecoder.decode(c[j].split("=")[1],"utf-8");
					}
				}
				
				
				Document doc = Jsoup.parse(content1);
				
				String qunar_api_token = doc.getElementById("qunar_api_token").text();
				
				String ss [] = content1.split("'\\),'");
				
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
				
				if (StringUtils.isEmpty(sb.toString())) {
					ss = content1.split(";\\},'");

					for (int k = 0; k < ss.length; k++) {
						int len = ss[k].split("','").length;
						if (len > 10 && len < 15) {
							String cc[] = ss[k].split("','");
							for (int j = 0; j < cc.length; j++) {
								if (j < 11) {
									cc[j].split("':'");
									sb.append(cc[j].split("':'")[1] + ":");
								}
							}
						}
					}
				}
				
				String header = FingerPrintUtil.getQunarHeader(sb.toString());
				
				
				logger.info(header);
				
				buffer.setLength(0);
				
				
				String m = FingerPrintUtil.getQunarM(qunar_api_token,qtime);
				
				String token = FingerPrintUtil.getQunarToken(qunar_api_token,qtime);
				
				String ps [] = token.split(":");
				

				
				logger.info("开始查询航班详细价格******");
				
				buffer.setLength(0);
				
				buffer.append("<feeye-official>");
				buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
				buffer.append("<url>/flight/api/touchInnerOta</url> ");
				buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
				buffer.append("<isHttps>true</isHttps>");
				buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
				buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
				buffer.append("<method>post</method>");
				buffer.append("<encod>utf-8</encod> ");
				
				buffer.append("<params>");
				buffer.append("<param name='backDate'></param>");
				buffer.append("<param name='cat'>touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip</param>");
				
				buffer.append("<param name='code'>"+flightNoStr+"</param>");
				buffer.append("<param name='destCity'>广州</param>");
				buffer.append("<param name='extparams'>{\"ex_track\":\"AthenaUrban\"}</param>");
				buffer.append("<param name='from'>f_athena_urban</param>");
				buffer.append("<param name='startCity'>北京</param>");
				buffer.append("<param name='startDate'>2021-05-22</param>");
				buffer.append("<param name='touchToken'>"+touchToken+"</param>");
				buffer.append("<param name='reqSource'>touch</param>");
				
				long time = System.currentTimeMillis();
				
				buffer.append("<param name='_v'>2</param>");
				buffer.append("<param name='st'>"+time+"</param>");
				buffer.append("<param name='__m__'>"+m+"</param>");
				buffer.append("</params>");
				
				logger.info(cookie);
				
				buffer.append("<headers>");
				buffer.append("<head name='"+ps[0]+"'>"+ps[1]+"</head >");
				buffer.append("<head name='csht'></head >");
				buffer.append("<head name='pre'>"+header+"</head >");
				buffer.append("<head name='wps'>6</head >");
				buffer.append("<head name='Host'>m.flight.qunar.com</head >");
				buffer.append("<head name='Origin'>https://m.flight.qunar.com</head >");
				buffer.append("<head name='Cookie'>"+cookie+"</head >");
				buffer.append("<head name='Content-Type'>application/x-www-form-urlencoded</head >");
				buffer.append("<head name='Referer'>"+deail+"</head >");
				buffer.append("<head name='User-Agent'>Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Mobile Safari/537.36</head>");
				buffer.append("</headers>");
				buffer.append("</feeye-official>");
				
				//发送第一次请求
				String content = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
				
				logger.info(content);
				
				
				
				if(content.contains("1999")){

					deail = "https://m.flight.qunar.com/ncs/page/flightdetail?startCity=%E5%8C%97%E4%BA%AC&destCity=%E5%B9%BF%E5%B7%9E&startDate=2021-05-22&code="+flightNoStr+"&touchToken="+touchToken+"&from=f_athena_urban&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&extparams={%22ex_track%22:%22AthenaUrban%22}&_firstScreen=1&_gogokid=12";
					
	                buffer.setLength(0);
					buffer.append("<feeye-official>");
					buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
					buffer.append(
							"<url>/ncs/page/flightdetail?startCity=%E5%8C%97%E4%BA%AC&destCity=%E5%B9%BF%E5%B7%9E&startDate=2021-05-22&backDate=&code="
									+ flightNoStr + "&touchToken=" + touchToken
									+ "&from=f_athena_urban&cat=touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip&extparams=%7b%22ex_track%22%3a%22AthenaUrban%22%7d</url> ");
					buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
					buffer.append("<isHttps>true</isHttps>");
					buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
					buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
					buffer.append("<method>get</method>");
					buffer.append("<encod>utf-8</encod> ");
					
					buffer.append("<headers>");
					buffer.append("<head name='Host'>m.flight.qunar.com</head >");
					buffer.append("<head name='Cookie'>"+cookie+"</head >");
					buffer.append("<head name='Referer'>"+url+"</head >");
					buffer.append("<head name='User-Agent'>Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Mobile Safari/537.36</head>");
					buffer.append("</headers>");
					buffer.append("</feeye-official>");
					
					//发送第一次请求
					content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
					rs1 = content1.split("#@_@#");
					newCookie = rs1[0];
					content1 = rs1[1];
					
					
					if(!StringUtils.isEmpty(newCookie)){
						cookie = cookie+""+newCookie;
					}
					
					c =cookie.split(";");
					qtime = "";
					for (int j = 0; j < c.length; j++) {
						if(c[j].contains("QN668")){
							qtime = URLDecoder.decode(c[j].split("=")[1],"utf-8");
						}
					}
					
					
					doc = Jsoup.parse(content1);
					
					qunar_api_token = doc.getElementById("qunar_api_token").text();
					
					ss = content1.split("'\\),'");
					
					sb = new StringBuffer();
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
					
					if (StringUtils.isEmpty(sb.toString())) {
						ss = content1.split(";\\},'");

						for (int k = 0; k < ss.length; k++) {
							int len = ss[k].split("','").length;
							if (len > 10 && len < 15) {
								String cc[] = ss[k].split("','");
								for (int j = 0; j < cc.length; j++) {
									if (j < 11) {
										cc[j].split("':'");
										sb.append(cc[j].split("':'")[1] + ":");
									}
								}
							}
						}
					}
					
					header = FingerPrintUtil.getQunarHeader(sb.toString());
					
					
					logger.info(header);
					
					buffer.setLength(0);
					
					
					m = FingerPrintUtil.getQunarM(qunar_api_token,qtime);
					
					token = FingerPrintUtil.getQunarToken(qunar_api_token,qtime);
					
					ps = token.split(":");
					

					
					logger.info("开始查询航班详细价格******");
					
					buffer.setLength(0);
					
					buffer.append("<feeye-official>");
					buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
					buffer.append("<url>/flight/api/touchInnerOta</url> ");
					buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
					buffer.append("<isHttps>true</isHttps>");
					buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
					buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
					buffer.append("<method>post</method>");
					buffer.append("<encod>utf-8</encod> ");
					
					buffer.append("<params>");
					buffer.append("<param name='backDate'></param>");
					buffer.append("<param name='cat'>touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip</param>");
					
					buffer.append("<param name='code'>"+flightNoStr+"</param>");
					buffer.append("<param name='destCity'>广州</param>");
					buffer.append("<param name='extparams'>{\"ex_track\":\"AthenaUrban\"}</param>");
					buffer.append("<param name='from'>f_athena_urban</param>");
					buffer.append("<param name='startCity'>北京</param>");
					buffer.append("<param name='startDate'>2021-05-22</param>");
					buffer.append("<param name='touchToken'>"+touchToken+"</param>");
					buffer.append("<param name='reqSource'>touch</param>");
					
					time = System.currentTimeMillis();
					
					buffer.append("<param name='_v'>2</param>");
					buffer.append("<param name='st'>"+time+"</param>");
					buffer.append("<param name='__m__'>"+m+"</param>");
					buffer.append("</params>");
					
					logger.info(cookie);
					
					buffer.append("<headers>");
					buffer.append("<head name='"+ps[0]+"'>"+ps[1]+"</head >");
					buffer.append("<head name='csht'></head >");
					buffer.append("<head name='pre'>"+header+"</head >");
					buffer.append("<head name='wps'>6</head >");
					buffer.append("<head name='Host'>m.flight.qunar.com</head >");
					buffer.append("<head name='Origin'>https://m.flight.qunar.com</head >");
					buffer.append("<head name='Cookie'>"+cookie+"</head >");
					buffer.append("<head name='Content-Type'>application/x-www-form-urlencoded</head >");
					buffer.append("<head name='Referer'>"+deail+"</head >");
					buffer.append("<head name='User-Agent'>Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Mobile Safari/537.36</head>");
					buffer.append("</headers>");
					buffer.append("</feeye-official>");
					
					//发送第一次请求
					content = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
					
				}

				
				//logger.info(content);
				
				List<CabinInfo> listCabin = new ArrayList<CabinInfo>();
				
				String infos[] = content.split("\\\\\\\\\\\\\"title\\\\\\\\\\\\\"");
				if(infos.length <= 1){
					return requestRes;
				}
				for (int j = 0; j < infos.length; j++) {
					
					content = infos[j]+"\\\\\\\\\\\\\"title\\\\\\\\\\\\\"";
					
					Pattern p7=Pattern.compile("\\\\\\\\\\\\\"price\\\\\\\\\\\\\":\\\\\\\\\\\\\"(\\d{1,6})");
					
					Matcher matcher7=p7.matcher(content);
					
					CabinInfo cabinInfo = null;
					
					if(matcher7.find()){
						String price=matcher7.group(1);
						cabinInfo = new CabinInfo();
						cabinInfo.setPrice(price);
					}
					
					
					Pattern p6=Pattern.compile("(\\\\\"insurDesc\\\\\":\\\\\"([\u4E00-\u9FA5]{1,5})\\\\\"){0,1},\\\\\"insurPrice\\\\\":\\\\\"(\\d{1,4}){0,1}");
					
					Matcher matcher6=p6.matcher(content);
					
					while(matcher6.find()){
						String type=matcher6.group(2);
						String bx=matcher6.group(3);
						if(cabinInfo != null){
							if("航意险".equals(type)){
								cabinInfo.setPrice(Float.parseFloat(cabinInfo.getPrice())+Float.parseFloat(bx)+"");
							}
						}
					}
					
					
					
					Pattern p2=Pattern.compile("\\\\\\\\\\\\\"title\\\\\\\\\\\\\":\\\\\\\\\\\\\"([\u4E00-\u9FA5]{0,10}(：[\u4E00-\u9FA5]{0,10}){0,1})");
					
					Matcher matcher2=p2.matcher(content);
					
					while(matcher2.find()){
						String title=matcher2.group(1);
						if(cabinInfo != null){
							cabinInfo.setPriceType(title);
						}
					}
					
					Pattern p3=Pattern.compile("\\\\\\\\\\\\\"bookingParamKey\\\\\\\\\\\\\":\\\\\\\\\\\\\"(f_domestic_uniform_booking_CZ3106_[a-zA-Z0-9]{4}_[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})");
					
					Matcher matcher3=p3.matcher(content);
				
					while(matcher3.find()){
						String bookingParamKey=matcher3.group(1);
						if(cabinInfo != null){
							cabinInfo.setRequestId(bookingParamKey);
						}
					}
					
					
					Pattern p4=Pattern.compile("\\\\\\\\\\\\\"cabin\\\\\\\\\\\\\":\\\\\\\\\\\\\"([a-zA-Z0-9]{1,2})");
					
					Matcher matcher4=p4.matcher(content);
					
					while(matcher4.find()){
						String cabin=matcher4.group(1);
						if(cabinInfo != null){
							cabinInfo.setCabinCode(cabin);
						}
					}
					
					Pattern p5=Pattern.compile("\\\\\"pid\\\\\":\\d{1,15},\\\\\"token\\\\\":\\\\\"([a-zA-Z0-9]{32})\\\\\",\\\\\"expCode\\\\\":\\\\\"([a-zA-Z0-9]{1,20})\\\\\",\\\\\"productTypeCode\\\\\":\\\\\"([a-zA-Z0-9]{1,20})\\\\\"");
					
					Matcher matcher5=p5.matcher(content);
					
					
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
				
				time = System.currentTimeMillis();
				
				String req = "{\"sc\":\"北京\",\"dc\":\"广州\",\"st\":\"2021-05-22\",\"dt\":\"\",\"t\":"+time+",\"cb\":4,\"ft\":\"oneway\"}";
				
				req = URLEncoder.encode(req, "UTF-8");
				
				
				
                
                
                System.out.println(JSONObject.toJSON(listCabin));
                
                CabinInfo cabinInfo = listCabin.get(0);
                
                String tokens = cabinInfo.getComment();
                tokens = URLEncoder.encode(tokens, "UTF-8");
                String bookingParamKey = cabinInfo.getRequestId();
                
                buffer.setLength(0);
                
                logger.info("开始创建订单******");
                
                
                String cUrl = "https://m.flight.qunar.com/flight/tts/book?bookingParamKey="+bookingParamKey+"&realVendorType=1&searchParam="+req+"&choroToken=&tokens="+tokens+"&cat=lowFlight-search-box_urban_information_flow_oneWayTrip&";
                
				buffer.append("<feeye-official>");
				buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
				buffer.append("<url>/flight/tts/book?bookingParamKey="+bookingParamKey+"&realVendorType=1&searchParam="+req+"&choroToken=&tokens="+tokens+"&cat=lowFlight-search-box_urban_information_flow_oneWayTrip&</url> ");
				buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
				buffer.append("<isHttps>true</isHttps>");
				buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
				buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
				buffer.append("<method>get</method>");
				buffer.append("<encod>utf-8</encod> ");
				
				buffer.append("<headers>");
				
				buffer.append("<head name='Host'>m.flight.qunar.com</head >");
				buffer.append("<head name='Origin'>https://m.flight.qunar.com</head >");
				buffer.append("<head name='Cookie'>"+cookie+"</head >");
				buffer.append("<head name='Content-Type'>application/x-www-form-urlencoded</head >");
				buffer.append("<head name='Referer'>"+deail+"</head >");
				buffer.append("<head name='User-Agent'>Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Mobile Safari/537.36</head>");
				buffer.append("</headers>");
				buffer.append("</feeye-official>");
				
				//发送第一次请求
				content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
				
				rs1 = content1.split("#@_@#");
				newCookie = rs1[0];
				
				cookie = cookie + ";" +newCookie;
				
				infos = content1.split("<script type=\"text/javascript\">receive\\(");
				
				if(infos.length < 4){
					//失败情况，待完善
					//receive({"module":"home","config":{"loaded":true,"error":true,"errorInfo":{"code":1,"status":1,"des":"对不起，您预订的航班已经售完，请重新搜索预订"},"fromUrl":"/ncs/page/flightlist?depCity=北京&arrCity=广州&goDate=2021-05-22","group":"B","isLogin":true}})</script></html>#@_@#
					System.out.println("创单失败……"+content1);
					return requestRes;
				}
				
				StringBuffer newJson = new StringBuffer("{");
				
				String passengerId = "";
				String constructionFee = "";
				String fuelFee = "";
				String basePrice = "";
				String sellPriceType = "";
				String sellPriceTypeCN = "";
				String productPrice = "0";
						
				for (int j = 1; j < infos.length; j++) {
					
					String jsonValue = infos[j].replace(")</script>", "");
					
					if(jsonValue.contains("\"module\":\"passenger\"")){
						
						JSONObject jsObject = JSONObject.parseObject(jsonValue);
						JSONObject jsonconfig = jsObject.getJSONObject("config");
						JSONArray jsoncommonListData = jsonconfig.getJSONArray("commonListData");
						JSONObject passenger = jsoncommonListData.getJSONObject(0);
						
						passengerId = passenger.getString("id");
						passenger.put("selected", true);
						passenger.put("invalidDay", null);
						passenger.put("age", 35);
						newJson.append("\"passenger\":["+passenger.toJSONString()+"],");
						
					}else if(jsonValue.contains("\"module\":\"insure\"")){
						
						JSONObject jsObject = JSONObject.parseObject(jsonValue);
						JSONObject jsonconfig = jsObject.getJSONObject("config");
						JSONArray jsoninsurances = jsonconfig.getJSONArray("insurances");
						newJson.append("\"insure\":"+jsoninsurances.toJSONString()+",");
						
					}else if(jsonValue.contains("\"module\":\"contacter\"")){
						
						JSONObject jsObject = JSONObject.parseObject(jsonValue);
						JSONObject jsonconfig = jsObject.getJSONObject("config");
						JSONObject jsonautoFillContacter = jsonconfig.getJSONObject("autoFillContacter");
						newJson.append("\"contacter\":"+jsonautoFillContacter.toJSONString()+",");
						
					}else if(jsonValue.contains("\"module\":\"priceDetail\"")){
						
						JSONObject jsObject = JSONObject.parseObject(jsonValue);
						JSONObject jsonconfig = jsObject.getJSONObject("config");
						JSONObject jsonpriceInfo = jsonconfig.getJSONObject("priceInfo");
						newJson.append("\"priceBase\":"+jsonpriceInfo.toJSONString()+",");
						
					}else if(jsonValue.contains("\"module\":\"submit\"")){
						
						JSONObject jsObject = JSONObject.parseObject(jsonValue);
						JSONObject jsonconfig = jsObject.getJSONObject("config");
						JSONObject jsonstaticData = jsonconfig.getJSONObject("staticData");
						JSONObject jsonadult = jsonstaticData.getJSONObject("priceInfo").getJSONObject("adult");
						JSONObject jsongoPrice = jsonadult.getJSONObject("goPrice");
						constructionFee = jsongoPrice.getString("constructionFee");
						fuelFee = jsongoPrice.getString("fuelFee");
						basePrice = jsongoPrice.getString("basePrice");
						sellPriceType = jsongoPrice.getString("sellPriceType");
						String productPriceArray = jsonadult.getString("productPriceArray");
						//尊享飞服务 ¥29
						Pattern pp = Pattern.compile("([\u4E00-\u9FA5]{1,10}) ¥(\\d{1,6})");
						Matcher matcherP=pp.matcher(productPriceArray);
						if(matcherP.find()){
							sellPriceTypeCN = matcherP.group(1);
							productPrice = matcherP.group(2);
						}
						newJson.append("\"staticData\":"+jsonstaticData.toJSONString()+",");
						
					}
					
				}
				
				int allPrice = Integer.parseInt(constructionFee)+Integer.parseInt(fuelFee)+Integer.parseInt(basePrice)+Integer.parseInt(productPrice);

				String price = " \"allPrice\":" + allPrice + ",\"invoice\":null,\"price\":{\"amount\":" + allPrice
						+ ",\"BABY\":{\"name\":\"婴儿\",\"ticket\":[],\"tax\":0,\"fuel\":0,\"minus\":[],\"number\":0},\"ADU\":{\"name\":\"成人\",\"ticket\":[{\"price\":\""
						+ basePrice + "\",\"num\":1,\"name\":\"票价\",\"preText\":\"\",\"tag\":\"default\",\"priceTag\":\""
						+ sellPriceType + "\"}],\"tax\":0,\"fuel\":" + constructionFee
						+ ",\"minus\":[],\"number\":1},\"CHI\":{\"name\":\"儿童\",\"ticket\":[],\"tax\":0,\"fuel\":0,\"minus\":[],\"number\":0},\"insures\":{},\"insureMinus\":{},\"XProducts\":{},\"qmalls\":{\""
						+ sellPriceTypeCN + "\":{\"undefined\":{\"count\":1,\"price\":\"" + productPrice
						+ "\"}}},\"baggage\":{},\"common\":[],\"marketingList\":[],\"vipPriceList\":[]},";
			
				newJson.append(
						price + "\"hotel\":{\"selectHotelIndex\":0,\"maxRoomCount\":1,\"minRoomCount\":1,\"choseRoomNum\":0,\"roomInventory\":1,\"maxAvailNightsNum\":1,\"totalNightsNum\":1,\"packageRoomPrice\":0,\"config\":{\"hotelInfos\":[]}},\"insureMinusMap\":{},\"XProductMap\":{},\"qmallMap\":{\""
								+ passengerId + "\":{}},\"insureMap\":{\"" + passengerId
								+ "\":{\"combine_ins\":false,\"ins\":false,\"delay_ins\":false}},\"serverStorage\":{},\"customSource\":\"\",\"cashCoupon\":{},\"ouid\":\"\",\"cat\":\"touchjpgg_lowFlight-search-box_urban_information_flow_oneWayTrip\",\"viewData\":{\"viewTotalPrice\":\""
								+ allPrice + "\"}}");
				
				String jsonStr = (newJson.toString()).replaceAll("\n", " ").replaceAll("\r", " ").trim();
				
				logger.info(jsonStr);
				
                buffer.setLength(0);
                
                
                
                String parms = Base64.encodeToString(jsonStr.getBytes());
                
                
				buffer.append("<feeye-official>");
				buffer.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
				                  ///flight/tts/createOrder?slen=44003&_t=1620389431034&newBooking=true
				buffer.append("<url>/flight/tts/createOrder?slen="+(newJson.toString().length())+"&_t="+System.currentTimeMillis()+"&newBooking=true</url> ");
				buffer.append("<type>".concat(grabCondition.getGrabType()+"").concat("</type>"));
				buffer.append("<isHttps>true</isHttps>");
				buffer.append("<isDes>true</isDes>");
				buffer.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
				buffer.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
				buffer.append("<method>post</method>");
				buffer.append("<encod>utf-8</encod> ");
				
				buffer.append("<json>");
				buffer.append(parms);
				buffer.append("</json>");
				
				buffer.append("<headers>");
				
				buffer.append("<head name='Host'>m.flight.qunar.com</head >");
				
				buffer.append("<head name='Content-Type'>application/json</head >");
				buffer.append("<head name='Origin'>https://m.flight.qunar.com</head >");
				buffer.append("<head name='Cookie'>"+cookie+"</head >");
				buffer.append("<head name='Referer'>"+cUrl+"</head >");
				buffer.append("<head name='User-Agent'>Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Mobile Safari/537.36</head>");
				buffer.append("</headers>");
				buffer.append("</feeye-official>");
				
				//发送第一次请求
				content1 = new FeeyeHttpClient(ParamsParser.parse(buffer.toString()),grabCondition).executeHttpsResponse();
				
				logger.info(content1);
				
			}
			
			
			
		} catch (Exception e) {
			logger.error("doRequest",e);
		}
		return requestRes;
	}
	
	@Override
	protected GrabResult doParse(Map<String, Object> requestRes) throws GrabException {
		GrabResult grabResult = new GrabResult(grabCondition);
		
		String content = (String) requestRes.get("content");
		if(content == null || "".equals(content)){
			logger.error("无数据可解析，返回空结果");	
			grabResult.setSuccess(false);
			return grabResult;
		}else if(content.contains("traceid")){
			logger.error("验证码拦截，直接返回空结果");
			grabResult.setSuccess(false);
			return grabResult;
		}else if(content.contains("_errorMsg")){
			logger.error("ip被封或请求参数错误,返回结果");
			grabResult.setSuccess(false);
			return grabResult;
		}
		String[] contentArr = content.split("#@_@#");
		
		
		try {
			
		} catch (Exception e) {
			logger.error("doParse",e);
		}
		return grabResult;
	}
	

	public String getDeparture() {
		return departure;
	}
	public void setDeparture(String departure) {
		this.departure = departure;
	}
	public String getArrival() {
		return arrival;
	}
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}
}
*/
