package org.example.tasktrackerbot.config;

import org.example.tasktrackerbot.keyboard.Keyboard;
import org.example.tasktrackerbot.keyboard.KeyboardType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class KeyboardMapConfig {

    @Bean
    public Map<KeyboardType, Keyboard> createKeyboardMap(List<Keyboard> keyboardList) {
        return keyboardList.stream()
                .collect(Collectors.toMap(keyboard -> keyboard.getKeyboardType(), keyboard -> keyboard));
    }

}
