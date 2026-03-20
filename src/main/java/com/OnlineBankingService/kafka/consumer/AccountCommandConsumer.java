package com.OnlineBankingService.kafka.consumer;

import com.OnlineBankingService.entity.ProcessedCommand;
import com.OnlineBankingService.kafka.command.DepositCommand;
import com.OnlineBankingService.kafka.command.WithdrawCommand;
import com.OnlineBankingService.kafka.command.TransferCommand;
import com.OnlineBankingService.kafka.command.OpenDebitAccountCommand;
import com.OnlineBankingService.kafka.command.CloseDebitAccountCommand;
import com.OnlineBankingService.repository.ProcessedCommandRepository;
import com.OnlineBankingService.service.DebitAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountCommandConsumer {

    private final DebitAccountService debitAccountService;
    private final ProcessedCommandRepository processedCommandRepository;

    @KafkaListener(topics = "core.deposit-command", groupId = "core-service-group")
    @Transactional
    public void consumeDeposit(DepositCommand command) {
        if (processedCommandRepository.existsById(command.operationId())) {
            return;
        }

        debitAccountService.processDepositCommand(command);

        processedCommandRepository.save(
                ProcessedCommand.builder()
                        .operationId(command.operationId())
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }

    @KafkaListener(topics = "core.withdraw-command", groupId = "core-service-group")
    @Transactional
    public void consumeWithdraw(WithdrawCommand command) {
        if (processedCommandRepository.existsById(command.operationId())) {
            return;
        }

        debitAccountService.processWithdrawCommand(command);

        processedCommandRepository.save(
                ProcessedCommand.builder()
                        .operationId(command.operationId())
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }

    @KafkaListener(topics = "core.transfer-command", groupId = "core-service-group")
    @Transactional
    public void consumeTransfer(TransferCommand command) {
        if (processedCommandRepository.existsById(command.operationId())) {
            return;
        }

        debitAccountService.processTransferCommand(command);

        processedCommandRepository.save(
                ProcessedCommand.builder()
                        .operationId(command.operationId())
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }

    @KafkaListener(topics = "core.open-debit-account-command", groupId = "core-service-group")
    @Transactional
    public void consumeOpenDebitAccount(OpenDebitAccountCommand command) {
        if (processedCommandRepository.existsById(command.operationId())) {
            return;
        }

        debitAccountService.processOpenDebitAccountCommand(command);

        processedCommandRepository.save(
                ProcessedCommand.builder()
                        .operationId(command.operationId())
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }

    @KafkaListener(topics = "core.close-debit-account-command", groupId = "core-service-group")
    @Transactional
    public void consumeCloseDebitAccount(CloseDebitAccountCommand command) {
        if (processedCommandRepository.existsById(command.operationId())) {
            return;
        }

        debitAccountService.processCloseDebitAccountCommand(command);

        processedCommandRepository.save(
                ProcessedCommand.builder()
                        .operationId(command.operationId())
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }
}