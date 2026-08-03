package org.example.tasktrackerbot.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.MessageDeleteScheduler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class GlobalExceptionHandler {

    private final MessageDeleteScheduler messageDeleteScheduler;
    private final MessageSender messageSender;
    private final Map<Class<? extends Exception>, BiConsumer<String, Exception>> exceptionHandlersMap = new HashMap<>();

    public GlobalExceptionHandler(MessageDeleteScheduler messageDeleteScheduler, MessageSender messageSender) {
        this.messageDeleteScheduler = messageDeleteScheduler;
        this.messageSender = messageSender;

    }

    public void handle(String chatId, Exception exception) {

        if(chatId == null) {
            handleGeneral(exception);
            return;
        }
        if (exception instanceof BotException botException) {
            log.error("BotException: {}, chatId: {}", botException.getInternalMessage(), chatId, botException);
            Integer messageId = messageSender.sendMessage(chatId, botException.getMessage());
            messageDeleteScheduler.scheduleDelete(chatId, messageId.toString(), 10);
            return;
        }
        handleGeneral(exception);

    }

    public void handleGeneral(Exception exception) {
        log.error("Возникло исключение: {}, message: {}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
    }
}
