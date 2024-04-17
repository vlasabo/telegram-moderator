package com.telegram.moderator.service;

import com.telegram.moderator.model.IncomeActionType;
import com.telegram.moderator.model.OutcomeActionType;
import com.telegram.moderator.model.processor.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Queue;

/**
 * Created by vladimirsabo on 10.04.2024
 */
@Service
@RequiredArgsConstructor
public class UpdateParsingService {
    private final AbstractMessageProcessor messageProcessor;


    public void parseUpdate(Update update,
                            Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                            Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue) {
        checkIsNotNull(update);
        Message message = getMessage(update);
        checkIsNotNull(message);
        messageProcessor.processMessage(incomeActionQueue,outcomeActionQueue, update, message);
    }

    private Message getMessage(Update update) {
        if (update.hasEditedMessage()) {
            return update.getEditedMessage();
        } else {
            return update.getMessage();
        }
    }

    private void checkIsNotNull(BotApiObject botApiObject) {
        if (botApiObject == null) {
            throw new RuntimeException("Update is null!");
        }
    }
}
