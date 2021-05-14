package com.feeye.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.Orderinfo;
import com.feeye.service.processor.DailiyunService;
import com.feeye.service.processor.Y8CreateB2BOrderService;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author xs
 * @description
 * @date 2019/5/20
 */
public class Test {

    public static void main(String[] args) throws Exception {

//        //MU登录密码加密
//        String password = "";
//        ScriptEngineManager manager = new ScriptEngineManager();
//        ScriptEngine engine = manager.getEngineByName("js");
//        String jsFileName = "C:\\新建文件夹\\aes.min.js";
//        FileReader reader = new FileReader(jsFileName);
//        engine.eval(reader);
//        if ((engine instanceof Invocable)) {
//            Invocable invoke = (Invocable) engine;
//            String passengerStr ="FuPAGgSixx9C6KurjkpljcnTBTRa9hTTEBCF8KDDYcIx+NMQvCpLlGWFS0tP5R6IAMPlBPNCvwAxccUGwmtZkdkGhQc6mi56b0M0WY9ExsWIyB3/cCix8wDlIWNQYNlMSWfv0RdROnOhWGJbRUUMMOvPxtqdQ1RcVHCiSuycI6KLXKFOoNaoTjgNmU41Jh9exzzLXil/EevMSTn2JaJX8NDDEID+FZ9ne3evrkvYV62xOK7HIGV8rK7zoKiSfFoT74VBBkISk/YYSBOVuBUpW/dW3/REz0/mYaWxNMkIPtbrerCK2elqO30xCWNTx7i4n315vwzaT2fwLqwNr1UpevdvukfX6NzFTsKRCLKc42vbiKlN0k1nqQ4i97R8Kpll35hH1hpper6YY2y8oK52nfRS3tYOEBJSq1PfP9izSSU6f7ExO+7qrguUwQWqLmAjjijpQAba9NBwEUfotj9SwHPp6awGCvY269tlQr+nCWQbz7QuK0wBfEhJPOuOFW5M9GgqzkvZtp/4XYgbLocgcQ==";
//            password = (String) invoke.invokeFunction("decrypt", passengerStr);
//            System.out.println("加密后的登录密码: " + password);
//        }
//        try {
//            if (reader != null) {
//                reader.close();
//            }
//        } catch (Exception e) {
//        }

//        String s1 = "2020-01-17 19:21:23收到 【东方航空】验证码为:45+32，尊敬的旅客您好，请保护好您的短信验证码并输入计算结果。";
//        String s2 = s1.substring(22).trim();
//        System.out.println(s2);
//        SMSReciveUtil.getMathResult(s2);
//
//
//
//
//
//        Set <String> s = new HashSet ();
//        s.add("a");
//        s.add("b");
//        s.add("c");
//        s.add("a");
//
//        System.out.println(s.size());

//        for (int i = 0; i < 10; i++) {
//            System.out.println(FingerPrintUtil.getDesc());
//        }

        /********************测试webDriver方式开始*****************************/
//        WebDriver driver = PhantomjsDriverUtil.getProxyWebDriver(proxyIp, String.valueOf(proxyPort), "feeyeapp", "feeye789");

        String ip_port = DailiyunService.getRandomIp(50);
        String proxyIp = ip_port.split(":")[0];
        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
        HttpHost dailiyunProxy = new HttpHost(proxyIp, proxyPort, "http");

        String orderJson = "{\n" +
                "    \"account\": \"LVMAMA03_LVMAMA_wnz665868@\n\",\n" +
                "    \"airline\": \"GS\",\n" +
                "    \"bankType\": \"CMB\",\n" +
                "    \"childrenMobile\": \"\",\n" +
                "    \"code\": \"\",\n" +
                "    \"codePassword\": \"\",\n" +
                "    \"creditNo\": \"5187188380513124\",\n" +
                "    \"cvv\": \"369\",\n" +
                "    \"drawerType\": \"GW\",\n" +
                "    \"expireMonth\": \"05\",\n" +
                "    \"expireYear\": \"2028\",\n" +
                "    \"flights\": [\n" +
                "        {\n" +
                "            \"airline\": \"GS\",\n" +
                "            \"arrival\": \"CAN\",\n" +
                "            \"cabin\": \"A\",\n" +
                "            \"departure\": \"TSN\",\n" +
                "            \"departureDate\": \"2020-04-30\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"GS7895\",\n" +
                "            \"price\": \"368.0\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"32550175\",\n" +
                "    \"idCardNo\": \"410781197310154713\",\n" +
                "    \"idCardType\": \"IDCARD\",\n" +
                "    \"ifUsedCoupon\": false,\n" +
                "    \"isOutticket\": \"false\",\n" +
                "    \"linkMan\": \"华勇\",\n" +
                "    \"matchCabin\": false,\n" +
                "    \"mobile\": \"15897736493\",\n" +
                "    \"orderNo\": \"406786710\",\n" +
                "    \"orderTime\": \"2019-03-11 00:12:13\",\n" +
                "    \"otheraccount\": \"fyyzm1_wo4feizhiyou\",\n" +
                "    \"ownername\": \"王涛\",\n" +
                "    \"passengers\": [\n" +
                "        {\n" +
                "            \"birthday\": \"1984-07-19 00:00:00+08:00\",\n" +
                "            \"id\": \"37617002\",\n" +
                "            \"idcard\": \"411425198407198112\",\n" +
                "            \"passengerCardType\": \"身份证\",\n" +
                "            \"passengerName\": \"邵玉风\",\n" +
                "            \"passengerSex\": \"男\",\n" +
                "            \"passengerType\": \"成人\"\n" +
                "        },\n" +
//                "        {\n" +
//                "            \"birthday\": \"1983-11-21 00:00:00+08:00\",\n" +
//                "            \"id\": \"37617004\",\n" +
//                "            \"idcard\": \"350781198311212039\",\n" +
//                "            \"passengerCardType\": \"身份证\",\n" +
//                "            \"passengerName\": \"梁邵平\",\n" +
//                "            \"passengerSex\": \"男\",\n" +
//                "            \"passengerType\": \"成人\"\n" +
//                "        },\n" +
                "        {\n" +
                "            \"birthday\": \"1983-11-30 00:00:00+08:00\",\n" +
                "            \"id\": \"37617003\",\n" +
                "            \"idcard\": \"350802198311306035\",\n" +
                "            \"passengerCardType\": \"身份证\",\n" +
                "            \"passengerName\": \"林炜\",\n" +
                "            \"passengerSex\": \"男\",\n" +
                "            \"passengerType\": \"成人\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"payType\": \"xyk\",\n" +
                "    \"payerMobile\": \"18530203144\",\n" +
                "    \"qiangpiao\": \"2\",\n" +
                "    \"username\": \"fzybian\",\n" +
                "    \"ytype\": \"\"\n" +
                "}";


        double s = 502.02002;
        System.out.println(Math.ceil(s));
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        System.out.println(decimalFormat.format(s));

        JSONObject jsonObject = JSON.parseObject(orderJson);
        Orderinfo orderinfo = jsonObject.toJavaObject(Orderinfo.class);

        /*WebDriver driver = WebDriverObtain.getChromeDriver(proxyIp, String.valueOf(proxyPort), "feeyeapp", "feeye789");
        GSCreateOrderService service = new GSCreateOrderService();
        for (int i = 0; i <1 ; i++) {
            long start = System.currentTimeMillis();
            String cookie = service.queryFlights(orderinfo, "", driver);
            System.out.println("GS查询航班时间: " + (System.currentTimeMillis() - start) / 1000);
        }*/
        /********************测试webDriver方式结束*****************************/


//        new KNdelPassengerTest().getJsContent("", "decrypt", "HHTxsfzjaOf4vnQ+GdtAnRCIcyfNLW/ga0f/bC/gYzU5SQ2yAb3DG454+IRoacwi/KfWQdv0XMheCEWm3bQ/fA==");






















/*        String pwd ="80327777a";
        String md5Pwd = DigestUtils.md5Hex(pwd).toUpperCase();
        System.out.println(md5Pwd);


        String a = "#@_@#<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
                "<html>\n" +
                "<head><title>302 Found</title></head>\n" +
                "<body bgcolor=\"white\">\n" +
                "<h1>302 Found</h1>\n" +
                "<p>The requested resource resides temporarily under a different URI.</p>\n" +
                "<hr/>Powered by Tengine</body>\n" +
                "</html>\n" +
                "#@_@#http://www.luckyair.net/hnatravel/signcode.jsp?flag=1&redirect=aHR0cDovL3d3dy5sdWNreWFpci5uZXQvZmxpZ2h0cmVzdWx0L2ZsaWdodHJlc3VsdDIwMTYuYWN0aW9uP3RyaXBUeXBlPU9ORVdBWSZkZXNjPXNYTEhqV1RSR3VtLzNVZ2ZYbFZ0Tm15TFZBWE4rK2daaXdUdkN2N1dWNVhzVk5rY1cwYnFIMkkyRFZ3ZlQvblhpTUp5ai8yZDF3U3N2RG9JbStjM0poeXdRbUZ1OXJEcjh5M1V4TGNUNSs0PSZpbmRleD0xJmZsaWdodHNlcT0xJm9yZ0NpdHk9TFVNJmRzdENpdHk9WElZJmZsaWdodERhdGU9MjAxOS0wNS0yOCY%3D";
        System.out.println(a.split("#@_@#"));



        String b ="\t<form action=\"http://www.luckyair.net/flightresult/flightresult2016.action\" method=\"post\" id=\"form\">\n" +
                "\t\n" +
                "            <input type=\"hidden\" name=\"flightseq\" value=\"1\">\n" +
                "            \n" +
                "            <input type=\"hidden\" name=\"index\" value=\"1\">\n" +
                "            \n" +
                "            <input type=\"hidden\" name=\"desc\" value=\"sXLHjWTRGum/3UgfXlVtNmyLVAXN++gZiwTvCv7WV5WUazMYg0zp11+82ljr98tmiMJyj/2d1wSsvDoIm+c3JhywQmFu9rDr8y3UxLcT5+4\">\n" +
                "            \n" +
                "            <input type=\"hidden\" name=\"7\" value=\"\">\n" +
                "            \n" +
                "            <input type=\"hidden\" name=\"dstCity\" value=\"KMG\">\n" +
                "            \n" +
                "            <input type=\"hidden\" name=\"flightDate\" value=\"2019-06-02\">\n" +
                "            \n" +
                "            <input type=\"hidden\" name=\"orgCity\" value=\"JHG\">\n" +
                "            \n" +
                "            <input type=\"hidden\" name=\"tripType\" value=\"ONEWAY\">\n" +
                "            \n" +
                "</form>";
        Document parse = Jsoup.parse(b);
        Map <String, String> map = new HashMap <String, String>();
        Element form = parse.getElementById("form");
        Elements input = form.getElementsByTag("input");
        for (Element e : input) {
            if(StringUtils.isNotEmpty(e.val())) {
                map.put(e.attr("name"), e.val());
            }
        }
        System.out.println(map.toString());*/

        Test test = new Test();
//        String str = test.hexStr2Str("324-2335065486%2B%C2%ED%C3%C0%B7%BC%3B324-2335065487%2B%CB%EF%D2%E5");
//        System.out.println(str);


//        String s ="324-2335065486%2B%C2%ED%C3%C0%B7%BC%3B324-2335065487%2B%CB%EF%D2%E5";
//        System.out.println(URLDecoder.decode(s,"gb2312"));
//
//        String result ="876-2131263765 解琛琛;876-2131263766 解江珍";
//        String[] split = result.split(";");
//        for (int i =0; i<split.length; i++) {
//            String[] strings = split[i].split(" ");
//            String ticketNo = strings[0];
//            String passengerName = strings[1];
//            System.out.println(passengerName +ticketNo);
//        }


//        String  back =
//                "   <script>\n" +
//                "        var submitResult = {\"success\":true,\"data\":82507208}\n" +
//                "        var shoppingCart = {\"success\":true,\"data\":{\"shoppingCartId\":\"e3aa79648a9c4848a5037e4f1dfa2813\",\"totalFare\":\"1180\",\"currency\":\"CNY\",\"offers\":[{\"type\":\"Flight\",\"detail\":{\"passengers\":[{\"passengerId\":null,\"fullName\":\"邵玉风\",\"passengerType\":\"ADT\",\"certificateNo\":\"411425198407198112\",\"specialCertificateTypeNo\":null,\"certificateType\":\"NI\"},{\"passengerId\":null,\"fullName\":\"林炜\",\"passengerType\":\"ADT\",\"certificateNo\":\"350802198311306035\",\"specialCertificateTypeNo\":null,\"certificateType\":\"NI\"}],\"itinerarys\":[{\"itineraryType\":\"OW\",\"stopTime\":null,\"orgDsts\":[{\"serviceId\":\"OW-TICKET-b81b88e71f3c4cada84fd9923d4c8a5e#OW-KMG-SZX-20191201-8L9979-WEB-U#SRV-1\",\"flightNo\":\"8L9979\",\"planeType\":null,\"airlineCode\":\"8L\",\"airlineName\":\"祥鹏航空\",\"stopCityCode\":null,\"stopCityName\":null,\"departureCode\":\"KMG\",\"departureName\":\"昆明\",\"departureDate\":\"2019-12-01\",\"departureTime\":\"2019-12-01 17:25:00\",\"departureTerminal\":\"--\",\"departureAirPortName\":\"昆明长水国际机场\",\"arrivalCode\":\"SZX\",\"arrivalName\":\"深圳\",\"arrivalAirPortName\":\"深圳宝安国际机场\",\"arrivalTerminal\":\"T3\",\"arrivalDate\":\"2019-12-01\",\"arrivalTime\":\"2019-12-01 19:35:00\",\"productCode\":\"WEB\",\"cabinCode\":\"U\",\"reCabinCode\":null,\"mileage\":\"1245\"}]}],\"ticketCartItems\":[{\"offerItemType\":\"TICKET\",\"detail\":{\"offerItemId\":\"OW-TICKET-b81b88e71f3c4cada84fd9923d4c8a5e#OW-KMG-SZX-20191201-8L9979-WEB-U\",\"serviceId\":\"OW-TICKET-b81b88e71f3c4cada84fd9923d4c8a5e#OW-KMG-SZX-20191201-8L9979-WEB-U#SRV-1\",\"departureCode\":\"KMG\",\"arrivalCode\":\"SZX\",\"deductible\":null,\"passengerPrices\":[{\"totalFare\":\"1080\",\"currency\":\"CNY\",\"count\":\"2\",\"passengerType\":\"ADT\"}],\"taxs\":[{\"currency\":\"CNY\",\"code\":\"YQ\",\"amount\":0,\"name\":\"燃油\"},{\"currency\":\"CNY\",\"code\":\"CN\",\"amount\":100,\"name\":\"机建\"}]}}],\"ancillaryCartItems\":null}}]}}\n" +
//                "        var isJJC = \"\"\n" +
//                "    </script>";
//
//        String regex = "var submitResult = \\{\"success\":true,\"data\":(.*?)\\}";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher m = pattern.matcher(back);
//        while (m.find()) {
//            if (StringUtils.isNotEmpty(m.group(1))) {
//                String result = m.group(1);
//                System.out.println(result);
//            }
//        }
    }

    @org.junit.Test
    public void a() throws IOException {
        InputStream inputStream = Y8CreateB2BOrderService.class.getResourceAsStream("/pay.txt");
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line= br.readLine())!=null) {
            sb.append(line);
        }
        String result = sb.toString();
        Document doc = Jsoup.parse(result);
        Element table = doc.getElementsByClass("tableinfo1").get(0);
        Element tr = table.getElementsByTag("tr").get(1);
        String tdHtml = tr.getElementsByTag("td").get(7).html();
        if (tdHtml.contains("出票完成")) {
            StringBuilder sbu = new StringBuilder();
            Document document = Jsoup.parse(result);
            // Elements checkInChooses = document.getElementsByAttributeValue("name", "checkInChoose");
            Element msgTable = doc.getElementsByClass("tableinfo1").get(4);
            Elements checkInChooses = msgTable.getElementsByTag("tr");
            for(int index=1;index<checkInChooses.size();index++){
                Element checkInChoose = checkInChooses.get(index);
                String passengerName = checkInChoose.getElementById("psgname" + (index-1)).text();
                // String passengerName = document.getElementsByAttributeValue("name", "psgName").get(index).val();
                String passengerInfo = checkInChoose.getElementById("docid" + (index-1)).text();
                // String passengerInfo = document.getElementsByAttributeValue("name", "docNo").get(index).val();
                 String ticketNo = checkInChoose.getElementsByClass("tktdetail").get(0).attr("tktno");
                        // document.getElementsByAttributeValue("name", "tktNumber").get(index).val();
                // String recNo = document.getElementById("recNo").val();
                // String pnrNo = document.getElementsByClass("order_pnr_detail").val();
               /* sbu.append(passengerName).append("##").append(passengerInfo).append("##").append(ticketNo).append("##")
                        .append(recNo).append("##").append(pnrNo).append("#_#");*/
                // logger.info("乘客："+passengerName+"-"+passengerInfo+",票号："+ticketNo);
                System.out.println("乘客："+passengerName+"-"+passengerInfo+",票号："+ticketNo);
                // logger.info("订单号：" + recNo + ",编码：" + pnrNo);
            }
           //  orderInfo =  sbu.toString() + "@_@";
           //  break;
        }
    }

    @org.junit.Test
    public void c() {
        System.out.println(getRandomString(8));
    }
    public String getRandomString(int length){
        //定义组成随机字符串的所有字符
        String str="0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            //产生62以内的随机数，因为组成随机字符串的字符有62个
            int number=random.nextInt(62);
            //将str的第number个字符加到sb的末尾
            sb.append(str.charAt(number));
        }
        return sb.toString();

    }

    @org.junit.Test
    public void aVoid() {
        String back = "\n" +
                "\n" +
                "\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                "<html>\n" +
                "\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "var sWaitingBoxLan = \"zh_CN\";\n" +
                "</script>\n" +
                "\n" +
                "<meta http-equiv=\"pragma\" content=\"no-cache\">\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<base href=\"https://y8b2b.travelsky.com:443/y8b2b/\"></base>\n" +
                "<meta http-equiv=\"cache-control\" content=\"no-cache\">\n" +
                "<meta http-equiv=\"expires\" content=\"0\">    \n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/> \n" +
                "<meta name=\"author\" content=\"TravelSky\"/>\n" +
                "<script src=\"js/jquery.js\"></script>\n" +
                "<!-- whether need new group -->\n" +
                "\n" +
                "\t<iframe id=\"iframeA\" name=\"iframeA\" src=\"\" width=\"0\" height=\"0\" style=\"display:none;\" ></iframe>\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "function setIframeAhash(){\n" +
                "/* \tvar isDisplayNewGroup = '1';  \n" +
                "\tif(isDisplayNewGroup!='0'){ */\n" +
                "\t\ttry{\n" +
                "\t\t\tvar hashH = $(\".content_all\").height()+20; //get self height\n" +
                "\t\t \tvar URL = document.URL ;\n" +
                "\t\t\t//deal with special page\n" +
                "\t\t\t//ReservationIndex.do\n" +
                "\t\t\tif(URL.indexOf(\"ReservationIndex.do\")!=-1 || hashH <300){\n" +
                "\t\t\t\thashH += 300;\n" +
                "\t\t\t}\n" +
                "\t\t\t//AVFillin.do\n" +
                "\t\t\tif(URL.indexOf(\"AVFillin.do\")!=-1 || URL.indexOf(\"DomesticPricing.do\")!=-1){\n" +
                "\t\t\t\thashH += 80;\n" +
                "\t\t\t}\n" +
                "\t\t\tif(URL.indexOf(\"ShoppingQuery.do\")!=-1 || URL.indexOf(\"AVQuery.do\")!=-1){\n" +
                "\t\t\t\thashH += 50;\n" +
                "\t\t\t}\n" +
                "\t\t\t//ModpasswdF.do\n" +
                "\t\t\tif(URL.indexOf(\"ModpasswdF.do\")!=-1){\n" +
                "\t\t\t\thashH += 100;\n" +
                "\t\t\t}\n" +
                "\t\t\t//FrontxxxQuery.do\n" +
                "\t\t\tvar urlReg = /^[A-Za-z.\\/0-9_:]+Front[A-Za-z]+Query\\.do$/ ;\n" +
                "\t\t\tif(urlReg.test(URL)){\n" +
                "\t\t\t\thashH += 100;\n" +
                "\t\t\t}\n" +
                "\t\t\t/*è?????è????·????????????é??é??BUG?¤???????2014.3.11?????? -è?·*/\n" +
                "\t\t\tif( hashH < 800){\n" +
                "\t\t\t\thashH = 800;\n" +
                "\t\t\t}\n" +
                "\t\t\t/*è?????è????·????????????é??é??BUG?¤???????2014.3.11??????-???*/\n" +
                "\t\t\t//è?????iframeA???src\n" +
                "\t\t\tvar urlC = \"https://y8b2b.travelsky.com:443/y8b2b/iframe.jsp?\"+new Date().getTime(); \n" +
                "\t\t\t\n" +
                "\t\t\tdocument.getElementById(\"iframeA\").src=urlC+\"#\"+hashH; //set height to iframeA's src\n" +
                "\t\t\t//set top focus\n" +
                "\t\t\t//window.scrollTo(0,0) ;\n" +
                "\t\t}catch (e) {\n" +
                "\t\t\t//TODO: handle exception\n" +
                "\t\t}\n" +
                "/* \t} */\n" +
                "}\n" +
                "window.onload=setIframeAhash;\n" +
                "</script>\n" +
                "\n" +
                "<!-- 这里参考WebTool中相应的说明 -->\n" +
                "\n" +
                "<base href=\"https://y8b2b.travelsky.com:443/y8b2b/\"></base>\n" +
                "<head>\n" +
                "\n" +
                "<style>\n" +
                "    .insurance{\n" +
                "        text-align:right!important;\n" +
                "        width:50%;\n" +
                "    }\n" +
                "</style>\n" +
                "<title>PNR入库</title>\n" +
                "<script type=\"text/javascript\" src=\"js/jquery.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"js/util.js?v=20200511155343204\"></script>\n" +
                "<link href=\"css/css_all.css?v=20200511155343204\" rel=\"stylesheet\" type=\"text/css\" /><!-- zhuguojing eidt 20120425-->\n" +
                "<link href=\"css/css_table.css?v=20200511155343204\" rel=\"stylesheet\" type=\"text/css\" />\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"content_all\">\n" +
                "\t<div class=\"top\">\n" +
                "\t\t<!-- 引入页头 -->\n" +
                "\t\t\n" +
                "<meta http-equiv=\"pragma\" content=\"no-cache\">\n" +
                "<meta http-equiv=\"cache-control\" content=\"no-cache\">\n" +
                "<meta http-equiv=\"expires\" content=\"0\">    \n" +
                "\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "var sWaitingBoxLan = \"zh_CN\";\n" +
                "</script>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<base href=\"https://y8b2b.travelsky.com:443/y8b2b/\"></base>\n" +
                "\t\n" +
                "\t</div>\n" +
                "\t<div class=\"clearb\"></div>\n" +
                "\t<div class=\"pagenav\">\n" +
                "\t\t<!-- 引入左侧导航栏 -->\n" +
                "\t\t\n" +
                "\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "var sWaitingBoxLan = \"zh_CN\";\n" +
                "</script>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<base href=\"https://y8b2b.travelsky.com:443/y8b2b/\"></base>\n" +
                "<script src=\"js/jquery.js\"></script>\n" +
                "<script src=\"js/jqueryUI/jquery.ui.core.min.js?v=20200511155343204\"></script>\n" +
                "<script src=\"js/jqueryUI/jquery.ui.widget.min.js?v=20200511155343204\"></script>\n" +
                "<script src=\"js/jqueryUI/jquery.ui.accordion.min.js?v=20200511155343204\"></script>\n" +
                "\n" +
                "\t</div>\n" +
                "\t<div class=\"booking\">\n" +
                "\t\t<div class=\"book_tit\">PNR入库\n" +
                "\t\t  <p>\n" +
                "\t\t\t<span class=\"info_num\">1&nbsp;&nbsp;<span class=\"info_numf\">输入PNR</span></span>\n" +
                "\t\t\t\t<span class=\"info_num_cur\">2&nbsp;&nbsp;<span class=\"info_num_curf\">选择</span></span>\n" +
                "\t\t\t\t<span class=\"info_num\">3&nbsp;&nbsp;<span class=\"info_numf\">填写确认订单</span></span>\n" +
                "\t\t\t\t<span class=\"info_num\">4&nbsp;&nbsp;<span class=\"info_numf\">支付</span></span>\n" +
                "\t\t\t\t<span class=\"info_num\">5&nbsp;&nbsp;<span class=\"info_numf\">出票完成</span></span>\n" +
                "\t\t  </p>\n" +
                "\t\t</div>\n" +
                "\t\t<div class=\"clearb\"></div>\n" +
                "\t\t<!-- PNR内容及注意事项 -->\n" +
                "\t\t<div class=\"confirm_p1\">\n" +
                "\t\t\t<div class=\"conf_tit\">PNR内容<a href=\"javascript:void(0);\" onclick=\"return false;\"><img src=\"images/confirm_titab.gif\" alt=\"关闭\" width=\"9\" height=\"9\" border=\"0\" id=\"pnrimg\"/></a></div>\n" +
                "\t\t\t<div class=\"conf_p1_dis\" id=\"pnrcontext\">\n" +
                "\t\t\t齐宝瑞;<BR/><BR/>  Y87541 E   FR10JUL  ZUHTNA HK1   1030 1515      E T1--<BR/><BR/>WNZ201 \n" +
                "\t\t\t</div>\n" +
                "\t\t\t\n" +
                "\t\t\t<div class=\"conf_tit\">注意事项<a href=\"javascript:void(0);\" onclick=\"return false;\"><img src=\"images/confirm_titab.gif\" alt=\"打开\" width=\"9\" height=\"9\" border=\"0\" id=\"carefulimg\"/></a></div>\n" +
                "\t\t\t<div id=\"carefulcontext\">注意事项内容</div>\n" +
                "\t\t\t\n" +
                "\t\t</div>\n" +
                "\t\t<div class=\"clearb\"></div>\n" +
                "\t\t<!-- 航班信息 -->\n" +
                "\t\t<div class=\"confirm_p2\">\n" +
                "\t\t\t<div class=\"conf_tit conf_tit2\">航班信息</div><!-- 航班信息 -->\n" +
                "\t\t    <div class=\"conf_p2_dis\">\n" +
                "     \t\t\t\t\n" +
                "     \t\t\t\t \n" +
                "\t\t \t\t\t\n" +
                "\t\t \t\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tableleft\">\n" +
                " \t\t\t \t\t\t\t<tr>\n" +
                "    \t\t\t\t\t\t\t<td width=\"10%\" class=\"cont_p2_lef\"><img src=\"images/conf_logo.gif\" alt=\"航空公司logo\"/><span class=\"fc1\">Y87541</span></td>\n" +
                "    \t\t\t\t\t\t\t<td width=\"90%\" class=\"cont_p2_rig\">\n" +
                "    \t\t\t\t\t\t\t\t<table width=\"98%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"tableleft2\">\n" +
                "      \t\t\t\t\t\t\t\t\t<tr>\n" +
                "        \t\t\t\t\t\t\t\t\t<th>起飞：</th>\n" +
                "        \t\t\t\t\t\t\t\t\t<td colspan=\"5\">珠海金湾机场ZUH(2020-07-10 10:30)</td>\n" +
                "        \t\t\t\t\t\t\t\t</tr>\n" +
                "        \t\t\t\t\t\t\t\t<tr>\t\n" +
                "        \t\t\t\t\t\t\t\t\t<th>到达：</th>\n" +
                "        \t\t\t\t\t\t\t\t\t<td colspan=\"5\">济南遥墙机场TNA(2020-07-10 15:15)</td>\n" +
                "     \t\t\t\t\t \t\t\t\t</tr>\n" +
                "      \t\t\t\t\t\t\t\t\t<tr>\n" +
                "        \t\t\t\t\t\t\t\t\t<th>机型：</th>\n" +
                "        \t\t\t\t\t\t\t\t\t<td>738</td>\n" +
                "       \t\t\t\t\t\t\t\t\t\t<th>折扣：</th>\n" +
                "        \t\t\t\t\t\t\t\t\t<td colspan=\"3\">\n" +
                "        \t\t\t\t\t\t\t\t\t\tE \n" +
                "        \t\t\t\t\t\t\t\t\t\t</td>\n" +
                "      \t\t\t\t\t\t\t\t\t</tr>\n" +
                "     \t\t\t\t    \t\t\t\t<tr>\n" +
                "        \t\t\t\t\t\t\t\t\t<th>公布运价：</th>\n" +
                "        \t\t\t\t\t\t\t\t\t<td>成人：-</td>\n" +
                "        \t\t\t\t\t\t\t\t\t<th>机场建设费：</th>\n" +
                "        \t\t\t\t\t\t\t\t\t<td>成人：50.0  \n" +
                "        \t\t\t\t\t\t\t\t\t\t\n" +
                "        \t\t\t\t\t\t\t\t\t\t\n" +
                "        \t\t\t\t\t\t\t\t\t\t\n" +
                "        \t\t\t\t\t\t\t\t\t\t\n" +
                "        \t\t\t\t\t\t\t\t\t</td>\n" +
                "        \t\t\t\t\t\t\t\t\t<th>燃油附加：</th>\n" +
                "        \t\t\t\t\t\t\t\t\t<td>成人：0.0\n" +
                "        \t\t\t\t\t\t\t\t\t\t\n" +
                "        \t\t\t\t\t\t\t\t\t\t\n" +
                "        \t\t\t\t\t\t\t\t\t\t\n" +
                "        \t\t\t\t\t\t\t\t\t\t\n" +
                "        \t\t\t\t\t\t\t\t\t</td>\n" +
                "      \t\t\t\t\t\t\t\t\t</tr>\n" +
                "    \t\t\t\t\t\t\t</table>\n" +
                "    \t\t   \t\t\t\t</td>\n" +
                "  \t\t\t\t\t\t</tr>\n" +
                "\t\t \t\t\t</table>\n" +
                "\t\t \t\t\t\t\t\n" +
                "         \t\t\t\n" +
                "\t\t\t</div>\n" +
                "\t  \t</div>\n" +
                "\t  \t<!-- 票面价信息和购买保险 -->\n" +
                "\t\t<div class=\"confirm_p2\">\n" +
                "\t\t\t<div class=\"conf_tit\">票价信息</div><!-- 票价信息 -->\n" +
                "\t\t\t<div class=\"conf_p2_dis\">\n" +
                "\t\t\t<!-- 票面价信息 -->\n" +
                "\t\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tableinfo2\">\n" +
                "\t\t\t<tr>\n" +
                " \t\t\t\t<th width=\"72px\" >选中</th>\n" +
                " \t\t\t\t<th width=\"90px\" >票面价</th>\n" +
                " \t\t\t\t\n" +
                " \t\t\t\t\n" +
                " \t\t\t\t<th width=\"120px\" >基础代理费率%</th>\n" +
                " \t\t\t\t<th width=\"120px\" >附加代理费率%</th>\n" +
                " \t\t\t\t\n" +
                " \t\t\t\t<th>EI</th>\n" +
                "\t\t\t</tr>\n" +
                "  \t\t\t\n" +
                " \t \t\t\t<tr>\n" +
                "    \t\t\t\t<td ><label>\n" +
                "      \t\t\t\t\t<input type=\"radio\"  id=\"adradio\" name=\"adradio\" adultPrice='320.0' value='0' onclick=\"doclick()\"/>\n" +
                "    \t\t\t\t</label></td>\n" +
                "    \t\t\t\t<td >320.0(成人)</td>\n" +
                "    \t\t\t\t\n" +
                "    \t\t\t\t\n" +
                "\t    \t\t\t\t<td >0.0</td>\n" +
                "\t    \t\t\t\t<td >0.0</td>\n" +
                "    \t\t\t\t\n" +
                "    \t\t\t\t<td class=\"tdbor1\">EI/不得自愿签转</td>\n" +
                "  \t\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t</table>\n" +
                "\t\t\t<p></p>\n" +
                "\n" +
                "         <!--当保险销售开关,航意险文件下载开关开启以及保险状态为启用的时候,显示航意险文件-->\n" +
                "                \n" +
                "\t\t\t<!-- 购买保险 -->\n" +
                "\t\t\t<form id=\"hostinform\" action=\"PnrHostin.do\" method=\"post\">\n" +
                "\t\t\t\n" +
                "\t\t\t<input type=\"hidden\" id=\"adu\" name=\"adusign\" value=\"-1\" />\n" +
                "\t\t\t<input type=\"hidden\" id=\"chd\" name=\"chdsign\" value=\"-1\" />\n" +
                "\t\t\t<input type=\"hidden\" id=\"inf\" name=\"infsign\" value=\"-1\" />\n" +
                "\t\t\t<input type=\"hidden\" id=\"linkerMobile\" name=\"linkerMobile\" value=\"\" />\n" +
                "\t\t\t<input type=\"hidden\" name=\"vipCode\" value='' />\n" +
                "\t\t\t<input type=\"hidden\" name=\"BPnrNo\" value='NWL4EQ' />\n" +
                "\t\t\t<input type=\"hidden\" name=\"priceData\" id=\"priceData\" value= '' />\n" +
                "\t\t\t<input type=\"hidden\" name=\"subsystem\" id=\"subsystem\" value='' />\n" +
                "\t\t\t<input type=\"hidden\" name=\"rand4page\" id=\"rand4page\" value='6959' />\n" +
                "\t\t\t<input type=\"hidden\" name=\"sellerCode\" id=\"sellerCode\" value=\"\"/>\n" +
                "\t\t\t<input type=\"hidden\" name=\"insPayAmont\" id=\"insPayAmont\" value=\"\"/>\n" +
                "\t\t\t<input type=\"hidden\" name=\"insAgentAmont\" id=\"insAgentAmont\" value=\"\"/>\n" +
                "\t\t\t<input type=\"hidden\" name=\"tourCode\" id=\"tourCode\" value=\"\"/>\n" +
                "\t\t\t</form>\n" +
                "  \t<div class=\"confirm_p2\">\n" +
                "\t<div class=\"conf_p2_dis\">\t\n" +
                "\t\t<div id=\"infodiv1\">\t\n" +
                "\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tableFNFC\">\n" +
                "\t\t  \t<tr>\n" +
                "   \t\t\t\t<th><b>PNR类型：</b></th>\n" +
                "    \t\t\t<td>\n" +
                "    \t\t\t\t\n" +
                "    \t\t\t\t散客\n" +
                "    \t\t\t\t\n" +
                "    \t\t\t</td>\n" +
                "  \t\t\t</tr>\n" +
                "  \t\t\t\n" +
                "  \t\t\t<tr>\n" +
                "\t    \t\t<th><b>票面价：</b></th>\n" +
                "\t    \t\t<td>&nbsp;</td>\n" +
                "  \t\t\t</tr>  \t\t\t\n" +
                "  \t\t\t<tr>\n" +
                "    \t\t\t<th><b>FC：</b></th>\n" +
                "    \t\t\t<td>&nbsp;</td>\n" +
                "  \t\t\t</tr>\n" +
                "  \t\t\t<tr>\n" +
                "   \t\t\t\t<th><b>成人/儿童EI：</b></th>\n" +
                "    \t\t\t<td>&nbsp;</td>\n" +
                "  \t\t\t</tr>  \t\t\t\n" +
                "\t  \t\t<tr>\n" +
                "\t    \t\t<th><b>票面价总计：</b></th>\n" +
                "\t    \t\t<td>&nbsp;</td>\n" +
                "\t  \t\t</tr>\n" +
                "\t  \t\t<tr>\n" +
                "\t    \t\t<th><b>税费总计：</b></th>\n" +
                "\t    \t\t<td>&nbsp;</td>\n" +
                "\t  \t\t</tr>\n" +
                "\t  \t\t<tr>\n" +
                "\t    \t\t<th><b>代理费总计：</b></th>\n" +
                "\t    \t\t<td>&nbsp;</td>\n" +
                "\t  \t\t</tr>\n" +
                "\t  \t\t<tr>\n" +
                "\t    \t\t<th><b>支付金额总计：</b></th>\n" +
                "\t    \t\t<td><span class=\"fc1 fs1 fb1\">&nbsp;</span>&nbsp;</td>\n" +
                "\t  \t\t</tr>\n" +
                "\t </table>\n" +
                "    </div>\n" +
                "      \n" +
                "    \n" +
                "    <!-- 售票员代码 -->\t\t\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "  \n" +
                "  </div>\n" +
                "</div>\n" +
                "</div>\n" +
                "\t\t</div>\t\n" +
                "\t\t\n" +
                "\t     \t \n" +
                "\t\t  \t\t<input id=\"hostin\" type=\"button\" class=\"but1 top20 left300\" value=\"入库\"/>\n" +
                "\t\t  \t\n" +
                "\t\t  \t<span id=\"caculateErrNotice\" class=\"fc1 top20 left220\" style=\"color: red; display: none; font-weight: bold;\">运价计算错误，总支付金额必须大于零！</span>\n" +
                "   \t  \t\n" +
                "  </div>\n" +
                "\n" +
                "\t<div class=\"clearb\"></div>\n" +
                "\t<!-- 引入页尾 -->\n" +
                "\t\n" +
                "\n" +
                "\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "var sWaitingBoxLan = \"zh_CN\";\n" +
                "</script>\n" +
                "\n" +
                "\n" +
                "<base href=\"https://y8b2b.travelsky.com:443/y8b2b/\"></base>\n" +
                "\n" +
                "\t<!-- 引入浏览统计 -->\n" +
                "\t\n" +
                "\n" +
                "\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "var sWaitingBoxLan = \"zh_CN\";\n" +
                "</script>\n" +
                "\n" +
                "<base href=\"https://y8b2b.travelsky.com:443/y8b2b/\"></base>\n" +
                "<div class=\"cnzzer\">\n" +
                "\t  \n" +
                "</div>\t\n" +
                "\n" +
                "\n" +
                "</div>\n" +
                "</body>\n" +
                "<script type=\"text/javascript\"  language=\"javascript\">\n" +
                "\t//add by zlong 2013/1/21 国际化\n" +
                "\tvar msg_zl_364 = \"请您输入联系手机\";//请您输入联系手机\n" +
                "\tvar msg_zl_368 = \"抱歉,您输入的联系手机格式有误,请输入11位的手机号码\";//抱歉,您输入的联系手机格式有误,请输入11位的手机号码\n" +
                "\tvar msg_zl_405 = \"您当前预订的是纯儿童票!\";//您当前预订的是纯儿童票!\n" +
                "\tvar msg_zl_406 = \"\\n航空公司规定儿童乘机必须有成人陪同\";//\\n航空公司规定儿童乘机必须有成人陪同\n" +
                "\tvar msg_zl_407 = \"\\n请确认所有儿童乘客有成人陪同\";//\\n请确认所有儿童乘客有成人陪同\n" +
                "\tvar msg_zl_408 = \"\\n如无成人陪同，乘客可能无法登机\";//\\n如无成人陪同，乘客可能无法登机\n" +
                "\tvar msg_zl_409 = \"\\n您确定预订纯儿童票吗？\";//\\n您确定预订纯儿童票吗？\n" +
                "\tvar msg_zl_417 = \"PNR含有NO航段，确定入库后，NO航段会被系统删除?\";//PNR含有NO航段，确定入库后，NO航段会被系统删除?\n" +
                "\tvar msg_zl_436 = \"抱歉，您还没有选择成人运价，请选择后再提交\";//抱歉，您还没有选择成人运价，请选择后再提交\n" +
                "\tvar msg_zl_438 = \"抱歉，您还没有选择儿童运价，请选择后再提交\";//抱歉，您还没有选择儿童运价，请选择后再提交\n" +
                "\tvar msg_zl_440 = \"抱歉，您还没有选择婴儿运价，请选择后再提交\";//抱歉，您还没有选择婴儿运价，请选择后再提交\n" +
                "\tvar msg_zl_441 = \"抱歉，您还没有选择军残运价，请选择后再提交\";//抱歉，您还没有选择军残运价，请选择后再提交\n" +
                "\tvar msg_zl_442 = \"抱歉，您还没有选择警残运价，请选择后再提交\";//抱歉，您还没有选择警残运价，请选择后再提交\n" +
                "\tvar msg_sellerCodeFormatErr = \"您输入的售票员代码格式有误，请您重新输入\";//您输入的售票员代码格式有误,请您重新输入\n" +
                "\t//end\n" +
                "\t/**成人最低价格*/\n" +
                "\tvar adultMinPrice = \"320.0\";\n" +
                "\t/**儿童最低价格*/\n" +
                "\tvar childMinPrice = \"\";\n" +
                "\t/**婴儿最低价格*/\n" +
                "\tvar infantMinPrice = \"\";\n" +
                "\t/**军残最低价格*/\n" +
                "\tvar gmMinPrice = \"\";\n" +
                "\t/**警察最低价格*/\n" +
                "\tvar jcMinPrice = \"\"\n" +
                "\t\n" +
                "\t$(document).ready(function(){\n" +
                "\t\tsetTimeout(selectLowestPrice,1000);\n" +
                "\t});\n" +
                "\t//检查手机号码\n" +
                "\tfunction testMobile(str){\n" +
                "\t var pattern = /^1([3|4|5|8])[0-9]{9}$/;\n" +
                "\t return pattern.test(str);\n" +
                "\t}\n" +
                "\n" +
                "\tfunction doclick(){\n" +
                "\t\tvar adu = $(\":radio[name=adradio]\").length == 0?true:false;\n" +
                "\t\tvar chr = $(\":radio[name=chradio]\").length == 0?true:false;\n" +
                "\t\tvar inf = $(\":radio[name=inradio]\").length == 0?true:false;\n" +
                "\t\tvar soldier = $(\":radio[name=gmradio]\").length == 0?true:false;\n" +
                "\t\tvar police = $(\":radio[name=jcradio]\").length == 0?true:false;\n" +
                "\t\t\n" +
                "\t\tvar adui = -1;  \n" +
                "\t\tvar chri = -1;\n" +
                "\t\tvar infi = -1;\n" +
                "\t\tvar soldieri = -1;\n" +
                "\t\tvar policei = -1;\n" +
                "\t\tvar vipcode = $(\":input[name=vipCode]\").val();\n" +
                "\t\n" +
                "\t\t$(\":radio[name=adradio]\").each(function(i){\n" +
                "\t\t\tif($(\":radio[name=adradio]\").get(i).checked == true){\n" +
                "\t\t\t\tadu = true;\n" +
                "\t\t\t\tadui = i;\n" +
                "\t\t\t}\n" +
                "\t\t});\t\n" +
                "\t\t\n" +
                "\t\t$(\":radio[name=gmradio]\").each(function(i){\n" +
                "\t\t\tif($(\":radio[name=gmradio]\").get(i).checked == true){\n" +
                "\t\t\t\tsoldier = true;\n" +
                "\t\t\t\tsoldieri = i;\n" +
                "\t\t\t}\n" +
                "\t\t});\t\n" +
                "\t\t\n" +
                "\t\t$(\":radio[name=jcradio]\").each(function(i){\n" +
                "\t\t\tif($(\":radio[name=jcradio]\").get(i).checked == true){\n" +
                "\t\t\t\tpolice = true;\n" +
                "\t\t\t\tpolicei = i;\n" +
                "\t\t\t}\n" +
                "\t\t});\t\n" +
                "\t\n" +
                "\t\t$(\":radio[name=chradio]\").each(function(i){\n" +
                "\t\t\tif($(\":radio[name=chradio]\").get(i).checked == true){\n" +
                "\t\t\t\tchr = true;\n" +
                "\t\t\t\tchri = i;\n" +
                "\t\t\t}\n" +
                "\t\t});\n" +
                "\t\n" +
                "\t\t$(\":radio[name=inradio]\").each(function(i){\n" +
                "\t\t\tif($(\":radio[name=inradio]\").get(i).checked == true){\n" +
                "\t\t\t\tinf = true;\n" +
                "\t\t\t\tinfi = i;\n" +
                "\t\t\t}\n" +
                "\t\t});\n" +
                "\t\n" +
                "\t\n" +
                "\t\tif($(\"#subsystem\").val()==\"subsystem\"){\n" +
                "\t\t\t$(\"#infodiv1\").html(\"\");\n" +
                "\t\t\t$(\"#infodiv1\").html($(\"#priceData\").val());\t\t\n" +
                "\t\t\ttry {\n" +
                "\t\t   \t\tsetIframeAhash() ;\n" +
                "\t\t\t} catch (e) {\n" +
                "\t\t\t\t\n" +
                "\t\t\t}\n" +
                "\t\t}else{\n" +
                "\t\t\tif(adu&&chr&&inf && soldier && police){\n" +
                "\t\t\t\tdoajax(adui,chri,infi,soldieri,policei,vipcode);\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\n" +
                "\tfunction doajax(adui,chri,infi,soldieri,policei,vipcode){ \n" +
                "\t\tvar param1 = -1;\n" +
                "\t\tvar param2 = -1;\n" +
                "\t\tvar param3 = -1;\n" +
                "\t\tvar param5 = -1;\n" +
                "\t\tvar param6 = -1;\n" +
                "\t\tvar param4 = 6959;\n" +
                "\t\tif(adui != -1){\n" +
                "\t\t\tparam1 = $(\":radio[name=adradio]:eq(\"+adui+\")\").val();\n" +
                "\t\t}\n" +
                "\t\tif(chri != -1){\n" +
                "\t\t\tparam2 = $(\":radio[name=chradio]:eq(\"+chri+\")\").val();\n" +
                "\t\t}\n" +
                "\t\tif(infi != -1){\n" +
                "\t\t\tparam3 = $(\":radio[name=inradio]:eq(\"+infi+\")\").val();\n" +
                "\t\t}\n" +
                "\t\tif(soldieri != -1){\n" +
                "\t\t\tparam5 = $(\":radio[name=gmradio]:eq(\"+soldieri+\")\").val();\n" +
                "\t\t}\n" +
                "\t\tif(policei != -1){\n" +
                "\t\t\tparam6 = $(\":radio[name=jcradio]:eq(\"+policei+\")\").val();\n" +
                "\t\t}\n" +
                "    \t$.post(\"DomesticPnrPriceCompute.do\",{adult:param1,child:param2,infant:param3,randomNum:param4,gm:param5,jc:param6,vipCode:vipcode,doajax:'yes'},function(data){\n" +
                "\t\t\t$(\"#infodiv1\").html(\"\");\n" +
                "\t\t\tif(data.indexOf(\"errorCaculate\") > -1){\n" +
                "\t\t\t\t$(\"#hostin\").hide();\n" +
                "\t\t\t\t$(\"#caculateErrNotice\").show();\n" +
                "\t\t\t}else{\n" +
                "\t\t\t\t$(\"#hostin\").show();\n" +
                "\t\t\t\t$(\"#caculateErrNotice\").hide();\n" +
                "\t\t\t}\n" +
                "\t\t\tif(data.indexOf(\"200016\") > -1) {\n" +
                "\t\t\t\t$(\"#infodiv1\").hide();\n" +
                "\t\t\t} else {\n" +
                "\t\t\t\t$(\"#infodiv1\").html(data);\n" +
                "\t\t\t}\n" +
                "\t\t\tcalculateInsAmonut();\n" +
                "\t\t\ttry {\n" +
                "\t\t   \t\tsetIframeAhash() ;\n" +
                "\t\t\t} catch (e) {\n" +
                "\t\t\t\t// TODO: handle exception\n" +
                "\t\t\t}\n" +
                "\t\t});  \n" +
                "    }\n" +
                "    \n" +
                "\t$(\"#hostin\").click(function(){\n" +
                "\t\tvar adunum = 1;\n" +
                "\t\tvar chdnum = 0;\n" +
                "\t\tvar infnum = 0;\n" +
                "\t\tvar soldierNum = 0;\n" +
                "\t\tvar policeNum = 0;\n" +
                "\t\tvar needmobile = false;\n" +
                "\t\tvar cdeSwitch = 0;\n" +
                "\t\n" +
                "\t\t//CDE暂不支持保险\n" +
                "\t\tif(cdeSwitch == 1){\n" +
                "\t\t\tvar insadultnum=0;\n" +
                "\t\t\tvar inschildnum=0;\n" +
                "\t\t\tvar insSoldierNum=0;\n" +
                "\t\t\tvar insPoliceNum=0;\n" +
                "\t\t\t$(\"input[type=checkbox][name=adultInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\tinsadultnum = insadultnum+1;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t\t$(\"input[type=checkbox][name=childInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\tinschildnum = inschildnum+1;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t\t$(\"input[type=checkbox][name=soldierInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\tinsSoldierNum = insSoldierNum+1;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t\t$(\"input[type=checkbox][name=policeInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\tinsPoliceNum = insPoliceNum+1;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t\tif(insadultnum > 0 || inschildnum > 0 || insSoldierNum > 0 || insPoliceNum > 0){\n" +
                "\t\t\t\talert(\"抱歉，CDE暂不支持保险\");\n" +
                "  \t\t\t\treturn false;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\t\n" +
                "\t\tif(needmobile){\n" +
                "\t\t\tif($.trim($(\"#contactorMobile\").val()) == \"\"){\n" +
                "\t\t\t\talert(msg_zl_364);\n" +
                "\t\t\t\treturn false;\n" +
                "\t\t\t}\t\n" +
                "\t\t\tif(!testMobile($.trim($(\"#contactorMobile\").val()))){\n" +
                "\t\t\t\talert(msg_zl_368);\n" +
                "\t\t\t\treturn false;\n" +
                "\t\t\t}\t\n" +
                "\t\t}\n" +
                "\t\n" +
                "\t\tvar aduflag = adunum > 0?false:true;\n" +
                "\t\tvar chdflag = chdnum > 0?false:true;\n" +
                "\t\tvar infflag = infnum > 0?false:true;\n" +
                "\t\tvar soldierFlag = soldierNum > 0?false:true;\n" +
                "\t\tvar policeFlag = policeNum > 0?false:true;\n" +
                "\t\t\n" +
                "\t\tif(adunum > 0){\n" +
                "\t\t\t$(\":radio[name=adradio]\").each(function(i){\n" +
                "\t\t\t\tif($(\":radio[name=adradio]\").get(i).checked == true){\n" +
                "\t\t\t\t\taduflag = true;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t}\n" +
                "\t\t\n" +
                "\t\tif(soldierNum > 0){\n" +
                "\t\t\t$(\":radio[name=gmradio]\").each(function(i){\n" +
                "\t\t\t\tif($(\":radio[name=gmradio]\").get(i).checked == true){\n" +
                "\t\t\t\t\tsoldierFlag = true;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t}\n" +
                "\t\t\n" +
                "\t\tif(policeNum > 0){\n" +
                "\t\t\t$(\":radio[name=jcradio]\").each(function(i){\n" +
                "\t\t\t\tif($(\":radio[name=jcradio]\").get(i).checked == true){\n" +
                "\t\t\t\t\tpoliceFlag = true;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t}\n" +
                "\t\n" +
                "\t\tif(chdnum > 0){\n" +
                "\t\t\t$(\":radio[name=chradio]\").each(function(i){\n" +
                "\t\t\t\tif($(\":radio[name=chradio]\").get(i).checked == true){\n" +
                "\t\t\t\t\tchdflag = true;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t}\n" +
                "\t\n" +
                "\t\tif(infnum > 0){\n" +
                "\t\t\t$(\":radio[name=inradio]\").each(function(i){\n" +
                "\t\t\t\tif($(\":radio[name=inradio]\").get(i).checked == true){\n" +
                "\t\t\t\t\tinfflag = true;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t}\n" +
                "\t\n" +
                "\t\tif(adunum==0 && soldierNum==0 && policeNum==0){\n" +
                "\t\t\tif(!window.confirm(msg_zl_405\n" +
                "\t\t\t\t+msg_zl_406\n" +
                "\t\t\t\t+msg_zl_407\n" +
                "\t\t\t\t+msg_zl_408\n" +
                "\t\t\t\t+msg_zl_409)){\n" +
                "\t\t\t\treturn false;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tif(aduflag&&chdflag&&infflag && soldierFlag && policeFlag){\n" +
                "\t\t\tif(!check()){\n" +
                "\t\t\t\treturn;\n" +
                "\t\t\t}\n" +
                "\t\t\t//验证售票员代码\n" +
                "\t\t\tif('false' == 'true'){\n" +
                "\t\t\t\tif(\"\"!=$.trim($(\"#ticketSellerCode\").val())){\n" +
                "\t\t\t\t\tif(!CheckSellerCode($.trim($(\"#ticketSellerCode\").val())) || getLength($(\"#ticketSellerCode\").val())>20 ){\n" +
                "\t\t\t\t\t\talert(msg_sellerCodeFormatErr);\n" +
                "\t\t\t\t\t\treturn;\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t\t$(\"#sellerCode\").val($(\"#ticketSellerCode\").val());\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t\t$(\"#tourCode\").val($(\"#tourCodeInput\").val());\n" +
                "\t\t\tvar hasNOSegment = false ;\n" +
                "\t\t\t\n" +
                "\t\t\tif(hasNOSegment){//存在NO航段\n" +
                "\t\t\t\tif(window.confirm(msg_zl_417)){\n" +
                "\t\t\t\t\t$('<input type=\"hidden\"/>')\n" +
                "\t\t\t        .attr('name', 'randomNum')\n" +
                "\t\t\t        .val('6959')\n" +
                "\t\t\t        .appendTo(\"#hostinform\");\n" +
                "\t\t\t        $(\"#linkermobile\").val($(\"#contactorMobile\").val());\n" +
                "\t\t\t        $(\"#hostin\").attr(\"disabled\",true);\n" +
                "\t\t\t\t\t$(\"#hostinform\").submit();\t\t\t\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}else{\n" +
                "\t\t\t\t$('<input type=\"hidden\"/>')\n" +
                "\t\t        .attr('name', 'randomNum')\n" +
                "\t\t        .val('6959')\n" +
                "\t\t        .appendTo(\"#hostinform\");\n" +
                "\t\t        $(\"#linkermobile\").val($(\"#contactorMobile\").val());\n" +
                "\t\t        $(\"#hostin\").attr(\"disabled\",true);\n" +
                "\t\t\t\t$(\"#hostinform\").submit();\n" +
                "\t\t\t} \t\n" +
                "\t\t}else if(!aduflag){\n" +
                "\t\t\talert(msg_zl_436);\n" +
                "\t\t}else if(!chdflag){\n" +
                "\t\t\talert(msg_zl_438);\n" +
                "\t\t}else if(!infflag){\n" +
                "\t\t\talert(msg_zl_440);\n" +
                "\t\t}else if(!soldierFlag){\n" +
                "\t\t\talert(msg_zl_441);\n" +
                "\t\t}else if(!policeFlag){\n" +
                "\t\t\talert(msg_zl_442);\n" +
                "\t\t}\n" +
                "\t});\n" +
                "\t\n" +
                "\t//pnr内容显示隐藏\n" +
                "\t$(\"#pnrimg\").click(function(){\n" +
                "\t\tif($(\"#pnrcontext\").css(\"display\") != \"none\"){\n" +
                "\t\t\t$(\"#pnrcontext\").hide();\n" +
                "\t\t\t$(\"#pnrimg\").attr(\"src\",\"images/confirm_titab2.gif\");\n" +
                "\t\t}else{\n" +
                "\t\t\t$(\"#pnrcontext\").show();\n" +
                "\t\t\t$(\"#pnrimg\").attr(\"src\",\"images/confirm_titab.gif\");\n" +
                "\t\t}\n" +
                "\t});\n" +
                "\t\n" +
                "\t//注意事项内容显示\t\n" +
                "\t$(\"#carefulimg\").click(function(){\n" +
                "\t\tif($(\"#carefulcontext\").css(\"display\") != \"none\"){\n" +
                "\t\t\t$(\"#carefulcontext\").hide();\n" +
                "\t\t\t$(\"#carefulimg\").attr(\"src\",\"images/confirm_titab2.gif\");\n" +
                "\t\t}else{\n" +
                "\t\t\t$(\"#carefulcontext\").show();\n" +
                "\t\t\t$(\"#carefulimg\").attr(\"src\",\"images/confirm_titab.gif\");\n" +
                "\t\t}\n" +
                "\t});\n" +
                " \t\t//购买保险\n" +
                "\t\t//自动计算增加保险后的支付金额，由于运价通过Ajax获取，因此很多操作元素只有运价加载后才会有\n" +
                "\t\tvar insRate = 0.0;\n" +
                "\t\tvar segnum = 1;\n" +
                "\t\tvar insuranceid = '';\n" +
                "\t\tvar inspremiumADT = 0;\n" +
                "\t\tvar inspremiumCHD = 0;\n" +
                "\t\tvar inspremiumINF = 0;\n" +
                "\t\tvar inspremiumGM = 0;\n" +
                "\t\tvar inspremiumJC = 0;\n" +
                "\t\t//调用该方法可以自动刷新页面信息\n" +
                "\t\tfunction calculateInsAmonut(){\n" +
                "\t\t\tif(insuranceid ==''){\n" +
                "\t\t\t\treturn;\n" +
                "\t\t\t}\n" +
                "\t\t\tvar fareAmont = $(\"#fareAmont\").val()*1;\n" +
                "\t\t\tvar insadultnum=0;\n" +
                "\t\t\tvar inschildnum=0;\n" +
                "\t\t\tvar insinfantnum=0;\n" +
                "\t\t\tvar insSoldierNum=0;\n" +
                "\t\t\tvar insPoliceNum=0;\n" +
                "\t\t\t$(\"input[type=checkbox][name=adultInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\tinsadultnum = insadultnum+1;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t\t$(\"input[type=checkbox][name=childInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\tinschildnum = inschildnum+1;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t\t$(\"input[type=checkbox][name=soldierInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\tinsSoldierNum = insSoldierNum+1;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t\t$(\"input[type=checkbox][name=policeInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\tinsPoliceNum = insPoliceNum+1;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t});\n" +
                "\t\t\tvar insAmont = 0;\n" +
                "\t\t\tvar insAmontstr = \"\";\n" +
                "\t\t\tvar insAgentFee = 0;\n" +
                "\t\t\tvar insAgentstr = \"\";\n" +
                "\t\t\t//暂时只支持成人儿童\n" +
                "\t\t\tif(insadultnum>0){\n" +
                "\t\t\t\tinsAmontadult = inspremiumADT*segnum*insadultnum;\n" +
                "\t\t\t\tinsAmont = insAmont+insAmontadult;\n" +
                "\t\t\t\tif(insAmontstr==\"\"){\n" +
                "\t\t\t\t\tinsAmontstr = inspremiumADT.toString()+\"*\"+segnum.toString()+\"*\"+insadultnum.toString();\n" +
                "\t\t\t\t}else{\n" +
                "\t\t\t\t\tinsAmontstr = insAmontstr + \"+\" + inspremiumADT.toString()+\"*\"+segnum.toString()+\"*\"+insadultnum.toString();\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t\tif(insSoldierNum>0){\n" +
                "\t\t\t\tinsAmontSoldier = insSoldierNum*inspremiumGM*segnum;\n" +
                "\t\t\t\tinsAmont = insAmont+ insAmontSoldier;\n" +
                "\t\t\t\tif(insAmontstr==\"\"){\n" +
                "\t\t\t\t\tinsAmontstr = inspremiumGM+\"*\"+segnum.toString()+\"*\"+insSoldierNum;\n" +
                "\t\t\t\t}else{\n" +
                "\t\t\t\t\tinsAmontstr = insAmontstr + \"+\" + inspremiumGM+\"*\"+segnum.toString()+\"*\"+insSoldierNum;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t\tif(insPoliceNum>0){\n" +
                "\t\t\t\tinsAmontPolice = insPoliceNum*inspremiumJC*segnum;\n" +
                "\t\t\t\tinsAmont = insAmont+ insAmontPolice;\n" +
                "\t\t\t\tif(insAmontstr==\"\"){\n" +
                "\t\t\t\t\tinsAmontstr = inspremiumJC+\"*\"+segnum.toString()+\"*\"+insPoliceNum;\n" +
                "\t\t\t\t}else{\n" +
                "\t\t\t\t\tinsAmontstr = insAmontstr + \"+\" + inspremiumJC+\"*\"+segnum.toString()+\"*\"+insPoliceNum;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t\tif(inschildnum>0){\n" +
                "\t\t\t\tinsAmontchild = inschildnum*inspremiumCHD*segnum;\n" +
                "\t\t\t\tinsAmont = insAmont+ insAmontchild;\n" +
                "\t\t\t\tif(insAmontstr==\"\"){\n" +
                "\t\t\t\t\tinsAmontstr = inspremiumCHD+\"*\"+segnum.toString()+\"*\"+inschildnum;\n" +
                "\t\t\t\t}else{\n" +
                "\t\t\t\t\tinsAmontstr = insAmontstr + \"+\" + inspremiumCHD+\"*\"+segnum.toString()+\"*\"+inschildnum;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\tvar insPaystr = insAmont.toFixed(2).toString()+\"(\"+inspremiumADT+\"*\"+insadultnum+\"+\"+inspremiumCHD+\"*\"+inschildnum+\")\";\n" +
                "\t\t\tif(insAmontstr!=\"\"){\n" +
                "\t\t\t\tinsAmontstr = insAmont.toFixed(2).toString()+\"(\"+insAmontstr+\")\";\n" +
                "\t\t\t}else{\n" +
                "\t\t\t\tinsAmontstr = insAmont.toFixed(2).toString();\n" +
                "\t\t\t}\n" +
                "\t\t\t\n" +
                "\t\t\tvar insAgent = insAmont * insRate/100;\n" +
                "\t\t\tvar insAgentstr = insAgent.toFixed(2).toString() + \"(\"+insAmont.toFixed(2).toString()+\"*\"+insRate.toString()+\"%)\";\n" +
                "\t\t\tvar payAmont = fareAmont + insAmont - insAgent;\n" +
                "\t\t\tvar insAgentDetailstr = \"+\"+insAmont.toString()+\"-\"+insAgent.toString();\n" +
                "\t\t\t/* 判断是否支持毛额销售 */\n" +
                "\t\t\tif((1).toString()==='1'){\n" +
                "\t\t\t\tinsAgentDetailstr = \"+\"+insAmont.toString();\n" +
                "\t\t\t\tpayAmont = fareAmont + insAmont;\n" +
                "\t\t\t}\n" +
                "\t\t\t$(\"#insAmount\").html(insAmontstr);//保费总额\n" +
                "\t\t\t$(\"#insAgent\").html(insAgentstr);//保险代理费\n" +
                "\t\t\t$(\"#insPayAmont\").attr(\"value\",insAmont);\n" +
                "\t\t\t$(\"#insAgentAmont\").attr(\"value\",insAgent);\n" +
                "\t\t\t$(\"#payAmount\").html(payAmont.toFixed(2));//支付金额总计\n" +
                "\t\t\t$(\"#insAgentDetail\").html(insAgentDetailstr);\n" +
                "\t\t\ttry {\n" +
                "\t\t\t   \t\tsetIframeAhash() ;\n" +
                "\t\t\t\t} catch (e) {\n" +
                "\t\t\t\t\t// TODO: handle exception\n" +
                "\t\t\t\t}\n" +
                "\t\t\t} \n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\t\tvar msg_ins_che_mobile_adt = \"购买保险乘客联系手机不能为空，请您输入第{0}个成人旅客联系手机！！\";//购买保险,您输入第{0}个成人旅客联系手机不能为空！\n" +
                "\t\t\tvar msg_ins_che_mobile_adt_check = \"第{0}个成人旅客联系手机格式有误，请输入11位的手机号码！！\";//购买保险,您输入第{0}个成人旅客联系手机不能为空！\n" +
                "\t\t\tvar msg_ins_che_mobile_chd = \"购买保险乘客联系手机不能为空，请您输入第{0}个儿童旅客联系手机！！\";//购买保险,{0}您输入第{1}个儿童旅客联系手机不能为空！\n" +
                "\t\t\tvar msg_ins_che_mobile_chd_check = \"第{0}个儿童旅客联系手机格式有误，请输入11位的手机号码！！\";//购买保险,您输入第{0}个成人旅客联系手机不能为空！\t\t\n" +
                "\t\t\tvar msg_ins_che_mobile_gm = \"购买保险乘客联系手机不能为空，请您输入第{0}个军残旅客联系手机！！\";//购买保险,您输入第{0}个军残旅客联系手机不能为空！\n" +
                "\t\t\tvar msg_ins_che_mobile_gm_check = \"第{0}个军残旅客联系手机格式有误，请输入11位的手机号码！！\";//第{0}个军残旅客联系手机格式有误，请输入11位的手机号码！\n" +
                "\t\t\tvar msg_ins_che_mobile_jc = \"购买保险乘客联系手机不能为空，请您输入第{0}个警残旅客联系手机！！\";//购买保险,您输入第{0}个警残旅客联系手机不能为空！\n" +
                "\t\t\tvar msg_ins_che_mobile_jc_check = \"第{0}个警残旅客联系手机格式有误，请输入11位的手机号码！！\";//第{0}个警残旅客联系手机格式有误，请输入11位的手机号码！\n" +
                "\n" +
                "\t\t\tfunction check(){\n" +
                "\t\t\t\tcheckflag = true;\n" +
                "\t\t\t\t//购买保险时，联系人必须填写手机号\n" +
                "\t\t\t\t$(\"input[name=adultInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\t\t\t//勾选了保险，此项不能为空\n" +
                "\t\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\t\tvar x = $(\"input[name=adultPhone]:eq(\"+i+\")\");\n" +
                "\t\t\t\t\t\tif($.trim(x.val())== ''){\n" +
                "\t\t\t\t\t\t\talert(getB2BMessageWithPara(msg_ins_che_mobile_adt, new Array([i+1])));\n" +
                "\t\t\t\t\t\t\tcheckflag = false;\n" +
                "\t\t\t\t\t\t\treturn false;\n" +
                "\t\t\t\t\t\t}else if(!testMobile11($.trim(x.val()))){\n" +
                "\t\t\t\t\t\t\talert(getB2BMessageWithPara(msg_ins_che_mobile_adt_check, new Array([i+1])));\n" +
                "\t\t\t\t\t\t\tcheckflag = false;\n" +
                "\t\t\t\t\t\t\treturn false;\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t});\t\n" +
                "\t\t\t\tif(!checkflag){\n" +
                "\t\t\t\t\treturn false;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t$(\"input[name=soldierInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\t\t//勾选了保险，此项不能为空\n" +
                "\t\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\t\tvar x = $(\"input[name=soldierPhone]:eq(\"+i+\")\");\n" +
                "\t\t\t\t\t\tif($.trim(x.val())== ''){\n" +
                "\t\t\t\t\t\t\talert(getB2BMessageWithPara(msg_ins_che_mobile_gm, new Array([i+1])));\n" +
                "\t\t\t\t\t\t\tcheckflag = false;\n" +
                "\t\t\t\t\t\t\treturn false;\n" +
                "\t\t\t\t\t\t}else if(!testMobile11($.trim(x.val()))){\n" +
                "\t\t\t\t\t\t\talert(getB2BMessageWithPara(msg_ins_che_mobile_gm_check, new Array([i+1])));\n" +
                "\t\t\t\t\t\t\tcheckflag = false;\n" +
                "\t\t\t\t\t\t\treturn false;\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t});\t\n" +
                "\t\t\t\tif(!checkflag){\n" +
                "\t\t\t\t\treturn false;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t$(\"input[name=policeInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\t\t//勾选了保险，此项不能为空\n" +
                "\t\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\t\tvar x = $(\"input[name=policePhone]:eq(\"+i+\")\");\n" +
                "\t\t\t\t\t\tif($.trim(x.val())== ''){\n" +
                "\t\t\t\t\t\t\talert(getB2BMessageWithPara(msg_ins_che_mobile_jc, new Array([i+1])));\n" +
                "\t\t\t\t\t\t\tcheckflag = false;\n" +
                "\t\t\t\t\t\t\treturn false;\n" +
                "\t\t\t\t\t\t}else if(!testMobile11($.trim(x.val()))){\n" +
                "\t\t\t\t\t\t\talert(getB2BMessageWithPara(msg_ins_che_mobile_jc_check, new Array([i+1])));\n" +
                "\t\t\t\t\t\t\tcheckflag = false;\n" +
                "\t\t\t\t\t\t\treturn false;\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t});\t\n" +
                "\t\t\t\tif(!checkflag){\n" +
                "\t\t\t\t\treturn false;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t$(\"input[name=childInsuranceIndex]\").each(function(i){\n" +
                "\t\t\t\t\t//勾选了保险，此项不能为空\n" +
                "\t\t\t\t\tif(this.checked){\n" +
                "\t\t\t\t\t\tvar x = $(\"input[name=childPhone]:eq(\"+i+\")\");\n" +
                "\t\t\t\t\t\tif($.trim(x.val())== ''){\n" +
                "\t\t\t\t\t\t   alert(getB2BMessageWithPara(msg_ins_che_mobile_chd, new Array([i+1])));\n" +
                "\t\t\t\t\t\t   checkflag = false;\n" +
                "\t\t\t\t\t\t   return false;\n" +
                "\t\t\t\t\t\t}else if(!testMobile11($.trim(x.val()))){\n" +
                "\t\t\t\t\t\t\talert(getB2BMessageWithPara(msg_ins_che_mobile_chd_check, new Array([i+1])));\n" +
                "\t\t\t\t\t\t\tcheckflag = false;\n" +
                "\t\t\t\t\t\t\treturn false;\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t});\t\n" +
                "\t\t\t\tif(!checkflag){\n" +
                "\t\t\t\t\treturn false;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\treturn true;\n" +
                "\t\t\t}\n" +
                "\t\t\t\n" +
                "\t\t\tfunction testMobile11(str){\n" +
                "\t\t\t\t var pattern = /^1([3|4|5|7|8])[0-9]{9}$/;\n" +
                "\t\t\t\t return pattern.test(str);\n" +
                "\t\t\t}\n" +
                "\t\n" +
                "\t\t\tfunction CheckSellerCode(str){\n" +
                "\t\t\t\tvar pattern = /^[_a-zA-Z0-9]+$/;\n" +
                "\t\t\t\tif(pattern.test(str))\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\treturn true;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\treturn false;\n" +
                "\t\t\t}\n" +
                "\t\t\t\n" +
                "\t\t\tfunction selectLowestPrice() {\n" +
                "\t\t\t\tif (null !== adultMinPrice) {\n" +
                "\t\t\t\t\tvar adtRadio = $(\"input:radio[adultPrice=\" + adultMinPrice + \"]\").eq(0);\n" +
                "\t\t\t\t\t$(adtRadio).attr(\"checked\", true);\n" +
                "\t\t\t\t\t$(adtRadio).click();\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\tif (null !== childMinPrice) {\n" +
                "\t\t\t\t\tvar chdRadio = $(\"input:radio[childPrice=\" + childMinPrice + \"]\").eq(0);\n" +
                "\t\t\t\t\t$(chdRadio).attr(\"checked\", true);\n" +
                "\t\t\t\t\t$(chdRadio).click();\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\tif (null !== infantMinPrice) {\n" +
                "\t\t\t\t\tvar infRadio = $(\"input:radio[infantPrice=\" + infantMinPrice + \"]\").eq(0);\n" +
                "\t\t\t\t\t$(infRadio).attr(\"checked\", true);\n" +
                "\t\t\t\t\t$(infRadio).click();\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\tif (undefined !== gmMinPrice && null !== gmMinPrice ) {\n" +
                "\t\t\t\t\tvar gmRadio = $(\"input:radio[gmPrice=\" + gmMinPrice + \"]\").eq(0);\n" +
                "\t\t\t\t\t$(gmRadio).attr(\"checked\", true);\n" +
                "\t\t\t\t\t$(gmRadio).click();\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\tif (undefined !== jcMinPrice && null !== jcMinPrice ) {\n" +
                "\t\t\t\t\tvar jcRadio = $(\"input:radio[jcPrice=\" + jcMinPrice + \"]\").eq(0);\n" +
                "\t\t\t\t\t$(jcRadio).attr(\"checked\", true);\n" +
                "\t\t\t\t\t$(jcRadio).click();\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "</script>\n" +
                "</html>";
        Document document = Jsoup.parse(back);
        String price = "420";
        Elements elements = document.getElementsByAttributeValue("name","adradio");
        float lowerPrice = 0;
        String value = "-1";
        for(Element element:elements){
            String adultPrice = element.attr("adultPrice");

                if(lowerPrice==0){
                    lowerPrice = Float.parseFloat(adultPrice);
                    value = element.attr("value");
                }
                if(lowerPrice>Float.parseFloat(adultPrice)){
                    lowerPrice = Float.parseFloat(adultPrice);
                    value = element.attr("value");
                }
            }
        if(lowerPrice==0){
            /*resultMap.put("error", "当前所选运价"+(Float.parseFloat(price) / passengers.length())+"不存在");
            return resultMap;*/
            System.out.println("不存在\"");
        }
        String adultPrice = lowerPrice+"";
        String proxyPrice = document.getElementsByClass("tableinfo2").get(0).getElementsByTag("tr").get(1)
                .getElementsByTag("td").get(2).text().split("\\(")[0];
        // 比较运价
        if (Float.parseFloat(adultPrice) > (Float.parseFloat(price) / 1)) {
            /*resultMap.put("error", "当前运价" + adultPrice + "高于票面价" + price);
            return resultMap;*/
            System.out.println("高于票面价");
        }
    }

    @org.junit.Test
    public void a9() throws IOException {
        String orderNo = "2020101616364964104845";
        String token = "4f233c4f-96aa-41b9-8c0b-5cffcd7db376";
        HttpGet get = new HttpGet("/api/iorder/v2/detail/"+orderNo);
        HttpHost target = new HttpHost("www.xiamenair.com", 443, "https");
        get.setHeader("Host", "www.xiamenair.com");
        get.setHeader("Referer", "https://www.xiamenair.com/zh-cn/nodetails?orderNo="+orderNo);
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
        get.setHeader("accessToken",token);
        CloseableHttpClient httpclient = HttpClients.custom().build();
        CloseableHttpResponse response = httpclient.execute(target, get);
        String back = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println(back);
    }

    @org.junit.Test
    public void w1book() throws IOException {
        Map<String, String> resMap = new HashMap<>();
        resMap.put("id","");
        resMap.put("memberID","");
        resMap.put("certNumber","");
        resMap.put("loginKeyInfo","");
        resMap.put("token","");
    }
}