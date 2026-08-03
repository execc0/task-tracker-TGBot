package org.example.tasktrackerbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegistrationStepService extends AbstractStateService implements StepHandlerProvider {

    public RegistrationStepService(BotService botService,
                                   MessageSender messageSender,
                                   UserStateService userStateService,
                                   ObjectMapper objectMapper,
                                   MessageDeleteScheduler messageDeleteScheduler) {
        super(botService, messageSender, userStateService, objectMapper, messageDeleteScheduler);
    }

    public Map<UserState, StepHandler> getHandlers() {
        return Map.of(UserState.REGISTER_AWAITING_NAME, this::handleNameStep,
                UserState.REGISTER_AWAITING_USERNAME, this::handleUsernameStep,
                UserState.REGISTER_AWAITING_EMAIL, this::handleEmailStep,
                UserState.REGISTER_AWAITING_PASSWORD, this::handlePasswordStep);
    }

    public void startRegistration(String chatId) {
        super.start(chatId, UserState.REGISTER_AWAITING_NAME, "Введите ваше имя:");
    }

    public void handleNameStep(String chatId, String name, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.REGISTER_AWAITING_USERNAME, "name", name, "Введите ваш username: ");
        super.deleteUserMessage(chatId, messageId);
    }

    public void handleUsernameStep(String chatId, String username, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.REGISTER_AWAITING_EMAIL, "username", username, "Введите ваш email: ");
        super.deleteUserMessage(chatId, messageId);
    }

    public void handleEmailStep(String chatId, String email, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.REGISTER_AWAITING_PASSWORD, "email", email, "Введите ваш пароль: ");
        super.deleteUserMessage(chatId, messageId);
    }

    public void handlePasswordStep(String chatId, String password, Integer messageId) {

        userStateService.setTemp(chatId, "password", password);
        UserRegisterRequest request = super.finishFlow(chatId, messageId, UserRegisterRequest.class);
        botService.register(request.getName(), request.getUsername(), request.getEmail(), request.getPassword(), chatId);

    }

}
