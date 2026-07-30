package org.example.tasktrackerbot.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.commands.BotCommand;
import org.example.tasktrackerbot.commands.dispatcher.BotCommandDispatcher;
import org.example.tasktrackerbot.exception.GlobalExceptionHandler;
import org.example.tasktrackerbot.exception.NullMessageException;
import org.example.tasktrackerbot.queries.CallbackQuery;
import org.example.tasktrackerbot.queries.dispatcher.BotCallbackQueryDispatcher;
import org.example.tasktrackerbot.session.StepHandler;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;



import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class UpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    private final BotCallbackQueryDispatcher callbackQueryDispatcher;
    private final GlobalExceptionHandler exceptionHandler;
    private final BotCommandDispatcher commandDispatcher;
    private final UserStateService userStateService;
    private final Map<UserState, StepHandler> registrationHandlerMap;

    public UpdateHandler(GlobalExceptionHandler exceptionHandler,
                         BotCommandDispatcher commandDispatcher,
                         BotCallbackQueryDispatcher callbackQueryDispatcher,
                         UserStateService userStateService,
                         Map<UserState, StepHandler> registrationHandlerMap) {
        this.callbackQueryDispatcher = callbackQueryDispatcher;
        this.exceptionHandler = exceptionHandler;
        this.commandDispatcher = commandDispatcher;
        this.userStateService = userStateService;
        this.registrationHandlerMap = registrationHandlerMap;
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

            // Callback - обработка нажатий на кнопки
            if (update.getCallbackQuery() != null) {
                callbackQueryDispatcher.dispatchCallbackQuery(update, chatId);
                return;
            }

            // Если нет текста и не было нажатия на кнопку - проблема
            if (!update.getMessage().hasText()) {
                throw new IllegalArgumentException("Текст сообщения пуст");
            }
            log.info("Получено сообщение из telegram: {}", update.getMessage().getText());

            // State - обработка ввода после нажатия на кнопку
            if(userStateService.getState(chatId) != UserState.NONE) {
                registrationHandlerMap.get(userStateService.getState(chatId)).handle(chatId, update.getMessage().getText());
                return;
            }

            // Обработка обычных команд
            String command = parseCommand(update);
            commandDispatcher.dispatchCommand(update, command, chatId);

        } catch (Exception exception) {
            exceptionHandler.handle(chatId, exception);
        }

    }


    private String extractChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }
        exceptionHandler.handleNullMessageException(new NullMessageException("Ошибка! Сообщение отсутствует"));
        return null;
    }


    private String parseCommand(Update update) {
        String[] textMessageWords = update.getMessage().getText().trim().split(" ");
        return textMessageWords[0];
    }

}
