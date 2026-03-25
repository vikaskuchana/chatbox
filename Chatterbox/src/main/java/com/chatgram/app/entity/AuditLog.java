package com.chatgram.app.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    private String id;
    private int userId;
    private String action;
    private String resource;
    private String resourceId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Map<String, Object> metadata;
}
