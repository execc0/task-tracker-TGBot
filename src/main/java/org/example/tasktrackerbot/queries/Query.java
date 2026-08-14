package org.example.tasktrackerbot.queries;

import lombok.Getter;

public enum Query {

    STATE_CANCEL("state:cancel"),
    STATE_RETURN("state:return"),
    CONFIRM("state:confirm"),
    GET_TASKS("task:get:list"),
    CREATE_TASK("task:create"),
    LOGIN("auth:login"),
    UNLINK("auth:unlink"),
    REGISTER("auth:register"),
    AUTH_MENU("menu:start"),
    MAIN_MENU("menu:main"),
    TASK_MENU("menu:task"),
    USER_MENU("menu:user"),
    PROFILE_MENU("menu:profile"),
    USER_DELETE("user:delete"),
    UPDATE_USERNAME("user:username"),
    UPDATE_EMAIL("user:email"),
    UPDATE_PASSWORD("user:password"),
    UPDATE_NAME("user:name");

    // STATUS_TODO,
    // STATUS_IN_PROGRESS,
    // STATUS_DONE;

    @Getter
    private final String callback;
//  private final String input;
    Query(String callback) {
        this.callback = callback;
    }


}
