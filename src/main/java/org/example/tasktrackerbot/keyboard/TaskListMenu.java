package org.example.tasktrackerbot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class TaskListMenu implements Keyboard {

    @Override
    public KeyboardType getKeyboardType() {
        return KeyboardType.TASK_LIST_MENU;
    }

    @Override
    public InlineKeyboardMarkup getKeyboard() {


        InlineKeyboardButton returnToMainMenu = InlineKeyboardButton.builder()
                .text("Назад")
                .callbackData(KeyboardType.TASK_MENU.getCallback())
                .build();

        InlineKeyboardButton createTaskButton = InlineKeyboardButton.builder()
                .text("<<<")
                .callbackData("page:previous")
                .build();

        InlineKeyboardButton getOwnTasksButton = InlineKeyboardButton.builder()
                .text(">>>")
                .callbackData("page:next")
                .build();


        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(returnToMainMenu, createTaskButton, getOwnTasksButton))
                .build();


    }
}
