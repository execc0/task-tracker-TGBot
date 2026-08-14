package org.example.tasktrackerbot.config;

import org.example.tasktrackerbot.commands.Command;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommandToQueryMapConfig {

    @Bean(name = "commandToQueryMap")
    public Map<String, String> createCommandToQueryMap() {
        return Map.of(Command.START.getCommandText(), Command.START.getCallback(),
                Command.REGISTER.getCommandText(), Command.REGISTER.getCallback(),
                Command.LOGIN.getCommandText(), Command.LOGIN.getCallback(),
                Command.MENU.getCommandText(), Command.MENU.getCallback(),
                Command.UNLINK.getCommandText(), Command.UNLINK.getCallback());
    }

}
