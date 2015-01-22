package com.netaporter.test.utils.pages.driver;
        import com.netaporter.test.utils.pages.driver.WaitTime;
        import org.openqa.selenium.*;
        import org.openqa.selenium.support.ui.ExpectedConditions;
        import org.openqa.selenium.support.ui.WebDriverWait;
        import java.util.List;
/*
 * User: Prashant.Ramcharan@net-a-porter.com
 * Date: 06/02/2014
 *
 * <---- PURPOSE ---->
 * Single point of implementation for handling Wait (Syncing).
 * Can be utilised by any WebDriver and a more efficient and standardised approach to polling the DOM for WebElements.
 */
public class Wait {
    WebDriver driver;
    public Wait(WebDriver webDriver) {
        this.driver = webDriver;
    }
    public WebElement ForVisibleAndEnabledElement(By locator) {
        return ForVisibleAndEnabledElement(locator, WaitTime.DEFAULT);
    }
    public WebElement ForVisibleAndEnabledElement(By locator, WaitTime waittime) {
        WebDriverWait wait = new WebDriverWait(driver, waittime.Value());
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (WebDriverException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    public WebElement ForVisibleElement(By locator) {
       return ForElement(locator, WaitTime.DEFAULT);
    }
    public WebElement ForVisibleElement(By locator, WaitTime waittime) {
        WebDriverWait wait = new WebDriverWait(driver, waittime.Value());
        try {
            WebElement we = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return wait.until(ExpectedConditions.visibilityOf(we));
        } catch (TimeoutException ex) {
            return null;
        }
    }
    public WebElement ForVisibleElement(WebElement element) {
       return ForVisibleElement(element, WaitTime.DEFAULT);
    }
    public WebElement ForVisibleElement(WebElement element, WaitTime waittime) {
        WebDriverWait wait = new WebDriverWait(driver, waittime.Value());
        try {
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException ex) {
            return null;
        }
    }
    public WebElement ForElement(By locator) {
        return ForElement(locator, WaitTime.DEFAULT);
    }

    public WebElement ForElement(By locator, WaitTime waittime) {
        WebDriverWait wait = new WebDriverWait(driver, waittime.Value());
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ex) {
            return null;
        }
    }
    public WebElement ForElement(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ex) {
            return null;
        }
    }
    public List<WebElement> ForElements(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (TimeoutException ex) {
            return null;
        }
    }
    public List<WebElement> ForElements(By locator) {
        return ForElements(locator, WaitTime.DEFAULT);
    }
    public List<WebElement> ForElements(By locator, WaitTime waittime) {
        WebDriverWait wait = new WebDriverWait(driver, waittime.Value());
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (TimeoutException ex) {
            return null;
        }
    }
}