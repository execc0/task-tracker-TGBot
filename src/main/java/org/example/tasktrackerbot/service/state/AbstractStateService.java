package org.example.tasktrackerbot.service.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.keyboard.CancelKeyboard;
import org.example.tasktrackerbot.keyboard.CancelOrReturnKeyboard;
import org.example.tasktrackerbot.keyboard.Keyboard;
import org.example.tasktrackerbot.keyboard.KeyboardType;
import org.example.tasktrackerbot.responder.MessageFormatter;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.session.MessageDeleteScheduler;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.security.Key;
import java.util.Map;

public abstract class AbstractStateService {


    protected final BotService botService;
    protected final MessageSender messageSender;
    protected final UserStateService userStateService;
    protected final ObjectMapper objectMapper;
    protected final MessageDeleteScheduler messageDeleteScheduler;
    protected final Map<KeyboardType, Keyboard> keyboardProviderMap;
    protected final CancelOrReturnKeyboard cancelOrReturnKeyboard;
    protected final CancelKeyboard cancelKeyboard;
    protected final MessageFormatter messageFormatter;

    public AbstractStateService(BotService botCommandService,
                                MessageSender messageSender,
                                UserStateService userStateService,
                                ObjectMapper objectMapper,
                                MessageDeleteScheduler messageDeleteScheduler,
                                Map<KeyboardType, Keyboard> keyboardProviderMap,
                                CancelOrReturnKeyboard cancelOrReturnKeyboard,
                                CancelKeyboard cancelKeyboard,
                                MessageFormatter messageFormatter) {
        this.botService = botCommandService;
        this.messageSender = messageSender;
        this.userStateService = userStateService;
        this.objectMapper = objectMapper;
        this.messageDeleteScheduler = messageDeleteScheduler;
        this.keyboardProviderMap = keyboardProviderMap;
        this.cancelOrReturnKeyboard = cancelOrReturnKeyboard;
        this.cancelKeyboard = cancelKeyboard;
        this.messageFormatter = messageFormatter;
    }

    protected void start(String chatId, UserState nextState) {

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

        String message = nextState.getPromptText();
        String messageWithBar = messageFormatter.buildProgressBar(nextState) + "\n" + message;
        InlineKeyboardMarkup keyboard = cancelKeyboard.getKeyboard();
        if (nextState.getKeyboardType() != null) {
            keyboard = keyboardProviderMap.get(nextState.getKeyboardType()).getKeyboard();
        }
        Integer messageId = messageSender.sendKeyboardMessage(chatId, messageWithBar, keyboard);
        userStateService.setTemp(chatId, "bot_message_id", messageId.toString());

    }

    protected void handleNextStep(String chatId, Integer userMessageId, UserState nextState, String key,
                                  String input, String message) {
        userStateService.setState(chatId, nextState);
        userStateService.setTemp(chatId, key, input);
        String messageId = userStateService.getTempField(chatId, "bot_message_id");
        String messageWithBar = messageFormatter.buildProgressBar(nextState) + "\n" + message;
        messageSender.editOrSendNewMessage(chatId, messageId, messageWithBar, cancelOrReturnKeyboard.getKeyboard());
        deleteUserMessage(chatId, userMessageId);
    }


    protected void handleNextStep(String chatId, Integer userMessageId, UserState nextState, String key,
                                  String input, String message,
                                  InlineKeyboardMarkup keyboard) {
        userStateService.setState(chatId, nextState);
        userStateService.setTemp(chatId, key, input);
        String messageId = userStateService.getTempField(chatId, "bot_message_id");
        String messageWithBar = messageFormatter.buildProgressBar(nextState) + "\n" + message;
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

    protected void finishFlow(String chatId, Integer userMessageId) {

        String messageToDelete = userStateService.getTempField(chatId, "bot_message_id");
        userStateService.clearTemp(chatId);
        userStateService.clearState(chatId);
        scheduleMessageDelete(chatId, messageToDelete, 5);
        deleteUserMessage(chatId, userMessageId);

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


}
