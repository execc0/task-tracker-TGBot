package org.example.tasktrackerbot.keyboard;

import org.example.tasktrackerbot.queries.Query;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class TaskListMenu implements Keyboard {

    @Override
    public KeyboardType getKeyboardType() {
        return KeyboardType.GET_TASKS;
    }

    @Override
    public InlineKeyboardMarkup getKeyboard() {


        InlineKeyboardButton returnToTaskMenu = InlineKeyboardButton.builder()
                .text("⬅️ Назад")
                .callbackData(KeyboardType.TASK_MENU.getCallback())
                .build();

        InlineKeyboardButton deleteTaskButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDDD1\uFE0F Удалить задачу")
                .callbackData(Query.DELETE_TASK.getCallback())
                .build();

        InlineKeyboardButton createTaskButton = InlineKeyboardButton.builder()
                .text("<<<")
                .callbackData(Query.TASKS_PREV_PAGE.getCallback())
                .build();

        InlineKeyboardButton getOwnTasksButton = InlineKeyboardButton.builder()
                .text(">>>")
                .callbackData(Query.TASKS_NEXT_PAGE.getCallback())
                .build();


        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(createTaskButton, getOwnTasksButton))
                .keyboardRow(new InlineKeyboardRow(returnToTaskMenu, deleteTaskButton))
                .build();


    }
}
