package org.example.tasktrackerbot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.DTO.request.LinkSocialRequest;
import org.example.tasktrackerbot.DTO.request.LinkSocialRequestPayload;
import org.example.tasktrackerbot.DTO.request.UserLoginRequest;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;
import org.example.tasktrackerbot.DTO.response.UserResponse;
import org.example.tasktrackerbot.exception.ApiErrorResponse;
import org.example.tasktrackerbot.exception.ApiLoginException;
import org.example.tasktrackerbot.exception.ApiRegisterException;
import org.example.tasktrackerbot.security.SignatureService;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TaskTrackerApiClient {

    private final WebClient taskTrackerWebClient;
    private final SignatureService signatureService;

    public TaskTrackerApiClient(WebClient taskTrackerWebClient, SignatureService signatureService) {
        this.taskTrackerWebClient = taskTrackerWebClient;
        this.signatureService = signatureService;
    }



    public String register(UserRegisterRequest userRegisterRequest) {

        return taskTrackerWebClient.post()
                .uri("/auth/register")
                .bodyValue(userRegisterRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(ApiErrorResponse.class)
                                .flatMap(errorBody -> {
                                    String errorMessage = extractErrorMessage(errorBody);
                                    log.error("Ошибка при регистрации {}", errorMessage);
                                    return Mono.error(new ApiRegisterException("Ошибка при регистрации: " + errorMessage));
                                }))
                .bodyToMono(UserResponse.class)
                .block()
                .getToken();

    }

    public String login(UserLoginRequest userLoginRequest, String chatId) {

        String token;

        token = taskTrackerWebClient.post()
                .uri("/auth/login")
                .bodyValue(userLoginRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(ApiErrorResponse.class)
                                .flatMap(errorBody -> {
                                    String errorMessage = extractErrorMessage(errorBody);
                                    log.error("Ошибка при логине {}", errorMessage);
                                    return Mono.error(new ApiLoginException("Ошибка при логине: " + errorMessage));
                                })
                )
                .bodyToMono(UserResponse.class)
                .block()
                .getToken();

        LinkSocialRequestPayload payload = new LinkSocialRequestPayload("Telegram", chatId, System.currentTimeMillis());
        String signature = signatureService.createSignature(payload);
        LinkSocialRequest linkSocialRequest = new LinkSocialRequest(payload, signature);
        linkSocial(linkSocialRequest);

        return """
                Вы успешно вошли в аккаунт
                """;

    }

    public void linkSocial(LinkSocialRequest linkSocialRequest) {

        taskTrackerWebClient.post()
                .uri("/auth/link-social")
                .bodyValue(linkSocialRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(ApiErrorResponse.class)
                                .flatMap(errorBody -> {
                                    String errorMessage = extractErrorMessage(errorBody);
                                    log.error("Ошибка при попытке связать аккаунт: {}", errorMessage);
                                    return Mono.error(new ApiLoginException("Ошибка при логине: " + errorMessage));
                                })
                )
                .toBodilessEntity()
                .block();

    }

    private String extractErrorMessage(ApiErrorResponse response) {
        if (response.errors() != null && !response.errors().isEmpty()) {
            return String.join(",", response.errors());
        }
        if (response.message() != null) {
            return response.message();
        }
        return "Неизвестная ошибка сервера";
    }

}
