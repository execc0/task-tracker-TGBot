package org.example.tasktrackerbot.client;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.DTO.request.*;
import org.example.tasktrackerbot.DTO.request.signable.LoginAndLinkRequest;
import org.example.tasktrackerbot.DTO.request.signable.LoginByChatIdRequest;
import org.example.tasktrackerbot.DTO.request.signable.RegisterAndLinkRequest;
import org.example.tasktrackerbot.DTO.response.AuthResponse;
import org.example.tasktrackerbot.exception.ApiErrorResponse;
import org.example.tasktrackerbot.exception.ApiLoginException;
import org.example.tasktrackerbot.exception.ApiRegisterException;
import org.example.tasktrackerbot.exception.SocialLinkException;
import org.example.tasktrackerbot.security.SignatureService;
import org.example.tasktrackerbot.service.TokenHandlerService;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class TaskTrackerApiClient {

    private final WebClient taskTrackerWebClient;
    private final TokenHandlerService tokenHandlerService;

    public TaskTrackerApiClient(WebClient taskTrackerWebClient, SignatureService signatureService, TokenHandlerService tokenHandlerService) {
        this.taskTrackerWebClient = taskTrackerWebClient;
        this.tokenHandlerService = tokenHandlerService;
    }



    public String registerAndLink(RegisterAndLinkRequest request, String chatId) {

        String token = taskTrackerWebClient.post()
                .uri("/auth/register-and-link")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(ApiErrorResponse.class)
                                .flatMap(errorBody -> {
                                    String errorMessage = extractErrorMessage(errorBody);
                                    return Mono.error(new ApiRegisterException("Ошибка при регистрации: " + errorMessage));
                                }))
                .bodyToMono(AuthResponse.class)
                .block()
                .getToken();

        log.info("Получен токен: {}", token);

        return token;


    }

    public String loginAndLink(LoginAndLinkRequest request, String chatId) {


        String token = taskTrackerWebClient.post()
                .uri("/auth/login-and-link")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(ApiErrorResponse.class)
                                .flatMap(errorBody -> {
                                    String errorMessage = extractErrorMessage(errorBody);
                                    return Mono.error(new ApiLoginException("Ошибка при логине: " + errorMessage));
                                })
                )
                .bodyToMono(AuthResponse.class)
                .block()
                .getToken();

        log.info("Получен токен: {}", token);

        return token;


    }

    public void unlink(UnlinkSocialRequest request) {

        taskTrackerWebClient.post()
                .uri("/auth/unlink-social")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(ApiErrorResponse.class)
                                .flatMap(errorBody -> {
                                    String errorMessage = extractErrorMessage(errorBody);
                                    return Mono.error(new SocialLinkException("Ошибка при попытке отвязать аккаунт: " + errorMessage));
                                })
                )
                .toBodilessEntity()
                .block();

    }

    public String loginByChatId(LoginByChatIdRequest request) {

        return taskTrackerWebClient.post()
                .uri("/auth/login/telegram")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(ApiErrorResponse.class)
                                .flatMap(errorBody -> {
                                    String errorMessage = extractErrorMessage(errorBody);
                                    return Mono.error(new ApiLoginException("Ошибка! Необходима авторизация " + errorMessage));
                                })
                )
                .bodyToMono(AuthResponse.class)
                .block()
                .getToken();

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
