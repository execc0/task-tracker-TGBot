package org.example.tasktrackerbot.exception;

public class InvalidCommandInputException extends RuntimeException {
    public InvalidCommandInputException(String message) {
        super(message);
    }
}
