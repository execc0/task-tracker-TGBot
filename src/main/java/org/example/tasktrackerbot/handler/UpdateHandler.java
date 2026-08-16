package org.example.tasktrackerbot.handler;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.TransactionOptions;
import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.commands.dispatcher.BotCommandDispatcher;
import org.example.tasktrackerbot.exception.GlobalExceptionHandler;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.exception.NullMessageException;
import org.example.tasktrackerbot.queries.dispatcher.BotCallbackQueryDispatcher;
import org.example.tasktrackerbot.security.AuthorizationFilter;
import org.example.tasktrackerbot.session.*;
import org.example.tasktrackerbot.session.dispatcher.StepHandlerDispatcher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;



import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@Component
@Slf4j
public class UpdateHandler implements LongPollingUpdateConsumer {

    private final BotCallbackQueryDispatcher callbackQueryDispatcher;
    private final GlobalExceptionHandler exceptionHandler;
    private final BotCommandDispatcher commandDispatcher;
    private final UserStateService userStateService;
    private final StepHandlerDispatcher stepHandlerDispatcher;
    private final AuthorizationFilter authorizationFilter;
    private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final Cache<String, ReentrantLock> chatLocks = Caffeine.newBuilder()
                    .expireAfterAccess(30L, TimeUnit.MINUTES)
                    .build();

    public UpdateHandler(GlobalExceptionHandler exceptionHandler,
                         BotCommandDispatcher commandDispatcher,
                         BotCallbackQueryDispatcher callbackQueryDispatcher,
                         UserStateService userStateService,
                         StepHandlerDispatcher stepHandlerDispatcher, AuthorizationFilter authorizationFilter) {
        this.callbackQueryDispatcher = callbackQueryDispatcher;
        this.exceptionHandler = exceptionHandler;
        this.commandDispatcher = commandDispatcher;
        this.userStateService = userStateService;
        this.stepHandlerDispatcher = stepHandlerDispatcher;
        this.authorizationFilter = authorizationFilter;
    }

    @Override
    public void consume(List<Update> updates) {
        // Виртуальный поток для каждого обновления
        updates.forEach(update ->
                    virtualExecutor.execute(() -> consume(update)));

    }

    public void consume(Update update) {

        TransactionOptions options = new TransactionOptions();
        options.setBindToScope(true);
        ITransaction transaction = Sentry.startTransaction("Telegram update", "bot.consume", options);

        try {
            processUpdate(update, transaction);
        } finally {
            transaction.finish();
        }
    }

    public void processUpdate(Update update, ITransaction transaction) {

        String chatId = null;

        try {
            chatId = extractChatId(update);
            transaction.setTag("chat_id", chatId);
        } catch (Exception e) {
            exceptionHandler.handle(chatId, e, transaction);
            return;
        }

        // Получаем или создаем лок для конкретного chatId
        ReentrantLock lock = chatLocks.get(chatId, id -> new ReentrantLock(true));
        lock.lock();
        try {

            authorizationFilter.filter(update, chatId);
            routeUpdate(update, transaction, chatId);

        } catch (Exception exception) {
            exceptionHandler.handle(chatId, exception, transaction);
        } finally {
            lock.unlock();
        }

    }

    private void routeUpdate(Update update, ITransaction transaction, String chatId) {

        // Callback - обработка нажатий на кнопки
        if (update.hasCallbackQuery()) {
            transaction.setName("Telegram callback: " + update.getCallbackQuery().getData());
            callbackQueryDispatcher.dispatchCallbackQuery(update, chatId);
            return;
        }

        // Если нет текста и не было нажатия на кнопку - проблема
        if (!update.getMessage().hasText()) {
            throw new InvalidCommandInputException("Текст сообщения пуст.",
                    String.format("Сообщение chatId: %s не имеет текста", chatId));
        }
        log.info("Получено сообщение из telegram: {}", update.getMessage().getText());

        // State - обработка ввода после нажатия на кнопку
        if(userStateService.getState(chatId) != UserState.NONE) {
            String input = update.getMessage().getText();
            transaction.setName("Telegram state input");
            stepHandlerDispatcher.dispatchStateInput(input, chatId, update.getMessage().getMessageId());
            return;
        }

        // Обработка обычных команд
        String command = parseCommand(update);
        transaction.setName("Telegram command input: " + command);
        commandDispatcher.dispatchCommand(update, command, chatId);

    }

    private String extractChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }
        throw new NullMessageException("Ошибка! Сообщение отсутствует");
    }


    private String parseCommand(Update update) {
        String[] textMessageWords = update.getMessage().getText().trim().split(" ");
        return textMessageWords[0];
    }

}
