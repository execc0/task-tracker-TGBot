package org.example.tasktrackerbot.queries.flow;

import org.example.tasktrackerbot.service.UnlinkStepService;
import org.springframework.stereotype.Component;

@Component
public class UnlinkCallbackQuery implements FlowCallbackQuery {

    private final UnlinkStepService unlinkStepService;

    public UnlinkCallbackQuery(UnlinkStepService unlinkStepService) {
        this.unlinkStepService = unlinkStepService;
    }

    @Override
    public String getQuery() {
        return "auth:unlink";
    }

    @Override
    public void execute(String chatId) {
        unlinkStepService.startUnlink(chatId);
    }
}
