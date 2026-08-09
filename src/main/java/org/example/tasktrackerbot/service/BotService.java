package org.example.tasktrackerbot.service;

import org.example.tasktrackerbot.DTO.API.request.TaskCreateRequest;
import org.example.tasktrackerbot.DTO.API.request.UnlinkSocialRequest;
import org.example.tasktrackerbot.DTO.API.request.UserLoginRequest;
import org.example.tasktrackerbot.DTO.API.request.UserRegisterRequest;
import org.example.tasktrackerbot.DTO.API.request.signable.LinkRequest;
import org.example.tasktrackerbot.DTO.API.request.signable.LoginAndLinkRequest;
import org.example.tasktrackerbot.DTO.API.request.signable.LoginByChatIdRequest;
import org.example.tasktrackerbot.DTO.API.request.signable.RegisterAndLinkRequest;
import org.example.tasktrackerbot.DTO.API.response.PageResponseDTO;
import org.example.tasktrackerbot.DTO.API.response.TaskResponse;
import org.example.tasktrackerbot.client.TaskTrackerApiClient;
import org.example.tasktrackerbot.exception.UserAlreadyAuthorizedException;
import org.example.tasktrackerbot.keyboard.Keyboard;
import org.example.tasktrackerbot.keyboard.KeyboardType;
import org.example.tasktrackerbot.queries.Query;
import org.example.tasktrackerbot.responder.MessageFormatter;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.security.SignatureService;
import org.example.tasktrackerbot.service.navigation.NavigationService;
import org.example.tasktrackerbot.session.MessageDeleteScheduler;
import org.example.tasktrackerbot.session.TokenHandlerService;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class BotService implements QueryHandlerProvider {

    private final TaskTrackerApiClient taskTrackerApiClient;
    private final MessageSender messageSender;
    private final SignatureService signatureService;
    private final TokenHandlerService tokenHandlerService;
    private final Map<KeyboardType, Keyboard> keyboardProviderMap;
    private final MessageDeleteScheduler messageDeleteScheduler;
    private final UserStateService userStateService;
    private final NavigationService navigationService;
    private final MessageFormatter messageFormatter;

    public BotService(TaskTrackerApiClient taskTrackerApiClient,
                      MessageSender messageSender,
                      SignatureService signatureService,
                      TokenHandlerService tokenHandlerService, Map<KeyboardType, Keyboard> keyboardProviderMap,
                      MessageDeleteScheduler messageDeleteScheduler,
                      UserStateService userStateService,
                      NavigationService navigationService, MessageFormatter messageFormatter) {
        this.taskTrackerApiClient = taskTrackerApiClient;
        this.messageSender = messageSender;
        this.signatureService = signatureService;
        this.tokenHandlerService = tokenHandlerService;
        this.keyboardProviderMap = keyboardProviderMap;
        this.messageDeleteScheduler = messageDeleteScheduler;
        this.userStateService = userStateService;
        this.navigationService = navigationService;
        this.messageFormatter = messageFormatter;
    }

    public Map<Query, QueryHandler> getQueryHandlers() {
        return Map.of(Query.GET_TASKS, this::getOwnTasks);
    }


    public void authorizeByChatId(String chatId) {

        if (tokenHandlerService.hasToken(chatId)) {
            return;
        }

        LoginByChatIdRequest loginByChatIdRequest = new LoginByChatIdRequest(chatId, System.currentTimeMillis(), null);
        loginByChatIdRequest.setSignature(signatureService.createSignature(loginByChatIdRequest));

        String token = taskTrackerApiClient.loginByChatId(loginByChatIdRequest);

        tokenHandlerService.saveToken(chatId, token);

    }

    public void start(String chatId) {
        String message = """
        Привет! Это бот для Task Tracker, сейчас находится в разработке.
        Список доступных команд:
        /register
        /login
        /menu
        Ссылка на репозиторий API: https://github.com/execc0/task-tracker
        """;
        Integer menuId = messageSender.sendKeyboardMessage(chatId, message,
                keyboardProviderMap.get(KeyboardType.AUTH_MENU).getKeyboard());
        userStateService.setMenuId(chatId, menuId.toString());

    }




    public void register(String name, String username, String email, String password, String chatId) {

        if (tokenHandlerService.hasToken(chatId)) {
            throw new UserAlreadyAuthorizedException("Вы уже авторизованы. Сначала отвяжите текущий аккаунт командой /unlink",
                    "Ошибка при вызове метода login, пользователь уже авторизован chatId: " + chatId);
        }

        UserRegisterRequest registerRequest = new UserRegisterRequest(name, username, email, password);
        LinkRequest linkRequest =  new LinkRequest("Telegram", chatId, System.currentTimeMillis(), null);
        linkRequest.setSignature(signatureService.createSignature(linkRequest));

        RegisterAndLinkRequest registerAndLinkRequest = new RegisterAndLinkRequest(registerRequest, linkRequest);

        String token = taskTrackerApiClient.registerAndLink(registerAndLinkRequest, chatId);
        tokenHandlerService.saveToken(chatId, token);


        Integer sentMessageId = messageSender.sendMessage(chatId, "Регистрация прошла успешно");
        messageDeleteScheduler.scheduleDelete(chatId, sentMessageId.toString(), 10);
        navigationService.mainMenu(chatId);


    }

    public void getOwnTasks(String chatId) {

        String token = tokenHandlerService.getToken(chatId);

        PageResponseDTO<TaskResponse> pageResponse = taskTrackerApiClient.getOwnTasks(token);

        String tasksFormatted = messageFormatter.formatTaskDTOList(pageResponse.getContent());

        String menuId = userStateService.getMenuId(chatId);

        Integer newMessageId = messageSender.editOrSendNewMessage(chatId, menuId, tasksFormatted,
                keyboardProviderMap.get(KeyboardType.GET_TASKS).getKeyboard());

        userStateService.setMenuId(chatId, newMessageId.toString());

    }

    public void createOwnTask(TaskCreateRequest request, String chatId) {

        String token = tokenHandlerService.getToken(chatId);

        taskTrackerApiClient.createOwnTask(request, token);

        Integer sentMessageId = messageSender.sendMessage(chatId, String.format("Задача с названием %s успешно создана",
                request.getTitle()));

        messageDeleteScheduler.scheduleDelete(chatId, sentMessageId.toString(), 10);

    }

    public void login(String username, String password, String chatId) {

        if (tokenHandlerService.hasToken(chatId)) {
            throw new UserAlreadyAuthorizedException("Вы уже авторизованы. Сначала отвяжите текущий аккаунт командой /unlink",
                    "Ошибка при вызове метода login, пользователь уже авторизован chatId: " + chatId);
        }

        UserLoginRequest userLoginRequest = new UserLoginRequest(username, password);

        LinkRequest linkRequest = new LinkRequest("Telegram", chatId, System.currentTimeMillis(), null);
        linkRequest.setSignature(signatureService.createSignature(linkRequest));

        LoginAndLinkRequest loginAndLinkRequest = new LoginAndLinkRequest(userLoginRequest, linkRequest);

        String token = taskTrackerApiClient.loginAndLink(loginAndLinkRequest, chatId);

        tokenHandlerService.saveToken(chatId, token);

        Integer sentMessageId = messageSender.sendMessage(chatId, "Авторизация прошла успешно");
        navigationService.mainMenu(chatId);

        messageDeleteScheduler.scheduleDelete(chatId, sentMessageId.toString(), 10);

    }

    public void unlink(String username, String password, String chatId) {

        UnlinkSocialRequest unlinkSocialRequest = new UnlinkSocialRequest(username, password, "Telegram", chatId);

        tokenHandlerService.deleteToken(chatId); // удаляем заранее

        taskTrackerApiClient.unlink(unlinkSocialRequest);

        tokenHandlerService.deleteToken(chatId); // на случай ре-авторизации

        Integer sentMessageId = messageSender.sendMessage(chatId, "Ваш аккаунт успешно отвязан");

        messageDeleteScheduler.scheduleDelete(chatId, sentMessageId.toString(), 10);


    }

}