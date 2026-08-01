package org.example.tasktrackerbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.StepHandler;
import org.example.tasktrackerbot.session.StepHandlerProvider;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegistrationStepService implements StepHandlerProvider {

    private final BotService botCommandService;
    private final MessageSender messageSender;
    private final UserStateService userStateService;
    private final ObjectMapper objectMapper;

    public RegistrationStepService(BotService botCommandService, MessageSender messageSender, UserStateService userStateService, ObjectMapper objectMapper) {
        this.botCommandService = botCommandService;
        this.messageSender = messageSender;
        this.userStateService = userStateService;
        this.objectMapper = objectMapper;
    }

    public Map<UserState, StepHandler> getHandlers() {

        return Map.of(UserState.REGISTER_AWAITING_NAME, this::handleNameStep,
                UserState.REGISTER_AWAITING_USERNAME, this::handleUsernameStep,
                UserState.REGISTER_AWAITING_EMAIL, this::handleEmailStep,
                UserState.REGISTER_AWAITING_PASSWORD, this::handlePasswordStep);
    }

    public void startRegistration(String chatId) {

        if (userStateService.getState(chatId) != UserState.NONE) {
            messageSender.sendMessage(chatId, "Предыдущий диалог отменён");
        }

        userStateService.clearState(chatId);
        userStateService.clearTemp(chatId);
        userStateService.setState(chatId, UserState.REGISTER_AWAITING_NAME);
        messageSender.sendMessage(chatId, "Введите ваше имя: ");

    }

    public void handleNameStep(String chatId, String name) {
        userStateService.setState(chatId, UserState.REGISTER_AWAITING_USERNAME);
        userStateService.setTemp(chatId, "name", name);
        messageSender.sendMessage(chatId, "Введите ваш username: ");

    }

    public void handleUsernameStep(String chatId, String username) {
        userStateService.setState(chatId, UserState.REGISTER_AWAITING_EMAIL);
        userStateService.setTemp(chatId, "username", username);
        messageSender.sendMessage(chatId, "Введите ваш email: ");
    }

    public void handleEmailStep(String chatId, String email) {
        userStateService.setState(chatId, UserState.REGISTER_AWAITING_PASSWORD);
        userStateService.setTemp(chatId, "email", email);
        messageSender.sendMessage(chatId, "Введите ваш пароль: ");
    }

    public void handlePasswordStep(String chatId, String password) {
        userStateService.setTemp(chatId, "password", password);
        Map<Object, Object> map = userStateService.getAllTempFields(chatId);
        UserRegisterRequest request = objectMapper.convertValue(map, UserRegisterRequest.class);
        botCommandService.register(request.getName(), request.getUsername(), request.getEmail(), request.getPassword(), chatId);
        userStateService.clearTemp(chatId);
        userStateService.clearState(chatId);
    }

}
