package com.netaporter.test.utils.pages;

import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("pageRegistry")
@Scope("cucumber-glue")
public class PageRegistry {

    Map<String, IPage> pageNameToPageObject = new HashMap<String, IPage>();

    IPageRegistryListener pageRegistryListener;

    public void addPage(String name, IPage page) {
        pageNameToPageObject.put(name, page);

        if (pageRegistryListener != null) {
            pageRegistryListener.pageAdded(name, page);
        }
    }

    public IPage lookupPage(String pageName) {
        IPage page = pageNameToPageObject.get(pageName);
        if (page == null) {
            throw new RuntimeException("Unable to find Page Object for Page Name: " + pageName);
        }
        return page;
    }

    public void setPageRegistryListener(IPageRegistryListener pageRegistryListener) {
        this.pageRegistryListener = pageRegistryListener;
    }

}
