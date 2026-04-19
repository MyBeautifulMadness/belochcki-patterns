package com.OnlineBankingService.notifications;

import org.springframework.beans.factory.annotation.Value;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final PushSubscriptionRepository repository;
    private final String publicKey;
    private final String privateKey;
    private final String subject;

    public NotificationService(
            PushSubscriptionRepository repository,
            @Value("${vapid.publicKey}") String publicKey,
            @Value("${vapid.privateKey}") String privateKey,
            @Value("${vapid.subject}") String subject
    ) {
        this.repository = repository;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.subject = subject;
    }

    private PushService createPushService() throws Exception {
        PushService pushService = new PushService(publicKey, privateKey);
        pushService.setSubject(subject);
        return pushService;
    }

    public void sendToUser(UUID userId, String payload) {
        List<PushSubscription> subs = repository.findAllByUserId(userId);

        for (PushSubscription sub : subs) {
            try {
                PushService pushService = createPushService();

                Notification notification = new Notification(
                        sub.getEndpoint(),
                        sub.getP256dh(),
                        sub.getAuth(),
                        payload.getBytes(StandardCharsets.UTF_8)
                );

                pushService.send(notification);
            } catch (Exception ignored) {}
        }
    }

    public void sendToAll(String payload) {
        List<PushSubscription> subs = repository.findAll();

        for (PushSubscription sub : subs) {
            try {
                PushService pushService = createPushService();

                Notification notification = new Notification(
                        sub.getEndpoint(),
                        sub.getP256dh(),
                        sub.getAuth(),
                        payload.getBytes(StandardCharsets.UTF_8)
                );

                pushService.send(notification);
            } catch (Exception ignored) {}
        }
    }
}
