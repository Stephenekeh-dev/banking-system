package com.steve.approval_service.repository;

import com.steve.approval_service.model.Approval;
import com.steve.approval_service.model.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApprovalRepository extends JpaRepository<Approval, UUID> {
    List<Approval> findByTransactionId(UUID transactionId);
    List<Approval> findByStatus(ApprovalStatus status);
}
