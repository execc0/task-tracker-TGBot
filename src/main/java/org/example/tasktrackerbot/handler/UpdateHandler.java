package org.example.tasktrackerbot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
@Slf4j
public class UpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public UpdateHandler(TelegramClient telegramClient) {
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

        log.info("Получено сообщение из telegram: {}", update.getMessage().getText());
        String text = """
        Привет! Это бот для Task Tracker, сейчас находится в разработке.
        Ссылка на репозиторий API: https://github.com/execc0/task-tracker
        """;
        String chatId = String.valueOf(update.getMessage().getChatId());
        sendMessage(chatId, text);
        log.info("Сообщение успешно отправлено, chatId: {}", chatId);
    }

    private void sendMessage(String chatId, String message) {

        SendMessage sendMessage = new SendMessage(chatId, message);

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка! Сообщение не было отправлено: {}", e.getMessage());
        }

    }
}
