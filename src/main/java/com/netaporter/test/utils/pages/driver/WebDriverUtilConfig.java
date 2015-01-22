package com.netaporter.test.utils.pages.driver;

import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import com.netaporter.test.utils.enums.WebsiteEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: J.Christian@net-a-porter.com
 * Date: 26/03/2013
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
@Component("webDriverUtilConfig")
public class WebDriverUtilConfig {

    @Value("${baseUrl}")
    String baseUrl;

    @Value("${brand}")
    String brand;

    @Value("${channel}")
    String channel;

    @Value("${region}")
    String region;

    @Value("${WebBotLanguage}")
    String language;

    @Value("${WebBotCountry}")
    String country;

    @Value("${disableLoadingDefaultHome}")
    String disableLoadingDefaultHome;

    @Value("${firstPageLoadedOverrideURL}")
    String firstPageLoadedOverrideURL;

    public String getBaseUrl() {
        if(valueIsSet(baseUrl)){
            return baseUrl;
        }
       return null;
    }

    public String getCountry() {
        if(valueIsSet(country)){
            return country;
        }
        return null;
    }

    public String getLanguage() {
        if(valueIsSet(language)){
            return language;
        }
        return null;
    }

    public String getBrand() {
        if(valueIsSet(brand)) {
            return brand;
        }
        return null;
    }
    public String getChannel() {
        //if channel is set return channel
        if(valueIsSet(channel)) {
            return channel;
        }
        //otherwise try to get channel from brand and region
        if(valueIsSet(brand) && valueIsSet(region)){
            return (SalesChannelEnum.getByWebsiteAndRegion(WebsiteEnum.valueOf(brand), RegionEnum.valueOf(region))).toString();
        }
        return null;
    }

    public String getRegion() {
       if(valueIsSet(region)) {
            return region;
        }
        return null;
    }

    public boolean isDisableLoadingDefaultHome() {
        if((disableLoadingDefaultHome != null) && (disableLoadingDefaultHome.equalsIgnoreCase("true"))) {
            return true;
        }
        return false;
    }

    public String getFirstPageLoadedOverrideURL() {
        if (valueIsSet(firstPageLoadedOverrideURL)) {
            return firstPageLoadedOverrideURL;
        }
        return null;
    }

    public boolean valueIsSet(String value){
        if(value != null && ! value.contains("${") && ! value.equals("")){
            return true;
        }
        return false;
    }

}
