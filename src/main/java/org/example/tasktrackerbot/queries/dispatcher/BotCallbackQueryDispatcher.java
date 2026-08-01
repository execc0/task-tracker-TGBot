package org.example.tasktrackerbot.queries.dispatcher;

import org.example.tasktrackerbot.queries.flow.FlowCallbackQuery;
import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.session.StepHandlerDispatcher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dispatcher который отвечает за нахождение нужного обработчика для CallbackQuery (нажатий на кнопки)
 * Кнопки делятся на два типа: те, что начинают новую цепочку диалога и те, что отвечают за выбор параметра внутри диалога.
 */
@Component
public class BotCallbackQueryDispatcher {

    private final Map<String, FlowCallbackQuery> botFlowQueryMap;
    private final StepHandlerDispatcher stepHandlerDispatcher;
    private final BotService botService;

    public BotCallbackQueryDispatcher(List<FlowCallbackQuery> flowQueryList, StepHandlerDispatcher stepHandlerDispatcher, BotService botService) {
        botFlowQueryMap = flowQueryList.stream()
                .collect(Collectors.toMap(query -> query.getQuery(), query -> query));
        this.stepHandlerDispatcher = stepHandlerDispatcher;
        this.botService = botService;
    }

    public void dispatchCallbackQuery(Update update, String chatId) {
        String query = update.getCallbackQuery().getData();

        botService.authorizeByChatId(chatId);

        // Если callback - Flow (начало новой цепочки диалога) - вызываем нужный Flow обработчик.
        if (botFlowQueryMap.containsKey(query)) {
            botFlowQueryMap.get(query).execute(chatId);
            return;
        }

        // Иначе: операция связана с вводом состояния, передаем в stepHandler
        String value = query.split(":")[1];
        stepHandlerDispatcher.dispatchStateInput(value, chatId);

    }

}
