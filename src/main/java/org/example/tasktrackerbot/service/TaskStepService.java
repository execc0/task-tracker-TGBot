package org.example.tasktrackerbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.request.Priority;
import org.example.tasktrackerbot.DTO.request.Status;
import org.example.tasktrackerbot.DTO.request.TaskCreateRequest;
import org.example.tasktrackerbot.keyboard.TaskPriorityKeyboard;
import org.example.tasktrackerbot.keyboard.TaskStatusKeyboard;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskStepService extends AbstractStateService implements StepHandlerProvider {

    private final TaskPriorityKeyboard taskPriorityKeyboard;
    private final TaskStatusKeyboard taskStatusKeyboard;


    public TaskStepService(BotService botService,
                           MessageSender messageSender,
                           UserStateService userStateService,
                           ObjectMapper objectMapper, TaskPriorityKeyboard taskPriorityKeyboard,
                           TaskStatusKeyboard taskStatusKeyboard,
                           MessageDeleteScheduler messageDeleteScheduler) {
        super(botService, messageSender, userStateService, objectMapper, messageDeleteScheduler);
        this.taskPriorityKeyboard = taskPriorityKeyboard;
        this.taskStatusKeyboard = taskStatusKeyboard;
    }

    public Map<UserState, StepHandler> getHandlers() {

        return Map.of(UserState.TASK_CREATE_AWAITING_TITLE, this::handleTitleStep,
                UserState.TASK_CREATE_AWAITING_DESCRIPTION, this::handleDescriptionStep,
                UserState.TASK_CREATE_AWAITING_PRIORITY, this::handlePriorityStep,
                UserState.TASK_CREATE_AWAITING_STATUS, this::handleStatusStep);
    }

    public void startTaskCreation(String chatId) {

        super.start(chatId, UserState.TASK_CREATE_AWAITING_TITLE, "Введите название задачи: ");

    }

    public void handleTitleStep(String chatId, String title, Integer messageId) {

        super.handleNextStep(chatId, messageId, UserState.TASK_CREATE_AWAITING_DESCRIPTION, "title", title, "Введите описание задачи: ");
        super.deleteUserMessage(chatId, messageId);

    }

    public void handleDescriptionStep(String chatId, String description, Integer messageId) {

        super.handleNextStep(chatId, messageId, UserState.TASK_CREATE_AWAITING_PRIORITY, "description",
                description, "Выберите приоритет задачи: ",taskPriorityKeyboard.getKeyboard());
        super.deleteUserMessage(chatId, messageId);

    }

    public void handlePriorityStep(String chatId, String priority, Integer messageId) {

        super.handleNextStep(chatId, messageId, UserState.TASK_CREATE_AWAITING_STATUS, "priority",
                priority, "Выберите статус задачи: ", taskStatusKeyboard.getKeyboard());
        super.deleteUserMessage(chatId, messageId);

    }

    public void handleStatusStep(String chatId, String status, Integer messageId) {

        userStateService.setTemp(chatId, "status", status);
        TaskCreateRequest request = super.finishFlow(chatId, messageId, TaskCreateRequest.class);
        botService.createOwnTask(request, chatId);
        super.deleteUserMessage(chatId, messageId);


    }

}
