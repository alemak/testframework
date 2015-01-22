package com.netaporter.test.utils.pages.driver;

import cucumber.api.java.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 28/06/2013
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/test.xml"})
//@ActiveProfiles("webdriverutiltest,https")
public class WebDriverUtilTest {

    @Autowired
    protected WebDriverUtil webBot;

    @Test
    public void checkBaseUrlConfig(){
        System.out.println(webBot.getChannel());
        assertEquals(webBot.getBaseUrl(), "https://net-a-porter.com");
        assertEquals(webBot.getRegion(), "AM");
    }
}
