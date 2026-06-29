package org.example.tasktrackerbot.commands;

import org.example.tasktrackerbot.client.TaskTrackerApiClient;
import org.example.tasktrackerbot.handler.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class RegisterCommand implements BotCommand {

    private final CommandHandler commandHandler;

    public RegisterCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public String getCommand() {
        return "/register";
    }

    @Override
    public String execute(Update update) {
        return commandHandler.register(update);
    }

}
