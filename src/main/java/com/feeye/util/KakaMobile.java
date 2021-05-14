package com.feeye.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KOKO手机接码工具类
 * @ClassName: KakaMobile
 * @date 2020年1月13日 下午5:52:42
 */
public class KakaMobile {

    private static final Logger logger = Logger.getLogger(KakaMobile.class);
    //我们自己的开发者帐户,用来计算提成
    private static String sysAccount ="feeye";
    //存用户tokenMap, 以用户名作为key
    private static Map <String, String> tokenMap = new HashMap <String, String>();
    //项目编号,东方航空短信接码
    private static String xmid = "360574";

    public static void main(String[] args) {
        KakaMobile test = new KakaMobile();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String userName = "754118982";
        String passWord = "Abc123";

        String mobile = test.getMobile(httpClient, userName, passWord).get(0);
        mobile = test.checkMobile(httpClient, userName, passWord, mobile);
        System.out.println("锁定手机号返回:" + mobile);
        test.getMessage(httpClient, userName, passWord, mobile);
        System.out.println("获取指定号码对应短信:" + mobile);

        test.cleanMobile(httpClient, userName, passWord, mobile);
    }

    /**
     * 获取手机号码
     * @param httpclient
     * @param userName
     * @param password
     * @return
     */
	public static List<String> getMobile(CloseableHttpClient httpclient, String userName, String password) {
        List <String> list = new ArrayList <String>();
        try {
            if (StringUtils.isEmpty(tokenMap.get(userName))) {
                login(httpclient, userName, password);
            }
            HttpGet get = new HttpGet("http://dkh.hfsxf.com:81/service.asmx/GetHM2Str?token=" + tokenMap.get(userName) + "&xmid=" + xmid + "&sl=1&lx=13&a1=&a2=&pk=&ks=0&rj=" + sysAccount);
            get.setHeader("Host", "dkh.hfsxf.com:81");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            CloseableHttpResponse response = httpclient.execute(get);

            String back = EntityUtils.toString(response.getEntity(), "utf-8");

            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("error", e);
                }
            }

            //hm=137*******1,136*******2,137*******3
            if (back != null && back.contains("hm")) {
                String mobiles[] = back.split("hm=")[1].split(",");
                for (int i = 0; i < mobiles.length; i++) {
                    list.add(mobiles[i]);
                }
            }

        } catch (Exception e) {
            logger.error("error", e);
        }
        System.out.println("获取到的手机号码：" + list.toString());
        return list;
    }

    /**
     * 锁定号码
     * @param httpclient
     * @param userName
     * @param password
     * @param mobile
     * @return
     */
    public static String checkMobile(CloseableHttpClient httpclient, String userName, String password, String mobile) {
        try {
            if (StringUtils.isEmpty(tokenMap.get(userName))) {
                login(httpclient, userName, password);
            }
            HttpGet get = new HttpGet("http://dkh.hfsxf.com:81/service.asmx/mkHM2Str?token=" + tokenMap.get(userName) + "&xmid=" + xmid + "&hm=" + mobile + "&op=0&pk=&rj=" + sysAccount);
            get.setHeader("Host", "dkh.hfsxf.com:81");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            CloseableHttpResponse response = httpclient.execute(get);

            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("error", e);
                }
            }
            logger.info(back);

            if ("1".equals(back)) {
                return "success";
            }

        } catch (Exception e) {
            logger.error("error", e);
        }
        return "error";
    }

    /**
     * 获取指定号码对应短信
     * @param httpclient
     * @param userName
     * @param password
     * @param mobile
     * @return
     */
	public static String getMessage(CloseableHttpClient httpclient,String userName, String password, String mobile) {
		try {
            if (StringUtils.isEmpty(tokenMap.get(userName))) {
                login(httpclient, userName, password);
            }
            HttpGet get = new HttpGet("http://dkh.hfsxf.com:81/service.asmx/GetYzm2Str?token=" + tokenMap.get(userName) + "&xmid=" + xmid + "&hm=" + mobile + "&sf=1");
			get.setHeader("Host","dkh.hfsxf.com:81");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
			CloseableHttpResponse response = httpclient.execute(get);
			
			String back = EntityUtils.toString(response.getEntity(),"utf-8");
			if(response != null){
				try {
					response.close();
				} catch (IOException e) {
					logger.error("error",e);
				}
			}
			logger.info(back);
			int count = 0;
			while("1".equals(back) && count < 30){
				Thread.sleep(2000);
				response = httpclient.execute(get);
				back = EntityUtils.toString(response.getEntity(),"utf-8");
				if(response != null){
					try {
						response.close();
					} catch (IOException e) {
						logger.error("error",e);
					}
				}
				count++;
			}
			
			if(back != null && back.length() > 4){
				return back;
			}
            
		}catch (Exception e) {
			logger.error("error",e);
		}
		return "error";
	}

    /**
     * 释放手机号码
     * @param httpclient
     * @param userName
     * @param password
     * @param mobile
     */
    public static void cleanMobile(CloseableHttpClient httpclient, String userName, String password, String mobile) {
        try {
            if (StringUtils.isEmpty(tokenMap.get(userName))) {
                login(httpclient, userName, password);
            }
            HttpGet get = new HttpGet("http://dkh.hfsxf.com:81/service.asmx/sfHmStr?token=" + tokenMap.get(userName) + "&hm=" + mobile);
            get.setHeader("Host", "dkh.hfsxf.com:81");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            CloseableHttpResponse response = httpclient.execute(get);

            String back = EntityUtils.toString(response.getEntity(), "utf-8");

            logger.info(back);
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("error", e);
                }
            }

        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    /**
     * 登录
     * @param httpclient
     * @param name
     * @param psw
     */
    public static void login(CloseableHttpClient httpclient, String name, String psw) {
        try {
            HttpGet get = new HttpGet("http://dkh.hfsxf.com:81/service.asmx/UserLoginStr?name=" + name + "&psw=" + psw);
            get.setHeader("Host", "dkh.hfsxf.com:81");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            CloseableHttpResponse response = httpclient.execute(get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info(back);
            if (StringUtils.isNotEmpty(back) && back.length() > 15) {
                tokenMap.put(name, back);
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("error", e);
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
    }
}
