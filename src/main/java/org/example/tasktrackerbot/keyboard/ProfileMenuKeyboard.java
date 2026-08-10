package org.example.tasktrackerbot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class ProfileMenuKeyboard implements Keyboard {
    @Override
    public KeyboardType getKeyboardType() {
        return KeyboardType.PROFILE_MENU;
    }

    @Override
    public InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardButton returnToMainMenu = InlineKeyboardButton.builder()
                .text("\uD83D\uDEAA Назад")
                .callbackData(KeyboardType.MAIN_MENU.getCallback())
                .build();

        InlineKeyboardButton changeUsernameButton = InlineKeyboardButton.builder()
                .text("✏️ Сменить username")
                .callbackData("user:change:username")
                .build();

        InlineKeyboardButton changeNameButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDCDD Сменить имя")
                .callbackData("user:change:name")
                .build();

        InlineKeyboardButton changePasswordButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDD11 Сменить пароль")
                .callbackData("user:change:password")
                .build();

        InlineKeyboardButton deleteUserButton = InlineKeyboardButton.builder()
                .text("❌ Удалить пользователя")
                .callbackData("user:delete")
                .build();


        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(changeUsernameButton, changeNameButton))
                .keyboardRow(new InlineKeyboardRow(changePasswordButton, deleteUserButton))
                .keyboardRow(new InlineKeyboardRow(returnToMainMenu))
                .build();



    }
}
