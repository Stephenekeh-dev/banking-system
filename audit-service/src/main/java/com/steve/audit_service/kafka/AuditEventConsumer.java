package com.steve.audit_service.kafka;

import com.steve.audit_service.dto.AuditEvent;
import com.steve.audit_service.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditLogService auditLogService;

    @KafkaListener(topics = "audit-events", groupId = "audit-service-group")
    public void consume(AuditEvent event) {
        auditLogService.recordEvent(
                event.getServiceName(),
                event.getAction(),
                event.getPerformedBy(),
                event.getDetails()
        );
        System.out.println("Audit log saved: " + event.getAction());
    }
}