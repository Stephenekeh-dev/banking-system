package com.steve.auth_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {
    private String serviceName;
    private String action;
    private String performedBy;
    private String details;
    private LocalDateTime timestamp;
}
