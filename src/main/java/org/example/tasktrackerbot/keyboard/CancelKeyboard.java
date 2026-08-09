package org.example.tasktrackerbot.keyboard;

import org.example.tasktrackerbot.queries.Query;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class CancelKeyboard implements Keyboard {

    @Override
    public KeyboardType getKeyboardType() {
        return KeyboardType.CANCEL;
    }

    @Override
    public InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text("Отмена")
                .callbackData(Query.STATE_CANCEL.getCallback())
                .build();


        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(cancelButton))
                .build();
    }
}
