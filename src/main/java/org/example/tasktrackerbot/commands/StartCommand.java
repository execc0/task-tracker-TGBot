package org.example.tasktrackerbot.commands;

import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.service.navigation.NavigationService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommand implements BotCommand {

    private final BotService botService;

    public StartCommand(BotService botService) {
        this.botService = botService;
    }

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public void execute(Update update) {
        botService.start(update.getMessage().getChatId().toString());
    }
}
