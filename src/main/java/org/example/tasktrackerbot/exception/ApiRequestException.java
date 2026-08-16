package org.example.tasktrackerbot.exception;

public class ApiRequestException extends BotException {

    public ApiRequestException(String message, String internalMessage) {
        super(message, internalMessage);
    }

    public ApiRequestException(String message) {
        super(message);
    }
}
