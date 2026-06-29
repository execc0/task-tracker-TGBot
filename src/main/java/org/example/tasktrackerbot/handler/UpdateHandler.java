package org.example.tasktrackerbot.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.commands.BotCommand;
import org.example.tasktrackerbot.exception.GlobalExceptionHandler;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.exception.NullMessageException;
import org.example.tasktrackerbot.responder.MessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;



import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class UpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    private final Map<String, BotCommand> botCommandMap;
    private final MessageSender messageSender;
    private final GlobalExceptionHandler exceptionHandler;

    public UpdateHandler(Map<String, BotCommand> botCommandMap, MessageSender messageSender, GlobalExceptionHandler exceptionHandler) {
        this.botCommandMap = botCommandMap;
        this.messageSender = messageSender;
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

            if (!botCommandMap.containsKey(command)) {
                throw new InvalidCommandInputException("Ошибка! Введена неверная команда" + textMessageWords[0]);
            }

            String text = botCommandMap.get(command).execute(update);
            messageSender.sendMessage(chatId, text);

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
