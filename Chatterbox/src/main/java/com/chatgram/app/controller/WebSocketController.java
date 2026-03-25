package com.chatgram.app.controller;


import com.chatgram.app.model.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload WebSocketMessage message) {
        // Broadcast typing indicator
        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getPayLoad().toString(),
                message
        );
    }

    @MessageMapping("/chat.status")
    public void handleStatus(@Payload WebSocketMessage message) {
        // Broadcast online/offline status
        messagingTemplate.convertAndSend("/topic/status", message);
    }
}
