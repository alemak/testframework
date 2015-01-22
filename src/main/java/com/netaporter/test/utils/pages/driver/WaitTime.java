package com.netaporter.test.utils.pages.driver;

public enum WaitTime {
    DEFAULT(30),
    MEDIUM(15),
    MAXIMUM(60),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10);
    private int timeout;
    WaitTime(int timeout) {
        this.timeout = timeout;
    }
    int Value() {
        return timeout;
    }
}