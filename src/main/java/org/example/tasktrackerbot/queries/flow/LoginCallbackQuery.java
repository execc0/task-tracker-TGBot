package org.example.tasktrackerbot.queries.flow;

import org.example.tasktrackerbot.service.LoginStepService;
import org.springframework.stereotype.Component;

@Component
public class LoginCallbackQuery implements FlowCallbackQuery {

    private final LoginStepService loginStepService;

    public LoginCallbackQuery(LoginStepService loginStepService) {
        this.loginStepService = loginStepService;
    }

    @Override
    public String getQuery() {
        return "auth:login";
    }

    @Override
    public void execute(String chatId) {
        loginStepService.startLogin(chatId);
    }
}
