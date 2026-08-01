package org.example.tasktrackerbot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class TaskPriorityKeyboard implements Keyboard {
    @Override
    public InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardButton TODOButton = InlineKeyboardButton.builder()
                .text("LOW")
                .callbackData("priority:LOW")
                .build();

        InlineKeyboardButton inProgressButton = InlineKeyboardButton.builder()
                .text("MEDIUM")
                .callbackData("priority:MEDIUM")
                .build();

        InlineKeyboardButton doneButton = InlineKeyboardButton.builder()
                .text("HIGH")
                .callbackData("priority:HIGH")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(TODOButton, inProgressButton, doneButton))
                .build();
    }
}
