package org.example.tasktrackerbot.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.commands.BotCommand;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class UpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    private final Map<String, BotCommand> botCommandMap;
    private final TelegramClient telegramClient;

    public UpdateHandler(Map<String, BotCommand> botCommandMap, TelegramClient telegramClient) {
        this.botCommandMap = botCommandMap;
        this.telegramClient = telegramClient;
    }

    @Override
    public void consume(List<Update> updates) {
        LongPollingSingleThreadUpdateConsumer.super.consume(updates);
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage()) {
            throw new RuntimeException("Ошибка! Сообщение отсутствует");
        }
        if (!update.getMessage().hasText()) {
            throw new RuntimeException("Ошибка! Сообщение не содержит текста");
        }

        log.info("DEBUG: Список всех ключей в мапе: {}", botCommandMap.keySet());
        log.info("DEBUG: Список всех значений в мапе: {}", botCommandMap.values());
        log.info("Получено сообщение из telegram: {}", update.getMessage().getText());
        String chatId = String.valueOf(update.getMessage().getChatId());
        String[] textMessageWords = update.getMessage().getText().trim().split(" ");

        if (botCommandMap.containsKey(textMessageWords[0])) {
            log.info("Found the command in the map");
            String text = botCommandMap.get(textMessageWords[0]).execute(update);
            sendMessage(chatId, text);
            return;
        }
        log.error("Ошибка! Введена неверная команда: {}", textMessageWords[0]);
        sendMessageDefault(chatId);
    }

    private void sendMessage(String chatId, String message) {

        SendMessage sendMessage = new SendMessage(chatId, message);

        try {
            telegramClient.execute(sendMessage);
            log.info("Сообщение успешно отправлено, chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка! Сообщение не было отправлено: {}", e.getMessage());
        }

    }

    private void sendMessageDefault(String chatId) {
        String text = """
                Ошибка! Введена неверная команда.
                Для начала работы с ботом введите:
                /start
                """;
        sendMessage(chatId, text);
    }
}
