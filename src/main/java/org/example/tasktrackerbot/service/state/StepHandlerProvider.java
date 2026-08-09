package org.example.tasktrackerbot.service.state;

import org.example.tasktrackerbot.session.UserState;

import java.util.Map;

public interface StepHandlerProvider {

    public Map<UserState, StepHandler> getStepHandlers();

}
