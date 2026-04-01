package com.steve.approval_service.service.impl;

import com.steve.approval_service.dto.ApprovalRequest;
import com.steve.approval_service.dto.ApprovalResponse;
import com.steve.approval_service.model.Approval;
import com.steve.approval_service.model.ApprovalStatus;
import com.steve.approval_service.repository.ApprovalRepository;
import com.steve.approval_service.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;

    @Override
    public ApprovalResponse createApproval(ApprovalRequest request) {
        // simple initial logic: auto-approve every transaction
        Approval approval = Approval.builder()
                .transactionId(request.getTransactionId())
                .status(ApprovalStatus.APPROVED)
                .reason("Auto-approved")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        approval = approvalRepository.save(approval);
        return mapToResponse(approval);
    }

    @Override
    public ApprovalResponse updateApprovalStatus(UUID approvalId, ApprovalStatus status, String reason) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        approval.setStatus(status);
        approval.setReason(reason);
        approval.setUpdatedAt(LocalDateTime.now());

        approval = approvalRepository.save(approval);
        return mapToResponse(approval);
    }

    @Override
    public List<ApprovalResponse> getApprovalsByTransaction(UUID transactionId) {
        return approvalRepository.findByTransactionId(transactionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApprovalResponse> getApprovalsByStatus(ApprovalStatus status) {
        return approvalRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ApprovalResponse mapToResponse(Approval approval) {
        return ApprovalResponse.builder()
                .id(approval.getId())
                .transactionId(approval.getTransactionId())
                .status(approval.getStatus())
                .reason(approval.getReason())
                .createdAt(approval.getCreatedAt())
                .updatedAt(approval.getUpdatedAt())
                .build();
    }
}