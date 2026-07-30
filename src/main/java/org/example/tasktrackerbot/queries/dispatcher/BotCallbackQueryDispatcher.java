package org.example.tasktrackerbot.queries.dispatcher;

import org.example.tasktrackerbot.queries.CallbackQuery;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BotCallbackQueryDispatcher {

    private final Map<String, CallbackQuery> botQueryMap;

    public BotCallbackQueryDispatcher(List<CallbackQuery> queryList) {
        botQueryMap = queryList.stream()
                .collect(Collectors.toMap(query -> query.getQuery(), query -> query));
    }

    public void dispatchCallbackQuery(Update update, String chatId) {
        String query = update.getCallbackQuery().getData();
        botQueryMap.get(query).execute(chatId);
    }

}
