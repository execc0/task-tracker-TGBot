package org.example.tasktrackerbot.handler;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.commands.dispatcher.BotCommandDispatcher;
import org.example.tasktrackerbot.exception.GlobalExceptionHandler;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.exception.NullMessageException;
import org.example.tasktrackerbot.queries.dispatcher.BotCallbackQueryDispatcher;
import org.example.tasktrackerbot.service.AuthorizationService;
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
    private final AuthorizationService authorizationService;
    private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final Cache<String, ReentrantLock> chatLocks = Caffeine.newBuilder()
                    .expireAfterAccess(30L, TimeUnit.MINUTES)
                    .build();

    public UpdateHandler(GlobalExceptionHandler exceptionHandler,
                         BotCommandDispatcher commandDispatcher,
                         BotCallbackQueryDispatcher callbackQueryDispatcher,
                         UserStateService userStateService,
                         StepHandlerDispatcher stepHandlerDispatcher, AuthorizationService authorizationService) {
        this.callbackQueryDispatcher = callbackQueryDispatcher;
        this.exceptionHandler = exceptionHandler;
        this.commandDispatcher = commandDispatcher;
        this.userStateService = userStateService;
        this.stepHandlerDispatcher = stepHandlerDispatcher;
        this.authorizationService = authorizationService;
    }

    @Override
    public void consume(List<Update> updates) {
        // Виртуальный поток для каждого обновления
        updates.forEach(update ->
                    virtualExecutor.execute(() -> consume(update)));

    }

    public void consume(Update update) {

        String chatId = null;

        try {
            chatId = extractChatId(update);
        } catch (Exception e) {
            exceptionHandler.handle(chatId, e);
        }

        // Получаем или создаем лок для конкретного chatId
        ReentrantLock lock = chatLocks.get(chatId, id -> new ReentrantLock());
        lock.lock();
        try {

            authorizationService.authorize(update, chatId);

            // Callback - обработка нажатий на кнопки
            if (update.hasCallbackQuery()) {
                callbackQueryDispatcher.dispatchCallbackQuery(update, chatId);
                return;
            }

            // Если нет текста и не было нажатия на кнопку - проблема
            if (!update.getMessage().hasText()) {
                throw new InvalidCommandInputException("Текст сообщения пуст",
                        String.format("Сообщение chatId: %s не имеет текста", chatId));
            }
            log.info("Получено сообщение из telegram: {}", update.getMessage().getText());

            // State - обработка ввода после нажатия на кнопку
            if(userStateService.getState(chatId) != UserState.NONE) {
                stepHandlerDispatcher.dispatchStateInput(update.getMessage().getText(), chatId, update.getMessage().getMessageId());
                return;
            }

            // Обработка обычных команд
            String command = parseCommand(update);
            commandDispatcher.dispatchCommand(update, command, chatId);

        } catch (Exception exception) {
            exceptionHandler.handle(chatId, exception);
        } finally {
            lock.unlock();
        }

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
