package org.example.tasktrackerbot.service.state.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.API.request.UserLoginRequest;
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
import org.example.tasktrackerbot.service.navigation.NavigationService;
import org.example.tasktrackerbot.service.state.AbstractStateService;
import org.example.tasktrackerbot.service.state.StepHandler;
import org.example.tasktrackerbot.service.state.StepHandlerProvider;
import org.example.tasktrackerbot.session.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginStepService extends AbstractStateService implements StepHandlerProvider, QueryHandlerProvider {

    private final NavigationService navigationService;

    public LoginStepService(BotService botCommandService,
                            MessageSender messageSender,
                            UserStateService userStateService,
                            ObjectMapper objectMapper,
                            MessageDeleteScheduler messageDeleteScheduler,
                            Map<KeyboardType, Keyboard> keyboardProviderMap,
                            CancelOrReturnKeyboard cancelOrReturnKeyboard,
                            CancelKeyboard cancelKeyboard,
                            MessageFormatter messageFormatter, NavigationService navigationService) {
        super(botCommandService, messageSender, userStateService, objectMapper, messageDeleteScheduler, keyboardProviderMap, cancelOrReturnKeyboard, cancelKeyboard, messageFormatter);
        this.navigationService = navigationService;
    }


    @Override
    public Map<UserState, StepHandler> getStepHandlers() {
        return Map.of(UserState.LOGIN_AWAITING_USERNAME, this::handleUsernameStep,
                UserState.LOGIN_AWAITING_PASSWORD, this::handlePasswordStep);
    }

    @Override
    public Map<Query, QueryHandler> getQueryHandlers() {
        return Map.of(Query.LOGIN, this::startLogin);
    }

    public void startLogin(String chatId) {
        super.start(chatId, UserState.LOGIN_AWAITING_USERNAME);
    }

    public void handleUsernameStep(String chatId, String username) {
        super.handleNextStep(chatId, UserState.LOGIN_AWAITING_PASSWORD, "username", username, "Введите пароль: ");
    }

    public void handlePasswordStep(String chatId, String password) {

        userStateService.setTemp(chatId, "password", password);
        UserLoginRequest request = super.finishFlow(chatId, UserLoginRequest.class);
        botService.login(request.getUsername(), request.getPassword(), chatId);
        navigationService.mainMenu(chatId);


    }


}
