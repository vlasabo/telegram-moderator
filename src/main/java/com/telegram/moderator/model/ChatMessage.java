package com.telegram.moderator.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.List;

/**
 * Created by vladimirsabo on 10.04.2024
 */
@Document
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    @Id
    private String id;
    private Integer messageId;
    @Indexed
    private Long chatId;
    private String message;
    private boolean updated;
    private List<PhotoSize> photos;
}
