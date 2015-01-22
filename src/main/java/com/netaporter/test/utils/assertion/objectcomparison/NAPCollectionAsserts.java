package com.netaporter.test.utils.assertion.objectcomparison;

import org.junit.Assert;

import java.util.List;

public class NAPCollectionAsserts {

    public static <T> void assertListContains(List<T> list, T item){
        boolean found = false;
        for (T currItem : list){
            if (currItem.equals(item)){
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }
}
