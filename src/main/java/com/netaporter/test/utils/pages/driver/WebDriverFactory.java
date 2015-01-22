package com.netaporter.test.utils.pages.driver;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Create browser profiles
 */
public class WebDriverFactory {
    static Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);
    private static final String DRIVER_FIREFOX = "firefox";
    private static final String DRIVER_CHROME = "chrome";
    private static final String DRIVER_IOS = "ios";
    private static final String DRIVER_APPIUM = "appium";
    private static final String DRIVER_SAFARI = "safari";
    //Default PAC url - override from configuration property files
    private static final String PROXY_PAC =  "http://wpad.london.net-a-porter.com/wpad-dev.dat";

    private @Value("${userAgentOverride}") String userAgentOverride;
    private @Value("${chromeOptions}") String chromeOptions;
    private @Value("${chromePreferences}") String chromePreferences;
    private @Value("${proxyURL}") String proxyURL;
    private @Value("${proxyPAC}") String proxyPAC;
    private @Value("${deviceName}") String deviceName;
    private @Value("${platformName}") String platformName;
    private @Value("${platformVersion}") String platformVersion;
    private @Value("${browserName}") String browserName;
    private @Value("${avd}") String avd;
    private @Value("${app}") String app;
    private @Value("${udid}") String udid;

    private static WebDriver driver;

    private String driverName;
    private boolean withProxy;

    public WebDriverFactory(String driverName, boolean withProxy) {
        this.driverName = driverName.trim();
        this.withProxy = withProxy;
    }

    public synchronized WebDriver getDriver() {
        return getDriver(null);
    }
    public synchronized void resetDriver() {
        driver = null;
    }

    public synchronized WebDriver getDriver(String startPage) {
        if (driver == null) {
            if (driverName.equalsIgnoreCase(DRIVER_FIREFOX)) {
                driver = createFirefoxDriver(withProxy);
           }
            else if(driverName.equalsIgnoreCase(DRIVER_APPIUM)){
                    driver = createAppiumDriver(withProxy);
            }
            else if(driverName.equalsIgnoreCase(DRIVER_SAFARI)){
                driver = createSafariDriver(withProxy);
            } else {
                // Default to Chrome
                driver = createChromeDriver(withProxy);
            }

            // Goto default start page if specified
            if(startPage != null) {
                driver.get(startPage);
            }
        }

        return driver;
    }

    private WebDriver createAppiumDriver(boolean withProxy){
        logger.debug("Creating Appium driver");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        if(platformName != null && !platformName.equals("${platformName}")){
            capabilities.setCapability("platformName", platformName);
        } else{
            capabilities.setCapability("platformName", "iOS");
        }
        if(deviceName !=null && !deviceName.equals("${deviceName}")){
            capabilities.setCapability("deviceName",deviceName);
        } else{
            capabilities.setCapability("deviceName", "iPhone Simulator");
        }
        if(platformVersion !=null && !platformVersion.equals("${platformVersion}")){
            capabilities.setCapability("platformVersion",platformVersion);
        } else{
            capabilities.setCapability("platformVersion", "7.1");
        }
        if(browserName !=null && !browserName.equals("${browserName}")){
            capabilities.setCapability("browserName",browserName);
        } else{
            capabilities.setCapability("browserName", "Safari");
        }
        //iOS Only
        //Auto accept alerts
        if(capabilities.getCapability("platformName").equals("iOS")){
           capabilities.setCapability("autoAcceptAlerts", true);
        }
        //Android Only
        if(capabilities.getCapability("platformName").equals("Android")){
            if(avd != null && !avd.equals("${avd}")){
               capabilities.setCapability("avd", avd.startsWith("@")?avd:"@"+avd);
            }

        }
        //Apps
        if(app !=null && !app.equals("${app}")){
            capabilities.setCapability("app", app);
            capabilities.setBrowserName("");
        }
        if(udid!=null && !udid.equals("${udid}")){
            capabilities.setCapability("udid", udid);
        }


        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"),capabilities);
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        logger.info("Created Appium webdriver with capabilities: " + capabilities.toString());
        driver = new Augmenter().augment(driver);
        return driver;
    }

    public void quit() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public boolean isOpen() {
        return driver != null;
    }

    private FirefoxDriver createFirefoxDriver(boolean withProxy) {
        // TODO support userAgentOverride for Firefox if we're still using this?
        logger.debug("Creating FirefoxDriver");
        FirefoxDriver driver;
        if (withProxy) {
            logger.debug("Setting up Firefox profile with proxy");
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("network.proxy.type", 2);
            //If autoconfig PAC url is provided - use it for configuration
            if(proxyPAC !=null && !proxyPAC.equals("${proxyPAC}")){
                profile.setPreference("network.proxy.autoconfig_url",proxyPAC);
                logger.debug("Setting proxy preference: network.proxy.autoconfig_url :" + proxyPAC);
            }
            //if no autoconfig - try to use proxy url
            else  if(proxyURL !=null && !proxyURL.equals("${proxyURL")){
                   try{
                       String[] proxy = proxyURL.split(":");
                       if(proxy.length != 2){
                           throw new MalformedURLException("Proxy url may be malformed: " + proxyURL + "\n should be URL:PORT");
                       }
                       profile.setPreference("network.proxy.type", 1);
                       logger.debug("Setting proxy preference: network.proxy.type :" + 1);
                       profile.setPreference("network.proxy.http", proxy[0]);
                       logger.debug("Setting proxy preference: network.proxy.http :" + proxy[0]);
                       profile.setPreference("network.proxy.http_port", Integer.valueOf(proxy[1]).intValue());
                       logger.debug("Setting proxy preference: network.proxy.http_port :" + Integer.valueOf(proxy[1]).intValue());
                       profile.setPreference("network.proxy.ssl", proxy[0]);
                       logger.debug(("Setting proxy preference: network.proxy.ssl :" + proxy[0]));
                       profile.setPreference("network.proxy.ssl_port", Integer.valueOf(proxy[1]).intValue());
                       logger.debug(("Setting proxy preference: network.proxy.ssl_port :" + Integer.valueOf(proxy[1]).intValue()));
                   }catch (MalformedURLException e){
                       e.printStackTrace();
                   }
                }
            else{
             // no PAC or proxy url proveded - use default PAC
                profile.setPreference("network.proxy.autoconfig_url",PROXY_PAC);
                logger.debug("Setting proxy preference: network.proxy.autoconfig_url :" + PROXY_PAC);
            }
            driver = new FirefoxDriver(profile);
            driver.manage().window().setSize(new Dimension(1024, 768));
        } else {
            driver = new FirefoxDriver();
        }
        logger.info("FirefoxDriver created with settings: " + driver.getCapabilities().toString());
        return driver;
    }

    private SafariDriver createSafariDriver(boolean withProxy) {
        logger.debug("Creating SafariDriver");
        SafariDriver driver;
        if (withProxy) {
            //TODO: add functionality for proxy
            driver = new SafariDriver();
        }
        else
         {
            driver = new SafariDriver();
        }
        logger.info("Safari driver created with settings: " + driver.getCapabilities().toString());
        return driver;
    }

    private ChromeDriver createChromeDriver(boolean withProxy) {
        logger.debug("Creating ChromeDriver");
        ChromeDriver driver;
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<String, Object>();
      //  prefs.put("intl.accept_languages", "en,en-US");

        if(chromePreferences !=null && !chromePreferences.equals("${chromePreferences}")){
            List<String> p = Arrays.asList(chromePreferences.split("\\s*;\\s*"));
            for(String pr:p){
                Object[]pair = pr.split("\\s*=\\s*");
                Integer convert= null;
                try{
                    convert = Integer.parseInt(pair[1].toString());
                }catch(NumberFormatException nfe){

                }
                prefs.put(pair[0].toString(), convert == null? pair[1]:convert);
            }
        }
        if(prefs!=null)options.setExperimentalOption("prefs", prefs);
        if(chromeOptions !=null && !chromeOptions.equals("${chromeOptions}")){
            List<String> opts = Arrays.asList(chromeOptions.split("\\s*;\\s*"));
            options.addArguments(opts);              }
        String binaryLocation = System.getProperty("chrome.binary.location");
        if(!StringUtils.isBlank(binaryLocation)) {
            options.setBinary(new File(binaryLocation));
        }

       if (userAgentOverride != null & !userAgentOverride.equals("${userAgentOverride}")) {
            options.addArguments("--user-agent='" + userAgentOverride + "'");
        }

        if (withProxy) {
                        //If autoconfig PAC url is provided - use it for configuration
            if(proxyPAC !=null && !proxyPAC.equals("${proxyPAC}")){
                options.addArguments("--proxy-pac-url=" + proxyPAC);
            }
            //if no autoconfig - try to use proxy url
            else if(proxyURL !=null && !proxyURL.equals("${proxyURL}")){
                options.addArguments("--proxy-server="+proxyURL);
                }
            else {
                // no PAC or proxy url proveded - use default PAC
                options.addArguments("--proxy-pac-url=" + PROXY_PAC);
            }
        }
        try {
            logger.info("ChromeDriver created with options: " + options.toJson().toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver = new ChromeDriver(options);
       // driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        return driver;
    }
    public String getUserAgentOverride() {
        return userAgentOverride;
    }

    public void setUserAgentOverride(String userAgentOverride) {
        logger.debug("Setting userAgentOverride to " + userAgentOverride);
        this.userAgentOverride = userAgentOverride;
    }
}
