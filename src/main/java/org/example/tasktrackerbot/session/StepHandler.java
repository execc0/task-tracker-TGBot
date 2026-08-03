package org.example.tasktrackerbot.session;

public interface StepHandler {
    /**
     * @param messageId id сообщения пользователя для удаления,
     *                   или null если ввод пришёл через callback (нечего удалять)
     */
    public void handle (String chatId, String input, Integer messageId);

}
