package com.telegram.moderator.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Created by vladimirsabo on 10.04.2024
 */
@Service
public class FormMessageService {

    public SendMessage getMessage(String text, Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    public SendMessage addUsernameTag(String username, @NotNull SendMessage message) {
        message.setText("@" + username + "\n" + message.getText());
        return message;
    }
}
