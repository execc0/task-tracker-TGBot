package org.example.tasktrackerbot.queries;
import org.example.tasktrackerbot.DTO.API.request.Priority;
import org.example.tasktrackerbot.DTO.API.request.Status;


import lombok.Getter;

public enum Query {

    STATE_CANCEL("state:cancel"),
    STATE_RETURN("state:return"),
    CONFIRM("state:confirm"),

    GET_TASKS("task:get:list"),
    CREATE_TASK("task:create"),
    TASKS_NEXT_PAGE("tasks:next"),
    TASKS_PREV_PAGE("tasks:previous"),
    DELETE_TASK("task:delete"),

    LOGIN("auth:login"),
    UNLINK("auth:unlink"),
    REGISTER("auth:register"),

    START_MENU("menu:start"),
    AUTH_MENU("menu:auth"),
    MAIN_MENU("menu:main"),
    TASK_MENU("menu:task"),
    USER_MENU("menu:user"),
    PROFILE_MENU("menu:profile"),

    USER_DELETE("user:delete"),
    UPDATE_USERNAME("user:username"),
    UPDATE_EMAIL("user:email"),
    UPDATE_PASSWORD("user:password"),
    UPDATE_NAME("user:name"),

    STATUS_TODO("status:TODO", Status.TODO.getText()),
    STATUS_IN_PROGRESS("status:IN_PROGRESS", Status.IN_PROGRESS.getText()),
    STATUS_DONE("status:DONE", Status.DONE.getText()),

    PRIORITY_LOW("priority:LOW", Priority.LOW.getText()),
    PRIORITY_MEDIUM("priority:MEDIUM", Priority.MEDIUM.getText()),
    PRIORITY_HIGH("priority:HIGH", Priority.HIGH.getText());

    @Getter
    private final String callback;
    @Getter
    private final String text;

    Query(String callback) {
        this.callback = callback;
        this.text = null;
    }

    Query(String callback, String text) {
        this.callback = callback;
        this.text = text;
    }



}
