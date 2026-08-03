package org.example.tasktrackerbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.request.UnlinkSocialRequest;
import org.example.tasktrackerbot.DTO.request.UserLoginRequest;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UnlinkStepService extends AbstractStateService implements StepHandlerProvider {

    public UnlinkStepService(UserStateService userStateService, MessageSender messageSender,
                             ObjectMapper objectMapper, BotService botService, MessageDeleteScheduler messageDeleteScheduler) {
        super(botService, messageSender, userStateService, objectMapper, messageDeleteScheduler);
    }

    @Override
    public Map<UserState, StepHandler> getHandlers() {
        return Map.of(UserState.UNLINK_AWAITING_USERNAME, this::handleUsernameStep,
                UserState.UNLINK_AWAITING_PASSWORD, this::handlePasswordStep);
    }

    public void startUnlink(String chatId) {
        super.start(chatId, UserState.UNLINK_AWAITING_USERNAME, "Введите ваш username: ");
    }

    public void handleUsernameStep(String chatId, String username, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.UNLINK_AWAITING_PASSWORD, "username", username, "Введите пароль: ");
        super.deleteUserMessage(chatId, messageId);
    }

    public void handlePasswordStep(String chatId, String password, Integer messageId) {

        userStateService.setTemp(chatId, "password", password);
        UnlinkSocialRequest request = super.finishFlow(chatId, messageId, UnlinkSocialRequest.class);
        Integer botMessageId = botService.unlink(request.getUsername(), request.getPassword(), chatId);
        super.scheduleMessageDelete(chatId, botMessageId.toString());
        super.deleteUserMessage(chatId, messageId);

    }

}