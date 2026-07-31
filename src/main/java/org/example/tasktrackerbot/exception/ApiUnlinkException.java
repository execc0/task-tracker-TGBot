package org.example.tasktrackerbot.exception;

public class ApiUnlinkException extends BotException {
    public ApiUnlinkException(String message) {
        super(message);
    }

    public ApiUnlinkException(String message, String internalMessage) {
        super(message, internalMessage);
    }
}
