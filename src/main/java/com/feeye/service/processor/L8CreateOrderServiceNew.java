//package com.feeye.service.processor;
//
//import java.io.*;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLDecoder;
//import java.util.*;
//import java.util.concurrent.*;
//
//import com.alibaba.fastjson.JSON;
//import com.feeye.service.DailiyunService;
//import com.feeye.util.*;
//import com.gargoylesoftware.htmlunit.*;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.gargoylesoftware.htmlunit.util.Cookie;
//import com.gargoylesoftware.htmlunit.util.NameValuePair;
//
//import oracle.net.aso.f;
//
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.httpclient.auth.MalformedChallengeException;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpResponse;
//import org.apache.http.auth.AUTH;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.protocol.HttpClientContext;
//import org.apache.http.impl.auth.BasicScheme;
//import org.apache.http.impl.client.*;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import com.feeye.bean.CabinInfo;
//import com.feeye.bean.FlightInfo;
//import com.feeye.common.Constant;
//import com.feeye.feeyetcb.util.DateUtil;
//import com.feeye.proxyip.ProxyService;
//import com.feeye.service.official.OfficialMain;
//
//
//
///**
// * @author peng.lw
// * @ClassName: L8Processor
// * @Description: TODO(祥鹏航空官网抓取)
// * @date 2015-5-7 下午04:32:44
// */
//
//public class L8CreateOrderService {
//    //支付前最大耗时 单位分钟
//    private static  final int maxcreateOrdertime=8;
//    private static final String errormsg="abuyun error";
//    private static final String abuyunerror="Please try again later";
//
//    private static final String splitchar = "#@_@#";
//
//    private static final Logger logger = Logger.getLogger(L8CreateOrderService.class);
//
//    public static Map<String, String> cabin_priceType_map = new HashMap<String, String>();
//
//    public static String desc = "";
//
//
//
//    public String createOrdertest(String orderJson,int retrycount){
//        try {
//            JSONObject json = new JSONObject(orderJson);
//            String order_id=json.getString("id");
//            //生单失败
//            /*sendCreateOrderInfo("error", "生单失败", "", "policytest", "", order_id, "", "", null, "", "", "false");
//            logger.info("生单失败");*/
//            //生单成功
//            /*sendCreateOrderInfo("success", "生单成功", "", "policytest", "", order_id, "", "", null, "", "", "false");
//            logger.info("生单成功");*/
//            //支付失败
//            /*sendCreateOrderInfo("success", "支付失败", "1000" + "", "policytest", "2018098880", order_id, "", "", null, "0", "", "false");
//            logger.info("支付失败");*/
//            //支付成功
//
//            /*sendCreateOrderInfo("success", "支付成功", "", "policytest", "", order_id, "true", "", null, "0", "", "false");
//            logger.info("支付成功");*/
//            //回填失败
//            /*logger.info("回填失败");
//            sendCreateOrderInfo("success", "未找到票号信息", "1000" + "", "policytest", "2018098880", order_id, "true", "true", "", "0", "", "false");*/
//            //回填成功
//            //sendCreateOrderInfo("success", "获取到票号", "1000" + "", "policytest", "2018098880", order_id, "", "true", "张成锋##420984199108080758##909090", "0", "", "false");
//
//            logger.info("回填成功");
//        } catch (JSONException e) {
//            logger.error("json转换异常",e);
//        }
//
//
//        return "";
//    }
//
//
//    /**
//     *
//     * @param orderJson  订单信息
//     * @param retrycount 选择航班整体重试次数
//     * @param submitretrycount  提交乘客信息整体重试次数
//     * @param starttime
//     * @return
//     */
//    public String createOrder(String orderJson,int retrycount,int submitretrycount,long starttime) {
//        //打印请求参数
//        logger.info("8L出票请求参数 orderJson="+orderJson);
//        //long starttime=System.currentTimeMillis();
//        String group = "";
//        String selectedFlight = "";
//        String backFlight = "";
//        String luggagecode = "";
//        String session = UUID.randomUUID().toString();
//        String childrenUser = "";
//        String order_id = "";
//        String cancelUrl = PropertiesUtils.getPropertiesValue("config", "cancelUrl");
//        String billNo="";
//        long usetime=0;
//        try {
//            JSONObject json = new JSONObject(orderJson);
//            order_id = json.getString("id");
//            //使用代理云
//            String ip_port= DailiyunService.getRandomIp(50);
//            String proxyIp = ip_port.split(":")[0];   //代理IP
//            String proxyPort =ip_port.split(":")[1];  //端口
//            JSONArray flightArr = json.getJSONArray("flights");
//            //添加billNo
//            try {
//                billNo=json.getString("billNo");
//
//            }catch (JSONException e){
//                logger.error("未获取到billno");
//            }
////            String departure = json.getString("departure");
////            String arrival = json.getString("arrival");
////            String departureDate = json.getString("departureDate");
////            String flightNo = json.getString("flightNo");
////            float price = Float.parseFloat(json.getString("price"));
////            String cabin = json.getString("cabin");
//
//            String account = json.getString("account");
//
//            childrenUser = json.getString("username");
//
//            String userAccount[] = account.split("_");
//
//            String otheraccount = json.getString("otheraccount");
//
//            Random random = new Random();
//
//            int s = random.nextInt(999) % (999 - 100 + 1) + 100;
//
//            String cookie = "";
//
//            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                sendOrderStatus(childrenUser, order_id, "已取消出票");
//                logger.info(childrenUser+order_id+"已取消出票");
//                return "取消出票";
//            }
//            logger.info(childrenUser+order_id+" 进入官网首页");
//            sendOrderStatus(childrenUser, order_id, "进入官网首页");
//            //进入首页添加重试 重试次数3
//            int getindexRetry=3;
//            String soso = getIndex(session, proxyIp, proxyPort, cookie);//
//            while((soso==null||(soso.contains(errormsg)||soso.split("=").length!=3))&&getindexRetry>=0){
//            	 sendOrderStatus(childrenUser, order_id, "重新进入官网首页");
//                getindexRetry--;
//                soso= getIndex(session, proxyIp, proxyPort, cookie);
//            }
//            //&&(retrycount==0||submitretrycount==0)
//            if((soso==null||(soso.contains(errormsg)||soso.split("=").length!=3))){
//                logger.info(childrenUser+order_id+"进入官网首页失败");
//                sendCreateOrderInfo("error", "进入官网首页失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                return soso;
//            }
//
//            String content = "";
//            String[] rs = null;
//
//            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                sendOrderStatus(childrenUser, order_id, "已取消出票");
//                logger.info(order_id+childrenUser+"已取消出票");
//                return "取消出票";
//            }
//            logger.info(order_id+childrenUser+"登录官网账号");
//            sendOrderStatus(childrenUser, order_id, "登录官网账号");
//
//
//            //官网登录
//            cookie = login(userAccount[0], userAccount[1], session, cookie, soso, proxyIp,proxyPort , otheraccount);
//            logger.info("登录状态--"+cookie);
//            //修改登录判断条件
//            if (!cookie.contains("true")) {
//                int count = 0;
//                //如果验证码错误，最多重试5次
//                while (!cookie.contains("true") && count < 6) {
//                    if (cookie.contains("点数不足，请充值")) {
//                        logger.info("验证码账号点数不足，请充值");
//                        sendCreateOrderInfo("error", "验证码账号点数不足，请充值", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                        return "验证码账号点数不足，请充值";
//                    }
//                    if(cookie.contains("官网账号用户名或密码不正确")){
//                        logger.info(order_id+" "+childrenUser+" 官网账号用户名或密码不正确");
//                        sendCreateOrderInfo("error", "官网账号用户名或密码不正确", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                        return "官网账号用户名或密码不正确";
//                    }
//                    if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                        sendOrderStatus(childrenUser, order_id, "已取消出票");
//                        logger.info(childrenUser+"已取消出票");
//                        return "取消出票";
//                    }
//                    //重新获取代理云ip
//                    ip_port=DailiyunService.getRandomIp(50);
//                    proxyIp=ip_port.split(":")[0];
//                    proxyPort=ip_port.split(":")[1];
//                    cookie = login(userAccount[0], userAccount[1], session, cookie, soso,proxyIp,proxyPort, otheraccount);
//                    count++;
//                }
//                //&&(retrycount==0||submitretrycount==0)  登录失败直接返回
//                if (!cookie.contains("true")) {
//                    logger.info("账号登录失败");
//                    sendCreateOrderInfo("error", "账号登录失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                    return "登录失败";
//                } else {
//                    sendOrderStatus(childrenUser, order_id, "官网账号登录成功");
//                    logger.info("登录成功");
//                }
//            }else{
//                sendOrderStatus(childrenUser, order_id, "官网账号登录成功");
//                logger.info("登录成功");
//            }
//
//            sendOrderStatus(childrenUser, order_id, "查询航班信息");
//            logger.info("8L官网出票--加载官网价格--" + order_id);
//            //desc 参数无效，重新生成获取航班
//            int countDesc = 0;
//            //暂时注释 从文件中获取查询结果
//            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                sendOrderStatus(childrenUser, order_id, "已取消出票");
//                logger.info(order_id+childrenUser+"已取消出票");
//                return "取消出票";
//            }
//            cookie= cookie.split(splitchar)[0];
//            //content = getFlightInfoList(session, departure, arrival, departureDate, cookie, proxyIp, proxyPort,otheraccount);
//           //content=searchFlighterbefore(cookie,departure,arrival,departureDate,proxyIp,proxyPort);
//            //更新ip
//            ip_port=DailiyunService.getRandomIp(50);
//            proxyIp=ip_port.split(":")[0];
//            proxyPort=ip_port.split(":")[1];
//            int retryCountIndex = 0;
//            while (StringUtils.isEmpty(selectedFlight)||(flightArr.length()==2&&StringUtils.isEmpty(backFlight))&&retryCountIndex<3) {
//
//	            Map<String,String> resultContent = new HashMap<String,String>();
//	            resultContent=getFlightInfoList( session,  orderJson,  cookie,  proxyIp,  proxyPort, retrycount, otheraccount);
//	            if(resultContent==null||resultContent.size()==0){
//	            	content = "查询异常";
//	            }else{
//	            	content = resultContent.get("error");
//	            }
//
//	            if(StringUtils.isNotEmpty(content)){
//		            while (StringUtils.isNotEmpty(content)||"查询异常".equals(content)||content.contains("http://www.luckyair.net/error.jsp")
//		                    || content.contains("http://www.luckyair.net/404.html")
//		                    ||content.contains(errormsg)||content.equals("响应超时")||content.contains("http://www.luckyair.net/hnatravel/signcode.jsp")) {
//		                if(countDesc>5){
//		                    break;
//		                }
//		                if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//		                    sendOrderStatus(childrenUser, order_id, "已取消出票");
//		                    logger.info(childrenUser+"已取消出票");
//		                    return "取消出票";
//		                }
//		                //添加超时取消
//		                //超过最大创单时间
//		                usetime=(System.currentTimeMillis()-starttime)/1000;
//		                if(usetime>maxcreateOrdertime*60){
//		                    logger.info(order_id+" 超过最大创单时间，停止创单");
//		                    sendCreateOrderInfo("error", "超过最大创单时间，停止创单", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//		                    return "超过最大创单时间，停止创单";
//		                }
//
//		                //content = getFlightInfoList(session, departure, arrival, departureDate, cookie, proxyIp, proxyPort,otheraccount);
//		               //重试跟换ip
//		                ip_port=DailiyunService.getRandomIp(50);
//		                proxyIp=ip_port.split(":")[0];
//		                proxyPort=ip_port.split(":")[1];
//		                resultContent=getFlightInfoList( session,  orderJson,  cookie,  proxyIp,  proxyPort, retrycount, otheraccount);
//		                if(resultContent==null||resultContent.size()==0){
//		                	countDesc++;
//		                	continue;
//		                }
//		                countDesc++;
//		                content = resultContent.get("error");
//		            }
//	            }
//	            boolean hasFlight = false;
//	            for(Map.Entry<String, String> reslut:resultContent.entrySet()){
//	            	String flightNo = reslut.getKey();
//	            	float price = 0;
//	            	String fType = "go";
//	            	for(int i=0;i<flightArr.length();i++){
//	            		JSONObject flight = flightArr.getJSONObject(i);
//	            		String fno = flight.getString("flightNo");
//	            		if(fno.equalsIgnoreCase(flightNo)){
//	            			price = Float.parseFloat(flight.getString("price"));
//	            			fType = flight.getString("fType");
//	            			break;
//	            		}
//	            	}
//	            	List<FlightInfo> list = CrawlData.doCrawl(reslut.getValue());
//	            	logger.info("查询航班解析结果："+JSON.toJSONString(list));
//
//	            	if (list == null) {
//	                    list = new ArrayList<FlightInfo>();
//	                }
//	                for (FlightInfo flightInfo : list) {
//	                    if (flightInfo.getFlightNo().equals(flightNo)) {
//	                        hasFlight = true;
//	                        for (CabinInfo cabinInfo : flightInfo.getCabins()) {
//	                            if (Float.parseFloat(cabinInfo.getPrice()) == price) {
//	                                group = cabinInfo.getGroup();
//	                                if("go".equals(fType)){
//	                                	selectedFlight = cabinInfo.getGroupCode();
//	                                }else if("back".equals(fType)){
//	                                	backFlight = cabinInfo.getGroupCode();
//	                                }
//	                                luggagecode = cabinInfo.getLuggagecode();
//	                                break;
//	                            }
//	                        }
//	                    }
//	                }
//	                logger.info("8L官网出票--" + order_id + "--航班信息:" + list.toString());
//	            }
//	            if (!hasFlight) {
//	                logger.info(order_id+childrenUser+"未查询到航班信息");
//	                sendCreateOrderInfo("error", "未查询到航班信息", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//	                return "ERROR:未查询到航班信息";
//	            }
//	            //&&(retrycount==0||submitretrycount==0)
//	            retryCountIndex = retryCountIndex -1;
//            }
//            if (StringUtils.isEmpty(selectedFlight)||(flightArr.length()==2&&StringUtils.isEmpty(backFlight))) {
//                 logger.info(order_id+childrenUser+"官网已变价，请重新出票");
//                 sendCreateOrderInfo("error", "官网已变价，请重新出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                 return "ERROR:找不到对应的价格";
//            }
//            sendOrderStatus(childrenUser, order_id, "选择航班信息");
//            logger.info("8L官网出票--" + order_id + "--选择航班信息");
//            //检查订单状态、是否已取消
//            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                sendOrderStatus(childrenUser, order_id, "已取消出票");
//                logger.info(order_id+childrenUser+"已取消出票");
//                return "取消出票";
//            }
//            //选择匹配航班  重选择航班后的结果页面中获取出发到达地
//            FlightInfo selectedFlightInfo = selectFlightNo(group, selectedFlight, session, cookie, "", proxyIp, proxyPort, luggagecode,backFlight);
//            //整体重试提交航班结果  5次
//            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                sendOrderStatus(childrenUser, order_id, "已取消出票");
//                logger.info(order_id+childrenUser+"已取消出票");
//                return "取消出票";
//            }
//            while (selectedFlightInfo == null && retrycount > 0) {
//                sendOrderStatus(childrenUser, order_id, "选择航班重试中。。。。");
//                logger.info(order_id+"选择航班重试中。。。。");
//                if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                    sendOrderStatus(childrenUser, order_id, "已取消出票");
//                    logger.info(order_id+childrenUser+"已取消出票");
//                    return "取消出票";
//                }
//                logger.info(order_id+"选择航班整体重试次数："+retrycount);
//                return   createOrder(orderJson, --retrycount,submitretrycount,starttime);
//            }
//            if (selectedFlightInfo == null) {
//                sendCreateOrderInfo("error", "选择航班失败，请重新出票", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                logger.info("8L官网出票--" + order_id + "--提交航班选择结果失败");
//                return "进入选择价格失败";
//            }
//
//
//            logger.info("8L官网出票--" + order_id + "--选择航班信息结果:" + JSON.toJSONString(selectedFlightInfo));
//            sendOrderStatus(childrenUser, order_id, "填写乘客信息");
//            logger.info("8L官网出票--" + order_id + "--填写乘客信息");
//            //填写乘客信息前检查票是否取消
//            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                sendOrderStatus(childrenUser, order_id, "已取消出票");
//                logger.info(childrenUser+"已取消出票");
//                return "取消出票";
//            }
//            //超过最大创单时间
//            usetime=(System.currentTimeMillis()-starttime)/1000;
//            if(usetime>maxcreateOrdertime*60){
//                logger.info(order_id+childrenUser+"超过最大创单时间，停止创单");
//                sendCreateOrderInfo("error", "超过最大创单时间，停止创单", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                return "超过最大创单时间，停止创单";
//            }
//
//            sendOrderStatus(childrenUser, order_id, "准备创建订单");
//            int submitcoderetrycount=1;
//            //修改代理ip
//            ip_port=DailiyunService.getRandomIp(50);
//            proxyIp=ip_port.split(":")[0];
//            proxyPort=ip_port.split(":")[1];
//            String result = submitPassengerInfo(session, selectedFlightInfo, orderJson, cookie, soso, proxyIp, proxyPort,submitcoderetrycount);
//            //创单情况未知 可能阿布云返回错误信息 获取响应流中断 阿布云连接超时 不重试
//            if(StringUtil.isEmpty(result)||result.contains(abuyunerror)|| result.contains("订单创建失败")||result.contains("响应超时")){
//                sendCreateOrderInfo("error", "创单情况未知，请检查官网是否创单成功", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                logger.info(childrenUser+order_id+"创单情况未知，请检查官网是否创单成功");
//                return "创单情况未知，请检查官网是否创单成功";
//            }
//
//            if(result.contains("提交乘客信息验证码处理失败")){
//                sendCreateOrderInfo("error", "提交乘客信息验证码处理失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                logger.info("8L官网出票"+order_id+"--"+childrenUser+"提交乘客信息验证码处理失败");
//                return "提交乘客信息验证码处理失败";
//            }
//
//            //8l订单id
//            String orderno = getOrderNo(result);
//            // logger.info("8L官网出票--" + order_id + "--提交乘客信息返回第一次结果：" + result);
//            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                sendOrderStatus(childrenUser, order_id, "已取消出票");
//                logger.info(order_id+childrenUser+"已取消出票");
//                return "取消出票";
//            }
//            while ((result.contains("http://www.luckyair.net/error.jsp")
//                    || result.contains("http://www.luckyair.net/404.html")) && submitretrycount > 0) {
//                sendOrderStatus(childrenUser, order_id, "创单重试中。。。");
//                logger.info(order_id+"创单重试中。。。");
//                if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                    sendOrderStatus(childrenUser, order_id, "已取消出票");
//                    logger.info(order_id+childrenUser + "已取消出票");
//                    return "取消出票";
//                }
//                logger.info(order_id+"整体重试次数："+submitretrycount);
//                //超过最大创单时间
//                usetime=(System.currentTimeMillis()-starttime)/1000;
//                if(usetime>maxcreateOrdertime*60){
//                    logger.info(order_id+"超过最大创单时间，停止创单");
//                    sendCreateOrderInfo("error", "超过最大创单时间，停止创单", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                    return "超过最大创单时间，停止创单";
//                }
//                return createOrder(orderJson, retrycount,--submitretrycount,starttime);
//            }
//
//
//            //创单情况未知 可能阿布云返回错误信息 获取响应流中断 不重试
//            if(StringUtil.isEmpty(result)||result.contains(abuyunerror)|| result.contains("订单创建失败")){
//                sendCreateOrderInfo("error", "创单情况未知，请检查官网是否创单成功", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                logger.info(order_id+childrenUser+"创单情况未知，请检查官网是否创单成功");
//                return "创单情况未知，请检查官网是否创单成功";
//            }
//
//            //账号中有未支付订单 不重试
//            if(result.contains("账户中有未支付订单")||result.contains("机票订单人数超过限制")){
//                sendCreateOrderInfo("error", "账户中有未支付订单或者机票订单人数超过限制", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                logger.info(childrenUser+order_id+"账户中有未支付订单或者机票订单人数超过限制");
//                return "账户中有未支付订单或者机票订单人数超过限制";
//            }
//
//
//            // logger.info("8L官网出票--" + order_id + "--提交乘客信息返回最终结果：" + result);
//            if(StringUtils.isEmpty(orderno)){
//                sendCreateOrderInfo("error", "8L订单号为空，创单失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                logger.info(childrenUser+"8L订单号为空，创单失败");
//                return "8L订单号为空，创单失败";
//            }
//
//            logger.info("8L官网出票--feyee订单系统订单号："+order_id+"--查询订单(8L订单号):"+orderno+"的订单信息");
//            sendOrderStatus(childrenUser, order_id, "查询订单信息");
//            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                sendOrderStatus(childrenUser, order_id, "已取消出票");
//                logger.info(order_id+childrenUser+"已取消出票");
//                return "取消出票";
//            }
//            sendOrderStatus(childrenUser, order_id, "查询订单信息");
//            result=searchMyOrder(orderJson, session, cookie, soso, proxyIp, proxyPort, orderno);
//            if (StringUtils.isEmpty(result)||!result.contains("orderId")||result.contains(errormsg)) {
//                sendOrderStatus(childrenUser, order_id, "查询订单信息重试中。。。");
//                logger.info("8L官网出票--" + order_id + "--查询订单信息重试中。。。");
//                Element eleId = null;
//                try {
//                    if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                        sendOrderStatus(childrenUser, order_id, "已取消出票");
//                        logger.info(order_id+childrenUser+"已取消出票");
//                        return "取消出票";
//                    }
//                    int count = 0;
//                    while (eleId == null && count < 5) {
//                        if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                            sendOrderStatus(childrenUser, order_id, "已取消出票");
//                            logger.info(order_id+childrenUser+"已取消出票");
//                            return "取消出票";
//                        }
//                        //重新获取ip
//                        ip_port=DailiyunService.getRandomIp(50);
//                        proxyIp=ip_port.split(":")[0];
//                        proxyPort=ip_port.split(":")[1];
//                        result = searchMyOrder(orderJson, session, cookie, soso, proxyIp, proxyPort, orderno);
//                        if (StringUtils.isNotEmpty(result)&&!result.contains(errormsg)) {
//                            Document doc = Jsoup.parse(result);
//                            eleId = doc.getElementsByAttributeValue("name","orderId").get(0);
//                        } else {
//                            eleId = null;
//                        }
//                        count++;
//                    }
//                } catch (Exception e) {
//                    logger.info(order_id+"重新获取订单信息失败！");
//                    sendCreateOrderInfo("error", " 获取订单信息失败", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                    logger.error("error", e);
//                }
//            }
//            // logger.info("8L获取订单信息结果页面：\r\n"+result);
//            if(StringUtils.isEmpty(result)||!result.contains("orderId")){
//                logger.info(order_id+childrenUser+"多次获取到订单信息失败，请检查是否创单成功");
//                sendCreateOrderInfo("error", " 多次获取订单信息失败，请检查是否创单成功", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                return "获取订单信息失败";
//            }
//            if ("cancel".equals(cancel(cancelUrl, order_id, childrenUser))) {
//                sendOrderStatus(childrenUser, order_id, "已取消出票");
//                logger.info(order_id+childrenUser+"已取消出票");
//                return "取消出票";
//            }
//            logger.info("8L官网出票--" + order_id + "--支付订单");
//            sendOrderStatus(childrenUser, order_id, "准备支付");
//            //如果超过5分钟，则取消支付
//            /*usetime=(System.currentTimeMillis()-starttime)/1000;
//            if(usetime>maxcreateOrdertime*60){
//                logger.info("超过最大创单时间，取消支付");
//                sendCreateOrderInfo("error", "超过最大创单时间，取消支付", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                return "超过最大创单时间，取消支付";
//            }*/
//            String payResult = payOrder(result, orderJson, childrenUser, order_id, cookie, session, soso, proxyIp, proxyPort,orderno,billNo);
//            logger.info("8L官网出票--" + order_id + "--支付订单返回结果：" + payResult);
//            logger.info(order_id+childrenUser+"8L官网出票耗时"+(System.currentTimeMillis()-starttime)/1000+"s");
//            return payResult;
//        } catch (Exception e) {
//            logger.error("error", e);
//            sendCreateOrderInfo("error", "创单未知错误,请确认官网是否有订单，再重试", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//            logger.info("8L官网出票--" + order_id + "--创单未知错误");
//        }
//        return "ERROR:创单未知错误,请确认官网是否有订单，再重试";
//
//    }
//
//
//    /*public String startCreateOrder(){
//
//    }*/
//
//    private String getOrderNo(String orederRes) {
//        String orederNo = "";
//        try {
//            Element element = Jsoup.parse(orederRes);
//            Elements elements = element.select("input[name=orderNO_nginx]");
//            if (elements == null || elements.size() == 0) {
//                return orederNo;
//            }
//            return elements.get(0).attr("value");
//        }catch (Exception e){
//            logger.error("获取8L订单号异常",e);
//        }
//        return orederNo;
//    }
//
//
//    private String cancel(String url, String id, String childrenUser) {
//        CloseableHttpClient client = null;
//        CloseableHttpResponse response = null;
//        InputStream re = null;
//        HttpGet get = null;
//        HttpPost post = null;
//        String result = null;
//        try {
//            BasicCookieStore cookieStore = new BasicCookieStore();
//            Integer timeout = Integer.parseInt("60000");
//            RequestConfig requestConfig = RequestConfig.custom()
//                    .setSocketTimeout(timeout).setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
//                    .build();
//            client = HttpClients.custom().setDefaultCookieStore(cookieStore)
//                    .build();
//            List<BasicNameValuePair> nameValueParis = new ArrayList<BasicNameValuePair>();
//            nameValueParis.add(new BasicNameValuePair("orderId", id));
//            nameValueParis.add(new BasicNameValuePair("codetype", "order"));
//            nameValueParis.add(new BasicNameValuePair("childrenUser", childrenUser));
//            post = new HttpPost(url);
//            post.setEntity(new UrlEncodedFormEntity(nameValueParis, "utf-8"));
//            post.setConfig(requestConfig);
//            response = client.execute(post);
//            result = EntityUtils.toString(response.getEntity(), "utf-8");
//            JSONObject jo = new JSONObject(result);
//            result = jo.getString("msg");
//        } catch (Exception e) {
//            // TODO: handle exception
//        } finally {
//            try {
//                if (re != null) {
//                    re.close();
//                }
//                if (response != null) {
//                    response.close();
//                }
//                if (get != null) {
//                    get.releaseConnection();
//                }
//                if (post != null) {
//                    post.releaseConnection();
//                }
//                if (client != null) {
//                    client.close();
//                }
//            } catch (Exception e) {
//                logger.error("error", e);
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 支付订单
//     *
//     * @param backHtml
//     * @param orderJson
//     * @param childrenUser
//     * @param order_id
//     * @param cookie
//     * @param session
//     * @param soso
//     * @param proxyIp
//     * @param proxyPort
//     * @return
//     */
//    public String payOrder(String backHtml, String orderJson, String childrenUser, String order_id, String cookie, String session, String soso, String proxyIp, String proxyPort,String l8ordernum,String billNo) {
//        String orderId = "";
//        String oId = "";
//        String payType = "";
//        String payResult = "";
//        String payTypeZh  = "";
//        try {
//            JSONObject json = new JSONObject(orderJson);
//            payType = json.getString("payType");
//            JSONArray flights = json.getJSONArray("flights");
//            String isCoupon = json.getString("ifUsedCoupon");
//            Document doc = Jsoup.parse(backHtml);
//            JSONArray passengers = json.getJSONArray("passengers");
//            if (payType.equals("zfbjk")) {
//                payTypeZh = "支付宝";
//            }else{
//                payTypeZh = "易宝";
//            }
//            Element eleId = doc.getElementById("orderId");
//            if (eleId != null) {
//                orderId = eleId.val();
//                /*Element ele_id = doc.getElementById("orderid");
//                if (ele_id == null) {
//                    int sIndex = backHtml.indexOf("订单号：") + "订单号：".length();
//                    oId = backHtml.substring(backHtml.indexOf("订单号：") + "订单号：".length(), sIndex + 20);
//                }*/
//                float allPrice = 0;
//                try {
//                    Element eleTotal = doc.getElementById("totalAmount");
//                    if (eleTotal!=null) {
//                        String totalAmount = doc.getElementById("totalAmount").text().trim().replace("￥", "");
//                        String payChargeAmount = doc.getElementById("payChargeAmount").text().trim().replace("￥", "");
//                        allPrice = Float.parseFloat(totalAmount) + Float.parseFloat(payChargeAmount);
//                    } else {
//                        Elements eleTotalName = doc.getElementsByAttributeValue("name", "totalAmount");
//                        String totalAmount = "";
//                        if (eleTotalName.size() > 0) {
//                            totalAmount = eleTotalName.get(0).text().trim().replace("￥", "");
//                        }
//                        Elements eleticketPayCharge = doc.getElementsByAttributeValue("name", "ticketPayCharge");
//                        String payChargeAmount = "0";
//                        if (eleticketPayCharge.size() > 0) {
//                            //payChargeAmount = eleticketPayCharge.get(0).val().split(";")[0].split("_")[2];
//                            payChargeAmount = eleticketPayCharge.get(0).attr("value");
//                        }
//                        if(StringUtils.isEmpty(payChargeAmount)){
//                            payChargeAmount="0";
//                        }
//                        allPrice = Float.parseFloat(totalAmount) + Float.parseFloat(payChargeAmount);
//                    }
//
//                } catch (Exception e) {
//                    logger.error("获取订单总价异常！",e);
//                    sendCreateOrderInfo("error", "创建订单情况未知，请人工确认是否成功", "", childrenUser, "", order_id, "", "", null, "", "", "false",billNo);
//                    return "ERROR:创建订单失败";
//                }
//
//
//
//                String url = "http://www.luckyair.net/frontend/order/MyOrderDetail.action?id=" + orderId;
//
//                sendOrderStatus(childrenUser, order_id, "支付订单");
//                logger.info("准备支付订单 用户："+childrenUser+" 订单号："+order_id+"祥鹏订单id:"+orderId);
//                if (payType.equals("zfbjk")) {
//                    OfficialZfbPayService pay = new OfficialZfbPayService();
//                    payResult = pay.payAllOrder(url, cookie, orderJson);
//                } else if (payType.equals("ybzh")) {
//                    OfficialXykPayService pay = new OfficialXykPayService();
//                    long starttime=System.currentTimeMillis();
//                    payResult = pay.payAllOrder(url, cookie, orderJson, null);
//                    logger.info("支付耗时"+(System.currentTimeMillis()-starttime)/1000+"s");
//                }
//                //String payResult = "SUCCESS";
//                //发送创单成功信息
//                sendCreateOrderInfo("success", "创单成功", "", childrenUser, l8ordernum, order_id, "false", "", null, "", "", "false",billNo);
//                //推送创单情况 payStatus:"YB易宝"
//                sendCreateOrderInfo("success", "", allPrice + "", childrenUser, l8ordernum, order_id, "", "", null, "", "", isCoupon,billNo);
//                if ("SUCCESS".equals(payResult)) {
//                    logger.info("支付成功");
//                    sendCreateOrderInfo("success", "支付成功", allPrice + "", childrenUser, l8ordernum, order_id, "true", "", null, payTypeZh, "", isCoupon,billNo);
//                    StringBuffer tno = null;
//                    int count = 120;
//                    logger.info("开始获取票号信息");
//                    sendOrderStatus(childrenUser, order_id, "开始获取票号信息");
//                    long starttime=System.currentTimeMillis();
//                    while (tno == null && count > 0) {
//                        tno = getTicketNo(url, session, cookie, soso, proxyIp, proxyPort,passengers,flights);
//                        if (tno != null) {
//
//                            String ticketnos[] = tno.toString().split("@_@");
//                            if (ticketnos.length == 2) {
//                                // 判断获取的票号数是否与乘客数量相符合,不相符合重新
//                                String[] ticketCount = ticketnos[0].split("#_#");
//                                if (ticketCount.length == passengers.length()) {
//                                    if (payType.equals("zfbjk")) {
//                                        sendCreateOrderInfo("success", "", allPrice + "", childrenUser, l8ordernum, order_id, "", "true", ticketnos[0], "支付宝", ticketnos[1], isCoupon,billNo);
//                                    } else if (payType.equals("ybzh")) {
//                                        logger.info("用户："+childrenUser+"订单号："+order_id+"8L订单号："+l8ordernum+"回填信息："+tno);
//                                        sendCreateOrderInfo("success", "", allPrice + "", childrenUser, l8ordernum, order_id, "", "true", ticketnos[0], "易宝", ticketnos[1], isCoupon,billNo);
//                                    }
//                                } else {
//                                    tno = null;
//                                }
//                                // 只获取到票号情况，比较票号数量与乘客数量是否符合
//                            } else if (ticketnos.length == 1 && ticketnos[0].contains("#_#")) {
//                                String[] ticketCount = ticketnos[0].split("#_#");
//                                if (ticketCount.length == passengers.length()) {
//                                    logger.info("用户："+childrenUser+"订单号："+order_id+"8L订单号："+l8ordernum+"回填信息："+tno);
//                                    sendCreateOrderInfo("success", "", allPrice + "", childrenUser, l8ordernum, order_id, "", "true", ticketnos[0], payTypeZh, "", isCoupon,billNo);
//                                }
//                                // 只获取到支付流水号情况
//                            } else if (ticketnos.length == 1 && ticketnos[0].contains("@_@")) {
//                                logger.info("用户："+childrenUser+"订单号："+order_id+"8L订单号："+l8ordernum+"回填信息："+tno);
//                                sendCreateOrderInfo("success", "", allPrice + "", childrenUser, l8ordernum, order_id, "", "true", "", payTypeZh, ticketnos[0], isCoupon,billNo);
//                            }
//                            logger.info("回填耗时："+(System.currentTimeMillis()-starttime)/1000+"s");
//                            return "SUCCESS";
//                        }
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            logger.error("线程休眠打断",e);
//                        }
//                        count--;
//                    }
//                    logger.info("回填耗时："+(System.currentTimeMillis()-starttime)/1000+"s");
//                    if (tno == null && count <= 0) {
//                        logger.info("获取票号信息失败");
//                        sendCreateOrderInfo("error", "未找到票号信息", allPrice + "", childrenUser, l8ordernum, order_id, "true", "", "", payTypeZh, "", "false",billNo);
//                        return "SUCCESS:未找到票号信息";
//                    }
//
//                } else {
//                    logger.info("支付失败");
//
//                    if (payResult!=null&&payResult.contains("TXN_RESULT_ACCOUNT_BALANCE_NOT_ENOUGH")) {
//                        payResult = "支付宝余额不足";
//                    }
//                    sendCreateOrderInfo("error", payResult,  "", childrenUser, l8ordernum, order_id, "false", "", null, "", "", "false",billNo);
//
//                    return "ERROR:" + payResult;
//                }
//
//            } else {
//                logger.info(backHtml);
//            }
//        } catch (Exception e) {
//            logger.error("error", e);
//            if(StringUtils.isNotEmpty(payResult)&&payResult.equals("SUCCESS")){
//                sendCreateOrderInfo("error", "支付成功，解析回填失败", "", childrenUser, l8ordernum, order_id, "true", "", null, payTypeZh, "", "false",billNo);
//            }else{
//                logger.info("支付失败");
//                if (payResult!=null&&payResult.contains("TXN_RESULT_ACCOUNT_BALANCE_NOT_ENOUGH")) {
//                    payResult = "支付宝余额不足";
//                }
//                sendCreateOrderInfo("error", payResult,  "", childrenUser, l8ordernum, order_id, "false", "", null, "", "", "false",billNo);
//            }
//        }
//        return null;
//    }
//
//
//
//    public String imageCode1(String session, String proxyIp, String proxyPort, String cookie,String codeAccount) {
//        StringBuffer sb = new StringBuffer();
//        String content = "";
//        try {
//            sb.append("<feeye-official>");
//            sb.append("<official>" + Constant.L8.toString() + "</official>");
//            sb.append("<url>http://www.luckyair.net/hnatravel/imagecode?code=" + Math.random() + "</url>");
//            //sb.append("<codeUrl>true" + "@" + "feeyesb_123456789" + "</codeUrl>");
//            sb.append("<codeUrl>true" + "@" + codeAccount + "</codeUrl>");
//            sb.append("<codeParseType>2</codeParseType>");//设置验证码破解类型  1  2  yunsu破解
//            sb.append("<type>3</type> ");
//            sb.append("<codeType>5000</codeType>");
//            sb.append("<method>get</method>");
//            sb.append("<session>" + session + "</session> ");
//            sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
//            sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
//            sb.append("<encod>utf-8</encod>");
//            sb.append("<headers>");
//            sb.append("<head name='Host'>www.luckyair.net</head>");
//            sb.append("<head name='Referer'>http://www.luckyair.net/</head>");
//            sb.append("<head name='Cookie'>" + cookie + "</head>");
//            sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
//            sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 " +
//                    "Firefox/42.0</head>");
//            sb.append("</headers>");
//            sb.append("</feeye-official>");
//            content = OfficialMain.setRequestParams(sb.toString());
//
//        } catch (Exception e) {
//            logger.error("error", e);
//        }
//        return content;
//    }
//
//    public String imageCode(String session, String proxyIp, String proxyPort, String cookie) {
//        StringBuffer sb = new StringBuffer();
//        String content = "";
//        try {
//            sb.append("<feeye-official>");
//            sb.append("<official>" + Constant.L8.toString() + "</official>");
//            sb.append("<url>http://www.luckyair.net/hnatravel/imagecode?code=" + Math.random() + "</url>");
//            sb.append("<type>3</type> ");
//            sb.append("<codeUrl>true</codeUrl>");
//            sb.append("<codeParseType>1</codeParseType>");
//            sb.append("<method>get</method>");
//            sb.append("<session>" + session + "</session> ");
//            sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
//            sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
//            sb.append("<encod>utf-8</encod>");
//            sb.append("<headers>");
//            sb.append("<head name='Host'>www.luckyair.net</head>");
//            sb.append("<head name='Referer'>http://www.luckyair.net/</head>");
//            sb.append("<head name='Cookie'>" + cookie + "</head>");
//            sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
//            sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
//            sb.append("</headers>");
//            sb.append("</feeye-official>");
//            content = OfficialMain.setRequestParams(sb.toString());
//        } catch (Exception e) {
//            logger.error("error", e);
//        }
//        return content;
//    }
//
//    private String checkImage(String ticketcode, String proxyIp, String proxyPort, String session, String jessionId) {
//        StringBuffer sb = new StringBuffer();
//        String content = "";
//        try {
//            sb.append("<feeye-official>");
//            sb.append("<official>" + Constant.L8.toString() + "</official>");
//            sb.append("<url>http://www.luckyair.net/hnatravel/checkSignCode</url> ");
//            sb.append("<type>3</type> ");
//            sb.append("<method>POST</method>");
//            sb.append("<session>" + session + "</session> ");
//            sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
//            sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
//            sb.append("<encod>utf-8</encod>");
//            sb.append("<params>");
//            sb.append("<param name='tickcode'>" + ticketcode + "</param>");
//            sb.append("<param name='redirect'>aHR0cDovL3d3dy5sdWNreWFpci5uZXQv</param>");
//            sb.append("<param name='flag'>0</param>");
//            sb.append("<param name='submit'>提交</param>");
//            sb.append("</params>");
//            sb.append("<headers>");
//            sb.append("<head name='Host'>www.luckyair.net</head>");
//            sb.append("<head name='ozuid'>15595168130</head>");
//            sb.append("<head name='Referer'>http://www.luckyair.net/hnatravel/signcode.jsp?flag=0&redirect=aHR0cDovL3d3dy5sdWNreWFpci5uZXQv</head>");
//            sb.append("<head name='Cookie'>" + jessionId + "</head>");
//            sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
//            sb.append("</headers>");
//            sb.append("</feeye-official>");
//            content = OfficialMain.setRequestParams(sb.toString());
//            content=handleAbuyunError(sb.toString(),content);
//            if(content.contains(errormsg)){
//                return content;
//            }
//        } catch (Exception e) {
//            logger.error("error", e);
//        }
//        return content;
//    }
//
//
//    /**
//     * map 转cookie头字符串
//     *
//     * @param cookies
//     * @return
//     */
//    private String parseCookie(Map<String, String> cookies) {
//        StringBuilder cookiesStr = new StringBuilder();
//        for (Map.Entry<String, String> entry : cookies.entrySet()) {
//            cookiesStr.append(entry.getKey() + "=" + entry.getValue() + ";");
//        }
//        return cookiesStr.toString();
//    }
//
//    private Map<String, String> cookieStrToMap(String cookieStr) {
//        Map<String, String> allcookiesMap = new HashMap<String, String>();
//        for (String cok : cookieStr.split(";")) {
//            if(cok.contains("=")) {
//                allcookiesMap.put(cok.split("=")[0], cok.split("=")[1]);
//            }
//        }
//        return allcookiesMap;
//    }
//
//    /**
//     * @param userName     官网登录账号用户名
//     * @param passWord     登录密码
//     * @param session
//     * @param cookie
//     * @param soso
//     * @param proxyIp
//     * @param proxyPort
//     * @param otheraccount
//     * @return
//     */
//    public String login(String userName, String passWord, String session, String cookie, String soso, String proxyIp, String proxyPort, String otheraccount) {
//        //StringBuilder allcookies = new StringBuilder();
//        if(soso.split("=").length!=3){
//            return "false";
//        }
//        Map<String, String> allcookiesMap = new HashMap<String, String>();
//        // allcookies.append(soso);
//        for (String cok : soso.split(";")) {
//            allcookiesMap.put(cok.split("=")[0], cok.split("=")[1]);
//        }
//        StringBuilder sb = new StringBuilder();
//        sb.append("<feeye-official>");
//        sb.append("<official>L8</official>");
//        sb.append("<url>https://www.luckyair.net/jcaptcha?.tmp=" + System.currentTimeMillis() + "</url> ");
//        sb.append("<type>3</type> ");
//        sb.append("<method>get</method>");
//        sb.append("<session>" + session + "</session> ");
//        sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
//        sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
//        sb.append("<codeUrl>true" + "@" + otheraccount + "</codeUrl>");
//        sb.append("<codeParseType>2</codeParseType>");//设置验证码破解类型  1  2  yunsu破解
//        //添加验证码类型参数
//        sb.append("<codeType>3040</codeType>");
//
//        sb.append("<encod>utf-8</encod>");
//        sb.append("<headers>");
//        sb.append("<head name='Host'>www.luckyair.net</head>");
//        sb.append("<head name='Referer'>http://www.luckyair.net/</head>");
//        sb.append("<head name='Cookie'>" + parseCookie(allcookiesMap) + "</head>");
//        sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
//        sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
//        sb.append("</headers>");
//        sb.append("</feeye-official>");
//        String content = OfficialMain.setRequestParams(sb.toString());
//        //获取验证码重试
//        content=handleAbuyunError(sb.toString(),content);
//        if(content.contains(errormsg)){
//            return "false";
//        }
//        String[] rs = content.split("#@_@#");
//        //如果cookie不为空
//        if (org.apache.commons.lang3.StringUtils.isNotEmpty(rs[0])) {
//            //cookie去重
//            allcookiesMap.putAll(cookieStrToMap(rs[0]));
//        }
//        //转换为cookie头
//        String ticketcode = "";
//        if (rs.length == 2) {
//            ticketcode = rs[1];
//        }
//        logger.info(ticketcode);
//        //获取验证码的账号点数不足、充值提醒
//        if(ticketcode.contains("点数不足")){
//            return "点数不足，请充值";
//        }
//
//
//        sb.setLength(0);
//
//        sb.append("<feeye-official>");
//        sb.append("<official>" + Constant.L8.toString() + "</official>");
//        sb.append("<url>https://www.luckyair.net/login.action</url> ");
//        sb.append("<type>3</type> ");
//        sb.append("<method>post</method>");
//        sb.append("<session>" + session + "</session> ");
//        sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
//        sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
//        sb.append("<encod>gb2312</encod>");
//        sb.append("<params>");
//        logger.info(userName + "--" + passWord + "--" + ticketcode);
//        sb.append("<param name='userName'>" + userName + "</param>");
//        sb.append("<param name='password'>" + passWord + "</param>");
//        sb.append("<param name='j_captcha_response'>" + ticketcode + "</param>");
//        sb.append("</params>");
//        sb.append("<headers>");
//        sb.append("<head name='Host'>www.luckyair.net</head>");
//        sb.append("<head name='Referer'>https://www.luckyair.net/login.action</head>");
//        sb.append("<head name='Cookie'>" + parseCookie(allcookiesMap) + "</head>");
//        sb.append("</headers>");
//        sb.append("</feeye-official>");
//        content = OfficialMain.setRequestParams(sb.toString());
//
//        content=handleAbuyunError(sb.toString(),content);
//        if(content.contains(errormsg)){
//            return content;
//        }
//
//        if(content.contains("您输入的用户名或密码有误")){
//            return "官网账号用户名或密码不正确";
//        }
//        rs = content.split("#@_@#");
//
//        if(rs.length==2) {
//            // logger.info("登录响应结果：\r\n" + rs[1]);
//        }
//        if (org.apache.commons.lang3.StringUtils.isNotEmpty(rs[0])) {
//            allcookiesMap.putAll(cookieStrToMap(rs[0]));
//        }
//
//       /* if (rs.length == 2 && rs[1].contains("errmsg01Alone")) {
//            //验证码错误
//            String loginResult = login(userName, passWord, session, "", parseCookie(allcookiesMap), proxyIp, proxyPort, otheraccount);
//            if (loginResult.contains("webdomain")) {
//                return loginResult;
//            }
//        }*/
//
//        boolean isloginsuccess=false;
//        //重定向处理
//        if (rs.length == 3) {
//            //添加转发请求
//            String toLocationRes = toLocation(session, proxyIp, proxyPort, cookie + soso, rs[2]);
//            if(toLocationRes.contains(errormsg)){
//                return toLocationRes;
//            }
//            String[] tolocationResArry = toLocationRes.split("#@_@#");
//            //如果cookie不为空
//            if (org.apache.commons.lang3.StringUtils.isNotEmpty(tolocationResArry[0])) {
//                allcookiesMap.putAll(cookieStrToMap(tolocationResArry[0]));
//            }
//            if(tolocationResArry[1].contains("退出")){
//                isloginsuccess=true;
//            }
//            // logger.info("登录后台重定向结果页面：\r\n" + tolocationResArry[1]);
//        }
//
//        return parseCookie(allcookiesMap)+splitchar+isloginsuccess;
//    }
//
//    /**
//     * @param session
//     * @param proxyIp
//     * @param proxyPort
//     * @param cookie
//     * @param Location
//     * @return
//     */
//    public String toLocation(String session, String proxyIp, String proxyPort, String cookie, String Location) {
//        String content = "";
//        try {
//            StringBuilder sb = new StringBuilder();
//            sb.append("<feeye-official>");
//            sb.append("<official>" + Constant.L8.toString() + "</official>");
//            sb.append("<url>" + Location + "</url> ");
//            sb.append("<type>3</type> ");
//            sb.append("<method>get</method>");
//            sb.append("<session>" + session + "</session> ");
//            sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
//            sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
//            sb.append("<encod>utf-8</encod> ");
//            sb.append("<headers>");
//            sb.append("<head name='Host'>www.luckyair.net</head>");
//            sb.append("<head name='Cookie'>" + cookie + "</head>");
//            sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
//            sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
//            sb.append("</headers>");
//            sb.append("</feeye-official>");
//            content = OfficialMain.setRequestParams(sb.toString());
//            content=handleAbuyunError(sb.toString(),content);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//        return content;
//    }
//
//
//    private WebClient getClient() {
//        WebClient webClient = new WebClient(BrowserVersion.CHROME, "192.168.1.100", 809);
//        DefaultCredentialsProvider defaultCredentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
//        //设置代理用户名、密码
//        defaultCredentialsProvider.addCredentials("guoji", "guoji20160309");
//        //关闭css
//        webClient.getOptions().setCssEnabled(false);
//        webClient.getOptions().setJavaScriptEnabled(true);
//        webClient.getOptions().setRedirectEnabled(true);
//        webClient.getOptions().setUseInsecureSSL(true);
//        webClient.setCookieManager(new CookieManager());
//        webClient.getOptions().setUseInsecureSSL(false);
//        return webClient;
//    }
//
//
//    private String cookieToStr(Set<Cookie> cookies) {
//        StringBuilder cookieStr = new StringBuilder();
//        for (Cookie cookie : cookies) {
//            /*if (cookie.getName().equals("JSESSIONID") || cookie.getName().equals("route")) {
//                cookieStr.append(cookie.getName() + "=" + cookie.getValue() + ";");
//            }*/
//            cookieStr.append(cookie.getName() + "=" + cookie.getValue() + ";");
//        }
//        return cookieStr.toString();
//    }
//
//    //webclient
//    public String getIndex() {
//        String url = "http://www.luckyair.net/";
//        WebClient webClient = getClient();
//        Set<Cookie> cookieSet = null;
//        try {
//            HtmlPage page = webClient.getPage(url);
//            //logger.info(page.asXml());
//            CookieManager cookieManager = webClient.getCookieManager();
//            cookieSet = cookieManager.getCookies();
//        } catch (IOException e) {
//            logger.error("进入首页失败！", e);
//        } finally {
//            webClient.closeAllWindows();
//        }
//        return cookieToStr(cookieSet);
//    }
//
//    /**
//     * 进入首页
//     *
//     * @param session
//     * @return
//     */
//    public String getIndex(String session, String proxyIp, String proxyPort, String cookie) {
//        String jsessionId = "";
//
//        StringBuffer sb = new StringBuffer();
//
//        sb.append("<feeye-official>");
//        sb.append("<official>" + Constant.L8.toString() + "</official>");
//        sb.append("<url>http://www.luckyair.net/</url> ");
//        sb.append("<firstRequest>true</firstRequest> ");
//        sb.append("<type>3</type> ");// 5
//        sb.append("<method>get</method>");
//        sb.append("<session>" + session + "</session> ");
//        sb.append("<proxyIp>" + proxyIp + "</proxyIp>");
//        sb.append("<proxyPort>" + proxyPort + "</proxyPort>");
//        sb.append("<encod>utf-8</encod>");
//        sb.append("<headers>");
//        sb.append("<head name='Host'>www.luckyair.net</head>");
//        sb.append("<head name='Referer'>http://www.luckyair.net/</head>");
//        sb.append("<head name='Cookie'>" + cookie + "</head>");
//        sb.append("<head name='Upgrade-Insecure-Requests'>1</head>");
//        sb.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
//        sb.append("</headers>");
//        sb.append("</feeye-official>");
//        String content = OfficialMain.setRequestParams(sb.toString());
//        //添加阿布云报错处理
//        //content=handleAbuyunError(sb.toString(),content);
//        if(content==null){
//            return content;
//        }
//
//        // logger.info("8L首页结果页面：\r\n"+content);
//        String[] rs = content.split("#@_@#");
//        String Location = "";
//        if (content.contains("302 Found")) {
//            if (rs.length == 3) {
//                Location = rs[2];
//                content = toLocation(session, proxyIp, proxyPort, "", Location);
//            }
//            if(content==null||content.contains(errormsg)){
//                return content;
//            }
//
//            if (content != null) {
//                jsessionId = content.split("#@_@#")[0];
//            }
//        } else if (rs.length == 2) {
//            jsessionId = rs[0];
//            return jsessionId;
//        }
//        return jsessionId;
//    }
//
//
//    public String handleAbuyunError(String request, String resp) {
//        //出现阿布云服务器错误时的重试次数
//        int retrycount = 5;
//        int i = 0;
//        if (resp==null||resp.contains(abuyunerror)) {
//            while (i < retrycount) {
//                resp = OfficialMain.setRequestParams(request);
//                if (resp!=null&&!resp.contains(abuyunerror)) {
//                    return resp;
//                }
//                i++;
//            }
//        } else {
//            return resp;
//        }
//
//        return "abuyun error";
//    }
//    /**
//     * 验证码识别
//     *
//     * @param session
//     * @param proxyIp
//     * @param proxyPort
//     * @param url
//     * @param cookie
//     * @return
//     */
//    public String getLocationImg(String session, String proxyIp, String proxyPort, String url, String cookie) {
//        String jsessionId = "";
//        String sso_sign_eking = "";
//        String content = toLocation(session, proxyIp, proxyPort, cookie, url);
//        if (content != null) {
//            cookie = cookie + content.split("#@_@#")[0];
//        }
//
//        if (StringUtils.isNotEmpty(content) && content.contains("请输入验证码")) {
//
//            content = imageCode(session, proxyIp, proxyPort, cookie);
//            if (content.equals("#@_@##@_@#")) {
//                content = imageCode(session, proxyIp, proxyPort, cookie);
//            }
//            String rs2[] = content.split("#@_@#");
//            String ticketcode = "";
//            if (rs2.length == 2) {
//                ticketcode = rs2[1];
//            }
//            logger.info(ticketcode);
//
//
//            content = checkImage(ticketcode, proxyIp, proxyPort, session, cookie);
//
//
//            String rs[] = content.split("#@_@#");
//            if (rs.length == 2 || rs.length == 3) {
//                sso_sign_eking = rs[0];
//            }
//        }
//        return cookie + sso_sign_eking;
//    }
//
//    public Map<String,String> getFlightInfoList(String session, String orderJson, String cookie, String proxyIp, String proxyPort,int retrycount,String codeAccount) {
//    	String content = "";
//    	Map<String,String> resultContent = new HashMap<String,String>();;
//		try {
//			JSONObject json = new JSONObject(orderJson);
//			JSONArray flightsArr = json.getJSONArray("flights");
//			String dept = flightsArr.getJSONObject(0).getString("departure");
//			String arrival = flightsArr.getJSONObject(0).getString("arrival");
//			String dateString = flightsArr.getJSONObject(0).getString("departureDate");
//			String returnDate = "";
//			String tripType = "ONEWAY";
//			StringBuffer buffer = new StringBuffer();
//
//			buffer.append("<feeye-official>");
//			buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//			buffer.append("<url>http://www.luckyair.net/flight/searchflight2016.action</url> ");
//			buffer.append("<type>3</type> ");
//			buffer.append("<session>" + session + "</session>");
//			buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//			buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//			buffer.append("<method>post</method>");
//			buffer.append("<max>20</max> ");
//			buffer.append("<encod>utf-8</encod> ");
//			buffer.append("<params>");
//
//			if (flightsArr != null && flightsArr.length() > 1) {
//				buffer.append("<param name='tripType'>ROUNDTRIP</param>");
//				returnDate = flightsArr.getJSONObject(1).getString("departureDate");
//				tripType = "RETURN";
//			} else {
//				buffer.append("<param name='tripType'>ONEWAY</param>");
//			}
//			buffer.append("<param name='orgCity_interNAtional'></param>");
//			buffer.append("<param name='dstCity_interNAtional'></param>");
//			buffer.append("<param name='passengerCount.adultNum'>1</param>");
//			buffer.append("<param name='passengerCount.childNum'>0</param>");
//			buffer.append("<param name='passengerCount.disabledSoldierNum'>0</param>");
//			buffer.append("<param name='passengerCount.disabledPoliceNum'>0</param>");
//			buffer.append("<param name='orgCity'>" + dept + "</param>");
//			buffer.append("<param name='dstCity'>" + arrival + "</param>");
//			buffer.append("<param name='flightDate'>" + dateString + "</param>");
//			buffer.append("<param name='returnDate'>" + returnDate + "</param>");
//			buffer.append("<param name='passengerCount.infantNum'>0</param>");
//
//			buffer.append("</params>");
//			buffer.append("<headers>");
//			buffer.append("<head name='Referer'>http://www.luckyair.net/</head >");
//			buffer.append("<head name='Cookie'>" + cookie + "</head>");
//			buffer.append("</headers>");
//			buffer.append("</feeye-official>");
//
//			content = OfficialMain.setRequestParams(buffer.toString());
//			if(StringUtils.isEmpty(content)){
//				return null;
//			}
//			content = "";
//			// logger.info("查询第一步结果："+content);
//			for(int i=0;i<flightsArr.length();i++){
//				JSONObject flights = flightsArr.getJSONObject(i);
//				String departure = flights.getString("departure");
//				String arri = flights.getString("arrival");
//				String departureDate = flights.getString("departureDate");
//				String flightNo = flights.getString("flightNo");
//				String fType = flights.getString("fType");
//				content = getFlightText(session, departure, arri , departureDate, cookie, proxyIp, proxyPort,tripType,fType);
//				// logger.info("查询第二步结果："+content);
//				if(StringUtils.isEmpty(content)){
//					return null;
//				}
//				if (content.contains(errormsg) || content.contains("http://www.luckyair.net/error.jsp")
//						|| content.contains("http://www.luckyair.net/404.html")
//						|| content.contains("http://www.luckyair.net/hnatravel/signcode.jsp")) {
//					resultContent.put("error", content);
//					return resultContent;
//				}
//				// 处理验证码
//				String[] arrs = content.split(splitchar);
//				// 添加验证码处理
//				/*
//				 * if(arrs.length==3&&arrs[2].contains(
//				 * "http://www.luckyair.net/hnatravel/signcode.jsp")) {
//				 * if(retrycount>=0) {
//				 * logger.info("8L官网出票--"+"查询航班信息处理验证码重试中。。"+retrycount); cookie =
//				 * handsecuritycode(content, session, proxyIp, proxyPort, cookie,
//				 * codeAccount); if(cookie.equals("响应超时")){ return "响应超时"; } content
//				 * = getFlightInfoList(session, dept, arrival, dateString, cookie,
//				 * proxyIp, proxyPort, --retrycount,codeAccount); return content;
//				 * }else{ logger.info("8L官网出票--"+"查询航班信息处理验证码重试重试次数已用完"+retrycount);
//				 * return "提交乘客信息验证码处理失败"; } }
//				 */
//
//				if (content != null) {
//					String rs1[] = content.split("#@_@#");
//					if (rs1.length == 2) {
//						content = rs1[1];
//					}
//					if (rs1.length == 3) {
//						// logger.info(rs1[1]);
//					}
//				}
//				// logger.info(content);
//				if (StringUtils.isEmpty(content)) {
//					resultContent.put("error","未查询到结果");
//					logger.info("未查询到结果。。。");
//					return resultContent;
//				}
//
//				if ("-1".equals(content)) {
//					resultContent.put("error", "请求失败，需重新拨号连接");
//					logger.info("请求失败，需重新拨号连接。。。");
//					return resultContent;
//				}
//				resultContent.put(flightNo,content);
//			}
//		} catch (Exception e) {
//			logger.error("error",e);
//			resultContent.put("error", "查询异常");
//		}
//        return resultContent;
//    }
//
//
//
//
//
////    public String getFlightInfoList(String session, String orderJson, String cookie,
////                                    String proxyIp, String proxyPort,String otheraccount) {
////
////        String[] arrs = null;
////        String content="";
////        //添加第一步
////
////        content = getFlightText(session, orderJson, cookie, proxyIp, proxyPort);
////        logger.info("获取航班结果:"+content);
////        arrs = content.split(splitchar);
////        //添加验证码处理
////        if (arrs.length == 3 && arrs[2].contains("http://www.luckyair.net/hnatravel/signcode.jsp")) {
////            cookie = handsecuritycode(content, session, proxyIp, proxyPort, cookie, otheraccount);
////            if(cookie.contains("响应超时")){
////                return cookie;
////            }
////            content = getFlightText(session, orderJson, cookie, proxyIp, proxyPort);
////        }
////
////        if (content.contains(errormsg)) {
////            return content;
////        }
////
////        if (content.contains("http://www.luckyair.net/404.html") || content.contains("http://www.luckyair" +
////                ".net/hnatravel/signcode.jsp")) {
////            return content;
////        }
////
////        if (content != null) {
////            String rs1[] = content.split("#@_@#");
////            if (rs1.length == 2) {
////                content = rs1[1];
////            }
////            if (rs1.length == 3) {
////                //logger.info(rs1[1]);
////            }
////        }
////        //logger.info(content);
////        if (StringUtils.isEmpty(content)) {
////            logger.info("未查询到结果。。。");
////        }
////
////        if ("-1".equals(content)) {
////            logger.info("请求失败，需重新拨号连接。。。");
////        }
////
////        return cookie + "#@_@#" + content;
////    }
//
//    /**
//     * webclient 获取查询航班数据
//     *
//     * @return
//     */
//    public String searchFlighterbefore(String cookies, String depart, String arrival, String depDate,String proxyip,String port) {
//        //设置代理ip、端口
//        WebClient webClient = new WebClient(BrowserVersion.CHROME, proxyip, Integer.parseInt(port));
//        DefaultCredentialsProvider defaultCredentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
//        //设置代理用户名、密码
//        //defaultCredentialsProvider.addCredentials("guoji", "guoji20160309");
//        defaultCredentialsProvider.addCredentials("feeyeapp", "feeyeapp789");
//        //关闭css
//        String content="";
//        URL searchURL = null;
//        try {
//            searchURL = new URL("http://www.luckyair.net/flight/searchflight2016.action");
//        } catch (MalformedURLException e) {
//            logger.error("url转换异常", e);
//        }
//        WebRequest request = new WebRequest(searchURL);
//        //设置请求头
//        request.setAdditionalHeader("Cookie", cookies);
//        request.setAdditionalHeader("Host", "www.luckyair.net");
//        request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
//        request.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        request.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//        request.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
//        request.setAdditionalHeader("Referer", "http://www.luckyair.net/");
//        request.setAdditionalHeader("Upgrade-Insecure-Requests", "1");
//        request.setAdditionalHeader("Connection", "keep-alive");
//
//        //设置请求参数
//        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
//        requestParams.add(new NameValuePair("tripType", "ONEWAY"));
//        requestParams.add(new NameValuePair("orgCity", depart));
//        requestParams.add(new NameValuePair("orgCity_interNAtional", ""));
//        requestParams.add(new NameValuePair("dstCity", arrival));
//        requestParams.add(new NameValuePair("dstCity_interNAtional", ""));
//        requestParams.add(new NameValuePair("passengerCount.adultNum", "1"));
//        requestParams.add(new NameValuePair("flightDate", depDate));
//        requestParams.add(new NameValuePair("returnDate", ""));
//        requestParams.add(new NameValuePair("passengerCount.childNum", "0"));
//        requestParams.add(new NameValuePair("passengerCount.disabledSoldierNum", "0"));
//        requestParams.add(new NameValuePair("passengerCount.disabledPoliceNum", "0"));
//        requestParams.add(new NameValuePair("passengerCount.infantNum", "0"));
//
//        request.setRequestParameters(requestParams);
//
//
//        webClient.getOptions().setActiveXNative(false);
//        webClient.getOptions().setCssEnabled(false);
//        webClient.getOptions().setJavaScriptEnabled(true);
//        webClient.getOptions().setRedirectEnabled(true);
//        webClient.getOptions().setUseInsecureSSL(true);
//        webClient.getOptions().setThrowExceptionOnScriptError(false);
//        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//        webClient.waitForBackgroundJavaScript(10 * 1000);//等待js执行完
//
//        webClient.setCookieManager(new CookieManager());
//        webClient.getCookieManager().setCookiesEnabled(true);
//        try {
//            HtmlPage page = webClient.getPage(request);
//            content=page.asXml();
//            // logger.info(page.asXml());
//        } catch (IOException e) {
//            logger.error("获取祥鹏登录页面失败！", e);
//        } finally {
//            webClient.closeAllWindows();
//        }
//        return content;
//    }
//
//
//
//    public String getFlightbefore2(String session, String dept, String arrival, String dateString, String cookie,
//                                  String proxyIp, String proxyPort,String tripType, String fType) {
//        StringBuffer buffer = new StringBuffer();
//
//        buffer.append("<feeye-official>");
//        buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//        buffer.append("<url>http://www.luckyair.net/flightresult/flightresult2016.action</url> ");
//        buffer.append("<type>3</type> ");
//        buffer.append("<session>" + session + "</session>");
//        buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//        buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//        buffer.append("<method>post</method>");
//        buffer.append("<max>20</max> ");
//        buffer.append("<encod>utf-8</encod> ");
//        buffer.append("<params>");
//        buffer.append("<param name='orgCity'>" + dept + "</param>");
//        buffer.append("<param name='dstCity'>" + arrival + "</param>");
//        buffer.append("<param name='flightDate'>" + dateString + "</param>");
//        buffer.append("<param name='index'>1</param>");
//        if(StringUtils.isNotEmpty(fType)&&"back".equals(fType)){
//        	buffer.append("<param name='flightseq'>2</param>");
//        }else{
//        	buffer.append("<param name='flightseq'>1</param>");
//        }
//        //添加参数  改版
//        buffer.append("<param name='tripType'>"+tripType+"</param>");
//
//        /*if (StringUtils.isEmpty(L8CreateOrderService.desc)) {
//            L8CreateOrderService.desc = FingerPrintUtil.getDesc(0);
//        }*/
//        /*buffer.append("<param name='desc'>" + "coBPtm4BZy5Ly7E1arnlj7CXTKP53y8ZEZEaNLSd6wsY9k71j9VGETY/4EEyleWX" +
//                "</param>");*/
//        /*buffer.append("<param name='desc'>" + JSExeUtil.exeJs("/L8getCipherValue.js")+ "</param>");*/
//        String desc = FingerPrintUtil.getDesc();
//        // String bbb=JSExeUtil.exeJs("/L8getCipherValue.js");
//        try {
//            desc = URLDecoder.decode(desc,"utf-8");
//            //bbb=URLDecoder.decode(bbb,"utf-8");
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//
//        //logger.info("desc=="+desc+" asdasd==="+bbb);
//        buffer.append("<param name='desc'>" + desc+ "</param>");
//
//        buffer.append("</params>");
//        buffer.append("<headers>");
//        buffer.append("<head name='Referer'>http://www.luckyair.net/flight/searchflight2016.action</head >");
//        buffer.append("<head name='Cookie'>" + cookie + "ozuid=1559516810;" + "</head>");
//        buffer.append("<head name='Host'>www.luckyair.net</head>");
//        buffer.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 " +
//                "Firefox/61.0</head>");
//        buffer.append("<head name='X-Requested-With'>XMLHttpRequest</head>");
//        buffer.append("<head name='Accept'>*/*</head>");
//        buffer.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;" +
//                "q=0.2</head>");
//        buffer.append("<head name='Connection'>keep-alive</head>");
//        buffer.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//        buffer.append("<head name='Content-Type'>application/x-www-form-urlencoded; charset=UTF-8</head>");
//        buffer.append("<head name='Origin'>http://www.luckyair.net</head>");
//        buffer.append("</headers>");
//        buffer.append("</feeye-official>");
//        String content = OfficialMain.setRequestParams(buffer.toString());
////        content = handleAbuyunError(buffer.toString(), content);
//        return content;
//    }
//
//    public String getFlightbefore(String session, String dept, String arrival, String dateString, String cookie,
//                                  String proxyIp, String proxyPort) {
//        String url="http://www.luckyair.net/flightresult/flightresult2016.action";
//        String result = "";
//        CloseableHttpClient closeableHttpClient = null;
//        try {
//            int timeout=1000*2*60;
//            HttpHost  proxy = new HttpHost(proxyIp, Integer.parseInt(proxyPort), "http");
//           // CredentialsProvider credsProvider = new BasicCredentialsProvider();
//            //credsProvider.setCredentials(new AuthScope(proxyIp, Integer.parseInt(proxyPort)), new UsernamePasswordCredentials("feeyeapp", "feeye789"));
//
//            /*RequestConfig requestConfig=RequestConfig.custom().setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).setConnectTimeout(timeout).build();
//            closeableHttpClient=HttpClients.custom().setProxy(proxy).setDefaultCredentialsProvider(credsProvider).build();*/
//            RequestConfig requestConfig=RequestConfig.custom().setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).setConnectTimeout(timeout).build();
//            closeableHttpClient=HttpClients.createDefault();
//            HttpPost httpPost=new HttpPost(url);
//            httpPost.setConfig(requestConfig);
//            BasicScheme proxyAuth = new BasicScheme();
//            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
//            BasicAuthCache authCache = new BasicAuthCache();
//            authCache.put(proxy, proxyAuth);
//
//            CredentialsProvider credsProvider = new BasicCredentialsProvider();
//            credsProvider.setCredentials(
//                    new AuthScope(proxy),
//                    new UsernamePasswordCredentials("feeyeapp", "feeye789"));
//            HttpClientContext context = HttpClientContext.create();
//            context.setAuthCache(authCache);
//            context.setCredentialsProvider(credsProvider);
//
//
//
//
//
//
//            List<org.apache.http.NameValuePair> nameValuePairs=new ArrayList<>();
//            nameValuePairs.add(new BasicNameValuePair("orgCity",dept));
//            nameValuePairs.add(new BasicNameValuePair("dstCity",arrival));
//            nameValuePairs.add(new BasicNameValuePair("flightDate",dateString));
//            nameValuePairs.add(new BasicNameValuePair("index","1"));
//            nameValuePairs.add(new BasicNameValuePair("flightseq","1"));
//            nameValuePairs.add(new BasicNameValuePair("tripType","ONEWAY"));
//            String desc = FingerPrintUtil.getDesc();
//            try {
//                desc = URLDecoder.decode(desc,"utf-8");
//            } catch (UnsupportedEncodingException e1) {
//                e1.printStackTrace();
//            }
//            nameValuePairs.add(new BasicNameValuePair("desc",desc));
//            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8"));
//            //增加认证头
//           // String credentials="feeyeapp:feeye789";
//          //  httpPost.setHeader("Authorzation","Basic "+ Base64.encodeBase64String(credentials.getBytes("utf-8")));
//           // logger.info("认证头==="+"Basic "+ Base64.encodeBase64String(credentials.getBytes("utf-8")));
//            httpPost.setHeader("Referer","http://www.luckyair.net/flight/searchflight2016.action");
//            httpPost.setHeader("Cookie",cookie);
//            httpPost.setHeader("Host","www.luckyair.net");
//            httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0");
//            HttpResponse httpResponse=closeableHttpClient.execute(httpPost,context);
//            result=EntityUtils.toString(httpResponse.getEntity(),"utf-8");
//
//        } catch (Exception e) {
//            logger.error("查询航班结果出错",e);
//        } finally {
//            try {
//                closeableHttpClient.close();
//            } catch (IOException e) {
//                logger.error("关闭异常",e);
//            }
//        }
//        return result;
//
//    }
//
//
//    /**
//     * @return
//     */
//    public String getFlightbefore1(String session, String dept, String arrival, String dateString, String cookie, String proxyIp, String proxyPort) {
//        StringBuffer buffer = new StringBuffer();
//
//        buffer.append("<feeye-official>");
//        buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//        buffer.append("<url>http://www.luckyair.net/flightresult/flightresult2016.action</url> ");
//        buffer.append("<type>3</type> ");
//        buffer.append("<session>" + session + "</session>");
//        buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//        buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//        buffer.append("<method>post</method>");
//        buffer.append("<max>20</max> ");
//        buffer.append("<encod>utf-8</encod> ");
//        buffer.append("<params>");
//        buffer.append("<param name='orgCity'>" + dept + "</param>");
//        buffer.append("<param name='dstCity'>" + arrival + "</param>");
//        buffer.append("<param name='flightDate'>" + dateString + "</param>");
//        buffer.append("<param name='index'>1</param>");
//        buffer.append("<param name='flightseq'>1</param>");
//        /*if (StringUtils.isEmpty(L8CreateOrderService.desc)) {
//            L8CreateOrderService.desc = FingerPrintUtil.getDesc(0);
//        }*/
//        buffer.append("<param name='desc'>" + "coBPtm4BZy5Ly7E1arnlj7CXTKP53y8ZEZEaNLSd6wsY9k71j9VGETY/4EEyleWX" + "</param>");
//        buffer.append("</params>");
//        buffer.append("<headers>");
//        buffer.append("<head name='Referer'>http://www.luckyair.net/flight/searchflight2016.action</head >");
//        buffer.append("<head name='Cookie'>" + cookie + "ozuid=1559516810;" + "</head>");
//        buffer.append("<head name='Host'>www.luckyair.net</head>");
//        buffer.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0</head>");
//        buffer.append("<head name='X-Requested-With'>XMLHttpRequest</head>");
//        buffer.append("<head name='Accept'>*/*</head>");
//        buffer.append("<head name='Accept-Language'>zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2</head>");
//        buffer.append("<head name='Connection'>keep-alive</head>");
//        buffer.append("<head name='Accept-Encoding'>gzip, deflate</head>");
//        buffer.append("<head name='Content-Type'>application/x-www-form-urlencoded; charset=UTF-8</head>");
//        buffer.append("<head name='Origin'>http://www.luckyair.net</head>");
//        buffer.append("<head name='Referer'>http://www.luckyair.net/</head>");
//        buffer.append("</headers>");
//        buffer.append("</feeye-official>");
//        String content = OfficialMain.setRequestParams(buffer.toString());
//        content=handleAbuyunError(buffer.toString(),content);
//        return content;
//    }
//
//    public String getFlightText(String session, String dept,String arrival , String dateString, String cookie, String proxyIp, String proxyPort,String tripType,String fType) {
//        String FlightinfoBefor=getFlightbefore2(session, dept ,arrival, dateString , cookie, proxyIp, proxyPort,tripType,fType);
////        if(StringUtils.isNotEmpty(FlightinfoBefor)&&FlightinfoBefor.contains(errormsg)){
////            return FlightinfoBefor;
////        }
//        return FlightinfoBefor;
//    }
//
//
//    public HtmlPage confirm() {
//        String url = "";
//        return null;
//    }
//
//    /**
//     * @param backFlight
//     * @return webclient
//     */
//    public HtmlPage selectFlightNoWebC(String group, String groupCode, String session, String cookie, String soso, String proxyIp, String proxyPort, String luggagecode, String backFlight) throws ExecutionException, InterruptedException {
//        //设置代理ip、端口
//        //使用阿布云代理
//        //WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24, "http-dyn.abuyun.com", 9020);
//        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24, proxyIp, Integer.parseInt(proxyPort));
//        String newGroup = groupCode.replaceAll(" ", "+");
//        String backFlightNew = "";
//        String tripType = "ONEWAY";
//        if(StringUtils.isNotEmpty(backFlight)){
//        	backFlightNew = backFlight.replaceAll(" ", "+");
//        	tripType = "ROUNDTRIP";
//        }
//        DefaultCredentialsProvider defaultCredentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
//        //设置代理用户名、密码
//        //defaultCredentialsProvider.addCredentials("HL7F5JF125K85K8D", "FC393F432489B2E5");
//        defaultCredentialsProvider.addCredentials("feeyeapp", "feeye789");
//
//        //关闭css
//        URL searchURL = null;
//        try {
//            searchURL = new URL("http://www.luckyair.net/flight/preorder.action");
//        } catch (MalformedURLException e) {
//            logger.error("url转换异常", e);
//        }
//        final WebRequest request = new WebRequest(searchURL);
//        //设置请求头
//       /* String cookietemp="JSESSIONID=94A8E7BA975F09FF949D74A36AE9AFA2.l1; route=1239b01c22d09714783a880dcfe24ced; OZ_SI_2638=sTime=1530599146&sIndex=50; OZ_1U_2638=vid=vb3b16eb7acbc8.0&ctime=1530670350&ltime=1530670264; OZ_1Y_2638=erefer=-&eurl=http%3A//www.luckyair.net/&etime=1530599146&ctime=1530670350&ltime=1530670264&compid=2638; notice-read-ETweb=true; pgv_pvi=7097380864; pgv_si=s4717509632; WT_FPC=id=210cef4205211795a1a1530600748431:lv=1530669526490:ss=1530669293725; sso_sign_eking=e65718a4-ae2d-46be-b45b-e3a6a9be828f; ozuid=15595168130; webdomain=584V8S3Z6a9Z39Ep6O5Y09Ab660i3Of5/zzGIF8dQEJZsbjk5qej/Q7QZTVQU1SVf70DQDV3EacrSYp+IOD9QYTsSFL8gIXN87YXJdTHERuhnQhnQP1svq4ta1cR5d8Ylc01Yn0PfLHwtQloiqY8o8aPN5cCfVNk8OHGkhwEAO8Iilxh7Rd/sP0qpcrFUg6sibyD1bRmeyGd1Nb2A2EAB683B3886400; OZ_RU_2638=15595168130";
//        request.setAdditionalHeader("Cookie", cookietemp);*/
//        request.setAdditionalHeader("Cookie", cookie);
//        request.setAdditionalHeader("Host", "www.luckyair.net");
//        request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
//        request.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        request.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//        request.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
//        request.setAdditionalHeader("Referer", "http://www.luckyair.net/flight/searchflight2016.action");
//        request.setAdditionalHeader("Upgrade-Insecure-Requests", "1");
//        request.setAdditionalHeader("Connection", "keep-alive");
//        request.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
//
//        //设置请求参数
//        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
//        requestParams.add(new NameValuePair("isQuickLogin", "notQuickLogin"));
//        requestParams.add(new NameValuePair("infant", "0"));
//        requestParams.add(new NameValuePair("sessionIsNull", "true"));
//        requestParams.add(new NameValuePair("child", "0"));
//        requestParams.add(new NameValuePair("adult", "1"));
//        requestParams.add(new NameValuePair("selectedFlight", newGroup));
//        requestParams.add(new NameValuePair("backFlight", backFlightNew));
//        requestParams.add(new NameValuePair("code", ""));
//        requestParams.add(new NameValuePair("backProductCode", luggagecode));
//        requestParams.add(new NameValuePair("goProductCode", ""));
//        requestParams.add(new NameValuePair("tripType", tripType));
//
//        request.setRequestParameters(requestParams);
//
//
//        // webClient.getOptions().setActiveXNative(false);
//        webClient.getOptions().setCssEnabled(false);
//        webClient.getOptions().setJavaScriptEnabled(false);
//        webClient.getOptions().setRedirectEnabled(true);
//        webClient.getOptions().setUseInsecureSSL(true);
//        webClient.getOptions().setThrowExceptionOnScriptError(false);
//        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//        webClient.waitForBackgroundJavaScript(10 * 1000);//等待js执行完
//        webClient.getOptions().setTimeout(20*1000);
//
//        webClient.setCookieManager(new CookieManager());
//        webClient.getCookieManager().setCookiesEnabled(true);
//        HtmlPage page = null;
//        try {
//             ExecutorService executorService= Executors.newFixedThreadPool(1);
//            //添加超时
//            Callable<HtmlPage> task=new Callable<HtmlPage>() {
//                @Override
//                public HtmlPage call() throws Exception {
//                    return webClient.getPage(request);
//                }
//            };
//            Future<HtmlPage> result= executorService.submit(task);
//            //最多等待30秒
//            page=result.get(30,TimeUnit.SECONDS);
//            logger.debug(page.asXml());
//        } catch (Exception e) {
//            logger.error("获取祥鹏登录页面失败！", e);
//        } finally {
//            webClient.closeAllWindows();
//        }
//
//        return page;
//    }
//
//    /**
//     * @param group
//     * @param groupCode
//     * @param session
//     * @param cookie
//     * @param soso
//     * @param proxyIp
//     * @param proxyPort
//     * @param luggagecode
//     * @param backFlight
//     * @return
//     */
//    public FlightInfo selectFlightNo(String group, String groupCode, String session, String cookie, String soso, String proxyIp, String proxyPort, String luggagecode, String backFlight) {
//
//        try {
//            HtmlPage page = selectFlightNoWebC(group, groupCode, session, cookie, soso, proxyIp, proxyPort, luggagecode, backFlight);
//            if (page == null) {
//                return null;
//            }
//            String content = page.asXml();
//             //logger.info("选择航班结果页面\r\n"+content);
//            if(content.contains(abuyunerror)){
//                logger.info("阿布云服务器错误");
//                return null;
//            }
//            FlightInfo flight = new FlightInfo();
//            if(!content.contains("struts.token")){
//                return null;
//            }
//            List<FlightInfo> flightInfosSelected = CrawlData.doCrawlJson(group,backFlight);
//            for (FlightInfo flightInfo : flightInfosSelected) {
//                flight = flightInfo;
//                flight.setArrivalCN(new GetCity().arrivalCN(content));
//                flight.setDeparutreCN(new GetCity().deparutreCN(content));
//                try {
//                    String token = new GetCity().getXMLValueByName(content, "struts.token");
//                    flight.setToken(token);
//                } catch (Exception e) {
//                    logger.error("选择航班结果页面数据解析异常",e);
//                    return null;
//                }
//                //判断是否需要航意险
//                String hyx="订单包含一份30元航意险";
//                if (content.contains(hyx)){
//                    flight.setHasInsurance(true);
//                }
//                return flight;
//            }
//        } catch (Exception e) {
//            // TODO: handle exception\
//            logger.error("error", e);
//        }
//        return null;
//    }
//
//
////    public FlightInfo selectFlightNo1(String group, String groupCode, String session, String cookie, String soso, String proxyIp, String proxyPort, String luggagecode) {
////
////        try {
////            HtmlPage page = selectFlightNoWebC(group, groupCode, session, cookie, soso, proxyIp, proxyPort, luggagecode);
////            //添加重试
////            FlightInfo flight = new FlightInfo();
////            int retrycount=3;
////           while((page==null||!page.asXml().contains("struts.token"))&&retrycount>=0){
////                retrycount--;
////                logger.info("选择航班重试中");
////               String ip_port= DailiyunService.getRandomIp(50);
////                proxyIp = ip_port.split(":")[0];   //代理IP
////                proxyPort =ip_port.split(":")[1];  //端口
////               page = selectFlightNoWebC(group, groupCode, session, cookie, soso, proxyIp, proxyPort, luggagecode);
////            }
////
////            if(page==null||!page.asXml().contains("struts.token")){
////               return null;
////            }
////
////            String content = page.asXml();
////
////            List<FlightInfo> flightInfosSelected = CrawlData.doCrawlJson(group);
////            for (FlightInfo flightInfo : flightInfosSelected) {
////                flight = flightInfo;
////                flight.setArrivalCN(new GetCity().arrivalCN(content));
////                flight.setDeparutreCN(new GetCity().deparutreCN(content));
////                try {
////                    String token = new GetCity().getXMLValueByName(content, "struts.token");
////                    flight.setToken(token);
////                } catch (Exception e) {
////                    logger.error("选择航班结果页面数据解析异常",e);
////                    return null;
////                }
////                //判断是否需要航意险
////                String hyx="订单包含一份30元航意险";
////                if (content.contains(hyx)){
////                    flight.setHasInsurance(true);
////                }
////                return flight;
////            }
////        } catch (Exception e) {
////            // TODO: handle exception\
////            logger.error("error", e);
////        }
////        return null;
////    }
//
//
//    public static void getLowestPrice(List<CabinInfo> cabinfo) {
//        Collections.sort(cabinfo, new Comparator<CabinInfo>() {
//            public int compare(CabinInfo c1, CabinInfo c2) {
//                Float price1 = Float.parseFloat(com.feeye.util.StringUtil.isEmpty(c1.getPrice()) ? "0" : c1.getPrice());
//                Float price2 = Float.parseFloat(com.feeye.util.StringUtil.isEmpty(c2.getPrice()) ? "0" : c2.getPrice());
//                return price1.compareTo(price2);
//            }
//        });
//        for (int i = cabinfo.size() - 1; i >= 0; i--) {
//            if (i > 0) {
//                cabinfo.remove(i);
//            }
//        }
//    }
//
//    public String getPriceType(String code, String cabin) {
//        if ("BJHH".equals(code)) {
//            return "BJHH"; // 保价护航
//        }
//        return cabin_priceType_map.get(cabin);
//    }
//
//
//    /**
//     * 验证码处理
//     * @param content
//     * @param session
//     * @param proxyip
//     * @param port
//     * @param cookie
//     * @return
//     */
//    public String handsecuritycode(String content, String session, String proxyip, String port, String cookie,String codeAccount) {
//        String[] arrs = content.split(splitchar);
//        String newcookie = cookie;
//        String response = "";
//        if (arrs.length == 3) {
//            response = toLocation(session, proxyip, port, cookie, arrs[2]);
//            if(StringUtils.isEmpty(response)){
//                return "响应超时";
//            }
//            //获取jessionid
//            Map<String, String> cookiemap = cookieStrToMap(cookie);
//            newcookie = parseCookie(cookiemap);
//            cookiemap.put(response.split(splitchar)[0].replaceAll(";","").split("=")[0], response.split(splitchar)[0].replaceAll(";","").split("=")[1]);
//            int maxretry=4;
//            while((StringUtils.isNotEmpty(response)) && (response.contains("请输入验证码"))&&maxretry>=0) {
//                maxretry--;
//                response = imageCode1(session, proxyip, port, parseCookie(cookiemap),codeAccount);
//                if(StringUtils.isEmpty(response)){
//                    continue;
//                }
//                logger.info("8L检查验证码响应结果1"+response);
//                String[] rs2 = response.split("#@_@#");
//                String ticketcode = "";
//                if (rs2.length == 2) {
//                    ticketcode = rs2[1];
//                }
//                response = checkImage(ticketcode, proxyip, port, session, parseCookie(cookiemap));
//                if(StringUtils.isEmpty(response)){
//                    continue;
//                }
//                logger.info("8L检查验证码响应结果2"+response);
//
//            }
//            //如果验证未通过 则继续重试
//            logger.info("8L检查验证码响应结果3"+response);
//            arrs=response.split(splitchar);
//            if(StringUtils.isNotEmpty(arrs[0])){
//                cookiemap.put(arrs[0].replaceAll(";","").split("=")[0],arrs[0].replaceAll(";","").split("=")[1]);
//                response = toLocation(session, proxyip, port, parseCookie(cookiemap), arrs[2]);
//                if(StringUtils.isEmpty(response)){
//                    return "响应超时";
//                }
//                arrs=response.split(splitchar);
//                cookiemap.put(arrs[0].replaceAll(";","").split("=")[0],arrs[0].replaceAll(";","").split("=")[1]);
//                newcookie=parseCookie(cookiemap);
//            }
//
//
//
//        }
//        return newcookie;
//    }
//
//    /**
//     * @param session
//     * @param flightinfo
//     * @param orderJson
//     * @param cookie
//     * @param soso
//     * @param proxyIp
//     * @param proxyPort
//     * @return
//     */
//    public String submitPassengerInfo(String session, FlightInfo flightinfo, String orderJson, String cookie, String soso, String proxyIp, String proxyPort,int retrycount) {
//        String content = "";
//        try {
//            JSONObject json = new JSONObject(orderJson);
//            String couponNum = json.getString("code");
//            String isCoupon = json.getString("ifUsedCoupon");
//            String codeAccount=json.getString("otheraccount");
//            String childrenUser = json.getString("username");
//            String order_id = json.getString("id");
//
//            StringBuffer buffer = new StringBuffer();
//
//            //String desc = FingerPrintUtil.getDesc(1);
//            String desc = FingerPrintUtil.getDesc();
//            // String bbb=JSExeUtil.exeJs("/L8getCipherValue.js");
//            try {
//                desc = URLDecoder.decode(desc,"utf-8");
//                //bbb=URLDecoder.decode(bbb,"utf-8");
//            } catch (UnsupportedEncodingException e1) {
//                e1.printStackTrace();
//            }
//
//            String code = "";
//            if (isCoupon.equals("true")) {
///*
//                String result = null;
//                int count = 0;
//                //sendOrderStatus(childrenUser, order_id, "添加官网优惠券");
//                while ((result == null || (result.contains("ERROR") && result.contains("验证码错误"))) && count < 3) {
//                    result = addCouponNo(orderJson, session, proxyIp, proxyPort, cookie, "");
//                    count++;
//                }
//                if (result.contains("ERROR")) {
//                    sendCreateOrderInfo("error", result, "", childrenUser, "", order_id, "", "", null, "", "", "false");
//                    return result;
//                } else if (result.contains("SUCCESS")) {
//                    code = result.split(":")[1];
//                }*/
//            }
//
//            buffer.setLength(0);
//
//            //	提交订单信息
//            buffer.append("<feeye-official>");
//            buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//            buffer.append("<url>http://www.luckyair.net/flight/submitorder.action</url> ");
//            buffer.append("<type>3</type> ");//
//            buffer.append("<session>" + session + "</session>");
//            buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//            buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//            buffer.append("<method>post</method>");
//            buffer.append("<max>20</max> ");
//            buffer.append("<encod>utf-8</encod> ");
//            buffer.append("<params>");
//            logger.info("提交乘客加密参数："+desc);
//            buffer.append("<param name='desc'>" + desc + "</param>");
//            //添加参数
//
//            //添加参数
//            buffer.append("<param name='bjhhInsurance'>" + "false" + "</param>");
//            buffer.append("<param name='invoiceRemark'>N</param>");
//            buffer.append("<param name='struts.token.name'>struts.token</param>");
//            buffer.append("<param name='struts.token'>" + flightinfo.getToken() + "</param>");
//            //添加参数
//            buffer.append("<param name='productCode'>"+flightinfo.getCabins().get(0).getGroupCode().split(";")[5]+"</param>");
//            //	如果有使用优惠券
//            if (isCoupon.equals("true")) {
//                buffer.append("<param name='couponNo'>" + couponNum + "</param>");
//                buffer.append("<param name='coupon_couponNo'>" + couponNum + "</param>");
//            } else {
//                buffer.append("<param name='coupon_couponNo'></param>");
//            }
//
//            if (isCoupon.equals("true")) {
//                buffer.append("<param name='parValue'>50</param>");
//                buffer.append("<param name='quotaValue'>100</param>");
//            }
//
//            JSONArray passengers = json.getJSONArray("passengers");
//
//            for (int i = 0; i < passengers.length(); i++) {
//                JSONObject jObject = passengers.getJSONObject(i);
//                if ("儿童".equals(jObject.getString("passengerType"))) {
//                    buffer.append("<param name='passengerList_name'>" + jObject.getString("passengerName") + "CHD</param>");
//                } else {
//                    buffer.append("<param name='passengerList_name'>" + jObject.getString("passengerName") + "</param>");
//                }
//                //buffer.append("<param name='passengerList_certificateType_input'>" + jObject.getString("passengercardType") + "</param>");
//                //buffer.append("<param name='passengerList_certificateType_input'>" + "身份证" + "</param>");
//                String cardtype= jObject.getString("passengerCardType");
//                if(cardtype.contains("护照")){
//                    cardtype="有效护照";
//                }
//                buffer.append("<param name='passengerList_certificateType_input'>" + cardtype + "</param>");
//                if ("儿童".equals(jObject.getString("passengerType"))) {
//                    buffer.append("<param name='passengerList_passengerType'>CHILD</param>");
//                } else {
//                    buffer.append("<param name='passengerList_passengerType'>ADULT</param>");
//                }
//                buffer.append("<param name='passengerList_certificateType'>NI</param>");
//                buffer.append("<param name='passengerList_certificateNo'>" + jObject.getString("idcard") + "</param>");
//                //提交参数改版
//                if(cardtype.equals("身份证")){
//                    buffer.append("<param name='passengerList_birthday'>" + getBirth(jObject.getString("idcard")) + "</param>");
//                    logger.info("出生日期："+getBirth(jObject.getString("idcard")));
//                }else{
//                    buffer.append("<param name='passengerList_birthday'></param>");
//                }
//                buffer.append("<param name='passengerList_mobilePhone'></param>");
//
//                if (isCoupon.equals("true")||flightinfo.isHasInsurance()) {
//                    buffer.append("<param name='passengerList_isInsure'>1</param>");
//                    buffer.append("<param name='passengerList_isInsure_input'>航意险:30元/份  保额260万元</param>");
//                } else {
//                    buffer.append("<param name='passengerList_isInsure'>0</param>");
//                    buffer.append("<param name='passengerList_isInsure_input'>航意险:0份(如需购买可自行选择)</param>");
//                    buffer.append("<param name='passengerList_insurance2'>0</param>");
//                    buffer.append("<param name='passengerList_insurance2_input'>延误险:0份(如需购买可自行选择)</param>");
//                }
//
//                buffer.append("<param name='passengerList_insurance1'>0</param>");
//                buffer.append("<param name='passengerList_insurance1_input'>退票险:0份(如需购买可自行选择)</param>");
//            }
//
//
//            buffer.append("<param name='j_captcha_response'>" + code + "</param>");
//            buffer.append("<param name='pointAmount_tmp'>0</param>");
//            buffer.append("<param name='pointAmount'>0</param>");
//            buffer.append("<param name='pointAmountFlag'>0</param>");
//            buffer.append("<param name='fullPointPay'></param>");
//            buffer.append("<param name='ams_balance_value'>0</param>");
//            buffer.append("<param name='contactName'>" + json.getString("linkMan") + "</param>"); // 用户姓名
//            buffer.append("<param name='contactTel'></param>");
//            //添加参数
//            buffer.append("<param name='countryCode1'>86</param>");
//            buffer.append("<param name='contactMobile'>" + json.getString("mobile") + "</param>"); // 用户手机号
//            //buffer.append("<param name='contactEmail'>" + json.getString("linkemail") + "</param>"); // 用户邮箱
//            buffer.append("<param name='contactEmail'></param>"); // 用户邮箱
//            buffer.append("<param name='ms'>no</param>");
//            buffer.append("<param name='ex'>no</param>");
//            //添加参数
//            buffer.append("<param name='countryCode2'></param>");
//            if (isCoupon.equals("true")) {
//                buffer.append("<param name='product_name'>保价护航</param>");
//            } else {
//                //buffer.append("<param name='product_name'>网站专享</param>");
//                buffer.append("<param name='product_name'>智选经济舱</param>");
//            }
//            buffer.append("<param name='segment_depCode_name'>" + flightinfo.getDeparutreCN() + "</param>"); // 出发地
//            buffer.append("<param name='segment_arrCode_name'>" + flightinfo.getArrivalCN() + "</param>");  // 目的地
//            buffer.append("<param name='segment_depTime_name'>" + flightinfo.getDeparutreDate() + " " + flightinfo.getDeparutreTime() + "</param>");  // 出发时间
//            buffer.append("<param name='segment_arrTime_name'>" + flightinfo.getArriveDate() + " " + flightinfo.getArriveTime() + "</param>"); // 到达时间
//            String backflight = "";
//            if(StringUtils.isNotEmpty(flightinfo.getCabins().get(0).getBackFlight())){
//            	backflight = flightinfo.getCabins().get(0).getBackFlight();
//            	/*
//            	 * 往返单新增的参数
//            	 */
//            	buffer.append("<param name='saveCom'>on</param>");
//            	buffer.append("<param name='product_name'>智选经济舱</param>");
//            	buffer.append("<param name='segment_depCode_name'>" + flightinfo.getArrivalCN() + "</param>"); // 出发地
//                buffer.append("<param name='segment_arrCode_name'>" + flightinfo.getDeparutreCN() + "</param>");  // 目的地
//                buffer.append("<param name='segment_depTime_name'>" + flightinfo.getDeparutreDate1() + " " + flightinfo.getDeparutreTime1() + "</param>");  // 出发时间
//                buffer.append("<param name='segment_arrTime_name'>" + flightinfo.getArriveDate1() + " " + flightinfo.getArriveTime1() + "</param>"); // 到达时间
//            }
//            buffer.append("<param name='backFlight'>" + backflight + "</param>");
//            // 已选机票
//            buffer.append("<param name='selectedFlight'>" + flightinfo.getCabins().get(0).getGroupCode() + "</param>");
//            //如果是有航意险 则需要值
//            buffer.append("<param name='insuranceFare'>0.00</param>");
//
//            /*buffer.append("<param name='totalFare'>" + ((Double.parseDouble(flightinfo.getCabins().get(0).getPrice()) + 51) + "").substring(0, ((Double.parseDouble(flightinfo.getCabins().get(0).getPrice()) + 51) + "").length() - 2) + "</param>");
//            buffer.append("<param name='totalFareOld'>" + ((Double.parseDouble(flightinfo.getCabins().get(0).getPrice()) + 51) + "").substring(0, ((Double.parseDouble(flightinfo.getCabins().get(0).getPrice()) + 51) + "").length() - 2) + "</param>");
//            buffer.append("<param name='ticketprice'>" + flightinfo.getCabins().get(0).getPrice() + "</param>");
//            buffer.append("<param name='sumnetfare'>" + (Double.parseDouble(flightinfo.getCabins().get(0).getPrice()) + 51) + "0" + "</param>");*/
//
//            //票面价+民航基金+燃油费
//            buffer.append("<param name='totalFare'>" + (flightinfo.getCabins().get(0).getPrice()+50+50)+ "</param>");
//            //票面价+民航基金+燃油费
//            buffer.append("<param name='totalFareOld'>" + (flightinfo.getCabins().get(0).getPrice()+50+50)+ "</param>");
//            //票面价
//            buffer.append("<param name='ticketprice'>" +(flightinfo.getCabins().get(0).getPrice()) + "</param>");
//            //票面价+民航基金
//            buffer.append("<param name='sumnetfare'>" + (flightinfo.getCabins().get(0).getPrice()+50) + "</param>");
//
//
//            if (isCoupon.equals("true")) {
//                buffer.append("<param name='parValuesum'>50</param>");
//                buffer.append("<param name='quotaValuesum'>100</param>");
//            } else {
//                buffer.append("<param name='parValuesum'></param>");
//                buffer.append("<param name='quotaValuesum'></param>");
//            }
//
//            buffer.append("</params>");
//            logger.info("提交乘客信息参数："+buffer.toString());
//            buffer.append("<headers>");
//            buffer.append("<head name='Referer'>http://www.luckyair.net/flight/preorder.action</head >");
//            buffer.append("<head name='Cookie'>" + cookie + "</head>");
//            buffer.append("<head name='Host'>www.luckyair.net</head>");
//            buffer.append("</headers>");
//            buffer.append("</feeye-official>");
//
//            content = OfficialMain.setRequestParams(buffer.toString());
//
//            if(StringUtils.isEmpty(content)){
//            	return null;
//            }
//
//            String[] arrs=content.split(splitchar);
//            //添加验证码处理
//            if(arrs.length==3&&arrs[2].contains("http://www.luckyair.net/hnatravel/signcode.jsp")) {
//                if(retrycount>=0) {
//                    /*logger.info("8L官网出票--"+order_id+"--"+childrenUser+"提交乘客信息处理验证码重试中。。"+retrycount);
//                    cookie = handsecuritycode(content, session, proxyIp, proxyPort, cookie, codeAccount);
//                    if(cookie.equals("响应超时")){
//                        return cookie;
//                    }*/
//                    String  ip_port=DailiyunService.getRandomIp(50);
//                    proxyIp=ip_port.split(":")[0];
//                    proxyPort=ip_port.split(":")[1];
//                    content = submitPassengerInfo(session, flightinfo, orderJson, cookie, soso, proxyIp, proxyPort, --retrycount);
//                    return content;
//                }else{
//                    logger.info("8L官网出票--"+order_id+"--"+childrenUser+"提交乘客信息处理验证码重试重试次数已用完"+retrycount);
//                    return  "提交乘客信息验证码处理失败";
//                }
//            }
////             logger.info("提交乘客信息响应结果："+content);
//
//            if (content != null) {
//                String rs1[] = content.split("#@_@#");
//                if (rs1.length == 2) {
//                    content = rs1[1];
//                }
//                if (rs1.length == 3) {
//                    // logger.info(rs1[2]);
//                }
//            }
//        } catch (Exception e) {
//            logger.error("error", e);
//        }
//        return content;
//    }
//
//
//    /**
//     * 从身份证号中获取出生日期  格式 yyyy-MM-dd
//     * @param idcard
//     * @return
//     */
//    private String getBirth(String idcard){
//        String birth="";
//        if(idcard.length()==15){
//            birth="19"+idcard.substring(6,8)+"-"+idcard.substring(8,10)+"-"+idcard.substring(10,12);
//        }else{
//            birth=idcard.substring(6,10)+"-"+idcard.substring(10,12)+"-"+idcard.substring(12,14);
//        }
//        return birth;
//    }
//    /**
//     * 添加优惠券
//     *
//     * @param orderJson
//     */
//    public String addCouponNo(String orderJson, String session, String proxyIp, String proxyPort, String cookie, String soso) {
//
//        // L&X 获取添加优惠券时的验证码
//        StringBuffer buffer = new StringBuffer();
//
//        try {
//
//            JSONObject json = new JSONObject(orderJson);
//            String couponNum = json.getString("code");
//            String isCoupon = json.getString("ifUsedCoupon");
//
//            if (isCoupon.equals("true")) {
//                buffer.append("<feeye-official>");
//                buffer.append("<official>L81</official>");
//                buffer.append("<url>https://www.luckyair.net/jcaptcha?.tmp=" + System.currentTimeMillis() + "</url> ");
//                buffer.append("<type>0</type> ");
//                buffer.append("<method>get</method>");
//                buffer.append("<session>" + session + "</session> ");
//                buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//                buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//                buffer.append("<codeUrl>true</codeUrl>");
//                buffer.append("<codeParseType>1</codeParseType>");
//                buffer.append("<encod>utf-8</encod>");
//                buffer.append("<headers>");
//                buffer.append("<head name='Host'>www.luckyair.net</head>");
//                buffer.append("<head name='Referer'>http://www.luckyair.net/</head>");
//                buffer.append("<head name='Cookie'>" + cookie + soso + "</head>");
//                buffer.append("<head name='Upgrade-Insecure-Requests'>1</head>");
//                buffer.append("<head name='User-Agent'>Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0</head>");
//                buffer.append("</headers>");
//                buffer.append("</feeye-official>");
//                String txtJcaptchaContent = OfficialMain.setRequestParams(buffer.toString());
//                String[] rs = txtJcaptchaContent.split("#@_@#");
//
//                String ticketcode = "";
//                if (rs.length == 2) {
//                    ticketcode = rs[1];
//                }
//
//                buffer.setLength(0);
//
//                logger.info(couponNum + "---" + ticketcode);
//
//                // L&X 填入优惠券信息
//                buffer.append("<feeye-official>");
//                buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//                buffer.append("<url>http://www.luckyair.net/frontend/couponNoDictionaries/couponValidate.action</url> ");
//                buffer.append("<type>3</type> ");
//                buffer.append("<session>" + session + "</session>");
//                buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//                buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//                buffer.append("<method>post</method>");
//                buffer.append("<max>20</max> ");
//                buffer.append("<encod>utf-8</encod> ");
//                buffer.append("<params>");
//                buffer.append("<param name='couponNo'>" + couponNum + "</param>");
//                buffer.append("<param name='txtJcaptcha'>" + ticketcode + "</param>");
//                buffer.append("</params>");
//                buffer.append("<headers>");
//                buffer.append("<head name='Referer'>http://www.luckyair.net/flight/preorder.action</head >");
//                buffer.append("<head name='Cookie'>" + cookie + soso + "</head>");
//                buffer.append("<head name='Host'>www.luckyair.net</head>");
//                buffer.append("</headers>");
//                buffer.append("</feeye-official>");
//                String content = OfficialMain.setRequestParams(buffer.toString());
//                rs = content.split("#@_@#");
//
//                if (rs.length == 2) {
//                    //{"message":"优惠券信息提取失败！","object":null,"result":false}
//                    if (!StringUtils.isEmpty(content)) {
//                        logger.info(content);
//                        JSONObject jsonObject = new JSONObject(rs[1]);
//                        String result = jsonObject.getString("result");
//                        if (result == "false") {
//                            String msg = jsonObject.getString("message");
//                            if (msg.contains("校验失败，该券只适用于")) {
//                                msg = msg.substring(0, 30) + "...";
//                            }
////							String childrenUser = json.getString("username");
////							String order_id = json.getString("id");
////							sendCreateOrderInfo("error", msg, "", childrenUser, "", order_id, "", "", null, "");
//
//                            return "ERROR:" + couponNum + msg;
//                        } else {
//                            return "SUCCESS:" + ticketcode;
//                        }
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            // TODO: handle exception
//            logger.error("error", e);
//        }
//        return "ERROR:添加验证码未知错误";
//    }
//
//    public static void main(String[] args) throws IOException {
//        long starttime=System.currentTimeMillis();
//        try {
//            Thread.sleep(2*1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println((System.currentTimeMillis()-starttime)/1000+"S");
//
//        String desc = FingerPrintUtil.getDesc();
//        // String bbb=JSExeUtil.exeJs("/L8getCipherValue.js");
//        try {
//            desc = URLDecoder.decode(desc,"utf-8");
//            //bbb=URLDecoder.decode(bbb,"utf-8");
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//        System.out.println(desc);
//        /*String str = "{\"code\":0,\"message\":\"SUCCESS1\",\"createTime\":1501164018894,\"result\":{\"id\":11699002,\"noPayAmount\":1309,\"orderNo\":\"yyw170727220018700\",\"status\":0}}";
//        JSONObject jobject;
//		try {
//			jobject = new JSONObject(str);
//
//		String message = jobject.getString("message");
//		if(!message.equals("SUCCESS")){
//			System.out.println("飞耶系统提醒：创单失败,接口调用失败");
//		}
//		JSONObject reslut = jobject.getJSONObject("result");
//		String id = reslut.getString("id");	//订单ID
//		String noPayAmount = reslut.getString("noPayAmount");	//待支付总价
//		String orderNo = reslut.getString("orderNo");	//订单号
//		String status = reslut.getString("status");
//		if("".equals(orderNo)||orderNo==null){
//			System.out.println("飞耶系统提醒：创单失败");
//		}
//		System.out.println(id+"@@"+orderNo+"@@");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}*/
//
//
//        //账号  15595168130_feeye123
//        //注释  测完查询结果打开
//        //String orderJson = "{\"departure\":\"SZX\",\"arrival\":\"KMG\",\"departureDate\":\"2018-07-23\",\"flightNo\":\"8L9980\",\"price\":\"1510.0\",\"cabin\":\"\",\"account\":\"15595168130_feeye123\",\"username\":\"\",\"id\":\"\",\"otheraccount\":\"\"}";
//        String orderJson = "{\r\n" +
//                "    \"id\": \"27886042\",\r\n" +
//                "    \"account\": \"15019417726_zcf080758\",\r\n" +
//                "    \"airline\": \"MF\",\r\n" +
//                "    \"orderNo\": \"1524794945160\",\r\n" +
//                "    \"username\": \"policytest\",\r\n" +
//                "    \"childrenMobile\": \"18617070230\",\r\n" +
//                "    \"payType\": \"ybzh\",\r\n" +
//                "    \"code\": \"\",\r\n" +
//                "    \"orderTime\": \"2018-06-13 15:35:16\",\r\n" +
//                "    \"codePassword\": \"\",\r\n" +
//                "    \"price\": \"1510.0\",\r\n" +
//                "    \"departure\": \"SZX\",\r\n" +
//                "    \"arrival\": \"KMG\",\r\n" +
//                "    \"departureDate\": \"2018-07-23\",\r\n" +
//                "    \"flightNo\": \"8L9980\",\r\n" +
//                "    \"cabin\": \"B\",\r\n" +
//                "    \"mobile\": \"13532989542\",\r\n" +
//                "    \"creditNo\": \"18713083283\",\r\n" +
//                "    \"expireMonth\": \"null\",\r\n" +
//                "    \"expireYear\": \"null\",\r\n" +
//                "    \"cvv\": \"15817476200\",\r\n" +
//                "    \"ownername\": \"null\",\r\n" +
//                "    \"idCardType\": \"null\",\r\n" +
//                "    \"idCardNo\": \"null\",\r\n" +
//                "    \"payerMobile\": \"12346579\",\r\n" +
//                "    \"account_no\": \"null\",\r\n" +
//                "    \"deduct_third_code\": \"null\",\r\n" +
//                "    \"linkMan\": \"罗轩\",\r\n" +
//                "    \"linkemail\":\"luoxuan@feeyemail.com\",\r\n" +
//                "    \"isOutticket\": \"true\",\r\n" +
//                "    \"ytype\": \"\",\r\n" +
//                "    \"passengers\": [\r\n" +
//                "        {\r\n" +
//                "            \"passengerName\": \"罗轩\",\r\n" +
//                "            \"idcard\": \"441423199501081014\",\r\n" +
//                "            \"passengerType\": \"成人\",\r\n" +
//                "            \"passengercardType\": \"身份证\",\r\n" +
//                "            \"birthday\": \"1995-01-08\",\r\n" +
//                "            \"passengerSex\": \"null\"\r\n" +
//                "        },\r\n" +
//               /* "        {\r\n" +
//                "            \"passengerName\": \"WANG/ALEE\",\r\n" +
//                "            \"idcard\": \"PA0189134\",\r\n" +
//                "            \"passengerType\": \"成人\",\r\n" +
//                "            \"passengercardType\": \"护照\",\r\n" +
//                "            \"birthday\": \"\",\r\n" +
//                "            \"passengerSex\": \"null\"\r\n" +
//                "        },\r\n" +*//*
//                /*"        {\r\n" +
//                "            \"passengerName\": \"陈若愚\",\r\n" +
//                "            \"idcard\": \"361002201112310625\",\r\n" +
//                "            \"passengerType\": \"儿童\",\r\n" +
//                "            \"passengercardType\": \"身份证\",\r\n" +
//                "            \"birthday\": \"2011-12-31\",\r\n" +
//                "            \"passengerSex\": \"null\"\r\n" +
//                "        }\r\n" +*/
//                "    ],\r\n" +
//                "    \"ifUsedCoupon\": false,\r\n" +
//                "    \"drawerType\": \"GW\",\r\n" +
//                "    \"qiangpiao\": \"\",\r\n" +
//                "    \"otheraccount\": \"feeyesb_123456789\"\r\n" +
//                "}";
//
//        L8CreateOrderService l8CreateOrderService = new L8CreateOrderService();
//        // l8CreateOrderService.createOrder(orderJson);
//    }
//
//
//    public String searchMyOrder(String orderJson, String session, String cookie, String soso, String proxyIp, String proxyPort, String orderno) {
//        StringBuffer buffer = new StringBuffer();
//        String content = "";
//        try {
//            //{"id":"27424","account":"18680683221_feeye123","airline":"8L","orderNo":"1493899426579","username":"policytest","childrenMobile":"18025330859","payType":"zfbjk","code":"","orderTime":"2017-05-05 20:06:37","codePassword":"","price":"410.0","departure":"SZX","arrival":"KMG","departureDate":"2017-05-23","flightNo":"8L9980","cabin":"E","mobile":"18680683221","creditNo":"null","expireMonth":"null","expireYear":"null","cvv":"null","ownername":"null","idCardType":"null","idCardNo":"null","payerMobile":"null","account_no":"20887111375369520156","deduct_third_code":"d63e83ba4c30e0ff546e9ddb1505f608","linkMan":"邹林梅","isOutticket":"false","ytype":"???","passengers":[{"passengerName":"田津瑞","idcard":"150802201203270917","passengerType":"成人","passengercardType":"身份证","birthday":"null"}],"ifUsedCoupon":false,"drawerType":"GW","qiangpiao":"","otheraccount":"fdsfasfa_d54126"}
//
//            buffer.append("<feeye-official>");
//            buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//            buffer.append("<url>http://www.luckyair.net/frontend/order/SearchMyOrder!doQuery.action</url> ");
//            buffer.append("<type>3</type> ");
//            buffer.append("<session>" + session + "</session>");
//            buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//            buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//            buffer.append("<method>post</method>");
//            buffer.append("<max>20</max> ");
////		    buffer.append("<token>keep</token>");
//            buffer.append("<encod>utf-8</encod> ");
//            buffer.append("<params>");
//            buffer.append("<param name='certificateNo'></param>");
//
//            //buffer.append("<param name='paxName'>" + jObject.getString("passengerName") + "</param>");
//            buffer.append("<param name='paxName'></param>");
//            buffer.append("<param name='pnr'></param>");
//            buffer.append("<param name='issCode'>859</param>");
//            buffer.append("<param name='ticketNo'></param>");
//            buffer.append("<param name='orderNo'>"+orderno+"</param>");
//            buffer.append("<param name='orderDate1'></param>");
//            buffer.append("<param name='orderDate2'></param>");
//            buffer.append("<param name='orgCity'></param>");
//            buffer.append("<param name='dstCity'></param>");
//            buffer.append("<param name='flightNo'></param>");
//            buffer.append("<param name='flightDate1'></param>");
//            buffer.append("<param name='flightDate2'></param>");
//            buffer.append("<param name='status'>WP</param>");
//            buffer.append("<param name='couponNo'></param>");
//            buffer.append("<param name='couponType'></param>");
//            buffer.append("<param name='disabledType'></param>");
//            buffer.append("<param name='orderMan'></param>");
//            buffer.append("<param name='officeNo'></param>");
//            buffer.append("<param name='ticketType'></param>");
//            buffer.append("<param name='ec_i'>ec</param>");
//            buffer.append("<param name='eti'></param>");
//
//            buffer.append("<param name='eti_p'></param>");
//            buffer.append("<param name='ec_efn'></param>");
//            buffer.append("<param name='ec_ev'></param>");
//            buffer.append("<param name='ec_crd'>10</param>");
//
//            buffer.append("<param name='ec_p'>1</param>");
//            buffer.append("<param name='ec_totalpages'>1</param>");
//            buffer.append("<param name='ec_totalrows'></param>");
//            buffer.append("<param name='ec_pg'>1</param>");
//
//            buffer.append("</params>");
//            buffer.append("<headers>");
//            buffer.append("<head name='Referer'>http://www.luckyair.net/frontend/order/SearchMyOrder.action</head >");
//            buffer.append("<head name='Cookie'>" + cookie+ "</head>");
//            buffer.append("<head name='Host'>www.luckyair.net</head>");
//            buffer.append("</headers>");
//            buffer.append("</feeye-official>");
//
//            content = OfficialMain.setRequestParams(buffer.toString());
//            //content=handleAbuyunError(buffer.toString(),content);
//            if(StringUtils.isEmpty(content)){
//                return content;
//            }
//            if (content != null) {
//                String rs1[] = content.split("#@_@#");
//                if (rs1.length == 2) {
//                    content = rs1[1];
//
//                    Element ele = Jsoup.parse(content);
//                    ele=ele.getElementById("ec_table_body");
//                    //配置属性值以指定字符串开头的元素
//                    Elements elements= ele.select("a[href^=javascript:OpenNewLen('/frontend/order/MyOrderDetail.action?id=]");
//                    if (elements!=null&&elements.size()>0) {
//                        String href=elements.get(0).attr("href");
//                        href=href.substring(href.indexOf("'")+1,href.lastIndexOf("'"));
//                        logger.info("L8订单herf"+href);
//                        buffer.setLength(0);
//                        buffer.append("<feeye-official>");
//                        buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//                        buffer.append("<url>http://www.luckyair.net" + href + "</url> ");
//                        buffer.append("<type>3</type> ");
//                        buffer.append("<session>" + session + "</session>");
//                        buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//                        buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//                        buffer.append("<method>post</method>");
//                        buffer.append("<max>20</max> ");
//                        buffer.append("<encod>utf-8</encod> ");
//                        buffer.append("<headers>");
//                        buffer.append("<head name='Referer'>http://www.luckyair.net/frontend/order/SearchMyOrder.action</head >");
//                        buffer.append("<head name='Cookie'>" + cookie+ "</head>");
//                        buffer.append("<head name='Host'>www.luckyair.net</head>");
//                        buffer.append("</headers>");
//                        buffer.append("</feeye-official>");
//
//                        content = OfficialMain.setRequestParams(buffer.toString());
//
//                        //content=handleAbuyunError(buffer.toString(),content);
//                        if(StringUtils.isEmpty(content)){
//                            return content;
//                        }
//
//                        if (content != null) {
//                            rs1 = content.split("#@_@#");
//                            if (rs1.length == 2) {
//                                content = rs1[1];
////
////        	        	    	String payResult = payOrder(content, orderJson, "policytest", "27424", cookie, session, soso, proxyIp, proxyPort);
////
//                                return content;
//                            }
//                        }
//                    } else {
//                        return null;
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            // TODO: handle exception
//            logger.error("error", e);
//        }
//        return content;
//    }
//
//    /**
//     * @param session
//     * @param cookie
//     * @param soso
//     * @param proxyIp
//     * @param proxyPort
//     * @return
//     */
//    public String buySeat(String session, String cookie, String soso, String proxyIp, String proxyPort) {
//
//        StringBuffer buffer = new StringBuffer();
//
//        buffer.append("<feeye-official>");
//        buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//        buffer.append("<url>http://www.luckyair.net/extraProduct/frontend/meal/buySeat.action</url> ");
//        buffer.append("<type>3</type> ");
//        buffer.append("<session>" + session + "</session>");
//        buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//        buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//        buffer.append("<method>post</method>");
//        buffer.append("<max>20</max> ");
////		buffer.append("<token>keep</token>");
//        buffer.append("<encod>utf-8</encod> ");
//        buffer.append("<params>");
//        buffer.append("<param name='couponFare'></param>");
//        buffer.append("<param name='couponNos'></param>");
//        buffer.append("<param name='judgeSub'>no</param>");
//        buffer.append("<param name='couponMealsGoAmount'></param>");
//        buffer.append("<param name='couponMealsbackAmount'></param>");
//        buffer.append("<param name='couponLuggageGOAmount'></param>");
//        buffer.append("<param name='couponLuggageBackAmount'></param>");
//        buffer.append("<param name='seg1Cabin'>Q</param>");
//        buffer.append("<param name='seg2Cabin'></param>");
//
//        buffer.append("<param name='passengerslist'></param>");
//        buffer.append("<param name='passengersListBack'></param>");
//        buffer.append("<param name='luggage_couponNo'></param>");
//        buffer.append("<param name='j_captcha_response'></param>");
//
//        buffer.append("</params>");
//        buffer.append("<headers>");
//        buffer.append("<head name='Referer'>http://www.luckyair.net/flight/submitorder.action</head >");
//        buffer.append("<head name='Cookie'>" + cookie + soso + "</head>");
//        buffer.append("<head name='Host'>www.luckyair.net</head>");
//        buffer.append("</headers>");
//        buffer.append("</feeye-official>");
//
//        String content = OfficialMain.setRequestParams(buffer.toString());
//        if (content != null) {
//            String rs1[] = content.split("#@_@#");
//            if (rs1.length == 2) {
//                content = rs1[1];
//            }
//            if (rs1.length == 3) {
//                // logger.info(rs1[2]);
//            }
//        }
//        return content;
//    }
//
//
//    /**
//     * @param flightinfo
//     * @param session
//     * @param cookie
//     * @param soso
//     * @param proxyIp
//     * @param proxyPort
//     * @return
//     */
//    public String submitorder(FlightInfo flightinfo, String session, String cookie, String soso, String proxyIp, String proxyPort) {
//
//        StringBuffer buffer = new StringBuffer();
//        buffer.append("<feeye-official>");
//        buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//        buffer.append("<url>http://www.luckyair.net/flight/submitorder2014.action</url> ");
//        buffer.append("<type>3</type> ");
//        buffer.append("<session>" + session + "</session>");
//        buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//        buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//        buffer.append("<method>post</method>");
////		buffer.append("<token>keep</token>");
//        buffer.append("<max>20</max> ");
//        buffer.append("<encod>utf-8</encod> ");
//        buffer.append("<params>");
//        buffer.append("<param name='flightNumber'>" + flightinfo.getFlightNo() + "</param>");
//        buffer.append("<param name='airplaneClass'>736</param>");
//        buffer.append("<param name='pSeat'></param>");
//        buffer.append("<param name='pSeats'></param>");
//        buffer.append("<param name='boardingPrice1'></param>");
//
//        buffer.append("</params>");
//        buffer.append("<headers>");
//        buffer.append("<head name='Referer'>http://www.luckyair.net/extraProduct/frontend/meal/buySeat.action</head >");
//        buffer.append("<head name='Cookie'>" + cookie + soso + "</head>");
//        buffer.append("<head name='Host'>www.luckyair.net</head>");
//        buffer.append("</headers>");
//        buffer.append("</feeye-official>");
//
//        String content = OfficialMain.setRequestParams(buffer.toString());
//        if (content != null) {
//            String rs1[] = content.split("#@_@#");
//            if (rs1.length == 2) {
//                content = rs1[1];
//
//
//            }
//            if (rs1.length == 3) {
//                logger.info(rs1[2]);
//            }
//        }
//        return content;
//    }
//
//
//    /**
//     * 获取票号信息
//     *
//     * @param url
//     * @param session
//     * @param cookie
//     * @param soso
//     * @param proxyIp
//     * @param proxyPort
//     * @param flights
//     * @return
//     */
//    public StringBuffer getTicketNo(String url, String session, String cookie, String soso, String proxyIp, String proxyPort,JSONArray passengers, JSONArray flights) {
//
//        StringBuffer buffer = new StringBuffer();
//        buffer.append("<feeye-official>");
//        buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//        buffer.append("<url>" + url + "</url> ");
//        buffer.append("<type>7</type> ");
//        buffer.append("<session>" + session + "</session>");
//        buffer.append("<proxyIp>" + proxyIp + "</proxyIp>");
//        buffer.append("<proxyPort>" + proxyPort + "</proxyPort>");
//        buffer.append("<method>get</method>");
////		buffer.append("<token>keep</token>");
//        buffer.append("<max>20</max> ");
//        buffer.append("<encod>utf-8</encod> ");
//        buffer.append("<headers>");
//        buffer.append("<head name='Referer'>http://www.luckyair.net/frontend/order/SearchMyOrder.action</head >");
//        buffer.append("<head name='Cookie'>" + cookie +  "</head>");
//        buffer.append("<head name='Host'>www.luckyair.net</head>");
//        buffer.append("</headers>");
//        buffer.append("</feeye-official>");
//
//        String content = OfficialMain.setRequestParams(buffer.toString());
//        logger.info("cookies===="+cookie);
//        if (content != null) {
//            // logger.info("获取订单详情结果页面\r\n"+content.split(splitchar)[1]);
//            String rs1[] = content.split("#@_@#");
//            if (rs1.length == 2) {
//
//                content = rs1[1];
//
//                Document paxSegIds = Jsoup.parse(content);
//
//                Elements eles = paxSegIds.getElementsByAttributeValue("name", "paxSegIds");
//
//                StringBuffer ss = new StringBuffer();
//
//                for (int i = 0; i < passengers.length(); i++) {
//                    String passInfo = paxSegIds.getElementById("A" + (i + 1)).val();
//                    String tInfo = eles.get(i).val();
//                    try {
//                    	if(flights.length()==2){
//                        	tInfo = eles.get(i*2).val();
//                        }
//					} catch (Exception e) {
//						logger.error("error",e);
//					}
//                    //tInfo = "96028969;65776906;ADULT;I;;2017-05-23 19:55:00.0;320.0;田津瑞;SZX;789456120;WEB;";
//                    try {
//                        JSONObject jsonObject = new JSONObject(passInfo);
//                        String idcard = jsonObject.getString("no");
//                        String name = jsonObject.getString("name");
//                        //替换/
//                        name=name.replace("/","");
//                        String id = jsonObject.getString("id");
//
//                        if (tInfo.contains(id)) {
//                            String tinfos[] = tInfo.split(";");
//                            if (!StringUtils.isEmpty(tinfos[9])) {
//                                ss.append(name + "##" + idcard + "##859-" + tinfos[9] + "#_#");
//                            } else {
//                                return null;
//                            }
//                        }
//                    } catch (Exception e) {
//                        logger.error("error", e);
//                        return null;
//                    }
//                }
//
//                try {
//                    // L&X 获得支付流水号
//                    Elements elesTable = paxSegIds.getElementsByClass("tb_style04_a").get(paxSegIds.getElementsByClass("tb_style04_a").size() - 1).getElementsByTag("tr");
//                    Elements elesTb = null;
//                    String payNo = "";
//                    for (int i = 0; i < elesTable.size(); i++) {
//                        if (i == 0) {
//                            elesTb = elesTable.get(i).getElementsByTag("th");
//                        } else {
//                            elesTb = elesTable.get(i).getElementsByTag("td");
//                        }
//                        if (elesTb.get(4).html().equals("支付成功") || elesTb.get(4).html().equals("支付完成")) {
//                            payNo = elesTb.get(1).html();
//                            break;
//                        }
//                    }
//                    if (!StringUtils.isEmpty(payNo)) {
//                        ss.append("@_@" + payNo);
//                    }
//                } catch (Exception e) {
//                    // TODO: handle exception
//                    logger.error("error", e);
//                    return null;
//                }
//
//                return ss;
//
//            }
//            if (rs1.length == 3) {
//                //  logger.info(rs1[2]);
//            }
//        }
//        return null;
//    }
//
//
//    public static String getJsonByMdfive(String addTimeFrom, String addTimeTo, String status, String start,
//                                         String limit, String apiKey, String sign, String timestamp, String validVendorIds)// \"sign\":\"zo6pFa7QeJ9wW2WRGs0N\",
//    {
//        String end = DateUtil.date2String(new Date(), "yyyy-MM-dd HH:mm:ss");
//        // String json =
//        // "{\"addTimeFrom\":\""+addTimeFrom+"\",\"addTimeTo\":\""+end+"\",\"apiKey\":\""+apiKey+"\",\"limit\":"+limit+",\"start\":"+start+",\"status\":"+status+",\"timestamp\":\""+end+"\",\"validVendorIds\":\""+validVendorIds+"\"}";
//        String json = "apiKey" + apiKey + "planInfo" + "[{\"addTimeFrom\":\"" + addTimeFrom + "\",\"addTimeTo\":\""
//                + end + "\",\"limit\":" + limit + ",\"start\":" + start + ",\"status\":" + status + "}]timestamp" + end
//                + "validVendorIds" + validVendorIds;
//
//        String signresult = sign + json + sign;
//        signresult = MD5Util.getMD5(signresult).toUpperCase();
//        json = "{\"apiKey\":\"" + apiKey + "\",\"planInfo\":[{\"addTimeFrom\":\"" + addTimeFrom + "\",\"addTimeTo\":\""
//                + end + "\",\"limit\":" + limit + ",\"start\":" + start + ",\"status\":" + status + "}],\"sign\":\""
//                + signresult + "\",\"timestamp\":\"" + end + "\",\"validVendorIds\":\"" + validVendorIds + "\"}";
//        // json =
//        // "{\"addTimeFrom\":\""+addTimeFrom+"\",\"addTimeTo\":\""+end+"\",\"status\":"+status+",\"start\":"+start+",\"limit\":"+limit+",\"apiKey\":\""+apiKey+"\",\"sign\":\""+signresult+"\",\"timestamp\":\""+end+"\",\"validVendorIds\":\""+validVendorIds+"\"}";
//        return json;
//    }
//
//    /**
//     * 推送创单情况
//     * String result = request.getParameter("result");  //是否创单成功
//     * String message = request.getParameter("message"); //失败消息
//     * String price = request.getParameter("price");  //采购总金额
//     * String childrenUser = request.getParameter("childrenUser");//子帐号
//     * String newOrderId = request.getParameter("newOrderId"); //创建订单成功后的官网订单号
//     * String orderId = request.getParameter("orderId");  //原订单主键ID
//     * String isPassuccess = request.getParameter("isPassuccess");  //是否支付成功
//     * String isPassenge = request.getParameter("isPassenge");      //是否票号回填
//     * String[] passengeMessage = request.getParameterValues("passengeMessage");  // 获取票号回填到系统   格式为:姓名##生份证##票号
//     * String payTransactionid = request.getParameter("payTransactionid");       //获取票号回填的交易号 SC时代表联系电话
//     * String payStatus = request.getParameter("payStatus");       //获取支付方式
//     * String isSuccess = request.getParameter("isSuccess");       //是否完结
//     * String isautoB2C = request.getParameter("isautoB2C");       //是否自动出票
//     * String ifUsedCoupon = request.getParameter("ifUsedCoupon");       //是否使用红包
//     */
//    public static String sendCreateOrderInfo(String result, String message, String price, String childrenUser, String newOrderId
//            , String orderId, String isPassuccess, String isPassenge, String passengeMessage, String payStatus, String payTransactionid
//            , String ifUsedCoupon,String billNo) {
//        try {
//            String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrl");
//
//
//            StringBuffer buffer = new StringBuffer();
//            buffer.append("<feeye-official>");
//            buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//            buffer.append("<url>" + orderUrl + "</url> ");
//            buffer.append("<type>0</type> ");
//            buffer.append("<method>post</method>");
//            buffer.append("<max>20</max> ");
//            buffer.append("<encod>utf-8</encod> ");
//            buffer.append("<params>");
//            buffer.append("<param name='result'>" + result + "</param>");
//            buffer.append("<param name='message'>" + message + "</param>");
//            buffer.append("<param name='dicountMoney'>" + 0 + "</param>");
//            buffer.append("<param name='price'>" + price + "</param>");
//            buffer.append("<param name='childrenUser'>" + childrenUser + "</param>");
//            buffer.append("<param name='newOrderId'>" + newOrderId + "</param>");
//            buffer.append("<param name='orderId'>" + orderId + "</param>");
//            buffer.append("<param name='isPassuccess'>" + isPassuccess + "</param>");
//            buffer.append("<param name='isPassenge'>" + isPassenge + "</param>");
//            buffer.append("<param name='passengeMessageOther'>" + passengeMessage + "</param>");
//            buffer.append("<param name='payStatus'>" + payStatus + "</param>");
//            buffer.append("<param name='payTransactionid'>" + payTransactionid + "</param>");
//            buffer.append("<param name='ifUsedCoupon'>" + ifUsedCoupon + "</param>");
//            buffer.append("<param name='billNo'>" + billNo + "</param>");
//
//            buffer.append("</params>");
//            buffer.append("</feeye-official>");
//
//            String content = OfficialMain.setRequestParams(buffer.toString());
//            if (content != null) {
//                String rs1[] = content.split("#@_@#");
//                if (rs1.length == 2) {
//                    content = rs1[1];
//                    return content;
//                }
//                if (rs1.length == 3) {
//                    logger.info(rs1[2]);
//                    return rs1[2];
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error("error", e);
//        }
//        return null;
//    }
//
//
//    /**
//     * 推送创单情况
//     * String childrenUser = request.getParameter("childrenUser");//子帐号
//     * String orderId = request.getParameter("orderId");  //原订单主键ID
//     * String payStatus = request.getParameter("payStatus");       //获取支付方式
//     */
//    public static String sendOrderStatus(String childrenUser, String orderId, String status) {
//        try {
//            String orderUrl = PropertiesUtils.getPropertiesValue("config", "orderUrlStatus");
//
//            StringBuffer buffer = new StringBuffer();
//            buffer.append("<feeye-official>");
//            buffer.append("<official>" + Constant.L8.toString() + "</official> ");
//            buffer.append("<url>" + orderUrl + "</url> ");
//            buffer.append("<type>0</type> ");
//            buffer.append("<method>post</method>");
//            buffer.append("<max>20</max> ");
//            buffer.append("<encod>utf-8</encod> ");
//            buffer.append("<params>");
//            buffer.append("<param name='childrenUser'>" + childrenUser + "</param>");
//            buffer.append("<param name='orderId'>" + orderId + "</param>");
//            buffer.append("<param name='orderStatus'>" + status + "</param>");
//
//            buffer.append("</params>");
//            buffer.append("</feeye-official>");
//
//            String content = OfficialMain.setRequestParams(buffer.toString());
//            if (content != null) {
//                String rs1[] = content.split("#@_@#");
//                if (rs1.length == 2) {
//                    content = rs1[1];
//                    return content;
//                }
//                if (rs1.length == 3) {
//                    logger.info(rs1[2]);
//                    return rs1[2];
//                }
//            }
//        } catch (Exception e) {
//            logger.error("推送\"" + status + "\"情况异常");
//        }
//        return null;
//    }
//
//
//}
