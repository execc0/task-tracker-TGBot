package org.example.tasktrackerbot.commands;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class RegisterCommand implements BotCommand {

    private final BotService botService;

    public RegisterCommand(BotService botService) {
        this.botService = botService;
    }

    @Override
    public String getCommand() {
        return "/register";
    }

    @Override
    public String execute(Update update) {

        String[] textMessageWords = update.getMessage().getText().trim().split(" ");

        if (textMessageWords.length != 5) {
            log.warn("Введён неверный формат команды /register: {}", textMessageWords[0]);
            return """
                    Ошибка! Введён неверный формат строки для команды /register
                    Верный формат: /register name username email password
                    Пример: /register ExampleName ExampleUsername example@example.org Password123
                    """;
        }

        return botService.register(textMessageWords[1], textMessageWords[2], textMessageWords[3], textMessageWords[4]);
    }

}
