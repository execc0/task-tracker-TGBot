package org.example.tasktrackerbot.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.commands.BotCommand;
import org.example.tasktrackerbot.exception.GlobalExceptionHandler;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.exception.NullMessageException;
import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;



import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class UpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    private final Map<String, BotCommand> botCommandMap;
    private final GlobalExceptionHandler exceptionHandler;
    private final BotService botService;

    public UpdateHandler(Map<String, BotCommand> botCommandMap, GlobalExceptionHandler exceptionHandler, BotService botService) {
        this.botCommandMap = botCommandMap;
        this.botService = botService;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void consume(List<Update> updates) {
        LongPollingSingleThreadUpdateConsumer.super.consume(updates);
    }

    @Override
    public void consume(Update update) {

        String chatId = extractChatId(update);
        if (chatId == null) {
            return;
        }

        try {
            if (!update.getMessage().hasText()) {
                throw new IllegalArgumentException("Текст сообщения пуст");
            }

            log.info("DEBUG: Список всех ключей в botCommandMap: {}", botCommandMap.keySet());
            log.info("Получено сообщение из telegram: {}", update.getMessage().getText());
            String[] textMessageWords = update.getMessage().getText().trim().split(" ");
            String command = textMessageWords[0];

            if(!command.equals("/login") && !command.equals("/register")) {
                botService.authorizeByChatId(chatId);
            }

            if (!botCommandMap.containsKey(command)) {
                throw new InvalidCommandInputException("Ошибка! Введена неверная команда: " + textMessageWords[0]
                        + "\nДля начала работы введите /start");
            }

            botCommandMap.get(command).execute(update);

        } catch (Exception exception) {
            exceptionHandler.handle(chatId, exception);
        }

    }


    private String extractChatId(Update update) {
        if(!update.hasMessage()) {
            exceptionHandler.handleNullMessageException(new NullMessageException("Ошибка! Сообщение отсутствует"));
            return null;
        }
        return update.getMessage().getChatId().toString();
    }

}
