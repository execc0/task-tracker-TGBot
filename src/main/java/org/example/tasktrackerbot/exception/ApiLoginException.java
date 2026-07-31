package org.example.tasktrackerbot.exception;

public class ApiLoginException extends BotException {
    public ApiLoginException(String message) {
        super(message);
    }

    public ApiLoginException(String message, String internalMessage) {
        super(message, internalMessage);
    }
}
