package com.netaporter.test.utils.cucumber.glue;

import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.metrics.StopWatch;
import com.netaporter.test.utils.pages.driver.WebDriverUtil;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Wrapper for Cucumber Scenarios. Contains common hooks including
 * embedding of screenshots in the scenario results.
 * To use - include the path to this class in the feature(scenario) glue.
 */
@SuppressWarnings("ALL")
@ContextConfiguration("classpath:cucumber.xml")
public class ScenarioWrapper {
    static Logger logger = LoggerFactory.getLogger(ScenarioWrapper.class);
    static String countryProperty;
    static String languageProperty;
    /**
     * Tag the feature/scenario with one of these tags to set the country value for regionalised pages' paths
     */
      static final String countryTags =
            "@country=GB," +
            "@country=DE," +
            "@country=FR," +
            "@country=CN," +
            "@country=BR," +
            "@country=AU," +
            "@country=US," +
            "@country=HK";
    /**
     * Tag the feature/scenario with one of these tags to set the language value for regionalised pages' paths
     */
    static final String languageTags =
            "@language=EN," +
            "@language=DE," +
            "@language=FR," +
            "@language=ZH";

    @Autowired
    public WebDriverUtil webBot;

    @Before
    public void beforeScenario() throws Exception {
        StopWatch.start();
    }

    @After
    public void afterScenario(Scenario result) {
        embedScreenshot(result);
        webBot.quit();
        StopWatch.stop();
    }

    public void embedScreenshot(Scenario result) {
        if (result.getStatus().equals("failed")) {
            try {
                byte[] screenshotBytes = webBot.takeScreenShotAsBytes();
                SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss");
                String timestamp = ft.format(new Date(System.currentTimeMillis()));
                if (screenshotBytes != null) {
                    result.write( timestamp +" Screenshot taken for URL: " + webBot.getCurrentUrl());
                    result.embed(screenshotBytes, "image/png");
                }

            } catch (WebDriverException exception) {
                logger.error(exception.getMessage());
            }
        }
    }
    @Before(countryTags)
    public void beforeCountryScenario(Scenario scenario) throws NoSuchMethodException {
        countryProperty = System.getProperty("WebBotCountry");
        for(String tagname: scenario.getSourceTagNames()){
            if(tagname.contains("@country=")){
                String country = tagname.replaceFirst("@country=","");
                webBot.setCountry(country.toLowerCase());
                System.setProperty("WebBotCountry", country.toLowerCase());
                break;
            }
        };
    }
    @After(countryTags)
    public void afterCountryScenario(){
        restoreCountry();
    }

    private void restoreCountry() {
        if(countryProperty!=null)
            webBot.setCountry(countryProperty);
            System.setProperty("WebBotCountry",countryProperty);
    }

    @Before(languageTags)
    public void beforeLanguageScenario(Scenario scenario) throws NoSuchMethodException {
        languageProperty = System.getProperty("WebBotLanguage");
        for(String tagname: scenario.getSourceTagNames()){
            if(tagname.contains("@language=")){
                String language = tagname.replaceFirst("@language=","");
                webBot.setLanguage(language.toLowerCase());
                System.setProperty("WebBotLanguage", language.toLowerCase());
                break;
            }
        };
    }
    @After(languageTags)
    public void afterLanguagelScenario(){
        restoreLanguage();
    }

    private void restoreLanguage() {
        if(languageProperty!=null)
            webBot.setLanguage(languageProperty);
            System.setProperty("WebBotLanguage",languageProperty);
    }


}
