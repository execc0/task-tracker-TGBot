package org.example.tasktrackerbot.queries;

import lombok.Getter;

public enum Query {

    STATE_CANCEL("state:cancel"),
    STATE_RETURN("state:return"),
    GET_TASKS("task:get:list"),
    CREATE_TASK("task:create"),
    LOGIN("auth:login"),
    UNLINK("auth:unlink"),
    REGISTER("auth:register"),
    AUTH_MENU("menu:start"),
    MAIN_MENU("menu:main"),
    TASK_MENU("menu:task"),
    USER_MENU("menu:user"),
    PROFILE_MENU("menu:profile");

    @Getter
    private final String callback;

    Query(String callback) {
        this.callback = callback;
    }


}
