package org.example.tasktrackerbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${task-tracker.api.url}")
    private String taskTrackerApiUrl;

    @Bean(name = "taskTrackerWebClient")
    public WebClient createWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(taskTrackerApiUrl)
                .build();
    }
}
