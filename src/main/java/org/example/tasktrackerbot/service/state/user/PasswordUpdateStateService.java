package org.example.tasktrackerbot.service.state.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.example.tasktrackerbot.session.MessageDeleteScheduler;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PasswordUpdateStateService extends AbstractStateService implements StepHandlerProvider, QueryHandlerProvider {


    public PasswordUpdateStateService(BotService botCommandService,
                                      MessageSender messageSender,
                                      UserStateService userStateService,
                                      ObjectMapper objectMapper,
                                      MessageDeleteScheduler messageDeleteScheduler,
                                      CancelOrReturnKeyboard cancelOrReturnKeyboard,
                                      CancelKeyboard cancelKeyboard) {
        super(botCommandService, messageSender, userStateService, objectMapper, messageDeleteScheduler, cancelOrReturnKeyboard, cancelKeyboard);
    }

    @Override
    public Map<Query, QueryHandler> getQueryHandlers() {
        return Map.of(Query.UPDATE_PASSWORD, this::startPasswordUpdate);
    }

    @Override
    public Map<UserState, StepHandler> getStepHandlers() {
        return Map.of(UserState.USER_CHANGE_AWAITING_PASSWORD, this::handlePasswordStep);
    }

    public void startPasswordUpdate(String chatId) {

        super.start(chatId, UserState.USER_CHANGE_AWAITING_PASSWORD);

    }

    public void handlePasswordStep(String chatId, String password, Integer messageId) {

        super.finishFlow(chatId, messageId);
        botService.updateOwnPassword(chatId, password);

    }

}
