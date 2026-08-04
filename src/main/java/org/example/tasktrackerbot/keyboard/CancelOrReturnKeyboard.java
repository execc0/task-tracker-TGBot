package org.example.tasktrackerbot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class CancelOrReturnKeyboard implements Keyboard {


    @Override
    public KeyboardType getKeyboardType() {
        return KeyboardType.RETURN_OR_CANCEL;
    }

    @Override
    public InlineKeyboardMarkup getKeyboard() {

        return InlineKeyboardMarkup.builder()
                .keyboardRow(getCancelOrReturnRow())
                .build();

    }
}
