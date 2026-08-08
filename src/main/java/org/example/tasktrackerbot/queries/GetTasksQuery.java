package org.example.tasktrackerbot.queries;

import org.example.tasktrackerbot.queries.flow.FlowCallbackQuery;
import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;

@Component
public class GetTasksQuery implements FlowCallbackQuery {

    private final BotService botService;

    public GetTasksQuery(BotService botService) {
        this.botService = botService;
    }

    @Override
    public String getQuery() {
        return "task:get_list";
    }

    @Override
    public void execute(String chatId) {
        botService.getOwnTasks(chatId);
    }
}
