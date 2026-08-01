package org.example.tasktrackerbot.service;

import org.example.tasktrackerbot.DTO.request.*;
import org.example.tasktrackerbot.DTO.request.signable.LinkRequest;
import org.example.tasktrackerbot.DTO.request.signable.LoginAndLinkRequest;
import org.example.tasktrackerbot.DTO.request.signable.LoginByChatIdRequest;
import org.example.tasktrackerbot.DTO.request.signable.RegisterAndLinkRequest;
import org.example.tasktrackerbot.client.TaskTrackerApiClient;
import org.example.tasktrackerbot.keyboard.AuthKeyboard;
import org.example.tasktrackerbot.keyboard.MainMenuKeyboard;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.security.SignatureService;
import org.example.tasktrackerbot.session.TokenHandlerService;
import org.springframework.stereotype.Component;

@Component
public class BotService {

    private final TaskTrackerApiClient taskTrackerApiClient;
    private final MessageSender messageSender;
    private final SignatureService signatureService;
    private final TokenHandlerService tokenHandlerService;
    private final AuthKeyboard authKeyboard;
    private final MainMenuKeyboard mainMenuKeyboard;

    public BotService(TaskTrackerApiClient taskTrackerApiClient, MessageSender messageSender, SignatureService signatureService, TokenHandlerService tokenHandlerService, AuthKeyboard authKeyboard, MainMenuKeyboard mainMenuKeyboard) {
        this.taskTrackerApiClient = taskTrackerApiClient;
        this.messageSender = messageSender;
        this.signatureService = signatureService;
        this.tokenHandlerService = tokenHandlerService;
        this.authKeyboard = authKeyboard;
        this.mainMenuKeyboard = mainMenuKeyboard;
    }

    public boolean isAuthorized(String chatId) {
        boolean isAuthorized = true;
        try {
            authorizeByChatId(chatId);
        } catch (Exception exception){
            isAuthorized = false;
        }
        return isAuthorized;
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
        messageSender.sendKeyboardMessage(chatId, message, authKeyboard.getKeyboard());
    }

    public void mainMenu(String chatId) {

        String message = """
                Основное меню. Операции представлены ниже:
                """;
        messageSender.sendKeyboardMessage(chatId, message, mainMenuKeyboard.getKeyboard());

    }


    public void register(String name, String username, String email, String password, String chatId) {

        if (tokenHandlerService.hasToken(chatId)) {
            messageSender.sendMessage(chatId, "Вы уже авторизованы. Сначала отвяжите текущий аккаунт командой /unlink");
            return;
        }

        UserRegisterRequest registerRequest = new UserRegisterRequest(name, username, email, password);
        LinkRequest linkRequest =  new LinkRequest("Telegram", chatId, System.currentTimeMillis(), null);
        linkRequest.setSignature(signatureService.createSignature(linkRequest));

        RegisterAndLinkRequest registerAndLinkRequest = new RegisterAndLinkRequest(registerRequest, linkRequest);

        String token = taskTrackerApiClient.registerAndLink(registerAndLinkRequest, chatId);
        tokenHandlerService.saveToken(chatId, token);


        messageSender.sendMessage(chatId, "Регистрация прошла успешно");
        messageSender.sendKeyboardMessage(chatId, "Основное меню: ", mainMenuKeyboard.getKeyboard());

    }

    public void createOwnTask(TaskCreateRequest request, String chatId) {

        String token = tokenHandlerService.getToken(chatId);

        taskTrackerApiClient.createOwnTask(request, token);

        messageSender.sendMessage(chatId, String.format("Задача с названием %s успешно создана", request.getTitle()));

    }

    public void login(String username, String password, String chatId) {

        if (tokenHandlerService.hasToken(chatId)) {
            messageSender.sendMessage(chatId, "Вы уже авторизованы. Сначала отвяжите текущий аккаунт командой /unlink");
            return;
        }

        UserLoginRequest userLoginRequest = new UserLoginRequest(username, password);

        LinkRequest linkRequest = new LinkRequest("Telegram", chatId, System.currentTimeMillis(), null);
        linkRequest.setSignature(signatureService.createSignature(linkRequest));

        LoginAndLinkRequest loginAndLinkRequest = new LoginAndLinkRequest(userLoginRequest, linkRequest);

        String token = taskTrackerApiClient.loginAndLink(loginAndLinkRequest, chatId);

        tokenHandlerService.saveToken(chatId, token);

        messageSender.sendMessage(chatId, "Авторизация прошла успешно");
        messageSender.sendKeyboardMessage(chatId, "Основное меню: ", mainMenuKeyboard.getKeyboard());

    }

    public void unlink(String username, String password, String chatId) {

        UnlinkSocialRequest unlinkSocialRequest = new UnlinkSocialRequest(username, password, "Telegram", chatId);

        tokenHandlerService.deleteToken(chatId); // удаляем заранее

        taskTrackerApiClient.unlink(unlinkSocialRequest);

        tokenHandlerService.deleteToken(chatId); // на случай ре-авторизации

        messageSender.sendMessage(chatId, "Ваш аккаунт успешно отвязан");


    }

}