package org.example.tasktrackerbot.commands;

import org.example.tasktrackerbot.client.TaskTrackerApiClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class LoginCommand implements BotCommand {

    private final TaskTrackerApiClient taskTrackerApiClient;

    public LoginCommand(TaskTrackerApiClient taskTrackerApiClient) {
        this.taskTrackerApiClient = taskTrackerApiClient;
    }

    @Override
    public String getCommand() {
        return "/login";
    }

    @Override
    public String execute(Update update) {
        return taskTrackerApiClient.login(update);
    }
}
