package com.feeye.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author xs
 * @date 2019/3/19 10:58
 */
public class PropertiesUtils {
    /**
     * 资源文件工具类
     */
    // key是绝对路径
    private static Map <String, Properties> repository = new HashMap <String, Properties>();

    /**
     * 从指定路径下的配置文件中查询某个属性值
     *
     * @param path       如果是相对路径，则默认从用户目录下查找
     * @param attribute
     * @param isInternal 是否是jar包中的资源文件
     * @return
     */
    public static String getProperty(String path, String attribute, boolean isInternal) {
        Properties prop = null;
        if (!isInternal) {
            File propFile = new File(path);
            String absolutePath = null;
            try {
                absolutePath = propFile.getCanonicalPath();
            } catch (IOException e) {
                absolutePath = propFile.getAbsolutePath();
            }
            // 首先尝试读取内存中的值
            prop = repository.get(absolutePath);
            if (prop != null) {
                return prop.getProperty(attribute);
            } else {
                // 加载资源文件
                prop = loadExternalProperties(absolutePath);
                if (prop != null) {
                    repository.put(absolutePath, prop);
                    return prop.getProperty(attribute);
                }
            }
        }
        path = path.replaceAll("\\\\", "/");
        String jarPath = path.startsWith("/") ? path : "/" + path;
        prop = repository.get("jar:" + jarPath);
        if (prop != null) {
            return prop.getProperty(attribute);
        } else {
            prop = loadInternalProperties(jarPath);
            if (prop != null) {
                repository.put("jar:" + jarPath, prop);
                return prop.getProperty(attribute);
            }
        }
        return null;
    }

    public static String getProperty(String path, String attribute) {
        return getProperty(path, attribute, false);
    }

    /**
     * 更新属性值，同时会写入到外部资源文件中
     *
     * @param path
     * @param attribute
     * @param value
     */
    public synchronized static void setProperty(String path, String attribute, String value) {
        File propFile = new File(path);
        String absolutePath = null;
        try {
            absolutePath = propFile.getCanonicalPath();
        } catch (IOException e) {
            absolutePath = propFile.getAbsolutePath();
        }
        // 尝试读取内存中的值
        Properties prop = repository.get(absolutePath);
        if (prop == null) {
            // 如果还没有读取到内存，则需要先读出值
            prop = loadExternalProperties(absolutePath);
        }
        if (prop == null) {
            System.out.println("setProperty:" + path + " " + attribute + " " + value + "  失败");
            return;
        }
        prop.setProperty(attribute, value);
        // 同时修改外部文件
        try {
            prop.store(new FileOutputStream(propFile), "Modify Attribute:" + attribute + "\r\nLast Modified:" + new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载外部资源文件
     *
     * @param filePath
     * @return
     */
    private static synchronized Properties loadExternalProperties(String filePath) {
        InputStream in = null;
        try {
            Properties prop = new Properties();
            in = new FileInputStream(new File(filePath));
            prop.load(in);
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 加载jar包中的资源文件
     *
     * @param jarPath
     * @return
     */
    private static synchronized Properties loadInternalProperties(String jarPath) {
        InputStream in = null;
        try {
            in = com.feeye.util.PropertiesUtils.class.getResourceAsStream(jarPath);
            Properties prop = new Properties();
            prop.load(in);
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String getPropertiesValue(String properties, String key) {
        Properties prop = new Properties();
        InputStream in = com.feeye.util.PropertiesUtils.class.getResourceAsStream("/" + properties + ".properties");
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);// 关闭流
        }
        String url = (String) prop.get(key);
        return url;
    }

    public static String setPropertiesValue(String fileName, String properties, String key) {
        Properties prop = new Properties();
//        InputStream in = com.feeye.util.PropertiesUtils.class.getResourceAsStream("/" + properties + ".properties");
        OutputStream fos =  null;
        try {
           String path = PropertiesUtils.class.getClass().getClassLoader().getResource("/").getPath();
            new FileOutputStream(path);
            prop.store(fos,"setKey");
//            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);// 关闭流
        }
        String url = (String) prop.get(key);
        return url;
    }


    public static void main(String[] args) {
        PropertiesUtils.setPropertiesValue("config.properties","fingerid","1111");
    }
}

