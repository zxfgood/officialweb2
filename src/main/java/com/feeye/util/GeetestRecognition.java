package com.feeye.util;

public class GeetestRecognition
{
    private static final String api = "http://api.ddocr.com/api/gateway.jsonp";

    public static String recognition(String gt, String referer, String challenge, String secretkey)
    {
        StringBuffer body = new StringBuffer();
        body.append("wtype=geetest&secretkey=").append(secretkey);
        body.append("&gt=").append(gt).append("&referer=").append(referer);
        body.append("&challenge=").append(challenge == null ? "" : challenge);

        body.append("&ip=");
        body.append("&proxyuser=");
        body.append("&proxypwd=");
        body.append("&devCode=a91394df88f64b9bb570f096f6923e83");
        body.append("&supporttype=");
        String resp = HttpUtils.getInstance().post("http://api.ddocr.com/api/gateway.jsonp", body.toString());
        return resp;
    }

    public static void main(String[] args)
    {
        String str = recognition("f7de35c933f0311502b5a6a4b6a98842", "https://passport.ceair.com/cesso/geet!geetInit.shtml", "8f9a42841fa207c144b37316172274b0", "");
        System.out.println(str);
    }
}
