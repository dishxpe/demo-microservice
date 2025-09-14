package com.collection.univapi.api.service.audit;

import com.collection.univapi.api.model.audit.AuditLog;
import com.collection.univapi.api.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    public final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log (String appId, String endpoint, String method, Integer statusCode) {
        AuditLog auditLog = AuditLog.builder()
                .appId(appId)
                .endpoint(endpoint)
                .method(method)
                .statusCode(statusCode)
                .timestamp(java.time.Instant.now())
                .build();

        auditLogRepository.save(auditLog);
    }
}
