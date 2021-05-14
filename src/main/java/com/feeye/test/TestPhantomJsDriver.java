/*
package com.feeye.test;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Set;

public class TestPhantomJsDriver {
	public static PhantomJSDriver getPhantomJSDriver(){
        //设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", false);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        //驱动支持
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,"C:\\chromedriver\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");

        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        return  driver;
    }

    public static void main(String[] args) {
        WebDriver driver=getPhantomJSDriver();
        driver.get("http://www.tianjin-air.com");
        Set<Cookie> cookies = driver.manage().getCookies();
//        System.out.println(driver.getPageSource());
        System.out.println("GS首页cookie：" + cookies);

        driver.get("http://www.tianjin-air.com/flight/select.html");
        cookies = driver.manage().getCookies();
        System.out.println("查询航班页面cookie：" + cookies);
        System.out.println(driver.getPageSource());
    }
}
*/
