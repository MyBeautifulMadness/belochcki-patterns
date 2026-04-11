package com.OnlineBankingService.metrics;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestMetrics {

    private static final AtomicInteger totalRequests = new AtomicInteger();
    private static final AtomicInteger errorRequests = new AtomicInteger();

    public static void record(int status) {

        totalRequests.incrementAndGet();

        if (isFaultError(status)) {
            errorRequests.incrementAndGet();
        }
    }

    private static boolean isFaultError(int status) {
        return status == 500 || status == 502;
    }

    public static double getErrorRate() {
        int total = totalRequests.get();
        if (total == 0) return 0;
        return (errorRequests.get() * 100.0) / total;
    }

    public static void reset() {
        totalRequests.set(0);
        errorRequests.set(0);
    }
}