package org.example.tasktrackerbot.exception;

public class NullMessageException extends BotException {

    public NullMessageException(String message) {
        super(message);
    }

    public NullMessageException(String message, String internalMessage) {
        super(message, internalMessage);
    }

}
