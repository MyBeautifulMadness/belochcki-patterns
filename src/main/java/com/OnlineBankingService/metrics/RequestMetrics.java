package com.OnlineBankingService.metrics;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestMetrics {

    private static final AtomicInteger total = new AtomicInteger();
    private static final AtomicInteger errors = new AtomicInteger();

    private static volatile long windowMinute = currentMinute();

    @Getter
    private static volatile boolean circuitOpen = false;

    public static void record(int status) {

        rollWindowIfNeeded();

        total.incrementAndGet();

        if (status == 500 || status == 502) {
            errors.incrementAndGet();
        }
    }

    private static void evaluateCircuitAtWindowEnd() {
        double rate = getErrorRate();
        circuitOpen = rate > 70.0;
    }

    public static double getErrorRate() {
        int t = total.get();
        if (t == 0) return 0;
        return (errors.get() * 100.0) / t;
    }

    private static void rollWindowIfNeeded() {

        long nowMinute = currentMinute();

        if (nowMinute != windowMinute) {

            evaluateCircuitAtWindowEnd();

            windowMinute = nowMinute;

            total.set(0);
            errors.set(0);
        }
    }

    private static long currentMinute() {
        return System.currentTimeMillis() / 60000;
    }
}