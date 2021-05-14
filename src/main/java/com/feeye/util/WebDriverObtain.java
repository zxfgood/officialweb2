package com.feeye.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.io.IOException;

public class WebDriverObtain {
	private static ChromeDriverService service;

	// public static WebDriver getfixfoxdriver() {
	// //-headless
	// FirefoxOptions firefoxOptions = new FirefoxOptions();
	// //设置无头启动模式
	// // firefoxOptions.addArguments("-headless");
	// System.setProperty("webdriver.firefox.bin", "D:\\Mozilla
	// Firefox\\firefox.exe");
	// System.setProperty("webdriver.gecko.driver",
	// "C:\\Users\\hasee\\Desktop\\lean\\selenium\\geckodriver.exe");
	// // 创建一个 ChromeDriver 的接口，用于连接 Chrome（chromedriver.exe
	// 的路径可以任意放置，只要在newFile（）的时候写入你放的路径即可）
	//// FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxOptions);
	// // 创建一个 Chrome 的浏览器实例
	//// return firefoxDriver;
	// }

	// public static PhantomJSDriver getPhantomJSDriver() {
	// //设置必要参数
	// DesiredCapabilities dcaps = new DesiredCapabilities();
	// //ssl证书支持
	// dcaps.setCapability("acceptSslCerts", true);
	// //截屏支持
	// dcaps.setCapability("takesScreenshot", false);
	// //css搜索支持
	// dcaps.setCapability("cssSelectorsEnabled", true);
	// //不加载图片
	// dcaps.setCapability("phantomjs.page.settings.loadImages", "false");
	// //js支持
	// dcaps.setJavascriptEnabled(true);
	// //设置代理
	// dcaps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0
	// (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)
	// Chrome/68.0.3440.106 Safari/537.36");
	// //驱动支持
	// dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
	// "C:\\Users\\hasee\\Desktop\\lean\\selenium\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
	//
	// PhantomJSDriver driver = new PhantomJSDriver(dcaps);
	// return driver;
	// }

	/*
	 * public static WebDriver getChromeDriver() throws IOException {
	 * System.setProperty("webdriver.chorm.driver",
	 * "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"); //
	 * 创建一个 ChromeDriver 的接口，用于连接 Chrome（chromedriver.exe
	 * 的路径可以任意放置，只要在newFile（）的时候写入你放的路径即可） service = new
	 * ChromeDriverService.Builder().usingDriverExecutable(new
	 * File("C:\\Users\\hasee\\Desktop\\lean\\selenium\\chromedriver.exe")).
	 * usingAnyFreePort().build(); service.start(); // 创建一个 Chrome 的浏览器实例 return
	 * new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome()); }
	 */

	public static WebDriver getGeckoDriver(String ip_port) {
		WebDriver driver = null;
		try {
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			System.setProperty("webdriver.firefox.bin", "F:\\Firefox\\firefox.exe");
//
//			// 指定firefox.exe插件的位置
			System.setProperty("webdriver.gecko.driver", "C:\\chromedriver\\geckodriver.exe");
//			
			firefoxOptions.addArguments(
					"--user-Agent=Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36");
//			// FirefoxOptions.addArguments("--window-size=1920,1080");
//			 firefoxOptions.addArguments("--headless");
//
//			String ip_port = DailiyunService.getRandomIp(50);
			if(StringUtils.isNotEmpty(ip_port)){
				String proxyIp = ip_port.split(":")[0];
				int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
	//
				FirefoxProfile profile = new FirefoxProfile();
				// 使用代理
				profile.setPreference("network.proxy.type", 1);
				// http协议代理配置
				profile.setPreference("network.proxy.http", proxyIp);
				profile.setPreference("network.proxy.http_port", proxyPort);
				profile.setPreference("network.proxy.ssl", proxyIp);
				profile.setPreference("network.proxy.ssl_port", proxyPort);
				profile.setPreference("network.proxy.ftp", proxyIp);
				profile.setPreference("network.proxy.ftp_port", proxyPort);
				profile.setPreference("network.proxy.socks", proxyIp);
				profile.setPreference("network.proxy.socks_port", proxyPort);
	
				// 所有协议公用一种代理配置，如果单独配置，这项设置为false，再类似于http的配置
				profile.setPreference("network.proxy.share_proxy_settings", true);
	
				// 对于localhost的不用代理，这里必须要配置，否则无法和webdriver通讯
				profile.setPreference("network.proxy.no_proxies_on", "localhost");
	
				firefoxOptions.setProfile(profile);
			}
			driver = new FirefoxDriver(firefoxOptions);
		} catch (Exception e) {
			e.printStackTrace();
			if (driver != null) {
				driver.quit();
			}
		}
		return driver;

	}

	public static WebDriver getChromeDriver() {
		WebDriver driver = null;
		try {
			System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");
			// System.setProperty("webdriver.chrome.driver",
			// "C:\\Users\\hasee\\Desktop\\lean\\selenium\\chormdirver\\2.17\\chromedriver.exe");
			ChromeOptions chromeOptions = new ChromeOptions();
			// 设置为 headless 模式 （必须）
			// chromeOptions.addArguments("--headless");
			// 禁用沙盒
			chromeOptions.addArguments("no-sandbox");
			chromeOptions.addArguments("--window-size=1920,1080");
			// l浏览器语言
			chromeOptions.addArguments("--lang=" + "zh-CN");
			// 设置忽略 Chrome 浏览器证书错误报警提示
			chromeOptions.addArguments("--test-type", "--ignore-certificate-errors");

			chromeOptions.addArguments(
					"--user-Agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.105 Safari/537.37");
			// chromeOptions.addArguments("Cache-Control", "");
			// //禁止图片
			// Map<String, Object> prefs = new HashMap<String, Object>();
			// prefs.put("profile.managed_default_content_settings.images", 2);
			// chromeOptions.setExperimentalOption("prefs", prefs);
			driver = new ChromeDriver(chromeOptions);

		} catch (Exception e) {
			e.printStackTrace();
			if (driver != null) {
				driver.quit();
			}
		}
		return driver;
	}

	public static void takeScreenShotChromeFirefox(WebDriver driver, String name) {
		String screenshootDir = "D:/image";
		File output = null;
		File file;
		output = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		file = new File(screenshootDir, name + ".png");
		try {
			FileUtils.copyFile(output, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
