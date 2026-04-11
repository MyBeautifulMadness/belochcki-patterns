package com.OnlineBankingService.metrics;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MetricsSenderScheduler {

    @Scheduled(cron = "59 * * * * *")
    public void sendMinuteMetrics() {

        double errorRate = RequestMetrics.getErrorRate();

        LogSender.sendMinuteMetrics(
                "info-service",
                errorRate
        );
    }
}
