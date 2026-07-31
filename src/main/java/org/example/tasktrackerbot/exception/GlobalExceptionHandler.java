package org.example.tasktrackerbot.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.responder.MessageSender;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class GlobalExceptionHandler {

    private final MessageSender messageSender;
    private final Map<Class<? extends Exception>, BiConsumer<String, Exception>> exceptionHandlersMap = new HashMap<>();

    public GlobalExceptionHandler(MessageSender messageSender) {
        this.messageSender = messageSender;

    }

    public void handle(String chatId, Exception exception) {

        if (exception instanceof BotException botException) {
            log.error("BotException: {}, chatId: {}", botException.getInternalMessage(), chatId);
            messageSender.sendMessage(chatId, botException.getMessage());
            return;
        }
        handleGeneral(exception);

    }

    public void handleGeneral(Exception exception) {
        log.error("Возникло исключение: {}, message: {}", exception.getClass().getSimpleName(), exception.getMessage());
    }
}
