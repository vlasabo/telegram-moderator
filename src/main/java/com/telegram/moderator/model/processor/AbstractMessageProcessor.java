package com.telegram.moderator.model.processor;

import com.telegram.moderator.model.IncomeActionType;
import com.telegram.moderator.model.OutcomeActionType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Queue;

/**
 * Created by vladimirsabo on 16.04.2024
 */
@Getter
@Setter
public abstract class AbstractMessageProcessor {
    private AbstractMessageProcessor nextProcessor;

    public abstract void processMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                                        Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue,
                                        Update update, Message message);

    protected void processNext(Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                               Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue,
                               Update update, Message message) {
        AbstractMessageProcessor nextProcessor = getNextProcessor();
        if (nextProcessor != null) {
            nextProcessor.processMessage(incomeActionQueue, outcomeActionQueue, update, message);
        }
    }
}
