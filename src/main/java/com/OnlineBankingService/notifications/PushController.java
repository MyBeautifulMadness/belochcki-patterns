package com.OnlineBankingService.notifications;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push")
public class PushController {

    private final PushSubscriptionRepository repository;

    public PushController(PushSubscriptionRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody PushSubscription subscription) {
        repository.save(subscription);
    }
}
