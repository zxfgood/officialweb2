package com.feeye.service.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.Orderinfo;
import com.feeye.bean.Passergeninfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author xs
 * @description
 * @date 2019/5/8
 */
public class CZNEWService {
    private static final Logger logger = Logger.getLogger(CZNEWService.class);


    //查询航班信息
    private String queryFlights(CloseableHttpClient httpclient, RequestConfig config, Orderinfo orderinfo) {
        String back = "";
        try {
            HttpPost httpPost = new HttpPost("/portal/flight/direct/query");
            //请求主机
            HttpHost httpHost = new HttpHost("b2c.csair.com", 443, "https");
            //解析查询参数
            JSONObject queryObject = new JSONObject();
            queryObject.put("depCity", orderinfo.getFlights().get(0).getDeparture());
            queryObject.put("arrCity", orderinfo.getFlights().get(0).getArrival());
            queryObject.put("flightDate", orderinfo.getFlights().get(0).getDepartureDate().replaceAll("-", ""));
            //设置成人 儿童 婴儿数量
            int adultNum = 0;
            int childNum = 0;
            int infantNum = 0;
            for (Passergeninfo passengers : orderinfo.getPassengers()) {
                switch (passengers.getPassengerType()) {
                    case "成人":
                        adultNum++;
                        break;
                    case "儿童":
                        childNum++;
                        queryObject.put("childNum", childNum);
                        break;
                    case "婴儿":
                        infantNum++;
                        queryObject.put("infantNum", infantNum);
                        break;
                    default:
                        break;
                }
            }
            queryObject.put("adultNum", String.valueOf(adultNum));
            queryObject.put("childNum", String.valueOf(childNum));
            queryObject.put("infantNum", String.valueOf(infantNum));
            queryObject.put("cabinOrder", "0");
            queryObject.put("airLine", 1);
            queryObject.put("flyType", 0);
            queryObject.put("international", "0");
            queryObject.put("action", "0");
            queryObject.put("segType", "1");
            queryObject.put("cache", 0);
            queryObject.put("preUrl", "");
            queryObject.put("isMember", "");

            StringEntity entity = new StringEntity(queryObject.toString(), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);
            httpPost.setConfig(config);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Host", "b2c.csair.com");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
            CloseableHttpResponse response = httpclient.execute(httpHost, httpPost);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            if (StringUtils.isNotEmpty(back)) {
                JSONObject backJson = JSON.parseObject(back);
                if ("true".equals(backJson.getString("needverify"))) {
                    logger.info("查询航班需要验证" + back);
                    System.out.println("查询航班需要验证:" + back);
                }
                if ("true".equals(backJson.getString("success"))) {
                    System.out.println("航班查询成功:" + back);
                    logger.info("查询航班成功" + back);
                } else if ("false".equals(backJson.getString("success"))) {
                    logger.info("查询航班失败" + backJson.getString("errorMsg"));
                    System.out.println("查询航班失败:" + backJson.getString("errorMsg"));
                    return backJson.getString("errorMsg");
                }
            } else {
                logger.info("查询航班失败" + back);
            }
        } catch (IOException e) {
            logger.error("CZ官网查询航班错误", e);
        }
        return back;
    }


    public static void main(String[] args) {
        String orderJson = "{\n" +
                "    \"account\": \"13682690632_119617\",\n" +
                "    \"airline\": \"CZ\",\n" +
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
                "            \"airline\": \"CZ\",\n" +
                "            \"arrival\": \"WUH\",\n" +
                "            \"cabin\": \"\",\n" +
                "            \"departure\": \"SZX\",\n" +
                "            \"departureDate\": \"2019-07-26\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"CZ3911\",\n" +
                "            \"price\": \"750.00\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"32550175\",\n" +
                "    \"idCardNo\": \"410781197310154713\",\n" +
                "    \"idCardType\": \"IDCARD\",\n" +
                "    \"ifUsedCoupon\": false,\n" +
                "    \"isOutticket\": \"false\",\n" +
                "    \"linkMan\": \"华勇\",\n" +
                "    \"matchCabin\": false,\n" +
                "    \"mobile\": \"13072660551\",\n" +
                "    \"orderNo\": \"406786710\",\n" +
                "    \"orderTime\": \"2019-03-11 00:12:13\",\n" +
                "    \"otheraccount\": \"fyyzm1_wo4feizhiyou\",\n" +
                "    \"ownername\": \"王涛\",\n" +
                "    \"passengers\": [\n" +
                "        {\n" +
                "            \"birthday\": \"1975-11-29 00:00:00+08:00\",\n" +
                "            \"id\": \"37617005\",\n" +
                "            \"idcard\": \"532225197511291117\",\n" +
                "            \"passengerCardType\": \"身份证\",\n" +
                "            \"passengerName\": \"尹建飞\",\n" +
                "            \"passengerSex\": \"男\",\n" +
                "            \"passengerType\": \"成人\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"birthday\": \"1983-11-21 00:00:00+08:00\",\n" +
                "            \"id\": \"37617004\",\n" +
                "            \"idcard\": \"350781198311212039\",\n" +
                "            \"passengerCardType\": \"身份证\",\n" +
                "            \"passengerName\": \"梁邵平\",\n" +
                "            \"passengerSex\": \"男\",\n" +
                "            \"passengerType\": \"成人\"\n" +
                "        },\n" +
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

        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig config = null;
        CZNEWService cznewService = new CZNEWService();
        JSONObject jsonObject = JSON.parseObject(orderJson);
        Orderinfo orderinfo = jsonObject.toJavaObject(Orderinfo.class);

        for (int i = 0; i < 50; i++) {
            cznewService.queryFlights(httpClient, config, orderinfo);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
