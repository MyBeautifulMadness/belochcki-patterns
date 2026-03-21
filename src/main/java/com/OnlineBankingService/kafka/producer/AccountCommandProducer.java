package com.OnlineBankingService.kafka.producer;

import com.OnlineBankingService.kafka.command.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountCommandProducer {

    private static final String DEPOSIT_TOPIC = "core.deposit-command";
    private static final String WITHDRAW_TOPIC = "core.withdraw-command";
    private static final String TRANSFER_TOPIC = "core.transfer-command";
    private static final String OPEN_DEBIT_TOPIC = "core.open-debit-account-command";
    private static final String CLOSE_DEBIT_TOPIC = "core.close-debit-account-command";
    private static final String MASTER_DEPOSIT_TOPIC = "core.master-account-deposit-command";
    private static final String MASTER_WITHDRAW_TOPIC = "core.master-account-withdraw-command";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendDeposit(DepositCommand command) {
        kafkaTemplate.send(DEPOSIT_TOPIC, command.operationId().toString(), command);
    }

    public void sendWithdraw(WithdrawCommand command) {
        kafkaTemplate.send(WITHDRAW_TOPIC, command.operationId().toString(), command);
    }

    public void sendTransfer(TransferCommand command) {
        kafkaTemplate.send(TRANSFER_TOPIC, command.operationId().toString(), command);
    }

    public void sendOpenDebitAccount(OpenDebitAccountCommand command) {
        kafkaTemplate.send(OPEN_DEBIT_TOPIC, command.operationId().toString(), command);
    }

    public void sendCloseDebitAccount(CloseDebitAccountCommand command) {
        kafkaTemplate.send(CLOSE_DEBIT_TOPIC, command.operationId().toString(), command);
    }

    public void sendMasterAccountDeposit(MasterAccountDepositCommand command) {
        kafkaTemplate.send(
                MASTER_DEPOSIT_TOPIC, command.operationId().toString(), command
        );
    }

    public void sendMasterAccountWithdraw(MasterAccountWithdrawCommand command) {
        kafkaTemplate.send(
                MASTER_WITHDRAW_TOPIC, command.operationId().toString(), command
        );
    }
}