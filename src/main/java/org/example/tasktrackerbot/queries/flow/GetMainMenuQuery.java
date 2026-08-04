package org.example.tasktrackerbot.queries.flow;

import org.example.tasktrackerbot.service.navigation.NavigationService;
import org.springframework.stereotype.Component;

@Component
public class GetMainMenuQuery implements  FlowCallbackQuery {

    private final NavigationService navigationService;

    public GetMainMenuQuery(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @Override
    public String getQuery() {
        return "menu:main";
    }

    @Override
    public void execute(String chatId) {
        navigationService.mainMenu(chatId);
    }
}
