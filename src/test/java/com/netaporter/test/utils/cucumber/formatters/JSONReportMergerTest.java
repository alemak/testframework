package com.netaporter.test.utils.cucumber.formatters;

import org.junit.Test;

/**
 * Created by a.makarenko on 03/12/2014.
 */
public class JSONReportMergerTest {
    @Test
    public void TestMerge(){
        JunitReportMerger.replace("INTL-results.xml", "INTLrerun-results.xml");
    }
}
