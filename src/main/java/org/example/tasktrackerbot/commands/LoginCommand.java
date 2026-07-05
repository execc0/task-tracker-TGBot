package org.example.tasktrackerbot.commands;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class LoginCommand implements BotCommand {

    private final BotService botService;

    public LoginCommand(BotService botService) {
        this.botService = botService;
    }

    @Override
    public String getCommand() {
        return "/login";
    }

    @Override
    public void execute(Update update) {

        String[] textMessageWords = update.getMessage().getText().trim().split(" ");

        if(textMessageWords.length != 3) {
            log.warn("Введён неверный формат команды /login: {}", textMessageWords[0]);
            throw new InvalidCommandInputException("""
                    Ошибка! Введён неверный формат строки для команды /login
                    Верный формат: /login username password
                    Пример: /login ExampleUsername Password123
                    """);
        }

        botService.login(textMessageWords[1], textMessageWords[2], update.getMessage().getChatId().toString());
    }
}
