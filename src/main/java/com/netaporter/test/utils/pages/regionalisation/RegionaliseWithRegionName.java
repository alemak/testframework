package com.netaporter.test.utils.pages.regionalisation;

import com.netaporter.test.utils.enums.RegionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by a.makarenko on 7/18/14.
 */
public class RegionaliseWithRegionName implements RegionalisePathBehavior {
    static Logger logger  = LoggerFactory.getLogger(RegionaliseWithRegionName.class);
    @Override
    public String getRegionalisedPath(String baseUrl, RegionEnum region, String country, String language, String path) {
        StringBuilder sb = new StringBuilder(baseUrl);
        //sb.append("/"); this is causing double slash to occur and breaking changecountry on nap
        sb.append(region.name().toLowerCase());
        sb.append("/");
        sb.append(path);
        logger.debug("Creating regionalized path: " + sb.toString());
        return sb.toString();
    }
}
