package com.steve.transaction_service.service;

import com.steve.transaction_service.dto.CreateTransactionRequest;
import com.steve.transaction_service.dto.TransactionResponse;
import com.steve.transaction_service.entity.Transaction;
import com.steve.transaction_service.entity.TransactionType;
import com.steve.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionResponse createTransaction(CreateTransactionRequest request, String userEmail) {
        Transaction transaction = Transaction.builder()
                .accountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .type(TransactionType.valueOf(request.getType().toUpperCase()))
                .targetAccount(request.getTargetAccount())
                .userEmail(userEmail)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        return new TransactionResponse(transaction);
    }

    public List<TransactionResponse> getUserTransactions(String userEmail) {
        return transactionRepository.findByUserEmail(userEmail)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }
}