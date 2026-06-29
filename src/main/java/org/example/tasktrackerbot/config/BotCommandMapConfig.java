package org.example.tasktrackerbot.config;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.commands.BotCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class BotCommandMapConfig {

    private final List<BotCommand> botCommandList;

    public BotCommandMapConfig(List<BotCommand> botCommandList) {
        this.botCommandList = botCommandList;
    }

    @Bean(name = "botCommandMap")
    public Map<String, BotCommand> createBotCommandMap() {
        Map<String, BotCommand> botCommandMap = new HashMap<>();
        for (BotCommand botCommand: botCommandList) {
            log.info("Filling the map: {}", botCommand.getCommand());
            botCommandMap.put(botCommand.getCommand(), botCommand);
        }
        log.info("Ключи в мапе сразу после заполнения до DI: {}", botCommandMap.keySet());
        return botCommandMap;
    }

}
