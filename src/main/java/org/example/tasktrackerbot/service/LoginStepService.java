package org.example.tasktrackerbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.request.UserLoginRequest;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginStepService extends AbstractStateService implements StepHandlerProvider {

    public LoginStepService(UserStateService userStateService,
                            MessageSender messageSender,
                            ObjectMapper objectMapper,
                            BotService botService,
                            MessageDeleteScheduler messageDeleteScheduler) {
        super(botService, messageSender, userStateService, objectMapper, messageDeleteScheduler);
    }

    @Override
    public Map<UserState, StepHandler> getHandlers() {
        return Map.of(UserState.LOGIN_AWAITING_USERNAME, this::handleUsernameStep,
                UserState.LOGIN_AWAITING_PASSWORD, this::handlePasswordStep);
    }

    public void startLogin(String chatId) {
        super.start(chatId, UserState.LOGIN_AWAITING_USERNAME, "Введите ваш username: ");
    }

    public void handleUsernameStep(String chatId, String username, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.LOGIN_AWAITING_PASSWORD, "username", username, "Введите пароль: ");
        super.deleteUserMessage(chatId, messageId);
    }

    public void handlePasswordStep(String chatId, String password, Integer messageId) {

        userStateService.setTemp(chatId, "password", password);
        UserLoginRequest request = super.finishFlow(chatId, messageId, UserLoginRequest.class);
        Integer botMessageId = botService.login(request.getUsername(), request.getPassword(), chatId);
        super.scheduleMessageDelete(chatId, botMessageId.toString());
        super.deleteUserMessage(chatId, messageId);

    }

}
