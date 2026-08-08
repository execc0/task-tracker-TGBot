package org.example.tasktrackerbot.commands.dispatcher;

import org.example.tasktrackerbot.commands.BotCommand;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dispatcher, который отвечает за нахождение нужного обработчика для команд
 * Команды начинаются с "/" и обрабатываются полностью за один раз без промежуточных состояний
 */
@Component
public class BotCommandDispatcher {

    private final MessageSender messageSender;
    private final Map<String, BotCommand> botCommandMap;

    public BotCommandDispatcher(MessageSender messageSender, List<BotCommand> botCommandList) {
        this.messageSender = messageSender;
        botCommandMap = botCommandList.stream()
                .collect(Collectors.toMap(botCommand -> botCommand.getCommand(), botCommand -> botCommand));
    }

    public void dispatchCommand(Update update, String command, String chatId) {

        if (!botCommandMap.containsKey(command)) {
            throw new InvalidCommandInputException("Ошибка! Введена неверная команда: " + command
                    + "\nДля начала работы введите /start");
        }
        Integer messageId = update.getMessage().getMessageId();
        messageSender.deleteMessage(chatId, messageId.toString());
        botCommandMap.get(command).execute(update);

    }

}
