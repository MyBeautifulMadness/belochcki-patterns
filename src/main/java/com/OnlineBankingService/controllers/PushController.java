package com.OnlineBankingService.controllers;

import com.OnlineBankingService.PushSubscriptionRepository;
import com.OnlineBankingService.entities.PushSubscription;
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
