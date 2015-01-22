package com.netaporter.test.utils.cucumber.steps;

import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import com.netaporter.test.utils.pages.IPage;
import com.netaporter.test.utils.pages.PageRegistry;
import com.netaporter.test.utils.pages.driver.WebDriverUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 * User: J.Christian@net-a-porter.com
 * Date: 23/03/2013
 * Time: 00:00
 * To change this template use File | Settings | File Templates.
 */
public class BaseStepWithWebDriver extends BaseStep {

    @Autowired
    protected WebDriverUtil webBot;
    @Autowired
    protected PageRegistry pageRegistry;

    @PostConstruct
    public void initRegion(){
         if(region!=null) {
             if (webBot.getRegion() != region.toString()) {
                 webBot.setRegion(region.name());
             }
         }

    }
    public IPage lookupPage(String pageName) {
        return pageRegistry.lookupPage(pageName);
    }

    public void setCurrentPage(IPage page){
        webBot.setCurrentPage(page);
    }

    protected String getLastSegmentInUrlPath() {
        String url = webBot.getCurrentUrl();
        String[] result = url.split("/");
        String lastSegment = result[result.length - 1];
        int queryString = lastSegment.indexOf('?');
        if(queryString != -1) {
            lastSegment = lastSegment.substring(0, queryString);
        }
        return lastSegment;
    }
    protected void setRegion(String regionName){
        this.region = RegionEnum.valueOf(regionName.toUpperCase());
        webBot.setRegion(region.name());
    }
    protected void setChannel(SalesChannelEnum channel) {
        this.channelId = channel.getId();
        webBot.setChannel(channel);
    }

}
