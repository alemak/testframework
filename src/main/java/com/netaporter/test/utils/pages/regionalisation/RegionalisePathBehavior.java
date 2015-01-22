package com.netaporter.test.utils.pages.regionalisation;

import com.netaporter.test.utils.enums.RegionEnum;

/**
 * Created by a.makarenko on 7/18/14.
 */
public interface RegionalisePathBehavior {
    String getRegionalisedPath(String baseUrl, RegionEnum region, String country, String language, String path);
}
