package org.example.tasktrackerbot.keyboard;

import org.example.tasktrackerbot.queries.Query;
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
                .text(Query.STATUS_TODO.getText())
                .callbackData(Query.STATUS_TODO.getCallback())
                .build();

        InlineKeyboardButton inProgressButton = InlineKeyboardButton.builder()
                .text(Query.STATUS_IN_PROGRESS.getText())
                .callbackData(Query.STATUS_IN_PROGRESS.getCallback())
                .build();

        InlineKeyboardButton doneButton = InlineKeyboardButton.builder()
                .text(Query.STATUS_DONE.getText())
                .callbackData(Query.STATUS_DONE.getCallback())
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(TODOButton, inProgressButton, doneButton))
                .keyboardRow(getCancelOrReturnRow())
                .build();

    }
}
