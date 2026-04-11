package com.OnlineBankingService.metrics;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestMetrics {

    private static final AtomicInteger total = new AtomicInteger();
    private static final AtomicInteger errors = new AtomicInteger();

    private static volatile long windowStart = nowMinute();

    private static volatile boolean circuitOpen = false;
    private static volatile long circuitOpenedAt = 0;

    public static void record(int status) {
        rollWindowIfNeeded();

        total.incrementAndGet();

        if (status == 500 || status == 502) {
            errors.incrementAndGet();
        }

        evaluateCircuit();
    }

    public static boolean isCircuitOpen() {
        if (!circuitOpen) return false;

        long now = System.currentTimeMillis();

        if (now - circuitOpenedAt >= 60_000) {
            circuitOpen = false;
            reset();
            return false;
        }

        return true;
    }

    public static double getErrorRate() {
        int t = total.get();
        if (t == 0) return 0;
        return (errors.get() * 100.0) / t;
    }

    private static void evaluateCircuit() {
        if (circuitOpen) return;

        if (getErrorRate() > 70.0) {
            circuitOpen = true;
            circuitOpenedAt = System.currentTimeMillis();
        }
    }

    private static void rollWindowIfNeeded() {
        long now = nowMinute();

        if (now != windowStart) {
            windowStart = now;
            reset();
        }
    }

    private static void reset() {
        total.set(0);
        errors.set(0);
    }

    private static long nowMinute() {
        return System.currentTimeMillis() / 60000;
    }
}