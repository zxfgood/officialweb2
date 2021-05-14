package com.feeye.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author: zcf
 * @Date: 2018/7/19 18 20
 * @Description:
 **/
public class PhantomjsDriverUtil {
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsDriverUtil.class);

    static {
        System.setProperty("phantomjs.binary.path", "C:\\chromedriver\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
    }

    public static WebDriver getWebDriver() {
    	 WebDriver driver = null;
    	try {
    		 DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
    	     desiredCapabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
    	     driver = new PhantomJSDriver(desiredCapabilities);
		} catch (Exception e) {
			logger.error("error",e);
			if(driver!=null){
				driver.quit();
			}
		} 
       
        return driver;
    }

    public static WebDriver getProxyWebDriver(String proxyip, String proxyport, String proxyusername, String proxypassword) {
        String ipandPort = proxyip + ":" + proxyport;
        ArrayList<String> proxyarrs = new ArrayList<String>();
        String auth = proxyusername + ":" + proxypassword;
        proxyarrs.add("--proxy=" + ipandPort);
        proxyarrs.add("--proxy-auth=" + auth);
        proxyarrs.add("--proxy-type=http");
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
        desiredCapabilities.setCapability("phantomjs.page.settings.loadImages", true);
        desiredCapabilities.setCapability("cssSelectorsEnabled", false);
        desiredCapabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, proxyarrs);
        WebDriver driver = new PhantomJSDriver(desiredCapabilities);
        return driver;
    }


    public static  WebDriver getChromeDriver(String proxyip,String port,String Proxyusername,String proxypassword){
        ChromeOptions chromeOptions=new ChromeOptions();
        //代理设置文件路径
        chromeOptions.addExtensions(new File("C:\\Users\\Administrator\\Desktop\\zcf\\selenium\\proxy.zip"));
        //chromeDriver驱动路径
        System.setProperty("webdriver.chrome.driver","C:\\chromedriver\\chromedriver.exe");
        //chromeOptions.addArguments("--headless");
        //禁用沙箱
        chromeOptions.addArguments("--test-type --no-sandbox");
        chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
        //禁止加载图片
        /*Map<String,Object> prefs=new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images","2");
        chromeOptions.setExperimentalOption("prefs",prefs);*/
        WebDriver webDriver=new ChromeDriver(chromeOptions);
        return webDriver;
    }

    public static WebDriver getChormDriver(){
        WebDriver  webDriver=null;
        return webDriver;
    }

    public static  String getSecuritycode(WebDriver webDriver, WebElement ele) {
        File screenshotLocation = null;
        File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        // Get entire page screenshot
        BufferedImage fullImg = null;
        try {
            fullImg = ImageIO.read(screenshot);
        } catch (IOException e) {
            logger.error("获取验证码图片错误", e);
            return null;
        }
        // Get the location of element on the page
        Point point = ele.getLocation();
        // Get width and height of the element
        int eleWidth = ele.getSize().getWidth();
        int eleHeight = ele.getSize().getHeight();
        // Crop the entire page screenshot to get only element screenshot
        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
        try {
            ImageIO.write(eleScreenshot, "jpg", screenshot);
        } catch (IOException e) {
            logger.error("图片格式转换错误", e);
            return null;
        }
        // Copy the element screenshot to disk
        //以日期新建文件夹  以时分秒毫秒作为文件名
//        LocalDate localDate = LocalDate.now();
        String filepath = "C:/imacode/";
        File directory = new File(filepath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        filepath = filepath + File.separatorChar + System.currentTimeMillis() + ".jpg";
        screenshotLocation = new File(filepath);
        try {
            FileUtils.copyFile(screenshot, screenshotLocation);
        } catch (IOException e) {
            logger.error("图片保存为文件异常", e);
        }

        return filepath;
    }

    public static void takeScreenShotChromeFirefox(WebDriver webDriver, String name) {
        String screenshootDir = "C:/image";
        File output = null;
        File file;
        output = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        file = new File(screenshootDir, name + ".png");
        try {
            FileUtils.copyFile(output, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebElement getWebElement(WebDriver webDriver, String tagname, String xpath, String classname, String id) {
        WebElement webElement = null;
        try {
            if (StringUtils.isNotEmpty(tagname)) {
                webElement = webDriver.findElement(By.tagName(tagname));
            } else if (StringUtils.isNotEmpty(xpath)) {
                webElement = webDriver.findElement(By.xpath(xpath));
            } else if (StringUtils.isNotEmpty(classname)) {
                webElement = webDriver.findElement(By.className(classname));
            } else if (StringUtils.isNotEmpty(id)) {
                webElement = webDriver.findElement(By.id(id));
            }
        } catch (NoSuchElementException e) {
            logger.error("没有找到此元素", e);
        }
        return webElement;
    }


}
