package org.example.tasktrackerbot.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.commands.dispatcher.BotCommandDispatcher;
import org.example.tasktrackerbot.exception.GlobalExceptionHandler;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.exception.NullMessageException;
import org.example.tasktrackerbot.queries.dispatcher.BotCallbackQueryDispatcher;
import org.example.tasktrackerbot.session.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;



import java.util.List;


@Component
@Slf4j
public class UpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    private final BotCallbackQueryDispatcher callbackQueryDispatcher;
    private final GlobalExceptionHandler exceptionHandler;
    private final BotCommandDispatcher commandDispatcher;
    private final UserStateService userStateService;
    private final StepHandlerDispatcher stepHandlerDispatcher;

    public UpdateHandler(GlobalExceptionHandler exceptionHandler,
                         BotCommandDispatcher commandDispatcher,
                         BotCallbackQueryDispatcher callbackQueryDispatcher,
                         UserStateService userStateService,
                         StepHandlerDispatcher stepHandlerDispatcher) {
        this.callbackQueryDispatcher = callbackQueryDispatcher;
        this.exceptionHandler = exceptionHandler;
        this.commandDispatcher = commandDispatcher;
        this.userStateService = userStateService;
        this.stepHandlerDispatcher = stepHandlerDispatcher;
    }

    @Override
    public void consume(List<Update> updates) {
        LongPollingSingleThreadUpdateConsumer.super.consume(updates);
    }

    @Override
    public void consume(Update update) {

        String chatId = null;

        try {

            chatId = extractChatId(update);

            // Callback - обработка нажатий на кнопки
            if (update.getCallbackQuery() != null) {
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
                stepHandlerDispatcher.dispatchStateInput(update.getMessage().getText(), chatId);
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
        throw new NullMessageException("Ошибка! Сообщение отсутствует");
    }


    private String parseCommand(Update update) {
        String[] textMessageWords = update.getMessage().getText().trim().split(" ");
        return textMessageWords[0];
    }

}
