package org.example.tasktrackerbot.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.commands.BotCommand;
import org.example.tasktrackerbot.exception.GlobalExceptionHandler;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.exception.NullMessageException;
import org.example.tasktrackerbot.queries.CallbackQuery;
import org.example.tasktrackerbot.service.BotService;
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

    private final Map<String, BotCommand> botCommandMap;
    private final GlobalExceptionHandler exceptionHandler;
    private final BotService botCommandService;
    private final Map<String, CallbackQuery> botQueryMap;
    private final UserStateService userStateService;
    private final Map<UserState, StepHandler> registrationHandlerMap;

    public UpdateHandler(Map<String, BotCommand> botCommandMap,
                         GlobalExceptionHandler exceptionHandler,
                         BotService botCommandService,
                         Map<String, CallbackQuery> botQueryMap,
                         UserStateService userStateService,
                         Map<UserState, StepHandler> registrationHandlerMap) {
        this.botCommandMap = botCommandMap;
        this.botCommandService = botCommandService;
        this.exceptionHandler = exceptionHandler;
        this.botQueryMap = botQueryMap;
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

            // Callback - нажатие на кнопки
            if (update.getCallbackQuery() != null) {
                handleCallback(update, chatId);
                return;
            }

            if (!update.getMessage().hasText()) {
                throw new IllegalArgumentException("Текст сообщения пуст");
            }
            log.info("Получено сообщение из telegram: {}", update.getMessage().getText());

            // State - ввод после нажатия на кнопку
            if(userStateService.getState(chatId) != UserState.NONE) {
                registrationHandlerMap.get(userStateService.getState(chatId)).handle(chatId, update.getMessage().getText());
                return;
            }

            // Обработка обычных команд
            String command = parseCommand(update);

            if(!command.equals("/login") && !command.equals("/register") && !command.equals("/start")) {
                botCommandService.authorizeByChatId(chatId);
            }

            if (!botCommandMap.containsKey(command)) {
                throw new InvalidCommandInputException("Ошибка! Введена неверная команда: " + command
                        + "\nДля начала работы введите /start");
            }

            botCommandMap.get(command).execute(update);

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

    private void handleCallback(Update update, String chatId) {
        String query = update.getCallbackQuery().getData();
        botQueryMap.get(query).execute(chatId);
    }

    private String parseCommand(Update update) {
        String[] textMessageWords = update.getMessage().getText().trim().split(" ");
        return textMessageWords[0];
    }

}
