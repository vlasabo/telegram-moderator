package com.telegram.moderator.model.processor;

import com.telegram.moderator.model.IncomeActionType;
import com.telegram.moderator.model.OutcomeActionType;
import com.telegram.moderator.service.FormMessageService;
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
public class VoiceMessageProcessor extends AbstractMessageProcessor {
    private final FormMessageService formMessageService;

    @Override
    public void processMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                               Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue,
                               Update update, Message message) {
        Integer messageId = message.getMessageId();
        log.info("processMessage with id = {}", messageId);
        if (message.hasVoice()) {
            processVoiceMessage(incomeActionQueue, outcomeActionQueue, message, messageId);
        } else {
            log.info("message with id = {} doesn't contain a voiceMessage", messageId);
        }
        processNext(incomeActionQueue, outcomeActionQueue, update, message);
    }

    private void processVoiceMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue, Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue, Message message, Integer messageId) {
        incomeActionQueue.add(Pair.of(IncomeActionType.DELETE, message));
        log.info("add message with id = {} to delete in queue!", messageId);
        SendMessage messageAfterDeleteVoice = getMessageAfterDeleteVoice(message);
        outcomeActionQueue.add(Pair.of(OutcomeActionType.SEND, messageAfterDeleteVoice));
        log.info("add new message to send in queue! Text is \"{}\"", messageAfterDeleteVoice.getText());
    }

    private SendMessage getMessageAfterDeleteVoice(Message incomeMessage) {
        SendMessage outcomeMessage = formMessageService.getMessage("Никаких голосовых, ублюдок!", incomeMessage.getChatId());
        return formMessageService.addUsernameTag(incomeMessage.getFrom().getUserName(), outcomeMessage);
    }
}
