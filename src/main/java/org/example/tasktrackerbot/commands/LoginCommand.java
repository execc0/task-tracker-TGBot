package org.example.tasktrackerbot.commands;

import lombok.extern.slf4j.Slf4j;
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
    public String execute(Update update) {

        String[] textMessageWords = update.getMessage().getText().trim().split(" ");

        if(textMessageWords.length != 3) {
            log.warn("Введён неверный формат команды /login: {}", textMessageWords[0]);
            return """
                    Ошибка! Введён неверный формат строки для команды /login
                    Верный формат: /login username password
                    Пример: /register ExampleUsername Password123
                    """;
        }

        return botService.login(textMessageWords[1], textMessageWords[2]);
    }
}
