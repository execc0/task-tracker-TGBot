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
public class NameUpdateStateService extends AbstractStateService implements QueryHandlerProvider, StepHandlerProvider {

    public NameUpdateStateService(BotService botCommandService,
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
        return Map.of(Query.UPDATE_NAME, this::startNameUpdate);
    }

    @Override
    public Map<UserState, StepHandler> getStepHandlers() {
        return Map.of(UserState.USER_CHANGE_AWAITING_NAME, this::handleNameStep);
    }

    public void startNameUpdate(String chatId) {

        super.start(chatId, UserState.USER_CHANGE_AWAITING_NAME);

    }

    public void handleNameStep(String chatId, String name, Integer userMessageId) {

        super.finishFlow(chatId, userMessageId);
        botService.updateOwnName(chatId, name);

    }

}
