package org.example.tasktrackerbot.service.state.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.API.request.UnlinkSocialRequest;
import org.example.tasktrackerbot.keyboard.CancelKeyboard;
import org.example.tasktrackerbot.keyboard.CancelOrReturnKeyboard;
import org.example.tasktrackerbot.queries.Query;
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



    public UnlinkStepService(UserStateService userStateService,
                             MessageSender messageSender,
                             ObjectMapper objectMapper,
                             BotService botService,
                             MessageDeleteScheduler messageDeleteScheduler,
                             CancelOrReturnKeyboard cancelOrReturnKeyboard,
                             CancelKeyboard cancelKeyboard) {
        super(botService, messageSender, userStateService, objectMapper,
                messageDeleteScheduler, cancelOrReturnKeyboard, cancelKeyboard);
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

    public void handleUsernameStep(String chatId, String username, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.UNLINK_AWAITING_PASSWORD, "username", username, "Введите пароль: ");
    }

    public void handlePasswordStep(String chatId, String password, Integer messageId) {

        userStateService.setTemp(chatId, "password", password);
        UnlinkSocialRequest request = super.finishFlow(chatId, messageId, UnlinkSocialRequest.class);
        botService.unlink(request.getUsername(), request.getPassword(), chatId);


    }

}