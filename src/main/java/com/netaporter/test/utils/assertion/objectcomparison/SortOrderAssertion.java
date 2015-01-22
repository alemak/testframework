package com.netaporter.test.utils.assertion.objectcomparison;

import com.netaporter.test.utils.enums.SortOrderEnum;

import java.util.List;

/**
 * User: x.qi@london.net-a-porter.com
 * Date: 13/06/2013
 */
public class SortOrderAssertion {


    public static boolean assertIntegerValuesInCorrectOrders(SortOrderEnum sortOrder, final List<? extends Comparable> values) {
        if (values.size() == 1 || values.isEmpty()) {
            return true;
        }

        Comparable previousValue = values.get(0);

        for (int i = 1; i < values.size(); i++) {
            Comparable currentValue = values.get(i);

            if (sortOrder.isDescending()) {

                if (previousValue.compareTo(currentValue) < 0) {
                    return false;
                }
            } else {
                if (previousValue.compareTo(currentValue) > 0) {
                    return false;
                }
            }

            previousValue = currentValue;
        }

        return true;
    }
}
