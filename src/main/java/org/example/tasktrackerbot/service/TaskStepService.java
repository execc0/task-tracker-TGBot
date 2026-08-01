package org.example.tasktrackerbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.request.Priority;
import org.example.tasktrackerbot.DTO.request.Status;
import org.example.tasktrackerbot.DTO.request.TaskCreateRequest;
import org.example.tasktrackerbot.keyboard.TaskPriorityKeyboard;
import org.example.tasktrackerbot.keyboard.TaskStatusKeyboard;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.StepHandler;
import org.example.tasktrackerbot.session.StepHandlerProvider;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskStepService implements StepHandlerProvider {

    private final BotService botCommandService;
    private final MessageSender messageSender;
    private final UserStateService userStateService;
    private final ObjectMapper objectMapper;
    private final TaskPriorityKeyboard taskPriorityKeyboard;
    private final TaskStatusKeyboard taskStatusKeyboard;


    public TaskStepService(BotService botCommandService,
                           MessageSender messageSender,
                           UserStateService userStateService,
                           ObjectMapper objectMapper, TaskPriorityKeyboard taskPriorityKeyboard, TaskStatusKeyboard taskStatusKeyboard) {
        this.botCommandService = botCommandService;
        this.messageSender = messageSender;
        this.userStateService = userStateService;
        this.objectMapper = objectMapper;
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

        if (userStateService.getState(chatId) != UserState.NONE) {
            messageSender.sendMessage(chatId, "Предыдущий диалог отменён");
        }

        userStateService.clearState(chatId);
        userStateService.clearTemp(chatId);
        userStateService.setState(chatId, UserState.TASK_CREATE_AWAITING_TITLE);
        messageSender.sendMessage(chatId, "Введите название задачи: ");

    }

    public void handleTitleStep(String chatId, String title) {
        userStateService.setState(chatId, UserState.TASK_CREATE_AWAITING_DESCRIPTION);
        userStateService.setTemp(chatId, "title", title);
        messageSender.sendMessage(chatId, "Введите описание задачи: ");

    }

    public void handleDescriptionStep(String chatId, String description) {
        userStateService.setState(chatId, UserState.TASK_CREATE_AWAITING_PRIORITY);
        userStateService.setTemp(chatId, "description", description);
        messageSender.sendKeyboardMessage(chatId, "Выберите приоритет задачи: ", taskPriorityKeyboard.getKeyboard());
    }

    public void handlePriorityStep(String chatId, String priority) {
        userStateService.setState(chatId, UserState.TASK_CREATE_AWAITING_STATUS);
        userStateService.setTemp(chatId, "description", priority);
        messageSender.sendKeyboardMessage(chatId, "Выберите статус задачи: ", taskStatusKeyboard.getKeyboard());
    }

    public void handleStatusStep(String chatId, String status) {
        userStateService.setTemp(chatId, "status", status);
        Map<Object, Object> map = userStateService.getAllTempFields(chatId);
        TaskCreateRequest request = objectMapper.convertValue(map, TaskCreateRequest.class);
        botCommandService.createOwnTask(request, chatId);
        userStateService.clearTemp(chatId);
        userStateService.clearState(chatId);
    }

}
