package org.example.tasktrackerbot.exception;

public class ApiServerError extends BotException {
    public ApiServerError(String message) {
        super(message);
    }

    public ApiServerError(String message, String internalMessage) {
      super(message, internalMessage);
    }

}
