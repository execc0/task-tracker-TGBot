package org.example.tasktrackerbot.commands;

import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MenuCommand implements BotCommand {

    private final BotService botService;

    public MenuCommand(BotService botService) {
        this.botService = botService;
    }

    @Override
    public String getCommand() {
        return "/menu";
    }

    @Override
    public void execute(Update update) {

        String chatId = update.getMessage().getChatId().toString();

        botService.mainMenu(chatId);
    }
}
