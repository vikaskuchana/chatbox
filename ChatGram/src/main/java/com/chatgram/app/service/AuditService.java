package com.chatgram.app.service;



import com.chatgram.app.entity.AuditLog;
import com.chatgram.app.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void logAction(Long userId, String action, String resource, String resourceId, Map<String, Object> metadata) {
        AuditLog log = AuditLog.builder()
                .userId(Math.toIntExact(userId))
                .action(action)
                .resource(resource)
                .resourceId(resourceId)
                .metadata(metadata)
                .build();

        auditLogRepository.save(log);
    }

    @KafkaListener(topics = "user.audit", groupId = "audit-group")
    public void consumeAuditEvents(Map<String, Object> auditData) {
        AuditLog log = AuditLog.builder()
                .userId((int) ((Number) auditData.get("userId")).longValue())
                .action((String) auditData.get("action"))
                .resource((String) auditData.get("resource"))
                .resourceId((String) auditData.get("resourceId"))
                .ipAddress((String) auditData.get("ipAddress"))
                .userAgent((String) auditData.get("userAgent"))
                .metadata((Map<String, Object>) auditData.get("metadata"))
                .build();

        auditLogRepository.save(log);
    }
}