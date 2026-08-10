package org.example.tasktrackerbot.exception;

public class NotFoundException extends BotException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, String internalMessage) {
        super(message, internalMessage);
    }
}
