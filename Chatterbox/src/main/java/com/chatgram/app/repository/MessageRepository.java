package com.chatgram.app.repository;

import com.chatgram.app.model.Message;
import com.chatgram.app.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatIdOrderByCreatedAtAsc(String chatId);
    List<Message> findByStatusAndSenderId(MessageStatus status, Long senderId);
}
