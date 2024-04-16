package com.telegram.moderator.model.processor;

import com.telegram.moderator.model.IncomeActionType;
import com.telegram.moderator.model.OutcomeActionType;
import com.telegram.moderator.service.FormMessageService;
import lombok.RequiredArgsConstructor;
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
public class VoiceMessageProcessor extends AbstractMessageProcessor {
    private final FormMessageService formMessageService;

    @Override
    public void processMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                               Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue,
                               Update update) {
        if (update.getMessage().hasVoice()) {
            incomeActionQueue.add(Pair.of(IncomeActionType.DELETE, update.getMessage()));
            outcomeActionQueue.add(Pair.of(OutcomeActionType.SEND, getMessageAfterDeleteVoice(update.getMessage())));
        }
        processNext(incomeActionQueue, outcomeActionQueue, update);
    }

    private SendMessage getMessageAfterDeleteVoice(Message incomeMessage) {
        SendMessage outcomeMessage = formMessageService.getMessage("Никаких голосовых, ублюдок!", incomeMessage.getChatId());
        return formMessageService.addUsernameTag(incomeMessage.getFrom().getUserName(), outcomeMessage);
    }
}
