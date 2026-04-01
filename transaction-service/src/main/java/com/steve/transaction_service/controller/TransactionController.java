package com.steve.transaction_service.controller;

import com.steve.transaction_service.dto.CreateTransactionRequest;
import com.steve.transaction_service.dto.TransactionResponse;
import com.steve.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public TransactionResponse create(@RequestBody CreateTransactionRequest request,
                                      Authentication auth) {
        return transactionService.createTransaction(request, auth.getName());
    }

    @GetMapping
    public List<TransactionResponse> getUserTransactions(Authentication auth) {
        return transactionService.getUserTransactions(auth.getName());
    }
}
