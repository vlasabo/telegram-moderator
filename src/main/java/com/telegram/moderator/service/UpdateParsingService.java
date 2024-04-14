package com.telegram.moderator.service;

import com.telegram.moderator.model.IncomeActionType;
import com.telegram.moderator.model.OutcomeActionType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
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
    private final FormMessageService formMessageService;
    private final ChatMessageService chatMessageService;
    @Value("${bot.masterId}")
    private String masterId;

    public void parseUpdate(Update update,
                            Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                            Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue) {
        checkIsNotNull(update);
        Message message = getMessage(update);
        checkIsNotNull(message);
        chatMessageService.saveChatMessage(message, update.hasEditedMessage());
        checkVoiceMessage(incomeActionQueue, outcomeActionQueue, message);
        checkPhotoMessage(incomeActionQueue, outcomeActionQueue, message);
    }

    private Message getMessage(Update update) {
        if (update.hasEditedMessage()) {
            return update.getEditedMessage();
        } else {
            return update.getMessage();
        }
    }

    private void checkPhotoMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                                   Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue,
                                   Message message) {
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
    }

    private void checkVoiceMessage(Queue<Pair<IncomeActionType, Message>> incomeActionQueue,
                                   Queue<Pair<OutcomeActionType, SendMessage>> outcomeActionQueue,
                                   Message message) {
        if (message.hasVoice()) {
            incomeActionQueue.add(Pair.of(IncomeActionType.DELETE, message));
            outcomeActionQueue.add(Pair.of(OutcomeActionType.SEND, getMessageAfterDeleteVoice(message)));
        }
    }

    private SendMessage getMessageAfterDeleteVoice(Message incomeMessage) {
        SendMessage outcomeMessage = formMessageService.getMessage("Никаких голосовых, ублюдок!", incomeMessage.getChatId());
        return formMessageService.addUsernameTag(incomeMessage.getFrom().getUserName(), outcomeMessage);
    }

    private void checkIsNotNull(BotApiObject botApiObject) {
        if (botApiObject == null) {
            throw new RuntimeException("Update is null!");
        }
    }
}
