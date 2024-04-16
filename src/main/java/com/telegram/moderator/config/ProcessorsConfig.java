package com.telegram.moderator.config;

import com.telegram.moderator.model.processor.AbstractMessageProcessor;
import com.telegram.moderator.model.processor.PhotoMessageProcessor;
import com.telegram.moderator.model.processor.SaveMessageProcessor;
import com.telegram.moderator.model.processor.VoiceMessageProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by vladimirsabo on 16.04.2024
 */
@Configuration
public class ProcessorsConfig {
    @Bean
    @Primary
    public AbstractMessageProcessor messageProcessor(SaveMessageProcessor saveMessageProcessor,
                                                     VoiceMessageProcessor voiceMessageProcessor,
                                                     PhotoMessageProcessor photoMessageProcessor) {
        saveMessageProcessor.setNextProcessor(photoMessageProcessor);
        photoMessageProcessor.setNextProcessor(voiceMessageProcessor);
        return saveMessageProcessor;
    }
}
