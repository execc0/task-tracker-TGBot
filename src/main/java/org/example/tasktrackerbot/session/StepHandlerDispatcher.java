package org.example.tasktrackerbot.session;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
public class StepHandlerDispatcher {

    private final Map<UserState, StepHandler> stepHandlersMap;
    private final UserStateService userStateService;

    public StepHandlerDispatcher(Map<UserState, StepHandler> stepHandlersMap, UserStateService userStateService) {
        this.stepHandlersMap = stepHandlersMap;
        this.userStateService = userStateService;
    }

    public void dispatchStateInput(String input, String chatId) {
        UserState currentState = userStateService.getState(chatId);
        if (!stepHandlersMap.containsKey(currentState)) {
            throw new RuntimeException("Не найден нужный обработчик состояния для состояния: " + currentState);
        }
        stepHandlersMap.get(currentState).handle(chatId, input);
    }

}
