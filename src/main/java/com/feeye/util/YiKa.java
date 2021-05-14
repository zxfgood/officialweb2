/*
package com.feeye.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

*/
/**
 * @description: 千万卡手机验证码! yika66.com
 * @author: domcj
 * @date: 2018/12/12 15:48
 *//*

public class YiKa {

	public static final Logger logger=Logger.getLogger(YiKa.class);
//	public static final String loginAccount = "feeyesb";
//	public static final String loginPassword = "feeyesb";
	public static final Integer xmid = 3028;   //项目编码
	public static final int timeout = 30*1000;
	public static final String charset = "UTF-8";
	public static final String url_req = "http://kapi.yika66.com:20153/User/";
	public static final String login_reqType = "login";
	public static final String phoneNumber_reqType = "getPhone"; //获取号码
//	public static final String numberAgain_reqType = "GetTaskStr";  //重复获取
	public static final String code_reqType = "getMessage";    //获取验证码
//	public static final String codeAgain_reqType = "GetYzmLogStr";  //重复获取
	public static final String release_reqType = "releasePhone";    //释放号码
	public static final String blackList_reqType = "addBlack";   //加入黑名单

	public static String getSmsInfo(String loginAccount,String loginPassword) {
		String result = null;
		try {

			Map<String, Object> loginMap = new LinkedHashMap<>();
			loginMap.put("uName", loginAccount);
			loginMap.put("pWord", loginPassword);
			String token = sendHttpRequest(url_req+login_reqType, loginMap);  //获取登陆token
			String phoneNumber = getPhoneNumber(token, loginMap);
			if (phoneNumber==null) {
				phoneNumber = getPhoneNumber(token, loginMap);   //获取手机号重试一次
			}
			if ("-8".equals(phoneNumber)) {
				return phoneNumber;
			}
			loginMap.clear();
			loginMap.put("token", token);
			loginMap.put("Phone", phoneNumber);
			loginMap.put("ItemId", xmid);
			String smsCode = null;
			try {
				int retryTimes = 0;
				while (++retryTimes<=15) {
					smsCode = sendHttpRequest(url_req+code_reqType, loginMap);  //获取验证码
					if ("1".equals(smsCode)) {    //1代表还没收到验证信息
						logger.info(smsCode+"还未获取到验证码，重试"+retryTimes);
						try {
							Thread.sleep(3*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
			} catch (Exception e) {
				logger.error( e.getMessage(), e);
//				smsCode = sendHttpRequest(url_req+codeAgain_reqType, loginMap);  //获取验证码调用超时或者丢包
			}
			if (smsCode.length()>4) {
				smsCode.substring(smsCode.length()-4, smsCode.length());
			}
			loginMap.clear();
			loginMap.put("token", token);
			loginMap.put("hm", phoneNumber);
			result = sendHttpRequest(url_req+release_reqType, loginMap);
			logger.info("释放号码返回结果"+result);  //1 成功释放
			loginMap.put("xmid", xmid);
			result = sendHttpRequest(url_req+blackList_reqType, loginMap);
			logger.info("加入黑名单返回结果"+result); //1 成功 0没登录或失败 -1增加失败 -2黑名单已存在
			logger.info("结果"+phoneNumber+"_"+smsCode);
			return phoneNumber+"_"+smsCode;
		} catch (Throwable e) {
			logger.error( e.getMessage(), e);
		}
		return null;
	}

	public static String getSmsCode(String token, String phoneNumber) {
		Map<String, Object> loginMap = new LinkedHashMap<>();
		loginMap.put("token", token);
		loginMap.put("Phone", phoneNumber);
		loginMap.put("ItemId", xmid);
		loginMap.put("code", "utf8");
		String smsCode = null;
		String result = null;
		try {
			int retryTimes = 0;
			while (++retryTimes<=5) {
				try {
					smsCode = sendHttpRequest(url_req+code_reqType, loginMap).replace(" ", "+");  //获取验证码
					logger.info("千万卡返回："+smsCode);
					if ((StringUtils.isEmpty(smsCode))||
						(!StringUtils.isEmpty(smsCode)&&!smsCode.contains("MSG"))) {    //1代表还没收到验证信息
						logger.info(smsCode+"还未获取到验证码，重试"+retryTimes);
						Thread.sleep(5*1000);
					} else {
						break;
					}
				} catch (Exception e) {
					logger.error("error",e);
					try {
						Thread.sleep(5*1000);
					} catch (InterruptedException e1) {
					}
				}
			}
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
		}
//		if(!StringUtil.isEmpty(smsCode)&&smsCode.contains("&")){
//			smsCode = smsCode.split("&")[3];
//		}
		result = getMathResult(smsCode);
//		if (smsCode.length()>4) {
//			smsCode = smsCode.substring(smsCode.length()-4, smsCode.length());
//		}
		logger.info("最终获取到的验证码:"+result);
		try {
			loginMap.clear();
			loginMap.put("token", token);
			loginMap.put("phoneList", xmid+"-"+phoneNumber);
			loginMap.put("code", "utf8");
			String agentResult = sendHttpRequest(url_req+blackList_reqType, loginMap);
//			if (!StringUtil.isEmpty(result)&&!result.contains("MSG")) {    //1代表还没收到验证信息
//				if(!StringUtil.isEmpty(result)&&result.contains("&")){
//					result = result.split("&")[3];
//				}
//				if (result.length()>4) {d
//					smsCode = smsCode.substring(smsCode.length()-4, smsCode.length());
//				}
			agentResult = getMathResult(result);
//				regex = "【东方航空】验证码：([0-9]+)";
//				pattern = Pattern.compile(regex);
//				m = pattern.matcher(result);
//				while(m.find()) {
//					if(StringUtils.isNotEmpty(m.group(1))) {
//						smsCode = m.group(1);
//						logger.info("smsCode:"+smsCode);
//					}
//				}
//			}
			logger.info("加入黑名单返回结果"+agentResult); //1 成功 0没登录或失败 -1增加失败 -2黑名单已存在
			loginMap.clear();
			loginMap.put("token", token);
			loginMap.put("phoneList", phoneNumber+"-"+xmid);
			loginMap.put("code", "utf8");
			String releaseResult = sendHttpRequest(url_req+release_reqType, loginMap);
			logger.info("释放号码返回结果"+releaseResult);  //1 成功释放
			logger.info("结果"+result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String getMathResult(String sms){
		String result = "";
		String regex = "【东方航空】您的动态验证码为：([0-9]+.[0-9]+)，请输入计算结果，感谢您对东航官网的支持！";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(sms);
		while(m.find()) {
			if(StringUtils.isNotEmpty(m.group(1))) {
				String smsCode = m.group(1);
				logger.info("smsCode:"+smsCode);
				if(smsCode.contains("+")){
					String[] results = smsCode.split("\\+");
					result = ((int)(Float.parseFloat(results[0]) + Float.parseFloat(results[1])))+"";
					logger.info("result:"+result);
				}
				if(smsCode.contains("-")){
					String[] results = smsCode.split("-");
					result = ((int)(Float.parseFloat(results[0]) - Float.parseFloat(results[1])))+"";
					logger.info("result:"+result);
				}
				if(smsCode.contains("×")){
					String[] results = smsCode.split("×");
					result = ((int)(Float.parseFloat(results[0]) * Float.parseFloat(results[1])))+"";
					logger.info("result:"+result);
				}
				if(smsCode.contains("÷")){
					String[] results = smsCode.split("÷");
					result = ((int)(Float.parseFloat(results[0]) / Float.parseFloat(results[1])))+"";
					logger.info("result:"+result);
				}
			}
		}
		return result;
	}
	
	
	public static Map<String, String> getPhoneNumber(String loginAccount,String loginPassword) {
		Map<String, Object> loginMap = new LinkedHashMap<>();
		loginMap.put("uName", loginAccount);
		loginMap.put("pWord", loginPassword);
		loginMap.put("Developer", "JxpZo1qwqZHeJqggYB4CLg==");
		loginMap.put("code", "utf8");
		String token = sendHttpRequest(url_req+login_reqType, loginMap);  //获取登陆token
		logger.info("登录返回:"+token);
		if(!StringUtil.isEmpty(token)&&token.contains("&")){
			token = token.split("&")[0];
		}
		String phoneNumber = getPhoneNumber(token, loginMap);
		if (phoneNumber==null||phoneNumber.contains("False")) {
			phoneNumber = getPhoneNumber(token, loginMap);   //获取手机号重试一次
		}
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.put("token", token);
		resultMap.put("phoneNumber", phoneNumber);
		return resultMap;

	}
	*/
/**
	 * 获取千万卡手机号码
	 * @param token
	 * @param map
	 * @return
	 *//*

	public static String getPhoneNumber(String token, Map<String, Object> map) {
		String phoneNumber = "";
		map.clear();
		map.put("token", token);
		map.put("ItemId", xmid);
		map.put("code", "utf8");
		phoneNumber = sendHttpRequest(url_req+phoneNumber_reqType, map);
		logger.info("获取到手机号码返回:"+phoneNumber);
		if (!StringUtils.isEmpty(phoneNumber)&&(phoneNumber.contains(";"))) {
			return phoneNumber.split(";")[0];
		} 
		return phoneNumber;
	}

//	public static String sendHttpRequest(String url, Map<String, Object> paraMap) {
//		HttpMethod method;
//		String responseString = "";
//		BufferedReader reader = null;
//		HttpClient httpclient = new HttpClient();
//		// 设置连接超时时间
//		httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
//		// 设置回应超时
//		httpclient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
//
//		method = new GetMethod(url);
//		method.getParams().setCredentialCharset(charset);
//		if (paraMap!=null&&!paraMap.isEmpty()) {
//			String queryStr = createLinkString(paraMap);
//			method.setQueryString(queryStr);
//		}
//		method.addRequestHeader("User-Agent", "Mozilla/4.0");
//		try {
//			int executeMethod = httpclient.executeMethod(method);
//			logger.info("状态码：" + executeMethod);
//			StringBuilder stringBuilder = new StringBuilder();
//			String str;
//			reader = new BufferedReader(new InputStreamReader(
//					method.getResponseBodyAsStream(), charset));
//			while ((str = reader.readLine()) != null) {
//				stringBuilder.append(str);
//			}
//			responseString = stringBuilder.toString();
//		} catch (Exception e) {
//			logger.error("千万卡请求异常", e);
//			throw new RuntimeException("千万卡请求异常"+url.substring(34), e);
//		} finally {
//			if (reader != null) {
//				try {
//					reader.close();
//				} catch (Exception e) {
//					logger.error("千万卡请求异常", e);
//				}
//			}
//			if (method != null) {
//				method.releaseConnection();
//			}
//		}
//		return responseString;
//	}

	private static String createLinkString(Map<String, Object> params) {
		String paraStr = "";
		for (String key : params.keySet()) {
			String value = "";
			try {
				value = URLEncoder.encode(String.valueOf(params.get(key)), charset);
			} catch (Exception e) {
				logger.error("error", e);
			}
			paraStr += "&"+key+"="+value;
		}
		return paraStr.substring(1);
	}

	public static void main(String[] args) {
		String smsCode = "【东方航空】您的动态验证码为：00+62，请输入计算结果，感谢您对东航官网的支持！";
		String regex = "【东方航空】您的动态验证码为：([0-9]+.[0-9]+)，请输入计算结果，感谢您对东航官网的支持！";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(smsCode);
		String result = "";
		while(m.find()) {
			if(StringUtils.isNotEmpty(m.group(1))) {
				result = m.group(1);
				logger.info("result:"+result);
				if(result.contains("+")){
					String[] results = result.split("\\+");
					result = ((int)(Float.parseFloat(results[0]) + Float.parseFloat(results[1])))+"";
					logger.info("result:"+result);
				}
				if(result.contains("-")){
					String[] results = result.split("-");
					result = ((int)(Float.parseFloat(results[0]) - Float.parseFloat(results[1])))+"";
					logger.info("result:"+result);
				}
				if(result.contains("×")){
					String[] results = result.split("×");
					result = ((int)(Float.parseFloat(results[0]) * Float.parseFloat(results[1])))+"";
					logger.info("result:"+result);
				}
				if(result.contains("÷")){
					String[] results = result.split("÷");
					result = ((int)(Float.parseFloat(results[0]) / Float.parseFloat(results[1])))+"";
					logger.info("result:"+result);
				}
			}
		}
	}

}
*/
