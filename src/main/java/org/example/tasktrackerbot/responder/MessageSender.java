package org.example.tasktrackerbot.responder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
public class MessageSender {

    private final TelegramClient telegramClient;

    public MessageSender(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public void sendMessage(String chatId, String message) {

        SendMessage sendMessage = new SendMessage(chatId, message);

        try {
            telegramClient.execute(sendMessage);
            log.info("Сообщение успешно отправлено, chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка! Сообщение не было отправлено: {}", e.getMessage());
        }

    }

    public void sendMessageDefault(String chatId) {
        String text = """
                Ошибка! Введена неверная команда.
                Для начала работы с ботом введите:
                /start
                """;
        sendMessage(chatId, text);
    }
}
