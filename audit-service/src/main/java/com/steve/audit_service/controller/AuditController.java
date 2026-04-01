package com.steve.audit_service.controller;

import com.steve.audit_service.model.AuditLog;
import com.steve.audit_service.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogService auditLogService;

    @PostMapping("/log")
    public AuditLog logEvent(@RequestBody AuditLog request) {
        return auditLogService.recordEvent(
                request.getServiceName(),
                request.getAction(),
                request.getPerformedBy(),
                request.getDetails()
        );
    }
}