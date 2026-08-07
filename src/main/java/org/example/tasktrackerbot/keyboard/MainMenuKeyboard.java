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

        InlineKeyboardButton createTaskButton = InlineKeyboardButton.builder()
                .text("Создать задачу")
                .callbackData("task:create")
                .build();

        InlineKeyboardButton getOwnTasksButton = InlineKeyboardButton.builder()
                .text("Задачи")
                .callbackData("task:get")
                .build();

        InlineKeyboardButton returnToAuthButton = InlineKeyboardButton.builder()
                .text("Стартовое меню")
                .callbackData("menu:start")
                .build();


        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(returnToAuthButton, createTaskButton, getOwnTasksButton))
                .build();

    }

}
