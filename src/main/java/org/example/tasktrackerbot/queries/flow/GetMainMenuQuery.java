package org.example.tasktrackerbot.queries.flow;

import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;

@Component
public class GetMainMenuQuery implements  FlowCallbackQuery {

    private final BotService botService;

    public GetMainMenuQuery(BotService botService) {
        this.botService = botService;
    }

    @Override
    public String getQuery() {
        return "menu:main";
    }

    @Override
    public void execute(String chatId) {
        botService.mainMenu(chatId);
    }
}
