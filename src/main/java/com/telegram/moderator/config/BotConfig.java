package com.telegram.moderator.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by vladimirsabo on 10.04.2024
 */
@Configuration
@Getter
@NoArgsConstructor
public class BotConfig {
    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
}
