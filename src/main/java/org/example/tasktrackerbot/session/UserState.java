package org.example.tasktrackerbot.session;

import lombok.Getter;
import org.example.tasktrackerbot.keyboard.KeyboardType;

public enum UserState {
    NONE(0, 0),

    REGISTER_AWAITING_NAME(1, 4, NONE, "Введите ваше имя:"),
    REGISTER_AWAITING_USERNAME(2, 4, REGISTER_AWAITING_NAME, "Введите ваш username: "),
    REGISTER_AWAITING_EMAIL(3, 4, REGISTER_AWAITING_USERNAME, "Введите ваш email: "),
    REGISTER_AWAITING_PASSWORD(4, 4, REGISTER_AWAITING_EMAIL, "Введите ваш пароль: "),

    LOGIN_AWAITING_USERNAME(1, 2, NONE, "Введите ваш username: "),
    LOGIN_AWAITING_PASSWORD(2, 2, LOGIN_AWAITING_USERNAME, "Введите пароль: "),

    TASK_CREATE_AWAITING_TITLE(1, 4, NONE, "Введите название задачи: "),
    TASK_CREATE_AWAITING_DESCRIPTION(2, 4, TASK_CREATE_AWAITING_TITLE, "Введите описание задачи: "),
    TASK_CREATE_AWAITING_PRIORITY(3, 4, TASK_CREATE_AWAITING_DESCRIPTION, "Выберите приоритет задачи: ",  KeyboardType.TASK_PRIORITY),
    TASK_CREATE_AWAITING_STATUS(4, 4, TASK_CREATE_AWAITING_PRIORITY, "Выберите статус задачи: ", KeyboardType.TASK_STATUS),

    UNLINK_AWAITING_USERNAME(1, 2, NONE, "Введите ваш username: "),
    UNLINK_AWAITING_PASSWORD(2, 2, UNLINK_AWAITING_USERNAME, "Введите пароль: ");

    @Getter
    private final int currentStep;
    @Getter
    private final int totalSteps;
    @Getter
    private final UserState previousState;
    @Getter
    private final String promptText;
    @Getter
    private final KeyboardType keyboardType;

    

    UserState(int currentStep, int totalSteps, UserState previousState, String promptText, KeyboardType keyboardType) {
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.previousState = previousState;
        this.promptText = promptText;
        this.keyboardType = keyboardType;
    }

    UserState(int currentStep, int totalSteps, UserState previousState, String promptText) {
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.previousState = previousState;
        this.promptText = promptText;
        this.keyboardType = null;
    }


    UserState(int currentStep, int totalSteps) {
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.previousState = null;
        this.promptText = null;
        this.keyboardType = null;

    }


}
