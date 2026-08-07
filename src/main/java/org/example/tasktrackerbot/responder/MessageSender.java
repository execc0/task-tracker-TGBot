package org.example.tasktrackerbot.responder;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.example.tasktrackerbot.exception.BotException;
import org.example.tasktrackerbot.exception.FailToExecuteException;
import org.example.tasktrackerbot.exception.NullMessageException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@Slf4j
public class MessageSender {

    private final TelegramClient telegramClient;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    public MessageSender(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public Integer sendMessage(String chatId, String message) {

        SendMessage sendMessage = new SendMessage(chatId, message);

        return execute(sendMessage, chatId, false);

    }

    public Integer sendKeyboardMessage(String chatId, String message, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .replyMarkup(markup)
                .build();
        return execute(sendMessage, chatId, false);
    }

    public Integer sendMessageDefault(String chatId) {
        String text = """
                Ошибка! Введена неверная команда.
                Для начала работы с ботом введите:
                /start
                """;
        return sendMessage(chatId, text);
    }

    public void deleteMessage(String chatId, String messageId) {

        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(Integer.parseInt(messageId))
                .build();

        execute(deleteMessage, chatId, false);

    }

    public Integer editMessage(String chatId, String messageId, String message) {

        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(Integer.parseInt(messageId))
                .text(message)
                .build();

        return execute(editMessage, chatId, false);

    }

    public Integer editMessage(String chatId, String messageId, String message, InlineKeyboardMarkup keyboard) {

        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(Integer.parseInt(messageId))
                .replyMarkup(keyboard)
                .text(message)
                .build();

        return execute(editMessage, chatId, false);

    }
    public Integer editOrSendNewMessage(String chatId, String messageId, String message) {

        try {
            if (messageId != null) {
                return editMessage(chatId, messageId, message);
            }
            throw new NullPointerException("Попытка отредактировать сообщение при messageId == null");
        } catch (NullMessageException e) {
            return sendMessage(chatId, message);
        }

    }


    public Integer editOrSendNewMessage(String chatId, String messageId, String message, InlineKeyboardMarkup keyboard) {

        try {
            if (messageId != null) {
                return editMessage(chatId, messageId, message, keyboard);
            }
            throw new NullPointerException("Попытка отредактировать сообщение при messageId == null");
        } catch (Exception e) {
            return sendKeyboardMessage(chatId, message, keyboard);
        }

    }

    public void answerCallback(String callBackQueryId) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callBackQueryId)
                .build();

        execute(answerCallbackQuery);
    }

    public void answerCallback(String callBackQueryId, String text) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callBackQueryId)
                .text(text)
                .showAlert(false)
                .build();

        execute(answerCallbackQuery);
    }

    private Integer execute(SendMessage sendMessage, String chatId, boolean retrying) {
        try {
            Message sent = telegramClient.execute(sendMessage);
            log.info("Сообщение успешно отправлено, chatId: {}, messageId: {}", chatId, sent.getMessageId());
            return sent.getMessageId();
        } catch (TelegramApiException e) {
            if (e instanceof TelegramApiRequestException requestException && isRetriable(requestException, retrying)) {
                Integer retryAfter = requestException.getParameters().getRetryAfter();
                sleepSilent(retryAfter*1000);
                return execute(sendMessage, chatId, true);
            }
            throw new FailToExecuteException("Не удалось отправить сообщение: " + e.getMessage());
        }
    }

    private void execute(AnswerCallbackQuery answerCallbackQuery) {
        try {
            telegramClient.execute(answerCallbackQuery);
            log.info("Ответ на нажатие кнопки отправлен, callbackId: {}", answerCallbackQuery.getCallbackQueryId());
        } catch (TelegramApiException e) {
            throw new FailToExecuteException("Не удалось погасить кнопку " + e.getMessage());
        }
    }

    private void execute(DeleteMessage deleteMessage, String chatId, boolean retrying) {
        try {
            telegramClient.execute(deleteMessage);
            log.info("Сообщение успешно удалено, chatId: {}", chatId);
        } catch (TelegramApiException e) {
            if (e instanceof TelegramApiRequestException requestException && isRetriable(requestException, retrying)) {
                Integer retryAfter = requestException.getParameters().getRetryAfter();
                sleepSilent(retryAfter*1000);
                execute(deleteMessage, chatId, true);
            }
            throw new FailToExecuteException("Не удалось удалить сообщение: {} " + e.getMessage());
        }
    }

    private Integer execute(EditMessageText editMessageText, String chatId, boolean retrying) {
        try {
            telegramClient.execute(editMessageText);
            log.info("Сообщение успешно отредактировано, chatId: {}, messageId: {}", chatId, editMessageText.getMessageId());
            return editMessageText.getMessageId();
        }  catch (TelegramApiException e) {
            if (e instanceof TelegramApiRequestException requestException && isRetriable(requestException, retrying)) {
                Integer retryAfter = requestException.getParameters().getRetryAfter();
                sleepSilent(retryAfter*1000);
                return execute(editMessageText, chatId, true);
            }
            throw new FailToExecuteException("Не удалось отредактировать сообщение: {} " + e.getMessage());
        }
    }

    private void sleepSilent(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Прерван сон потока {} ", Thread.currentThread().getName());
        }

    }

    private boolean isRetriable(TelegramApiRequestException exception, boolean retrying) {
        return exception.getErrorCode() == 429 && !retrying;
    }

}
