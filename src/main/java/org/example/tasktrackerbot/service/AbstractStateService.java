package org.example.tasktrackerbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.MessageDeleteScheduler;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Map;

public abstract class AbstractStateService {


    protected final BotService botService;
    protected final MessageSender messageSender;
    protected final UserStateService userStateService;
    protected final ObjectMapper objectMapper;
    protected final MessageDeleteScheduler messageDeleteScheduler;

    public AbstractStateService(BotService botCommandService, MessageSender messageSender, UserStateService userStateService, ObjectMapper objectMapper, MessageDeleteScheduler messageDeleteScheduler) {
        this.botService = botCommandService;
        this.messageSender = messageSender;
        this.userStateService = userStateService;
        this.objectMapper = objectMapper;
        this.messageDeleteScheduler = messageDeleteScheduler;
    }

    protected void start(String chatId, UserState nextState, String message) {

        if (userStateService.getState(chatId) != UserState.NONE) {
            messageSender.sendMessage(chatId, "Предыдущий диалог отменён");
        }

        userStateService.clearState(chatId);
        userStateService.clearTemp(chatId);
        userStateService.setState(chatId, nextState);
        Integer messageId = messageSender.sendMessage(chatId, message);
        userStateService.setTemp(chatId, "message_id", messageId.toString());

    }

    protected void handleNextStep(String chatId, UserState nextState, String key, String input, String message) {
        userStateService.setState(chatId, nextState);
        userStateService.setTemp(chatId, key, input);
        String messageId = userStateService.getTempField(chatId, "message_id");
        messageSender.editMessage(chatId, messageId, message);
    }

    protected void handleNextStep(String chatId, UserState nextState, String key,
                                  String input, String message, InlineKeyboardMarkup keyboard) {
        userStateService.setState(chatId, nextState);
        userStateService.setTemp(chatId, key, input);
        String messageId = userStateService.getTempField(chatId, "message_id");
        messageSender.editMessage(chatId, messageId, message, keyboard);
    }

    protected <T> T finishFlow(String chatId, Class<T> DTOClass) {

        Map<Object, Object> map = userStateService.getAllTempFields(chatId);
        T request = objectMapper.convertValue(map, DTOClass);
        String messageToDelete = userStateService.getTempField(chatId, "message_id");
        userStateService.clearTemp(chatId);
        userStateService.clearState(chatId);
        scheduleMessageDelete(chatId, messageToDelete, 5);
        return request;

    }

    protected void scheduleMessageDelete(String chatId, String messageId, Integer delaySeconds) {
        messageDeleteScheduler.scheduleDelete(chatId, messageId, delaySeconds);
    }

    protected void scheduleMessageDelete(String chatId, String messageId) {
        messageDeleteScheduler.scheduleDelete(chatId, messageId, 15);
    }

}
