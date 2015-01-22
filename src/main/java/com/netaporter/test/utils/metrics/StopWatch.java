package com.netaporter.test.utils.metrics;

import java.lang.System;import java.util.Date;

public class StopWatch {

    private static Date start;
    private static Date stop;

    public static void start() {
        start = new Date();
    }

    public static void stop() {
        stop = new Date();
        print();
    }

    private static void print() {
        long timeInMilliseconds = stop.getTime() - start.getTime();
        System.out.println("Scenario Executed in: " + timeInMilliseconds + " ms");
    }
}