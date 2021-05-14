package com.feeye.service.processor;

import com.feeye.util.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

/**
 * @Author: zcf
 * @Date: 2018/9/20 15 23
 * @Description:
 **/
public class DailiyunService {
    private static final Logger logger = Logger.getLogger(DailiyunService.class);

    public static BlockingQueue<String> proxyIPQueue = new LinkedBlockingQueue<String>();


    /**
     * 获取的代理ip的集合  格式为  ip:port_status  status  表示代理ip是否有效
     *
     * @param ipsum 每次获取的ip总数
     * @return
     */
    public synchronized static void getIpList(int ipsum) {
        if (proxyIPQueue.size() > 0) {
            return;
        }
        //进行地址请求获取ip
        boolean success = false;
        String result = "";
        int count = 0;
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=3000&detail=true"; //count值过大会造成失效IP过多
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=" + ipsum + "&detail=true";
        String url = "http://feeyeapp.v4.dailiyun.com/query.txt?key=NP031FF6E7&word=&rand=false&detail=true" + "&count=" + ipsum;
        // PropertiesUtils.getPropertiesValue("config","dlyurl")+"&count="+ipsum;
        //count值过大会造成失效IP过多
       /* HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);//连接超时处理
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);//读写超时处理*/
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet method = new HttpGet(url);
        int timeout = 1000 * 5;
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setCircularRedirectsAllowed(true)
                .setStaleConnectionCheckEnabled(true).build();
        method.setConfig(defaultRequestConfig);

        while (!success && count < 3) {
            try {
                if (count > 0) {
                    Thread.sleep(5000);
                }
                // client.executeMethod(method);
                HttpResponse httpResponse = client.execute(method);
                //result = new String(method.getResponseBody(), method.getResponseCharSet());
                result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                method.releaseConnection();
                if (!StringUtils.isEmpty(result)) {
                    String ips[] = result.split("\r\n");
                    //ej:1.197.202.79:57112,1.197.202.79,中国-河南-济源--电信,1517818761,1517819661
                    //tips:proxyIp:port,realOuterIp,location,importTime,expireTime
                    for (int i = 0; i < ips.length; i++) {
                        String[] ipDetail = ips[i].split(",");
                        String ip = ipDetail[0];
                        String expireStr = ipDetail[4];
                        Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,8}");
                        if (p.matcher(ip).find()) {
                            if (!StringUtils.isEmpty(expireStr)) {
                                Integer exprire = Integer.parseInt(expireStr);
                                Integer now = (int) (System.currentTimeMillis() / 1000);
                                //如果剩余有效时间大于180秒，才入库
                                if (exprire - now > 180) {
                                    //添加老版代理云提取标记
                                    proxyIPQueue.add(ip + "-" + exprire);
                                }
                            }
                        }
                    }
                }
                success = true;
                logger.info("老版代理云提取ip数量--是：" + proxyIPQueue.size());
            } catch (Exception e) {
                logger.error("老版代理云提取ip报错:" + e);
                count++;
            } finally {
                try {
                    if (method != null) {
                        method.releaseConnection();
                    }
                } catch (Exception e) {
                    logger.error("getIpListOld--释放请求连接出错:" + e);
                }
            }
        }
    }


    /**
     * 获取的代理ip的集合  格式为  ip:port_status  status  表示代理ip是否有效
     *
     * @param ipsum 每次获取的ip总数
     * @return
     */
    public synchronized static void getNewIpList(int ipsum) {
        if (proxyIPQueue.size() > 0) {
            return;
        }
        //进行地址请求获取ip
        boolean success = false;
        String result = "";
        int count = 0;
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=3000&detail=true"; //count值过大会造成失效IP过多
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=" + ipsum + "&detail=true";
        String url = "http://feeyedaili.v4.dailiyun.com/query.txt?key=NPB7B48A6A&word=&rand=false&detail=true&count=" + ipsum;
        // PropertiesUtils.getPropertiesValue("config","dlyurl")+"&count="+ipsum;
        //count值过大会造成失效IP过多
       /* HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);//连接超时处理
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);//读写超时处理*/
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet method = new HttpGet(url);
        int timeout = 1000 * 5;
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setCircularRedirectsAllowed(true)
                .setStaleConnectionCheckEnabled(true).build();
        method.setConfig(defaultRequestConfig);

        while (!success && count < 3) {
            try {
                if (count > 0) {
                    Thread.sleep(5000);
                }
                // client.executeMethod(method);
                HttpResponse httpResponse = client.execute(method);
                //result = new String(method.getResponseBody(), method.getResponseCharSet());
                result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                method.releaseConnection();
                if (!StringUtils.isEmpty(result)) {
                    String ips[] = result.split("\r\n");
                    //ej:1.197.202.79:57112,1.197.202.79,中国-河南-济源--电信,1517818761,1517819661
                    //tips:proxyIp:port,realOuterIp,location,importTime,expireTime
                    for (int i = 0; i < ips.length; i++) {
                        String[] ipDetail = ips[i].split(",");
                        String ip = ipDetail[0];
                        String expireStr = ipDetail[4];
                        Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,8}");
                        if (p.matcher(ip).find()) {
                            if (!StringUtils.isEmpty(expireStr)) {
                                Integer exprire = Integer.parseInt(expireStr);
                                Integer now = (int) (System.currentTimeMillis() / 1000);
                                //如果剩余有效时间大于180秒，才入库
                                if (exprire - now > 180) {
                                    //添加老版代理云提取标记
                                    proxyIPQueue.add(ip + "-" + exprire);
                                }
                            }
                        }
                    }
                }
                success = true;
                logger.info("老版代理云提取ip数量--是：" + proxyIPQueue.size());
            } catch (Exception e) {
                logger.error("老版代理云提取ip报错:" + e);
                count++;
            } finally {
                try {
                    if (method != null) {
                        method.releaseConnection();
                    }
                } catch (Exception e) {
                    logger.error("getIpListOld--释放请求连接出错:" + e);
                }
            }
        }
    }

    /**
     * 随即获取其中一个有效ip 有效时间为10 分钟
     *
     * @param ipsum 每次获取的ip总数
     * @return
     */
    public static String getRandomDailiIp(int ipsum) {
        String ip_port = proxyIPQueue.poll();
        if (StringUtils.isEmpty(ip_port)) {
            getIpList(ipsum);
        }
        ip_port = proxyIPQueue.poll();
        if (!StringUtils.isEmpty(ip_port)) {
            String expireStr = ip_port.split("-")[1];
            ip_port = ip_port.split("-")[0];
            Integer exprire = Integer.parseInt(expireStr);
            Integer now = (int) (System.currentTimeMillis() / 1000);
            //如果剩余有效时间大于60秒，才进行使用
            if (exprire - now > 60) {
                return ip_port;
            } else {
                return getRandomDailiIp(ipsum);
            }
        }
        return null;
    }


    /**
     * 随即获取其中一个有效ip 有效时间为10 分钟
     *
     * @param ipsum 每次获取的ip总数
     * @return
     */
    public static String getRandomNewDailiIp(int ipsum) {
        String ip_port = proxyIPQueue.poll();
        if (StringUtils.isEmpty(ip_port)) {
            getNewIpList(ipsum);
        }
        ip_port = proxyIPQueue.poll();
        if (!StringUtils.isEmpty(ip_port)) {
            String expireStr = ip_port.split("-")[1];
            ip_port = ip_port.split("-")[0];
            Integer exprire = Integer.parseInt(expireStr);
            Integer now = (int) (System.currentTimeMillis() / 1000);
            //如果剩余有效时间大于60秒，才进行使用
            if (exprire - now > 60) {
                return ip_port;
            } else {
                return getRandomNewDailiIp(ipsum);
            }
        }
        return null;
    }


    /**
     * 获取的代理ip的集合  格式为  ip:port_status  status  表示代理ip是否有效
     *
     * @param ipsum 每次获取的ip总数
     * @return
     */
    public static List<String> getIpListOld(int ipsum) {
        //进行地址请求获取ip
        boolean success = false;
        String result = "";
        int count = 0;
        List<String> proxyList = new ArrayList<String>();
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=3000&detail=true"; //count值过大会造成失效IP过多
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=" + ipsum + "&detail=true";
        String url = "http://feeyeapp.v4.dailiyun.com/query.txt?key=NP031FF6E7&word=&rand=false&detail=true" + "&count=" + ipsum;
        //count值过大会造成失效IP过多
       /* HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);//连接超时处理
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);//读写超时处理*/
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet method = new HttpGet(url);
        int timeout = 1000 * 5;
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setCircularRedirectsAllowed(true)
                .setStaleConnectionCheckEnabled(true).build();
        method.setConfig(defaultRequestConfig);

        while (!success && count < 3) {
            try {
                if (count > 0) {
                    Thread.sleep(5000);
                }
                // client.executeMethod(method);
                HttpResponse httpResponse = client.execute(method);
                //result = new String(method.getResponseBody(), method.getResponseCharSet());
                result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                method.releaseConnection();
                if (!StringUtils.isEmpty(result)) {
                    String ips[] = result.split("\r\n");
                    //ej:1.197.202.79:57112,1.197.202.79,中国-河南-济源--电信,1517818761,1517819661
                    //tips:proxyIp:port,realOuterIp,location,importTime,expireTime
                    for (int i = 0; i < ips.length; i++) {
                        String[] ipDetail = ips[i].split(",");
                        String ip = ipDetail[0];
                        String expireStr = ipDetail[4];
                        Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,8}");
                        if (p.matcher(ip).find()) {
                            if (!StringUtils.isEmpty(expireStr)) {
                                Integer exprire = Integer.parseInt(expireStr);
                                Integer now = (int) (System.currentTimeMillis() / 1000);
                                //如果剩余有效时间大于360秒，才入库
                                if (exprire - now > 360) {
                                    //添加老版代理云提取标记
                                    ip += "_0";
                                    proxyList.add(ip);
                                }
                            }
                        }
                    }
                }
                success = true;
                logger.info("老版代理云提取ip数量--是：" + proxyList.size());
            } catch (Exception e) {
                logger.error("老版代理云提取ip报错:" + e);
                count++;
            } finally {
                try {
                    if (method != null) {
                        method.releaseConnection();
                    }
                } catch (Exception e) {
                    logger.error("getIpListOld--释放请求连接出错:" + e);
                }
            }
        }
        return proxyList;
    }


    /**
     * 获取的代理ip的集合  格式为  ip:port_status  status  表示代理ip是否有效
     *
     * @param ipsum 每次获取的ip总数
     * @return
     */
    public static List<String> getIpListNew(int ipsum) {
        //进行地址请求获取ip
        boolean success = false;
        String result = "";
        int count = 0;
        List<String> proxyList = new ArrayList<String>();
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=3000&detail=true"; //count值过大会造成失效IP过多
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=" + ipsum + "&detail=true";
        String url = PropertiesUtils.getPropertiesValue("config", "dlyurl") + "&count=" + ipsum;
        //count值过大会造成失效IP过多
       /* HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);//连接超时处理
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);//读写超时处理*/
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet method = new HttpGet(url);
        int timeout = 1000 * 5;
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setCircularRedirectsAllowed(true)
                .setStaleConnectionCheckEnabled(true).build();
        method.setConfig(defaultRequestConfig);

        while (!success && count < 3) {
            try {
                if (count > 0) {
                    Thread.sleep(5000);
                }
                // client.executeMethod(method);
                HttpResponse httpResponse = client.execute(method);
                //result = new String(method.getResponseBody(), method.getResponseCharSet());
                result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                method.releaseConnection();
                if (!StringUtils.isEmpty(result)) {
                    String ips[] = result.split("\r\n");
                    //ej:1.197.202.79:57112,1.197.202.79,中国-河南-济源--电信,1517818761,1517819661
                    //tips:proxyIp:port,realOuterIp,location,importTime,expireTime
                    for (int i = 0; i < ips.length; i++) {
                        String[] ipDetail = ips[i].split(",");
                        String ip = ipDetail[0];
                        String expireStr = ipDetail[4];
                        Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,8}");
                        if (p.matcher(ip).find()) {
                            if (!StringUtils.isEmpty(expireStr)) {
                                Integer exprire = Integer.parseInt(expireStr);
                                Integer now = (int) (System.currentTimeMillis() / 1000);
                                //如果剩余有效时间大于360秒，才入库
                                if (exprire - now > 360) {
                                    //添加老版代理云提取标记
                                    ip += "_0";
                                    proxyList.add(ip);
                                }
                            }
                        }
                    }
                }
                success = true;
                logger.info("老版代理云提取ip数量--是：" + proxyList.size());
            } catch (Exception e) {
                logger.error("老版代理云提取ip报错:" + e);
                count++;
            } finally {
                try {
                    if (method != null) {
                        method.releaseConnection();
                    }
                } catch (Exception e) {
                    logger.error("getIpListOld--释放请求连接出错:" + e);
                }
            }
        }
        return proxyList;
    }

    /**
     * 获取的代理ip的集合  格式为  ip:port_status  status  表示代理ip是否有效
     *
     * @param ipsum 每次获取的ip总数
     * @return
     * @Param word ip所在的地区
     */
    public static List<String> getIpListOld(int ipsum, String word) {
        //进行地址请求获取ip
        boolean success = false;
        String result = "";
        int count = 0;
        List<String> proxyList = new ArrayList<String>();
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=3000&detail=true"; //count值过大会造成失效IP过多
        //String url = "http://dly.134t.com/query.txt?key=NP031FF6E7&word=&count=" + ipsum + "&detail=true";
        String url = "http://feeyeapp.v4.dailiyun.com/query.txt?key=NP031FF6E7&rand=false&detail=true" + "&count=" + ipsum + "&word=" + word;
        //count值过大会造成失效IP过多
       /* HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);//连接超时处理
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);//读写超时处理*/
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet method = new HttpGet(url);
        int timeout = 1000 * 5;
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setCircularRedirectsAllowed(true)
                .setStaleConnectionCheckEnabled(true).build();
        method.setConfig(defaultRequestConfig);

        while (!success && count < 3) {
            try {
                if (count > 0) {
                    Thread.sleep(5000);
                }
                // client.executeMethod(method);
                HttpResponse httpResponse = client.execute(method);
                //result = new String(method.getResponseBody(), method.getResponseCharSet());
                result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                method.releaseConnection();
                if (!StringUtils.isEmpty(result)) {
                    String ips[] = result.split("\r\n");
                    //ej:1.197.202.79:57112,1.197.202.79,中国-河南-济源--电信,1517818761,1517819661
                    //tips:proxyIp:port,realOuterIp,location,importTime,expireTime
                    for (int i = 0; i < ips.length; i++) {
                        String[] ipDetail = ips[i].split(",");
                        String ip = ipDetail[0];
                        String expireStr = ipDetail[4];
                        Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,8}");
                        if (p.matcher(ip).find()) {
                            if (!StringUtils.isEmpty(expireStr)) {
                                Integer exprire = Integer.parseInt(expireStr);
                                Integer now = (int) (System.currentTimeMillis() / 1000);
                                //如果剩余有效时间大于60秒，才入库
                                if (exprire - now > 60) {
                                    //添加老版代理云提取标记
                                    ip += "_0";
                                    proxyList.add(ip);
                                }
                            }

                        }
                    }
                }
                success = true;
                logger.info("老版代理云提取ip数量--是：" + proxyList.size());
            } catch (Exception e) {
                logger.error("老版代理云提取ip报错:" + e);
                count++;
            } finally {
                try {
                    if (method != null) {
                        method.releaseConnection();
                    }
                } catch (Exception e) {
                    logger.error("getIpListOld--释放请求连接出错:" + e);
                }
            }
        }
        return proxyList;
    }

    /**
     * 随即获取其中一个有效ip 有效时间为10 分钟
     *
     * @param ipsum 每次获取的ip总数
     * @return
     */
    public static String getRandomIp(int ipsum) {
        Random random = new Random();
        String ip_port = "";
        List<String> allip = getIpListOld(ipsum);
        ip_port = allip.get(random.nextInt(allip.size()));
        ip_port = ip_port.split("_")[0];
        return ip_port;
    }

    /**
     * 随即获取其中一个有效ip 有效时间为10 分钟
     *
     * @param ipsum 每次获取的ip总数
     * @return
     * @Param word ip所在的地区
     */
    public static String getRandomIp(int ipsum, String word) {
        Random random = new Random();
        String ip_port = "";
        List<String> allip = getIpListOld(ipsum, word);
        ip_port = allip.get(random.nextInt(allip.size()));
        ip_port = ip_port.split("_")[0];
        return ip_port;
    }
}
