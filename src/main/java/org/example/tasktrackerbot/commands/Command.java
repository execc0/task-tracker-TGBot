package org.example.tasktrackerbot.commands;

import lombok.Getter;
import org.example.tasktrackerbot.queries.Query;

public enum Command {

    LOGIN("/login", Query.LOGIN.getCallback()),
    REGISTER("/register", Query.REGISTER.getCallback()),
    MENU("/menu", Query.MAIN_MENU.getCallback()),
    UNLINK("/unlink", Query.UNLINK.getCallback()),
    START("/start", Query.AUTH_MENU.getCallback());

    @Getter
    private final String commandText;
    @Getter
    private final String callback;

    Command(String commandText, String callback) {
        this.commandText = commandText;
        this.callback = callback;
    }

}
