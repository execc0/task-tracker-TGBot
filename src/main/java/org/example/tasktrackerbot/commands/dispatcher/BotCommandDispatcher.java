package org.example.tasktrackerbot.commands.dispatcher;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.queries.dispatcher.BotCallbackQueryDispatcher;
import org.example.tasktrackerbot.responder.MessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Dispatcher, который отвечает за нахождение нужного обработчика для команд
 * Команды начинаются с "/" и обрабатываются полностью за один раз без промежуточных состояний
 */
@Component
@Slf4j
public class BotCommandDispatcher {

    private final MessageSender messageSender;
    private final Map<String, String> commandToQueryMap;
    private final BotCallbackQueryDispatcher botCallbackQueryDispatcher;

    public BotCommandDispatcher(MessageSender messageSender,
                                Map<String, String> commandToQueryMap,
                                BotCallbackQueryDispatcher botCallbackQueryDispatcher) {
        this.messageSender = messageSender;
        this.commandToQueryMap = commandToQueryMap;
        this.botCallbackQueryDispatcher = botCallbackQueryDispatcher;
    }

    public void dispatchCommand(Update update, String command, String chatId) {
        log.info("Получена команда из telegram: {}, chatId: {}", command, chatId);
        if (!commandToQueryMap.containsKey(command)) {
            throw new InvalidCommandInputException("Ошибка! Введена неверная команда: " + command
                    + "\nДля начала работы введите /start");
        }
        String query = commandToQueryMap.get(command);
        Integer messageId = update.getMessage().getMessageId();
        messageSender.deleteMessage(chatId, messageId.toString());
        botCallbackQueryDispatcher.dispatchCallbackQuery(query, chatId, null);

    }

}
