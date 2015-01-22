package com.netaporter.test.utils.pages.exceptions;

/**
 * Created by a.makarenko on 10/7/14.
 *
 * */
public class PageRegionalisationException extends RuntimeException {
    public PageRegionalisationException(String message) {
        super(message);
    }

    public PageRegionalisationException(Throwable throwable) {
        super(throwable);
    }
    public PageRegionalisationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
