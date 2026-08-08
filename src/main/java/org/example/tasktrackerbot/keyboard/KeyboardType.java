package org.example.tasktrackerbot.keyboard;

import lombok.Getter;


public enum KeyboardType {

    RETURN_OR_CANCEL,
    CANCEL,

    AUTH_MENU("menu:start"),
    MAIN_MENU("menu:main"),
    TASK_MENU("menu:task"),
    USER_MENU("menu:user"),
    TASK_LIST_MENU("menu:task_list"),
    TASK_PRIORITY,
    TASK_STATUS;

    @Getter
    private final String callback;

    KeyboardType() {
        this.callback = null;
    }

    KeyboardType(String callback) {
        this.callback = callback;
    }




}
