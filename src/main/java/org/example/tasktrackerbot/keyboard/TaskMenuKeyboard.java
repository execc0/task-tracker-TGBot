package org.example.tasktrackerbot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class TaskMenuKeyboard implements Keyboard {

    public KeyboardType getKeyboardType() {
        return KeyboardType.TASK_MENU;
    }

    public InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardButton returnToMainMenu = InlineKeyboardButton.builder()
                .text("Назад")
                .callbackData(KeyboardType.MAIN_MENU.getCallback())
                .build();

        InlineKeyboardButton createTaskButton = InlineKeyboardButton.builder()
                .text("Создать задачу")
                .callbackData("task:create")
                .build();

        InlineKeyboardButton getOwnTasksButton = InlineKeyboardButton.builder()
                .text("Мои задачи")
                .callbackData("task:get_list")
                .build();


        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(returnToMainMenu, createTaskButton, getOwnTasksButton))
                .build();

    }
}
