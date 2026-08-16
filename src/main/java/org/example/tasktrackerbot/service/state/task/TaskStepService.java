package org.example.tasktrackerbot.service.state.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.API.request.TaskCreateRequest;
import org.example.tasktrackerbot.keyboard.*;
import org.example.tasktrackerbot.queries.Query;
import org.example.tasktrackerbot.responder.MessageFormatter;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.service.QueryHandler;
import org.example.tasktrackerbot.service.QueryHandlerProvider;
import org.example.tasktrackerbot.service.state.AbstractStateService;
import org.example.tasktrackerbot.service.state.StepHandler;
import org.example.tasktrackerbot.service.state.StepHandlerProvider;
import org.example.tasktrackerbot.session.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskStepService extends AbstractStateService implements StepHandlerProvider, QueryHandlerProvider {



    public TaskStepService(BotService botCommandService,
                           MessageSender messageSender,
                           UserStateService userStateService,
                           ObjectMapper objectMapper,
                           MessageDeleteScheduler messageDeleteScheduler,
                           Map<KeyboardType, Keyboard> keyboardProviderMap,
                           CancelOrReturnKeyboard cancelOrReturnKeyboard,
                           CancelKeyboard cancelKeyboard,
                           MessageFormatter messageFormatter) {
        super(botCommandService, messageSender, userStateService, objectMapper,
                messageDeleteScheduler, keyboardProviderMap, cancelOrReturnKeyboard, cancelKeyboard, messageFormatter);
    }


    public Map<UserState, StepHandler> getStepHandlers() {

        return Map.of(UserState.TASK_CREATE_AWAITING_TITLE, this::handleTitleStep,
                UserState.TASK_CREATE_AWAITING_DESCRIPTION, this::handleDescriptionStep,
                UserState.TASK_CREATE_AWAITING_PRIORITY, this::handlePriorityStep,
                UserState.TASK_CREATE_AWAITING_STATUS, this::handleStatusStep);
    }

    public Map<Query, QueryHandler> getQueryHandlers() {
        return Map.of(Query.CREATE_TASK, this::startTaskCreation);
    }

    public void startTaskCreation(String chatId) {

        super.start(chatId, UserState.TASK_CREATE_AWAITING_TITLE);

    }

    public void handleTitleStep(String chatId, String title) {

        super.handleNextStep(chatId, UserState.TASK_CREATE_AWAITING_DESCRIPTION, "title", title, "Введите описание задачи: ");

    }

    public void handleDescriptionStep(String chatId, String description) {

        super.handleNextStep(chatId, UserState.TASK_CREATE_AWAITING_PRIORITY, "description",
                description, "Выберите приоритет задачи: ", keyboardProviderMap.get(KeyboardType.TASK_PRIORITY).getKeyboard());

    }

    public void handlePriorityStep(String chatId, String priority) {

        super.handleNextStep(chatId, UserState.TASK_CREATE_AWAITING_STATUS, "priority",
                priority, "Выберите статус задачи: ", keyboardProviderMap.get(KeyboardType.TASK_STATUS).getKeyboard());

    }

    public void handleStatusStep(String chatId, String status) {

        userStateService.setTemp(chatId, "status", status);
        TaskCreateRequest request = super.finishFlow(chatId, TaskCreateRequest.class);
        botService.createOwnTask(request, chatId);



    }

}
