package org.example.tasktrackerbot.exception;

import lombok.Getter;

@Getter
public class BotException extends RuntimeException {

  private String internalMessage;

  public BotException(String message) {
    super(message);
  }

  public BotException(String message, String internalMessage) {
    super(message);
    this.internalMessage = internalMessage;
  }

}
