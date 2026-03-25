package com.chatgram.app.controller;


import com.chatgram.app.model.MessageStatus;
import com.chatgram.app.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    /**
     * Handle typing indicators
     * Client sends to: /app/chat.typing
     * Broadcast to: /topic/chat/{chatId}/typing
     */
    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, Object> payload, Principal principal) {
        String chatId = (String) payload.get("chatId");
        String username = principal.getName();

        log.info("User {} is typing in chat {}", username, chatId);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId + "/typing",
                Map.of(
                        "type", "TYPING",
                        "username", username,
                        "isTyping", payload.get("isTyping")
                )
        );
    }

    /**
     * Handle online/offline status
     * Client sends to: /app/user.status
     * Broadcast to: /topic/status
     */
    @MessageMapping("/user.status")
    public void handleStatus(@Payload Map<String, Object> payload, Principal principal) {
        String username = principal.getName();
        String status = (String) payload.get("status"); // "ONLINE" or "OFFLINE"

        log.info("User {} status changed to {}", username, status);

        messagingTemplate.convertAndSend(
                "/topic/status",
                Map.of(
                        "type", "STATUS_UPDATE",
                        "username", username,
                        "status", status
                )
        );
    }

    /**
     * Handle read receipts when user reads a message
     * Client sends to: /app/message.read
     */
    @MessageMapping("/message.read")
    public void handleMessageRead(@Payload Map<String, Object> payload, Principal principal) {
        Long messageId = ((Number) payload.get("messageId")).longValue();

        log.info("User {} read message {}", principal.getName(), messageId);

        // Update message status to READ
        messageService.updateMessageStatus(messageId, MessageStatus.READ);
    }

    /**
     * Called when client subscribes to a chat
     * Client subscribes to: /topic/chat/{chatId}
     */
    @SubscribeMapping("/topic/chat/{chatId}")
    public void onChatSubscribe(@DestinationVariable String chatId, Principal principal) {
        log.info("User {} subscribed to chat {}", principal.getName(), chatId);

        // Optionally: Mark all messages as delivered for this user
        // Or send chat history
    }

    /**
     * Send direct message to a specific user
     * This is called internally, not by clients
     */
    public void sendToUser(Long userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                destination,
                payload
        );
    }
}
