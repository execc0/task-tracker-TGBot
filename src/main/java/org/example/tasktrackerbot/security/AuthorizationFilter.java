package org.example.tasktrackerbot.security;

import org.example.tasktrackerbot.exception.ApiLoginException;
import org.example.tasktrackerbot.exception.UserAlreadyAuthorizedException;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.BotService;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;

@Service
public class AuthorizationFilter {

    private final MessageSender messageSender;
    private final BotService botService;
    private final UserStateService userStateService;
    private static final Set<String> QUERIES_REQUIRE_NO_AUTH = Set.of("auth:login", "auth:register");
    private static final Set<String> QUERIES_NEED_NO_AUTH = Set.of("state:cancel", "state:return", "menu:start", "menu:main", "menu:auth");
    private static final Set<String> COMMANDS_REQUIRE_NO_AUTH = Set.of("/login", "/register");
    private static final Set<UserState> STATES_NEED_NO_AUTH = Set.of(
            UserState.LOGIN_AWAITING_USERNAME, UserState.LOGIN_AWAITING_PASSWORD,
            UserState.REGISTER_AWAITING_USERNAME, UserState.REGISTER_AWAITING_NAME,
            UserState.REGISTER_AWAITING_EMAIL, UserState.REGISTER_AWAITING_PASSWORD
    );

    public AuthorizationFilter(MessageSender messageSender, BotService botService, UserStateService userStateService) {
        this.messageSender = messageSender;
        this.botService = botService;
        this.userStateService = userStateService;
    }

    public boolean isAuthorized(String chatId) {
        boolean isAuthorized = true;
        try {
            botService.authorizeByChatId(chatId);
        } catch (ApiLoginException exception){
            isAuthorized = false;
        }
        return isAuthorized;
    }

    public void filter(Update update, String chatId) {

        if (update.hasCallbackQuery()) {
            authorizeForCallback(update, chatId);
            return;
        }

        if (userStateService.getState(chatId) != UserState.NONE) {
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            authorizeForText(update, chatId);
        }

    }

    private void authorizeForCallback(Update update, String chatId) {

        String query = update.getCallbackQuery().getData();
        if(QUERIES_NEED_NO_AUTH.contains(query)) {
            return;
        }
        if(QUERIES_REQUIRE_NO_AUTH.contains(query)) {
            if (isAuthorized(chatId)) {
                throw new UserAlreadyAuthorizedException("Вы уже авторизованы. Сначала отвяжите текущий аккаунт командой /unlink",
                        String.format("Ошибка при вводе нажатии на кнопку chatId: %s query: %s", query, chatId));
            }
            return;
        }
        botService.authorizeByChatId(chatId);

    }

    private void authorizeForText(Update update, String chatId) {

        String[] textMessageWords = update.getMessage().getText().trim().split(" ");
        String command = textMessageWords[0];
        if (command.equals("/start")) return;
        if(COMMANDS_REQUIRE_NO_AUTH.contains(command)) {
            if (isAuthorized(chatId)) {
                messageSender.deleteMessage(chatId, update.getMessage().getMessageId().toString());
                throw new UserAlreadyAuthorizedException("Вы уже авторизованы. Сначала отвяжите текущий аккаунт командой /unlink",
                        String.format("Ошибка при вводе команды chatId: %s command: %s", command, chatId));
            }
            return;
        }
        botService.authorizeByChatId(chatId);

    }


}

