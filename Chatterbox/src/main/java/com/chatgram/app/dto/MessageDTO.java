package com.chatgram.app.dto;


import com.chatgram.app.model.MessageStatus;
import com.chatgram.app.model.MessageType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private String chatId;
    private String content;
    private MessageType messageType;
    private MessageStatus status;
    private LocalDateTime createdAt;
}

