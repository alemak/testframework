package com.netaporter.test.utils.enums;

/**
 * User: x.qi@london.net-a-porter.com
 * Date: 16/05/2013
 */
public enum SortOrderEnum {

   DEFAULT("default", null), NEW_IN("newIn", true), PRICE_HIGH("price-desc", true), PRICE_LOW("price-asc", false), DISCOUNT_HIGH("discount_perc-desc", true), DISCOUNT_LOW("discount_perc-asc", false);

    private String orderValue;

    private Boolean isDescending;

    SortOrderEnum(String orderValue, Boolean descending) {
        this.orderValue = orderValue;
        this.isDescending = descending;
    }

    public String getOrderValue() {
        return orderValue;
    }

    public Boolean isDescending() {
        return isDescending;
    }


    public static SortOrderEnum create(String orderValue) {
        for(SortOrderEnum sortOrder: SortOrderEnum.values()) {
            if (sortOrder.getOrderValue().equals(orderValue)) {
                return sortOrder;
            }
        }

        return null;
    }


}
