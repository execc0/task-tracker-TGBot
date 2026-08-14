package org.example.tasktrackerbot.exception;

public class UnmodifiableMessageException extends RuntimeException {

    public UnmodifiableMessageException(String message) {
        super(message);
    }

}
