package com.telegram.moderator.model.processor;

import com.telegram.moderator.model.IncomeActionType;
import com.telegram.moderator.model.OutcomeActionType;
import com.telegram.moderator.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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
public class SaveMessageProcessor extends AbstractMessageProcessor {
    private final ChatMessageService chatMessageService;
    @Override
    public void processMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                               Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue,
                               Update update, Message message) {
        Integer messageId = message.getMessageId();
        log.info("processMessage with id = {}", messageId);
        chatMessageService.saveChatMessage(message, update.hasEditedMessage());
        log.info("message with id = {} is saved", messageId);
        processNext(incomeActionQueue, outcomeActionQueue, update, message);
    }
}
