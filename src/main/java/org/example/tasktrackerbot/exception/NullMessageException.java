package org.example.tasktrackerbot.exception;

public class NullMessageException extends RuntimeException {
    public NullMessageException(String message) {
        super(message);
    }
}
