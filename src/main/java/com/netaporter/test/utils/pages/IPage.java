package com.netaporter.test.utils.pages;

import java.util.List;

public interface IPage {

    void go();
    void goWithParams(String params);

    String getPath();
    String getRegionalisedPath();
    String getTitle();
    public List<String> getErrorMessages();
    public List<String> getMandatoryFieldErrors();
    public List<String> getMandatoryDropDownFieldErrors();
    boolean isPageRegionalised();

}
