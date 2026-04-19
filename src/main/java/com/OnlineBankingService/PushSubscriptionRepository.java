package com.OnlineBankingService;

import org.springframework.data.jpa.repository.JpaRepository;
import com.OnlineBankingService.entities.PushSubscription;

import java.util.List;
import java.util.UUID;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, String> {
    List<PushSubscription> findAllByUserId(UUID userId);
}