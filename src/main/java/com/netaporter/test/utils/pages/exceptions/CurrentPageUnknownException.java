package com.netaporter.test.utils.pages.exceptions;

/*
* Thrown when the current page property of the WebDriverUtil class is not unknown.
 */
public class CurrentPageUnknownException extends RuntimeException {

    public CurrentPageUnknownException(String message) {
        super(message);
    }

    public CurrentPageUnknownException(Throwable throwable) {
        super(throwable);
    }

    public CurrentPageUnknownException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
