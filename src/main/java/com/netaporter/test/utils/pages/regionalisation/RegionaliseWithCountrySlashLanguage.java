package com.netaporter.test.utils.pages.regionalisation;

import com.netaporter.test.utils.enums.RegionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionaliseWithCountrySlashLanguage implements RegionalisePathBehavior {
    static Logger logger  = LoggerFactory.getLogger(RegionaliseWithRegionAndCountrySlashLanguage.class);
    @Override
    public String getRegionalisedPath(String baseUrl, RegionEnum region, String country, String language, String path) {
        if (country == null || language == null) {
            throw new IllegalArgumentException("Invalid country and language pair. Country = " + country +", language = " + language);
        }
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append(country.toLowerCase());
        sb.append("/");
        sb.append(language.toLowerCase());
        sb.append("/");
        sb.append(path);
        logger.debug("Creating regionalized path: " + sb.toString());
        return sb.toString();
    }
}
