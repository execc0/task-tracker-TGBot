package org.example.tasktrackerbot.service.navigation;

import org.example.tasktrackerbot.keyboard.Keyboard;
import org.example.tasktrackerbot.keyboard.KeyboardType;
import org.example.tasktrackerbot.queries.Query;
import org.example.tasktrackerbot.responder.MessageSender;
import org.example.tasktrackerbot.service.QueryHandler;
import org.example.tasktrackerbot.service.QueryHandlerProvider;
import org.example.tasktrackerbot.session.UserState;
import org.example.tasktrackerbot.session.UserStateService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Map;
import java.util.Objects;

@Service
public class NavigationService implements QueryHandlerProvider {

    private final MessageSender messageSender;
    private final UserStateService userStateService;
    private final Map<KeyboardType, Keyboard> keyboardProviderMap;

    public NavigationService(MessageSender messageSender,
                             UserStateService userStateService,
                             Map<KeyboardType, Keyboard> keyboardMap) {
        this.messageSender = messageSender;
        this.userStateService = userStateService;
        this.keyboardProviderMap = keyboardMap;
    }

    @Override
    public Map<Query, QueryHandler> getQueryHandlers() {
        return Map.of(Query.STATE_CANCEL, this::cancel,
                Query.STATE_RETURN, this::returnToPreviousStep,
                Query.MAIN_MENU, this::mainMenu,
                Query.AUTH_MENU, this::startMenu,
                Query.TASK_MENU, this::taskMenu);
    }


    public void taskMenu(String chatId) {

        String menuId = getMenuMessageId(chatId);
        String message = """
                Меню операций с задачами. Доступные операции:
                Создать - запускает процесс создания задачи.
                Мои задачи - постранично выводит список ваших задач.
                Свободные задачи - выводит список свободных задач. Свободную задачу может взять любой пользователь.
                Редактировать - запускает процесс редактирования вашей задачи.
                Удалить - запускает процесс удаления задачи.
                """;
        Integer newMenuId = messageSender.editOrSendNewMessage(chatId, menuId, message,
                keyboardProviderMap.get(KeyboardType.TASK_MENU).getKeyboard());
        userStateService.setMenuId(chatId, newMenuId.toString());

    }


    public void mainMenu(String chatId) {

        String message = """
                Основное меню. Доступные операции:
                Задачи - управление задачами. Создание, удаление, поиск, редактирование задач.
                Профиль - управление профилем. Редактирование имени пользователя, смена пароля, смена email.
                Авторизация - возврат к меню авторизации.
                """;
        String menuId = getMenuMessageId(chatId);
        Integer newMenuId = messageSender.editOrSendNewMessage(chatId, menuId, message,
                keyboardProviderMap.get(KeyboardType.MAIN_MENU).getKeyboard());
        userStateService.setMenuId(chatId, newMenuId.toString());

    }

    public void startMenu(String chatId) {

        String message = """
        Привет! Это бот для Task Tracker, сейчас находится в разработке.
        На данный момент реализован не весь функционал. Некоторые кнопки могут не работать, а описание не соответствовать действительности.
        Ссылка на репозиторий API: https://github.com/execc0/task-tracker
        Ссылка на репозиторий бота: https://github.com/execc0/task-tracker-TGBot
        """;
        String messageId = getMenuMessageId(chatId);
        Integer menuId = messageSender.editOrSendNewMessage(chatId, messageId, message,
                keyboardProviderMap.get(KeyboardType.AUTH_MENU).getKeyboard());
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

        if (keyboardType != null && keyboardProviderMap.containsKey(keyboardType)) {
             keyboard = keyboardProviderMap.get(keyboardType).getKeyboard();
        } else {
            if (previousState.getPreviousState() == UserState.NONE) {
                keyboard = keyboardProviderMap.get(KeyboardType.CANCEL).getKeyboard();
            } else keyboard = keyboardProviderMap.get(KeyboardType.RETURN_OR_CANCEL).getKeyboard();

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
