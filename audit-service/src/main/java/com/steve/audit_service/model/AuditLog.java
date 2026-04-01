package com.steve.audit_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;     // e.g. auth-service, approval-service
    private String action;          // e.g. LOGIN, TRANSACTION_APPROVED
    private String performedBy;     // e.g. user email or system
    private String details;         // additional info (JSON/string)
    private LocalDateTime timestamp;
}
