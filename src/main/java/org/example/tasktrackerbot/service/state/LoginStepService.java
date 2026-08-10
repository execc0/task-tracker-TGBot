package org.example.tasktrackerbot.service.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.API.request.UserLoginRequest;
import org.example.tasktrackerbot.keyboard.CancelKeyboard;
import org.example.tasktrackerbot.keyboard.CancelOrReturnKeyboard;
import org.example.tasktrackerbot.queries.Query;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.service.QueryHandler;
import org.example.tasktrackerbot.service.QueryHandlerProvider;
import org.example.tasktrackerbot.service.navigation.NavigationService;
import org.example.tasktrackerbot.session.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginStepService extends AbstractStateService implements StepHandlerProvider, QueryHandlerProvider {

    private final NavigationService navigationService;

    public LoginStepService(UserStateService userStateService,
                            MessageSender messageSender,
                            ObjectMapper objectMapper,
                            BotService botService,
                            MessageDeleteScheduler messageDeleteScheduler,
                            CancelOrReturnKeyboard cancelOrReturnKeyboard,
                            CancelKeyboard cancelKeyboard, NavigationService navigationService) {
        super(botService, messageSender, userStateService, objectMapper, messageDeleteScheduler, cancelOrReturnKeyboard, cancelKeyboard);
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
        super.start(chatId, UserState.LOGIN_AWAITING_USERNAME, "Введите ваш username: ");
    }

    public void handleUsernameStep(String chatId, String username, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.LOGIN_AWAITING_PASSWORD, "username", username, "Введите пароль: ");
    }

    public void handlePasswordStep(String chatId, String password, Integer messageId) {

        userStateService.setTemp(chatId, "password", password);
        UserLoginRequest request = super.finishFlow(chatId, messageId, UserLoginRequest.class);
        botService.login(request.getUsername(), request.getPassword(), chatId);
        navigationService.mainMenu(chatId);


    }


}
