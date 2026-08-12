package org.example.tasktrackerbot.service.state.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.API.request.TaskCreateRequest;
import org.example.tasktrackerbot.keyboard.CancelKeyboard;
import org.example.tasktrackerbot.keyboard.CancelOrReturnKeyboard;
import org.example.tasktrackerbot.keyboard.TaskPriorityKeyboard;
import org.example.tasktrackerbot.keyboard.TaskStatusKeyboard;
import org.example.tasktrackerbot.queries.Query;
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

    private final TaskPriorityKeyboard taskPriorityKeyboard;
    private final TaskStatusKeyboard taskStatusKeyboard;


    public TaskStepService(BotService botService,
                           MessageSender messageSender,
                           UserStateService userStateService,
                           ObjectMapper objectMapper,
                           TaskPriorityKeyboard taskPriorityKeyboard,
                           TaskStatusKeyboard taskStatusKeyboard,
                           MessageDeleteScheduler messageDeleteScheduler,
                           CancelOrReturnKeyboard cancelOrReturnKeyboard,
                           CancelKeyboard cancelKeyboard) {
        super(botService, messageSender, userStateService, objectMapper,
                messageDeleteScheduler, cancelOrReturnKeyboard, cancelKeyboard);
        this.taskPriorityKeyboard = taskPriorityKeyboard;
        this.taskStatusKeyboard = taskStatusKeyboard;
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

    public void handleTitleStep(String chatId, String title, Integer messageId) {

        super.handleNextStep(chatId, messageId, UserState.TASK_CREATE_AWAITING_DESCRIPTION, "title", title, "Введите описание задачи: ");

    }

    public void handleDescriptionStep(String chatId, String description, Integer messageId) {

        super.handleNextStep(chatId, messageId, UserState.TASK_CREATE_AWAITING_PRIORITY, "description",
                description, "Выберите приоритет задачи: ",taskPriorityKeyboard.getKeyboard());

    }

    public void handlePriorityStep(String chatId, String priority, Integer messageId) {

        super.handleNextStep(chatId, messageId, UserState.TASK_CREATE_AWAITING_STATUS, "priority",
                priority, "Выберите статус задачи: ", taskStatusKeyboard.getKeyboard());

    }

    public void handleStatusStep(String chatId, String status, Integer messageId) {

        userStateService.setTemp(chatId, "status", status);
        TaskCreateRequest request = super.finishFlow(chatId, messageId, TaskCreateRequest.class);
        botService.createOwnTask(request, chatId);



    }

}
