package org.example.tasktrackerbot.service.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.session.MessageDeleteScheduler;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Map;

public abstract class AbstractStateService {


    protected final BotService botService;
    protected final MessageSender messageSender;
    protected final UserStateService userStateService;
    protected final ObjectMapper objectMapper;
    protected final MessageDeleteScheduler messageDeleteScheduler;

    public AbstractStateService(BotService botCommandService, MessageSender messageSender, UserStateService userStateService, ObjectMapper objectMapper, MessageDeleteScheduler messageDeleteScheduler) {
        this.botService = botCommandService;
        this.messageSender = messageSender;
        this.userStateService = userStateService;
        this.objectMapper = objectMapper;
        this.messageDeleteScheduler = messageDeleteScheduler;
    }

    protected void start(String chatId, UserState nextState, String message) {

        if (userStateService.getState(chatId) != UserState.NONE) {
            Integer messageToDelete = messageSender.sendMessage(chatId, "Предыдущий диалог отменён");
            String canceledId = userStateService.getTempField(chatId, "bot_message_id");
            scheduleMessageDelete(chatId, messageToDelete.toString(), 10);
            if (canceledId != null && !canceledId.isEmpty()) {
                deleteUserMessage(chatId, Integer.parseInt(canceledId));
            }
        }

        userStateService.clearState(chatId);
        userStateService.clearTemp(chatId);
        userStateService.setState(chatId, nextState);
        String messageWithBar = buildProgressBar(nextState) + "\n" + message;
        Integer messageId = messageSender.sendMessage(chatId, messageWithBar);
        userStateService.setTemp(chatId, "bot_message_id", messageId.toString());

    }

    protected void handleNextStep(String chatId, Integer userMessageId, UserState nextState, String key,
                                  String input, String message) {
        userStateService.setState(chatId, nextState);
        userStateService.setTemp(chatId, key, input);
        String messageId = userStateService.getTempField(chatId, "bot_message_id");
        String messageWithBar = buildProgressBar(nextState) + "\n" + message;
        messageSender.editMessage(chatId, messageId, messageWithBar);
        deleteUserMessage(chatId, userMessageId);
    }


    protected void handleNextStep(String chatId, Integer userMessageId, UserState nextState, String key,
                                  String input, String message,
                                  InlineKeyboardMarkup keyboard) {
        userStateService.setState(chatId, nextState);
        userStateService.setTemp(chatId, key, input);
        String messageId = userStateService.getTempField(chatId, "bot_message_id");
        String messageWithBar = buildProgressBar(nextState) + "\n" + message;
        messageSender.editMessage(chatId, messageId, messageWithBar, keyboard);
        deleteUserMessage(chatId, userMessageId);
    }



    protected <T> T finishFlow(String chatId, Integer userMessageId, Class<T> DTOClass) {

        Map<Object, Object> map = userStateService.getAllTempFields(chatId);
        T request = objectMapper.convertValue(map, DTOClass);
        String messageToDelete = userStateService.getTempField(chatId, "bot_message_id");
        userStateService.clearTemp(chatId);
        userStateService.clearState(chatId);
        scheduleMessageDelete(chatId, messageToDelete, 5);
        deleteUserMessage(chatId, userMessageId);
        return request;

    }

    protected void scheduleMessageDelete(String chatId, String messageId, Integer delaySeconds) {
        messageDeleteScheduler.scheduleDelete(chatId, messageId, delaySeconds);
    }

    protected void scheduleMessageDelete(String chatId, String messageId) {
        messageDeleteScheduler.scheduleDelete(chatId, messageId, 10);
    }


    protected void deleteUserMessage(String chatId, Integer userMessageId) {
        if (userMessageId != null) {
            messageSender.deleteMessage(chatId, userMessageId.toString());
        }
    }

    private String buildProgressBar(UserState nextState) {

        final int barLength = 5;

        StringBuilder progressBar = new StringBuilder();

        int totalSteps = nextState.getTotalSteps();
        int currentStep = nextState.getCurrentStep();

        int dotsToDraw = (int) Math.round((double) currentStep/totalSteps * barLength);

        for (int i = 1; i <= barLength; i++) {
            progressBar.append(i <= dotsToDraw ? "🟩" : "⬜");
        }

        return progressBar + String.format(" Шаг %d/%d", currentStep, totalSteps);

    }

}
