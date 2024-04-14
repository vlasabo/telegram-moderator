package com.telegram.moderator.repository;

import com.telegram.moderator.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Created by vladimirsabo on 10.04.2024
 */
public interface MessageRepository extends MongoRepository<ChatMessage, String> {
    Optional<ChatMessage> findByChatIdAndMessageId(Long chatId, Integer messageId);
}
