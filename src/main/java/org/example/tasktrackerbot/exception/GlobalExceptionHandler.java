package org.example.tasktrackerbot.exception;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.MessageDeleteScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

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

    public void handle(String chatId, Exception exception, ITransaction transaction) {

        if(chatId == null) {
            handleGeneral(exception, transaction);
            return;
        }
        if (exception instanceof ApiServerError apiServerError) {
            handleBotException(apiServerError, chatId);
            Sentry.captureException(apiServerError);
            return;
        }
        if (exception instanceof BotException botException) {
            handleBotException(botException, chatId);
            return;
        }
        if (exception instanceof ResourceAccessException resourceAccessException) {
            log.error("Task Tracker API недоступен: {}", resourceAccessException.getMessage(), resourceAccessException);
            transaction.setThrowable(resourceAccessException);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
            Integer messageId = messageSender.sendMessage(chatId, "Сервер временно недоступен. Пожалуйста, повторите попытку позже.");
            messageDeleteScheduler.scheduleDelete(chatId, messageId.toString(), 10);
            return;
        }
        handleGeneral(exception, transaction);

    }

    private void handleBotException(BotException botException, String chatId) {

        log.warn("BotException: {}, chatId: {}", botException.getInternalMessage(), chatId, botException);
        Integer messageId = messageSender.sendMessage(chatId, botException.getMessage());
        messageDeleteScheduler.scheduleDelete(chatId, messageId.toString(), 10);

    }


    public void handleGeneral(Exception exception, ITransaction transaction) {
        log.error("Возникло исключение: {}, message: {}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
        transaction.setThrowable(exception);
        transaction.setStatus(SpanStatus.UNKNOWN_ERROR);
    }
}
