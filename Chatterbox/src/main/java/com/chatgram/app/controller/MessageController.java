package com.chatgram.app.controller;

import com.chatgram.app.dto.MessageDTO;
import com.chatgram.app.model.MessageStatus;
import com.chatgram.app.model.MessageType;
import com.chatgram.app.security.JwtTokenProvider;
import com.chatgram.app.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final JwtTokenProvider jwtUtil;

    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> payload) {

        String token = authHeader.substring(7);
        Long senderId = jwtUtil.extractUserId(token);

        MessageDTO message = messageService.sendMessage(
                senderId,
                payload.get("chatId"),
                payload.get("content"),
                MessageType.valueOf(payload.getOrDefault("type", "TEXT"))
        );

        return ResponseEntity.ok(message);
    }

    @GetMapping("/history/{chatId}")
    public ResponseEntity<List<MessageDTO>> getChatHistory(@PathVariable String chatId) {
        return ResponseEntity.ok(messageService.getChatHistory(chatId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        messageService.updateMessageStatus(id,
                MessageStatus.valueOf(payload.get("status")));

        return ResponseEntity.ok().build();
    }
}
