package org.example.tasktrackerbot.responder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
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

        execute(sendMessage, chatId);

    }

    public void sendKeyboardMessage(String chatId, String message, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .replyMarkup(markup)
                .build();
        execute(sendMessage, chatId);
    }

    public void sendMessageDefault(String chatId) {
        String text = """
                Ошибка! Введена неверная команда.
                Для начала работы с ботом введите:
                /start
                """;
        sendMessage(chatId, text);
    }

    private void execute(SendMessage sendMessage, String chatId) {
        try {
            telegramClient.execute(sendMessage);
            log.info("Сообщение успешно отправлено, chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка! Сообщение не было отправлено: {}", e.getMessage());
        }
    }
}
