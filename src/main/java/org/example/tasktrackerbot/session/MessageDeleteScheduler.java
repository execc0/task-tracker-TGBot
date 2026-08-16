package org.example.tasktrackerbot.session;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.exception.NullMessageException;
import org.example.tasktrackerbot.responder.MessageSender;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
@Slf4j
public class MessageDeleteScheduler {


    private static final String MESSAGE_DELETE_KEY = "messages_to_delete";
    private final StringRedisTemplate stringRedisTemplate;
    private final MessageSender messageSender;

    public MessageDeleteScheduler(StringRedisTemplate stringRedisTemplate, MessageSender messageSender) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.messageSender = messageSender;
    }

    public void scheduleDelete(String chatId, String messageId, Integer delaySeconds) {

        if (messageId == null) {
            throw new NullPointerException("Не удалось поставить в очередь удаление сообщения, messageId == null");
        }

        log.info("Проверка сообщений на удаление...");
        String value = chatId + ":" + messageId;
        double deleteAt = Instant.now().getEpochSecond() + delaySeconds;
        stringRedisTemplate.opsForZSet().add(MESSAGE_DELETE_KEY, value, deleteAt);

    }

    @Scheduled(fixedRate = 5000) // 5 секунд
    public void deleteScheduled() {

        Set<String> messagesToDelete = stringRedisTemplate.opsForZSet().rangeByScore(MESSAGE_DELETE_KEY, 0, Instant.now().getEpochSecond());

        if (messagesToDelete == null || messagesToDelete.isEmpty()) {
            return;
        }

        messagesToDelete.forEach(value -> {
            try {
                messageSender.deleteMessage(parseChatId(value), parseMessageId(value));
            } finally {
                stringRedisTemplate.opsForZSet().remove(MESSAGE_DELETE_KEY, value);
            }
        });
        log.info("Сообщения удалены");

    }

    private String parseChatId(String value) {
        return value.split(":")[0];
    }

    private String parseMessageId(String value) {
        return value.split(":")[1];
    }

}
