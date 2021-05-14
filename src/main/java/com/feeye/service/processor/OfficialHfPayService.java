/*
package com.feeye.service.processor;

import action.RsaEnc;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OfficialHfPayService {
    private static final Logger logger = Logger.getLogger(OfficialHfPayService.class);
    private static final int timeout = 70000;

    public Map <String, String> fhPayMF(String url, String orderJson, CloseableHttpClient httpClient, RequestConfig config) {
        CloseableHttpResponse response = null;
        String back = "";
        Map <String, String> resultMap = new HashMap <>();
        try {
//            JSONObject json = new JSONObject(orderJson);
//            String order_id = json.getString("id");
//            String payType = json.getString("payType");
//            String agentAccount = json.getString("creditNo");
//            String agentPassword = json.getString("cvv");

            //解析查询参数
            Map <String, String> paramMap = new HashMap <String, String>();
            LinkedHashMap <String, String> encrypMap = new LinkedHashMap <String, String>();
            Map <String, String> map = new HashMap <String, String>();
            if(StringUtils.isNotEmpty(url)) {
                String[] paramArr = url.substring(45).split("&");
                for (int i = 0; i < paramArr.length; i++) {
                    String[] split = paramArr[i].split("=");
                    if ("MerId".equals(split[0]) || "OrdId".equals(split[0]) || "OrdAmt".equals(split[0])) {
                        map.put(split[0], split[1]);
                    }
                }
                encrypMap.put("merId", map.get("MerId"));
                encrypMap.put("ordId", map.get("OrdId"));
                encrypMap.put("ordAmt", map.get("OrdAmt"));
                //TODO 从订单中获取
                encrypMap.put("agentAccount", "fzy001"); //商户用户号
                encrypMap.put("agentPassword", "1234qwer"); //密码
                //加密payInfo参数
                String payInfo = payInfoEncryp(encrypMap);
                paramMap.put("PayInfo", payInfo);
                paramMap.put("EtclientFlag", "Y");
                paramMap.put("EtclientUsrId", "fzy001");
                paramMap.put("TrueGateId", "61");
                paramMap.put("PartnerCode", "B9");
                String params = url.substring(45) +"&" + boundParams(paramMap);
                HttpGet get = new HttpGet("https://mas.chinapnr.com/gar/entry.do?" + params);
                get.setConfig(config);
                get.setHeader("Host", "mas.chinapnr.com");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
                response = httpClient.execute(get);
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(back);
                org.dom4j.Document document = DocumentHelper.parseText(back);
                org.dom4j.Element rootElement = document.getRootElement();
                if (StringUtils.equals("000000", rootElement.elementText("RespCode"))) { //支付成功
                    System.out.println("支付成功");
                    logger.info("");
                    String money = rootElement.elementText("OrdAmt"); //支付总金额
                    String bankNo = rootElement.elementText("TrxId");  //交易流水号
                    String returnUrl = rootElement.elementText("RetUrl");  //回调通知商户url
                    resultMap.put("money",money);
                    resultMap.put("bankNo",bankNo);
                    resultMap.put("returnUrl",returnUrl);
                } else {
                    String errorMsg = rootElement.elementText("ErrorMsg");
                    System.out.println(errorMsg);
                    resultMap.put("stop", errorMsg);
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("reeor", e);
                }
            }
        }
        return resultMap;
    }

    //PayInfo参数加密
    private String payInfoEncryp(LinkedHashMap <String, String> params) {
        String payInfoSing = "";
        String merKeyUr = "C:/PgPubk.key";
        for (Map.Entry <String, String> entry : params.entrySet()) {
            entry.getValue();
        }
        Object[] objects = params.values().toArray();
        RsaEnc rsaEnc = new RsaEnc();
        int ret = rsaEnc.rsaEncrypt(objects[0].toString(), objects[1].toString(), objects[2].toString(), objects[3].toString(), objects[4].toString(), merKeyUr);
        if (ret == 0) {
            payInfoSing = rsaEnc.getRasStr();
        }
        return payInfoSing;
    }

    //组装查询参数
    private String boundParams(Map <String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry <String, String> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        OfficialHfPayService officialHfPayService = new OfficialHfPayService();
        String url ="https://mas.chinapnr.com/gar/RecvMerchant.do?OrdAmt=230.00&BgRetUrl=https://pay.xiamenair.com/xmair-trade-app/chinapnr/pc/callBack&RetUrl=https://www.xiamenair.com/api/ipay/ticket/res&MerPriv=&UsrMp=&GateId=&PayUsrId=874039&DivDetails=&CurCode=&ChkValue=H4sIAAAAAAAAAG1WR5urOBD8QT6QnDjsQUJCJIEJNuAbySTjMA4Cfv1q5oWd3X0HPkEjlahSdzXUNA2HpZZ9PZrNu3CBX2hXHjORpsGRacDHozYDC9b7D1RTjdR7QE1yhnV%2Fb%2FqWqEyEkJG6Pha4PvojxZZURbXiIjB7kTnzUXQ7wCgCzEX96CLML3Om2pJR0oilAWev3b5LpVScS3NLY%2BmcD%2FqtaFddLovvNIE3Rz6zNOnfJTl3mXwQHcU6F%2FPPPeJ%2FMAqir%2F6EURGpyRLzWRpWcyT7J8dkjuxes3jVFGQ1OQrs%2BHOTk1FLY%2FeD88fSJ38XsLr2lldAuQ6mBvd6YA51SIFItPBOQjNXkI%2Bhxrgmev1TJ%2FcAQRqB3vVoBJhTp%2Bjg%2BzbSSpSR8ysNIfncNx9c%2Fp2s9uXDxHkNWew2fHznLYRlElxzxbqVRj%2FqMzj8wKQU9S7xRVz7ezcysYr8iI4ULhMU4SWNuK4dUPgoUenKY1zjf8fYacYOBT0B0h7Dhmq%2BuPyGf%2F0zfoAZYl8cHAQamn%2FqpMHQj%2FCRwuILS2PMjxTrksarWzXoYharL87tyfe9Ue3HHMBYTdF%2B9NHPc9tDz4vw7EZg4rkxupG%2F%2BswXKjW6jzDXHbf%2F1Rn4ewCWJkTs81ywDa4mj2kFmsLTe3k8seD9gklXkvDobeBDkIV5WngBJrsBqbmdKOfXWnF3NHl3mXWjCv2YLv0kQUO%2BODQrb8U%2BCPZlGyt6A4Umx1Jxg%2FiS6mijBk1qxtTZ9VPVZbH2OqXNU307ZRT1XYMeKL%2B2J%2F8gnKZ1qIVkb9BXdlScnZmuyNgml6O7LsGD0CXWBEk3gLQWusnfjivqgddYOK9ijba91Zt6D0%2Fi6JKut2e7KuCmLt7%2Bq%2FL2z7gAzVE7gF1VkD4%2FXbrr4aw8snGR3A9mnrnVLheeWRyXJ%2BljsVSN7plt3ubRxefadPYmmZ7UOfnhdgNeyjp4n5sXwL5PUuPubvIbvou9UQofPdjJhtGvEKgpBIB0da0vqWnCEsEHrOsPWGMd%2BgXXPaipvmQ2SE2bpRD6nDQgxPiqabB2JvWdKi5zBndIZfxKZfXJ71%2BOrHaFwv2g%2Fr7OJtbwe11XDIzPP3Zp%2BFW%2F%2FH75%2Fsw7OuNfI48dJgq3n7lVmsxPKcyA7tmiVrzdNNPn8HIWtukdaaosnq6spIB9zcUMQ4H5GgWAhYDndSDWEGO2m4EK66aug4lspiuEPCeptiWa9iA873TITKi37rU0Avbbr%2F7LTTnMX1eERYfzyaNPTPdgBFvM61CkyK6pDV5NO%2F7GSRXr8SeNUl57jmRFfvilB%2FfMfsnfTTkC3hemv4XgtOVFQTX4AMzwv7h4EKZYp%2BqyTWbNGE6es3YC5EqxqSv1PVVnCsUvHSzfjyn0v%2FHjOP%2BcL%2BL83e8eD%2FyHDoD32Q9AsYOX%2Fo7AObbdm%2BzutY2aiJUJxc3hehhAuivf0822y1idWBEHYCG1ryKV%2FHuMchgti50VMrmCl0QNstscDz3tuIFfdZA6aJe45gL4S0UohfZMhSqol3fgSOOruNRdGVV46HTULRJ3vXDn4LRtqDs6uiMON6Ed2gnUF%2BberMJQiPg6RkulanBbrbTDupqfzw0ZiseQVP25OmvXrMlgU7%2Bn3cHcrgsuzFOuhKS1FQe0TaMW1kbmbl3YOXhgSZbw%2BsoCTTrtHKVNLHYIfKLSO858oY%2BJdxjyw52yHXJVQ9lMwrPbZtfHLOJKhNrqFiprby8vYmN%2BB%2FNWvYRxpTpmwUYtET3JMJYz0UBac18jGNQYUoJZjP7v19wrX7%2F82hJ%2F9DATH2%2B83%2FH8OX96MzomFvfg4y2Vf3ixiXWpJLy7D2exinBC4f6XJ3u%2BeER%2ByMtQonUkupDX1rc%2B1xMYPjzAzAz6gCH%2F%2B38C5osA5j4MqlWshtf9eL871SLRzURO15gcYqjVuzZZkNVdV66Jw%2FJAl8Po9MF%2FH6Z1bo0pOOonrzX2l3NwWQjrpjO3yB6HQgk2ud27wQcKlaSWb8esZPoVHfHiMWr3tiO2ZjrEGVcEPN6hSJWTtdg6kH9fUwuXk2jvOud17uwqMC%2BFrBlFrRNJU67rkHzsDekcPKN1u5VeIrBlUala8y6ud6vt29IW59i9IabKuFV87gXgMXjT6MWqa7wsvNA3884%2FCtpOFD5uq%2BlZuHpj%2BOUCMkGwN6ru6Zu8veVpFn%2FIF3O76rQrKhK9TmV50a%2BTXSguxC6%2Fp8tIy0J9sXuWY2siUcGLYZOE%2BVAI1A902q1Gvf7rr78BzOTfzpwJAAA%3D&MerId=874039&OrdId=234046000004914114&Version=10&Pid=&CmdId=Buy";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig config = null;
        officialHfPayService.fhPayMF(url, "", httpClient, config);
    }
}
*/
