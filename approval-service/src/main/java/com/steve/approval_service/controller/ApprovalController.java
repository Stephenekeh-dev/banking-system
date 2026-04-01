package com.steve.approval_service.controller;

import com.steve.approval_service.dto.ApprovalRequest;
import com.steve.approval_service.dto.ApprovalResponse;
import com.steve.approval_service.model.ApprovalStatus;
import com.steve.approval_service.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    // ✅ Create approval
    @PostMapping
    public ResponseEntity<ApprovalResponse> createApproval(@RequestBody ApprovalRequest request) {
        ApprovalResponse response = approvalService.createApproval(request);
        return ResponseEntity.ok(response);
    }

    // ✅ Update approval status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApprovalResponse> updateApprovalStatus(
            @PathVariable UUID id,
            @RequestParam ApprovalStatus status,
            @RequestParam(required = false) String reason) {

        ApprovalResponse response = approvalService.updateApprovalStatus(id, status, reason);
        return ResponseEntity.ok(response);
    }

    // ✅ Get approvals by transaction
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<ApprovalResponse>> getApprovalsByTransaction(
            @PathVariable UUID transactionId) {
        return ResponseEntity.ok(approvalService.getApprovalsByTransaction(transactionId));
    }

    // ✅ Get approvals by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApprovalResponse>> getApprovalsByStatus(
            @PathVariable ApprovalStatus status) {
        return ResponseEntity.ok(approvalService.getApprovalsByStatus(status));
    }
}