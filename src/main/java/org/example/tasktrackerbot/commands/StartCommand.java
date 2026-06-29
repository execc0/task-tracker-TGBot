package org.example.tasktrackerbot.commands;

import org.example.tasktrackerbot.client.TaskTrackerApiClient;
import org.example.tasktrackerbot.handler.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommand implements BotCommand {

    private final CommandHandler commandHandler;

    public StartCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public String execute(Update update) {
        return commandHandler.start(update);
    }
}
