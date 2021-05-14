package com.feeye.service.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class U3CreateOrderService {
    private static final int timeout = 40000;

    public static void main(String[] args) {
        String back = "{\"id\":\"27886042\",\"account\":\"13532989542_feeye123\",\"airline\":\"MF\",\"orderNo\":\"1524794945160\",\"username\":\"policytest\",\"childrenMobile\":\"18617070230\",\"payType\":\"zfb\",\"code\":\"\",\"orderTime\":\"2018-06-13 15:35:16\",\"codePassword\":\"\",\"price\":\"450\",\"departure\":\"XMN\",\"arrival\":\"HGH\",\"departureDate\":\"2018-07-03\",\"flightNo\":\"MF8531\",\"cabin\":\"B\",\"mobile\":\"13532989542\",\"creditNo\":\"18713083283\",\"expireMonth\":\"null\",\"expireYear\":\"null\",\"cvv\":\"15817476200\",\"ownername\":\"null\",\"idCardType\":\"null\",\"idCardNo\":\"null\",\"payerMobile\":\"12346579\",\"account_no\":\"null\",\"deduct_third_code\":\"null\",\"linkMan\":\"����\",\"isOutticket\":\"true\",\"ytype\":\"?????\",\"passengers\":[{\"passengerName\":\"����\",\"idcard\":\"441423199501081014\",\"passengerType\":\"����\",\"passengercardType\":\"���֤\",\"birthday\":\"1995-01-08\",\"passengerSex\":\"null\"}],\"ifUsedCoupon\":false,\"drawerType\":\"GW\",\"qiangpiao\":\"\",\"otheraccount\":\"b_b\"}";
//		Map<String, String> resultMap = ParseFlightInFo.checkFlightIsExist("MF8050", null, back);
//		String status = resultMap.get("status");
//		System.out.println(status);
        new U3CreateOrderService().StartCreateOrder(back);


//		CloseableHttpClient httpclient = HttpClients.createDefault();
//        PropertiesUtil prop = new PropertiesUtil();
//        // ȡ�����õķ�����id��Χ
//        String url = "http://183.62.225.15:8081/policybook/handleQnrChangedOrder";
//        HttpPost httpPost = new HttpPost(url);
//        RequestConfig config = RequestConfig.custom().setConnectTimeout(35000).setConnectionRequestTimeout(35000).setSocketTimeout(60000).build();
//        httpPost.setConfig(config);
//        String jsonObject = "{\"tag\":\"flight.national.supply.sl.ticketNoUpdate\",\"createTime\":\"1537262227213\",\"sign\":\"961a3b2ab5188d29bb5c2243f399ff37\",\"data\":\"eyJvbGRUaWNrZXRObyI6IjEyMzQ1Njc4OSIsInR0c09yZGVyTm8iOiIxMjM0NTY3ODkiLCJuZXdUaWNrZXRObyI6Ijk4Ny02NTQzMjEiLCJ1cGRhdGVUaW1lIjoiMTUzNzI2MjAwNTg3NCJ9\"}";
//		StringEntity entity1 = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
//		httpPost.setEntity(entity1);
//        HttpResponse response = httpclient.execute(httpPost);
//        HttpEntity entity = response.getEntity();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
//        String line = null;
//        while ((line = reader.readLine()) != null) {
//           System.out.println(line);
//        }
//        if (entity != null) {
//            entity.consumeContent();
//        }
//        return null;


    }

    public String StartCreateOrder(String orderJson) {
        if (StringUtils.isEmpty(orderJson)) {
            return "ERROR:���ݲ�����";
        }
        System.out.println("��������������" + orderJson);
        // ��½�˺�
        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();//һ��cookies
        String cookie = "";
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //��ʼ��SSL����
        } catch (Exception e) {
            e.printStackTrace();
        }

        org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();

        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
            e1.printStackTrace();
        }


        HttpGet get = null;
        HttpPost post = null;
        String childrenUser = "";
        String order_id = "";
        InputStream re = null;

        RequestConfig config = RequestConfig.custom()
                .build();

        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout)
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setStaleConnectionCheckEnabled(true).build();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setSSLSocketFactory(sslsf)
                .setDefaultCookieStore(cookieStore).build();
        CloseableHttpResponse response = null;
        try {
            searchFlights(config, httpclient, cookieStore, orderJson);
        } catch (Exception e) {
            sendCreateOrderInfo("error", "����ʧ��", "", childrenUser, "", order_id, "", "", null, "", "", "false", "true");
            e.printStackTrace();
        } finally {
            if (re != null) {
                try {
                    re.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (get != null) {
                get.releaseConnection();
            }
            if (post != null) {
                post.releaseConnection();
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "SUCCESS";
    }

    private void searchFlights(RequestConfig config, CloseableHttpClient httpclient, BasicCookieStore cookieStore,
                               String orderJson) throws Exception {
        InputStream re = null;
        try {
            HttpHost target = new HttpHost("flights.sichuanair.com", 80, "http");
//			String url = "http://flights.sichuanair.com/3uair/ibe/common/loginStatus.do?callback=jQuery18307014227981723825_"+new Date().getTime()+"&_="+new Date().getTime();
            String url = "http://flights.sichuanair.com/3uair/ibe/air/checkFlightSearchNeedValidationCode.do?ConversationID=&isClickSearch&jsonpcallback=jQuery183018881565598630745_" + new Date().getTime() + "&_=" + new Date().getTime();
            HttpGet get = new HttpGet(url);
            get.setConfig(config);
            get.setHeader("Referer",
                    "http://flights.sichuanair.com/");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            get.setHeader("Host", "flights.sichuanair.com");

            CloseableHttpResponse response = httpclient.execute(target, get);
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer buf = new StringBuffer();

            if (null != listCookie && listCookie.size() > 0) {
                for (int i = 0; i < listCookie.size(); i++) {
                    buf.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                }
            }
            String cookie = buf.toString();

            System.out.println(cookie);

            HttpPost post = new HttpPost("/3uair/ibe/common/processSearchForm.do");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("Search/AirlineMode", "false"));
            nameValue.add(new BasicNameValuePair("Search/DateInformation/departDate", "2018-11-20"));
            nameValue.add(new BasicNameValuePair("Search/DateInformation/departDate_display", "2018-11-20"));
            nameValue.add(new BasicNameValuePair("Search/DateInformation/returnDate", "2018-11-22"));
            nameValue.add(new BasicNameValuePair("Search/OriginDestinationInformation/Destination/location", "CITY_CTU_CN"));
            nameValue.add(new BasicNameValuePair("Search/OriginDestinationInformation/Destination/location_input", "�ɶ�"));
            nameValue.add(new BasicNameValuePair("Search/OriginDestinationInformation/Origin/location", "CITY_BJS_CN"));
            nameValue.add(new BasicNameValuePair("Search/OriginDestinationInformation/Origin/location_input", "����"));
            nameValue.add(new BasicNameValuePair("Search/Passengers/adults", "1"));
            nameValue.add(new BasicNameValuePair("Search/Passengers/children", "0"));
            nameValue.add(new BasicNameValuePair("Search/calendarCacheSearchDays", "60"));
            nameValue.add(new BasicNameValuePair("Search/calendarSearch", "false"));
            nameValue.add(new BasicNameValuePair("Search/calendarSearched", "false"));
            nameValue.add(new BasicNameValuePair("Search/flightType", "oneway"));
            nameValue.add(new BasicNameValuePair("Search/isUserPrice", "1"));
            nameValue.add(new BasicNameValuePair("Search/promotionCode", ""));
            nameValue.add(new BasicNameValuePair("Search/searchType", "F"));
            nameValue.add(new BasicNameValuePair("destinationLocationSearchBoxType", "L"));
            nameValue.add(new BasicNameValuePair("dropOffLocationRequired", "false"));
            nameValue.add(new BasicNameValuePair("searchTypeValidator", "F"));
            nameValue.add(new BasicNameValuePair("xSellMode", "false"));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
            post.setConfig(config);
            post.setHeader("Cookie", cookie);
            post.setHeader("Host", "flights.sichuanair.com");
            post.setHeader("Referer", "	\r\n" +
                    "http://www.sichuanair.com/");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("back:" + back);
            Header[] location = response.getHeaders("Location");
            String locationValue = "";
            for (int i = 0; i < location.length; i++) {
                locationValue = location[i].getValue();
                System.out.println("resultLocation:" + locationValue);
            }
//			
//			if(StringUtils.isNotEmpty(locationValue)) {
//				
//				String regEx = "redirect=(.*)";
//				Pattern pattern = Pattern.compile(regEx);
//				Matcher m = pattern.matcher(locationValue);
//				String redirect = "";
//				while(m.find()) {
//					if(StringUtils.isNotEmpty(m.group(1))) {
//						redirect = m.group(1);
//					}
//				}
//				System.out.println("redirect:"+redirect);
//				
//				
//				
//				get = new HttpGet(locationValue);
//				get.setConfig(config);
//				get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//				get.setHeader("Host", "www.luckyair.net");
//				get.setHeader("Cookie",cookie);
//				response = httpclient.execute(get);
//				back = EntityUtils.toString(response.getEntity(), "utf-8");
//				String newCookie = "";
//				Header[] headersArr = response.getAllHeaders();
//				for(Header header:headersArr) {
//					if("Set-Cookie".equals(header.getName())) {
//						newCookie += header.getValue() + ";";
//						String s[] = newCookie.split(";");
//						newCookie = s[0]+";";
//					}
//				}
//				cookie += newCookie;
//				System.out.println("��֤��cookie:"+cookie);
//				System.out.println("��֤��back:"+back);
//				get = new HttpGet("http://www.luckyair.net/hnatravel/imagecode?code="+Math.random());
//				get.setConfig(config);
//				get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//				get.setHeader("Host", "www.luckyair.net");
//				get.setHeader("Cookie",cookie);
//				response = httpclient.execute(get);
//				OutputStream os = null;
//				String random = "0." + (long) ((Math.random() + 1) * 10000000000000000L);
//				String fileUri = "C://testImg//" + random + ".jpg";
//				re = response.getEntity().getContent();
//				os = new FileOutputStream(fileUri);
//				IOUtils.copy(re, os);
//				os.close();
//				InputStream is = new FileInputStream(new File(fileUri));
//		
//				response.close();
//				String validtext = YunSu.getValidCode(is, "5000", "","");
//				System.out.println("���ýӿڻ�ȡ��֤�뷵�أ�" + validtext);
//				org.dom4j.Document document = DocumentHelper.parseText(validtext);
//				org.dom4j.Element root = document.getRootElement();
//				String error = root.elementText("Error");
//				String result = "";
//				if (error != null && !"".equals(error)) {
//					result = error;
//					if(result.contains("��������")){
//						System.out.println(result);
//					}
//				} else {
//					result = root.elementText("Result");
//				}
//				
//				
//				String[] cookieStr = cookie.split(";");
//				String c = "";
//				for(int i= 0;i<cookieStr.length;i++) {
//					if(cookieStr[i].contains("sso_sign_eking")) {
//						continue;
//					}
//					c = c + cookieStr[i] + ";";
//				}
//				cookie = c;
//				post = new HttpPost("/hnatravel/checkSignCode");
//				nameValue = new ArrayList<NameValuePair>();
//				nameValue.add(new BasicNameValuePair("flag", "1"));
//				nameValue.add(new BasicNameValuePair("redirect", redirect));
//				nameValue.add(new BasicNameValuePair("submit", "�ύ"));
//				nameValue.add(new BasicNameValuePair("tickcode", result));
//				post.setEntity(new UrlEncodedFormEntity(nameValue, "gb2312"));
//				post.setConfig(config);
//				post.setHeader("Cookie",cookie);
//				post.setHeader("Host","www.luckyair.net");
//				post.setHeader("Referer","http://www.luckyair.net/flight/searchflight2016.action");
//				post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//				response = httpclient.execute(target,post);
//				back = EntityUtils.toString(response.getEntity(), "utf-8");
////				if (null != listCookie && listCookie.size() > 0) {
////					for (int i = 0; i < listCookie.size(); i++) {
////						buf.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
////					}
////				}
////				cookie = cookie + ";"+ buf.toString();
//				System.out.println("��֤�󷵻�:"+back);
//				headersArr = response.getAllHeaders();
//				newCookie = "";
//				for(Header header:headersArr) {
//					if("Set-Cookie".equals(header.getName())) {
//						newCookie += header.getValue() + ";";
//					}
//				}
//				cookie += newCookie;
//				cookie = cookie.replace(" Domain=www.luckyair.net; Path=/;", "");
//				System.out.println(cookie);
//				cookieStr = cookie.split(";");
//				c = "";
//				for(int i= 0;i<cookieStr.length;i++) {
//					if(!cookieStr[i].contains("sso_sign_eking")) {
//						continue;
//					}
//					c = c + cookieStr[i] + ";";
//				}
//				cookie = c;
//				System.out.println(cookie);
////				cookie = "OZ_1U_2638=vid=vb31ff1b6097f9.0&ctime=1538979641&ltime=1538979640; pgv_pvi=9735269376; WT_FPC=id=24d015bfd4a1ed2ee1d1530003263407:lv=1530676145489:ss=1530676131207; ozuid=15595168130; OZ_RU_2638=15595168130; JSESSIONID=3D0CB8A4F559ABBCCBF8D46CE8BB7D28=-&eurl=http%3A//www.luckyair.net/&etime=1538965031&ctime=1538979641&ltime=1538979640&compid=2638; notice-read-ETweb=true; pgv_si=s4335926272; sso_sign_eking=bb0bd9c9-cc83-451e-9fbc-58a8f05379c8";
//				
//				
//				post = new HttpPost("/flight/searchflight2016.action");
//				nameValue = new ArrayList<NameValuePair>();
//				nameValue.add(new BasicNameValuePair("dstCity", "KMG"));
//				nameValue.add(new BasicNameValuePair("dstCity_interNAtional", ""));
//				nameValue.add(new BasicNameValuePair("flightDate", "2018-10-19"));
//				nameValue.add(new BasicNameValuePair("returnDate", ""));
//				nameValue.add(new BasicNameValuePair("passengerCount.adultNum", "1"));
//				nameValue.add(new BasicNameValuePair("passengerCount.childNum", "0"));
//				nameValue.add(new BasicNameValuePair("passengerCount.infantNum", "0"));
//				nameValue.add(new BasicNameValuePair("orgCity", "SZX"));
//				nameValue.add(new BasicNameValuePair("orgCity_interNAtional", ""));
//				nameValue.add(new BasicNameValuePair("tripType", "ONEWAY"));
//				post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//				post.setConfig(config);
//				post.setHeader("Cookie",cookie);
//				post.setHeader("Host","www.luckyair.net");
//				post.setHeader("Referer","http://www.luckyair.net/");
//				post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//				response = httpclient.execute(target,post);
//				back = EntityUtils.toString(response.getEntity(), "utf-8");
//				System.out.println("back:"+back);
//				location = response.getHeaders("Location");
//				locationValue = "";
//				for (int i = 0; i < location.length; i++) {
//					locationValue = location[i].getValue();
//					System.out.println("resultLocation:" + locationValue);
//				}
//				
//				post = new HttpPost("/flightresult/flightresult2016.action");
//				nameValue = new ArrayList<NameValuePair>();
//				nameValue.add(new BasicNameValuePair("desc", "coBPtm4BZy5Ly7E1arnlj2ydnv1nXyuwJSYyoNHwmbUXYl/elXICeY115jwY6dt2"));
//				nameValue.add(new BasicNameValuePair("dstCity", "KMG"));
//				nameValue.add(new BasicNameValuePair("flightDate", "2018-10-19"));
//				nameValue.add(new BasicNameValuePair("flightseq", "1"));
//				nameValue.add(new BasicNameValuePair("index", "1"));
//				nameValue.add(new BasicNameValuePair("orgCity", "SZX"));
//				nameValue.add(new BasicNameValuePair("tripType", "ONEWAY"));
//				nameValue.add(new BasicNameValuePair("", ""));
//				post.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//				post.setConfig(config);
//				post.setHeader("Cookie",cookie);
//				post.setHeader("Host","www.luckyair.net");
//				post.setHeader("Referer","http://www.luckyair.net/flight/searchflight2016.action");
//				post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//				response = httpclient.execute(target,post);
//				back = EntityUtils.toString(response.getEntity(), "utf-8");
//				System.out.println("back:"+back);
//				location = response.getHeaders("Location");
//				locationValue = "";
//				for (int i = 0; i < location.length; i++) {
//					locationValue = location[i].getValue();
//					System.out.println("resultLocation:" + locationValue);
//				}
//				

//			}


        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (re != null) {
                re.close();
            }
        }
    }

    public String getRand() {
        Random r = new Random();
        long rand = Math.abs(r.nextLong());
        String randStr = "0." + rand;
        if (randStr.length() > 18) {
            randStr = randStr.substring(0, 18);
        }
        return randStr;
    }

    /**
     * ���ʹ������ String result = request.getParameter("result"); //�Ƿ񴴵��ɹ� String
     * message = request.getParameter("message"); //ʧ����Ϣ String price =
     * request.getParameter("price"); //�ɹ��ܽ�� String childrenUser =
     * request.getParameter("childrenUser");//���ʺ� String newOrderId =
     * request.getParameter("newOrderId"); //���������ɹ���Ĺ��������� String orderId =
     * request.getParameter("orderId"); //ԭ��������ID String isPassuccess =
     * request.getParameter("isPassuccess"); //�Ƿ�֧���ɹ� String isPassenge =
     * request.getParameter("isPassenge"); //�Ƿ�Ʊ�Ż��� String[] passengeMessage =
     * request.getParameterValues("passengeMessage"); // ��ȡƱ�Ż��ϵͳ
     * ��ʽΪ:����##����֤##Ʊ�� String payTransactionid =
     * request.getParameter("payTransactionid"); //��ȡƱ�Ż���Ľ��׺� SCʱ������ϵ�绰 String
     * payStatus = request.getParameter("payStatus"); //��ȡ֧����ʽ String isSuccess
     * = request.getParameter("isSuccess"); //�Ƿ���� String isautoB2C =
     * request.getParameter("isautoB2C"); //�Ƿ��Զ���Ʊ String ifUsedCoupon =
     * request.getParameter("ifUsedCoupon"); //�Ƿ�ʹ�ú��
     */
    public static String sendCreateOrderInfo(String result, String message, String price, String childrenUser,
                                             String newOrderId, String orderId, String isPassuccess, String isPassenge, String passengeMessage,
                                             String payStatus, String payTransactionid, String ifUsedCoupon, String isSuccess) {
        try {
//			String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrl");
            String orderUrl = "";
            StringBuffer buffer = new StringBuffer();
            buffer.append("<feeye-official>");
            buffer.append("<official>" + "MF" + "</official> ");
            buffer.append("<url>" + orderUrl + "</url> ");
            buffer.append("<type>0</type> ");
            buffer.append("<method>post</method>");
            buffer.append("<max>20</max> ");
            buffer.append("<encod>utf-8</encod> ");
            buffer.append("<params>");
            buffer.append("<param name='result'>" + result + "</param>");
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

            buffer.append("</params>");
            buffer.append("</feeye-official>");

//			String content = OfficialMain.setRequestParams(buffer.toString());
//			if (content != null) {
//				String rs1[] = content.split("#@_@#");
//				if (rs1.length == 2) {
//					content = rs1[1];
//					return content;
//				}
//				if (rs1.length == 3) {
//					System.out.println(rs1[2]);
//					return rs1[2];
//				}
//			}

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
