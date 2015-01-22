package com.netaporter.test.utils.enums;

/**
 * Created with IntelliJ IDEA.
 * User: a.kogan@london.net-a-porter.com
 * Date: 22/03/2013
 * Time: 16:39
 */
public enum RegionEnum {
    INTL("DC1","GB","EN"),
    AM("DC2", "US", "EN"),
    APAC("DC3", "HK", "EN");

    private String dc;
    private String country;
    private String language;
    RegionEnum(String DC, String country, String language) {
        setDC(DC);
        setCountry(country);
        setLanguage(language);
    }
    public String getCountry(){
        return this.country;
    }

    public String getLanguage(){
        return this.language;
    }

    public void setCountry(String country){
        this.country = country;
    }

    public void setLanguage(String language){
        this.language = language;
    }

    public String getDC() {
        return dc;
    }

    public void setDC(String DC) {
        this.dc = DC;
    }



}
