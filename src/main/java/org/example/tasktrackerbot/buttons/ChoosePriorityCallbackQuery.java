package org.example.tasktrackerbot.buttons;

import org.example.tasktrackerbot.session.StepHandlerDispatcher;
import org.springframework.stereotype.Component;

@Component
public class ChoosePriorityCallbackQuery implements ActionCallbackQuery {

    private final StepHandlerDispatcher stepHandlerDispatcher;

    public ChoosePriorityCallbackQuery(StepHandlerDispatcher stepHandlerDispatcher) {
        this.stepHandlerDispatcher = stepHandlerDispatcher;
    }

    @Override
    public String getQuery() {
        return "priority";
    }

    @Override
    public void execute(String chatId, String priority) {
        stepHandlerDispatcher.dispatchStateInput(priority, chatId);
    }
}
