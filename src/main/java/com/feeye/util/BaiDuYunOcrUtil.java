package com.feeye.util;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @description 百度云Ocr识别图片
 * @author xs
 * @date 2019/4/1
 */
public class BaiDuYunOcrUtil {
    public static final String APP_ID = "15911194";
    public static final String API_KEY = "8zqLY5qNYZPYjMjfUsQG8QUW";
    public static final String SECRET_KEY = "X7rwwe2hg7BaAgpmzjaWfLoD7KpFtW9m";

    /**
     *@description 识别图片验证码
     *@author  xs
     *@date  2019/4/1
     *@param url
     *@return resultStr
     */
    public static String getVerifyCode(String url) throws JSONException {
        String resultStr = "";
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(60000);
        client.setSocketTimeoutInMillis(60000);
        // 传入可选参数调用接口
        HashMap <String, String> options = new HashMap <>();
        options.put("language_type", "CHN_ENG"); //中英文
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");
        // 调用接口
        JSONObject res = client.handwriting(url,options);
//        System.out.println(res);
        JSONArray resultList = res.getJSONArray("words_result");
        for (Object result : resultList) {
            JSONObject object = (JSONObject) result;
            resultStr = object.getString("words");
        }
        System.out.println(resultStr);
        return resultStr;
    }

    public static void main(String[] args) {
        BaiDuYunOcrUtil.getVerifyCode("C:\\testImg\\1db6f50bf042489eaffd245b0c8f9100.jpg");
    }
}
