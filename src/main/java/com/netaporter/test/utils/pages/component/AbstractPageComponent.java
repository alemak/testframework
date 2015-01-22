package com.netaporter.test.utils.pages.component;

import com.netaporter.test.utils.pages.driver.WebDriverUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Common behaviour for components on any page
 */
public class AbstractPageComponent {

    @Autowired
    protected WebDriverUtil webBot;

}