package org.example.tasktrackerbot.queries.flow;

public class UnlinkCallbackQuery implements FlowCallbackQuery {
    @Override
    public String getQuery() {
        return "auth:unlink";
    }

    @Override
    public void execute(String chatId) {

    }
}
