package com.collection.univapi.api.service.audit;

import com.collection.univapi.api.model.audit.AuditLog;
import com.collection.univapi.api.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    public void log(String appId, String endpoint, String method, Integer statusCode, String errorMessage) {
        AuditLog auditLog = AuditLog.builder()
                .appId(appId)
                .endpoint(endpoint)
                .method(method)
                .statusCode(statusCode)
                .errorMessage(errorMessage)
                .timestamp(Instant.now())
                .build();

        auditLogRepository.save(auditLog);
    }
}
