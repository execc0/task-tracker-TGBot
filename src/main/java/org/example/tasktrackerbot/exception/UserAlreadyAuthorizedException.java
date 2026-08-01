package org.example.tasktrackerbot.exception;

public class UserAlreadyAuthorizedException extends BotException {
    public UserAlreadyAuthorizedException(String message) {
        super(message);
    }

    public UserAlreadyAuthorizedException(String message, String internalMessage) {
        super(message, internalMessage);
    }
}
