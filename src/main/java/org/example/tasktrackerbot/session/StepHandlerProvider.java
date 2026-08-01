package org.example.tasktrackerbot.session;

import java.util.Map;

public interface StepHandlerProvider {

    public Map<UserState, StepHandler> getHandlers();

}
