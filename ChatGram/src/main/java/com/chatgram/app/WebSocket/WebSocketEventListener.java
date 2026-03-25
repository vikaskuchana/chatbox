package com.chatgram.app.WebSocket;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : "Anonymous";

        log.info("User connected: {}", username);

        // Broadcast user online status
        messagingTemplate.convertAndSend(
                "/topic/status",
                Map.of(
                        "type", "USER_CONNECTED",
                        "username", username,
                        "status", "ONLINE"
                )
        );
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : "Anonymous";

        log.info("User disconnected: {}", username);

        // Broadcast user offline status
        messagingTemplate.convertAndSend(
                "/topic/status",
                Map.of(
                        "type", "USER_DISCONNECTED",
                        "username", username,
                        "status", "OFFLINE"
                )
        );
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String username = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : "Anonymous";

        log.info("User {} subscribed to {}", username, destination);
    }

    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : "Anonymous";

        log.info("User {} unsubscribed", username);
    }
}