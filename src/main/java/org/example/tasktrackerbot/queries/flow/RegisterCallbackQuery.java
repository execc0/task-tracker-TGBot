package org.example.tasktrackerbot.queries.flow;

import org.example.tasktrackerbot.service.step.RegistrationStepService;
import org.springframework.stereotype.Component;

@Component
public class RegisterCallbackQuery implements FlowCallbackQuery {

    private final RegistrationStepService registrationStepService;

    public RegisterCallbackQuery(RegistrationStepService registrationStepService) {
        this.registrationStepService = registrationStepService;
    }

    @Override
    public String getQuery() {
        return "auth:register";
    }

    @Override
    public void execute(String chatId) {
        registrationStepService.startRegistration(chatId);
    }

}
