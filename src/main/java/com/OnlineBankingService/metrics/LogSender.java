package com.OnlineBankingService.metrics;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LogSender {

    public static void send(String service, String message) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8090/logs");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String body = String.format(
                        "{\"service\":\"%s\",\"message\":\"%s\"}",
                        service,
                        message.replace("\"", "'")
                );

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }

                conn.getResponseCode();

            } catch (Exception ignored) {
            }
        }).start();
    }
}
