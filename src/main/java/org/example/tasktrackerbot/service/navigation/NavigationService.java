package org.example.tasktrackerbot.service.navigation;

import org.example.tasktrackerbot.keyboard.AuthKeyboard;
import org.example.tasktrackerbot.keyboard.Keyboard;
import org.example.tasktrackerbot.keyboard.KeyboardType;
import org.example.tasktrackerbot.keyboard.MainMenuKeyboard;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Map;

@Service
public class NavigationService {

    private final MessageSender messageSender;
    private final MainMenuKeyboard mainMenuKeyboard;
    private final AuthKeyboard authKeyboard;
    private final UserStateService userStateService;
    private final Map<KeyboardType, Keyboard> keyboardMap;

    public NavigationService(MessageSender messageSender, MainMenuKeyboard mainMenuKeyboard,
                             AuthKeyboard authKeyboard, UserStateService userStateService, Map<KeyboardType, Keyboard> keyboardMap) {
        this.messageSender = messageSender;
        this.mainMenuKeyboard = mainMenuKeyboard;
        this.authKeyboard = authKeyboard;
        this.userStateService = userStateService;
        this.keyboardMap = keyboardMap;
    }

    @Bean
    public Map<String, NavigationHandler> createNavigationHandlerMap() {
        return Map.of("state:cancel", this::cancel,
                "state:return", this::returnToPreviousStep,
                "menu:main", this::mainMenu,
                "menu:start", this::startMenu);
    }



    public void mainMenu(String chatId) {

        String message = """
                Основное меню. Операции представлены ниже:
                """;
        String messageId = getMenuMessageId(chatId);
        Integer menuId = messageSender.editOrSendNewMessage(chatId, messageId, message, mainMenuKeyboard.getKeyboard());
        userStateService.setMenuId(chatId, menuId.toString());

    }

    public void startMenu(String chatId) {

        String message = """
        Привет! Это бот для Task Tracker, сейчас находится в разработке.
        Список доступных команд:
        /register
        /login
        /menu
        Ссылка на репозиторий API: https://github.com/execc0/task-tracker
        """;
        String messageId = getMenuMessageId(chatId);
        Integer menuId = messageSender.editOrSendNewMessage(chatId, messageId, message, authKeyboard.getKeyboard());
        userStateService.setMenuId(chatId, menuId.toString());

    }

    public void cancel(String chatId) {
        String messageId = getTempMessageId(chatId);
        if (messageId != null) {
            messageSender.deleteMessage(chatId, messageId);
        }
        userStateService.clearState(chatId);
        userStateService.clearTemp(chatId);
    }

    public void returnToPreviousStep(String chatId) {
        UserState currentState = userStateService.getState(chatId);
        UserState previousState = currentState.getPreviousState();

        if (previousState == UserState.NONE || previousState == null || previousState.getPromptText() == null) {
            cancel(chatId);
            return;
        }

        String message = previousState.getPromptText();
        KeyboardType keyboardType = previousState.getKeyboardType();

        InlineKeyboardMarkup keyboard;

        if (keyboardType != null && keyboardMap.containsKey(keyboardType)) {
             keyboard = keyboardMap.get(keyboardType).getKeyboard();
        } else {
            if (previousState.getPreviousState() == UserState.NONE) {
                keyboard = keyboardMap.get(KeyboardType.CANCEL).getKeyboard();
            } else keyboard = keyboardMap.get(KeyboardType.RETURN_OR_CANCEL).getKeyboard();

        }

        String tempMessageId = getTempMessageId(chatId);

        String messageWithBar = buildProgressBar(previousState) + "\n" + message;

        messageSender.editOrSendNewMessage(chatId, tempMessageId, messageWithBar, keyboard);
        userStateService.setState(chatId, previousState);


    }

    private String getTempMessageId(String chatId) {
        return userStateService.getTempField(chatId, "bot_message_id");
    }

    private String getMenuMessageId(String chatId) {
        return userStateService.getMenuId(chatId);
    }

    private String buildProgressBar(UserState nextState) {

        final int barLength = 5;

        StringBuilder progressBar = new StringBuilder();

        int totalSteps = nextState.getTotalSteps();
        int currentStep = nextState.getCurrentStep();

        int dotsToDraw = (int) Math.round((double) currentStep/totalSteps * barLength);

        for (int i = 1; i <= barLength; i++) {
            progressBar.append(i <= dotsToDraw ? "🟩" : "⬜");
        }

        return progressBar + String.format(" Шаг %d/%d", currentStep, totalSteps);

    }


}
