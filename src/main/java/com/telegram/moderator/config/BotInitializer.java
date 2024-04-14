package com.telegram.moderator.config;

import com.telegram.moderator.model.ModeratorBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladimirsabo on 10.04.2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final ModeratorBot bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
            bot.updateCommands(getCommandsList());
        } catch (TelegramApiException e) {
            log.error("Unexpected error!\n",e);
        }
    }

    private List<BotCommand> getCommandsList() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Приветствие"));
        return listOfCommands;
    }
}
