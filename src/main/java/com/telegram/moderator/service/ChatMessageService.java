package com.telegram.moderator.service;

import com.telegram.moderator.model.ChatMessage;
import com.telegram.moderator.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by vladimirsabo on 14.04.2024
 */
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final MessageRepository messageRepository;

    public void saveChatMessage(Message message, boolean isEditedMessage) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(message.getChatId());
        chatMessage.setMessage(message.getText());
        chatMessage.setMessageId(message.getMessageId());
        chatMessage.setUpdated(isEditedMessage);
        chatMessage.setPhotos(message.getPhoto());
        messageRepository.save(chatMessage);
    }
}
