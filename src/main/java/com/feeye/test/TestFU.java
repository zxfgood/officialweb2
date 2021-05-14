package com.feeye.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.feeye.bean.OrderJson;
import com.feeye.service.processor.FUCreateOrderService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author xs
 * @description
 * @date 2019/4/16
 */
public class TestFU {
    public static void main(String[] args) throws IOException {
        String orderJson ="{\n" +
                "    \"account\": \"15603823706_qwe123456\",\n" +
                "    \"airline\": \"FU\",\n" +
                "    \"bankType\": \"GDB\",\n" +
                "    \"childrenMobile\": \"\",\n" +
                "    \"code\": \"\",\n" +
                "    \"codePassword\": \"\",\n" +
                "    \"creditNo\": \"4063661270845097\",\n" +
                "    \"cvv\": \"147\",\n" +
                "    \"drawerType\": \"GW\",\n" +
                "    \"expireMonth\": \"06\",\n" +
                "    \"expireYear\": \"2024\",\n" +
                "    \"flights\": [\n" +
                "        {\n" +
                "            \"airline\": \"FU\",\n" +
                "            \"arrival\": \"SYX\",\n" +
                "            \"cabin\": \"I\",\n" +
                "            \"departure\": \"HRB\",\n" +
                "            \"departureDate\": \"2019-04-21\",\n" +
                "            \"fType\": \"go\",\n" +
                "            \"flightNo\": \"FU6761\",\n" +
                "            \"price\": \"435.0\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"33424048\",\n" +
                "    \"idCardNo\": \"410781198508214713\",\n" +
                "    \"idCardType\": \"IDCARD\",\n" +
                "    \"ifUsedCoupon\": false,\n" +
                "    \"isOutticket\": \"true\",\n" +
                "    \"linkMan\": \"侯平辉\",\n" +
                "    \"matchCabin\": false,\n" +
                "    \"mobile\": \"18568517119\",\n" +
                "    \"orderNo\": \"ORLCECW8D10JP4103285\",\n" +
                "    \"orderTime\": \"2019-04-16 14:14:46\",\n" +
                "    \"otheraccount\": \"fyyzm1_wo4feizhiyou\",\n" +
                "    \"ownername\": \"王辉\",\n" +
                "    \"passengers\": [\n" +
                "        {\n" +
                "            \"birthday\": \"1997-04-01\",\n" +
                "            \"id\": \"38838314\",\n" +
                "            \"idcard\": \"239005199704012813\",\n" +
                "            \"passengerCardType\": \"身份证\",\n" +
                "            \"passengerName\": \"刘晗\",\n" +
                "            \"passengerType\": \"成人\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"payType\": \"xyk\",\n" +
                "    \"payerMobile\": \"18568638588\",\n" +
                "    \"qiangpiao\": \"\",\n" +
                "    \"username\": \"fzylin\",\n" +
                "    \"ytype\": \"优惠经济舱\"\n" +
                "}";
        JSONObject jsonObject = JSON.parseObject(orderJson);
        OrderJson orderJsonBean = jsonObject.toJavaObject(OrderJson.class);
        FUCreateOrderService fuCreateOrderService = new FUCreateOrderService();
        String fileUri = "C://testImg//preOrderBack.txt";
        File file = new File(fileUri);
        String s = "";
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((s=br.readLine()) != null) {
            sb.append(s);
        }
        sb.toString();

        String back = fuCreateOrderService.handerDataPreOrder(sb.toString(), orderJsonBean);
        System.out.println(back);
    }
}
