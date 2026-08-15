package org.example.tasktrackerbot.keyboard;

import org.example.tasktrackerbot.queries.Query;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

public interface Keyboard {

    public KeyboardType getKeyboardType();

    public InlineKeyboardMarkup getKeyboard();

    public default InlineKeyboardRow getCancelOrReturnRow() {

        InlineKeyboardButton returnButton = InlineKeyboardButton.builder()
                .text("⬅️ Назад")
                .callbackData(Query.STATE_RETURN.getCallback())
                .build();

        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text("⛔ Отмена")
                .callbackData(Query.STATE_CANCEL.getCallback())
                .build();


        return new InlineKeyboardRow(returnButton, cancelButton);

    }


}
