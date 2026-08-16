package org.example.tasktrackerbot.service.state.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.API.request.UnlinkSocialRequest;
import org.example.tasktrackerbot.keyboard.CancelKeyboard;
import org.example.tasktrackerbot.keyboard.CancelOrReturnKeyboard;
import org.example.tasktrackerbot.keyboard.Keyboard;
import org.example.tasktrackerbot.keyboard.KeyboardType;
import org.example.tasktrackerbot.queries.Query;
import org.example.tasktrackerbot.responder.MessageFormatter;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.service.QueryHandler;
import org.example.tasktrackerbot.service.QueryHandlerProvider;
import org.example.tasktrackerbot.service.state.AbstractStateService;
import org.example.tasktrackerbot.service.state.StepHandler;
import org.example.tasktrackerbot.service.state.StepHandlerProvider;
import org.example.tasktrackerbot.session.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UnlinkStepService extends AbstractStateService implements StepHandlerProvider, QueryHandlerProvider {


    public UnlinkStepService(BotService botCommandService,
                             MessageSender messageSender,
                             UserStateService userStateService,
                             ObjectMapper objectMapper,
                             MessageDeleteScheduler messageDeleteScheduler,
                             Map<KeyboardType, Keyboard> keyboardProviderMap,
                             CancelOrReturnKeyboard cancelOrReturnKeyboard,
                             CancelKeyboard cancelKeyboard,
                             MessageFormatter messageFormatter) {
        super(botCommandService, messageSender, userStateService, objectMapper,
                messageDeleteScheduler, keyboardProviderMap, cancelOrReturnKeyboard, cancelKeyboard, messageFormatter);
    }

    @Override
    public Map<UserState, StepHandler> getStepHandlers() {
        return Map.of(UserState.UNLINK_AWAITING_USERNAME, this::handleUsernameStep,
                UserState.UNLINK_AWAITING_PASSWORD, this::handlePasswordStep);
    }

    public Map<Query, QueryHandler> getQueryHandlers() {
        return Map.of(Query.UNLINK, this::startUnlink);
    }

    public void startUnlink(String chatId) {
        super.start(chatId, UserState.UNLINK_AWAITING_USERNAME);
    }

    public void handleUsernameStep(String chatId, String username) {
        super.handleNextStep(chatId, UserState.UNLINK_AWAITING_PASSWORD, "username", username, "Введите пароль: ");
    }

    public void handlePasswordStep(String chatId, String password) {

        userStateService.setTemp(chatId, "password", password);
        UnlinkSocialRequest request = super.finishFlow(chatId, UnlinkSocialRequest.class);
        botService.unlink(request.getUsername(), request.getPassword(), chatId);


    }

}