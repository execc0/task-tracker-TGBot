package org.example.tasktrackerbot.keyboard;

import lombok.Getter;
import org.example.tasktrackerbot.queries.Query;


public enum KeyboardType {

    RETURN_OR_CANCEL,
    CANCEL,
    CANCEL_OR_CONFIRM,

    AUTH_MENU(Query.AUTH_MENU),
    MAIN_MENU(Query.MAIN_MENU),
    TASK_MENU(Query.TASK_MENU),
    USER_MENU(Query.USER_MENU),
    GET_TASKS(Query.GET_TASKS),
    PROFILE_MENU(Query.PROFILE_MENU),
    TASK_PRIORITY,
    TASK_STATUS;

    @Getter
    private final String callback;

    KeyboardType() {
        this.callback = null;
    }

    KeyboardType(Query query) {
        this.callback = query.getCallback();
    }




}
