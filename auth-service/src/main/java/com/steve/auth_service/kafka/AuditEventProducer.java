package com.steve.auth_service.kafka;

import com.steve.auth_service.dto.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLoginAudit(String userEmail, boolean success) {
        AuditEvent event = AuditEvent.builder()
                .serviceName("auth-service")
                .action(success ? "LOGIN_SUCCESS" : "LOGIN_FAILURE")
                .performedBy(userEmail)
                .details("User login attempt from auth-service")
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("audit-events", event);
        log.info("Audit event sent: {} for {}", event.getAction(), userEmail);
    }

    public void sendRegistrationAudit(String userEmail) {
        AuditEvent event = AuditEvent.builder()
                .serviceName("auth-service")
                .action("USER_REGISTERED")
                .performedBy(userEmail)
                .details("New user registration")
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("audit-events", event);
    }
}