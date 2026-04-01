package com.steve.audit_service.service;

import com.steve.audit_service.model.AuditLog;
import com.steve.audit_service.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLog recordEvent(String serviceName, String action, String performedBy, String details) {
        AuditLog log = AuditLog.builder()
                .serviceName(serviceName)
                .action(action)
                .performedBy(performedBy)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        return repository.save(log);
    }
}