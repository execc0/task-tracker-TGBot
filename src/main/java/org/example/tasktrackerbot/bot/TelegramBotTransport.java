package org.example.tasktrackerbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.dispatcher.UpdateDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
@Slf4j
public class TelegramBotTransport implements SpringLongPollingBot {

    @Value("${bot.secret}")
    private String botToken;

    private final UpdateDispatcher updateDispatcher;

    public TelegramBotTransport(UpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateDispatcher;
    }
}
