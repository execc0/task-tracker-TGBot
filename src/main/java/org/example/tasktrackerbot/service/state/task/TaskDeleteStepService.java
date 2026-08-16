package org.example.tasktrackerbot.service.state.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.DTO.API.response.TaskResponse;
import org.example.tasktrackerbot.keyboard.CancelKeyboard;
import org.example.tasktrackerbot.keyboard.CancelOrReturnKeyboard;
import org.example.tasktrackerbot.keyboard.Keyboard;
import org.example.tasktrackerbot.keyboard.KeyboardType;
import org.example.tasktrackerbot.queries.Query;
import org.example.tasktrackerbot.responder.MessageFormatter;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.service.QueryHandler;
import org.example.tasktrackerbot.service.QueryHandlerProvider;
import org.example.tasktrackerbot.service.state.AbstractStateService;
import org.example.tasktrackerbot.service.state.StepHandler;
import org.example.tasktrackerbot.service.state.StepHandlerProvider;
import org.example.tasktrackerbot.session.MessageDeleteScheduler;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskDeleteStepService extends AbstractStateService implements QueryHandlerProvider, StepHandlerProvider {


    public TaskDeleteStepService(BotService botCommandService,
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

    @Override
    public Map<Query, QueryHandler> getQueryHandlers() {
        return Map.of(Query.DELETE_TASK, this::startTaskDelete);
    }

    @Override
    public Map<UserState, StepHandler> getStepHandlers() {
        return Map.of(UserState.TASK_DELETE_AWAITING_ID, this::handleIdStep,
                UserState.TASK_DELETE_AWAITING_CONFIRMATION, this::handleConfirmationStep);
    }

    public void startTaskDelete(String chatId) {

        super.start(chatId, UserState.TASK_DELETE_AWAITING_ID);

    }

    public void handleIdStep(String chatId, String taskId, Integer userMessageId) {

        TaskResponse task = botService.getOwnTask(chatId, taskId);

        String taskFormatted = messageFormatter.formatTaskDTO(task);
        String message = "Подтвердите удаление задачи: \n\n" + taskFormatted;

        super.handleNextStep(chatId, userMessageId, UserState.TASK_DELETE_AWAITING_CONFIRMATION, "task_id", taskId, message,
                keyboardProviderMap.get(KeyboardType.CANCEL_OR_CONFIRM).getKeyboard());


    }

    public void handleConfirmationStep(String chatId, String value, Integer userMessageId) {

        String taskId = userStateService.getTempField(chatId, "task_id");
        botService.deleteOwnTask(chatId, taskId);
        super.finishFlow(chatId, userMessageId);

    }
}
