package com.steve.transaction_service.kafka;

import com.steve.transaction_service.entity.Transaction;
import com.steve.transaction_service.entity.TransactionType;
import com.steve.transaction_service.event.TransactionEvent;
import com.steve.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionConsumer {

    private final TransactionRepository transactionRepository;

    @KafkaListener(topics = "transactions", groupId = "transaction-service")
    public void consume(TransactionEvent event) {
        Transaction tx = new Transaction();
        tx.setAccountNumber(event.getAccountNumber());
        tx.setAmount(event.getAmount());

        // ✅ Safe enum conversion
        try {
            tx.setType(TransactionType.valueOf(event.getType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid transaction type: " + event.getType());
        }

        tx.setUserEmail(event.getUserEmail());
        tx.setTargetAccount(event.getTargetAccount()); // ✅ Added
        tx.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(tx);
    }
}