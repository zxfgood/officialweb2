package com.feeye.test;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author xs
 * @description
 * @date 2019/6/28
 */
public class NewTest {
    public static void main(String[] args) throws IOException {
/*        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://210.73.194.46/api/FYApi");
        List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
        nameValue.add(new BasicNameValuePair("Type", "TicketResult"));
        nameValue.add(new BasicNameValuePair("TicketInfo", "eyJPcmRlcklkIjoiTjA2MjAxOTA2MjgxNDUxMTcxMTEwMDEiLCJwbnJObyI6IjAwMDAwMCIsIkZsaWdodEluZm8iOlt7IkZsaWdodE5vIjoiSFU3MTQyIiwiQ2FiaW4iOiJWIiwiVHlwZSI6IjEiLCJkZXBDaXR5Q29kZSI6IlBWRyIsImFyckNpdHlDb2RlIjoiQ0FOIiwiZGVwRGF0ZVRpbWUiOiIyMDE5LTA3LTA3IDA4OjIwIiwiYXJyVGltZSI6IjExOjAwIiwiZnVsUHJpY2UiOjAsIm90aGVyUHJpY2UiOiIifV0sIlBhc3NlbmdlckluZm8iOlt7InBhc3NOYW1lIjoi5LiB5pmT5am3IiwidGlja2V0Tm8iOiI4ODAyMTk1Njk1NTM0IiwicGFzc1R5cGUiOiIxIiwicGFzc0NhcmRUeXBlIjoiTkkiLCJwYXNzQ2FyZE5vIjoiMzcwNjgzMTk5MjExMjc3MjI4IiwicGF5UHJpY2UiOiI3NzAuMCJ9XX0="));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValue, "UTF-8"));
        System.out.println(nameValue.toString());
        HttpResponse res1 = httpClient.execute(httpPost);
        String retJson = EntityUtils.toString(res1.getEntity(), "UTF-8");
        System.out.println(retJson);
    }*/


        String text = "<ul class=\"route-info-right\">\n" +
                "<li>\n" +
                "<strong>状态：</strong><span class=\"outstanding\">已使用</span>\n" +
                "</li>\n" +
                "<li>\n" +
                "<strong>票款小计\n" +
                "\t\t\t\t\t\t\t\t\t：</strong>\n" +
                "<div class=\"tip tip-em\">\n" +
                "<a href=\"javascript:\" class=\"tip-trigger\">1270</a>\n" +
                "<div class=\"tip-con\">\n" +
                "<div class=\"tip-pop\">\n" +
                "<div class=\"tip-main\">\n" +
                "<p class=\"tip-p\">￥1220(机票价)<br>\n" +
                "</p>\n" +
                "<p class=\"tip-p\">￥50.0(民航发展基金)<br>\n" +
                "</p>\n" +
                "<p class=\"tip-p\">￥0.0(燃油费)<br>\n" +
                "</p>\n" +
                "<p class=\"tip-p-b\">\n" +
                "<b>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t￥1270</b>(票款小计)</p>\n" +
                "</div>\n" +
                "<span class=\"tip-arrow\"></span>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</li>\n" +
                "<li>\n" +
                "<strong>直减：</strong><span class=\"outstanding\">\n" +
                "\t\t\t\t\t\t\t-￥0</span>\n" +
                "</li>\n" +
                "<li>\n" +
                "<strong>优惠券：</strong><span class=\"outstanding\">\n" +
                "\t\t\t\t\t\t\t-￥1000</span>\n" +
                "</li>\n" +
                "<li>\n" +
                "<strong>优惠券号：</strong></li>\n" +
                "<li>\n" +
                "<strong>行李：\n" +
                "\t\t\t\t\t\t\t\t</strong>20KG</li>\n" +
                "</ul>";

        File file = new File("C:/HU.txt");
        String tex = FileUtils.readFileToString(file,"UTF-8");
        Document doc = Jsoup.parse(tex);
        Elements elements = doc.getElementsByClass("route-info-right");
        StringBuffer couponId = new StringBuffer();
        for (int i = 0; i <elements.size() ; i++) {
            String couponInfo = elements.get(i).getElementsByTag("li").get(4).text();
            if (couponInfo.contains("优惠券号")) {
                couponId.append(couponInfo.substring(5));
                couponId.append("|");
            }
        }
        couponId.delete(couponId.length() - 1, couponId.length());
        System.out.println(couponId);
        couponId.toString().split("\\|");



//        System.out.println(new NewTest().isNeedProxy());
    }

    // 判断访问目标网站是否需要代理
    private boolean isNeedProxy() {
        boolean result = true;
        URL url;
        try {
            url = new URL("http://www.tianjin-air.com/api/airLowFareSearch/search");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);
            int i = connection.getContentLength();
            if (i > 0) {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}