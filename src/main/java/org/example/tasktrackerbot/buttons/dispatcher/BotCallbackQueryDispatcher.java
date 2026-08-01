package org.example.tasktrackerbot.buttons.dispatcher;

import org.example.tasktrackerbot.buttons.ActionCallbackQuery;
import org.example.tasktrackerbot.buttons.FlowCallbackQuery;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BotCallbackQueryDispatcher {

    private final Map<String, FlowCallbackQuery> botFlowQueryMap;
    private final Map<String, ActionCallbackQuery> botActionQueryMap;

    public BotCallbackQueryDispatcher(List<FlowCallbackQuery> flowQueryList, List<ActionCallbackQuery> actionQueryList) {
        botFlowQueryMap = flowQueryList.stream()
                .collect(Collectors.toMap(query -> query.getQuery(), query -> query));
        botActionQueryMap = actionQueryList.stream()
                .collect(Collectors.toMap(query -> query.getQuery(), query -> query));
    }

    public void dispatchCallbackQuery(Update update, String chatId) {
        String query = update.getCallbackQuery().getData();

        if (botFlowQueryMap.containsKey(query)) {
            botFlowQueryMap.get(query).execute(chatId);
            return;
        }
        String actionQuery = query.split(":")[0];
        String value = query.split(":")[1];
        if (botActionQueryMap.containsKey(actionQuery)) {
            botActionQueryMap.get(actionQuery).execute(chatId, value);
            return;
        }
        throw new RuntimeException("Не найден нужный обработик для кнопки, query: " + query);

    }

}
