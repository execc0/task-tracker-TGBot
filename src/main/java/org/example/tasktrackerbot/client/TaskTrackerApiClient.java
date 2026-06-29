package org.example.tasktrackerbot.client;

import org.example.tasktrackerbot.DTO.request.UserLoginRequest;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;
import org.example.tasktrackerbot.DTO.response.UserResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TaskTrackerApiClient {

    private final WebClient taskTrackerWebClient;

    public TaskTrackerApiClient(WebClient taskTrackerWebClient) {
        this.taskTrackerWebClient = taskTrackerWebClient;
    }



    public String register(UserRegisterRequest userRegisterRequest) {

        return taskTrackerWebClient.post()
                .uri("/auth/register")
                .bodyValue(userRegisterRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block()
                .getToken();

    }

    public String login(UserLoginRequest userLoginRequest) {

        return taskTrackerWebClient.post()
                .uri("/auth/login")
                .bodyValue(userLoginRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block()
                .getToken();
    }

}
