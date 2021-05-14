package com.feeye.util;

import okhttp3.*;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class HttpUtils
{
    private static HttpUtils httpUtils = new HttpUtils();
    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    private HttpUtils()
    {
        this.builder.retryOnConnectionFailure(true);
        this.builder.connectTimeout(8L, TimeUnit.SECONDS);
        this.builder.readTimeout(30L, TimeUnit.SECONDS);
    }

    public static HttpUtils getInstance()
    {
        return httpUtils;
    }

    public String post(String url, String params)
    {
        try
        {
            OkHttpClient client = this.builder.build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, params);
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getBody(String url)
    {
        try
        {
            OkHttpClient client = this.builder.build();
            Request request = new Request.Builder().url(url).addHeader("Connection", "close").build();
            Response response = client.newCall(request).execute();
            return response.body().bytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String get(String url)
    {
        try
        {
            OkHttpClient client = this.builder.build();
            Request request = new Request.Builder().url(url).addHeader("Connection", "close").build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String get(String url, String ip, int port)
    {
        try
        {
            OkHttpClient.Builder cb = new OkHttpClient.Builder();
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            cb.proxy(proxy);
            cb.connectTimeout(1L, TimeUnit.SECONDS);
            cb.readTimeout(2L, TimeUnit.SECONDS);
            OkHttpClient client = cb.build();
            Request request = new Request.Builder().url(url).addHeader("Connection", "close").build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception localException) {}
        return null;
    }

    public static void main(String[] args)
    {
        for (int i = 0; i < 300; i++)
        {
            long a = System.currentTimeMillis();
            String aString = getInstance().get("http://seo.chinaz.com/");
            System.out.println("时间-" + (System.currentTimeMillis() - a) + "\r\n");
        }
    }
}
