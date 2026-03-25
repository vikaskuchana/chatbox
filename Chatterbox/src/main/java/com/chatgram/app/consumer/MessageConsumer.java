package com.chatgram.app.consumer;


import com.chatgram.app.dto.MessageDTO;
import com.chatgram.app.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.chatgram.app.model.MessageStatus;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @KafkaListener(topics = "message.sent", groupId = "message-group")
    public void consumeMessage(MessageDTO message) {
        log.info("Received message: {}", message.getId());

        // Send via WebSocket to recipient
        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatId(), message);

        MessageStatus messageStatus = MessageStatus.DELIVERED;
        messageService.updateMessageStatus(message.getId(), messageStatus);
    }

    @KafkaListener(topics = "message.delivered", groupId = "delivery-group")
    public void consumeDeliveryReceipt(MessageDTO message) {
        log.info("Message delivered: {}", message.getId());

        // Notify sender
        messagingTemplate.convertAndSend("/queue/user/" + message.getSenderId(),
                Map.of("type", "DELIVERY_RECEIPT", "messageId", message.getId()));
    }

    @KafkaListener(topics = "message.read", groupId = "read-group")
    public void consumeReadReceipt(MessageDTO message) {
        log.info("Message read: {}", message.getId());

        // Notify sender
        messagingTemplate.convertAndSend("/queue/user/" + message.getSenderId(),
                Map.of("type", "READ_RECEIPT", "messageId", message.getId()));
    }
}