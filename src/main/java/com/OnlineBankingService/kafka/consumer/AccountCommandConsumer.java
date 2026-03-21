package com.OnlineBankingService.kafka.consumer;

import com.OnlineBankingService.entity.ProcessedCommand;
import com.OnlineBankingService.kafka.command.*;
import com.OnlineBankingService.repository.ProcessedCommandRepository;
import com.OnlineBankingService.service.DebitAccountService;
import com.OnlineBankingService.service.MasterAccountService;
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
    private final MasterAccountService masterAccountService;

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

    @KafkaListener(topics = "core.master-account-deposit-command", groupId = "core-service-group")
    @Transactional
    public void consumeMasterAccountDeposit(MasterAccountDepositCommand command) {
        if (processedCommandRepository.existsById(command.operationId())) {
            return;
        }

        masterAccountService.processDepositCommand(command);

        processedCommandRepository.save(
                ProcessedCommand.builder()
                        .operationId(command.operationId())
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }

    @KafkaListener(topics = "core.master-account-withdraw-command", groupId = "core-service-group")
    @Transactional
    public void consumeMasterAccountWithdraw(MasterAccountWithdrawCommand command) {
        if (processedCommandRepository.existsById(command.operationId())) {
            return;
        }

        masterAccountService.processWithdrawCommand(command);

        processedCommandRepository.save(
                ProcessedCommand.builder()
                        .operationId(command.operationId())
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }
}