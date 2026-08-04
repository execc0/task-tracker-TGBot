package org.example.tasktrackerbot.queries.flow;

import org.example.tasktrackerbot.service.state.TaskStepService;
import org.springframework.stereotype.Component;

@Component
public class CreateTaskQuery implements FlowCallbackQuery {

    private final TaskStepService taskStepService;

    public CreateTaskQuery(TaskStepService taskStepService) {
        this.taskStepService = taskStepService;
    }

    @Override
    public String getQuery() {
        return "task:create";
    }

    @Override
    public void execute(String chatId) {
        taskStepService.startTaskCreation(chatId);
    }
}
