package com.telegram.moderator.model.processor;

import com.telegram.moderator.model.IncomeActionType;
import com.telegram.moderator.model.OutcomeActionType;
import com.telegram.moderator.service.FormMessageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Queue;

/**
 * Created by vladimirsabo on 16.04.2024
 */
@Service
@RequiredArgsConstructor
public class PhotoMessageProcessor extends AbstractMessageProcessor {
    private final FormMessageService formMessageService;
    @Value("${bot.masterId}")
    private String masterId;

    @Override
    public void processMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                               Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue,
                               Update update) {
        Message message = update.getMessage();
        if (message.hasPhoto()) {
            incomeActionQueue.add(Pair.of(IncomeActionType.SAVE, message));
            String text = String.format("Переслано из \"%s\", пользователь %s", message.getChat().getTitle(),
                    message.getFrom().getUserName() == null ?
                            message.getFrom().getFirstName() + " " + message.getFrom().getLastName() :
                            "@" + message.getFrom().getUserName());

            outcomeActionQueue.add(Pair.of(
                    OutcomeActionType.SEND,
                    formMessageService.getMessage(text, Long.parseLong(masterId))
            ));
        }
        processNext(incomeActionQueue, outcomeActionQueue, update);
    }
}
