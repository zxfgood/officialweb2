package com.feeye.service.processor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
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
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZHCreateB2BOrderService {
    private static final int timeout = 40000;

    public static void main(String[] args) {
        String back = "\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n" +
                " <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n" +
                "\r\n" +
                "\r\n" +
                "<script language=\"javascript\">\r\n" +
                "	function goPay(){ \r\n" +
                "		var btnObj = document.getElementById(\"payButton\");\r\n" +
                "		btnObj.disabled = true;\r\n" +
                "	    document.all.pnrimportform.glostr.value = '�����&&&13007501560&&1002.0&1';\r\n" +
                "		document.all.pnrimportform.operate.value = \"goPay\";\r\n" +
                "		document.all.pnrimportform.orderNO.value = \"FX2019030141393681\";\r\n" +
                "		document.all.pnrimportform.target=\"_blank\";\r\n" +
                "		document.all.pnrimportform.submit();	      \r\n" +
                "	}\r\n" +
                "	\r\n" +
                "	function checkBank(){\r\n" +
                "		var banklist = document.getElementsByName(\"payBank\");\r\n" +
                "		for(var i=0;i<banklist.length;i++){\r\n" +
                "			if(banklist[i].checked)\r\n" +
                "				return true;\r\n" +
                "		}\r\n" +
                "		return false;\r\n" +
                "	}\r\n" +
                "	\r\n" +
                "	function goReturn(){\r\n" +
                "		document.all.pnrimportform.operate.value = \"show\";\r\n" +
                "		document.all.pnrimportform.target=\"\";\r\n" +
                "		document.all.pnrimportform.submit();	      \r\n" +
                "	}\r\n" +
                "	\r\n" +
                "</script>\r\n" +
                "\r\n" +
                "<html><!--�˴�Ϊ��ͼ����//-->\r\n" +
                "<head>\r\n" +
                "<title>��ƱԤ��������ȷ�ϼ�֧����</title>\r\n" +
                "<link  rel=\"stylesheet\" href=\"/b2b/b2bUI/css/style.css\" type=\"text/css\"/>\r\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"/b2b/css/orderManage.css\">\r\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"/b2b/css/aa.css\">\r\n" +
                "<script language=\"javascript\" src=\"/b2b/js/findObj.js\"></script>\r\n" +
                "</head>\r\n" +
                "<body style=\"overflow-x: hidden; overflow-y: auto;\">   \r\n" +
                "<div class=\"pa-st-10\">\r\n" +
                "                    	<div class=\"pr_st\">\r\n" +
                "                    	 <ul>\r\n" +
                "                       	   <li class=\"wi-10\">\r\n" +
                "                            	<p>PNR��ѯ</p>\r\n" +
                "                                <div><span>&nbsp;</span></div>\r\n" +
                "                            </li>\r\n" +
                "                       	   <li class=\"wi-10\">\r\n" +
                "                            	<p>ȷ���ÿ���Ϣ</p>\r\n" +
                "                                <div><span>&nbsp;</span></div>\r\n" +
                "                            </li>\r\n" +
                "                       	   <li class=\"wi-10\">\r\n" +
                "                            	<p>������ϵ����Ϣ</p>\r\n" +
                "                                <div><span>&nbsp;</span></div>\r\n" +
                "                            </li>\r\n" +
                "                        	\r\n" +
                "                       	   <li class=\"wi-10\">\r\n" +
                "                            	<p>����֧��</p>\r\n" +
                "                                <div class=\"or\"><span>&nbsp;</span></div>\r\n" +
                "                            </li>\r\n" +
                "                            \r\n" +
                "                        </ul>\r\n" +
                "                        <br />\r\n" +
                "                        <div class=\"pr-st-bg\">&nbsp;</div>\r\n" +
                "                        </div>\r\n" +
                "</div>\r\n" +
                " <form method=\"post\" name=\"pnrimportform\"\r\n" +
                "				action=\"/b2b/b2a/PnrImportCtrl.do\">\r\n" +
                "				<input name=\"operate\" type=\"hidden\" /> <input name=\"glostr\"\r\n" +
                "					type=\"hidden\" /> <input name=\"orderNO\" type=\"hidden\" />\r\n" +
                " <table width=\"100%\" border=\"0\" align=\"left\" cellpadding=\"0\" cellspacing=\"0\">\r\n" +
                "           \r\n" +
                "             <tr>\r\n" +
                "		           <td align=\"center\">\r\n" +
                "	                 <input type=\"button\" class=\"sysbut\" id=\"payButton\" style=\"cursor:hand\" value=\"����֧��\" onClick=\"goPay()\">&nbsp;&nbsp;\r\n" +
                "		             <input type=\"button\" class=\"sysbut\" style=\"cursor:hand\" value=\"��    ��\" onClick=\"goReturn()\">\r\n" +
                "		           </td>\r\n" +
                "		         </tr>\r\n" +
                "              </table>  \r\n" +
                "\r\n" +
                "  </form>\r\n" +
                "\r\n" +
                "<script language=\"javascript\" src=\"/b2b/pages/2008/js/webtrends.js\"></script></body>\r\n" +
                "</html>\r\n" +
                "";
        //����
        String regEx = "document.all.pnrimportform.glostr.value = '(.*?)';|document.all.pnrimportform.orderNO.value = \"(.*?)\";";
        Pattern pattern = Pattern.compile(regEx);
        Matcher m = pattern.matcher(back);
        while (m.find()) {
            if (StringUtils.isNotEmpty(m.group(1))) {
                System.out.println("glostr:" + m.group(1));
            }
            if (StringUtils.isNotEmpty(m.group(2))) {
                System.out.println("orderNO:" + m.group(2));
            }
        }
    }

    public String StartCreateOrder(String orderJson) {
        if (StringUtils.isEmpty(orderJson)) {
            return "ERROR:���ݲ�����";
        }
        System.out.println("��������������" + orderJson);
        String session = UUID.randomUUID().toString();
        // ��½�˺�
        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();// һ��cookies
        String cookie = "";
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // ��ʼ��SSL����
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

        RequestConfig config = RequestConfig.custom().build();

        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setStaleConnectionCheckEnabled(true).build();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
                .setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore).build();
        CloseableHttpResponse response = null;
        try {
            cookie = login(httpclient, config, cookieStore, cookie, orderJson, session);
//			searchFlights(httpclient, config, cookie, orderJson);
            Map<String, String> nextShowParamMap = queryPnr(httpclient, config, cookie, orderJson);
            if (nextShowParamMap == null) {
                return "";
            }
            Map<String, String> goOrderCheckWithPayParamMap = nextShow(httpclient, config, cookie, orderJson, nextShowParamMap);
            if (goOrderCheckWithPayParamMap == null) {
                return "";
            }
            Map<String, String> goPayParamMap = goOrderCheckWithPay(httpclient, config, cookie, orderJson, goOrderCheckWithPayParamMap);
            if (goPayParamMap == null) {
                return "";
            }
            String location = goPay(httpclient, config, cookie, orderJson, goPayParamMap);
            Map<String, String> gatewayPaypayment = gatewayPaypayment(httpclient, config, cookie, location);
            if (gatewayPaypayment == null) {
                return "";
            }
            String orderID = gatewayPaypayment.get("orderID");
            cookie = gatewayPaypayment.get("cookie");
            location = gatewayPayReqPayChannel(httpclient, config, cookie, orderID, location);
            Map<String, String> adapterPayingPayMap = adapterPayingPay(httpclient, config, cookie, location);
            if (adapterPayingPayMap == null) {
                return "";
            }
            String request_msg = adapterPayingPayMap.get("request_msg");
            cookie = adapterPayingPayMap.get("cookie");
            Map<String, String> payChoiceMap = payChoice(httpclient, config, cookie, request_msg, location);
            String back = payChoiceMap.get("back");
            cookie = payChoiceMap.get("cookie");
            Map<String, String> resultMap = parseResult(back, orderJson, httpclient, cookie, config);
            String returnBack = payComfirmOrPayFinish(httpclient, config, cookie, resultMap, "payComfirm");

            returnBack = payComfirmOrPayFinish(httpclient, config, cookie, resultMap, "payFinish");
            Document doc = Jsoup.parse(returnBack);
            request_msg = doc.getElementById("request_msg").val();
            Map<String, String> predepositPayReturnVerifyMap = predepositPayReturnVerify(httpclient, config, cookie, request_msg);
            cookie = predepositPayReturnVerifyMap.get("cookie");
            location = predepositPayReturnVerifyMap.get("location");
            Map<String, String> returnResulMap = gatewayPaySyncReturnResul(httpclient, config, cookie, location);
            String alipaysubmit = returnResulMap.get("alipaysubmit");
            String orderNo = returnResulMap.get("orderNo");
            location = syncNotifyBusiness(httpclient, config, cookie, alipaysubmit, orderNo);
            //����Ҫ��֮ǰ�յ�½��b2b
            location = paymentCallback(httpclient, config, cookie, location);
            flightSearch(httpclient, config, cookie, location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void flightSearch(CloseableHttpClient httpclient, RequestConfig config, String cookie, String locationUrl) {
        try {
            HttpGet get = new HttpGet(locationUrl);
            get.setConfig(config);
            get.setHeader("Host", "b2b.shenzhenair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            get.setHeader("Cookie", cookie);
            HttpHost target = new HttpHost("b2b.shenzhenair.com", 80, "http");
            CloseableHttpResponse response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("flightSearch����:" + back);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private String paymentCallback(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                   String locationUrl) {
        try {
            HttpGet get = new HttpGet(locationUrl);
            get.setConfig(config);
            get.setHeader("Host", "b2b.shenzhenair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            get.setHeader("Cookie", cookie);
            HttpHost target = new HttpHost("b2b.shenzhenair.com", 80, "http");
            CloseableHttpResponse response = httpclient.execute(target, get);
            Header[] location = response.getHeaders("Location");
            String locationValue = "";
            for (int i = 0; i < location.length; i++) {
                locationValue = location[i].getValue();
                System.out.println("Location:" + locationValue);
            }
            return locationValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String syncNotifyBusiness(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                      String alipaysubmit, String orderNo) {
        try {
            HttpPost post = new HttpPost(alipaysubmit);
            post.setConfig(config);
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("orderNo", orderNo));
            System.out.println("syncNotifyBusiness����:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "szapgw.shenzhenair.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            post.setHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryF4ho05AKtQweRLLr");
            CloseableHttpResponse response = httpclient.execute(post);
            Header[] location = response.getHeaders("Location");
            String locationValue = "";
            for (int i = 0; i < location.length; i++) {
                locationValue = location[i].getValue();
                System.out.println("Location:" + locationValue);
            }
            return locationValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private Map<String, String> gatewayPaySyncReturnResul(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                                          String location) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            HttpGet get = new HttpGet(location);
            get.setConfig(config);
            get.setHeader("Host", "szapgw.shenzhenair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            get.setHeader("Cookie", cookie);
            HttpHost target = new HttpHost("szapgw.shenzhenair.com", 80, "http");
            CloseableHttpResponse response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("gatewayPaySyncReturnResul���أ�" + back);
            Thread.sleep(10 * 1000);//�ȴ�ʮ��
            Document doc = Jsoup.parse(back);
            String alipaysubmit = doc.getElementById("alipaysubmit").attr("action");
            String orderNo = doc.getElementsByAttributeValue("name", "orderNo").val();
            resultMap.put("alipaysubmit", alipaysubmit);
            resultMap.put("orderNo", orderNo);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> predepositPayReturnVerify(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                                          String request_msg) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            HttpPost post = new HttpPost("/szairpay-bank/notify/predeposit-PayReturn-verify.action");
            HttpHost target = new HttpHost("fb.shenzhenair.com", 80, "http");
            post.setConfig(config);
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("request_msg", request_msg));
            System.out.println("predepositPayReturnVerify����:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "fb.shenzhenair.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            Header[] headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    cookie += header.getValue() + ";";
                }
            }
            String[] co = cookie.split("\\;");
            String cc = "";
            for (String c : co) {
                if (!c.contains("Path") && !c.contains("HttpOnly")) {
                    cc = cc + c + ";";
                }
            }
            cookie = cc.replaceAll(" ", "").replaceAll("\\;\\;", "\\;");

            Header[] location = response.getHeaders("Location");
            String locationValue = "";
            for (int i = 0; i < location.length; i++) {
                locationValue = location[i].getValue();
                System.out.println("Location:" + locationValue);
            }
            resultMap.put("cookie", cookie);
            resultMap.put("location", locationValue);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String payComfirmOrPayFinish(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                         Map<String, String> paramMap, String type) {
        try {
            StringBuilder sbu = new StringBuilder();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                sbu.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8") + "&");
            }
            if (sbu != null && sbu.length() > 0) {
                sbu.delete(sbu.length() - 1, sbu.length());
            }
            System.out.println("payComfirm����" + sbu.toString());
            HttpGet get = new HttpGet("/subscribe/" + type + ".html?" + sbu.toString() + "_=" + System.currentTimeMillis());
            get.setConfig(config);
            get.setHeader("Host", "yck.shenzhenair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            get.setHeader("Cookie", cookie);
            HttpHost target = new HttpHost("yck.shenzhenair.com", 443, "https");
            CloseableHttpResponse response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("payComfirm���أ�" + back);
            return back;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private Map<String, String> parseResult(String back, String orderJson, CloseableHttpClient httpclient,
                                            String cookie, RequestConfig config) {
        Map<String, String> resultMap = new HashMap<String, String>();
        InputStream re = null;
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            String jsFileName = "C:\\jsScript\\out.js";
            FileReader reader = new FileReader(jsFileName);   // ִ��ָ���ű�
            engine.eval(reader);
            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;
                String payPasswd = (String) invoke.invokeFunction("sha_one", "b2b456789@");
                resultMap.put("payPasswd", payPasswd);
            }
            //�������
            Document doc = Jsoup.parse(back);
            resultMap.put("tradeId", "");
            resultMap.put("payAcc", doc.getElementById("payAcc").val());
            resultMap.put("payType", "1");
            resultMap.put("time_stamp", doc.getElementById("time_stamp").val());
            resultMap.put("platformNo", doc.getElementById("platformNo").val());
            resultMap.put("reqOrderSeq", doc.getElementById("reqOrderSeq").val());
            resultMap.put("transAmt", doc.getElementById("transAmt").val());
            resultMap.put("reqDate", doc.getElementById("reqDate").val());
            resultMap.put("limitTime", doc.getElementById("limitTime").val());
            resultMap.put("merchantNo", doc.getElementById("merchantNo").val());
            resultMap.put("redEnvelopeId", doc.getElementById("redEnvelopeId").val());
            resultMap.put("redEnvelopeAmt", doc.getElementById("redEnvelopeAmt").val());
            resultMap.put("asynResponseUrl", doc.getElementById("asynResponseUrl").val());
            resultMap.put("browserJumpUrl", doc.getElementById("browserJumpUrl").val());
//			resultMap.put("_", System.currentTimeMillis()+"");

            HttpGet get = new HttpGet("/validateCode/getImage.html");//https://yck.shenzhenair.com/validateCode/getImage.html
            HttpHost target = new HttpHost("yck.shenzhenair.com", 443, "https");
            get.setConfig(config);
            get.setHeader("Host", "yck.shenzhenair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            get.setHeader("Cookie", cookie);
            CloseableHttpResponse response = httpclient.execute(target, get);
            String fileUri = "C://testImg//" + System.currentTimeMillis() + ".jpg";
            re = response.getEntity().getContent();
            OutputStream os = new FileOutputStream(fileUri);
            IOUtils.copy(re, os);
            os.close();
            System.out.print("������֤��");
            Scanner scan = new Scanner(System.in);
            String vrtifycode = scan.nextLine();
            System.out.println("��֤�룺" + vrtifycode);
            resultMap.put("checkCode", vrtifycode);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (re != null) {
                try {
                    re.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Map<String, String> payChoice(CloseableHttpClient httpclient, RequestConfig config, String cookie, String request_msg, String location) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            HttpPost post = new HttpPost("/subscribe/payChoice.html");
            HttpHost target = new HttpHost("yck.shenzhenair.com", 443, "https");
            post.setConfig(config);
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("request_msg", request_msg));
            System.out.println("payChoice����:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "yck.shenzhenair.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Origin", "https://fb.shenzhenair.com");
            post.setHeader("Referer", location);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            Header[] headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    cookie += header.getValue() + ";";
                }
            }
            String[] co = cookie.split("\\;");
            String cc = "";
            for (String c : co) {
                if (!c.contains("Path") && !c.contains("HttpOnly")) {
                    cc = cc + c + ";";
                }
            }
            cookie = cc.replaceAll(" ", "").replaceAll("\\;\\;", "\\;");

            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("payChoice���أ�" + back);
            resultMap.put("back", back);
            resultMap.put("cookie", cookie);
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> adapterPayingPay(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                                 String location) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            HttpGet get = new HttpGet(location);
            get.setConfig(config);
            get.setHeader("Host", "fb.shenzhenair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            get.setHeader("Cookie", cookie);
            CloseableHttpResponse response = httpclient.execute(get);
            Header[] headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    cookie += header.getValue() + ";";
                }
            }
            String[] co = cookie.split("\\;");
            String cc = "";
            for (String c : co) {
                if (!c.contains("Path") && !c.contains("HttpOnly")) {
                    cc = cc + c + ";";
                }
            }
            cookie = cc.replaceAll(" ", "").replaceAll("\\;\\;", "\\;");
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("adapterPayingPay���أ�" + back);
            Document doc = Jsoup.parse(back);
            String request_msg = StringEscapeUtils.unescapeHtml4(doc.getElementsByAttributeValue("name", "request_msg").get(0).val());
            map.put("request_msg", request_msg);
            map.put("cookie", cookie);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String gatewayPayReqPayChannel(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                           String orderID, String location) {
        try {
            HttpPost post = new HttpPost("/szairpay/gateway/gateway-Pay-reqPayChannel.action");
            HttpHost target = new HttpHost("szapgw.shenzhenair.com", 80, "http");
            post.setConfig(config);
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("orderID", orderID));
            nameValue.add(new BasicNameValuePair("payChannelID", "661"));
            nameValue.add(new BasicNameValuePair("credit_card_no", ""));
            nameValue.add(new BasicNameValuePair("effective_year", ""));
            nameValue.add(new BasicNameValuePair("effective_moth", ""));
            nameValue.add(new BasicNameValuePair("security_code", ""));
            nameValue.add(new BasicNameValuePair("certificate_type", ""));
            nameValue.add(new BasicNameValuePair("certificates_no", ""));
            nameValue.add(new BasicNameValuePair("credit_card_username", ""));
            nameValue.add(new BasicNameValuePair("phone_no", ""));
            nameValue.add(new BasicNameValuePair("paycredit", "1"));
            nameValue.add(new BasicNameValuePair("authCode", ""));
            nameValue.add(new BasicNameValuePair("prepay_cardno", "CGO0081_B2A"));
            System.out.println("gatewayPayReqPayChannel����:" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "szapgw.shenzhenair.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", location);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("gatewayPayReqPayChannel���أ�" + back);
            //�������
            JSONObject json = new JSONObject(back);
            String context = json.getString("context");
            return context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private Map<String, String> gatewayPaypayment(CloseableHttpClient httpclient, RequestConfig config, String cookie, String location) {
        try {
            Map<String, String> map = new HashMap<String, String>();
            HttpGet get = new HttpGet(location);
            get.setConfig(config);
            get.setHeader("Host", "szapgw.shenzhenair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            get.setHeader("Cookie", cookie);
            CloseableHttpResponse response = httpclient.execute(get);
            Header[] headersArr = response.getAllHeaders();
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName())) {
                    cookie += header.getValue() + ";";
                }
            }
            String[] co = cookie.split("\\;");
            String cc = "";
            for (String c : co) {
                if (!c.contains("Path") && !c.contains("HttpOnly")) {
                    cc = cc + c + ";";
                }
            }
            cookie = cc.replaceAll(" ", "").replaceAll("\\;\\;", "\\;");
            System.out.println("�ض���ǰ������cookie:" + cookie);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("gatewayPaypayment���أ�" + back);
            String regEx = "var orderNo = \"(.*?)\";";
            String orderNo = "";
            Pattern pattern = Pattern.compile(regEx);
            Matcher m = pattern.matcher(back);
            while (m.find()) {
                if (StringUtils.isNotEmpty(m.group(1))) {
                    orderNo = m.group(1);
                }
            }
            map.put("orderID", orderNo);
            map.put("cookie", cookie);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String goPay(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson,
                         Map<String, String> paramMap) {
        String location = "";
        try {
            HttpPost post = new HttpPost("/b2b/b2a/PnrImportCtrl.do");
            HttpHost target = new HttpHost("b2b.shenzhenair.com", 80, "http");
            post.setConfig(config);
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "b2b.shenzhenair.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "http://b2b.shenzhenair.com/b2b/b2a/PnrImportCtrl.do?operate=show");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("goPay���أ�" + back);
            //�������
            String regEx = "window.location.href='(.*?)'";
            Pattern pattern = Pattern.compile(regEx);
            Matcher m = pattern.matcher(back);
            while (m.find()) {
                if (StringUtils.isNotEmpty(m.group(1))) {
                    location = m.group(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private Map<String, String> nextShow(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson,
                                         Map<String, String> paramMap) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            HttpPost post = new HttpPost("/b2b/b2a/PnrImportCtrl.do");
            HttpHost target = new HttpHost("b2b.shenzhenair.com", 80, "http");
            post.setConfig(config);
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "b2b.shenzhenair.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "http://b2b.shenzhenair.com/b2b/b2a/PnrImportCtrl.do?operate=show");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("pnr��ѯ��һ�����أ�" + back);
            //�������
            Document doc = Jsoup.parse(back);
            String allPrice = doc.getElementsByAttributeValue("name", "pnrImportEnt.allPrice").val();
            String fareType = doc.getElementsByAttributeValue("name", "pnrImportEnt.fareType").val();
            String allTicketPrice = doc.getElementsByAttributeValue("name", "pnrImportEnt.allTicketPrice").val();
            String pointRate = doc.getElementsByAttributeValue("name", "pnrImportEnt.pointRate").val();
            String backRate = doc.getElementsByAttributeValue("name", "pnrImportEnt.backRate").val();
            String pageFixedFee = doc.getElementsByAttributeValue("name", "pnrImportEnt.pageFixedFee").val();

            resultMap.put("operate", "goOrderCheckWithPay");
            resultMap.put("globalstr", "�Ž���&&&13007501560&&" + allPrice + "&1");
            resultMap.put("KAcoding", "");
            resultMap.put("pnrImportEnt.allPrice", allPrice);
            resultMap.put("pnrImportEnt.fareType", fareType);
            resultMap.put("pnrImportEnt.allTicketPrice", allTicketPrice);
            resultMap.put("pnrImportEnt.pointRate", pointRate);
            resultMap.put("pnrImportEnt.backRate", backRate);
            resultMap.put("pnrImportEnt.pageFixedFee", pageFixedFee);
            resultMap.put("contactname", "�Ž���");
            resultMap.put("contacttel", "");
            resultMap.put("contactemail", "");
            resultMap.put("contactmobile", "13007501560");
            resultMap.put("contactno", "");
            resultMap.put("KAcodingTX", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    private Map<String, String> goOrderCheckWithPay(CloseableHttpClient httpclient, RequestConfig config, String cookie,
                                                    String orderJson, Map<String, String> paramMap) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            HttpPost post = new HttpPost("/b2b/b2a/PnrImportCtrl.do");
            HttpHost target = new HttpHost("b2b.shenzhenair.com", 80, "http");
            post.setConfig(config);
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                nameValue.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "b2b.shenzhenair.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "http://b2b.shenzhenair.com/b2b/b2a/PnrImportCtrl.do?operate=show");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("goOrderCheckWithPay���أ�" + back);
            //����
            String regEx = "document.all.pnrimportform.glostr.value = '(.*?)';|document.all.pnrimportform.orderNO.value = \"(.*?)\";";
            Pattern pattern = Pattern.compile(regEx);
            Matcher m = pattern.matcher(back);
            while (m.find()) {
                if (StringUtils.isNotEmpty(m.group(1))) {
                    resultMap.put("glostr", m.group(1));
                }
                if (StringUtils.isNotEmpty(m.group(2))) {
                    resultMap.put("orderNO", m.group(2));
                }
            }
            resultMap.put("operate", "goPay");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    private Map<String, String> queryPnr(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            HttpPost post = new HttpPost("/b2b/b2a/PnrImportCtrl.do");
            HttpHost target = new HttpHost("b2b.shenzhenair.com", 80, "http");
            post.setConfig(config);
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("operate", "query"));
            nameValue.add(new BasicNameValuePair("pnrImportType", "2"));
            nameValue.add(new BasicNameValuePair("pnrcode", "QSGDTY"));
            System.out.println("pnr��ѯ���������" + nameValue.toString());
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "b2b.shenzhenair.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "http://b2b.shenzhenair.com/b2b/b2a/PnrImportCtrl.do?operate=show");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("pnr��ѯ���أ�" + back);
            //������Ѱ�Ҷ�ӦƱ���
            JSONObject json = new JSONObject(orderJson);
            String price = json.getJSONArray("flights").getJSONObject(0).getString("price");
            Document doc = Jsoup.parse(back);
            Elements trEle = doc.getElementsByClass("tab1-st").get(2).getElementsByTag("table").get(0).getElementsByTag("tr");
            for (int i = 1; i < trEle.size(); i++) {
                Element tr = trEle.get(i);
                String trPrice = tr.getElementsByTag("td").get(3).text().replaceAll("��", "");
                if (Float.parseFloat(price) != Float.parseFloat(trPrice)) {
                    continue;
                }
                System.out.println(trPrice);
                String priceId = tr.getElementsByAttributeValue("name", "priceId").get(0).val();
                resultMap.put("operate", "nextShow");
                resultMap.put("globalstr", "");
                resultMap.put("priceId", priceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    private void searchFlights(CloseableHttpClient httpclient, RequestConfig config, String cookie, String orderJson) {
        HttpGet get = null;
        HttpHost target = null;
        try {
            get = new HttpGet("/dzb2b/FrontUserQuery.do");
            get.setConfig(config);
            target = new HttpHost("dzb2b.travelsky.com", 443, "https");
            get.setHeader("Host", "dzb2b.travelsky.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            CloseableHttpResponse response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(back);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String login(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore,
                        String cookie, String orderJson, String session) {

        HttpGet get = null;
        HttpPost post = null;
        HttpHost target = null;
        String childrenUser = "";
        String order_id = "";
        InputStream re = null;

        CloseableHttpResponse response = null;
        try {
            get = new HttpGet(
                    "/b2b/");
            get.setConfig(config);
            target = new HttpHost("b2b.shenzhenair.com", 80, "http");
            get.setHeader("Host", "b2b.shenzhenair.com");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer buf = new StringBuffer();

            if (null != listCookie && listCookie.size() > 0) {
                for (int i = 0; i < listCookie.size(); i++) {
                    buf.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                }
            }
            cookie = buf.toString();
            System.out.println(cookie);
            // ��ȡ��֤������
            get = new HttpGet("/b2b/outuser/outuserMng.do?operate=createVcode");
            get.setConfig(config);
            get.setHeader("Host", "b2b.shenzhenair.com");
            get.setHeader("Cookie", cookie);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(target, get);
            Header[] headersArr = response.getAllHeaders();
            String loginVerifycode = "";
            for (Header header : headersArr) {
                if ("Set-Cookie".equals(header.getName()) && header.getValue().contains("loginVerifycode")) {
                    loginVerifycode = header.getValue();
                }
            }
            loginVerifycode = loginVerifycode.split(";")[0].split("=")[1];

//			String fileUri = "C://testImg//" + session + ".jpg";
//			re = response.getEntity().getContent();
//			OutputStream os = new FileOutputStream(fileUri);
//			IOUtils.copy(re, os);
//			os.close();
//			System.out.print("������֤��");
//			Scanner scan = new Scanner(System.in);
//			String vrtifycode = scan.nextLine();
//			System.out.println("��֤�룺" + vrtifycode);

//			config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
//					.setConnectionRequestTimeout(timeout).setRedirectsEnabled(true).build();
            post = new HttpPost("/b2b/outuser/outuserMng.do");
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("operate", "b2bLogin"));
            nameValue.add(new BasicNameValuePair("userName", "LIJG"));
            nameValue.add(new BasicNameValuePair("userPass", "b2b456789"));
            nameValue.add(new BasicNameValuePair("verifycode", ""));
            nameValue.add(new BasicNameValuePair("validateCode", loginVerifycode));
            post.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
            post.setHeader("Host", "b2b.shenzhenair.com");
            post.setHeader("Cookie", cookie);
            post.setHeader("Referer", "http://b2b.shenzhenair.com/b2b/");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
            response = httpclient.execute(target, post);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(back);
//			Header[] headersArr = response.getAllHeaders();
//			String newCookie = "";
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equals(header.getName())) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			System.out.println("�ض���ǰ������cookie:" + newCookie);
//			cookie += newCookie;
//			Header[] location = response.getHeaders("Location");
//			String locationValue = "";
//			for (int i = 0; i < location.length; i++) {
//				locationValue = location[i].getValue();
//				System.out.println("Location:" + locationValue);
//			}
//
//			get = new HttpGet(locationValue);
//			get.setConfig(config);
//			get.setHeader("Host", "ib2b.shenzhenair.com");
//			get.setHeader("Cookie", cookie);
//			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0)	 Gecko/20100101 Firefox/43.0");
//			response = httpclient.execute(target, get);
//			headersArr = response.getAllHeaders();
//			newCookie = "";
//			for (Header header : headersArr) {
//				if ("Set-Cookie".equals(header.getName())) {
//					newCookie += header.getValue() + ";";
//				}
//			}
//			cookie += newCookie;
//			System.out.println("�ض����������cookie:" + newCookie);
            return cookie;
        } catch (Exception e) {
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
        }
        return cookie;
    }
}
