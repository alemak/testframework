package com.netaporter.test.utils.pages.driver;

import com.google.common.base.Predicate;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import com.netaporter.test.utils.enums.WebsiteEnum;
import com.netaporter.test.utils.factories.TestCardFactory;
import com.netaporter.test.utils.network.NetworkUtil;
import com.netaporter.test.utils.pages.IPage;
import com.netaporter.test.utils.pages.exceptions.CurrentPageUnknownException;
import com.netaporter.test.utils.pages.exceptions.PageElementNotFoundException;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.*;
import static org.openqa.selenium.OutputType.BYTES;

/**
 * Encapsulate interaction with the Selenium webdriver
 */
@Component
@Scope("cucumber-glue")
public final class WebDriverUtil {
    static Logger logger  = LoggerFactory.getLogger(WebDriverUtil.class);
    public static class WebDriverUtilException extends Exception{
        public WebDriverUtilException(String message){
            super(message);
        }
    }

    /**
     * Factory to provide the required webdriver implementation (Chrome, Firefox, Safari, Appium)
     */
    @Autowired
    private WebDriverFactory webDriverFactory;
    /**
     * Factory to provide the credit cards for tests
     */
    @Autowired
    private TestCardFactory testCardFactory;

    private Wait wait;
    /**
     * Base URL (starting page for web testing)
     */
    @Value("#{webDriverUtilConfig.baseUrl}")
    private String baseUrl;
    /**
     * Brand value (NAP, OUT, MrP)
     */
    @Value("#{webDriverUtilConfig.brand}")
    private String brand;

    /**
     * Sales channel (value of {@link com.netaporter.test.utils.enums.SalesChannelEnum})
     */
    @Value("#{webDriverUtilConfig.channel}")
    private String channel;

    /**
     * Region (value of {@link com.netaporter.test.utils.enums.RegionEnum})
     * */
    @Value("#{webDriverUtilConfig.region}")
    private String region;
    /**
     * Language (used in the page URL)
     */
    @Value("#{webDriverUtilConfig.language}")
    private String language;

     /**
     * Country (used in the page URL)
     * */
    @Value("#{webDriverUtilConfig.country}")
    private String country;

    /**
     * If set to true - the webdriver will start with a blank page as opposed to opening baseUrl
     */
    @Value("#{webDriverUtilConfig.disableLoadingDefaultHome}")
    private boolean disableLoadingDefaultHome;


    /**
     * If set - the webdriver will start with this URL, not the baseURL
     */
    @Value("#{webDriverUtilConfig.firstPageLoadedOverrideURL}")
    private String firstPageLoadedOverrideURL;

    /**
     * Keeps track of the current page during the test execution
     */
    public IPage currentPage;

    private String originalWindowHandle;

    /**
     * Initialise the wait after the spring bean creation
     */
    @PostConstruct
    public void init(){
        wait = new Wait(getDriver());
    }

    /**
     * Select a random non-selected element from checkboxes, options in a select or radio buttons
     * Returns the text of the element, does not select it
     */
    public String selectRandomOption(By optionElement) {
        List<WebElement> options = getSelectOptions(optionElement);
        List<String> optionsText = new ArrayList<String>();
        for(WebElement option: options){
            if(!option.isSelected()) {
                optionsText.add(option.getText());
            }
        }

        if(optionsText.isEmpty()) {
            throw new RuntimeException("No other options available");
        }

        Collections.shuffle(optionsText);
        return optionsText.get(0);
    }


    public String getRegion(){
            return region;
    }
    /**
     * Returns region as RegionEnum
     */
    public RegionEnum getRegionEnum(){
        return getRegion().equals(null)? null: RegionEnum.valueOf(getRegion());
    }
    /**
     * Returns channel (e.g. NAP_INTL). If the channel is not set then tries to determine it from Brand and Region settings
     */
    public String getChannel() {
        if( channel == null) {
            //try to get the channel by brand and region
            if(brand != null && region != null) {
                return (SalesChannelEnum.getByWebsiteAndRegion(WebsiteEnum.valueOf(brand), RegionEnum.valueOf(region))).toString();
            }
        }
        return channel;
    }
    /**
     * Returns channel (e.g. NAP_INTL) as SalesChannelEnum
     */
    public SalesChannelEnum getChannelEnum(){
        return getChannel().equals(null)? null:SalesChannelEnum.valueOf(getChannel());
}

    public void setRegion(String region){
        logger.debug("Setting region to " + region);
        this.region = region;
    }
    public void setChannel(SalesChannelEnum channel) {
        logger.debug("Setting channel to " + channel);
        this.channel = channel.getName();
    }

    public String getLanguage() {
        if(language == null){
            if(region!=null){
                return RegionEnum.valueOf(region).getLanguage();
            }
            //default
            return "EN";
        }
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        if(country == null){
            if(region!=null){
                return RegionEnum.valueOf(region).getCountry();
            }
            //default
            return "GB";
        }
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFirstPageLoadedOverrideURL() {
        return firstPageLoadedOverrideURL;
    }

    public void setFirstPageLoadedOverrideURL(String firstPageLoadedOverrideURL) {
        this.firstPageLoadedOverrideURL = firstPageLoadedOverrideURL;
    }

    public boolean isDisableLoadingDefaultHome() {
        return disableLoadingDefaultHome;
    }

    public void setDisableLoadingDefaultHome(boolean disableLoadingDefaultHome) {
        this.disableLoadingDefaultHome = disableLoadingDefaultHome;
    }


    public void quit() {
        if (webDriverFactory.isOpen()) {
            logger.debug("Quitting webdriver");
            webDriverFactory.quit();
        }
    }
    /**
     * Deletes all cookies
     */
    public void clearCookies() {
        logger.debug("Deleting all cookies");
        getDriver().manage().deleteAllCookies();
    }
    /**
     * Deletes the named cookie
     */
    public void deleteCookieNamed(String cookieName) {

        Set<Cookie> cookies = getDriver().manage().getCookies();
        for (Cookie cookie : cookies ) {
            if(cookie.getName().equals(cookieName)) {
                getDriver().manage().deleteCookie(cookie);
                logger.info("The cookie called '" + cookieName + "' was deleted");
                return;
            }
        }

        fail("Could not find cookie called '" + cookieName + "'");
    }
    /**
     * Take a screenshot
     */
    public byte[] takeScreenShotAsBytes() {
        if (webDriverFactory.isOpen()) {
            logger.debug("Taking screenshot");
            return ((TakesScreenshot) getDriver()).getScreenshotAs(BYTES);
        }
        return null;
    }

    public void addCookie(Cookie cookie) {
        logger.debug("Adding cookie:"  + cookie.toString());
        getDriver().manage().addCookie(cookie);
    }
    /**
     * Adds a cookie with provided name and value
     * Domain is parsed from the baseUrl property
     */
    public void addCookie(String cookieName, String cookieValue) {
        if (cookieValue == null || cookieValue.equalsIgnoreCase("null")) {
            return;
        }
        String domain = "";
        try {
            domain = new URL(this.getBaseUrl()).getHost();
        } catch (MalformedURLException e) {
        }

        Cookie cookie = new Cookie(cookieName, cookieValue, domain, "/", DateUtils.addDays(new Date(), 1));
        getDriver().manage().addCookie(cookie);
    }

    /**
     * Moves mouse to the element and performs a click on the point specified by offset values
     *
     */
    public void clickAtPoint(WebElement element, Integer xOffset, Integer yOffset) {
        Actions build = new Actions(getDriver());
        build.moveToElement(element, xOffset, yOffset).click().build().perform();
    }

    /**
     * Go to the specified page (by page object path value)
     */
    public void goToPage(IPage page) {
        this.currentPage = page;
        String url = getURL(page.getPath());
        goToPage(url);
    }
    /**
     * Go to the specified page (parameters added)
     */
    public void goToPageWithParams(IPage page, String params) {
        this.currentPage = page;
        String url = getURL(page.getPath() + "?" + params);
        goToPage(url);
    }
    public void goToPageWithPathParams(IPage page, String params) {
        this.currentPage = page;
        String url = getURL(params + page.getPath());
        goToPage(url);
    }
    /**
     * Go to the specified page applying region(language/country) parameters specified by the
     * page's regionalisation behavior object
     */
    public void goToRegionalisedPage(IPage page) {
        this.currentPage = page;
        String url = page.getRegionalisedPath();
        goToPage(url);
    }
    /**
     * Go to the specified URL
     */
    private void goToPage(String url){
        logger.debug("Loading URL: " + url);
        getDriver().get(url);
    }

    public void goToRegionalisedPageWithParams(IPage page, String params) {
        this.currentPage = page;
        String url = page.getRegionalisedPath() + "?" + params;
        logger.debug("Loading URL: " + url);
        getDriver().get(url);
    }

    private String getURL(String path) {
        StringBuilder sb = new StringBuilder(getBaseUrl());
        sb.append(path);
        return sb.toString();
    }
    public void switchToIFrameWithIndex(int index) {
        getDriver().switchTo().frame(index);
        getDriver().switchTo().activeElement();
    }

    public void focusOnParentWindow() {
        getDriver().switchTo().window(getDriver().getWindowHandles().iterator().next());
    }

    public void switchToIFrame(By element) {
        getDriver().switchTo().frame(findElement(element));
        getDriver().switchTo().activeElement();
    }

	public void switchToWindow(String name) {
		originalWindowHandle = getDriver().getWindowHandle();
        getDriver().switchTo().window(name);
		//webDriver.switchTo().activeElement();
	}

    public void showElement(WebElement webElement) {
        ((JavascriptExecutor) getDriver()).executeScript("$(arguments[0]).show();", webElement);
    }

    public WebElement findElement(By locator) throws PageElementNotFoundException {
       return findElement(locator, WaitTime.DEFAULT);
    }

    public WebElement findElement(By locator, WaitTime timeout) throws PageElementNotFoundException {
        WebElement we = getElement(locator,timeout);
        if(we != null){
            return we;
        }
        else{
            throw new PageElementNotFoundException("Could not find element " + locator.toString() + " on page");
        }
    }
    public WebElement findElement(By locator, int timeout) throws PageElementNotFoundException {
        WebElement we = getElement(locator,timeout);
        if(we != null){
            return we;
        }
        else{
            throw new PageElementNotFoundException("Could not find element " + locator.toString() + " on page");
        }
    }
    public List<WebElement> getSelectOptions(By element) {
        return new Select(findElement(element)).getOptions();
    }


    public String getSelectedOption(By element) {
        for(WebElement cardDropDownOption: getSelectOptions(element)){
            if(cardDropDownOption.isSelected()) {
                return cardDropDownOption.getText();
            }
        }

        throw new RuntimeException("No drop down option selected");
    }
    public List<WebElement> findElements(By locator) throws PageElementNotFoundException {
        return findElements(locator, WaitTime.DEFAULT);
    }
    public List<WebElement> findElements(By locator, WaitTime timeout) throws PageElementNotFoundException {
       List<WebElement> we = getElements(locator, timeout);
        if(we !=null){
            return we;
        }
        else{
            throw new PageElementNotFoundException("Could not find element " + locator.toString() + " on page");
        }
    }
    public List<WebElement> findElements(By locator, int timeout) throws PageElementNotFoundException {
        List<WebElement> we = getElements(locator, timeout);
        if(we !=null){
            return we;
        }
        else{
            throw new PageElementNotFoundException("Could not find element " + locator.toString() + " on page");
        }
    }

    public void clearElements(By... elements) {
        for (By element : elements) {
            findElement(element).clear();
        }
    }
    public void setText(By element, CharSequence charSequence) {
        clearElements(element);
        findElement(element).sendKeys(charSequence);
    }
    public void selectByText(By element, String text) {
        new Select(findElement(element)).selectByVisibleText(text);
    }

    public boolean isCurrentPage(IPage page) {
        try {
            String urlWithoutQueryString = URLDecoder.decode(getDriver().getCurrentUrl(), "UTF-8");
            if (urlWithoutQueryString.contains("?")) {
                urlWithoutQueryString = urlWithoutQueryString.substring(0, urlWithoutQueryString.indexOf("?"));
            }
            if (urlWithoutQueryString.contains(";")) {
                urlWithoutQueryString = urlWithoutQueryString.substring(0, urlWithoutQueryString.indexOf(";"));
            }

            if (urlWithoutQueryString.contains("/Shop/")) {
                // For URLs with Shop in the path
                return urlWithoutQueryString.endsWith(page.getPath());
            } else {
                // For non-Shop URLs eg. /signinpurchasepath, /purchasepath would both pass with a simple URL.endsWith()
                // so we only take the very last part of the path and check for equality
                return urlWithoutQueryString.substring(urlWithoutQueryString.lastIndexOf("/")+1, urlWithoutQueryString.length()).equalsIgnoreCase(page.getPath());
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean isCurrentPageWithParams(IPage page, String... nameAndValList) {
        boolean isCurrentPage = isCurrentPage(page);

        if (isCurrentPage) {
            List<BasicNameValuePair> expectedParams = new ArrayList<BasicNameValuePair>();
            for (int i = 0; i < nameAndValList.length; i += 2) {
                expectedParams.add(new BasicNameValuePair(nameAndValList[i], nameAndValList[i + 1]));
            }

            try {
                List<NameValuePair> actualParams = URLEncodedUtils.parse(new URI(getDriver().getCurrentUrl()), "UTF-8");
                for (NameValuePair expectedParam : expectedParams) {
                    if (!actualParams.contains(expectedParam)) {
                        isCurrentPage = false;
                        break;
                    }
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

        }

        return isCurrentPage;
    }

    /**
     * The current page, this is the page that was most recently navigated. In some cases if this is invoked
     * before a page is navigated to the currentPage may be null and an {@link CurrentPageUnknownException} is thrown.
     * @return currentPage
     * @throws CurrentPageUnknownException
     */
    public IPage getCurrentPage() throws CurrentPageUnknownException {
        if (currentPage == null) {
            throw new CurrentPageUnknownException("The current page is not known. Have you navigated to the page before invoking a function on it?");
        }
        return currentPage;
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    public boolean isElementPresent(By element) {
        try {
            findElement(element);
            return true;
        } catch (PageElementNotFoundException exception) {
            return false;
        }
    }

    public boolean isElementPresent(By element, int waitForSeconds) {
        try {
            findElement(element, waitForSeconds);
            return true;
        } catch (PageElementNotFoundException exception) {
            return false;
        }
    }


    public boolean currentUrlContains(String expectedText) {
        return getDriver().getCurrentUrl().contains(expectedText);
    }

    // TODO inspecting the whole page source for a small bit of data is an anti-pattern! Think about getting rid of this.
    public String getPageSource() {
        return getDriver().getPageSource();
    }

    public String getUserAgent(){
        return (String)((JavascriptExecutor)getDriver()).executeScript("return navigator.userAgent;");
    }

    public Cookie getCookie(String cookie) {
        return getDriver().manage().getCookieNamed(cookie);
    }

    public Set<Cookie> getCookies() {
        return getDriver().manage().getCookies();
    }

    public String getTitle() {
        return getDriver().getTitle();
    }

    public String getBaseUrl() {
        if (baseUrl == null) {
            InetAddress address = NetworkUtil.getFirstNonLoopbackAddress(true, false);
            baseUrl = address.getHostAddress();
        }

        return baseUrl.startsWith("http://")||baseUrl.startsWith("https://") ? baseUrl : "http://" + baseUrl;
    }

    public void setBaseUrl(String url){
        baseUrl = url;
    }

    public SalesChannelEnum getSalesChannelByBrandAndRegion(){
        return SalesChannelEnum.getByWebsiteAndRegion(WebsiteEnum.valueOf(getBrand()), RegionEnum.valueOf(getRegion()));
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void waitForElementToDisappear(By element) {
        waitUntil(elementNotPresent(element));
    }

    public void waitForElementToDisappear(By element, int timeOut) {
        waitUntil(elementNotPresent(element), timeOut);
    }

    private Predicate<WebDriver> elementNotPresent(final By element) {
        return new Predicate<WebDriver>() {
            @Override public boolean apply(WebDriver driver) {
                return !isElementPresent(element,3);
            }
        };
    }


    private WebDriverWait getWebDriverWait(int timeOut) {

        return new WebDriverWait(getDriver(), timeOut);
    }

    public void waitUntil(Predicate<WebDriver> untilPredicate){
        waitUntil(untilPredicate, 10);
    }

    public void waitUntil(Predicate<WebDriver> untilPredicate, int maxSecondsWait){
        WebDriverWait wait = new WebDriverWait(getDriver(), maxSecondsWait); //Ten seconds max wait
        wait.until(untilPredicate);
    }

    /**
     * This assumes there is only another window... Maybe this method could be perfectioned a little :)
     * @see #switchToWindow(String)
     */
    public void focusToOtherWindow() throws WebDriverUtilException {

        String currentWindowHandle = getDriver().getWindowHandle();

        Set<String> handles = getDriver().getWindowHandles();
        for (String handle : handles){
            if (!handle.equals(currentWindowHandle)){
                getDriver().switchTo().window(handle);
                return;
            }
        }
        //Didn't find any other window other than the current one.
        throw new WebDriverUtilException("Error: no other windows besides the current one");
    }

    public void setCurrentPage(IPage page){
        this.currentPage = page;
    }


    public void reload() {
        getDriver().navigate().refresh();
    }

    public void navigateForward() {
        getDriver().navigate().forward();
    }

    public void navigateBack()  {
        getDriver().navigate().back();
    }

    public void maximizeWindow()  {
        getDriver().manage().window().maximize();
    }

    public void switchToPopUpWindow(String elementOnPopUpPageId) {
        originalWindowHandle = getDriver().getWindowHandle();
        for (String selectedWindow: getDriver().getWindowHandles()){
            if(!selectedWindow.contains(originalWindowHandle)){
                //Switch to a popup window
                getDriver().switchTo().window(selectedWindow);
                //check that its the popup window we are looking for
                try{
                    getDriver().findElement(By.id(elementOnPopUpPageId));
                }catch (NoSuchElementException nsee)  {
                    throw new CurrentPageUnknownException("Popup window could not be found", nsee);
                }
            }
        }
    }

    public void closePopUpWindow() {
        getDriver().switchTo().window(originalWindowHandle);
    }

    public void mouseOver(WebElement webElement) {
        Actions actions = new Actions(getDriver());

        actions.moveToElement(webElement).perform();
    }





    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //
    // Asserts
    //
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    public void assertTextPresent(By element, String expectedText) {
        WebElement webElement = findElement(element);

        String lowerCaseExpectedText = expectedText.toLowerCase();
        String lowerCaseElementText = webElement.getText().toLowerCase();
        assertTrue("Expecting element " + element + " to contain: " + lowerCaseExpectedText + " but it contained: " + lowerCaseElementText, lowerCaseElementText.contains(lowerCaseExpectedText));

        // Should be displayed too
        assertTrue(webElement.isDisplayed());
    }

    public void assertElementsExist(By... elements) {
        for (By element : elements) {
            assertTrue("Element " + element + " should exist", findElements(element).size() > 0);
        }
    }
    public void assertElementsDoNotExist(By... elements) {
        for (By element : elements) {
            assertFalse("Element " + element + " should not exist", findElements(element).size() > 0);
        }
    }

   public void assertTagPresentInSource(String... tagExpressions) {
        String viewSource = getPageSource();

        for(String regex:tagExpressions){
            Assert.assertTrue("Could not match CM tag in source : " + regex, viewSource.contains(regex));
        }
    }

    public boolean exists(WebElement element, By selector){
        List<WebElement> found = null;
        if (element != null)
            found = element.findElements(selector);
        else
            found = getDriver().findElements(selector);
        if (found == null || found.size() == 0)
            return false;
        return true;
    }

    public boolean hasFocus(RemoteWebElement element) {
        // Get focussed element
        RemoteWebElement focusedElement = (RemoteWebElement) ((JavascriptExecutor) getDriver()).executeScript("return document.activeElement");
        return element.getId().equals(focusedElement.getId());
    }

    public void executeScript(String script, WebElement webElement) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript(script, webElement);
      }

    public WebDriver getDriver() {
        if (disableLoadingDefaultHome) {
            return webDriverFactory.getDriver();
        }
        return webDriverFactory.getDriver(firstPageLoadedOverrideURL != null ? firstPageLoadedOverrideURL : baseUrl);
    }

    public WebDriverFactory getWebDriverFactory() {
        return webDriverFactory;
    }
    public TestCardFactory getTestCardFactory() {
        return testCardFactory;
    }
    public void setWebDriverFactory(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }
    //Initialize the Page elements
    public void initPageElements(IPage page){
        PageFactory.initElements(this.getDriver(), page);
    }

    //Returns the By object using field name from @FindBy annotation from passed Page Object
    //Use to avoid declaring both FindBy and By with the same values.
    public By getBy(Object o,  String fieldName) {
        try {
            return new Annotations(o.getClass().getDeclaredField(fieldName)).buildBy();
        } catch (NoSuchFieldException e) { return null; }
    }

    //use for debugging purposes: will highlight the element on the page
    public void highlightElement(WebElement element) {
        for (int i = 0; i < 5; i++) {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
             js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "color: yellow; border: 2px solid yellow;");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "");
        }
    }

    //############################Prashant's utils ########################################
    public void click(By locator) {

        WebElement we = wait.ForVisibleAndEnabledElement(locator);
        if(we!=null){
            we.click();
        }
        else{
            throw new ElementNotVisibleException("WebElement " + locator.toString() + " is not visible or not enabled");
        }
    }
    public void click(WebElement element) {
        element.click();
    }
    public void clickAndWait(By locator, WaitTime waitTime) {
        click(locator);
        try {
            waitExplicitly(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void clickAndWaitForJQueryCompletion(By locator){
        click(locator);
        waitForJQueryCompletion();
    }
    public void clickFirstElement(By locator) {
        List<WebElement> elements = wait.ForElements(locator);
        wait.ForVisibleElement(elements.get(0)).click();
    }
    public void clickLastElement(By locator) {
        List<WebElement> elements = wait.ForElements(locator);
        wait.ForVisibleElement(elements.get(elements.size() - 1)).click();
    }
    public void type(By locator, String text) {
        WebElement element = wait.ForVisibleAndEnabledElement(locator);
        element.clear();
        element.sendKeys(text);
    }
    public void type(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }
    public void typeAndWait(WebElement element, String text, WaitTime waitTime) {
        element.clear();
        element.sendKeys(text);
        try {
            waitExplicitly(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void typeAndTab(By locator, String text) {
        WebElement element = wait.ForVisibleAndEnabledElement(locator);
        element.clear();
        element.sendKeys(text);
        element.sendKeys(Keys.TAB);
    }
    public void selectElementText(By locator, String text) {
        Select select = new Select(wait.ForElement(locator));
        if (select.isMultiple()) {
            select.deselectAll();
        }
        select.selectByVisibleText(text);
    }
    public void selectElementIndex(By locator, Integer index) {
        Select select = new Select(wait.ForElement(locator));
        if (select.isMultiple()) {
            select.deselectAll();
        }
        select.selectByIndex(index);
    }
    public Select getSelectElement(By locator) {
        Select select = new Select(wait.ForElement(locator));
        return select;
    }
    public WebElement getElement(By locator) {
        return getElement(locator, WaitTime.DEFAULT);
    }
    public WebElement getElement(By locator, WaitTime timeout) {
        return wait.ForElement(locator, timeout);
    }
    public WebElement getElement(By locator, int timeout) {
        return wait.ForElement(locator, timeout);
    }
    public List<WebElement> getElements(By locator) {
        return getElements(locator, WaitTime.DEFAULT);
    }
    public List<WebElement> getElements(By locator, WaitTime timeout) {
        return wait.ForElements(locator, timeout);
    }
    public List<WebElement> getElements(By locator, int timeout) {
        return wait.ForElements(locator, timeout);
    }
    public String getElementText(By locator) {
        return wait.ForVisibleElement(locator).getText();
    }
    public String getElementValue(By locator) {
        return wait.ForVisibleElement(locator).getAttribute("value");
    }
    public String getElementAttribute(By locator, String attributeName) {
        return wait.ForVisibleElement(locator).getAttribute(attributeName);
    }
    public void sendKey(By locator, Keys key) {
        wait.ForVisibleElement(locator).sendKeys(key);
    }
    public void clear(By locator) {
        wait.ForVisibleElement(locator).clear();
    }
    public void waitExplicitly(WaitTime waitTime) throws InterruptedException {
        Thread.sleep(waitTime.Value() * 1000);
    }
    public <T> T executeJavascript(String script) {
        JavascriptExecutor js = ((JavascriptExecutor) getDriver());
        try {
            if (!script.startsWith("return")) {
                script = "return " + script;
            }
            return (T) js.executeScript(script);
        } catch (Exception e) {}
        return null;
    }
    public void waitForJQueryCompletion(){
        Clock clock = new SystemClock();
        boolean jqueryFlag = false;
        long scheduledEndTime = clock.laterBy(new Duration(WaitTime.DEFAULT.Value() * 1000, MILLISECONDS).in(MILLISECONDS));
        while (clock.isNowBefore(scheduledEndTime) && jqueryFlag == false) {
            Object result = executeJavascript("jQuery.active === 0;");
            jqueryFlag = result == null ? false : (Boolean) result;
        }
    }
    public void waitForElementAttributeMatch(By locator, String attribute, String attributeValue, WaitTime waitTime) {
        Clock clock = new SystemClock();
        boolean matched = false;
        long scheduledEndTime = clock.laterBy(new Duration(waitTime.Value() * 1000, MILLISECONDS).in(MILLISECONDS));
        while (clock.isNowBefore(scheduledEndTime) && matched == false) {
            WebElement element = wait.ForElement(locator);
            String elementAttribute = element.getAttribute(attribute);
            if (elementAttribute.equals(attributeValue)) {
                matched = true;
            }
        }
        if (matched == false) {
            throw new NotFoundException("WebElement <" + locator + "> attribute <" + attribute + "> does not match attribute value <" + attributeValue + ">");
        }
    }
    public void verifyText(By locator, String expectedText){
        Assert.assertEquals(expectedText.trim(), getElementText(locator).trim());
    }
    public void verifyValue(By locator, String expectedValue){
        Assert.assertEquals(expectedValue.trim(), getElementValue(locator).trim());
    }
    public void verifyAttribute(By locator, String attribute, String expectedAttributeValue){
        Assert.assertEquals(expectedAttributeValue.trim(), getElement(locator).getAttribute(attribute).trim());
    }

}

