package com.telegram.moderator.model.processor;

import com.telegram.moderator.model.IncomeActionType;
import com.telegram.moderator.model.OutcomeActionType;
import com.telegram.moderator.service.FormMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PhotoMessageProcessor extends AbstractMessageProcessor {
    private final FormMessageService formMessageService;
    @Value("${bot.masterId}")
    private String masterId;

    @Override
    public void processMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                               Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue,
                               Update update, Message message) {
        Integer messageId = message.getMessageId();
        log.info("processMessage with id = {}", messageId);
        if (message.hasPhoto()) {
            processPhotoMessage(incomeActionQueue, outcomeActionQueue, message, messageId);
        } else {
            log.info("message with id {} doesn't contain a photo", messageId);
        }
        processNext(incomeActionQueue, outcomeActionQueue, update, message);
    }

    private void processPhotoMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue, Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue, Message message, Integer messageId) {
        log.info("message contains a photo!");
        incomeActionQueue.add(Pair.of(IncomeActionType.SAVE, message));
        String text = getForwardText(message);
        log.info("add message with id {} to redirect in queue! Text is \"{}\"", messageId, text);
        outcomeActionQueue.add(Pair.of(
                OutcomeActionType.SEND,
                formMessageService.getMessage(text, Long.parseLong(masterId))
        ));
    }

    private String getForwardText(Message message) {
        return String.format("Переслано из \"%s\", пользователь %s", message.getChat().getTitle(),
                message.getFrom().getUserName() == null ?
                        message.getFrom().getFirstName() + " " + message.getFrom().getLastName() :
                        "@" + message.getFrom().getUserName());
    }
}
