package org.example.tasktrackerbot.queries.dispatcher;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.QueryHandler;
import org.example.tasktrackerbot.session.dispatcher.StepHandlerDispatcher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Dispatcher который отвечает за нахождение нужного обработчика для CallbackQuery (нажатий на кнопки)
 * Кнопки делятся на два типа: те, что начинают новую цепочку диалога и те, что отвечают за выбор параметра внутри диалога.
 */
@Component
@Slf4j
public class BotCallbackQueryDispatcher {

    private final Map<String, QueryHandler> queryHandlerMap;
    private final StepHandlerDispatcher stepHandlerDispatcher;
    private final MessageSender messageSender;

    public BotCallbackQueryDispatcher(Map<String, QueryHandler> queryHandlerMap, StepHandlerDispatcher stepHandlerDispatcher,
                                      MessageSender messageSender) {
        this.queryHandlerMap = queryHandlerMap;

        this.stepHandlerDispatcher = stepHandlerDispatcher;
        this.messageSender = messageSender;
    }

    public void dispatchCallbackQuery(Update update, String chatId) {

        log.info("Получено нажатие на кнопку из Telegram chatId: {} query: {}", chatId, update.getCallbackQuery().getData());

        String query = update.getCallbackQuery().getData();
        String callBackQueryId = update.getCallbackQuery().getId();

        dispatchCallbackQuery(query, chatId, callBackQueryId);

    }

    public void dispatchCallbackQuery(String query, String chatId, String callBackQueryId) {

        // Обработка нажатий на кнопки (навигация или начало новой цепочки диалога)
        if (queryHandlerMap.containsKey(query)) {
            queryHandlerMap.get(query).handle(chatId);
            messageSender.answerCallback(callBackQueryId);
            return;
        }

        // Иначе: операция связана с вводом состояния, передаем в stepHandlerDispatcher
        String value = query.split(":")[1];
        stepHandlerDispatcher.dispatchStateInput(value, chatId, null);
        messageSender.answerCallback(callBackQueryId, "Принято!");

    }

}
