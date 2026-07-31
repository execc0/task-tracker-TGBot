package org.example.tasktrackerbot.commands;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class RegisterCommand implements BotCommand {

    private final BotService botCommandService;

    public RegisterCommand(BotService botCommandService) {
        this.botCommandService = botCommandService;
    }

    @Override
    public String getCommand() {
        return "/register";
    }

    @Override
    public void execute(Update update) {

        String[] textMessageWords = update.getMessage().getText().trim().split(" ");
        String chatId = update.getMessage().getChatId().toString();
        String command = textMessageWords[0];

        if (textMessageWords.length != 5) {
            throw new InvalidCommandInputException("""
                    Ошибка! Введён неверный формат строки для команды /register
                    Верный формат: /register name username email password
                    Пример: /register ExampleName ExampleUsername example@example.org Password123
                    """, String.format("Введён неверный формат команды /register: %s", command));
        }

        botCommandService.register(textMessageWords[1], textMessageWords[2], textMessageWords[3], textMessageWords[4], chatId);
    }

}
