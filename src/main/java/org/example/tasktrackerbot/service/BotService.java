package org.example.tasktrackerbot.service;

import org.example.tasktrackerbot.DTO.request.UserLoginRequest;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;
import org.example.tasktrackerbot.client.TaskTrackerApiClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BotService {

    private final TaskTrackerApiClient taskTrackerApiClient;

    public BotService(TaskTrackerApiClient taskTrackerApiClient) {
        this.taskTrackerApiClient = taskTrackerApiClient;
    }

    public String start() {
        return """
        Привет! Это бот для Task Tracker, сейчас находится в разработке.
        Список доступных команд:
        /register
        /login
        Ссылка на репозиторий API: https://github.com/execc0/task-tracker
        """;
    }

    public String register(String email, String name, String username, String password) {

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(name, username, email, password);

        return taskTrackerApiClient.register(userRegisterRequest);

    }

    public String login(String username, String password) {


        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername(username);
        userLoginRequest.setPassword(password);

        return taskTrackerApiClient.login(userLoginRequest);

    }

}