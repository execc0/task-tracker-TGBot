package org.example.tasktrackerbot.keyboard;

import org.example.tasktrackerbot.queries.Query;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class TaskPriorityKeyboard implements Keyboard {

    public KeyboardType getKeyboardType() {
        return KeyboardType.TASK_PRIORITY;
    }

    @Override
    public InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardButton LOWButton = InlineKeyboardButton.builder()
                .text(Query.PRIORITY_LOW.getText())
                .callbackData(Query.PRIORITY_LOW.getCallback())
                .build();

        InlineKeyboardButton MEDIUMButton = InlineKeyboardButton.builder()
                .text(Query.PRIORITY_MEDIUM.getText())
                .callbackData(Query.PRIORITY_MEDIUM.getCallback())
                .build();

        InlineKeyboardButton HIGHButton = InlineKeyboardButton.builder()
                .text(Query.PRIORITY_HIGH.getText())
                .callbackData(Query.PRIORITY_HIGH.getCallback())
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(LOWButton, MEDIUMButton, HIGHButton))
                .keyboardRow(getCancelOrReturnRow())
                .build();
    }
}
