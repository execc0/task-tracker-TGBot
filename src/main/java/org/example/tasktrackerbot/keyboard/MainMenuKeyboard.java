package org.example.tasktrackerbot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class MainMenuKeyboard implements Keyboard {

    public KeyboardType getKeyboardType() {
        return KeyboardType.MAIN_MENU;
    }

    public InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardButton taskMenuButton = InlineKeyboardButton.builder()
                .text("Задачи")
                .callbackData(KeyboardType.TASK_MENU.getCallback())
                .build();

        InlineKeyboardButton profileMenuButton = InlineKeyboardButton.builder()
                .text("Профиль")
                .callbackData(KeyboardType.USER_MENU.getCallback())
                .build();


        InlineKeyboardButton returnToAuthButton = InlineKeyboardButton.builder()
                .text("Стартовое меню")
                .callbackData(KeyboardType.AUTH_MENU.getCallback())
                .build();


        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(returnToAuthButton, taskMenuButton, profileMenuButton))
                .build();

    }

}
