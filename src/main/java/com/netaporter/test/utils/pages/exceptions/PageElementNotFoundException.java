package com.netaporter.test.utils.pages.exceptions;

/*
 * Thrown when WebDriver is unable to find an element on the page.
 */
public class PageElementNotFoundException extends RuntimeException {

    public PageElementNotFoundException(String message) {
        super(message);
    }

    public PageElementNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public PageElementNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
