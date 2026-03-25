package com.chatgram.app.service;


import com.chatgram.app.dto.MessageDTO;
import com.chatgram.app.entity.User;
import com.chatgram.app.model.Message;
import com.chatgram.app.model.MessageStatus;

import com.chatgram.app.model.MessageType;
import com.chatgram.app.repository.MessageRepository;
import com.chatgram.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuditService auditService;

    @Transactional
    public MessageDTO sendMessage(Long senderId, String chatId, String content, MessageType type) {
        // Persist message
        Message message = Message.builder()
                .senderId(senderId)
                .chatId(chatId)
                .content(content)
                .messageType(type)
                .status(MessageStatus.SENT)
                .build();

        message = messageRepository.save(message);

        // Publish to Kafka for async processing
        MessageDTO dto = convertToDTO(message);
        kafkaTemplate.send("message.sent", dto);

        // Audit
        auditService.logAction(senderId, "MESSAGE_SENT", "Message", message.getId().toString(), null);

        return dto;
    }

    public List<MessageDTO> getChatHistory(String chatId) {
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chatId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMessageStatus(Long messageId, MessageStatus status) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setStatus(status);

        if (status == MessageStatus.DELIVERED) {
            message.setDeliveredAt(java.time.LocalDateTime.now());
            kafkaTemplate.send("message.delivered", convertToDTO(message));
        } else if (status == MessageStatus.READ) {
            message.setReadAt(java.time.LocalDateTime.now());
            kafkaTemplate.send("message.read", convertToDTO(message));
        }

        messageRepository.save(message);
    }

    private MessageDTO convertToDTO(Message message) {
        User sender = userRepository.findById(message.getSenderId()).orElse(null);

        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(sender != null ? sender.getDisplayName() : "Unknown")
                .chatId(message.getChatId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
