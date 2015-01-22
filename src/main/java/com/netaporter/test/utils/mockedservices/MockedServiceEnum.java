package com.netaporter.test.utils.mockedservices;

/**
 * Created with IntelliJ IDEA.
 * User: a.kogan@london.net-a-porter.com
 * Date: 15/03/2013
 * Time: 11:20
 */
public enum MockedServiceEnum {

    WLS     ("woas.wishlist-service.host",  "woas.wishlist-service.port",   "woas.wishlist-service.scheme"),
    Webapp  ("woas.webapp.host",            "woas.webapp.port",             "woas.webapp.scheme"),
    PS      ("woas.product-service.host",   "woas.product-service.port",    "woas.product-service.scheme"),
    SPAPI   ("woas.spapi.host",             "woas.spapi.port",              "woas.spapi.scheme");


    private String hostProp;
    private String portProp;
    private String schemeProp;

    MockedServiceEnum(String hostProp, String portProp, String schemeProp) {
        this.hostProp = hostProp;
        this.portProp = portProp;
        this.schemeProp = schemeProp;
    }

    public String getHostProp() {
        return hostProp;
    }

    public String getPortProp() {
        return portProp;
    }

    public String getSchemeProp() {
        return schemeProp;
    }
}
