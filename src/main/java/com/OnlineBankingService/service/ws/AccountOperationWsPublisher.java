package com.OnlineBankingService.service.ws;

import com.OnlineBankingService.dto.ws.AccountOperationsInvalidationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountOperationWsPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyAccountOperationsChanged(UUID accountId) {
        messagingTemplate.convertAndSend(
                "/topic/accounts/" + accountId + "/operations",
                AccountOperationsInvalidationEvent.forAccount(accountId)
        );
    }
}