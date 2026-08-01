package org.example.tasktrackerbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktrackerbot.exception.ApiErrorResponse;
import org.example.tasktrackerbot.exception.ApiServerError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Configuration
public class WebClientConfig {

    private final ObjectMapper objectMapper;

    @Value("${task-tracker.api.url}")
    private String taskTrackerApiUrl;

    public WebClientConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean(name = "taskTrackerRestClient")
    public RestClient createWebClient(RestClient.Builder builder) {
        return builder
                .baseUrl(taskTrackerApiUrl)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError,
                        (request, response) -> handleServerError(request, response))
                .build();
    }

    private void handleServerError(HttpRequest request, ClientHttpResponse response) throws IOException {

        ApiErrorResponse responseBody = objectMapper.readValue(response.getBody(), ApiErrorResponse.class);

        String errorMessage = extractErrorMessage(responseBody);

        throw new ApiServerError("Внутрення ошибка сервера. Повторите попытку позже",
                "Ошибка сервера при вызове API, URI: " + request.getURI() + " response: " + errorMessage);

    }

    private String extractErrorMessage(ApiErrorResponse response) {
        if (response.errors() != null && !response.errors().isEmpty()) {
            return String.join(",", response.errors());
        }
        if (response.message() != null) {
            return response.message();
        }
        throw new RuntimeException("Неизвестная ошибка сервера");
    }

}
