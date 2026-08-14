package org.example.tasktrackerbot.exception;

public class PageNumberException extends BotException {
    public PageNumberException(String message) {
        super(message);
    }

    public PageNumberException(String message, String internalMessage) {
        super(message, internalMessage);
    }

}
