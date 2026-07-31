package org.example.tasktrackerbot.exception;

public class InvalidCommandInputException extends BotException {
    public InvalidCommandInputException(String message) {
        super(message);
    }

    public InvalidCommandInputException(String message, String internalMessage) {
        super(message, internalMessage);
    }
}
