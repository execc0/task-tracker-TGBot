package org.example.tasktrackerbot.keyboard;

import org.example.tasktrackerbot.queries.Query;
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
                .callbackData(Query.UPDATE_USERNAME.getCallback())
                .build();

        InlineKeyboardButton changeNameButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDCDD Сменить имя")
                .callbackData(Query.UPDATE_NAME.getCallback())
                .build();

        InlineKeyboardButton changePasswordButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDD11 Сменить пароль")
                .callbackData(Query.UPDATE_PASSWORD.getCallback())
                .build();

        InlineKeyboardButton deleteUserButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDDD1\uFE0F Удалить пользователя")
                .callbackData(Query.USER_DELETE.getCallback())
                .build();

        InlineKeyboardButton changeEmailButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDCE7 Сменить email")
                .callbackData("user:email")
                .build();


        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(changeUsernameButton, changeNameButton))
                .keyboardRow(new InlineKeyboardRow(changePasswordButton, changeEmailButton))
                .keyboardRow(new InlineKeyboardRow(returnToMainMenu, deleteUserButton))
                .build();



    }
}
