package org.example.tasktrackerbot.commands;

import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommand implements BotCommand {

    private final BotService botCommandService;

    public StartCommand(BotService botCommandService) {
        this.botCommandService = botCommandService;
    }

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public void execute(Update update) {
        botCommandService.start(update.getMessage().getChatId().toString());
    }
}
