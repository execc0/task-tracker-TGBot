package org.example.tasktrackerbot.queries.flow;

/**
 * Классы - Query, которые реализуют этот интерфейс, начинают новую цепочку диалога.
 * Как правило, в методе, вызываемом внутри execute обнуляется предыдущее состояние пользователя.
 */
public interface FlowCallbackQuery {

    public String getQuery();

    public void execute(String chatId);

}
