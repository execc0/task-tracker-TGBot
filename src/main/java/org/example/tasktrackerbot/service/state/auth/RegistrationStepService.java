package org.example.tasktrackerbot.service.state.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.API.request.UserRegisterRequest;
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
public class RegistrationStepService extends AbstractStateService implements StepHandlerProvider, QueryHandlerProvider {

    private final NavigationService navigationService;

    public RegistrationStepService(BotService botCommandService,
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


    public Map<UserState, StepHandler> getStepHandlers() {
        return Map.of(UserState.REGISTER_AWAITING_NAME, this::handleNameStep,
                UserState.REGISTER_AWAITING_USERNAME, this::handleUsernameStep,
                UserState.REGISTER_AWAITING_EMAIL, this::handleEmailStep,
                UserState.REGISTER_AWAITING_PASSWORD, this::handlePasswordStep);
    }

    public Map<Query, QueryHandler> getQueryHandlers() {
        return Map.of(Query.REGISTER, this::startRegistration);
    }

    public void startRegistration(String chatId) {
        super.start(chatId, UserState.REGISTER_AWAITING_NAME);
    }

    public void handleNameStep(String chatId, String name, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.REGISTER_AWAITING_USERNAME, "name", name, "Введите ваш username: ");
    }

    public void handleUsernameStep(String chatId, String username, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.REGISTER_AWAITING_EMAIL, "username", username, "Введите ваш email: ");
    }

    public void handleEmailStep(String chatId, String email, Integer messageId) {
        super.handleNextStep(chatId, messageId, UserState.REGISTER_AWAITING_PASSWORD, "email", email, "Введите ваш пароль: ");
    }

    public void handlePasswordStep(String chatId, String password, Integer messageId) {

        userStateService.setTemp(chatId, "password", password);
        UserRegisterRequest request = super.finishFlow(chatId, messageId, UserRegisterRequest.class);
        botService.register(request.getName(), request.getUsername(), request.getEmail(), request.getPassword(), chatId);
        navigationService.mainMenu(chatId);

    }

}
