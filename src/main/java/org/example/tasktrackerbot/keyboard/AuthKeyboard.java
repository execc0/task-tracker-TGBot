package org.example.tasktrackerbot.keyboard;


import org.example.tasktrackerbot.queries.Query;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class AuthKeyboard implements Keyboard {

    public KeyboardType getKeyboardType() {
        return KeyboardType.AUTH_MENU;
    }

    public InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardButton loginButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDD11 Вход")
                .callbackData(Query.LOGIN.getCallback())
                .build();

        InlineKeyboardButton registerButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDCDD Регистрация")
                .callbackData(Query.REGISTER.getCallback())
                .build();

        InlineKeyboardButton unlinkButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDD12 Отвязать")
                .callbackData(Query.UNLINK.getCallback())
                .build();

        InlineKeyboardButton menuButton = InlineKeyboardButton.builder()
                .text("\uD83C\uDFE0 Основное меню")
                .callbackData(KeyboardType.MAIN_MENU.getCallback())
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(registerButton, loginButton, unlinkButton))
                .keyboardRow(new InlineKeyboardRow(menuButton))
                .build();
    }

}
