package org.example.tasktrackerbot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.DTO.request.*;
import org.example.tasktrackerbot.DTO.request.signable.LoginAndLinkRequest;
import org.example.tasktrackerbot.DTO.request.signable.LoginByChatIdRequest;
import org.example.tasktrackerbot.DTO.request.signable.RegisterAndLinkRequest;
import org.example.tasktrackerbot.DTO.response.AuthResponse;
import org.example.tasktrackerbot.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;


@Component
@Slf4j
public class TaskTrackerApiClient {

    private final RestClient taskTrackerRestClient;
    private final ObjectMapper objectMapper;

    public TaskTrackerApiClient(RestClient taskTrackerRestClient,
                                ObjectMapper objectMapper) {
        this.taskTrackerRestClient = taskTrackerRestClient;
        this.objectMapper = objectMapper;
    }



    public String registerAndLink(RegisterAndLinkRequest request, String chatId) {

        String token = taskTrackerRestClient.post()
                .uri("/auth/register-and-link")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                    String message = extractErrorMessage(response);
                    throw new ApiRegisterException(message,
                            String.format("Ошибка при вызове API, StatusCode: %s метод: registerAndLink, сообщение: %s",
                                    response.getStatusCode(), message));
                }
                )
                .body(AuthResponse.class)
                .getToken();

        log.debug("Получен токен: {}", token);

        return token;


    }

    public String loginAndLink(LoginAndLinkRequest request, String chatId) {


        String token = taskTrackerRestClient.post()
                .uri("/auth/login-and-link")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                            String message = extractErrorMessage(response);
                            throw new ApiLoginException(message,
                                    String.format("Ошибка при вызове API, StatusCode: %s метод: loginAndLink, сообщение: %s",
                                            response.getStatusCode(), message));
                        }
                )
                .body(AuthResponse.class)
                .getToken();

        log.debug("Получен токен: {}", token);

        return token;


    }

    public void unlink(UnlinkSocialRequest request) {

        taskTrackerRestClient.post()
                .uri("/auth/unlink-social")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                            String message = extractErrorMessage(response);
                            throw new ApiUnlinkException(message,
                                    String.format("Ошибка при вызове API, StatusCode: %s метод: unlink, сообщение: %s",
                                            response.getStatusCode(), message));
                        }
                )
                .toBodilessEntity();

    }

    public String loginByChatId(LoginByChatIdRequest request) {

        return taskTrackerRestClient.post()
                .uri("/auth/login/telegram")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                            String message = extractErrorMessage(response);
                            throw new ApiLoginException("Необходима авторизация. Для начала работы введите команду /start",
                                    String.format("Ошибка при вызове API, StatusCode: %s метод: loginByChatId, сообщение: %s",
                                            response.getStatusCode(), message));
                        }
                )
                .body(AuthResponse.class)
                .getToken();

    }

    public void createOwnTask(TaskCreateRequest request, String token) {

        taskTrackerRestClient.post()
                .uri("tasks/my")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                            String message = extractErrorMessage(response);
                            throw new ApiUnlinkException(message,
                                    String.format("Ошибка при вызове API, StatusCode: %s метод: createOwnTask, сообщение: %s",
                                            response.getStatusCode(), message));
                        }
                )
                .toBodilessEntity();

    }


    private String extractErrorMessage(ClientHttpResponse response) throws IOException {

        ApiErrorResponse responseBody = objectMapper.readValue(response.getBody(), ApiErrorResponse.class);

        if (responseBody.errors() != null && !responseBody.errors().isEmpty()) {
            return String.join(",", responseBody.errors());
        }
        if (responseBody.message() != null) {
            return responseBody.message();
        }
        throw new ApiServerError("Внутрення ошибка сервера. Повторите попытку позже",
                "Ошибка при парсинге сообщения об ошибке" + responseBody);
    }



}
