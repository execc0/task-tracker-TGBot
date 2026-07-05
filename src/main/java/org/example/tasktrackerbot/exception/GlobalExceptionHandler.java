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

        createExceptionHandlersMap();

    }

    private void createExceptionHandlersMap() {

        exceptionHandlersMap.put(NullMessageException.class, (chatId, exception) ->
                handleNullMessageException((NullMessageException) exception));

        exceptionHandlersMap.put(IllegalArgumentException.class, (chatId, exception) ->
                handleIllegalArgumentException(chatId, (IllegalArgumentException) exception));

        exceptionHandlersMap.put(InvalidCommandInputException.class, (chatId, exception) ->
                handleInvalidCommandInputException(chatId, (InvalidCommandInputException) exception));

        exceptionHandlersMap.put(ApiLoginException.class, (chatId, exception) ->
                handleApiLoginException(chatId, (ApiLoginException) exception));

        exceptionHandlersMap.put(ApiRegisterException.class, (chatId, exception) ->
                handleApiRegisterException(chatId, (ApiRegisterException) exception));

    }

    public void handle(String chatId, Exception exception) {
        if (!exceptionHandlersMap.containsKey(exception.getClass())) {
            handleGeneralException(chatId, exception);
            return;
        }
        exceptionHandlersMap.get(exception.getClass()).accept(chatId, exception);
    }

    public void handleNullMessageException(NullMessageException exception) {
        log.warn("Обработано исключение: NullMessageException, message: {}", exception.getMessage());
    }

    public void handleIllegalArgumentException(String chatId, IllegalArgumentException exception) {
        log.warn("Обработано исключение: IllegalArgumentException, message: {}", exception.getMessage());
        messageSender.sendMessage(chatId, exception.getMessage());
    }

    public void handleInvalidCommandInputException(String chatId, InvalidCommandInputException exception) {
        log.warn("Обработано исключение: InvalidCommandInputException, message: {}", exception.getMessage());
        messageSender.sendMessage(chatId, exception.getMessage());
    }

    public void handleApiLoginException(String chatId, ApiLoginException exception) {
        log.warn("Обработано исключение: ApiLoginException, message: {}", exception.getMessage());
        messageSender.sendMessage(chatId, exception.getMessage());
    }

    public void handleApiRegisterException(String chatId, ApiRegisterException exception) {
        log.warn("Обработано исключение: ApiRegisterException, message: {}", exception.getMessage());
        messageSender.sendMessage(chatId, exception.getMessage());
    }

    public void handleGeneralException(String chatId, Exception exception) {
        log.error("Необработанное исключение: {}", exception.getMessage());
        String text = """
                Возникла непредвиденная ошибка!
                Попробуйте повторить запрос позже или свяжитесь с поддержкой
                """;
        messageSender.sendMessage(chatId, text);
    }

}
