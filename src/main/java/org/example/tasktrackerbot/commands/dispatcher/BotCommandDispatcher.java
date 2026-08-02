package org.example.tasktrackerbot.commands.dispatcher;

import org.example.tasktrackerbot.commands.BotCommand;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
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

    private final Map<String, BotCommand> botCommandMap;
    private final BotService botService;

    public BotCommandDispatcher(List<BotCommand> botCommandList, BotService botService) {
        botCommandMap = botCommandList.stream()
                .collect(Collectors.toMap(botCommand -> botCommand.getCommand(), botCommand -> botCommand));
        this.botService = botService;
    }

    public void dispatchCommand(Update update, String command, String chatId) {

        // Команды для которых авторизация НЕ требуется
        if(command.equals("/login") || command.equals("/register") || command.equals("/start")) {
            botCommandMap.get(command).execute(update);
            return;
        }

        botService.authorizeByChatId(chatId);

        // Остальные команды - после авторизации
        if (!botCommandMap.containsKey(command)) {
            throw new InvalidCommandInputException("Ошибка! Введена неверная команда: " + command
                    + "\nДля начала работы введите /start");
        }
        botCommandMap.get(command).execute(update);

    }

}
