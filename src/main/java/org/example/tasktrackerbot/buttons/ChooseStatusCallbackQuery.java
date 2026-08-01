package org.example.tasktrackerbot.buttons;


import org.example.tasktrackerbot.session.StepHandlerDispatcher;
import org.springframework.stereotype.Component;

@Component
public class ChooseStatusCallbackQuery implements ActionCallbackQuery {

    private final StepHandlerDispatcher stepHandlerDispatcher;

    public ChooseStatusCallbackQuery(StepHandlerDispatcher stepHandlerDispatcher) {
        this.stepHandlerDispatcher = stepHandlerDispatcher;
    }

    @Override
    public String getQuery() {
        return "status";
    }

    @Override
    public void execute(String chatId, String status) {
       stepHandlerDispatcher.dispatchStateInput(status, chatId);
    }
}
