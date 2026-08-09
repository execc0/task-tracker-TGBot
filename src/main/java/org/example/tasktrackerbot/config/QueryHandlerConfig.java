package org.example.tasktrackerbot.config;

import org.example.tasktrackerbot.service.QueryHandler;
import org.example.tasktrackerbot.service.QueryHandlerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class QueryHandlerConfig {

    @Bean
    public Map<String, QueryHandler> createQueryHandlerMap(List<QueryHandlerProvider> queryHandlerProviders) {

        return queryHandlerProviders.stream()
                .flatMap(provider -> provider.getQueryHandlers().entrySet().stream())
                .collect(Collectors.toMap(entry -> entry.getKey().getCallback(),
                        entry -> entry.getValue()));

    }

}
