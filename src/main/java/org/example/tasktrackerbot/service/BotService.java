package org.example.tasktrackerbot.service;

import org.example.tasktrackerbot.DTO.request.UserLoginRequest;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;
import org.example.tasktrackerbot.client.TaskTrackerApiClient;
import org.example.tasktrackerbot.responder.MessageSender;
import org.springframework.stereotype.Component;

@Component
public class BotService {

    private final TaskTrackerApiClient taskTrackerApiClient;
    private final MessageSender messageSender;

    public BotService(TaskTrackerApiClient taskTrackerApiClient, MessageSender messageSender) {
        this.taskTrackerApiClient = taskTrackerApiClient;
        this.messageSender = messageSender;
    }

    public void start(String chatId) {
        String message = """
        Привет! Это бот для Task Tracker, сейчас находится в разработке.
        Список доступных команд:
        /register
        /login
        Ссылка на репозиторий API: https://github.com/execc0/task-tracker
        """;
        messageSender.sendMessage(chatId, message);
    }

    public void register(String name, String username, String email, String password, String chatId) {

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(name, username, email, password);

        String message = taskTrackerApiClient.register(userRegisterRequest);

        messageSender.sendMessage(chatId, message);

    }

    public void login(String username, String password, String chatId) {

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername(username);
        userLoginRequest.setPassword(password);

        String message = taskTrackerApiClient.login(userLoginRequest, chatId);

        messageSender.sendMessage(chatId, message);
    }

}