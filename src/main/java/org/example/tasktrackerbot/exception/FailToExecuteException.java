package org.example.tasktrackerbot.exception;

public class FailToExecuteException extends RuntimeException {
    public FailToExecuteException(String message) {
        super(message);
    }
}
