package com.steve.audit_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {
    private String serviceName;   // e.g. auth-service
    private String action;        // e.g. LOGIN_SUCCESS
    private String performedBy;   // e.g. user email
    private String details;       // free text or JSON
    private LocalDateTime timestamp;
}