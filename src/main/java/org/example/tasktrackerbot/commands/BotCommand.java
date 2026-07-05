package org.example.tasktrackerbot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommand {

    public String getCommand();

    public void execute(Update update);

}
