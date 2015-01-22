package com.netaporter.test.utils.pages.component.element;

import com.google.common.base.Function;
import org.openqa.selenium.WebElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: x.qi@london.net-a-porter.com
 * Date: 11/06/2013
 */
public class WebElementDataExtractingFunctions {

    private static final Pattern PERCENTAGE_PATTERN = Pattern.compile("\\d{2}\\%");
    private static final Pattern PRICE_PATTERN = Pattern.compile("\\p{Sc}(\\d+(\\,)*)+");


    public static Function<WebElement, String> extractText = new Function<WebElement, String>() {
        public String apply(WebElement webElement) {
            return webElement.getText();
        }
    };


    public static Function<WebElement, Integer> extractTextToInteger = new Function<WebElement, Integer>() {
        public Integer apply(WebElement webElement) {
            return Integer.valueOf(webElement.getText());
        }
    };


    public static Function<WebElement, String> extractHrefAttribute = new Function<WebElement, String>() {
        public String apply(WebElement webElement) {
            String href = webElement.getAttribute("href");
            return href == null ? "" : href;
        }
    };


    public static Function<WebElement, Integer> extractPercentageFromText = new Function<WebElement, Integer>() {
        public Integer apply(WebElement webElement) {
            Matcher percentageMatcher = PERCENTAGE_PATTERN.matcher(webElement.getText());

            if (percentageMatcher.find()) {
                String percentageValue = percentageMatcher.group();

                return Integer.valueOf(percentageValue.substring(0, percentageValue.length() - 1));
            }

            return 0;
        }
    };


    public static Function<WebElement, Integer> extractPriceFromText = new Function<WebElement, Integer>() {
        public Integer apply(WebElement webElement) {
            //Note: assume price symbol is a character before the value
            Matcher priceMatcher = PRICE_PATTERN.matcher(new String(webElement.getText()));

            String priceValue = "£0";
            while (priceMatcher.find()) {
                priceValue = priceMatcher.group();
            }
            return Integer.valueOf(priceValue.substring(1).replace(",", ""));
        }
    };


}
