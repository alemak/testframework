package com.netaporter.test.utils.pages;

import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.pages.driver.WebDriverUtil;
import com.netaporter.test.utils.pages.exceptions.PageRegionalisationException;
import com.netaporter.test.utils.pages.regionalisation.RegionalisePathBehavior;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public abstract class AbstractPage implements IPage {
    static Logger logger = LoggerFactory.getLogger(AbstractPage.class);
    RegionalisePathBehavior regionalise;
    protected String pageName;
    protected String path;

    private List<String> params = new ArrayList<String>();
	protected static final By ERROR = By.cssSelector(".error");

    @PostConstruct
    private void init(){
        webBot.initPageElements(this);
        pageRegistry.addPage(pageName, this);
    }
    @Autowired
    protected WebDriverUtil webBot;

    @Autowired
    private PageRegistry pageRegistry;

    public void setRegionalisePathBehavior(RegionalisePathBehavior reg){
        regionalise = reg;
    }

    public AbstractPage(String pageName, String path) {
        validateRequiredArgs(pageName, path);
        this.pageName = pageName;
        this.path = path;
    }

    public AbstractPage(String pageName, String path, List<String> params) {
        this(pageName, path);
        this.params = params;
    }

    private void validateRequiredArgs(String pageName, String path) {
        if(pageName == null) {
            throw new IllegalArgumentException("Page Name cannot be NULL");
        }
        if(path == null) {
            throw new IllegalArgumentException("Page Path cannot be NULL");
        }
    }

    public String getPath() {
        return path;
    }

    public String getRegionalisedPath(){
        //If the region value is set - the page needs to have the RegionalisedPathBehavior set (in the page constructor:
        // setRegionalisePathBehavior(...behavior implementation....);
        if(webBot.getRegion()!=null && regionalise == null){
            String message = "There is a region setting : "
                    + webBot.getRegion() + " present which means that the pages should be regionalised, however the RegionalisePathBehavior for the page " + this.getClass().getName() + " is not set.";
            logger.error(message);
            throw new PageRegionalisationException(message);
        }
        return regionalise.getRegionalisedPath(getBaseUrl(), RegionEnum.valueOf(getRegion()), getCountry(), getLanguage(), getPath());
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void go() {
        if (isPageRegionalised()) {
            webBot.goToRegionalisedPage(this);
        } else {
            webBot.goToPage(this);
        }
    }

    public void goWithParams(String params) {
        if (isPageRegionalised()) {
            webBot.goToRegionalisedPageWithParams(this, params);
        } else {
            webBot.goToPageWithParams(this, params);
        }
    }

    public void click(By clickableElement) {
        webBot.click(clickableElement);
    }

    /**
     * Returns the element with the H1 tag
     * @return
     */
    public WebElement getPageHeading() {
        return webBot.findElement(By.cssSelector("h1"));
    }

    public String getBaseUrl(){return webBot.getBaseUrl();}

    public String getChannel() {
        return webBot.getChannel();
    }

    public String getRegion(){
        return webBot.getRegion();
    }

    public String getTitle() {
        return webBot.getTitle();
    }

    public String getCountry() {return webBot.getCountry();}

    public String getLanguage() {return webBot.getLanguage();}

    public void setWebBot(WebDriverUtil webBot){
        this.webBot = webBot;
    }

    public String getPageName() {
        return pageName;
    }

    @Override
    public boolean isPageRegionalised() {
        if(webBot.getRegion()!=null){
            return true;
        }
        else return false;
    }

    public List<String> getErrorMessages() {
        List<WebElement> errorMessagesList = webBot.findElements(By.cssSelector("ul.error li"));
        List<String> xs = new ArrayList<String>(errorMessagesList.size());
        for(WebElement e : errorMessagesList)
            xs.add(e.getText());
        return xs;
    }

    public List<String> getMandatoryFieldErrors() {
        List<WebElement> fieldErrors =
                webBot.findElements(By.xpath("//label[@class=\"error-field\"] | //label[@class=\"error\"]"));
        List<String> xs = new ArrayList<String>(fieldErrors.size());
        for(WebElement e : fieldErrors)
            xs.add(e.getAttribute("for").toLowerCase());
        return xs;
    }

    public List<String> getMandatoryDropDownFieldErrors() {
        List<WebElement> fieldErrors =
                webBot.findElements(By.xpath("//span[@class='errorAsterix']/preceding-sibling::select"));
        List<String> xs = new ArrayList<String>(fieldErrors.size());
        for(WebElement e : fieldErrors)
            xs.add(e.getAttribute("name").toLowerCase());
        return xs;
    }
    /*
   Waits for a specific element to exist for up to 10 seconds (e.g. searchType=By.cssSelector(".popup-window"))
    */
    public void waitForElementToExist(By searchType) throws Throwable{
        logger.info("Waiting for element to exist "+searchType);
        for(int i = 0; i <= 100 ; i++) {
            if (webBot.exists(null, searchType)) {
                logger.info("Found");
                return;
            } else {
                Thread.sleep(100);
            }
            System.out.print(".");
        }
        fail("Could not find element: "+searchType);
    }

    /*
    Waits for a specific element to not exist for up to 10 seconds (e.g. searchType=By.cssSelector(".popup-window"))
     */
    public void waitForElementToNotExist(By searchType) throws Throwable{
        logger.info("Waiting for element to not exist "+searchType);
        for(int i = 0; i <= 100 ; i++) {
            if (!(webBot.exists(null, searchType))) {
                System.out.println();
                return;
            } else {
                Thread.sleep(100);
            }
            System.out.print(".");
        }
        fail("Element did not fail to exist: "+searchType);
    }

    /*
    Waits for a specific element to be visible for up to 10 seconds
     */
    public void waitForElementToBeVisible(String cssSelector) throws Throwable{
        // Fail fast if the element doesn't exist
        if (!webBot.exists(null, By.cssSelector(cssSelector))) {
            fail("Could not determine if element is visible as it does not exist: " + cssSelector);
        }

        logger.info("Waiting for element to appear "+cssSelector);
        for(int i = 0; i <= 40 ; i++) {
            // find it each time to prevent selenium reference going stale
            WebElement e = webBot.findElement(By.cssSelector(cssSelector));
            if (e.isDisplayed()) {
                logger.info("Element is visible");
                return;
            } else {
                Thread.sleep(250);
            }
            System.out.print(".");
        }
        fail("Element did not become visible: "+cssSelector);
    }

    /*
    Waits for a specific element to not be visible for up to 10 seconds
    */
    public void waitForElementToNotBeVisible(String cssSelector) throws Throwable{
        // Return fast if the element doesn't exist
        if (!webBot.exists(null, By.cssSelector(cssSelector))) {
            return;
        }

        logger.info("Waiting for element to disappear "+cssSelector);
        for(int i = 0; i <= 40 ; i++) {
            // find it each time to prevent selenium reference going stale
            WebElement e = webBot.findElement(By.cssSelector(cssSelector));
            if (!e.isDisplayed()) {
                logger.info("Element is not visible");
                return;
            } else {
                Thread.sleep(250);
            }
            System.out.print(".");
        }
        fail("Element did not become invisible: "+cssSelector);
    }


}

