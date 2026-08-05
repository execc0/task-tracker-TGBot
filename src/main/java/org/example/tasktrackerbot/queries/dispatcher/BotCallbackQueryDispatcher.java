package org.example.tasktrackerbot.queries.dispatcher;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.exception.UserAlreadyAuthorizedException;
import org.example.tasktrackerbot.queries.flow.FlowCallbackQuery;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.service.navigation.NavigationHandler;
import org.example.tasktrackerbot.session.dispatcher.StepHandlerDispatcher;
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
@Slf4j
public class BotCallbackQueryDispatcher {

    private final Map<String, FlowCallbackQuery> botFlowQueryMap;
    private final StepHandlerDispatcher stepHandlerDispatcher;
    private final MessageSender messageSender;
    private final Map<String, NavigationHandler> navigationHandlerMap;

    public BotCallbackQueryDispatcher(List<FlowCallbackQuery> flowQueryList, StepHandlerDispatcher stepHandlerDispatcher,
                                      MessageSender messageSender, Map<String, NavigationHandler> navigationHandlerMap) {
        botFlowQueryMap = flowQueryList.stream()
                .collect(Collectors.toMap(query -> query.getQuery(), query -> query));
        this.stepHandlerDispatcher = stepHandlerDispatcher;
        this.messageSender = messageSender;
        this.navigationHandlerMap = navigationHandlerMap;
    }

    public void dispatchCallbackQuery(Update update, String chatId) {


        String query = update.getCallbackQuery().getData();
        String callBackQueryId = update.getCallbackQuery().getId();
        log.info("Получено нажатие на кнопку из Telegram chatId: {} query: {}", chatId, query);

        // Если callback - Flow (начало новой цепочки диалога) - вызываем нужный Flow обработчик.
        if (botFlowQueryMap.containsKey(query)) {
            botFlowQueryMap.get(query).execute(chatId);
            messageSender.answerCallback(callBackQueryId);
            return;
        }

        if (navigationHandlerMap.containsKey(query)) {
            navigationHandlerMap.get(query).handle(chatId);
            messageSender.answerCallback(callBackQueryId);
            return;
        }

        // Иначе: операция связана с вводом состояния, передаем в stepHandlerDispatcher
        String value = query.split(":")[1];
        stepHandlerDispatcher.dispatchStateInput(value, chatId, null);
        messageSender.answerCallback(callBackQueryId, "Принято!");

    }

}
