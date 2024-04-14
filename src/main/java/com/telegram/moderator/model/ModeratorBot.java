package com.telegram.moderator.model;

import com.telegram.moderator.config.BotConfig;
import com.telegram.moderator.service.UpdateParsingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by vladimirsabo on 10.04.2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ModeratorBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final UpdateParsingService updateParsingService;
    private final Queue<Pair<IncomeActionType, Message>> incomeActionQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue = new ConcurrentLinkedQueue<>();
    @Value("${bot.masterId}")
    private String masterId;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            parseAndUpdateActions(update);
            processIncomeActions();
            processOutcomeActions();
        } catch (Exception e) {
            log.error("Unexpected error while processing update", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    private void parseAndUpdateActions(Update update) {
        updateParsingService.parseUpdate(update, incomeActionQueue, outcomeActionQueue);
    }

    private void processIncomeActions() throws TelegramApiException {
        while (!incomeActionQueue.isEmpty()) {
            Pair<IncomeActionType, Message> pair = incomeActionQueue.poll();
            processIncomeAction(pair);
        }
    }

    private void processOutcomeActions() {
        while (!outcomeActionQueue.isEmpty()) {
            Pair<OutcomeActionType, SendMessage> pair = outcomeActionQueue.poll();
            processOutcomeAction(pair);
        }
    }

    private void deleteMessage(Message message) throws TelegramApiException {
        log.info("deleteMessage(Message {})", message);
        String chatId = String.valueOf(message.getChatId());
        Integer messageId = message.getMessageId();
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        execute(deleteMessage);
    }

    private void sendMessage(SendMessage message) throws TelegramApiException {
        execute(message);
    }

    public void updateCommands(List<BotCommand> listOfCommands) throws TelegramApiException {
        execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), "ru"));
    }

    private void processIncomeAction(Pair<IncomeActionType, Message> pair) throws TelegramApiException {
        switch (pair.getLeft()) {
            case DELETE -> processDeleteAction(pair.getRight());
            case SAVE -> processSaveAction(pair.getRight());
            case NOTHING -> log.info("No action required for income action pair: {}", pair);
            default -> log.warn("Unhandled income action type: {}", pair.getLeft());
        }
    }

    private void processOutcomeAction(Pair<OutcomeActionType, SendMessage> pair) {
        switch (pair.getLeft()) {
            case SEND -> processSendAction(pair.getRight());
            case NOTHING -> log.info("No action required for outcome action pair: {}", pair);
            default -> log.warn("Unhandled outcome action type: {}", pair.getLeft());
        }
    }

    private void processSaveAction(Message message) throws TelegramApiException {
        //getLast isn't work, JDK problem...
        int biggestPhotoIndex = message.getPhoto().size() - 1;
        GetFile getFile = new GetFile(message.getPhoto().get(biggestPhotoIndex).getFileId());
        File photo = execute(getFile);
        java.io.File filePhoto = downloadFile(photo);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(new InputFile(filePhoto));
        sendPhoto.setChatId(masterId);
        execute(sendPhoto);
    }

    private void processDeleteAction(Message message) {
        try {
            deleteMessage(message);
        } catch (TelegramApiException e) {
            log.error("Error while deleting message", e);
        }
    }

    private void processSendAction(SendMessage message) {
        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            log.error("Error while sending message", e);
        }
    }
}
