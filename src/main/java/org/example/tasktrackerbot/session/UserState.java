package org.example.tasktrackerbot.session;

import lombok.Getter;

public enum UserState {
    NONE(0, 0),

    REGISTER_AWAITING_NAME(1, 4),
    REGISTER_AWAITING_USERNAME(2, 4),
    REGISTER_AWAITING_EMAIL(3, 4),
    REGISTER_AWAITING_PASSWORD(4, 4),

    LOGIN_AWAITING_USERNAME(1, 2),
    LOGIN_AWAITING_PASSWORD(2, 2),

    TASK_CREATE_AWAITING_TITLE(1, 4),
    TASK_CREATE_AWAITING_DESCRIPTION(2, 4),
    TASK_CREATE_AWAITING_PRIORITY(3, 4),
    TASK_CREATE_AWAITING_STATUS(4, 4),

    UNLINK_AWAITING_USERNAME(1, 2, NONE),
    UNLINK_AWAITING_PASSWORD(2, 2, UNLINK_AWAITING_USERNAME);

    @Getter
    private final int currentStep;
    @Getter
    private final int totalSteps;
    @Getter
    private final UserState previousState;

    UserState(int currentStep, int totalSteps, UserState previousState) {
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.previousState = previousState;
    }

    UserState(int currentStep, int totalSteps) {
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.previousState = null;
    }


}
