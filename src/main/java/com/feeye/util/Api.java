package com.feeye.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Date;

public class Api {
    private static final Logger log = LoggerFactory.getLogger(Api.class);
    protected String app_id;
    protected String app_key;
    protected String pd_id;
    protected String pd_key;
    protected String pred_url;

    public void Init(String app_id, String app_key, String pd_id, String pd_key) {
        this.app_id = app_id;
        this.app_key = app_key;
        this.pd_id = pd_id;
        this.pd_key = pd_key;
        this.pred_url = "http://pred.fateadm.com";
    }

    /**
     * 查询余额
     * 参数：无
     * 返回值：
     * resp.ret_code：正常返回0
     * resp.err_msg：异常时返回异常详情
     */
    public Util.HttpResp QueryBalc() throws Exception {
        long cur_tm = new Date().getTime() / 1000;    // 时间戳精确到秒。所以除以1000
        String stm = String.valueOf(cur_tm);
        String sign = Util.CalcSign(pd_id, pd_key, stm);
        String url = this.pred_url + "/api/custval";
        String params = "user_id=" + this.pd_id + "&timestamp=" + stm + "&sign=" + sign;
        String pres = Util.HttpPost(url, params);
        Util.HttpResp resp = Util.ParseHttpResp(pres);
        return resp;
    }

    /***
     * 查询余额：直接返回余额结果
     * 参数：无
     * 返回值： 用户余额:double
     */
    public double QueryBalcExtend() throws Exception {
        Util.HttpResp resp = QueryBalc();
        return resp.cust_val;
    }

    /**
     * 充值接口
     * 参数：cardid：充值卡号, cardkey：充值卡签名串
     * 返回值：
     * resp.ret_code：正常返回0
     * resp.err_msg：异常时返回异常详情
     */
    public Util.HttpResp Charge(String cardid, String cardkey) throws Exception {
        long cur_tm = new Date().getTime() / 1000;    // 时间戳精确到秒。所以除以1000
        String stm = String.valueOf(cur_tm);
        String sign = Util.CalcSign(pd_id, pd_key, stm);
        String csign = Util.CalcMd5(pd_key + stm + cardid + cardkey);
        String url = this.pred_url + "/api/charge";
        String params = "user_id=" + pd_id + "&timestamp=" + stm + "&sign=" + sign + "&cardid=" + cardid + "&csign=" + csign;
        String pres = Util.HttpPost(url, params);
        Util.HttpResp resp = Util.ParseHttpResp(pres);
        return resp;
    }

    /***
     * 充值接口：直接返回是否成功
     * 参数：cardid：充值卡号, cardkey：充值卡签名串
     * 返回值： 充值成功返回 0
     */
    private int ChargeExtend(String cardid, String cardkey) throws Exception {
        Util.HttpResp resp = Charge(cardid, cardkey);
        return resp.ret_code;
    }


    /**
     * 文件形式进行验证码识别
     * 参数： pred_type：识别类型  file_name：文件名
     * 返回值：
     * resp.ret_code：正常返回0
     * resp.err_msg：异常时返回异常详情
     * resp.req_Id：唯一订单号
     * resp.pred_resl：识别的结果
     */
    public Util.HttpResp PredictFromFile(String pred_type, String file_name) throws Exception {
        byte[] file_data = Util.ReadBinaryFile(file_name);
        if (file_data == null) {
            Util.HttpResp resp = new Util.HttpResp();
            resp.ret_code = -1;
            resp.err_msg = "ERROR: read file failed! file_name: " + file_name;
            return resp;
        }
        Util.HttpResp resp = Predict(pred_type, file_data);
        return resp;
    }

    /***
     * 文件形式进行验证码识别：直接返回识别结果
     * 参数： pred_type：识别类型  file_name：文件名
     * 返回值： 识别的结果:String
     */
    public Util.HttpResp PredictFromFileExtend(String pred_type, String file_name) throws Exception {
        Util.HttpResp resp = PredictFromFile(pred_type, file_name);
        return resp;
    }

    /**
     * 验证码识别
     * 参数： pred_type：识别类型  img_data：图片数据
     * 返回值：
     * resp.ret_code：正常返回0
     * resp.err_msg：异常时返回异常详情
     * resp.req_Id：唯一订单号
     * resp.pred_resl：识别的结果
     */
    public Util.HttpResp Predict(String pred_type, byte[] img_data) throws Exception {
        long cur_tm = new Date().getTime() / 1000;    // 时间戳精确到秒。所以除以1000
        String stm = String.valueOf(cur_tm);
        String sign = Util.CalcSign(pd_id, pd_key, stm);
        String asign = "";
        URL url = new URL(pred_url + "/api/capreg");
        if (!app_id.isEmpty()) {
            asign = Util.CalcSign(app_id, app_key, stm);
        }
        String pres = Util.MFPost(url, img_data, stm, pd_id, sign, app_id, asign, pred_type);
        // System.out.println(pres);
        Util.HttpResp resp = Util.ParseHttpResp(pres);
        return resp;
    }

    /***
     * 验证码识别
     * 参数： pred_type：识别类型  img_data：图片数据
     * 返回值： 识别的结果:String
     */
    public String PredictExtend(String pred_type, byte[] img_data) throws Exception {
        Util.HttpResp resp = Predict(pred_type, img_data);
        return resp.pred_resl;
    }

    /**
     * 识别失败，进行退款请求
     * 参数： req_id：需要退款的订单号
     * 返回值：
     * resp.ret_code：正常返回0
     * resp.err_msg：异常时返回异常详情
     * <p>
     * 注意：
     * Predict识别接口，仅在RetCode == 0时才会进行扣款，才需要进行退款请求，否则无需进行退款操作
     * 注意2：
     * 退款仅在正常识别出结果后，无法通过网站验证的情况，请勿非法或者滥用，否则可能进行封号处理
     */
    public Util.HttpResp Justice(String req_id) throws Exception {
        long cur_tm = new Date().getTime() / 1000;     // 时间戳精确到秒。所以除以1000
        String stm = String.valueOf(cur_tm);
        String sign = Util.CalcSign(pd_id, pd_key, stm);
        String url = pred_url + "/api/capjust";
        String params = "user_id=" + pd_id + "&timestamp=" + stm + "&sign=" + sign + "&request_id=" + req_id;
        String pres = Util.HttpPost(url, params);
        Util.HttpResp resp = Util.ParseHttpResp(pres);
        return resp;
    }

    /***
     * 退款请求： 直接返回是否成功 
     * 参数： req_id：需要退款的订单号
     * 返回值： 返回 0 代表成功
     */
    public int JusticeExtend(String req_id) throws Exception {
        Util.HttpResp resp = Justice(req_id);
        return resp.ret_code;
    }

    public static void main(String[] args) throws Exception {
        String result ="";
        Api api = new Api();
        String app_id = "312050";
        String app_key = "CzscK7uaF9yydZ2RnfHWEv8qpqziOssQ";
        String pd_id = "112050";  //PD账号
        String pd_key = "kdRc5Pu6M4ekP3UkUShlh9cqqumnS+3t";  //PD秘钥
        // 对象生成之后，在任何操作之前，需要先调用初始化接口
        api.Init(app_id, app_key, pd_id, pd_key);
        Long startTime = System.currentTimeMillis();
        //  306005000 FU  30400 四位数字字母组合  40300 DZ 3U 三位验证码
        Util.HttpResp httpResp = api.PredictFromFileExtend("40300", "C:\\testImg\\DZ.jpg");
        if(!(0 == httpResp.ret_code) && StringUtils.isNotEmpty(httpResp.err_msg)) {
            result = httpResp.err_msg; //错误信息
        }else {
            result =httpResp.pred_resl +";" + httpResp.req_id;
        }
        Long endTime = System.currentTimeMillis();
        Long time = (endTime - startTime) / 1000;
        log.info("验证码识别耗时：" + time + "s" + "\t" + "识别结果：" + result);
        System.out.println("验证码识别耗时：" + time + "s" + "\t" + "识别结果：" + result);
    }

    /**
     * 验证码识别接口
     * @param pd_id 用户PD账号
     * @param pd_key 用户PD秘钥
     * @param code_Type 识别类型
     * @param file_name 文件名
     * @return
     */
    public static String getValidCode(String pd_id, String pd_key, String code_Type, String file_name) {
        String result = "";
        Api api = new Api();
        String app_id = "312050";
        String app_key = "CzscK7uaF9yydZ2RnfHWEv8qpqziOssQ";
        api.Init(app_id, app_key, pd_id, pd_key);
        Long startTime = System.currentTimeMillis();
        try {
            Util.HttpResp httpResp = api.PredictFromFileExtend(code_Type, file_name);
            if(!(0 == httpResp.ret_code) && StringUtils.isNotEmpty(httpResp.err_msg)) {
                result = httpResp.err_msg; //错误信息
            }else {
                result =httpResp.pred_resl;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result ="未知错误";
        }
        Long endTime = System.currentTimeMillis();
        Long time = (endTime - startTime) / 1000;
        System.out.println("验证码识别耗时：" + time + "s" + "\t" + "识别结果：" + result);
        return result;
    }
}
