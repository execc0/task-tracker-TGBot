package org.example.tasktrackerbot.commands;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.DTO.request.UnlinkSocialRequest;
import org.example.tasktrackerbot.exception.InvalidCommandInputException;
import org.example.tasktrackerbot.service.BotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class UnlinkCommand implements BotCommand {

    private final BotService botCommandService;

    public UnlinkCommand(BotService botCommandService) {
        this.botCommandService = botCommandService;
    }

    @Override
    public String getCommand() {
        return "/unlink";
    }

    @Override
    public void execute(Update update) {

        String[] textMessageWords = update.getMessage().getText().trim().split(" ");
        String chatId = update.getMessage().getChatId().toString();

        if (textMessageWords.length != 3) {
            log.warn("Введён неверный формат команды /unlink: {}", textMessageWords[0]);
            throw new InvalidCommandInputException("""
                    Ошибка! Введён неверный формат строки для команды /unlink
                    Верный формат: /unlink username password
                    """);
        }

        botCommandService.unlink(textMessageWords[1], textMessageWords[2], chatId);
    }
}
