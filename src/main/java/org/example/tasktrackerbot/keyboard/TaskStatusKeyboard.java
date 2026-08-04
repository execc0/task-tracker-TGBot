package org.example.tasktrackerbot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class TaskStatusKeyboard implements Keyboard {

    public KeyboardType getKeyboardType() {
        return KeyboardType.TASK_STATUS;
    }

    @Override
    public InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardButton TODOButton = InlineKeyboardButton.builder()
                .text("TODO")
                .callbackData("status:TODO")
                .build();

        InlineKeyboardButton inProgressButton = InlineKeyboardButton.builder()
                .text("IN_PROGRESS")
                .callbackData("status:IN_PROGRESS")
                .build();

        InlineKeyboardButton doneButton = InlineKeyboardButton.builder()
                .text("DONE")
                .callbackData("status:DONE")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(TODOButton, inProgressButton, doneButton))
                .keyboardRow(getCancelOrReturnRow())
                .build();

    }
}
