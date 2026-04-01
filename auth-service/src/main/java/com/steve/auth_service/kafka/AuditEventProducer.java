package com.steve.auth_service.kafka;

import com.steve.auth_service.dto.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLoginAudit(String userEmail, boolean success) {
        AuditEvent event = AuditEvent.builder()
                .serviceName("auth-service")
                .action(success ? "LOGIN_SUCCESS" : "LOGIN_FAILURE")
                .performedBy(userEmail)
                .details("User login attempt")
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("audit-events", event);
    }
}