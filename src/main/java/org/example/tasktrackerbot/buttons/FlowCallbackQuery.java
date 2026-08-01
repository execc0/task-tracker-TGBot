package org.example.tasktrackerbot.buttons;

public interface FlowCallbackQuery {

    public String getQuery();

    public void execute(String chatId);

}
