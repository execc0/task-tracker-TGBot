package org.example.tasktrackerbot.commands;

import org.example.tasktrackerbot.service.navigation.NavigationService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MenuCommand implements BotCommand {

    private final NavigationService navigationService;

    public MenuCommand(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @Override
    public String getCommand() {
        return "/menu";
    }

    @Override
    public void execute(Update update) {

        String chatId = update.getMessage().getChatId().toString();

        navigationService.mainMenu(chatId);
    }
}
