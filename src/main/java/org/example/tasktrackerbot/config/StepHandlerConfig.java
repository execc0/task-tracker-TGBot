package org.example.tasktrackerbot.config;

import org.example.tasktrackerbot.service.step.StepHandler;
import org.example.tasktrackerbot.service.step.StepHandlerProvider;
import org.example.tasktrackerbot.session.UserState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class StepHandlerConfig {

    @Bean(name = "stepHandlersMap")
    public Map<UserState, StepHandler> createStepHandlersMap(List<StepHandlerProvider> providerList) {

        return providerList.stream()
                .flatMap(list -> list.getHandlers().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

}
