package org.example.tasktrackerbot.queries;

import org.example.tasktrackerbot.service.RegistrationService;
import org.springframework.stereotype.Component;

@Component
public class RegisterQuery implements CallbackQuery {

    private final RegistrationService registrationService;

    public RegisterQuery(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public String getQuery() {
        return "auth:register";
    }

    @Override
    public void execute(String chatId) {
        registrationService.startRegistration(chatId);
    }

}
