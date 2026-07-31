package org.example.tasktrackerbot.exception;

public class ApiRegisterException extends BotException {
  public ApiRegisterException(String message) {
    super(message);
  }

  public ApiRegisterException(String message, String internalMessage) {
    super(message, internalMessage);
  }
}
