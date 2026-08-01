package org.example.tasktrackerbot.keyboard;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class AuthKeyboard implements Keyboard {

    public InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardButton loginButton = InlineKeyboardButton.builder()
                .text("Вход")
                .callbackData("auth:login")
                .build();

        InlineKeyboardButton registerButton = InlineKeyboardButton.builder()
                .text("Регистрация")
                .callbackData("auth:register")
                .build();

        InlineKeyboardButton unlinkButton = InlineKeyboardButton.builder()
                .text("Отвязать")
                .callbackData("auth:unlink")
                .build();

        InlineKeyboardButton menuButton = InlineKeyboardButton.builder()
                .text("Основное меню")
                .callbackData("menu:main")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(registerButton, loginButton, unlinkButton))
                .keyboardRow(new InlineKeyboardRow(menuButton))
                .build();
    }

}
