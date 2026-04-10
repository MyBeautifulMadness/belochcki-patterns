package com.OnlineBankingService.service.ws;

import com.OnlineBankingService.dto.ws.AccountOperationsInvalidationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountOperationWsPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyAccountOperationsChanged(UUID accountId) {
        eventPublisher.publishEvent(new AccountOperationsChangedInternalEvent(accountId));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAccountOperationsChanged(AccountOperationsChangedInternalEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/accounts/" + event.accountId() + "/operations",
                AccountOperationsInvalidationEvent.forAccount(event.accountId())
        );
    }

    private record AccountOperationsChangedInternalEvent(UUID accountId) {
    }
}