//package com.feeye.service.processor;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import org.apache.log4j.Logger;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import com.alibaba.fastjson.JSON;
//import com.feeye.grab.bean.GrabResult;
//import com.feeye.grab.bean.grabCondition.GrabCondition;
//import com.feeye.official.proxy.bean.CabinInfo;
//import com.feeye.official.proxy.bean.FlightInfo;
//import com.feeye.official.proxy.common.Constants;
//import com.feeye.official.proxy.common.ParamsParser;
//import com.feeye.official.proxy.components.FeeyeHttpClient;
//import com.feeye.official.proxy.exception.GrabException;
//import com.feeye.official.proxy.inter.Executor;
//import com.feeye.official.proxy.util.FingerPrintUtil;
//import com.feeye.util.StringUtil;
//
///**
// * 天津航空的政策抓取
// * @author Administrator
// * @Date 2018.06.14
// *
// */
//public class GSProcessor extends Executor{
//
//	private static final Logger logger = Logger.getLogger(GSProcessor.class);
//	private String departure;
//	private String arrival;
//
//	public GSProcessor(GrabCondition grabCondition) {
//		super(grabCondition);
//	}
//
//
//
//	public static void main(String[] args) {
//		GrabCondition grabCondition = new GrabCondition();
//		grabCondition.setAirline("GS");
//		grabCondition.setFlightline("TSN-HGH");
//		grabCondition.setFlyDate("2019-07-25");
//		grabCondition.setGrabType(3);
//		grabCondition.setProxyIP("222.220.64.118:57114");
//		GSProcessor u3 = new GSProcessor(grabCondition);
//		try {
//				Map<String, Object> map = new HashMap<String,Object>();
//				map = u3.doRequest();
////				String content = FileUtil.readFile("C:\\Users\\Administrator\\Desktop\\GS(1).txt");
////				map.put("content", content);
//				GrabResult result = u3.doParse(map);
////				System.out.println(result.getPolicydata().size());
//				logger.info("查询回来的结果:"+JSON.toJSONString(result));
//		} catch (GrabException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	@Override
//	protected Map<String, Object> doRequest() throws GrabException{
//		Map<String, Object> requestRes = new HashMap<String, Object>();
//		try {
//			String[] pairs = grabCondition.getFlightline().split("-");
//			departure = pairs[0];
//			arrival = pairs[1];
//			String content = "";
//			String proxyIp = "";
//			String proxyPort = "";
//			if(grabCondition.getProxyIP() !=null){
//				pairs = grabCondition.getProxyIP().split(":");
//				proxyIp = pairs[0];
//				proxyPort = pairs[1];
//			}
//
//				String session = UUID.randomUUID().toString();
//		        StringBuilder sb = new StringBuilder();
//		        String cookie = "";
//
//		        sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>GET</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encod>utf-8</encod> ");
//				sb.append("<headers>");
//				sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//				sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//				sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//				String rs[] = content.split("#@_@#");
//
//				if(rs.length == 2){
//					cookie = rs[0];
//					content = rs[1];
//				}
//
//
//				String newCookie = cookie;
//				String cookies[] = cookie.split(";");
//				String csrfToken = "";
//				for (int i = 0; i < cookies.length; i++) {
//					if(cookies[i].contains("csrfToken")){
//						csrfToken = cookies[i].replace(" Path=/", "").split("=")[1];
//					}
//				}
//
//				newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//
//
//
//				Thread.sleep(7000);
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/public/image/home/20190705001.jpg</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//				sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//				sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
//				sb.append("<head name='Connection'>keep-alive</head>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//				rs = content.split("#@_@#");
//				if (rs.length == 2) {
//
//					cookies = newCookie.split(";");
//
//					newCookie =  "";
//
//					for (int i = 0; i < cookies.length; i++) {
//						if(!cookies[i].contains("N_V_IN")){
//							newCookie = newCookie + cookies[i]+";";
//						}
//					}
//
//					newCookie = newCookie +rs[0];
//				}
//				newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/api/schema/base</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//
//
//
//
//
//				String JSESSIONID = "";
//				for(int i=0;i<4;i++){
//					if(StringUtil.isEmpty(JSESSIONID)){
//						JSESSIONID = getJSESSIONID(session, proxyIp, proxyPort, newCookie, csrfToken);
//					}else{
//						break;
//					}
//				}
//				if(StringUtil.isEmpty(JSESSIONID)){
//					requestRes.put("content", "JSESSIONID没拿到");
//					return requestRes;
//				}
//
//				newCookie =  "";
//
//				for (int i = 0; i < cookies.length; i++) {
//					if(!cookies[i].contains("N_V_IN")){
//						newCookie = newCookie + cookies[i]+";";
//					}
//				}
//
//				newCookie = newCookie +JSESSIONID;
//
//				newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/api/information/getCitys</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/api/information/getCitys</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/api/information/getCitys</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/api/information/getCitys</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/api/information/getCitys</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//
//
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/public/image/home/20190705002.jpg</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/api/recommendLine/recomendLindSearch</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>post</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<json>");
//				String json = "{\"regions\":[\"SOUTH_CHINA\"],\"size\":10,\"_csrf\":\""+csrfToken+"\"}";
//				sb.append(json);
//				sb.append("</json>");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//				rs = content.split("#@_@#");
//				String ccCookie = "";
//				if (rs.length == 2) {
//					ccCookie = rs[0];
//				}
//
//	            cookies = newCookie.split(";");
//
//				newCookie =  "";
//
//				for (int i = 0; i < cookies.length; i++) {
//					if(!cookies[i].contains("N_V_IN") && !cookies[i].contains("route")){
//						newCookie = newCookie + cookies[i]+";";;
//					}
//				}
//
//				newCookie = newCookie + ccCookie;
//				newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/public/image/home/20190705008.jpg</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//				rs = content.split("#@_@#");
//				if (rs.length == 2) {
//
//					cookies = newCookie.split(";");
//
//					newCookie =  "";
//
//					for (int i = 0; i < cookies.length; i++) {
//						if(!cookies[i].contains("N_V_IN")){
//							newCookie = newCookie + cookies[i]+";";
//						}
//					}
//
//					newCookie = newCookie +rs[0];
//				}
//				newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/public/image/home/20190705003.jpg</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Accept'>application/json, text/plain, */*</head>");
//				sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//				sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//				sb.append("<head name='Connection'>keep-alive</head>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//
//
//
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/public/image/home/20190704003.jpg</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Accept'>application/json, text/plain, */*</head>");
//				sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//				sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//				sb.append("<head name='Connection'>keep-alive</head>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/public/image/home/20190705004.png</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Accept'>application/json, text/plain, */*</head>");
//				sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//				sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//				sb.append("<head name='Connection'>keep-alive</head>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//
//
//
//
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/public/image/home/20190705005.png</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Accept'>application/json, text/plain, */*</head>");
//				sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//				sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//				sb.append("<head name='Connection'>keep-alive</head>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//
//
//
//
//
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/public/image/home/20190705009.png</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Accept'>application/json, text/plain, */*</head>");
//				sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//				sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//				sb.append("<head name='Connection'>keep-alive</head>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//
//
//
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/public/image/home/20190705007.jpg</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Accept'>application/json, text/plain, */*</head>");
//				sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//				sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//				sb.append("<head name='Connection'>keep-alive</head>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//				rs = content.split("#@_@#");
//				if (rs.length == 2) {
//
//					cookies = newCookie.split(";");
//
//					newCookie =  "";
//
//					for (int i = 0; i < cookies.length; i++) {
//						if(!cookies[i].contains("N_V_IN")){
//							newCookie = newCookie + cookies[i]+";";
//						}
//					}
//
//					newCookie = newCookie +rs[0];
//				}
//				newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/api/fareTrend/airFareTrends?org="+departure+"&dst="+arrival+"&startDate=2019-07-01&endDate=2019-08-31&airline=GS</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>get</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<headers>");
//				sb.append("<head name='Connection'>keep-alive</head>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//				rs = content.split("#@_@#");
//
//				logger.info(content);
//
//				if (rs.length == 2) {
//
//					cookies = newCookie.split(";");
//
//					newCookie =  "";
//
//					for (int i = 0; i < cookies.length; i++) {
//						if(!cookies[i].contains("N_V_IN")){
//							newCookie = newCookie + cookies[i]+";";
//						}
//					}
//
//					newCookie = newCookie +rs[0];
//				}
//				newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//
//
//
//
//				sb.setLength(0);
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/flight/select.html</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>post</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<params>");
//				sb.append("<param name='ADT'>1</param>");
//				sb.append("<param name='CNN'>0</param>");
//				sb.append("<param name='INF'>0</param>");
//				sb.append("<param name='PWD'>0</param>");
//				sb.append("<param name='MWD'>0</param>");
//				sb.append("<param name='cabin'>Economy</param>");
//				sb.append("<param name='_csrf'>"+csrfToken+"</param>");
//				sb.append("<param name='type'>0</param>");
//				sb.append("<param name='origin'>"+departure+"</param>");
//				sb.append("<param name='destination'>"+arrival+"</param>");
//				sb.append("<param name='departureDate'>"+grabCondition.getFlyDate()+"</param>");
//				sb.append("<param name='arrivalDate'></param>");
//				sb.append("</params>");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//
//				rs = content.split("#@_@#");
//
//				if (rs.length == 2) {
//					ccCookie = rs[0];
//				}
//
//	            cookies = newCookie.split(";");
//
//				newCookie =  "";
//
//				for (int i = 0; i < cookies.length; i++) {
//					if(!cookies[i].contains("N_V_IN")){
//						newCookie = newCookie + cookies[i]+";";;
//					}
//				}
//
//				newCookie = newCookie + ccCookie;
//				newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//
//
//
//				logger.info(newCookie);
//
//				String desc= FingerPrintUtil.getDesc();
//
//
//				//desc = "coBPtm4BZy5Ly7E1arnlj4vyFx4IUisDADFJEBhSGfIXYl/elXICeY115jwY6dt2";
//
//				String json1 = "{\"passenger\":{\"ADT\":1,\"CNN\":0,\"INF\":0,\"PWD\":0,\"MWD\":0}," +
//						"\"originDestinations\":[{\"origin\":\""+departure+"\",\"destination\":\""+arrival+
//						"\",\"departureDate\":\""+grabCondition.getFlyDate()+"\"}],\"cabin\":\"Economy\",\"offset\":3,\"_csrf\":\""+
//						csrfToken+"\",\"desc\":\""+desc+"\"}";
//
//				sb = new StringBuilder();
//				sb.append("<feeye-official>");
//				sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//				sb.append("<url>http://www.tianjin-air.com/api/airLowFareSearch/search</url> ");
//				sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//				sb.append("<method>post</method>");
//				sb.append("<session>"+session+"</session>");
//				sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//				sb.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
//				sb.append("<encode>utf-8</encode> ");
//				sb.append("<json>");
//				sb.append(json1);
//				sb.append("</json>");
//				sb.append("<headers>");
//				sb.append("<head name='Cookie'>"+newCookie+"</head>");
//				sb.append("<head name='Host'>www.tianjin-air.com</head>");
//				sb.append("<head name='Referer'>http://www.tianjin-air.com/flight/select.html</head>");
//				sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//				sb.append("</headers>");
//				sb.append("</feeye-official>");
//				content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//
//				logger.info(content);
//
//
//
//
//
//				if(content.contains("#@_@#")){
//					content = content.split("#@_@#")[1];
//				}
//				if(content.contains("data")){
//					requestRes.put("content", content);
//					return requestRes;
//				}else if(content.contains("10001")){
//					requestRes.put("content", "价格更新没这么快，逛逛首页了解更多");
//					return requestRes;
//				}else if(content.contains("10000") || content.contains("10002")){
//					sb.setLength(0);
//					sb.append("<feeye-official>");
//					sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//					sb.append("<url>http://www.tianjin-air.com/hnatravel/imagecodeajax?v=2</url> ");
////					sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//					sb.append("<type>0</type> ");
//					sb.append("<codeUrl>true</codeUrl>");
//					sb.append("<codeParseType>1</codeParseType>");
//					sb.append("<method>get</method>");
//					sb.append("<session>"+session+"</session>");
//					sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//					sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//					sb.append("<encode>utf-8</encode> ");
//					sb.append("<headers>");
//					sb.append("<head name='Connection'>keep-alive</head>");
//					sb.append("<head name='Cookie'>"+newCookie+"</head>");
//					sb.append("<head name='Host'>www.tianjin-air.com</head>");
//					sb.append("<head name='Referer'>http://www.tianjin-air.com/flight/select.html</head>");
//					sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//					sb.append("</headers>");
//					sb.append("</feeye-official>");
//					content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//					rs = content.split("#@_@#");
//					String ci="";
//					String ticketcode = "";
//					if (rs.length == 2) {
//						ticketcode = rs[1];
//						String ck [] = rs[0].split(";");
//						for (int i = 0; i < ck.length; i++) {
//							if(ck[i].contains("ci")){
//								ci = ck[i];
//							}
//						}
//					}
//
//					if(StringUtil.isEmpty(ticketcode)){
//						requestRes.put("content", "验证码的值没拿到");
//						return requestRes;
//					}
//					newCookie = newCookie + ci+";";
//					newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//					/*FingerPrintUtil.addFingerid();*/
//					desc=FingerPrintUtil.getDesc();
//					json1 = "{\"passenger\":{\"ADT\":1,\"CNN\":0,\"INF\":0,\"PWD\":0,\"MWD\":0}," +
//							"\"originDestinations\":[{\"origin\":\""+departure+"\",\"destination\":\""+arrival+
//							"\",\"departureDate\":\""+grabCondition.getFlyDate()+"\"}],\"cabin\":\"Economy\",\"offset\":3,\"_csrf\":\""+
//							csrfToken+"\",\"vc\":\""+ticketcode+"\",\"desc\":\""+desc+"\"}";
//					sb = new StringBuilder();
//					sb.append("<feeye-official>");
//					sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//					sb.append("<url>http://www.tianjin-air.com/api/airLowFareSearch/search</url> ");
//					sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//					sb.append("<method>post</method>");
//					sb.append("<session>"+session+"</session>");
//					sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//					sb.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
//					sb.append("<encode>utf-8</encode> ");
//					sb.append("<json>");
//					sb.append(json1);
//					sb.append("</json>");
//					sb.append("<headers>");
//					sb.append("<head name='Accept'>application/json, text/plain, */*</head>");
//					sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//					sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//					sb.append("<head name='Connection'>keep-alive</head>");
//					sb.append("<head name='Content-Type'>application/json;charset=utf-8</head>");
//					sb.append("<head name='Cookie'>"+newCookie+"</head>");
//					sb.append("<head name='Host'>www.tianjin-air.com</head>");
//					sb.append("<head name='Referer'>http://www.tianjin-air.com/flight/select.html</head>");
//					sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//					sb.append("</headers>");
//					sb.append("</feeye-official>");
//					content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//					rs = content.split("#@_@#");
//					if(content.contains("#@_@#")){
//						content = content.split("#@_@#")[1];
//					}
//					String pa = "";
//					pa = rs[0];
//					for(int i=0;i<10;i++){
//						if(StringUtil.isEmpty(pa)){
//							Map<String,String> returnMap = getPa(session, proxyIp, proxyPort, newCookie, csrfToken, desc);
//							pa = returnMap.get("pa");
//							newCookie = returnMap.get("cookie");
//						}else{
//							break;
//						}
//					}
//					if(StringUtil.isEmpty(pa)){
//						requestRes.put("content", "pa没拿到");
//						return requestRes;
//					}
//
//					newCookie = newCookie + pa;
//					newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//					/*FingerPrintUtil.addFingerid();
//					desc=FingerPrintUtil.getDesc();*/
//					json1 = "{\"passenger\":{\"ADT\":1,\"CNN\":0,\"INF\":0,\"PWD\":0,\"MWD\":0}," +
//							"\"originDestinations\":[{\"origin\":\""+departure+"\",\"destination\":\""+arrival+
//							"\",\"departureDate\":\""+grabCondition.getFlyDate()+"\"}],\"cabin\":\"Economy\",\"offset\":3,\"_csrf\":\""+
//							csrfToken+"\",\"desc\":\""+desc+"\"}";
//					sb = new StringBuilder();
//					sb.append("<feeye-official>");
//					sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//					sb.append("<url>http://www.tianjin-air.com/api/airLowFareSearch/search</url> ");
//					sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//					sb.append("<method>post</method>");
//					sb.append("<session>"+session+"</session>");
//					sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//					sb.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
//					sb.append("<encode>utf-8</encode> ");
//					sb.append("<json>");
//					sb.append(json1);
//					sb.append("</json>");
//					sb.append("<headers>");
//					sb.append("<head name='Accept'>application/json, text/plain, */*</head>");
//					sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//					sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//					sb.append("<head name='Connection'>keep-alive</head>");
//					sb.append("<head name='Content-Type'>application/json;charset=utf-8</head>");
//					sb.append("<head name='Cookie'>"+newCookie+"</head>");
//					sb.append("<head name='Host'>www.tianjin-air.com</head>");
//					sb.append("<head name='Referer'>http://www.tianjin-air.com/flight/select.html</head>");
//					sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//					sb.append("</headers>");
//					sb.append("</feeye-official>");
//					content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//					if(content.contains("#@_@#")){
//						content = content.split("#@_@#")[1];
//					}
//
//					sb = null;
//					requestRes.put("content", content);
//
//				}
//
//		} catch (Exception e) {
////			logger.error("doRequest",e);
//			throw new GrabException(e);
//		}
//
//		return requestRes;
//	}
//
//	public String getJSESSIONID(String session,String proxyIp,String proxyPort,String newCookie,String csrfToken)  throws GrabException{
//		String JSESSIONID = "";
//		try{
//			StringBuffer sb = new StringBuffer();
//			sb.append("<feeye-official>");
//			sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//			sb.append("<url>http://www.tianjin-air.com/api/validate/captcha</url> ");
//			sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//			sb.append("<method>get</method>");
//			sb.append("<session>"+session+"</session>");
//			sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//			sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//			sb.append("<encode>utf-8</encode> ");
//			sb.append("<headers>");
//			sb.append("<head name='Cookie'>"+newCookie+"</head>");
//			sb.append("<head name='Host'>www.tianjin-air.com</head>");
//			sb.append("<head name='Referer'>http://www.tianjin-air.com/</head>");
//			sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//			sb.append("<head name='Accept'>*/*</head>");
//			sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//			sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//			sb.append("<head name='Connection'>keep-alive</head>");
//			sb.append("</headers>");
//			sb.append("</feeye-official>");
//			String content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//			String rs[] = content.split("#@_@#");
//			if (rs.length == 2) {
//				JSESSIONID =  rs[0];
//			}
//		}catch(Exception e){
//			throw new GrabException(e);
//		}
//		return JSESSIONID;
//	}
//
//	public Map<String,String> getPa(String session,String proxyIp,String proxyPort,String newCookie,String csrfToken,String desc) throws GrabException{
//		Map<String,String> returnMap = new HashMap<String,String>();
//		String pa = "";
//		try{
//			String cookies[] = newCookie.split(";");
//			newCookie =  "";
//			for (int i = 0; i < cookies.length; i++) {
//				if(!cookies[i].contains("ci")){
//					newCookie = newCookie + cookies[i]+";";
//				}
//			}
//			StringBuffer sb = new StringBuffer();
//			sb.setLength(0);
//			sb.append("<feeye-official>");
//			sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//			sb.append("<url>http://www.tianjin-air.com/hnatravel/imagecodeajax?v=2</url> ");
////			sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//			sb.append("<type>0</type> ");
//			sb.append("<codeUrl>true</codeUrl>");
//			sb.append("<codeParseType>1</codeParseType>");
//			sb.append("<method>get</method>");
//			sb.append("<session>"+session+"</session>");
//			sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//			sb.append("<proxyPort>"+proxyPort+"</proxyPort>");
//			sb.append("<encode>utf-8</encode> ");
//			sb.append("<headers>");
//			sb.append("<head name='Connection'>keep-alive</head>");
//			sb.append("<head name='Cookie'>"+newCookie+"</head>");
//			sb.append("<head name='Host'>www.tianjin-air.com</head>");
//			sb.append("<head name='Referer'>http://www.tianjin-air.com/flight/select.html</head>");
//			sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//			sb.append("</headers>");
//			sb.append("</feeye-official>");
//			String content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//			String rs[] = content.split("#@_@#");
//			String ci="";
//			String ticketcode = "";
//			if (rs.length == 2) {
//				ticketcode = rs[1];
//				String ck [] = rs[0].split(";");
//				for (int i = 0; i < ck.length; i++) {
//					if(ck[i].contains("ci")){
//						ci = ck[i];
//					}
//				}
//			}
//
//			if(StringUtil.isEmpty(ticketcode)){
//				returnMap.put("pa", "");
//				returnMap.put("cookie", newCookie);
//				return returnMap;
//			}
//			newCookie = newCookie + ci+";";
//			newCookie = newCookie.replaceAll(" Max-Age=3600; Path=/; HttpOnly", "").replaceAll(" Path=/;", "").replaceAll(" Path=/", "").replaceAll(" path=/", "").replaceAll(" HttpOnly", "");
//
//			String json1 = "{\"passenger\":{\"ADT\":1,\"CNN\":0,\"INF\":0,\"PWD\":0,\"MWD\":0}," +
//					"\"originDestinations\":[{\"origin\":\""+departure+"\",\"destination\":\""+arrival+
//					"\",\"departureDate\":\""+grabCondition.getFlyDate()+"\"}],\"cabin\":\"Economy\",\"offset\":3,\"_csrf\":\""+
//					csrfToken+"\",\"vc\":\""+ticketcode+"\",\"desc\":\""+desc+"\"}";
//			sb.setLength(0);
//			sb.append("<feeye-official>");
//			sb.append("<official>".concat(grabCondition.getAirline()).concat("</official>"));
//			sb.append("<url>http://www.tianjin-air.com/api/airLowFareSearch/search</url> ");
//			sb.append("<type>"+grabCondition.getGrabType()+"</type> ");
//			sb.append("<method>post</method>");
//			sb.append("<session>"+session+"</session>");
//			sb.append("<proxyIp>".concat(proxyIp).concat("</proxyIp>"));
//			sb.append("<proxyPort>".concat(proxyPort).concat("</proxyPort>"));
//			sb.append("<encode>utf-8</encode> ");
//			sb.append("<json>");
//			sb.append(json1);
//			sb.append("</json>");
//			sb.append("<headers>");
//			sb.append("<head name='Accept'>application/json, text/plain, */*</head>");
//			sb.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3</head>");
//			sb.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//			sb.append("<head name='Connection'>keep-alive</head>");
//			sb.append("<head name='Content-Type'>application/json;charset=utf-8</head>");
//			sb.append("<head name='Cookie'>"+newCookie+"</head>");
//			sb.append("<head name='Host'>www.tianjin-air.com</head>");
//			sb.append("<head name='Referer'>http://www.tianjin-air.com/flight/select.html</head>");
//			sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0</head>");
//			sb.append("</headers>");
//			sb.append("</feeye-official>");
//			content = new FeeyeHttpClient(ParamsParser.parse(sb.toString()),grabCondition).execute4Response();
//			rs = content.split("#@_@#");
//			pa = rs[0];
//			returnMap.put("pa", pa);
//			returnMap.put("cookie", newCookie);
//		}catch(Exception e){
//			throw new GrabException(e);
//		}
//		return returnMap;
//	}
//
//	@Override
//	protected GrabResult doParse(Map<String, Object> requestRes) throws GrabException {
//		GrabResult grabResult = new GrabResult(grabCondition);
//		String content = (String)requestRes.get("content");
//		if(null == content){
//			grabResult.setSuccess(false);
//			grabResult.setErrorCode(GrabResult.ERRORCODE_WRONGPAGE);
//			return grabResult;
//		}
//		if(content.contains("没拿到")){
//			grabResult.setSuccess(false);
//			grabResult.setErrorMessage(content);
//			return grabResult;
//		}else if(content.equals("价格更新没这么快，逛逛首页了解更多")){
//			grabResult.setSuccess(false);
//			grabResult.setErrorMessage(content);
//			return grabResult;
//		}
//		// 如果抓取没有返回正常页面
//		if(!checkRequestException(content, grabResult)){
//			return grabResult;
//		}
//		List<FlightInfo> flightList = new ArrayList<FlightInfo>();
//		try {
//			JSONObject result = new JSONObject(content);
//			content = null;
//			if(!result.has("success")){
//				grabResult.setSuccess(false);
//				grabResult.setErrorCode(GrabResult.ERRORCODE_WRONGPAGE);
//				return grabResult;
//			}
//			String flag = result.getString("success");
//			if(flag.equalsIgnoreCase("True")){
//				grabResult.setSuccess(true);
//			}
//			if(result.has("data")){
//				JSONObject data = result.getJSONObject("data");
//				if(data.has("originDestinations")){
//					JSONObject originDestinations = data.getJSONArray("originDestinations").getJSONObject(0);
//					if(originDestinations.has("airItineraries")){
//						JSONArray airItineraries = originDestinations.getJSONArray("airItineraries");
//						for (int i = 0; i < airItineraries.length(); i++) {
//							JSONObject json = airItineraries.getJSONObject(i);
//							JSONObject flightSegments = json.getJSONArray("flightSegments").getJSONObject(0);
//							String marketingAirlineCode = flightSegments.getString("marketingAirlineCode");
//							String operatingAirlineCode = flightSegments.getString("operatingAirlineCode");
//							//如果是共享数据，则不存
//							if(!grabCondition.getAirline().equalsIgnoreCase(marketingAirlineCode) && !grabCondition.getAirline().equalsIgnoreCase(operatingAirlineCode)){
//								continue;
//							}
//							String flightNumber = flightSegments.getString("flightNumber");
//							if(!flightNumber.contains(grabCondition.getAirline())){
//								flightNumber = grabCondition.getAirline()+flightNumber;
//							}
//							String departureDate = flightSegments.getString("departureDate");
//							String departureTime = flightSegments.getString("departureTime").substring(0, 5);
//							String arrivalDate = flightSegments.getString("arrivalDate");
//							String arrivalTime = flightSegments.getString("arrivalTime").substring(0, 5);
//							String departureAirportCode = flightSegments.getString("departureAirportCode");
//							String arrivalAirportCode = flightSegments.getString("arrivalAirportCode");
//							FlightInfo flight = new FlightInfo();
//							flight.setWebType(grabCondition.getAirline());
//							flight.setFlightNo(flightNumber);
//							flight.setDeparutreDate(departureDate);
//							flight.setDeparutreTime(departureTime);
//							flight.setArriveDate(arrivalDate);
//							flight.setArriveTime(arrivalTime);
//							flight.setDeparutre(departureAirportCode);
//							flight.setArrival(arrivalAirportCode);
////							JSONArray airItineraryPrices = json.getJSONArray("airItineraryPrices");
//							JSONObject airItineraryPrices = json.getJSONObject("airItineraryPrices");
//							JSONArray business = airItineraryPrices.getJSONArray("business");
//							JSONArray superEconomy = airItineraryPrices.getJSONArray("superEconomy");
//							JSONArray economy = airItineraryPrices.getJSONArray("economy");
//							List<CabinInfo> cabinList = new ArrayList<CabinInfo>();
//							for (int j = 0; j < business.length(); j++) {
//								JSONObject cabin = business.getJSONObject(j);
//								String fareFamilyCode = cabin.getString("fareFamilyCode");
//								/*if(fareFamilyCode.equalsIgnoreCase("GEFLEX")){//全价经济舱不展示
//									continue;
//								}*/
//								JSONObject farePrices = cabin.getJSONArray("travelerPrices").getJSONObject(0)
//										.getJSONArray("farePrices").getJSONObject(0);
//								String baseFare = farePrices.getString("baseFare");
//								String bookingClass = farePrices.getString("bookingClass");
//								//座位数
//								String inventoryQuantity = farePrices.getString("inventoryQuantity");
//								String inventoryStatus  = farePrices.getString("inventoryStatus");
//								String seat = inventoryQuantity;
//								if(!StringUtil.isEmpty(inventoryStatus) && inventoryStatus.equalsIgnoreCase("Available")){
//									seat = 10+"";
//								}
//								CabinInfo cabinInfo = new CabinInfo();
//								cabinInfo.setCabinCode(bookingClass);
//								cabinInfo.setPriceType(fareFamilyCode);
//								cabinInfo.setPrice(baseFare);
//								cabinInfo.setLastSeat(seat);
//								cabinList.add(cabinInfo);
//							}
//							for (int j = 0; j < superEconomy.length(); j++) {
//								JSONObject cabin = superEconomy.getJSONObject(j);
//								String fareFamilyCode = cabin.getString("fareFamilyCode");
//								/*if(fareFamilyCode.equalsIgnoreCase("GEFLEX")){//全价经济舱不展示
//									continue;
//								}*/
//								JSONObject farePrices = cabin.getJSONArray("travelerPrices").getJSONObject(0)
//										.getJSONArray("farePrices").getJSONObject(0);
//								String baseFare = farePrices.getString("baseFare");
//								String bookingClass = farePrices.getString("bookingClass");
//								//座位数
//								String inventoryQuantity = farePrices.getString("inventoryQuantity");
//								String inventoryStatus  = farePrices.getString("inventoryStatus");
//								String seat = inventoryQuantity;
//								if(!StringUtil.isEmpty(inventoryStatus) && inventoryStatus.equalsIgnoreCase("Available")){
//									seat = 10+"";
//								}
//								CabinInfo cabinInfo = new CabinInfo();
//								cabinInfo.setCabinCode(bookingClass);
//								cabinInfo.setPriceType(fareFamilyCode);
//								cabinInfo.setPrice(baseFare);
//								cabinInfo.setLastSeat(seat);
//								cabinList.add(cabinInfo);
//							}
//							for (int j = 0; j < economy.length(); j++) {
//								JSONObject cabin = economy.getJSONObject(j);
//								String fareFamilyCode = cabin.getString("fareFamilyCode");
//								/*if(fareFamilyCode.equalsIgnoreCase("GEFLEX")){//全价经济舱不展示
//									continue;
//								}*/
//								JSONObject farePrices = cabin.getJSONArray("travelerPrices").getJSONObject(0)
//										.getJSONArray("farePrices").getJSONObject(0);
//								String baseFare = farePrices.getString("baseFare");
//								String bookingClass = farePrices.getString("bookingClass");
//								//座位数
//								String inventoryQuantity = farePrices.getString("inventoryQuantity");
//								String inventoryStatus  = farePrices.getString("inventoryStatus");
//								String seat = inventoryQuantity;
//								if(!StringUtil.isEmpty(inventoryStatus) && inventoryStatus.equalsIgnoreCase("Available")){
//									seat = 10+"";
//								}
//								CabinInfo cabinInfo = new CabinInfo();
//								cabinInfo.setCabinCode(bookingClass);
//								cabinInfo.setPriceType(fareFamilyCode);
//								cabinInfo.setPrice(baseFare);
//								cabinInfo.setLastSeat(seat);
//								cabinList.add(cabinInfo);
//							}
//							flight.setCabins(cabinList);
//							flightList.add(flight);
//						}
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			throw new GrabException(e);
////			logger.error("doParse",e);
////			logger.error("content:"+content);
//		}
//		grabResult.setSuccess(true);
//		grabResult.setPolicydata(transferToPolicyDatasVO(flightList));
//		return grabResult;
//	}
//
//
//
//}
