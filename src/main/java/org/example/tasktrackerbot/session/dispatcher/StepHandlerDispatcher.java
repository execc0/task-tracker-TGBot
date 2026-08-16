package org.example.tasktrackerbot.session.dispatcher;

import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.state.StepHandler;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Member;
import java.util.Map;

/**
 * Класс, который отвечает за нахождение обработчика сообщений, завязанных на состоянии.
 */
@Component
public class StepHandlerDispatcher {

    private final Map<UserState, StepHandler> stepHandlersMap;
    private final UserStateService userStateService;
    private final MessageSender messageSender;

    public StepHandlerDispatcher(Map<UserState, StepHandler> stepHandlersMap, UserStateService userStateService, MessageSender messageSender) {
        this.stepHandlersMap = stepHandlersMap;
        this.userStateService = userStateService;
        this.messageSender = messageSender;
    }

    public void dispatchStateInput(String input, String chatId, Integer messageId) {

        if (messageId != null) {
            messageSender.deleteMessage(chatId, messageId.toString());
        }

        UserState currentState = userStateService.getState(chatId);
        if (!stepHandlersMap.containsKey(currentState)) {
            throw new RuntimeException(String.format("Не найден нужный обработчик состояния для состояния: %s input: %s", currentState, input));
        }
        stepHandlersMap.get(currentState).handle(chatId, input);
    }

}
