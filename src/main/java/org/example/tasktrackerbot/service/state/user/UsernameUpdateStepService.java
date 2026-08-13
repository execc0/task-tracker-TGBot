package org.example.tasktrackerbot.service.state.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.example.tasktrackerbot.session.MessageDeleteScheduler;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UsernameUpdateStepService extends AbstractStateService implements StepHandlerProvider, QueryHandlerProvider {


    public UsernameUpdateStepService(BotService botCommandService,
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
    public Map<Query, QueryHandler> getQueryHandlers() {
        return Map.of(Query.UPDATE_USERNAME, this::startUsernameChange);
    }

    @Override
    public Map<UserState, StepHandler> getStepHandlers() {
        return Map.of(UserState.USER_CHANGE_AWAITING_USERNAME, this::handleUsernameStep);
    }

    private void startUsernameChange(String chatId) {

        super.start(chatId, UserState.USER_CHANGE_AWAITING_USERNAME);

    }

    private void handleUsernameStep(String chatId, String username, Integer messageId) {

        super.finishFlow(chatId, messageId);
        botService.updateOwnUsername(chatId, username);

    }

}
