package org.example.tasktrackerbot.commands;

import org.example.tasktrackerbot.handler.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class LoginCommand implements BotCommand {

    private final CommandHandler commandHandler;

    public LoginCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public String getCommand() {
        return "/login";
    }

    @Override
    public String execute(Update update) {
        return commandHandler.login(update);
    }
}
