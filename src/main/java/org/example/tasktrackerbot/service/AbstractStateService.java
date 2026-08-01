package org.example.tasktrackerbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Map;

public abstract class AbstractStateService {


    protected final BotService botService;
    protected final MessageSender messageSender;
    protected final UserStateService userStateService;
    protected final ObjectMapper objectMapper;

    public AbstractStateService(BotService botCommandService, MessageSender messageSender, UserStateService userStateService, ObjectMapper objectMapper) {
        this.botService = botCommandService;
        this.messageSender = messageSender;
        this.userStateService = userStateService;
        this.objectMapper = objectMapper;
    }

    protected void start(String chatId, UserState nextState, String message) {

        if (userStateService.getState(chatId) != UserState.NONE) {
            messageSender.sendMessage(chatId, "Предыдущий диалог отменён");
        }

        userStateService.clearState(chatId);
        userStateService.clearTemp(chatId);
        userStateService.setState(chatId, nextState);
        messageSender.sendMessage(chatId, message);

    }

    protected void handleNextStep(String chatId, UserState nextState, String key, String input, String message) {
        userStateService.setState(chatId, nextState);
        userStateService.setTemp(chatId, key, input);
        messageSender.sendMessage(chatId, message);
    }

    protected void handleNextStep(String chatId, UserState nextState, String key,
                                  String input, String message, InlineKeyboardMarkup keyboard) {
        userStateService.setState(chatId, nextState);
        userStateService.setTemp(chatId, key, input);
        messageSender.sendKeyboardMessage(chatId, message, keyboard);
    }

    protected <T> T finishFlow(String chatId, Class<T> DTOClass) {

        Map<Object, Object> map = userStateService.getAllTempFields(chatId);
        T request = objectMapper.convertValue(map, DTOClass);
        userStateService.clearTemp(chatId);
        userStateService.clearState(chatId);
        return request;

    }

}
