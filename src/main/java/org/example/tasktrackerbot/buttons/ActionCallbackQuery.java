package org.example.tasktrackerbot.buttons;

public interface ActionCallbackQuery {

    public String getQuery();

    public void execute(String chatId, String value);

}
