package com.netaporter.test.utils.pages.regionalisation;

import com.netaporter.test.utils.enums.RegionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by a.makarenko on 9/16/14.
 */
public class RegionaliseWithRegionAndCountrySlashLanguage implements RegionalisePathBehavior {
    static Logger logger  = LoggerFactory.getLogger(RegionaliseWithRegionAndCountrySlashLanguage.class);
    @Override
    public String getRegionalisedPath(String baseUrl, RegionEnum region, String country, String language, String path) {
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append(region.name().toLowerCase());
        sb.append("/");
        sb.append(country !=null? country.toLowerCase():region.getCountry().toLowerCase());
        sb.append("/");
        sb.append(language != null? language.toLowerCase(): region.getLanguage().toLowerCase());
        sb.append("/");
        sb.append(path);
        logger.debug("Creating regionalized path: " + sb.toString());
        return sb.toString();
    }
}
