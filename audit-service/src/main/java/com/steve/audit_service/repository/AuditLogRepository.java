package com.steve.audit_service.repository;

import com.steve.audit_service.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;




import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPerformedByOrderByTimestampDesc(String performedBy);
    List<AuditLog> findByServiceNameOrderByTimestampDesc(String serviceName);
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
}
