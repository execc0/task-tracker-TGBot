package org.example.tasktrackerbot.handler;

import org.example.tasktrackerbot.DTO.request.UserLoginRequest;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;
import org.example.tasktrackerbot.DTO.response.UserResponse;
import org.example.tasktrackerbot.client.TaskTrackerApiClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CommandHandler {

    private final TaskTrackerApiClient taskTrackerApiClient;

    public CommandHandler(TaskTrackerApiClient taskTrackerApiClient) {
        this.taskTrackerApiClient = taskTrackerApiClient;
    }

    public String start(Update update) {
        return """
        Привет! Это бот для Task Tracker, сейчас находится в разработке.
        Список доступных команд:
        /register
        /login
        Ссылка на репозиторий API: https://github.com/execc0/task-tracker
        """;
    }

    public String register(Update update) {

        String[] textMessageWords = update.getMessage().getText().trim().split(" ");

        if (textMessageWords.length != 5) {
            return """
                    Ошибка! Введён неверный формат строки для команды /register
                    Верный формат: /register email name username password
                    Пример: /register example@example.org ExampleName ExampleUsername Password123
                    """;
        }

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail(textMessageWords[1]);
        userRegisterRequest.setName(textMessageWords[2]);
        userRegisterRequest.setUsername(textMessageWords[3]);
        userRegisterRequest.setPassword(textMessageWords[4]);

        return taskTrackerApiClient.register(userRegisterRequest);

    }

    public String login(Update update) {

        String[] textMessageWords = update.getMessage().getText().trim().split(" ");

        if(textMessageWords.length != 3) {
            return """
                    Ошибка! Введён неверный формат строки для команды /login
                    Верный формат: /login username password
                    Пример: /register ExampleUsername Password123
                    """;
        }

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername(textMessageWords[1]);
        userLoginRequest.setPassword(textMessageWords[2]);

        return taskTrackerApiClient.login(userLoginRequest);

    }

}
