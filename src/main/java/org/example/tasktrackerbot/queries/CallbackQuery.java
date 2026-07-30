package org.example.tasktrackerbot.queries;

public interface CallbackQuery {

    public String getQuery();

    public void execute(String chatId);

}
