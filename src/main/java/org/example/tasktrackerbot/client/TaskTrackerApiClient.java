package org.example.tasktrackerbot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.example.tasktrackerbot.DTO.API.request.TaskCreateRequest;
import org.example.tasktrackerbot.DTO.API.request.UnlinkSocialRequest;
import org.example.tasktrackerbot.DTO.API.request.signable.LoginAndLinkRequest;
import org.example.tasktrackerbot.DTO.API.request.signable.LoginByChatIdRequest;
import org.example.tasktrackerbot.DTO.API.request.signable.RegisterAndLinkRequest;
import org.example.tasktrackerbot.DTO.API.response.AuthResponse;
import org.example.tasktrackerbot.DTO.API.response.PageResponseDTO;
import org.example.tasktrackerbot.DTO.API.response.TaskResponse;
import org.example.tasktrackerbot.DTO.API.response.UserResponse;
import org.example.tasktrackerbot.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;


@Component
@Slf4j
public class TaskTrackerApiClient {

    private final RestClient taskTrackerRestClient;
    private final ObjectMapper objectMapper;
    @Value("${internal.api.key}")
    private String internalApiKey;

    public TaskTrackerApiClient(RestClient taskTrackerRestClient,
                                ObjectMapper objectMapper) {
        this.taskTrackerRestClient = taskTrackerRestClient;
        this.objectMapper = objectMapper;
    }



    public String registerAndLink(RegisterAndLinkRequest request, String chatId) {

        String token = taskTrackerRestClient.post()
                .uri("/auth/register-and-link")
                .header("X-Internal-API-Key", internalApiKey)
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

    public void userDelete(String token) {

        taskTrackerRestClient.delete()
                .uri("/users/me")
                .header("X-Internal-API-Key", internalApiKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                            String message = extractErrorMessage(response);
                            throw new ApiLoginException(message,
                                    String.format("Ошибка при вызове API, StatusCode: %s метод: getOwnUser, сообщение: %s",
                                            response.getStatusCode(), message));
                        }
                )
                .toBodilessEntity();


    }

    public void updateOwnUsername(String token, String password) {

        generalPatchRequest(token, "/users/me/username?username=", password);

    }

    public void updateOwnName(String token, String name) {

        generalPatchRequest(token, "/users/me/name?name=", name);

    }

    public void updateOwnEmail(String token, String email) {

        generalPatchRequest(token, "/users/me/email?email=", email);

    }

    public void updateOwnPassword(String token, String password) {

        generalPatchRequest(token, "/users/me/password?password=", password);

    }

    private void generalPatchRequest(String token, String uri, String value) {

        taskTrackerRestClient.patch()
                .uri(uri + "{value}", value)
                .header("X-Internal-API-Key", internalApiKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                            String message = extractErrorMessage(response);
                            throw new ApiLoginException(message,
                                    String.format("Ошибка при вызове API, StatusCode: %s метод: generalPatchRequest, uri: %s, сообщение: %s",
                                            response.getStatusCode(), uri, message));
                        }
                )
                .toBodilessEntity();

    }



    public UserResponse getOwnUser(String token) {

        return taskTrackerRestClient.get()
                .uri("/users/me")
                .header("X-Internal-API-Key", internalApiKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                            String message = extractErrorMessage(response);
                            throw new ApiLoginException(message,
                                    String.format("Ошибка при вызове API, StatusCode: %s метод: getOwnUser, сообщение: %s",
                                            response.getStatusCode(), message));
                        }
                )
                .body(UserResponse.class);

    }

    public String loginAndLink(LoginAndLinkRequest request, String chatId) {


        return taskTrackerRestClient.post()
                .uri("/auth/login-and-link")
                .header("X-Internal-API-Key", internalApiKey)
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


    }

    public void unlink(UnlinkSocialRequest request) {

        taskTrackerRestClient.post()
                .uri("/auth/unlink-social")
                .header("X-Internal-API-Key", internalApiKey)
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
                .header("X-Internal-API-Key", internalApiKey)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                            String message = extractErrorMessage(response);
                            throw new ApiLoginException("Необходима авторизация." +
                                    " Для начала работы авторизуйтесь, используя /register или /login, или введите /start",
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
                .header("X-Internal-API-Key", internalApiKey)
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

        log.debug("Выполнен запрос к API: requestBody {}", request);

    }

    public PageResponseDTO<TaskResponse> getOwnTasks(String token, String pageNum) {

        return taskTrackerRestClient.get()
                .uri("tasks/my?page={page}", pageNum)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("X-Internal-API-Key", internalApiKey)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, response) -> {
                            String message = extractErrorMessage(response);
                            throw new ApiUnlinkException(message,
                                    String.format("Ошибка при вызове API, StatusCode: %s метод: getOwnTasks, сообщение: %s",
                                            response.getStatusCode(), message));
                        }
                )
                .body(new ParameterizedTypeReference<PageResponseDTO<TaskResponse>>() {
                });

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
